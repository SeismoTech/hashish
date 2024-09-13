package org.seismotech.hashish.api;

public interface Kernel8X extends Kernel8 {
  Kernel8X clone();
  void block(short v);
  void block(int v);
  void block(long block);
  int preferredBlockSize(int len);
  void block(byte[] block, int off);
  void block(byte[] block, int off, int len);
}
