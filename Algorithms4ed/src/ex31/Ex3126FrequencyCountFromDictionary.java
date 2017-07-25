package ex31;

import static v.ArrayUtils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import st.ST;
import v.Tuple2;

/* p392
  3.1.26 Frequency count from a dictionary. Modify FrequencyCounter to 
  take the name of a dictionary file as its argument, count frequencies 
  of the words from standard input that are also in that file, and print 
  two tables of the words with their frequencies, one sorted by frequency, 
  the other sorted in the order found in the dictionary file.
  
*/

public class Ex3126FrequencyCountFromDictionary {
  
  public static void report(String f) {
    // print frequencies of words read on stdin that are also in the file 
    // named f sorted by frequency and print the same sorted by order of  
    // first occurrence in the file.
      
    // build ST named st of words from stdin where values are counts
    ST<String, Integer> st = new ST<>();
    Scanner sc = new Scanner(System.in); String k;
    
    while (sc.hasNext()) {
      k = sc.next(); 
      if (st.contains(k)) st.put(k, st.get(k) + 1);
      else st.put(k,1);
    }
    sc.close();
    
    // build ST named dict of words from File(f) where values are counts
    // build ST named order of words from File(f) where values are ordinals of 1st occurrences
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    ST<String, Integer> dict = new ST<>();
    ST<String, Integer> order = new ST<>(); int c = 0;
    
    while (sc.hasNext()) {
      k = sc.next(); 
      if (st.contains(k)) dict.put(k, st.get(k) + 1);
      else dict.put(k, 1);
      if (!order.contains(k)) order.put(k, c++);
    }    
    sc.close();
    
    // convert st to an array including only words in File(f)
    Tuple2<String,Integer>[] t = ofDim(Tuple2.class, st.size()); c = 0;
    
    for (String word : st.keys())
      if (dict.contains(word))
        t[c++] = new Tuple2<String,Integer>(word,st.get(word));
    
    // remove nulls from the array (caused by words in stdin not in File(f))
    t = take(t,c);
    
    // define a comparator to sort array first by frequency
    Comparator<Tuple2<String,Integer>> tcomp = (x,y) -> {
      int d = x._2 - y._2;
      if (d != 0) return d;
      return x._1.compareTo(y._1); 
    };
    
    Arrays.sort(t,tcomp);
    
    // find length of longest word in t for formatting
    int max = 0;
    for (int i = 0; i<t.length; i++) if (t[i]._1.length() > max) max = t[i]._1.length();
    
    System.out.println("input words and frequencies sorted by frequency:");
    System.out.println("word         frequency");
    for (int i = 0; i<t.length; i++) System.out.printf("%-"+max+"s  %d\n", t[i]._1, t[i]._2);
    
    // redefine comparator to sort array by order of occurrence in File(f)
    tcomp = (x,y) -> { return order.get(x._1) - order.get(y._1); };
    
    Arrays.sort(t,tcomp);
    
    System.out.println("\ninput words and frequencies in dictionary order:");
    for (int i = 0; i<t.length; i++) System.out.printf("%-"+max+"s  %d\n", t[i]._1, t[i]._2);
  }
  
  public static void main(String[] args) {
    
    report("tale.txt");
/*
    input words and frequencies sorted by frequency:
    word         frequency
    monseigneur  1
    confidence   2
    expression   3
    appearance   4
    understand   5
    everything   6
    guillotine   7
    themselves   8
    afterwards   9
    
    input words and frequencies in dictionary order:
    everything   6
    appearance   4
    confidence   2
    expression   3
    themselves   8
    understand   5
    afterwards   9
    monseigneur  1
    guillotine   7
    
    the input words:
    afterwards
    afterwards
    afterwards
    afterwards
    afterwards
    afterwards
    afterwards
    afterwards
    afterwards
    appearance
    appearance
    appearance
    appearance
    confidence
    confidence
    everything
    everything
    everything
    everything
    everything
    everything
    expression
    expression
    expression
    guillotine
    guillotine
    guillotine
    guillotine
    guillotine
    guillotine
    guillotine
    monseigneur
    oblada
    obladi
    themselves
    themselves
    themselves
    themselves
    themselves
    themselves
    themselves
    themselves
    understand
    understand
    understand
    understand
    understand  
*/    
  
  }
  
}

