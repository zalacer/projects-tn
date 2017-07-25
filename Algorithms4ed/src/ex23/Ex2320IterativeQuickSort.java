package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2320IterativeQuickSort {

  /* p306  
  2.3.20 Nonrecursive quicksort. Implement a nonrecursive version of quicksort based
  on a main loop where a subarray is popped from a stack to be partitioned, and the 
  resulting subarrays are pushed onto the stack. Note : Push the larger of the subarrays 
  onto the stack first, which guarantees that the stack will have at most lg N entries.
  */ 
  
  public static void main(String[] args) {

    //sort.Quicks.quickIt is an iterative version of quicksort

    compareQuickSortDoubleAlgs("quick", "quickIt", 5);
/*
    quick vs quickIt average times over 5 trials for random Double arrays
    array length           quick         time/prev           quickIt         time/prev
       2^8                0.000                NA             0.000                NA
       2^9                0.000               NaN             3.000          Infinity
      2^10                0.000               NaN             0.000             0.000
      2^11                0.000               NaN             3.200          Infinity
      2^12               15.800          Infinity             9.200             2.875
      2^13                3.000             0.190            21.800             2.370
      2^14                9.200             3.067             9.600             0.440
      2^15                9.600             1.043             6.200             0.646
      2^16               15.600             1.625            21.800             3.516
      2^17               37.600             2.410            46.600             2.138
      2^18               99.800             2.654           103.200             2.215
      2^19              211.800             2.122           243.600             2.360
      2^20              545.600             2.576           521.000             2.139
      2^21             1316.400             2.413          1157.600             2.222
      2^22             2714.800             2.062          3020.200             2.609
      2^23             8807.600             3.244          8177.800             2.708

*/
  }

}

