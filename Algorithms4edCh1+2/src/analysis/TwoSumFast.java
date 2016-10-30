package analysis;

import java.util.Arrays;

import edu.princeton.cs.algs4.BinarySearch;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

//p189
public class TwoSumFast {
  @SuppressWarnings("deprecation")
  public static int count(int[] a) { 
    // Count pairs that sum to 0.
    Arrays.sort(a);
    int N = a.length;
    int cnt = 0;
    for (int i = 0; i < N; i++)
      if (BinarySearch.rank(-a[i], a) > i)
        cnt++;
    return cnt;
  }
  
  public static void main(String[] args) {
    @SuppressWarnings("deprecation")
    int[] a = In.readInts(args[0]);
    StdOut.println(count(a));
  }
}

