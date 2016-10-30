package ex22;

import static sort.SortCompare.timeRandomInput;

public class Ex2228TopDownVsBottomUpMergeSorts {

  /* p287
  2.2.28 Top-down versus bottom-up. Use SortCompare to compare top-down and 
  bottom-up mergesort for N=10^3 , 10*4 , 10^5 , and 10^6 .
   */ 

  public static void sortCompareTest(String alg1, String alg2, int n, int trials) {

    double time1, time2;
    time1 = timeRandomInput(alg1, n, trials);
    time2 = timeRandomInput(alg2, n, trials);
    System.out.printf("For %d random Doubles %s is", n, alg1); 
    System.out.printf(" %.1f times faster than %s\n\n", time2/time1, alg2); 
  }
  
  public static void main(String[] args) {
    
    int[] len = {1000, 10000, 100000, 1000000};
    int[] trials = {101, 31, 11, 3};
    for (int i = 0; i < len.length; i++) {
      sortCompareTest("topDownAcCoSm", "bottomUpCoFmSm", len[i], trials[i]);
      sortCompareTest("bottomUpCoFmSm", "topDownAcCoSm", len[i], trials[i]);
    }
    
/*
    For 1000 random Doubles topDownAcCoSm is 0.6 times faster than bottomUpCoFmSm
    
    For 1000 random Doubles bottomUpCoFmSm is 0.1 times faster than topDownAcCoSm
    
    For 10000 random Doubles topDownAcCoSm is 1.5 times faster than bottomUpCoFmSm
    
    For 10000 random Doubles bottomUpCoFmSm is 0.8 times faster than topDownAcCoSm
    
    For 100000 random Doubles topDownAcCoSm is 1.2 times faster than bottomUpCoFmSm
    
    For 100000 random Doubles bottomUpCoFmSm is 0.8 times faster than topDownAcCoSm
    
    For 1000000 random Doubles topDownAcCoSm is 1.5 times faster than bottomUpCoFmSm
    
    For 1000000 random Doubles bottomUpCoFmSm is 0.9 times faster than topDownAcCoSm


*/
  }
  
}

