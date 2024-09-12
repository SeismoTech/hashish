package com.seismotech.hashish.impl;

import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel8;

public abstract class HashingKernel8 extends BareHashing implements Hashing {

  protected abstract Kernel8 newKernel();

  @Override
  public Hasher hasher() {
    return new HasherKernel8(newKernel());
  }

  @Override
  public long hash(byte x) {
    final Kernel8 kernel = newKernel();
    kernel.block(x);
    kernel.finish(1);
    return kernel.hash64();
  }

  @Override
  public long hash(short x) {
    final Kernel8 kernel = newKernel();
    kernel.add(x);
    kernel.finish(2);
    return kernel.hash64();
  }

  @Override
  public long hash(int x) {
    final Kernel8 kernel = newKernel();
    kernel.add(x);
    kernel.finish(4);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel8 kernel = newKernel();
    kernel.add(x);
    kernel.finish(8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final Kernel8 kernel = newKernel();
    kernel.add(xs, off, len);
    kernel.finish(len);
    return kernel.hash64();
  }

  @Override
  public long hash(char[] xs, int off, int len) {
    final Kernel8 kernel = newKernel();
    kernel.add(xs, off, len);
    kernel.finish(2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final Kernel8 kernel = newKernel();
    kernel.add(xs, off, len);
    kernel.finish(2*len);
    return kernel.hash64();
  }
}
