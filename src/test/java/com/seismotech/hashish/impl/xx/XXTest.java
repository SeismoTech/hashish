package com.seismotech.hashish.impl.xx;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.XXHash32;
import net.openhft.hashing.LongHashFunction;

import com.seismotech.hashish.util.Bits;
import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.test.Contester;

class XXTest {

  static final int[] SEEDS = {0, 0x05E15AA0, 0xF5E15AA0};

  @Nested
  class XXH32 {
    @Test
    void compatibleWithApacheOnEmpty() {
      for (int seed: SEEDS) {
        final XXHash32 axx = new XXHash32(seed);
        final XX32Hashing sxx = new XX32Hashing(seed);
        checkWithApache(axx, sxx, new byte[0], 0);
      }
    }

    @Test
    void compatibleWithApacheOnRandomBytes() {
      final int T = 200;
      final Contester gen = new Contester();
      final byte[] contest = new byte[1*1024*1024];
      for (int seed: SEEDS) {
        final XXHash32 axx = new XXHash32(seed);
        final XX32Hashing sxx = new XX32Hashing(seed);
        for (int i = 0; i < T; i++) {
          final int n = gen.randomFill(contest);
          checkWithApache(axx, sxx, contest, n);
        }
      }
    }

    void checkWithApache(XXHash32 axx, XX32Hashing sxx, byte[] bs, int n) {
      axx.reset();
      axx.update(bs, 0, n);
      final long expected = axx.getValue();
      final long computed = sxx.hash(bs, 0, n);
      assertEquals(expected, computed);
    }
  }

  @Nested
  class XXH64 {
    @Test
    void compatibleWithOpenHTFOnEmpty() {
      for (int seed: SEEDS) {
        final LongHashFunction rxx = LongHashFunction.xx(seed);
        final XX64Hashing sxx = new XX64Hashing(seed);
        checkWithOpenHTF(rxx, sxx, new byte[0], 0);
      }
    }

    @Test
    void compatibleWithOpenHTFOnRandomBytes() {
      final int T = 200;
      final Contester gen = new Contester();
      final byte[] contest = new byte[1*1024*1024];
      for (int seed: SEEDS) {
        final LongHashFunction rxx = LongHashFunction.xx(seed);
        final XX64Hashing sxx = new XX64Hashing(seed);
        for (int i = 0; i < T; i++) {
          final int n = gen.randomFill(contest);
          checkWithOpenHTF(rxx, sxx, contest, n);
        }
      }
    }

    void checkWithOpenHTF(LongHashFunction rxx, XX64Hashing sxx,
        byte[] bs, int n) {
      final long expected = rxx.hashBytes(bs, 0, n);
      final long computed = sxx.hash(bs, 0, n);
      assertEquals(expected, computed);
    }
  }
}
