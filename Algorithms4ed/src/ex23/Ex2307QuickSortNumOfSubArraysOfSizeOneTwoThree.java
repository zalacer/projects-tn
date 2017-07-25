package ex23;

import static sort.Quicks.quickIntSub012;
import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


//@SuppressWarnings("unused")
public class Ex2307QuickSortNumOfSubArraysOfSizeOneTwoThree {

  /* p303  
   2.3.7  Find the expected number of subarrays of size 0, 1, and 2 when quicksort 
   is used to sort an array of N items with distinct keys. If you are mathematically 
   inclined, do the math; if not, run some experiments to develop hypotheses.
   
   There's an answer to this using recursion relations at 
   https://stackoverflow.com/questions/30283079/what-is-the-expected-number-of-subarrays-of-size-0-1-and-2-when-quicksort-is-us
   
   That answer is also available in this project at Ex2307QuickSortNumberOfSubArrays.pdf.
   
   The results of that answer are for an array of length N:
   1. the average number of sub-arrays of length 0 is (N+1)/3.
   2. the average number of sub-arrays of length 1 is (N+1)/3.
   3. the average number of sub-arrays of length 2 is (N+1)/6.
   
   Using the test below for 1000 trials of arrays of length 1000 with distinct elements:
    1. the average number of sub-arrays of length 0 is 334.174 ≅ (N+1)/3 == 1001/3 == 333+⅔
    2. the average number of sub-arrays of length 1 is 333.413 ≅ (N+1)/3 == 1001/3 == 333+⅔
    3. the average number of sub-arrays of length 2 is 167.175 ≅ (N+1)/6 == 1001/6 == 166+⅚
    
   The test confirms the mathematical results.
   
  */ 
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    long zero = 0, one = 0, two = 0; int[] d; int t = 1000;
    Random r = SecureRandom.getInstanceStrong();
    int[] a = range(1,1001);
    for (int i = 0; i < t; i++) {
      shuffle(a,r); // quickIntSub012 does not do a shuffle
      d = quickIntSub012(a); 
      zero+=d[0]; one+=d[1]; two+=d[2];
    }
    System.out.printf("%5.3f,  %5.3f,  %5.3f\n", (1.*zero)/t,  (1.*one)/t,  (1.*two)/t);
    
  /*
  
      trials with N = 1000 and 1000 trials
      ====================================
      334.174,  333.413,  167.175
      
  */
  }
                      
}

