package ex24;

/* p329
  2.4.7  The largest item in a heap must appear in position 1, and the second 
  largest must be in position 2 or position 3. Give the list of positions in a
  heap of size 31 where the kth largest (i) can appear, and (ii) cannot appear, 
  for k=2, 3, 4 (assuming the values to be distinct).
  
  For this exercise we use a max heap so the heap property is: the key stored in 
  each node is greater than or equal to the keys in its children (according to
  some total order). (https://en.wikipedia.org/wiki/Binary_heap#Heap_implementation).
  
  Therefore 
  1. the 2nd largest can be in position 2 or 3 and not in any other position.
  2. the 3rd largest can be in position 2 through 7 and not in any other position.
  3. the 4th largest can be in position 2 through 15 and not in any other position.
  4. generally for k > 1 the kth largest can be in position 2 through (2^k)-1.
  
 */

public class Ex2407HeapPositionsOfKthLargest {

  public static void main(String[] args) {

  }

}
