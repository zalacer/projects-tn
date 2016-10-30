package ds;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<T> implements Iterable<T> {

  private final int capacity = 9; // default length of a
  private T[] a;
  private int first = 0; //index of next element to be added
  private int last = 0; //index of next element to be removed
  private int N; // current size
  
  @SuppressWarnings("unchecked")
  public RingBuffer(){
    a = (T[]) new Object[capacity];
  }

  @SuppressWarnings("unchecked")
  public RingBuffer(int length){
    a = (T[]) new Object[length];
  }
  
  @SuppressWarnings("unchecked")
  public RingBuffer(T[] b){
    if (b == null) a = (T[]) new Object[capacity];
    else { 
      a = b; N = a.length; first = 0; last = 0;
      for (T t : a) if (t != null) {
      }
    }
  }
  
  public int last() {
    return last;
  }

  public int size() {
    return N;
  }

  public boolean isEmpty() {
    return N == 0;
  }

  public boolean isFull() {
    return N == a.length;
  }
  
  public int remaining() {
    // return the number of unused elements in a
    return a.length - N;
  }

  public void add(T t) {
    // add an element to the end of the buffer
    if(isFull()) {
      throw new IllegalStateException("add: queue overflow: cannot add an element");
    } else {
      a[last] = t;
      last = (last + 1) % a.length;
      N++;
    }
  }
  
  public int add(T[] ta) {
    // add at most ta.length elements from ta consecutively to the 
    // end of the buffer and return the number added
    if(isFull())
      throw new IllegalStateException("add: queue overflow: cannot add an element");
    if (ta == null || ta.length == 0) return 0;
    int r = remaining();
    int n = r < ta.length ? r : ta.length;
    // performance can be improved by eliminating the modulo operations
    for (int i = 0; i < n; i++) a[(last+i) % a.length] = ta[i];
    last = (last + n) % a.length;
    N+=n;
    return n;
  }

  public T remove() {
    // remove an element from the beginning of the buffer and return it
    T item; 
    if(isEmpty()) {
      throw new NoSuchElementException("remove: queue underflow: no element to remove");
    } else {
      item = a[first];
      a[first] = null;
      first = (first + 1) % a.length;
      N--;
    }
    return item;
  }
  
  public Object[] remove(int k) {
    // remove at most k elements from the beginning of the buffer and return
    // them in a Object[] of length at most k
    if (k == 0) return new Object[0];
    if(isEmpty()) {
      throw new NoSuchElementException("remove: queue underflow: no element to remove");
    } else {
      int n = N < k ? N : k;
      Object[] oa = new Object[n];
      for (int i = 0; i < n; i++) {
        // performance can be improved by eliminating the modulo operations
        oa[i] = a[(first+i) % a.length];
        a[(first+i) % a.length] = null;
      }
      first = (first + k) % a.length;
      N-=k;
      return oa;
    }
  }
    
  public T peek() {
    // return the first element of the buffer, if possible, without removing it
    // same as peek(1)
    if (isEmpty()) throw new NoSuchElementException("peek: queue underflow");
    return a[first];
  }
  
  public T peek(int k) {
    // return the kth element of the buffer, if possible, without removing it
    if (k < 1) throw new IllegalArgumentException("peek: k must be > 0");
    if (isEmpty() || N < k) throw new NoSuchElementException("peek: queue underflow");
    return a[(first+k-1) % a.length];
  }
  
  public Iterator<T> iterator() {
    return new ArrayIterator();
  }
  
  private class ArrayIterator implements Iterator<T> {
    private int i = 0;
    public boolean hasNext() { return i < size(); }

    public T next() {
      if (!hasNext()) throw new NoSuchElementException();
      T t = a[(i + first) % a.length];
      i++;
      return t;
    }
  }
  
  public Object[] toArray() {
    // return the Items in this Queue in an Object[] in LIFO order
    Object[] oa = new Object[N];
    int c = 0;
    for (T i : this) oa[c++] = i;
    return oa;
  }

  @SuppressWarnings("unchecked")
  public  T[] toArray(T...t) {
    // return a T[] containing this buffers's data in LIFO order
    if (t == null) throw new IllegalArgumentException("toArray: t must be non null");
    T[] ta = Arrays.copyOf(t, N);
    int c = 0;
    for (T i : this) ta[c++] = i;
    return ta;
  }
  
  public Object[] getArray() {
    // this is for debugging
    return a;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + Arrays.hashCode(toArray());
    result = prime * result + capacity;
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
    RingBuffer other = (RingBuffer) obj;
    if (N != other.N) {
//      System.out.println("N issue");
      return false;
    }
    if (!Arrays.equals(toArray(), other.toArray())) {
//      System.out.println("N issue");
      return false;
    }
    if (capacity != other.capacity) {
//      System.out.println("capacity issue");
      return false;
    }
    if (first != other.first) {
//      System.out.println("first issue");
      return false;
    }
    if (last != other.last) {
//      System.out.println("last issue");
      return false;
    }
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("RingBuffer(");
    if (isEmpty()) return sb.append(")").toString();
    for (T i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
}
