package ex22;

import static sort.NaturalMerge.isSorted;
import static sort.NaturalMerge.sort;
import static sort.SortCompare.timeRandomInput;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import edu.princeton.cs.algs4.StdOut;

public class Ex2216NaturalMergeSort {

  /* p285
  2.2.16 Natural mergesort. Write a version of bottom-up mergesort that takes 
  advantage of order in the array by proceeding as follows each time it needs 
  to find two arrays to merge: find a sorted subarray (by incrementing a pointer 
  until finding an entry that  is smaller than its predecessor in the array), 
  then find the next, then merge them. Analyze the running time of this 
  algorithm in terms of the array size and the number of maximal increasing 
  sequences in the array.

  The running time of natural mergesort is like that of bottom-up mergesort
  except with log(avgInitialRunLength) instead of lg and higher order factors
  due to mixed initial run lengths.

  Based on testing with pseudorandom arrays the average run length is close to 2 so
  it might perform about the same as bottom-up mergesort. But most data of interest 
  isn't random so natural mergesort should perform much better than top-down or 
  bottom-up mergesort for many applications and that's a reason why timsort works so 
  well. Note however, NaturalMerge has none of timsort's or any other enhancements and
  uses strictly monotonically increasing runs with no reversals. 
  
  SortCompare shows that NaturalMerge significantly outperforms MergeBU and Merge for 
  random arrays. In fact it even runs more than twice as fast as MergeX. The output 
  results are below. 
  
  A disadvantage of natural mergesorts including NaturalMerge is an extra space 
  requirement for storing run size data and this is ~N/avgInitialRunLength initially 
  which is ~N worst case and decreases exponentially since each pass approximately reduces 
  the number of runs by a factor of two, since there may be a trailing run that can't be 
  merged until a level with an even number of runs.
  
  (For an informative discussion on merge algorithms particularly timsort, 
  see  https://svn.python.org/projects/python/trunk/Objects/listsort.txt.
  It's source can be downloaded from
  http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/TimSort.java/)
  
   */ 

  public static void main(String[] args) {

    Random r; Integer[] w;

    for (int i = 2; i < 1002; i++) {
      w = rangeInteger(1, i, 1);
      r = new Random(System.currentTimeMillis());
      shuffle(w, r); 
      sort(w);
      assert isSorted(w);
    }

    String alg1 = "NaturalMerge"; 
    String alg2 = "MergeBU"; 
    int n = 10000;
    int trials = 10;
    double time1, time2;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    StdOut.printf("For %d random Doubles %s is", n, alg1); 
    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    /*
      running MergeBU first
      time1=0.079
      time2=0.047000000000000014
      For 10000 random Doubles MergeBU is 0.6 times faster than NaturalMerge
      
      running NaturalMerge first
      time1=0.047
      time2=0.10200000000000002
      For 10000 random Doubles NaturalMerge is 2.2 times faster than MergeBU
     */

    alg1 = "NaturalMerge"; 
    alg2 = "Merge"; 
    n = 10000;
    trials = 10;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    StdOut.printf("For %d random Doubles\n    %s is", n, alg1); 
    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    /*
       running Merge first
       time1=0.14400000000000002
       time2=0.05
       For 10000 random Doubles Merge is 0.3 times faster than NaturalMerge   
  
       running NaturalMerge first
       time2=0.134
       For 10000 random Doubles NaturalMerge is 3.5 times faster than Merge
     */
    
    alg1 = "NaturalMerge"; 
    alg2 = "MergeX"; 
    n = 10000;
    trials = 10;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    StdOut.printf("For %d random Doubles\n    %s is", n, alg1); 
    StdOut.printf(" %.1f times faster than %s\n", time2/time1, alg2); 
    /*
       running MergeX first
       time1=0.10899999999999999
       time2=0.045000000000000005
       For 10000 random Doubles MergeX is 0.4 times faster than NaturalMerge

       running NaturalMerge first
       time1=0.049000000000000016
       time2=0.127
       For 10000 random Doubles
       NaturalMerge is 2.6 times faster than MergeX
     */
  }

}
