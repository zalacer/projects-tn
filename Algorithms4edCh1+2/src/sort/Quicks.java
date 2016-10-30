package sort;

import static analysis.Log.*;
import static java.lang.Math.ceil;
import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.*;
import static sort.Merges.*;
import static utils.RandomUtils.randomString;
import static sort.QuickX.sort;
import static sort.Shell.shellSortWithIncrementArray;

import java.util.Arrays;
import java.awt.Font;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import analysis.Histogram;
import analysis.Timer;
import edu.princeton.cs.algs4.StdDraw;
import v.Tuple2;
import sort.Heap;
import sort.HeapRaw;

@SuppressWarnings("unused")
public class Quicks<T> {
  
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
  public static interface Function2<A,B,C> {
    C apply(A a, B b);
  }
  
  @FunctionalInterface
  public static interface Function3<A,B,C,D> {
    D apply(A a, B b, C c);
  }
  
  @FunctionalInterface
  public interface RecursiveConsumer2<A,B> {
    void accept(final A a,final B b,final Consumer2<A,B> self);
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
    public static <A,B> Consumer2<A,B> consumer2(RecursiveConsumer2<A,B> f) {
      final Recursive<Consumer2<A,B>> r = new Recursive<>();
      return r.f = (a,b) -> f.accept(a,b,r.f);
    }
    public static <A,B,C> Consumer3<A,B,C> consumer3(RecursiveConsumer3<A,B,C> f) {
      final Recursive<Consumer3<A,B,C>> r = new Recursive<>();
      return r.f = (a,b,c) -> f.accept(a,b,c,r.f);
    }
    public static <A,B,C,D> Consumer4<A,B,C,D> consumer4(RecursiveConsumer4<A,B,C,D> f) {
      final Recursive<Consumer4<A,B,C,D>> r = new Recursive<>();
      return r.f = (a,b,c,d) -> f.accept(a,b,c,d,r.f);
    }
  }
  
  /* 
  method suffix dictionary:
  3Way : entropy-optimal 3-way (fat) partitioning by J. Bentley and D. McIlroy p299
  AeCE   : accept equals in compares and return number of compares and 
           exchanges (e3x2311)
  AePSub0 : accept equals in compares and return number of partitions and
            counts of number of subarrays of length 0 (e3x2311)
  AePSub012 : accept equals in compares and return number of partitions and
              counts of number of subarrays of length 0 (e3x2311)            
  C      : return number of compares, for ex2304, ex3408 p303      
  CEP    : return number of compares, exchanges and partitions
  Co : implemented with cutoff to selection sort
  CoM3T9F3 : implemented with cutoff, median of 3, Tukey ninther and fast 3-way
  Ct     : return number of compares that return true
  E : return total number of exchanges.
  EL     : return number of exchanges of largest element, for ex2303 p303
  F3 : implemented with fast 3-way (Bentley & McIlroy) partitioning ex2322 p306
  Igss : ignore small subarrays below a cutoff and finish with insertion sort ex2326
  Int : for int[]s only
  IntIt : for int[]s only and iterative
  It : iterative
  M3 : implemented with median-of-three partitioning p296 ex 2318 p305
  M5 : implemented with median-of-five partitioning ex 2319 p305
  P : return total number of partitions
  Rd : return recursive depth
  Rp : implement random pivot selection ex 2329
  Sen : implemented with sentinels to remove bounds checks
  Sslfi : return sizes of subarrays left for insertion sort ex2326
  Sub0 : return number of subarrays of zero length
  Sub012 : return counts of number of subarrays of lengths 0,1,2 and 
            the number of partitions.
  Sub012 : return counts of number of subarrays of lengths 0,1,2 for ex2307 p303
  T9 : implemented with Tukey ninther pivot selection ex2323
  VCo: variable cutoff implemented as command line arg
  */
  
  public static <T extends Comparable<? super T>> int partition(T[] z) {
    // quick sort partition p291
    if (z == null ) return -1;
    int lo = 0; int hi = z.length-1;
    int i = lo, j = hi+1;
    T v = z[lo]; T t;
    while (true) {
      while (z[++i].compareTo(v)<0) if (i == hi) break;
      while (v.compareTo(z[--j])<0) if (j == lo) break;
      if (i >= j) {
        System.out.println("scan break at i="+i+" j="+j);
        break;
      }
      t = z[i]; z[i] = z[j]; z[j] = t;
    }
    t = z[lo]; z[lo] = z[j]; z[j] = t;
    return j;
  }
  
  public static <T extends Comparable<? super T>> void quick(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289
    if (z == null || z.length < 2) return;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      Random r = null;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickCo(T[] z, int cutoff, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with cutoff at length < M
    if (z == null || z.length < 2) return;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length;
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Consumer2<Integer,Integer> insertionSort =(lo, hi) -> {
      for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
              exch.accept(j, j-1);
    };
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int N = hi - lo + 1;     
      if (N < cutoff) { insertionSort.accept(lo, hi); return; } // cutoff to insertion sort
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quick(T[] z, int low, int high) {
    // quicksort algorithm 2.5 p289 for subarray
    if (z == null || z.length < 2) return;
    int n = high-low+1;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(low, high);
  }
  
  public static <T> void quick(T[] z, int low, int high, Comparator<T> c) {
    // quicksort algorithm 2.5 p289 for subarray
    if (z == null || z.length < 2) return;
    int n = high-low+1;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (c.compare(z[++i],v)<0) if (i == hi) break;
        while (c.compare(v,z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(low, high);
  }
  
  public static <T extends Comparable<? super T>> void sampleSort0(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with sampleSort pivot selection for ex2324
    if (z == null || z.length < 2) return;
    Consumer2<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length;
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Consumer2<Integer,Integer> insertionSort =(lo, hi) -> {
      for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
              exch.accept(j, j-1);
    };
    Consumer4<Tuple2<T,Integer>[], Integer,Integer,Comparator<Tuple2<T,Integer>>> 
      insertionSortC =(a,lo,hi,c) -> {
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && c.compare(a[j],a[j-1])<0; j--)
            exch.accept(j, j-1);
    };
    Function3<Integer,Integer,Integer,int[]> selectEvenly = (lo,hi,m) -> {
      // return indices of m elements evenly selected from any sub-array[lo..hi]
      int N = hi-lo+1; int[] ix = new int[m]; int c = 0;
      for (int i=0; i<m; i++) ix[c++] = (i*N/m + N/(2*m))+lo;
      return ix;
    };
    if (n<8) { insertionSort.accept(0,n-1); return; }
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int N=hi-lo+1, k=0, size=0, c; 
      System.out.println("lo="+lo+" hi="+hi+" N="+N);
      // find size of sample array and limit its length to 255
      k = 32 - Integer.numberOfLeadingZeros(N-1); 
      System.out.println("k="+k);
      k = k>10 ? 8 : k-2;
      System.out.println("k="+k);
      size = (int)(Math.pow(2,k)-1);
      System.out.println("size="+size);
      // select sample elements evenly based on review of sampling methods in
      // http://www.cra.org/Activities/craw_archive/dmp/awards/2007/Berlin/jberlin/sample_sort_tr.pdf
      // this is available locally at 
      // SampleSortUsingTheStandardTemplateAdaptiveParallelLibrary-sample_sort_tr-Berlin-2007.pdf
      int[] ix = selectEvenly.apply(lo,hi,size); //get indices of sample
      // sort sample elements and their indices
      par(ix);
      Tuple2<T,Integer>[] ta = ofDim(Tuple2.class,size);
      for (int l=0; l<size; l++) ta[l] = new Tuple2<T,Integer>(z[ix[l]],ix[l]);
      par(z);
      par(ta);
      Comparator<Tuple2<T,Integer>> comp = (t1,t2) -> { return t1._1.compareTo(t2._1); };
      if (n<8) insertionSortC.accept(ta,0,size-1,comp);
      else quick(ta,0,size-1,comp);
      par(z);
      par(ta);
      // in ta the median is ta[size/2]._1 and its index in z is ta[size/2]._2
      // relocate median to lo
      exch.accept(lo,ta[size/2]._2);
      // relocate elements in ta up to median to just after lo in order upwards
      for (int l=0; l<size/2; l++) exch.accept(lo+1+l,ta[l]._2);
      par(z);
      // relocate elements in ta after median to hi end in reverse order downwards
      c=0; for (int l=size-1; l>size/2; l--) exch.accept(hi-c++,ta[l]._2); 
      par(z);
//  
//    This method is faulty because ix[] becomes obsolete  when exchanging elements up
//    to size/2 exchanges elements that are in >size/2 so it would be necessary to
//    track and avoid that. 
//      
//      c=0; for (int l=0; l<size; l++) exch.accept(lo+c++,ix[l]);
//      //sort sample elements
//      if (n<8) insertionSort.accept(lo,lo+size-1);
//      else quick(z,lo,lo+size-1);
//      // median is at lo+size/2, relocate sample elements above it to hi end
//      c=0; for (int l=lo+size-1; l>lo+size/2; l--) exch.accept(l,hi+c--);
      // adjust lo and hi and define i and j
      lo = lo+size/2; hi = hi-size/2; int i = lo, j = hi+1;
      // proceed as usual
      T pivot = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(pivot)<0) if (i == hi) break;
        while (pivot.compareTo(z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      if (hi-lo+1 < 8) { insertionSort.accept(lo,hi); return; }
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void sampleSort(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with sampleSort pivot selection for ex2324
    if (z == null || z.length < 2) return;
    Consumer2<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length;
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Consumer2<Integer,Integer> insertionSort =(lo, hi) -> {
      for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
              exch.accept(j, j-1);
    };
    Function3<Integer,Integer,Integer,int[]> selectEvenly = (lo,hi,m) -> {
      // return indices of m elements evenly selected from any sub-array[lo..hi]
      int N = hi-lo+1; int[] ix = new int[m]; int c = 0;
      for (int i=0; i<m; i++) ix[c++] = (i*N/m + N/(2*m))+lo;
      return ix;
    };
    if (n<8) { insertionSort.accept(0,n-1); return; }
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int N=hi-lo+1, k=0, size=0, c;
      // find size of sample array and limit its length to 255
      // this method creates samples that are too big which slow performance
//      k = 32 - Integer.numberOfLeadingZeros(N-1);  
//      k = k>10 ? 8 : k-2;
//      size = (int)(Math.pow(2,k)-1);
      size = (int)(ceil(2*(lg(N)-lg(lg(N)))-1));
          //NlgN when k is about lgN-lglgN.
      // select sample elements evenly based on review of sampling methods in
      // http://www.cra.org/Activities/craw_archive/dmp/awards/2007/Berlin/jberlin/sample_sort_tr.pdf
      // this is available locally at 
      // SampleSortUsingTheStandardTemplateAdaptiveParallelLibrary-sample_sort_tr-Berlin-2007.pdf
      int[] ix = selectEvenly.apply(lo,hi,size); //get indices of sample
      // sort sample elements and their indices
      // relocate sample elements to start at lo contiguously
      c=0; for (int l=0; l<size; l++) exch.accept(lo+c++,ix[l]);
      //sort sample elements
      if (n<8) insertionSort.accept(lo,lo+size-1);
      else quick(z,lo,lo+size-1);
      // median is at (or next to) lo+size/2, relocate sample elements above it to hi end
      c=0; for (int l=lo+size-1; l>lo+size/2; l--) exch.accept(l,hi+c--);
      // adjust lo and hi and define i and j
      lo = lo+size/2; hi = hi-size/2; int i = lo, j = hi+1;
      // proceed as usual
      T pivot = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(pivot)<0) if (i == hi) break;
        while (pivot.compareTo(z[--j])<0) if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      if (hi-lo+1 < 9) { insertionSort.accept(lo,hi); return; }
      int j = partition.apply(lo, hi);
      if (j-lo > hi-j) { //sort smallest 1st to reduce recursive depth
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
      } else {
        self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
        self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      }
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void sampleSort2(T[] z, int p, boolean shuffle) {
    // samplesort based on http://parallelcomp.uw.hu/ch09lev1sec5.html
    // p = number of processors
    BiPredicate<T,T> less = (t1,t2) -> { return t1.compareTo(t2) < 0; };
    Consumer3<T[],Integer,Integer> exch = (x,a,b) -> { T t = x[a]; x[a] = x[b]; x[b] = t; };
    Consumer3<T[],Integer,Integer> insertionSort =(x,lo, hi) -> {
      for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && less.test(x[j], x[j-1]); j--)
              exch.accept(x, j, j-1);
    };
    Function2<T[],Integer,T[]> selectEvenly = (a,m) -> {
      // select m elements evenly from a
      T[] q = ofDim(a[0].getClass(), m); int c=0;
      for (int i=0; i<m; i++) q[c++] = a[i*a.length/m + a.length/(2*m)];
      return q;
    };
    if (z == null || z.length < 2) return; int n = z.length;
    if (n<10) { insertionSort.accept(z,0,n-1); return; }
    if (p == 1 || n/p<(p-1)) { sort(z); return; } //sort is sort.QuickX.sort
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(z,k-1,l); }
    }
    // partition z into p slices and put them into T[][]v
    int[] u = new int[p+1]; u[0] = 0; u[p] = n;
    for (int i=1;i<p;i++) u[i] = (n)*i/p; 
    T[][] v = ofDim(z[0].getClass(), p, 0);
    for (int i=0; i<p; i++) v[i] = slice(z,u[i],u[i+1]);
    // selectEvenly p-1 elements from each slice and put them in T[]w
    T[] w = selectEvenly.apply(v[0],p-1);
    for (int i=1; i<p; i++) w = append(w, selectEvenly.apply(v[i],p-1));
    // sort w
    if (w.length<10) insertionSort.accept(w,0,w.length-1); else sort(w);
//    par(w);
    // selectEvenly p-1 elements from w
    T[] g = selectEvenly.apply(w,p-1);
//    par(g);
    // partially sort z into p buckets <w[0],<w[1],...,<w[p-2],>=w[p-2]
    ArrayList<T>[] ala = ofDim(ArrayList.class, p); int c = 0;
    for (int i=0; i<p; i++) ala[c++] = new ArrayList<T>();
    LOOP:
    for (int i=0; i<n; i++) {
      for (int j=0; j<p-1; j++ )
        if (less.test(z[i],g[j])) { ala[j].add(z[i]); continue LOOP; }
      ala[p-1].add(z[i]);
    }       
    // convert the ArrayLists to arrays reusing T[][]v
    for (int i=0; i<p; i++) v[i] = ala[i].toArray(g);
    // sort the arrays in T[][]v each in a separate thread
//    System.out.print("allocation "); for (int i=0; i<p; i++) System.out.print(v[i].length+" ");
//    System.out.println();
    Iterator<T[]> ita = iterator(v); Thread[] threads = new Thread[p]; 
    for (int i=0; i<p; i++) threads[i] = new Thread(() -> { sort(ita.next()); });
    for (Thread t : threads) t.start();
    for (Thread t : threads)
      try {t.join();} catch (InterruptedException e) {Thread.currentThread().interrupt();}
    c=0; for (int i=0;i<p;i++) for (int j=0;j<v[i].length;j++) z[c++] = v[i][j];
  }
  
  public static void sampleSortInteger2(Integer[] z, int p, boolean shuffle) {
    // samplesort based on http://parallelcomp.uw.hu/ch09lev1sec5.html
    // p = number of processors
    BiPredicate<Integer,Integer> less = (t1,t2) -> { return t1.compareTo(t2) < 0; };
    Consumer3<Integer[],Integer,Integer> exch = (x,a,b) -> { Integer t = x[a]; x[a] = x[b]; x[b] = t; };
    Consumer3<Integer[],Integer,Integer> insertionSort =(x,lo, hi) -> {
      for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && less.test(x[j], x[j-1]); j--)
              exch.accept(x, j, j-1);
    };
    Function2<Integer[],Integer,Integer[]> selectEvenly = (a,m) -> {
      // select m elements evenly from a
      Integer[] q = ofDim(Integer.class, m); int c=0;
      for (int i=0; i<m; i++) q[c++] = a[i*a.length/m + a.length/(2*m)];
      return q;
    };
    if (z == null || z.length < 2) return; int n = z.length;
    if (n<10) { insertionSort.accept(z,0,n-1); return; }
    if (p == 1 || n/p<(p-1)) { sort(z); return; } //sort is sort.QuickX.sort
    if (shuffle) {
      Random r = null; int l;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(z,k-1,l); }
    }
    // partition z into p slices and put them into T[][]v
    int[] u = new int[p+1]; u[0] = 0; u[p] = n;
    for (int i=1;i<p;i++) u[i] = (n)*i/p; 
    Integer[][] v = ofDim(Integer.class, p, 0);
    for (int i=0; i<p; i++) v[i] = slice(z,u[i],u[i+1]);
    // selectEvenly p-1 elements from each slice and put them in T[]w
    Integer[] w = selectEvenly.apply(v[0],p-1);
    for (int i=1; i<p; i++) w = append(w, selectEvenly.apply(v[i],p-1));
    // sort w
    if (w.length<10) insertionSort.accept(w,0,w.length-1); else sort(w);
//    w = new Integer[]{50};
//    par(w);
    // selectEvenly p-1 elements from w
    Integer[] g = selectEvenly.apply(w,p-1);
//    par(g);
    // partially sort z into p buckets <w[0],<w[1],...,<w[p-2],>=w[p-2]
    ArrayList<Integer>[] ala = ofDim(ArrayList.class, p); int c = 0;
    for (int i=0; i<p; i++) ala[c++] = new ArrayList<Integer>();
    LOOP:
    for (int i=0; i<n; i++) {
      for (int j=0; j<p-1; j++ )
        if (less.test(z[i],g[j])) { ala[j].add(z[i]); continue LOOP; }
      ala[p-1].add(z[i]);
    }       
    // convert the ArrayLists to arrays reusing T[][]v
    for (int i=0; i<p; i++) v[i] = ala[i].toArray(g);
    // sort the arrays in T[][]v each in a separate thread
//    System.out.print("allocation "); for (int i=0; i<p; i++) System.out.print(v[i].length+" ");
//    System.out.println();
    Iterator<Integer[]> ita = iterator(v); Thread[] threads = new Thread[p]; 
    for (int i=0; i<p; i++) threads[i] = new Thread(() -> { sort(ita.next()); });
    for (Thread t : threads) t.start();
    for (Thread t : threads)
      try {t.join();} catch (InterruptedException e) {Thread.currentThread().interrupt();}
    c=0; for (int i=0;i<p;i++) for (int j=0;j<v[i].length;j++) z[c++] = v[i][j];
  }
  
  public static <T extends Comparable<? super T>> void quickF3(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289
    if (z == null || z.length < 2) return;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      Random r = null;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    BiPredicate<T,T> less = (t1,t2) -> { return t1.compareTo(t2) < 0; };
    BiPredicate<T,T> eq = (t1,t2) -> { return t1.compareTo(t2) == 0; };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      // Bentley-McIlroy 3-way partitioning from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (less.test(z[++i], v))
              if (i == hi) break;
          while (less.test(v, z[--j]))
              if (j == lo) break;

          // pointers cross
          if (i == j && eq.test(z[i], v))
              exch.accept(++p, i);
          if (i >= j) break;

          exch.accept(i, j);
          if (eq.test(z[i], v)) exch.accept(++p, i);
          if (eq.test(z[j], v)) exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++)
          exch.accept(k, j--);
      for (int k = hi; k >= q; k--)
          exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickCoM3T9F3v1(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with cutoff to selection, median of 3 pivot selection,
    // Tukey ninther median selection and fast 3-way Bentley-McIlroy partitioning
    if (z == null || z.length < 2) return;
    int n = z.length, l, INSERTION_SORT_CUTOFF = 8, MEDIAN_OF_3_CUTOFF = 40;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
//    if (shuffle) {
//      Random r = null;
//      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//      if (r == null) r = new Random(System.currentTimeMillis());
//      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
//    }
    BiPredicate<T,T> less = (t1,t2) -> { return t1.compareTo(t2) < 0; };
    BiPredicate<T,T> eq = (t1,t2) -> { return t1.compareTo(t2) == 0; };
    Consumer2<Integer,Integer> insertionSort = (lo,hi) -> {
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      for (int i = lo; i <= hi; i++)
        for (int j = i; j > lo && less.test(z[j], z[j-1]); j--)
            exch.accept(j, j-1);
    };
    Function3<Integer,Integer,Integer,Integer> median3 = (i,j,k) -> {
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      return (less.test(z[i], z[j]) ?
          (less.test(z[j], z[k]) ? j : less.test(z[i], z[k]) ? k : i) :
          (less.test(z[k], z[j]) ? j : less.test(z[k], z[i]) ? k : i));
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
//      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1;
      if (N <= INSERTION_SORT_CUTOFF) { insertionSort.accept(lo, hi); return; }
      else if (N <= MEDIAN_OF_3_CUTOFF)
        { int m = median3.apply(lo, lo + N/2, hi); exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        int m1 = median3.apply(lo, lo + eps, lo + eps + eps);
        int m2 = median3.apply(mid - eps, mid, mid + eps);
        int m3 = median3.apply(hi - eps - eps, hi - eps, hi); 
        int ninther = median3.apply(m1, m2, m3);
        exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (less.test(z[++i], v))
              if (i == hi) break;
          while (less.test(v, z[--j]))
              if (j == lo) break;

          // pointers cross
          if (i == j && eq.test(z[i], v))
              exch.accept(++p, i);
          if (i >= j) break;

          exch.accept(i, j);
          if (eq.test(z[i], v)) exch.accept(++p, i);
          if (eq.test(z[j], v)) exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++)
          exch.accept(k, j--);
      for (int k = hi; k >= q; k--)
          exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickCoM3T9F3v2(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with cutoff to selection, median of 3 pivot selection,
    // Tukey ninther median selection and fast 3-way Bentley-McIlroy partitioning
//    if (z == null || z.length < 2) return;
//    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
//    if (shuffle) {
//      Random r = null; int l;
//      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//      if (r == null) r = new Random(System.currentTimeMillis());
//      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
//    }
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
//      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
      if (N < 19) { 
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      else if (N < 41) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
  }
  
  public static <T extends Comparable<? super T>> void quickVCoRpF3(T[] z, int M) {
    // ex 2329
    // quicksort algorithm 2.5 p289 with cutoff < M to selection, random pivot 
    // selection and fast 3-way Bentley-McIlroy partitioning.
//    if (z == null || z.length < 2) return;
    Random r = new Random(System.currentTimeMillis());
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
//      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1; T t;
      if (N < M) { 
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      
      // random pivot selection
      int x = r.nextInt(N);
      t = z[lo]; z[lo] = z[lo+x]; z[lo+x] = t;
      
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
  }
  
  public static <T extends Comparable<? super T>> void quickVCoM3T9F3v2(T[] z, int M,boolean shuffle) {
    // ex2325
    // quicksort algorithm 2.5 p289 with cutoff < M to selection, median of 3 pivot selection,
    // Tukey ninther median selection and fast 3-way Bentley-McIlroy partitioning
//    if (z == null || z.length < 2) return;
    int n = z.length;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    if (shuffle) {
      Random r = new Random(System.currentTimeMillis()); int l;
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
      if (N < M) { 
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      else if (N <= 40) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
  }
  
  public static <T extends Comparable<? super T>> double quickVCoRdM3T9F3v2(T[] z, int M,boolean shuffle) {
    // ex2328
    // quicksort algorithm 2.5 p289 with cutoff < M to selection, median of 3 pivot selection,
    // Tukey ninther median selection, fast 3-way Bentley-McIlroy partitioning and returning
    // the avg recursive depth.   
//    if (z == null || z.length < 2) return;
    int[] rec = {0};
    List<Integer> list = new ArrayList<>();
//
//    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
//    if (shuffle) {
//      Random r = null; int l;
//      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//      if (r == null) r = new Random(System.currentTimeMillis());
//      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
//    }
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      rec[0]++; list.add(rec[0]);
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
      if (N < M) { 
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      else if (N <= 40) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
    return mean(list.toArray(new Integer[0]));
  }
  
  public static <T extends Comparable<? super T>> void quickVCoIgssM3T9F3v2(T[] z, int M,boolean shuffle) {
    // ex2327
    // quicksort algorithm 2.5 p289 with cutoff < M to ignore, median of 3 pivot selection,
    // Tukey ninther median selection and fast 3-way Bentley-McIlroy partitioning and
    // finish with insertion sort of entire array.
//    if (z == null || z.length < 2) return;
//    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
//    if (shuffle) {
//      Random r = null; int l;
//      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//      if (r == null) r = new Random(System.currentTimeMillis());
//      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
//    }
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
//      if (hi <= lo) return;
      if (hi - lo + 1 < M) return;
//      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
//      if (N < M) { 
//        for (int i = lo; i <= hi; i++)
//          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
//            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
//        return;
//      }
      if (N <= 40) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
    // finish with insertion sort with sentinel and without exchanges Ex2.1.24-25 p267
    int N = z.length; T t;
    // put smallest first
    for (int i = N - 1; i > 0; i--) 
      if (z[i].compareTo(z[i-1])<0) { 
        t=z[i]; z[i]=z[i-1]; z[i-1]=t; //exch(z, i, i-1);
      }
    for (int i = 1; i < N; i++) { 
      t = z[i];
      int j = i;
      while (t.compareTo(z[j - 1])<0) z[j] = z[--j];
      z[j] = t;
    }
  }
  
  public static <T extends Comparable<? super T>> Integer[] quickVCoSslfiM3T9F3v2(T[] z, int M,boolean shuffle) {
    // ex2326
    // quicksort algorithm 2.5 p289 with cutoff < M to selection, median of 3 pivot selection,
    // Tukey ninther median selection, fast 3-way Bentley-McIlroy partitioning and returns the
    // sizes of subarrays left for insertion sort.
//    if (z == null || z.length < 2) return;
//    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
//    if (shuffle) {
//      Random r = null; int l;
//      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//      if (r == null) r = new Random(System.currentTimeMillis());
//      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
//    }
    List<Integer> ss = new ArrayList<>();
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
      if (N < M) {
        ss.add(N);
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      else if (N <= 40) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0) ;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      self.accept(lo, j);
      self.accept(i, hi);
    });
    sort.accept(0, z.length-1);
    return ss.toArray(new Integer[0]);
  }
  
  public static <T extends Comparable<? super T>> T[] quickSort(T[] z) {
    quickCoM3T9F3v3(z,false);
    return z;
  }
  
  public static <T extends Comparable<? super T>> T[] sort(T[] z) {
    quickCoM3T9F3v3(z,false);
    return z;
  }
  
  public static <T extends Comparable<? super T>> void quickCoM3T9F3v3(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with cutoff to selection, median of 3 pivot selection,
    // Tukey ninther median selection and fast 3-way Bentley-McIlroy partitioning
    if (z == null || z.length < 2) return;
    int n = z.length;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    if (shuffle) {
      Random r = new Random(System.currentTimeMillis()); int l;
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
    quickCoM3T9F3v3(z, 0, z.length-1);
  }
  
  public static <T extends Comparable<? super T>> void quickCoM3T9F3v3(T[] z, int lo, int hi) {
//      if (hi <= lo) return;
      //from http://algs4.cs.princeton.edu/23quicksort/QuickX.java
      int N = hi - lo + 1, i1, j1, k1; T t;
      if (N <= 8) { 
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      else if (N <= 40) {
        i1=lo; j1=lo+N/2; k1=hi;
        int m = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[m]; z[m]=z[lo]; z[lo]= t; 
      } //exch.accept(m, lo); }
      else  { // Tukey ninther
        int eps = N/8;
        int mid = lo + N/2;
        i1=lo; j1=lo+eps; k1=lo+eps+eps;
        int m1 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=mid-eps; j1=mid; k1=mid+eps;
        int m2 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=hi-eps-eps; j1=hi-eps; k1=hi;
        int m3 = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        i1=m1; j1=m2; k1=m3;
        int ninther = (z[i1].compareTo(z[j1])<0 ?
            (z[j1].compareTo(z[k1])<0 ? j1 : z[i1].compareTo(z[k1])<0 ? k1 : i1) :
              (z[k1].compareTo(z[j1])<0 ? j1 : z[k1].compareTo(z[i1])<0 ? k1 : i1));
        t=z[ninther]; z[ninther]=z[lo]; z[lo]=t; // exch.accept(ninther, lo);
      }
      // Bentley-McIlroy 3-way partitioning 
      int i = lo, j = hi+1;
      int p = lo, q = hi+1;

      T v = z[lo];
      while (true) {
          while (z[++i].compareTo(v)<0) if (i == hi) break;
          while (v.compareTo(z[--j])<0)  if (j == lo) break;

          // pointers cross
          if (i == j && z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; }//exch.accept(++p, i);
          if (i >= j) break;

          t=z[i]; z[i]=z[j]; z[j]=t; //exch.accept(i, j);
          if (z[i].compareTo(v)==0) { t=z[++p]; z[p]=z[i]; z[i]=t; } //exch.accept(++p, i);
          if (z[j].compareTo(v)==0) { t=z[--q]; z[q]=z[j]; z[j]=t; } //exch.accept(--q, j);
      }

      i = j + 1;
      for (int k = lo; k <= p; k++) { t=z[k]; z[k]=z[j]; z[j]=t; j--; } //exch.accept(k, j--);
      for (int k = hi; k >= q; k--) { t=z[k]; z[k]=z[i]; z[i]=t; i++; } //exch.accept(k, i++);

      quickCoM3T9F3v3(z,lo, j);
      quickCoM3T9F3v3(z, i, hi);
  }

  public static <T extends Comparable<? super T>> void quickIt(T[] z) {
    // iterative quick sort for int[]s
    //based on iterativeQuicksort5 from http://kosbie.net/cmu/summer-08/15-100/handouts/IterativeQuickSort.java
    int[] range = new int[z.length+1];
    range[0] = z.length-1;
    int k, l, sortedCount = 0;
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
    // partition z[lo] to a[hi], assumes lo < hi
      if (lo == hi) return lo;
      int i = lo - 1, j = hi; T t;
//      BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
      while (true) {
          while (z[++i].compareTo(z[hi])<0) ; // z[hi] is sentinel
          while (z[hi].compareTo(z[--j])<0)  if (j == lo) break;        
          if (i >= j) break;              
          t = z[i]; z[i]=z[j]; z[j]=t;            
      }
      t = z[i]; z[i]=z[hi]; z[hi]=t;             
      return i;
    };
    while (sortedCount < z.length) {
      for (k=0; k<z.length; k++)
        if (range[k] >= k) {
          l = range[k];
          for ( ; k<=l; k++) {
            int p = partition.apply(k,l);
            sortedCount++;
            if (p > k) range[k] = p-1;
            if (p < l) range[p+1] = l;
            range[k = p] = -1; //mark sorted
          }
        }
        else {
          while ((l = range[k-range[k]]) < 0) range[k] += l;
          k += -range[k]-1;
        }
    }
 }
  
  public static <T extends Comparable<? super T>> void quickM3(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with median of 3 partitioning ex2318
    if (z == null || z.length < 2) return;
    Random r = null;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }   
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1, mid=lo+(j-lo)/2; T t, pivot;
      // median of 3 (lo,mid,hi)
      if (z[hi].compareTo(z[lo])<0)   { t = z[hi]; z[hi] = z[lo]; z[lo] = t; }
      if (z[mid].compareTo(z[lo])<0)  { t = z[mid]; z[mid] = z[lo]; z[lo] = t; }
      if (z[hi].compareTo(z[mid])<0)  { t = z[mid]; z[mid] = z[hi]; z[hi] = t; }
      t = z[mid]; z[mid] = z[lo]; z[lo] = t; 
      pivot = z[lo];     
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(pivot)<0) if (i == hi) break;
        while (pivot.compareTo(z[--j])<0) ;  //if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put pivot = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickM5(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with median of 5 partitioning ex2319
    if (z == null || z.length < 2) return;
    Random r = null;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }   
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1, mid=lo+(j-lo)/2, len=j-i, med; T t, pivot = z[lo];
      if (len < 10) {
      // median of 3 (lo,mid,hi)
      if (z[hi].compareTo(z[lo])<0)   { t = z[hi]; z[hi] = z[lo]; z[lo] = t; }
      if (z[mid].compareTo(z[lo])<0)  { t = z[mid]; z[mid] = z[lo]; z[lo] = t; }
      if (z[hi].compareTo(z[mid])<0)  { t = z[mid]; z[mid] = z[hi]; z[hi] = t; }
      t = z[mid]; z[mid] = z[lo]; z[lo] = t; 
      pivot = z[lo];
      }
      else if (len >= 10) { // use median of 5
        int i1=lo, j1=lo+(j-lo)/4, k = mid, l1=hi-len/4, m=hi, tm; // i  j  k   l  m        
        if(z[i1].compareTo(z[i])<0)  { tm = i1; i1 = j1; j1 = tm; }
        if(z[l1].compareTo(z[k])<0)  { tm = k; k = l1; l1 = tm; }
        if(z[k].compareTo(z[i1])<0)  { tm = j1; j1 = l1; l1 = tm; k=i1; }
        i1=m;
        if(z[j1].compareTo(z[i1])<0) {tm = i1; i1 = j1; j1 = tm; }
        if(z[i1].compareTo(z[k])<0)  {tm = j1; j1 = l1; l1 = tm; i1=k; }
        med = z[l1].compareTo(z[i1])<0 ? l1 : i1;
        t = z[med]; z[med] = z[lo]; z[lo] = t;
        pivot = z[lo];
      }      
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(pivot)<0) if (i == hi) break;
        while (pivot.compareTo(z[--j])<0) ;  //if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put pivot = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickCoM5(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with median of 5 partitioning ex2319 and
    // cutoff to insertion for subarray lengths < 10
    if (z == null || z.length < 2) return;
    Random r = null;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }   
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into z[lo..i-1], z[i], z[i+1..hi]. p291
      int i = lo, j = hi+1, mid=lo+(j-lo)/2, len=j-i, med; T t, pivot = z[lo];
      // use median of 5
      int i1=lo, j1=lo+(j-lo)/4, k = mid, l1=hi-len/4, m=hi, tm; // i  j  k   l  m        
      if(z[i1].compareTo(z[i])<0)  { tm = i1; i1 = j1; j1 = tm; }
      if(z[l1].compareTo(z[k])<0)  { tm = k; k = l1; l1 = tm; }
      if(z[k].compareTo(z[i1])<0)  { tm = j1; j1 = l1; l1 = tm; k=i1; }
      i1=m;
      if(z[j1].compareTo(z[i1])<0) {tm = i1; i1 = j1; j1 = tm; }
      if(z[i1].compareTo(z[k])<0)  {tm = j1; j1 = l1; l1 = tm; i1=k; }
      med = z[l1].compareTo(z[i1])<0 ? l1 : i1;
      t = z[med]; z[med] = z[lo]; z[lo] = t;
      pivot = z[lo];        
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(pivot)<0) if (i == hi) break;
        while (pivot.compareTo(z[--j])<0) ;  //if (j == lo) break;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put pivot = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      if (hi-lo<9) { //cutoff to insertion sort
        T t;
        for (int i = lo; i <= hi; i++)
          for (int j = i; j > lo && z[j].compareTo(z[j-1])<0; j--)
            { t = z[j]; z[j]=z[j-1]; z[j-1] = t; }
        return;
      }
      int j = partition.apply(lo, hi);
      self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickSen(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with sentinels for ex2317
    if (z == null || z.length < 2) return;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      Random r = null;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
//    int maxi = indexOfMax(z);  // index of max for right sentinel
//    if (maxi != z.length-1) exch.accept(maxi,z.length-1);
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo,  j = hi; // left and right scan indices
      int middle = lo + (hi - lo) / 2;
      T pivot = z[middle];
      T v = z[lo]; T t;
      while (i<j) { // Scan right, scan left, check for scan complete, and swap.
        while (z[i].compareTo(pivot)<0)  //if (i == hi) break;
          i++; 
        while (pivot.compareTo(z[j])<0) 
          j--;
        if (j==i) break;
        exch.accept(i,j);
      }
//      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(0, n-1);
  }
  
  public static <T extends Comparable<? super T>> void quickSen2(T[] z, boolean shuffle) {
    // quicksort algorithm 2.5 p289 with sentinels for ex2317
    if (z == null || z.length < 2) return;
    BiConsumer<Integer,Integer> exch = (a,b) -> { T t = z[a]; z[a] = z[b]; z[b] = t; };
    int n = z.length, l;
    if (shuffle) {
      Random r = null;
      try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      for (int k=n; k>1; k--) { l=r.nextInt(k); exch.accept(k-1,l); }
    }
//    int maxi = indexOfMax(z);  // index of max for right sentinel
//    if (maxi != z.length-1) exch.accept(maxi,z.length-1);
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo,  j = hi+1; // left and right scan indices
      T v = z[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (z[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(z[--j])<0) ;
        if (i >= j) break;
        exch.accept(i,j);
      }
      exch.accept(lo,j); // Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer2<Integer,Integer> sort = Recursive.consumer2((lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(lo, hi);
      self.accept(lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(0, n-1);
  }
  
  public static <T> void quick(T[] z, Comparator<T> c) {
    // quicksort algorithm 2.5 p289 comparator version for ex 2305 p303
    if (z == null || z.length < 2) return;
    Random r = null;
    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    int n = z.length, l; T t1;
    // shuffle with Fisher-Yates
    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<T[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = a[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (c.compare(a[++i],v)<0) if (i == hi) break;
        while (c.compare(v,a[--j])<0) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;// Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(a, j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(z, 0, n-1);
  }
  
  public static  <T extends Comparable<? super T>> void quick3Way(T[] z) {
    //entropy-optimal by J. Bentley and D. McIlroy p299
    if (z == null || z.length < 2) return;
    int n = z.length, l; //T t1;
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    BiConsumer<Integer,Integer> exch = (p,q) -> { T t = z[p]; z[p] = z[q]; z[q] = t; };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int lt = lo, i = lo+1, gt = hi, t;
      T v = a[lo];
      while (i <= gt) {
        int cmp = z[i].compareTo(v);
        if (cmp < 0) exch.accept(lt++, i++);
        else if (cmp > 0) exch.accept(i, gt--);
        else i++;
      } // Now a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
      self.accept(a, lo, lt - 1);
      self.accept(a, gt + 1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static  <T extends Comparable<? super T>> void quick3Way1stPartitionTrace(T[] z) {
    //entropy-optimal by J. Bentley and D. McIlroy p299
    if (z == null || z.length < 2) return;
    int n = z.length, l; //T t1;
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    System.out.printf("  %2s  %2s  %2s   %s\n", "lt", "i", "gt", indices(z));
//    System.out.println(iv+blnk2+"     "+show(z));
    BiConsumer<Integer,Integer> exch = (p,q) -> { T t = z[p]; z[p] = z[q]; z[q] = t; };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int lt = lo, i = lo+1, gt = hi, t;
      String sp1 = "   ";
      System.out.printf("  %2d  %2d  %2d%s%s\n", lt, lt, gt, sp1, show(z));
      T v = a[lo];
      while (i <= gt) {
        int cmp = z[i].compareTo(v);
        if (cmp < 0) {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          exch.accept(lt++, i++);
        }
        else if (cmp > 0) {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          exch.accept(i, gt--);
        }
        else {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          i++;
        }
      } // Now a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
      
//      self.accept(a, lo, lt - 1);
//      self.accept(a, gt + 1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static  <T extends Comparable<? super T>> void quick3WayTrace(T[] z) {
    //entropy-optimal by J. Bentley and D. McIlroy p299
    if (z == null || z.length < 2) return;
    int n = z.length, l; //T t1;
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    System.out.printf("  %2s  %2s  %2s   %s\n", "lt", "i", "gt", indices(z));
//    System.out.println(iv+blnk2+"     "+show(z));
    BiConsumer<Integer,Integer> exch = (p,q) -> { T t = z[p]; z[p] = z[q]; z[q] = t; };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int lt = lo, i = lo+1, gt = hi, t;
      String sp1 = "   ";
      System.out.printf("  %2d  %2d  %2d%s%s\n", lt, lt, gt, sp1, show(z));
      T v = a[lo];
      while (i <= gt) {
        int cmp = z[i].compareTo(v);
        if (cmp < 0) {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          exch.accept(lt++, i++);
        }
        else if (cmp > 0) {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          exch.accept(i, gt--);
        }
        else {
          System.out.printf("  %2d  %2d  %2d%s%s\n", lt, i, gt, sp1, show(z,i));
          i++;
        }
      } // Now a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
      
      self.accept(a, lo, lt - 1);
      self.accept(a, gt + 1, hi);
    });
    sort.accept(z, 0, n-1);
    System.out.printf("               %s\n", show(z));
  }
  
  public static void quickInt(int[] z) {
    // quicksort algorithm 2.5 for int[]s.
    if (z == null || z.length < 2) return;
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static void quickInt3Way(int[] z) {
    //entropy-optimal by J. Bentley and D. McIlroy p299
    if (z == null || z.length < 2) return;
    int n = z.length;
//  Random r = null;
//  try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//  if (r == null) r = new Random(System.currentTimeMillis());
//  int l, t1;
//  // shuffle with Fisher-Yates
//  for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    BiConsumer<Integer,Integer> exch = (p,q) -> { int t = z[p]; z[p] = z[q]; z[q] = t; };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int lt = lo, i = lo+1, gt = hi, t;
      int v = a[lo];
      while (i <= gt) {
        int cmp =  a[i]-v; //a[i].compareTo(v);
        if (cmp < 0) exch.accept(lt++, i++);
        else if (cmp > 0) exch.accept(i, gt--);
        else i++;
      } // Now a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
      self.accept(a, lo, lt - 1);
      self.accept(a, gt + 1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static void quickIntIt(int[] z) {
    // iterative quick sort for int[]s
    //based on iterativeQuicksort5 from http://kosbie.net/cmu/summer-08/15-100/handouts/IterativeQuickSort.java
    int[] range = new int[z.length+1];
    range[0] = z.length-1;
    int k, l, sortedCount = 0;
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
    // partition z[lo] to a[hi], assumes lo < hi
      if (lo == hi) return lo;
      int i = lo - 1;
      int j = hi;
      BiConsumer<Integer,Integer> exch = (a,b) -> { int t = z[a]; z[a] = z[b]; z[b] = t; };
      while (true) {
          while (z[++i] < z[hi]) ; // z[hi] is sentinel, no need for "if (i == hi)" test  
          while (z[hi] < z[--j])  if (j == lo) break;        
          if (i >= j) break;              
          exch.accept(i, j);               
      }
      exch.accept(i, hi);             
      return i;
    };
    while (sortedCount < z.length) {
      for (k=0; k<z.length; k++)
        if (range[k] >= k) {
          l = range[k];
          for ( ; k<=l; k++) {
            int p = partition.apply(k,l);
            sortedCount++;
            if (p > k) range[k] = p-1;
            if (p < l) range[p+1] = l;
            range[k = p] = -1; //mark sorted
          }
        }
        else {
          while ((l = range[k-range[k]]) < 0) range[k] += l;
          k += -range[k]-1;
        }
    }
 }
  
  public static void quickIntItCo(int[] z) {
    // iterative quick sort for int[]s with cutoff to selection for subarrays length < 7
    //based on iterativeQuicksort5 from http://kosbie.net/cmu/summer-08/15-100/handouts/IterativeQuickSort.java
    int[] range = new int[z.length+1];
    range[0] = z.length-1;
    int k, l, sortedCount = 0;
    BiConsumer<Integer,Integer> exch = (a,b) -> { int t = z[a]; z[a] = z[b]; z[b] = t; };
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
    // partition z[lo] to a[hi], assumes lo < hi
      if (lo == hi) return lo;
      int i = lo - 1;
      int j = hi;
      while (true) {
          while (z[++i] < z[hi]) ; // z[hi] is sentinel, no need for "if (i == hi)" test  
          while (z[hi] < z[--j])  if (j == lo) break;        
          if (i >= j) break;              
          exch.accept(i, j);               
      }
      exch.accept(i, hi);             
      return i;
    };
    while (sortedCount < z.length) {
      for (k=0; k<z.length; k++)
        if (range[k] >= k) {
          l = range[k];
          if (l-k < 7) {
            // run selectionsort on indices z[i..j]
            for (int m=k; m<=l; m++) {
              for (int n=m; n>k && z[n-1]>z[n]; n--) exch.accept(n, n-1);
              range[m] = -((l+1)-m); sortedCount++;
            }
            k = l;
          }
          else {
            for ( ; k<=l; k++) {
              int p = partition.apply(k,l);
              sortedCount++;
              if (p > k) range[k] = p-1;
              if (p < l) range[p+1] = l;
              range[k = p] = -1; //mark sorted
            }
          }
        }
        else {
          while ((l = range[k-range[k]]) < 0) range[k] += l;
          k += -range[k]-1;
        }
    }
 }

  public static void quickIntItAe(int[] z) {
    // iterative quick sort for int[]s with compares accepting equal
    // based on iterativeQuicksort5 from http://kosbie.net/cmu/summer-08/15-100/handouts/IterativeQuickSort.java
    int[] range = new int[z.length+1];
    range[0] = z.length-1;
    int k, l, sortedCount = 0;
    Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
      // partition z[lo] to a[hi], assumes lo < hi
      if (lo == hi) return lo;
      int i = lo - 1;
      int j = hi;
      BiConsumer<Integer,Integer> exch = (a,b) -> { int t = z[a]; z[a] = z[b]; z[b] = t; };
      while (true) {
        while (z[++i] <= z[hi]) if (i == hi) break; // z[hi] no longer sentinel
        while (z[hi] <= z[--j])  if (j == lo) break;        
        if (i >= j) break;              
        exch.accept(i, j);               
      }
      exch.accept(i, hi);             
      return i;
    };
    while (sortedCount < z.length) {
      for (k=0; k<z.length; k++)
        if (range[k] >= k) {
          l = range[k];
          for ( ; k<=l; k++) {
            int p = partition.apply(k,l);
            sortedCount++;
            if (p > k) range[k] = p-1;
            if (p < l) range[p+1] = l;
            range[k = p] = -1; //mark sorted
          }
        }
        else {
          while ((l = range[k-range[k]]) < 0) range[k] += l;
          k += -range[k]-1;
        }
    }
  }  

  public static void quickIntAe(int[] z) {
    // quicksort algorithm 2.5 for int[]s accepts equal in compares
    if (z == null || z.length < 2) return;
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] <= v) if (i == hi) break;
        while (v <= a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static <T extends Comparable<? super T>> int[] quickRd(T[] z) {
    // quicksort algorithm 2.5 p289 no shuffle and returns recursive depth
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
    int[] rec = {0}; int[] max = {0};
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<T[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; // left and right scan indices
      T v = a[lo]; T t;
      while (true) { // Scan right, scan left, check for scan complete, and swap.
        while (a[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(a[--j])<0) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;// Put v = a[j] into position by swap
      return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      rec[0]++; if (rec[0] > max[0]) max[0] = rec[0];
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1); // Sort left part a[lo .. j-1].
      self.accept(a, j+1, hi); // Sort right part a[j+1 .. hi].
    });
    sort.accept(z, 0, n-1);
    return max;
  }
  
  public static <T extends Comparable<? super T>> int quickEl(T[] z) {
    // quicksort algorithm 2.5 returns number of times largest element is exchanged
    if (z == null || z.length < 2) return 0;
    T max = max(z); T[] w = z.clone();
    if (howManyOf(w,max) > 1) throw new IllegalArgumentException("quickEl: max not unique");
    int[] ex = {1}; // counter for #exchanges of max
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<T[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      T v = a[lo]; T t;
      while (true) {
        while (a[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(a[--j])<0) if (j == lo) break;
        if (i >= j) break;
        if (a[i].equals(max) || a[j].equals(max)) ex[0]++;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      if (a[lo].equals(max) || a[j].equals(max)) ex[0]++;
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return ex[0];
  }
  
  public static int quickIntEL(int[] z) {
    // quicksort algorithm 2.5 returns number of times largest element is exchanged
    if (z == null || z.length < 2) return 0;
    int max = max(z); int[] w = z.clone();
    if (howManyOf(w,max) > 1) throw new IllegalArgumentException("quickIntEL: max not unique");
    int[] ex = {0}; // counter for #exchanges of max
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
//        System.out.println("\nbefore swap");
//        par(z);
        if (a[i] == max || a[j] == max) ex[0]++;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
//        System.out.println("after swap");
//        par(z); System.out.println();
      }
//      System.out.println("\nbefore swap2");
//      par(z);
//      System.out.println("lo="+lo+" j="+j);
      if (lo != j && a[lo] == max || a[j] == max) ex[0]++;
      t = a[lo]; a[lo] = a[j]; a[j] = t;
//      System.out.println("after swap2");
//      par(z); System.out.println();
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
//      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return ex[0];
  }
  
  public static int quickIntC(int[] z) {
    // quicksort algorithm 2.5 returns number of compares (made in partition
    // and originally coded with less().
    if (z == null || z.length < 2) return 0;
    int[] c = {0}; // counter for #compares
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
//      int compares = c[0];
      int i = lo, j = hi+1; 
      int v = a[lo]; int t; //System.out.println("v="+v);
      while (true) {
        while (a[++i] < v) {
          c[0]++; //System.out.println("iInLoop="+i);
          if (i == hi) break;
        }
        c[0]++; 
        while (v < a[--j]) {
          c[0]++; //System.out.println("jInLoop="+j);
          if (j == lo) break;
        }
        c[0]++; //System.out.println("i="+i+" j="+j);
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
//        par(z);
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
//      System.out.println("lo="+lo+" hi="+hi+" subLen="+(hi-lo+1)+" compares="+(c[0]-compares)+" j="+j);
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
//      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return c[0];
  }
  
  public static int quickIntCt(int[] z) {
    // quicksort algorithm 2.5 returns number of compares that return true
    // and originally coded with less().
    if (z == null || z.length < 2) return 0;
    int[] c = {0}; // counter for #compares
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
//      int compares = c[0];
      int i = lo, j = hi+1; 
      int v = a[lo]; int t; //System.out.println("v="+v);
      while (true) {
        while (a[++i] < v) {
          c[0]++;
          if (i == hi) break;
        }
        while (v < a[--j]) {
          c[0]++; //System.out.println("jInLoop="+j);
          if (j == lo) break;
        }
        //System.out.println("i="+i+" j="+j);
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
//        par(z);
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
//      System.out.println("lo="+lo+" hi="+hi+" subLen="+(hi-lo+1)+" compares="+(c[0]-compares)+" j="+j);
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
//      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return c[0];
  }
  
  public static int[] quickIntCEP(int[] z) {
    // quicksort algorithm 2.5 returns number of compares, swaps and partitions.
    if (z == null || z.length < 2) return new int[]{-1};
    int[] c = {0,0,0}; // counter for #compares, #swaps and #partitions
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
//      int compares = c[0]; int swaps = c[1];
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) {
          c[0]++;
          if (i == hi) break;
        }
        c[0]++;
        while (v < a[--j]) {
          c[0]++;
          if (j == lo) break;
        }
        c[0]++;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; c[1]++; //swap
//        System.out.println("swap("+i+","+j+")");
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t; c[1]++;
//      System.out.println("swap("+lo+","+j+")");
//      System.out.println("subLen="+(hi-lo+1)+" compares="+(c[0]-compares)
//          +" swaps="+(c[1]-swaps)+" j="+j);
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      c[2]+=2;
//      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return c;
  }
  
  public static int[] quickIntAeCE(int[] z) {
    // quicksort algorithm 2.5 accepting equals in compares and 
    // returns number of compares, swaps and partitions
    if (z == null || z.length < 2) return new int[]{-1};
    int[] c = {0,0,0}; // counter for #compares, #swaps and #partitions
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
//      int compares = c[0]; int swaps = c[1];
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] <= v) {
          c[0]++;
          if (i == hi) break;
        }
        c[0]++;
        while (v <= a[--j]) {
          c[0]++;
          if (j == lo) break;
        }
        c[0]++;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; c[1]++; //swap
//        System.out.println("swap("+i+","+j+")");
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t; c[1]++;
//      System.out.println("swap("+lo+","+j+")");
//      System.out.println("subLen="+(hi-lo+1)+" compares="+(c[0]-compares)
//          +" swaps="+(c[1]-swaps)+" j="+j);
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      c[2]+=2;
//      par(z);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    
    return c;
  }
  
  public static int[] quickIntSub012(int[] z) {
    // quicksort algorithm 2.5 returns number of subarrays of lengths 0, 1 and 2
    // for ex2307 p303.
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
    List<Integer> list = new ArrayList<>();
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      list.add(hi - lo);
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    int zero = 0, one = 0, two = 0;
    for (Integer v : list) {
      if (v < 0) zero++;
      else if (v == 0)  one++;
      else if (v == 1) two++;
    }
    return new int[]{zero,one,two};
  }
  
  public static int[] quickIntPSub012(int[] z) {
    // quicksort algorithm 2.5 returns number of subarrays of lengths 0, 1 and 2
    // and the number of partitions.
    // for ex2307 p303.
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
    int[] c = {0}; // counter for number of partitions
    List<Integer> list = new ArrayList<>();
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      list.add(hi - lo);
      if (hi <= lo) return;
      c[0]++;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    int zero = 0, one = 0, two = 0;
    for (Integer v : list) {
      if (v < 0) zero++;
      else if (v == 0)  one++;
      else if (v == 1) two++;
    }
    return new int[]{c[0],zero,one,two};
  }
  
  public static int[] quickIntAePSub012(int[] z) {
    // quicksort algorithm 2.5 accept equals in compares and returns the total
    // numbers of partitions and subarrays of lengths 0, 1 and 2.
    // for ex2307 p303.
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
    int[] c = {0}; // counter for number of partitions
    List<Integer> list = new ArrayList<>();
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] <= v) if (i == hi) break;
        while (v <= a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      list.add(hi - lo);
      if (hi <= lo) return;
      c[0]+=2;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    int zero = 0, one = 0, two = 0;
    for (Integer v : list) {
      if (v < 0) zero++;
      else if (v == 0)  one++;
      else if (v == 1) two++;
    }
    return new int[]{c[0],zero,one,two};
  }
  
  public static int[] quickIntAePSub0(int[] z) {
    // quicksort algorithm 2.5 accept equals in compares and return number of
    // partitions and  subarrays of length 0.
    // for ex2307 p303.
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
//    List<Integer> list = new ArrayList<>();
    int[] c = {0,0}; //{ partitions, subArrs0 }
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] <= v) if (i == hi) break;
        while (v <= a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if ((hi - lo) < 0) c[1]++;
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      c[0]+=2;
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
//    int zero = 0, one = 0, two = 0;
//    for (Integer v : list) {
//      if (v < 0) zero++;
//      else if (v == 0)  one++;
//      else if (v == 1) two++;
//    }
    return c;
  }
  
  public static int[] quickIntRd(int[] z) {
    // quicksort algorithm 2.5 for int[]s return recursive depth
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length; 
    int[] rec = {0}; int[] max = {0};
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<int[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      int v = a[lo]; int t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<int[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      rec[0]++; if (rec[0] > max[0]) max[0] = rec[0];
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    return max;
  }
  
  public static void quickDbl(double[] z) {
    // quicksort algorithm 2.5 for double[]s.
    if (z == null || z.length < 2) return;
    int n = z.length; 
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<double[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      double v = a[lo]; double t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<double[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
  }
  
  public static int[] quickDblRd(double[] z) {
    // quicksort algorithm 2.5 for double[]s returns recursive depth
    if (z == null || z.length < 2) return new int[]{-1};
    int n = z.length;
    int[] rec = {0}; int[] max = {0};
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l, t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<double[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      // Partition into a[lo..i-1], a[i], a[i+1..hi]. p291
      int i = lo, j = hi+1; 
      double v = a[lo]; double t;
      while (true) {
        while (a[++i] < v) if (i == hi) break;
        while (v < a[--j]) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t; //swap
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j; 
    };
    Consumer3<double[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      if (hi <= lo) return;
      rec[0]++; if (rec[0] > max[0]) max[0] = rec[0];
      int j = partition.apply(a, lo, hi);
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
      rec[0]--;
    });
    sort.accept(z, 0, n-1);
    return max;
  }
 
  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  public static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (a[i].compareTo(a[i-1])<0) return false;
    return true;
  }
  
  public static boolean isSorted(int[] a) {
    return isSorted(a, 0, a.length - 1);
  } 
  
  public static boolean isSorted(int[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  public static boolean isSorted(double[] a) {
    return isSorted(a, 0, a.length - 1);
  } 
  
  public static boolean isSorted(double[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (a[i] < a[i-1]) return false;
    return true;
  }
  
  private static <T extends Comparable<? super T>> String indices(T[] a) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < a.length; i++) {
      if (i >= 9) sb.append(i + "  ");
      else sb.append(i + "   ");
    }
    return sb.toString();
  }
  
  public static <T extends Comparable<? super T>> String show(T[] a) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < a.length; i++)
      sb.append(a[i] + "   ");
    return sb.toString();
  }
  
  public static <T extends Comparable<? super T>> String show(T[] a, int x) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < a.length; i++) {
      if (i == x) {
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        sb.append("["+a[i]+"]  ");
      }
      else sb.append(a[i] + "   ");
    }
    return sb.toString();
  }
  
  public static <T extends Comparable<? super T>> int partitionTrace(T[] z) {
    // print quick sort partition trace for ex2301 p303
    if (z == null ) return -1; if (z.length == 1) return 0;
    int n = z.length;
    int[] c = {0};
    String blnk = "                     ";
    String iv = "       initial values";
    String as = "        after shuffle";
    String slsr = "scan left, scan right";
    String ex = "             exchange";
    String fex = "       final exchange";
    String res = "               result";
    String sp;
    System.out.println(blnk+"  "+"i"+"   "+"j"+"   "+indices(z)+"\n");
    System.out.println(iv+"  "+0+"  "+(n-1)+"   "+show(z)+"\n");
    // shuffle
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    int lo = 0; int hi = z.length-1;
    int i = lo, j = hi+1;
    T v = z[lo]; T t;
    while (true) {
      while (z[++i].compareTo(v)<0) {
        c[0]++; if (i == hi) break;
      }
      c[0]++; 
      while (v.compareTo(z[--j])<0) {
        c[0]++; if (j == lo) break;
      }
      c[0]++; 
      if (i >= j) {
        System.out.println("scan break at i="+i+" j="+j);
        break;
      }
      if (i <= 9 && j <= 9) sp="   ";
      else if (i <= 9 && j > 9 || i > 9 && j <= 9) sp="  ";
      else sp=" ";
      System.out.println(slsr+"  "+i+sp+j+"   "+show(z)+"\n");
      t = z[i]; z[i] = z[j]; z[j] = t;
      System.out.println(ex+"  "+i+sp+j+"   "+show(z)+"\n");
    }
    t = z[lo]; z[lo] = z[j]; z[j] = t;
    if (lo <= 9 && j <= 9) sp="   ";
    else if (lo <= 9 && j > 9 || lo > 9 && j <= 9) sp="  ";
    else sp=" ";
    System.out.println(fex+"  "+lo+sp+j+"   "+show(z)+"\n");
    System.out.println(res+"  "+lo+sp+j+"   "+show(z)+"\n");
    return c[0];
  }
  
  public static <T extends Comparable<? super T>> int partitionTraceAE(T[] z, int a, int b) {
    // print quick sort partition trace for ex2301 p303
    if (z == null ) return -1; if (z.length == 1) return 0;
    T[] w = z.clone();
    int[] c= {0,0}; // compares,swaps
    int n = z.length;
    String blnk = "                     ";
    String iv = "       initial values";
    String as = "        after shuffle";
    String slsr = "scan left, scan right";
    String ex = "             exchange";
    String fex = "       final exchange";
    String res = "               result";
    String sp;
//    System.out.println(blnk+"  "+"i"+"   "+"j"+"   "+indices(z)+"\n");
//    System.out.println(iv+"  "+0+"  "+(n-1)+"   "+show(z)+"\n");
    // shuffle
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    int lo = 0; int hi = z.length-1;
    int i = lo, j = hi+1; System.out.println("i=lo="+i+" j=hi+1="+j);
    T p = z[lo]; T t; System.out.println("p="+p+" (value of partition element z[lo])");
    while (true) {
//      System.out.println("ind "+i+" "+j);
      System.out.println("comparext1 "+z[i+1]+" to p (i="+(i+1)+")");
      while (z[++i].compareTo(p)<=0) {
        System.out.println("compare1 "+z[i]+" to p (i="+i+")");
        c[0]++; if (i == hi) break; 
      }
      c[0]++; 
      System.out.println("comparext2 p to "+z[j-1]+" (j="+(j-1)+")");
      while (p.compareTo(z[--j])<=0) {
        System.out.println("compare2 p to "+z[j]);
        c[0]++; if (j == lo) break;
      }
      c[0]++; 
      if (i >= j) {
        System.out.println("scan break at i="+i+" j="+j);
        break;
      }
//      if (i <= 9 && j <= 9) sp="   ";
//      else if (i <= 9 && j > 9 || i > 9 && j <= 9) sp="  ";
//      else sp=" ";
//      System.out.println(slsr+"  "+i+sp+j+"   "+show(z)+"\n");
      t = z[i]; z[i] = z[j]; z[j] = t; System.out.println("swap i="+i+" j="+j); c[1]++;
//      System.out.println(ex+"  "+i+sp+j+"   "+show(z)+"\n");
    }
    t = z[lo]; z[lo] = z[j]; z[j] = t; 
    System.out.println("final swap lo="+lo+" j="+j); c[1]++;
    System.out.println("compares="+c[0]+" swaps="+c[1]);
    if (lo <= 9 && j <= 9) sp="   ";
    else if (lo <= 9 && j > 9 || lo > 9 && j <= 9) sp="  ";
    else sp=" ";
    System.out.println("ini: "+arrayToString(w,-1));
    System.out.println("fin: "+arrayToString(z,-1));

//    System.out.println(fex+"  "+lo+sp+j+"   "+show(z)+"\n");
//    System.out.println(res+"  "+lo+sp+j+"   "+show(z));
    return c[0];
  }
  
  public static <T extends Comparable<? super T>> void quickTrace(T[] z) {
    // print quicksort algorithm 2.5 p289 trace for ex23202 p303
    if (z == null || z.length < 2) return;
    int n = z.length;
    String blnk = "                     ";
    String blnk2 = "         ";
    String iv = " initial values ";
    String as = "       random shuffle";
    String res = " result         ";
    String sp;
    System.out.printf("                 %2s  %2s  %2s   %s\n", "lo", "j", "hi", indices(z));
    System.out.println(iv+blnk2+"     "+show(z)+"\n");
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<T[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      int i = lo, j = hi+1; 
      T v = a[lo]; T t;
      while (true) {
        while (a[++i].compareTo(v)<0) if (i == hi) break;
        while (v.compareTo(a[--j])<0) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t;
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j;
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      String sp1 = lo == 0 ? "  " : "   ";
      if (hi <= lo) {
        if (hi == lo)
          System.out.printf("                 %2d      %2d%s%s\n", lo, lo, sp1, show(z,lo));
        return;
      }
      int j = partition.apply(a, lo, hi);
      sp1 = j == 0 ? " " : "   ";
      System.out.printf("                 %2d  %2d  %2d%s%s\n", lo, j, hi, sp1, show(z,j));
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    System.out.println("\n"+res+blnk2+"     "+show(z)+"\n");
  }
  
  public static <T extends Comparable<? super T>> void quickTraceAE(T[] z) {
    // print quicksort algorithm 2.5 p289 trace accepting equals in compares
    if (z == null || z.length < 2) return;
    int n = z.length;
    String blnk = "                     ";
    String blnk2 = "         ";
    String iv = " initial values ";
    String as = "       random shuffle";
    String res = " result         ";
    String sp;
    System.out.printf("                 %2s  %2s  %2s   %s\n", "lo", "j", "hi", indices(z));
    System.out.println(iv+blnk2+"     "+show(z)+"\n");
//    Random r = null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    if (r == null) r = new Random(System.currentTimeMillis());
//    int n = z.length, l; T t1;
//    // shuffle with Fisher-Yates
//    for (int k=n; k>1; k--) { l=r.nextInt(k); t1=z[k-1]; z[k-1]=z[l]; z[l]=t1; }
    Function3<T[],Integer,Integer,Integer> partition = (a,lo,hi) -> {
      int i = lo, j = hi+1; 
      T v = a[lo]; T t;
      while (true) {
        while (a[++i].compareTo(v)<=0) if (i == hi) break;
        while (v.compareTo(a[--j])<=0) if (j == lo) break;
        if (i >= j) break;
        t = a[i]; a[i] = a[j]; a[j] = t;
      }
      t = a[lo]; a[lo] = a[j]; a[j] = t;
      return j;
    };
    Consumer3<T[],Integer,Integer> sort = Recursive.consumer3((a,lo,hi,self) -> {
      String sp1 = lo == 0 ? "  " : "   ";
      if (hi <= lo) {
        if (hi == lo)
          System.out.printf("                 %2d      %2d%s%s\n", lo, lo, sp1, show(z,lo));
        return;
      }
      int j = partition.apply(a, lo, hi);
      sp1 = j == 0 ? " " : "   ";
      System.out.printf("                 %2d  %2d  %2d%s%s\n", lo, j, hi, sp1, show(z,j));
      self.accept(a, lo, j-1);
      self.accept(a, j+1, hi);
    });
    sort.accept(z, 0, n-1);
    System.out.println("\n"+res+blnk2+"     "+show(z)+"\n");
  }
  
  public static void printNumberOfExchangesOfMaxElement() {
    // print the number of exchanges of max element for int arrays
    // with ranges [0,1) to [0,13)
    System.out.println("  arrayLength    #exchangesOfMaxElement");
    for (int i = 1; i<22; i++) {
      int max = -1;
      int [] a = range(0,i);
      int n = a.length;
      Iterator<int[]> it =  permutations(a);
      while (it.hasNext()) {
        int[] q = it.next();
        int m=quickIntEL(q);   
        if (m > max) max = m;
      }
      System.out.printf("  %2d            %2d\n", n, max);
    }
  }
  
  public static void printArraysOfLength10WithMaxNumberOfCompares() {
    // print the number of compares for each permutation of range(1,11).
    int[] z = range(1,11);
    int n = z.length; int max = Integer.MIN_VALUE; double lim = (1.*n*n)/2;
    Iterator<int[]> it =  permutations(z); int c = 0;
    while (it.hasNext()) {
      int[] q = it.next();
      int[] p = q.clone();
      int v=quickIntC(q); 
      if (v == 63) { par(p); c++; }
    }
    System.out.println("total number of arrays found="+c);
  }
  
  public static long algo2pt5NumComparesWithEqualItems(int n) {
    // return the number of compares done by algorithm 2.5 when the
    // input array has all equal elements. For exercise2308 p303.
    if (n <= 1) return 0;
    else return n + n%2 + algo2pt5NumComparesWithEqualItems(n/2) +
        algo2pt5NumComparesWithEqualItems((n-1)/2);
  }
  
  public static long ncei(int n) {
    return algo2pt5NumComparesWithEqualItems(n);
  }
  
  @SafeVarargs
  public static void printPermutationsStats(int[]...t) {
    // print average compares, exchanges and partitions of all permutations of each
    // array in t using quick sort algo2.5
    System.out.println("                                          over all unique permutations of a[]");
    //                                    [1,1,1]          4.000          2.000          6.000            2.000

    System.out.println("                       a[]   meanCompares  meanExchanges  compares+Exchanges  partitions");
    for (int i = 0; i < t.length; i++) {
      int[] z = t[i];
      List<Integer> compares = new ArrayList<>();
      List<Integer> exchanges = new ArrayList<>();
      List<Integer> partitions = new ArrayList<>();
      Iterator<int[]> it = permutations(z);
      Map<Integer,int[]> map = new HashMap<>();
      while (it.hasNext()) { int[] x = it.next(); map.put(Arrays.hashCode(x),x); }
      for (Integer y : map.keySet()) {
        int[] d = quickIntCEP(map.get(y));
        compares.add(d[0]); exchanges.add(d[1]); partitions.add(d[2]);
      }
      double mc = mean(compares.toArray(new Integer[0]));
      double me = mean(exchanges.toArray(new Integer[0])); 
      double mp = mean(partitions.toArray(new Integer[0])); 
      System.out.printf(" %25s        %7.3f        %7.3f        %7.3f        %7.3f\n",
          arrayToString(z,-1), mc, me, mc+me, mp);
    }
  }

  public static void minCompares(int[] z) {
    // print min compares over all permutations of z using quick sort algo2.5
    int min = Integer.MAX_VALUE; int[] minArr = null;
    List<Integer> compares = new ArrayList<>();
    Iterator<int[]> it = permutations(z);
    Map<Integer,int[]> map = new HashMap<>();
    while (it.hasNext()) { int[] x = it.next(); map.put(Arrays.hashCode(x),x); }
    for (Integer y : map.keySet()) {
      int[] d = quickIntCEP(map.get(y));
      if (d[0] < min) { min = d[0]; minArr = map.get(y); }
    }
    System.out.println("min compares = "+min+" for "+arrayToString(minArr));
  }
  
  public static void maxCompares(int[] z) {
    // print max compares over all permutations of z using quick sort algo2.5
    int max = Integer.MIN_VALUE; int[] maxArr = null;
    List<Integer> compares = new ArrayList<>();
    Iterator<int[]> it = permutations(z);
    Map<Integer,int[]> map = new HashMap<>();
    while (it.hasNext()) { int[] x = it.next(); map.put(Arrays.hashCode(x),x); }
    for (Integer y : map.keySet()) {
      int[] x = map.get(y).clone();
      int[] d = quickIntCEP(map.get(y));
      if (d[0] > max) { max = d[0]; maxArr = x; }
    }
    System.out.println("max compares = "+max+" for "+arrayToString(maxArr));
  }
  
  @SuppressWarnings("unchecked")
  public static void compareQuickSortIntAlgs(String alg1, String alg2, int x, int trials) {
    // compare run time performance of alg1 with alg2 with int[] arrays populated
    // randomly with ints from [0,x) and using array lengths starting at 2^8 and doubling
    // to 2^15 with each length tested trials times and printing the average of the 
    // resulting times along with ratios based on times for the previous array size.
    Consumer<int[]>[] cons = (Consumer<int[]>[])newInstance(Consumer.class,2);
    Consumer2<Integer[],Boolean>[] consb = (Consumer2<Integer[],Boolean>[])newInstance(Consumer2.class,2);
    String[] algs = {alg1,alg2};  
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "quickInt": 
          Consumer<int[]> m01 = (a) -> quickInt(a); 
          cons[i] = m01; break;
        case "quickIntAe": 
          Consumer<int[]> m02 = (a) -> quickIntAe(a); 
          cons[i] = m02; break; 
        case "quickIntIt":
          Consumer<int[]> m03 = (a) -> quickIntIt(a); 
          cons[i] = m03; break;
        case "quickIntItAe":
          Consumer<int[]> m04 = (a) -> quickIntIt(a); 
          cons[i] = m04; break;
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; int[] d, d2; int c = 0;
    Timer t = new Timer(); long time1, time2, prev1 = 0, prev2 = 0;
//    int[] len = {256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144}; // array lengths 
//    System.out.println();
//    String[] lens = {"2^8","2^9","2^10","2^11","2^12","2^13","2^14","2^15","2^16","2^17","2^18"};
    int len = 128; int lens = 7; String slen = "2^"+lens;
    Function<Integer,String> getSlen = (y) -> { return "2^"+y; };
    System.out.println("    "+alg1+" vs "+alg2+" average times over "+trials+" trials "
        +"for random int[]s with keys in [0.."+(x-1)+"]");
//    System.out.println("    array                  average times (ms)");
    System.out.printf("    array length  %14s    %14s    %14s    %14s\n",alg1,"time/prev",alg2, "time/prev");
    while (true) {
      len*=2; lens+=1; slen = getSlen.apply(lens);
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = r.ints(len,0, x).toArray();
        d2 = d.clone();
        if (c % 2 == 0) {
          t.reset();
          cons[0].accept(d);
          time1 += t.num();
        } else {
          t.reset();
          cons[1].accept(d);
          time2 += t.num();
        }
        if (c % 2 == 0) {
          t.reset();
          cons[1].accept(d2);          
          time2 += t.num();
        } else {
          t.reset();
          cons[0].accept(d2);
          time1 += t.num();
        }
      }
//      System.out.printf("    %6s           %10.3f        %10.3f\n",
//          lens[k], 1.*time1/trials, 1.*time2/trials);
      if (c == trials) {
        System.out.printf("    %6s           %10.3f        %10s        %10.3f        %10s\n",
            slen, 1.*time1/trials, "NA", 1.*time2/trials, "NA");
      } else {
        System.out.printf("    %6s           %10.3f        %10.3f        %10.3f        %10.3f\n",
            slen, 1.*time1/trials, (1.*time1/prev1), 
            1.*time2/trials, (1.*time2/prev2));
      }
      prev1 = time1; prev2 = time2;
    }

  }
  
  @SuppressWarnings("unchecked")
  public static void compareQuickSortIntegerAlgs(String alg1, String alg2, int x, int trials) {
    // compare run time performance of alg1 with alg2 with int[] arrays populated
    // randomly with ints from [0,x) and using array lengths starting at 2^8 and doubling
    // to 2^15 with each length tested trials times and printing the average of the 
    // resulting times along with ratios based on times for the previous array size.
    Consumer2<Integer[],Boolean>[] cons = (Consumer2<Integer[],Boolean>[])newInstance(Consumer2.class,2);
    String[] algs = {alg1,alg2};  
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
      case "quick":
        Consumer2<Integer[],Boolean> m01 = (a,b) -> quick(a,b); 
        cons[i] = m01; break;
      case "quickSen":
        Consumer2<Integer[],Boolean> m02 = (a,b) -> quickSen2(a,b); 
        cons[i] = m02; break;
      case "quickM3":
        Consumer2<Integer[],Boolean> m03 = (a,b) -> quickM3(a,b); 
        cons[i] = m03; break;
      case "quickM5":
        Consumer2<Integer[],Boolean> m04 = (a,b) -> quickM5(a,b); 
        cons[i] = m04; break;
      case "quickCoM5":
        Consumer2<Integer[],Boolean> m05 = (a,b) -> quickCoM5(a,b); 
        cons[i] = m05; break;
      case "quickIt":
        Consumer2<Integer[],Boolean> m06 = (a,b) -> quickIt(a); 
        cons[i] = m06; break;
      case "quickF3":
        Consumer2<Integer[],Boolean> m07 = (a,b) -> quickF3(a,b); 
        cons[i] = m07; break;
      case "quickCoM3T9F3v1":
        Consumer2<Integer[],Boolean> m08 = (a,b) -> quickCoM3T9F3v1(a,b); 
        cons[i] = m08; break;
      case "quickCoM3T9F3v2":
        Consumer2<Integer[],Boolean> m14 = (a,b) -> quickCoM3T9F3v2(a,b); 
        cons[i] = m14; break;
      case "quickCoM3T9F3v3":
        Consumer2<Integer[],Boolean> m15 = (a,b) -> quickCoM3T9F3v3(a,b); 
        cons[i] = m15; break;
      case "quickX":
        Consumer2<Integer[],Boolean> m09 = (a,b) -> QuickX.sort(a); 
        cons[i] = m09; break;
      case "Arrays.sort":
        Consumer2<Integer[],Boolean> m10 = (a,b) -> Arrays.sort(a); 
        cons[i] = m10; break;
      case "shell":
        Consumer2<Integer[],Boolean> m11 = (a,b) -> shellSortWithIncrementArray(a); 
        cons[i] = m11; break;
      case "mergeTopDownAcCoSm32":
        Consumer2<Integer[],Boolean> m12 = (a,b) -> topDownAcCoSm32(a); 
        cons[i] = m12; break; 
      case "mergeTopDownAcCoSm8":
        Consumer2<Integer[],Boolean> m13 = (a,b) -> topDownAcCoSm8(a); 
        cons[i] = m13; break;
      case "sampleSort2":
        Consumer2<Integer[],Boolean> m16 = (a,b) -> sampleSort2(a,3,false); 
        cons[i] = m16; break;
      case "sampleSortInteger2":
        Consumer2<Integer[],Boolean> m17 = (a,b) -> sampleSortInteger2(a,2,false); 
        cons[i] = m17; break;
      case "sampleSort":
        Consumer2<Integer[],Boolean> m18 = (a,b) -> sampleSort(a,false); 
        cons[i] = m18; break; 
      case "heap":
        Consumer2<Integer[],Boolean> m19 = (a,b) -> Heap.sort(a); 
        cons[i] = m19; break; 
      case "heapRaw":
        Consumer2<Integer[],Boolean> m20 = (a,b) -> HeapRaw.sort(a); 
        cons[i] = m20; break; 
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; Integer[] d, d2; int c = 0;
    Timer t = new Timer(); long time1, time2, prev1 = 0, prev2 = 0;
//    int[] len = {256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144}; // array lengths 
//    System.out.println();
//    String[] lens = {"2^8","2^9","2^10","2^11","2^12","2^13","2^14","2^15","2^16","2^17","2^18"};
    int len = 128; int lens = 7; String slen = "2^"+lens;
    Function<Integer,String> getSlen = (y) -> { return "2^"+y; };
    System.out.println("    "+alg1+" vs "+alg2+" average times over "+trials+" trials "
        +"for random Integer[]s with keys in [0.."+(x-1)+"]");
//    System.out.println("    array                  average times (ms)");
    System.out.printf("    array length  %14s    %14s    %14s    %14s\n",alg1,"time/prev",alg2, "time/prev");
    while (true) {
      len*=2; lens+=1; slen = getSlen.apply(lens);
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = new Integer[len];
        for (int j = 0; j < len; j++) d[j] = r.nextInt(x);
        d2 = d.clone();
        if (c % 2 == 0) {
          t.reset();
          cons[0].accept(d,false);
          time1 += t.num();
        } else {
          t.reset();
          cons[1].accept(d,false);
          time2 += t.num();
        }
        if (c % 2 == 0) {
          t.reset();
          cons[1].accept(d2,false);          
          time2 += t.num();
        } else {
          t.reset();
          cons[0].accept(d2,false);
          time1 += t.num();
        }
      }
//      System.out.printf("    %6s           %10.3f        %10.3f\n",
//          lens[k], 1.*time1/trials, 1.*time2/trials);
      if (c == trials) {
        System.out.printf("    %6s           %10.3f        %10s        %10.3f        %10s\n",
            slen, 1.*time1/trials, "NA", 1.*time2/trials, "NA");
      } else {
        System.out.printf("    %6s           %10.3f        %10.3f        %10.3f        %10.3f\n",
            slen, 1.*time1/trials, (1.*time1/prev1), 
            1.*time2/trials, (1.*time2/prev2));
      }
      prev1 = time1; prev2 = time2;
    }

  }
  
  @SuppressWarnings("unchecked")
  public static  void compareQuickSortDoubleAlgs2(String alg1, String alg2, int trials) {
    // compare run time performance of alg1 with alg2 with int[] arrays populated
    // randomly with Doubles from [0,1) and array lengths starting at 2^8 and doubling
    // forever with each length tested trials times and printing the average of the 
    // resulting times along with ratios based on times for the previous array size.
    Consumer2<Double[],Boolean>[] cons = (Consumer2<Double[],Boolean>[])newInstance(Consumer2.class,2);
    String[] algs = {alg1,alg2};  
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "quick":
          Consumer2<Double[],Boolean> m01 = (a,b) -> quick(a,b); 
          cons[i] = m01; break;
        case "quickSen":
          Consumer2<Double[],Boolean> m02 = (a,b) -> quickSen2(a,b); 
          cons[i] = m02; break;
        case "quickM3":
          Consumer2<Double[],Boolean> m03 = (a,b) -> quickM3(a,b); 
          cons[i] = m03; break;
        case "quickM5":
          Consumer2<Double[],Boolean> m04 = (a,b) -> quickM5(a,b); 
          cons[i] = m04; break;
        case "quickCoM5":
          Consumer2<Double[],Boolean> m05 = (a,b) -> quickCoM5(a,b); 
          cons[i] = m05; break;
        case "quickIt":
          Consumer2<Double[],Boolean> m06 = (a,b) -> quickIt(a); 
          cons[i] = m06; break;
        case "quickF3":
          Consumer2<Double[],Boolean> m07 = (a,b) -> quickF3(a,b); 
          cons[i] = m07; break; //quickCoM3T9F3
        case "quickCoM3T9F3v1":
          Consumer2<Double[],Boolean> m08 = (a,b) -> quickCoM3T9F3v2(a,b); 
          cons[i] = m08; break;
        case "quickCoM3T9F3v2":
          Consumer2<Double[],Boolean> m14 = (a,b) -> quickCoM3T9F3v2(a,b); 
          cons[i] = m14; break;
        case "quickCoM3T9F3v3":
          Consumer2<Double[],Boolean> m15 = (a,b) -> quickCoM3T9F3v3(a,b); 
          cons[i] = m15; break;
        case "quickX":
          Consumer2<Double[],Boolean> m09 = (a,b) -> QuickX.sort(a); 
          cons[i] = m09; break;
        case "Arrays.sort":
          Consumer2<Double[],Boolean> m10 = (a,b) -> Arrays.sort(a); 
          cons[i] = m10; break; //shellSortWithIncrementArray
        case "shell":
          Consumer2<Double[],Boolean> m11 = (a,b) -> shellSortWithIncrementArray(a); 
          cons[i] = m11; break;
        case "mergeTopDownAcCoSm32":
          Consumer2<Double[],Boolean> m12 = (a,b) -> topDownAcCoSm32(a); 
          cons[i] = m12; break; 
        case "mergeTopDownAcCoSm8":
          Consumer2<Double[],Boolean> m13 = (a,b) -> topDownAcCoSm8(a); 
          cons[i] = m13; break;
        case "sampleSort":
          Consumer2<Double[],Boolean> m16 = (a,b) -> sampleSort(a,false); 
          cons[i] = m16; break; 
        case "heap":
          Consumer2<Double[],Boolean> m17 = (a,b) -> Heap.sort(a); 
          cons[i] = m17; break; 
        case "heapRaw":
          Consumer2<Double[],Boolean> m18 = (a,b) -> HeapRaw.sort(a); 
          cons[i] = m18; break; 
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; Double[] d; double tmp1, tmp2; int len, exp;
    Timer timer = new Timer(); long time, prev = 0; String slen;
    List<Double> list = new ArrayList<>(); boolean first = true;
    Iterator<Double> it = null;
    Function<Integer,String> getSlen = (y) -> { return "2^"+y; };
    System.out.println("    "+alg1+" vs "+alg2+" average times over "+trials
        +" trials for random Double arrays");
    System.out.printf("    array length  %14s    %14s    %14s    %14s\n",alg1,"time/prev",alg2, "time/prev");
   
    for (int i = 0; i < cons.length; i++) {
      len = 128; exp = 7; first = true;
      if (i>0) it = list.iterator();
      while (true) {
        len*=2; exp+=1; 
        if (exp==23) break; else slen = getSlen.apply(exp);
        time = 0; time = 0; 
        for (int j = 0; j < trials; j++) {
          r = new Random(System.currentTimeMillis());
          d = new Double[len];          
          for (int k = 0; k < len; k++) d[k] = r.nextDouble();
          if (first) for (int k=0; k<100; k++) cons[i].accept(d,false);
          timer.reset();
          cons[i].accept(d,false);
          time += timer.num();
        }
        if (i==0) {
          if (first) {
            first = false;
            tmp1 = 1.*time/trials; list.add(tmp1);
//            System.out.printf("    %6s           %10.3f        %10s\n",
//                slen, tmp1, "NA");
          } else {
            tmp1 = 1.*time/trials; list.add(tmp1);
            tmp2 = 1.*time/prev; list.add(tmp2);
//            System.out.printf("    %6s           %10.3f        %10.3f\n",
//                slen, tmp1, tmp2);
          }
        } else if (i>0) {
          if (first) {
            first = false;
            System.out.printf("    %6s           %10.3f        %10s        %10.3f        %10s\n",
                slen, it.next(), "NA", 1.*time/trials, "NA");
          } else {
            System.out.printf("    %6s           %10.3f        %10.3f        %10.3f        %10.3f\n",
                slen, it.next(), it.next(), 1.*time/trials, (1.*time/prev));
          }
        }
        prev = time;
      }
    }

  }
  
  @SuppressWarnings("unchecked")
  public static  void compareQuickSortDoubleAlgs(String alg1, String alg2, int trials) {
    // compare run time performance of alg1 with alg2 with int[] arrays populated
    // randomly with Doubles from [0,1) and array lengths starting at 2^8 and doubling
    // forever with each length tested trials times and printing the average of the 
    // resulting times along with ratios based on times for the previous array size.
    Consumer2<Double[],Boolean>[] cons = (Consumer2<Double[],Boolean>[])newInstance(Consumer2.class,2);
    String[] algs = {alg1,alg2};  
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "quick":
          Consumer2<Double[],Boolean> m01 = (a,b) -> quick(a,b); 
          cons[i] = m01; break;
        case "quickSen":
          Consumer2<Double[],Boolean> m02 = (a,b) -> quickSen2(a,b); 
          cons[i] = m02; break;
        case "quickM3":
          Consumer2<Double[],Boolean> m03 = (a,b) -> quickM3(a,b); 
          cons[i] = m03; break;
        case "quickM5":
          Consumer2<Double[],Boolean> m04 = (a,b) -> quickM5(a,b); 
          cons[i] = m04; break;
        case "quickCoM5":
          Consumer2<Double[],Boolean> m05 = (a,b) -> quickCoM5(a,b); 
          cons[i] = m05; break;
        case "quickIt":
          Consumer2<Double[],Boolean> m06 = (a,b) -> quickIt(a); 
          cons[i] = m06; break;
        case "quickF3":
          Consumer2<Double[],Boolean> m07 = (a,b) -> quickF3(a,b); 
          cons[i] = m07; break; //quickCoM3T9F3
        case "quickCoM3T9F3v1":
          Consumer2<Double[],Boolean> m08 = (a,b) -> quickCoM3T9F3v2(a,b); 
          cons[i] = m08; break;
        case "quickCoM3T9F3v2":
          Consumer2<Double[],Boolean> m14 = (a,b) -> quickCoM3T9F3v2(a,b); 
          cons[i] = m14; break;
        case "quickCoM3T9F3v3":
          Consumer2<Double[],Boolean> m15 = (a,b) -> quickCoM3T9F3v3(a,b); 
          cons[i] = m15; break;
        case "quickX":
          Consumer2<Double[],Boolean> m09 = (a,b) -> QuickX.sort(a); 
          cons[i] = m09; break;
        case "Arrays.sort":
          Consumer2<Double[],Boolean> m10 = (a,b) -> Arrays.sort(a); 
          cons[i] = m10; break; //shellSortWithIncrementArray
        case "shell":
          Consumer2<Double[],Boolean> m11 = (a,b) -> shellSortWithIncrementArray(a); 
          cons[i] = m11; break;
        case "mergeTopDownAcCoSm32":
          Consumer2<Double[],Boolean> m12 = (a,b) -> topDownAcCoSm32(a); 
          cons[i] = m12; break; 
        case "mergeTopDownAcCoSm8":
          Consumer2<Double[],Boolean> m13 = (a,b) -> topDownAcCoSm8(a); 
          cons[i] = m13; break; 
        case "sampleSort":
          Consumer2<Double[],Boolean> m16 = (a,b) -> sampleSort(a,false); 
          cons[i] = m16; break; 
        case "heap":
          Consumer2<Double[],Boolean> m17 = (a,b) -> Heap.sort(a); 
          cons[i] = m17; break;  //quickCoM3T9F3v2
        case "heapRaw":
          Consumer2<Double[],Boolean> m18 = (a,b) -> HeapRaw.sort(a); 
          cons[i] = m18; break; 
        case "quickVCoM3T9F3v2Cutoff9":
          Consumer2<Double[],Boolean> m19 = (a,b) -> quickVCoM3T9F3v2(a,9,false); 
          cons[i] = m19; break; //quickVCoIgssM3T9F3v2
        case "quickVCoM3T9F3v2Cutoff19":
          Consumer2<Double[],Boolean> m20 = (a,b) -> quickVCoM3T9F3v2(a,19,false); 
          cons[i] = m20; break;
        case "quickVCoIgssM3T9F3v2Cutoff9":
          Consumer2<Double[],Boolean> m21 = (a,b) -> quickVCoIgssM3T9F3v2(a,9,false); 
          cons[i] = m21; break; 
        case "quickVCoIgssM3T9F3v2Cutoff19":
          Consumer2<Double[],Boolean> m22 = (a,b) -> quickVCoIgssM3T9F3v2(a,19,false); 
          cons[i] = m22; break;   
        case "quickVCoIgssM3T9F3v2Cutoff25":
          Consumer2<Double[],Boolean> m23 = (a,b) -> quickVCoIgssM3T9F3v2(a,25,false); 
          cons[i] = m23; break;   
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; Double[] d, d2; int c = 0;
    Timer timer = new Timer(); long time1, time2, prev1 = 0, prev2 = 0;
//    int[] len = {256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144}; // array lengths 
//    System.out.println();
//    String[] lens = {"2^8","2^9","2^10","2^11","2^12","2^13","2^14","2^15","2^16","2^17","2^18"};
    int len = 128; int lens = 7; String slen = "2^"+lens;
    Function<Integer,String> getSlen = (y) -> { return "2^"+y; };
    System.out.println("    "+alg1+" vs "+alg2+" average times over "+trials
        +" trials for random Double arrays");
    System.out.printf("    array length  %14s    %14s    %14s    %14s\n",alg1,"time/prev",alg2, "time/prev");
    while (true) {
      len*=2; lens+=1; slen = getSlen.apply(lens);
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = new Double[len];
        for (int l = 0; l < len; l++) d[l] = r.nextDouble();
        d2 = d.clone();
        if (c % 2 == 0) {
          timer.reset();
          cons[0].accept(d,false);
          time1 += timer.num();
        } else {
          timer.reset();
          cons[1].accept(d,false);
          time2 += timer.num();
        }
        if (c % 2 == 0) {
          timer.reset();
          cons[1].accept(d2,false);          
          time2 += timer.num();
        } else {
          timer.reset();
          cons[0].accept(d2,false);
          time1 += timer.num();
        }
      }
//      System.out.printf("    %6s           %10.3f        %10.3f\n",
//          lens[k], 1.*time1/trials, 1.*time2/trials);
      if (c == trials) {
        System.out.printf("    %6s           %10.3f        %10s        %10.3f        %10s\n",
            slen, 1.*time1/trials, "NA", 1.*time2/trials, "NA");
      } else {
        System.out.printf("    %6s           %10.3f        %10.3f        %10.3f        %10.3f\n",
            slen, 1.*time1/trials, (1.*time1/prev1), 
            1.*time2/trials, (1.*time2/prev2));
      }
      prev1 = time1; prev2 = time2;
    }

  }
  
  @SuppressWarnings("unchecked")
  public static  void compareQuickSortStringAlgs(String alg1, String alg2, int trials) {
    // compare run time performance of alg1 with alg2 with int[] arrays populated
    // randomly with Strings and array lengths starting at 2^8 and doubling forever 
    // with each length tested trials times and printing the average of the resulting 
    // times along with ratios based on times for the previous array size.
    Consumer2<String[],Boolean>[] cons = (Consumer2<String[],Boolean>[])newInstance(Consumer2.class,2);
    String[] algs = {alg1,alg2};  
    for (int i = 0; i< algs.length; i++) {
      switch (algs[i]) {
        case "quick":
          Consumer2<String[],Boolean> m01 = (a,b) -> quick(a,b); 
          cons[i] = m01; break;  
        case "quickSen":
          Consumer2<String[],Boolean> m02 = (a,b) -> quickSen2(a,b); 
          cons[i] = m02; break;
        case "quickM3":
          Consumer2<String[],Boolean> m03 = (a,b) -> quickM3(a,b); 
          cons[i] = m03; break;
        case "quickM5":
          Consumer2<String[],Boolean> m04 = (a,b) -> quickM5(a,b); 
          cons[i] = m04; break;
        case "quickCoM5":
          Consumer2<String[],Boolean> m05 = (a,b) -> quickCoM5(a,b); 
          cons[i] = m05; break;
        case "quickIt":
          Consumer2<String[],Boolean> m06 = (a,b) -> quickIt(a); 
          cons[i] = m06; break;
        case "quickF3":
          Consumer2<String[],Boolean> m07 = (a,b) -> quickF3(a,b); 
          cons[i] = m07; break;
        case "quickCoM3T9F3v1":
          Consumer2<String[],Boolean> m08 = (a,b) -> quickCoM3T9F3v1(a,b); 
          cons[i] = m08; break;
        case "quickCoM3T9F3v2":
          Consumer2<String[],Boolean> m14 = (a,b) -> quickCoM3T9F3v2(a,b); 
          cons[i] = m14; break;
        case "quickCoM3T9F3v3":
          Consumer2<String[],Boolean> m15 = (a,b) -> quickCoM3T9F3v3(a,b); 
          cons[i] = m15; break;
        case "quickX":
          Consumer2<String[],Boolean> m09 = (a,b) -> QuickX.sort(a); 
          cons[i] = m09; break;
        case "Arrays.sort":
          Consumer2<String[],Boolean> m10 = (a,b) -> Arrays.sort(a); 
          cons[i] = m10; break;
        case "shell":
          Consumer2<String[],Boolean> m11 = (a,b) -> shellSortWithIncrementArray(a); 
          cons[i] = m11; break;
        case "mergeTopDownAcCoSm32":
          Consumer2<String[],Boolean> m12 = (a,b) -> topDownAcCoSm32(a); 
          cons[i] = m12; break; 
        case "mergeTopDownAcCoSm8":
          Consumer2<String[],Boolean> m13 = (a,b) -> topDownAcCoSm8(a); 
          cons[i] = m13; break; 
        default: throw new IllegalArgumentException("compare: unrecognized alg");
      }
    }
    Random r; String[] d, d2; int c = 0;
    Timer timer = new Timer(); long time1, time2, prev1 = 0, prev2 = 0;
//    int[] len = {256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144}; // array lengths 
//    System.out.println();
//    String[] lens = {"2^8","2^9","2^10","2^11","2^12","2^13","2^14","2^15","2^16","2^17","2^18"};
    int len = 128; int lens = 7; String slen = "2^"+lens;
    Function<Integer,String> getSlen = (y) -> { return "2^"+y; };
    System.out.println("    "+alg1+" vs "+alg2+" average times over "+trials
        +" trials for random String arrays");
    System.out.printf("    array length  %14s    %14s    %14s    %14s\n",alg1,"time/prev",alg2, "time/prev");
    while (true) {
      len*=2; lens+=1; slen = getSlen.apply(lens);
      time1 = 0; time2 = 0; 
      for (int i = 0; i < trials; i++) {
        c++;
        r = new Random(System.currentTimeMillis());
        d = randomString(len).split("");
        d2 = d.clone();
        if (c % 2 == 0) {
          timer.reset();
          cons[0].accept(d,false);
          time1 += timer.num();
        } else {
          timer.reset();
          cons[1].accept(d,false);
          time2 += timer.num();
        }
        if (c % 2 == 0) {
          timer.reset();
          cons[1].accept(d2,false);          
          time2 += timer.num();
        } else {
          timer.reset();
          cons[0].accept(d2,false);
          time1 += timer.num();
        }
      }
//      System.out.printf("    %6s           %10.3f        %10.3f\n",
//          lens[k], 1.*time1/trials, 1.*time2/trials);
      if (c == trials) {
        System.out.printf("    %6s           %10.3f        %10s        %10.3f        %10s\n",
            slen, 1.*time1/trials, "NA", 1.*time2/trials, "NA");
      } else {
        System.out.printf("    %6s           %10.3f        %10.3f        %10.3f        %10.3f\n",
            slen, 1.*time1/trials, (1.*time1/prev1), 
            1.*time2/trials, (1.*time2/prev2));
      }
      prev1 = time1; prev2 = time2;
    }

  }

  private static void best(int[] z, int lo, int hi) {
    // precondition:  a[lo..hi] contains keys lo to hi, in order
    // from http://algs4.cs.princeton.edu/23quicksort/QuickBest.java.html
    for (int i = lo; i <= hi; i++) assert z[i] == i;
    BiConsumer<Integer,Integer> exch = (a,b) -> { int t = z[a]; z[a] = z[b]; z[b] = t; };
    if (hi <= lo) return;
    int mid = lo + (hi - lo) / 2;
    best(z, lo, mid-1);
    best(z, mid+1, hi);
    exch.accept(lo, mid);
  }

  public static int[] best(int n) {
    // from http://algs4.cs.princeton.edu/23quicksort/QuickBest.java.html
    int[] a = new int[n];
    for (int i = 0; i < n; i++)
      a[i] = i;
    best(a, 0, n-1);
    return a;
  }
  
  public static int[] bestInt(int[] x) {
    int n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    int[] y = new int[n];
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static int[] bestInt(int[] x, int n) {
    if (x == null) return null;
    if (n == 0) return new int[]{};
    if (n < 0 || n > x.length) n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    int[] y;
    if (n == x.length) y = x.clone();
    else y = take(x,n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
   
  public static double[] bestDbl(double[] x) {
    if (x == null) return null;
    int n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    double[] y = new double[n];
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static double[] bestDbl(double[] x, int n) {
    if (x == null) return null;
    if (n == 0) return new double[]{};
    if (n < 0 || n > x.length) n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    double[] y;
    if (n == x.length) y = x.clone();
    else y = take(x,n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static <T extends Comparable<? super T>> T[] best(T[] x) {
    if (x == null) return null;
    int n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    @SuppressWarnings("unchecked")
    T[] y = (T[]) newInstance(x.getClass().getComponentType(),n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static <T> T[] best(T[] x, Comparator<T> c) {
    if (x == null) return null;
    int n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x,c);
    @SuppressWarnings("unchecked")
    T[] y = (T[]) newInstance(x.getClass().getComponentType(),n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> T[] best(T[] x, int n) {
    if (x == null) return null;
    if (n == 0) return (T[]) newInstance(x.getClass().getComponentType(),0);
    if (n < 0 || n > x.length) n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    T[] y;
    if (n == x.length) y = x.clone();
    else y = take(x,n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T[] best(T[] x, int n, Comparator<T> c) {
    if (x == null) return null;
    if (n == 0) return (T[]) newInstance(x.getClass().getComponentType(),0);
    if (n < 0 || n > x.length) n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x,c);
    T[] y;
    if (n == x.length) y = x.clone();
    else y = take(x,n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static String[] bestString(String[] x) {
    if (x == null) return null;
    int n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    String[] y = new String[n];
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static String[] bestString(String[] x, int n) {
    if (x == null) return null;
    if (n == 0) return new String[]{};
    if (n < 0 || n > x.length) n = x.length;
    int[] bestn = best(n);
    Arrays.sort(x);
    String[] y;
    if (n == x.length) y = x.clone();
    else y = take(x,n);
    for (int i = 0; i < n; i++) y[i] = x[bestn[i]];
    return y;
  }
  
  public static double avgRunTimeWithCutoff(int M, int N, int trials) {
    // re ex2325
    // return the runtime in ms for executing quickCo with cutoff M on random double
    // arrays of length N trials times
    if (M<0||trials<=0||N<0) return -1.;
    Random r=null;
//    try { r = SecureRandom.getInstanceStrong(); } catch (NoSuchAlgorithmException e) {}
//    r = r==null ? r=new Random(System.currentTimeMillis()) : r;
    Double[] d = new Double[N]; double time = 0; Timer t = new Timer();
    for (int i=0; i<trials; i++)  {
      r=new Random(System.currentTimeMillis()); 
      for (int j=0; j<N; j++) d[j] = r.nextDouble();
       { t.reset(); quickVCoM3T9F3v2(d,M,false); time+=t.num(); }
    }
    return time/trials;
  }
  
  public static Object[] findRuntimesAndBestCutoffs(int min, int max, int trials) {
    // re ex2325
    // find best cutoffs in range [min,max] for random double arrays of lengths 
    // N=10^3,10^4,10^5,10^6 and return them in an int[]
    int[] len = {1000,10000,100000,1000000};
    double[][] runtimes = new double[len.length][];
    for (int i=0; i<runtimes.length; i++) runtimes[i] = new double[max-min+1];
    int[] cutoffs = new int[len.length]; double time; double low; int c=-1;
    for (int i=0; i<len.length; i++) {
      low = Double.POSITIVE_INFINITY;
      for (int j=min; j<=max; j++) {
        time = avgRunTimeWithCutoff(j,len[i],trials);
        runtimes[i][j] = time;
        if (time < low) { low = time; c = j; }    
      }
      cutoffs[i] = c;
    }  
    return new Object[]{cutoffs,runtimes};
  }
  
  public static void plotAvgRunTimesVsCutoffs() {
    double[][] runtimes = new double[4][];
    runtimes[0] = new double[]{1.12,0.42,0.36,0.94,1.7,1.65,1.81,1.6,1.34,0.89,0.1,0.12, 0.12,0.12,0.1,0.09,0.13,0.09,0.06,0.12,0.08,0.1,0.12,0.03,0.11,0.09,0.11,0.09,0.11,0.09,0.1};
    runtimes[1] = new double[]{1.69,1.65,1.63,1.57,1.53,1.54,1.47,1.46,1.38,1.39,1.43,1.42,1.42,1.39,1.38,1.37,1.41,1.4,1.39,1.4,1.39,1.42,1.43,1.41,1.43,1.41,1.41,1.43,1.46,1.46,1.47};
    runtimes[2] = new double[]{21.98,21.72,21.28,20.67,20.17,20.08,19.56,19.41,19.26,19.25,19.11,18.93,18.89,18.89,18.93,18.76,18.97,18.79,18.68,18.97,18.92,18.86,19.72,18.87,18.99,19.01,19.06,19.11,19.12,19.16,19.16};
    runtimes[3] = new double[]{329.19,327.0,325.0,316.9,311.68,311.48,307.46,303.78,303.8,302.09,300.31,300.45,299.38,302.64,301.16,298.31,298.29,298.18,297.87,301.33,298.52,298.58,298.85,300.23,299.38,300.14,300.45,300.71,301.26,301.83,302.71};
    StdDraw.setCanvasSize(700, 700);
    double maxX = 4.;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled);
    double maxY = 1.2*max(runtimes[3]);
    double maxYscaled = 1.1*maxY;
    StdDraw.setYscale(-2-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.009);
    for (int i = 0; i < runtimes.length; i++)
      for (int j = 0; j < runtimes[i].length; j++)
        StdDraw.point(i+.5, 35.+runtimes[i][j]);
    Font font = new Font("Arial", Font.PLAIN, 20);
    StdDraw.setFont(font);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(-.3, 10, "array length");
    StdDraw.text(.5, 10, "10^3");
    StdDraw.text(1.5, 10, "10^4");
    StdDraw.text(2.5, 10, "10^5");
    StdDraw.text(3.5, 10, "10^6");
    font = new Font("Arial", Font.BOLD, 15);
    StdDraw.setFont(font);
    StdDraw.text(0.25, 35.+min(runtimes[0]), String.format("%5.3f",min(runtimes[0])));
    StdDraw.text(1.25, 35.+min(runtimes[1]), String.format("%5.3f",min(runtimes[1])));
    StdDraw.text(2.25, 35.+min(runtimes[2]), String.format("%5.3f",min(runtimes[2])));
    StdDraw.text(3.25, 35.+min(runtimes[3]), String.format("%5.3f",min(runtimes[3])));
    System.out.println(min(runtimes[0]));
    System.out.println(min(runtimes[2]));
  }
  
  public static void plotAvgRunTimesVsCutoffsLg() {
    double[][] runtimes = new double[4][];
    runtimes[0] = new double[]{1.12,0.42,0.36,0.94,1.7,1.65,1.81,1.6,1.34,0.89,0.1,0.12, 0.12,0.12,0.1,0.09,0.13,0.09,0.06,0.12,0.08,0.1,0.12,0.03,0.11,0.09,0.11,0.09,0.11,0.09,0.1};
    runtimes[1] = new double[]{1.69,1.65,1.63,1.57,1.53,1.54,1.47,1.46,1.38,1.39,1.43,1.42,1.42,1.39,1.38,1.37,1.41,1.4,1.39,1.4,1.39,1.42,1.43,1.41,1.43,1.41,1.41,1.43,1.46,1.46,1.47};
    runtimes[2] = new double[]{21.98,21.72,21.28,20.67,20.17,20.08,19.56,19.41,19.26,19.25,19.11,18.93,18.89,18.89,18.93,18.76,18.97,18.79,18.68,18.97,18.92,18.86,19.72,18.87,18.99,19.01,19.06,19.11,19.12,19.16,19.16};
    runtimes[3] = new double[]{329.19,327.0,325.0,316.9,311.68,311.48,307.46,303.78,303.8,302.09,300.31,300.45,299.38,302.64,301.16,298.31,298.29,298.18,297.87,301.33,298.52,298.58,298.85,300.23,299.38,300.14,300.45,300.71,301.26,301.83,302.71};
    StdDraw.setCanvasSize(700, 900);
    double maxX = 4.;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled);
    double maxY = 1.2*lg(max(runtimes[3]));
    double maxYscaled = 1.1*maxY;
    StdDraw.setYscale(-6, maxYscaled);
    StdDraw.setPenRadius(.009);
    for (int i = 0; i < runtimes.length; i++)
      for (int j = 0; j < runtimes[i].length; j++)
        StdDraw.point(i+.5, lg(runtimes[i][j]));
    Font font = new Font("Arial", Font.PLAIN, 20);
    StdDraw.setFont(font);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(-.3, 10, "array length");
    StdDraw.text(.5, 10, "10^3");
    StdDraw.text(1.5, 10, "10^4");
    StdDraw.text(2.5, 10, "10^5");
    StdDraw.text(3.5, 10, "10^6");
    font = new Font("Arial", Font.BOLD, 15);
    StdDraw.setFont(font);
    StdDraw.text(0.25, lg(min(runtimes[0])), String.format("%5.3f",min(runtimes[0])));
    StdDraw.text(.9, lg(min(runtimes[0])), "min runtime");
    StdDraw.text(1.25, lg(min(runtimes[1])), String.format("%5.3f",min(runtimes[1])));
    StdDraw.text(1.9, lg(min(runtimes[1])), "min runtime");
    StdDraw.text(2.25, lg(min(runtimes[2])), String.format("%5.3f",min(runtimes[2])));
    StdDraw.text(2.9, lg(min(runtimes[2])), "min runtime");
    StdDraw.text(2.9, lg(min(runtimes[3])), "min runtime "+String.format("%5.3f",min(runtimes[3])));
  }
  
//  public static void plotTopDownMergeSortArrayAccesses(int n) {
//    System.out.println("top-down mergesort:");
//    double[] d = collectArrayAccessData(n);
//    System.out.println("max array accesses for sorting one array="+max(d));
//    double[] d2 = new double[n];
//    for (int i = 0; i < n; i++) d2[i] = 6.*(i+1)*lg(i+1);
//    StdDraw.setCanvasSize(700, 700);
//    double maxY = Math.max(max(d), max(d2)); 
//    System.out.println("max array accesses upper bound="+maxY); //maxY=27648.0 due to d2 for N = 512
//    double maxYscaled = 1.10*maxY;
//    int maxX = n;
//    double maxXscaled = 1.05*maxX;
//    StdDraw.setXscale(-1, maxXscaled+1);
//    StdDraw.setYscale(-1-(2.*maxYscaled/89.25), maxYscaled);
//    StdDraw.setPenRadius(.01);
//    Font font = new Font("Arial", Font.BOLD, 20);
//    StdDraw.setFont(font);
//    StdDraw.setPenColor(StdDraw.BLACK);
//    StdDraw.text(maxXscaled/2, .97*maxYscaled, "Array accesses top-down mergesort "
//        +"for array lengths from 1 to "+n);
//    StdDraw.point(0., 0.);
//    for (int i = 0; i < n; i++) StdDraw.point(i+1, d[i]);
//    StdDraw.setPenColor(StdDraw.BOOK_RED);
//    // this plots the worst case number of array accesses
//    StdDraw.textLeft(maxXscaled/5, .75*maxYscaled, "In red is upper bound 6NlgN");
//    for (int i = 0; i < n; i++) StdDraw.point(1.+i, d2[i]);
//  }
  
  public static void plotHistogramOfSizesOfSubarraysLeftForInsertionSort(int M, int N) {
    // ex2326
    // plot a histogram of the sizes of subarrays left for insertion sort for random
    // double array of length N and cutoff M
    if (M<0||N<=0) return;
    Double[] d = new Double[N];
    Random r=new Random(System.currentTimeMillis());
    for (int i = 0; i < N; i++) d[i] = r.nextDouble();
    Integer[] ss = quickVCoSslfiM3T9F3v2(d, M, false);
    Histogram h = new Histogram(M);
    for (int i = 0; i <ss.length; i++) h.addDataPoint(ss[i]);
    StdDraw.setCanvasSize(500, 100);
    h.draw();
  }
  
  public static void printAvgRecDepthVsLengthAndCutoff() {
    // ex2328
    // print the average recursive depth for various cutoffs and array lengths
    // using randomized Integer arrays will all unique keys.
    int[] cutoff = {10,20,50}; double res[] = new double[cutoff.length];
    int[] len = {1000,10000,100000,1000000}; 
    String[] lens = {"10^3","10^4","10^5","10^6"};  
    Integer[] a; Random r; double d;
    System.out.println("Average recursive depth vs. length and cutoff");
    System.out.println("length    cutoff 10    cutoff 20    cutoff 50");
    for (int i = 0; i < len.length; i++) {
      a = rangeInteger(0,len[i]); r = new Random(System.currentTimeMillis());   
      for (int k = 0; k < cutoff.length; k++) {        
        shuffle(a,r);
        res[k] = quickVCoRdM3T9F3v2(a,cutoff[k],false);
      }
      System.out.printf(" %5s  %11.3f  %11.3f  %11.3f\n",lens[i],res[0],res[1],res[2]);
    }
  }
  
  public static void testRandomPivotSelectionVsArrayRandomizationSorted(int trials) {
    // ex2329 
    // print runtimes for sorting arrays for sorted Integer arrays withall distinct keys 
    // and lengths 10^3, 10^4, 10^5, and 10^6 using random pivot selection vs. array
    // randomization and cutoffs of 10, 20 and 50.
    int[] cutoff = {10,20,50}; 
    double res1[] = new double[cutoff.length]; double res2[] = new double[cutoff.length];
    int[] len = {1000,10000,100000,1000000}; String[] lens = {"10^3","10^4","10^5","10^6"};  
    Integer[] a, b;  long time; Timer t = new Timer();
    System.out.println(trials+" trials for each length and cutoff");
    System.out.println("using sorted Integer arrays with all unique keys");
    System.out.println("runtimes in ms");
    System.out.println("                  cutoff 10                 cutoff 20                 cutoff 50");
    System.out.println("length      shuffle     rndPivot      shuffle     rndPivot      shuffle     rndPivot");
    System.out.println("------      -------     --------      -------     --------      -------     --------");
    for (int i = 0; i < len.length; i++) {
      a = rangeInteger(0,len[i]); time = 0;  
      for (int j = 0; j < cutoff.length; j++) {
        for (int k = 0; k < trials; k++) {
          b = a.clone();
          t.reset();
          quickVCoM3T9F3v2(b,cutoff[j],true);
          time+=t.num();
        }
        res1[j] = (1.*time)/trials;
        time = 0;
        for (int k = 0; k < trials; k++) {
          b = a.clone();
          t.reset();
          quickVCoRpF3(b,cutoff[j]);
          time+=t.num();
        }
        res2[j] = (1.*time)/trials;
        
      }
      System.out.printf("%5s   %11.3f  %11.3f  %11.3f  %11.3f  %11.3f  %11.3f\n",
          lens[i],res1[0],res2[0],res1[1],res2[1],res1[2],res2[2]);
    }
  }

  public static void testRandomPivotSelectionVsArrayRandomizationReverseSorted(int trials) {
    // ex2329 
    // print runtimes for sorting arrays for sorted Integer arrays withall distinct keys 
    // and lengths 10^3, 10^4, 10^5, and 10^6 using random pivot selection vs. array
    // randomization and cutoffs of 10, 20 and 50.
    int[] cutoff = {10,20,50}; 
    double res1[] = new double[cutoff.length]; double res2[] = new double[cutoff.length];
    int[] len = {1000,10000,100000,1000000}; String[] lens = {"10^3","10^4","10^5","10^6"};  
    Integer[] a, b;  long time; Timer t = new Timer();
    System.out.println(trials+" trials for each length and cutoff");
    System.out.println("using reverse sorted Integer arrays with all unique keys");
    System.out.println("runtimes in ms");
    System.out.println("                  cutoff 10                 cutoff 20                 cutoff 50");
    System.out.println("length      shuffle     rndPivot      shuffle     rndPivot      shuffle     rndPivot");
    System.out.println("------      -------     --------      -------     --------      -------     --------");
    for (int i = 0; i < len.length; i++) {
      a = rangeInteger(len[i],-1); time = 0;  
      for (int j = 0; j < cutoff.length; j++) {
        for (int k = 0; k < trials; k++) {
          b = a.clone();
          t.reset();
          quickVCoM3T9F3v2(b,cutoff[j],true);
          time+=t.num();
        }
        res1[j] = (1.*time)/trials;
        time = 0;
        for (int k = 0; k < trials; k++) {
          b = a.clone();
          t.reset();
          quickVCoRpF3(b,cutoff[j]);
          time+=t.num();
        }
        res2[j] = (1.*time)/trials;
        
      }
      System.out.printf("%5s   %11.3f  %11.3f  %11.3f  %11.3f  %11.3f  %11.3f\n",
          lens[i],res1[0],res2[0],res1[1],res2[1],res1[2],res2[2]);
    }
  }
  
  public static void histogramOfRunningTimes(int N, int T) {
    // ex 2331
    // plot a histogram of running times of using quicksort to sort arrays
    // of random Doubles of length N for T trials
    if (N<=0||T<=0) return;
    Double[] d = new Double[N]; int[] times = new int[T];
    Timer t = new Timer(); Random r; long time=0;    
    for (int i = 0; i < T; i++) {
      r = new Random(System.currentTimeMillis());
      for (int j = 0; j < N; j++) d[j] = r.nextDouble();
      t.reset();
      quickCoM3T9F3v2(d,false);
      times[i] = (int)t.num();   
    }
    System.out.println("length="+N+" min="+min(times)+" max="+max(times));
    Histogram h = new Histogram(max(times)+1);
    for (int i = 0; i <T; i++) h.addDataPoint(times[i]);
    StdDraw.setCanvasSize(500,100);
    h.draw();
  }
  
  public static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
//    int[] kk = {0,0,0,0,0,0,1,1,1,6};
//    int[] a = {0,0,0,1,2,2,3,3,3,4,4,4,4,5,5,5,5,5,6,6,6,6,7,7,7,8,8,9};
//    Histogram histogram = new Histogram(10); 
//    for (int i = 0; i <a.length; i++) histogram.addDataPoint(a[i]);
//    StdDraw.setCanvasSize(500, 100);
//    histogram.draw();
    
    histogramOfRunningTimes(10000,100);
    
//    printAvgRecDepthVsLengthAndCutoff();
//    System.exit(0);
    
//  Random r5 = SecureRandom.getInstanceStrong();
//  Random r5 = new Random(System.currentTimeMillis());
////  Integer[] s40 = new Integer[10000]; 
////  for(int j = 0; j<s40.length; j++) s40[j] = r5.nextInt(102);
//////  System.out.println(quickVCoRdM3T9F3v2(s40,19,false));//quickCoRpF3
////  quickVCoRpF3(s40,19);
////  assert isSorted(s40);
////  System.exit(0);
//  
//  for (int i=3; i<1004; i++) {
//    Integer[] s41 = new Integer[i];
//    //Double[] d41 = new Double[i];
//    for(int j = 0; j<s41.length; j++) s41[j] = r5.nextInt(56);
//    //for(int j = 0; j<d41.length; j++) d41[j] = r5.nextDouble();
//    //par(s26);
////    quickVCoRdM3T9F3v2(s41,19,false);
//    quickVCoRpF3(s41,19);
//    //quickVCoIgssM3T9F3v2(d41,8,false);
//    //par(s26);
//    assert isSorted(s41);
//    //assert isSorted(d41);
//  }
//  System.exit(0);
    
//    plotAvgRunTimesVsCutoffs();
    
////    Object[] xx = findRuntimesAndBestCutoffs(0,30,100);
////    par(xx[0]); 
////    double[][] runtimes = (double[][])xx[1];
////    for (int i=0;i<runtimes.length;i++) par(runtimes[i]);
///* findRuntimesAndBestCutoffs(0,30,100);
//[23,15,18,18] avg 18.5
//  0     1   2     3   4   5     6    7    8    9  10  11   12   13   14  15   16   17   18   19   20   21  22   23   24   25
//[1.12,0.42,0.36,0.94,1.7,1.65,1.81,1.6,1.34,0.89,0.1,0.12, 0.12,0.12,0.1,0.09,0.13,0.09,0.06,0.12,0.08,0.1,0.12,0.03,0.11,0.09,0.11,0.09,0.11,0.09,0.1]
//[1.69,1.65,1.63,1.57,1.53,1.54,1.47,1.46,1.38,1.39,1.43,1.42,1.42,1.39,1.38,1.37,1.41,1.4,1.39,1.4,1.39,1.42,1.43,1.41,1.43,1.41,1.41,1.43,1.46,1.46,1.47]
//[21.98,21.72,21.28,20.67,20.17,20.08,19.56,19.41,19.26,19.25,19.11,18.93,18.89,18.89,18.93,18.76,18.97,18.79,18.68,18.97,18.92,18.86,19.72,18.87,18.99,19.01,19.06,19.11,19.12,19.16,19.16]
//[329.19,327.0,325.0,316.9,311.68,311.48,307.46,303.78,303.8,302.09,300.31,300.45,299.38,302.64,301.16,298.31,298.29,298.18,297.87,301.33,298.52,298.58,298.85,300.23,299.38,300.14,300.45,300.71,301.26,301.83,302.71]
//
//*/
//// 
////    double min,max,mean,stddev;
////    
////    double[] len103 = {1.12,0.42,0.36,0.94,1.7,1.65,1.81,1.6,1.34,0.89,0.1,0.12,0.12,0.12,0.1,0.09,0.13,0.09,0.06,0.12,0.08,0.1,0.12,0.03,0.11,0.09,0.11,0.09,0.11,0.09,0.1};
////    min=min(len103); max=max(len103); mean=mean(len103); stddev=stddev(len103);
////    System.out.printf("%7.3f  %7.3f  %7.3f  %7.3f  %7.3f\n", min, max, mean, stddev, 100.*((max-min)/min));
////    
////    double[] len104 = {1.69,1.65,1.63,1.57,1.53,1.54,1.47,1.46,1.38,1.39,1.43,1.42,1.42,1.39,1.38,1.37,1.41,1.4,1.39,1.4,1.39,1.42,1.43,1.41,1.43,1.41,1.41,1.43,1.46,1.46,1.47};
////    min=min(len104); max=max(len104); mean=mean(len104); stddev=stddev(len104);
////    System.out.printf("%7.3f  %7.3f  %7.3f  %7.3f   %7.3f\n", min, max, mean, stddev, 100.*((max-min)/min));
////        
////    double[] len105 = {21.98,21.72,21.28,20.67,20.17,20.08,19.56,19.41,19.26,19.25,19.11,18.93,18.89,18.89,18.93,18.76,18.97,18.79,18.68,18.97,18.92,18.86,19.72,18.87,18.99,19.01,19.06,19.11,19.12,19.16,19.16};
////    min=min(len105); max=max(len105); mean=mean(len105); stddev=stddev(len105);
////    System.out.printf("%7.3f  %7.3f  %7.3f  %7.3f   %7.3f\n", min, max, mean, stddev, 100.*((max-min)/min));
////    
////    double[] len106 = {329.19,327.0,325.0,316.9,311.68,311.48,307.46,303.78,303.8,302.09,300.31,300.45,299.38,302.64,301.16,298.31,298.29,298.18,297.87,301.33,298.52,298.58,298.85,300.23,299.38,300.14,300.45,300.71,301.26,301.83,302.71};
////    min=min(len106); max=max(len106); mean=mean(len106); stddev=stddev(len106);
////    System.out.printf("%7.3f  %7.3f  %7.3f  %7.3f   %7.3f\n", min, max, mean, stddev, 100.*((max-min)/min));
////   
////        System.exit(0);
//    
////    Integer[] s30 = {9,9,9,9,9,5,1,3,2,4,7,7,7,7};
////    quick(s30,5,9);
////    par(s30);
////    System.exit(0);
//    
////    compareQuickSortDoubleAlgs("quick", "quickSen", 5);
////    compareQuickSortStringAlgs("quick", "quickSen", 5);
////    System.exit(0);
//
//    Random r3 = SecureRandom.getInstanceStrong();
//    
////    Integer[] s27 = new Integer[10000]; 
////    for(int j = 0; j<s27.length; j++) s27[j] = r3.nextInt(102);
////    sampleSort(s27,false);
////    assert isSorted(s27);
////    System.exit(0);
//    
//    for (int i=3; i<1004; i++) {
//      Integer[] s26 = new Integer[i];
////      Double[] d26 = new Double[i];
//      for(int j = 0; j<s26.length; j++) s26[j] = r3.nextInt(56);
////      for(int j = 0; j<d26.length; j++) d26[j] = r3.nextDouble();
////      par(s26);
//      sampleSort(s26,false);
////      sampleSort(d26,false);
////      par(s26);
//      assert isSorted(s26);
////      assert isSorted(d26);
//    }
//    System.exit(0);
//    
//    Integer[] s25; int c11 = 0; int len = 2; int lens = 1;
////    len=8192; lens=13;
//    while (true) {
//      c11++;
//      len*=2; lens+=1;
//      if (lens == 24) len=2; lens=1; 
//    
////      s25 = rangeInteger(1,i);
////      String[] d = randomString(len).split(""); String[] d2 = d.clone();
//      Double[] d = new Double[len]; Double[] d2;
//      for(int j = 0; j<len; j++) d[j] = r3.nextDouble();
////       Integer[] d = new Integer[len];  Integer[] d2;
////      for(int j = 0; j<len; j++) d[j] = r3.nextInt(7);
//      d2 = d.clone();
////      s25 = new Integer[]{11,6,8,9,5,7,10,4,1,3,2};
////      s25 = new Integer[]{52,42,6,19,43,2,41,17,40,1,4,46,3,44,56,25,31,57,34,28,38,32,26,48,30,8,37,12,7,53,47,29,14,59,27,51,13,20,21,11,10,9,18,58,23,5,55,24,49,54,39,35,15,36,33,22,45,16,50,60};
////      s25 = new Integer[]{1,2};
////      shuffle(s25,r3);
////      par(s25);
////      QuickX.sort(s25);
//      quickCoM3T9F3v3(d,false);
//      pause(100);
////      quickCoM3T9F3v2(d2,false);
////      par(s25);
////      assert isSorted(d);
//
//    if (c11==1001) break;
//
//    }
//    System.exit(0);
//    
////    String[] p1 = "R B W W R W B R R W B R".split("\\s+");
////    quick3WayTrace(p1);
////    System.exit(0);
//    
//    par(best(10)); //[4,0,2,3,1,7,6,5,8,9]
//    int[] s23 = range(1,11);
//    par(bestInt(s23)); //[5,1,3,4,2,8,7,6,9,10]
//    
//    System.exit(0);
//   
////    Random r2 = SecureRandom.getInstanceStrong();
////////    int[] s20 = {1,1,1,1,1,3,3,3,3,3,2,2,2,2,2};
////////    s20 = range(1,50);
////    par(quickDblR(r2.doubles(10000).toArray())); //29,28,35,30
////    par(quickDblR(range(1.,1001.))); //999
////    par(quickDblR(range(1000.,0.))); //999
////    
//    par(quickDblR(fillDouble(1000, ()->5.))); //9
////    par(quickDblR(bestDbl(range(1.,1001.)))); //9
////    par(quickIntR(fillInt(1000, ()->1))); //511
//////   double[] dd = range(1.,10.);
//////   par(bestDbl(dd));
////    
////    for (int i = 3; i < 1004; i++) {
//////      System.out.println("i="+i);
////      double[] s21 = ;
//////      shuffle(s21,r2);
//////    par(s20);
////      quickDbl(s21);
////      assert isSorted(s21);
//////    quick3Way(s20);
//////    par(s20);
////    }
//    System.exit(0);
//
//    compareQuickSortIntAlgs("quickInt", "quickIntAe", 7, 100);
//    /* quickInt vs quickIntAe average times over 100 trials and ratios to previous
//    array length        quickInt         time/prev        quickIntAe         time/prev
//       2^8                0.080                NA             0.330                NA
//       2^9                0.070             0.875             0.210             0.636
//      2^10                0.220             3.143             0.320             1.524
//      2^11                0.320             1.455             0.690             2.156
//      2^12                0.500             1.563             1.770             2.565
//      2^13                0.550             1.100             3.790             2.141
//      2^14                0.940             1.709            12.010             3.169
//      2^15                1.640             1.745            45.970             3.828  
//   */
//    
//    compareQuickSortIntAlgs("quickIntIt", "quickIntItAe", 7, 100);
//
//    /* quickIntIt vs quickIntItAe average times over 100 trials and ratios to previous
//    array length      quickIntIt         time/prev      quickIntItAe         time/prev
//       2^8                0.170                NA             0.130                NA
//       2^9                0.110             0.647             0.030             0.231
//      2^10                0.190             1.727             0.150             5.000
//      2^11                0.220             1.158             0.200             1.333
//      2^12                0.570             2.591             0.470             2.350
//      2^13                0.950             1.667             0.920             1.957
//      2^14                1.950             2.053             1.910             2.076
//      2^15                4.140             2.123             4.190             2.194
//      2^16                8.200             1.981             7.930             1.893
//      2^17               16.470             2.009            16.000             2.018
//      2^18               32.870             1.996            33.100             2.069
//      2^19               68.500             2.084            68.290             2.063
//      2^20              141.950             2.072           142.280             2.083
//      2^21              299.290             2.108           296.760             2.086
//*/   
//    
//    
////    Integer[] s17 = {2,1,1,3,1,1,1};
////    partitionTraceAE(s17,0,s17.length-1);
//    
//    System.exit(0);
//
//    
//    Random r1 = SecureRandom.getInstanceStrong();
//    int[] s4 = new int[1000]; s4 = fillInt(1000, ()->1);
////    for (int i = 0; i < s4.length; i++) s4[i] = r1.nextInt(2);
//    int[] s5 = s4.clone(); int[] s6 = s4.clone();
//    par(quickIntAePSub0(s4)); //[17488,7489] partitions, sub0
//    par(quickIntCEP(s5)); //[126560,53469,11868] comparisons, swaps, partitions - regular
//    par(quickIntAeCE(s6)); //[6390715,13161,17488] comparisons, swaps, partitions
//    System.exit(0);
//    
//    int[] s2 = new int[1000]; int[] s3 = range(1,1001);
//    for (int i = 0; i < s2.length; i++) s2[i] = r1.nextInt(40);
//    par(quickIntAePSub0(s2));
//    System.exit(0);
////    quickIntAePSub0
////    Integer[] u1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
////    Integer[] u1 = {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2}; //scan break at i=1 j=0
////    Integer[] u2 = {2,1,2,1,2,1,2,1,2,1,2,1,2,1,2}; //scan break at i=14 j=13
////    Integer[] u3 = {1,1,2,1,2,1,2,1,2,1,2,1,2,2,2}; //<| scan break at i=2 j=0
////    Integer[] u31 = {1,1,2,1,2,1,2,1,2,1,2,1,2,2}; //scan break at i=2 j=0
////    Integer[] u4 = {1,2,1,2,1,2,1,2,1,2,1,2,2}; 
//    
//      Integer[] s1 = {3,1,2,2,1,3,3,3,2,1,3,2,1,1,2};
//    
////    Integer[] u2 = u1.clone(); 
////    int[] u3 = {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2}; int[] u4 = u3.clone();
////    par(quickIntAeCE(u3)); //[128,11,22]
////    par(quickIntCEP(u4)); //[54,23,18]
////    System.out.println();
////    System.out.println(partitionTraceAE(s1,0.s1.length-1)); 
////    System.out.println();
////    System.out.println(partitionTrace(u2,0,u2.length-1)); //17
//    
//    System.exit(0);
//
//    int[] x1 = {1,1}; int[] x2 = {1,1,1}; int[] x3 = {1,1,1,1}; int[] x4 = {1,1,1,1,1};
//    int[] x5 = {1,1,1,1,1,1}; int[][] x = {x1,x2,x3,x4,x5};
//    
//    int[] y1 = {2,1}; int[] y2 = {1,2,3}; int[] y3 = {1,2,3,4}; int[] y4 = {1,2,3,4,5};
//    int[] y5 = {1,2,3,4,5,6}; int[][] y = {y1,y2,y3,y4,y5};
//    
//    int[] v1 = {1,2}; int[] v2 = {1,2,1}; int[] v3 = {1,2,1,2}; int[] v4 = {1,2,1,2,1};
//    int[] v5 = {1,2,1,2,1,2}; int[][] v = {v1,v2,v3,v4,v5};
//    
//    for (int i = 0; i < x.length; i++) 
//      System.out.println(x[i].length+" "+arrayToString(quickIntAeCE(x[i])));
//    System.out.println();
//    for (int i = 0; i < y.length; i++) 
//      System.out.println(y[i].length+" "+arrayToString(quickIntAeCE(y[i])));
//    System.out.println();
//    for (int i = 0; i < v.length; i++) 
//      System.out.println(v[i].length+" "+arrayToString(quickIntAeCE(v[i]),-1));
//    System.exit(0);
//
///*  x
//    2 int[5,1,0]
//    3 int[12,2,0]
//    4 int[21,3,0]
//    5 int[32,4,0]
//    6 int[45,5,0]
//    y
//    2 int[4,1,0]
//    3 int[9,2,0]
//    4 int[15,3,0]
//    5 int[22,4,0]
//    6 int[30,5,0]
//    v
//    2 [4,1,0]
//    3 [8,2,0]
//    4 [11,2,0]
//    5 [22,4,0]
//    6 [25,4,0]
//
//*/
//    
////    int[] w1 = {2,1,2,1}; int[] w2 = w1.clone(); 
////    par(quickIntCEP(w1));
////    System.out.println(quickIntCt(w2));
////    
////    int[] w3 = {3,1,2,1}; int[] w4 = w3.clone();
////    par(quickIntCEP(w3));
////    System.out.println(quickIntCt(w4));
////    System.exit(0);
//    
////    maxCompares(new int[]{1,2,1}); //6
////    maxCompares(new int[]{2,1,2}); //6
////    minCompares(new int[]{1,2,3}); //4
////    maxCompares(new int[]{1,2,3}); //7
////    maxCompares(new int[]{1,2,1,2,1,2}); //16
////    minCompares(new int[]{1,2,3,1,2,3}); //12
////
////    System.exit(0);
//    int[] t0 = new int[]{1,1,1};
//    int[] t1 = new int[]{1,2,1}; 
//    int[] t2 = new int[]{1,2,3};
//    int[] t3 = new int[]{1,1,1,1};
//    int[] t4 = new int[]{1,2,1,2};
//    int[] t5 = new int[]{1,2,3,1};
//    int[] t6 = new int[]{1,1,1,1,1,1};
//    int[] t7 = new int[]{2,1,1,1,1,1};
//    int[] t8 = new int[]{2,2,1,1,1,1};
//    int[] t9 = new int[]{2,2,2,1,1,1};
//    int[] t10 = new int[]{1,2,3,1,1,1};
//    int[] t11 = new int[]{1,1,2,2,3,3};
//    int[] t12 = new int[]{1,1,1,1,1,1,1,1,1,1,1,1};
//    int[] t13 = new int[]{2,2,1,1,1,1,1,1,1,1,1,1};
//    int[] t14 = new int[]{2,2,2,1,1,1,1,1,1,1,1,1};
//    int[] t15 = new int[]{2,2,2,2,2,2,1,1,1,1,1,1};
//    int[] t16 = new int[]{1,2,3,1,1,1,1,1,1,1,1,1};
//    int[] t17 = new int[]{1,1,2,2,3,3,1,1,1,1,1,1};
//    int[] t18 = new int[]{1,1,1,1,2,2,2,2,3,3,3,3};
//    printPermutationsStats(t0,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18);
///*
//                                        over all unique permutations of a[]
//                         a[]    meanCompares  meanExchanges  meanCompares+Exchanges
//                     [1,2,1]           4.333          1.667          6.000
//                     [2,1,2]           5.333          2.000          7.333
//                     [1,2,3]           6.000          1.833          7.833
//               [1,2,1,2,1,2]          13.900          5.300         19.200
//               [1,2,3,1,2,3]          14.444          4.911         19.356
//   [1,2,1,2,1,2,1,2,1,2,1,2]          36.779         14.652         51.431
//   [1,2,3,1,2,3,1,2,3,1,2,3]          39.366         13.800         53.166
//   [1,1,1,1,1,1,1,1,1,1,1,1]          34.000         17.000         51.000
//   [1,1,1,1,1,1,1,1,1,2,1,1]          36.667         16.667         53.333
//*/
////    printPermsStats(new int[]{1,2,1});
////    printPermsStats(new int[]{2,1,2});
////    printPermsStats(new int[]{1,2,3});
////    printPermsStats(new int[]{1,2,1,2,1,2});
////    printPermsStats(new int[]{1,2,3,1,2,3});
////    printPermsStats(new int[]{1,2,1,2,1,2,1,2,1,2,1,2});
////    printPermsStats(new int[]{1,2,3,1,2,3,1,2,3,1,2,3});
////    printPermsStats(range(1,12));
//
//    System.exit(0);
//
//
//
//    Map<Integer,int[]> map = new HashMap<>();
//    Iterator<int[]> pint = permutations(new int[]{1,1,2,2});
//    int cc = 0;
//    while (pint.hasNext()) {
//      int[] xy = pint.next(); map.put(Arrays.hashCode(xy),xy);
//    }
//    for (Integer yx : map.keySet()) par(map.get(yx));
//    System.exit(0);
//    while (pint.hasNext()) { pa(pint.next()); cc++; }
//    System.out.println("cc="+cc);
//    cc = 0;
//    pint =  permutations(new int[]{1,2,3});
//    while (pint.hasNext()) { pa(pint.next()); cc++; }
//    System.out.println("cc="+cc);
//    
//    Random r = SecureRandom.getInstanceStrong();
//    int[] d4 = {1,2,1,2,1,2};
//    d4 = new int[]{1,2,2,1,1,2};
////    shuffle(d4,r);
//    par(d4);
//    quickIntC(d4);
//    System.out.println();
//    int[] d5 = {1,2,3,1,2,3};
//    shuffle(d5,r);
//    par(d5);
//    quickIntC(d5);
//    System.exit(0);
//    
//    int[] d = {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2};//{1,2,1,2,1,2}; //{1,1,1,1,1,1}; 
//    d = append(fillInt(50000,()->1),fillInt(50000,()->2));
//    int[] d2 = d.clone(); int[] d3 = d.clone();
//    System.out.println("2 keys equal freq alternating not shuffled");
//    par(quickIntSub012(d2)); //[31069,34466,31069]
//    par(quickIntCEP(d3)); //[2211140,730570] 2,941,710
//    shuffle(d,r);
//    int[] d1 = d.clone();                                              //[10953,44524,10953]
//    System.out.println("\n2 keys equal freq shuffled");
//    par(quickIntSub012(d)); //[3,9,3] [5,8,5] [353,824,353] [2135,3933,2135] [19939,40031,19939]
//    par(quickIntCEP(d1)); //[74,31] [19958,9295] [124400,57949] [1619989,749200] 2,369,189
//                                                             //[1569601,747636] 2,317,237
//                                                             //[1570028,748393] 2,318,421
//                                         //[11333,44334,11332] [1565573,747813] 2,313,386
//    
//    int[] c = append(fillInt(20000,()->1),fillInt(80000,()->2));
//    shuffle(c,r);
//    int[] c1 = c.clone();
//    System.out.println("\n2 keys 1-20% 2-80% shuffled");
//    par(quickIntSub012(c)); //[18369,40816,18368] [23697,38152,23697]
//    par(quickIntCEP(c1)); //[1557484,752713] [1554077,753366] 2,307,443
//                                          //[1585978,752447] 2,338,425
//                      //[23631,38185,23631] [1554923,753314] 2,308,237
//    int[] e = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
//    e = fillInt(100000, ()->1);
//    int[] e1 = e.clone();
//    System.out.println("\n1 key");
//    par(quickIntSub012(e)); //[5,8,5] [5,8,5] [47,977,47] [1809,4096,1809] [31071,34465,31071]
//    par(quickIntCEP(e1)); //[68,34] [19786,9893] [121034,60517] [1561130,780565] 2,341,695
////    int[] f = {2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
////    shuffle(f,r);
////    int[] f1 = f.clone();
////    par(quickIntSub012(f)); //[5,8,4] [5,8,5] [657,672,329] [3319,3341,1665] 
////    par(quickIntCEP(f1)); //[67,33] [27359,5273]  [178132,31055] 
//    int[] g = range(1,100001);
//    shuffle(g,r);
//    System.out.println("\n100K unique keys shuffled");
//    int[] g1 = g.clone();
//    par(quickIntSub012(g)); //[5,8,3] [7,7,4] [669,666,337] [33235,33383,16626]
//    par(quickIntCEP(g1)); //[95,22] [27359,5273] [2078004,394509] 2,472,513
//                                              //[2127028,391463] 2,518,491
//                          //[33427,33287,16598] [2202393,390867] 2,593,260
//            
//    
//    int[] h = append(fillInt(33334,()->1),append(fillInt(33333,()->2),fillInt(33333,()->3)));
//    shuffle(h,r);
//    System.out.println("\n3 keys equal freq shuffled");
//    par(quickIntSub012(h)); //[15847,42077,15846]         [21477,39262,21477]
//    par(quickIntCEP(h)); //[2894683,697349] 3,592,032  [2894683,697349]
//     // two elements    //[11333,44334,11332] [1565573,747813] 2,313,386
//     // one element     //[31071,34465,31071] [1561130,780565] 2,341,695
//     // all distinct    //[33427,33287,16598] [2202393,390867] 2,593,260
//    
//    int[] k = append(append(fillInt(25000,()->1),fillInt(25000,()->2)),
//        append(fillInt(25000,()->3),fillInt(25000,()->4)));
//    shuffle(k,r);
//    System.out.println("\n4 keys equal freq shuffled");
//    par(quickIntSub012(k)); 
//    par(quickIntCEP(k));
//    
//    
    
//    for (int i = 1; i < 100000; i++) {
//      int[] a = fillInt(i, () -> 5);
//      int N = i;
////      par(a);
////      System.out.println("N="+a.length+" "+quickIntC(a)+"\n");
//      System.out.println("N="+a.length+" "+quickIntC(a)+" "+(1.*(N)*lg(N)));
//      double r = ((1.*(N)*lg(N))-quickIntC(a))/(1.*(N)*lg(N));
//      System.out.println("N="+a.length+" "+quickIntC(a)+" "+(1.*(N)*lg(N)));
//    }
//    int N = 10000;
//    int[] b = fillInt(N, () -> 5); 
////    System.out.println("N="+b.length+" "+quickIntC(b)+" "+(1.*(N)*lg(N)));
//    double r = ((1.*(N)*lg(N))-ncei(N))/ncei(N);
//    System.out.println(r); 
 /* N          r
    10         0.2776646518797548
    100        0.1779886861302704
    1000       0.12000272922702718
    10000      0.09784956124307628
    100000     0.06394986160260911
    1000000    0.05914351893035099
    10000000   0.05467287181019356
    100000000  0.03737905177616354
    
 */   
//    printArraysOfLength10WithMaxNumberOfCompares(); //63
    
//    System.out.println(quickIntC(new int[]{1,1,1,1,1})); //10
//    System.out.println(quickIntC(new int[]{1,2,1,2,1})); //9
//    System.out.println(quickIntC(new int[]{1,2,3,4,5})); //18
//    System.out.println(quickIntC(new int[]{5,4,3,2,1})); //18

//    printNumberOfExchangesOfMaxElement();
    
//    for (int i = 1; i<13; i++) {
//      int max = -1;
//      int [] a = range(0,i);
//      int n = a.length;
//      Iterator<int[]> it =  permutations(a);
//      while (it.hasNext()) {
//        int[] q = it.next();
//        //      int[] p = q.clone();
////        par(q);
//        int m=quickIntEL(q);   
//        if (m > max) max = m;
////        System.out.println(m);
//        //      if (m == 3) par(p);
//      }
//      System.out.println("arrlen="+n+" max="+max);
//    }
/*    
  arrlen=1 max=0
  arrlen=2 max=1
  arrlen=3 max=1
  arrlen=4 max=2
  arrlen=5 max=2
  arrlen=6 max=3
  arrlen=7 max=3
  arrlen=8 max=4
  arrlen=9 max=4
  arrlen=10 max=5
  arrlen=11 max=5
  arrlen=12 max=6

*/
//    System.out.println(quickIntEL(a));
//    par(a);
    
    
//    String[] s = "E A S Y Q U E S T I O N".split("\\s+");
//    String[] s = "O   E   T   U   Q   I   Y   A   E   S   N   S".split("\\s+");
//    partitionTrace(s);
//    s = "Q U I C K S O R T E X A M P L E".split("\\s+");
//    s = "K R A T E L E P U I M Q C X O S".split("\\s+");
    // from http://algs4.cs.princeton.edu/23quicksort/
//    s = "Y A T N S S E Q O I U E".split("\\s+");   
//    quickTrace(s);
    
//    compare("topDown", -1,"quick", -1, 10);
//    compare("natural", -1,"topDownAcCoSm", 31, 100);
//    cutoff("topDown", -1,"topDownCo", -1, 1000);
    
    /* partition traces

                       i   j   0   1   2   3   4   5   6   7   8   9  10  11  

       initial values  0  11   E   A   S   Y   Q   U   E   S   T   I   O   N   

        after shuffle  0  11   O   E   T   U   Q   I   Y   A   E   S   N   S   

scan left, scan right  2  10   O   E  [T]  U   Q   I   Y   A   E   S  [N]  S   

             exchange  2  10   O   E  [N]  U   Q   I   Y   A   E   S  [T]  S   

scan left, scan right  3   8   O   E   N  [U]  Q   I   Y   A  [E]  S   T   S   

             exchange  3   8   O   E   N  [E]  Q   I   Y   A  [U]  S   T   S   

scan left, scan right  4   7   O   E   N   E  [Q]  I   Y  [A]  U   S   T   S   

             exchange  4   7   O   E   N   E  [A]  I   Y  [Q]  U   S   T   S   
                                       
scan left, scan right  3   2   O   E   N<->E   A   I   Y   Q   U   S   T   S   

       final exchange  0   5  [I]  E   N   E   A  [O]  Y   Q   U   S   T   S   

               result          I   E   N   E   A   O   Y   Q   U   S   T   S   


                                           a[]
                       i   j   0   1   2   3   4   5   6   7   8   9  10  11  

       initial values  0  11  [E]  A   S   Y   Q   U   E   S   T   I   O   N   

scan left, scan right  2   6   E   A  [S]  Y   Q   U  [E]  S   T   I   O   N   

             exchange  2   6   E   A  [E]  Y   Q   U  [S]  S   T   I   O   N  
             
scan left, scan right  3   2   E   A   S<->Y   Q   U   E   S   T   I   O   N                 

       final exchange  0   2  [E]  A  [E]  Y   Q   U   S   S   T   I   O   N   

               result          E   A   E   Y   Q   U   S   S   T   I   O   N   


    
  
    */
    
    /*

    for random Double arrays topDown cutoff -1 quick cutoff -1 trials 10
    array                      average times
    length              topDown             quick
        10                0.500            15.000
       100                0.100             8.600
        1K                1.900            82.300
       10K                6.200           742.700

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
    
//    Random q = SecureRandom.getInstanceStrong(); 
////    Integer[] b;  b = rangeInteger(1,30);
//    int[] b = range(1,30);
//    shuffle(b,q);
//    par(b);
//    quickInt(b);
////    quick(b);
////    topDown(b);
////    topDownFm(b);
////    topDownCo(b,30);
////    topDownAc(b);
////    topDownAm(b);
////    topDownSm(b);
////    topDownAcCoSm(b,16);
////    bottomUp(b);
////    bottomUpFm(b);
////    bottomUpCo(b,-1);
////    bottomUpSm(b);
////    bottomUpCoFmSm(b,10);
////    natural(b);
////    naturalFm(b);
////    naturalSm(b);
////    naturalCo(b,-1);
////    naturalCoFmSm(b,50);
//    par(b);
//    isSorted(b);
//    System.exit(0);
    
//    Random r = new Random(System.currentTimeMillis()); Integer[] a;
//    int c = 0;
//    for (int i = 1; i < 10001; i++) {
//      a = rangeInteger(0,i);
//      shuffle(a,r);
//      quick(a);
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
