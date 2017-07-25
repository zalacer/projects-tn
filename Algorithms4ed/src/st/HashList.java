package st;

import static com.google.common.hash.Hashing.combineOrdered;
import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import analysis.Timer;
import ds.Queue;
import v.Tuple2;

@SuppressWarnings("unused")
public class HashList<T> implements SeqInterface<T>, Iterable<T>, Cloneable {
  private RedBlackBSTD<T> rb;
  private SeparateChainingHashSTSETD<T> st;
//  private double p; // current index >= 0 of integral value
//  private double n; // current index < 0 of integral value
  private int size;

  public HashList() {
    rb = new RedBlackBSTD<T>();
    st = new SeparateChainingHashSTSETD<>();
    size = 0;
  }
  
  public HashList(T[] a) {
    rb = new RedBlackBSTD<T>();
    st = new SeparateChainingHashSTSETD<>();
    size = 0;
    if (a == null) return;
    for (T t : a)  add(t);
  }
  
  public HashList(HashList<? extends T> h) {
    rb = new RedBlackBSTD<T>();
    st = new SeparateChainingHashSTSETD<>();
    size = 0;
    for (T t : h) add(t);
  }
  
  public HashList(Collection<? extends T> c) {
    rb = new RedBlackBSTD<T>();
    st = new SeparateChainingHashSTSETD<>();
    size = 0;
    for (T t : c) add(t);
  }

  @Override 
  public void add(T t) { 
    // add t at the end
    double p = rb.isEmpty() ? 0. : ceil(rb.max()) + 1;
    rb.put(p, t); st.put(t, p); size++; }

  @Override
  public void addBack(T t) { add(t); }

  @Override
  public void addFront(T t) {
    // add t at index 0 shifting the indices of existing elements up by 1
    double p = rb.isEmpty() ? 0. : floor(rb.min()) - 1;
    rb.put(p, t); st.put(t, p); size++; 
  }

  @Override
  public void add(int i, T t) {
    // add t at index i shifting the indices of existing elements up by 1 starting at i
    if (i < 0 || i > size) throw new IndexOutOfBoundsException();
    if (i == 0) {
      if (isEmpty()) { add(t); return; }
      double p = floor(rb.select(0)) - 1;
      rb.put(p, t); st.put(t, p); size++; 
    }
    if (i == size) { add(t); return; }
    double lo = rb.select(i-1);
    double hi = rb.select(i);
    double mid = lo + (hi-lo)/2;
    rb.put(mid, t); st.put(t, mid); size++;
    double mid2 = lo + (mid-lo)/2;
    if (lo == mid2 || mid2 == mid) { relable(); return; }
    double mid3 = mid + (hi-mid)/2;
    if (mid == mid3 || mid3 == hi) relable();
  }

  @Override
  public void delete(T t) {
    // delete first occurrence of element t
    if (size == 0) return;
    RedBlackSETD rbs = st.get(t);
    if (rbs == null || rbs.isEmpty()) return;
    Double min = rbs.min();
    rb.delete(min);
    rbs.delete(min);
    if (rbs.isEmpty()) st.delete(t);
    size--;
  }

  @Override
  public T delete(int i) {
    // delete element with index i
    if (size == 0 || i < 0 || i > size) throw new IndexOutOfBoundsException();
    double d = rb.select(i);
    T t = rb.get(d);
    rb.delete(d);  
    RedBlackSETD rbs = st.get(t);
    rbs.delete(d);
    if (rbs.isEmpty()) st.delete(t);
    size--;
    return t;
  }

  @Override
  public T deleteFront() { return delete(0); }

  @Override
  public T deleteBack() { return delete(size-1); }

  @Override
  public boolean contains(T t) { 
    if (size == 0) return false;
    return st.contains(t); }

  @Override
  public boolean isEmpty() { return size == 0; }

  @Override
  public int size() { return size; }

  @Override
  public Iterator<T> iterator() {  return rb.valerator(); }

  // synonyms
  public void append(T t) { add(t); }

  public void prepend(T t) { addFront(t); }

  public void remove(T t) { delete(t); }

  public T remove(int i) { return delete(i); }

  public T removeFront() { return deleteFront(); }

  public T removeBack() { return deleteBack(); }

  // maintenance
  public void relable() {
    // relable indices to start at 0. and incremented by 1
    if (size == 0) return;
    rb.relable(0.);
    SeparateChainingHashSTSETD<T> tmp = new SeparateChainingHashSTSETD<>();
    for (Tuple2<Double,T> t : rb.entries()) tmp.put(t._2, t._1);
    st = tmp; 
  }

  // extra 
  public void addAll(HashList<? extends T> h) { 
    if (h == null || h.size == 0) return;
    for (T t : h) add(t); }
  
  public void addAll(Collection<? extends T> c) { 
    if (c == null || c.size() == 0) return;
    for (T t : c) add(t); }
  
  public void addAll(int index, HashList<? extends T> h) {
    if (h == null || h.size == 0) return;
    if (index < 0 || index > size) throw new IndexOutOfBoundsException();
    for (T t : h) add(index++, t); }
  
  public void addAll(int index, Collection<? extends T> c) { 
    if (c == null || c.size() == 0) return;
    if (index < 0 || index > size) throw new IndexOutOfBoundsException();
    for (T t : c) add(index++, t); }
  
  public void clear() {
    rb = new RedBlackBSTD<T>();
    st = new SeparateChainingHashSTSETD<>();
    size = 0;
  }
  
  @Override
  public HashList<T> clone() { return new HashList<T>(this); }
  
  @SuppressWarnings("unchecked")
  public int indexOf(Object o) {
    // return the index of the first occurrence of o if possible else return -1
    if (size == 0) return -1;
    Class<?> vclass = rb.getVclass();
    if (vclass != null && !vclass.isAssignableFrom(o.getClass()) 
        || !contains((T)o)) return -1; 
    return rb.rank(st.get((T)o).min());
  }
  
  public int[] indices(T t) {
    // return an int[] containing the indices of t
    if (contains(t)) {
      Double[] a = st.get(t).toArray();
      int[] b = new int[a.length];
      for (int i = 0; i < a.length; i++) b[i] = rb.rank(a[i]);
      return b;
    }
    return new int[0];  
  }
  
  public int[] indices() { 
    if (size == 0) return new int[0];
    return range(rb.rank(rb.min()),rb.rank(rb.max())+1); }
  
  @SuppressWarnings("unchecked")
  public int lastIndexOf(Object o) {
    // return the index of the last occurrence of o if possible else return -1
    if (size == 0) return -1;
    Class<?> vclass = rb.getVclass();
    if (vclass != null && !vclass.isAssignableFrom(o.getClass()) 
        || !contains((T)o)) return -1;
    System.out.println(st.get((T)o));
    return rb.rank(st.get((T)o).max());
  }
  
  public void removeAll(T t) { while (contains(t)) delete(t); }
  
  public void deleteAll(T t) { removeAll(t); }
  
  public void removeAll(HashList<? extends T> h) { 
    if (size == 0 || h == null || h.size == 0) return;
    for (T t : h) delete(t); }
  
  public void deleteAll(HashList<? extends T> h) { removeAll(h); }
  
  public void removeAll(Collection<? extends T> c) {
    if (size == 0 || c == null || c.size() == 0) return;
    for (T t : c) delete(t); }
  
  public void deleteAll(Collection<? extends T> c) { removeAll(c); }
  
  public void removeIf(Predicate<? super T> filter) { 
    if (size == 0) return;
    if (filter == null) throw new IllegalArgumentException();
    for (T t : this) if (filter.test(t)) removeAll(t); }
  
  public void deleteIf(Predicate<? super T> filter) { removeIf(filter); }
  
  public void removeRange(int from, int to) {
    if (size == 0) return;
    int f = from < 0 ? 0 : from;
    int t = to > size-1 ? size-1 : to;
    for (int i = f; i <= t; i++) delete(i);    
  }
  
  public void deleteRange(int from, int to) { removeRange(from, to); }
  
  public T replace(int index, T e) {
    // replace the element x at index with e and return x.
    if (size == 0 || index < 0 || index > size) throw new IndexOutOfBoundsException();
    T x = delete(index);
    add(index, e);
    return x;
  }
  
  public void replaceAll(UnaryOperator<T> operator) {
    if (size == 0) return;
    if (operator == null) throw new IllegalArgumentException();
    Double[] da; Tuple2<Integer,T>[] ta = ofDim(Tuple2.class, size); int c = 0;
    for (T t : st) {
      da = st.get(t).toArray();
      T u = operator.apply(t);
      for (int i = 0; i < da.length; i++) 
        ta[c++] = new Tuple2<Integer,T>(rb.rank(da[i]),u);
    }
    Comparator<Tuple2<Integer,T>> comp = (t1,t2) -> { return t1._1 - t2._1; };
    Arrays.sort(ta,comp);
    HashList<T> h = new HashList<>();
    for (Tuple2<Integer,T> t : ta) h.add(t._2);
    rb = h.rb; st = h.st; size = h.size; 
  }
  
  public void retainAll(Collection<? extends T> c) {
    if (size == 0) return;
    if (c == null || c.size() == 0) clear();
    for(T t : this) if (!c.contains(t)) removeAll(t);     
  }
  
  public void sort(Comparator<? super T> c) {
    HashList<T> h = new HashList<>(); 
    T[] a = toArray();
    Arrays.sort(a,c);
    for (T t : a) h.add(t);
    rb = h.rb; st = h.st; size = h.size;
  }
  
  public Spliterator<T> spliterator() { return rb.values().spliterator(); }
  
  public Stream<T> stream() { return StreamSupport.stream(spliterator(), false); }
  
  public HashList<T> subList(int from, int to) {
    if (size == 0) return new HashList<T>();
    int f = from < 0 ? 0 : from;
    int t = to > size-1 ? size-1 : to;
    return new HashList<T>(rb.toValArray(f, t));   
  }

  public Double[] toDoubleIndexArray() { return rb.toKeyArray(); }

  public Tuple2<Double,T>[] toEntryArray() { return rb.toEntryArray(); }

  public T[] toArray() { return rb.toValArray(); }

  public void show() {
    Iterable<T> it = rb.values();
    for (T t : it) System.out.print(t+" "); System.out.println();
  }
  
  @Override
  public String toString() { return arrayToString(rb.toValArray(),85,1,1); }
  
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    HashList other = (HashList) obj;
    if (size != other.size) return false;
    if (!Arrays.deepEquals(toArray(), other.toArray())) return false;
    return true;
  }
  
  @Override
  public int hashCode() {
    T[] a = toArray();
    Queue<HashCode> q = new Queue<>();
    HashFunction hf = Hashing.murmur3_32();
    final int nullHash = 1763263686; // also used in SeparateChainingHashSTSETD.hash
    for (T t : a) {
      int hc = t == null ? nullHash : t.hashCode();
      q.enqueue(hf.newHasher().putInt(hc).hash());
    }
    return combineOrdered(q).asInt() & 0x7fffffff;
  }
  
  public static void main(String[] args) {
    
    Double[] d = rangeDouble(0.,2000.);
    Integer[] ia = rangeInteger(0,2000);
    Timer t = new Timer(); // 1000:450ms; 2000:1350; 3000:2830 5000:7820; 10000:31090
//    HashList<Double> l2 = new HashList<>(d);
//    ArrayList<Double> a1 = new ArrayList<>(); ////3000:10  10000:0
//  for (int i = 0; i < d.length; i++) a1.add(d[i]); //3000:10  
//    SeparateChainingHashSTSETD<Double> st = new SeparateChainingHashSTSETD<>(1600);
//    double c = 0;
//    for (int i = 0; i < d.length; i++) st.put(d[i],c++); //2000:90-100 10000:110-120
    RedBlackBSTD<Integer> rb = new RedBlackBSTD<>(d,ia);
    System.out.println(t.elapsed());
//    System.exit(0);

    HashList<Double> l = new HashList<>();
    l.add(1.); l.add(2.); l.add(3.); l.add(null); l.add(5.);
    //    par(l.toArray());
    //    par(l.indices());
    //    par(l.toIndexArray());
    //    par(l.toEntryArray());
    l.prepend(0.); l.prepend(-1.); 
    //    par(l.toArray());
    l.add(3,6.); l.add(4,7.); l.add(5,8.);  //l.add(9.);
    l.relable(); l.add(7.); l.add(1,7.); 
    par(l.toArray());
    par(l.toEntryArray());
    par(l.indices(7.));
    l.show();
    System.out.println(l);
    System.out.println(l.indexOf(7.));
    System.out.println(l.lastIndexOf(7.));
    l.replaceAll((x) -> { return x == null ? null : x+1; });
    System.out.println(l);
    l.add(12.);
    System.out.println(l);
    l.add(null);
    System.out.println(l);
    HashList<Double> l3 = new HashList<>(l);
    System.out.println(l3);
    assert l.equals(l3);
    assert l.hashCode() == l3.hashCode();
//    l3.add(5.);
    System.out.println(l3);
    System.out.println(l.hashCode());
    System.out.println(l3.hashCode());
    
    
    


    //    ArrayList<Double> list = new ArrayList<>(10);
    //    list.add(0.); list.add(1.); list.add(2.); list.add(3.); list.add(6.);
    //    System.out.println(list);
    //    list.add(4,4.);
    //    System.out.println(list);
    //    System.out.println(list.size());
    //    list.add(5,5.);
    //    System.out.println(list);
    //    list.add(null); list.add(Double.POSITIVE_INFINITY); list.add(Double.NaN); 
    //    System.out.println(list);
    //    
    //    System.out.println((new Double(Double.POSITIVE_INFINITY)).hashCode());
    //    System.out.println(Integer.MAX_VALUE);

  }



}
