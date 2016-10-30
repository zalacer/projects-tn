package ex24;

import static v.ArrayUtils.*;

import java.util.Random;

import pq.MaxPQex2427;

/* p331
  2.4.27 Find the minimum. Add a min() method to MaxPQ. Your implementation
  should use constant time and constant extra space.
  
  This is done in pq.MaxPQex2427 using a Key field named minKey that references
  the Key with the minimum value and which is simply dereferenced by min() 
  provided the PQ isn't empty. The value of minKey is evaluated and possibly
  reset whenever a Key is input to the PQ, namely in the array constructor and
  insert(). It's also evaluated and possibly reset whenever a Key is removed from 
  the PQ, namely in delMax(). Whenever the PQ is about to become empty minKey is
  reset to null (in delMax()) and whenever it's been empty and a new Key is added
  minKey is set to its value.  If the PQ isn't empty and a new Key is added 
  minKey is compared to it and if the the Key is less than minKey, then minKey is
  set to it. So at all times if anything is in the PQ, minKey is not null and is set
  to the minimum key. If nothing is in the PQ minKey is null, and if the PQ has just
  one Key then minKey == min() == max(). getMinKey() is provided for demonstration
  that minKey == null when the PQ is empty. A similar scheme could be used for
  PQs with primitive keys, except then minKey would also be a primitive duplicating
  the value of the minimum Key. Certainly in this case it could be boxed, but that
  would needlessly add a small amount of overhead with no added value.
  
  Below is a demo of min() using pq.MaxPQex2427.
  
*/

public class Ex2427MaxPQMinMethodWithConstantTimeAndSpace {
 
  public static void main(String[] args) {

    Integer[] a;
    a = rangeInteger(1,21); // a = {1,2,...,20}, min value=1
    Random r = new Random(System.currentTimeMillis());
    shuffle(a,r);
    pa(a); System.out.println();
    MaxPQex2427<Integer> pq = new MaxPQex2427<Integer>(a);
    pa(pq.toArray());
    // test that min key should be 1 after initialization with a
    assert pq.min() == min(a);
    System.out.print(pq.min()+" ");
    // test that min key is 1 after each of 10 delMax ops
    for (int i = 0; i < 10; i++) {
      pq.delMax();
      assert pq.min() == 1;
      assert pq.min() == min(pq.toArray());
      System.out.print(pq.min()+" ");
    }
    System.out.println("\n");
    pq.show();
    System.out.println();
    // test that min key is adjusted correctly after inserting successively lower keys
    for (int i = 1; i < 11; i++) {
      pq.insert(-i);
      assert pq.min() == -i;
      assert pq.min() == min(pq.toArray());
      System.out.print(pq.min()+" ");
    }
    System.out.println("\n");
    pq.show();
    // test that minKey is reset to null when pq is empty.
    int size = pq.size();
    for (int i = 0; i < size; i++) pq.delMax();
    assert pq.isEmpty() == true;
    assert pq.getMinKey() == null;
    // test that min() == max() is 1 when pq.size() is 1.
    pq.insert(25);
    assert pq.min() == pq.max();
    // test that after adding a larger key pq.min() remains the same
    int min = pq.min();
    pq.insert(26);
    assert pq.min() == min;
    // test that after a key smaller than min is inserted pq.min() takes the value of that key
    pq.insert(24);
    assert pq.min() == 24;
    
  }

}
