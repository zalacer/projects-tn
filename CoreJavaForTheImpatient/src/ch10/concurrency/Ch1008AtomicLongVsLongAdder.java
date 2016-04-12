package ch10.concurrency;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

// 8. Generate 1,000 threads, each of which increments a counter 100,000 times.
// Compare the performance of using AtomicLong versus LongAdder.

// LongAdder appears to generally be 6-7 times faster than AtomicLong in this
// test configuration although sometimes it can appear to be 5 times slower but
// which may possibly be due to garbage collection side effects.

public class Ch1008AtomicLongVsLongAdder {

  public static final AtomicLong alcounter = new AtomicLong();
  public static final LongAdder lacounter = new LongAdder();

  public static void main(String[] args) throws InterruptedException {

    long start, stop, elapsed;

    Runnable task1 = () -> {for (int i = 0; i < 100000; i++) alcounter.incrementAndGet();};
    Thread[] ta = new Thread[1000];
    for (int i = 0; i < 1000; i++) ta[i] = new Thread(task1,"ta"+i);
    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) ta[i].start();
    for (int i = 0; i < 1000; i++) ta[i].join();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("elapsed="+elapsed); // elapsed=3539, 3067, 3251, 3238, 3135, 2644
    System.out.println("alcounter="+alcounter); // alcounter=100000000

    Runnable task2 = () -> {for (int i = 0; i < 100000; i++) lacounter.increment();};
    Thread[] tb = new Thread[1000];
    for (int i = 0; i < 1000; i++) tb[i] = new Thread(task2,"tb"+i);
    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) tb[i].start();
    for (int i = 0; i < 1000; i++) tb[i].join();
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("elapsed="+elapsed); // elapsed=556, 644, 646, 1789, 14443, 580
    // high readings after gc()
    System.out.println("lacounter="+lacounter); // lacounter=100000000
  }

}
