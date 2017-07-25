package sort;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class CycleSortWork {
  // https://rosettacode.org/wiki/Sorting_algorithms/Cycle_sort#Java
  // https://en.wikipedia.org/wiki/Cycle_sort

  public static int cycleSort(int[] a) {
    int writes = 0;

    for (int cycleStart = 0; cycleStart < a.length - 1; cycleStart++) {
      int val = a[cycleStart];

      // count the number of values that are smaller than val since cycleStart
      int pos = cycleStart;
      for (int i = cycleStart + 1; i < a.length; i++)
        if (a[i] < val)
          pos++;

      // there aren't any
      if (pos == cycleStart)
        continue;

      // skip duplicates
      while (val == a[pos])
        pos++;

      // put val into final position
      int tmp = a[pos];
      a[pos] = val;
      val = tmp;
      writes++;

      // repeat as long as we can find values to swap
      // otherwise start new cycle
      while (pos != cycleStart) {
        pos = cycleStart;
        for (int i = cycleStart + 1; i < a.length; i++)
          if (a[i] < val)
            pos++;

        while (val == a[pos])
          pos++;

        tmp = a[pos];
        a[pos] = val;
        val = tmp;
        writes++;
      }
    }
    return writes;
  }


  public static void main(String[] args) {
    int[] arr = {5, 0, 1, 2, 2, 3, 5, 1, 1, 0, 5, 6, 9, 8, 0, 1};
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {System.err.println("cannot instantiate Random");}
    arr = range(0,100);
    shuffle(arr, r);
 /*
    [29, 57, 93, 97, 92, 23, 62, 54, 3, 66, 17, 53, 35, 16, 28, 14, 73, 49, 52, 77, 72, 
    79, 18, 65, 61, 78, 13, 80, 94, 50, 15, 37, 64, 4, 99, 86, 21, 8, 96, 31, 30, 59, 26, 
    27, 95, 90, 58, 7, 51, 45, 75, 10, 67, 38, 55, 47, 36, 71, 60, 88, 6, 5, 68, 48, 33, 
    85, 19, 41, 34, 89, 0, 46, 12, 74, 76, 40, 20, 2, 9, 98, 84, 22, 81, 43, 91, 39, 82, 
    24, 70, 42, 11, 69, 63, 25, 56, 83, 1, 44, 32, 87]
*/ 
    System.out.println(Arrays.toString(arr));
    int writes = cycleSort(arr);
    System.out.println(Arrays.toString(arr));
    System.out.println("writes: " + writes);  //100
  }

}
