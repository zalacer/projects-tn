package analysis;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;

//http://algs4.cs.princeton.edu/14analysis/BitonicMax.java.html
//http://algs4.cs.princeton.edu/14analysis/BitonicMax.java

/******************************************************************************
 *  Compilation:  javac BitonicMax.java
 *  Execution:    java BitonicMax N 
 *  Dependencies: StdOut.java
 *
 *  Find the maximum in a bitonic array (strictly increasing, then strictly
 *  decreasing) of size N in log N time.
 * 
 *  % java BitonicMax N
 *
 ******************************************************************************/


public class BitonicMax {

    // create a bitonic array of size N
    public static int[] bitonic(int N) {
        int mid = StdRandom.uniform(N);
        int[] a = new int[N];
        for (int i = 1; i < mid; i++) {
            a[i] = a[i-1] + 1 + StdRandom.uniform(9);
        }

        if (mid > 0) a[mid] = a[mid-1] + StdRandom.uniform(10) - 5;

        for (int i = mid + 1; i < N; i++) {
            a[i] = a[i-1] - 1 - StdRandom.uniform(9);
        }

//        for (int i = 0; i < N; i++) {
//            StdOut.println(a[i]);
//        }
        return a;
    } 

    // find the index of the maximum in a bitonic subarray a[lo..hi]
    public static int bitonicMax(int[] a, int lo, int hi) {
        if (hi == lo) return hi;
        int mid = lo + (hi - lo) / 2;
        System.out.println("mid="+mid);
        if (a[mid] < a[mid + 1]) return bitonicMax(a, mid+1, hi);
        if (a[mid] > a[mid + 1]) return bitonicMax(a, lo, mid);
        else return mid;
    } 
    
    // binary search
    public static int indexOf(int[] a, int key) {
      int lo = 0;
      int hi = a.length - 1;
      while (lo <= hi) {
        // Key is in a[lo..hi] or not present.
        int mid = lo + (hi - lo) / 2;
        if      (key < a[mid]) hi = mid - 1;
        else if (key > a[mid]) lo = mid + 1;
        else return mid;
      }
      return -1;
    }



    public static void main(String[] args) {

        int N = Integer.parseInt(args[0]);
        int[] a = bitonic(N);
        StdOut.println("max = " + a[bitonicMax(a, 0, N-1)]);
    }
}

