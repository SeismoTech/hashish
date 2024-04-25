package com.seismotech.hashish.api;

import com.seismotech.ground.util.Bits;

public interface Hash {
  default int hash32() {return (int) hash64();}
  default long hash64() {return Bits.uint(hash32());}
  default long hash128H() {return 0;}
  default long hash128L() {return hash64();}
}
