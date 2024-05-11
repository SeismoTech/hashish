package com.seismotech.hashish.impl;

import static com.seismotech.ground.math.DMath.cdiv;
import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.api.Kernel8X;

public class HasherKernel8X extends BareHasher implements Hasher {

  private final Kernel8X kernel;
  private final int blockSize;
  private byte[] buf;
  private int used;
  private long total;

  public HasherKernel8X(Kernel8X kernel) {
    this.kernel = kernel;
    this.blockSize = kernel.preferredBlockSize(0);
    this.buf = new byte[blockSize + 8];
    this.used = 0;
    this.total = 0;
  }

  private boolean mayFlush() {
    if (blockSize <= used) {flush(); return true;}
    else return false;
  }

  private void flush() {
    innerFlush();
    used -= blockSize;
  }

  private void innerFlush() {
    kernel.block(buf, 0);
    Bits.le64(buf, 0, Bits.le64(buf, blockSize));
    total += blockSize;
  }

  @Override public long hash64() {
    final Kernel8X fernel = kernel.clone();
    fernel.block(buf, 0, used);
    fernel.finish(total+used);
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
      final int prefix = Math.min(blockSize-used, len);
      System.arraycopy(xs,off, buf,used, prefix);
      used += prefix;
      if (!mayFlush()) return this;
      i = prefix;
    }
    for (; i <= len-blockSize; i+=blockSize, total+=blockSize) {
      kernel.block(xs, off+i);
    }
    System.arraycopy(xs,off+i, buf,0, len-i);
    used = len-i;
    return this;
  }

  @Override public Hasher add(char[] xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      i = Math.min(cdiv(blockSize-used,2), len);
      Bits.copy(xs,off, buf,used, i);
      used += 2*i;
      if (!mayFlush()) return this;
    }
    //used is 0 or 1
    final int blockChars = blockSize/2, limit = len - blockChars;
    for (; i <= limit; i+=blockChars) {
      Bits.copy(xs,off+i, buf,used, blockChars);
      innerFlush();
    }
    Bits.copy(xs,off+i, buf,used, len-i);
    used += 2*(len-i);
    return this;
  }

  @Override public Hasher add(String xs, int off, int len) {
    int i = 0;
    if (0 < used) {
      i = Math.min(cdiv(blockSize-used,2), len);
      Bits.copy(xs,off, buf,used, i);
      used += 2*i;
      if (!mayFlush()) return this;
    }
    //used is 0 or 1
    final int blockChars = blockSize/2, limit = len - blockChars;
    for (; i <= limit; i+=blockChars) {
      Bits.copy(xs,off+i, buf,used, blockChars);
      innerFlush();
    }
    Bits.copy(xs,off+i, buf,used, len-i);
    used += 2*(len-i);
    return this;
  }
}
