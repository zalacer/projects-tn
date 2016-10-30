package ex25;

import static sort.Quick.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;

/* p356
  2.5.23 Sampling for selection. Investigate the idea of using sampling
  to improve selection. Hint: Using the median may not always be helpful.
  
  I guess this question is about improving the select(Comparable[]) 
  method on p346 which relies on using one randomly selected element of
  its argument array to partition it. Since it relies on quicksort's
  partition method, it's best case in terms of number of compares is when
  "each partitioning stage divides the array exactly in half." (p293).
  Selecting one random element is not likely to achieve the best case,
  however it usually avoids the worst. Sampling can be used to improve
  partitioning performance for selection as done for quicksort by using
  median-of-three partitioning introduced in the text on p296 or Tukey
  ninther as in QuickX.java.
   
 */

public class Ex2523SamplingForSelection {
  
  public static void main(String[] args) {
    
    SecureRandom r = new SecureRandom();
    Integer[] a, b, c;
    a = rangeInteger(1,40); 
    shuffle(a,r);
    b = a.clone(); c = a.clone();
    // selectCompares(), selectComparesM3 and selectComparesT9 return a 
    // 2-element array containing the number of compares and the value of the 
    // (k+1)st smallest element in the argument array. selectCompares() is 
    // identical to select(Comparable[]) on p346 with shuffle() commented out. 
    // selectComparesM3() is identical to selectCompares() but uses 
    // sort.Quick.partitionM3 that implements median-of-3 partitioning.
    // selectComparesT9() is identical to selectCompares() but uses 
    // sort.Quick.partitionT9 that implements Tukey ninther partitioning.
    long d = 0, n = 1000; double sum = 0, sumM3 = 0, sumT9 = 0;
    while (d < n) {
      sum +=   (long)(selectCompares(a, 19)[0]);
      sumM3 += (long)(selectComparesM3(b, 19)[0]);
      sumT9 += (long)(selectComparesT9(c, 19)[0]);
      d++;
    }
    System.out.println("avg compares using shuffle: "+Math.round(sum/n));
    System.out.println("avg compares using median-of-3: "+Math.round(sumM3/n));  
    System.out.println("avg compares using Tukey ninther: "+Math.round(sumT9/n));
  }
  /*  
    for selecting 19 with N = 39 (array length)   
    avg compares using shuffle: 609
    avg compares using median-of-3: 42
    avg compares using Tukey ninther: 50
    
   */
}


