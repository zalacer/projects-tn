package ex22;

import static java.lang.reflect.Array.newInstance;

public class Ex2210FasterMerge {

/* p285
  2.2.10 Faster merge. Implement a version of  merge() that copies the second half of
  a[] to  aux[] in decreasing order and then does the merge back to a[]. This change al-
  lows you to remove the code to test that each of the halves has been exhausted from the
  inner loop. Note: The resulting sort is not stable (see page 341).

  See merge() below.

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
    // copy 1st half of a to 1st half of aux in order
    for (int i = lo; i <= mid; i++)
      aux[i] = a[i]; 

    // copy 2nd half of a to 2nd half of aux in reverse order
    for (int j = mid+1; j <= hi; j++)
      aux[j] = a[hi-j+mid+1];

    // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
    int i = lo, j = hi; // j starts at hi and is decremented
    for (int k = lo; k <= hi; k++) 
      if (less(aux[j], aux[i])) 
           a[k] = aux[j--];
      else a[k] = aux[i++];
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
