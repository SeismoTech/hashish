package com.seismotech.hashish.impl;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel32;

public abstract class HashingKernel32 implements Hashing {

  protected abstract Kernel32 newKernel();

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

  private long integral(int x, int taillen) {
    final Kernel32 kernel = newKernel();
    kernel.tail(x, taillen, taillen);
    return kernel.hash64();
  }

  @Override
  public long hash(int x) {
    final Kernel32 kernel = newKernel();
    kernel.block(x);
    kernel.tail(0, 0, 4);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel32 kernel = newKernel();
    kernel.block((int) x);
    kernel.block((int) (x >>> 32));
    kernel.tail(0, 0, 8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final Kernel32 kernel = newKernel();
    final int blockno = len / 4;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le32(xs, off + 4*i));
    }
    final int taillen = len - 4*blockno;
    kernel.tail(Bits.tailLE32(xs, off + 4*blockno, taillen), taillen, len);
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
