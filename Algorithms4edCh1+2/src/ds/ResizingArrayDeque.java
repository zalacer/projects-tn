package ds;

import static java.util.Arrays.copyOf;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResizingArrayDeque<T> implements Iterable<T> {
  private T[] a;
  private int first = 0; // index of least recently added node in a
  private int last = 0; // index of most recently added node in a
  private int N = 0; // number of items on the queue

  @SuppressWarnings("unchecked")
  public ResizingArrayDeque() {
    this.a = (T[]) new Object[2];
  }

  @SuppressWarnings("unchecked")
  public ResizingArrayDeque(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
        + " constructor: int argument cannot be < 0");
    this.a = (T[]) new Object[capacity];
  }

  public ResizingArrayDeque(T[] sa) {
    a = sa;
    first = 0;
    last = sa.length;
    N = sa.length;
  }

  @SuppressWarnings("unchecked")
  public ResizingArrayDeque(ResizingArrayDeque<? extends T> bqos) {
    a = (T[]) new Object[bqos.size()];
    int i  = 0;
    for (T t : bqos) a[i++] = t;
    first = 0;
    last = a.length;
    N = a.length;
  }
  
  @SuppressWarnings("unchecked")
  public ResizingArrayDeque(Collection<? extends T> c) {
    if (c == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
        + "constructor: ResizingArrayQueueOfStrings argument cannot be null)");
    a = (T[]) new Object[c.size()];
    int i  = 0;
    for (T t : c) a[i++] = t;
    first = 0;
    last = a.length;
    N = a.length;
  }

  public int size() {
    return N;
  }

  public int getFirst() {
    // return the first filled index in a
    return first;
  }

  public int getLast() {
    // return the next to be filled index in a
    return last;
  }

  public boolean isEmpty() {
    return N == 0;
  }

  public boolean isFull() {
    return N == a.length;
  }

  private void resize(int newSize, int offset) {
    // offset is to create a vacancy at a[0] for pushLeft
    // and should normally be 0 or for pushLeft 1
    if (newSize < N) throw new IllegalArgumentException("resize: "
        + "newSize must be greater than the current size");
    @SuppressWarnings("unchecked")
    T[] temp = (T[]) new Object[newSize];
    for (int i = 0; i < N; i++) {
      temp[i+offset] = a[(first + i) % a.length];
    }
    a = temp;
    first = 0;
    N+=offset;
    last = N;
  }
  
  public void pushLeft(T t) { 
    // Add t to the beginning of the list.
    if (N == a.length) {
      resize(2*a.length, 1); // sets first to 0, increments N and creates empty a[0]
      a[0] = t;
      return;
    }
    int newfirst = first == 0 ? a.length-1 : first - 1;
    a[newfirst] = t;
    first = newfirst;
    N++;
  }
  
  public T popLeft() {
    // remove the first element from the list
    if (isEmpty()) {
      System.err.println("warning: queue is empty");
      return null;
    }
    boolean size1 = size() == 1 ? true : false;
    T t = (T) a[first];
    a[first] = null;
    if (size1) {
      first = 0; last = 0;
    } else first = (first + 1) % a.length; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2, 0); 
    return t;
  }

  public void pushRight(T t) { 
    // Add t to the end of the list
    if (N == a.length) resize(2*a.length, 0);  
    a[last] = t;
    last = (last + 1) % a.length; // handles wrap-around
    N++;
  }
  
  public T popRight() {
    // remove the last element from the last
    if (isEmpty()) {
      System.err.println("warning: queue is empty");
      return null;
    }
    T t = (T) a[last-1];
    a[last-1] = null;
    last--;; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2, 0); 
    return t;
  }

  public T peek() {
    if (isEmpty()) throw new NoSuchElementException("queue underflow");
    return a[first];
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
  
  public Iterator<T> reverseIterator() { return new ReverseArrayIterator(); }

  private class ReverseArrayIterator implements Iterator<T> { // Support LIFO iteration.
    private int i = N;
    public boolean hasNext() { return i > 0; }
    public T next() { return a[--i]; }
    public void remove() { }
  }
  
  public Object[] toArray() {
    // return an Object[] contains this list's data in LIFO order
    Object[] sa = new Object[N];
    int i = 0;
    Iterator<T> it = this.iterator();
    while(it.hasNext()) sa[i++] = it.next();
    return sa;
  }

  public T[] toArray(@SuppressWarnings("unchecked") T...t) {
    // return a T[] contains this list's data in LIFO order
    if (t == null) throw new IllegalArgumentException("toArray: this method "
        + "requires providing at least one T as an argument");
    T[] ta = copyOf(t, N);
    int i = 0;
    Iterator<T> it = this.iterator();
    while(it.hasNext()) ta[i++] = it.next();
    return ta;
  }
  
  public Object[] toReverseArray() {
    // return an Object[] contains this list's data in reverse (FIFO) order
    Object[] sa = new Object[N];
    int i = 0;
    Iterator<T> it = this.reverseIterator();
    while(it.hasNext()) sa[i++] = it.next();
    return sa;
  }
  
  public T[] toReverseArray(@SuppressWarnings("unchecked") T...t) {
    // return a T[] contains this list's data in reverse (FIFO) order
    if (t == null) throw new IllegalArgumentException("toReverseArray: this method "
        + "requires providing at least one T as an argument");
    T[] ta = copyOf(t, N);
    int i = 0;
    Iterator<T> it = this.reverseIterator();
    while(it.hasNext()) ta[i++] = it.next();
    return ta;
  }

  public T kthFromLast(int k) {
    if (k < 0) throw new IllegalArgumentException("kthFromLast: k must be > -1");
    if (k < 0) throw new IllegalArgumentException("kthFromLast: k must be > -1");
    if (this.size() < k) throw new NoSuchElementException("kth from last element does not exist");
    return a[(last-k) % a.length];
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
    ResizingArrayDeque other = (ResizingArrayDeque) obj;
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
    return "("+Arrays.toString(toArray()).replaceAll("[\\[\\] ]","")+")";
  }

}