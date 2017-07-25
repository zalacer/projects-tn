package ex24;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.util.Arrays;

import static utils.RandomUtils.randomString;
import analysis.Timer;
import pq.MaxPQ;


/* p335
  2.4.37 Performance driver II. Write a performance driver client program that uses in-
  sert to fill a priority queue, then does as many remove the maximum and insert opera-
  tions as it can do in 1 second, doing so multiple times on random sequences of keys of
  various lengths ranging from small to large; and prints out or plots the average number
  of remove the maximum operations it was able to do.

 */

//@SuppressWarnings("unused")
@SuppressWarnings("unused")
public class Ex2437MaxPQPerformanceDriverII {
 
  public static int prfDrvrII(int n, int len) {
    // return an estimate of how many delMax ops can be run/ in 1 second when 
    // interleaved with insert ops for a MaxPQ<String> initialized with n random 
    // String keys of length len.
    if (n<0 || len<0) throw new IllegalArgumentException("prfDrvrII: "
        +" both args must be >0");
    MaxPQ<String> h = new MaxPQ<>();
    Timer t = new Timer(); int i=0, j=0, iterations, r, adj, nadj; 
    double time, start, overhead; String s; boolean b;
    // set loop run time adjustment for overhead (experimentally based)
    if (n == 0) adj = 2000;
    else if (n == 1) adj = 700;
    else if (n == 2) adj = 400;
    else if (n == 3) adj = 300;
    else if (n == 4) adj = 300;
    else if (n <= 10) adj = 200;
    else if (n <= 100) adj = 20;
    else if (n <= 1000) adj = 5;
    else if (n <= 10000) adj = 1;
    else adj = 0;
    adj+=1000;
    // set nadj for number of random strings to create in an array or separate string
    if (n == 0) nadj = 1; // separate string in only this case
    else if (n == 1) nadj = 40000000;
    else if (n == 2) nadj = 20000000;
    else if (n == 3) nadj = 12000000;
    else if (n == 4) nadj = 10000000;
    else if (n <= 10) nadj = 9000000;
    else if (n <= 100) nadj = 6000000;
    else if (n <= 1000) nadj = 600000;
    else if (n <= 10000) nadj = 60000;
    else nadj = 6000;   
    if (n == 0) {
      s = randomString(len); start = System.currentTimeMillis();
      t.reset();
      while (System.currentTimeMillis() < start+adj) { h.insert(s); h.delMax(); i++; }
      time = t.num();
      iterations = i;
      // measure overhead
      start = System.currentTimeMillis(); t.reset();
      while (j < iterations) { j++; b = System.currentTimeMillis() < start+adj; }
      overhead = t.num();
      // correct for overhead
      time -= overhead;
    } else {
      for (int l = 0; l < n; l++) h.insert(randomString(len));
      String[] sa = new String[nadj];
      // creating a String[] of length nadj to ensure predictability of loop
      // overhead and based on expected number of iterations so that a new and
      // unique string will be inserted in each iteration. results from
      // Ex2436MaxPQPerformanceDriverI.getRandomStringLengths show that for string
      // length 7 randomString() generates up to 1000 consecutive unique strings,
      // for length 9 it generates up to 10000 consecutive unique strings, for 
      // length 10 it generates up to 100000 consecutive unique strings and for 
      // length 12 it generates up to 1M consecutive unique strings.
      
      for (int l = 0; l < nadj; l++) sa[l] = randomString(len);
      i = 0; j = 0; start = System.currentTimeMillis(); t.reset();
      while (System.currentTimeMillis() < start+adj) { 
        h.insert(sa[j]); h.delMax(); i++; j++; if (j == n) j = 0;
      }
      time = t.num();
      iterations = i;
      // measure overhead
      i = 0; j = 0; start = System.currentTimeMillis(); t.reset();
      while (i < iterations) { 
        s=sa[j]; i++; j++; if (j == n) j = 0;
        b = System.currentTimeMillis() < start+adj;         
      }
      overhead = t.num();
      // correct for overhead
      time -= overhead;
    }
    // normalize k to time = 1000ms
    r = (int)(round((1.*iterations*1000)/time));
    return r;
  }
  
  public static void prfDrvrIIClient() {
    int[] len = new int[]{1,2,3,4,5,10,100,1000}; // key lengths
    int[] n = new int[]{0,1,2,3,4,5,10,100,1000,10000}; // heap sizes
    System.out.println("             initial");
    System.out.println("key length  heap size  iterations in 1 sec");
    System.out.println("----------  ---------  -------------------");
    for (int j = 0; j < n.length; j++) 
      for (int i = 0; i < len.length; i++) {
        int r = prfDrvrII(n[j], len[i]);
        System.out.printf("%-6d      %-8d   %-10d\n", len[i], n[j], r);
      }   
  }

  public static void main(String[] args) {

    prfDrvrIIClient();
    
 /*
                 initial
    key length  heap size  iterations in 1 sec
    ----------  ---------  -------------------
    1           0          181033019 
    2           0          151881140 
    3           0          152312109 
    4           0          175258814 
    5           0          178716709 
    10          0          184028216 
    100         0          173436734 
    1000        0          148661258 
    
    this much seems to show that iterations/sec is independent of key length.
    it takes too much time to compute the rest mostly because of the large
    arrays that must be created to ensure predictable loop overhead. 
    randomString() performance is too variable to be used in the loops.
 
 */
  }
  
  /* prfDrvrII overhead time measurement
  overhead is inversely related to the heap size n, where this means a heap
  populated with n keys and not just a heap initialized with capacity n, because
  as the heap grows it may have to do more work to re-heapify itself after
  insertion and delMax, which causes the number of iterations/second to decrease
  while overhead takes an essentially fixed amount of time per iteration and so
  the total and relative time spent on overhead decreases compared to non-overhead.
  overhead is almost unaffected by key length but may increase somewhat with 
  increasing key length since longer strings may take longer to compare but mostly 
  that doesn't appear to happen for random strings generated by utils.randomString().
  
  heap                             suggested loop run                      suggested String
  size(n) overhead ms  key length  time adjustment(adj) iterations         array size (nadj)
  0       1373-1546    12          2000 ms              79648497-89287066  90000000
  1       527-544      12          700                  30466280-31008984  40000000
  2       263-287      12          400                  14951013-15901871  20000000            
  3       140-164      12          300                  8071468-10135661   12000000
  4       136-150      12          200                  7462925-7893482    10000000
  5       113-128      12          200                  6085962-7263887     9000000
  9       77-90        12          100                  4059461-4616824     7000000
  10      66-76        12          100                  4045792-4185458     6000000  
  100     8-16         12          20                   387262-463523        600000
  1000    1-3          12          5                    34656-38184           60000
  10000   0-1          12          1                    3864-4342              6000
  
*/

}
