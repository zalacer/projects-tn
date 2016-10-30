package ex14;

import static v.ArrayUtils.*;

//  p209
//  1.4.10  Modify binary search so that it always returns the element with the smallest
//  index that matches the search element (and still guarantees logarithmic running time).

public class Ex1410SmallestIndexOf {
  
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
    
    for (int i : b) assert i == largestIndexOf(a, i) - smallestIndexOf(a, i);
    
    
    
  }

}
