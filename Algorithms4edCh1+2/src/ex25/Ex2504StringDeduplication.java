package ex25;

import static v.ArrayUtils.*;

import java.util.TreeSet;

/* p353
  2.5.4  Implement a method  String[] dedup(String[] a) that returns the 
  objects in a[] in sorted order, with duplicates removed.
 */

public class Ex2504StringDeduplication {

  public static String[] dedup(String[] w){
    TreeSet<String> t = new TreeSet<>();
    for (String s : w) t.add(s);
    return t.toArray(new String[0]);
  }

  public static void main(String[] args) {

    String[] w = "a b b c c c d d d e e e e f f f f f".split("\\s+");
    show(dedup(w)); // a b c d e f 
    
  }

}
