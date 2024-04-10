package com.seismotech.hashish.impl;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel64;

public abstract class HashingKernel64 implements Hashing {

  protected abstract Kernel64 newKernel();

  @Override
  public long hash(byte x) {
    return integral(Bits.ubyte(x), 1);
  }

  @Override
  public long hash(char x) {
    return integral(x, 2);
  }

  @Override
  public long hash(short x) {
    return integral(Bits.ushort(x), 2);
  }

  @Override
  public long hash(int x) {
    return integral(Bits.uint(x), 4);
  }

  private long integral(long x, int taillen) {
    final Kernel64 kernel = newKernel();
    kernel.tail(x, taillen, taillen);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel64 kernel = newKernel();
    kernel.block(x);
    kernel.tail(0, 0, 8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final int W = 8;
    final Kernel64 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le64tail(xs, off + W*blockno, taillen), taillen, len);
    return kernel.hash64();
  }

  @Override
  public long hash(char[] xs, int off, int len) {
    final int W = 4;
    final Kernel64 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le64tail(xs, off + W*blockno, taillen), 2*taillen, 2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final int W = 4;
    final Kernel64 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le64tail(xs, off + W*blockno, taillen), 2*taillen, 2*len);
    return kernel.hash64();
  }
}
