package com.seismotech.hashish.java;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

import java.util.Random;

/**
 * - OpenJDK 21, 22:
 *     doesn't make SR on {@link #arrayFull},
 *     {@link #arrayComponent2} and {@link #arrayComponen4}
 * - Graal CE 21, 22:
 *     doesn't make SR on {@link #arrayFull} and {@link #arrayComponen4}
 * - Graal EE 21:
 *     doesn't make SR on {@link #arrayFull}
 * - Zing 21: 
 *     doesn't make SR on {@link #arrayFull} and {@link #arrayComponen4}
 */
@Fork(value = 1)
@Warmup(iterations = 4, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ScalarReplacementOfArraysBenchmark {

  static final Random rnd = new Random();
  
  int x;

  @Benchmark
  public int object() {
    MyObject o = new MyObject(x);
    return o.x;
  }

  @Benchmark
  public long[] arrayFull() {
    return new long[] {x, x, x, x};
  }

  @Benchmark
  public long arrayComponent1() {
    final long[] xs = new long[] {x, x+1, x+2, x+3};
    return xs[0] + xs[1] + xs[2] + xs[3];
  }

  @Benchmark
  public long arrayComponent2() {
    final long[] xs = new long[4];
    for (int i = 0; i < 4; i++) {xs[i] = x + i;}
    return xs[0] + xs[1] + xs[2] + xs[3];
  }

  @Benchmark
  public long arrayComponent3() {
    final long[] xs = new long[4];
    xs[0] = x;
    xs[1] = x+1;
    xs[2] = x+2;
    xs[3] = x+3;
    return xs[0] + xs[1] + xs[2] + xs[3];
  }

  @Benchmark
  public long arrayComponent4() {
    final long[] xs = new long[] {x, x+1, x+2, x+3};
    long total = 0;
    int i = 0;
    switch (rnd.nextInt(4)) {
    case 3: total += xs[i++];
    case 2: total += xs[i++];
    case 1: total += xs[i++];
    }
    total = xs[i];
    return total;
  }

  static class MyObject {
    final int x;
    public MyObject(int x) {
      this.x = x;
    }
  }
}
