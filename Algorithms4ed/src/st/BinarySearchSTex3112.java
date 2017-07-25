package st;

import static v.ArrayUtils.*;

import java.util.NoSuchElementException;
import java.util.Objects;

import ds.Queue;
import edu.princeton.cs.algs4.StdIn;
import sort.Merges;
import v.Tuple2K;

@SuppressWarnings("unused")
public class BinarySearchSTex3112<K extends Comparable<? super K>,V> {
    private static final int INIT_CAPACITY = 2;
    private Tuple2K<K,V>[] items;
    private int n = 0;
    private int compares = 0;

    public BinarySearchSTex3112() { this(INIT_CAPACITY); }

    public BinarySearchSTex3112(int capacity) { 
      items = ofDim(Tuple2K.class, capacity);
    }

    public BinarySearchSTex3112(Tuple2K<K,V>[] z) {
      if (z == null || z.length == 0) return;
      int n = z.length; int c = 0; 
      // weed out nulls
      Tuple2K<K,V>[] x = ofDim(Tuple2K.class, n);
      for (int i = 0; i < n; i++)
        if (Objects.nonNull(z[i]) && z[i]._1 != null && z[i]._2 != null) x[c++] = z[i];
      x = take(x,c);
      n = x.length;
      if (n == 0) { items = ofDim(Tuple2K.class, INIT_CAPACITY); return; }
      items = ofDim(Tuple2K.class, 2*n);
      Merges.topDownAcCoSm(x,31); // does this help performance?
      for (int  i = 0; i < n; i++) put(x[i]);
    }
    
    public BinarySearchSTex3112(K[] ka, V[] va) {
      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
      int n = Math.min(ka.length, va.length); int c = 0; 
      K[] kz = ofDim(ka.getClass().getComponentType(), n);
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (ka[i] != null) { kz[c++] = ka[i]; }
      }
      if (c == 0) { items = ofDim(Tuple2K.class, INIT_CAPACITY); return; }
      if (c < n) { n = c; kz = take(kz,c); }
      V[] vz = ofDim(va.getClass().getComponentType(), n); c = 0;
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (va[i] != null) { vz[c++] = va[i]; }
      }
      if (c == 0) { items = ofDim(Tuple2K.class, INIT_CAPACITY); return; }
      if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
      Tuple2K<K,V>[] x = ofDim(Tuple2K.class, n);
      for (int  i = 0; i < n; i++) x[i] = new Tuple2K<K,V>(kz[i], vz[i]);
      par(x);
      items = ofDim(Tuple2K.class, 2*n);
      for (int  i = 0; i < n; i++) put(x[i]);
    }

    private void resize(int capacity) {
        assert capacity >= n;
        Tuple2K<K,V>[] tmp = ofDim(Tuple2K.class, capacity);
        for (int i = 0; i < n; i++) tmp[i] = items[i];
        items = tmp;
    }

    public int size() { return n; }

    public boolean isEmpty() { return size() == 0; }

    public boolean contains(K k) {
        if (k == null) throw new NullPointerException();
        return get(k) != null;
    }

    public V get(K k) {
        if (k == null) throw new NullPointerException(); 
        if (isEmpty()) return null;
        Tuple2K<K,V> x = new Tuple2K<K,V>(k,null);
        int i = rank(k); 
        if (i < n && items[i].compareTo(x) == 0) return items[i]._2;
        return null;
    } 

    public int rank(K k) {
        if (k == null) throw new NullPointerException(); 
        int lo = 0, hi = n-1; 
        Tuple2K<K,V> x = new Tuple2K<K,V>(k,null);
        while (lo <= hi) { 
            int mid = lo + (hi - lo) / 2; 
            int cmp = x.compareTo(items[mid]);
            if      (cmp < 0) hi = mid - 1; 
            else if (cmp > 0) lo = mid + 1; 
            else return mid; 
        } 
        return lo;
    } 

    public void put(Tuple2K<K,V> t)  {
        if (t == null || t._1 == null) throw new NullPointerException();
        if (t._2 == null) { delete(t._1); return; }
        K k = t._1;
        int i = rank(k);
        if (i < n && items[i].compareTo(t) == 0) { compares++; items[i]._2 = t._2; return; }
        compares++;
        if (n == items.length) resize(2*items.length);
        for (int j = n; j > i; j--) items[j] = items[j-1];
        items[i] = t; n++;
        assert check();
    }
    
    public void put(K k, V v)  {
      if (k == null) throw new NullPointerException(); 
      if (v == null) { delete(k); return; }
      Tuple2K<K,V> x = new Tuple2K<K,V>(k,v);
      put(x);
    }
    
    public void delete(K k) {
        if (k == null) throw new NullPointerException(); 
        if (isEmpty()) return;
        int i = rank(k);
        Tuple2K<K,V> x = new Tuple2K<K,V>(k,null);
        if (i == n || items[i].compareTo(x) != 0) { compares++; return; }
        compares++;
        for (int j = i; j < n-1; j++) items[j] = items[j+1];
        n--; items[n] = null;
        if (n > 0 && n == items.length/4) resize(items.length/2);
        assert check();
    } 

    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException();
        delete(min());
    }
    
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException();
        delete(max());
    }
    
    public K min() { if (isEmpty()) return null; return items[0]._1; }
    
    public K max() { if (isEmpty()) return null; return items[n-1]._1; }

    public K select(int k) { if (k<0 || k>= n) return null; return items[k]._1; }

    public K floor(K k) {
        if (k == null) throw new NullPointerException(); 
        int i = rank(k);
        Tuple2K<K,V> x = new Tuple2K<K,V>(k,null);
        if (i < n && x.compareTo(items[i]) == 0) return items[i]._1;
        if (i == 0) return null;
        else return items[i-1]._1;
    }

    public K ceiling(K k) {
        if (k == null) throw new NullPointerException(); 
        int i = rank(k);
        if (i == n) return null; 
        else return items[i]._1;
    }

    public int size(K lo, K hi) {
        if (lo == null) throw new NullPointerException(); 
        if (hi == null) throw new NullPointerException();
        Tuple2K<K,V> x = new Tuple2K<K,V>(lo,null);
        Tuple2K<K,V> y = new Tuple2K<K,V>(hi,null);
        if (x.compareTo(y) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else              return rank(hi) - rank(lo);
    }

    public Iterable<K> keys() { return keys(min(), max()); }

    public Iterable<K> keys(K lo, K hi) {
        if (lo == null) throw new NullPointerException(); 
        if (hi == null) throw new NullPointerException(); 
        Queue<K> queue = new Queue<K>();
        Tuple2K<K,V> x = new Tuple2K<K,V>(lo,null);
        Tuple2K<K,V> y = new Tuple2K<K,V>(hi,null);
        if (x.compareTo(y) > 0) return queue;
        for (int i = rank(lo); i < rank(hi); i++) 
            queue.enqueue(items[i]._1);
        if (contains(hi)) queue.enqueue(items[rank(hi)]._1);
        return queue; 
    }

    private boolean check() { return isSorted() && rankCheck(); }

    private boolean isSorted() {
        for (int i = 1; i < size(); i++)
            if (items[i].compareTo(items[i-1]) < 0) return false;
        return true;
    }
 
    private boolean rankCheck() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (int i = 0; i < size(); i++)
            if (items[i]._1.compareTo(select(rank(items[i]._1))) != 0) return false;
        return true;
    }    
    
    @Override
    public String toString() {
      if (n == 0) return "{}";
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      for (int i = 0; i < n; i++) sb.append(items[i]._1+":"+items[i]._2+",");
      return sb.substring(0,sb.length()-1)+"}";      
    }
    
//    public String trace(Key k, Value v) {
//      // designed for ex3111 to trace E A S Y Q U E S T I O N : indices                             
//      StringBuilder sb = new StringBuilder(); 
//      StringBuilder sb2 = new StringBuilder();
//      sb.append(k); 
//      if (len(v)==1) sb.append(sp(6)); else sb.append(sp(5));
//      sb.append(v); 
//      sb.append(sp(4));
//      for (int i = 0; i < n; i++) {
//        sb2.append(keys[i]);
//        if ((i+1)<n && len(keys[i+1])==2) sb2.append(sp(1));
//        else sb2.append(sp(2));
//      }
//      String st2 = sb2.toString();
//      sb.append(st2); 
//      if (len(n)==1) sb.append(sp(31-len(st2))+n+sp(3));
//      else sb.append(sp(30-len(st2))+n+sp(3));
//      for (int i = 0; i < n; i++) {
//        sb.append(vals[i]);
//        if ((i+1)<n && len(vals[i+1])==2) sb.append(sp(1));
//        else sb.append(sp(2));
//      }
//      return sb.toString();
//    }
//    
//    public int len(Object x) { return (""+x).length(); }
      
    public int getCompares() { return compares; }

    public static void main(String[] args) {
          
      String[] keys = "E A S Y Q U E S T I O N".split("\\s+");
      Integer[] values = rangeInteger(0, keys.length);
      BinarySearchSTex3112<String, Integer> st = new BinarySearchSTex3112<>();
      for (int  i = 0; i < keys.length; i++) st.put(keys[i], values[i]);
      System.out.println(st);
      System.out.println(st.size()+"\n");

      st = new BinarySearchSTex3112<>(keys,values);
      System.out.println(st);
      System.out.println(st.size()+"\n");
      
      Tuple2K<String,Integer>[] t = ofDim(Tuple2K.class, keys.length);
      for (int i = 0; i < t.length; i++) 
        t[i] = new Tuple2K<String,Integer>(keys[i],values[i]);
      st = new BinarySearchSTex3112<>(t);
      System.out.println(st);
      System.out.println(st.size());
      System.out.println("compares = "+st.getCompares()+"\n");
      // 12 with and without mergesort, 

      
//        BinarySearchSTex3111<String, Integer> st = new BinarySearchSTex3111<String, Integer>();
//        for (int i = 0; !StdIn.isEmpty(); i++) {
//            String key = StdIn.readString();
//            st.put(key, i);
//        }
//        for (String s : st.keys())
//            System.out.println(s + " " + st.get(s));
    }
}
