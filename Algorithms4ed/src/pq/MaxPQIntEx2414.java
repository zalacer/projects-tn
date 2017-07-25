package pq;

import static v.ArrayUtils.*;
import analysis.Node;
import analysis.BTreePrinter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Arrays;
import v.Tuple2;

/******************************************************************************
 *  Compilation:  javac MaxPQ.java
 *  Execution:    java MaxPQ < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tinyPQ.txt
 *  
 *  Generic max priority queue implementation with a binary heap.
 *  Can be used with a comparator instead of the natural order,
 *  but the generic Key type must still be Comparable.
 *
 *  % java MaxPQ < tinyPQ.txt 
 *  Q X P (6 left on pq)
 *
 *  We use a one-based array to simplify parent and child calculations.
 *
 *  Can be optimized by replacing full exchanges with half exchanges 
 *  (ala insertion sort).
 *
 ******************************************************************************/
/**
 *  http://algs4.cs.princeton.edu/24pq/MaxPQ.java
 *  http://algs4.cs.princeton.edu/24pq/MaxPQ.java.html
 *  The {@code MaxPQ} class represents a priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-maximum</em>
 *  operations, along with methods for peeking at the maximum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  This implementation uses a binary heap.
 *  The <em>insert</em> and <em>delete-the-maximum</em> operations take
 *  logarithmic amortized time.
 *  The <em>max</em>, <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction takes time proportional to the specified capacity or the number of
 *  items used to initialize the data structure.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param <Key> the generic type of key on this priority queue
 */

public class MaxPQIntEx2414 implements Iterable<Integer> {
  private int[] pq;                    // store items at indices 1 to n
  private int n;                       // number of items on priority queue
  public int exchanges = 0;

  /**
   * Initializes an empty priority queue with the given initial capacity.
   *
   * @param  initCapacity the initial capacity of this priority queue
   */
  public MaxPQIntEx2414(int initCapacity) {
    pq = new int[initCapacity + 1];
    n = 0;
  }

  /**
   * Initializes an empty priority queue.
   */
  public MaxPQIntEx2414() {
    this(1);
  }

  /**
   * Initializes a priority queue from the array of keys.
   * Takes time proportional to the number of keys, using sink-based heap construction.
   *
   * @param  keys the array of keys
   */
      public MaxPQIntEx2414(int[] keys) {
          n = keys.length;
          pq = new int[keys.length + 1]; 
          for (int i = 0; i < n; i++)
              pq[i+1] = keys[i];
          for (int k = n/2; k >= 1; k--)
              sink(k);
          assert isMaxHeap();
      }

  /**
   * Returns true if this priority queue is empty.
   *
   * @return {@code true} if this priority queue is empty;
   *         {@code false} otherwise
   */
  public boolean isEmpty() {
    return n == 0;
  }

  /**
   * Returns the number of keys on this priority queue.
   *
   * @return the number of keys on this priority queue
   */
  public int size() {
    return n;
  }

  /**
   * Returns a largest key on this priority queue.
   *
   * @return a largest key on this priority queue
   * @throws NoSuchElementException if this priority queue is empty
   */
  public int max() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    return pq[1];
  }

  // helper function to double the size of the heap array
  private void resize(int capacity) {
    assert capacity > n;
    int[] temp = new int[capacity];
    for (int i = 1; i <= n; i++) {
      temp[i] = pq[i];
    }
    pq = temp;
  }


  /**
   * Adds a new key to this priority queue.
   *
   * @param  x the new key to add to this priority queue
   */
  public void insert(int x) {
    // double size of array if necessary
    if (n >= pq.length - 1) resize(2 * pq.length);
    // add x, and percolate it up to maintain heap invariant
    pq[++n] = x;
    swim(n);
    assert isMaxHeap();
  }

  public void bulkInsert(int...keys) {
    if (keys == null || keys.length == 0) return;
    for (int i = 0; i < keys.length; i++) {
      if (n >= pq.length - 1) resize(2 * pq.length);
      // add x, and percolate it up to maintain heap invariant
      pq[++n] = keys[i];
      swim(n);
    };
    assert isMaxHeap();
  }

  /**
   * Removes and returns a largest key on this priority queue.
   *
   * @return a largest key on this priority queue
   * @throws NoSuchElementException if this priority queue is empty
   */
  public int delMax() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    int max = pq[1];
    exch(1, n--);
    sink(1);
    if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length / 2);
    assert isMaxHeap();
    return max;
  }


  /***************************************************************************
   * Helper functions to restore the heap invariant.
   ***************************************************************************/

  private void swim(int k) {
    while (k > 1 && less(k/2, k)) {
      exch(k, k/2);
      k = k/2;
    }
  }

  private void sink(int k) {
    while (2*k <= n) {
      int j = 2*k;
      if (j < n && less(j, j+1)) j++;
      if (!less(k, j)) break;
      exch(k, j);
      k = j;
    }
  }
  
//  private void sink(int k) {
//    while (2*k <= n) {
//      int j = 2*k;
//      if (j < n && less(j, j+1)) j++;
//      if (!less(k, j)) break;
//      exch(k, j);
//      k = j;
//    }
//  }

  /***************************************************************************
   * Helper functions for compares and swaps.
   ***************************************************************************/
  private boolean less(int i, int j) {
      return pq[i] < pq[j];
  }

  private void exch(int i, int j) {
//    System.out.println("exch("+pq[i]+","+pq[j]+")");
    exchanges++;
    int swap = pq[i];
    pq[i] = pq[j];
    pq[j] = swap;
  }

  // is pq[1..N] a max heap?
  private boolean isMaxHeap() {
    return isMaxHeap(1);
  }

  // is subtree of pq[1..n] rooted at k a max heap?
  private boolean isMaxHeap(int k) {
    if (k > n) return true;
    int left = 2*k;
    int right = 2*k + 1;
    if (left  <= n && less(k, left))  return false;
    if (right <= n && less(k, right)) return false;
    return isMaxHeap(left) && isMaxHeap(right);
  }

  public Iterator<Integer> iterator() {
   return toIterator(toArray());
  }
 

  public int[] toArray() {
    int[] a = new int[n];
    for (int i = 1; i < n+1 ; i++) a[i-1] = pq[i];
    return a;
  }

  public int[] toEntireArray() {
    int[] a = new int[pq.length];
    for (int i = 0; i < pq.length ; i++) a[i] = pq[i];
    return a;
  }

  public void show() {
    if (n == 0) System.out.print("<nothing in pq>");
    for (int i = 1; i < n+1 ; i++) System.out.print(pq[i]+" ");
    System.out.println();
  }

  public void show(String x) {
    if (n == 0) System.out.print("<nothing in "+x+">");
    for (int i = 1; i < n+1 ; i++) System.out.print(pq[i]+" ");
    System.out.println();
  }

  public Node<Integer> toBtree() {
    Node<Integer>[] nodes = ofDim(Node.class, n+1);
    //      System.out.println("n="+n);
    if (isEmpty())  return null;
    for (int i = 1; i < n+1 ; i++) {
      nodes[i] = new Node<Integer>((Integer) new java.lang.Integer(pq[i]));
    }
    int c = 1;
    while(c<n/2) {
      nodes[c].left = nodes[2*c]; nodes[c].right = nodes[1+2*c++];
    }
    if (2*c<=n) nodes[c].left = nodes[2*c];
    if (2*c+1<=n) nodes[c].right = nodes[2*c+1];

    //      for (int i = 1; i < n+1; i++)
    //        System.out.println(nodes[i]+", "+nodes[i].left+", "+nodes[i].right);
    //      par(nodes);

    return nodes[1];
  }

  public void printHeap() {
    // ex2409
    Node<Integer> heap = toBtree();
    if (heap == null) System.out.println("<heap is empty>\n");
    else BTreePrinter.printNode(toBtree());
  }

  public static int[][] generateHeapPermutations(int[] z) {
    // ex2409
    MaxPQIntEx2414 px;
    Map<Integer,int[]> map = new HashMap<>();
    Iterator<int[]> it = permutations(z);
    while (it.hasNext()) {
      int[] a = it.next();
      px = new MaxPQIntEx2414(a);
      int[] b = px.toArray();
      map.put(Arrays.hashCode(b),b);
    }
    int[][] w = new int[map.size()][z.length]; int c = 0;
    for (Integer i : map.keySet()) w[c++] = map.get(i);
    return w;
  }
  
  
  public static Tuple2<Integer,List<int[]>> findHeapsWithMinOrMaxExchsForXdelMax(
      String maxOrMin, int len, int x, long...reportingInterval) {
    // find all heaps for input arrays of length len with distinct elements which 
    // have the max or min total number of exchanges for x delMax ops, report the 
    // results every reportingInterval or 30,000,000 iterations by default if 
    // reportingInterval isn't set and eventually return the arrays and the number 
    // of exchanges in a Tuple2.
    if (!(maxOrMin.toLowerCase().equals("max") || maxOrMin.toLowerCase().equals("min")))
      throw new IllegalArgumentException("maxOrMin must be max or min in any case");
    if (len<=0) throw new IllegalArgumentException("len must be >0");
    if (x<1) throw new IllegalArgumentException("x must be >=1");
    MaxPQIntEx2414 pq; int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE, e; 
    int[] a, b; double d = 0; long ri;
    List<int[]> z = new ArrayList<>();
    Iterator<int[]> it = permutations(range(1,len+1));
    if (reportingInterval == null || reportingInterval.length == 0
        || reportingInterval[0] < 0)  ri = 30000000;
    else ri = reportingInterval[0];
    if (maxOrMin.toLowerCase().equals("min")) {
      while (it.hasNext()) {
        a = it.next(); d++;
        pq = new MaxPQIntEx2414(a);
        b = pq.toArray();
        for (int i = 0; i<x; i++) pq.delMax();
        e = pq.exchanges;
        if (e == min)  z.add(b);
        else if (e < min) { min = e; z = new ArrayList<>(); z.add(b); }
        if (d%ri==0)  {  
          System.out.printf("count=%5.0f\n",d);
          System.out.println("min exchs="+min);
          System.out.println("heaps with min exchs:");
          for (int[] ia : z) par(ia);
          System.out.println();
        }
      }
    } else {
      while (it.hasNext()) {
        a = it.next();  d++;
        pq = new MaxPQIntEx2414(a);
        b = pq.toArray();
        for (int i = 0; i<x; i++) pq.delMax();
        e = pq.exchanges;
        if (e == max)  z.add(b);
        else if (e > max) { max = e; z = new ArrayList<>(); z.add(b); }
        if (d%ri==0)  {  
          System.out.printf("count=%5.0f\n",d);
          System.out.println("max exchs="+max);
          System.out.println("heaps with max exchs:");
          for (int[] ia : z) par(ia);
          System.out.println();
        }
      } 
    } 
    return new Tuple2<Integer,List<int[]>>(min,z);
  }

  public static void main(String[] args) {
    
    Tuple2<Integer,List<int[]>> f = findHeapsWithMinOrMaxExchsForXdelMax("min", 15, 3); 
    System.out.println("min exchs ="+f._1);
    System.out.println("heaps with min exchs:");
    List<int[]> g = f._2;
    for (int[] ia : g) par(ia);
    
    
//    MaxPQex2414<Integer> pq;
//    pq = new MaxPQex2414<Integer>(15,14,13,10,9,8,12,1,2,3,4,5,6,7,11);
//    par(pq.toArray());
//    pq.printHeap();

/*

    findHeapsWithMinOrMaxExchsForXdelMax("min", 15, 1); 
    count:       1050000000.0
    min exchs=2
    heaps with min exchs:
    [15,14,13,10,9,8,12,1,2,3,4,5,6,7,11]
                __15__  
               |      |  
        ______14      13______     
       |      |        |      | 
     _10_    _9_      _8_    _12_   
    |    |  |   |    |   |  |    |
    1    2  3   4    5   6  7   11 
    
    [15,14,13,9,10,8,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,8,10,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,8,12,11,1,2,3,4,5,6,7,10]
    [15,14,13,9,8,11,12,1,2,3,4,5,6,7,10]
    [15,14,13,8,9,11,12,1,2,3,4,5,6,7,10]
    [15,14,13,8,9,12,11,1,2,3,4,5,6,7,10]
    [15,14,13,8,9,10,12,1,2,3,4,5,6,7,11]
    [15,14,13,8,10,9,12,1,2,3,4,5,6,7,11]
    [15,14,13,10,8,9,12,1,2,3,4,5,6,7,11]
    [15,14,13,10,8,9,12,1,2,3,4,5,7,6,11]
    [15,14,13,8,10,9,12,1,2,3,4,5,7,6,11]
    [15,14,13,8,9,10,12,1,2,3,4,5,7,6,11]
    [15,14,13,8,9,12,11,1,2,3,4,5,7,6,10]
    [15,14,13,8,9,11,12,1,2,3,4,5,7,6,10]
    [15,14,13,9,8,11,12,1,2,3,4,5,7,6,10]
    [15,14,13,9,8,12,11,1,2,3,4,5,7,6,10]
    [15,14,13,9,8,10,12,1,2,3,4,5,7,6,11]
    [15,14,13,9,10,8,12,1,2,3,4,5,7,6,11]
    [15,14,13,10,9,8,12,1,2,3,4,5,7,6,11]
    [15,14,13,10,9,8,12,1,2,3,4,7,5,6,11]
    [15,14,13,9,10,8,12,1,2,3,4,7,5,6,11]
    [15,14,13,9,8,10,12,1,2,3,4,7,5,6,11]
    [15,14,13,9,8,12,11,1,2,3,4,7,5,6,10]
    [15,14,13,9,8,11,12,1,2,3,4,7,5,6,10]
    [15,14,13,8,9,11,12,1,2,3,4,7,5,6,10]
    [15,14,13,8,9,12,11,1,2,3,4,7,5,6,10]
    [15,14,13,8,9,10,12,1,2,3,4,7,5,6,11]
    [15,14,13,8,10,9,12,1,2,3,4,7,5,6,11]
    [15,14,13,10,8,9,12,1,2,3,4,7,5,6,11]
    [15,14,13,10,8,9,12,1,2,3,7,4,5,6,11]
    [15,14,13,8,10,9,12,1,2,3,7,4,5,6,11]
    [15,14,13,8,9,10,12,1,2,3,7,4,5,6,11]
    [15,14,13,8,9,12,11,1,2,3,7,4,5,6,10]
    [15,14,13,8,9,11,12,1,2,3,7,4,5,6,10]
    [15,14,13,9,8,11,12,1,2,3,7,4,5,6,10]
    [15,14,13,9,8,12,11,1,2,3,7,4,5,6,10]
    [15,14,13,9,8,10,12,1,2,3,7,4,5,6,11]
    [15,14,13,9,10,8,12,1,2,3,7,4,5,6,11]
    [15,14,13,10,9,8,12,1,2,3,7,4,5,6,11]
    
    findHeapsWithMinOrMaxExchsForXdelMax("min", 15, 2);
    count:       1230000000.0
    min exchs=5
    heaps with min exchs:
    [15,14,13,10,9,8,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,10,8,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,8,10,12,1,2,3,4,5,6,7,11]
    [15,13,14,9,8,12,11,1,2,3,4,5,6,10,7]
    [15,14,12,13,9,8,11,1,2,3,4,5,6,10,7]
    [15,14,12,9,13,8,11,1,2,3,4,5,6,10,7]
    [15,14,13,9,8,12,11,1,2,3,4,5,6,7,10]
    [15,14,13,9,8,11,12,1,2,3,4,5,6,7,10]
    [15,14,13,8,9,11,12,1,2,3,4,5,6,7,10]
    [15,14,13,8,9,12,11,1,2,3,4,5,6,7,10]
    [15,14,12,8,13,9,11,1,2,3,4,5,6,10,7]
    [15,14,12,13,8,9,11,1,2,3,4,5,6,10,7]
    [15,13,14,8,9,12,11,1,2,3,4,5,6,10,7]
    [15,14,13,8,9,10,12,1,2,3,4,5,6,7,11]
    [15,14,13,8,10,9,12,1,2,3,4,5,6,7,11]
    [15,14,13,10,8,9,12,1,2,3,4,5,6,7,11]
    [15,14,12,13,8,10,11,1,2,3,4,5,6,9,7]
    [15,14,12,8,13,10,11,1,2,3,4,5,6,9,7]
    [15,14,12,8,13,11,10,1,2,3,4,5,6,9,7]
    [15,14,12,13,8,11,10,1,2,3,4,5,6,9,7]
    [15,14,12,13,8,11,10,1,2,3,4,5,7,9,6]
    [15,14,12,8,13,11,10,1,2,3,4,5,7,9,6]
    [15,14,12,8,13,10,11,1,2,3,4,5,7,9,6]
    [15,14,12,13,8,10,11,1,2,3,4,5,7,9,6]
    [15,14,13,10,8,9,12,1,2,3,4,5,7,6,11]
    [15,14,13,8,10,9,12,1,2,3,4,5,7,6,11]
    [15,14,13,8,9,10,12,1,2,3,4,5,7,6,11]
    [15,14,12,13,8,9,11,1,2,3,4,5,7,10,6]
    [15,14,12,8,13,9,11,1,2,3,4,5,7,10,6]
    [15,14,13,8,9,11,12,1,2,3,4,5,7,6,10]
    [15,14,13,9,8,11,12,1,2,3,4,5,7,6,10]
    [15,14,12,9,13,8,11,1,2,3,4,5,7,10,6]
    [15,14,12,13,9,8,11,1,2,3,4,5,7,10,6]
    [15,14,13,9,8,10,12,1,2,3,4,5,7,6,11]
    [15,14,13,9,10,8,12,1,2,3,4,5,7,6,11]
    [15,14,13,10,9,8,12,1,2,3,4,5,7,6,11]
    [15,14,13,10,9,8,12,1,2,3,4,7,5,6,11]
    [15,14,13,9,10,8,12,1,2,3,4,7,5,6,11]
    [15,14,13,9,8,10,12,1,2,3,4,7,5,6,11]
    [15,14,12,13,9,8,11,1,2,3,4,7,5,10,6]
    [15,14,12,9,13,8,11,1,2,3,4,7,5,10,6]
    [15,14,13,9,8,11,12,1,2,3,4,7,5,6,10]
    [15,14,13,8,9,11,12,1,2,3,4,7,5,6,10]
    [15,14,12,8,13,9,11,1,2,3,4,7,5,10,6]
    [15,14,12,13,8,9,11,1,2,3,4,7,5,10,6]
    [15,14,13,8,9,10,12,1,2,3,4,7,5,6,11]
    [15,14,13,8,10,9,12,1,2,3,4,7,5,6,11]
    [15,14,13,10,8,9,12,1,2,3,4,7,5,6,11]
    [15,14,12,13,8,10,11,1,2,3,4,7,5,9,6]
    [15,14,12,8,13,10,11,1,2,3,4,7,5,9,6]
    [15,14,12,8,13,11,10,1,2,3,4,7,5,9,6]
    [15,14,12,13,8,11,10,1,2,3,4,7,5,9,6]
    [15,14,12,13,8,11,10,1,2,3,7,4,5,9,6]
    [15,14,12,13,8,10,11,1,2,3,7,4,5,9,6]
    [15,14,13,10,8,9,12,1,2,3,7,4,5,6,11]
    [15,14,13,8,10,9,12,1,2,3,7,4,5,6,11]
    [15,14,13,8,9,10,12,1,2,3,7,4,5,6,11]
    [15,13,14,8,9,12,11,1,2,3,7,4,5,10,6]
    [15,14,12,13,8,9,11,1,2,3,7,4,5,10,6]
    [15,14,13,8,9,12,11,1,2,3,7,4,5,6,10]
    [15,14,13,8,9,11,12,1,2,3,7,4,5,6,10]
    [15,14,13,9,8,11,12,1,2,3,7,4,5,6,10]
    [15,14,13,9,8,12,11,1,2,3,7,4,5,6,10]
    [15,14,12,13,9,8,11,1,2,3,7,4,5,10,6]
    [15,13,14,9,8,12,11,1,2,3,7,4,5,10,6]
    [15,14,13,9,8,10,12,1,2,3,7,4,5,6,11]
    [15,14,13,9,10,8,12,1,2,3,7,4,5,6,11]
    [15,14,13,10,9,8,12,1,2,3,7,4,5,6,11]

    findHeapsWithMinOrMaxExchsForXdelMax("min", 15, 3); 
    count=2040000000
    min exchs=8
    heaps with min exchs:
    [15,14,11,12,13,9,10,1,2,3,4,5,8,6,7]
    [15,14,11,13,12,9,10,1,2,3,4,5,8,6,7]
    [15,14,11,13,12,10,9,1,2,3,4,5,8,6,7]
    [15,14,11,12,13,10,9,1,2,3,4,5,8,6,7]
    [15,14,13,10,9,8,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,10,8,12,1,2,3,4,5,6,7,11]
    [15,14,13,9,8,10,12,1,2,3,4,5,6,7,11]
    [15,13,14,9,8,12,11,1,2,3,4,5,6,10,7]
    ...
  
*/
//     pq = new MaxPQex2414<Integer>(4,5,6,7,8,9,84,85,86,87,88,89,90,99,100);
//    pq.printHeap();
    
//    pq = new MaxPQex2414<Integer>(100,99,90, 9,8, 89,88, 7,6,5,4, 87,86,85,84);
//    pq.printHeap();
    
//    pq = new MaxPQex2414<Integer>(15,14,13,6,5,12,11,4,3,2,1,10,9,8,7);
//    pq = new MaxPQex2414<Integer>(15,14,7,13,12,6,5,11,10,9,8,4,3,2,1);
//    pq.printHeap();
//    pq.delMax();
//    pq.printHeap();
//    pq.delMax();
//    pq.printHeap();
//    pq.delMax();
//    pq.printHeap();
/*
       15               
      / \       
     /   \      
    /     \     
   /       \    
   14       7       
  / \      / \   
 /   \    /   \  
13   12   6   5   
/ \  / \ / \  / \ 
11 10 9 8 4 3  2 1 
                            
*/   
    
//  printHeaps(new String[]{"A","B","C","D","E"});
//  printHeaps(new String[]{"A","A","A","B","B"});
  

  }

}

