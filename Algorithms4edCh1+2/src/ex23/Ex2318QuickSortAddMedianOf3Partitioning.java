package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2318QuickSortAddMedianOf3Partitioning {

  /* p305  
  2.3.18 Median-of-3 partitioning. Add median-of-3 partitioning to quicksort, as 
  described in the text (see page 296). Run doubling tests to determine the 
  effectiveness of the change.
  */ 
  
  public static void main(String[] args) {
    
    //sort.Quicks.quickM3 uses median of 3 partitioning for all array lengths
    compareQuickSortDoubleAlgs("quick", "quickM3", 5);
/*
    quick vs quickM3 average times over 5 trials for random Double arrays
    array length           quick         time/prev           quickM3         time/prev
    quick vs quickM3 average times over 5 trials for random Double arrays
    array length           quick         time/prev           quickM3         time/prev
       2^8                1.200                NA             1.400                NA
       2^9                0.200             0.167             0.600             0.429
      2^10                0.800             4.000             0.600             1.000
      2^11                2.200             2.750             1.400             2.333
      2^12               10.600             4.818             7.800             5.571
      2^13                9.600             0.906            24.400             3.128
      2^14                5.000             0.521            41.400             1.697
      2^15               10.000             2.000             9.400             0.227
      2^16               17.400             1.740            16.400             1.745
      2^17               39.600             2.276            38.600             2.354
      2^18               91.600             2.313            84.400             2.187
      2^19              197.800             2.159           195.000             2.310
      2^20              432.000             2.184           438.600             2.249
      2^21             1452.600             3.363           988.800             2.254
      2^22             3197.000             2.201          2376.400             2.403
      2^23             8156.800             2.551          6362.400             2.677

*/
  
  }

}

