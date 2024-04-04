package com.seismotech.hashish.test;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * A MemoryUsage with its <i>memory pool</i> ({@code MemoryPoolMXBean})
 * attached.
 * Useful to check whether that memory stats are from heap or non-heap memory.
 */
public class PoolAttachedMemoryUsage {
  private final MemoryPoolMXBean pool;
  private final MemoryUsage usage;

  private PoolAttachedMemoryUsage(MemoryPoolMXBean pool, MemoryUsage usage) {
    this.pool = pool;
    this.usage = usage;
  }

  public MemoryUsage memoryUsage() {return usage;}
  public MemoryType type() {return pool.getType();}
  public MemoryPoolMXBean memoryPool() {return pool;}

  public static MemoryUsage add(List<PoolAttachedMemoryUsage> ms) {
    MemoryUsage all = OnMemoryUsage.NOTHING;
    for (final var m: ms) all = OnMemoryUsage.add(all, m.memoryUsage());
    return all;
  }

  public static class Builder {
    public static final Builder THE = new Builder();

    private final Map<String,MemoryPoolMXBean> name2pool;

    public Builder() {
      name2pool = new HashMap<>();
      for (final var pool: ManagementFactory.getMemoryPoolMXBeans()) {
        final String name = pool.getName();
        if (name2pool.containsKey(name)) throw new IllegalArgumentException(
          "Several pools sharing name `" + name + "`");
        name2pool.put(name, pool);
      }
    }

    public List<PoolAttachedMemoryUsage> attach(Map<String,MemoryUsage> mus) {
      return mus.entrySet().stream()
        .map(this::attach)
        .collect(Collectors.toList());
    }

    public PoolAttachedMemoryUsage attach(Map.Entry<String,MemoryUsage> entry) {
      return attach(entry.getKey(), entry.getValue());
    }

    public PoolAttachedMemoryUsage attach(String name, MemoryUsage usage) {
      final var pool = name2pool.get(name);
      if (pool == null) throw new IllegalArgumentException(
        "Usage for an unknown memory pool `" + name + "`");
      return new PoolAttachedMemoryUsage(pool, usage);
    }
  }
}
