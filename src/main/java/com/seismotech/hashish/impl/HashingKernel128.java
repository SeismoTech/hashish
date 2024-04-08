package com.seismotech.hashish.impl;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel128;

public abstract class HashingKernel128 implements Hashing {

  protected abstract Kernel128 newKernel();

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

  @Override
  public long hash(long x) {
    return integral(x, 8);
  }

  private long integral(long x, int taillen) {
    final Kernel128 kernel = newKernel();
    kernel.tail(0, x, taillen, taillen);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final Kernel128 kernel = newKernel();
    final int blockno = len / 16;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + 16*i), Bits.le64(xs, off + 16*i + 8));
    }
    final int tailoff = 16*blockno;
    final int taillen = len - tailoff;
    final long low, high;
    if (8 <= taillen) {
      low = Bits.le64(xs, off + tailoff);
      high = Bits.tailLE64(xs, off + tailoff + 8, taillen-8);
    } else {
      low = Bits.tailLE64(xs, off + tailoff, taillen);
      high = 0;
    }
    kernel.tail(low, high, taillen, len);
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
