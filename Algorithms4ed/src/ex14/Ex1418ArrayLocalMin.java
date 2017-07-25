package ex14;

import static v.ArrayUtils.pa;
import static v.ArrayUtils.shuffle;
import static v.ArrayUtils.take;
import static v.ArrayUtils.takeRight;
import static v.ArrayUtils.unique;
import static java.util.Arrays.copyOf;

import java.util.Arrays;
import java.util.Random;

//  p210
//  1.4.18 Local minimum of an array. Write a program that, given an array  a[] of N dis-
//  tinct integers, finds a local minimum: an index  i such that  a[i-1] < a[i] < a[i+1] .
//  Your program should use ~2lg N compares in the worst case.
//
//  Answer : Examine the middle value  a[N/2] and its two neighbors  a[N/2 - 1] and
//  a[N/2 + 1] . If  a[N/2] is a local minimum, stop; otherwise search in the half with the
//  smaller neighbor.

// this problem is incorrectly stated in the text on p210: the conditions on a local 
// miminum of an array a at index i are "a[i] < a[i-1] and a[i] < a[i+1] (assuming the 
// neighboring entry is in bounds)" (from http://algs4.cs.princeton.edu/14analysis/). 
// additionally, the assumption implies that both neighboring entries don't have to be 
// in bounds, but I will assume that at least one neighboring entry must be in bounds, 
// which rules out arrays under length 2.

public class Ex1418ArrayLocalMin {
  
  public static int arrayLocalMin(int[] z) {
    //return the index of a local minimum if possible or -1 which may be a false negative
    if (z == null)  return -1;
    if (countPairs(copyOf(z, z.length)) > 0) throw new IllegalArgumentException(
        "arrayLocalMin: the elements of z must be unique");
    int n = z.length;
    int deltaN = 0; // for final n index adjustment
  
    while(true) {
      if (n < 2) return -1;
      if (n == 2) return z[0] < z[1] ? deltaN : deltaN+1;
      if (n == 3 && z[1] < z[0] && z[1] < z[2]) return deltaN+1;
      // test the middle element
      if (z[n/2] < z[n/2-1] && z[n/2] < z[n/2+1]) return n/2+deltaN;
      if (z[n/2-1] <= z[n/2+1]) {
        z = take(z, n/2);
      } else {
        z = takeRight(z, n/2);
        deltaN+=(n - n/2);
      }
      n = z.length;
      
      continue;
    }
  }
  
  public static int countPairs(int[] a) {
    // count pairs of identical elements in a. if an element is repeated n times, n/2
    // is added to the count, for example 1,1,1,1 and 1,1,1,1,1 both have two pairs.
    // this method has linearithmic order of growth due to Arrays.sort mergesort
    if (a == null) throw new IllegalArgumentException("countPairs: the array must be non null");
    int N = a.length;
    if (N < 2) return 0;
    Arrays.sort(a); // NlogN
    int c = 0;
    for (int i = 0; i < N-1; i++)
      if (a[i] == a[i+1]) {c++; i++;} //N
    return c;
  }
  
  public static void main(String[] args) {
    
    int[] a = new int[]{9,8,7,6,5,4,3,2,1};
//    System.out.println(arrayLocalMin(a)); //8
//    
//    a = new int[]{1,2,3,4,5,6,7,8,9};
//    System.out.println(arrayLocalMin(a)); //0
//    
//    a = new int[]{2,4,3,5,1,0,7,8,9};
//    System.out.println(arrayLocalMin(a)); //5
//    
//    a = new int[]{2,4,3,0,1,5,7,8,9};
//    System.out.println(arrayLocalMin(a)); //3
//    
    a = new int[]{5,6,4};
    System.out.println(arrayLocalMin(a)); //-1
    // this may be considered to be a false positive since in [6,4] 4 is a local
    // minimum, but since 5 > 4 and a.length == 3 ,this subarray is skipped
    
    a = new int[]{5,6,4,3}; 
    System.out.println(arrayLocalMin(a)); //3
    // now with a.length = 4, [4,3] is examined
    
    a = new int[]{5,6,4,2,3};
    System.out.println(arrayLocalMin(a));//3
    
    a = new int[]{7,5,6,4,2,3,8};
    System.out.println(arrayLocalMin(a)); ////-1
    // false negative since a[4] is a local minimum - this is by design through
    // arranging for arrayLocalMin to test [2,3,8] on the second pass
      
    Random r = new Random(773);
    a = unique(r.ints(100002, -10000, 10001).toArray());
    System.out.println(arrayLocalMin(a)); //0
    
    a = unique(r.ints(50, -998, 1000).toArray());
    shuffle(a, r);
    pa(a,5000);
    // int[770,757,784,512,288,535,938,-456,-506,-269,-104,901,471,628,-942,-83,-714,-134,
    // -114,40,146,949,-934,874,-869,937,18,552,254,-167,324,167,-858,869,422,-188,-493,940,
    // 241,-685,411,-277,-46,-767,-943,278,-720,-245,-717,801]

    System.out.println(arrayLocalMin(a)); //12
    // found 12 on second pass
    // the indices of all the local minimums are:  1, 4, 8, 10, 12, 14, 16, 22, 24, 26, 29, 
    // 32, 36, 39, 41, 44, 46 and 48 
 
  }

}
