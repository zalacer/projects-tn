package sort;

import static v.ArrayUtils.rangeDouble;
import static v.ArrayUtils.shuffle;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

// for ex2440
public class HeapFloyd {
  public static double compares = 0;

  // This class should not be instantiated.
  private HeapFloyd(){}
  
  public static <T extends Comparable<? super T>> void heapsort(T[] z) {
    // all in one lambda version
    if (z == null || z.length < 2) return;
    int[] n = {z.length};
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a-1]; z[a-1] = z[b-1]; z[b-1] = t; };
    BiPredicate<Integer,Integer> less = (a,b) -> { return z[a-1].compareTo(z[b-1]) < 0; };
    Consumer<Integer> sink = (k) -> {
      while (2*k <= n[0]) {
        int j = 2*k;
        if (j < n[0] && less.test(j, j+1)) j++;
        if (!less.test(k, j)) break;
        exch.accept(k, j);
        k = j;
      }
    };
    Consumer<Integer> floyd = (k) -> {
      // sink to the bottom then swim
      while (2*k <= n[0]) { // sink larger child down
        int j = 2*k;
        if (j < n[0] && less.test(j, j+1)) j++;
        // if (!less(pq, k, j)) break; // this is what is omitted from sink()
        exch.accept(k, j);
        k = j;
      }
      // now do a swim(k) as for a MaxPQ
      while (k > 1 && less.test(k/2, k)) {
        exch.accept(k, k/2);
        k = k/2;
      }
    };
    for (int k = n[0]/2; k >= 1; k--)
      sink.accept(k);
    while (n[0] > 1) {
      exch.accept(1, n[0]--); 
      floyd.accept(1);
    }      
  }
  
  public static <V> void heapsort(V[] z, Comparator<V> c) {
    // all in one lambda version with Comparator
    if (z == null || z.length < 2) return;
    int[] n = {z.length};
    BiConsumer<Integer,Integer> exch = (a,b) -> { V t = z[a-1]; z[a-1] = z[b-1]; z[b-1] = t; };
    BiPredicate<Integer,Integer> less = (a,b) -> { return c.compare(z[a-1],z[b-1]) < 0; };
    Consumer<Integer> sink = (k) -> {
      while (2*k <= n[0]) {
        int j = 2*k;
        if (j < n[0] && less.test(j, j+1)) j++;
        if (!less.test(k, j)) break;
        exch.accept(k, j);
        k = j;
      }
    };
    Consumer<Integer> floyd = (k) -> {
      // sink to the bottom then swim
      while (2*k <= n[0]) { // sink larger child down
        int j = 2*k;
        if (j < n[0] && less.test(j, j+1)) j++;
        // if (!less(pq, k, j)) break; // this is what is omitted from sink()
        exch.accept(k, j);
        k = j;
      }
      // now do a swim(k) as for a MaxPQ
      while (k > 1 && less.test(k/2, k)) {
        exch.accept(k, k/2);
        k = k/2;
      }
    };
    for (int k = n[0]/2; k >= 1; k--)
      sink.accept(k);
    while (n[0] > 1) {
      exch.accept(1, n[0]--); 
      floyd.accept(1);
    }      
  }

  public static <T extends Comparable<? super T>> double sort(T[] z) {
    compares = 0;
    int n = z.length;
    for (int k = n/2; k >= 1; k--)
      sink(z, k, n);
    while (n > 1) {
      exch(z, 1, n--);
      floyd(z, 1, n);
    }
    return compares;
  }

  private static <T extends Comparable<? super T>> void sink(T[] z, int k, int n) {
    while (2*k <= n) {
      int j = 2*k;
      if (j < n && less(z, j, j+1)) j++;
      if (!less(z, k, j)) break;
      exch(z, k, j);
      k = j;
    }
  }

  private static <T extends Comparable<? super T>> void floyd(T[] z, int k, int n) {
    // sink to the bottom then swim
    while (2*k <= n) { // sink larger child down
      int j = 2*k;
      if (j < n && less(z, j, j+1)) j++;
      // if (!less(pq, k, j)) break; // this is what is omitted from sink()
      exch(z, k, j);
      k = j;
    }
    // now do a swim(k) as for a MaxPQ
    while (k > 1 && less(z, k/2, k)) {
      exch(z, k, k/2);
      k = k/2;
    }
  }
  
  // Comparator version
  
  public static <V> void sort(V[] z, Comparator<V> c) {
    int n = z.length;
    for (int k = n/2; k >= 1; k--)
      sink(z, k, n, c);
    while (n > 1) {
      exch(z, 1, n--);
      floyd(z, 1, n, c);
    }
  }

  private static <V> void sink(V[] z, int k, int n, Comparator<V> c) {
    while (2*k <= n) {
      int j = 2*k;
      if (j < n && less(z, j, j+1, c)) j++;
      if (!less(z, k, j, c)) break;
      exch(z, k, j);
      k = j;
    }
  }

  private static <V> void floyd(V[] z, int k, int n, Comparator<V> c) {
    // sink to the bottom then swim
    while (2*k <= n) { // sink larger child down
      int j = 2*k;
      if (j < n && less(z, j, j+1, c)) j++;
      // if (!less(pq, k, j)) break; // this is what is omitted from sink()
      exch(z, k, j);
      k = j;
    }
    // now do a swim(k) as for a MaxPQ
    while (k > 1 && less(z, k/2, k, c)) {
      exch(z, k, k/2);
      k = k/2;
    }
  }

  private static <T extends Comparable<? super T>> boolean less(T[] z, int i, int j) {
    compares++;
    return z[i-1].compareTo(z[j-1]) < 0;
  }
  
  private static <V> boolean less(V[] z, int i, int j, Comparator<V> c) {
    compares++;
    return c.compare(z[i-1], z[j-1]) < 0;
  }

  private static void exch(Object[] z, int i, int j) {
    Object swap = z[i-1];
    z[i-1] = z[j-1];
    z[j-1] = swap;
  }

  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    for (int i = 1; i < a.length; i++)
      if (a[i-1].compareTo(a[i]) > 0) return false;
    return true;
  }
  
  public static <V> boolean isSorted(V[] a, Comparator<V> c) {
    for (int i = 1; i < a.length; i++)
      if (c.compare(a[i-1], a[i]) > 0) return false;
    return true;
  }

  public static <T> void show(T[] z) {
    for (int i = 0; i < z.length; i++) System.out.print(z[i]+" ");
    System.out.println();
  }

  public static void main(String[] args) {

    SecureRandom sr = new SecureRandom();
    Double[] a = {0.,12.,3.,1.,5.,4.,6.,2.,7.,11.,10.,9.};
    a = rangeDouble(1.,100000.);
    shuffle(a,sr);
    // these are cases where omitting the swim part of floyd() results in isSorted false
    // a = new Double[]{9.0,7.0,4.0,8.0,2.0,1.0,3.0,6.0,5.0};
    // a = new Double[]{5.0,8.0,4.0,18.0,15.0,3.0,12.0,2.0,13.0,1.0,16.0,11.0,20.0,17.0,19.0,6.0,10.0,7.0,14.0,9.0};
    // a = new Double[]{5.0,8.0,4.0,18.0,15.0,3.0,12.0,2.0,13.0,1.0,16.0,11.0,20.0,17.0,19.0,6.0,10.0,7.0,14.0,9.0};
    // par(a);
    // a = new Double[]{2.0,3.0,4.0,5.0,1.0};
    Comparator<Double> c = (d,e) -> { return d.compareTo(e); };
    heapsort(a,c);
    System.out.println(isSorted(a));
    // show(a);
  }
}
