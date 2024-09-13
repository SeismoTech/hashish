package org.seismotech.hashish.api;

public interface Kernel256 extends Hash {
  Kernel256 clone();
  void block(long b0, long b1, long b2, long b3);
  void tail(long b0, long b1, long b2, long b3, int taillen, long totallen);
}
