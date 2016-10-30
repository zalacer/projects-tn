package ex14;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import ds.StaticSETofInts;

//  p210
//  1.4.21 Binary search on distinct values. Develop an implementation of binary search
//  for StaticSETofInts (see page 98) where the running time of contains() is guaranteed
//  to be ~ lg R, where R is the number of different integers in the array given as argument
//  to the constructor.

// I guess this means ~lgR after sorting. If all values are distinct then regular binary
// seach is all that's needed. My solution uses a merge sort customized to put all the
// unique elements at the front of the array during sorting and returns their count, coupled
// with binary search implemented to search only the first n elements of a sorted array.
// Since n will be just the unique elements from the sort, running time is ~lgR after sorting.
// See ds.StaticSETofInts and analysis.MergesortUnique for the code.

public class Ex1421BinarySearchOnDistinctValues {

  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    Random rnd = SecureRandom.getInstanceStrong(); // gives different results each time
    int [] a = rnd.ints(20, 1, 8).toArray();
    pa(a); //int[3,3,2,3,3,3,2,4,4,7,1,1,3,1,6,7,2,1,6,6]
    StaticSETofInts ssoi = new StaticSETofInts(a);
    // the array in ssi has all the unique elements sorted in the first 6 indices since
    // 5 is missing this run
    System.out.println(ssoi);
    // StaticSETofInts(1,2,3,4,6,7,3,4,7,7,1,2,3,6,7,1,2,6,7,6)
    for (int i = 1; i < 8; i++) 
      if (ssoi.contains(i)) { // uses ssi.indexOf(i, 6) to search only indices 0-6 inclusive
        System.out.println("ssoi contains "+i);
      } else {
        System.out.println("ssoi doesn't contain "+i);
      }
    //  ssi contains 1
    //  ssi contains 2
    //  ssi contains 3
    //  ssi contains 4
    //  ssi doesn't contain 5
    //  ssi contains 6
    //  ssi contains 7


  }

}
