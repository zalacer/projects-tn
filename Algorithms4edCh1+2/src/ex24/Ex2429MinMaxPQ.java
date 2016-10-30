package ex24;

import static v.ArrayUtils.par;
import static v.ArrayUtils.rangeInteger;

import pq.IntervalHeap;

/* p332
  2.4.29 Min/max priority queue. Design a data type that supports the following 
  operations: insert, delete the maximum, and delete the minimum (all in 
  logarithmic time); and find the maximum and find the minimum (both in constant 
  time). Hint: Use two heaps.
  
  I implemented an interval heap based on Sartaj Sahni's exposition in Data
  Structures, Algorithms, and Applications in C++  (1989) chapter 9 section 7.
  This is available at http://www.mhhe.com/engcs/compsci/sahni/enrich/c9/interval.pdf
  and locally at IntervalHeaps.pdf. It's otherwise known as a form of double-ended
  priority queue (DEPQ) and more information about them is at 
    http://www.cise.ufl.edu/~sahni/dsaaj/enrich/c13/double.htm. 
  A similiar implementation by Michal Goly is at
    https://github.com/MichalGoly/CS21120-DoubleEndedPriorityQueue/blob/master/src/cs21120/depq/Mwg2DEPQ.java#L3
  and locally at pq.DoubleEndedPQ. It contains information explaining the interval heap concept, 
  implementaton and performance characteristics.
  
  The basic idea of an interval heap is to combine a min and a max binary heap in one 
  level-ordered array by using TwoElement elements (Sahni actually calls them that) which
  I call Interval<K,K> for appropriateness and after Goly. The fields of each Interval 
  are named left and right where the left is always in the min heap and the right is 
  always in the max heap. For each interval A it's required that A.left <= A.right and
  for an Interval B to be a child of Interval A it's required that A.left <= B.left <= 
  B.right <= A.right. In other words the interval B must be contained in interval A. 
  For example, if A = (8,25) and B = (10,24) then B may be a child of A since the range 
  from 10-24 is contained in the range 8-25. In the heap[] array, element 0 is unused, 
  element 1 contains the min and the max of the respective heaps, which also means they 
  are the min and the max of both. The remaining Intervals are arranged in level order 
  simultaneously for the left and right fields taken separately to form min and max heaps 
  so that the children of the Interval at index i have indices 2i and 2i+1.
  
  Regarding my implementation at pq.IntervalHeap and demonstrated below, there are no 
  explicit sink(), swim(), heapify() or similar methods since that functionality is built 
  into insert(), delMin() and delMax() which all have logN time performance. min() and max() 
  have constant time performance. Optional array resizing is included but off by default
  except when an IntervalHeap is created with a constructor that has a boolean argument
  which, if true, enables resizing. It's normally off to meet the required performance
  constraints. However, it can be enabled with resizeOn() and resizeOneTime(). The latter
  enables resizing once and then immediately disables it. Resizing can be explicitly
  disabled with resizeOff(), and when enabled can only increase the array size by doubling
  it. I put that in since it's something I would want for usability. To help knowing when
  to resize, all runtime overflow and underflow exceptions are replaced with System.err 
  messages and returning false for insert() and null for delMin() and delMax() so users 
  don't have to start over completely because of an avoidable runtime exception. A safe way 
  to use delMin() is if (!h.isEmpty()) k = h.delMin() where h is an instantiated 
  IntervalHeap. Similarly, for insert(), if (h.isFull()) h.resizeOneTime(); h.insert(k);

*/

public class Ex2429MinMaxPQ {

  public static void main(String[] args) {
    
    Integer[] a = rangeInteger(1,33);
    IntervalHeap<Integer> h;
    h = new IntervalHeap<>(a);
    int c = 0;
    System.out.println("alternating delMin() and delMax() output:");
    while(!h.isEmpty()) {
      c++;
      if (c%2==0) System.out.print(h.delMax()+" ");
      else System.out.print(h.delMin()+" ");
    }
    System.out.println("\nisEmpty="+h.isEmpty());
    System.out.println("capacity="+h.capacity());
    for (int i : a) h.insert(i);
    System.out.println("isFull="+h.isFull());
    if (h.isFull()) { h.resizeOneTime(); h.insert(33); }
    System.out.println("capacity="+h.capacity());
    System.out.println("running delMax() and delMin() 7 times each with no output");
    for (int i = 0; i < 7; i++) {h.delMax(); h.delMin(); }
    System.out.println("max="+h.max());
    System.out.println("min="+h.min());
    System.out.println("show() output showing the intervals:");
    h.show();
    System.out.println("toArray() output:");
    par(h.toArray());
    System.out.println("toMinMaxArrays() output:");
    par(h.toMinMaxArrays());
    System.out.println("toString() output:");
    System.out.println(h);
    Integer x = h.delMin();
    if (x != null) ;
  }

}
