package pq;

import static analysis.Log.lg;
import static v.ArrayUtils.append;
import static v.ArrayUtils.highestIndexOfEqualOrLess;
import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.par;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

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

import analysis.BTreePrinter;
import analysis.Node;

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
//@SuppressWarnings("unchecked")
@SuppressWarnings({"unchecked","unused"})
public class MinPQ2ex2431<Key> implements Iterable<Key> {
    private Key[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue
    private Comparator<Key> comparator;  // optional comparator
    private Class<?> keyClass = null;

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public MinPQ2ex2431(int initCapacity) {
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public MinPQ2ex2431() {
        this(1);
    }

    /**
     * Initializes an empty priority queue with the given initial capacity,
     * using the given comparator.
     *
     * @param  initCapacity the initial capacity of this priority queue
     * @param  comparator the order to use when comparing keys
     */
    public MinPQ2ex2431(int initCapacity, Comparator<Key> comparator) {
        this.comparator = comparator;
        pq = (Key[]) new Object[initCapacity + 1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue using the given comparator.
     *
     * @param  comparator the order to use when comparing keys
     */
    public MinPQ2ex2431(Comparator<Key> comparator) {
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
    
    public MinPQ2ex2431(Key...keys) {
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
      if (n == 1) return;
      if ((n == 2 || n == 3) && less(n,1)) { exch(n,1); return; }
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


   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

//    public static <T extends Comparable<? super T>> int highestIndexOfEqualOrLess(T z[], T key) {
//      // return the highest index i in z such that z[i].compareTo(key) <= 0,
//      // else if all elements in z are greater than key return -1.
//      // z must be sorted in ascending order. uses fibonacci search.
//      if (z == null || z.length == 0) return -1;
//      int n = z.length;
//      if (n == 1) return z[0] == key ? 0 : -1;
//      int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
//          4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
//          832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
//          39088169,63245986,102334155,165580141};
//      int low = 0, mid = 0, i = 0, ret = 0;
//      while(fib[i] < n) i++;
//      while(i > 0) {
//        mid = low + fib[--i];
//        if (mid >= n || key.compareTo(z[mid])<0) continue;
//        else if (key.compareTo(z[mid])>0) { ret = mid; low = mid+1; i--; }
//        else {
//          while(mid < n-1 && z[mid].compareTo(z[mid+1])==0) mid++;
//          return mid;
//        }
//      }
//      if (ret == 0) { return z[0].compareTo(key)< 0 ? 0 : -1; }
//      else return ret;
//    }
//    
//    public static <T> int highestIndexOfEqualOrLess(T z[], T key, Comparator<T> c) {
//      // return the highest index i in z such that c.compare(z[i],key) <= 0,
//      // else if all elements in z are greater than key return -1.
//      // z must be sorted in ascending order. uses fibonacci search.
//      if (z == null || z.length == 0) return -1;
//      int n = z.length;
//      if (n == 1) return z[0] == key ? 0 : -1;
//      int[] fib = {0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,
//          4181,6765,10946,17711,28657,46368,75025,121393,196418,317811,514229,
//          832040,1346269,2178309,3524578,5702887,9227465,14930352,24157817,
//          39088169,63245986,102334155,165580141};
//      int low = 0, mid = 0, i = 0, ret = 0;
//      while(fib[i] < n) i++;
//      while(i > 0) {
//        mid = low + fib[--i];
//        if (mid >= n || c.compare(key,z[mid])<0) continue;
//        else if (c.compare(key,z[mid])>0) { ret = mid; low = mid+1; i--; }
//        else {
//          while(mid < n-1 && c.compare(z[mid],z[mid+1])==0) mid++;
//          return mid;
//        }
//      }
//      if (ret == 0) { return c.compare(z[0],key)< 0 ? 0 : -1; }
//      else return ret;
//    }
    
    private void swimOrig(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
            printHeap(); 
        }
    }
    
    private void swimDebug(int k) {
      // this method is called only by insert and is called only if n > 3
      int h = (int)Math.ceil(lg(n));
      System.out.println("h="+h); 
      int[] s;
      if (h == 2) s = new int[(int)(Math.ceil(lg(n)))]; 
      else s = new int[(int)(Math.ceil(lg(n)-1))];
      System.out.print("pq="); par(pq);
      int l=k, c = s.length-1;
      while (k > 1) { k = k/2;  if (k!=0) s[c--] = k; }
      System.out.println("c="+c);
      System.out.print("s="); par(s);
      Key[] ka = ofDim(keyClass, s.length);
      for (int i = 0; i<ka.length; i++) ka[i] = pq[s[i]];
      System.out.print("ka="); par(ka);
      int index = -2;
      if (comparator == null) index = highestIndexOfEqualOrLess(ka, pq[l]);
      else index = highestIndexOfEqualOrLess(ka, pq[l], comparator);
      System.out.println("index="+index);
      if (index < -1) { swimOrig(l); System.out.println("indexing failed"); return; }
      if (index+1 > s.length-1) return;
      Key nu = pq[l]; System.out.println("nu="+nu);
      pq[l] = ka[ka.length-1];
      for (int i = s.length-1; i>index+1; i--) pq[s[i]] = pq[s[i-1]];
      if (index == -1) pq[1] = nu;
      else pq[s[index+1]] = nu;
    }
    
    private void swim(int k) {
      // this method is called only by insert and is called only if n > 3
      int h = (int)Math.ceil(lg(n));
      int[] s;
      if (h == 2) s = new int[(int)(Math.ceil(lg(n)))]; 
      else s = new int[(int)(Math.ceil(lg(n)-1))];
      int l=k, c = s.length-1;
      while (k > 1) { k = k/2; s[c--] = k; }
      Key[] ka = ofDim(keyClass, s.length);
      for (int i = 0; i<ka.length; i++) ka[i] = pq[s[i]];
      int index = -2;
      if (comparator == null) index = highestIndexOfEqualOrLess(ka, pq[l]);
      else index = highestIndexOfEqualOrLess(ka, pq[l], comparator);
      if (index < -1) { swimOrig(l); return; } //indexing failed
      if (index+1 > s.length-1) return;
      Key nu = pq[l];
      pq[l] = ka[ka.length-1];
      for (int i = s.length-1; i>index+1; i--) pq[s[i]] = pq[s[i-1]];
      if (index == -1) pq[1] = nu;
      else pq[s[index+1]] = nu;
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
    
    private boolean less(int i, int j) {
      if (comparator == null) {
          return ((Comparable<Key>) pq[i]).compareTo(pq[j]) < 0;
      }
      else {
          return comparator.compare(pq[i], pq[j]) < 0;
      }
    }

    
    private boolean lessOrEqual(int i, int j) {
      if (comparator == null) {
          return ((Comparable<Key>) pq[i]).compareTo(pq[j]) <= 0;
      }
      else {
          return comparator.compare(pq[i], pq[j]) <= 0;
      }
    }

    private void exch(int i, int j) {
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
        private MinPQ2ex2431<Key> copy;

        // add all items to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            if (comparator == null) copy = new MinPQ2ex2431<Key>(size());
            else                    copy = new MinPQ2ex2431<Key>(size(), comparator);
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
      
      Random r = new Random(System.currentTimeMillis()); MinPQ2ex2431<Integer> pq2;
      // sink testing
      Integer[] aa = {9,1,7,2,6,3,5,4,8,10,11,12,13,14,15,16};
      shuffle(aa,r); par(aa);
      pq2 = new MinPQ2ex2431<Integer>(aa);
      pq2.printHeap();
      pq2.delMin();
      pq2.printHeap();
      System.exit(0);
      
      
      
      // swim testing
//      pq2 = new MinPQ2ex2431<Integer>(1,4,3,6); //(9,1,7,2,6,3,5,4,8);   //(0,9,1,7,6,3,5,4,8,10,11,12,13,14,15); //
      Integer[] b = append(rangeInteger(1,1000), rangeInteger(1001,2001));     
      pq2 = new MinPQ2ex2431<Integer>(b);
//      pq2.printHeap();
      pq2.insert(1000); 
//      pq2.printHeap();
//      pq2.insert(-1); 
//      pq2.printHeap();
      System.exit(0);
      
      Integer[] a = rangeInteger(1,20);
      shuffle(a,r);
      
      MinPQ2ex2431<Integer> pq = new MinPQ2ex2431<>(a);
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

