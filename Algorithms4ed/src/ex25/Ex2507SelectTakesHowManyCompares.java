package ex25;

import static sort.Quick.*;
import static v.ArrayUtils.*;

/* p354
  2.5.7  About how many compares are required, on the average, to find 
  the smallest of N items using select()?
  
  Amazingly just a couple for arrays of length 10, 100, 1000, 10000 and 100000,
  but it takes longer runtime for longer arrays. Arrays of length 2 take 2.5 
  compares on average.
 
 */

public class Ex2507SelectTakesHowManyCompares {
  
  public static double averageCompares(int n, int trials) {
    // return average compares to get (k+1)st element from n = 0 to n
    double[] data = new double[trials*n]; int c = 0;
    Integer[] z = rangeInteger(0,n+1);
    for (int i = 0; i < z.length-1; i++) {
      for (int j = 0; j < trials; j++)
        data[c++] = (1.*((long)selectCompares(z,i)[0]))/n;
    }
    return mean(data);
  }
  
  public static double averageCompares(int n, int k, int trials) {
    // return average compares to get (k+1)st element
    double[] data = new double[trials]; 
    Integer[] z = rangeInteger(0,n+1);
    for (int j = 0; j < trials; j++)
      data[j] = (1.*((long)selectCompares(z,k)[0]))/n;
    System.out.println(selectCompares(z,k)[1]);
    return mean(data);
  }

  public static void main(String[] args) {
    
    System.out.println(averageCompares(1,0,10000));     // 2.50
    System.out.println(averageCompares(9,0,10000));     // 2.06
    System.out.println(averageCompares(99,0,10000));    // 2.00
    System.out.println(averageCompares(999,0,10000));   // 2.00
    System.out.println(averageCompares(9999,0,10000));  // 2.00
    System.out.println(averageCompares(99999,0,10000)); // 2.00
  
  }

}
