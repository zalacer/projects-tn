package ex24;


/* p329
  2.4.10  Suppose that we wish to avoid wasting one position in a heap-ordered
  array pq[], putting the largest value in pq[0], its children in  pq[1] and  
  pq[2] , and so forth, proceeding in level order. Where are the parents and 
  children of pq[k]?
  
  The parent of p[k] is at floor((k-1)/2); its children are at 2k+1 and 2k+2.
  Ref.: https://en.wikipedia.org/wiki/Binary_heap
 */

public class Ex2410UsingIndex0InHeapOrderedArray {

  public static void main(String[] args) {
    
  }

}
