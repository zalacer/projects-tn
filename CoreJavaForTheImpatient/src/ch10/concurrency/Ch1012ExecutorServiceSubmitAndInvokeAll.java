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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 12. Repeat the preceding exercise, making a Callable<Map<String,
// Integer>> for each file and using an appropriate executor service.
// Merge the results when all are available. Why don’t you need to 
// use a ConcurrentHashMap?

// A concurrent map is not needed because each map is updated by only 
// one thread.

public class Ch1012ExecutorServiceSubmitAndInvokeAll {

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

  public static Callable<Map<String,Long>> 
      getCallableScanner(LinkedBlockingQueue<Path> q1) {
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
                System.out.println(Thread.currentThread().getName()
                    +" removed dummy and putting it back");
                q1.add(Paths.get("dummy"));
                break;
              } else {
                return wordCount(g);
              }
            }
          }
        }
      };
      return Collections.emptyMap();
    };
  }

  public static void main(String[] args)  {

    // 1st variation: build a list of futures with 
    // ExecutorService.submit(Callable<T> task)

    Set<Path> paths = getFilePaths("books");
    LinkedBlockingQueue<Path> lbq1 = new LinkedBlockingQueue<>(paths);
    lbq1.add(Paths.get("dummy"));
    LinkedBlockingQueue<Map<String,Long>> lbq2 = new LinkedBlockingQueue<>();
    int processors = Runtime.getRuntime().availableProcessors();
    processors = processors > 1 ? processors - 1 : processors;
    ArrayList<Future<Map<String, Long>>> futures = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(processors);

    for (int i = 0; i < 10; i++) {
      futures.add(executor.submit(getCallableScanner(lbq1)));
    }

    for (Future<Map<String, Long>> future : futures) {
      try {
        lbq2.add(future.get());
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor.shutdown();

    Map<String, Long> result = mapMerge(Long::sum, new ArrayList<Map<String, Long>>(lbq2));
    System.out.println();
    System.out.println("First variation top ten:");
    printTopN(result,10);

    // 2nd variation: build a list of Callable<T> named tasks then 
    // create a list of futures from them with 
    // ExecutorService.invokeAll(Collection<? extends Callable<T>> tasks)

    Set<Path> paths2 = getFilePaths("books");
    LinkedBlockingQueue<Path> lbq3 = new LinkedBlockingQueue<>(paths2);
    lbq3.add(Paths.get("dummy"));
    LinkedBlockingQueue<Map<String,Long>> lbq4 = new LinkedBlockingQueue<>();
    int processors2 = Runtime.getRuntime().availableProcessors();
    processors2 = processors2 > 1 ? processors2 - 1 : processors2;
    ExecutorService executor2 = Executors.newFixedThreadPool(processors2);
    List<Callable<Map<String,Long>>> tasks = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      tasks.add(getCallableScanner(lbq3));
    }

    List<Future<Map<String, Long>>> futures2 = Collections.emptyList();
    try {
      futures2 = executor2.invokeAll(tasks);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }

    for (Future<Map<String, Long>> future : futures2) {
      try {
        lbq4.add(future.get());
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor2.shutdown();

    Map<String, Long> result2 = mapMerge(Long::sum, new ArrayList<Map<String, Long>>(lbq4));
    System.out.println();
    System.out.println("Second variation top ten:");
    printTopN(result2,10);

  }

}
