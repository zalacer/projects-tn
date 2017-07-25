package ex23;

import static sort.Quicks.plotHistogramOfSizesOfSubarraysLeftForInsertionSort;

public class Ex2326QuickSortHistogramOfSubarraySizes {

  /* p307  
  2.3.26 Subarray sizes. Write a program that plots a histogram of the subarray sizes
  left for insertion sort when you run quicksort for an array of size N with a cutoff 
  for subarrays of size less than M. Run your program for M=10, 20, and 50 and N=10^5. 
  
  */ 
  
  public static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) {
    int[] s = {10,20,50};
    for (int i = 0; i<s.length; i++) {
      plotHistogramOfSizesOfSubarraysLeftForInsertionSort(s[i],100000);
      pause(3000);
    }
  }

}

