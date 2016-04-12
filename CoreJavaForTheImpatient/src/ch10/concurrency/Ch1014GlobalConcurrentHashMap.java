package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 14. Repeat the preceding exercise, using a global ConcurrentHashMap for
// collecting the word frequencies.

public class Ch1014GlobalConcurrentHashMap {

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
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

  public static Runnable getRunnableScanner(
      LinkedBlockingQueue<Path> q1, 
      ConcurrentHashMap<String, Long> chm) {
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
                try {
                  Files.lines(g).map(l -> l.toLowerCase()
                      .split("[\\P{L}]+"))
                  .flatMap(Arrays::stream)
                  .filter(w -> isWord(w))
                  .forEach(w -> chm.merge(w, 1L, Long::sum));
                } catch (Exception e) {
                  //e.printStackTrace();
                }
              }
            }
          }
        }
      };
    };
  }

  public static void main(String[] args)  {
    // in this solution all the runnables directly update the
    // global ConcurrentHashMap with each word found in a stream

    Set<Path> paths = getFilePaths("books");
    LinkedBlockingQueue<Path> lbq1 = new LinkedBlockingQueue<>(paths);
    lbq1.add(Paths.get("dummy"));
    ConcurrentHashMap<String, Long> chm = new ConcurrentHashMap<>();
    int processors = Runtime.getRuntime().availableProcessors();
    processors = processors > 1 ? processors - 1 : processors;
    List<Runnable> tasks = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(processors);        
    ExecutorCompletionService<String> service = 
        new ExecutorCompletionService<String>(executor);

    for (int i = 0; i < 10; i++) {
      tasks.add(getRunnableScanner(lbq1, chm));
    }

    for (Runnable task : tasks) {
      service.submit(task, "done");
    }

    for (int i = 0; i < tasks.size(); i++) {
      try {
        assert service.take().get().equals("done");
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor.shutdown();

    printTopN(chm,10);


  }

}
