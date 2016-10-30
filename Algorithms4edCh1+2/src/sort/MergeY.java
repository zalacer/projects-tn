package sort;

import static v.ArrayUtils.*;

import java.util.Comparator;

public class MergeY {
  private static final int CUTOFF = 7;  // cutoff to insertion sort

  private static int compares = 0;

  // This class should not be instantiated.
  private MergeY() { }

  private static <T extends Comparable<? super T>> void merge(
      T[] src, T[] dst, int lo, int mid, int hi) {

    // precondition: src[lo .. mid] and src[mid+1 .. hi] are sorted subarrays
//    assert isSorted(src, lo, mid);
//    assert isSorted(src, mid+1, hi);

    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) {
      if      (i > mid)              dst[k] = src[j++];
      else if (j > hi)               dst[k] = src[i++];
      else if (less(src[j], src[i])) dst[k] = src[j++];   // to ensure stability
      else                           dst[k] = src[i++];
    }

    // postcondition: dst[lo .. hi] is sorted subarray
//    assert isSorted(dst, lo, hi);
  }

  private static <T extends Comparable<? super T>> void sort(
      T[] src, T[] dst, int lo, int hi) {
    // if (hi <= lo) return;
    if (hi <= lo + CUTOFF) { 
      insertionSort(dst, lo, hi);
      return;
    }
    int mid = lo + (hi - lo) / 2;
    sort(dst, src, lo, mid);
    sort(dst, src, mid+1, hi);

    // if (!less(src[mid+1], src[mid])) {
    //    for (int i = lo; i <= hi; i++) dst[i] = src[i];
    //    return;
    // }
    // using System.arraycopy() is a bit faster than the above loop
    if (!less(src[mid+1], src[mid])) {
      System.arraycopy(src, lo, dst, lo, hi - lo + 1);
      return;
    }

    merge(src, dst, lo, mid, hi);
  }

  /**
   * Rearranges the array in ascending order, using the natural order.
   * @param a the array to be sorted
   */
  public static <T extends Comparable<? super T>> void sort(T[] a) {
    T[] aux = a.clone();
    sort(aux, a, 0, a.length-1);  
//    assert isSorted(a);
  }

  // sort from a[lo] to a[hi] using insertion sort
  private static <T extends Comparable<? super T>> void insertionSort(T[] a, int lo, int hi) {
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
  }


  /*******************************************************************
   *  Utility methods.
   *******************************************************************/

  // exchange a[i] and a[j]
  private static void exch(Object[] a, int i, int j) {
    Object swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  // is a[i] < a[j]?
  private static <T extends Comparable<? super T>> boolean less(T a, T b) {
    compares++;
    return a.compareTo(b) < 0;
  }

  // is a[i] < a[j]?
  private static <T> boolean less(
      T a, T b, Comparator<T> comparator) {
    return comparator.compare(a, b) < 0;
  }
  
  public static <T extends Comparable<? super T>> int comparisons(T[] a) {
    compares = 0;
    sort(a);
    return compares;
  }


  /*******************************************************************
   *  Version that takes Comparator as argument.
   *******************************************************************/

  /**
   * Rearranges the array in ascending order, using the provided order.
   * @param a the array to be sorted
   */
  public static <T> void sort(T[] a, Comparator<T> comparator) {
    T[] aux = a.clone();
    sort(aux, a, 0, a.length-1, comparator);
    assert isSorted(a, comparator);
  }

  private static <T> void merge(
      T[] src, T[] dst, int lo, int mid, int hi, Comparator<T> comparator) {

    // precondition: src[lo .. mid] and src[mid+1 .. hi] are sorted subarrays
    assert isSorted(src, lo, mid, comparator);
    assert isSorted(src, mid+1, hi, comparator);

    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) {
      if      (i > mid)                          dst[k] = src[j++];
      else if (j > hi)                           dst[k] = src[i++];
      else if (less(src[j], src[i], comparator)) dst[k] = src[j++];
      else                                       dst[k] = src[i++];
    }

    // postcondition: dst[lo .. hi] is sorted subarray
    assert isSorted(dst, lo, hi, comparator);
  }


  private static <T> void sort(T[] src, T[] dst, int lo, int hi, Comparator<T> comparator) {
    // if (hi <= lo) return;
    if (hi <= lo + CUTOFF) { 
      insertionSort(dst, lo, hi, comparator);
      return;
    }
    int mid = lo + (hi - lo) / 2;
    sort(dst, src, lo, mid, comparator);
    sort(dst, src, mid+1, hi, comparator);

    // if (!less(src[mid+1], src[mid])) {
    //    for (int i = lo; i <= hi; i++) dst[i] = src[i];
    //    return;
    // }
    // using System.arraycopy() is a bit faster than the above loop
    if (!less(src[mid+1], src[mid], comparator)) {
      System.arraycopy(src, lo, dst, lo, hi - lo + 1);
      return;
    }

    merge(src, dst, lo, mid, hi, comparator);
  }

  // sort from a[lo] to a[hi] using insertion sort
  private static <T> void insertionSort(T[] a, int lo, int hi, Comparator<T> comparator) {
    for (int i = lo; i <= hi; i++)
      for (int j = i; j > lo && less(a[j], a[j-1], comparator); j--)
        exch(a, j, j-1);
  }


  /***************************************************************************
   *  Check if array is sorted - useful for debugging.
   ***************************************************************************/
  @SuppressWarnings("unused")
  private static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
    return isSorted(a, 0, a.length - 1);
  }

  private static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
    for (int i = lo + 1; i <= hi; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }

  private static <T> boolean isSorted(T[] a, Comparator<T> comparator) {
    return isSorted(a, 0, a.length - 1, comparator);
  }

  private static <T> boolean isSorted(T[] a, int lo, int hi, Comparator<T> comparator) {
    for (int i = lo + 1; i <= hi; i++)
      if (less(a[i], a[i-1], comparator)) return false;
    return true;
  }

  // print array to standard output
  @SuppressWarnings("unused")
  private static void show(Object[] a) {
    for (int i = 0; i < a.length; i++) {
      System.out.println(a[i]);
    }
  }

  /**
   * Reads in a sequence of strings from standard input; mergesorts them
   * (using an optimized version of mergesort); 
   * and prints them to standard output in ascending order. 
   */
  public static void main(String[] args) {

    Integer[] ia4 = {3,7,1,5,2,6,0,4}; //{4,0,6,2,5,1,7,3};
    System.out.println(comparisons(ia4));
    pa(ia4,-1);

  }
}

/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
