package com.seismotech.hashish.impl.adler;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import java.util.zip.Adler32;
import org.apache.commons.codec.digest.MurmurHash3;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

import com.seismotech.hashish.impl.sip.SipHashing;
import com.seismotech.hashish.impl.murmur3.Murmur32Hashing;
import com.seismotech.hashish.impl.murmur3.Murmur128x32Hashing;
import com.seismotech.hashish.impl.murmur3.Murmur128x64Hashing;

@Fork(value = 1)
@Warmup(iterations = 4, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class AllByteArrayBenchmark {

  @State(Scope.Thread)
  public static class Context {
    @Param({"4", /*"16", "256", "65536",*/ "1048576"})
    public int size;

    final Adler32 staticAdler
      = new Adler32();

    final SipHashing staticSip
      = new SipHashing._2_4(0x0706050403020100L, 0x0f0e0d0c0b0a0908L);

    final HashFunction staticGuavaMurmur128x64
      = Hashing.murmur3_128(0);
    final Murmur32Hashing staticMurmur32
      = new Murmur32Hashing(0);
    final Murmur128x32Hashing staticMurmur128x32
      = new Murmur128x32Hashing(0);
    final Murmur128x64Hashing staticMurmur128x64
      = new Murmur128x64Hashing(0);

    public byte[] data;

    @Setup(Level.Iteration)
    public void doSetup() {
      data = new byte[size];
      new Random().nextBytes(data);
    }
  }

  //@Benchmark
  public long staticNativeAdler(Context ctx) {
    ctx.staticAdler.reset();
    ctx.staticAdler.update(ctx.data);
    return ctx.staticAdler.getValue();
  }

  //@Benchmark
  public long dynamicNativeAdler(Context ctx) {
    final Adler32 adler = new Adler32();
    adler.update(ctx.data);
    return adler.getValue();
  }

  //@Benchmark
  public long sip(Context ctx) {
    return ctx.staticSip.hash(ctx.data);
  }

  //@Benchmark
  public long adler8(Context ctx) {
    return AdlerHashing8.THE.hash(ctx.data);
  }

  //@Benchmark
  public long adler8X(Context ctx) {
    return AdlerHashing8X.THE.hash(ctx.data);
  }

  //@Benchmark
  public long adler8Vector(Context ctx) {
    return AdlerHashing8Vector.THE.hash(ctx.data);
  }

  //@Benchmark
  public int apacheMurmur32(Context ctx) {
    return MurmurHash3.hash32x86(ctx.data);
  }

  //@Benchmark
  public long murmur32(Context ctx) {
    return ctx.staticMurmur32.hash(ctx.data);
  }

  //@Benchmark
  public long murmur128x32(Context ctx) {
    return ctx.staticMurmur128x32.hash(ctx.data);
  }

  @Benchmark
  public long[] apacheMurmur128x64(Context ctx) {
    return MurmurHash3.hash128x64(ctx.data);
  }

  @Benchmark
  public HashCode guavaMurmur128x64(Context ctx) {
    return ctx.staticGuavaMurmur128x64.hashBytes(ctx.data);
  }

  @Benchmark
  public long murmur128x64(Context ctx) {
    return ctx.staticMurmur128x64.hash(ctx.data);
  }
}
