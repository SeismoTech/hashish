package com.seismotech.hashish.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GCObserverTest {

  @Disabled("Huge differences across GCs. See GCObserver documentation")
  @Test
  void gcEndsWithSensibleReclaimedMemory()
  throws Exception {
    try(final GCObserver gcobs = new GCObserver().deliverDelay(500)) {
      long r0 = gcobs.gc().get();
      System.err.println("Initial: " + r0);
      for (int i = 0; i < 10; i++) {
        final long r1 = gcobs.gc().get();
        System.err.println("Delta: " + (r1-r0));
        r0 = r1;
      }
    }
  }
}
