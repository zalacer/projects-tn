package ex24;

import static v.ArrayUtils.append;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import pq.MinPQ2ex2431;

/* p332
  2.4.31 Fast insert. Develop a compare-based implementation of the MinPQ 
  API such that insert uses ~ log log N compares and delete the minimum 
  uses ~2 log N compares. Hint : Use binary search on parent pointers to 
  find the ancestor in swim().
  
  from https://github.com/dvklopfenstein/PrincetonAlgorithms/blob/master/py/AlgsSedgewickWayne/MaxPQ.py
  # INSERT: IMPROVE TO lg (lg N)
  #
  # TRUE: It is possible to use binary search to improve our
  # binary heap implementation so that insert() takes ~ lg (lg N)
  # compares per operation (in the worst case), where N is
  # the number of keys in the data structure.
  #
  # EXPLANATION: Note that the keys on the path from a leaf to
  # the root are in nondecreasing order. Thus, we can binary
  # search to find how far up in the tree the inserted key will
  # end up. This takes only ~ lg lg N compares, though it still
  # takes ~ lg N exchanges (in the worst case).
  
  This exercise makes some sense for fast insert and I did that by tripling 
  the complexity of swim not counting the search method and with no certain 
  significant payoff especially in cases where there is a high level of key 
  duplication. Obviously if this approach is significantly better it would be 
  taught as the standard and widely implemented. 
  
  This exercise is nonsense regarding delete the minimum which already seems 
  very efficient and has nothing to do with insert(). I left it untouched.
  
  For possibly faster insert() the swim() implementation in MinPQ2ex2431 is 
  implemented using fibonacci search instead of binary search because it was 
  convenient and is more efficient. The search method is 
  v.ArrayUtils.highestIndexOfEqualOrLess(). A brief demo is below.

 */

public class Ex2431MinPQFastInsertAndDelMin {

  public static void main(String[] args) {

    // no output should be produced if all goes well.
    
    Random r = new Random(System.currentTimeMillis()); 
    MinPQ2ex2431<Integer> pq;
    Integer[] a = append(rangeInteger(1,1000), rangeInteger(1001,2001));
    // the array constructor uses insert() to insert each element from the
    // array into the PQ and runs isMinHeap() before it exits.
    shuffle(a,r);
    pq = new MinPQ2ex2431<>(a);
    pq.insert(1000); // the missing element
    assert pq.delMin() == 1;
    pq.insert(0); 
    assert pq.delMin() == 0;
   
  }

}
