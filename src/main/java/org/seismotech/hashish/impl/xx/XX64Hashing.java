package org.seismotech.hashish.impl.xx;

import org.seismotech.hashish.impl.HashingKernel256;

public class XX64Hashing extends HashingKernel256 {

  private final long seed;

  public XX64Hashing(long seed) {this.seed = seed;}

  @Override
  public XX64Kernel newKernel() {return new XX64Kernel(seed);}
}
