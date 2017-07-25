package st;

import static v.ArrayUtils.*;

import java.util.NoSuchElementException;

import ds.Queue;
import edu.princeton.cs.algs4.StdIn;

@SuppressWarnings({ "unchecked", "unused" })
public class BinarySearchSTex3111<Key extends Comparable<Key>, Value> {
    private static final int INIT_CAPACITY = 2;
    private Key[] keys;
    private Value[] vals;
    private int n = 0;
    private int compares = 0;

    public BinarySearchSTex3111() { this(INIT_CAPACITY); }

    public BinarySearchSTex3111(int capacity) { 
        keys = (Key[]) new Comparable[capacity]; 
        vals = (Value[]) new Object[capacity]; 
    }
    
    public BinarySearchSTex3111(Key[] ka, Value[] va) {
      if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
      int n = Math.min(ka.length, va.length); int c = 0; 
      Key[] kz = ofDim(ka.getClass().getComponentType(), n);
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (ka[i] != null) { kz[c++] = ka[i]; }
      }
      if (c == 0) {
        keys = (Key[]) new Comparable[INIT_CAPACITY]; 
        vals = (Value[]) new Object[INIT_CAPACITY]; 
        return;
      }
      if (c < n) { n = c; kz = take(kz,c); }
      Value[] vz = ofDim(va.getClass().getComponentType(), n); c = 0;
      for (int  i = 0; i < n; i++) {
        if (c == n) break;
        if (va[i] != null) { vz[c++] = va[i]; }
      }
      if (c == 0) {
        keys = (Key[]) new Comparable[INIT_CAPACITY]; 
        vals = (Value[]) new Object[INIT_CAPACITY]; 
        return;
      }
      if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
      keys = (Key[]) new Comparable[2*n];
      vals = (Value[]) new Object[2*n]; 
      for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
    }

    private void resize(int capacity) {
        assert capacity >= n;
        Key[]   tempk = (Key[])   new Comparable[capacity];
        Value[] tempv = (Value[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            tempk[i] = keys[i];
            tempv[i] = vals[i];
        }
        vals = tempv;
        keys = tempk;
    }

    public int size() { return n; }

    public boolean isEmpty() { return size() == 0; }

    public boolean contains(Key key) {
        if (key == null) throw new NullPointerException();
        return get(key) != null;
    }

    public Value get(Key key) {
        if (key == null) throw new NullPointerException(); 
        if (isEmpty()) return null;
        int i = rank(key); 
        if (i < n && keys[i].compareTo(key) == 0) return vals[i];
        return null;
    } 

    public int rank(Key key) {
        if (key == null) throw new NullPointerException(); 
        int lo = 0, hi = n-1; 
        while (lo <= hi) { 
            int mid = lo + (hi - lo) / 2; 
            int cmp = key.compareTo(keys[mid]);
            if      (cmp < 0) hi = mid - 1; 
            else if (cmp > 0) lo = mid + 1; 
            else return mid; 
        } 
        return lo;
    } 

    public void put(Key key, Value val)  {
        if (key == null) throw new NullPointerException(); 
        if (val == null) { delete(key); return; }
        int i = rank(key);
        if (i < n && keys[i].compareTo(key) == 0) { compares++; vals[i] = val; return; }
        compares++;
        if (n == keys.length) resize(2*keys.length);
        for (int j = n; j > i; j--) { keys[j] = keys[j-1]; vals[j] = vals[j-1]; }
        keys[i] = key; vals[i] = val; n++;
        assert check();
    }
    
    public void delete(Key key) {
        if (key == null) throw new NullPointerException(); 
        if (isEmpty()) return;
        int i = rank(key);
        if (i == n || keys[i].compareTo(key) != 0) { compares++; return; }
        compares++;
        for (int j = i; j < n-1; j++) { keys[j] = keys[j+1]; vals[j] = vals[j+1]; }
        n--; keys[n] = null; vals[n] = null;
        if (n > 0 && n == keys.length/4) resize(keys.length/2);
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
    
    public Key min() { if (isEmpty()) return null; return keys[0]; }
    
    public Key max() { if (isEmpty()) return null; return keys[n-1]; }

    public Key select(int k) { if (k<0 || k>= n) return null; return keys[k]; }

    public Key floor(Key key) {
        if (key == null) throw new NullPointerException(); 
        int i = rank(key);
        if (i < n && key.compareTo(keys[i]) == 0) return keys[i];
        if (i == 0) return null;
        else return keys[i-1];
    }

    public Key ceiling(Key key) {
        if (key == null) throw new NullPointerException(); 
        int i = rank(key);
        if (i == n) return null; 
        else return keys[i];
    }

    public int size(Key lo, Key hi) {
        if (lo == null) throw new NullPointerException(); 
        if (hi == null) throw new NullPointerException(); 
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else              return rank(hi) - rank(lo);
    }

    public Iterable<Key> keys() { return keys(min(), max()); }

    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new NullPointerException(); 
        if (hi == null) throw new NullPointerException(); 
        Queue<Key> queue = new Queue<Key>(); 
        if (lo.compareTo(hi) > 0) return queue;
        for (int i = rank(lo); i < rank(hi); i++) 
            queue.enqueue(keys[i]);
        if (contains(hi)) queue.enqueue(keys[rank(hi)]);
        return queue; 
    }

    private boolean check() { return isSorted() && rankCheck(); }

    private boolean isSorted() {
        for (int i = 1; i < size(); i++)
            if (keys[i].compareTo(keys[i-1]) < 0) return false;
        return true;
    }
 
    private boolean rankCheck() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (int i = 0; i < size(); i++)
            if (keys[i].compareTo(select(rank(keys[i]))) != 0) return false;
        return true;
    }    
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      for (int i = 0; i < n; i++) sb.append(keys[i]+":"+vals[i]+",");
      return sb.substring(0,sb.length()-1)+"}";      
    }
    
    public String trace(Key k, Value v) {
      // designed for ex3111 to trace E A S Y Q U E S T I O N : indices                             
      StringBuilder sb = new StringBuilder(); 
      StringBuilder sb2 = new StringBuilder();
      sb.append(k); 
      if (len(v)==1) sb.append(sp(6)); else sb.append(sp(5));
      sb.append(v); 
      sb.append(sp(4));
      for (int i = 0; i < n; i++) {
        sb2.append(keys[i]);
        if ((i+1)<n && len(keys[i+1])==2) sb2.append(sp(1));
        else sb2.append(sp(2));
      }
      String st2 = sb2.toString();
      sb.append(st2); 
      if (len(n)==1) sb.append(sp(31-len(st2))+n+sp(3));
      else sb.append(sp(30-len(st2))+n+sp(3));
      for (int i = 0; i < n; i++) {
        sb.append(vals[i]);
        if ((i+1)<n && len(vals[i+1])==2) sb.append(sp(1));
        else sb.append(sp(2));
      }
      return sb.toString();
    }
    
    public int len(Object x) { return (""+x).length(); }
      
    public int getCompares() { return compares; }

    public static void main(String[] args) {
      
      StringBuilder sb = new StringBuilder();
      sb.append(10.1);
      System.out.println(sb.toString());
      
      String[] keys = "E A S Y Q U E S T I O N".split("\\s+");
      Integer[] values = rangeInteger(0, keys.length);
      BinarySearchSTex3111<String, Integer> st = new BinarySearchSTex3111<>();
      for (int  i = 0; i < keys.length; i++) st.put(keys[i], values[i]);
      System.out.println(st);
      System.out.println(st.size()+"\n");
//      
//      st = new BinarySearchSTex3111<>(keys,values);
//      System.out.println(st);
//      System.out.println(st.size()+"\n");

      
//        BinarySearchSTex3111<String, Integer> st = new BinarySearchSTex3111<String, Integer>();
//        for (int i = 0; !StdIn.isEmpty(); i++) {
//            String key = StdIn.readString();
//            st.put(key, i);
//        }
//        for (String s : st.keys())
//            System.out.println(s + " " + st.get(s));
    }
}
