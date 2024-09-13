package org.seismotech.hashish.impl.sip;

import static java.lang.Long.rotateLeft;
import org.seismotech.hashish.api.Kernel64;
import static org.seismotech.hashish.impl.sip.SipHash.*;

public abstract class SipHashKernel implements Kernel64 {
  private long v0, v1, v2, v3;

  public SipHashKernel(long k0, long k1) {
    this.v0 = k0 ^ C0;
    this.v1 = k1 ^ C1;
    this.v2 = k0 ^ C2;
    this.v3 = k1 ^ C3;
  }

  public SipHashKernel(SipHashKernel other) {
    this.v0 = other.v0;
    this.v1 = other.v1;
    this.v2 = other.v2;
    this.v3 = other.v3;
  }

  abstract public SipHashKernel clone();

  @Override
  public long hash64() {return v0 ^ v1 ^ v2 ^ v3;}

  @Override
  public void block(long block) {
    v3 ^= block;
    compressionRounds();
    v0 ^= block;
  }

  @Override
  public void tail(long tail, int taillen, long totallen) {
    //Add one last block
    final long mask = ~((~0L) << (8*taillen));
    block((mask & tail) | ((totallen & 0xFF) << 56));
    //Proper finalization
    v2 ^= 0xFF;
    finalizationRounds();
  }

  protected void round() {
    v0 += v1;
    v1 = rotateLeft(v1, 13);
    v1 ^= v0;
    v0 = rotateLeft(v0, 32);

    v2 += v3;
    v3 = rotateLeft(v3, 16);
    v3 ^= v2;

    v2 += v1;
    v1 = rotateLeft(v1, 17);
    v1 ^= v2;
    v2 = rotateLeft(v2, 32);

    v0 += v3;
    v3 = rotateLeft(v3, 21);
    v3 ^= v0;
  }


  protected abstract void compressionRounds();

  protected abstract void finalizationRounds();

  //----------------------------------------------------------------------
  public static final class _1_3 extends SipHashKernel {

    public _1_3(long k0, long k1) {super(k0,k1);}

    public _1_3(_1_3 other) {super(other);}

    @Override public _1_3 clone() {return new _1_3(this);}

    @Override
    protected void compressionRounds() {
      round();
    }

    @Override
    protected void finalizationRounds() {
      round();
      round();
      round();
    }
  }

  //----------------------------------------------------------------------
  public static final class _1_4 extends SipHashKernel {

    public _1_4(long k0, long k1) {super(k0,k1);}

    public _1_4(_1_4 other) {super(other);}

    @Override public _1_4 clone() {return new _1_4(this);}

    @Override
    protected void compressionRounds() {
      round();
    }

    @Override
    protected void finalizationRounds() {
      round();
      round();
      round();
      round();
    }
  }

  //----------------------------------------------------------------------
  public static final class _2_4 extends SipHashKernel {

    public _2_4(long k0, long k1) {super(k0,k1);}

    public _2_4(_2_4 other) {super(other);}

    @Override public _2_4 clone() {return new _2_4(this);}

    @Override
    protected void compressionRounds() {
      round();
      round();
    }

    @Override
    protected void finalizationRounds() {
      round();
      round();
      round();
      round();
    }
  }

  //----------------------------------------------------------------------
  public static final class _4_8 extends SipHashKernel {

    public _4_8(long k0, long k1) {super(k0,k1);}

    public _4_8(_4_8 other) {super(other);}

    @Override public _4_8 clone() {return new _4_8(this);}

    @Override
    protected void compressionRounds() {
      round();
      round();
      round();
      round();
    }

    @Override
    protected void finalizationRounds() {
      round();
      round();
      round();
      round();

      round();
      round();
      round();
      round();
    }
  }
}
