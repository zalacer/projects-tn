package ex24;

/* p331
  2.4.26 Heap without exchanges. Because the exch() primitive is used in the
  sink() and swim() operations, the items are loaded and stored twice as often 
  as necessary. Give more efficient implementations that avoid this inefficiency, 
  a la insertion sort (see  Exercise 2.1.25).
  
  This is demonstrated by Abhijith Madhav in his MaxPQ implementation at
  https://github.com/AbhijithMadhav/Data-Structures-And-Algorithms/blob/master/Data%20Structures%20and%20Algorithms/src/ds/pq/MaxPQ.java,
  but note the order of comparison on line 215 should be inverted (as of 20160930).
  This version of MaxPQ (with the correction) is also locally avaiable at 
  pq.MaxPQAbhijithMadhav.
  
*/

public class Ex2426HeapWithoutExchanges {
 
  public static void main(String[] args) {
 
    }

}
