package ex31;

import static v.ArrayUtils.*;
import java.security.SecureRandom;

import st.ST;

/* p389
  3.1.7  What is the average number of distinct keys that FrequencyCounter 
  will find among N random nonnegative integers less than 1,000, for N=10, 
  10^2, 10^3, 10^4, 10^5 and 10^6?
     
 */

public class Ex3107FrequencyAvgNumberOfDistinctIntegers {
  
  public static double distinctCounter(int n, int m, int t) {
    // return the average number of distinct ints in n random 
    // ints in [0,m] over t trials.
    
    int distinct, k; int[] trials = new int[t];
    ST<Integer, Integer> st; 
    SecureRandom r = new SecureRandom(); 
   
    for (int j = 0; j < t; j++) {
      distinct = 0; st = new ST<>();
      r.setSeed(System.currentTimeMillis());
      for (int i  = 0; i  < n; i++) {
        k = r.nextInt(m);
        if (st.contains(k)) { st.put(k, st.get(k) + 1); }
        else { st.put(k, 1); distinct++; }
      }
      trials[j] = distinct;
    }
    
    return mean(trials);
  }
  
  public static void distinctTest(int m, int t) {
    // print results for distinctCounter(n, m, t) 
    // for n from 10 through 10^6 by powers of 10.
    int n = 1; double d;
    System.out.println("N         avg distinct");
    while (n < 1000000) {
      n *= 10;
      d = distinctCounter(n, m, t);
      System.out.printf("%-8d  %5.3f\n", n, d);
    }
  }

  public static void main(String[] args) {

    distinctTest(1000,10);
    
   /* N         avg distinct
      10        9.900
      100       94.400
      1000      631.800
      10000     1000.000
      100000    1000.000
      1000000   1000.000  */    
    
  }

}
