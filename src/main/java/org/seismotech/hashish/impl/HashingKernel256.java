package org.seismotech.hashish.impl;

import org.seismotech.ground.mem.Bits;
import org.seismotech.hashish.api.Hasher;
import org.seismotech.hashish.api.Hashing;
import org.seismotech.hashish.api.Kernel256;

public abstract class HashingKernel256 extends BareHashing implements Hashing {

  protected abstract Kernel256 newKernel();

  @Override
  public Hasher hasher() {
    return new HasherKernel256(newKernel());
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
    final Kernel256 kernel = newKernel();
    kernel.tail(x, 0, 0, 0, taillen, taillen);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    final int W = 32;
    final Kernel256 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      final int init = off + W*i;
      kernel.block(
        Bits.le64(xs, init),
        Bits.le64(xs, init + 8),
        Bits.le64(xs, init + 16),
        Bits.le64(xs, init + 24));
    }
    final int taillen = len - W*blockno;
    final int tailinit = off + W*blockno;
    //Ideal place to use an array.
    //But the usage pattern is too complex and only Graal EE
    //is able to do Scalar Replacement
    long b0 = 0, b1 = 0, b2 = 0, b3 = 0;
    if (taillen < 8) {
      b0 = Bits.le64tail(xs, tailinit, taillen);
    } else {
      b0 = Bits.le64(xs, tailinit);
      if (taillen < 16) {
        b1 = Bits.le64tail(xs, tailinit + 8, taillen - 8);
      } else {
        b1 = Bits.le64(xs, tailinit + 8);
        if (taillen < 24) {
          b2 = Bits.le64tail(xs, tailinit + 16, taillen - 16);
        } else {
          b2 = Bits.le64(xs, tailinit + 16);
          b3 = Bits.le64tail(xs, tailinit + 24, taillen - 24);
        }
      }
    }
    kernel.tail(b0, b1, b2, b3, taillen, len);
    return kernel.hash64();
  }

  @Override
  public long hash(char[] xs, int off, int len) {
    final int W = 16;
    final Kernel256 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      final int init = off + W*i;
      kernel.block(
        Bits.le64(xs, init),
        Bits.le64(xs, init + 4),
        Bits.le64(xs, init + 8),
        Bits.le64(xs, init + 12));
    }
    final int taillen = len - W*blockno;
    final int tailinit = off + W*blockno;
    long b0 = 0, b1 = 0, b2 = 0, b3 = 0;
    if (taillen < 4) {
      b0 = Bits.le64tail(xs, tailinit, taillen);
    } else {
      b0 = Bits.le64(xs, tailinit);
      if (taillen < 8) {
        b1 = Bits.le64tail(xs, tailinit + 4, taillen - 4);
      } else {
        b1 = Bits.le64(xs, tailinit + 4);
        if (taillen < 12) {
          b2 = Bits.le64tail(xs, tailinit + 8, taillen - 8);
        } else {
          b2 = Bits.le64(xs, tailinit + 8);
          b3 = Bits.le64tail(xs, tailinit + 12, taillen - 12);
        }
      }
    }
    kernel.tail(b0, b1, b2, b3, 2*taillen, 2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final int W = 16;
    final Kernel256 kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      final int init = off + W*i;
      kernel.block(
        Bits.le64(xs, init),
        Bits.le64(xs, init + 4),
        Bits.le64(xs, init + 8),
        Bits.le64(xs, init + 12));
    }
    final int taillen = len - W*blockno;
    final int tailinit = off + W*blockno;
    long b0 = 0, b1 = 0, b2 = 0, b3 = 0;
    if (taillen < 4) {
      b0 = Bits.le64tail(xs, tailinit, taillen);
    } else {
      b0 = Bits.le64(xs, tailinit);
      if (taillen < 8) {
        b1 = Bits.le64tail(xs, tailinit + 4, taillen - 4);
      } else {
        b1 = Bits.le64(xs, tailinit + 4);
        if (taillen < 12) {
          b2 = Bits.le64tail(xs, tailinit + 8, taillen - 8);
        } else {
          b2 = Bits.le64(xs, tailinit + 8);
          b3 = Bits.le64tail(xs, tailinit + 12, taillen - 12);
        }
      }
    }
    kernel.tail(b0, b1, b2, b3, 2*taillen, 2*len);
    return kernel.hash64();
  }
}
