package ex14;

import static v.ArrayUtils.*;
import ds.StaticSETofInts;

//  p209
//  1.4.11  Add an instance method howMany() to StaticSETofInts (page 99) that finds the
//  number of occurrences of a given key in time proportional to log N in the worst case.

public class Ex1411StaticSETofIntsHowMany {
  
  // StaticSETofInts.howManyOf is not static and does not take an array argument
  public static int howManyOf(int[] a, int key) {
    // return the number of occurrences of key in a
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return 0;
    int s = smallestIndexOf(a, key);
    if (s == -1) return 0;
    else return largestIndexOf(a, key) - s + 1;
  }
  
  public static int smallestIndexOf(int[] a, int key) {
    // return the smallest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next lower index
        result = mid;
        hi = mid - 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static int largestIndexOf(int[] a, int key) {
    // return the largest index of key in a if possible otherwise return -1
    // assumes a has been sorted in increasing order
    // O(log a.length) worst case running time
    if (a == null || a.length == 0 || key < a[0] || a[a.length-1] < key) return -1;
    int lo = 0;
    int hi = a.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      if (key == a[mid]) {
        // if found key, keep searching for it at next higher index
        result = mid;
        lo = mid + 1;
      }
      else if (key < a[mid]) hi = mid - 1;
      else lo = mid + 1;
    }
    return result;
  }
  
  public static void main(String[] args) {
    
    StaticSETofInts ssi = new StaticSETofInts(new int[]{1,2,3,3,4,5,5,5,5,5,6,7,7,7,8,9,9,9,9});
    for (int i : range(1,10))
      System.out.print(ssi.howManyOf(i)+" "); //1 1 2 1 5 1 3 1 4 
    System.out.println();
    
    //         0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
    int[] a = {1, 1 ,2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5};
    System.out.println(smallestIndexOf(a, 1)); //0
    System.out.println(smallestIndexOf(a, 2)); //2
    System.out.println(smallestIndexOf(a, 3)); //5
    System.out.println(smallestIndexOf(a, 4)); //9
    System.out.println(smallestIndexOf(a, 5)); //14
    System.out.println();
    System.out.println(largestIndexOf(a, 1)); //1
    System.out.println(largestIndexOf(a, 2)); //4
    System.out.println(largestIndexOf(a, 3)); //8
    System.out.println(largestIndexOf(a, 4)); //13
    System.out.println(largestIndexOf(a, 5)); //19
    
    int[] b = range(1,6);
    pa(b); //int[1,2,3,4,5]
    
    for (int i : b)
      assert i == largestIndexOf(a, i) - smallestIndexOf(a, i);
    
  for (int i : b) System.out.print(howManyOf(a, i)+" "); // 1 2 3 4 5
    
    System.out.println("\nhowManyOf(a, 9)="+howManyOf(a, 9)); //0
    
    int[] c = {1,3,5,7,9};
    System.out.println("\nhowManyOf(c, 4)="+howManyOf(c, 4)); //0
    for (int i : c) System.out.print(howManyOf(c, i)+" "); //1 1 1 1 1
    System.out.println();
    int[] d = {1,1,3,3,5,5,7,7,9,9};
    for (int i : d) System.out.print(howManyOf(d, i)+" "); //2 2 2 2 2 2 2 2 2 2

  }

}
