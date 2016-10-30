package ex24;

/* p330
  2.4.15  Design a linear-time certification algorithm to check whether an array pq[] is
  a min-oriented heap.
  
  This just involves checking the heap condition is satisfied for all elements in the
  array. For a min-oriented heap that condition is as already stated in answer to 
  exercise 2.4.8: The key stored in each node is smaller than or equal to the keys in its 
  children according to some total order (https://en.wikipedia.org/wiki/Binary_heap). For 
  both min and max oriented heaps using 1-based arrays in level order the children of the 
  item at index k are at indices 2k and 2k+1.
  
  From http://algs4.cs.princeton.edu/24pq/MinPQ.java.html and locally in pq.MinPQ:
  
    // is pq[1..N] a min heap?
    private boolean isMinHeap() {
        return isMinHeap(1);
    }
    
    // is subtree of pq[1..n] rooted at k a min heap?
    private boolean isMinHeap(int k) {
        if (k > n) return true;
        int left = 2*k;
        int right = 2*k + 1;
        if (left  <= n && greater(k, left))  return false;
        if (right <= n && greater(k, right)) return false;
        return isMinHeap(left) && isMinHeap(right);
    }  

 */

public class Ex2415MinPQarrayCertificationMethod {

  public static void main(String[] args) {

  }

}
