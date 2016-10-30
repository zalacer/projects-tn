package ex24;

import static sort.Quicks.sort;
import static v.ArrayUtils.*;

import java.util.Arrays;

import pq.MinPQ;

/* p330
  2.4.17  Prove that building a minimum-oriented priority queue of size k then
  doing N-k replace the minimum (insert followed by remove the minimum) ops 
  leaves the k largest of the N items in the priority queue.
  
  This is obvious because the op of removing the min has the effect of leaving the 
  remaining larger items, including those which may have been recently inserted. If
  an item is always inserted prior to removing the minimum, obviously the total 
  number of items in the pq remains constant. In other words a min pq can be used to 
  retain the top X number of items. For example see main.
    
 */

public class Ex2417EffectOfReplaceMinOpsOnMinPQ {
  
  public static void main(String[] args) {
    
    int N = 6, k = 3;
    Integer[] a = map(rangeInteger(0,k), x -> 2*x);
    par(a); //[0,2,4]
    Integer[] b = map(rangeInteger(0,N-k), x -> 3*x);
    par(b); //[0,3,6]
    MinPQ<Integer> pq = new MinPQ<Integer>(a);
    pq.show(); //0,2,4
    for (Integer i : b) { pq.insert(i); pq.delMin(); }
    pq.show(); //3 6 4
    assert pq.size() == k;
    assert Arrays.equals(sort(pq.toArray()),takeRight(sort(append(a,b)),k));

  }

}
