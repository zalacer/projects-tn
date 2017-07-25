package pq;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import v.ArrayUtils;

// from http://algs4.cs.princeton.edu/24pq/IndexMinPQ.java
// used for for graph.EagerPrimMSTtrace


@SuppressWarnings("unchecked")
public class IndexMinPQY<Key extends Comparable<Key>> implements Iterable<Integer> {
  private int maxN;        // maximum number of elements on PQ
  private int n;           // number of elements on PQ
  private int[] pq;        // binary heap using 1-based indexing
  private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
  private Key[] keys;      // keys[i] = priority of i

  public IndexMinPQY(int maxN) {
    if (maxN < 0) throw new IllegalArgumentException();
    this.maxN = maxN;
    n = 0;
    keys = (Key[]) new Comparable[maxN + 1];    // make this of length maxN??
    pq   = new int[maxN + 1];
    qp   = new int[maxN + 1];                   // make this of length maxN??
    for (int i = 0; i <= maxN; i++)
      qp[i] = -1;
  }

  public boolean isEmpty() { return n == 0; }

  public boolean contains(int i) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    return qp[i] != -1;
  }

  public int size() { return n; }

  public void insert(int i, Key key) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
    n++;
    qp[i] = n;
    pq[n] = i;
    keys[i] = key;
    swim(n);
  }

  public int minIndex() {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow");
    return pq[1];
  }

  public Key minKey() {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow");
    return keys[pq[1]];
  }

  public int delMin() {
    if (n == 0) throw new NoSuchElementException("Priority queue underflow");
    int min = pq[1];
    exch(1, n--);
    sink(1);
    assert min == pq[n+1];
    qp[min] = -1;        // delete
    keys[min] = null;    // to help with garbage collection
    pq[n+1] = -1;        // not needed
    return min;
  }

  public Key keyOf(int i) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    else return keys[i];
  }

  public void changeKey(int i, Key key) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    keys[i] = key;
    swim(qp[i]);
    sink(qp[i]);
  }

  public void change(int i, Key key) { changeKey(i, key); }

  public void decreaseKey(int i, Key key) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    if (keys[i].compareTo(key) <= 0)
      throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
    keys[i] = key;
    swim(qp[i]);
  }

  public void increaseKey(int i, Key key) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    if (keys[i].compareTo(key) >= 0)
      throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
    keys[i] = key;
    sink(qp[i]);
  }

  public void delete(int i) {
    if (i < 0 || i >= maxN) throw new IndexOutOfBoundsException();
    if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
    int index = qp[i];
    exch(index, n--);
    swim(index);
    sink(index);
    keys[i] = null;
    qp[i] = -1;
  }

  private boolean greater(int i, int j) {
    return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
  }

  private void exch(int i, int j) {
    int swap = pq[i];
    pq[i] = pq[j];
    pq[j] = swap;
    qp[pq[i]] = i;
    qp[pq[j]] = j;
  }

  private void swim(int k) {
    while (k > 1 && greater(k/2, k)) {
      exch(k, k/2);
      k = k/2;
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

  public Iterator<Integer> iterator() { return new HeapIterator(); }

  private class HeapIterator implements Iterator<Integer> {
    // create a new pq
    private IndexMinPQY<Key> copy;

    // add all elements to copy of heap
    // takes linear time since already in heap order so no keys move
    public HeapIterator() {
      copy = new IndexMinPQY<Key>(pq.length - 1);
      for (int i = 1; i <= n; i++)
        copy.insert(pq[i], keys[pq[i]]);
    }

    public boolean hasNext()  { return !copy.isEmpty();                     }
    public void remove()      { throw new UnsupportedOperationException();  }

    public Integer next() {
      if (!hasNext()) throw new NoSuchElementException();
      return copy.delMin();
    }
  }
  
  public Integer[] toArray() { 
    if (isEmpty()) return new Integer[0];
    return ArrayUtils.toArray(iterator()); }

  public static void main(String[] args) {
    // insert a bunch of strings
    String[] strings = { "it", "was", "the", "best", "of", "times", "it", "was", "the", "worst" };

    IndexMinPQY<String> pq = new IndexMinPQY<String>(strings.length);
    for (int i = 0; i < strings.length; i++) {
      pq.insert(i, strings[i]);
    }
    par(pq.toArray());

    // delete and print each key
    while (!pq.isEmpty()) {
      int i = pq.delMin();
      System.out.println(i + " " + strings[i]);
    }
    System.out.println();

    // reinsert the same strings
    for (int i = 0; i < strings.length; i++) {
      pq.insert(i, strings[i]);
    }

    // print each key using the iterator
    for (int i : pq) {
      System.out.println(i + " " + strings[i]);
    }
    while (!pq.isEmpty()) {
      pq.delMin();
    }

  }
}

