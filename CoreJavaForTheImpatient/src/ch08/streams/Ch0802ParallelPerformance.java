package ch08.streams;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toConcurrentMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 2. Measure the difference when counting long words with a parallelStream
// instead of a stream. Call System.currentTimeMillis before and after the
// call and print the difference. Switch to a larger document (such as War and Peace) if
// you have a fast computer.

// The outstanding performance winners used parallel streams from a  List<String> to a map or
// explicit ConcurrentMap. These completed faster than the other combinations by several orders 
// of magnitude. Alternatives included streaming Object instead of String, streaming from arrays
// and using one stream instead of parallel streams. 

public class Ch0802ParallelPerformance {

  public static void main(String[] args) throws IOException, InterruptedException {

    Object[] words = Files.newBufferedReader(Paths.get("books/WarAndPeace.txt")).lines()
        .map(w -> w.split("[\\P{L}]+")).flatMap(Arrays::stream)
        .map(String::toLowerCase).toArray();

    String[] wordStrings = new String[words.length];
    for (int i = 0; i < words.length; i++) {
      wordStrings[i] = (String) words[i];
    }

    List<Object> lo = new ArrayList<Object>();
    for (int i = 0; i < words.length; i++) {
      lo.add(words[i]);
    }

    List<String> los = new ArrayList<String>();
    for (int i = 0; i < words.length; i++) {
      lo.add((String) words[i]);
    }

    long start, stop, elapsed;

    System.out.println("naive parallel bench of String[] stream to map using words");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(words).parallel().collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    // 184161783, 99288053, 97261756, 96993130, 76773455

    System.out.println("\n");

    System.out.println("naive parallel bench of String[] stream to a map using wordStrings");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(wordStrings).parallel().collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    // 168903208, 79714953, 73663285, 89877222, 70929727

    System.out.println("\n");

    System.out.println("parallel bench of Object[] stream to a ConcurrentMap using words");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(words).parallel().collect(toConcurrentMap(s -> s, s -> 1, (x, y) -> x + y));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    // 150366683, 150356867, 115564014, 121156966, 73855607

    System.out.println("\n");

    System.out.println("parallel bench of String[] stream to a ConcurrentMap using wordStrings");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(wordStrings).parallel().collect(toConcurrentMap(s -> s, s -> 1, (x, y) -> x + y));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    // 118355581, 101619118, 95298378, 70798984, 79756005

    System.out.println("\n");

    System.out.println("parallel bench of List<Object> stream to a map using lo");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      lo.parallelStream().collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    // 174266801, 94953001, 86016059, 83306151, 95293469

    System.out.println("\n");

    System.out.println("parallel bench of List<String> stream to a map using los");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      los.parallelStream().collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    //173132, 206599, 146806, 102184, 147251

    System.out.println("\n");

    System.out.println("parallel bench of List<String> stream to a Concurrent map using los");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      los.parallelStream().collect(toConcurrentMap(s -> s, s -> 1, (x, y) -> x + y));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
      Thread.sleep(1000);
    }
    //3616586, 157960, 162423, 141897, 131188, 
    System.out.println("\n");

    System.out.println("linear bench of Object[] stream to a map using words");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(words).collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
    }
    // 125782507, 67300153, 56680061, 74540112, 66245284

    System.out.println("\n");

    System.out.println("linear bench of String[] stream to a map using wordStrings");
    for (int i = 0; i < 5; i++) {
      start = System.nanoTime();
      Arrays.stream(wordStrings).collect(groupingBy(identity(), counting()));
      stop = System.nanoTime();
      elapsed = stop - start;
      System.out.print(elapsed + ", ");
    }
    // 122455919, 58186954, 52023283, 90550125, 83476608

    //        Map<Object, Long> wordCount = Arrays.stream(words).collect(groupingBy(identity(), counting()));
    //        System.out.println(wordCount.size()); // 17624
    //        // output looks ok using:
    //        int count = 0;
    //        for (Object o : wordCount.keySet()) {
    //            System.out.printf("%-20s : %d\n", o, wordCount.get(o));
    //            //count++;
    //            if (count == 9) break;
    //        }
  }

}


//// example
//// http://www.oracle.com/technetwork/articles/java/architect-streams-pt2-2227132.html
//Stream<String> words = Stream.of("Java", "Magazine", "is", "the", "best");
//
//Map<String, Long> letterToCount = words.map(w -> w.split("")).flatMap(Arrays::stream)
//      .collect(groupingBy(identity(), counting()));
//
//System.out.println(letterToCount);
//// {a=4, b=1, e=3, g=1, h=1, i=2, J=1, M=1, n=1, s=2, t=2, v=1, z=1}