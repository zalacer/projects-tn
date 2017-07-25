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
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Map.Entry;

import st.ST;

/* p389
  3.1.9  Add code to FrequencyCounter to keep track of the last call 
  to put(). Print the last word inserted and the number of words that 
  were processed in the input stream prior to this insertion. Run your 
  program for tale.txt with length cutoffs 1, 8, and 10.
  
  I assume cutoff x means exclusion of all words with length < x, 
  processed means processed after filtering by cutoff. A new method was
  implemented instead of modifiying FrequencyCounter directly.
       
 */

public class Ex3109AddCode2FrequencyCounter2TrackLastCallToPut {
  
  public static void frequencyCounter(String f, int cutoff) {
    // print the word inserted into an ST by its final call to put and also
    // the number of words that were processed in the file named f by that
    // ST prior to this insertion. in this example each string recognized by 
    // a Scanner scanning the file named f is taken as a word because the 
    // exercise statement provided no definition of what exactly "word" means.
    
    if (f == null) throw new NoSuchElementException(
        "frequencyCounter called with null string arg");
    
    ST<String, Integer> st = new ST<>(); 
    // k takes the value of one word from the file named f.
    // lasts represents the last word processed.
    // last represents the number of word processed up to and including lasts.
    String k, lasts = null, maxs = null; int words = 0, distinct = 0, max = 0, i;
    Scanner sc = null;
    
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    while (sc.hasNext()) {
      k = sc.next(); 
      if (k.length() < cutoff) continue;
      words++; lasts = k;
      if (st.contains(k)) { st.put(k, st.get(k) + 1); }
      else { st.put(k, 1); distinct++; }
    }
    
    sc.close();
    
    for (String key : st.keys()) {
      i = st.get(key);
      if (i > max) { max = i; maxs = key; }    
    }
    
    System.out.println("with cutoff "+cutoff+":");
    System.out.println("  most frequent word = "+maxs);
    System.out.println("  distinct words = "+distinct);
    System.out.println("  total words = "+words);
    System.out.println("  last word processed = "+lasts);
    System.out.println("  number of words processed prior to last put = "+(words-1)+"\n");
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
  
  // an alternative approach using Java Streams (java.util.stream).
  public static void streamsFrequencyCounter(String f, int cutoff)  {
    // print the most frequent word with length >= cutoff in the file
    // named f and also print the last word processed and the number 
    // of words processed prior to it after filtering by cutoff. In
    // this example "word" is defined by isWord().
    
    if (f == null) throw new NoSuchElementException(
        "streamsFrequencyCounter called with null string arg");
    
    // encapsulate variables in arrays to allow accessing them in streams
    // that require referenced variables to be effectively final.
    int[] x = {0}, distinct = {0}; String[] y = {null}; int[] cut = {cutoff};
    
    System.out.print("with cutoff "+cutoff+":");
    System.out.print("  most frequent word = ");
    
    try {
      Files.newBufferedReader(Paths.get(f)).lines() // this is a Stream<String> of lines.
      .map(l -> l.split("\\s+")).flatMap(Arrays::stream)
      .filter(w -> isWord(w)).filter(w -> w.length() >= cut[0])
      .filter(w -> {x[0]++; y[0]=w; return true;})
      // create a Map<String,Long> of words to their counts
      .map(String::toLowerCase).collect(groupingBy(identity(), counting()))
      // create a new Stream<Entry<String,Long>> from the map's entries.
      .entrySet().stream().filter(e -> {distinct[0]++; return true;})
      // sort the stream with entryComp and print the key of its first Entry.
      .sorted(entryComp).limit(10).forEach(e -> System.out.println(e.getKey()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println("  distinct words = "+distinct[0]);
    System.out.println("  total words = "+x[0]);
    System.out.println("  last word processed = "+y[0]);
    System.out.println("  number of words processed prior to last put = = "+(x[0]-1)+"\n");
  }
  
  public static void main(String[] args) throws IOException {
    
    System.out.println("Using a Scanner and an ST:\n");
    
    frequencyCounter("tale.txt",1);
    frequencyCounter("tale.txt",8);
    frequencyCounter("tale.txt",10);
    
    System.out.println("Using a BufferedReader and Streams:\n");

    streamsFrequencyCounter("tale.txt", 1); 
    streamsFrequencyCounter("tale.txt", 8);
    streamsFrequencyCounter("tale.txt", 10);
/*
    Using a Scanner and an ST:
    
    with cutoff 1:
      most frequent word = the
      distinct words = 10674
      total words = 135643
      last word processed = known
      number of words processed prior to last put = 135642
    
    with cutoff 8:
      most frequent word = business
      distinct words = 5126
      total words = 14346
      last word processed = faltering
      number of words processed prior to last put = 14345
    
    with cutoff 10:
      most frequent word = monseigneur
      distinct words = 2257
      total words = 4579
      last word processed = disfigurement
      number of words processed prior to last put = 4578
    
    Using a BufferedReader and Streams:
    
    with cutoff 1:  most frequent word = the
      distinct words = 10674
      total words = 135643
      last word processed = known
      number of words processed prior to last put = = 135642
    
    with cutoff 8:  most frequent word = business
      distinct words = 5126
      total words = 14346
      last word processed = faltering
      number of words processed prior to last put = = 14345
    
    with cutoff 10:  most frequent word = monseigneur
      distinct words = 2257
      total words = 4579
      last word processed = disfigurement
      number of words processed prior to last put = = 4578

*/   
  }

}
