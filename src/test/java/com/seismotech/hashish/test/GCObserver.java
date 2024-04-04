package com.seismotech.hashish.test;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import com.sun.management.GarbageCollectionNotificationInfo;
import static com.sun.management.GarbageCollectionNotificationInfo
  .GARBAGE_COLLECTION_NOTIFICATION;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

/**
 * This is an attempt to measure how much garbage is produced in a
 * code section.
 *
 * <p>But it failed because its operation generates garbage
 * depending on the enabled GC algorithm.
 * For instance,
 * ZGC seems to generate no garbage,
 * Parallel generates around 10Mb,
 * and C4 generates about 100Mb!
 * With that huge variation, it is very difficult to write a test that
 * works properly regardless of the chosen GC algorithm.
 *
 * <p>The intended usage is
 * <code><tt>
 * var gcobs = new GCObserver().deliverDelay(100);
 * ...
 * var r0 = gcobs.gc().get();
 *   ... code section to measure ...
 * var r1 = gcobs.gc().get();
 * var garbage = r1 - r0;
 * ...
 * gcobs.close();
 * </tt></code>
 *
 * <p>{@code deliverDelay} is the amount of time that {@code .gc().get()} will
 * wait for GC notifications.
 * Strictly speaking, it is the max amount of time between notifications:
 * after requesting a GC,
 * that amount will be awaited for the first notification;
 * after processing that notification,
 * it will be awaited again,
 * and so on,
 * until there is no new notification.
 *
 * <p>{@code .gc().get()} returns the size of reclaimed memory until since the
 * {@code GCObserver} was created.
 * Therefore, the amount of produced garbage in a code section is the
 * difference between that value before and after that section.
 *
 * <p>A {@code GCObserver} must be closed to remove GC listeners
 * and cancel timers.
 */
public class GCObserver implements AutoCloseable {

  private final MemoryMXBean mem;
  private final PoolAttachedMemoryUsage.Builder usageBuilder;
  private final GCListener gcer;
  private final Timer deliverer;
  private long deliverDelay;
  private TimerTask deliverTask;
  private CompletableFuture<Long> gcnews;
  private long reclaimed;
  private boolean closed;

  public GCObserver() {
    this.mem = ManagementFactory.getMemoryMXBean();
    this.usageBuilder = new PoolAttachedMemoryUsage.Builder();
    this.gcer = new GCListener();
    this.deliverer = new Timer(true);
    this.deliverDelay = 500;
    this.deliverTask = null;
    this.gcnews = null;
    this.reclaimed = 0;
    this.closed = false;
    start();
  }

  private void start() {
    for (final var gcbean: ManagementFactory.getGarbageCollectorMXBeans()) {
      ((NotificationEmitter) gcbean).addNotificationListener(gcer, null, null);
    }
  }

  public GCObserver deliverDelay(long millis) {
    deliverDelay = millis;
    return this;
  }

  public synchronized void close() {
    deliverer.cancel();
    for (final var gcbean: ManagementFactory.getGarbageCollectorMXBeans()) {
      try {
        ((NotificationEmitter) gcbean)
          .removeNotificationListener(gcer, null, null);
      } catch (ListenerNotFoundException ignored) {}
    }
    closed = true;
  }

  public synchronized long reclaimed() {return reclaimed;}

  public synchronized Future<Long> gc() {
    if (closed) throw new IllegalStateException(
      "Closed: cannot use this object anymore");
    if (gcnews == null) {
      gcnews = new CompletableFuture<>();
      reinstallDeliverer0(true);
      mem.gc();
    }
    return gcnews;
  }

  private synchronized void gcDone(GarbageCollectionNotificationInfo gcnot) {
    reclaimed +=
      usedHeapMemory(gcnot.getGcInfo().getMemoryUsageBeforeGc())
      - usedHeapMemory(gcnot.getGcInfo().getMemoryUsageAfterGc());
    reinstallDeliverer0(false);
  }

  private long usedHeapMemory(Map<String,MemoryUsage> mus) {
    return usageBuilder.attach(mus).stream()
        .filter(mu -> mu.type() == MemoryType.HEAP)
        .mapToLong(mu -> mu.memoryUsage().getUsed())
        .sum();
  }

  private void reinstallDeliverer0(boolean create) {
    if (deliverTask != null) deliverTask.cancel();
    else if (!create) return;
    deliverTask = new TimerTask() {
      @Override public void run() {deliver(this);}
    };
    deliverer.schedule(deliverTask, deliverDelay);
  }

  private void deliver(TimerTask runningTask) {
    if (deliverTask != runningTask) return;
    gcnews.complete(reclaimed);
    gcnews = null;
    deliverTask = null;
  }

  private class GCListener implements NotificationListener {
    @Override
    public void handleNotification(Notification notification, Object handback) {
      if (!notification.getType().equals(GARBAGE_COLLECTION_NOTIFICATION))
        return;
      gcDone(
        GarbageCollectionNotificationInfo.from(
          (CompositeData) notification.getUserData()));
    }
  }
}
