package sort;

import static v.ArrayUtils.*;
import static java.lang.reflect.Array.*;
import static analysis.Log.*;

import java.awt.Font;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.princeton.cs.algs4.StdDraw;
import v.Tuple2;


//p273 ALGORITHM 2.4 Top-down mergesort

@SuppressWarnings("unused")
public class TopDownMerge {

  private static final int INSERTIONSORT_CUTOFF = 7; 

  public static int sortindent = 0;

  public static int mergeindent = 0;

  public static int writes = 0;  // write array access for ex2.2.6 p284

  public static int reads = 0; // read array access for ex2.2.6 p284

  private static int compares = 0; // for ex 2.2.7 p284 compares monotonically increase
  
  private static int tcompares = 0; // for per-iteration compares counting
 
  public static double[] data = null; //data for ex2.2.6 p284

  public static Object[] ttt = null;

  private TopDownMerge(){}

  public static <T extends Comparable<? super T>> void sort(T[] a) {
    @SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    sort(a, aux, 0, a.length - 1);
  }

  // ex 2.2.2 p284 for tracing
  public static <T extends Comparable<? super T>> void sort2(T[] a) {
    @SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    ttt = new Object[a.length];
    sort2(a, aux, 0, a.length - 1);
  }

  //ex 2.2.5 p284  
  public static <T extends Comparable<? super T>> void sort3(T[] a) {
    //System.out.println("sort("+arrayToString(a,1000,1,1)+")");
    @SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    sort3(a, aux, 0, a.length - 1);
  }

  // for performance improvements
  public static <T extends Comparable<? super T>> void sort4(T[] a) {
    //System.out.println("sort("+arrayToString(a,1000,1,1)+")");
    //@SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    //    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    T[] aux = a.clone();
    sort4(aux, a, 0, a.length - 1);
  }

  //ex 2.2.6 array accesses
  public static <T extends Comparable<? super T>> void sort5(T[] a) {
    @SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length);
    writes += a.length;
    sort5(a, aux, 0, a.length - 1);
  }

  //ex 2.2.7 p284 compares monotonically increase
  public static <T extends Comparable<? super T>> void sort6(T[] a) {
    @SuppressWarnings("unchecked")
    // // Allocate space for aux just once.
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length);
    //    writes += a.length;
    compares = 0;
    sort6(a, aux, 0, a.length - 1);
  }

  public static void sortInt(int[] a) {
    int[] aux = new int[a.length];
    compares = 0;
    sortInt(a, aux, 0, a.length - 1);
  }

  private static void sortInt(int[]a, int[] aux, int lo, int hi) {
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sortInt(a, aux, lo, mid);
    sortInt(a, aux, mid+1, hi); // Sort right half.
    mergeInt(a, aux, lo, mid, hi);  
  }

  private static <T extends Comparable<? super T>>void sort(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort(a, aux, lo, mid); // Sort left half.
    sort(a, aux, mid+1, hi); // Sort right half.
    merge(a, aux, lo, mid, hi); // Merge results (code on page 271).
  }

  //ex 2.2.2 p284 for tracing
  private static <T extends Comparable<? super T>>void sort2(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (lo != hi) {
      System.out.println(repeat(' ',sortindent*2)+"sort(a,"+lo+","+hi+")");
      sortindent++; mergeindent = sortindent;
    }
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort2(a, aux, lo, mid); // Sort left half.
    sort2(a, aux, mid+1, hi); // Sort right half.
    merge2(a, aux, lo, mid, hi); // Merge results (code on page 271).
  }

  // for ex2.2.5 p284 
  private static <T extends Comparable<? super T>>void sort3(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    //    System.out.println((mid-lo+1)+" "+(hi-mid));
    sort3(a, aux, lo, mid); // Sort left half.
    sort3(a, aux, mid+1, hi); // Sort right half.
    merge3(a, aux, lo, mid, hi); // Merge results (code on page 271).
    //    System.out.println((mid-lo+1)+" "+(hi-mid) + " lo="+lo+" mid="+mid+" hi="+hi);
    //    System.out.print("("+(mid-lo+1)+","+(hi-mid)+"), ");
    System.out.print(""+((mid-lo+1)+(hi-mid))+",");
  }

  // for performance improvements
  private static <T extends Comparable<? super T>> void sort4(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
//    if (lo != hi) {
//      System.out.println(repeat(' ',sortindent*2)+"sort(a,"+lo+","+hi+")");
//      sortindent++; mergeindent = sortindent;
//    }
    //    pa(aux,1000,1,1);
    if (hi <= lo + INSERTIONSORT_CUTOFF) { 
      insertionSort4(aux, lo, hi);
      return;
    }
    //    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort4(aux, a, lo, mid); // Sort left half.
    sort4(aux, a, mid+1, hi); // Sort right half.
    // exercises 2.2.8 and 2.2.11
    if (!less4(a[mid+1], a[mid])) {
      System.arraycopy(a, lo, aux, lo, hi - lo + 1);
      return;
    }
    merge4(a, aux, lo, mid, hi); 
  }

  //ex 2.2.6 plot array accesses
  private static <T extends Comparable<? super T>> void sort5(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort5(a, aux, lo, mid); // Sort left half.
    sort5(a, aux, mid+1, hi); // Sort right half.
    merge5(a, aux, lo, mid, hi); // Merge results (code on page 271).
  }

  //ex 2.2.7 p284 compares monotonically increase
  private static <T extends Comparable<? super T>>void sort6(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    //    System.out.println("hi="+hi);
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort6(a, aux, lo, mid); // Sort left half.
    sort6(a, aux, mid+1, hi); // Sort right half.
    merge6(a, aux, lo, mid, hi); // Merge results (code on page 271).
  }

  //p271
  public static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
  }

  //ex 2.2.2 p284 for tracing
  public static <T extends Comparable<? super T>> void merge2(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    System.out.printf("%20s", repeat(' ',mergeindent*2)+"merge(a,"+lo+","+mid+","+hi+")   ");
    mergeindent--;
    sortindent--;
    // Merge a[lo..mid] with a[mid+1..hi].
    //    System.out.println("a.length="+a.length+" lo="+lo+" mid="+mid+" hi="+hi);
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    //    pa(aux,1000,1,1);
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
    show2(a, lo, hi);
  }

  //for ex2.2.5 p284  
  public static <T extends Comparable<? super T>> void merge3(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // Merge a[lo..mid] with a[mid+1..hi].
    //    System.out.println("a.length="+a.length+" lo="+lo+" mid="+mid+" hi="+hi);
    int i = lo, j = mid+1;
    //    System.out.println("i="+i+" j="+j);
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
  }

  // for performance improvements
  public static <T extends Comparable<? super T>> void merge4(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // Merge a[lo..mid] with a[mid+1..hi].
//    System.out.print(repeat(' ',mergeindent*2)+"merge(a,"+lo+","+mid+","+hi+")");
//    mergeindent--;
//    sortindent--;
    int i = lo, j = mid+1;
//    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
//      aux[k] = a[k];
    //    pa(aux,1000,1,1);
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if      (i > mid)               aux[k] = a[j++];
      else if (j > hi )               aux[k] = a[i++];
      else if (less4(a[j], a[i]))     aux[k] = a[j++];
      else                            aux[k] = a[i++];
    }
  }

  //ex 2.2.6 plot array accesses
  public static <T extends Comparable<? super T>> void merge5(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) {// Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
      writes++; reads++;
    }
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) { a[k] = aux[j++]; writes++; reads++; }
      else if (j > hi ) { a[k] = aux[i++]; writes++; reads++; }
      else if (less(aux[j], aux[i])) { a[k] = aux[j++]; writes++; reads += 3;}
      else { a[k] = aux[i++]; writes++; reads += 3; }
    }
  }

  //ex 2.2.7 p284 compares monotonically increase
  public static <T extends Comparable<? super T>> void merge6(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    //    System.out.print(""+lo+", "+mid+", "+hi);
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) {
        a[k] = aux[j++];
        compares++;
      }
      else {
        compares++;
        a[k] = aux[i++];
      }
    }
    //    System.out.println(", "+compares);
  }

  public static <T extends Comparable<? super T>> void mergeInt(
      int[] a, int[] aux, int lo, int mid, int hi) { 
    tcompares = 0;
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++)
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) {
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (aux[j] < aux[i]) {
        a[k] = aux[j++];
        compares++; tcompares++;
      }
      else {
        compares++; tcompares++;
        a[k] = aux[i++];
      }
    }
//    System.out.println("tcompares=" +tcompares);
  }

  // for performance improvement
  private static <T extends Comparable<? super T>> void insertionSort(T[] a, int lo, int hi) {
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
  }
  
  private static <T extends Comparable<? super T>> void insertionSort4(T[] a, int lo, int hi) {
    System.out.println("insertionSort4");
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && less4(a[j], a[j-1]); j--)
        exch(a, j, j-1);
  }

  // for insertionSort
  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> boolean less4(T v, T w) {
    compares++;
    return v.compareTo(w) < 0; 
  }

  //ex 2.2.7 p284 compares monotonically increase
  private static <T extends Comparable<? super T>> boolean less6(T v, T w) {
//    compares++;
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  private static <T extends Comparable<? super T>> void show2(T[] a, int lo, int hi) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++) {
      if (i+1 == lo) {
        System.out.print(a[i] + " ");
      } else if (i == lo) {
        System.out.print("{"+a[i]+"  "); 
      } else if (i == hi) {
        System.out.print(a[i] + "} ");
      } else System.out.print(a[i] + "  ");
    }
    System.out.println();
  }

  // ex2.2.6
  public static double[] collectArrayAccessData(int n) {
    // for ex2206 collect the number of array accesses for top-down
    // mergesort for random Double arrays of length 1 to n and return
    // it in a double[] of length n
    double[] d = new double[n];
    Double[] t = null;
    Random r = new Random(System.currentTimeMillis());
    for (int i = 1; i <= n; i++) {
      t = new Double[i];
      for (int j = 0; j < i; j++) t[j] = r.nextDouble();
      writes = 0; reads = 0;
      sort5(t);
      d[i-1] = (double)(writes + reads);
    }
    return d;
  }

  // ex2.2.6
  public static void plotTopDownMergeSortArrayAccesses(int n) {
    System.out.println("top-down mergesort:");
    double[] d = collectArrayAccessData(n);
    System.out.println("max array accesses for sorting one array="+max(d));
    double[] d2 = new double[n];
    for (int i = 0; i < n; i++) d2[i] = 6.*(i+1)*lg(i+1);
    StdDraw.setCanvasSize(700, 700);
    double maxY = Math.max(max(d), max(d2)); 
    System.out.println("max array accesses upper bound="+maxY); //maxY=27648.0 due to d2 for N = 512
    double maxYscaled = 1.10*maxY;
    int maxX = n;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled+1);
    StdDraw.setYscale(-1-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.01);
    Font font = new Font("Arial", Font.BOLD, 20);
    StdDraw.setFont(font);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(maxXscaled/2, .97*maxYscaled, "Array accesses top-down mergesort "
        +"for array lengths from 1 to "+n);
    StdDraw.point(0., 0.);
    for (int i = 0; i < n; i++) StdDraw.point(i+1, d[i]);
    StdDraw.setPenColor(StdDraw.BOOK_RED);
    // this plots the worst case number of array accesses
    StdDraw.textLeft(maxXscaled/5, .75*maxYscaled, "In red is upper bound 6NlgN");
    for (int i = 0; i < n; i++) StdDraw.point(1.+i, d2[i]);
  }

  public static void printComparesVsLgNfactorial() {
    // print the number of compares vs. lg(arrayLength! for reverse sorted
    // arrays of lengths 2-170.  
    Integer[] ia3 = null; int n;
    System.out.println("\nNumber of compares vs. lg(N!) for top-down mergesort:");
    System.out.println("    n  compares       lg(n!)       lg(n!)/compares");   
    for (int i = 1; i < 171; i++) {
      ia3 = rangeInteger(i,0);
      n = ia3.length;
      double f = lg(factorial(new BigInteger(""+n)).doubleValue());
      compares = 0; 
      sort6(ia3);
      System.out.printf("%5d     %5d      %8.3f       %5.3f\n", 
          n, compares, f, f/compares); 
    }
  }

  public static <T extends Comparable<? super T>> void printNumberOfComparisons(T[] a) {
    compares = 0;
    sort6(a);
    System.out.println(compares);
  }

  public static <T extends Comparable<? super T>> int comparisons(T[] a) {
    compares = 0;
    sort6(a);
    return compares;
  }
  
  public static <T extends Comparable<? super T>> int comparisons4(T[] a) {
    compares = 0;
    sort4(a);
    return compares;
  }
  
  public static <T extends Comparable<? super T>> int comparisonsIntArray(int[] a) {
    compares = 0;
    sortInt(a);
    return compares;
  }


  public static int[] insert(int[] z, int e, int p) {
    // return the array formed by inserting e into z at index p and shifting elements
    // in z starting at p up one. if p == z.length then e is appended to z.
    if (z == null) throw new IllegalArgumentException("insert: z can't be null");
    if (p < 0 || p > z.length) 
      throw new IllegalArgumentException("insert: p must be >= 0 and < z.length+1");
    int[] y = new int[z.length+1];
    if (p == 0) {
      y[0] = e; for (int i = 0; i < z.length; i++) y[i+1] = z[i];
      return y;
    }
    else if (p == z.length) {
      for (int i = 0; i < z.length; i++) y[i] = z[i]; y[z.length] = e;
      return y;
    }
    else {
      for (int i = 0; i < p; i++) y[i] = z[i]; y[p] = e;
      for (int i = p+1; i < y.length; i++) y[i] = z[i-1];
      return y;
    }
  }

  public static <T> T[] insert(T[] z, T e, int p) {
    // return the array formed by inserting e into z at index p and shifting elements
    // in z starting at p up one. if p == z.length then e is appended to z.
    if (z == null) throw new IllegalArgumentException("insert: z can't be null");
    if (e == null) throw new IllegalArgumentException("insert: e can't be null");
    if (p < 0 || p > z.length) 
      throw new IllegalArgumentException("insert: p must be >= 0 and < z.length+1");
    T[] y = ofDim(e.getClass(), z.length+1);
    if (p == 0) {
      y[0] = e; for (int i = 0; i < z.length; i++) y[i+1] = z[i];
      return y;
    }
    else if (p == z.length) {
      for (int i = 0; i < z.length; i++) y[i] = z[i]; y[z.length] = e;
      return y;
    }
    else {
      for (int i = 0; i < p; i++) y[i] = z[i]; y[p] = e;
      for (int i = p+1; i < y.length; i++) y[i] = z[i-1];
      return y;
    }
  }

  public static Iterator<int[]> intArrayIterator(int[][] a) {
    return new Iterator<int[]>() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() { return c < len; }
      public int[] next() { return a[c++];}
    };
  }

  public static Iterator<Integer[]> integerArrayIterator(Integer[][] a) {
    return new Iterator<Integer[]>() {
      int len = a.length;
      int c = 0;
      public boolean hasNext() { return c < len; }
      public Integer[] next() { return a[c++];}
    };
  }

  public static Iterator<int[]> insertions(int[] z, int e) {
    // return the Iterator<T[]> of all insertions of e into z
    if (z == null) throw new IllegalArgumentException("insert: z can't be null");
    //    if (e == null) throw new IllegalArgumentException("insert: e can't be null");
    int[][] y = new int[z.length+1][z.length+1];
    for (int i = 0; i <= z.length; i++) y[i] = insert(z, e, i);
    //    for (int i = 0; i < z.length+1; i++) pa(z[i],-1);
    return intArrayIterator(y);
  }

  public static Iterator<Integer[]> insertions(Integer[] z, int e) {
    // return the Iterator<T[]> of all insertions of e into z
    if (z == null) throw new IllegalArgumentException("insert: z can't be null");
    //    if (e == null) throw new IllegalArgumentException("insert: e can't be null");
    Integer[][] y = new Integer[z.length+1][z.length+1];
    for (int i = 0; i <= z.length; i++) y[i] = insert(z, e, i);
    //    for (int i = 0; i < z.length+1; i++) pa(z[i],-1);
    return integerArrayIterator(y);
  }

  public static void comparesInversionSearchIntegerArrays() {
    // print arrays a and b where b is constructed by inserting an additional
    // element into a and mergesort compares(b) >= compares(a)
    // the lengths of a ranges from 2-7
    List<Tuple2<Integer[],Integer[]>> foundList = new ArrayList<>();
    Iterator<Integer[]> it; Iterator<Integer[]> in;
    Integer[] a; Integer[] a1; Integer[] b; Integer[] c; Object[] oa;
    int comparesa = 0; int comparesb = 0;
    int[] rangeMax = {6,8,10,12,14,16,18,24,26}; 
    for (int i = 0; i < rangeMax.length-1; i++) {
      a = rangeInteger(2,rangeMax[i],2);
      a1 = new Integer[a.length];
      c = rangeInteger(2,rangeMax[i+1],2);
      it = permutations(a);
      while (it.hasNext()) {
        oa = it.next();
        for (int j = 0; j < a.length; j++) a1[j] = (Integer)oa[j];
        comparesa = comparisons(a1.clone());
        for (int j = 0; j < c.length; j++) {
          in = insertions(a1,c[j]-1);
          LOOP:
            while (in.hasNext()) {
              b = in.next();
              comparesb = comparisons(b.clone());
              if (comparesb < comparesa) {
                for (Tuple2<Integer[],Integer[]> t : foundList)
                  if (Arrays.equals(t._1, b) && Arrays.equals(t._2,a1)) continue LOOP;  
                foundList.add(new Tuple2<Integer[],Integer[]>(b,a1));
                System.out.printf("%2d      %-40s    %2d      %-40s\n", comparesb,
                    arrayToString(b,-1), comparesa, arrayToString(a1,-1));
              }
            }
        }
      }
    }
  }
  
  public static void comparesInversionSearchIntArrays() {
    // print arrays a and b where b is constructed by inserting an additional
    // element into a and mergesort compares(b) < compares(a)
    // the lengths of a ranges from 2-7
    System.out.println("compares b                 array b             compares a                   array a");
    List<Tuple2<int[],int[]>> foundList = new ArrayList<>();
    Iterator<int[]> it; Iterator<int[]> in;
    int[] a; int[] a1; int[] b; int[] c;
    int comparesa = 0; int comparesb = 0;
    int[] rangeMax = {6,8,10,12,14,16,18,24,26}; 
    for (int i = 0; i < rangeMax.length-1; i++) {
      a = range(2,rangeMax[i],2);
      c = range(2,rangeMax[i+1],2); //System.out.println("b="+arrayToString(b,-1));
      it = permutations(a);
      while (it.hasNext()) {
        a1 = it.next();
        comparesa = comparisonsIntArray(a1.clone());
        for (int j = 0; j < c.length; j++) {
          in = insertions(a1,c[j]-1);
          LOOP:
            while (in.hasNext()) {
              b = in.next();
              comparesb = comparisonsIntArray(b.clone());
              if (comparesb < comparesa) {
                for (Tuple2<int[],int[]> t : foundList)
                  if (Arrays.equals(t._1, b) && Arrays.equals(t._2,a1)) continue LOOP;  
                foundList.add(new Tuple2<int[],int[]>(b,a1));
                System.out.printf("%2d     %35s     %2d     %35s\n", comparesb,
                    arrayToString(b,-1), comparesa, arrayToString(a1,-1));
              }
            }
        }
      }
    }
  }

  public static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

//    comparesInversionSearchIntArrays();
//    comparesInversionSearchIntegerArrays();

//    Integer[] ia5 = {2,8,10,12,4,6};
//    System.out.println(comparisons(ia5));
//    pa(ia5,-1);
    
    int[] ia6 = {4,0,6,2,5,1,7,3};
    sortInt(ia6);
    pa(ia6,-1);

//    Integer[] ia4 = {3,7,1,5,2,6,0,4}; //{4,0,6,2,5,1,7,3};
//    System.out.println(comparisons4(ia4));
//    pa(ia4,-1);
       

 /*
       7      [2,4,1,10,8,6]                     8      [2,4,10,8,6]                  
       7      [2,4,1,8,10,6]                     8      [2,4,8,10,6]                  
       7      [4,2,1,8,10,6]                     8      [4,2,8,10,6]                  
       7      [3,4,2,8,10,6]                     8      [4,2,8,10,6]                  
       7      [4,3,2,8,10,6]                     8      [4,2,8,10,6]                  
       7      [5,4,2,8,10,6]                     8      [4,2,8,10,6]
       10     [2,8,10,12,4,6,1]                 11      [2,8,10,12,4,6]               
       10     [2,8,10,12,4,6,3]                 11      [2,8,10,12,4,6]                
*/
    //    compares = 0;
    //    sort6(ia4);
    //    System.out.println(compares); //4
    //    
    //    Integer[] ia5 = rangeInteger(5,0);
    //    compares = 0;
    //    sort6(ia5);
    //    System.out.println(compares); //5
    //    
    //    printComparesVsLgNfactorial();

    //    Integer[] ia2 = fill(9, ()->(new Integer(1))); Integer[] ia3 = ia2.clone();
    //    Integer[] ia2 = rangeInteger(1,10); Integer[] ia3 = ia2.clone();
    //
    //    sort3(ia2);  // {4,3,2,1} -> 2,2,4  //2.67 avg length, 8 total length    
    //               // {5,4,3,2,1} -> 2,3,2,5 //2.4, 12
    //             // {6,5,4,3,2,1} -> 2,3,2,3,6 //2.67, 16
    //           // {7,6,5,4,3,2,1} -> 2,2,4,2,3,7 //2.57, 20
    //         // {8,7,6,5,4,3,2,1} -> 2,2,4,2,2,4,8 //3, 24
    //       // {9,8,7,6,5,4,3,2,1} -> 2,3,2,5,2,2,4,9 //3.22, 29
    ////                              
    //    
    ////    System.out.println();
    //    Integer[] ia3 = null; int n;
    //                      //    2         1       1.000
    //    System.out.println("    n  compares       lg(n!)       lg(n!)/compares");   
    //    for (int i = 2; i < 171; i++) {
    //      ia3 = rangeInteger(i,0);
    //      n = ia3.length;
    //      double f = lg(factorial(new BigInteger(""+n)).doubleValue());
    //      compares = 0;
    //      sort6(ia3);
    //      System.out.printf("%5d     %5d      %8.3f       %5.3f\n", 
    //          n, compares, f, f/compares); 
    //    }

    /*  {4,3,2,1} merges       {5,4,3,2,1} merges 
    lo mid hi totmerges    lo mid hi totmerges
     0, 0, 1, 1             0, 0, 1, 1
     2, 2, 3, 2             0, 1, 2, 2
     0, 1, 3, 4             3, 3, 4, 3
                            0, 2, 4, 5


     */
    //    pa(ia2,-1);

    //    plotTopDownMergeSortArrayAccesses(512);   
    //    
    //    writes = 0;
    //    Integer[] ia = rangeInteger(0,39);
    //    sort3(ia);
    //    pa(ia,-1);
    //    System.out.println(writes);
    //    System.out.println(reads);
    //    System.out.println(writes+reads);



    //p273 Trace of merge results for top-down mergesort
    //    String[] sc = "M E R G E S O R T E X A M P L E".split("\\s+");
    //    sort2(sc);
    //    show2(sc, -1, -1);
    /*                            0 
          merge(a,0,0,1)      E M R G E S O R T E X A M P L E 
          merge(a,2,2,3)      E M G R E S O R T E X A M P L E 
        merge(a,0,1,3)        E G M R E S O R T E X A M P L E 
          merge(a,4,4,5)      E G M R E S O R T E X A M P L E 
          merge(a,6,6,7)      E G M R E S O R T E X A M P L E 
        merge(a,4,5,7)        E G M R E O R S T E X A M P L E 
      merge(a,0,3,7)          E E G M O R R S T E X A M P L E 
          merge(a,8,8,9)      E E G M O R R S E T X A M P L E 
          merge(a,10,10,11)   E E G M O R R S E T A X M P L E 
        merge(a,8,9,11)       E E G M O R R S A E T X M P L E 
          merge(a,12,12,13)   E E G M O R R S A E T X M P L E 
          merge(a,14,14,15)   E E G M O R R S A E T X M P E L 
        merge(a,12,13,15)     E E G M O R R S A E T X E L M P 
      merge(a,8,11,15)        E E G M O R R S A E E L M P T X 
    merge(a,0,7,15)           A E E E E G L M M O P R R S T X 
  A E E E E G L M M O P R R S T X 


     */
    // ex 2.2.2 p 284
    // for Top-down mergesort call trace
    //    String[] sb = "E A S Y Q U E S T I O N".split("\\s+");
    //    sort2(sb);
    //    show(sb);

    // for Trace of merge results for top-down mergesort
    //    sb = "E A S Y Q U E S T I O N".split("\\s+");
    //    sort2(sb);
    //    show2(sb, -1, -1);

    /* p284
  2.2.2  Give traces, in the style of the trace given with Algorithm 2.4, showing
   how thekeys  E A S Y Q U E S T I O N  are sorted with top-down mergesort.

   Top-down mergesort call trace:

          sort(a,0,11)
            sort(a,0,5)               sort right half
              sort(a,0,2)
                sort(a,0,1)
                  merge(a,0,0,1)
                merge(a,0,1,2)
              sort(a,3,5)
                sort(a,3,4)
                  merge(a,3,3,4)
                merge(a,3,4,5)
              merge(a,0,2,5)
            sort(a,6,11)              sort left half
              sort(a,6,8)
                sort(a,6,7)
                  merge(a,6,6,7)
                merge(a,6,7,8)
              sort(a,9,11)
                sort(a,9,10)
                  merge(a,9,9,10)
                merge(a,9,10,11)
              merge(a,6,8,11)
            merge(a,0,5,11)           merge results


     Trace of merge results for top-down mergesort:

                 lo   hi                  [a]
                  |   |     0  1  2  3  4  5  6  7  8  9 10 11  
                  V   V    -----------------------------------
          merge(a,0,0,1)   {A  E} S  Y  Q  U  E  S  T  I  O  N  
        merge(a,0,1,2)     {A  E  S} Y  Q  U  E  S  T  I  O  N  
          merge(a,3,3,4)    A  E  S {Q  Y} U  E  S  T  I  O  N  
        merge(a,3,4,5)      A  E  S {Q  U  Y} E  S  T  I  O  N  
      merge(a,0,2,5)       {A  E  Q  S  U  Y} E  S  T  I  O  N  
          merge(a,6,6,7)    A  E  Q  S  U  Y {E  S} T  I  O  N  
        merge(a,6,7,8)      A  E  Q  S  U  Y {E  S  T} I  O  N  
          merge(a,9,9,10)   A  E  Q  S  U  Y  E  S  T {I  O} N  
        merge(a,9,10,11)    A  E  Q  S  U  Y  E  S  T {I  N  O} 
      merge(a,6,8,11)       A  E  Q  S  U  Y {E  I  N  O  S  T} 
    merge(a,0,5,11)        {A  E  E  I  N  O  Q  S  S  T  U  Y} 


     */    

    //    // for ex 2.2.1 p 284
    ////    String[] sa = "E E G M R A C E R T".split("\\s+");
    //    String[] sb = "A E Q S U Y E I N O S T".split("\\s+");
    //    sort2(sb);
    //    show(sb);
    /*
    i=0 j=1
    i=1 j=1
    [A,null,null,null,null,null,null,null,null,null,null,null]
    i=1 j=2
    [A,E,null,null,null,null,null,null,null,null,null,null]
    i=0 j=2
    i=1 j=2
    [A,E,null,null,null,null,null,null,null,null,null,null]
    i=2 j=2
    [A,E,null,null,null,null,null,null,null,null,null,null]
    i=2 j=3
    [A,E,Q,null,null,null,null,null,null,null,null,null]
    i=3 j=4
    i=4 j=4
    [A,E,Q,S,null,null,null,null,null,null,null,null]
    i=4 j=5
    [A,E,Q,S,U,null,null,null,null,null,null,null]
    i=3 j=5
    i=4 j=5
    [A,E,Q,S,U,null,null,null,null,null,null,null]
    i=5 j=5
    [A,E,Q,S,U,null,null,null,null,null,null,null]
    i=5 j=6
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=0 j=3
    i=1 j=3
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=2 j=3
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=3 j=3
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=3 j=4
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=3 j=5
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=3 j=6
    [A,E,Q,S,U,Y,null,null,null,null,null,null]
    i=6 j=7
    i=7 j=7
    [A,E,Q,S,U,Y,E,null,null,null,null,null]
    i=7 j=8
    [A,E,Q,S,U,Y,E,I,null,null,null,null]
    i=6 j=8
    i=7 j=8
    [A,E,Q,S,U,Y,E,I,null,null,null,null]
    i=8 j=8
    [A,E,Q,S,U,Y,E,I,null,null,null,null]
    i=8 j=9
    [A,E,Q,S,U,Y,E,I,N,null,null,null]
    i=9 j=10
    i=10 j=10
    [A,E,Q,S,U,Y,E,I,N,O,null,null]
    i=10 j=11
    [A,E,Q,S,U,Y,E,I,N,O,S,null]
    i=9 j=11
    i=10 j=11
    [A,E,Q,S,U,Y,E,I,N,O,S,null]
    i=11 j=11
    [A,E,Q,S,U,Y,E,I,N,O,S,null]
    i=11 j=12
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=6 j=9
    i=7 j=9
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=8 j=9
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=9 j=9
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=9 j=10
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=9 j=11
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=9 j=12
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=0 j=6
    i=1 j=6
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=2 j=6
    [A,E,Q,S,U,Y,E,I,N,O,S,T]
    i=2 j=7
    [A,E,E,S,U,Y,E,I,N,O,S,T]
    i=2 j=8
    [A,E,E,I,U,Y,E,I,N,O,S,T]
    i=2 j=9
    [A,E,E,I,N,Y,E,I,N,O,S,T]
    i=2 j=10
    [A,E,E,I,N,O,E,I,N,O,S,T]
    i=3 j=10
    [A,E,E,I,N,O,Q,I,N,O,S,T]
    i=4 j=10
    [A,E,E,I,N,O,Q,S,N,O,S,T]
    i=4 j=11
    [A,E,E,I,N,O,Q,S,S,O,S,T]
    i=4 j=12
    [A,E,E,I,N,O,Q,S,S,T,S,T]
    i=5 j=12
    [A,E,E,I,N,O,Q,S,S,T,U,T]
    i=6 j=12
    [A,E,E,I,N,O,Q,S,S,T,U,Y]
    A E E I N O Q S S T U Y 
     */

    //    Double[] d = rangeDouble(10.,-.5,-.5);
    //    pa(d,1000,1,1);
    //    sort(d);
    //    show(d);
    //    
    //    Integer[] ia = rangeInteger(0,6);
    ////    sort3(ia);
    ////    show(ia);
    ///*    
    //    sort(a,0,5)
    //      sort(a,0,2)
    //        sort(a,0,1)
    //          merge(a,0,0,1)
    //        merge(a,0,1,2)
    //      sort(a,3,5)
    //        sort(a,3,4)
    //          merge(a,3,3,4)
    //        merge(a,3,4,5)
    //      merge(a,0,2,5)
    //    0 1 2 3 4 5 
    //*/
    //    
    //    ia = rangeInteger(0,6);
    ////    sort4(ia);
    ////    show(ia);
    ///*  before implementing insertionSort with CUTOFF4INSERTIONSORT = 15 
    //    sort(a,0,5)
    //      sort(a,0,2)
    //        sort(a,0,1)
    //          sort(a,3,5)
    //            sort(a,3,4)
    //    0 1 2 3 4 5 
    //*/
    //    
    //    ia = rangeInteger(0,6);
    //    sort4(ia);
    //    show(ia);
    ///*  after implementing insertionSort with CUTOFF4INSERTIONSORT = 15 
    //    sort(a,0,5)
    //    0 1 2 3 4 5 
    //*/   

  }

}
