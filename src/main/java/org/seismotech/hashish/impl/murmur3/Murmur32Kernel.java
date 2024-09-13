package org.seismotech.hashish.impl.murmur3;

import static java.lang.Integer.rotateLeft;
import org.seismotech.hashish.api.Kernel32;

public class Murmur32Kernel implements Kernel32 {

  public static final int C1 = 0xcc9e2d51;
  public static final int C2 = 0x1b873593;

  public static final int C3 = 0xe6546b64;

  private int h1;

  public Murmur32Kernel(int seed) {
    this.h1 = seed;
  }

  private Murmur32Kernel(Murmur32Kernel other) {
    this.h1 = other.h1;
  }

  @Override public int hash32() {return h1;}

  @Override public Kernel32 clone() {return new Murmur32Kernel(this);}

  @Override
  public void block(int block) {
    h1 = 5 * rotateLeft(h1 ^ bmix(block), 13) + C3;
  }

  @Override
  public void tail(int tail, int taillen, long totallen) {
    if (0 < taillen) h1 ^= bmix(tail);
    h1 = MurmurHash.fmix32(h1 ^ ((int) totallen));
  }

  private int bmix(int b) {
    return rotateLeft(b * C1, 15) * C2;
  }
}
