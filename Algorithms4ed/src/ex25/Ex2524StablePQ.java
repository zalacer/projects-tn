package ex25;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import pq.MaxPQ;
import v.Tuple2C;

/* p356
  2.5.24 Stable priority queue. Develop a stable priority-queue implementation 
  (which returns duplicate keys in the same order in which they were inserted).
   
 */

public class Ex2524StablePQ {
  
  public static class StableMaxPQ<T extends Comparable<? super T>> implements Iterable<T> {
    long d = Long.MAX_VALUE; // index, could switch to double if need more keys
    MaxPQ<Tuple2C<Long,T>> maxpq;
    
    public StableMaxPQ(int c) { maxpq = new MaxPQ<Tuple2C<Long,T>>(c); }
    
    public StableMaxPQ() { this(1); }
    
    @SafeVarargs
    public StableMaxPQ(T...t) {
      maxpq = new MaxPQ<Tuple2C<Long,T>>(1);
      if (t == null || t.length == 0) return;        
      for (int i = 0; i < t.length; i++) maxpq.insert(new Tuple2C<Long,T>(d--,t[i]));
    }
    
    public boolean isEmpty() { return maxpq.isEmpty(); }
    
    public int size() { return maxpq.size(); }
    
    public T max() {
      if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
      return maxpq.max()._2;
    }
    
    public void insert(T x) { maxpq.insert(new Tuple2C<Long,T>(d--,x)); }
       
    @SuppressWarnings("unchecked")
    public void bulkInsert(T...t) {
      if (t == null || t.length == 0) return;
      Tuple2C<Long,T>[] a = ofDim(Tuple2C.class, t.length);
      for (int i = 0; i < a.length; i++) a[i] = new Tuple2C<Long,T>(d--,t[i]);
      maxpq.bulkInsert(a);
    }
    
    public T delMax() { return maxpq.delMax()._2;}
    
    public Iterator<T> iterator() { return new HeapIterator(); }
    
    private class HeapIterator implements Iterator<T> {
      Iterator<Tuple2C<Long,T>> it = maxpq.iterator();
      @Override public boolean hasNext() { return it.hasNext(); }
      @Override public T next() { return it.next()._2; }     
    }
    
    public T[] toArray() {
      Tuple2C<Long,T>[] a = maxpq.toArray();
      T[] b = ofDim(a[0]._2.getClass(),a.length);
      for (int i = 0; i < a.length; i++) b[i] = a[i]._2;
      return b;
    }
    
    public void show() {
      Tuple2C<Long,T>[] a = maxpq.toArray();
      if (a.length == 0) { System.out.print("<nothing in pq>"); return; }
      for (int i = 0; i < a.length ; i++) System.out.print(a[i]._2+" ");
      System.out.println();
    }   
  }
  
  public static void main(String[] args) {
    
    StableMaxPQ<Integer> pq = new StableMaxPQ<>(1,2,3,4,5);
    pq.show(); //5 4 2 1 3 
    while (!pq.isEmpty()) System.out.print(pq.delMax()+" "); System.out.println();
    //5 4 2 1 3 
    
    Integer[] a = {500,500,500};
    int[] idha = identityHashCodes(a);
    pq = new StableMaxPQ<>(a);
    Integer[] b = pq.toArray();
    int[] idhb = identityHashCodes(b);
    assert Arrays.equals(idha,idhb);
    
    a = new Integer[]{1,1,1};  // in this case all elements have the same identityHashCode
    idha = identityHashCodes(a);
    pq = new StableMaxPQ<>(a);
    b = pq.toArray();
    idhb = identityHashCodes(b);
    assert Arrays.equals(idha,idhb);
  }

}


