package ex22;

import static java.lang.reflect.Array.newInstance;

public class Ex2211ThreeMergeSortImprovements {

/* p285
  2.2.11 Improvements. Implement the three improvements to mergesort that are de-
  scribed in the text on page 275: Add a cutoff for small subarrays, test whether the array is
  already in order, and avoid the copy by switching arguments in the recursive code.
  
  This is available at http://algs4.cs.princeton.edu/22mergesort/MergeX.java.html and
  in the course downloads.
  
*/

  public static <T extends Comparable<? super T>> void sort(T[] a) {
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length);    
    sort(a, aux, 0, a.length - 1);
  }

  private static <T extends Comparable<? super T>>void sort(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort(a, aux, lo, mid); // Sort left half.
    sort(a, aux, mid+1, hi); // Sort right half.
    merge(a, aux, lo, mid, hi); // Merge results (code on page 271).
  }

  private static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    for (int i = lo; i <= mid; i++)
      aux[i] = a[i]; 

    for (int j = mid+1; j <= hi; j++)
      aux[j] = a[hi-j+mid+1];

    int i = lo, j = hi; 
    for (int k = lo; k <= hi; k++) 
      if (less(aux[j], aux[i])) a[k] = aux[j--];
      else                      a[k] = aux[i++];
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
    sort(a);
    show(a);

  }

}
