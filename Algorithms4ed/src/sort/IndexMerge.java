package sort;

import static v.ArrayUtils.*;

// http://algs4.cs.princeton.edu/22mergesort/Merge.java.html
public class IndexMerge {

  private IndexMerge(){}

  public static <T extends Comparable<? super T>>  int[] sort(T[] a) {
    int N = a.length;
    int[] index = new int[N];
    for (int i = 0; i < N; i++)
      index[i] = i;

    int[] aux = new int[N];
    sort(a, index, aux, 0, N-1);
    return index;
  }

  private static <T extends Comparable<? super T>> void sort(
      T[] a, int[] index, int[] aux, int lo, int hi) {
    if (hi <= lo) return;
    int mid = lo + (hi - lo) / 2;
    sort(a, index, aux, lo, mid);
    sort(a, index, aux, mid + 1, hi);
    merge(a, index, aux, lo, mid, hi);
  }

  private static <T extends Comparable<? super T>> void merge(
      T[] a, int[] index, int[] aux, int lo, int mid, int hi) {
    // copy to aux[]
    for (int k = lo; k <= hi; k++) {
      aux[k] = index[k]; 
    }
    // merge back to a[]
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) {
      if      (i > mid)                    index[k] = aux[j++];
      else if (j > hi)                     index[k] = aux[i++];
      else if (less(a[aux[j]], a[aux[i]])) index[k] = aux[j++];
      else                                 index[k] = aux[i++];
    }
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0;
  }

  public static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public static void main(String[] args) {
    
    Integer[] a = {19,1,16,27,4,11,6,2,10,7,18,3};
    int[] index = sort(a);
    pa(index,-1);
    Integer[] z = new Integer[a.length];
    for (int i = 0; i < a.length; i++) z[i] = a[index[i]];
    pa(z,-1); //[1,2,3,4,6,7,10,11,16,18,19,27]

  }

}
