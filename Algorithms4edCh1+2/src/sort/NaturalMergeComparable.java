package sort;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Random;

import static java.lang.reflect.Array.newInstance;
 
// adapted from: 
// http://www.iti.fh-flensburg.de/lang/algorithmen/sortieren/merge/natmerge.htm

public class NaturalMergeComparable {
  
  public static <T extends Comparable<? super T>> void sort(T[] a) {
    @SuppressWarnings("unchecked")
    T[] aux = (T[])newInstance(a.getClass().getComponentType(),a.length);
    while (mergeruns(a, aux) & mergeruns(aux, a));
  }

  private static <T extends Comparable<? super T>> boolean mergeruns(T[] a, T[] aux) {
    int n = a.length;
    int i = 0, k = 0; T x;
    boolean up = true;

    // select and merge runs
    while (i < n) {
      k=i;
      do x = a[i++]; while (i < n && x.compareTo(a[i]) <= 0); // up
      while (i < n && x.compareTo(a[i]) >= 0) x = a[i++];     // down
      merge(a, aux, k, i-1, up);
      up = !up;
    }
    return k != 0;
  }

  private static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int hi, boolean up) {
    int k = up ? lo: hi;
    int d = up ? 1: -1;
    int i = lo, j = hi;

    while (i <= j) {
      if (a[i].compareTo(a[j]) <= 0) aux[k] = a[i++];
      else aux[k] = a[j--];
      k += d;
    }
  }
 
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }
  
  private static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }
  
  private static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public static void main(String[] args) {
    
    Integer[] a = {9, 1, 8, 2, 7, 3, 6, 4, 5};
    sort(a);
    assert isSorted(a);
    pa(a,-1);
    show(a);
    
    Random r = new Random(System.currentTimeMillis()); 
    Integer[] w, x; 
    w = rangeInteger(0,11); // [0,1,2,3,4,5,6,7,8,9,10]
    x = w.clone();
    
    shuffle(x,r);
    pa(x,-1);
    NaturalMergeComparable.sort(x);
    pa(x,-1);
    assert Arrays.equals(x,w);
    
  }

}
