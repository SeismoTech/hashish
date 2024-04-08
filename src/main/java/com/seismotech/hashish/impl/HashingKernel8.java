package com.seismotech.hashish.impl;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel8;

public abstract class HashingKernel8 implements Hashing {

  protected abstract Kernel8 newKernel();

  @Override
  public long hash(byte x) {
    final Kernel8 kernel = newKernel();
    kernel.block(x);
    kernel.finish(1);
    return kernel.hash64();
  }

  @Override
  public long hash(char x) {
    final Kernel8 kernel = newKernel();
    kernel.block((byte) x);
    kernel.block((byte) (x >> 8));
    kernel.finish(2);
    return kernel.hash64();
  }

  @Override
  public long hash(short x) {
    return hash((char) x);
  }

  @Override
  public long hash(int x) {
    final Kernel8 kernel = newKernel();
    kernel.block((byte) x);
    kernel.block((byte) (x >>> 8));
    kernel.block((byte) (x >>> 16));
    kernel.block((byte) (x >>> 24));
    kernel.finish(4);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel8 kernel = newKernel();
    kernel.block((byte) x);
    kernel.block((byte) (x >>> 8));
    kernel.block((byte) (x >>> 16));
    kernel.block((byte) (x >>> 24));
    kernel.block((byte) (x >>> 32));
    kernel.block((byte) (x >>> 40));
    kernel.block((byte) (x >>> 48));
    kernel.block((byte) (x >>> 56));
    kernel.finish(8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final Kernel8 kernel = newKernel();
    for (int i = 0; i < len; i++) kernel.block(xs[off+i]);
    kernel.finish(len);
    return kernel.hash64();
  }
}
