package ds;

import static analysis.MergesortUnique.sort;
import java.util.Arrays;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// text p99 re Ex1411 p209
// added smallestIndexOf, largestIndexOf, howManyOf for Ex1411
//
// modified constructor with analysis.MergesortUnique.sort, added indexOf 
// and customized contains for Ex1421

public class StaticSETofInts {
  private int[] a;
  private int r;

  public StaticSETofInts(int[] keys) {
    a = new int[keys.length];
    for (int i = 0; i < keys.length; i++)
      a[i] = keys[i]; // defensive copy
//    Arrays.sort(a);
    r = sort(a);
  }

  public boolean contains(int key) {
    return indexOf(key, r-1) != -1; 
  }
  
  public boolean contains2(int key) {
    return rank(key) != -1; 
  }

  private int rank(int key) { 
    // unmodified binary search.
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) { 
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }
  
  private int indexOf(int key, int hi) {
    // binary search modified to search only the 1st n elementss
    int lo = 0;
    while (lo <= hi) {
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if      (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
  }

  
  public int smallestIndexOf(int key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public int largestIndexOf(int key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  @Override
  public String toString() {
    return "StaticSETofInts("+Arrays.toString(a).replaceAll("[\\[\\] ]*", "")+")";
  }
  
  public int howManyOf(int key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = smallestIndexOf(key);
    if (s == -1) return 0;
    else return largestIndexOf(key) - s + 1;
  }


  public static void main(String[] args) {

    @SuppressWarnings("deprecation")
    int[] w = In.readInts(args[0]);
    StaticSETofInts set = new StaticSETofInts(w);
    while (!StdIn.isEmpty()) { 
      // Read key, print if not in whitelist.
      int key = StdIn.readInt();
      if (set.rank(key) == -1)
        StdOut.println(key);
    }
  }

}
