package sort;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import analysis.Timer;
import ds.LinkedList;
import ds.Queue;


@SuppressWarnings("unused")
public class NaturalMerge {

  private NaturalMerge(){}

  public static <T extends Comparable<? super T>> void sort(T[] z) {
    // natural mergesort with monotonically increasing runs
    int n = z.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), z.length); 
    Queue<Integer> runs = getRuns(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == n) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      merge(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % n;
    }
  }
  
  public static <T extends Comparable<? super T>> Queue<Integer> getRuns(T[] a) {
    // search for increasing runs and return their lengths in a queue. 
    // this works since they are contiguous enabling calculation of 
    // their indices for merge input. run only once and after that new   
    // run lengths are calculated and enqueued in sort().
    Queue<Integer> runs = new Queue<Integer>();
    int i = 0, c= 0;
    while(i < a.length) {
      c = i;
      if (i < a.length -1) 
        while(i < a.length -1 && less(a[i], a[i+1])) i++;
      runs.enqueue(++i - c); 
    }
//    int[] r = (int[])unbox(runs.toArray(new Integer[2]));
//    System.out.println("avgRunLength="+mean(r));
    return runs;
  }
   
  public static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // the standard merge for this course
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) 
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { 
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
  }
  
  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }
  
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }

  private static <T extends Comparable<? super T>> void show(T[] a) { 
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  private static <T extends Comparable<? super T>> void show(T[] a, int lo, int hi) { 
    for (int i = 0; i < a.length; i++) {
      if (i == lo) {
        System.out.print("{"+a[i]+"  "); 
      } else if (i == hi) {
        System.out.print(a[i] + "} ");
      } else System.out.print(a[i] + "  ");
    }
    System.out.println();
  }
  
  public static void main(String[] args) {
    
    Random r; Integer[] w; Integer[] x;

//    for (int i = 2; i < 1002; i++) {
//      w = rangeInteger(1, i, 1);
//      x = w.clone();
//      r = new Random(System.currentTimeMillis());
//      shuffle(w, r);
//      sort(w);
//      assert isSorted(w);
//      assert w.length == x.length;
//    }
    
    w = rangeInteger(1,10000002);
    r = new Random(779537509);
    shuffle(w,r);
    Timer t = new Timer();
    sort(w);
    t.stop(); //45 sorted,  13600 shuffled
    assert isSorted(w); //45sorted, 
    
  }

}
