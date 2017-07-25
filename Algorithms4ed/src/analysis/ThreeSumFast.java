package analysis;


//p190

import java.util.Arrays;

import edu.princeton.cs.algs4.BinarySearch;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class ThreeSumFast {

  @SuppressWarnings("deprecation")
  public static int count(int[] a) { 
    // Count triples that sum to 0.
    Arrays.sort(a);
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)
      for (int j = i+1; j < N; j++)
        if (BinarySearch.rank(-a[i]-a[j], a) > j)
          cnt++;
    return cnt;
  }
  
  // http://algs4.cs.princeton.edu/14analysis/ThreeSumFast.java
  public static boolean containsDuplicates(int[] a) {
    for (int i = 1; i < a.length; i++)
        if (a[i] == a[i-1]) return true;
    return false;
  }
  
  // http://algs4.cs.princeton.edu/14analysis/ThreeSumFast.java
  public static void printFastAndSafe(int[] a) {
    int N = a.length;
    Arrays.sort(a);
    if (containsDuplicates(a)) throw new IllegalArgumentException(
        "array contains duplicate integers");
    for (int i = 0; i < N; i++) {
        for (int j = i+1; j < N; j++) {
            int k = Arrays.binarySearch(a, -(a[i] + a[j]));
            if (k > j) StdOut.println(a[i] + " " + a[j] + " " + a[k]);
        }
    }
} 
  
  public static void main(String[] args) {
    @SuppressWarnings("deprecation")
    int[] a = In.readInts(args[0]);
    StdOut.println(count(a));
  }
}