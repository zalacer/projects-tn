package ch08.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

//16. Find the 500 longest strings in War and Peace with a parallel stream. Is it any faster
//than using a serial stream?

// Performance is not significantly better for parallel vs serial, and the serial 
// version greatly outperformed (more than 2X) the parallel version when run last.

public class Ch0816FindLongestStrings {

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }
  
  public static Comparator<Entry<String, Integer>> comp = (e1, e2) -> {
    int c2 = e2.getValue().compareTo(e1.getValue());
    int c1 = e1.getKey().compareTo(e1.getKey());
    return c2 == 0 ? c1 : c2;
  };

  // assuming string means word according to isWord() and only for distinct words
  public static List<Entry<String, Integer>> findLongestWords(String fileName, int n) {
    List<Entry<String, Integer>> r = null;
    try {
      r = Files.newBufferedReader(Paths.get(fileName)).lines()
          .map(l -> l.split("\\s+")).flatMap(Arrays::stream).filter(w -> isWord(w))
          .map(String::toLowerCase).collect(Collectors.toSet()).stream() // deduplication
          .collect(Collectors.toConcurrentMap(w -> w, w -> w.length()))
          .entrySet().stream().sorted(comp).limit(n).collect(Collectors.toList());
      //forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return r;
  }

  public static List<Entry<String, Integer>> parFindLongestWords(String fileName, int n) {
    List<Entry<String, Integer>> r = null;
    try {
      r = Files.newBufferedReader(Paths.get(fileName)).lines().parallel()
          .map(l -> l.split("\\s+")).flatMap(Arrays::stream).parallel().filter(w -> isWord(w))
          .map(String::toLowerCase).collect(Collectors.toSet()).parallelStream() // dedup
          .collect(Collectors.toConcurrentMap(w -> w, w -> w.length()))
          .entrySet().parallelStream().sorted(comp).limit(n).collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return r;
  }

  public static void main(String[] args) throws IOException {

    // methodology: 1st 3 runs started with parFindLongestWords and ended
    // with findLongestWords and reversed the order for the last 3 runs.

    long start, stop, elapsed;
    String b = "books/WarAndPeace.txt";
    
    start = System.currentTimeMillis();
    List<Entry<String, Integer>> t1 = parFindLongestWords(b,500);
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("\nparFindLongestWords(\"WarAndPeace.txt\",500):");
    System.out.println("elapsed = "+elapsed);
    System.out.println("t1.size() = "+t1.size());
    System.out.println("t1.get(0) = "+t1.get(0));
    //  parFindLongestWords("WarAndPeace.txt",500):
    //  elapsed = 987, 873, 888, 828, 883, 679
    //  t1.size() = 500
    //  t1.get(0) = characteristically=18

    start = System.currentTimeMillis();
    List<Entry<String, Integer>> t0 = findLongestWords(b,500);
    stop = System.currentTimeMillis();
    elapsed = stop - start;
    System.out.println("\nfindLongestWords(\"WarAndPeace.txt\",500):");
    System.out.println("elapsed = "+elapsed);
    System.out.println("t0.size() = "+t0.size());
    System.out.println("t0.get(0) = "+t0.get(0));
    //  findLongestWords("WarAndPeace.txt",500):
    //  elapsed = 412, 396, 529, 858, 813,780
    //  t0.size() = 500
    //  t0.get(0) = characteristically=18

  }

}
