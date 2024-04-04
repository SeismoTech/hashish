package com.seismotech.hashish.test;

import java.util.function.Consumer;
import java.io.IOException;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.JavaClass;

/**
 * A higher order variation of {@link SimpleAllocationChecker}
 * and {@link TryAllocationChecker}.
 *
 * <p>Intended usage:
 * {@code
 * AllocationChecker.eventuallyAvoid()
 *   .allocationOf(Tracked.class)
 *   .at(context)
 *   .transformingLong((context, i) -> *code section to check*)
 *   .check(Assertions::assertTrue);
 * }
 * The <i>code section to check</i> should produce a long value
 * that will be tracked inside AllocationChecker to avoid compiler
 * considers that section dead code.
 */
public class AllocationChecker {

  public static Contextual<Void> eventuallyAvoid() {
    return new Contextual<>();
  }

  //----------------------------------------------------------------------
  public static abstract class Iteration<C> {
    protected final HeapMemory mem;
    protected Class<?> tracked;
    protected int maxRounds;
    protected int max0Run;
    protected int every;
    protected boolean verbose;

    public Iteration() {
      this.mem = new HeapMemory();
      this.tracked = null;
      this.maxRounds = 25;
      this.max0Run = 3;
      this.every = 100_000;
      this.verbose = false;
    }

    public Iteration(Iteration<?> other) {
      this.mem = other.mem;
      this.tracked = other.tracked;
      this.maxRounds = other.maxRounds;
      this.max0Run = other.max0Run;
      this.every = other.every;
      this.verbose = other.verbose;
    }

    protected abstract C me();

    public C allocationOf(Class<?> tracked) {
      this.tracked = tracked;
      return me();
    }

    public C checkEvery(int n) {
      this.every = n;
      return me();
    }

    public C checkLimit(int rounds) {
      this.maxRounds = rounds;
      return me();
    }

    public C checkUntilNoAllocRunOf(int n) {
      this.max0Run = n;
      return me();
    }

    public C verbose() {return verbose(true);}

    public C verbose(boolean enabled) {
      this.verbose = enabled;
      return me();
    }
  }

  //----------------------------------------------------------------------
  public static class Contextual<V>
    extends Iteration<Contextual<V>> {

    protected V vehicle;

    public Contextual() {
      this.vehicle = null;
    }

    public Contextual(Iteration other, V vehicle) {
      super(other);
      this.vehicle = vehicle;
    }

    @Override
    protected Contextual<V> me() {return this;}

    public <V2> Contextual<V2> at(V2 vehicle) {
      return new Contextual<>(this, vehicle);
    }

    public LongTransformer<V> transformingLong(LongStep<V> step) {
      return new LongTransformer<>(this, step);
    }
  }

  //----------------------------------------------------------------------
  public static class LongTransformer<V>
    extends Iteration<LongTransformer<V>> {

    protected V vehicle;
    private LongStep<V> step;
    private LongReduction redux;

    public LongTransformer(final Contextual<V> context, LongStep<V> step) {
      super(context);
      this.vehicle = context.vehicle;
      this.step = step;
      this.redux = LongReduction.ADD;
    }

    @Override
    protected LongTransformer<V> me() {return this;}

    public LongTransformer<V> reducedWith(LongReduction redux) {
      this.redux = redux;
      return this;
    }

    public LongTransformer<V> reducedWithXor() {
      return reducedWith(LongReduction.XOR);
    }

    public LongTransformer<V> reducedWithAdd() {
      return reducedWith(LongReduction.ADD);
    }

    public boolean check()
    throws IOException {
      long k = 0;
      long r = 0;
      int runSize = 0;
      outer:
      for (int i = 0; i < maxRounds; i++) {
        mem.gc();
        final long gc0 = mem.collectionCount();
        for (int j = 0; j < every; j++) {
          r = redux.reduced(r, step.step(vehicle, j));
        }
        try (final JavaDump dump = JavaDump.createAt("alloc-checker")) {
          dump.dump();
          final long gc1 = mem.collectionCount();
          if (verbose) System.err.println(
            "Garbage collections: " + gc0 + " -> " + gc1);
          if (gc0 != gc1) continue;
          final Heap heap = dump.heap();
          final JavaClass allocated
            = heap.getJavaClassByName(tracked.getName());
          final int n = (allocated == null) ? 0 : allocated.getInstancesCount();
          if (verbose) System.err.println("Allocated: " + n);
          if (n == 0) {
            runSize++;
            if (max0Run <= runSize) break outer;
          } else {
            runSize = 0;
          }
        }
      }
      //Poor-man solution to ensure compiler doesn't consider `step` dead code
      System.err.println("Reduced: " + r);
      return max0Run <= runSize;
    }

    public void check(Consumer<Boolean> checker)
    throws IOException {
      checker.accept(check());
    }
  }

  //----------------------------------------------------------------------
  public static interface LongStep<V> {
    long step(V vehicle, long i);
  }

  public static interface LongReduction {
    long reduced(long a, long b);

    final LongReduction XOR = (a,b) -> a^b;
    final LongReduction ADD = (a,b) -> a+b;
  }
}
