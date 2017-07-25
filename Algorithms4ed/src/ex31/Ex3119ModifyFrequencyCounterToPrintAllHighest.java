package ex31;

import static v.ArrayUtils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ds.Queue;
import st.ST;

/* p390
  3.1.19  Modify  FrequencyCounter to print all of the values having
  the highest frequency of occurrence, not just one of them. 
  Hint : Use a  Queue 
*/

public class Ex3119ModifyFrequencyCounterToPrintAllHighest {
  
  public static String[] getMostFrequent(String f, int l) {
    // return the most frequent words of length >= l in the file named f
    // if l > 0;  if l < 1 do not filter words by length.  
    // in this example each string recognized by a Scanner scanning the 
    // file named f is taken as a word because the exercise statement 
    // provided no definition of what exactly "word" means.
    
    ST<String, Integer> st = new ST<>(); 
    // k takes the value of one word from the file named f.
    String k; int max = 0, i;
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
      if (l > 0 && key.length() < l) continue;
      i = st.get(key);
      if (i > max) max = i;      
    }
    
    Queue<String> q = new Queue<>();
    for (String key : st.keys()) {
      if (l > 0 && key.length() < l) continue;
      if (st.get(key) == max) q.enqueue(key);
    }
    
    return q.toArray(String.class);
  }
   
  public static void main(String[] args) {
    
    String s = "tale.txt";
    System.out.println(arrayToString(getMostFrequent(s,17),90,1,1));
    /* 
      [abolishedexpression,
       businessabsorption,
       childrenresounded,
       circumstantialand,
       diamondcutdiamond,
       extinguishertopped,
       farmergeneralhowsoever,
       fellowinscrutables,
       frightenedravetear,
       furnishedevidently,
       garrisonanddockyard,
       hangingswordalley,
       implacablelooking,
       monseigneurforming,
       pancrasinthefields,
       pockethandkerchief,
       releasedcongratulating,
       runninghidingdoing,
       thingnothingstartles,
       undistinguishable]  
    */ 
    
  }

}
