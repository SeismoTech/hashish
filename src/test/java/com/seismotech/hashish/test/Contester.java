package com.seismotech.hashish.test;

import java.util.Random;

/**
 * Random contests generator.
 */
public class Contester {

  private static final int BUFFER_SIZE = 1024;

  private final Random rnd;
  private byte[] buffer;

  public Contester() {
    this.rnd = new Random();
    this.buffer = null;
  }

  private byte[] buffer() {
    if (buffer == null) buffer = new byte[BUFFER_SIZE];
    return buffer;
  }

  public int randomInt() {return rnd.nextInt();}
  public long randomLong() {return rnd.nextLong();}

  public void randomFulfill(byte[] bs) {
    rnd.nextBytes(bs);
  }

  public int randomFill(byte[] bs) {
    return randomFill(bs, 0, bs.length);
  }

  public int randomFill(byte[] bs, int min) {
    return randomFill(bs, min, bs.length);
  }

  public int randomFill(byte[] bs, int min, int max) {
    return randomFill(bs, min, max, 1);
  }

  public int randomFill(byte[] bs, int min, int max, int by) {
    final int n = (min == max) ? max
      : (min + by*rnd.nextInt((max-min+1)/by));
    if (n == bs.length || bs.length < BUFFER_SIZE) rnd.nextBytes(bs);
    else {
      final byte[] tmp = buffer();
      int off = 0;
      while (off < n) {
        rnd.nextBytes(tmp);
        final int copy = Math.min(tmp.length, n - off);
        System.arraycopy(tmp,0, bs,off, copy);
        off += copy;
      }
    }
    return n;
  }
}
