package org.seismotech.hashish.impl.adler;

/**
 * <p><b>Endianness</b>
 * No endianness problems, as Adler works on bytes.
 */
public class Adler {
  public static final int BASE = 65521;

  public static int mod(int a) {return a % BASE;}

  /**
   * Produces a number congruent mod BASE, that uses 20 bits at most,
   * assuming that {@code a} is a positive number.
   * It should be cheaper than a full module.
   *
   * <p>The math:
   * (x*2^16 + y) % B
   * = ((x*2^16) % B + y) % B
   * = (x * 15 + y) % B
   */
  public static int chop(int a) {return (a & 0xFFFF) + 15*(a >>> 16);}
}
