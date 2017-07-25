package ex24;

import static v.ArrayUtils.*;

import java.util.Iterator;

import pq.MaxPQInt;

/* p330
  2.4.18  In  MaxPQ , suppose that a client calls insert() with an item that 
  is larger than all items in the queue, and then immediately calls  delMax(). 
  Assume that there are no duplicate keys. Is the resulting heap identical to 
  the heap as it was before these operations? Answer the same question for two
  insert() operations (the first with a key larger than all keys in the queue 
  and the second for a key larger than that one) followed by two  delMax() 
  operations.
  
  Yes and yes.  In fact it appears to work for any number of insertions of a
  largest element followed by the same number of delMaxs.  A way to test this
  is given below.
   
 */

public class Ex2418HowDoesInsertThenDelMaxAffectMaxPQ {
  
  public static void testReversability(int len, int ops, int limit) {  
    // test reversability of a max heap after ops insertions of a max item 
    // followed by ops delMaxs for up to limit permutations of range(0,len).
    // at the first failure print the heap array that failed and exit else
    // print "all good" on exit.
    // if limit < 1 all permutations are tested.
    if (len < 2 || ops < 1 || limit == 0) return;
    int max = len, d = 0; boolean f = false;
    Iterator<int[]> it = permutations(range(0,len));
    LOOP:
    while (it.hasNext()) {
      int[] a = it.next();
      MaxPQInt pq = new MaxPQInt(a);
      int[] b = pq.toArray();
      for (int i = 0; i < ops; i++) pq.insert(max++);
      for (int i = 0; i < ops; i++) pq.delMax();
      int[] c =  pq.toArray();
      for (int i = 0; i < b.length; i++)
        if (b[i] != c[i]) { par(b); f = true; break LOOP; }
      if (limit > 0 && ++d == limit) break;
    }
    if (!f) System.out.println("all good");
  }

  public static void main(String[] args) {
    
    testReversability(250,501, 1000); 
    
  }

}
