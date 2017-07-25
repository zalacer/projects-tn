package ex11;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//import static vutils.ArrayUtils.*;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.Scanner;

//  1.1.39 Random matches. Write a BinarySearch client that takes an  int value T as
//  command-line argument and runs T trials of the following experiment for N = 10^3 , 10^4 ,
//  10^5 , and 10^6 : generate two arrays of N randomly generated positive six-digit  int values,
//  and find the number of values that appear in both arrays. Print a table giving the average
//  value of this quantity over the T trials for each value of N.

// Since it seemed to me that multiset intersection is needed for this exercise, I implemented
// that conveniently using maps and omitted use of binary search.

public class Ex1139RandomMatches {
  
  public static int[] randomArray(int size, int digits) {
    // return a random array of ints with digits digits of length size
    if (0 >= size) throw new IllegalArgumentException("randomArray: "
        + "size must be > 0");
    int maxDigits = (""+Integer.MAX_VALUE).length();
    if (0 >= digits || digits > maxDigits) throw new IllegalArgumentException("randomArray: "
        + "digits must be > 0  and <= "+maxDigits);
    int maxSize4MaxDigits = Integer.MAX_VALUE - 8 - (int)Math.pow(10, maxDigits-1) + 1;
    if (digits == maxDigits && size > maxSize4MaxDigits) throw new IllegalArgumentException(
        "randomArray: with digits == "+maxDigits+" the max allowable size is "+maxSize4MaxDigits);
        
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(735632797);
    int min = (int) Math.pow(10,digits-1);
    return r.ints(size, min, min*10).toArray();
  }
  
  public static int[] intersectMultiset(int[] a, int[]b) {
    // return an int[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection.
    if (a == null || b == null) return new int[]{}; 
    Map<Integer,Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++) mapa.merge(a[i], 1, Integer::sum);
    Map<Integer,Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++) mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    int[] c = new int[len];
    int cindex = 0;
    Integer ia; Integer ib; int count;
    for (Integer e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e); ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++) c[cindex++] = e;
      }
    }
    int[] d = take(c,cindex);
    Arrays.sort(d);
    return d;
  }
  
  public static long[] intersectMultiset(long[] a, long[]b) {
    // return a long[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection.
    if (a == null || b == null) return new long[]{}; 
    Map<Long,Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++) mapa.merge(a[i], 1, Integer::sum);
    Map<Long,Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++) mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    long[] c = new long[len];
    int cindex = 0;
    Integer ia; Integer ib; int count;
    for (Long e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e); ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++) c[cindex++] = e;
      }
    }
    long[] d = take(c,cindex);
    Arrays.sort(d);
    return d;
  }
  
  public static double[] intersectMultiset(double[] a, double[]b) {
    // return a double[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection.
    if (a == null || b == null) return new double[]{}; 
    Map<Double,Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++) mapa.merge(a[i], 1, Integer::sum);
    Map<Double,Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++) mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    double[] c = new double[len];
    int cindex = 0;
    Integer ia; Integer ib; int count;
    for (Double e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e); ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++) c[cindex++] = e;
      }
    }
    double[] d = take(c,cindex);
    Arrays.sort(d);
    return d;
  }
  
  public static <T> T[] intersectMultiset(T[] a, T[]b) {
    // return a double[] containing the elements of the multiset
    // intersection of a and b. that means if a given value
    // occurs n times in b then just first n occurrences of it 
    // in a will be in the multiset intersection.
    if (a == null || b == null) return null;
    Map<T,Integer> mapa = new HashMap<>();
    for (int i = 0; i < a.length; i++) mapa.merge(a[i], 1, Integer::sum);
    Map<T,Integer> mapb = new HashMap<>();
    for (int i = 0; i < b.length; i++) mapb.merge(b[i], 1, Integer::sum);
    int len = a.length >= b.length ? a.length : b.length;
    T[] c = ofDim(a.getClass().getComponentType(), len);
    int cindex = 0;
    Integer ia; Integer ib; int count;
    for (T e : mapa.keySet()) {
      if (mapb.containsKey(e)) {
        ia = mapa.get(e); ib = mapb.get(e);
        count = ia <= ib ? ia : ib;
        for (int i = 0; i < count; i++) c[cindex++] = e;
      }
    }
    return take(c,cindex);
  }
  
  public static double[] searchClient(int trials) {
    int[] a1; int[] a2;
//    int[] dims = {10,100,1000,10000};
    int[] dims = {1000,10000,100000,1000000};
    long[][] totals = new long[trials][4];
    long[] grandTotals = {0,0,0,0};
    double[] avgs = {0,0,0,0};
    for (int i = 0; i < trials; i++) {
      for (int j = 0; j < dims.length; j++) {
        a1 = randomArray(dims[j],6); a2 = randomArray(dims[j],6);
        totals[i][j]+=intersectMultiset(a1,a2).length;
      }
    }
    for (int i = 0; i < totals.length; i++) {
      for (int j = 0; j < totals[i].length; j++) {
        grandTotals[j]+=totals[i][j];
      }
    }
    for (int i = 0; i < grandTotals.length; i++) {
      avgs[i] = 1.*grandTotals[i]/trials;
    }
    return avgs;
  }
  
  public static void main(String[] args) {
    
    Instant start = null;
    Instant end = null;
    long millis = 0;
    start = Instant.now();
    double[] r = searchClient(5);
    end = Instant.now();
    millis = Duration.between(start, end).toMillis();;
    System.out.println("millis="+millis); //millis=772989 (12.88315 minutes)
    pa(r); //double[1.4,110.6,10002.4,498670.6]

    
    
//    double[] r = searchClient(5); //with int[] dims = {10,100,1000,10000};
//    pa(r); // double[0.0,0.0,1.4,107.0] 
// 
//    int[] a = {1,2,3,3,4,5,5,5};
//    int[] b = {2,3,3,4,4,5,5};
//    int[] c = intersectMultiset(a,b);
//    pa(c); //int[2,3,3,4,5,5]

    
 
  
  }

}
