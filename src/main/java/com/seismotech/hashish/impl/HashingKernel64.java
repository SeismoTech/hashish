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
    kernel.tail(0, 8, 8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final Kernel64 kernel = newKernel();
    final int blockno = len / 8;
    for (int i = 0; i < blockno; i++) {
      kernel.block((long) Bits.LE64_FROM_BYTES.get(xs, off + 8*i));
    }
    final int taillen = len - 8*blockno;
    kernel.tail(Bits.tailLE64(xs, off + 8*blockno, taillen), taillen, len);
    return kernel.hash64();
  }

  // long hash(char[] xs);

  // long hash(char[] xs, int off, int len);

  // long hash(short[] xs);

  // long hash(short[] xs, int off, int len);

  // long hash(int[] xs);

  // long hash(int[] xs, int off, int len);

  // long hash(long[] xs);

  // long hash(long[] xs, int off, int len);

  // long hash(float[] xs);

  // long hash(float[] xs, int off, int len);

  // long hash(double[] xs);

  // long hash(double[] xs, int off, int len);

  // long hash(String xs);

  // long hash(String xs, int off, int len);
}
