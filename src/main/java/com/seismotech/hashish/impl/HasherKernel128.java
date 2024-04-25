package com.seismotech.hashish.impl;

import static com.seismotech.ground.math.DMath.cdiv;
import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Kernel128;

public class HasherKernel128 implements Hasher {

  private final Kernel128 kernel;
  private byte[] buf;
  private int used;
  private long total;

  private static final int W = 16;

  public HasherKernel128(Kernel128 kernel) {
    this.kernel = kernel;
    this.buf = new byte[W+8];
    this.used = 0;
    this.total = 0;
  }

  private boolean mayFlush() {
    if (W <= used) {flush(); return true;}
    else return false;
  }

  private void flush() {
    innerFlush();
    used -= W;
  }

  private void innerFlush() {
    kernel.block(Bits.le64(buf, 0), Bits.le64(buf, W/2));
    Bits.le64(buf, 0, Bits.le64(buf, W));
    total += W;
  }

  @Override public long hash64() {
    //FIXME: This is problably returning a 128 bit hash
    final Kernel128 fernel = kernel.clone();
    fernel.tail(Bits.le64(buf,0), Bits.le64(buf,8), used, total+used);
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
      final int prefix = Math.min(W-used, len);
      System.arraycopy(xs,off, buf,used, prefix);
      used += prefix;
      if (!mayFlush()) return this;
      i = prefix;
    }
    for (; i <= len-W; i+=W, total+=W) {
      kernel.block(Bits.le64(xs,off+i), Bits.le64(xs,off+i+W/2));
    }
    System.arraycopy(xs,off+i, buf,0, len-i);
    used = len-i;
    return this;
  }

  @Override public Hasher add(char[] xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      i = Math.min(cdiv(W-used,2), len);
      Bits.copy(xs,off, buf,used, i);
      used += 2*i;
      if (!mayFlush()) return this;
    }
    //used is 0 or 1
    final int limit = len - W/2;
    for (; i <= limit; i+=W/2) {
      Bits.copy(xs,off+i, buf,used, W/2);
      innerFlush();
    }
    Bits.copy(xs,off+i, buf,used, len-i);
    used += 2*(len-i);
    return this;
  }

  @Override public Hasher add(String xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      i = Math.min(cdiv(W-used,2), len);
      Bits.copy(xs,off, buf,used, i);
      used += 2*i;
      if (!mayFlush()) return this;
    }
    //used is 0 or 1
    final int limit = len - W/2;
    for (; i <= limit; i+=W/2) {
      Bits.copy(xs,off+i, buf,used, W/2);
      innerFlush();
    }
    Bits.copy(xs,off+i, buf,used, len-i);
    used += 2*(len-i);
    return this;
  }
}
