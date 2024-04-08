package com.seismotech.hashish.api;

public interface Kernel128 extends Hash {
  Kernel128 clone();
  void block(long low, long high);
  void tail(long low, long high, int taillen, long totallen);
}
