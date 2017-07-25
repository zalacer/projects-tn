package ex31;

import java.util.Arrays;
import ds.Queue;

/* p389
  3.1.2  Develop a symbol-table implementation ArrayST that uses 
  an (unordered) array as the underlying data structure to implement 
  our basic symbol-table API.
  
  This is already implemented at http://algs4.cs.princeton.edu/31elementary/ArrayST.java
  that's available in this project at st.ArrayST.
  
  from p363: API for a generic basic symbol table: 
  
  public class ST<Key, Value>
  ST()  create a symbol table
  void put(Key key, Value val)  put key-value pair into the table
                                (remove  key from table if value is  null )
  Value get(Key key)  value paired with  key (null if key is absent)
  void delete(Key key)  remove  key (and its value) from table
  boolean contains(Key key)  is there a value paired with  key ?
  boolean isEmpty()  is the table empty?
  int size()  number of key-value pairs in the table
  Iterable<Key> keys()  all the keys in the table
  
 */

public class Ex3102ImplementUnorderedArrayST {
  
  public static interface UnorderedST<K,V> { 
    // not naming this ST to avoid conflict with existing classes.
    // this is also implemented in package st.
    public void put(K k, V v);
    public V get(K k);
    public void delete(K k);
    public boolean contains(K k);
    public boolean isEmpty();
    public int size();
    public Iterable<K> keys();  
  }
  
  public static abstract class UnorderedSTAbstractST<K,V> implements UnorderedST<K,V> {
    // this is implemented to ensure existence of empty constructor 
    // even if non-empty constructors are defined later.
    // this is also implemented in package st.
    UnorderedSTAbstractST(){}
  }
  
  @SuppressWarnings("unchecked")
  public static class ArrayST<K,V> extends UnorderedSTAbstractST<K,V> {
    // most methods taken from http://algs4.cs.princeton.edu/31elementary/ArrayST.java.html
    // this is also implemented as st.UnorderedArraySt.
    private static final int INIT_SIZE = 8;
    private int n = 0; // length of keys and values
    private K[] keys;
    private V[] vals;
    
    ArrayST() {
      keys = (K[]) new Object[INIT_SIZE];
      vals = (V[]) new Object[INIT_SIZE];
    }

    @Override
    public void put(K k, V v) {
      // append k to keys and v to vals
      delete(k); // eliminate possible duplicates.
      if (n >= vals.length) resize(2*n);
      vals[n] = v;
      keys[n] = k;
      n++;
    }

    @Override
    public V get(K k) {
      // if k is in keys return the V with the same index in vals.
      for (int i = 0; i < n; i++)
        if (keys[i].equals(k)) return vals[i];
      return null;
    }

    @Override
    public void delete(K k) {
      // remove k from keys and get(k) from vals.
      for (int i = 0; i < n; i++) {
        if (k.equals(keys[i])) {
            keys[i] = keys[n-1];
            vals[i] = vals[n-1];
            keys[n-1] = null;
            vals[n-1] = null;
            n--;
            if (n > 0 && n == keys.length/4) resize(keys.length/2);
            return;
        }
      }
    }

    @Override
    public boolean contains(K k) {
      // return true if k is int keys else false.
      for (int i = 0; i < n; i++)
        if (keys[i].equals(k)) return true;
      return false;
    }

    @Override
    public boolean isEmpty() {
      return n == 0;
    }

    @Override
    public int size() {
      // return the number of k-v pairs in this.
      return n;
    }

    @Override
    public Iterable<K> keys() {
      // return something that has an Iterator<K>.
      // using Queue because it has an array constructor.
      return new Queue<K>(keys);
    }
    
    private void resize(int capacity) {
      // resize keys and vals to capacity
        K[] tempk = (K[]) new Object[capacity];
        V[] tempv = (V[]) new Object[capacity];
        for (int i = 0; i < n; i++) tempk[i] = keys[i];
        for (int i = 0; i < n; i++) tempv[i] = vals[i];
        keys = tempk; vals = tempv;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      for (int i = 0; i < n-1; i++) sb.append(keys[i]+"="+vals[i]+",");
      sb.append(keys[n-1]+"="+vals[n-1]+"}");
      return sb.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(keys);
      result = prime * result + n;
      result = prime * result + Arrays.hashCode(vals);
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
      ArrayST other = (ArrayST) obj;
      if (!Arrays.equals(keys, other.keys))
        return false;
      if (n != other.n)
        return false;
      if (!Arrays.equals(vals, other.vals))
        return false;
      return true;
    }
  }

    public static void main(String[] args) {
      
 
  }
  
}
