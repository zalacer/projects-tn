package ex34;

import static v.ArrayUtils.*;

import java.util.function.Function;

import st.SequentialSearchST;

/* p480
  3.4.1  Insert the keys E A S Y Q U T I O N in that order into an 
  initially empty table of M = 5 lists, using separate chaining. Use 
  the hash function  11 k % M to transform the kth letter of the 
  alphabet into a table index.
  
  This is implemented in the class SCHST below by defining hash initially
  as an undefined Function<Key,Integer>, using setHash() to define it
  conditionally to 11 * (char) key % m when keys are Characters, else it's 
  the default key.hashCode() & 0x7fffffff) % m, and running setHash() on 
  receipt of the first non-null key processed by put.
 */             

public class Ex3401SeparateChainingEASYQUTION {

  private static class SCHST<Key,Value> {
    private int n; // number of key-value pairs
    private int m; // hash table size
    private SequentialSearchST<Key,Value>[] st; // array of ST objects
    private Function<Key, Integer> hash;
    private Class<?> kclass;
    private Class<?> vclass;
    
    public SCHST() { this(1009); }
    
    public SCHST(int M) { 
      // Create M linked lists.
      this.m = M;
      st = ofDim(SequentialSearchST.class, m);
      for (int i = 0; i < M; i++) st[i] = new SequentialSearchST<Key, Value>();
    }
    
    private void setHash() { 
      if (kclass != null && kclass == Character.class)
        hash = key -> { return 11 * (char) key % m; };
        else hash = key -> { return (key.hashCode() & 0x7fffffff) % m; };
    }

    public Value get(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to get() is null");
      if (size() == 0) return null;      
      int i = hash.apply(key);
      return st[i].get(key);
    } 
    
    public void put(Key key, Value val) {
      if (key == null) throw new IllegalArgumentException("first argument to put() is null");
      if (kclass == null) { kclass = key.getClass(); setHash(); }
      if (val == null) { delete(key); return; }
      if (vclass == null) vclass = val.getClass();
      int i = hash.apply(key);
      if (!st[i].contains(key)) n++;
      st[i].put(key, val);
    } 
    
    public void delete(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to delete() is null");
      if (size() == 0) return;
      int i = hash.apply(key);
      if (st[i].contains(key)) n--;
      st[i].delete(key);
    } 
  
    public int size() { return n; }
    
    public boolean isEmpty() { return size() == 0; }
    
    public boolean contains(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to contains() is null");
      return get(key) != null;
    } 
    
    public void show() {
      if (isEmpty()) { System.out.println("hash table is empty"); return; }
      for (int i = 0; i < m; i++)
        for (Key key : st[i].keys())
          System.out.print(key+":"+get(key)+" ");
      System.out.println();
    }
  }

  public static void main(String[] args) {
    
    String[] ka = "E A S Y Q U T I O N".split("\\s+");
    Integer[] va =  rangeInteger(0,ka.length);
    
    SCHST<Character,Integer> ht = new SCHST<>(5);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 U:5 Q:4 S:2 I:7 N:9 E:0 Y:3 T:6 O:8 
    
    ht = new SCHST<>(); // M = 1009
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 E:0 I:7 N:9 O:8 Q:4 S:2 T:6 U:5 Y:3 
    
    ht = new SCHST<>(10);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // Q:4 S:2 I:7 T:6 A:1 U:5 N:9 E:0 Y:3 O:8
    
    ht = new SCHST<>(11);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // E:0 A:1 S:2 Y:3 Q:4 U:5 T:6 I:7 O:8 N:9 
    
    ht = new SCHST<>(223);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // S:2 T:6 U:5 A:1 Y:3 E:0 I:7 N:9 O:8 Q:4 
    
    ht = new SCHST<>(523);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 E:0 I:7 N:9 O:8 Q:4 S:2 T:6 U:5 Y:3
    
    System.out.println(ht.contains('A')); // true
    System.out.println(ht.contains('Z')); // false
    
  }

}

