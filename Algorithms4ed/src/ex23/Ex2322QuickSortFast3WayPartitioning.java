package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;
import static sort.Quicks.compareQuickSortIntegerAlgs;

public class Ex2322QuickSortFast3WayPartitioning {

  /* p306  
  2.3.22 Fast 3-way partitioning. ( J. Bentley and D. McIlroy) Implement an entropy
  optimal sort based on keeping item's with equal keys at both the left and right 
  ends of the subarray. Maintain indices p and q such that a[lo..p-1] and a[q+1..hi] 
  are all equal to  a[lo], an index i such that a[p..i-1] are all less than  a[lo] ,
  and an index  j such that  a[j+1..q] are all greater than a[lo]. Add to the inner 
  partitioning loop code to swap a[i] with a[p] (and increment  p ) if it is equal to  
  v and to swap  a[j] with  a[q] (and decrement  q ) if it is equal to v before the 
  usual comparisons of a[i] and a[j] with v. After the partitioning loop has 
  terminated, add code to swap the items with equal keys into position.
  
  Note : This code complements the code given in the text, in the sense that it does 
  extra swaps for keys equal to the partitioning item’s key, while the code in the text 
  does extra swaps for keys that are not equal to the partitioning item’s key.
  
             __________________________________________________
            |   |                                              |
     before | v |                                              |
            |___|______________________________________________|
             ^                                                ^
             lo                                               hi
  
             __________________________________________________
            |     |          |                |          |     |
     during | =v  |  <v      |                |     >v   |  =v |
            |_____|__________|________________|__________|_____|
             ^    ^           ^                ^          ^   ^
             lo   p           i                j          q   hi
    
             __________________________________________________
            |            |                          |          |
      after |  <v        |          =v              |    >v    |
            |____________|__________________________|__________|
             ^            ^                          ^        ^
             lo           j                          i        hi    
  
  */ 
  
  public static void main(String[] args) {
    
    // sort.Quicks.quickF3 integrates fast 3-way partitioning into algo2.5 
    // with no other changes; sort.Quicks.quick is plain algo2.5. comparing them
    // with random doubles measures the overhead of fast 3-way partitioning since
    // it has it has no advantage when there aren't repeated values since then
    // the pivot won't be repeated.

    compareQuickSortDoubleAlgs("quick", "quickF3", 5);
/*
    quick vs quickFast3Way average times over 5 trials for random Double arrays
    array length           quick         time/prev     quickFast3Way         time/prev
       2^8                1.200                NA             3.000                NA
       2^9                0.200             0.167             0.400             0.133
      2^10                0.600             3.000             1.000             2.500
      2^11                1.600             2.667             1.600             1.600
      2^12                5.600             3.500             3.200             2.000
      2^13               24.400             4.357             8.000             2.500
      2^14               43.400             1.779            12.600             1.575
      2^15               40.600             0.935            35.000             2.778
      2^16               16.800             0.414            21.800             0.623
      2^17               41.400             2.464            41.600             1.908
      2^18               86.400             2.087            95.400             2.293
      2^19              183.600             2.125           222.600             2.333
      2^20              418.000             2.277           481.400             2.163
      2^21             1014.800             2.428          1409.200             2.927
      2^22             2331.400             2.297          3364.800             2.388
      2^23             6771.600             2.905          8212.600             2.441
    
*/ 
    
    // this shows the advantage of fast 3-way partitioning by using Integer arrays
    // with repeated values from 0 through 6.
    
    compareQuickSortIntegerAlgs("quick", "quickF3", 7, 5);
/*
    quick vs quickFast3Way average times over 5 trials for random Integer[]s with keys in [0..6]
    array length           quick         time/prev     quickFast3Way         time/prev
       2^8                1.200                NA             2.200                NA
       2^9                0.400             0.333             0.600             0.273
      2^10                1.200             3.000             1.000             1.667
      2^11                1.400             1.167             0.400             0.400
      2^12                7.000             5.000             1.200             3.000
      2^13               21.800             3.114             2.400             2.000
      2^14               19.800             0.908             8.000             3.333
      2^15               21.200             1.071             6.800             0.850
      2^16               20.200             0.953            14.400             2.118
      2^17               31.400             1.554            19.600             1.361
      2^18               66.800             2.127            19.600             1.000
      2^19              131.200             1.964            40.200             2.051
      2^20              256.800             1.957            80.000             1.990
      2^21              559.000             2.177           144.200             1.803
      2^22             1159.200             2.074           473.600             3.284
      2^23             2545.000             2.195           663.600             1.401
      2^24             5364.400             2.108          1285.600             1.937
    
*/    
  }

}

