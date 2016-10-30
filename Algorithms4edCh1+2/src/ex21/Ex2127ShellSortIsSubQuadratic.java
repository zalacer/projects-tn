package ex21;

import static sort.SortCompare.*;

import edu.princeton.cs.algs4.StdOut;

/* p267
  2.1.27 Shellsort is subquadratic. Use SortCompare to compare shellsort with insertion
  sort and selection sort on your computer. Use array sizes that are increasing powers of
  2, starting at 128.
 
 */

public class Ex2127ShellSortIsSubQuadratic {

  public static void main(String[] args) {
 
    String alg1 = "Shell"; 
    String alg2 = "Insertion"; 
    int trials = 3;
    double time1, time2;
    int p = 7;
    int n = 128;
    
//    System.out.println("Shell sort vs. Insertion sort");
//    System.out.println("========================================================================");
//    while(p < 18) {
//      time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
//      time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
//      StdOut.printf("For %d random Doubles %s is", n, alg1); 
//      StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2);
//      n = 2*n; p++;
//    }
//    
    System.out.println();
    
    alg1 = "Shell"; 
    alg2 = "Selection";
    p = 7;
    n = 128;
 
    System.out.println("Shell sort vs. Selection sort");
    System.out.println("========================================================================");
    while(p < 18) {
      time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
      time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
      StdOut.printf("For %d random Doubles %s is", n, alg1); 
      StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2);
      n = 2*n; p++;
    }
    
    //  Shell sort vs. Insertion sort
    //  ========================================================================
    //  For 128 random Doubles Shell is Infinity times faster than Insertion
    //  For 256 random Doubles Shell is 3.0 times faster than Insertion
    //  For 512 random Doubles Shell is 5.0 times faster than Insertion
    //  For 1024 random Doubles Shell is 4.0 times faster than Insertion
    //  For 2048 random Doubles Shell is 3.7 times faster than Insertion
    //  For 4096 random Doubles Shell is 10.3 times faster than Insertion
    //  For 8192 random Doubles Shell is 32.5 times faster than Insertion
    //  For 16384 random Doubles Shell is 73.0 times faster than Insertion
    //  For 32768 random Doubles Shell is 167.6 times faster than Insertion
    //  For 65536 random Doubles Shell is 315.3 times faster than Insertion
    //  For 131072 random Doubles Shell is 566.9 times faster than Insertion


    //  Shell sort vs. Selection sort
    //  ========================================================================
    //  For 128 random Doubles Shell is 2.0 times faster than Selection
    //  For 256 random Doubles Shell is Infinity times faster than Selection
    //  For 512 random Doubles Shell is 7.0 times faster than Selection
    //  For 1024 random Doubles Shell is 10.0 times faster than Selection
    //  For 2048 random Doubles Shell is 3.3 times faster than Selection
    //  For 4096 random Doubles Shell is 17.7 times faster than Selection
    //  For 8192 random Doubles Shell is 30.3 times faster than Selection
    //  For 16384 random Doubles Shell is 107.1 times faster than Selection
    //  For 32768 random Doubles Shell is 281.1 times faster than Selection
    //  For 65536 random Doubles Shell is 516.1 times faster than Selection
    //  For 131072 random Doubles Shell is 1376.3 times faster than Selection


  }
}
