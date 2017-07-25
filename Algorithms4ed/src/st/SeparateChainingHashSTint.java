package st;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Iterator;

//import ds.Queue;
import ds.Stack;

// modified from http://algs4.cs.princeton.edu/34hash/SeparateChainingHashST.java

public class SeparateChainingHashSTint {
    private static final int INIT_CAPACITY = 4;

    private int n;                                // number of key-value pairs
    private int m;                                // hash table size
    private SequentialSearchSTint[] st;  // array of linked-list symbol tables

    public SeparateChainingHashSTint() { this(INIT_CAPACITY); } 

    public SeparateChainingHashSTint(int m) {
        this.m = m;
        st = ofDim(SequentialSearchSTint.class, m);
        for (int i = 0; i < m; i++)
            st[i] = new SequentialSearchSTint();
    } 

    private void resize(int chains) {
        SeparateChainingHashSTint temp = new SeparateChainingHashSTint(chains);
        for (int i = 0; i < m; i++) {
            for (int key : st[i].keys()) {
                temp.put(key, st[i].get(key));
            }
        }
        this.m  = temp.m;
        this.n  = temp.n;
        this.st = temp.st;
    }

    private int hash(int key) { return (key & 0x7fffffff) % m;} 
    
    public int getM() { return m; }

    public int size() { return n; } 

    public boolean isEmpty() { return size() == 0; }

    public boolean contains(int key) {
        return get(key) != Integer.MIN_VALUE;
    } 

    public int get(int key) {
        int i = hash(key);
        return st[i].get(key);
    } 

    public void put(int key, int val) {
        if (val == Integer.MIN_VALUE) {  delete(key); return;  }
        if (n >= 10*m) resize(2*m);
        int i = hash(key);
        if (!st[i].contains(key)) n++;
        st[i].put(key, val);
    } 

    public void delete(int key) {
        int i = hash(key);
        if (st[i].contains(key)) n--;
        st[i].delete(key);
        if (m > INIT_CAPACITY && n <= 2*m) resize(m/2);
    } 

    public Iterable<Integer> keys() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < m; i++) {
            for (int key : st[i].keys())
              stack.push(key);
        }
        return stack;
    }

    public int[] toArray() {
      int[] a = new int[size()]; int c = 0;
      for (int i = 0; i < m; i++) {
          for (int key : st[i].keys())
            a[c++] = key;
      }
      Arrays.sort(a);
      return a;
    }
    
    public int emptyLists() {
      int e = 0;
      for (int i = 0; i < st.length; i++)
        if (st[i].isEmpty()) e++;
      return e;
    }

    public static void main(String[] args) { 
      
      int[] a = range(0,10);
      SeparateChainingHashSTint ht = new SeparateChainingHashSTint();
      for (int i = 0; i < a.length; i++) ht.put(i,i);
      Iterator<Integer> it = ht.keys().iterator();
      while (it.hasNext()) System.out.print(it.next()+" ");
      System.out.println();
      par(ht.toArray());

    }
}

