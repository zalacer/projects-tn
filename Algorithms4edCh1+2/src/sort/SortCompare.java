package sort;

//import static v.ArrayUtils.*;
import static sort.Insertion.*;
import static sort.Selection.*;
//import static sort.Merges.*;

import edu.princeton.cs.algs4.BinaryInsertion;
import edu.princeton.cs.algs4.Heap;
//import edu.princeton.cs.algs4.Insertion;
import sort.Insertion;
import sort.Selection;
import edu.princeton.cs.algs4.InsertionX;
import edu.princeton.cs.algs4.Merge;
import edu.princeton.cs.algs4.MergeBU;
import edu.princeton.cs.algs4.MergeX;
import edu.princeton.cs.algs4.Quick;
import edu.princeton.cs.algs4.Quick3way;
import edu.princeton.cs.algs4.QuickX;
//import edu.princeton.cs.algs4.Selection;
import edu.princeton.cs.algs4.Shell;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import sort.NaturalMerge;
import sort.Merges;

import static java.lang.Math.pow;
import static v.ArrayUtils.shellSort;
import static v.ArrayUtils.unbox;

import java.util.ArrayList;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/21elementary/SortCompare.java
 *  http://algs4.cs.princeton.edu/21elementary/SortCompare.java.html
 *  Compilation:  javac SortCompare.java
 *  Execution:    java SortCompare alg1 alg2 n trials
 *  Dependencies: StdOut.java Stopwatch.java
 *  
 *  Sort n random real numbers, trials times using the two
 *  algorithms specified on the command line.
 * 
 *  % java SortCompare Insertion Selection 1000 100 
 *  For 1000 random Doubles 
 *    Insertion is 1.7 times faster than Selection
 *
 *  Note: this program is designed to compare two sorting algorithms with
 *  roughly the same order of growth, e,g., insertion sort vs. selection
 *  sort or mergesort vs. quicksort. Otherwise, various system effects
 *  (such as just-in-time compiliation) may have a significant effect.
 *  One alternative is to execute with "java -Xint", which forces the JVM
 *  to use interpreted execution mode only.
 *
 ******************************************************************************/

import java.util.Arrays;
import java.util.List;

public class SortCompare { 
  
  public static int[] increments = createIncrementArray();
  
  public static int[] createIncrementArray() {
    // based on https://en.wikipedia.org/wiki/Shellsort#Gap_sequences this is O(N**(4/3))
    List<Integer> list = new ArrayList<>();
    int k = 0; long r = 0;
    list.add(1);
    
    while(true) {
      r = (long) (pow(4,k) + 3.*pow(2,k-1) + 1);
      if (r >= Integer.MAX_VALUE) break;
      if (r > 0) list.add((int)r);
      k++;
    }
    
    int[] z = (int[]) unbox(list.toArray(new Integer[0]));
    shellSort(z);
    return z;
  }
  
  public static  <T extends Comparable<? super T>> void shellSortWithIncrementArray(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int k = 0;
    int h = 1;
//    int[] inc = {1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929,16001, 36289, 64769, 146305, 260609};
//    int[] inc = {1, 4, 13, 40, 121, 364, 1093};
    while (h < N/3) {
      h = increments[++k];
    }
    
//    System.out.println("h="+h);
    
//    int[] inc = {1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929,
//        16001, 36289, 64769, 146305, 260609};
    while (true) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h)
          exch(a, j, j-h);
      }
      if (k == 0) {
        break;
      } else h = increments[--k];
//      h = k > 0 ? inc[--k] : 1;

    }
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }
  
  public static double time(String alg, Double[] a) { 
    Stopwatch sw = new Stopwatch(); 
    if      (alg.equals("Insertion"))       Insertion.sort(a); 
    else if (alg.equals("InsertionX"))      InsertionX.sort(a); 
    else if (alg.equals("BinaryInsertion")) BinaryInsertion.sort(a); 
    else if (alg.equals("Selection"))       Selection.sort(a); 
    else if (alg.equals("Bubble"))          Bubble.sort(a); 
    else if (alg.equals("Shell"))           Shell.sort(a); 
    else if (alg.equals("Merge"))           Merge.sort(a); 
    else if (alg.equals("MergeX"))          MergeX.sort(a); 
    else if (alg.equals("MergeBU"))         MergeBU.sort(a); 
    else if (alg.equals("Quick"))           Quick.sort(a); 
    else if (alg.equals("Quick3way"))       Quick3way.sort(a); 
    else if (alg.equals("QuickX"))          QuickX.sort(a); 
    else if (alg.equals("Heap"))            Heap.sort(a); 
    else if (alg.equals("System"))          Arrays.sort(a);
    else if (alg.equals("shellSortWithIncrementArray")) shellSortWithIncrementArray(a);
    else if (alg.equals("sortWithSentinel")) Insertion.sortWithSentinel(a);
    else if (alg.equals("sortWithoutExchanges")) Insertion.sortWithoutExchanges(a);
    else if (alg.equals("sortWithSentinelAndWithoutExchanges")) Insertion.sortWithSentinelAndWithoutExchanges(a);
    else if (alg.equals("selectionTestHypo2128")) Selection.selectionTestHypo2128(a);
    else if (alg.equals("insertionTestHypo2128")) Insertion.insertionTestHypo2128(a);
    else if (alg.equals("NaturalMerge")) NaturalMerge.sort(a);
    else if (alg.equals("topDown")) Merges.topDown(a);
    else if (alg.equals("topDownAm")) Merges.topDownAm(a);
    else if (alg.equals("topDownAcCoSm")) Merges.topDownAcCoSm(a,31);
    else if (alg.equals("bottomUpCoFmSm")) Merges.bottomUpCoFmSm(a,8);
    else throw new IllegalArgumentException("Invalid algorithm: " + alg);
    return sw.elapsedTime(); 
  }
  
  public static double time(String alg, double[] a) { 
    Stopwatch sw = new Stopwatch(); 
    if (alg.equals("System"))               Arrays.sort(a);
//    else if (alg.equals("swapSort"))        swapSort(a);
    else throw new IllegalArgumentException("Invalid algorithm: " + alg);
    return sw.elapsedTime(); 
  }

  public static double time(String alg, Integer[] a) { 
    Stopwatch sw = new Stopwatch(); 
    if      (alg.equals("Insertion"))       Insertion.sort(a); 
    else if (alg.equals("InsertionX"))      InsertionX.sort(a); 
    else if (alg.equals("BinaryInsertion")) BinaryInsertion.sort(a); 
    else if (alg.equals("Selection"))       Selection.sort(a); 
    else if (alg.equals("Bubble"))          Bubble.sort(a); 
    else if (alg.equals("Shell"))           Shell.sort(a); 
    else if (alg.equals("Merge"))           Merge.sort(a); 
    else if (alg.equals("MergeX"))          MergeX.sort(a); 
    else if (alg.equals("MergeBU"))         MergeBU.sort(a); 
    else if (alg.equals("Quick"))           Quick.sort(a); 
    else if (alg.equals("Quick3way"))       Quick3way.sort(a); 
    else if (alg.equals("QuickX"))          QuickX.sort(a); 
    else if (alg.equals("Heap"))            Heap.sort(a); 
    else if (alg.equals("System"))          Arrays.sort(a);
    else if (alg.equals("shellSortWithIncrementArray")) shellSortWithIncrementArray(a);
    else if (alg.equals("sortWithSentinel")) Insertion.sortWithSentinel(a);
    else if (alg.equals("sortWithoutExchanges")) Insertion.sortWithoutExchanges(a);
    else if (alg.equals("sortWithSentinelAndWithoutExchanges")) Insertion.sortWithSentinelAndWithoutExchanges(a);
    else if (alg.equals("selectionTestHypo2128")) selectionTestHypo2128(a);
    else if (alg.equals("insertionTestHypo2128")) insertionTestHypo2128(a);
    else if (alg.equals("NaturalMerge")) NaturalMerge.sort(a);
    else if (alg.equals("topDown")) Merges.topDown(a);
    else if (alg.equals("topDownAm")) Merges.topDownAm(a);
    else if (alg.equals("topDownAcCoSm")) Merges.topDownAcCoSm(a,31);
    else if (alg.equals("bottomUpCoFmSm")) Merges.bottomUpCoFmSm(a,8);
    else throw new IllegalArgumentException("Invalid algorithm: " + alg);
    return sw.elapsedTime(); 
  } 

  public static double time(String alg, int[] a) { 
    Stopwatch sw = new Stopwatch(); 
    if (alg.equals("insertionSortInt")) Insertion.insertionSortInt(a); 
    return sw.elapsedTime(); 
  } 
  
  // Use alg to sort trials random arrays of length n.
  public static double timeRandomInput(String alg, int n, int trials)  {
    double total = 0.0;
    if (alg.equals("swapSort")) {
      double[] a = new double[n];
      for (int t = 0; t < trials; t++) {
        for (int i = 0; i < n; i++)
          a[i] = StdRandom.uniform(0.0, 1.0);
        total += time(alg, a);
      } 
      return total; 
    } else {
      Double[] a = new Double[n];
      // Perform one experiment (generate and sort an array).
      for (int t = 0; t < trials; t++) {
        for (int i = 0; i < n; i++)
          a[i] = StdRandom.uniform(0.0, 1.0);
        total += time(alg, a);
      } 
      return total; 
    }
  } 

  // Use alg to sort trials random arrays of length n. 
  public static double timeSortedInput(String alg, int n, int trials) {
    double total = 0.0;
    if (alg.equals("swapSort")) {
      double[] a = new double[n];
      for (int t = 0; t < trials; t++) {
        for (int i = 0; i < n; i++)
          a[i] = 1.0 * i;
        total += time(alg, a);
      } 
      return total; 
    } else {
      Double[] a = new Double[n];
      // Perform one experiment (generate and sort an array).
      for (int t = 0; t < trials; t++) {
        for (int i = 0; i < n; i++)
          a[i] = 1.0 * i;
        total += time(alg, a);
      } 
      return total; 
    }
  } 

  public static double timeIdenticalKeysInput(String alg, int n, int trials) {
    double total = 0.0;
    Integer[] a = new Integer[n];
    // Perform one experiment (generate and sort an array).
    for (int t = 0; t < trials; t++) {
      for (int i = 0; i < n; i++)
        a[i] = 9;
      total += time(alg, a);
    } 
    return total; 
  }

  public static double timeReverseSortedInput(String alg, int n, int trials) {
    double total = 0.0;
    if (alg.equals("insertionSortInt")) {
      int[] a = new int[n];
      // Perform one experiment (generate and sort an array).
      for (int t = 0; t < trials; t++) {
        for (int i = n-1, j = 0; i > -1; i--, j++)
          a[j] = i;
        total += time(alg, a);
      } 
      return total; 
    }
    Integer[] a = new Integer[n];
    // Perform one experiment (generate and sort an array).
    for (int t = 0; t < trials; t++) {
      for (int i = n-1, j = 0; i > -1; i--, j++)
        a[j] = i;
      total += time(alg, a);
    } 
    return total; 
  } 

  public static void main(String[] args) {

    String alg1 = "Insertion"; 
    String alg2 = "sortWithSentinel"; 
    int n = 10000;
    int trials = 3;
    double time1, time2;
//    time1 = timeReverseSortedInput(alg1, n, trials);   // Total for alg1. 
//    time2 = timeReverseSortedInput(alg2, n, trials);   // Total for alg2.     
//    time1 = timeIdenticalKeysInput(alg1, n, trials);   // Total for alg1. 
//    time2 = timeIdenticalKeysInput(alg2, n, trials);   // Total for alg2. 
//    StdOut.printf("For %d identical Integers\n    %s is", n, alg1); 
//    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 

    //        String alg1 = args[0]; 
    //        String alg2 = args[1]; 
    //        int n = Integer.parseInt(args[2]);
    //        int trials = Integer.parseInt(args[3]);
    //        double time1, time2;
    //        if (args.length == 5 && args[4].equals("sorted")) {
    //            time1 = timeSortedInput(alg1, n, trials);   // Total for alg1. 
    //            time2 = timeSortedInput(alg2, n, trials);   // Total for alg2. 
    //        }
    //        else {
                time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
                time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
                System.out.println("time1="+time1);
                System.out.println("time2="+time2);
    //        }
    //
            StdOut.printf("For %d random Doubles\n    %s is", n, alg1); 
            StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
  } 
} 

