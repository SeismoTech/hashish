package com.seismotech.hashish.impl;

import com.seismotech.hashish.api.Hasher;

public abstract class BareHasher implements Hasher {
  @Override
  public Hasher add(char x) {return add((short) x);}

  @Override
  public Hasher add(float x) {return add(Float.floatToIntBits(x));}

  @Override
  public Hasher add(double x) {return add(Double.doubleToLongBits(x));}

  @Override
  public Hasher add(byte[] xs) {return add(xs, 0, xs.length);}

  @Override
  public Hasher add(char[] xs) {return add(xs, 0, xs.length);}

  @Override
  public Hasher add(String xs) {return add(xs, 0, xs.length());}

  @Override
  public Hasher addCode(Object x) {return add(x.hashCode());}
}
