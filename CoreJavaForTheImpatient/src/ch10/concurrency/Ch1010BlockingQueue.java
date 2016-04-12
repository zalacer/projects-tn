package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 10. Use a blocking queue for processing files in a directory. One thread
// walks the file tree and inserts files into a queue. Several threads 
// remove the files and search each one for a given keyword, printing out 
// any matches. When the producer is done, it should put a dummy file into 
// the queue.

public class Ch1010BlockingQueue {

  public static boolean contains(Path f, String word) {
    if (!Files.exists(f))
      return false;
    if (!Files.isRegularFile(f))
      return false;
    String[] s = new String[] { word.toLowerCase() };
    boolean r = false;
    try (Stream<String> ss = Files.lines(f)) {
      r = ss.map(w -> w.toLowerCase().split("\\s+"))
          .flatMap(Arrays::stream)
          .anyMatch(x -> x.matches(s[0]));
    } catch (IOException e) {}
    return r;
  }

  public static Set<Path> getFilePaths(String dname) {
    Set<Path> set = new HashSet<Path>();
    Path d = Paths.get(dname);
    if (!Files.isDirectory(d))
      return set;
    try (Stream<Path> sp = Files.walk(d)) {
      set = sp.filter(Files::isRegularFile).collect(Collectors.toSet());
    } catch (IOException e) {
    }
    return set;
  }

  public static Runnable getInitializer(LinkedBlockingQueue<Path> q, Set<Path> s) {
    return () -> {
      for (Path p: s) q.add(p);
      q.add(Paths.get("dummy"));
    };
  }

  public static Runnable getScanner(LinkedBlockingQueue<Path> q, String word) {
    return () -> {
      Path f, g;
      while (true) {
        f = q.peek();
        if (f != null) {
          if (f.toString().equals("dummy")) {
            break;
          } else {
            g = q.poll();
            if (g != null) {
              if (g.toString().equals("dummy")) {
                q.add(Paths.get("dummy"));
              } else if (contains(g, word)) {
                System.out.println(g);
              }
            }
          }
        }
      };
    };
  }


  public static void startThreads(Thread[] threads) {
    for (Thread t : threads) {
      t.start(); 
    }
  }

  public static void joinThreads(Thread[] threads) {
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {

    Set<Path> paths = getFilePaths("books");

    String keyWord = "monster";

    LinkedBlockingQueue<Path> lbq = new LinkedBlockingQueue<>();

    Thread[] threads = new Thread[4];
    threads[0] = new Thread(getInitializer(lbq, paths), "initializer");
    threads[1] = new Thread(getScanner(lbq, keyWord), "scanner1");
    threads[2] = new Thread(getScanner(lbq, keyWord), "scanner2");
    threads[3] = new Thread(getScanner(lbq, keyWord), "scanner3");

    startThreads(threads);
    joinThreads(threads);
    
    //  output:
    //  books\Metamorphosis1956.txt
    //  books\Frankenstein7244.txt
    //  books\WarAndPeace.txt    

  }

}
