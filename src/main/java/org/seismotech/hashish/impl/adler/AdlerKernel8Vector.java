package org.seismotech.hashish.impl.adler;

import jdk.incubator.vector.*;

import static org.seismotech.ground.util.Bits.ubyte;
import static org.seismotech.ground.util.Bits.ushort;
import org.seismotech.hashish.api.Kernel8X;

/**
 * An attempt to
 */
public class AdlerKernel8Vector extends AdlerKernel8X {

  private static final VectorSpecies<Byte> SPECIES_BYTE;
  private static final VectorSpecies<Short> SPECIES_SHORT;
  private static final int BYTE_LEN;
  private static final int SHORT_LEN;
  //private static final VectorMask<Byte> SHIFT;
  private static final VectorShuffle<Byte> EXCHANGE;
  private static final ShortVector UNSIGNED;
  private static final ShortVector SCALE;
  private static final short[] SCALE_ARRAY;

  static {
    final VectorSpecies<Byte> tmp = ByteVector.SPECIES_PREFERRED;
    if (tmp.vectorByteSize() <= 32) {
      SPECIES_BYTE = tmp;
      SPECIES_SHORT = ShortVector.SPECIES_PREFERRED;
    } else {
      SPECIES_BYTE = ByteVector.SPECIES_256;
      SPECIES_SHORT = ShortVector.SPECIES_256;
    }
    BYTE_LEN = SPECIES_BYTE.length();
    SHORT_LEN = SPECIES_SHORT.length();
    //SHIFT = VectorMask.fromLong(SPECIES_BYTE, (~0L) << SHORT_LEN);
    EXCHANGE = VectorShuffle.iota(SPECIES_BYTE, SHORT_LEN, 1, true);
    UNSIGNED = ShortVector.broadcast(SPECIES_SHORT, (short) 0xFF);
    SCALE_ARRAY = new short[SHORT_LEN];
    for (int i = 0; i < SCALE_ARRAY.length; i++)
      SCALE_ARRAY[i] = (short) (SHORT_LEN-i);
    SCALE = ShortVector.fromArray(SPECIES_SHORT, SCALE_ARRAY, 0);
  }

  public AdlerKernel8Vector() {super();}

  protected AdlerKernel8Vector(AdlerKernel8Vector other) {super(other);}

  @Override
  public Kernel8X clone() {return new AdlerKernel8Vector(this);}

  @Override
  public int preferredBlockSize(int lenIgnored) {
    return SPECIES_BYTE.length();
  }

  @Override
  public void block(byte[] bs, int off) {
    ByteVector bv = ByteVector.fromArray(SPECIES_BYTE, bs, off);
    ShortVector sv = (ShortVector) bv.convert(VectorOperators.B2S, 0);
    sv = sv.and(UNSIGNED);
    final int a0 = sv.reduceLanes(VectorOperators.ADD);
    sv = sv.mul(SCALE);
    final int b0 = ushort(sv.reduceLanes(VectorOperators.ADD));

    //bv = bv.compress(SHIFT);
    bv = bv.rearrange(EXCHANGE);
    sv = (ShortVector) bv.convert(VectorOperators.B2S, 0);
    sv = sv.and(UNSIGNED);
    final int a1 = sv.reduceLanes(VectorOperators.ADD);
    sv = sv.mul(SCALE);
    final int b1 = ushort(sv.reduceLanes(VectorOperators.ADD));

    // b = b + SLEN*a + b0 + SLEN*a + SLEN*a0 + b1
    //   = b + SLEN(a + a + a0) + b0 + b1
    b = Adler.chop(b + SHORT_LEN * (a + a + a0) + b0 + b1);
    a = Adler.chop(a + a0 + a1);
  }
}
