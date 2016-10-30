package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2324SampleSort {

  /* p306  
  2.3.24 Samplesort. ( W. Frazer and A. McKellar) Implement a quicksort based 
  on using a sample of size 2k-1. First, sort the sample, then arrange to have 
  the recursive routine partition on the median of the sample and to move the 
  two halves of the rest of the sample to each subarray, such that they can be 
  used in the subarrays, without having to be sorted again. This algorithm is 
  called samplesort.
  
  From Algorithms in C parts 1-4, Sedgewick:
  This algorithm, which uses about NlgN comparisons when k is about lgN-lglgN,
  is called samplesort. Run empirical studies to determine the best value of 
  sample size for N=10^3-10^6. Does it matter whether quicksort or samplesort 
  is used?
  Re.: https://books.google.com/books?id=7OdVCgAAQBAJ&pg=PT641&lpg=PT641&dq=sedgewick+samplesort&source=bl&ots=4lvppXjP_K&sig=jo6h3STrCLKyRG_x432Fx2wn204&hl=en&sa=X&ved=0ahUKEwjypLy-v5jPAhVG6YMKHUJGCrI4ChDoAQglMAE#v=onepage&q=sedgewick%20samplesort&f=false
  
  My implementation is sort.Quicks.sampleSort.  Regular quickSort alg2.5 runs faster
  at least when using sample size ceil(2*(lg(N)-lg(lg(N)))-1) with sampleSort and
  the latter calls the former to sort samples with length >8. Below is a  doubling
  test comparing the two.
  
  */ 
  
  public static void main(String[] args) {
    
 
    compareQuickSortDoubleAlgs("quick", "sampleSort", 50);
/*
    quick vs sampleSort average times over 50 trials for random Double arrays
    array length           quick         time/prev        sampleSort         time/prev
       2^8                0.220                NA             0.840                NA
       2^9                0.980             4.455             1.400             1.667
      2^10                0.720             0.735             1.100             0.786
      2^11                0.380             0.528             3.060             2.782
      2^12                0.740             1.947             2.080             0.680
      2^13                1.740             2.351             4.160             2.000
      2^14                3.440             1.977             8.540             2.053
      2^15                7.120             2.070            16.920             1.981
      2^16               15.740             2.211            35.940             2.124
      2^17               35.640             2.264            80.920             2.252
      2^18               80.080             2.247           160.660             1.985
      2^19              185.520             2.317           347.800             2.165
      2^20              435.720             2.349           759.960             2.185
      2^21              990.480             2.273          1776.700             2.338
      2^22             2344.700             2.367          4535.040             2.553
*/    

    
  }

}

