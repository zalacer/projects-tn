package ex24;

import static sort.HeapIntEx2416Ex2441.findMaxCompares;
import static sort.HeapIntEx2416Ex2441.findMinCompares;


/* p330
  2.4.16  For N=32, give arrays of items that make heapsort use as many and as 
  few compares as possible.
  
  The fewest I found is 187 comparisons for
  [32,31,30,1,2,3,28,4,5,6,7,8,9,10,27,11,12,13,14,15,16,17,18,29,19,20,21,22,23,24,26,25].
  
  The most I found is 236 comparisons for
  [1,2,3,4,5,6,7,8,9,10,11,12,28,13,14,15,16,17,18,19,20,21,22,23,24,25,27,32,29,30,31,26].
 
  This results probably aren't the absolute best and worst cases because they're based
  on small subsets of array permutations due to time constraints. The methods used are 
  setup to run in main.
   
 */

public class Ex2416HeapSortCasesForMaxAndMinCompares {

  public static void main(String[] args) {
    
    findMinCompares();
    
    findMaxCompares();  

  }

}
