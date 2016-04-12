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
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 13. Use an ExecutorCompletionService instead and merge the results as soon
// as they become available.

public class Ch1013ExecutorCompletionService {

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

  @SafeVarargs
  public static <K,V> Map<K,V> mapMerge(
          BiFunction<? super V,? super V,? extends V> biFun, Map<K,V>...maps) {
      if (maps.length == 0) return Collections.emptyMap();
      if (maps.length == 1) return maps[0];
      for (int i = 1; i < maps.length; i++) {
          if (maps[0].size() == 0) {
              maps[0] = maps[i];
          } else if (maps[i].size() == 0) {
              continue;
          } else {
              for (Entry<K,V> e : maps[i].entrySet()) {
                  maps[0].merge(e.getKey(), e.getValue(), biFun);
              }
          }
      }
      return maps[0];
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

  public static Callable<Map<String,Long>> getCallableScanner(LinkedBlockingQueue<Path> q1) {
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
                    +" removed dummy and adding it back");
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

    Set<Path> paths = getFilePaths("books");
    LinkedBlockingQueue<Path> lbq1 = new LinkedBlockingQueue<>(paths);
    lbq1.add(Paths.get("dummy"));
    int processors = Runtime.getRuntime().availableProcessors();
    processors = processors > 1 ? processors - 1 : processors;
    ExecutorService executor = Executors.newFixedThreadPool(processors);
    List<Callable<Map<String,Long>>> tasks = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      tasks.add(getCallableScanner(lbq1));
    }

    ExecutorCompletionService<Map<String, Long>> service = 
        new ExecutorCompletionService<Map<String, Long>>(executor);

    for (Callable<Map<String,Long>> task : tasks) {
      service.submit(task);
    }

    Map<String, Long> result = Collections.emptyMap();

    for (int i = 0; i < tasks.size(); i++) {
      try {
        result = mapMerge(Long::sum, result, service.take().get());
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor.shutdown();

    System.out.println("top ten:");
    printTopN(result,10);

  }

}
