package ex22;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.*;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import ds.Queue;

@SuppressWarnings("unused")
public class Ex2221Triplicates {

  /* p286
  2.2.21 Triplicates. Given three lists of N names each, devise a linearithmic 
  algorithm to determine if there is any name common to all three lists, and if 
  so, return the first such name.
  
   1. sort two of the lists with linearithmic sort.
   2. iterate over the unsorted list and use binary search to determine if the
      current name is in both of the sorted lists.
      
   Implementation below named firstInAll() assumes the lists are arrays since
   using linked lists wasn't specified, and works for any number of lists.
   
   */ 
  
  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> T firstInAll(T[]...t) {
    // return the first T in all lists else returns null.
    // taking first to mean first in the only or shortest list.
    if (t == null || t.length == 0) return null;
    if (t.length == 1 && t[0].length > 0) return t[0][0];
    int len = Integer.MAX_VALUE; // length of smallest array in t
    int l = 0; // index of smallest array in t
    for (int i = 0; i < t.length; i++)
      if (t[i].length == 0) return null;
      else if (t[i].length < len) { len = t[i].length; l = i; }
    BiFunction<T[],T,Integer> search = (a, key)-> {
      int lo = 0;
      int hi = a.length - 1;
      while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if      (key.compareTo(a[mid])<0) hi = mid - 1;
        else if (key.compareTo(a[mid])>0) lo = mid + 1;
        else return mid;
      }
      return -1;
    };
    Consumer<T[]> mergesort = (z) -> {
      // natural in-place mergesort 
      if (z == null) return;
      int N = z.length; if (N < 2) return;
      @SuppressWarnings("unchecked")
      T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
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
        for (int k = lo; k <= hi; k++)
          if (i > mid) a[k] = b[j++];
          else if (j > hi ) a[k] = b[i++];
          else if (b[j].compareTo(b[i]) < 0) a[k] = b[j++];
          else a[k] = b[i++];
      }; 
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
    };
    for (int i = 0; i < t.length; i++)
      if (i == l) continue;
      else mergesort.accept(t[i]);
    LOOP:
    for (int i = 0; i < len; i++) {
      for (int k = 0; k < t.length; k++)
        if (k == l) continue;
        else if (search.apply(t[k], t[l][i]) == -1) continue LOOP;
      return t[l][i];
    }
    return null;
  }
    
  public static void main(String[] args) {
    
    String[] u = {"Rob","Jen","Mike","Liz","Bill","Linda","Dave","Barb"};
    String[] v = {"Mary","Jim","Dave","Sue","Joe","Jess","Tom","Liz"};
    String[] w = {"Matt","Liz","Don","Lisa","Dave","Kim","Ron","Deb"};
    System.out.println(firstInAll(u,v,w)); //Liz
    
    String[] x = {"Rob","Jen","Mike","Liz","Bill","Linda","Dave","Barb"};
    String[] y = {"Mary","Jim","Rick","Sue","Joe","Jess","Tom","Marge"};
    String[] z = {"Matt","Bet","Don","Lisa","Steve","Kim","Ron","Deb"};
    System.out.println(firstInAll(x,y,z)); //null
  }
  
}

