package org.seismotech.hashish.api;

public interface KernelX extends Hash {
  KernelX clone();
  int preferredBlockSize(int len);
  void block(byte[] block, int off);
  void tail(byte[] block, int off, int len);
}
