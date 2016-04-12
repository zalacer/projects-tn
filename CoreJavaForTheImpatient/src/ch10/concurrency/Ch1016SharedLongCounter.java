package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 16. Write a program that walks a directory tree and generates a 
// thread for each file. In the threads, count the number of words 
// in the files and, without using locks, update a shared counter 
// that is declared as public static long count = 0; Run the 
// program multiple times. What happens? Why?

// The multi-processing version always returns an inaccurate and 
// lower count because of races.

public class Ch1016SharedLongCounter {

  public static long count = 0;

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

    // single-threaded
    for (Path p : files(Paths.get(s)))
      count+=countWords(p);
    System.out.println("count = " + count); // 18 every time

    // multi-threaded
    count = 0;
    ExecutorService executor = Executors.newCachedThreadPool();
    for (Path p : files(Paths.get(s)))
      executor.execute(() -> count+=countWords(p));
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("count = " + count); // 4, 10, 6, 2, 10, 15, 3, 2, 2, 13, 12, 1, 8, 1, 13

  }

}
