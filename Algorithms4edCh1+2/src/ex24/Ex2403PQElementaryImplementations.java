package ex24;

import pq.MaxPQOrderedArray;
import pq.MaxPQOrderedLinkedList;
import pq.MaxPQUnorderedArray;
import pq.MaxPQUnorderedLinkedList;

/* p329
  2.4.3  Provide priority-queue implementations that support insert and remove
  the maximum, one for each of the following underlying data structures: unordered 
  array, ordered array, unordered linked list, and linked list. Give a table of 
  the worst-case bounds for each operation for each of your four implementations.
  
  Table of Worst Case Run Time Bounds of Elementary PQ Implementations
  --------------------------------------------------------------------
                                          remove       
  Class                        insert     maximum       cost model
  ------------------------     ------     -------     --------------  
  MaxPQUnorderedArray            N†         2N‡       array accesses      
  MaxPQOrderedArray             2N‡          N†       array accesses 
  MaxPQUnorderedLinkedList       1           N        node accesses
  MaxPQOrderedLinkedList         N           1        node accesses
  
  † If insert or remove maximum causes array resizing, otherwise 1.
  ‡ If insert or remove maximum causes array resizing, otherwise N.
  
 */

public class Ex2403PQElementaryImplementations {

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
