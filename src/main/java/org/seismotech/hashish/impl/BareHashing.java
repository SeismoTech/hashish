package org.seismotech.hashish.impl;

import org.seismotech.hashish.api.Hashing;

public abstract class BareHashing implements Hashing {
  @Override
  public long hash(char x) {return hash((short) x);}

  @Override
  public long hash(float x) {return hash(Float.floatToIntBits(x));}

  @Override
  public long hash(double x) {return hash(Double.doubleToLongBits(x));}

  @Override
  public long hash(byte[] xs) {return hash(xs, 0, xs.length);}

  @Override
  public long hash(char[] xs) {return hash(xs, 0, xs.length);}

  @Override
  public long hash(String xs) {return hash(xs, 0, xs.length());}
}
