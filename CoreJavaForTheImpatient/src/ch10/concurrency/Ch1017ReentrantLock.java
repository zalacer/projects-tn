package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 17. Fix the program of the preceding exercise with using a lock.

public class Ch1017ReentrantLock {

  public static long count = 0;
  public static final Lock countLock = new ReentrantLock();

  public static void updateCount(long n) {
    countLock.lock();
    try {
      count+=n; 
    } finally {
      countLock.unlock();
    }  
  }

  public static long countWords(Path f) {
    try {
      return Files.lines(f).mapToLong(s -> s.split("\\s+").length).sum();
    } catch (IOException e) {
      return 0;  
    }
  }

  public static Set<Path> files(Path p) throws IOException {        
    try (Stream<Path> entries = Files.walk(p).filter(x->x.toFile().isFile())) {
      return entries.collect(Collectors.toSet());
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {

    String s = "dirtest";

    // multi-threaded
    count = 0;
    ExecutorService executor = Executors.newCachedThreadPool();
    for (Path p : files(Paths.get(s)))
      executor.execute(() -> updateCount(countWords(p)));
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("count = " + count); // 18, 18, 18

  }

}
