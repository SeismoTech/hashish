package com.seismotech.hashish.test;

import java.io.IOException;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.JavaClass;

/**
 * An allocation checker based on heap dump.
 * It checks when a given class is not anymore allocated in a code section.
 * Better use {@link TryAllocationChecker} or {@link AllocationChecker}.
 *
 * <p>Indented usage:
 * {@code
 * var checker = new SimpleAllocationChecker(Tracked.class);
 * var run = 0;
 * for (int i = 0; i < M && run < R; i++) {
 *   checker.enter();
 *   for (int j = 0; j < N; j++) {
 *     //Code section to check
 *   }
 *   run = checker.exit(run);
 * }
 * assertEquals(R, run);
 * }
 * Where topically {@code N} is a huge constant, around 1e6,
 * {@code M} is an small one, around 25,
 * and {@code R} is in the range [1,3].
 * It is very important that the <i>code section to check</i> is not dead
 * code for the compiler;
 * for instance, some artificial sum-up can be computed
 * and later used (printed).
 */
public class SimpleAllocationChecker {
  private final HeapMemory mem;
  private final Class<?> tracked;
  private boolean verbose;
  private long gccount;

  public SimpleAllocationChecker(Class<?> tracked) {
    this.mem = new HeapMemory();
    this.tracked = tracked;
    this.verbose = false;
    this.gccount = 0;
  }

  public SimpleAllocationChecker verbose(boolean enabled) {
    this.verbose = enabled;
    return this;
  }

  public void enter() {
    mem.gc();
    gccount = mem.collectionCount();
  }

  public int exit(int run)
  throws IOException {
    try (final JavaDump dump = JavaDump.createAt("simple-alloc-checker")) {
      dump.dump();
      final long gc1 = mem.collectionCount();
      if (verbose) System.err.println(
        "Garbage collections: " + gccount + " -> " + gc1);
      if (gccount != gc1) return 0;
      final Heap heap = dump.heap();
      final JavaClass allocated = heap.getJavaClassByName(tracked.getName());
      final int n = (allocated == null) ? 0 : allocated.getInstancesCount();
      if (verbose) System.err.println("Allocated: " + n);
      return (n == 0) ? run+1 : 0;
    }
  }
}
