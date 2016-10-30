package sort;

import static v.ArrayUtils.*;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

public class ShellWork {
  
  public static int[] increments = createIncrementArray();

  public static  <T extends Comparable<? super T>> void sort(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int h = 1;
    while (h < N/3) h = 3*h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
    System.out.println("h="+h);
    while (h >= 1) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h)
          exch(a, j, j-h);
      }
      h = h/3;
      System.out.println("h="+h);
    }
  }
  
  // excercise 2.1.11 p264 
  public static int[] createIncrementArray() {
    // based on https://en.wikipedia.org/wiki/Shellsort#Gap_sequences this is O(N**(4/3))
    List<Integer> list = new ArrayList<>();
    int k = 0; long r = 0;
    list.add(1);
    
    while(true) {
      r = (long) (pow(4,k) + 3.*pow(2,k-1) + 1);
      if (r >= Integer.MAX_VALUE) break;
      if (r > 0) list.add((int)r);
      k++;
    }
    
    int[] z = (int[]) unbox(list.toArray(new Integer[0]));
    shellSort(z);
    return z;
  }
    
    
//  public static int[] createIncrementArray() {
//    //9*4**k - 9*2**k+1 and 4**k - 3*2**k + 1)
//    List<Integer> list = new ArrayList<>();
//    int k = 0; long r = 0; //long r1 = 0; long r2 = 0;
////    while(true) {
//     // r = (long) (9.*pow(4,k) - 9.*pow(2,k) + 1);
//      r = (long) (9.*(pow(4,k-1) - pow(2,k/2)) + 1);
//      if (r >= Integer.MAX_VALUE) break;
////      if (r > 0) list.add((int)r);
//      k++;
//    }
//    k = 0;
//    while(true) {
////      r = (long) (pow(4,k) - 3.*pow(2,k) + 1);
//      r = (long) (pow(4,k+1) - 6.*pow(2,(1.*k+1)/2) + 1);
//      if (r >= Integer.MAX_VALUE) break;
//      if (r > 0) list.add((int)r);
//      k++;
//    }
//    
//    list.add(1);
//    
//    while(true) {
//      r = (long) (pow(4,k) + 3.*pow(2,k-1) + 1);
//      if (r >= Integer.MAX_VALUE) break;
//      if (r > 0) list.add((int)r);
//      k++;
//    }
//    
//    
//    System.out.println(list.size()); //30
//    System.out.println(list);
//    
//    int[] z = (int[]) unbox(list.toArray(new Integer[0]));
//    shellSort(z);
//    int[] a = {4,1,2,7,5,6,3};
//    return z;
//  }
  
  // excercise 2.1.11 p264 
  public static  <T extends Comparable<? super T>> void shellSortWithIncrementArray(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int k = 0;
    int h = 1;
//    int[] inc = {1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929,16001, 36289, 64769, 146305, 260609};
    @SuppressWarnings("unused")
    int[] inc = {1, 4, 13, 40, 121, 364, 1093};
    while (h < N/3) {
      h = increments[++k];
    }
    
    System.out.println("h="+h);
    
//    int[] inc = {1, 5, 19, 41, 109, 209, 505, 929, 2161, 3905, 8929,
//        16001, 36289, 64769, 146305, 260609};
    while (h >= 1) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h)
          exch(a, j, j-h);
      }
      if (k == 0) {
        break;
      } else h = increments[--k];
//      h = k > 0 ? inc[--k] : 1;

    }
  }
  
  
  public static  <T extends Comparable<? super T>> void printTraceAfterEachPass(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int h = 1; 
    System.out.println("  Shellsort trace (array contents after each pass)");
    System.out.printf("%7s  %s\n", "input", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), "");
    while (h < N/3) h = 3*h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
    while (h >= 1) { // h-sort the array.
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) {
          exch(a, j, j-h);
//        System.out.printf("%9s  %34s  %s\n", ""+h+"-sort",  arrayToString(a, 1000, 0,0)
//            .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), ""+a[j]+", "+a[j-h]);
        }
      }
      System.out.printf("%7s  %s\n", ""+h+"-sort",  arrayToString(a, 1000, 0,0)
          .replaceAll("[\\[\\]]"," ").replaceAll(",","  "));
      h = h/3;
    }
  }
  
  public static  <T extends Comparable<? super T>> void printVeryDetailedTrace(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int h = 1; boolean p = false; 
    System.out.println("  Very Detailed trace of shellsort (insertions)            a[j-h], a[j]");
    System.out.printf("%7s  %s  or just a[j]\n", "input", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), "");
    while (h < N/3) h = 3*h + 1; // 1, 4, 13, 40, 121, 364, 1093, ...
    while (h >= 1) { // h-sort the array.
      p = false;
      for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
        int j = i;
        while (true) {
//        for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) {
          if (j >= h) {
            if (less(a[j], a[j-h])) {
              exch(a, j, j-h);
              if (!p) {
                System.out.printf("%7s  %34s  %s\n", ""+h+"-sort",  arrayToString(a, 1000, 0,0)
                    .replaceAll("[\\[\\]]"," ").replaceAll(",","  "),
                    "a["+(j-h)+"]="+a[j-h]+", a["+j+"]="+a[j]);
                p = true;
              } else {
                System.out.printf("%7s  %34s  %s\n", "",  arrayToString(a, 1000, 0,0)
                    .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), 
                    "a["+(j-h)+"]="+a[j-h]+", a["+j+"]="+a[j]);
              }
            } else {
              System.out.printf("%7s  %34s  %s\n", "",  arrayToString(a, 1000, 0,0)
                  .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), "a["+j+"]="+a[j]);
              break;
            }
          } else break;
          j -= h;
        }
      }
//      System.out.printf("%7s  %s\n", ""+h+"-sort",  arrayToString(a, 1000, 0,0)
//          .replaceAll("[\\[\\]]"," ").replaceAll(",","  "));
      h = h/3;
    }
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }

  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) { 
    // Test whether the array entries are in order.
    for (int i = 1; i < a.length; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }

  @SuppressWarnings("unused")
  private static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public static void main(String[] args) {
    
    int[] increments = createIncrementArray();
    pa(increments,1000,1,1);
        
    
//    Integer[] z = {5,4,3,2,1};
////    sort(z);
//    shellSortWithIncrementArray(z);
//    pa(z);
//    
//    pa(createIncrementArray());
    
//    String[] sa = "S H E L L S O R T E X A M P L E".split("\\s");
//    sa = "E A S Y S H E L L S O R T Q U E S T I O N".split("\\s");
//    printTraceAfterEachPass(sa);
//    printVeryDetailedTrace(sa);
  }
  
/*
 increments
 [1, 3, 8, 23, 77, 281, 1073, 4193, 16577, 65921, 262913, 1050113, 4197377, 16783361, 67121153, 268460033, 1073790977]
int[
  1,
  3,
  8,
  23,
  77,
  281,
  1073,
  4193,
  16577,
  65921,
  262913,
  1050113,
  4197377,
  16783361,
  67121153,
  268460033,
  1073790977]

  Very Detailed trace of shellsort (insertions)            a[j-h], a[j]
  input   S  H  E  L  L  S  O  R  T  E  X  A  M  P  L  E   or just a[j]
13-sort   P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[0]=P, a[13]=S
          P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[14]=L
          P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[15]=E
 4-sort   L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[0]=L, a[4]=P
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[5]=S
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[6]=O
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[7]=R
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[8]=T
          L  H  E  L  P  E  O  R  T  S  X  A  M  S  L  E   a[5]=E, a[9]=S
          L  E  E  L  P  H  O  R  T  S  X  A  M  S  L  E   a[1]=E, a[5]=H
          L  E  E  L  P  H  O  R  T  S  X  A  M  S  L  E   a[10]=X
          L  E  E  L  P  H  O  A  T  S  X  R  M  S  L  E   a[7]=A, a[11]=R
          L  E  E  A  P  H  O  L  T  S  X  R  M  S  L  E   a[3]=A, a[7]=L
          L  E  E  A  P  H  O  L  M  S  X  R  T  S  L  E   a[8]=M, a[12]=T
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[4]=M, a[8]=P
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[4]=M
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[13]=S
          L  E  E  A  M  H  O  L  P  S  L  R  T  S  X  E   a[10]=L, a[14]=X
          L  E  E  A  M  H  L  L  P  S  O  R  T  S  X  E   a[6]=L, a[10]=O
          L  E  E  A  M  H  L  L  P  S  O  R  T  S  X  E   a[6]=L
          L  E  E  A  M  H  L  L  P  S  O  E  T  S  X  R   a[11]=E, a[15]=R
          L  E  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[7]=E, a[11]=L
          L  E  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[7]=E
 1-sort   E  L  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[0]=E, a[1]=L
          E  E  L  A  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=E, a[2]=L
          E  E  L  A  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=E
          E  E  A  L  M  H  L  E  P  S  O  L  T  S  X  R   a[2]=A, a[3]=L
          E  A  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=A, a[2]=E
          A  E  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[0]=A, a[1]=E
          A  E  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[4]=M
          A  E  E  L  H  M  L  E  P  S  O  L  T  S  X  R   a[4]=H, a[5]=M
          A  E  E  H  L  M  L  E  P  S  O  L  T  S  X  R   a[3]=H, a[4]=L
          A  E  E  H  L  M  L  E  P  S  O  L  T  S  X  R   a[3]=H
          A  E  E  H  L  L  M  E  P  S  O  L  T  S  X  R   a[5]=L, a[6]=M
          A  E  E  H  L  L  M  E  P  S  O  L  T  S  X  R   a[5]=L
          A  E  E  H  L  L  E  M  P  S  O  L  T  S  X  R   a[6]=E, a[7]=M
          A  E  E  H  L  E  L  M  P  S  O  L  T  S  X  R   a[5]=E, a[6]=L
          A  E  E  H  E  L  L  M  P  S  O  L  T  S  X  R   a[4]=E, a[5]=L
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[3]=E, a[4]=H
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[3]=E
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[8]=P
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[9]=S
          A  E  E  E  H  L  L  M  P  O  S  L  T  S  X  R   a[9]=O, a[10]=S
          A  E  E  E  H  L  L  M  O  P  S  L  T  S  X  R   a[8]=O, a[9]=P
          A  E  E  E  H  L  L  M  O  P  S  L  T  S  X  R   a[8]=O
          A  E  E  E  H  L  L  M  O  P  L  S  T  S  X  R   a[10]=L, a[11]=S
          A  E  E  E  H  L  L  M  O  L  P  S  T  S  X  R   a[9]=L, a[10]=P
          A  E  E  E  H  L  L  M  L  O  P  S  T  S  X  R   a[8]=L, a[9]=O
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[7]=L, a[8]=M
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[7]=L
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[12]=T
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[12]=S, a[13]=T
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[12]=S
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[14]=X
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  R  X   a[14]=R, a[15]=X
          A  E  E  E  H  L  L  L  M  O  P  S  S  R  T  X   a[13]=R, a[14]=T
          A  E  E  E  H  L  L  L  M  O  P  S  R  S  T  X   a[12]=R, a[13]=S
          A  E  E  E  H  L  L  L  M  O  P  R  S  S  T  X   a[11]=R, a[12]=S
          A  E  E  E  H  L  L  L  M  O  P  R  S  S  T  X   a[11]=R
          
       Detailed trace of shellsort (insertions)            a[j-h], a[j]
  input   S  H  E  L  L  S  O  R  T  E  X  A  M  P  L  E   or just a[j]
13-sort   P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[0]=P, a[13]=S
          P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[14]=L
          P  H  E  L  L  S  O  R  T  E  X  A  M  S  L  E   a[15]=E
 4-sort   L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[0]=L, a[4]=P
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[5]=S
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[6]=O
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[7]=R
          L  H  E  L  P  S  O  R  T  E  X  A  M  S  L  E   a[8]=T
          L  H  E  L  P  E  O  R  T  S  X  A  M  S  L  E   a[5]=E, a[9]=S
          L  E  E  L  P  H  O  R  T  S  X  A  M  S  L  E   a[1]=E, a[5]=H
          L  E  E  L  P  H  O  R  T  S  X  A  M  S  L  E   a[10]=X
          L  E  E  L  P  H  O  A  T  S  X  R  M  S  L  E   a[7]=A, a[11]=R
          L  E  E  A  P  H  O  L  T  S  X  R  M  S  L  E   a[3]=A, a[7]=L
          L  E  E  A  P  H  O  L  M  S  X  R  T  S  L  E   a[8]=M, a[12]=T
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[4]=M, a[8]=P
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[4]=M
          L  E  E  A  M  H  O  L  P  S  X  R  T  S  L  E   a[13]=S
          L  E  E  A  M  H  O  L  P  S  L  R  T  S  X  E   a[10]=L, a[14]=X
          L  E  E  A  M  H  L  L  P  S  O  R  T  S  X  E   a[6]=L, a[10]=O
          L  E  E  A  M  H  L  L  P  S  O  R  T  S  X  E   a[6]=L
          L  E  E  A  M  H  L  L  P  S  O  E  T  S  X  R   a[11]=E, a[15]=R
          L  E  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[7]=E, a[11]=L
          L  E  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[7]=E
 1-sort   E  L  E  A  M  H  L  E  P  S  O  L  T  S  X  R   a[0]=E, a[1]=L
          E  E  L  A  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=E, a[2]=L
          E  E  L  A  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=E
          E  E  A  L  M  H  L  E  P  S  O  L  T  S  X  R   a[2]=A, a[3]=L
          E  A  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[1]=A, a[2]=E
          A  E  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[0]=A, a[1]=E
          A  E  E  L  M  H  L  E  P  S  O  L  T  S  X  R   a[4]=M
          A  E  E  L  H  M  L  E  P  S  O  L  T  S  X  R   a[4]=H, a[5]=M
          A  E  E  H  L  M  L  E  P  S  O  L  T  S  X  R   a[3]=H, a[4]=L
          A  E  E  H  L  M  L  E  P  S  O  L  T  S  X  R   a[3]=H
          A  E  E  H  L  L  M  E  P  S  O  L  T  S  X  R   a[5]=L, a[6]=M
          A  E  E  H  L  L  M  E  P  S  O  L  T  S  X  R   a[5]=L
          A  E  E  H  L  L  E  M  P  S  O  L  T  S  X  R   a[6]=E, a[7]=M
          A  E  E  H  L  E  L  M  P  S  O  L  T  S  X  R   a[5]=E, a[6]=L
          A  E  E  H  E  L  L  M  P  S  O  L  T  S  X  R   a[4]=E, a[5]=L
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[3]=E, a[4]=H
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[3]=E
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[8]=P
          A  E  E  E  H  L  L  M  P  S  O  L  T  S  X  R   a[9]=S
          A  E  E  E  H  L  L  M  P  O  S  L  T  S  X  R   a[9]=O, a[10]=S
          A  E  E  E  H  L  L  M  O  P  S  L  T  S  X  R   a[8]=O, a[9]=P
          A  E  E  E  H  L  L  M  O  P  S  L  T  S  X  R   a[8]=O
          A  E  E  E  H  L  L  M  O  P  L  S  T  S  X  R   a[10]=L, a[11]=S
          A  E  E  E  H  L  L  M  O  L  P  S  T  S  X  R   a[9]=L, a[10]=P
          A  E  E  E  H  L  L  M  L  O  P  S  T  S  X  R   a[8]=L, a[9]=O
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[7]=L, a[8]=M
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[7]=L
          A  E  E  E  H  L  L  L  M  O  P  S  T  S  X  R   a[12]=T
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[12]=S, a[13]=T
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[12]=S
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  X  R   a[14]=X
          A  E  E  E  H  L  L  L  M  O  P  S  S  T  R  X   a[14]=R, a[15]=X
          A  E  E  E  H  L  L  L  M  O  P  S  S  R  T  X   a[13]=R, a[14]=T
          A  E  E  E  H  L  L  L  M  O  P  S  R  S  T  X   a[12]=R, a[13]=S
          A  E  E  E  H  L  L  L  M  O  P  R  S  S  T  X   a[11]=R, a[12]=S
          A  E  E  E  H  L  L  L  M  O  P  R  S  S  T  X   a[11]=R          
          
  
*/
}
