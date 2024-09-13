package org.seismotech.hashish.impl.adler;

import org.seismotech.hashish.impl.HashingKernel8X;

public class AdlerHashing8X extends HashingKernel8X {

  public static final AdlerHashing8X THE = new AdlerHashing8X();

  @Override
  protected AdlerKernel8X newKernel() {return new AdlerKernel8X();}
}
