package org.seismotech.hashish.impl.xx;

import static java.lang.Integer.rotateLeft;

import org.seismotech.ground.util.Bits;
import org.seismotech.hashish.api.Kernel128;

public class XX32Kernel implements Kernel128 {

  static final int PRIME32_1 = 0x9E3779B1;
  static final int PRIME32_2 = 0x85EBCA77;
  static final int PRIME32_3 = 0xC2B2AE3D;
  static final int PRIME32_4 = 0x27D4EB2F;
  static final int PRIME32_5 = 0x165667B1;

  private int acc1, acc2, acc3, acc4;
  private int acc;

  public XX32Kernel(int seed) {
    this.acc1 = seed + PRIME32_1 + PRIME32_2;
    this.acc2 = seed + PRIME32_2;
    this.acc3 = seed;
    this.acc4 = seed - PRIME32_1;
    this.acc = 0;
  }

  private XX32Kernel(XX32Kernel other) {
    this.acc1 = other.acc1;
    this.acc2 = other.acc2;
    this.acc3 = other.acc3;
    this.acc4 = other.acc4;
    this.acc = other.acc;
  }

  @Override public int hash32() {return acc;}

  @Override
  public Kernel128 clone() {return new XX32Kernel(this);}

  @Override
  public void block(long low, long high) {
    acc1 = round(acc1, Bits.low(low));
    acc2 = round(acc2, Bits.high(low));
    acc3 = round(acc3, Bits.low(high));
    acc4 = round(acc4, Bits.high(high));
  }

  @Override
  public void tail(long low, long high, int taillen, long totallen) {
    if (totallen < 16) {
      //totallen < 16 => block has not been called => acc3 == seed
      acc = acc3 + PRIME32_5;
    } else {
      acc = rotateLeft(acc1, 1) + rotateLeft(acc2, 7)
        + rotateLeft(acc3, 12) + rotateLeft(acc4, 18);
    }

    acc += (int) totallen;

    switch (taillen) {
    case 12: round4(low);  low >>>= 32;  low |= high << 32;
    case 8:  round4(low);  low >>>= 32;
    case 4:  round4(low);
    case 0:
      break;

    case 13: round4(low);  low >>>= 32;  low |= high << 32;  high >>>= 32;
    case 9:  round4(low);  low >>>= 32;  low |= high << 32;
    case 5:  round4(low);  low >>>= 32;
             round1(low);
      break;

    case 14: round4(low);  low >>>= 32;  low |= high << 32;  high >>>= 32;
    case 10: round4(low);  low >>>= 32;  low |= high << 32;
    case 6:  round4(low);  low >>>= 32;
             round1(low);  low >>>= 8;
             round1(low);
      break;

    case 15: round4(low);  low >>>= 32;  low |= high << 32;  high >>>= 32;
    case 11: round4(low);  low >>>= 32;  low |= high << 32;
    case 7:  round4(low);  low >>>= 32;
    case 3:  round1(low);  low >>>= 8;
    case 2:  round1(low);  low >>>= 8;
    case 1:  round1(low);
      break;
    }

    acc = avalanche(acc);
  }

  private static int round(int acc, int lane) {
    return rotateLeft(acc + lane * PRIME32_2, 13) * PRIME32_1;
  }

  private static int round4(int acc, int lane) {
    return rotateLeft(acc + lane * PRIME32_3, 17) * PRIME32_4;
  }

  private static int round1(int acc, int lane) {
    return rotateLeft(acc + lane * PRIME32_5, 11) * PRIME32_1;
  }

  private void round4(long low) {
    acc = round4(acc, (int) low);
  }

  private void round1(long low) {
    acc = round1(acc, ((int) low) & 0xFF);
  }

  private static int avalanche(int acc) {
    acc ^= acc >>> 15;
    acc *= PRIME32_2;
    acc ^= acc >>> 13;
    acc *= PRIME32_3;
    acc ^= acc >>> 16;
    return acc;
  }
}
