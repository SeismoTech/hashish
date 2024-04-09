package com.seismotech.hashish.impl.xx;

import static java.lang.Long.rotateLeft;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Kernel256;

public class XX64Kernel implements Kernel256 {

  static final long PRIME64_1 = 0x9E3779B185EBCA87L;
  static final long PRIME64_2 = 0xC2B2AE3D27D4EB4FL;
  static final long PRIME64_3 = 0x165667B19E3779F9L;
  static final long PRIME64_4 = 0x85EBCA77C2B2AE63L;
  static final long PRIME64_5 = 0x27D4EB2F165667C5L;

  private long acc1, acc2, acc3, acc4;
  private long acc;

  public XX64Kernel(long seed) {
    this.acc1 = seed + PRIME64_1 + PRIME64_2;
    this.acc2 = seed + PRIME64_2;
    this.acc3 = seed;
    this.acc4 = seed - PRIME64_1;
    this.acc = 0;
  }

  private XX64Kernel(XX64Kernel other) {
    this.acc1 = other.acc1;
    this.acc2 = other.acc2;
    this.acc3 = other.acc3;
    this.acc4 = other.acc4;
    this.acc = other.acc;
  }

  @Override public long hash64() {return acc;}

  @Override
  public Kernel256 clone() {return new XX64Kernel(this);}

  @Override
  public void block(long lane1, long lane2, long lane3, long lane4) {
    acc1 = round(acc1, lane1);
    acc2 = round(acc2, lane2);
    acc3 = round(acc3, lane3);
    acc4 = round(acc4, lane4);
  }

  @Override
  public void tail(long lane1, long lane2, long lane3, long lane4,
      int taillen, long totallen) {
    if (totallen < 32) {
      acc = acc3 + PRIME64_5;
    } else {
      acc = rotateLeft(acc1, 1) + rotateLeft(acc2, 7)
        + rotateLeft(acc3, 12) + rotateLeft(acc4, 18);
      acc = mergeAcc(mergeAcc(mergeAcc(mergeAcc(acc, acc1), acc2), acc3), acc4);
    }

    acc += totallen;

    long lastlane;
    if (8 <= taillen) {
      acc = round8(acc, lane1);
      if (16 <= taillen) {
        acc = round8(acc, lane2);
        if (24 <= taillen) {
          acc = round8(acc, lane3);
          lastlane = lane4;
        } else {
          lastlane = lane3;
        }
      } else {
        lastlane = lane2;
      }
    } else {
      lastlane = lane1;
    }

    if ((taillen & 4) != 0) {
      acc = round4(acc, lastlane & 0xFFFF_FFFFL);
      lastlane >>>= 32;
    }

    switch (taillen & 3) {
    case 3: acc = round1(acc, lastlane & 0xFF);  lastlane >>>= 8;
    case 2: acc = round1(acc, lastlane & 0xFF);  lastlane >>>= 8;
    case 1: acc = round1(acc, lastlane & 0xFF);
    }

    acc = avalanche(acc);
  }

  private static long round(long acc, long lane) {
    return rotateLeft(acc + lane * PRIME64_2, 31) * PRIME64_1;
  }

  private static long round8(long acc, long lane) {
    return rotateLeft(acc ^ round(0, lane), 27) * PRIME64_1 + PRIME64_4;
  }

  private static long round4(long acc, long lane) {
    return rotateLeft(acc ^ (lane * PRIME64_1), 23) * PRIME64_2 + PRIME64_3;
  }

  private static long round1(long acc, long lane) {
    return rotateLeft(acc ^ (lane * PRIME64_5), 11) * PRIME64_1;
  }

  private static long mergeAcc(long acc, long accn) {
    return (acc ^ round(0, accn)) * PRIME64_1 + PRIME64_4;
  }

  private static long avalanche(long acc) {
    acc ^= acc >>> 33;
    acc *= PRIME64_2;
    acc ^= acc >>> 29;
    acc *= PRIME64_3;
    acc ^= acc >>> 32;
    return acc;
  }
}
