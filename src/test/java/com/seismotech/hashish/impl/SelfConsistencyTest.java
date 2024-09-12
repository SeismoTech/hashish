package com.seismotech.hashish.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Random;
import java.util.stream.Stream;

import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Hasher;
import com.seismotech.hashish.test.Contester;

class SelfConsistencyTest {

  static Stream<Hashing> hashings() {
    return Stream.of(
      com.seismotech.hashish.impl.adler.AdlerHashing8.THE,
      com.seismotech.hashish.impl.adler.AdlerHashing8X.THE,
      com.seismotech.hashish.impl.adler.AdlerHashing8Vector.THE,
      new com.seismotech.hashish.impl.sip.SipHashing._2_4(0,0),
      new com.seismotech.hashish.impl.xx.XX32Hashing(0),
      new com.seismotech.hashish.impl.xx.XX64Hashing(0),
      new com.seismotech.hashish.impl.murmur3.Murmur32Hashing(0),
      new com.seismotech.hashish.impl.murmur3.Murmur128x32Hashing(0),
      new com.seismotech.hashish.impl.murmur3.Murmur128x64Hashing(0)
    );
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentOnByte(Hashing hashing) {
    for (int i = 0; i < 0x100; i++) {
      final byte b = (byte) i;
      final byte[] bs = new byte[] {b};
      final long ref = hashing.hash(bs);
      assertEquals(ref, hashing.hash(b));
      assertEquals(ref, hashing.hasher().add(b).hash64());
      assertEquals(ref, hashing.hasher().add(bs).hash64());
    }
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentOnChar(Hashing hashing) {
    for (int i = 0; i < 0x10000; i++) {
      final char b = (char) i;
      final ByteBuffer buf = buffer(2);
      buf.putChar(b);
      final long ref = hashing.hash(buf.array());
      assertEquals(ref, hashing.hash(b));
      assertEquals(ref, hashing.hasher().add(b).hash64());
      assertEquals(ref, hashing.hasher().add(buf.array()).hash64());
    }
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentOnInt(Hashing hashing) {
    final Random rnd = new Random();
    for (int i = 0; i < 10000; i++) {
      final int x = rnd.nextInt();
      final ByteBuffer buf = buffer(4);
      buf.putInt(x);
      final long ref = hashing.hash(buf.array());
      assertEquals(ref, hashing.hash(x));
      assertEquals(ref, hashing.hasher().add(x).hash64());
      assertEquals(ref, hashing.hasher().add(buf.array()).hash64());
    }
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentOnLong(Hashing hashing) {
    final Random rnd = new Random();
    for (int i = 0; i < 10000; i++) {
      final long x = rnd.nextLong();
      final ByteBuffer buf = buffer(8);
      buf.putLong(x);
      final long ref = hashing.hash(buf.array());
      assertEquals(ref, hashing.hash(x));
      assertEquals(ref, hashing.hasher().add(x).hash64());
      assertEquals(ref, hashing.hasher().add(buf.array()).hash64());
    }
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentOnCharsAndString(Hashing hashing) {
    final Contester contester = new Contester();
    final byte[] bs = new byte[64*1024];
    final char[] xs = new char[bs.length/2];
    for (int i = 0; i < 1000; i++) {
      final int n = contester.randomFill(bs, 0, bs.length, 2);
      final CharBuffer buf = buffer(bs, n).asCharBuffer();
      buf.get(xs, 0, n/2);
      final long expected = hashing.hash(bs,0,n);
      assertEquals(expected, hashing.hash(xs,0,n/2));
      assertEquals(expected, hashing.hash(new String(xs,0,n/2)));
      assertEquals(expected, hashing.hasher().add(bs,0,n).hash64());
      assertEquals(expected, hashing.hasher().add(xs,0,n/2).hash64());
      assertEquals(expected,
         hashing.hasher().add(new String(xs,0,n/2)).hash64());
    }
  }

  @ParameterizedTest
  @MethodSource("hashings")
  void consistentMixedHasher(Hashing hashing) {
    final Contester contester = new Contester();
    final byte[] bs = new byte[64*1024];
    final ByteBuffer bb = buffer(bs, bs.length);
    for (int i = 0; i < 2000; i++) {
      final int n = contester.randomFill(bs, 0, bs.length);
      bb.position(0).limit(n);
      final long expected = hashing.hash(bs,0,n);
      final Hasher her = hashing.hasher();
      while (bb.hasRemaining()) {
        final int rem = bb.remaining();
        final int m = contester.choose(rem);
        final int disc = contester.choose(8);
        switch (disc) {
        case 7:
        case 6: {
          final char[] cs = new char[m/2];
          for (int j = 0; j < cs.length; j++) cs[j] = bb.getChar();
          if (disc == 6) her.add(cs);
          else her.add(new String(cs));
          break;
        }
        case 5: {
          her.add(bs, bb.position(), m);
          bb.position(bb.position()+m);
          break;
        }
        case 4: if (8 <= rem) {her.add(bb.getLong()); break;}
        case 3: if (4 <= rem) {her.add(bb.getInt()); break;}
        case 2: if (2 <= rem) {her.add(bb.getChar()); break;}
        case 1: if (2 <= rem) {her.add(bb.getShort()); break;}
        case 0: her.add(bb.get()); break;
        }
      }
      assertEquals(expected, her.hash64());
    }
  }

  static ByteBuffer buffer(int n) {
    return ByteBuffer.allocate(n).order(ByteOrder.LITTLE_ENDIAN);
  }

  static ByteBuffer buffer(byte[] bs, int len) {
    return ByteBuffer.wrap(bs).order(ByteOrder.LITTLE_ENDIAN).limit(len);
  }
}
