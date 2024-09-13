package org.seismotech.hashish.impl.murmur3;

import org.seismotech.hashish.impl.HashingKernel32;

public class Murmur32Hashing extends HashingKernel32 {

  private final int seed;

  public Murmur32Hashing(int seed) {this.seed = seed;}

  @Override
  public Murmur32Kernel newKernel() {return new Murmur32Kernel(seed);}
}
