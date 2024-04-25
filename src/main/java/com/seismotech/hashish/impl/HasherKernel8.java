package com.seismotech.hashish.impl;

import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Kernel8;

public class HasherKernel8 implements Hasher {

  private final Kernel8 kernel;
  private long total;

  public HasherKernel8(Kernel8 kernel) {
    this.kernel = kernel;
    this.total = 0;
  }

  @Override public long hash64() {
    final Kernel8 fernel = kernel.clone();
    fernel.finish(total);
    return fernel.hash64();
  }

  @Override public Hasher add(byte x) {
    kernel.block(x);
    total++;
    return this;
  }

  @Override public Hasher add(short x) {
    kernel.add(x);
    total += 2;
    return this;
  }

  @Override public Hasher add(int x) {
    kernel.add(x);
    total += 4;
    return this;
  }

  @Override public Hasher add(long x) {
    kernel.add(x);
    total += 8;
    return this;
  }

  @Override public Hasher add(byte[] xs, int off, int len) {
    kernel.add(xs, off, len);
    total += len;
    return this;
  }

  @Override public Hasher add(char[] xs, int off, int len) {
    kernel.add(xs, off, len);
    total += 2*len;
    return this;
  }

  @Override public Hasher add(String xs, int off, int len) {
    kernel.add(xs, off, len);
    total += 2*len;
    return this;
  }
}
