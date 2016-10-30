package sort;

import static v.ArrayUtils.*;
import analysis.Timer;
import java.util.Iterator;
import java.util.Random;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/24pq/Heap.java
 *  http://algs4.cs.princeton.edu/24pq/Heap.java.html
 *  Compilation:  javac Heap.java
 *  Execution:    java Heap < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tiny.txt
 *                http://algs4.cs.princeton.edu/24pq/words3.txt
 *  
 *  Sorts a sequence of strings from standard input using heapsort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Heap < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java Heap < words3.txt
 *  all bad bed bug dad ... yes yet zoo   [ one string per line ]
 *
 ******************************************************************************/

/**
 *  The {@code Heap} class provides a static methods for heapsorting
 *  an array.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

@SuppressWarnings("unused")
public class HeapIntEx2439 {
  public static int compares = 0;

    // This class should not be instantiated.
    private HeapIntEx2439() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param pq the array to be sorted
     */
    public static long sort(int[] pq) {
        Timer t = new Timer(); long l = 0;
        int n = pq.length;
        t.reset(); // the for loop is the construction phase, p324
        for (int k = n/2; k >= 1; k--) {
            sink(pq, k, n);
        }
        l = t.num();
        while (n > 1) {
            exch(pq, 1, n--);
            t.reset();
            sink(pq, 1, n);
            l += t.num();
        }
        return l;
    }

   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

    private static void sink(int[] pq, int k, int n) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(pq, j, j+1)) j++;
            if (!less(pq, k, j)) break;
            exch(pq, k, j);
            k = j;
        }
    }

   /***************************************************************************
    * Helper functions for comparisons and swaps.
    * Indices are "off-by-one" to support 1-based indexing.
    ***************************************************************************/
    private static boolean less(int[] pq, int i, int j) {
        compares++;
        return pq[i-1] - pq[j-1] < 0;
    }

    private static void exch(int[] pq, int i, int j) {
        int swap = pq[i-1];
        pq[i-1] = pq[j-1];
        pq[j-1] = swap;
    }

    // is v < w ?
    private static <T extends Comparable<? super T>> boolean less(int v, int w) {
      compares++;
      return v - w < 0;
    }
        
   /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/
    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] - a[i-1] < 0) return false;
        return true;
    }
    
    // print array to standard output
    private static <T> void show(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

    public static int[] heapSortWorstCase(int N) {
      // return an int[] of length N that's a worst case for heapsort 
      int[] heap = range(0,N+1);
      int len = 0, n = 0, i, j;
      while(n < heap.length) {
        n++; heap[1]=1; len=1;
        for(i=2; i<=N; i++) {
          j=len;
          while(j>1) { heap[j] = heap[j/2]; j/=2;}
          heap[1]=i; heap[++len]=1;
        }
      }
      return drop(heap,1);
    }
    
    public static int[] heapSortWorstCase0BasedLevelOrder(int N) {
      // return a 0-based level order int[] of length N that's a 
      // worst case for heapsort 
      int[] heap = range(0,N+1);
      int len = 0, n = 0, i, j;
      while(n < heap.length) {
        n++; heap[1]=1; len=1;
        for(i=2; i<=N; i++) {
          j=len;
          while(j>1) { heap[j] = heap[j/2]; j/=2;}
          heap[1]=i; heap[++len]=1;
        }
      }
      return heap;
    }
    
    public static boolean is1BasedLevelOrdered(int[] z) {
      // return true if z is in level order base-1
      // else return false 
      int n = z.length;
      for (int i = 1; i < (n-1)/2; i++) {
        if (i*2 < n-1 && z[i] > z[i*2]) {
          System.out.println("testing "+i+", "+i*2);
          return false;
        }
        if (i*2+1 < n-1 && z[i] > z[i*2+1]) {
          System.out.println("testing "+i+", "+i*2+1);
          return false; 
        }
      }
      return true; 
    }
    
    public static boolean is0BasedLevelOrdered(int[] z) {
      // return true if z is in level order base-0
      // else return false 
      int n = z.length;
      for (int i = 0; i < n/2; i++) {
        if (i*2+1 < n-1 && z[i] > z[i*2+1]) {
          System.out.println("testing "+i+", "+i*2+1);
          return false;
        }
        if (i*2+2 < n-1 && z[i] > z[i*2+2]) {
          System.out.println("testing "+i+", "+i*2+2);
          return false; 
        }
      }
      return true; 
    }

    /**
     * Reads in a sequence of strings from standard input; heapsorts them; 
     * and prints them to standard output in ascending order. 
     *
     * @param args the command-line arguments
     */
    
    public static void findMinCompares() {
      // ex2416
      int[] z = range(1,33), w=null, x, y; int min=Integer.MAX_VALUE; double c = 0;
      Iterator<int[]> it = permutations(z);
      while (it.hasNext()) {
        c++;
        x = it.next(); y = x.clone();
        compares = 0;
        sort(x);
        if (compares < min) {
          min = compares; w = y;
        }
        if (c%30000000 == 0) {
          System.out.printf("c=%5.0f\n",c);
          System.out.println("min="+min);
          par(w);
          System.out.println();
        }       
      }
    }
    
    public static void findRandomMinCompares() {
      // this appears to be innefective vs permutations
      // ex2416
      int[] z = range(1,33), w=null, x; int min=Integer.MAX_VALUE; double c = 0;
      Random r;
      while (true) {
        r = new Random(System.currentTimeMillis());
        shuffle(z,r); x = z.clone();
        compares = 0; c++;
        sort(z);
        if (compares < min) {
          min = compares; w = x;
        }
        if (c%10000000 == 0) {
          System.out.printf("c=%5.0f\n",c);
          System.out.println("min="+min);
          par(w);
          System.out.println();
        }       
      }
    }
    
    public static void findMaxCompares() {
      // ex2416
      int[] z = range(1,33), w=null, x, y; int max=Integer.MIN_VALUE; double c = 0;
      Iterator<int[]> it = permutations(z);
      while (it.hasNext()) {
        c++;
        x = it.next(); y = x.clone();
        compares = 0;
        sort(x);
        if (compares > max) {
          max = compares; w = y;
        }
        if (c%30000000 == 0) {
          System.out.printf("c=%5.0f\n",c);
          System.out.println("max="+max);
          par(w);
          System.out.println();
        }       
      }
    }
    
    public static void main(String[] args) {
     
      findMaxCompares();  
      findMinCompares();
      findRandomMinCompares();


      
//      int[] q = {17,7,2,9,81,9,1,13,15,3,27,15};
//      sort(q);
//      par(q);
//      System.out.println(compares);
            
//        String[] a = StdIn.readAllStrings();
//        HeapEx2416.sort(a);
//        show(a);
//        assert isSorted(a);
      
//      int[] a = range(1,33);
//      Iterator<int[]> it = permutations(a);
//      while (it.hasNext()) {
//        par(it.next());
//      }
      
      
      
    }
}
