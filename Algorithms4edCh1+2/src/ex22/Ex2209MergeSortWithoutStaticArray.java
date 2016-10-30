package ex22;

import static java.lang.reflect.Array.newInstance;

public class Ex2209MergeSortWithoutStaticArray {

/* p284
  2.2.9  Use of a static array like  aux[] is inadvisable in library software because 
  multiple clients might use the class concurrently. Give an implementation of  Merge 
  that does not use a static array. Do not make  aux[] local to  merge() (see the Q&A 
  for this section).
  Hint : Pass the auxiliary array as an argument to the recursive  sort()
  
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
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
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
    sort(a);
    show(a);
    
  }
    
}
