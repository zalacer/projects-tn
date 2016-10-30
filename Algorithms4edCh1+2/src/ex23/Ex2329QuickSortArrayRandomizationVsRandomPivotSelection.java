package ex23;

import static sort.Quicks.testRandomPivotSelectionVsArrayRandomizationSorted;
import static sort.Quicks.testRandomPivotSelectionVsArrayRandomizationReverseSorted;

public class Ex2329QuickSortArrayRandomizationVsRandomPivotSelection {

  /* p307  
  2.3.29 Randomization. Run empirical studies to compare the effectiveness of
  the strategy of choosing a random partitioning item with the strategy of initially
  randomizing the array (as in the text). Use a cutoff for arrays of size M, and 
  sort arrays of N distinct elements, for M=10, 20, and 50 and N = 10^3, 10^4, 10^5, 
  
  In all cases choosing a random pivot way outperforms shuffling the array.  At least
  partly this is due to the time taken by shuffling which is included in the runtimes.
  
  */ 
  
  public static void main(String[] args) {
   
    testRandomPivotSelectionVsArrayRandomizationSorted(100);
    // 
/*
    100 trials for each length and cutoff
    using sorted Integer arrays with all unique keys
    runtimes in ms
                      cutoff 10                 cutoff 20                 cutoff 50
    length      shuffle     rndPivot      shuffle     rndPivot      shuffle     rndPivot
    ------      -------     --------      -------     --------      -------     --------
     10^3         1.200        0.320        1.930        0.840        2.630        0.750
     10^4         4.780        0.400        2.060        0.300        2.020        0.230
     10^5        21.810        5.200       26.300        4.060       26.520        3.190
     10^6       340.810       62.450      405.270       55.910      420.290       56.130
 
*/
    
    testRandomPivotSelectionVsArrayRandomizationReverseSorted(100);
/*
    100 trials for each length and cutoff
    using reverse sorted Integer arrays with all unique keys
    Runtimes in ms
                      cutoff 10                 cutoff 20                 cutoff 50
    length      shuffle     rndPivot      shuffle     rndPivot      shuffle     rndPivot
    ------      -------     --------      -------     --------      -------     --------
     10^3         1.170        0.260        1.820        1.050        2.730        1.050
     10^4         7.000        3.480        5.620        0.490        2.160        0.390
     10^5        20.850        6.730       27.490        5.840       28.520        5.070
     10^6       333.200       83.650      419.070       76.300      443.020       69.380
    
*/
  }

}

