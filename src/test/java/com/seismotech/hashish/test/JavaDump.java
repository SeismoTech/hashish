package com.seismotech.hashish.test;

import java.lang.management.ManagementFactory;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.IOException;
import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;

public class JavaDump implements Closeable {

  private static final HotSpotDiagnosticMXBean diag
    = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);

  public static JavaDump create()
  throws IOException {
    return new JavaDump(null);
  }

  public static JavaDump createAt(String filename)
  throws IOException {
    return new JavaDump(filename);
  }

  private final Path dir;
  private boolean live;
  private boolean dumped;
  private Heap heap;

  private JavaDump(String filename)
  throws IOException {
    Path dir;
    if (filename == null) {
      dir = Files.createTempDirectory("dump");
    } else {
      dir = Paths.get(filename);
      OnFile.deleteTree(dir);
      Files.createDirectories(dir);
    }
    this.dir = dir;
    this.live = false;
    this.dumped = false;
    this.heap = null;
  }

  public JavaDump live(boolean enabled) {
    this.live = enabled;
    return this;
  }

  @Override
  public void close()
  throws IOException {
    //FIXME: No way to close a Heap
    //heap.close();
    OnFile.deleteTree(dir);
  }

  public Path rootDir() {return dir;}

  public Path dumpFile() {return dir.resolve("dump.hprof");}

  public void dump()
  throws IOException {
    if (dumped) return;
    final Path file = dumpFile();
    Files.deleteIfExists(file);
    diag.dumpHeap(file.toString(), live);
    dumped = true;
  }

  public Heap heap()
  throws IOException {
    if (heap == null) heap = HeapFactory.createHeap(dumpFile().toFile());
    return heap;
  }
}
