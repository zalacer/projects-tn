package sort;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/******************************************************************************
 * http://algs4.cs.princeton.edu/23quicksort/Quick.java
 * http://algs4.cs.princeton.edu/23quicksort/Quick.java.html 
 *  Compilation:  javac Quick.java
 *  Execution:    java Quick < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/23quicksort/tiny.txt
 *                http://algs4.cs.princeton.edu/23quicksort/words3.txt
 *
 *  Sorts a sequence of strings from standard input using quicksort.
 *   
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Quick < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *       
 *  % java Quick < words3.txt
 *  all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 *
 *  Remark: For a type-safe version that uses static generics, see
 *
 *    http://algs4.cs.princeton.edu/23quicksort/QuickPedantic.java
 *
 ******************************************************************************/

/**
 *  The {@code Quick} class provides static methods for sorting an
 *  array and selecting the ith smallest element in an array using quicksort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Quick {
    private static long compares = 0L;

    // This class should not be instantiated.
    private Quick() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static <T extends Comparable<? super T>> void sort(T[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length - 1);
        assert isSorted(a);
    }

    // quicksort the subarray from a[lo] to a[hi]
    private static <T extends Comparable<? super T>> void sort(T[] a, int lo, int hi) { 
        if (hi <= lo) return;
        int j = partition(a, lo, hi);
        sort(a, lo, j-1);
        sort(a, j+1, hi);
        assert isSorted(a, lo, hi);
    }

    // partition the subarray a[lo..hi] so that a[lo..j-1] <= a[j] <= a[j+1..hi]
    // and return the index j.
    public static <T extends Comparable<? super T>> int partition(T[] a, int lo, int hi) {
        int i = lo;
        int j = hi + 1;
        T v = a[lo];
        while (true) { 

            // find item on lo to swap
            while (less(a[++i], v))
                if (i == hi) break;

            // find item on hi to swap
            while (less(v, a[--j]))
                if (j == lo) break;      // redundant since a[lo] acts as sentinel

            // check if pointers cross
            if (i >= j) break;

            exch(a, i, j);
        }

        // put partitioning item v at a[j]
        exch(a, lo, j);

        // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
        return j;
    }
    
    public static <T extends Comparable<? super T>> int partitionM3(T[] a, int lo, int hi) {
      // using median-of-3 partitioning
      int i = lo;
      int j = hi + 1;
      int N = hi - lo + 1;
      int m = median3(a, lo, lo + N/2, hi);
      exch(a, m, lo);
      T v = a[lo];
      while (true) { 

          // find item on lo to swap
          while (less(a[++i], v))
              if (i == hi) break;

          // find item on hi to swap
          while (less(v, a[--j]))
              if (j == lo) break;      // redundant since a[lo] acts as sentinel

          // check if pointers cross
          if (i >= j) break;

          exch(a, i, j);
      }

      // put partitioning item v at a[j]
      exch(a, lo, j);

      // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
      return j;
   }
    
    public static <T extends Comparable<? super T>> int partitionT9(T[] a, int lo, int hi) {
      // using Tukey ninther partitioning
      int i = lo;
      int j = hi + 1;
      int N = hi - lo + 1;
      // Tukey ninther calculation
      int eps = N/8;
      int mid = lo + N/2;
      int m1 = median3(a, lo, lo + eps, lo + eps + eps);
      int m2 = median3(a, mid - eps, mid, mid + eps);
      int m3 = median3(a, hi - eps - eps, hi - eps, hi); 
      int ninther = median3(a, m1, m2, m3);
      exch(a, ninther, lo);
      // end Tukey ninther
      T v = a[lo];
      while (true) { 

          // find item on lo to swap
          while (less(a[++i], v))
              if (i == hi) break;

          // find item on hi to swap
          while (less(v, a[--j]))
              if (j == lo) break;      // redundant since a[lo] acts as sentinel

          // check if pointers cross
          if (i >= j) break;

          exch(a, i, j);
      }

      // put partitioning item v at a[j]
      exch(a, lo, j);

      // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
      return j;
    } 
    
    
    // return the index of the median element among a[i], a[j], and a[k]
    private static <T extends Comparable<? super T>> int median3(T[] a, int i, int j, int k) {
        return (less(a[i], a[j]) ?
               (less(a[j], a[k]) ? j : less(a[i], a[k]) ? k : i) :
               (less(a[k], a[j]) ? j : less(a[k], a[i]) ? k : i));
    }
    
    /**
     * Rearranges the array so that {@code a[k]} contains the kth smallest key;
     * {@code a[0]} through {@code a[k-1]} are less than (or equal to) {@code a[k]}; and
     * {@code a[k+1]} through {@code a[n-1]} are greater than (or equal to) {@code a[k]}.
     *
     * @param  a the array
     * @param  k the rank of the key
     * @return the key of rank {@code k}
     */
    public static <T extends Comparable<? super T>> T select(T[] a, int k) {
        if (k < 0 || k >= a.length) {
            throw new IndexOutOfBoundsException("Selected element out of bounds");
        }
        StdRandom.shuffle(a);
        int lo = 0, hi = a.length - 1;
        while (hi > lo) {
            int i = partition(a, lo, hi);
            if      (i > k) hi = i - 1;
            else if (i < k) lo = i + 1;
            else return a[i];
        }
        return a[lo];
    }
    
    public static <T extends Comparable<? super T>> T select2(T[] a, int k) {
      // from text p346
      StdRandom.shuffle(a);
      int lo = 0, hi = a.length - 1;
      while (hi > lo) {
        int j = partition(a, lo, hi);
        if (j == k) return a[k];
        else if (j > k) hi = j - 1;
        else if (j < k) lo = j + 1;
      }
      return a[k];
    }
    
    public static <T extends Comparable<? super T>> Object[] selectCompares(T[] a, int k) {
      // from text p346
      compares = 0;
//      StdRandom.shuffle(a);
      int lo = 0, hi = a.length - 1;
      while (hi > lo) {
        int j = partition(a, lo, hi);
        if (j == k) return new Object[]{compares,a[k]};
        else if (j > k) hi = j - 1;
        else if (j < k) lo = j + 1;
      }
      return new Object[]{compares,a[k]};
    }
    
    public static <T extends Comparable<? super T>> Object[] selectComparesM3(T[] a, int k) {
      // using median-of-3 partitioning
      compares = 0;
//      StdRandom.shuffle(a);
      int lo = 0, hi = a.length - 1;
      while (hi > lo) {
        int j = partitionM3(a, lo, hi);
        if (j == k) return new Object[]{compares,a[k]};
        else if (j > k) hi = j - 1;
        else if (j < k) lo = j + 1;
      }
      return new Object[]{compares,a[k]};
    }
    
    public static <T extends Comparable<? super T>> Object[] selectComparesT9(T[] a, int k) {
      // using median-of-3 partitioning
      compares = 0;
//      StdRandom.shuffle(a);
      int lo = 0, hi = a.length - 1;
      while (hi > lo) {
        int j = partitionT9(a, lo, hi);
        if (j == k) return new Object[]{compares,a[k]};
        else if (j > k) hi = j - 1;
        else if (j < k) lo = j + 1;
      }
      return new Object[]{compares,a[k]};
    }
    
    public static <T extends Comparable<? super T>> T selectRecursive(T[] a, int k, 
        boolean first, int...hl) {
      // based on text p346
      int lo, hi;
      if (first) { StdRandom.shuffle(a); lo = 0; hi = a.length - 1; }
      else { lo = hl[0]; hi = hl[1]; }
      if (hi > lo)  {
        int j = partition(a, lo, hi);
        if (j == k) return a[k];
        else if (j > k) { hi = j - 1; return selectRecursive(a, k, false, new int[]{lo,hi}); }
        else if (j < k) { lo = j + 1; return selectRecursive(a, k, false, new int[]{lo,hi}); }
      }
      return a[k];
    }



   /***************************************************************************
    *  Helper sorting functions.
    ***************************************************************************/
    
    // is v < w ?
    private static <T extends Comparable<? super T>> boolean less(T v, T w) {
        compares++;
        return v.compareTo(w) < 0;
    }
        
    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


   /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/
    private static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }


    // print array to standard output
    private static <T extends Comparable<? super T>> void show(T[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }

    /**
     * Reads in a sequence of strings from standard input; quicksorts them; 
     * and prints them to standard output in ascending order. 
     * Shuffles the array and then prints the strings again to
     * standard output, but this time, using the select method.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        String[] a = StdIn.readAllStrings();
        Quick.sort(a);
        show(a);
        assert isSorted(a);

        // shuffle
        StdRandom.shuffle(a);

        // display results again using select
        StdOut.println();
        for (int i = 0; i < a.length; i++) {
            String ith = (String) Quick.select(a, i);
            StdOut.println(ith);
        }
    }

}

