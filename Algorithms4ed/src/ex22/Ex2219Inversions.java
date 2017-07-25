package ex22;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.*;

import java.util.Random;
import java.util.function.Function;

import ds.Queue;

public class Ex2219Inversions {

  /* p286
  2.2.19 Inversions. Develop and implement a linearithmic algorithm for computing
  the number of inversions in a given array (the number of exchanges that would be
  performed by insertion sort for that array—see Section 2.1). This quantity is related
  to the Kendall tau distance; see Section 2.5.
  
   */ 
  
  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }
  
  public static  <T extends Comparable<? super T>> long countInversionsNe2(T[] q) {
    // returns number of inversions in q using standard ~N^2 algo
    int n = q.length; long inversions = 0; 
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++)
            if (q[j].compareTo(q[i]) < 0) inversions++;
    return inversions;
  }
    
  // using lambda expressions to pack it all in one method
  public static <T extends Comparable<? super T>> long countInversions(T[] z) {
    // return the number of inversions in q.
    // based on natural in-place mergesort hence runs in ~NlogN time.
    // has side-effect of sorting q in-place, if it's not null and not already sorted.
    if (z == null) return 0;
    int N = z.length; if (N < 2) return 0;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
    long[] inversions = new long[1];
    // lambda expressions
    Function<T[],Queue<Integer>> getRuns = (x) -> {
      int m = x.length;
      Queue<Integer> runs = new Queue<Integer>();
      int i = 0, c= 0;
      while(i < m) {
        c = i;
        if (i < m -1) 
          while(i < m -1 && x[i].compareTo(x[i+1]) < 0) i++;
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
        else if (b[j].compareTo(b[i]) < 0) {
          a[k] = b[j++]; inversions[0] += (mid - i + 1); }
        else a[k] = b[i++];
      }       
    }; // end lambda expressions      
    Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == N) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    }   
    return inversions[0];
  }

  public static void main(String[] args) {
    
    Random r = new Random(System.currentTimeMillis()); Integer[] a;
    for (int i = 1; i < 10001; i++) {
      a = rangeInteger(0,i);
      shuffle(a,r);
      assert countInversionsNe2(a) == countInversions(a);
    }

  }
  
}

