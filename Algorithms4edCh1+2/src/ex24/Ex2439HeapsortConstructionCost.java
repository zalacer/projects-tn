package ex24;

import static sort.HeapIntEx2439.sort;
import static v.ArrayUtils.range;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import analysis.Timer;

/* p335
  2.4.39 Cost of construction. Determine empirically the percentage of time
  heapsort spends in the construction phase for N = 10^3, 10^6, and 10^9 
 */

public class Ex2439HeapsortConstructionCost {

  public static double timeHeapSortConstruction (int N) {
    // return the percentage of time sort.HeapIntEx2439.sort
    // spends in construction for an int[] of length N
    Random r = new Random(System.currentTimeMillis());
    int[] z = range(1,N+1);
    shuffle(z,r);
    Timer t = new Timer();
    double l = sort(z);
    double k = t.num();
    return l*100/k;
  }

  public static void main(String[] args) {

    System.out.println(timeHeapSortConstruction(1000)); 
    // 22.22222222222222
    
    System.out.println(timeHeapSortConstruction(1000000)); 
    // 81.34556574923548
    
    System.out.println(timeHeapSortConstruction(1000000000)); 

  }

}
