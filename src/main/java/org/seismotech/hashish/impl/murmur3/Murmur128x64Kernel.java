package org.seismotech.hashish.impl.murmur3;

import static java.lang.Long.rotateLeft;
import org.seismotech.ground.util.Bits;
import org.seismotech.hashish.api.Kernel128;

public class Murmur128x64Kernel implements Kernel128 {

  public static final long C1 = 0x87c37b91114253d5L;
  public static final long C2 = 0x4cf5ad432745937fL;

  private long h1, h2;

  public Murmur128x64Kernel(int seed) {
    this.h1 = this.h2 = Bits.uint(seed);
  }

  private Murmur128x64Kernel(Murmur128x64Kernel other) {
    this.h1 = other.h1;
    this.h2 = other.h2;
  }

  @Override public long hash64() {return h1 ^ h2;}
  @Override public long hash128H() {return h2;}
  @Override public long hash128L() {return h1;}

  @Override
  public Kernel128 clone() {return new Murmur128x64Kernel(this);}

  @Override
  public void block(long low, long high) {
    h1 = (rotateLeft(h1 ^ bmix1(low),  27) + h2) * 5 + 0x52dce729;
    h2 = (rotateLeft(h2 ^ bmix2(high), 31) + h1) * 5 + 0x38495ab5;
  }

  @Override
  public void tail(long low, long high, int taillen, long totallen) {
    if (taillen > 0) {
      if (taillen > 8) {
        high &= (1L << 8*(taillen-8)) - 1;
        h2 ^= bmix2(high);
      } else if (taillen < 8) {
        low &= (1L << 8*(taillen-8)) - 1;
      }
      h1 ^= bmix1(low);
    }
    h1 ^= totallen;  h2 ^= totallen;
    h1 += h2;  h2 += h1;
    h1 = MurmurHash.fmix64(h1);
    h2 = MurmurHash.fmix64(h2);
    h1 += h2;  h2 += h1;
  }

  private long bmix1(long k1) {
    return rotateLeft(k1 * C1, 31) * C2;
  }

  private long bmix2(long k2) {
    return rotateLeft(k2 * C2, 33) * C1;
  }
}
