package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2327QuickSortIgnoreSmallSubarraysAndFinishWithInsertionSort {

  /* p307  
  2.3.27 Ignore small subarrays. Run experiments to compare the following strategy for
  dealing with small subarrays with the approach described in Exercise 2.3.25: Simply
  ignore the small subarrays in quicksort, then run a single insertion sort after the 
  quicksort completes. Note : You may be able to estimate the size of your computer’s 
  cache memory with these experiments, as the performance of this method is likely to 
  degrade when the array does not fit in the cache.
  
  The results of several comparison doubling tests are below. Based on these, ignoring
  small subarrays and doing an insertion sort at the end is competitive for smaller arrays
  but as array length increases over 10^13-10^14 is becomes clearly slower and has higher
  doubling ratios.  In conclusion the ignoring small subarrays approach is generally
  ineffective, but maybe it has a niche of applicability.
  */ 
  
  public static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) {
    
    // quickVCoM3T9F3v2Cutoff19 is quicksort with cutoff to insertion sort for subarrays
    // with length < 19, median of 3, Tukey ninther and fast 3-way partitioning
    // quickVCoIgssM3T9F3v2Cutoff9 is the same except it cutsoff to ignore sorting at 9
    // and does an insertion sort on the array at the end.
   
    compareQuickSortDoubleAlgs("quickVCoM3T9F3v2Cutoff19", "quickVCoIgssM3T9F3v2Cutoff9", 100);
/*
    quickVCoM3T9F3v2Cutoff19 vs quickVCoIgssM3T9F3v2Cutoff9 average times over 100 trials for random Double arrays
 array length  quickVCoM3T9F3v2Cutoff19   time/prev  quickVCoIgssM3T9F3v2Cutoff9 time/prev
         2^8                0.160                NA             0.180                NA
         2^9                0.240             1.500             0.120             0.667
        2^10                0.850             3.542             1.020             8.500
        2^11                3.830             4.506             3.380             3.314
        2^12                4.780             1.248             1.710             0.506
        2^13                1.110             0.232             1.260             0.737
        2^14                2.780             2.505             3.070             2.437
        2^15                5.340             1.921             5.740             1.870
        2^16               11.320             2.120            12.380             2.157
        2^17               25.700             2.270            29.320             2.368
        2^18               59.560             2.318            69.280             2.363
        2^19              137.180             2.303           159.080             2.296
        2^20              313.960             2.289           360.300             2.265      
*/  
    
    // quickVCoIgssM3T9F3v2Cutoff19 is the same as quickVCoIgssM3T9F3v2Cutoff9 except with
    // cutoff at 19
    
    compareQuickSortDoubleAlgs("quickVCoM3T9F3v2Cutoff19", "quickVCoIgssM3T9F3v2Cutoff19", 100);
/*
    quickVCoM3T9F3v2Cutoff19 vs quickVCoIgssM3T9F3v2Cutoff19 average times over 100 trials for random Double arrays
 array length  quickVCoM3T9F3v2Cutoff19   time/prev quickVCoIgssM3T9F3v2Cutoff19 time/prev
         2^8                0.120                NA             0.220                NA
         2^9                0.140             1.167             0.190             0.864
        2^10                0.790             5.643             1.080             5.684
        2^11                3.640             4.608             2.440             2.259
        2^12                4.220             1.159             0.980             0.402
        2^13                1.380             0.327             1.430             1.459
        2^14                2.700             1.957             2.690             1.881
        2^15                5.290             1.959             5.330             1.981
        2^16               11.260             2.129            11.750             2.205
        2^17               25.700             2.282            28.920             2.461
        2^18               59.880             2.330            71.640             2.477
*/
    
 // quickVCoIgssM3T9F3v2Cutoff25 is the same as quickVCoIgssM3T9F3v2Cutoff9 except with
    // cutoff at 25
    
    compareQuickSortDoubleAlgs("quickVCoM3T9F3v2Cutoff19", "quickVCoIgssM3T9F3v2Cutoff25", 100);
/*
    quickVCoM3T9F3v2Cutoff19 vs quickVCoIgssM3T9F3v2Cutoff25 average times over 100 trials for random Double arrays
 array length  quickVCoM3T9F3v2Cutoff19   time/prev quickVCoIgssM3T9F3v2Cutoff25 time/prev
         2^8                0.100                NA             0.230                NA
         2^9                0.150             1.500             0.200             0.870
        2^10                1.040             6.933             0.820             4.100
        2^11                3.480             3.346             2.400             2.927
        2^12                5.050             1.451             1.710             0.713
        2^13                1.440             0.285             1.340             0.784
        2^14                2.570             1.785             3.010             2.246
        2^15                5.290             2.058             5.410             1.797
        2^16               11.290             2.134            11.670             2.157
        2^17               25.630             2.270            29.060             2.490
        2^18               60.010             2.341            74.610             2.567    
*/    
    
  }

}

