package com.seismotech.hashish.impl.murmur3;

import com.seismotech.hashish.impl.HashingKernel128;

public class Murmur128x64Hashing extends HashingKernel128 {

  private final int seed;

  public Murmur128x64Hashing(int seed) {this.seed = seed;}

  @Override
  public Murmur128x64Kernel newKernel() {return new Murmur128x64Kernel(seed);}
}
