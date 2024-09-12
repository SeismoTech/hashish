
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.GarbageCollectorMXBean;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.util.Random;
import java.util.List;

/**
 * Code to explore GC notifications.
 */
public class GCNotification {

  static final MemoryMXBean mem = ManagementFactory.getMemoryMXBean();

  public static void main(String[] args) {

    for (var pool: ManagementFactory.getMemoryPoolMXBeans()) {
      System.out.println(pool.getName() + ": " + pool.getType());
    }

    registerForMemUsageChanges();
    for (int j = 0; j < 10; j++) {
      System.out.println("Main thread: " + Thread.currentThread());
      Container[] conts = new Container[10_000_000];
      for (int i = 0; i < conts.length; i++) conts[i] = new Container();
      long total = 0;
      for (int i = 0; i < conts.length; i+=2) total += conts[i].value;
      conts = null;
      mem.gc();
    }
  }

  private static void registerForMemUsageChanges() {
    List<GarbageCollectorMXBean> garbageCollectorMXBeans
      = ManagementFactory.getGarbageCollectorMXBeans();
    for (var garbageCollectorMXBean: garbageCollectorMXBeans) {
      listenForGarbageCollectionOn(garbageCollectorMXBean);
    }
  }

  private static void listenForGarbageCollectionOn(
    GarbageCollectorMXBean garbageCollectorMXBean) {
    NotificationEmitter notificationEmitter
      = (NotificationEmitter)garbageCollectorMXBean;
    GarbageListener listener = new GarbageListener();
    notificationEmitter.addNotificationListener(listener, null, null);
  }

  public static class GarbageListener implements NotificationListener {
    @Override
    public void handleNotification(Notification notification, Object handback) {
      System.out.println("Notification thread: " + Thread.currentThread());
      if (notification.getType().equals(
            GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)){
        final GarbageCollectionNotificationInfo gcnot
          = GarbageCollectionNotificationInfo.from(
            (CompositeData) notification.getUserData());
        final GcInfo gcinfo = gcnot.getGcInfo();
        System.out.println("GC Nofification"
            + " name: " + gcnot.getGcName()
            + ", action: " + gcnot.getGcAction()
            + ", cause: " + gcnot.getGcCause()
            + ", before: " + gcinfo.getMemoryUsageBeforeGc()
            + ", after: " + gcinfo.getMemoryUsageAfterGc());
      }
    }
  }

  //----------------------------------------------------------------------
  static final Random RND = new Random();

  static class Container {
    int value;

    Container() {
      this.value = RND.nextInt();
    }
  }
}
