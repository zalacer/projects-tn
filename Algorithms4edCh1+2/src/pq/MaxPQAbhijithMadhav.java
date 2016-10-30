package pq;

import static v.ArrayUtils.*;

import java.util.Random;

/**
 * from https://raw.githubusercontent.com/AbhijithMadhav/Data-Structures-And-Algorithms/master/Data%20Structures%20and%20Algorithms/src/ds/pq/MaxPQ.java
 * @author kempa
 * 
 * @param <Key>
 *            is the type of the elements in the PQ. It extends the Comparable
 *            class
 */
@SuppressWarnings("unchecked")
public class MaxPQAbhijithMadhav<Key extends Comparable<Key>>
{

  /*
   * 2.4.27 Find the minimum. Add a min() method to MaxPQ. Your implementation
   * should use constant time and constant extra space.
   * 
   * 2.4.22 Array resizing. Add array resizing to MaxPQ, and prove bounds like
   * those of Proposition Q for array accesses, in an amortized sense.
   * 
   * 2.4.13 Describe a way to avoid the j < N test in sink().
   * 
   * 2.4.26 Heap without exchanges. Because the exch() primitive is used in
   * the sink() and swim() operations, the items are loaded and stored twice
   * as often as necessary. Give more efficient implementations that avoid
   * this inefficiency, a la insertion sort (see Exercise 2.1.25).
   * 
   * 2.4.15 Design a linear-time certification algorithm to check whether an
   * array pq[] is a min-oriented heap.
   */

  private Key[] pq; // heap-ordered complete binary tree in pq[1..N] with pq[0] unused
  private int N; 

  private int minIndex; // Pointer to the minimum key

  /**
   * Constructs an empty Max-PQ with the specified intial capacity
   * 
   * @param capacity
   *            Initial capacity of the Max-PQ
   */
  public MaxPQAbhijithMadhav(int capacity)
  {
    pq = (Key[]) new Comparable[capacity + 1];
    N = 0;
    minIndex = -1;
  }

  /**
   * Constructs a Max-PQ initialised with the specified array
   * 
   * @param a
   *            array whose elements are to be used to initialise Max-PQ
   */
  public MaxPQAbhijithMadhav(Key a[])
  {
    pq = (Key[]) new Comparable[a.length + 1];
    System.arraycopy(a, 0, pq, 1, a.length);

    N = pq.length - 1;

    for (int i = pq.length / 2; i >= 1; i--) sink(i);
    
    minIndex = -1;

//    assert isMaxHeap(1); 
//    show();
  }

  /**
   * Tests if Max-PQ is empty
   * 
   * @return Test result
   */
  public boolean isEmpty()
  {
    return N == 0;
  }

  /**
   * Returns size of Max-PQ
   * 
   * @return Size of Max-PQ
   */
  public int size()
  {
    return N;
  }

  /**
   * Insert a key into Max-PQ in logarithmic time
   * 
   * @param v
   *            Key to be inserted
   */
  public void insert(Key v)
  {
    
    // resizing
    if (N >= pq.length - 1)
      pq = resize(pq, 2 * pq.length);
    
    // Insert
    pq[++N] = v;
    int i = swim(N);

    // Mark the min
    if (minIndex == -1)
      minIndex = i;
    else if (v.compareTo(pq[minIndex]) < 0)
      minIndex = i;
  }

  /**
   * Resizes the specified array with the specified size
   * 
   * @param a
   *            Array to be resized
   * @param n
   *            New size
   * @return Resized array
   */
  Key[] resize(Key[] a, int n)
  {
    Key[] b = (Key[]) new Comparable[n];
    System.arraycopy(a, 0, b, 0, (a.length < b.length) ? a.length
        : b.length);
    return b;
  }

  /**
   * pq[i] swims to its rightful place.
   * 
   * @param i
   *            Index of element which has to swim
   * @return Index of key after the swim
   */
  private int swim(int i)
  {
    Key t = pq[i]; // Save key for later restoring
    while (i > 1 && less(pq[i / 2], pq[i]))
    {
      // exch(pq, i / 2, i);
      pq[i] = pq[i / 2]; // half exchanges
      i = i / 2;
    }
    pq[i] = t; // restore key
    return i;
  }

  /**
   * Delete the max key in logarithmic time
   * 
   * @return The max key which was deleted
   */
  public Key delMax()
  {
    if (N == 0)
      throw new RuntimeException("Priority Queue Underflow");

    Key max = pq[1];
    // exch(pq, 1, N);
    pq[1] = pq[N]; // half exchange
    pq[N--] = null;
    sink(1);

    // resize
    if (N == pq.length / 4)
      pq = resize(pq, pq.length / 2);

    // keep track of min
    if (N == 0)
      minIndex = -1;

    return max;
  }

  /**
   * Returns the max key in constant time
   * 
   * @return The max key
   */
  public Key max()
  {
    if (N == 0)
      throw new RuntimeException("Priority Queue Underflow");
    return pq[1];

  }

  /**
   * Returns the min key in constant time
   * 
   * @return The min key
   */
  public Key min()
  {
    if (minIndex != -1)
      return pq[minIndex];
    return null;
  }

  /**
   * pq[i] sinks to its rightful place
   * 
   * @param i
   *            Index of the element which should sink
   * @return New Index of the element which has sunk
   */
  private int sink(int i)
  {

    Key t = pq[i]; // Save key for later restoring
    while (2 * i + 1 <= N) // while pq[i] has both the children
    {
      int j = 2 * i + 1; // rightmost child

      // 1st comparision: Making j point to the greatest of the two
      // children as pq[i] should sink to j
      //if (less(pq[j - 1], pq[j])) order of comparison should be inverted
      //  j--;
      if (less(pq[j], pq[j-1])) j--;

      // 2nd comparsion. Stop sinking if pq[i] is not less than its
      // greatest child
      if (!less(t, pq[j]))
        break;

      // exch(pq, i, j);
      pq[i] = pq[j]; // half exchanges
      i = j; // Prepare 'i' for the next iteration
    }

    // if pq[i] had only one child, the comparision for sink hasn't been
    // done yet
    if (2 * i == N)
      if (less(pq[i], pq[N]))
      {
        // exch(pq, i, 2 * i);
        pq[i] = pq[N];
        i = N;
      }

    pq[i] = t; // restore
    return i;
  }

  /**
   * Is the tree rooted at i a max-oriented heap?
   * 
   * @param i
   *            Index of the root
   * @return
   */
  boolean isMaxHeap(int i)
  {
    if (2 * i > N) // no children
      return true;
    
    // Not a max heap if parent is lesser than its children
    if (less(pq[i], pq[2 * i])
        || ((2 * i + 1 <= N) && less(pq[i], pq[2 * i + 1])))
      return false;
    
    // Recursively test for left and right subheaps
    return (isMaxHeap(2 * i) && isMaxHeap(2 * 1 + 1));
  }
  
  @SuppressWarnings("unused")
  private boolean isMinHeap(int k) {
    if (k > N) return true;
    int left = 2*k;
    int right = 2*k + 1;
    if (left  <= N && greater(k, left))  return false;
    if (right <= N && greater(k, right)) return false;
    return isMinHeap(left) && isMinHeap(right);
  }
  
  
  
  @SuppressWarnings("rawtypes")
  private boolean less(Comparable a, Comparable b)
  {
    return a.compareTo(b) < 0;
  }
  
  private boolean greater(int i, int j) {
        return ((Comparable<Key>) pq[i]).compareTo(pq[j]) > 0;
  }
  
  /**
   * Deletes the item in node i from the heap
   * 
   * @param i
   * @return
   */
  public Key delete(int i)
  {
    if (i > pq.length - 1 || i < 1)
      return null;
    Key key = pq[i];
    pq[i] = pq[N--];
    if (less(pq[i], key))
      sink(i);
    else
      swim(i);
    return key;
  }
  
  public void show() {
    if (N == 0) System.out.print("<nothing in pq>");
    for (int i = 1; i < N+1 ; i++) System.out.print(pq[i]+" ");
    System.out.println();
  }

  
  public static void main(String[] args) {

    Random r = new Random(System.currentTimeMillis());
    Integer[] ix = new Integer[8];
    for (int i = 0; i < ix.length; i++) ix[i] = r.nextInt(8);
    par(ix);
    MaxPQAbhijithMadhav<Integer> pq = new MaxPQAbhijithMadhav<Integer>(ix);
//    System.out.println(pq.max());
//    System.out.println(ArrayUtils.max(ix));
    pq.insert(8);
    System.out.println(pq.min());
  }
  
}