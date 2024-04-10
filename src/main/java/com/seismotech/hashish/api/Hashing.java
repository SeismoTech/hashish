package com.seismotech.hashish.api;

public interface Hashing {

  long hash(byte x);

  long hash(char x);

  long hash(short x);

  long hash(int x);

  long hash(long x);

  default long hash(float x) {
    return hash(Float.floatToIntBits(x));
  }

  default long hash(double x) {
    return hash(Double.doubleToLongBits(x));
  }

  default long hash(byte[] xs) {
    return hash(xs, 0, xs.length);
  }

  long hash(byte[] xs, int off, int len);

  default long hash(char[] xs) {
    return hash(xs, 0, xs.length);
  }

  long hash(char[] xs, int off, int len);

  // long hash(short[] xs);

  // long hash(short[] xs, int off, int len);

  // long hash(int[] xs);

  // long hash(int[] xs, int off, int len);

  // long hash(long[] xs);

  // long hash(long[] xs, int off, int len);

  // long hash(float[] xs);

  // long hash(float[] xs, int off, int len);

  // long hash(double[] xs);

  // long hash(double[] xs, int off, int len);

  // long hash(String xs);

  // long hash(String xs, int off, int len);
}
