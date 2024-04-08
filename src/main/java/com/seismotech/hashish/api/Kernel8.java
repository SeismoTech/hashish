package com.seismotech.hashish.api;

public interface Kernel8 extends Hash {
  Kernel8 clone();
  void block(byte v);
  void finish(long totallen);
}
