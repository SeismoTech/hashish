package org.seismotech.hashish.impl;

import org.seismotech.ground.mem.Bits;
import org.seismotech.hashish.api.Hasher;
import org.seismotech.hashish.api.Hashing;
import org.seismotech.hashish.api.Kernel8X;

public abstract class HashingKernel8X extends BareHashing implements Hashing {

  protected abstract Kernel8X newKernel();

  @Override
  public Hasher hasher() {
    return new HasherKernel8X(newKernel());
  }

  @Override
  public long hash(byte x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(1);
    return kernel.hash64();
  }

  @Override
  public long hash(char x) {
    return hash((short) x);
  }

  @Override
  public long hash(short x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(2);
    return kernel.hash64();
  }

  @Override
  public long hash(int x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(4);
    return kernel.hash64();
  }

  @Override
  public long hash(long x) {
    final Kernel8X kernel = newKernel();
    kernel.block(x);
    kernel.finish(8);
    return kernel.hash64();
  }

  @Override
  public long hash(byte[] xs, int off, int len) {
    return hash1(xs, off, len);
  }

  private long hash0(byte[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    final int blockno = len / 8;
    for (int i = 0; i < blockno; i++) {
      kernel.block((long) Bits.le64(xs, off + 8*i));
    }
    final int taillen = len - 8*blockno;
    kernel.block(xs, off + 8*blockno, taillen);
    kernel.finish(len);
    return kernel.hash64();
  }

  private long hash1(byte[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    final int blocksize = kernel.preferredBlockSize(len);
    final int blockno = len / blocksize;
    for (int i = 0; i < blockno; i++) {
      kernel.block(xs, off + i*blocksize);
    }
    final int taillen = len - blockno*blocksize;
    kernel.block(xs, off + blockno*blocksize, taillen);
    kernel.finish(len);
    return kernel.hash64();
  }

  public long hash(char[] xs, int off, int len) {
    final int W = 4;
    final Kernel8X kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i));
    }
    for (int i = W*blockno; i < len; i++) {
      kernel.block((short) xs[i]);
    }
    kernel.finish(2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(String xs, int off, int len) {
    final int W = 4;
    final Kernel8X kernel = newKernel();
    final int blockno = len / W;
    for (int i = 0; i < blockno; i++) {
      kernel.block(Bits.le64(xs, off + W*i));
    }
    for (int i = W*blockno; i < len; i++) {
      kernel.block((short) xs.charAt(i));
    }
    kernel.finish(2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(short[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    for (int i = 0; i < len; i++) kernel.add(xs[off+i]);
    kernel.finish(2*len);
    return kernel.hash64();
  }

  @Override
  public long hash(int[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    for (int i = 0; i < len; i++) kernel.add(xs[off+i]);
    kernel.finish(4*len);
    return kernel.hash64();
  }

  @Override
  public long hash(long[] xs, int off, int len) {
    final Kernel8X kernel = newKernel();
    for (int i = 0; i < len; i++) kernel.add(xs[off+i]);
    kernel.finish(8*len);
    return kernel.hash64();
  }
}
