package ex24;

import static pq.MaxPQIntEx2414.findHeapsWithMinOrMaxExchsForXdelMax;
import static v.ArrayUtils.par;

import java.util.List;

import v.Tuple2;

/* p330
  2.4.14  What is the minimum number of items that must be exchanged during a 
  remove the maximum operation in a heap of size N with no duplicate keys? 
  Give a heap of size 15 for which the minimum is achieved. Answer the same 
  questions for two and three successive remove the maximum operations.
  
  Since there's no stated requirement on the uniqueness of exchanged items and
  I'm mostly concerned with exchanges, I'm answering this question in terms of 
  that, which is 1/2 the number of items exchanged.
  
  Doing a restricted search I found 2, 5 and 8 as the minimum number of exchanges
  for 1, 2, and 3 remove the max ops for heaps of size 15 and they all have the
  following heap in common:
  
   [15,14,13,10,9,8,12,1,2,3,4,5,6,7,11]
                  __15__  
                 |      |  
          ______14      13______     
         |      |        |      | 
       _10_    _9_      _8_    _12_   
      |    |  |   |    |   |  |    |
      1    2  3   4    5   6  7   11 
      
   The method in main was used to do the searches.
   
 */

public class Ex2414PQMaxMinExchangesForDelMax {

  public static void main(String[] args) {
  
/* 
  findHeapsWithMinOrMaxExchsForXdelMax finds all heaps for input arrays of length 
  len with distinct elements which have the max or min total number   of exchanges 
  for x delMax ops, reports the results every reportingInterval  or 30 million 
  iterations by default if reportingInterval isn't set and eventually returns the 
  heap arrays and the number of exchanges in a Tuple2. It takes as arguments String 
  maxOrMin which must be "max" or "min", int len for the array length, int x for the 
  number of remove maximum operations, and optionally long...reportingInterval for 
  the number of iterations of array permutations between reports to stdout. The 
  reason for this reporting is that since it goes through all permutations of an int 
  array [1..len] and may not feasibly complete in a reasonable time, however if it 
  completes it returns the results in a Tuple2 which can be unpacked as shown below.  
     
  The command below finds all heaps of int[] range(1,16) ([1..15] which incurs the
  minimum number of exchanges for one delMax (remove the max) operation, reports its
  its results to stdout every 30 million iterations and returns its results in 
  Tuple2<Integer,List<Object[]>>. Since there over a trillion permutations of the array, 
  it probably won't complete, however my experiences is that satisfactory results may be 
  obtained from the first 1-2 billion permutations. */
    
    Tuple2<Integer,List<int[]>> f = findHeapsWithMinOrMaxExchsForXdelMax("min", 15, 3); 
    System.out.println("min exchs ="+f._1);
    System.out.println("heaps with min exchs:");
    List<int[]> g = f._2;
    for (int[] ia : g) par(ia);// print the heaps as 0-based arrays
    
  }

}
