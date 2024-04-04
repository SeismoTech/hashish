package com.seismotech.hashish.impl.sip;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.io.IOException;
import java.util.Arrays;

import com.seismotech.hashish.api.Hashing;
import com.seismotech.hashish.api.Kernel64;
import com.seismotech.hashish.impl.sip.SipHashing;

import com.seismotech.hashish.test.MemoryWatcher;
import com.seismotech.hashish.test.JavaDump;
import com.seismotech.hashish.test.SimpleAllocationChecker;
import com.seismotech.hashish.test.TryAllocationChecker;
import com.seismotech.hashish.test.AllocationChecker;
import com.seismotech.hashish.test.GCObserver;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.JavaClass;

class SipHashingTest {

  static final long FK0 = 0x0706050403020100L, FK1 = 0x0f0e0d0c0b0a0908L;

  @Nested
  static class PaperTest {
    static final byte[] DATA = new byte[15];
    static {
      for (int i = 0; i < DATA.length; i++) DATA[i] = (byte) i;
    }
    static final long EXPECTED = 0xa129ca6149be45e5L;

    @Test
    void fullArray() {
      final Hashing hashing = new SipHashing._2_4(FK0, FK1);
      final long hash = hashing.hash(DATA);
      assertEquals(EXPECTED, hash);
    }

    @Test
    void subarray() {
      final Hashing hashing = new SipHashing._2_4(FK0, FK1);
      final byte[] XDATA = new byte[DATA.length+16];
      for (int i = 0; i <= 8; i++) {
        Arrays.fill(XDATA, (byte) 0xff);
        System.arraycopy(DATA, 0, XDATA, i, DATA.length);
        final long hash = hashing.hash(XDATA, i, DATA.length);
        assertEquals(EXPECTED, hash);
      }
    }
  }

  @Nested
  static class AllocationTest {

    static final MemoryMXBean mem = ManagementFactory.getMemoryMXBean();

    @Test
    void newCreatesAKernel()
    throws IOException {
      try (
        final JavaDump preDump = JavaDump.createAt("pre");
        final JavaDump postDump = JavaDump.createAt("post")) {

        mem.gc(); mem.gc();

        final Kernel64 kernel1 = new SipHashKernel._2_4(FK0, FK1);
        kernel1.block(0x0112233445566778L);
        kernel1.tail(0, 0, 8);
        assertEquals(6888546022202425636L, kernel1.hash64());
        preDump.dump();

        final Kernel64 kernel2 = new SipHashKernel._2_4(FK0, FK1);
        kernel2.block(0x0112233445566779L);
        kernel2.tail(0, 0, 8);
        assertEquals(-3476958499151402114L, kernel2.hash64());
        postDump.dump();

        final Heap preHeap = preDump.heap();
        final JavaClass preKernel
          = preHeap.getJavaClassByName(SipHashKernel._2_4.class.getName());
        final long preKernelNo = preKernel.getInstancesCount();
        System.err.println("Pre instances: " + preKernelNo);

        final Heap postHeap = postDump.heap();
        final JavaClass postKernel
          = postHeap.getJavaClassByName(SipHashKernel._2_4.class.getName());
        final long postKernelNo = postKernel.getInstancesCount();
        System.err.println("Post instances: " + postKernelNo);

        assertThat(preKernelNo, greaterThan(0L));
        assertEquals(1, postKernelNo - preKernelNo);
      }
    }
  }

  @Nested
  static class EventuallyAvoidAllocationTest {

    static final long VK0 = System.currentTimeMillis(), VK1 = System.nanoTime();

    @Nested
    static class High {
      @BeforeAll
      static void pollute()
      throws IOException {
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._1_3.class)
          .at(new SipHashing._1_3(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .checkEvery(1000).checkLimit(10)
          .check();
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._1_4.class)
          .at(new SipHashing._1_4(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .checkEvery(1000).checkLimit(10)
          .check();
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._2_4.class)
          .at(new SipHashing._2_4(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .checkEvery(1000).checkLimit(10)
          .check();
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._4_8.class)
          .at(new SipHashing._4_8(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .checkEvery(1000).checkLimit(10)
          .check();
      }

      @Test
      void avoidsAllocationOfSipHashKernel13()
      throws IOException {
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._1_3.class)
          .at(new SipHashing._1_3(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .check(Assertions::assertTrue);
      }

      @Test
      void avoidsAllocationOfSipHashKernel14()
      throws IOException {
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._1_4.class)
          .at(new SipHashing._1_4(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .check(Assertions::assertTrue);
      }

      @Test
      void avoidsAllocationOfSipHashKernel24()
      throws IOException {
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._2_4.class)
          .at(new SipHashing._2_4(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .check(Assertions::assertTrue);
      }

      @Test
      void avoidsAllocationOfSipHashKernel48()
      throws IOException {
        AllocationChecker.eventuallyAvoid()
          .allocationOf(SipHashKernel._4_8.class)
          .at(new SipHashing._4_8(VK0, VK1))
          .transformingLong((hashing, i) -> hashing.hash(i))
          .check(Assertions::assertTrue);
      }
    }

    @Nested
    static class Low {
      @Test
      void avoidsAllocationOfSipHashKernel13()
      throws IOException {
        var sip = new SipHashing._1_3(VK0, VK1);
        var checker = new TryAllocationChecker(SipHashKernel._1_3.class);
        for (int i = 0; i < 25 && !checker.satisfied(); i++)
          try (var ignored = checker.enter()) {
            for (int j = 0; j < 100_000; j++) checker.consume(sip.hash(i*j));
          }
        checker.assertSatisfied();
      }

      @Test
      void avoidsAllocationOfSipHashKernel14()
      throws IOException {
        var sip = new SipHashing._1_4(VK0, VK1);
        var checker = new TryAllocationChecker(SipHashKernel._1_4.class);
        for (int i = 0; i < 25 && !checker.satisfied(); i++)
          try (var ignored = checker.enter()) {
            for (int j = 0; j < 100_000; j++) checker.consume(sip.hash(i*j));
          }
        checker.assertSatisfied();
      }

      @Test
      void avoidsAllocationOfSipHashKernel24()
      throws IOException {
        var sip = new SipHashing._2_4(VK0, VK1);
        var checker = new TryAllocationChecker(SipHashKernel._2_4.class);
        for (int i = 0; i < 25 && !checker.satisfied(); i++)
          try (var ignored = checker.enter()) {
            for (int j = 0; j < 100_000; j++) checker.consume(sip.hash(i*j));
          }
        checker.assertSatisfied();
      }

      @Test
      void avoidsAllocationOfSipHashKernel48()
      throws IOException {
        var sip = new SipHashing._4_8(VK0, VK1);
        var checker = new TryAllocationChecker(SipHashKernel._4_8.class);
        for (int i = 0; i < 25 && !checker.satisfied(); i++)
          try (var ignored = checker.enter()) {
            for (int j = 0; j < 100_000; j++) checker.consume(sip.hash(i*j));
          }
        checker.assertSatisfied();
      }
    }

    @Disabled
    @Test
    void avoidsAllocationOfSipHashKernel24TrackingMemory()
    throws Exception {
      var sip = new SipHashing._2_4(VK0, VK1);
      long total = 0;
      try(final GCObserver gcobs = new GCObserver().deliverDelay(100)) {
        long r0 = gcobs.gc().get();
        for (int i = 0; i < 10; i++) {
          for (int j = 0; j < 10_000_000; j++) {
            total += sip.hash(i*j);
          }
          long r1 = gcobs.gc().get();
          System.err.println("Delta: " + (r1-r0));
          r0 = r1;
        }
      }
      System.err.println(total);
    }
  }
}
