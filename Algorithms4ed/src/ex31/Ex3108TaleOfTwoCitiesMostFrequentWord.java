package ex31;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Map.Entry;

import st.ST;

/* p389
  3.1.8  What is the most frequently used word of ten letters or more in 
  Tale of Two Cities?
  
  "monseigneur"
       
 */

public class Ex3108TaleOfTwoCitiesMostFrequentWord {
  
  public static String getMostFrequent(String f, int l) {
    // return the most frequent word of length >= l in the file named f.
    // in this example each string recognized by a Scanner scanning the 
    // file named f is taken as a word because the exercise statement 
    // provided no definition of what exactly "word" means.
    
    ST<String, Integer> st = new ST<>(); 
    // k takes the value of one word from the file named f.
    String k, maxs = null; int max = 0, i;
    Scanner sc = null;
    
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    while (sc.hasNext()) {
      k = sc.next();
      if (st.contains(k)) { st.put(k, st.get(k) + 1); }
      else st.put(k, 1);
    }
    
    sc.close();
    
    for (String key : st.keys()) {
      if (key.length() < l) continue;
      i = st.get(key);
      if (i > max) { max = i; maxs = key; }    
    }
    
    return maxs;
  }

  // a concrete definition of word for an alternative approach
  public static boolean isWord(String s) {
    if (s.length() == 0) return false;
    return s.codePoints().allMatch(Character::isAlphabetic);
  }
  
  // a Comparator for an alternative approach
  public static Comparator<Entry<String, Long>> entryComp = (e1, e2) -> {
    int c2 = e2.getValue().compareTo(e1.getValue());
    int c1 = e1.getKey().compareTo(e1.getKey());
    return c2 == 0 ? c1 : c2;
  };
  
  public static void main(String[] args) throws IOException {

    String s = "tale.txt";
    
    System.out.println("most frequent = "+getMostFrequent(s, 10));
    // most frequent = monseigneur

    // an alternative approach using Java Streams (java.util.stream).
    Files.newBufferedReader(Paths.get(s)).lines()
        .map(l -> l.split("\\s+")).flatMap(Arrays::stream).filter(w -> isWord(w))
        .filter(w -> w.length() > 9)
        .map(String::toLowerCase).collect(groupingBy(identity(), counting()))
        .entrySet().stream().sorted(entryComp).limit(1)
        .forEach(e -> System.out.println("most frequent = "+e.getKey()));  
    // most frequent = monseigneur
    
  }

}
