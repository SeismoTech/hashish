package org.seismotech.hashish.impl.adler;

import static org.seismotech.ground.mem.Bits.ubyte;
import org.seismotech.hashish.api.Kernel8X;

public class AdlerKernel8X extends AdlerKernel8 implements Kernel8X {

  public AdlerKernel8X() {super();}

  protected AdlerKernel8X(AdlerKernel8X other) {super(other);}

  @Override
  public Kernel8X clone() {return new AdlerKernel8X(this);}

  @Override
  public void block(short block) {
    final int
      d0 = block & 0xFF,
      d1 = (block >>> 8) & 0xFF;
    int a0 = a, b0 = b;
    a0 += d0; b0 += a0;
    a0 += d1; b0 += a0;
    a = Adler.chop(a0); b = Adler.chop(b0);
  }

  @Override
  public void block(int block) {
    final int
      d0 = (int) (block & 0xFF),
      d1 = (int) ((block >>> 8) & 0xFF),
      d2 = (int) ((block >>> 16) & 0xFF),
      d3 = (int) ((block >>> 24) & 0xFF);
    int a0 = a, b0 = b;
    a0 += d0; b0 += a0;
    a0 += d1; b0 += a0;
    a0 += d2; b0 += a0;
    a0 += d3; b0 += a0;
    a = Adler.chop(a0); b = Adler.chop(b0);
  }

  @Override
  public void block(long block) {
    block0(block);
  }

  /**
   * A loop unrolling approach.
   */
  private void block0(long block) {
    final int
      d0 = (int) (block & 0xFF),
      d1 = (int) ((block >>> 8) & 0xFF),
      d2 = (int) ((block >>> 16) & 0xFF),
      d3 = (int) ((block >>> 24) & 0xFF),
      d4 = (int) ((block >>> 32) & 0xFF),
      d5 = (int) ((block >>> 40) & 0xFF),
      d6 = (int) ((block >>> 48) & 0xFF),
      d7 = (int) (block >>> 56);
    int a0 = a, b0 = b;
    a0 += d0; b0 += a0;
    a0 += d1; b0 += a0;
    a0 += d2; b0 += a0;
    a0 += d3; b0 += a0;
    a0 += d4; b0 += a0;
    a0 += d5; b0 += a0;
    a0 += d6; b0 += a0;
    a0 += d7; b0 += a0;
    a = Adler.chop(a0); b = Adler.chop(b0);
  }

  /**
   * This solution tries to use hw parallelism,
   * but it is slower that block0.
   */
  private void block1(long block) {
    final int d0 = (int) (block & 0xFF),
      d1 = (int) ((block >>> 8) & 0xFF),
      d2 = (int) ((block >>> 16) & 0xFF),
      d3 = (int) ((block >>> 24) & 0xFF),
      d4 = (int) ((block >>> 32) & 0xFF),
      d5 = (int) ((block >>> 40) & 0xFF),
      d6 = (int) ((block >>> 48) & 0xFF),
      d7 = (int) (block >>> 56);
    final int ainc = d0+d1+d2+d3 + d4+d5+d6+d7;
    final int binc = 8*a + 8*d0 + 7*d1 + 6*d2 + 5*d3 + 4*d4 + 3*d5 + 2*d6 + d7;
    a = Adler.chop(a + ainc);
    b = Adler.chop(b + binc);
  }


  @Override
  public int preferredBlockSize(int lenIgnored) {
    return 16;
  }

  @Override
  public void block(byte[] bs, int off) {
    block0(bs, off);
  }

  /**
   * A simple loop unrolling.
   */
  private void block0(byte[] bs, int off) {
    int a0 = a, b0 = b;
    a0 += ubyte(bs[off]); b0 += a0;
    a0 += ubyte(bs[off+1]); b0 += a0;
    a0 += ubyte(bs[off+2]); b0 += a0;
    a0 += ubyte(bs[off+3]); b0 += a0;
    a0 += ubyte(bs[off+4]); b0 += a0;
    a0 += ubyte(bs[off+5]); b0 += a0;
    a0 += ubyte(bs[off+6]); b0 += a0;
    a0 += ubyte(bs[off+7]); b0 += a0;
    a0 += ubyte(bs[off+8]); b0 += a0;
    a0 += ubyte(bs[off+9]); b0 += a0;
    a0 += ubyte(bs[off+10]); b0 += a0;
    a0 += ubyte(bs[off+11]); b0 += a0;
    a0 += ubyte(bs[off+12]); b0 += a0;
    a0 += ubyte(bs[off+13]); b0 += a0;
    a0 += ubyte(bs[off+14]); b0 += a0;
    a0 += ubyte(bs[off+15]); b0 += a0;
    a = Adler.chop(a0); b = Adler.chop(b0);
  }

  /**
   * An attempt to use hw parallelism, but again it was slower than block0.
   */
  private void block1(byte[] bs, int off) {
    int a0 = a, b0 = b, a1 = 0, b1 = 0;
    a0 += ubyte(bs[off]);   b0 += a0;  a1 += ubyte(bs[off+8]);  b1 += a1;
    a0 += ubyte(bs[off+1]); b0 += a0;  a1 += ubyte(bs[off+9]);  b1 += a1;
    a0 += ubyte(bs[off+2]); b0 += a0;  a1 += ubyte(bs[off+10]); b1 += a1;
    a0 += ubyte(bs[off+3]); b0 += a0;  a1 += ubyte(bs[off+11]); b1 += a1;
    a0 += ubyte(bs[off+4]); b0 += a0;  a1 += ubyte(bs[off+12]); b1 += a1;
    a0 += ubyte(bs[off+5]); b0 += a0;  a1 += ubyte(bs[off+13]); b1 += a1;
    a0 += ubyte(bs[off+6]); b0 += a0;  a1 += ubyte(bs[off+14]); b1 += a1;
    a0 += ubyte(bs[off+7]); b0 += a0;  a1 += ubyte(bs[off+15]); b1 += a1;
    a = Adler.chop(a0+a1); b = Adler.chop(b0 + 8*a0 + b1);
  }

  @Override
  public void block(byte[] bs, int off, int len) {
    int a0 = a, b0 = b;
    for (int i = 0; i < len; i++) {
      a0 += ubyte(bs[off+i]);  b0 += a0;
    }
    a = Adler.chop(a0); b = Adler.chop(b0);
  }
}
