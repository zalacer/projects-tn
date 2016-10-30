package pq;

import static v.ArrayUtils.*;
import analysis.Node;
import analysis.BTreePrinter;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/24pq/MinPQ.java.html
 *  Compilation:  javac MinPQ.java
 *  Execution:    java MinPQ < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tinyPQ.txt
 *  
 *  Generic min priority queue implementation with a binary heap.
 *  Can be used with a comparator instead of the natural order.
 *
 *  % java MinPQ < tinyPQ.txt
 *  E A E (6 left on pq)
 *
 *  We use a one-based array to simplify parent and child calculations.
 *
 *  Can be optimized by replacing full exchanges with half exchanges
 *  (ala insertion sort).
 *
 ******************************************************************************/

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 *  http://algs4.cs.princeton.edu/24pq/MinPQ.java
 *  http://algs4.cs.princeton.edu/24pq/MinPQ.java.html
 *  The {@code MinPQ} class represents a priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with methods for peeking at the minimum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  This implementation uses a binary heap.
 *  The <em>insert</em> and <em>delete-the-minimum</em> operations take
 *  logarithmic amortized time.
 *  The <em>min</em>, <em>size</em>, and <em>is-empty</em> operations take constant time.
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

@SuppressWarnings("unchecked")
public class MinPQ<Key> implements Iterable<Key> {
    private Key[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue
    private Comparator<Key> comparator;  // optional comparator
    private Class<?> keyClass = null;

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public MinPQ(int initCapacity) {
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public MinPQ() {
        this(1);
    }

    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order to use when comparing keys
     */
    public MinPQ(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order to use when comparing keys
     */
    public MinPQ(Comparator<Key> comparator) {
        this(1, comparator);
    }

    /**
     * Initializes a priority queue from the array of keys.
     * <p>
     * Takes time proportional to the number of keys, using sink-based heap construction.
     *
     * @param  keys the array of keys
     */
//    public MinPQ(Key[] keys) {
//        n = keys.length;
//        pq = (Key[]) new Object[keys.length + 1];
//        for (int i = 0; i < n; i++)
//            pq[i+1] = keys[i];
//        for (int k = n/2; k >= 1; k--)
//            sink(k);
//        assert isMinHeap();
//    }
    
    public MinPQ(Key...keys) {
      if (keys == null || keys.length == 0) {
        pq = (Key[]) new Object[1]; n = 0; return;
      }
      n = keys.length; int i = 0; Class<?> kclass;
      pq = (Key[]) new Object[keys.length + 1]; 
      while (i < n)
        if (keys[i] != null) {
          pq[i+1] = keys[i];
          kclass = keys[i].getClass();
          if (keyClass == null) keyClass = kclass;
          else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
          i++;
        }
      for (int k = n/2; k >= 1; k--)
        sink(k);
      assert isMinHeap();
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
     * Returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
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
     * @param  x the key to add to this priority queue
     */
    public void insert(Key x) {
      // double size of array if necessary
      if (x == null) { System.err.println("nulls not allowed"); return; }
      if (n == pq.length - 1) resize(2 * pq.length);
      Class<?> kclass = x.getClass();
      if (keyClass == null) keyClass = kclass;
      else if (kclass.isAssignableFrom(keyClass)) keyClass = kclass;
    
      // add x, and percolate it up to maintain heap invariant
      pq[++n] = x;
//      printHeap();
      swim(n);
      assert isMinHeap();
    }

    /**
     * Removes and returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Key delMin() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        exch(1, n);
        Key min = pq[n--];
        sink(1);
        pq[n+1] = null;         // avoid loitering and help with garbage collection
        if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length  / 2);
        assert isMinHeap();
        return min;
    }

    public void clear() {
      n = 0;
      pq = (Key[]) new Object[1]; n = 0;
    }

   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
//            printHeap(); 
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

   /***************************************************************************
    * Helper functions for compares and swaps.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        if (comparator == null) {
            return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
        }
        else {
            return comparator.compare(pq[i], pq[j]) > 0;
        }
    }

    private void exch(int i, int j) {
//        System.out.println("ex "+pq[i]+" "+pq[j]);
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

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


    /**
     * Returns an iterator that iterates over the keys on this priority queue
     * in ascending order.
     * <p>
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Key> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Key> {
        // create a new pq
        private MinPQ<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new MinPQ<Key>(size());
            else                    copy = new MinPQ<Key>(size(), comparator);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Key next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
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

    /**
     * Unit tests the {@code MinPQ} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
      
      MinPQ<Integer> pq2;
      pq2 = new MinPQ<Integer>(9,7,2,6,3,5,4,8);
      pq2.printHeap();
      pq2.insert(1); 
      pq2.printHeap();
      par(pq2.toArray());
      System.exit(0);
      
      Random r = new Random(System.currentTimeMillis());
      Integer[] a = rangeInteger(1,20);
      shuffle(a,r);
      
      MinPQ<Integer> pq = new MinPQ<>(a);
      pq.show();
      
      
//        MinPQ<String> pq = new MinPQ<String>();
//        while (!StdIn.isEmpty()) {
//            String item = StdIn.readString();
//            if (!item.equals("-")) pq.insert(item);
//            else if (!pq.isEmpty()) System.out.print(pq.delMin() + " ");
//        }
//        System.out.println("(" + pq.size() + " left on pq)");
    }

}

