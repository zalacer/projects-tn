package ex31;

import static v.ArrayUtils.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import st.ST;

/* p389
  3.1.6  Give the number of calls to put() and get() issued by 
  FrequencyCounter, as a function of the number W of words and 
  the number D of distinct words in the input.
  
  Looking http://algs4.cs.princeton.edu/31elementary/FrequencyCounter.java 
  that is in this project at st.FrequencyCounter, it does a put for every 
  word and also a get for every word that's already been (in)put.
  
  #puts = #words
  #gets = #words - #distinct
  
  This is experimentally verified for tales.txt below.
     
 */

public class Ex3106FrequencyCounterNumberOfCalls2PutAndGet {
  
  public static int[] distinctCounter(String f) {
    // return an int[] with the number of words and the number of distinct
    // words in the file named f and the number of calls to get and put  
    // issued by an ST while processing the same file. in this example 
    // each string recognized by a Scanner scanning the file named f is
    // taken as a word because the exercise statement provided no 
    // definition of what exactly "word" means.
    
    ST<String, Integer> st = new ST<>(); 
    // n represents the number of words.
    // d represents the number of distinct words.
    // g represents the number of calls to st.get().
    // p represents the number of calls to st.put().
    // k takes the value of one word from the file named f.
    int n = 0, d = 0, g = 0, p = 0; String k;
    Scanner sc = null;
    
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    while (sc.hasNext()) {
      k = sc.next(); n++;
      if (st.contains(k)) { st.put(k, st.get(k) + 1); g++; p++;}
      else { st.put(k, 1); d++; p++; }
    }
    
    sc.close();
    return new int[]{n,d,g,p};
  }
 
  public static void main(String[] args) {
    
    int[] r = distinctCounter("tale.txt");
    par(r); // [135643,10674,124969,135643]
    assert r[0] == r[3];         // #puts == #words
    assert r[2] == r[0] - r[1];  // #gets == #words - #distinct
    
  }

}
