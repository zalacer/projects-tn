package ds;

import static java.util.Arrays.copyOf;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

//p168
//1.3.38 Delete  k th element. Implement a class that supports the following API:
//public class GeneralizedQueue<Item>
//GeneralizedQueue()  create an empty queue
//boolean isEmpty()  is the queue empty?
//void insert(Item x)  add an item
//Item delete(int k)  delete and return the  kth least recently inserted item
//API for a generic generalized queue
//First, develop an implementation that uses an array implementation, and then develop
//one that uses a linked-list implementation. Note : the algorithms and data structures
//that we introduce in Chapter 3 make it possible to develop an implementation that
//can guarantee that both  insert() and  delete() take time prortional to the logarithm
//of the number of items in the queue—see Exercise 3.5.27.


public class GeneralizedArrayQueue<T> implements Iterable<T> {
  private T[] a;
  private int first = 0; // index of least recently added element in a
  private int last = 0; // index of next element to be added to a
  private int N = 0; // number of items on the queue

  @SuppressWarnings("unchecked")
  public GeneralizedArrayQueue() {
    this.a = (T[]) new Object[2];
  }

  @SuppressWarnings("unchecked")
  public GeneralizedArrayQueue(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("GeneralizedArrayQueue "
        + " constructor: int argument cannot be < 0");
    this.a = (T[]) new Object[capacity];
  }

  public GeneralizedArrayQueue(T[] sa) {
    if (sa == null) throw new IllegalArgumentException("GeneralizedArrayQueue "
        +" constructor: array argument cannot be null)");
    a = sa;
    last = a.length;
    N = last;
  }

  public GeneralizedArrayQueue(GeneralizedArrayQueue<T> bqos) {
    if (bqos == null) throw new IllegalArgumentException("GeneralizedArrayQueue "
        + "constructor: GeneralizedArrayQueue argument cannot be null)");
    a = bqos.toArray();
    last = a.length;
    N = last;
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
  
  public void insert(T t) {
    enqueue(t);
  }

  public T dequeue() {
    if (isEmpty()) throw new NoSuchElementException("dequeue: queue underflow");
    T t = (T) a[first];
    a[first] = null;
    first = (first + 1) % a.length; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }
  
  public T delete(int k) {
    // delete and return the kth least recently inserted item where the element
    // at a[(last - 1) % a.length] is the first such item
    if (isEmpty()) throw new NoSuchElementException("delete: queue underflow");
    if (k < 1) throw new IllegalArgumentException("delete: k must be > 0");
    if (size() < k) throw new NoSuchElementException(
        "delete: kth from last element does not exist");
    int index = (last-k) % a.length;
    if (index == first) {
      return dequeue();
    } else {
      T t = a[index];
      if (index == (last-1) % a.length) {
        last--; N--;
        if (N > 0 && N == a.length/4) resize(a.length/2); 
      } else {
        for (int i = index; i < last; i++) a[i] = a[i+1];
        last--; N--;
        if (N > 0 && N == a.length/4) resize(a.length/2); 
      }
      return t;
    }
  }

  public T peek() {
    if (isEmpty()) throw new NoSuchElementException("peek: queue underflow");
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
    T[] ta = copyOf(t, N);
    int i = 0;
    Iterator<T> it = this.iterator();
    while(it.hasNext()) ta[i++] = it.next();
    return ta;
  }

  public T kthFromLast(int k) {
    // return the kth from last element in the queue where 1st from last = last
    if (k < 1) throw new IllegalArgumentException("kthFromLast: k must be > 0");
    if (size() < k) throw new NoSuchElementException("kth from last element does not exist");
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
    GeneralizedArrayQueue other = (GeneralizedArrayQueue) obj;
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
    StringBuilder sb = new StringBuilder();
    sb.append("GeneralizedArrayQueue(");
    if (isEmpty()) return sb.append(")").toString();
    for (T t : this) sb.append(""+t+",");
    return sb.substring(0, sb.length()-1)+")";
  }


  public static void main(String[] args) {

  }

}