package com.seismotech.hashish.api;

public interface Hasher extends Hash {
  Hasher add(byte x);

  default Hasher add(char x) {return add((short) x);}

  Hasher add(short x);

  Hasher add(int x);

  Hasher add(long x);

  default Hasher add(float x) {
    return add(Float.floatToIntBits(x));
  }

  default Hasher add(double x) {
    return add(Double.doubleToLongBits(x));
  }

  default Hasher add(byte[] xs) {return add(xs, 0, xs.length);}

  Hasher add(byte[] xs, int off, int len);

  default Hasher add(char[] xs) {return add(xs, 0, xs.length);}

  Hasher add(char[] xs, int off, int len);

  default Hasher add(String xs) {return add(xs, 0, xs.length());}

  Hasher add(String xs, int off, int len);

  default Hasher addCode(Object x) {return add(x.hashCode());}
}
