package org.seismotech.hashish.impl.adler;

import org.seismotech.hashish.impl.HashingKernel8;

public class AdlerHashing8 extends HashingKernel8 {

  public static final AdlerHashing8 THE = new AdlerHashing8();

  @Override
  protected AdlerKernel8 newKernel() {return new AdlerKernel8();}
}
