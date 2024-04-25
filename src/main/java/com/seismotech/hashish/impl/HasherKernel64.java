package com.seismotech.hashish.impl;

import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Kernel64;

public class HasherKernel64 implements Hasher {

  private final Kernel64 kernel;
  private byte[] buf;
  private int used;
  private long total;

  public HasherKernel64(Kernel64 kernel) {
    this.kernel = kernel;
    this.buf = new byte[8+8];
    this.used = 0;
    this.total = 0;
  }

  private boolean mayFlush() {
    if (8 <= used) {flush(); return true;}
    else return false;
  }

  private void flush() {
    kernel.block(Bits.le64(buf, 0));
    Bits.le64(buf, 0, Bits.le64(buf, 8));
    used -= 8;
    total += 8;
  }

  @Override public long hash64() {
    final Kernel64 fernel = kernel.clone();
    fernel.tail(Bits.le64(buf,0), used, total+used);
    return fernel.hash64();
  }

  @Override public Hasher add(byte x) {
    buf[used++] = x;
    mayFlush();
    return this;
  }

  @Override public Hasher add(short x) {
    _add(x);
    mayFlush();
    return this;
  }

  private void _add(char x) {_add((short) x);}
  private void _add(short x) {
    Bits.le16(buf, used, x);
    used += 2;
  }

  @Override public Hasher add(int x) {
    Bits.le32(buf, used, x);
    used += 4;
    mayFlush();
    return this;
  }

  @Override public Hasher add(long x) {
    Bits.le64(buf, used, x);
    used += 8;
    flush();
    return this;
  }

  @Override public Hasher add(byte[] xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      final int prefix = Math.min(8-used, len);
      System.arraycopy(xs,off, buf,used, prefix);
      used += prefix;
      if (!mayFlush()) return this;
      i = prefix;
    }
    for (; i <= len-8; i+=8, total+=8) kernel.block(Bits.le64(xs, off+i));
    System.arraycopy(xs,off+i, buf,0, len-i);
    used = len-i;
    return this;
  }

  @Override public Hasher add(char[] xs, int off, int len) {
    int i = 0;
    if (used % 2 == 0) {
      if (0 < used) {
        final int prefix = Math.min(4-used/2, len);
        for (; i < prefix; i++) _add(xs[off+i]);
        if (!mayFlush()) return this;
      }
      for (; i <= len-4; i+=4, total+=8) kernel.block(Bits.le64(xs, off+i));
    } else {
      final int loaded = 8*used;
      long pend = Bits.le64(buf, 0) & ~(-1L << loaded);
      for (; i <= len-4; i+=4, total+=8) {
        final long block = Bits.le64(xs, off+i);
        kernel.block(pend | block << loaded);
        pend = block >>> 64-loaded;
      }
      Bits.le64(buf, 0, pend);
    }
    for (; i < len; i++) _add(xs[off+i]);
    mayFlush();
    return this;
  }

  @Override public Hasher add(String xs, int off, int len) {
    int i = 0;
    if (used % 2 == 0) {
      if (0 < used) {
        final int prefix = Math.min(4-used/2, len);
        for (; i < prefix; i++) _add(xs.charAt(off+i));
        if (!mayFlush()) return this;
      }
      for (; i <= len-4; i+=4, total+=8) kernel.block(Bits.le64(xs, off+i));
    } else {
      final int loaded = 8*used;
      long pend = Bits.le64(buf, 0) & ~(-1L << loaded);
      for (; i <= len-4; i+=4, total+=8) {
        final long block = Bits.le64(xs, off+i);
        kernel.block(pend | block << loaded);
        pend = block >>> 64-loaded;
      }
      Bits.le64(buf, 0, pend);
    }
    for (; i < len; i++) _add(xs.charAt(off+i));
    mayFlush();
    return this;
  }
}
