package sort;

import static v.ArrayUtils.*;
import static analysis.Log.lg;
import static java.lang.reflect.Array.*;

import java.awt.Font;
import java.util.Random;

import edu.princeton.cs.algs4.StdDraw;

//p278 Bottom-up mergesort
@SuppressWarnings("unused")
public class BottomUpMerge {
  
  public static int sortindent = 0;
  
  public static int mergeindent = 0;
  
  public static int szindent = 14;
  
 public static int writes = 0;  // write array access for ex2.2.6 p284
  
  public static int reads = 0; // read array access for ex2.2.6 p284
  
  public static double[] data = null; //data for ex2.2.6 p284

  private BottomUpMerge(){}
  
  public static <T extends Comparable<? super T>> void sort(T[] a) {
    // Do lg N passes of pairwise merges.
    int N = a.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
  }

  // ex2.2.3 traces
  public static <T extends Comparable<? super T>> void sort2(T[] a) {
    // Do lg N passes of pairwise merges.
    int N = a.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    for (int sz = 1; sz < N; sz = sz+sz) { // sz: subarray size
      szindent = szindent-2;
      System.out.printf("%s\n", space(szindent)+"sz="+sz);
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge2(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
    }
  }

  // for ex2.2.5 subarrays p284
  public static <T extends Comparable<? super T>> void sort3(T[] a) {
    // Do lg N passes of pairwise merges.
    int N = a.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    for (int sz = 1; sz < N; sz = sz+sz) { // sz: subarray size
//      szindent = szindent-2;
//      System.out.printf("%s\n", space(szindent)+"sz="+sz);
      for (int lo = 0; lo < N-sz; lo += sz+sz) { // lo: subarray index
        System.out.print("("+sz+","+(Math.min(lo+sz+sz-1, N-1)-(lo+sz-1))+"), ");
        merge3(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
      }
    }
  }
  
  // for performance improvements
  public static <T extends Comparable<? super T>> void sort4(T[] a) {
    // Do lg N passes of pairwise merges.
    int N = a.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
  }
  
  //ex 2.2.6 plot array accesses
  public static <T extends Comparable<? super T>> void sort5(T[] a) {
    // Do lg N passes of pairwise merges.
    int N = a.length;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(a.getClass().getComponentType(), a.length); 
    writes += N;
    for (int sz = 1; sz < N; sz = sz+sz) // sz: subarray size
      for (int lo = 0; lo < N-sz; lo += sz+sz) // lo: subarray index
        merge5(a, aux, lo, lo+sz-1, Math.min(lo+sz+sz-1, N-1));
  }
  
  //p271
  public static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // Merge a[lo..mid] with a[mid+1..hi].
//    System.out.println("a.length="+a.length+" lo="+lo+" mid="+mid+" hi="+hi);
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
  
  // ex2.2.3 traces
  public static <T extends Comparable<? super T>> void merge2(
      T[] a, T[] aux, int lo, int mid, int hi) { 
//    int sz = mid-lo+1;
    System.out.printf("%s", space(szindent)+"merge(a,"+lo+","+mid+","+hi+")   ");
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++)
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) {
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
    show2(a, lo, hi);
  }
  
  // for ex2.2.5 subarrays p284
  public static <T extends Comparable<? super T>> void merge3(
      T[] a, T[] aux, int lo, int mid, int hi) { 
//    int sz = mid-lo+1;
//    System.out.printf("%s", space(szindent)+"merge(a,"+lo+","+mid+","+hi+")   ");
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++)
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) {
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (less(aux[j], aux[i])) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
//    show2(a, lo, hi);
  }
  
  // for performance improvements
  public static <T extends Comparable<? super T>> void merge4(
      T[] a, T[] aux, int lo, int mid, int hi) { 
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
  }
   
  //ex 2.2.6 plot array accesses
  public static <T extends Comparable<? super T>> void merge5(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // Merge a[lo..mid] with a[mid+1..hi].
//    System.out.println("a.length="+a.length+" lo="+lo+" mid="+mid+" hi="+hi);
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) {// Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
      writes++; reads++;
    }
//    pa(aux,1000,1,1);
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) { a[k] = aux[j++]; writes++; reads++; }
      else if (j > hi ) { a[k] = aux[i++]; writes++; reads++; }
      else if (less(aux[j], aux[i])) { a[k] = aux[j++];  writes++; reads += 3;}
      else { a[k] = aux[i++]; writes++; reads += 3; }
    }
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
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
  public static void plotBottomUpMergeSortArrayAccesses(int n) {
    System.out.println("bottom-up mergesort:");
    double[] d = collectArrayAccessData(n);
    System.out.println("max array accesses for sorting one array="+max(d));
    double[] d2 = new double[n];
    for (int i = 0; i < n; i++) d2[i] = 6.*(i+1)*lg(i+1);
//    pa(d,-1);
    StdDraw.setCanvasSize(700, 700);
    double maxY = Math.max(max(d), max(d2));
    System.out.println("max array accesses upper bound="+maxY); //maxY=27648.0 due to d2 for N = 512
    double maxYscaled = 1.10*maxY;
    int maxX = d.length;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled+1);
    StdDraw.setYscale(-1-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.01);
    Font font = new Font("Arial", Font.BOLD, 20);
    StdDraw.setFont(font);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.text(maxXscaled/2, .97*maxYscaled, "Array accesses bottom-up mergesort "
        +"for array lengths from 1 to "+n);
    StdDraw.point(0., 0.);
    for (int i = 0; i < n; i++) StdDraw.point(i+1, d[i]);
    StdDraw.setPenColor(StdDraw.BOOK_RED);
    // this plots the worst case number of array accesses 
    StdDraw.textLeft(maxXscaled/5, .75*maxYscaled, "In red is upper bound 6NlgN");
    for (int i = 0; i < n; i++) StdDraw.point(1.+i, d2[i]);
  }
  
  public static void main(String[] args) {
    
    plotBottomUpMergeSortArrayAccesses(512);
    
//    Integer[] ia = rangeInteger(0,39);
//    sort3(ia);
//    pa(ia,-1);
    
//    String[] sa = "E A S Y Q U E S T I O N".split("\\s+");
//    sort2(sa);
//    show2(sa,-1,-1);
 /*
   Trace of merge results for bottom-up mergesort:
    (This is also the call trace except for an initial call to sort{T[]).)
    
                                               [a]
                                0  1  2  3  4  5  6  7  8  9 10 11
            sz=1               -----------------------------------
            merge(a,0,0,1)     {A  E} S  Y  Q  U  E  S  T  I  O  N  
            merge(a,2,2,3)      A  E {S  Y} Q  U  E  S  T  I  O  N  
            merge(a,4,4,5)      A  E  S  Y {Q  U} E  S  T  I  O  N  
            merge(a,6,6,7)      A  E  S  Y  Q  U {E  S} T  I  O  N  
            merge(a,8,8,9)      A  E  S  Y  Q  U  E  S {I  T} O  N  
            merge(a,10,10,11)   A  E  S  Y  Q  U  E  S  I  T {N  O} 
          sz=2
          merge(a,0,1,3)       {A  E  S  Y} Q  U  E  S  I  T  N  O  
          merge(a,4,5,7)        A  E  S  Y {E  Q  S  U} I  T  N  O  
          merge(a,8,9,11)       A  E  S  Y  E  Q  S  U {I  N  O  T} 
        sz=4
        merge(a,0,3,7)         {A  E  E  Q  S  S  U  Y} I  N  O  T  
      sz=8
      merge(a,0,7,11)          {A  E  E  I  N  O  Q  S  S  T  U  Y} 

*/
    
//    Double[] d = rangeDouble(10.,-.5,-.5);
//    pa(d,1000,1,1);
//    sort(d);
//    show(d);
//    
////    Integer[] ia = rangeInteger(0,6);
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
