package com.seismotech.hashish.test;

import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.function.LongConsumer;

/**
 * A failed attempt to discover object creation tracking heap usage.
 * It doesn't work because JVM updates memory usage statistics discretionarily,
 * not clearly not after each object creation.
 */
public class MemoryWatcher {
  private final int times;
  private final LongConsumer action;
  private final MemoryMXBean memory;
  private final List<GarbageCollectorMXBean> gcs;
  private long round;

  public MemoryWatcher(int times, LongConsumer action) {
    this.times = times;
    this.action = action;
    this.memory = ManagementFactory.getMemoryMXBean();
    this.gcs = ManagementFactory.getGarbageCollectorMXBeans();
    this.round = 0;
  }

  private long collections() {
    long total = 0;
    for (var gc: gcs) total += gc.getCollectionCount();
    return total;
  }

  public long usedMemory() {
    //memory.gc();  memory.gc();
    final long col0 = collections();
    final MemoryUsage mem0 = memory.getHeapMemoryUsage();
    for (int i = 0; i < times; i++) action.accept(round++);
    final MemoryUsage mem1 = memory.getHeapMemoryUsage();
    final long col1 = collections();
    System.err.println("Init: " + mem0 + ", " + col0);
    System.err.println("End: " + mem1 + ", " + col1);
    return mem1.getUsed() - mem0.getUsed();
  }
}
