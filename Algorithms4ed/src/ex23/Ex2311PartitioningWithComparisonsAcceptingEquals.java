package ex23;

import static sort.Quicks.compareQuickSortIntAlgs;
import static sort.Quicks.quickIntAePSub012;
import static sort.Quicks.quickIntPSub012;
import static v.ArrayUtils.par;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Ex2311PartitioningWithComparisonsAcceptingEquals {

  /* p303  
  2.3.11  Suppose that we scan over items with keys equal to the partitioning item’s
  key instead of stopping the scans when we encounter them. Show that the running time
  of this version of quicksort is quadratic for all arrays with just a constant number
  of distinct keys.
  
  This happens since when compares accept equals quicksort does very poor partitioning
  in which many zero length subarrays are created.  A case example of this is shown
  below along with the doubling test. The latter also showed that quicksort incurs a 
  high cost in terms of stack space especially when compares accept equals. The miminal
  utilization of stack is what I like about bottom-up mergesort, especially with Java
  which has no optimization of tail recursion.
  
  Actually proving that quicksort implemented with compares that accept equals is 
  quadratic for all arrays with distinct keys appears to be long and complicated and 
  requires concepts and methods not provided in this course. An example paper using the 
  required methodology is "Sorting Discrete i.i.d. Inputs: Quicksort Is Optimal" by 
  Sebastian Wild published on 8/16/2016 and available at https://arxiv.org/abs/1608.04906
  and in this project at SortingDiscreteIIDInputs-Quicksort is Optimal-Wild-2016.pdf.
  This paper is relevant because it proves for the first time the Sedgewick-Bentley 
  conjecture on the optimality of median-of-k Quicksort on equal keys, which has been 
  open since 2002.
  */ 
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
          
  /* showing quicksort is quadratic for arrays with a constant number of distinct keys.
     ----------------------------------------------------------------------------------
     both quickIntPSub012 quickIntAEPSub012 do an algorithm 2.5 quicksort and return a 
     three element array containing the number of partitions and subarrays of lengths 0, 
     1 and 2, however quickIntAEPSub012 is modified to accept equals in compares.  */
    
    Random r1 = SecureRandom.getInstanceStrong();
    int[] s1 = new int[10000]; 
    
    for (int i = 0; i < s1.length; i++) s1[i] = r1.nextInt(7);
    int[] s2 = s1.clone();
    
    int[] q1 = quickIntPSub012(s1); 
    par(q1); //[5898,1797,4102,1797] #partitions and subarrays of lengths 0, 1 and 2
    
    int[] q2 = quickIntAePSub012(s2);
    par(q2); //[17816,7817,1092,596] #partitions and subarrays of lengths 0, 1 and 2
    
    /* doubling ratio test
    In this test quickInt is quicksort algo2.5 implemented for int[]s and quickIntAE
    is the same except modified with compares that accept equals and both are given
    arrays with the same values populated randomly with ints from 0 to 6. Even with
    1GB of Xss cannot go above array size 2*15 consistently without quickIntAE crashing.
    The test still shows that quickIntAE appears to be approaching a doubling ratio of
    4 showing that its run time grows quadratically. */ 
    compareQuickSortIntAlgs("quickInt", "quickIntAe", 7, 100);
 /* quickInt vs quickIntAE average times over 100 trials and ratios to previous
    array length        quickInt         time/prev        quickIntAE         time/prev
       2^8                0.080                NA             0.330                NA
       2^9                0.070             0.875             0.210             0.636
      2^10                0.220             3.143             0.320             1.524
      2^11                0.320             1.455             0.690             2.156
      2^12                0.500             1.563             1.770             2.565
      2^13                0.550             1.100             3.790             2.141
      2^14                0.940             1.709            12.010             3.169
      2^15                1.640             1.745            45.970             3.828   
   
    To work around the StackOverflowError caused by recursive quickIntAE I implemented
    both it and quickInt iteratively as quickIntIt and quickIntItAE where only the 
    latter's compares accept equals. However, in this case it made no run time 
    performance difference: */   
    compareQuickSortIntAlgs("quickIntIt", "quickIntItAe", 7, 100);
 /* quickIntIt vs quickIntItAE average times over 100 trials and ratios to previous
    array length      quickIntIt         time/prev      quickIntItAE         time/prev
       2^8                0.170                NA             0.130                NA
       2^9                0.110             0.647             0.030             0.231
      2^10                0.190             1.727             0.150             5.000
      2^11                0.220             1.158             0.200             1.333
      2^12                0.570             2.591             0.470             2.350
      2^13                0.950             1.667             0.920             1.957
      2^14                1.950             2.053             1.910             2.076
      2^15                4.140             2.123             4.190             2.194
      2^16                8.200             1.981             7.930             1.893
      2^17               16.470             2.009            16.000             2.018
      2^18               32.870             1.996            33.100             2.069
      2^19               68.500             2.084            68.290             2.063
      2^20              141.950             2.072           142.280             2.083
      2^21              299.290             2.108           296.760             2.086      
*/   
  }
                      
}

