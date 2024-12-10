package org.seismotech.hashish.api;

public interface Hashing {

  Hasher hasher();

  long hash(byte x);

  long hash(char x);

  long hash(short x);

  long hash(int x);

  long hash(long x);

  long hash(float x);

  long hash(double x);

  long hash(byte[] xs);

  long hash(byte[] xs, int off, int len);

  long hash(char[] xs);

  long hash(char[] xs, int off, int len);

  long hash(String xs);

  long hash(String xs, int off, int len);

  long hash(short[] xs);

  long hash(short[] xs, int off, int len);

  long hash(int[] xs);

  long hash(int[] xs, int off, int len);

  long hash(long[] xs);

  long hash(long[] xs, int off, int len);

  // long hash(float[] xs);

  // long hash(float[] xs, int off, int len);

  // long hash(double[] xs);

  // long hash(double[] xs, int off, int len);
}
