package ex24;

import pq.MaxPQOrderedArray;
import pq.MaxPQOrderedLinkedList;
import pq.MaxPQUnorderedArray;
import pq.MaxPQUnorderedLinkedList;

/* p329
  2.4.4 Is an array that is sorted in decreasing order a max-oriented heap?
  
  No, it's just a reverse sorted array. For an array to be used as a heap, 
  it has to be put and accessed in level order as described on p313.
  
 */

public class Ex2404IsReverseSortedArrayAHeap {

  public static void main(String[] args) {

    MaxPQUnorderedArray<Integer> pq1 = new MaxPQUnorderedArray<Integer>(3,4,5,2,1);
    pq1.show();
    while(!pq1.isEmpty()) System.out.print(pq1.delMax()+" ");
    System.out.println();
    pq1.show("pq1"); System.out.println();

    MaxPQOrderedArray<Integer> pq2 = new MaxPQOrderedArray<Integer>(3,4,6,2,1);
    pq2.show();
    pq2.insert(5);
    pq2.show();
    pq2.insert(-1);
    pq2.show();
    pq2.insert(3);
    pq2.show();
    pq2.insert(7);
    pq2.show();
    pq2.insert(-2);
    pq2.show();
    pq2.insert(-3);
    pq2.show();
    while(!pq2.isEmpty()) System.out.print(pq2.delMax()+" ");
    System.out.println();
    pq2.show("pq2"); System.out.println();
    
    MaxPQUnorderedLinkedList<Integer> pq3 = new MaxPQUnorderedLinkedList<Integer>(2,5,1,3,4); 
    pq3.show();
    System.out.println(pq3.max());
    while(!pq3.isEmpty()) System.out.print(pq3.delMax()+" ");
    System.out.println();
    pq3.show("pq3"); System.out.println();

    MaxPQOrderedLinkedList<Integer> pq4 = new MaxPQOrderedLinkedList<Integer>(4,1,3,5,2); 
    pq4.show();
    System.out.println(pq4.max());
    while(!pq4.isEmpty()) System.out.println(pq4.delMax()+" ");
    pq4.show("pq4");
  }

}
