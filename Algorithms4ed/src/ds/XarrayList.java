package ds;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.par;
import static v.ArrayUtils.range;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.take;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import st.SeqInterface;
import v.ArrayUtils;

// exercise 3.5.27

public class XarrayList<T> implements SeqInterface<T>, Iterable<T> {
  private static final int DEFAULTSIZE = 17;
  private T[] a = null;
  private int firstIndex = 0; // index of least recently element added to a
  private int lastIndex = 0; // index of next element to be added to a
  private int N = 0; // number of items
  private int capacity = 0;
  private Class<?> tclass = null; // class of T

  public XarrayList() { this.capacity = DEFAULTSIZE; }

  public XarrayList(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("XarrayList "
        + "constructor: int argument cannot be < 0");
    this.capacity = capacity;
  }

  public XarrayList(T[] z) {
    if (z == null) { this.capacity = DEFAULTSIZE; return; }
    tclass = z.getClass().getComponentType();
    a = z;
    lastIndex = N = capacity = z.length;
  }

  public XarrayList(XarrayList<T> x) {
    if (x == null) { this.capacity = DEFAULTSIZE; return; }
    a = x.a.clone();  firstIndex = x.firstIndex;  lastIndex = x.lastIndex;
    N = lastIndex; capacity = x.capacity; tclass = x.tclass;
  }
  
  public XarrayList(Collection<? extends T> c) {
    if (c == null) { this.capacity = DEFAULTSIZE; return; }
    for (T t : c) add(t);
  }

  public int size() { return N; }
  
  public int capacity() { return capacity; }

  public int firstIndex() { return firstIndex; }

  public int lastIndex() { return lastIndex; }
  
  public T first() { return a[(firstIndex) % a.length]; }
  
  public T last() { return a[lastIndex-1 % a.length]; }
  
  public Class<?> tclass() { return tclass; }
 
  public boolean isEmpty() { return N == 0; }

  public boolean isFull() { return N == a.length; }
  
  private void resize(int newSize) { // adjust capacity
    if (newSize < N) throw new IllegalArgumentException("resize: "
        + "newSize is less than current size");
    int m = newSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : newSize;
    @SuppressWarnings("unchecked")
    T[] temp = tclass != null ? ofDim(tclass,m) : (T[]) (new Object[m]);
    for (int i = 0; i < N; i++) temp[i] = a[(firstIndex + i) % a.length];
    a = temp;  firstIndex = 0; lastIndex  = N; capacity = m;
  }
  
  public T get(int i) {
    if (i < 0 || i > N-1) throw new IndexOutOfBoundsException();
    return a[(firstIndex + i) % a.length];
  }
  
  public T getKthFromFirst(int k) {
    // return the kth from first element in the XarrayList where zeroth from first = first
    if (k < 0) throw new IllegalArgumentException("kthFromlastIndex: k must be > -1");
    if (this.size()-1 < k) throw new NoSuchElementException("kthFromFirst element does not exist");
    return a[(firstIndex+k) % a.length];
  }

  public T getKthFromLast(int k) {
    // return the kth from last element in the XarrayList where zeroth from last = last
    if (k < 0) throw new IllegalArgumentException("kthFromlastIndex: k must be > -1");
    if (this.size()-1 < k) throw new NoSuchElementException("kthFromLast element does not exist");
    return a[(lastIndex-1-k) % a.length];
  }
  
  public void add(T t) { // Add t to the end of the list.
    if (tclass == null && t != null) tclass = t.getClass();
    if (a == null) {
      int len = capacity > 0 ? capacity : DEFAULTSIZE;
      @SuppressWarnings("unchecked")
      T[] y = tclass != null ? ofDim(tclass,len) : (T[]) (new Object[len]);
      a = y;
    }
    if (tclass != null && a.getClass().getComponentType() != tclass) {
      T[] x = ofDim(tclass, a.length);
      for (int i = 0; i < N; i++) x[i] = a[(firstIndex + i) % a.length];
      a = x; firstIndex = 0; lastIndex = N;
    }
    if (N == a.length) resize(2*a.length);  
    a[lastIndex] = t;
    lastIndex = (lastIndex + 1) % a.length; // handles wrap-around
    N++;
  }

  public void append(T t) { add(t); }
  
  public void addBack(T t) { add(t); }

  public void add(T[] z) {
    if (z == null) return;
    if (tclass == null) tclass = z.getClass().getComponentType();
    for (T t : z) add(t);
  }
  
  public void append(T[] z) { add(z); }

  public void add(int index, T t) { // inserts t at index
    if (index < 0) throw new IllegalArgumentException("add: index < 0");
    if (index > N-1) throw new IndexOutOfBoundsException("add: index > N-1");
    if (N == a.length) resize(2*a.length);
    for (int i = (lastIndex - 1) % a.length; i >= index; i--) 
      a[(i+1) % a.length] = a[i % a.length];
    a[(firstIndex + index) % a.length] = t;
    lastIndex = (lastIndex + 1) % a.length; // handles wrap-around
    N++;
  }
  
  public void prepend(T t) { add(0,t); }
  
  public void addFront(T t) { add(0,t); }
  
  public T remove() { // remove and return the firstIndex element
    if (isEmpty()) {
      System.err.println("remove: warning: XarrayList is empty");
      return null;
    }
    T t = (T) a[firstIndex];
    a[firstIndex] = null;
    firstIndex = (firstIndex + 1) % a.length; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }
  
  public T del() { return remove(); }
  
  public T delete() { return remove(); }
  
  public T deleteFront() { return remove(); }
  
  public T removeFirst() { return remove(); }
  
  public T delFirst() { return remove(); }
  
  public T remove(int i) { // remove and return the ith element
    if (isEmpty()) {
      System.err.println("remove : warning: XarrayList is empty");
      return null;
    }
    if (i < 0 || i > N-1) throw new IndexOutOfBoundsException();
    T t = (T) a[firstIndex+i];
    a[firstIndex+i] = null;
    int j = i;
    while (j < N) a[(firstIndex + j) % a.length] = a[(firstIndex + (++j)) % a.length];
    a[(firstIndex + j) % a.length] = null;
    lastIndex = (lastIndex - 1) % a.length;
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }
  
  public T del(int i) { return remove(i); }
  
  public T delete(int i) { return remove(i); }
  
  public boolean remove(T t) { // remove the first occurrence of T
    if (isEmpty()) {
      System.err.println("remove : warning: XarrayList is empty");
      return false;
    }
    int i = -1;
    while (++i < N) 
      if (a[(firstIndex + i) % a.length].equals(t)) { delete(i); return true; }
    return false;
  }
  
  public boolean del(T t) { return remove(t); }
  
  public void delete(T t) {
    // this method exists to conform to SeqInterface
    remove(t); 
  }
  
  public boolean removeAll(T t) { // remove all occurrences of T
    if (isEmpty()) {
      System.err.println("remove : warning: XarrayList is empty");
      return false;
    }
    int i = -1; boolean found = false;
    while (++i < N) 
      if (a[(firstIndex + i) % a.length].equals(t)) { delete(i); found = true; }
    if (found) return true;
    return false;
  }
  
  public boolean delAll(T t) { return removeAll(t); }
  
  public boolean deleteAll(T t) { return removeAll(t); }
  
  public T removeLast() { // remove and return the lastIndex-1 element
    if (isEmpty()) {
      System.err.println("warning: Seq is empty");
      return null;
    }
    T t = (T) a[lastIndex-1];
    a[lastIndex-1] = null;
    lastIndex = (lastIndex - 1) % a.length; // handles wrap-around
    N--;
    if (N > 0 && N == a.length/4) resize(a.length/2); 
    return t;
  }
  
  public T delLast() { return removeLast(); }
  
  public T deleteBack() { return removeLast(); }
  
  public boolean contains(T t) {
    if (isEmpty()) return false;
    int i = -1;
    while (++i < N) {
      T u = a[(firstIndex + i) % a.length];
      if (u == t || u != null && u.equals(t)) return true;
    }
    return false;
  }
  
  @SuppressWarnings("unchecked")
  public void clear() {
    a = tclass != null ? ofDim(tclass, DEFAULTSIZE) : (T[]) new Object[DEFAULTSIZE];
    firstIndex = lastIndex = N = capacity = 0;    
  }
  
  public XarrayList<T> clone() { return new XarrayList<T>(this); }
  
  public int indexOf(Object o) {
    // return the index of the first occurrence of o if possible else return -1
    if (isEmpty()) return -1;
    if (tclass != null && o != null && tclass.isAssignableFrom(o.getClass())) return -1; 
    int i = -1;
    while (++i < N) {
      T t = a[(firstIndex + i) % a.length];
      if (t == o || t != null && t.equals(o)) return i;
    }
    return -1;
  }
  
  public int[] indices(T t) {
    if (isEmpty()) return new int[0];
    int[] z = new int[N]; int i = -1, c = 0;
    while (++i < N) {
      T u = a[(firstIndex + i) % a.length];
      if (u == t || u != null && u.equals(t)) z[c++] = i;
    }
    if (c == 0) return new int[0];
    return take(z,c);   
  }
  
  public int[] indices() { return range(0,N); }

  public T[] toArray() {
    @SuppressWarnings("unchecked")
    T[] ta = tclass != null ? ofDim(tclass,N) : (T[]) (new Object[N]);
    for (int i = 0; i < N; i++) ta[i] = a[(firstIndex + i) % a.length];
    return ta;
  }
  
  public List<T> toList() { return Arrays.asList(toArray()); }
  
  public Collection<T> toCollection() { return (Collection<T>) Arrays.asList(toArray()); }
  
  public Iterator<T> iterator() { return ArrayUtils.iterator(toArray()); }
  
  public Iterable<T> iterable = () -> iterator();
  
  public Spliterator<T> spliterator() { return iterable.spliterator(); }
  
  public Stream<T> stream() { return StreamSupport.stream(spliterator(), false); }
  
  public Object[] getArray() { return a.clone(); }
  
  public void show() { for (T t : this) System.out.print(t+" "); System.out.println(); }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + Arrays.hashCode(a);
    result = prime * result + firstIndex;
    result = prime * result + lastIndex;
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
    XarrayList other = (XarrayList) obj;
    if (N != other.N)
      return false;
    if (!Arrays.equals(a, other.a))
      return false;
    if (firstIndex != other.firstIndex)
      return false;
    if (lastIndex != other.lastIndex)
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    if (isEmpty()) return sb.append(")").toString();
    for (T t : this) sb.append(""+t+",");
    return sb.substring(0, sb.length()-1)+")";
  }


  public static void main(String[] args) {
    
    Integer[] g = rangeInteger(0,6);
    XarrayList<Integer> x = new XarrayList<>(g);
    System.out.println("size="+x.size());
    par(x.toArray());
    par(x.getArray());
    System.out.println(x);
    System.out.println(x.first());
    System.out.println(x.last());
    par(x.getArray());
    System.out.println("remove="+x.remove());
    System.out.println("size="+x.size());
    par(x.toArray());
    par(x.getArray());
    System.out.println("first="+x.first());
    System.out.println("last="+x.last());
    System.out.println("firstIndex="+x.firstIndex());
    System.out.println("add(3,9)"); x.add(3,9);
    System.out.println("size="+x.size());
    par(x.toArray());
    par(x.getArray());
    System.out.println("get(0)="+x.get(0));
    System.out.println("get(3)="+x.get(3));
    System.out.println("add(new Integer[]{6,7,8}"); x.add(new Integer[]{6,7,8});
    par(x.toArray());
    par(x.getArray());
    System.out.println("add(10)"); x.add(10);
    par(x.toArray());
    par(x.getArray());
    System.out.println("getKthFromFirst(0)="+x.getKthFromFirst(0));
    System.out.println("getKthFromLast(0)="+x.getKthFromLast(0));
    System.out.println("getKthFromFirst(x.size()-1)="+x.getKthFromFirst(x.size()-1));
    System.out.println("getKthFromLast(x.size()-1)="+x.getKthFromLast(x.size()-1));
//    System.out.println("getKthFromFirst(x.size())="+x.getKthFromFirst(x.size()));
//    System.out.println("getKthFromLast(x.size())="+x.getKthFromLast(x.size()));
    System.out.println("prepend(11)"); x.prepend(11);
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    System.out.println("size="+x.size());
    System.out.println("removeFirst="+x.removeFirst());
    System.out.println("size="+x.size());
    System.out.println("removeLast="+x.removeLast());
    System.out.println("size="+x.size());
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    System.out.println("firstIndex="+x.firstIndex());
    System.out.println("lastIndex="+x.lastIndex());
    System.out.println("x.remove(5)"); x.remove(5);
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    System.out.println("firstIndex="+x.firstIndex());
    System.out.println("lastIndex="+x.lastIndex());
    System.out.println("prepend(3)"); x.prepend(3);
    System.out.println("append(3)"); x.append(3);
    System.out.println("x.indices(3)"); par(x.indices(3));
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    Integer[] ar = x.toArray();
    for (Integer i : ar) System.out.println("x.contains("+i+")="+x.contains(i));
    System.out.println("x.contains(20)="+x.contains(20));
    pa(x.toArray());
    System.out.println("x.deleteAll(3)="+x.deleteAll(3));
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    System.out.println("contains(null)="+x.contains(null));
    System.out.println("add(3,null)"); x.add(3,null);
    System.out.println("contains(null)="+x.contains(null));
    System.out.println("indexOf(null)="+x.indexOf(null));
    par(x.toArray());
    par(x.getArray());
    par(x.indices());
    
  }

}