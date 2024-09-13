package org.seismotech.hashish.api;

public interface Kernel8 extends Hash {
  Kernel8 clone();
  void block(byte v);
  void finish(long totallen);

  default void add(short x) {
    block((byte) x);
    block((byte) (x >>> 8));
  }

  default void add(int x) {
    block((byte) x);
    block((byte) (x >>> 8));
    block((byte) (x >>> 16));
    block((byte) (x >>> 24));
  }

  default void add(long x) {
    block((byte) x);
    block((byte) (x >>> 8));
    block((byte) (x >>> 16));
    block((byte) (x >>> 24));
    block((byte) (x >>> 32));
    block((byte) (x >>> 40));
    block((byte) (x >>> 48));
    block((byte) (x >>> 56));
  }

  default void add(byte[] xs, int off, int len) {
    for (int i = 0; i < len; i++) block(xs[off+i]);
  }

  default void add(char[] xs, int off, int len) {
    for (int i = 0; i < len; i++) {
      final char x = xs[i];
      block((byte) x);
      block((byte) (x >>> 8));
    }
  }

  default void add(String xs, int off, int len) {
    for (int i = 0; i < len; i++) {
      final char x = xs.charAt(i);
      block((byte) x);
      block((byte) (x >>> 8));
    }
  }
}
