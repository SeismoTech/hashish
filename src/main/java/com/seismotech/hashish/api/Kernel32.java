package com.seismotech.hashish.api;

public interface Kernel32 extends Hash {
  Kernel32 clone();
  void block(int block);
  void tail(int tail, int taillen, long totallen);
}
