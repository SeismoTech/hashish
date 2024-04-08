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
    return (int) Bits.LE32_FROM_BYTES.get(xs, off);
  }

  public static long le64(byte[] xs, int off) {
    return (long) Bits.LE64_FROM_BYTES.get(xs, off);
  }

  public static int tailLE32(byte[] xs, int off, int len) {
    int tail = 0;
    switch (len) {
    case 3: tail |= ubyte(xs[off+2]) << 16;
    case 2: tail |= ubyte(xs[off+1]) << 8;
    case 1: tail |= ubyte(xs[off]);
    case 0: return tail;
    }
    return tailLE32IllegalLength(len);
  }

  public static long tailLE64(byte[] xs, int off, int len) {
    return (len < 4) ? tailLE32(xs, off, len)
      : (uint((int) LE32_FROM_BYTES.get(xs, off))
          | (uint(tailLE32(xs, off+4, len-4)) << 32));
  }

  private static int tailLE32IllegalLength(final int len) {
    throw new IllegalArgumentException(
      "`tailLE32` called with tail length " + len
      + ", but a value in the range [0,3] expected");
  }
}
