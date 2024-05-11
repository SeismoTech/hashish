package com.seismotech.hashish.api;

public interface Hasher extends Hash {
  Hasher add(byte x);

  Hasher add(char x);

  Hasher add(short x);

  Hasher add(int x);

  Hasher add(long x);

  Hasher add(float x);

  Hasher add(double x);

  Hasher add(byte[] xs);

  Hasher add(byte[] xs, int off, int len);

  Hasher add(char[] xs);

  Hasher add(char[] xs, int off, int len);

  Hasher add(String xs);

  Hasher add(String xs, int off, int len);

  Hasher addCode(Object x);
}
