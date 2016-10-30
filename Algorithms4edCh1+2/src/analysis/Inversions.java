package analysis;

import static v.ArrayUtils.*;
import static java.lang.reflect.Array.newInstance;

import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import ds.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/******************************************************************************
 * http://algs4.cs.princeton.edu/22mergesort/Inversions.java
 * http://algs4.cs.princeton.edu/22mergesort/Inversions.java.html
 *  Compilation:  javac Inversions.java
 *  Execution:    java Inversions < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  
 *  Read in N integers and count number of inversions in N log N time.
 *
 ******************************************************************************/

/**
 *  The {@code Inversions} class provides static methods to count the 
 *  number of <em>inversions</em> in either an array of integers or comparables.
 *  An inversion in an array {@code a[]} is a pair of indicies {@code i} and
 *  {@code j} such that {@code i} &lt; {@code j} and {@code a[i] &gt; a[j]}.
 *  <p>
 *  This implementation uses a generalization of mergesort. The <em>count</em>
 *  operation takes time proportional to <em>n</em> log <em>n</em>,
 *  where <em>n</em> is the number of keys in the array.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/22mergesort">Section 2.2</a>
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

@SuppressWarnings({"unused","rawtypes"})
public class Inversions {
  
  @FunctionalInterface
  public static interface Consumer3<A,B,C> {
    void accept(A a, B b, C c);
  }
  
  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }

    // do not instantiate
    private Inversions() { }

    // merge and count
    private static long merge(int[] a, int[] aux, int lo, int mid, int hi) {
        long inversions = 0;

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k]; 
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)           a[k] = aux[j++];
            else if (j > hi)            a[k] = aux[i++];
            else if (aux[j] < aux[i]) { a[k] = aux[j++]; inversions += (mid - i + 1); }
            else                        a[k] = aux[i++];
        }
        return inversions;
    }

    // return the number of inversions in the subarray b[lo..hi]
    // side effect b[lo..hi] is rearranged in ascending order
    private static long count(int[] a, int[] b, int[] aux, int lo, int hi) {
        long inversions = 0;
        if (hi <= lo) return 0;
        int mid = lo + (hi - lo) / 2;
        inversions += count(a, b, aux, lo, mid);  
        inversions += count(a, b, aux, mid+1, hi);
        inversions += merge(b, aux, lo, mid, hi);
        assert inversions == brute(a, lo, hi);
        return inversions;
    }

    /**
     * Returns the number of inversions in the integer array.
     * The argument array is not modified.
     * @param  a the array
     * @return the number of inversions in the array. An inversion is a pair of 
     *         indicies {@code i} and {@code j} such that {@code i &lt; j}
     *         and {@code a[i]} &gt; {@code a[j]}.
     */
    public static long count(int[] a) {
        int[] b   = new int[a.length];
        int[] aux = new int[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = a[i];
        long inversions = count(a, b, aux, 0, a.length - 1);
        return inversions;
    }

    // merge and count (Comparable version)
    private static long merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        long inversions = 0;

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k]; 
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)                a[k] = aux[j++];
            else if (j > hi)                 a[k] = aux[i++];
            else if (less(aux[j], aux[i])) { a[k] = aux[j++]; inversions += (mid - i + 1); }
            else                             a[k] = aux[i++];
        }
        return inversions;
    }

    // return the number of inversions in the subarray b[lo..hi]
    // side effect b[lo..hi] is rearranged in ascending order
    private static long count(Comparable[] a, Comparable[] b, Comparable[] aux, int lo, int hi) {
        long inversions = 0;
        if (hi <= lo) return 0;
        int mid = lo + (hi - lo) / 2;
        inversions += count(a, b, aux, lo, mid);  
        inversions += count(a, b, aux, mid+1, hi);
        inversions += merge(b, aux, lo, mid, hi);
        assert inversions == brute(a, lo, hi);
        return inversions;
    }

    /**
     * Returns the number of inversions in the comparable array.
     * The argument array is not modified.
     * @param  a the array
     * @return the number of inversions in the array. An inversion is a pair of 
     *         indicies {@code i} and {@code j} such that {@code i &lt; j}
     *         and {@code a[i].compareTo(a[j]) &gt; 0}.
     */
    public static long count(Comparable[] a) {
        Comparable[] b   = new Comparable[a.length];
        Comparable[] aux = new Comparable[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = a[i];
        long inversions = count(a, b, aux, 0, a.length - 1);
        return inversions;
    }

    // is v < w ?
    @SuppressWarnings("unchecked")
    private static boolean less(Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }

    // count number of inversions in a[lo..hi] via brute force (for debugging only)
    private static long brute(Comparable[] a, int lo, int hi) {
        long inversions = 0;
        for (int i = lo; i <= hi; i++)
            for (int j = i + 1; j <= hi; j++)
                if (less(a[j], a[i])) inversions++;
        return inversions;
    }
    
    // count number of inversions in a
    private static long brute(Comparable[] a) {
        long inversions = 0; int n = a.length;
        for (int i = 0; i <= n-1; i++)
            for (int j = i + 1; j <= n-1; j++)
                if (less(a[j], a[i])) inversions++;
        return inversions;
    }

    // count number of inversions in a[lo..hi] via brute force (for debugging only)
    private static long brute(int[] a, int lo, int hi) {
        long inversions = 0;
        for (int i = lo; i <= hi; i++)
            for (int j = i + 1; j <= hi; j++)
                if (a[j] < a[i]) inversions++;
        return inversions;
    }
    
    // count number of inversions in a
    private static long brute(int[] a) {
        long inversions = 0; int n = a.length;
        for (int i = 0; i <= n; i++)
            for (int j = i + 1; j <= n; j++)
                if (a[j] < a[i]) inversions++;
        return inversions;
    }
    
    public static <T extends Comparable<? super T>> long countInversions(T[] q) {
      // return the number of inversions in q without altering it.
      // based on natural in-place mergesort.
      T[] z = q;
      int n = z.length;
      @SuppressWarnings("unchecked")
      T[] aux = (T[]) newInstance(z.getClass().getComponentType(), z.length);
      long[] inversions  = new long[1];
      // lambda expressions
      BiPredicate<T,T> less = (u,v) -> {
        boolean b = u.compareTo(v) < 0 ? true : false;
        return b;
      };
      Function<T[],Queue<Integer>> getRuns = (x) -> {
        int m = x.length;
        Queue<Integer> runs = new Queue<Integer>();
        int i = 0, c= 0;
        while(i < m) {
          c = i;
          if (i < m -1) 
            while(i < m -1 && less.test(x[i], x[i+1])) i++;
          runs.enqueue(++i - c); 
        }
        return runs;
      };
      Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) 
          b[k] = a[k];
        for (int k = lo; k <= hi; k++) { 
          if (i > mid) a[k] = b[j++];
          else if (j > hi ) a[k] = b[i++];
          else if (less.test(b[j], b[i])) { a[k] = b[j++]; inversions[0] += (mid - i + 1); }
          else a[k] = b[i++];
        }       
      }; // end of lambda expressions      
      Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
      int rlen = 0; // offset in z
      while (runs.size() > 1) {
        run1 = runs.dequeue();
        // ensure one pass through z at a time with no overlap
        if (run1 + rlen == n) { runs.enqueue(run1); rlen = 0; continue; }
        run2 = runs.dequeue();
        merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
        runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % n;
      }   
      return inversions[0];
    }

    public static <T extends Comparable<? super T>> long countInversions2(T[] q) {
      // return the number of inversions in q without altering it.
      // based on bottom-up in-place mergesort.
      T[] z = q.clone();
      int N = z.length;
      @SuppressWarnings("unchecked")
      T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N); 
      long[] inversions  = new long[1];
      // lambda expressions
      BiPredicate<T,T> less = (u,v) -> {
        boolean b = u.compareTo(v) < 0 ? true : false;
//        System.out.println(b);
//        if (b == true) inversions[0]++;
        return b;
      };
      Consumer<T[]> show = (a) -> {
        for (int i = 0; i < a.length; i++)
          System.out.print(a[i] + " ");
      };
      Consumer3<T[],Integer,Integer> showRange = (a,lo,hi) -> {
        for (int i = 0; i < a.length; i++) {
          if (i == lo) {
            System.out.print("{"+a[i]+"  "); 
          } else if (i == hi) {
            System.out.print(a[i] + "} ");
          } else System.out.print(a[i] + "  ");
        }     
      };
      Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
        int i = lo, j = mid+1; int f = 0;
//        System.out.println("lo="+lo+" mid="+mid+" hi="+hi);
//        System.out.print("a="); showRange.accept(a,lo,hi);
//        System.out.print("aux="); showRange.accept(aux,lo,hi); System.out.println();
        for (int k = lo; k <= hi; k++) 
          b[k] = a[k];
        for (int k = lo; k <= hi; k++) { 
          if (i > mid) a[k] = b[j++];
          else if (j > hi ) a[k] = b[i++];
          else if (less.test(b[j], b[i])) {
            System.out.println("lo="+lo+" mid="+mid+" hi="+hi+" i="+i+" j="+j+" k="+k);
            System.out.print("a="); showRange.accept(a,lo,hi);
            System.out.print("aux="); showRange.accept(aux,lo,hi); System.out.println();
//            f = j - i; 
            a[k] = b[j++]; 
            System.out.println("after swap");
            System.out.print("a="); showRange.accept(a,lo,hi);
            System.out.print("aux="); showRange.accept(aux,lo,hi); System.out.println();
            f = mid - i + 1;  //
            System.out.println(f);
            inversions[0] += f;
            }
          else a[k] = b[i++];
        }       
      }; // end of lambda expressions      
      for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
        for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
          merge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
      return inversions[0];
    }
    
    public static  <T extends Comparable<? super T>> long countInversionsO2(T[] a) {
      int n = a.length; long inversions = 0; 
      for (int i = 0; i < n; i++)
          for (int j = i + 1; j < n; j++)
              if (a[j].compareTo(a[i]) < 0) inversions++;
      return inversions;
    }
 
    public static void main(String[] args) {
      
      Random r = new Random(System.currentTimeMillis()); Integer[] a;
      for (int i = 1; i < 10001; i++) {
        a = rangeInteger(0,i);
        shuffle(a,r);
        long brute = brute(a);
        assert countInversions(a) == brute;
//        assert countInversionsO2(a) == brute;
      }
 

      
      
      
      
      
      
      /**
       * Reads in a sequence of integers from standard input and prints
       * the number of inversions.
       */      
//        int[] a = StdIn.readAllInts();
//        int n = a.length;
//        Integer[] b = new Integer[n];
//        for (int i = 0; i < n; i++)
//            b[i] = a[i];
//        StdOut.println(Inversions.count(a));
//        StdOut.println(Inversions.count(b));
    }
}
