package ex23;

import static v.ArrayUtils.*;
import static sort.Quicks.compareQuickSortDoubleAlgs;

import java.util.Iterator;

public class Ex2319QuickSortAddMedianOf5Partitioning {

  /* p305  
  2.3.19 Median-of-5 partitioning. Implement a quicksort based on partitioning on the
  median of a random sample of five items from the subarray. Put the items of the sample
  at the appropriate ends of the array so that only the median participates in partitioning.
  Run doubling tests to determine the effectiveness of the change, in comparison both
  to the standard algorithm and to median-of-3 partitioning (see the previous exercise).
  Extra credit : Devise a median-of-5 algorithm that uses fewer than seven compares on
  any input.
  */ 
  
  public static int medianOf5Int(int[] z, int i, int j, int k, int l, int m){
    // returns index in z of key with median value over the values of the indices of z 
    // specified by the 5 int args using 6 comparisons and without altering z
    if (z == null) return -1; int n = z.length;
    int[] indices = {i,j,k,l,m};
    for (int q = 0; q < indices.length; q++) if (q < 0 || q > n-1) 
      throw new IllegalArgumentException("medianOf5Int: all int args must > 0 && < z.length");
    if (unique(indices).length < n) 
      throw new IllegalArgumentException("medianOf5Int: all int args must have unique values");   
    int t;
    if(z[j]<z[i]) { t = i; i = j; j = t; }
    if(z[l]<z[k]) { t = k; k = l; l = t; }
    if(z[k]<z[i]) { t = j; j = l; l = t; k=i; }
    i=m;
    if(z[j]<z[i]) { t = i; i = j; j = t; }
    if(z[i]<z[k]) { t = j; j = l; l = t; i=k; }
    return z[l]<z[i] ? l : i;
  }
  
  public static void medianOf5IntTest() {
    // runs medianOf5Int on all permutations of [1,2,3,4,5] and tests that
    // the value in the permutation of the index returned by it equals 3.
    int[] a = {1,2,3,4,5}, next; int c = 0;
    Iterator<int[]> it = permutations(a);
    while(it.hasNext()) {
      c++; next = it.next(); 
      assert next[medianOf5Int(next,0,1,2,3,4)] == 3;
    }
    System.out.println(c+" permutations done");
  }
  
  
  public static void main(String[] args) {

    medianOf5IntTest();
    
    // sort.Quicks.quickM5 uses median of 3 partitioning for subarray lengths < 10 
    // and median of 5 partitioning for subarray lengths >= 10

    compareQuickSortDoubleAlgs("quick", "quickM5", 5);
/*
    quick vs quickM5 average times over 5 trials for random Double arrays
    array length           quick         time/prev           quickM5         time/prev
       2^8                0.000                NA             6.200                NA
       2^9                0.000               NaN             0.000             0.000
      2^10                0.000               NaN             0.000               NaN
      2^11                3.000          Infinity             0.000               NaN
      2^12               15.600             5.200             6.400          Infinity
      2^13               27.800             1.782             9.600             1.500
      2^14                9.200             0.331            15.800             1.646
      2^15                9.200             1.000            68.800             4.354
      2^16               15.600             1.696            40.600             0.590
      2^17               56.200             3.603            53.000             1.305
      2^18               99.800             1.776            81.000             1.528
      2^19              190.200             1.906           168.800             2.084
      2^20              415.000             2.182           383.600             2.273
      2^21             1444.400             3.480           861.200             2.245
      2^22             3141.800             2.175          2184.000             2.536
      2^23             8065.200             2.567          5815.600             2.663
*/
    
  }

}

