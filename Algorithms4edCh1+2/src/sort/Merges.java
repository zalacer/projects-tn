package sort;

import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Function;

import analysis.Timer;
import ds.Queue;
import sort.Merges.Consumer5;

@SuppressWarnings("unused")
public class Merges {
  
  @FunctionalInterface
  public static interface Consumer2<A,B> {
    void accept(A a, B b);
  }
  
  @FunctionalInterface
  public static interface Consumer3<A,B,C> {
    void accept(A a, B b, C c);
  }
  
  @FunctionalInterface
  public static interface Consumer4<A,B,C,D> {
    void accept(A a, B b, C c, D d);
  }

  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }
  
  @FunctionalInterface
  public interface RecursiveConsumer3<A,B,C> {
    void accept(final A a,final B b,final C c,final Consumer3<A,B,C> self);
  }
  
  @FunctionalInterface
  public interface RecursiveConsumer4<A,B,C,D> {
    void accept(final A a,final B b,final C c,final D d,final Consumer4<A,B,C,D> self);
  }
  
  //based on: https://github.com/claudemartin/Recursive/blob/master/Recursive/src/ch/claude_martin/recursive/Recursive.java
  public static class Recursive<F> {
    private F f;
    public static <A,B,C> Consumer3<A,B,C> consumer3(RecursiveConsumer3<A,B,C> f) {
      final Recursive<Consumer3<A,B,C>> r = new Recursive<>();
      return r.f = (a,b,c) -> f.accept(a,b,c,r.f);
    }
    public static <A,B,C,D> Consumer4<A,B,C,D> consumer4(RecursiveConsumer4<A,B,C,D> f) {
      final Recursive<Consumer4<A,B,C,D>> r = new Recursive<>();
      return r.f = (a,b,c,d) -> f.accept(a,b,c,d,r.f);
    }
  }
  
  /* method suffix dictionary:
      Ac : avoids the copy in merge by switching a and aux in recursive code (topDown only)
      Am : create aux array in merge (for ex2226 p287)
      Co : cutoff to insertion sort
      Fm : faster merge as in ex2210 p285
      Np : returns number of passes (for ex2229m bottomUp and natural only)
      Sm : skip merge if if array already in order
  */
  
  public static <T extends Comparable<? super T>> void topDown(T[] z, int...xx) {
    // in-place topDown mergesort. 
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
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
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      merge.accept(a, b, lo, mid, hi); 
    });
    sort.accept(z, aux, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void topDownFm(T[] z, int...xx) {
    // in-place topDown mergesort with faster merge.
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };    
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      fasterMerge.accept(a, b, lo, mid, hi); 
    });
    sort.accept(z, aux, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void topDownCo(T[] z, int cutoff) {
    // in-place topDown mergesort with cutoff to insertion sort
    if (z == null) return; if (cutoff<0) cutoff = 0; int cut = cutoff;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
      for (int i = lo; i <= hi; i++)
        for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
          { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
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
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo + cut) { insertionSort.accept(a, lo, hi); return; }
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      merge.accept(a, b, lo, mid, hi); 
    });    
    sort.accept(z, aux, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void topDownSm(T[] z, int...xx) {
    // in-place topDown mergesort with skip merge when subarrays are in order
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
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
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      // skip merge when subarrays are in order
      if(a[mid].compareTo(a[mid+1])>0) merge.accept(a, b, lo, mid, hi); 
    });
    sort.accept(z, aux, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void topDownAc(T[] z, int...xx) {
    // in-place topDown mergesort with avoid copy from a to b in merge.
    // requires initially cloning z, inverting the order of z and aux or
    // a and b in sort.accept and interchanging a and b in all conditions 
    // in merge.
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    T[] aux = z.clone(); // aux is clone of z not any array of nulls of length N
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      // no copy from a to b
      for (int k = lo; k <= hi; k++)
        if (i > mid) b[k] = a[j++];     // interchange a and b in all conditions
        else if (j > hi ) b[k] = a[i++];
        else if (a[j].compareTo(a[i]) < 0) b[k] = a[j++];
        else b[k] = a[i++];
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(b, a, lo, mid);     // order of a and b inverted
      self.accept(b, a, mid+1, hi);   // order of a and b inverted
      merge.accept(a, b, lo, mid, hi); 
    });
    sort.accept(aux, z, 0, N - 1);   // order of z and aux inverted
  }
  
  public static <T extends Comparable<? super T>> void topDownAm(T[] z, int...xx) {
    // in-place topDown mergesort with aux array created in merge for ex2226 p287
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    Consumer4<T[],Integer,Integer,Integer> merge = (a,lo,mid,hi) -> {
      T[] aux = a.clone();
      int i = lo, j = mid+1;
      for (int k = lo; k <= hi; k++)
        if (i > mid) a[k] = aux[j++];
        else if (j > hi ) a[k] = aux[i++];
        else if (aux[j].compareTo(aux[i]) < 0) a[k] = aux[j++];
        else a[k] = aux[i++];
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, lo, mid);
      self.accept(a, mid+1, hi);
      merge.accept(a, lo, mid, hi); 
    });
    sort.accept(z, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void topDownAcCoSm(T[] z, int cutoff) {
    // in-place topDown mergesort with avoid copy, cutoff and skip merge.
    if (z == null) return; if (cutoff<0) cutoff = 0; int cut = cutoff;
    int N = z.length; if (N < 2) return;
    T[] aux = z.clone(); // aux is clone of a not any array of nulls of length N
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      // no copy from a to b
      for (int k = lo; k <= hi; k++)
        if (i > mid) b[k] = a[j++];     // interchange a and b in all conditions
        else if (j > hi ) b[k] = a[i++];
        else if (a[j].compareTo(a[i]) < 0) b[k] = a[j++];
        else b[k] = a[i++];
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo + cut) { insertionSort.accept(b, lo, hi); return; }
      int mid = lo + (hi - lo)/2;
      self.accept(b, a, lo, mid);     // order of a and b inverted
      self.accept(b, a, mid+1, hi);   // order of a and b inverted
      if (a[mid].compareTo(a[mid+1])>0) merge.accept(a, b, lo, mid, hi); 
      else System.arraycopy(a, lo, b, lo, hi - lo + 1);
    });
    sort.accept(aux, z, 0, N - 1);   // order of z and aux inverted
  }
  
  public static <T extends Comparable<? super T>> void topDownAcCoSm32(T[] z) {
    // in-place topDown mergesort with avoid copy, cutoff at 32 and skip merge.
    if (z == null) return; int cutoff = 32;
    int N = z.length; if (N < 2) return;
    T[] aux = z.clone(); // aux is clone of a not any array of nulls of length N
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      // no copy from a to b
      for (int k = lo; k <= hi; k++)
        if (i > mid) b[k] = a[j++];     // interchange a and b in all conditions
        else if (j > hi ) b[k] = a[i++];
        else if (a[j].compareTo(a[i]) < 0) b[k] = a[j++];
        else b[k] = a[i++];
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo + cutoff) { insertionSort.accept(b, lo, hi); return; }
      int mid = lo + (hi - lo)/2;
      self.accept(b, a, lo, mid);     // order of a and b inverted
      self.accept(b, a, mid+1, hi);   // order of a and b inverted
      if (a[mid].compareTo(a[mid+1])>0) merge.accept(a, b, lo, mid, hi); 
      else System.arraycopy(a, lo, b, lo, hi - lo + 1);
    });
    sort.accept(aux, z, 0, N - 1);   // order of z and aux inverted
  }
  
  public static <T extends Comparable<? super T>> void topDownAcCoSm8(T[] z) {
    // in-place topDown mergesort with avoid copy, cutoff at 32 and skip merge.
    if (z == null) return; int cutoff = 8;
    int N = z.length; if (N < 2) return;
    T[] aux = z.clone(); // aux is clone of a not any array of nulls of length N
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      // no copy from a to b
      for (int k = lo; k <= hi; k++)
        if (i > mid) b[k] = a[j++];     // interchange a and b in all conditions
        else if (j > hi ) b[k] = a[i++];
        else if (a[j].compareTo(a[i]) < 0) b[k] = a[j++];
        else b[k] = a[i++];
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo + cutoff) { insertionSort.accept(b, lo, hi); return; }
      int mid = lo + (hi - lo)/2;
      self.accept(b, a, lo, mid);     // order of a and b inverted
      self.accept(b, a, mid+1, hi);   // order of a and b inverted
      if (a[mid].compareTo(a[mid+1])>0) merge.accept(a, b, lo, mid, hi); 
      else System.arraycopy(a, lo, b, lo, hi - lo + 1);
    });
    sort.accept(aux, z, 0, N - 1);   // order of z and aux inverted
  }
  
  public static <T extends Comparable<? super T>> void topDownCoFmSm(T[] z, int cutoff) {
    // in-place topDown mergesort with cutoff to insertion sort
    if (z == null) return; if (cutoff<0) cutoff = 0; int cut = cutoff;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
      for (int i = lo; i <= hi; i++)
        for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
          { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };    
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo + cut) { insertionSort.accept(a, lo, hi); return; }
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      if(a[mid].compareTo(a[mid+1])>0) fasterMerge.accept(a, b, lo, mid, hi); 
    });    
    sort.accept(z, aux, 0, N - 1);
  }
  
  public static <T extends Comparable<? super T>> void bottomUp(T[] z, int...xx) {
    // in-place bottom-up mergesort
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
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
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));
  }
  
  public static <T extends Comparable<? super T>> void bottomUpFm(T[] z, int...xx) {
    // in-place bottom-up mergesort with faster merge
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };    
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        fasterMerge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));
  }
  
  public static <T extends Comparable<? super T>> void bottomUpCo(T[] z, int cutoff) {
    // in-place bottom-up mergesort with cutoff to insertion sort.
    if (z == null) return; if (cutoff<0) cutoff = 0;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
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
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) { // lo: subarray index
        int len = Math.min(lo+sz+sz-1,N-1) - lo + 1;
        if (len <= cutoff) insertionSort.accept(z,lo,Math.min(lo+sz+sz-1,N-1));
        else merge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));
      } 
  }
  
  public static <T extends Comparable<? super T>> void bottomUpSm(T[] z, int...xx) {
    // in-place bottom-up mergesort with skip merge when subarrays are in order.
    // xx is for interfacing with compare() and cutoff().
    if (z == null) return;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
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
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        // skip merge when subarrays are in order
        if(z[lo+sz-1].compareTo(z[lo+sz])>0)
          merge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));        
  }
  
  public static <T extends Comparable<? super T>> void bottomUpCoFmSm(T[] z, int cutoff) {
    // in-place bottom-up mergesort with cutoff, faster merge and skip merge.
    if (z == null) return; if (cutoff<0) cutoff = 0;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };    
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        // skip merge when subarrays are in order
        if(z[lo+sz-1].compareTo(z[lo+sz])>0)
          fasterMerge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));
  }

  public static <T extends Comparable<? super T>> int bottomUpNp(T[] z) {
    // in-place bottom-up mergesort modified to return the number of passes.
    if (z == null) return 0;
    int N = z.length; if (N < 2) return 0;
    int[] passes = {0};
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
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
    for (int sz = 1; sz < N; sz = sz+sz) {// sz: subarray size
      passes[0]++;
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge.accept(z, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1,N-1));
    }
    return passes[0];
  }
  
  public static <T extends Comparable<? super T>> void natural(T[] z, int...xx){
    // in-place natural mergesort
    // xx is for interfacing with compare() and cutoff().
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
  }
  
  public static <T extends Comparable<? super T>> void naturalFm(T[] z, int...xx){
    // in-place natural mergesort with faster merge.
    // xx is for interfacing with compare() and cutoff().
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
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };  
    Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == N) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      fasterMerge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    } 
  }
  
  public static <T extends Comparable<? super T>> void naturalCo(T[] z, int cutoff){
    // in-place natural mergesort with cutoff to insertion sort.
    if (z == null) return; if (cutoff<0) cutoff = 0;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
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
      int len = rlen+run1+run2-1 - rlen + 1;
      if (len <= cutoff) insertionSort.accept(z,rlen,rlen+run1+run2-1);
      else merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    } 
  }
  
  public static <T extends Comparable<? super T>> void naturalSm(T[] z, int...xx){
    // in-place natural mergesort with skip merge when subarrays are in order.
    // xx is for interfacing with compare() and cutoff().
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
      // skip merge when subarrays are in order
      if(z[rlen+run1-1].compareTo(z[rlen+run1])>0)
        merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);             
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    } 
  }
  
  public static <T extends Comparable<? super T>> int naturalNp(T[] z){
    // in-place natural mergesort modified to return the number of passes.
    if (z == null) return 0;
    int N = z.length; if (N < 2) return 0;
    int[] passes = {0};
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
    System.out.println("arraySize="+N+" numberOfRuns="+runs.size());
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen >= N) { runs.enqueue(run1); passes[0]++; rlen = 0; continue; }
      run2 = runs.dequeue();
      if (run1 + run2 + rlen > N) { 
        System.out.println("never happens");
        runs.enqueue(run1); runs.enqueue(run2); passes[0]++; rlen = 0; continue; 
      }
      merge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2); 
      if (rlen==N) { passes[0]++; rlen = 0; }
    }
    return passes[0];
  }
  
  public static <T extends Comparable<? super T>> void naturalCoFmSm(T[] z, int cutoff){
    // in-place natural mergesort with cutoff, faster merge and skip merge.
    if (z == null) return; if (cutoff<0) cutoff = 0;
    int N = z.length; if (N < 2) return;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), N);
    Consumer3<T[],Integer,Integer> insertionSort = (a,lo,hi) -> { T t;
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && a[j].compareTo(a[j-1])<0; j--)
        { t = a[j]; a[j] = a[j-1]; a[j-1] = t; }   
    };
    if (N<=cutoff) { insertionSort.accept(z,0,N-1); return; }
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
    Consumer5<T[],T[],Integer,Integer,Integer> fasterMerge = (a,b,lo,mid,hi) -> {
      // re ex2210 ex2211 p285 && ex2223 p287
      // copy 1st half of a to 1st half of aux in order
      for (int i = lo; i <= mid; i++)
        b[i] = a[i]; 
      // copy 2nd half of a to 2nd half of aux in reverse order
      for (int j = mid+1; j <= hi; j++)
        b[j] = a[hi-j+mid+1];
      // merge aux back to a without testing for exhaustion of either half (i>mid or j>hi)
      int i = lo, j = hi; // j starts at hi and is decremented
      for (int k = lo; k <= hi; k++) 
        if (b[j].compareTo(b[i])<0) 
             a[k] = b[j--];
        else a[k] = b[i++];
    };  
    Queue<Integer> runs = getRuns.apply(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == N) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      if(z[rlen+run1-1].compareTo(z[rlen+run1])>0)
        fasterMerge.accept(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);        
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % N;
    } 
  }
 
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (a[i].compareTo(a[i-1])<0) return false;
    return true;
  }
  
  @SuppressWarnings("unchecked")
  public static void compare(String alg1, int i1, String alg2, int i2, int trials) {
    Consumer2<Double[],Integer>[] cons = 
        (Consumer2<Double[],Integer>[])newInstance(Consumer2.class,2); 
    String[] algs = {alg1,alg2}; Integer[] ix = {i1,i2}; 
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "topDown": 
          Consumer2<Double[],Integer> m01 = (a,q) -> topDown(a,q); 
          cons[i] = m01; break;
        case "topDownAc": 
          Consumer2<Double[],Integer> m02 = (a,q) -> topDownAc(a,q);
          cons[i] = m02; break;
        case "topDownAm": 
          Consumer2<Double[],Integer> m18 = (a,q) -> topDownAm(a,q);
          cons[i] = m18; break;          
        case "topDownCo": 
          Consumer2<Double[],Integer> m03 = (a,q) -> topDownCo(a,q); 
          cons[i] = m03; break;
        case "topDownFm": 
          Consumer2<Double[],Integer> m04 = (a,q) -> topDownFm(a,q); 
          cons[i] = m04; break;
        case "topDownSm": 
          Consumer2<Double[],Integer> m05 = (a,q) -> topDownSm(a,q); 
          cons[i] = m05; break;
        case "topDownAcCoSm": 
          Consumer2<Double[],Integer> m06 = (a,q) -> topDownAcCoSm(a,q); 
          cons[i] = m06; break;
        case "topDownCoFmSm":
          Consumer2<Double[],Integer> m17 = (a,q) -> topDownAcCoSm(a,q); 
          cons[i] = m17; break;
        case "bottomUp": 
          Consumer2<Double[],Integer> m07 = (a,q) -> bottomUp(a,q); 
          cons[i] = m07; break;
        case "bottomUpCo": 
          Consumer2<Double[],Integer> m08 = (a,q) -> bottomUpCo(a,q); 
          cons[i] = m08; break; 
        case "bottomUpFm": 
          Consumer2<Double[],Integer> m09 = (a,q) -> bottomUpFm(a,q); 
          cons[i] = m09; break;
        case "bottomUpSm": 
          Consumer2<Double[],Integer> m10 = (a,q) -> bottomUpSm(a,q); 
          cons[i] = m10; break;
        case "bottomUpCoFmSm": 
          Consumer2<Double[],Integer> m11 = (a,q) -> bottomUpCoFmSm(a,q); 
          cons[i] = m11; break;
        case "natural": 
          Consumer2<Double[],Integer> m12 = (a,q) -> natural(a,q); 
          cons[i] = m12; break;
        case "naturalCo": 
          Consumer2<Double[],Integer> m13 = (a,q) -> naturalCo(a,q); 
          cons[i] = m13; break;
        case "naturalFm": 
          Consumer2<Double[],Integer> m14 = (a,q) -> naturalFm(a,q); 
          cons[i] = m14; break;
        case "naturalSm": 
          Consumer2<Double[],Integer> m15 = (a,q) -> naturalSm(a,q); 
          cons[i] = m15; break;
        case "naturalCoFmSm": 
          Consumer2<Double[],Integer> m16 = (a,q) -> naturalCoFmSm(a,q); 
          cons[i] = m16; break;
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; Double[] d, d2; int c = 0;
    Timer t = new Timer(); long time1, time2; 
    int[] len = {11, 101, 1001, 10001, 100001, 1000001}; // array lengths + 1
    String[] lens = {"10", "100", "1K", "10K", "100K", "1M"};
    System.out.println("\n    for random Double arrays "+alg1+" cutoff "+i1+" "+alg2+" cutoff "+i2);
    System.out.println("    array                      average times");
     System.out.printf("    length       %14s    %14s\n", alg1, alg2);
    for (int k = 0; k < len.length; k++) {
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = new Double[len[k]-1];
        for (int l = 0; l < len[k]-1; l++) d[l] = r.nextDouble();
        d2 = d.clone();
        if (c % 2 == 0) {
          t.reset();
          cons[0].accept(d,ix[0]);
          time1 += t.num();
        } else {
          t.reset();
          cons[1].accept(d,ix[1]);
          time2 += t.num();
        }
        if (c % 2 == 0) {
          t.reset();
          cons[1].accept(d2,ix[1]);          
          time2 += t.num();
        } else {
          t.reset();
          cons[0].accept(d2,ix[0]);
          time1 += t.num();
        }
      }
      System.out.printf("    %6s           %10.3f        %10.3f\n",
          lens[k], 1.*time1/trials, 1.*time2/trials);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static void cutoff(String alg1, int i1, String alg2, int i2, int trials) {
    Consumer2<Double[],Integer>[] cons = 
        (Consumer2<Double[],Integer>[])newInstance(Consumer2.class,2); 
    String[] algs = {alg1,alg2};
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "topDown": 
          Consumer2<Double[],Integer> m01 = (a,q) -> topDown(a,q); 
          cons[i] = m01; break;
        case "topDownAc": 
          Consumer2<Double[],Integer> m02 = (a,q) -> topDownAc(a,q);
          cons[i] = m02; break;
        case "topDownCo": 
          Consumer2<Double[],Integer> m03 = (a,q) -> topDownCo(a,q); 
          cons[i] = m03; break;
        case "topDownFm": 
          Consumer2<Double[],Integer> m04 = (a,q) -> topDownFm(a,q); 
          cons[i] = m04; break;
        case "topDownSm": 
          Consumer2<Double[],Integer> m05 = (a,q) -> topDownSm(a,q); 
          cons[i] = m05; break;
        case "topDownAcCoSm": 
          Consumer2<Double[],Integer> m06 = (a,q) -> topDownAcCoSm(a,q); 
          cons[i] = m06; break;
        case "topDownCoFmSm":
          Consumer2<Double[],Integer> m17 = (a,q) -> topDownAcCoSm(a,q); 
          cons[i] = m17; break;
        case "bottomUp": 
          Consumer2<Double[],Integer> m07 = (a,q) -> bottomUp(a,q); 
          cons[i] = m07; break;
        case "bottomUpCo": 
          Consumer2<Double[],Integer> m08 = (a,q) -> bottomUpCo(a,q); 
          cons[i] = m08; break; 
        case "bottomUpFm": 
          Consumer2<Double[],Integer> m09 = (a,q) -> bottomUpFm(a,q); 
          cons[i] = m09; break;
        case "bottomUpSm": 
          Consumer2<Double[],Integer> m10 = (a,q) -> bottomUpSm(a,q); 
          cons[i] = m10; break;
        case "bottomUpCoFmSm": 
          Consumer2<Double[],Integer> m11 = (a,q) -> bottomUpCoFmSm(a,q); 
          cons[i] = m11; break;
        case "natural": 
          Consumer2<Double[],Integer> m12 = (a,q) -> natural(a,q); 
          cons[i] = m12; break;
        case "naturalCo": 
          Consumer2<Double[],Integer> m13 = (a,q) -> naturalCo(a,q); 
          cons[i] = m13; break;
        case "naturalFm": 
          Consumer2<Double[],Integer> m14 = (a,q) -> naturalFm(a,q); 
          cons[i] = m14; break;
        case "naturalSm": 
          Consumer2<Double[],Integer> m15 = (a,q) -> naturalSm(a,q); 
          cons[i] = m15; break;
        case "naturalCoFmSm": 
          Consumer2<Double[],Integer> m16 = (a,q) -> naturalCoFmSm(a,q); 
          cons[i] = m16; break;
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; Double[] d, d2; int c = 0;
    Timer t = new Timer(); long time1, time2; 
    int[] cut = range(1,201);
    System.out.println("\n    for random Double arrays");
    System.out.println("    length                     average times");
     System.out.printf("    cutoff       %14s    %14s\n", alg1, alg2);
    for (int k = 0; k < cut.length; k++) {
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = new Double[cut[k]];
        for (int l = 0; l < cut[k]; l++) d[l] = r.nextDouble();
        d2 = d.clone();
        if (c % 2 == 0) {
          t.reset();
          cons[0].accept(d,cut[k]);
          time1 += t.num();
        } else {
          t.reset();
          cons[1].accept(d,cut[k]);
          time2 += t.num();
        }
        if (c % 2 == 0) {
          t.reset();
          cons[1].accept(d2,cut[k]);          
          time2 += t.num();
        } else {
          t.reset();
          cons[0].accept(d2,cut[k]);
          time1 += t.num();
        }
      }
      System.out.printf("    %6s           %10.3f        %10.3f\n",
          ""+(cut[k]), 1.*time1/trials, 1.*time2/trials);
    }
  }
  
  public static <T extends Comparable<? super T>> double runLength(T[] z) {
    // return the average run length for runs in z.
    if (z == null || z.length == 0) return 0; 
    if (z.length == 1) return 1;
    int i = 0, c= 0; int runs = 0;
    while(i < z.length) {
      c = i;
      if (i < z.length -1) 
        while(i < z.length -1 && z[i].compareTo(z[i+1])<0) i++;
      ++i; ++runs;
    }
    return (1.*z.length)/runs;
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
//    compare("natural", -1,"topDownAcCoSm", 31, 100);
//    cutoff("topDown", -1,"topDownCo", -1, 1000);
    
    /*

    for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                      average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.020             0.040
       100                0.020             0.090
        1K                0.670             0.650
       10K                0.770             0.820
      100K               10.030             9.860
        1M              143.160           142.880

    for random Double arrays topDownCoFmSm cutoff 31 bottomUpCoFmSm cutoff 8
    array                      average times
    length        topDownCoFmSm    bottomUpCoFmSm
        10                0.020             0.040
       100                0.110             0.040
        1K                0.490             1.360
       10K                1.550             1.470
      100K               13.420            17.950
        1M              201.910           261.040

    for random Double arrays topDownCoFmSm cutoff 31 natural cutoff -1
    array                  average times
    length        topDownCoFmSm           natural
        10                0.040             0.050
       100                0.130             0.050
        1K                0.490             1.300
       10K                1.170             2.040
      100K               12.220            17.950
        1M              166.780           265.630
 
  for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.010             0.030
       100                0.040             0.120
        1K                0.570             0.580
       10K                1.060             1.250
      100K               10.820            10.840
        1M              164.890           164.160
        
    for random Double arrays topDownCoFmSm cutoff 31 topDownAcCoSm cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.010             0.010
       100                0.030             0.120
        1K                0.270             0.330
       10K                1.040             1.000
      100K               11.130            11.030
        1M              163.150           162.540

    for random Double arrays alg1 cutoff 31 alg2 cutoff 31
    array                  average times
    length        topDownCoFmSm     topDownAcCoSm
        10                0.020             0.030
       100                0.040             0.130
        1K                0.740             0.630
       10K                0.930             1.050
      100K               10.180            10.140
        1M              144.400           144.960

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              natural     topDownAcCoSm
        10                0.080             0.010
       100                0.030             0.140
        1K                1.600             0.470
       10K                1.520             1.080
      100K               16.080            13.620
        1M              254.390           214.330

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              natural         naturalCo
        10                0.050             0.040
       100                0.030             0.130
        1K                1.110             0.680
       10K                1.210             1.640
      100K               15.910            16.770
        1M              256.990           262.260

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length             bottomUp        bottomUpCo
        10                0.040             0.020
       100                0.050             0.060
        1K                1.840             1.030
       10K                2.080             0.990
      100K               34.690            20.130
        1M              535.830           377.450

    for random Double arrays alg1 cutoff -1 alg2 cutoff 63
    array                  average times
    length              topDown         topDownCo
        10                0.060             0.060
       100                0.050             0.130
        1K                1.800             0.570
       10K                1.860             2.420
      100K               20.430            16.850
        1M              270.610           243.130

    for random Double arrays alg1 cutoff -1 alg2 cutoff 31
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.020
       100                0.100             0.060
        1K                3.430             0.390
       10K                5.980             3.180
      100K               20.290            13.970
        1M              272.900           216.520


    for random Double arrays alg1 cutoff -1 alg2 cutoff 32
    array                  average times
    length              topDown         topDownCo
        10                0.090             0.030
       100                0.050             0.120
        1K                1.540             0.820
       10K                2.060             5.560
      100K               19.050            19.250
        1M              280.550           388.940

    for random Double arrays alg1 cutoff -1 alg2 cutoff 8
    array                  average times
    length              topDown         topDownCo
        10                0.060             0.120
       100                0.120             0.050
        1K                1.420             1.920
       10K                2.080             5.630
      100K               20.720            15.920
        1M              288.440           240.970
 
    for random Double arrays alg1 cutoff -1 alg2 cutoff 75
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.030
       100                0.100             0.100
        1K                3.560             0.460
       10K                3.990             4.660
      100K               19.320            18.840
        1M              278.970           303.240

    for random Double arrays alg1 cutoff -1 alg2 cutoff 100
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.020
       100                0.060             0.060
        1K                2.470             1.080
       10K                2.770             5.450
      100K               27.270            22.140
        1M              474.860           309.080

    for random Double arrays alg1 cutoff -1 alg2 cutoff 50
    array                  average times
    length              topDown         topDownCo
        10                0.080             0.040
       100                0.100             0.080
        1K                3.510             0.790
       10K                3.900             1.840
      100K               20.810            22.200
        1M              277.370           380.020

    for random Double arrays alg1 cutoff -1 alg2 cutoff 130
    array                  average times
    length              topDown         topDownCo
        10                0.070             0.050
       100                0.030             0.070
        1K                2.040             0.740
       10K                2.190             3.870
      100K               28.540            23.130
        1M              486.730           433.000


    for random Double arrays
    length                     average times
    cutoff              topDown         topDownCo
         1                0.003             0.001
         2                0.010             0.005
         3                0.000             0.001
         4                0.001             0.001
         5                0.005             0.001
         6                0.015             0.003
         7                0.011             0.003
         8                0.014             0.003
         9                0.016             0.008
        10                0.024             0.006
        11                0.019             0.010
        12                0.022             0.003
        13                0.014             0.003
        14                0.009             0.000
        15                0.005             0.003
        16                0.006             0.002
        17                0.007             0.002
        18                0.009             0.001
        19                0.003             0.003
        20                0.004             0.003
        21                0.004             0.004
        22                0.004             0.003
        23                0.005             0.002
        24                0.004             0.003
        25                0.005             0.000
        26                0.003             0.000
        27                0.002             0.003
        28                0.002             0.001
        29                0.003             0.004
        30                0.004             0.003
        31                0.007             0.002
        32                0.004             0.003
        33                0.003             0.005
        34                0.001             0.004
        35                0.005             0.007
        36                0.004             0.001
        37                0.004             0.003
        38                0.004             0.003
        39                0.008             0.004
        40                0.007             0.008
        41                0.004             0.008
        42                0.003             0.003
        43                0.008             0.005
        44                0.003             0.006
        45                0.004             0.005
        46                0.002             0.006
        47                0.006             0.006
        48                0.003             0.006
        49                0.003             0.006
        50                0.007             0.008
        51                0.004             0.008
        52                0.005             0.004
        53                0.005             0.006
        54                0.010             0.010
        55                0.002             0.010
        56                0.003             0.009
        57                0.002             0.008
        58                0.004             0.007
        59                0.004             0.007
        60                0.008             0.004
        61                0.005             0.006
        62                0.007             0.005
        63                0.007             0.005
        64                0.007             0.007
        65                0.006             0.005
        66                0.003             0.011
        67                0.003             0.010
        68                0.004             0.010
        69                0.004             0.010
        70                0.003             0.013
        71                0.003             0.010
        72                0.002             0.012
        73                0.002             0.012
        74                0.006             0.007
        75                0.006             0.011
        76                0.006             0.012
        77                0.002             0.015
        78                0.006             0.009
        79                0.008             0.009
        80                0.003             0.014
        81                0.003             0.017
        82                0.007             0.013
        83                0.006             0.012
        84                0.005             0.012
        85                0.008             0.011
        86                0.006             0.013
        87                0.005             0.015
        88                0.002             0.019
        89                0.008             0.012
        90                0.013             0.011
        91                0.009             0.013
        92                0.011             0.015
        93                0.010             0.017
        94                0.007             0.017
        95                0.009             0.017
        96                0.008             0.015
        97                0.007             0.014
        98                0.006             0.017
        99                0.004             0.022
       100                0.007             0.020
       101                0.006             0.022
       102                0.008             0.020
       103                0.011             0.016
       104                0.011             0.016
       105                0.009             0.021
       106                0.008             0.023
       107                0.011             0.015
       108                0.010             0.021
       109                0.008             0.022
       110                0.009             0.021
       111                0.009             0.022
       112                0.007             0.024
       113                0.007             0.022
       114                0.005             0.025
       115                0.015             0.017
       116                0.011             0.024
       117                0.010             0.026
       118                0.012             0.024
       119                0.010             0.022
       120                0.011             0.029
       121                0.012             0.027
       122                0.011             0.028
       123                0.009             0.029
       124                0.013             0.027
       125                0.013             0.026
       126                0.011             0.026
       127                0.010             0.031
       128                0.012             0.029
       129                0.043             0.042
       130                0.013             0.032
       131                0.008             0.039
       132                0.018             0.040
       133                0.011             0.040
       134                0.012             0.039
       135                0.010             0.039
       136                0.015             0.029
       137                0.012             0.034
       138                0.012             0.037
       139                0.013             0.034
       140                0.017             0.032
       141                0.008             0.043
       142                0.018             0.031
       143                0.021             0.029
       144                0.016             0.039
       145                0.013             0.040
       146                0.020             0.030
       147                0.017             0.039
       148                0.023             0.032
       149                0.009             0.041
       150                0.015             0.034
       151                0.017             0.036
       152                0.017             0.042
       153                0.016             0.053
       154                0.023             0.053
       155                0.023             0.058
       156                0.016             0.054
       157                0.019             0.061
       158                0.023             0.053
       159                0.026             0.051
       160                0.028             0.050
       161                0.028             0.058
       162                0.024             0.041
       163                0.024             0.047
       164                0.022             0.046
       165                0.018             0.050
       166                0.012             0.061
       167                0.018             0.053
       168                0.020             0.056
       169                0.018             0.051
       170                0.019             0.057
       171                0.021             0.052
       172                0.017             0.057
       173                0.020             0.056
       174                0.018             0.055
       175                0.019             0.058
       176                0.019             0.059
       177                0.023             0.053
       178                0.017             0.061
       179                0.026             0.054
       180                0.017             0.061
       181                0.022             0.056
       182                0.022             0.062
       183                0.017             0.062
       184                0.015             0.067
       185                0.021             0.060
       186                0.021             0.065
       187                0.026             0.060
       188                0.019             0.070
       189                0.023             0.068
       190                0.023             0.068
       191                0.026             0.062
       192                0.021             0.069
       193                0.027             0.067
       194                0.021             0.075
       195                0.023             0.067
       196                0.017             0.083
       197                0.015             0.082
       198                0.017             0.079
       199                0.018             0.078
       200                0.022             0.077

    
    */
    
    Random q = SecureRandom.getInstanceStrong(); Integer[] b;
    b = rangeInteger(1,30);
    shuffle(b,q);
    par(b);
//    topDown(b);
//    topDownFm(b);
//    topDownCo(b,30);
//    topDownAc(b);
//    topDownAm(b);
//    topDownSm(b);
//    topDownAcCoSm(b,16);
//    bottomUp(b);
//    bottomUpFm(b);
    bottomUpCo(b,-1);
//    bottomUpSm(b);
//    bottomUpCoFmSm(b,10);
//    natural(b);
//    naturalFm(b);
//    naturalSm(b);
//    naturalCo(b,-1);
//    naturalCoFmSm(b,50);
    par(b);
    System.exit(0);
    
//    Random r = new Random(System.currentTimeMillis()); Integer[] a;
//    int c = 0;
//    for (int i = 1; i < 10001; i++) {
//      a = rangeInteger(0,i);
//      shuffle(a,r);
////      topDown(a);
////    topDownFm(a);
////      topDownCo(a,16);
////      topDownSm(a);
////      topDownAc(a);
////      topDownAm(a);
////      topDownAcCoSm(a,16);
////      bottomUp(a);
////      bottomUpFm(a);
////      bottomUpCo(a,-1);
////      bottomUpSm(a);
////      bottomUpCoFmSm(a,16);
////      natural(a);
////      naturalFm(a);
////      naturalCo(a,15);
////      naturalSm(a);
////      naturalCoFmSm(a,15);
////      System.out.println("c="+c);
////      par(a);
//      assert a.length == i;
//      assert isSorted(a);
//      c++;
//    }
//    System.out.println("c="+c);

  }

}
