package com.seismotech.hashish.test;

import java.io.IOException;
import java.io.Closeable;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.JavaClass;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A variation of {@link SimpleAllocationChecker} with a simpler usage pattern:
 * {@code
 * var checker = new TryAllocationChecker(Tracked.class);
 * for (int i = 0; i < M && !checker.satisfied(); i++) {
 *   try (var tmp = checker.enter()) {
 *     for (int j = 0; j < N; j++) {
 *       //Code section to check
 *     }
 *   }
 * }
 * checker.assertSatisfied();
 * }
 */
public class TryAllocationChecker {
  private final HeapMemory mem;
  private final Class<?> tracked;
  private int run0Limit;
  private boolean verbose;
  private int run0Size;
  private long gccount;
  private long blackhole;

  public TryAllocationChecker(Class<?> tracked) {
    this.mem = new HeapMemory();
    this.tracked = tracked;
    this.run0Limit = 3;
    this.verbose = false;
    this.run0Size = 0;
    this.gccount = 0;
    this.blackhole = 0;
  }

  public TryAllocationChecker noAllocRunOf(int n) {
    this.run0Limit = n;
    return this;
  }

  public TryAllocationChecker verbose(boolean enabled) {
    this.verbose = enabled;
    return this;
  }

  public void consume(long n) {blackhole += n;}

  public boolean satisfied() {return run0Limit <= run0Size;}

  public void assertSatisfied() {
    System.err.println("Reduced: " + blackhole);
    assertTrue(satisfied());
  }

  public CloseChecker enter() {
    mem.gc();
    gccount = mem.collectionCount();
    return new CloseChecker();
  }

  public class CloseChecker implements Closeable {
    public void close()
    throws IOException {
      try (final JavaDump dump = JavaDump.createAt("simple-alloc-checker")) {
        dump.dump();
        final long gc1 = mem.collectionCount();
        if (verbose) System.err.println(
          "Garbage collections: " + gccount + " -> " + gc1);
        if (gccount != gc1) {run0Size = 0; return;}
        final Heap heap = dump.heap();
        final JavaClass allocated = heap.getJavaClassByName(tracked.getName());
        final int n = (allocated == null) ? 0 : allocated.getInstancesCount();
        if (verbose) System.err.println("Allocated: " + n);
        if (n == 0) run0Size++;
        else run0Size = 0;
      }
    }
  }
}
