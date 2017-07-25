package ex21;

import static sort.SortCompare.*;
import static sort.Insertion.*;

import edu.princeton.cs.algs4.StdOut;

/* p267
  2.1.24 Insertion sort with sentinel. Develop an implementation of insertion sort that
  eliminates the j>0 test in the inner loop by first putting the smallest item into position.
  Use SortCompare to evaluate the effectiveness of doing so. Note : It is often possible to
  avoid an index-out-of-bounds test in this way—the element that enables the test to be
  eliminated is known as a sentinel.
  
  Testing shows insertion with sentinel is 10-25% faster than plain inserton for random double
  arrays of length 10K.
 
 */

public class Ex2124InsertionSortWithSentinel {

  public static void main(String[] args) {
    
    Integer[] ia = {7,6,5,4,3,2,1,0};
    sortWithSentinel(ia);
    assert isSorted(ia);

    String alg1 = "sortWithSentinel"; 
    String alg2 = "Insertion"; 
    int n = 10000;
    int trials = 10;
    double time1, time2;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    StdOut.printf("For %d random Doubles\n    %s is", n, alg1); 
    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    
//    time1=1.6849999999999996
//        time2=1.9960000000000004
//        For 10000 random Doubles
//            sortWithSentinel is 1.2 times faster than Insertion


  }
}
