package com.seismotech.hashish.impl;

import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel32;

public abstract class HashingKernel32 implements Hashing {

  protected abstract Kernel32 newKernel();

  @Override
  public Hasher hasher() {
    return new HasherKernel32(newKernel());
  }

  @Override
  public long hash(byte x) {
    return integral(Bits.ubyte(x), 1);
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
    final int W = 4;
    final Kernel32 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le32(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le32tail(xs, off + W*blockno, taillen), taillen, len);
    return kernel.hash64();
  }

  @Override
  public long hash(char[] xs, int off, int len) {
    final int W = 2;
    final Kernel32 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le32(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le32tail(xs, off + W*blockno, taillen), 2*taillen, 2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final int W = 2;
    final Kernel32 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le32(xs, off + W*i));
    }
    final int taillen = len - W*blockno;
    kernel.tail(Bits.le32tail(xs, off + W*blockno, taillen), 2*taillen, 2*len);
    return kernel.hash64();
  }
}
