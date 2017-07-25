package ex23;

import static analysis.Log.*;

public class Ex2306QuickSortComputeAvgNumCompares4DistinctVals {

  /* p303  
  2.3.6  Write a program to compute the exact value of C(N), and compare the 
  exact value with the approximation 2NlnN, for N = 100, 1,000, and 10,000.
  
   From p294 C(N) ~ 2*(N + 1)(1/3 + 1/4 + . . . + 1/(N + 1))
   exactly C(N) = 2*(N + 1)(H(N+1) - 1.5) 
   where H(i) is the ith harmonic number  = 1 + 1/2 + ... + 1/i == ln(N) + O(1);
  */ 
  
  public static double computeC(int n) {
    double h = 0;
    for (int i = 3; i < n+2; i++) h += 1./i;
    return 2.*(n+1)*h;
  }
  
  public static void computeAndCompareP()  {
    System.out.println("    N           C(N)          2NlnN     C(N)/2NlnN");
    int[] n = {100, 1000, 10000}; double c, x;
    for (int i : n) {
      c = computeC(i); x = 2.*i*ln(i);
      System.out.printf("%5d     %10.3f     %10.3f        %3.3f\n", i, c, x, c/x);
    }
  }
 
  public static void main(String[] args) {
    
    computeAndCompareP();    
    /*
       N           C(N)          2NlnN     C(N)/2NlnN
      100        746.850        921.034        0.811
     1000      11984.913      13815.511        0.867
    10000     165770.696     184206.807        0.900 
  */
  }
                      
}

