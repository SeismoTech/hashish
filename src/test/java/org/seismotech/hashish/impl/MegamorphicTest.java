package org.seismotech.hashish.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import org.seismotech.testafeo.heap.TryAllocationChecker;

import org.seismotech.hashish.api.Hashing;
import org.seismotech.hashish.api.Kernel64;
import org.seismotech.hashish.impl.HashingKernel64;

class MegamorphicTest {
  @Nested
  class Case1 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  @Nested
  class Case2 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  @Nested
  class Case3 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  @Nested
  class Case4 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  @Nested
  class Case5 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  @Nested
  class Case6 {
    static class MyHashing extends HashingKernel64 {
      protected  Kernel64 newKernel() {return new MyKernel();}
    }
    static class MyKernel implements Kernel64 {
      long s = 0;
      public MyKernel() {this(0);}
      public MyKernel(long s) {this.s = s;}
      public Kernel64 clone() {return new MyKernel(s);}
      public void block(long block) {s += block;}
      public void tail(long tail, int taillen, long totallen) {
        s += tail + totallen;
      }
      public long hash64() {return s;}
    }

    @Test
    void avoidsAllocationOfKernel()
    throws IOException {
      var hing = new MyHashing();
      var checker = new TryAllocationChecker(MyKernel.class);
      for (int i = 0; i < 25 && !checker.satisfied(); i++)
        try (var ignored = checker.enter()) {
          for (int j = 0; j < 100_000; j++) checker.consume(hing.hash(i*j));
        }
      checker.assertSatisfied();
    }
  }

  static final Hashing HING1 = new Case1.MyHashing();
  static final Hashing HING2 = new Case2.MyHashing();
  static final Hashing HING3 = new Case3.MyHashing();
  static final Hashing HING4 = new Case1.MyHashing();
  static final Hashing HING5 = new Case2.MyHashing();
  static final Hashing HING6 = new Case3.MyHashing();

  @Test
  void avoidsAllocationOf2Kernels()
  throws IOException {
    var checker = new TryAllocationChecker(
      Case1.MyKernel.class, Case2.MyKernel.class, Case3.MyKernel.class)
      .verbose(true);
    for (int i = 0; i < 25 && !checker.satisfied(); i++)
      try (var ignored = checker.enter()) {
        for (int j = 0; j < 100_000; j++) {
          final long k = i * (long) j;
          checker.consume(HING1.hash(k) + HING2.hash(k));
        }
      }
    checker.assertSatisfied();
  }

  @Test
  void avoidsAllocationOf3Kernels()
  throws IOException {
    var checker = new TryAllocationChecker(
      Case1.MyKernel.class, Case2.MyKernel.class, Case3.MyKernel.class)
      .verbose(true);
    for (int i = 0; i < 25 && !checker.satisfied(); i++)
      try (var ignored = checker.enter()) {
        for (int j = 0; j < 100_000; j++) {
          final long k = i * (long) j;
          checker.consume(HING1.hash(k) + HING2.hash(k) + HING3.hash(k));
        }
      }
    checker.assertSatisfied();
  }

  @Test
  void avoidsAllocationOfKernels()
  throws IOException {
    var checker = new TryAllocationChecker(
      Case1.MyKernel.class, Case2.MyKernel.class, Case3.MyKernel.class)
      .verbose(true);
    for (int i = 0; i < 25 && !checker.satisfied(); i++)
      try (var ignored = checker.enter()) {
        for (int j = 0; j < 100_000; j++) {
          final long k = i * (long) j;
          checker.consume(
            HING1.hash(k) + HING2.hash(k) + HING3.hash(k)
            + HING4.hash(k) + HING5.hash(k) + HING6.hash(k));
        }
      }
    checker.assertSatisfied();
  }

  @Test
  void multiKernels()
  throws IOException {
    for (int i = 0; i < 25; i++) {
      long s = 0;
      for (int j = 0; j < 100_000; j++) {
        final long k = i * (long) j;
        s += HING1.hash(k) + HING2.hash(k) + HING3.hash(k)
          + HING4.hash(k) + HING5.hash(k) + HING6.hash(k);
      }
      System.err.println(s);
    }
  }
}
