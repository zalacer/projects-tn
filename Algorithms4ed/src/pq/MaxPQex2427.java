package pq;

import static v.ArrayUtils.*;
import analysis.Node;
import analysis.BTreePrinter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/24pq/MaxPQ.java
 *  http://algs4.cs.princeton.edu/24pq/MaxPQ.java.html
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

//@SuppressWarnings({ "unchecked", "unused" })
@SuppressWarnings("unchecked")
public class MaxPQex2427<Key> implements Iterable<Key> {
  private Key[] pq;                    // store items at indices 1 to n
  private int n;                       // number of items on priority queue
  private Comparator<Key> comparator;  // optional Comparator
  private Class<?> keyClass = null;
  private Key minKey = null; // reference to least Key

  /**
   * Initializes an empty priority queue with the given initial capacity.
   *
   * @param  initCapacity the initial capacity of this priority queue
   */
  public MaxPQex2427(int initCapacity) {
    pq = (Key[]) new Object[initCapacity + 1];
    n = 0;
  }

  /**
   * Initializes an empty priority queue.
   */
  public MaxPQex2427() {
    this(1);
  }

  /**
   * Initializes an empty priority queue with the given initial capacity,
   * using the given comparator.
   *
   * @param  initCapacity the initial capacity of this priority queue
   * @param  comparator the order in which to compare the keys
   */
  public MaxPQex2427(int initCapacity, Comparator<Key> comparator) {
    this.comparator = comparator;
    pq = (Key[]) new Object[initCapacity + 1];
    n = 0;
  }

  /**
   * Initializes an empty priority queue using the given comparator.
   *
   * @param  comparator the order in which to compare the keys
   */
  public MaxPQex2427(Comparator<Key> comparator) {
    this(1, comparator);
  }

  /**
   * Initializes a priority queue from the array of keys.
   * Takes time proportional to the number of keys, using sink-based heap construction.
   *
   * @param  keys the array of keys
   */
  //    public MaxPQ(Key[] keys) {
  //        n = keys.length;
  //        pq = (Key[]) new Object[keys.length + 1]; 
  //        for (int i = 0; i < n; i++)
  //            pq[i+1] = keys[i];
  //        for (int k = n/2; k >= 1; k--)
  //            sink(k);
  //        assert isMaxHeap();
  //    }

  public MaxPQex2427(Key...keys) {
    if (keys == null || keys.length == 0) {
      pq = (Key[]) new Object[1]; n = 0; return;
    }
    n = keys.length; int i = 0;
    pq = (Key[]) new Object[n + 1]; 
    while (i < n) {
      if (keys[i] != null) {
        pq[i+1] = keys[i];
        if (minKey == null) minKey = keys[i];
        else if (less(keys[i], minKey)) { minKey = keys[i]; };
        if (keyClass == null) keyClass = keys[i].getClass();
      }
      i++;
    }
    if (i<n) pq = take(pq,i+1); n = i;
    for (int k = n/2; k >= 1; k--) sink(k);
    assert isMaxHeap();
  }

  public MaxPQex2427(Comparator<Key> comp, Key...keys) {
    comparator = comp;
    if (keys == null || keys.length == 0) {
      pq = (Key[]) new Object[1]; n = 0; return;
    }
    n = keys.length; int i = 0; Class<?> kclass = null; 
    pq = (Key[]) new Object[keys.length + 1]; 
    while (i < n) {
      if (keys[i] != null) {
        pq[i+1] = keys[i];
        if (minKey == null) minKey = keys[i];
        else if (less(keys[i], minKey)) { minKey = keys[i]; };
        kclass = keys[i].getClass();
        if (keyClass == null ) kclass = keys[i].getClass();
      } else {
        if (kclass != keyClass && kclass.isAssignableFrom(keyClass)) 
          keyClass = kclass; // prefer superclass of keyClass if its used
        i++;
      }
    }
    if (i<n) pq = take(pq,i+1); n = i;
    for (int k = n/2; k >= 1; k--) sink(k);
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
  public Key max() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    return pq[1];
  }

  public Key min() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    return (Key) minKey;
  }

  public Key getMinKey() {
    // for testing
    return (Key) minKey;
  }
  
  // helper function to double the size of the heap array
  private void resize(int capacity) {
    assert capacity > n;
    Key[] temp = (Key[]) new Object[capacity];
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
  public void insert(Key x) {
    // double size of array if necessary
    if (x == null) { System.err.println("nulls not allowed"); return; }
    Class<?> kclass = x.getClass();
    if (keyClass == null) keyClass = kclass;
    else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;

    if (n >= pq.length - 1) resize(2 * pq.length);
    
    // add x
    pq[++n] = x;
    
    // update minKey if necessary
    if (minKey == null) minKey = pq[n];
    else if (less(pq[n], minKey)) minKey = pq[n];

    // percolate x up to maintain heap invariant
    swim(n);
    assert isMaxHeap();
  }

  public void bulkInsert(Key...keys) {
    if (keys == null || keys.length == 0) return;
    Class<?> kclass;
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == null) { System.err.println("nulls not allowed"); continue; }
      kclass = keys[i].getClass();
      if (keyClass == null) keyClass = kclass;
      else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
      if (n >= pq.length - 1) resize(2 * pq.length);
      // add keys[i]
      pq[++n] = keys[i];
      // update mindex if necessary
      if (minKey == null) minKey = pq[n];
      else if (less(pq[n], minKey)) minKey = pq[n];
      // percolate keys[i] up to maintain heap invariant
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
  public Key delMax() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    Key max = pq[1];
    if (n == 1) {
      n = 0;
      minKey = pq[1] = null;
      return max;
    }
    exch(1, n--);  
    sink(1);
    pq[n+1] = null;   
    if (n == (pq.length - 1) / 4) resize(pq.length / 2);
    assert isMaxHeap();
    return max;
  }


  /***************************************************************************
   * Helper functions to restore the heap invariant.
   ***************************************************************************/

  private void swim(int k) {
    //    par(toArray());
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

  /***************************************************************************
   * Helper functions for compares and swaps.
   ***************************************************************************/
  private boolean less(int i, int j) {
    if (comparator == null) {
      return ((Comparable<Key>) pq[i]).compareTo(pq[j]) < 0;
    }
    else {
      return comparator.compare(pq[i], pq[j]) < 0;
    }
  }

  private boolean less(Key a, Key b) {
    if (comparator == null) {
      return ((Comparable<Key>) a).compareTo(b) < 0;
    }
    else {
      return comparator.compare(a, b) < 0;
    }
  }

  private void exch(int i, int j) {
    Key swap = pq[i];
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


  /***************************************************************************
   * Iterator.
   ***************************************************************************/

  /**
   * Returns an iterator that iterates over the keys on this priority queue
   * in descending order.
   * The iterator doesn't implement {@code remove()} since it's optional.
   *
   * @return an iterator that iterates over the keys in descending order
   */
  public Iterator<Key> iterator() {
    return new HeapIterator();
  }

  private class HeapIterator implements Iterator<Key> {

    // create a new pq
    private MaxPQex2427<Key> copy;

    // add all items to copy of heap
    // takes linear time since already in heap order so no keys move
    public HeapIterator() {
      if (comparator == null) copy = new MaxPQex2427<Key>(size());
      else                    copy = new MaxPQex2427<Key>(size(), comparator);
      for (int i = 1; i <= n; i++)
        copy.insert(pq[i]);
    }

    public boolean hasNext()  { return !copy.isEmpty();                     }
    public void remove()      { throw new UnsupportedOperationException();  }

    public Key next() {
      if (!hasNext()) throw new NoSuchElementException();
      return copy.delMax();
    }
  }

  public Key[] toArray() {
    Key[] a;
    if (keyClass != null) a = ofDim(keyClass,n);
    else a = (Key[]) new Object[n];
    for (int i = 1; i < n+1 ; i++) a[i-1] = pq[i];
    return a;
  }

  public Object[] toEntireArray() {
    return pq;
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

  public Node<Key> toBtree() {
    Node<Key>[] nodes = ofDim(Node.class, n+1);
    //      System.out.println("n="+n);
    if (isEmpty())  return null;
    for (int i = 1; i < n+1 ; i++) nodes[i] = new Node<Key>(pq[i]);
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
    Node<Key> heap = toBtree();
    if (heap == null) System.out.println("<heap is empty>\n");
    else BTreePrinter.printNode(toBtree());
  }

  public static int[][] generateHeapPermutations(int[] z) {
    // ex2409
    MaxPQex2427<Integer> pq;
    Map<Integer,int[]> map = new HashMap<>();
    Iterator<int[]> it = permutations(z);
    while (it.hasNext()) {
      Integer[] a = (Integer[])box(it.next());
      pq = new MaxPQex2427<Integer>(a);
      int[] b = (int[])unbox(pq.toArray());
      map.put(Arrays.hashCode(b),b);
    }
    int[][] w = new int[map.size()][z.length]; int c = 0;
    for (Integer i : map.keySet()) w[c++] = map.get(i);
    return w;
  }

  public static String[] translate(int[] z) {
    // ex2409
    String[] a = new String[z.length];
    for (int i = 0; i < z.length; i++) {
      if (z[i] == 1) a[i] = "A";
      else if (z[i] == 2) a[i] = "B";
      else if (z[i] == 3) a[i] = "C";
      else if (z[i] == 4) a[i] = "D";
      else if (z[i] == 5) a[i] = "E";
    }
    return a;   
  }

  public static void printHeaps(String[] s) {
    // ex2409
    int[] r = new int[s.length];
    String[] t = unique(s);
    for (int i = 0; i < t.length; i++) {
      if (s[i].equals("A")) r[i] = 1;
      else if (s[i].equals("B")) r[i] = 2;
      else if (s[i].equals("C")) r[i] = 3;
      else if (s[i].equals("D")) r[i] = 4;
      else if (s[i].equals("E")) r[i] = 5;
    }
    int[][] hp = generateHeapPermutations(r);
    String[][] hq = new String[hp.length][];

    System.out.println(arrayToString(s,-1)+" has "+hq.length+" heaps:\n");

    for (int i = 0; i < hp.length; i++) {
      hq[i] = translate(hp[i]);
      par(hq[i]);
    }
    System.out.println();

    for (int i = 0; i < hp.length; i++) {
      MaxPQex2427<String> pq = new MaxPQex2427<String>(hq[i]);
      pq.printHeap();
    }     
  }
  
  

  /**
   * Unit tests the {@code MaxPQ} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {

    Integer[] a;
//    a = rangeInteger(1,21);
    //    Random r = new Random(System.currentTimeMillis());
    //    shuffle(a,r);
    //    par(a);
    //    a = new Integer[]{4,8,3,6,5,7,9,2,1}; //7,5,4,9,3,2,6,1,8
    a = new Integer[]{20,19,17,18,16,15,12,11,14,9,1,7,6,10,3,8,2,4,13,5}; 
    MaxPQex2427<Integer> pq = new MaxPQex2427<>(a);
    int size = pq.size();
    System.out.println(pq.size());
    System.out.print(pq.min()+" ");
    for (int i = 0; i <size-1; i++) {
      System.out.print(pq.min()+" ");
//       System.out.println(pq.delMax()+", "+pq.min());
//       par(pq.toEntireArray()); System.out.println();
    }
//    System.out.println(pq.min());
//    System.out.println(pq.delMax());
//    par(pq.toEntireArray()); System.out.println();
//    System.out.println(pq.min());
//    System.out.println(pq.delMax());
//    par(pq.toEntireArray()); System.out.println();
//    
//    System.out.println();

    //    par(pq.toEntireArray());
    //    System.out.println(pq.min()+", "+pq.mindex); // 1
    //    
    //    pq.delMax();
    //    par(pq.toEntireArray());
    //    pq.delMax();
    //    par(pq.toEntireArray());
    //    pq.delMax();
    //    par(pq.toEntireArray());
    //    System.out.println(pq.min());
    //    pq.insert(0);
    //    System.out.println(pq.min());


    /*
[12,16,8,14,17,5,13,10,1,19,20,9,4,7,11,3,18,15,6,2]
[null,20,19,13,18,17,9,11,14,15,16,12,5,4,7,8,3,10,1,6,2]
1
[null,19,18,13,15,17,9,11,14,6,16,12,5,4,7,8,3,10,1,2,null]
[null,18,17,13,15,16,9,11,14,6,2,12,5,4,7,8,3,10,1,null,null]
mindex find count=7
[null,17,16,13,15,12,9,11,14,6,2,1,5,4,7,8,3,10,null,null,null]
1
1

     */

  }

}

