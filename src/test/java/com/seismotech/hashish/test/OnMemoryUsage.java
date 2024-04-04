package com.seismotech.hashish.test;

import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * Utility functions on MemoryUsage.
 */
public class OnMemoryUsage {

  public static final MemoryUsage NOTHING = new MemoryUsage(0,0,0,0);

  public static MemoryUsage add(MemoryUsage m1, MemoryUsage m2) {
    // Combined usage of 2 memory pools is obviously the addition for
    // init, used and committed, but it IS NOT for max.
    // Nevertheless, we are using addition as a good approximation.
    return new MemoryUsage(
      m1.getInit() + m2.getInit(),
      m1.getUsed() + m2.getUsed(),
      m1.getCommitted() + m2.getCommitted(),
      m1.getMax() + m2.getMax());
  }

  public static MemoryUsage add(List<MemoryUsage> ms) {
    MemoryUsage all = NOTHING;
    for (final var m: ms) all = add(all, m);
    return all;
  }
}
