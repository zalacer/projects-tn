package ex25;

import static analysis.KendallTau.distance;
import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.permutations;

import java.util.Iterator;

/* p355
  2.5.19 Kendall tau distance. Write a program  KendallTau.java that computes 
  the Kendall tau distance between two permutations in linearithmic time.
  
  This is already done in 
      http://algs4.cs.princeton.edu/25applications/KendallTau.java.html
  that is available locally at analysis.KendallTau.
  Note this requires
      http://algs4.cs.princeton.edu/22mergesort/Inversions.java
  that is available locally at analysis.Inversions.
  
  A demo of KendallTau is given below.
  
 */

public class Ex2519KendallTauDistance {
  
  public static void main(String[] args) {
    
    int[] a = new int[]{0,1,2,3};
    Iterator<int[]> it = permutations(a);
    // print the KendallTau distance from a to each permutation of a
    while (it.hasNext()) {
      int[] b = it.next();
      System.out.println(arrayToString(b,80,1,1)+"  "+distance(a, b));
    }
/*
    array      KendallTau distance from [0,1,2,3]
    [0,1,2,3]  0
    [0,1,3,2]  1
    [0,3,1,2]  2
    [3,0,1,2]  3
    [3,0,2,1]  4
    [0,3,2,1]  3
    [0,2,3,1]  2
    [0,2,1,3]  1
    [2,0,1,3]  2
    [2,0,3,1]  3
    [2,3,0,1]  4
    [3,2,0,1]  5
    [3,2,1,0]  6
    [2,3,1,0]  5
    [2,1,3,0]  4
    [2,1,0,3]  3
    [1,2,0,3]  2
    [1,2,3,0]  3
    [1,3,2,0]  4
    [3,1,2,0]  5
    [3,1,0,2]  4
    [1,3,0,2]  3
    [1,0,3,2]  2
    [1,0,2,3]  1    
*/
  }

}


