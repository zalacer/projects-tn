package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2317QuickSortRemoveBoundsChecksWithSentinels {

  /* p305  
  2.3.17 Sentinels. Modify the code in Algorithm 2.5 to remove both bounds checks 
  in the inner while loops. The test against the left end of the subarray is 
  redundant since the partitioning item acts as a sentinel ( v is never less than  
  a[lo]). To enable removal of the other test, put an item whose key is the largest 
  in the whole array into  a[length-1] just after the shuffle. This item will never 
  move (except possibly to be swapped with an item having the same key) and will serve 
  as a sentinel in all subarrays involving the end of the array. Note : When sorting 
  interior subarrays, the leftmost entry in the subarray to the right serves as a 
  sentinel for the right end of the subarray.
  
  Since making the last item in the array the largest was ineffective for eliminating
  the i == hi test without recoding partition() in other respects, I recoded it by
  changing the pivot to the middle element, put an i<j test in the outer while, removed
  sentinel tests for both inner whiles and put an i==j test just before the exchange at 
  the end of the outer while. This revised partition method is shown below. 
  
      // sort.Quicks.quickSen.partition
      Function2<Integer,Integer,Integer> partition = (lo,hi) -> {
        int i = lo,  j = hi; 
        int middle = lo + (hi - lo) / 2;
        T pivot = z[middle];
        T v = z[lo]; T t;
        while (i<j) { // j-i is the sentinel with the equivalent test (j-i)>0
          while (z[i].compareTo(pivot)<0)
            i++; 
          while (pivot.compareTo(z[j])<0) 
            j--;
          if (j==i) break;
          exch.accept(i,j);
        }
        return j;
      };
      
  With this partition method it's not necessary or useful to put a highest element 
  at the end of the array before sorting it, which was worse than innefective anyway
  because it delays actual sorting. 
  
  I think at least one sentinel must be retained with variations of algo2.5, however
  it could be changed from lo to hi by making hi the pivot as I did in my iterative
  version of it.
      
  Below are results from a doubling test comparing quickSen to aglo2.5 quick. Both
  take a boolean arg to toggle shuffle. For the test they were run with shuffle off 
  since randomized Double arrays were provided.
  */ 
  
  public static void main(String[] args) {

    compareQuickSortDoubleAlgs("quick", "quickSen", 5);
/*
    quick vs quickSen average times over 5 trials for random Double arrays
    array length           quick         time/prev          quickSen         time/prev
       2^8                0.800                NA             1.800                NA
       2^9                0.200             0.250             0.400             0.222
      2^10                0.400             2.000             0.600             1.500
      2^11                0.800             2.000             0.800             1.333
      2^12                2.000             2.500             4.800             6.000
      2^13               15.600             7.800            19.200             4.000
      2^14                5.800             0.372            17.000             0.885
      2^15               12.000             2.069            10.200             0.600
      2^16               19.400             1.617            19.600             1.922
      2^17               37.000             1.907            39.400             2.010
      2^18               84.600             2.286            84.600             2.147
      2^19              211.200             2.496           206.400             2.440
      2^20              406.200             1.923           435.800             2.111
      2^21             1360.200             3.349           950.200             2.180
      2^22             3069.200             2.256          2462.600             2.592
      2^23             7967.400             2.596          6457.800             2.622
      2^24            16285.600             2.044         18073.400             2.799
      2^25            33612.600             2.064         32176.600             1.780
  
*/
  
  }

}

