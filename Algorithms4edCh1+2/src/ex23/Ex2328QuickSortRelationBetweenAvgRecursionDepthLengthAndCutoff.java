package ex23;

import static sort.Quicks.printAvgRecDepthVsLengthAndCutoff;

public class Ex2328QuickSortRelationBetweenAvgRecursionDepthLengthAndCutoff {

  /* p307  
  2.3.28 Recursion depth. Run empirical studies to determine the average recursive
  depth used by quicksort with cutoff for arrays of size M, when sorting arrays of N
  distinct elements, for M=10, 20, and 50 and N = 10^3, 10^4, 10^5 , and 10^6. 
  */ 
  
  public static void main(String[] args) {
   
    printAvgRecDepthVsLengthAndCutoff(); // uses Integer arrays with all unique keys
/*
    Average recursive depth vs. length and cutoff
    length    cutoff 10    cutoff 20    cutoff 50
      10^3      150.000       79.000       31.000
      10^4     1536.000      787.500      313.000
      10^5    15229.000     8021.500     3065.000
      10^6   152296.000    80131.500    30748.000
*/
    
  }

}

