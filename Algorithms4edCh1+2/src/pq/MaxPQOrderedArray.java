package pq;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.NoSuchElementException;

//ex2403 p329
@SuppressWarnings("unchecked")
public class MaxPQOrderedArray<Key> {
  private Key[] z;
  private int n; // number of keys

  public MaxPQOrderedArray(int capacity) {
    if (capacity < 1) capacity = 1;
    z = (Key[]) new Object[capacity];
    n = 0;
  }

  public MaxPQOrderedArray() {
    this(1);
  }

  public MaxPQOrderedArray(Key...keys) {
    // filters out nulls
    if (keys == null || keys.length == 0) {
      z = (Key[]) new Object[1]; n = 0; 
      return;
    }
    n = keys.length;
    int m = n, j = 0;
    z = (Key[]) new Object[n]; 
    for (int i = 0; i < n; i++)
      if (keys[i] != null) z[j++] = keys[i]; else --m;
    if (m < n) { z = take(z,m); n = m; }
    Arrays.sort(z);
  }

  public boolean isEmpty() { return n == 0; }

  public int size() { return n; }
  
  private void resize(int newSize) {
    if (newSize < n) throw new IllegalArgumentException("resize: "
        + "new size must be greater than the current size");
    Key[] temp = (Key[]) new Object[newSize];
    for (int i = 0; i < n; i++) temp[i] = z[i];
    z = temp;
  }

  public Key max() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    return z[n-1];
  }

  public void insert(Key x) {
    if (x == null) return; // nulls not allowed
    if (n == z.length) resize(2 * z.length);
    for(int i = n-1; i > -1; i--) {
      if (equalOrLess(z[i],x)) { z[i+1] = x; n++; return; }
      else z[i + 1] = z[i]; 
    }
    z[0] = x; n++;
  }

  public Key delMax() {
    if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    n--; Key max = z[n];
    if (n > 0 && n == z.length/4) resize(z.length/2); 
    return max;
  }

  private boolean less(int i, int j) {
    return ((Comparable<Key>) z[i]).compareTo(z[j]) < 0;
  }
  
  private boolean equalOrLess(Key k1, Key k2) {
    return ((Comparable<Key>) k1).compareTo(k2) <= 0;
  }

  public int indexOfMax() {
    if (z.length == 1) return 0;
    int index = 0;
    for (int i = 1; i < n ; i++) if (less(index,i)) index = i;
    return index;
  }
  
  public Key[] toArray() {
    Key[] a;
    a = ofDim(z[0].getClass(),n);
    for (int i = 0; i < n ; i++) a[i] = z[i];
    return a;
  }
  
  public Object[] toEntireArray() {
    return z;
  }
  
  public void show() {
    if (n == 0) System.out.println("<nothing in pq>");
    for (int i = 0; i < n ; i++) System.out.print(z[i]+" ");
    System.out.println();
  }
  
  public void show(String pq) {
    if (n == 0) System.out.println("<nothing in "+pq+">");
    for (int i = 0; i < n ; i++) System.out.print(z[i]+" ");
    System.out.println();
  }

  public static void main(String[] args) {
    
    MaxPQOrderedArray<Integer> pq = new MaxPQOrderedArray<Integer>(3,4,6,2,1);
//    System.out.println(pq.max());
//    pa(pq.toArray());
//    pq.show();
//    System.out.println(pq.size());
    pq.show();
    pq.insert(5);
    pq.show();
    pq.insert(-1);
    pq.show();
    pq.insert(3);
    pq.show();
    pq.insert(7);
    pq.show();
    pq.insert(-2);
    pq.show();
    pq.insert(-3);
    pq.show();
    while(!pq.isEmpty()) System.out.print(pq.delMax()+" ");
    System.out.println();
    pq.show();
  }

}

