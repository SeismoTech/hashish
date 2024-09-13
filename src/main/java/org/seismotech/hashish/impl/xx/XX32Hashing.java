package org.seismotech.hashish.impl.xx;

import org.seismotech.hashish.impl.HashingKernel128;

public class XX32Hashing extends HashingKernel128 {

  private final int seed;

  public XX32Hashing(int seed) {this.seed = seed;}

  @Override
  public XX32Kernel newKernel() {return new XX32Kernel(seed);}
}
