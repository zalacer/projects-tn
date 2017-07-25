package ex24;

import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

import pq.LinkedMaxPQ;

/* p331
  2.4.24 Priority queue with explicit links. Implement a priority queue using
  a heap-ordered binary tree, but use a triply linked structure instead of an 
  array. You will need three links per node: two to traverse down the tree and 
  one to traverse up the tree. Your implementation should guarantee logarithmic 
  running time per operation, even if no maximum priority-queue size is known 
  ahead of time.
  
  Pavel on StackExchange solved this at https://stackoverflow.com/questions/31257243/how-do-i-implement-a-priority-queue-with-explicit-links-using-a-triply-linked-d
  His solution is available locally at pq.LinkedMaxPQ. which has been modified only to
  include an explicit constructor and isMaxHeap(). A demo is below.

*/


public class Ex2424PQwithExplicitLinks {
 
  public static void main(String[] args) {
           
    Random r = new Random(System.currentTimeMillis());
    Integer[] a =  rangeInteger(1,26);
    shuffle(a,r);
    LinkedMaxPQ<Integer> pq = new LinkedMaxPQ<>();
    for (Integer i : a) pq.insert(i);
    System.out.println(pq.isMaxHeap());
    for (int i = 0; i < a.length; i++)
      System.out.print(pq.delMax()+" ");
    System.out.println();
    System.out.println(pq.isEmpty());
  }

}
