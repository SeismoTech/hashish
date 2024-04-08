package com.seismotech.hashish.impl.adler;

import com.seismotech.hashish.impl.HashingKernel8X;

public class AdlerHashing8Vector extends HashingKernel8X {

  public static final AdlerHashing8Vector THE = new AdlerHashing8Vector();

  @Override
  protected AdlerKernel8Vector newKernel() {return new AdlerKernel8Vector();}
}
