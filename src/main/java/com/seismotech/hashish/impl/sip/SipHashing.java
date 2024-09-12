package com.seismotech.hashish.impl.sip;

import com.seismotech.hashish.impl.HashingKernel64;

public abstract class SipHashing extends HashingKernel64 {

  protected final long k0, k1;

  protected SipHashing(long k0, long k1) {
    this.k0 = k0;
    this.k1 = k1;
  }

  public static class _1_3 extends SipHashing {
    public _1_3(long k0, long k1) {super(k0,k1);}
    @Override
    protected SipHashKernel newKernel() {return new SipHashKernel._1_3(k0,k1);}
  }

  public static class _1_4 extends SipHashing {
    public _1_4(long k0, long k1) {super(k0,k1);}
    @Override
    protected SipHashKernel newKernel() {return new SipHashKernel._1_4(k0,k1);}
  }

  public static class _2_4 extends SipHashing {
    public _2_4(long k0, long k1) {super(k0,k1);}
    @Override
    protected SipHashKernel newKernel() {return new SipHashKernel._2_4(k0,k1);}
  }

  public static class _4_8 extends SipHashing {
    public _4_8(long k0, long k1) {super(k0,k1);}
    @Override
    protected SipHashKernel newKernel() {return new SipHashKernel._4_8(k0,k1);}
  }
}
