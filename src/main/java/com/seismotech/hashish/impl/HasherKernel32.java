package com.seismotech.hashish.impl;

import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Kernel32;

public class HasherKernel32 extends BareHasher implements Hasher {

  private final Kernel32 kernel;
  private byte[] buf;
  private int used;
  private long total;

  private static final int W = 4;
  private static final int MEGA = 8;
  private static final int SIZE = MEGA*W + 2*W;

  public HasherKernel32(Kernel32 kernel) {
    this.kernel = kernel;
    this.buf = new byte[SIZE];
    this.used = 0;
    this.total = 0;
  }

  private boolean tryFlush() {
    if (W <= used) {flush(); return true;}
    else return false;
  }

  private boolean mayFlush() {
    if (W*MEGA <= used) {flush(); return true;}
    else return false;
  }

  private void flush() {
    final int n = used & ~(W-1);
    for (int i = 0; i < n; i+=W) kernel.block(Bits.le32(buf, i));
    Bits.le32(buf, 0, Bits.le32(buf, n));
    used -= n;
    total += n;
  }

  @Override public long hash64() {
    tryFlush();
    final Kernel32 fernel = kernel.clone();
    fernel.tail(Bits.le32tail(buf,0,used), used, total+used);
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
    mayFlush();
    return this;
  }

  @Override public Hasher add(byte[] xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      final int prefix = Math.min(MEGA*W-used, len);
      System.arraycopy(xs,off, buf,used, prefix);
      used += prefix;
      if (!mayFlush()) return this;
      i = prefix;
    }
    for (; i <= len-W; i+=W, total+=W) kernel.block(Bits.le32(xs, off+i));
    System.arraycopy(xs,off+i, buf,0, len-i);
    used = len-i;
    return this;
  }

  @Override public Hasher add(char[] xs, int off, int len) {
    int i = 0;
    tryFlush();
    if (used % 2 == 0) {
      if (0 < used) {
        if (0 < len) {_add(xs[off+i]); i++;}
        if (!tryFlush()) return this;
      }
      for (; i <= len-2; i+=2, total+=4) kernel.block(Bits.le32(xs, off+i));
    } else {
      final int loaded = 8*used;
      int pend = Bits.le32(buf, 0) & ~(-1 << loaded);
      for (; i <= len-2; i+=2, total+=4) {
        final int block = Bits.le32(xs, off+i);
        kernel.block(pend | block << loaded);
        pend = block >>> 32-loaded;
      }
      Bits.le32(buf, 0, pend);
    }
    for (; i < len; i++) _add(xs[off+i]);
    //mayFlush();
    return this;
  }

  @Override public Hasher add(String xs, int off, int len) {
    int i = 0;
    tryFlush();
    if (used % 2 == 0) {
      if (0 < used) {
        if (0 < len) {_add(xs.charAt(off+i)); i++;}
        if (!tryFlush()) return this;
      }
      for (; i <= len-2; i+=2, total+=4) kernel.block(Bits.le32(xs, off+i));
    } else {
      final int loaded = 8*used;
      int pend = Bits.le32(buf, 0) & ~(-1 << loaded);
      for (; i <= len-2; i+=2, total+=4) {
        final int block = Bits.le32(xs, off+i);
        kernel.block(pend | block << loaded);
        pend = block >>> 32-loaded;
      }
      Bits.le32(buf, 0, pend);
    }
    for (; i < len; i++) _add(xs.charAt(off+i));
    //mayFlush();
    return this;
  }
}
