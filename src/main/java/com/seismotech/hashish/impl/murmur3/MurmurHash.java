package com.seismotech.hashish.impl.murmur3;

/**
 * <b>Definition</b>
 * There are 3 MurmurHash3 variants.
 * There is no formal definition.
 * There is only a [reference implementation](
 *  https://github.com/aappleby/smhasher/blob/master/src/MurmurHash3.cpp)
 *
 * <p><b>Variants</b>
 * There are 3 MurmurHash3 variants:
 * - MurmurHash3_32:
 *   works on 32 bits blocks and produces a 32 bits hash value.
 * - MurmurHash3_128_x86:
 *   works on 128 bits blocks, divided in 4 32 bits subblocks,
 *   and produces a 128 bits hash value.
 * - MurmurHash3_128_x64:
 *   works on 128 bits blocks, divided in 2 64 bits subblocks,
 *   and produces a 128 bits hash value.
 *
 * <p><b>Endianness</b>
 * In the reference implementation doesn't specify the correct endianness,
 * although it says
 * <i>if your platform needs to do endian-swapping or can only handle aligned
 * reads, do the conversion here</i>.
 * But, how do we know if we need to do endian-swapping?
 *
 * Other projects seem to have a clearer opinion on this:
 * - MurmurHash bindings for Cython say little-endian:
 * https://github.com/explosion/murmurhash/blob/master/murmurhash/MurmurHash3.cpp#L63
 * - Apache Commons Codec also says little-endian:
 * https://commons.apache.org/proper/commons-codec/jacoco/org.apache.commons.codec.digest/MurmurHash3.java.html
 */
public class MurmurHash {

  public static int fmix32(int h) {
    h ^= h >>> 16;
    h *= 0x85ebca6b;
    h ^= h >>> 13;
    h *= 0xc2b2ae35;
    h ^= h >>> 16;
    return h;
  }

  public static long fmix64(long h) {
    h ^= h >>> 33;
    h *= 0xff51afd7ed558ccdL;
    h ^= h >>> 33;
    h *= 0xc4ceb9fe1a85ec53L;
    h ^= h >>> 33;
    return h;
  }
}
