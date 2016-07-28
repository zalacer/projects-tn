package ex14;

import static v.ArrayUtils.*;
import java.util.function.IntPredicate;

//  p211
//  1.4.24 Throwing eggs from a building. Suppose that you have an N-story building and
//  plenty of eggs. Suppose also that an egg is broken if it is thrown off floor F or higher,
//  and unhurt otherwise. First, devise a strategy to determine the value of F such that the
//  number of broken eggs is ~lg N when using ~lg N throws, then find a way to reduce the
//  cost to ~2lg F.
//  Hint: binary search; repeated doubling and binary search.
//  https://stackoverflow.com/questions/17404642/throwing-eggs-from-a-building

//  The ~lgN strategy can be implemented with binary search for the least element satisfying 
//  a predicate as shown below with findFloorLgN().
//
//  The ~2lgF strategy can be implemented by searching at increasing powers of 2 from index 1 
//  until a dropped egg breaks and then doing the ~lgN strategy from the index of the next to 
//  last power of 2 up to and including the last, provided index 0 has already been tested and 
//  failed. This strategy is implemented in findFloor2lgF() below.


//  1.4.25 Throwing two eggs from a building. Consider the previous question, but now
//  suppose you only have two eggs, and your cost model is the number of throws. Devise
//  a strategy to determine F such that the number of throws is at most 2√N, then find a
//  way to reduce the cost to ~c √F. This is analogous to a situation where search hits (egg
//  intact) are much cheaper than misses (egg broken).
//  https://stackoverflow.com/questions/29965430/how-to-throw-2-eggs-from-a-building-and-find-the-floor-f-with-csqrtf-throws
//  http://www.datagenetics.com/blog/july22012/index.html

//  From http://algs4.cs.princeton.edu/14analysis/:
//  Throwing two eggs from a building. Consider the previous question, but now suppose you 
//  only have two eggs, and your cost model is the number of throws. Devise a strategy to 
//  determine F such that the number of throws is at most 2 sqrt(√ N), then find a way to 
//  reduce the cost to ~c √ F for some constant c.
//  
//  Solution to Part 1: To achieve 2 * sqrt(N), drop eggs at floors 
//  sqrt(N), 2 * sqrt(N), 3 * sqrt(N), ..., sqrt(N) * sqrt(N). 
//  (For simplicity, we assume here that sqrt(N) is an integer.) Let assume that the egg broke
//  at level k * sqrt(N). With the second egg you should then perform a linear search in the 
//  interval (k-1) * sqrt(N) to k * sqrt(N). In total you will be able to find the floor F in 
//  at most 2 * sqrt(N) trials.
//  
//  Hint for Part 2: 1 + 2 + 3 + ... k ~ 1/2 k^2. 

public class Ex1424ThrowingEggsFromBuilding {
  
  public static int findFloorLgN(int[] z, IntPredicate eggBreaks) {
    // return the smallest index of an element in z satisfying eggBreaks
    // if any and possible, otherwise return -1. assumes z has been sorted
    // in increasing order. O(log z.length) worst case running time.
    if (z == null || z.length == 0 || eggBreaks == null) return -1;
    if (z.length == 1 && eggBreaks.test(z[0])) return 0;
    int lo = 0;
    int hi = z.length - 1;
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      // if egg breaks, keep searching for the same condition downwards
      if (eggBreaks.test(z[mid])) {   // 
        result = mid;
        hi = mid - 1;
      }
      // if egg doesn't break test higher by adjusting lo upwards
      else if (!eggBreaks.test(z[mid])) lo = mid + 1;
    }
    return result;
  }
  
  public static int findFloor2lgF(int[] z, IntPredicate eggBreaks) {
    // return the smallest index of an element in z satisfying eggBreaks
    // if any and possible, otherwise return -1. assumes z has been sorted
    // in increasing order. O(2lgF) worst case running time where F is the
    // index found.
    if (z == null || z.length == 0 || eggBreaks == null) return -1;
    if (z.length == 1 && eggBreaks.test(z[0])) return 0;
    int n = z.length;
    int lo = 1;
    int hi = 1;
    int i = 1;
    // test indices at increasing powers of 2 until egg breaks or 
    // the end of the array has been reached
    while (true) {
      if (lo == n-1 || i > n-1) return -1;
      i*=2;
      if (i > n-1) i = n-1;
      if (eggBreaks.test(i)) {
        hi = i;
        break;
      }
      lo = i;
    }
    int result = -1;
    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      // if egg breaks, keep searching for the same condition downwards
      if (eggBreaks.test(z[mid])) {   // 
        result = mid;
        hi = mid - 1;
      }
      // if egg doesn't break test higher by adjusting lo upwards
      else if (!eggBreaks.test(z[mid])) lo = mid + 1;
    }
    return result;
  }
  
  
  public static int binIndexOf(int[] a, int key) { 
    // binary search.
    int lo = 0;
    int hi = a.length - 1;
    while (lo <= hi) { 
      // Key is in a[lo..hi] or not present.
      int mid = lo + (hi - lo) / 2;
      if (key < a[mid]) hi = mid - 1;
      else if (key > a[mid]) lo = mid + 1;
      else return mid;
    }
    return -1;
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
  
  
  public static int smallestIndexOf2(int[] a, int key) {
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
           
  public static void main(String[] args) {
    
    int[] building = {1,2,3,4,5,6,7,8,9,10,11,12,14,15};
    // suppose floor 11 is the lowest floor from which an egg breaks
    int f = findFloorLgN(building, x -> x >= 11);
    System.out.println(f); //10
    System.out.println(building[f]); //11
    
    building = append(range(1,13),range(14,1001));
    // building = {1,2,3,4,5,6,7,8,9,10,11,12,14...,1000};
    f = findFloor2lgF(building, x -> x >= 531);
    System.out.println(f); //529
    System.out.println(building[f]); //531
  
  }

}
