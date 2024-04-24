package com.seismotech.hashish.util;

import java.lang.invoke.VarHandle;
import java.lang.invoke.MethodHandles;
import java.nio.ByteOrder;

public class Bits {

  //----------------------------------------------------------------------
  // Unsigned conversion

  public static int ubyte(byte n) {return n & 0xFF;}
  public static int ushort(short n) {return n & 0xFFFF;}
  public static long uint(int n) {return n & 0xFFFF_FFFFL;}

  //----------------------------------------------------------------------
  // Combination

  public static int low(long n) {return (int) n;}
  public static int high(long n) {return (int) (n >>> 32);}
  public static long concat(int high, int low) {
    return (((long) high) << 32) | uint(low);
  }

  //----------------------------------------------------------------------
  // Reading from arrays

  public static final VarHandle LE32_FROM_BYTES
    = MethodHandles.byteArrayViewVarHandle(
      int[].class, ByteOrder.LITTLE_ENDIAN);
  public static final VarHandle LE64_FROM_BYTES
    = MethodHandles.byteArrayViewVarHandle(
      long[].class, ByteOrder.LITTLE_ENDIAN);

  public static int le32(byte[] xs, int off) {
    return (int) LE32_FROM_BYTES.get(xs, off);
  }

  public static long le64(byte[] xs, int off) {
    return (long) LE64_FROM_BYTES.get(xs, off);
  }

  public static int le32tail(byte[] xs, int off, int len) {
    int tail = 0;
    switch (len) {
    case 3: tail |= ubyte(xs[off+2]) << 16;
    case 2: tail |= ubyte(xs[off+1]) << 8;
    case 1: tail |= ubyte(xs[off]);
    case 0: return tail;
    }
    return tailIllegalLength(3, len);
  }

  public static long le64tail(byte[] xs, int off, int len) {
    return (len < 4) ? le32tail(xs, off, len)
      : (uint((int) LE32_FROM_BYTES.get(xs, off))
          | (uint(le32tail(xs, off+4, len-4)) << 32));
  }

  public static int le32(char[] xs, int off) {
    return xs[off] | (xs[off+1] << 16);
  }

  public static int le32tail(char[] xs, int off, int len) {
    int tail = 0;
    switch (len) {
    case 1: tail = xs[off];
    case 0: return tail;
    }
    return tailIllegalLength(1, len);
  }

  public static int le32(String xs, int off) {
    return xs.charAt(off) | (xs.charAt(off+1) << 16);
  }

  public static int le32tail(String xs, int off, int len) {
    int tail = 0;
    switch (len) {
    case 1: tail = xs.charAt(off);
    case 0: return tail;
    }
    return tailIllegalLength(1, len);
  }

  public static long le64(char[] xs, int off) {
    return uint(le32(xs,off)) | (uint(le32(xs,off+2)) << 32);
  }

  public static long le64tail(char[] xs, int off, int len) {
    long tail = 0;
    switch (len) {
    case 3: tail = ((long) xs[off+2]) << 32;
    case 2: tail |= ((long) xs[off+1]) << 16;
    case 1: tail |= xs[off];
    case 0: return tail;
    }
    return tailIllegalLength(3, len);
  }

  public static long le64(String xs, int off) {
    return uint(le32(xs,off)) | (uint(le32(xs,off+2)) << 32);
  }

  public static long le64tail(String xs, int off, int len) {
    long tail = 0;
    switch (len) {
    case 3: tail = ((long) xs.charAt(off+2)) << 32;
    case 2: tail |= ((long) xs.charAt(off+1)) << 16;
    case 1: tail |= xs.charAt(off);
    case 0: return tail;
    }
    return tailIllegalLength(3, len);
  }

  private static int tailIllegalLength(int max, int len) {
    throw new IllegalArgumentException(
      "A tail scanner called with tail length " + len
      + ", but a value in the range [0," + max + "] expected");
  }
}
