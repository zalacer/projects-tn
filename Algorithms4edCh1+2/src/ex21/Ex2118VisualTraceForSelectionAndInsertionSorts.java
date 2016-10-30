package ex21;

import java.util.Arrays;
import java.util.Random;

import sort.Insertion;
import sort.Selection;

/* p265
  2.1.18 Visual trace. Modify your solution to the previous exercise to make  Insertion
  and  Selection produce visual traces such as those depicted in this section. Hint : Judi-
  cious use of setYscale() makes this problem easy. Extra credit : Add the code necessary
  to produce red and gray color accents such as those in our figures.

  I adapted the code from http://algs4.cs.princeton.edu/21elementary/SelectionBars.java
  and from http://algs4.cs.princeton.edu/21elementary/SelectionBars.java, which are
  locally available in the sort package.

 */

public class Ex2118VisualTraceForSelectionAndInsertionSorts {

  public static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    Random r = new Random();
    int N = 20;
    Double[] da = new Double[N];
    for (int i = 0; i < da.length; i++) da[i] = r.nextDouble();

    while (true) {
      
      for (int i = 0; i < da.length; i++) da[i] = r.nextDouble();

      Selection.visualSort2(Arrays.copyOf(da, N));

      pause(1500);
      
      Insertion.visualSort2(da);
      
      pause(1500);

    }

  }

}
