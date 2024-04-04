package com.seismotech.hashish.test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

public class HeapMemory {

  private final MemoryMXBean mem;
  private final List<GarbageCollectorMXBean> gcs;

  public HeapMemory() {
    this.mem = ManagementFactory.getMemoryMXBean();
    this.gcs = ManagementFactory.getGarbageCollectorMXBeans();
  }

  public void gc() {mem.gc();}
  public void gc2() {mem.gc(); mem.gc();}

  public long collectionCount() {
    long n = 0;
    for (final var gc: gcs) n += gc.getCollectionCount();
    return n;
  }

  public long collectionMillis() {
    long t = 0;
    for (final var gc: gcs) t += gc.getCollectionTime();
    return t;
  }

  public MemoryUsage heapMemoryUsage() {return mem.getHeapMemoryUsage();}
  public MemoryUsage nonHeapMemoryUsage() {return mem.getNonHeapMemoryUsage();}

  public boolean isVerbose() {return mem.isVerbose();}
  public void verbose(boolean enabled) {mem.setVerbose(enabled);}
}
