package ex24;

import static v.ArrayUtils.*;

import pq.MaxPQ;


/* p335
  2.4.38 Exercise driver. Write an exercise driver client program that uses 
  the methods in our priority-queue interface of Algorithm 2.6 on difficult 
  or pathological cases that might turn up in practical applications. Simple 
  examples include keys that are already in order, keys in reverse order, all 
  keys the same, and sequences of keys having only two distinct values.

 */

public class Ex2438MaxPQPathologicalCasesExerciseDriver {
 
  public static void pathological()  {
    // test MaxPQ pathological cases
    MaxPQ<Integer> pq;
    
    // test sorted input
    pq = new MaxPQ<>(rangeInteger(0,1001));
    int[] a = new int[1001]; int c = 1000;
    while (!pq.isEmpty()) a[c--] = pq.delMax();
//    par(a);
    assert isSorted(a);
    System.out.println("sorted input test passed");
    
    // test reverse sorted input
    pq = new MaxPQ<>(rangeInteger(0,1001));
    a = new int[1001]; c = 1000;
    while (!pq.isEmpty()) a[c--] = pq.delMax();
    assert isSorted(a);
    System.out.println("reverse sorted input test passed");
    
    // test all keys the same
    pq = new MaxPQ<>(fill(1001, ()->1));
    a = new int[1001]; c = 0;
    while (!pq.isEmpty()) a[c++] = pq.delMax();
    assert isSorted(a);
    System.out.println("all keys the same input test passed");
    
    // test keys having only two distinct values
    Integer[] b = new Integer[1000]; c = 0;
    for (int i = 0; i<500; i++) { b[c++] = 1; b[c++] = 2; }
    pq = new MaxPQ<>(b); c = 999;
    while (!pq.isEmpty()) b[c--] = pq.delMax();
    assert isSorted(b);
    System.out.println("keys having only two distinct values input test passed");
  }

  public static void main(String[] args) {
    
    pathological();

  }

}
