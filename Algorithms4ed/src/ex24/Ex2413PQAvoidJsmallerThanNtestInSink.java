package ex24;

import java.util.Iterator;
import java.util.Random;

import pq.MaxPQex2413;
import v.ArrayUtils;

/* p330
  2.4.13  Describe a way to avoid the  j < N test in  sink().
  
  For sinking pq[k] first do it when it has two children while 2*k+1<=n.  When
  that's done, then if 2*k==n sink it again if less(k,n), knowing it has only a 
  child on the left. This method is implemented in https://github.com/AbhijithMadhav/Data-Structures-And-Algorithms/blob/master/Data%20Structures%20and%20Algorithms/src/ds/pq/MaxPQ.java
  but it has a mistake on line 215-216 as follows: 
      if (less(pq[j - 1], pq[j]))
              j--;
  should be
      if (less(pq[j], pq[j - 1]))
              j--;
              
  With this correction and using a regular (full) instead of a half exchange the 
  sink method is:
  
    private void sink(int k) {
      Key t = pq[k];
      while (2*k+1 <= n) { // pq[k] has 2 children
        int j = 2*k;
        if (less(j, j+1)) j++;
        if (!less(k, j)) break;
        exch(k, j);
        k = j;
      }
      if (2*k == n) // pq[k] has only one child
        if (less(k, n)) {
          exch(k, n);
          k = n;
        }
   }
   
   A demo is included below.
   
  
 */

public class Ex2413PQAvoidJsmallerThanNtestInSink {

  public static void main(String[] args) {
    Random r = new Random(System.currentTimeMillis());
    Integer[] x = new Integer[16384];
    for (int i = 0; i < x.length; i++) x[i] = r.nextInt(163);
    MaxPQex2413<Integer> pq = new MaxPQex2413<Integer>(x);
    assert pq.isMaxHeap();
    int max = pq.max();
    assert max == ArrayUtils.max(x);
    Iterator<Integer> it = pq.iterator(); int c = 0;
    while(it.hasNext() && it.next() == max) c++;
    System.out.println("the top "+c+" items are "+max);
      
  }

}
