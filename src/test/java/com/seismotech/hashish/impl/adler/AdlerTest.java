package com.seismotech.hashish.impl.adler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.util.zip.Adler32;

import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.test.Contester;

class AdlerTest {

  static Stream<Hashing> adlerVariants() {
    return Stream.of(
      AdlerHashing8.THE,
      AdlerHashing8X.THE,
      AdlerHashing8Vector.THE);
  }
  
  @ParameterizedTest
  @MethodSource("adlerVariants")
  void compatibleWithWikipediaExample(Hashing hashing) {
    final byte[] wikipedia = "Wikipedia".getBytes(StandardCharsets.UTF_8);
    final long hash = hashing.hash(wikipedia);
    assertEquals(0x11E60398, hash);
  }

  @ParameterizedTest
  @MethodSource("adlerVariants")
  void compatibleWithNativeAdlerOnEmptyArray(Hashing hashing) {
    final Adler32 ref = new Adler32();
    checkRef(ref, hashing, new byte[0], 0);
  }

  @ParameterizedTest
  @MethodSource("adlerVariants")
  void compatibleWithNativeAdlerOnRandomArrays(Hashing hashing) {
    final Adler32 ref = new Adler32();
    final int R = 100;
    final int MAX = 1*1024*1024;
    final Contester contest = new Contester();
    final byte[] data = new byte[MAX];
    for (int i = 0; i < R; i++) {
      final int n = contest.randomFill(data);
      checkRef(ref, hashing, data, n);
    }
  }

  void checkRef(Adler32 ref, Hashing hashing, byte[] bs, int len) {
    ref.reset();
    ref.update(bs, 0, len);
    final long expected = ref.getValue();
    final long computed = hashing.hash(bs, 0, len);
    //System.err.println(n + " -> " + expected + " ~ " + computed);
    assertEquals(expected, computed);
  }
}
