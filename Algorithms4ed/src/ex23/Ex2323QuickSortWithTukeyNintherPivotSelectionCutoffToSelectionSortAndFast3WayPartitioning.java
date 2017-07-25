package ex23;

import static sort.Quicks.compareQuickSortDoubleAlgs;

public class Ex2323QuickSortWithTukeyNintherPivotSelectionCutoffToSelectionSortAndFast3WayPartitioning {

  /* p306  
  2.3.23 Java system sort. Add to your implementation from Exercise 2.3.22 code to
  use the Tukey ninther to compute the partitioning item—choose three sets of three
  items, take the median of each, then use the median of the three medians as the 
  partitioning item. Also, add a cutoff to insertion sort for small subarrays.
  */ 
  
  public static void main(String[] args) {
    
    // sort.Quicks.quickCoM3T9F3v3 integrates cutoff to selection sort, median of
    // three pivot selection, Tukey ninther pivot selection and Bentley-McIlroy
    // fast 3-way partitioning into algo2.5. It's basically a clone of 
    // http://algs4.cs.princeton.edu/23quicksort/QuickX.java with just two methods
    // and everything else inlined and it's slower than quick from 2^9-2^14 for
    // random Doubles.

    compareQuickSortDoubleAlgs("quick", "quickCoM3T9F3v3", 50);
/*
    quick vs quickCoM3T9F3v3 average times over 50 trials for random Double arrays
    array length           quick         time/prev    quickCoM3T9F3v3         time/prev
       2^8                0.200                NA             0.080                NA
       2^9                0.040             0.200             0.500             6.250
      2^10                0.180             4.500             1.480             2.960
      2^11                0.380             2.111             2.940             1.986
      2^12                1.040             2.737             5.560             1.891
      2^13                2.020             1.942             5.360             0.964
      2^14                4.680             2.317             2.700             0.504
      2^15                9.580             2.047             5.560             2.059
      2^16               24.480             2.555            11.900             2.140
      2^17               47.160             1.926            27.160             2.282
      2^18              100.680             2.135            62.800             2.312
      2^19              226.380             2.249           143.480             2.285
      2^20              518.620             2.291           325.160             2.266
      2^21             1163.640             2.244           731.860             2.251
      2^22             2772.180             2.382          1679.640             2.295

*/ 

    
  }

}

