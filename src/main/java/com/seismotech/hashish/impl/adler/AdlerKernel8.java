package com.seismotech.hashish.impl.adler;

import com.seismotech.ground.util.Bits;
import com.seismotech.hashish.api.Kernel8;

public class AdlerKernel8 implements Kernel8 {
  protected int a;
  protected int b;

  public AdlerKernel8() {
    this.a = 1;
    this.b = 0;
  }

  protected AdlerKernel8(AdlerKernel8 other) {
    this.a = other.a;
    this.b = other.b;
  }

  @Override
  public int hash32() {
    return (Adler.mod(b) << 16) | Adler.mod(a);
  }

  @Override
  public Kernel8 clone() {return new AdlerKernel8(this);}

  @Override
  public void block(byte v) {
    final int uv = Bits.ubyte(v);
    a = Adler.chop(a + uv);
    b = Adler.chop(b + a);
  }

  @Override
  public void finish(long totallen) {}

  @Override
  public String toString() {
    return getClass().getName() + "[a=" + a + ", b=" + b + "]";
  }
}
