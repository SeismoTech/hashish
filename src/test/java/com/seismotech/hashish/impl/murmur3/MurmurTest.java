package com.seismotech.hashish.impl.murmur3;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.MurmurHash3;

import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.test.Contester;

class MurmurTest {

  @Nested
  class Murmur32 {
    @Test
    void compatibleWithApacheMurmurOnLongs() {
      final int T = 200;
      final Contester gen = new Contester();
      for (int seed: MurmurTestData.SEEDS) {
        final Murmur32Hashing murmur = new Murmur32Hashing(seed);
        for (int i = 0; i < T; i++) {
          final long contest = gen.randomLong();
          //Apache is making the *surprising* assumption that longs are
          //big-endian
          final long tsetnoc = Long.reverseBytes(contest);
          final int expected = MurmurHash3.hash32(tsetnoc, seed);
          final int computed = (int) murmur.hash(contest);
          assertEquals(expected, computed);
        }
      }
    }

    @Test
    void compatibleWithApacheMurmurOnBytes() {
      final int T = 200;
      final Contester gen = new Contester();
      final byte[] contest = new byte[1*1024*1024];
      for (int seed: MurmurTestData.SEEDS) {
        final Murmur32Hashing murmur = new Murmur32Hashing(seed);
        for (int i = 0; i < T; i++) {
          final int n = gen.randomFill(contest);
          final int expected = MurmurHash3.hash32x86(contest, 0, n, seed);
          final int computed = (int) murmur.hash(contest, 0, n);
          assertEquals(expected, computed);
        }
      }
    }
  }

  @Nested
  class Murmur128x64 {
    @Test
    void compatibleWithApacheMurmurOnBytes() {
      final int T = 200;
      final Contester gen = new Contester();
      final byte[] contest = new byte[1*1024*1024];
      for (int seed: MurmurTestData.SEEDS) {
        final Murmur128x64Hashing murmur = new Murmur128x64Hashing(seed);
        for (int i = 0; i < T; i++) {
          final int n = gen.randomFill(contest);
          final long[] apache = MurmurHash3.hash128x64(contest, 0, n, seed);
          final long expected = apache[0] ^ apache[1];
          final long computed = murmur.hash(contest, 0, n);
          assertEquals(expected, computed);
        }
      }
    }
  }

  @Test
  void glassTest() {
    final byte[] verse = MurmurTestData.VERSE
      .getBytes(StandardCharsets.UTF_8);
    for (int iseed = 0; iseed < MurmurTestData.SEEDS.length; iseed++) {
      final int seed = MurmurTestData.SEEDS[iseed];
      final Hashing[] hashing = {
        new Murmur32Hashing(seed),
        new Murmur128x32Hashing(seed),
        new Murmur128x64Hashing(seed),
      };
      for (int len = 0; len <= verse.length; len++) {
        for (int ihash = 0; ihash < hashing.length; ihash++) {
          assertEquals(
            MurmurTestData.EXPECTED[len][iseed][ihash],
            hashing[ihash].hash(verse, 0, len));
        }
      }
    }
  }
}
