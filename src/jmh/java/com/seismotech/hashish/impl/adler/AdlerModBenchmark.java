package com.seismotech.hashish.impl.adler;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@Fork(value = 1)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class AdlerModBenchmark {

  static final int BASE = 65521;
  
  @Param({"65", //"650", "6500",
          "65000", //"650000", "6500000",
          "65000000", //"650000000", "2065000000"
          })
  public int value;

  @Benchmark
  public int nativeModBM(AdlerModBenchmark ctx) {
    return ctx.value % BASE;
  }

  @Benchmark
  public int nativeRemBM(AdlerModBenchmark ctx) {
    return Integer.remainderUnsigned(ctx.value, BASE);
  }

  @Benchmark
  public int chopMulModBM(AdlerModBenchmark ctx) {
    return chopMulMod(ctx.value);
  }
  
  @Benchmark
  public int chopShiftModBM(AdlerModBenchmark ctx) {
    return chopShiftMod(ctx.value);
  }

  @Benchmark
  public int chopMulBM(AdlerModBenchmark ctx) {
    return chopMul(ctx.value);
  }
  
  @Benchmark
  public int chopShiftBM(AdlerModBenchmark ctx) {
    return chopShift(ctx.value);
  }
  
  /**
   * (x*2^16 + y) % B
   * == (x*2^16) % B + y % B
   * == x * 15 + y % B
   * == x * 15 + y
   */
  int chopMul(int a) {
    int tmp = a >>> 16;
    return (a & 0xFFFF) + 15*tmp;
  }

  int chopShift(int a) {
    int tmp = a >>> 16;
    return (a & 0xFFFF) + (tmp << 4) - tmp;
  }

  int modTiny(int a) {
    return (a < BASE) ? a : (a - BASE);
  }

  int chopMulMod(int a) {
    return modTiny(chopMul(chopMul(a)));
  }

  int chopShiftMod(int a) {
    return modTiny(chopShift(chopShift(a)));
  }
}
