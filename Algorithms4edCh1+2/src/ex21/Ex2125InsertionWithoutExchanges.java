package ex21;

import static sort.SortCompare.*;

import edu.princeton.cs.algs4.StdOut;

/* p267
  2.1.25 Insertion sort without exchanges. Develop an implementation of insertion sort
  that moves larger elements to the right one position with one array access per entry,
  rather than using exch(). Use SortCompare to evaluate the effectiveness of doing so.

  The implementation of insertion sort without exchanges is 
  sort.InsertionSort.sortWithoutExchanges().
  
  Testing with sort.SortCompare shows it's faster than regular insertion sort for random 
  arrays excluding JVM caching factors that can affect sequentially run trials. Test results 
  are shown below.
 */

public class Ex2125InsertionWithoutExchanges {

  public static void sortCompare(String alg1, String alg2, int n, int trials) {
    double time1, time2;
    time1 = timeRandomInput(alg1, n, trials);   // Total for alg1. 
    time2 = timeRandomInput(alg2, n, trials);   // Total for alg2. 
    StdOut.printf("For %d random Doubles %s is", n, alg1); 
    StdOut.printf(" %.3f times faster than %s\n", time2/time1, alg2); 
  }

  public static void main(String[] args) {

    //    Integer[] ia = {7,6,5,4,3,2,1,0};
    //    sortWithoutExchanges(ia);
    //    pa(ia); //Integer[0,1,2,3,4,5,6,7]
    //    
    //    ia = new Integer[]{7,6,5,4,3,2,1,0};
    //    sortWithSentinelAndWithoutExchanges(ia);
    //    pa(ia); //Integer[0,1,2,3,4,5,6,7]

    int[] sizes = {10, 100, 1000, 10000, 100000};
    for (int i : sizes) sortCompare("sortWithoutExchanges",  "Insertion", i, 10);

    /*    
    For 10 random Doubles sortWithoutExchanges is 0.000 times faster than Insertion
    For 100 random Doubles sortWithoutExchanges is 1.500 times faster than Insertion
    For 1000 random Doubles sortWithoutExchanges is 0.587 times faster than Insertion
    For 10000 random Doubles sortWithoutExchanges is 2.011 times faster than Insertion
    For 100000 random Doubles sortWithoutExchanges is 1.882 times faster than Insertion
     */  

  }
  
}
