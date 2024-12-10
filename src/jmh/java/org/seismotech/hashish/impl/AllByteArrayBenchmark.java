package org.seismotech.hashish.impl;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import java.util.zip.Adler32;
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.codec.digest.XXHash32;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;
import net.openhft.hashing.LongHashFunction;

import org.seismotech.hashish.impl.adler.AdlerHashing8;
import org.seismotech.hashish.impl.adler.AdlerHashing8X;
import org.seismotech.hashish.impl.adler.AdlerHashing8Vector;
import org.seismotech.hashish.impl.murmur3.Murmur32Hashing;
import org.seismotech.hashish.impl.murmur3.Murmur128x32Hashing;
import org.seismotech.hashish.impl.murmur3.Murmur128x64Hashing;
import org.seismotech.hashish.impl.xx.XX32Hashing;
import org.seismotech.hashish.impl.xx.XX64Hashing;
import org.seismotech.hashish.impl.sip.SipHashing;

/**
 * C2 21

 */
@Fork(value = 1)
@Warmup(iterations = 4, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class AllByteArrayBenchmark {

  @State(Scope.Thread)
  public static class Context {
    @Param({"3", /*"4", "16",*/ "31", /*"256", "65536", "1048576",*/ "1048607"})
    public int size;

    final Adler32 nativeAdler = new Adler32();

    final HashFunction guavaMurmur32 = Hashing.murmur3_32_fixed(0);
    final HashFunction guavaMurmur128x64 = Hashing.murmur3_128(0);
    final LongHashFunction ohftMurmur128x64 = LongHashFunction.murmur_3(0);
    final Murmur32Hashing murmur32 = new Murmur32Hashing(0);
    final Murmur128x32Hashing murmur128x32 = new Murmur128x32Hashing(0);
    final Murmur128x64Hashing murmur128x64 = new Murmur128x64Hashing(0);

    final XXHash32 apacheXX32 = new XXHash32(0);
    final LongHashFunction ohftXX64 = LongHashFunction.xx(0);
    final XX32Hashing xx32 = new XX32Hashing(0);
    final XX64Hashing xx64 = new XX64Hashing(0);

    final SipHashing sip
      = new SipHashing._2_4(0x0706050403020100L, 0x0f0e0d0c0b0a0908L);

    public byte[] data;

    @Setup(Level.Iteration)
    public void doSetup() {
      data = new byte[size];
      new Random().nextBytes(data);
    }
  }

  //@Benchmark
  public long nativeAdlerStatic(Context ctx) {
    ctx.nativeAdler.reset();
    ctx.nativeAdler.update(ctx.data);
    return ctx.nativeAdler.getValue();
  }

  //@Benchmark
  public long nativeAdlerDynamic(Context ctx) {
    final Adler32 adler = new Adler32();
    adler.update(ctx.data);
    return adler.getValue();
  }

  //@Benchmark
  public long hashishAdler8(Context ctx) {
    return AdlerHashing8.THE.hash(ctx.data);
  }

  //@Benchmark
  public long hashishAdler8X(Context ctx) {
    return AdlerHashing8X.THE.hash(ctx.data);
  }

  //@Benchmark
  public long hashishAdler8Vector(Context ctx) {
    return AdlerHashing8Vector.THE.hash(ctx.data);
  }

  //@Benchmark
  public int apacheMurmur32(Context ctx) {
    return MurmurHash3.hash32x86(ctx.data);
  }

  //@Benchmark
  public HashCode guavaMurmur32Static(Context ctx) {
    return ctx.guavaMurmur32.hashBytes(ctx.data);
  }

  //@Benchmark
  public long hashishMurmur32Static(Context ctx) {
    return ctx.murmur32.hash(ctx.data);
  }

  //@Benchmark
  public long hashishMurmur128x32Static(Context ctx) {
    return ctx.murmur128x32.hash(ctx.data);
  }

  @Benchmark
  public long[] apacheMurmur128x64(Context ctx) {
    return MurmurHash3.hash128x64(ctx.data);
  }

  @Benchmark
  public HashCode guavaMurmur128x64Static(Context ctx) {
    return ctx.guavaMurmur128x64.hashBytes(ctx.data);
  }

  @Benchmark
  public long ohftMurmur128x64Static(Context ctx) {
    return ctx.ohftMurmur128x64.hashBytes(ctx.data);
  }

  @Benchmark
  public long hashishMurmur128x64Static(Context ctx) {
    return ctx.murmur128x64.hash(ctx.data);
  }

  @Benchmark
  public long apacheXX32Static(Context ctx) {
    ctx.apacheXX32.reset();
    ctx.apacheXX32.update(ctx.data);
    return ctx.apacheXX32.getValue();
  }

  @Benchmark
  public long hashishXX32Static(Context ctx) {
    return ctx.xx32.hash(ctx.data);
  }

  @Benchmark
  public long ohftXX64Static(Context ctx) {
    return ctx.ohftXX64.hashBytes(ctx.data);
  }

  @Benchmark
  public long hashishXX64Static(Context ctx) {
    return ctx.xx64.hash(ctx.data);
  }

  //@Benchmark
  public long hashishSipStatic(Context ctx) {
    return ctx.sip.hash(ctx.data);
  }
}
