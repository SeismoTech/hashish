package com.seismotech.hashish.impl.murmur3;

import static java.lang.Integer.rotateLeft;
import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Kernel128;

public class Murmur128x32Kernel implements Kernel128 {

  public static final int C1 = 0x239b961b;
  public static final int C2 = 0xab0e9789;
  public static final int C3 = 0x38b34ae5;
  public static final int C4 = 0xa1e38b93;

  private int h1, h2, h3, h4;

  public Murmur128x32Kernel(int seed) {
    this.h1 = this.h2 = this.h3 = this.h4 = seed;
  }

  private Murmur128x32Kernel(Murmur128x32Kernel other) {
    this.h1 = other.h1;
    this.h2 = other.h2;
    this.h3 = other.h3;
    this.h4 = other.h4;
  }

  @Override public long hash64() {return hash128H() ^ hash128L();}
  @Override public long hash128H() {return Bits.concat(h4,h3);}
  @Override public long hash128L() {return Bits.concat(h2,h1);}

  @Override
  public Kernel128 clone() {return new Murmur128x32Kernel(this);}

  @Override
  public void block(long low, long high) {
    h1 = (rotateLeft(h1 ^ bmix1(Bits.low(low)  ), 19) + h2) * 5 + 0x561ccd1b;
    h2 = (rotateLeft(h2 ^ bmix2(Bits.high(low) ), 17) + h3) * 5 + 0x0bcaa747;
    h3 = (rotateLeft(h3 ^ bmix3(Bits.low(high) ), 15) + h4) * 5 + 0x96cd1c35;
    h4 = (rotateLeft(h4 ^ bmix4(Bits.high(high)), 13) + h1) * 5 + 0x32ac3b17;
  }

  @Override
  public void tail(long low, long high, int taillen, long totallen) {
    if (taillen > 0) {
      if (taillen < 8) {
        low &= (1L << 8*taillen) - 1;
        high = 0;
      } else {
        high &= (1L << 8*(taillen-8)) - 1;
      }
      if (taillen > 4) {
        if (taillen > 8) {
          if (taillen > 12) {
            h4 ^= bmix4(Bits.high(high));
          }
          h3 ^= bmix3(Bits.low(high));
        }
        h2 ^= bmix2(Bits.high(low));
      }
      h1 ^= bmix1(Bits.low(low));
    }
    h1 ^= totallen;  h2 ^= totallen;  h3 ^= totallen;  h4 ^= totallen;
    h1 += h2 + h3 + h4;  h2 += h1;  h3 += h1;  h4 += h1;
    h1 = MurmurHash.fmix32(h1);
    h2 = MurmurHash.fmix32(h2);
    h3 = MurmurHash.fmix32(h3);
    h4 = MurmurHash.fmix32(h4);
    h1 += h2 + h3 + h4;  h2 += h1;  h3 += h1;  h4 += h1;
  }

  private int bmix1(int k1) {
    return rotateLeft(k1 * C1, 15) * C2;
  }

  private int bmix2(int k2) {
    return rotateLeft(k2 * C2, 16) * C3;
  }

  private int bmix3(int k3) {
    return rotateLeft(k3 * C3, 17) * C4;
  }

  private int bmix4(int k4) {
    return rotateLeft(k4 * C4, 18) * C1;
  }
}
