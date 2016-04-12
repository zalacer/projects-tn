package ch10.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 15. Repeat the preceding exercise, using parallel streams. None 
// of the stream operations should have any side effects.

public class Ch1015StreamingWordIndex {

  public static Map<String, Long> buildWordIndex(String dir) {
    Path p = Paths.get(dir);
    ConcurrentHashMap<String, Long> chm = new ConcurrentHashMap<String, Long>();
    try (Stream<Path> fw = Files.walk(p)) {
      chm = fw.parallel().filter(f-> Files.isRegularFile(f)).reduce(
          new ConcurrentHashMap<String, Long>(),
          (m, f) -> { 
            ConcurrentHashMap<String, Long> n = new ConcurrentHashMap<>();
            try {
              n = Files.lines(f).map(l -> l.toLowerCase().split("[\\P{L}]+"))
                  .flatMap(Arrays::stream).filter(w -> isWord(w))
                  .collect(Collectors.toConcurrentMap(
                      k -> k,
                      w -> 1L, 
                      (x, y) -> x + y,
                      ConcurrentHashMap::new));
            } catch (Exception e) {
              e.printStackTrace();
            }
            return concurrentHashMapMerge(Long::sum, m, n);}, 
          (a, b) -> concurrentHashMapMerge(Long::sum, a, b));
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    return new TreeMap<String, Long>(chm);
  }

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }

  public static void printTopN(Map<String, Long> map, int n ) {
    map.entrySet().stream()
    .sorted(Comparator.comparing(e -> -e.getValue()))
    .limit(n).forEachOrdered(System.out::println);
  }

  @SafeVarargs
  public static <K,V> ConcurrentHashMap<K,V> concurrentHashMapMerge(
      BiFunction<? super V,? super V,? extends V> biFun, ConcurrentHashMap<K,V>...maps) {
    if (maps.length == 0) return new ConcurrentHashMap<K,V>();
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

  public static void main(String[] args) {

    Map<String, Long> index = buildWordIndex("books");
    printTopN(index, 10);

  }

}
