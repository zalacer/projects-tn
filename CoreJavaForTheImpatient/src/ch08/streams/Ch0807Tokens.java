package ch08.streams;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

// 7. Turning a file into a stream of tokens, list the first 100 tokens that 
// are words in the sense of the preceding exercise. Read the file again and 
// list the 10 most frequent words, ignoring letter case.

public class Ch0807Tokens {

  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }
  
  public static Comparator<Entry<String, Long>> entryComp = (e1, e2) -> {
    int c2 = e2.getValue().compareTo(e1.getValue());
    int c1 = e1.getKey().compareTo(e1.getKey());
    return c2 == 0 ? c1 : c2;
  };

  public static void main(String[] args) throws IOException {

    String b = "books/AlicesAdventuresInWonderland3339.txt";
    Object[] words100 = Files.newBufferedReader(Paths.get(b)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
        .filter(w -> isWord(w)).limit(100).toArray();
    System.out.println("first 100 tokens that are words:");
    for (Object o : words100) System.out.println(o);

    System.out.println("\ntop 10:");
    Files.newBufferedReader(Paths.get(b)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream).filter(w -> isWord(w))
        .map(String::toLowerCase).collect(groupingBy(identity(), counting()))
        .entrySet().stream().sorted(entryComp).limit(10).forEach(System.out::println);    
    //      top 10:
    //      the=1605
    //      and=766
    //      to=706
    //      a=614
    //      she=518
    //      of=493
    //      said=421
    //      it=362
    //      in=351
    //      was=333

  }

}
