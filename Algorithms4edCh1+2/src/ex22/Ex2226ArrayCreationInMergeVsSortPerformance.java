package ex22;

import static sort.Merges.compare;
import static sort.SortCompare.timeRandomInput;

public class Ex2226ArrayCreationInMergeVsSortPerformance {

  /* p287
  2.2.26 Array creation. Use SortCompare to get a rough idea of the effect on 
  performance on your machine of creating aux[] in merge() rather than in sort().
   */ 
 
  public static void main(String[] args)  {
    
    // compare is relatively unbiased by hysteresis effects because for every trial 
    // the order of execution of the algos is alternated.
    compare("topDown",-1,"topDownAm",-1,100); // int topDownAm aux created in merge 
/*    
    this shows topDownAm is slower than topDown for all array sizes, and from array
    length of 1K appears to be exponentially slower or worse.
    
    for random Double arrays topDown cutoff -1 topDownAm cutoff -1
    array                      average times
    length              topDown         topDownAm
        10                0.070             0.210
       100                0.070             0.090
        1K                0.870             1.800
       10K                2.260            53.210
*/    
    
    // SortCompare test
    String alg1 = "topDown"; 
    String alg2 = "topDownAm"; // topDown with Aux created in Merge
    int n = 2200;
    int trials = 10;
    double time1, time2;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    System.out.println("time1="+time1);
    System.out.println("time2="+time2);
    System.out.printf("For %d random Doubles %s is", n, alg1); 
    System.out.printf(" %.1f times faster than %s\n", time2/time1, alg2); 

/*
 * SortCompare is heavily biased by hysteresis effects such as caching.
   It shows topDownAm is faster than topDown for array sizes <= 2100
    time1=0.077
    time2=0.047
    For 1000 random Doubles topDown is 0.6 times faster than topDownAm
    
    time1=0.07900000000000001
    time2=0.041
    For 1000 random Doubles topDown is 0.5 times faster than topDownAm
    
    time1=0.11900000000000001
    time2=0.107
    For 2000 random Doubles topDown is 0.9 times faster than topDownAm
    
    time1=0.128
    time2=0.12000000000000001
    For 2100 random Doubles topDown is 0.9 times faster than topDownAm
    
    time1=0.085
    time2=0.15900000000000003
    For 2200 random Doubles topDown is 1.9 times faster than topDownAm
    
    time1=0.14100000000000001
    time2=0.18500000000000003
    For 2500 random Doubles topDown is 1.3 times faster than topDownAm
    
    time1=0.10300000000000001
    time2=0.13
    For 2200 random Doubles topDown is 1.3 times faster than topDownAm
    
    time1=0.259
    time2=0.8090000000000002
    For 10000 random Doubles topDown is 3.1 times faster than topDownAm

*/  
  }
  
}

