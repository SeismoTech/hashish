package com.seismotech.hashish.impl;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel8X;

public abstract class HashingKernel8X implements Hashing {

  protected abstract Kernel8X newKernel();

  @Override
  public long hash(byte x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(1);
    return kernel.hash64();
  }

  @Override
  public long hash(char x) {
    return hash((short) x);
  }

  @Override
  public long hash(short x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(2);
    return kernel.hash64();
  }

  @Override
  public long hash(int x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(4);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    return hash1(xs, off, len);
  }

  private long hash0(byte[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    final int blockno = len / 8;
    for (int i = 0; i < blockno; i++) {
      kernel.block((long) Bits.LE64_FROM_BYTES.get(xs, off + 8*i));
    }
    final int taillen = len - 8*blockno;
    kernel.block(xs, off + 8*blockno, taillen);
    kernel.finish(len);
    return kernel.hash64();
  }

  private long hash1(byte[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    final int blocksize = kernel.preferredBlockSize(len);
    final int blockno = len / blocksize;
    for (int i = 0; i < blockno; i++) {
      kernel.block(xs, off + i*blocksize);
    }
    final int taillen = len - blockno*blocksize;
    kernel.block(xs, off + blockno*blocksize, taillen);
    kernel.finish(len);
    return kernel.hash64();
  }
}
