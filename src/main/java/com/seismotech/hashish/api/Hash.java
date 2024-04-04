package com.seismotech.hashish.api;

public interface Hash {
  default int hash32() {return (int) hash64();}
  default long hash64() {return hash32() & 0xFFFFFFFFL;}
}
