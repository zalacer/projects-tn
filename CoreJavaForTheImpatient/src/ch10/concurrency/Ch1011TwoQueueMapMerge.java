package ch10.concurrency;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 11. Repeat the preceding exercise, but instead have each consumer
// compile a map of words and their frequencies that are inserted 
// into a second queue. A final thread merges the dictionaries and 
// prints the ten most common words. Why don’t you need to use a 
// ConcurrentHashMap?

// A concurrent map is not needed because each map is updated by only
// one thread.

public class Ch1011TwoQueueMapMerge {

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  public static Map<String,Long> wordCount(Path f) {
    Map<String, Long> m = Collections.emptyMap();
    try (Stream<String> ss = Files.lines(f)) {
      m = ss.map(w -> w.toLowerCase()
          .split("[\\P{L}]+")).flatMap(Arrays::stream)
          .filter(w -> isWord(w))
          .collect(groupingBy(identity(),counting()));
    } catch (IOException e) {}
    return m;
  }

  public static <K,V> Map<K,V> mapMerge(
      BiFunction<? super V,? super V,? extends V> biFun, ArrayList<Map<K,V>> maps) {
    if (maps.size() == 0) return Collections.emptyMap();
    if (maps.size() == 1) return maps.get(0);
    for (int i = 1; i < maps.size(); i++)
      for (Entry<K,V> e : maps.get(i).entrySet())
        maps.get(0).merge(e.getKey(), e.getValue(), biFun);
    return maps.get(0);
  }

  public static void printTopN(Map<String, Long> map, int n ) {
    map.entrySet().stream()
    .sorted(Comparator.comparing(e -> -e.getValue()))
    .limit(n).forEachOrdered(System.out::println);
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

  public static Runnable getScanner(
      LinkedBlockingQueue<Path> q1, LinkedBlockingQueue<Map<String,Long>> q2) {
    return () -> {
      Path f, g;
      while (true) {
        f = q1.peek();
        if (f != null) {
          if (f.toString().equals("dummy")) {
            break;
          } else {
            g = q1.poll();
            if (g != null) {
              if (g.toString().equals("dummy")) {
                q1.add(Paths.get("dummy"));
                break;
              } else {
                q2.add(wordCount(g));
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

  public static void main(String[] args)  {

    Set<Path> paths = getFilePaths("books");

    LinkedBlockingQueue<Path> lbq1 = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Map<String,Long>> lbq2 = new LinkedBlockingQueue<>();

    Thread[] threads = new Thread[4];
    threads[0] = new Thread(getInitializer(lbq1, paths), "initializer");
    threads[1] = new Thread(getScanner(lbq1, lbq2), "scanner1");
    threads[2] = new Thread(getScanner(lbq1, lbq2), "scanner2");
    threads[3] = new Thread(getScanner(lbq1, lbq2), "scanner3");

    startThreads(threads);
    joinThreads(threads);

    Map<String, Long> out = mapMerge(Long::sum, new ArrayList<Map<String, Long>>(lbq2));

    printTopN(out,10);
    
  }

}
