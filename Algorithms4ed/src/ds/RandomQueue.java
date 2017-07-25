package ds;

import static java.util.Arrays.copyOf;
import static v.ArrayUtils.shift;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

//  p168
//  1.3.35  Random queue. A random queue stores a collection of items and supports the
//  following API:
//  public class RandomQueue<Item>
//  RandomQueue() create an empty random queue
//  boolean isEmpty()  is the queue empty?
//  void enqueue(Item item)  add an item
//  Item dequeue()  remove and return a random item (sample without replacement)
//  Item sample() return a random item, but do not remove (sample with replacement)
//  API for a generic random queue
//  Write a class  RandomQueue that implements this API. Hint : Use an array representation
//  (with resizing). To remove an item, swap one at a random position (indexed  0 through
//  N-1 ) with the one at the last position (index  N-1 ). Then delete and return the last ob-
//  ject, as in  ResizingArrayStack . Write a client that deals bridge hands (13 cards each)
//  using RandomQueue<Card> 

// based on ResizingArrayQueue
public class RandomQueue<T> implements Iterable<T> {
  private T[] a;
  private int first = 0; // index of least recently added node in a
  private int last = 0; // index of most recently added node in a
  private int N = 0; // number of items on the queue
  private Random r;

  @SuppressWarnings("unchecked")
  public RandomQueue() {
    this.a = (T[]) new Object[2];
    this.r = makeRandom();
  }

  @SuppressWarnings("unchecked")
  public RandomQueue(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
        + " constructor: int argument cannot be < 0");
    this.a = (T[]) new Object[capacity];
    this.r = makeRandom();
  }

  public RandomQueue(T[] sa) {
    if (sa == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
        +" constructor: array argument cannot be null)");
    a = sa; first = 0; last = sa.length; N = last; r = makeRandom();
  }

  @SuppressWarnings("unchecked")
  public RandomQueue(RandomQueue<T> bqos) {
    if (bqos == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
        + "constructor: ResizingArrayQueueOfStrings argument cannot be null)");
    Object[] sa = bqos.toArray();
    for (int i = 0; i < sa.length; i++) enqueue((T) sa[i]);
    this.r = makeRandom();
  }
  
  public Random makeRandom() {
    // return an instance of Random
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(793095727);
    return r;
  }

  public int size() {
    return N;
  }

  public int getFirst() {
    return first;
  }

  public int getLast() {
    return last;
  }

  public boolean isEmpty() {
    return N == 0;
  }

  public boolean isFull() {
    return N == a.length;
  }

  private void resize(int newSize) {
    if (newSize < N) throw new IllegalArgumentException("resize: "
        + "newSize must be greater than the current size");
    @SuppressWarnings("unchecked")
    T[] temp = (T[]) new Object[newSize];
    for (int i = 0; i < N; i++) {
      temp[i] = a[(first + i) % a.length];
    }
    a = temp;
    first = 0;
    last  = N;
  }

  public void enqueue(T t) { // Add t to the end of the list.
    if (N == a.length) resize(2*a.length);  
    a[last] = t;
    last = (last + 1) % a.length; // handles wrap-around
    N++;
  }

  public T dequeue() {
    //swaps first with an element at a random position then returns first and removes it
    if (isEmpty()) {
      System.err.println("warning: queue is empty");
      return null;
    }
    T t = a[first];
    if (N > 1) {
      int n = r.nextInt(N-1) + 1;
      a[first] = a[(first+n) % a.length];
      a[(first+n) % a.length] = t;
    }
    t = a[first];
    a[first] = null;
    first = (first + 1) % a.length; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }

  public T sample() {
    // return a random element without removing it from the queue
    if (isEmpty()) throw new NoSuchElementException("queue underflow");
    return a[first+r.nextInt(N) % a.length];
  }
  
  public Iterator<T> randomIterator() { 
    // this is a random iterator
    T[] items = (T[]) toArray();
    shuffle(items, r);
    return Arrays.stream(items).iterator();
  }
  
  public static <T> void shuffle(T[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle "
        + "both the array and the Random argument must not be null");
    int len = a.length;
    for (int i = 0; i < len; i++) {
      int n = i + r.nextInt(len-i);
      T t = a[i];
      a[i] = a[n];
      a[n] = t;
    }
  }

  public Iterator<T> iterator() {
    return new ArrayIterator();
  }

  private class ArrayIterator implements Iterator<T> {
    private int i = 0;
    public boolean hasNext()  { return i < N; }

    public T next() {
      if (!hasNext()) throw new NoSuchElementException();
      T t = a[(i + first) % a.length];
      i++;
      return t;
    }
  }

  public T[] toArray() {
    @SuppressWarnings("unchecked")
    T[] sa = (T[]) new Object[N];
    int i = 0;
    Iterator<T> it = this.iterator();
    while(it.hasNext()) sa[i++] = it.next();
    return sa;
  }

  public T[] toArray(@SuppressWarnings("unchecked") T...t) {
    if (t == null) throw new IllegalArgumentException("toArray: this method "
        + "requires providing at least one T as an argument");
    T[] ta = (T[]) copyOf(t, N);
    int i = 0;
    Iterator<T> it = this.iterator();
    while(it.hasNext()) ta[i++] = it.next();
    return ta;
  }

  public T kthFromLast(int k) {
    if (k < 0) throw new IllegalArgumentException("kthFromLast: k must be > -1");
    if (this.size() < k) {
      System.err.println("kth from last element does not exist");
      return null;
    }
    @SuppressWarnings("unchecked")
    T[] a = (T[]) new Object[k];
    Iterator<T> it = this.iterator();
    int i = 0;
    while(i < k && it.hasNext()) {
      a[i++] = it.next();
    }
    if (!it.hasNext()) {
      return (T) a[k-1];
    } else {
      while(it.hasNext()) {
        shift(a,1); // shifts elements in a in place to the right by 1
        a[0] = it.next();
      }
      return (T) a[k-1];
    }
  }

  public Object[] getArray() {
    return a;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + Arrays.hashCode(a);
    result = prime * result + first;
    result = prime * result + last;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    RandomQueue other = (RandomQueue) obj;
    if (N != other.N)
      return false;
    if (!Arrays.equals(a, other.a))
      return false;
    if (first != other.first)
      return false;
    if (last != other.last)
      return false;
    return true;
  }

  @Override
  public String toString() {
    // returns a string representation of list with randomized order of nodes
    StringBuilder sb = new StringBuilder();
    sb.append("RandomQueue(");
    if (isEmpty()) return sb.append(")").toString();
    for (T t : this) sb.append(""+t+",");
    return sb.substring(0, sb.length()-1)+")";
  }


  public static void main(String[] args) {

  }

}