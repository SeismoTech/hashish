package org.seismotech.hashish.impl;

import org.seismotech.ground.mem.Bits;
import org.seismotech.hashish.api.Hasher;
import org.seismotech.hashish.api.Hashing;
import org.seismotech.hashish.api.Kernel128;

public abstract class HashingKernel128 extends BareHashing implements Hashing {

  protected abstract Kernel128 newKernel();

  @Override
  public Hasher hasher() {
    return new HasherKernel128(newKernel());
  }

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
    kernel.tail(x, 0, taillen, taillen);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final int W = 16;
    final Kernel128 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i), Bits.le64(xs, off + W*i + W/2));
    }
    final int tailoff = off + W*blockno;
    final int taillen = len - W*blockno;
    final long low, high;
    if (W/2 <= taillen) {
      low = Bits.le64(xs, tailoff);
      high = Bits.le64tail(xs, tailoff + W/2, taillen - W/2);
    } else {
      low = Bits.le64tail(xs, tailoff, taillen);
      high = 0;
    }
    kernel.tail(low, high, taillen, len);
    return kernel.hash64();
  }

  @Override
  public long hash(char[] xs, int off, int len) {
    final int W = 8;
    final Kernel128 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i), Bits.le64(xs, off + W*i + W/2));
    }
    final int tailoff = off + W*blockno;
    final int taillen = len - W*blockno;
    final long low, high;
    if (4 <= taillen) {
      low = Bits.le64(xs, tailoff);
      high = Bits.le64tail(xs, tailoff + W/2, taillen - W/2);
    } else {
      low = Bits.le64tail(xs, tailoff, taillen);
      high = 0;
    }
    kernel.tail(low, high, 2*taillen, 2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final int W = 8;
    final Kernel128 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i), Bits.le64(xs, off + W*i + W/2));
    }
    final int tailoff = off + W*blockno;
    final int taillen = len - W*blockno;
    final long low, high;
    if (4 <= taillen) {
      low = Bits.le64(xs, tailoff);
      high = Bits.le64tail(xs, tailoff + W/2, taillen - W/2);
    } else {
      low = Bits.le64tail(xs, tailoff, taillen);
      high = 0;
    }
    kernel.tail(low, high, 2*taillen, 2*len);
    return kernel.hash64();
  }
}
