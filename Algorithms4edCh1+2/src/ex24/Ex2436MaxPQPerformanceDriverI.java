package ex24;

import static v.ArrayUtils.*;

import java.util.Arrays;

import static utils.RandomUtils.randomString;
import analysis.Timer;
import pq.MaxPQ;


/* p335
  2.4.36 Performance driver I. Write a performance driver client program that uses 
  insert to fill a priority queue, then uses remove the maximum to remove half the 
  keys, then uses insert to fill it up again, then uses remove the maximum to remove 
  all the keys, doing so multiple times on random sequences of keys of various lengths 
  ranging from small to large; measures the time taken for each run; and prints out or 
  plots the average running times.

 */

//@SuppressWarnings("unused")
public class Ex2436MaxPQPerformanceDriverI {

  /* since in order to vary the key lengths strings are used as keys
     and a method for generating random strings is useful, using
     utils.RandomUtils.randomString the following several methods 
     estimate how long such strings should be in order for certain of 
     numbers of them to be unique because that can help to make 
     performance using such keys comparable. */

  public static int testRandomString(int n) {
    // return the minimum length such that n random   
    // strings of that length are unique
    String[] s = new String[n]; int len = 0;
    LOOP:
      while(true) {
        len++;
        for (int i = 0; i < n; i++) s[i] = randomString(len);
        Arrays.sort(s);
        for (int i = 0; i < n-1; i++) if (s[i].equals(s[i+1])) continue LOOP;
        return len;
      }
  }

  public static int testRandomString(int n, int trials) {
    // return the maximum of testRandomString(n) over all trials  
    int[] r = new int[trials]; int c = 0;
    for (int i = 0; i < trials; i++) r[c++] = testRandomString(n);
    return max(r);
  }

  public static int[] getRandomStringLengths(int trials) {
    // return an array of reasonable random string lengths such that 1K-1M 
    // of them are unique based on trials number of determinations
    // for 100 trials this returns [7,9,10,12]
    int n = 1000, c = 0; int[] r = new int[4];
    while (n <= 1000000) {
      r[c++] = testRandomString(n,trials); n*=10;
    }
    return r;
  }

  public static double prfDrvrI(int size, int keysize, int trials) {
    double[] d = new double[trials];
    for (int i = 0; i < trials; i++) {
      MaxPQ<String> h = new MaxPQ<>(size);
      String[] s = new String[size];
      for (int j = 0; j < size; j++) s[j]= randomString(keysize);
      Timer t = new Timer();
      for (int j = 0; j < size; j++) h.insert(s[j]);
      for (int j = 0; j < size/2; j++) h.delMax();
      for (int j = 0; j < size/2; j++) h.insert(s[j]);
      d[i] = t.num();   
    }
    //    System.out.println("trials="+trials);
    //    System.out.println("size="+size);
    //    System.out.println("keysize="+keysize);
    //    System.out.println("mean="+mean(d));
    //    System.out.println("stddev="+stddev(d));
    return mean(d);
  }

  public static void doublingRatioTestPrfDrvrI(int keysize, int trials) {
    int n = 512; double sum = 0, pre, post;
    System.out.println("\nprfDrvrI doubling test with key size "
        +keysize+" and "+trials+" trials");
    System.out.println("       N       time       time/prev");
    for (int i = 0; i < trials; i++) sum += prfDrvrI(n, keysize, trials);
    pre = sum/trials; int c = 0;
    while (true) {
      n *= 2;  sum = 0; 
      for (int i = 0; i < trials; i++) sum += prfDrvrI(n, keysize, trials);
      post = sum/trials;
      System.out.printf("%8d   %8.0f       %5.3f\n", n, post, post/pre);
      pre = post;
      if (++c == 8) break;
    }
  }

  public static void main(String[] args) {
    
    par(getRandomStringLengths(10));
    
    System.exit(0);

    int[] len = {12, 100, 500, 1000, 5000, 10000};
    for (int i = 0; i < len.length; i++)
      doublingRatioTestPrfDrvrI(len[i],10);
    /*
    prfDrvrI doubling test with key size 12 and 10 trials
           N       time       time/prev
        1024         16       3.538
        2048         69       4.214
        4096        311       4.489
        8192       1201       3.859
       16384       4867       4.053

   takes too long for more doubling tests
   
   prfDrvrI spot tests:

      trials=1000
      size=1000
      keysize=1
      mean=14.143
      stddev=1.5795714754471057

      trials=1000
      size=1000
      keysize=2
      mean=14.356
      stddev=2.6601391580780933

      trials=1000
      size=1000
      keysize=5
      mean=14.639
      stddev=2.3871274321613614

      trials=1000
      size=1000
      keysize=10
      mean=15.662
      stddev=2.7315228751041665

      trials=1000
      size=1000
      keysize=10
      mean=15.604
      stddev=1.7990053975517661

      trials=1000
      size=1000
      keysize=100
      mean=15.509
      stddev=3.0147981703603555

      trials=1000
      size=1000
      keysize=100
      mean=15.727
      stddev=2.3967092471168066

      trials=1000
      size=1000
      keysize=250
      mean=15.953
      stddev=1.665402223057399

      trials=1000
      size=1000
      keysize=500
      mean=17.011
      stddev=1.0334151861410559

      trials=1000
      size=1000
      keysize=1000
      mean=18.073
      stddev=1.6187313833033818

     */    


  }

}
