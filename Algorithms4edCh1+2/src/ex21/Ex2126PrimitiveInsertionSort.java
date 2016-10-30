package ex21;

import static v.ArrayUtils.*;
import static sort.SortCompare.*;
import static sort.Insertion.*;

import edu.princeton.cs.algs4.StdOut;

/* p267
  2.1.26 Primitive types. Develop a version of insertion sort that sorts arrays of  int
  values and compare its performance with the implementation given in the text (which
  sorts  Integer values and implicitly uses autoboxing and auto-unboxing to convert).
  
  Testing shows the primitive int version of insertion sort is 3.5 times faster than
  the generic version for sorting a reverse sorted array of length 10000.
 
 */

public class Ex2126PrimitiveInsertionSort {

  public static void main(String[] args) {
    
    int[] ia = {7,6,5,4,3,2,1,0};
    insertionSortInt(ia);
    pa(ia); //int[0,1,2,3,4,5,6,7]
    
    String alg1 = "insertionSortInt"; 
    String alg2 = "Insertion"; 
    int n = 10000;
    int trials = 3;
    double time1, time2;
    time1 = timeReverseSortedInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeReverseSortedInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    StdOut.printf("For %d reverse sorted integers\n    %s is", n, alg1); 
    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    
    //  time1=0.304
    //  time2=1.064
    //  For 10000 reverse sorted integers insertionSortInt is 3.5 times faster than Insertion

  }
}
