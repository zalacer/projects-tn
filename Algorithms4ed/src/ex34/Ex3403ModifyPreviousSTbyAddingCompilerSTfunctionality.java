package ex34;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.function.Function;

import ds.Queue;
import ds.Stack;

/* p480
  3.4.3  Modify your implementation of the previous exercise to include an 
  integer field for each key-value pair that is set to the number of entries 
  in the table at the time that pair is inserted. Then implement a method 
  that deletes all keys (and associated values) for which the field is greater 
  than a given integer k. Note: This extra functionality is useful in 
  implementing the symbol table for a compiler.
  
  This is implemented below in class SCHCST that stands for Separate Chaining
  Hash Compiler Symbol Table. The added Node field is named entries and the 
  added method is regressToStage(int). A node's entries field is updated when 
  it's assigned a key-value pair for the first time and when its value is 
  updated for an existing key.
  
  
 */   

@SuppressWarnings("unused")
public class Ex3403ModifyPreviousSTbyAddingCompilerSTfunctionality {

  private static class SCHCST<Key,Value> {

    private class Node {
      private Key key;
      private Value val;
      private Node next;
      private int entries;

      public Node(){};

      public Node(Key key, Value val, Node next, int entries)  {
        this.key  = key;
        this.val  = val;
        this.next = next;
        this.entries = entries;
      }
      
      @Override public String toString() {
        if (next == null)
          return "("+key+","+entries+",null)";
        return "("+key+","+entries+","+next.key+")";
      }

      public String allToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this);
        Node node = this;
        while (node.next != null) {
          node = node.next;
          sb.append("->"+node);
        }
        return sb.toString();
      }

      public Value getN(Key k) {
        if (k == null) throw new NullPointerException("argument to getN() is null");
        if (key == null) return null;
        if (key.equals(k)) return val;
        Node node = this;
        while (node.next != null) {
          node = node.next;
          if (node.key != null && node.key.equals(k)) return node.val;
        }
        return null;
      }
      
      public Node getNodeN(Key k) {
        if (k == null) throw new NullPointerException("argument to getN() is null");
        if (key == null) return null;
        if (key.equals(k)) return this;
        Node node = this;
        while (node.next != null) {
          node = node.next;
          if (node.key != null && node.key.equals(k)) return node;
        }
        return null;
      }

      public void putN(Key k, Value v, int e) {
        if (k == null) throw new NullPointerException("first argument to putN() is null"); 
        if (v == null) { deleteN(key); return; }
        if (key == null) { key = k; val = v; entries = e; return; }
        if (key.equals(k)) val = v;
        Node node = this;
        while (node.next != null) {
          node = node.next;
          if (node.key.equals(k)) { node.val = v; entries = e; return; }
        }
        Node nextNode = new Node(key,val,next,entries);
        key = k; val = v; next = nextNode; entries = e;
      }

      public void deleteN(Key k) {
        if (k == null) throw new NullPointerException("argument to deleteN() is null");
        if (key == null) return;
        if (key.equals(k)) {
          if (next == null) { key = null; val = null;  return; }
          key = next.key; val = next.val; entries = next.entries;
          Node node = next; next = next.next; node = null;
          return;
        }
        Node node = this, p = null;
        while (node.next != null) {
          p = node;
          node = node.next;
          if (node.key.equals(k)) {
            p.next = node.next;
            node = null;
          }
        }
      }

      public boolean containsN(Key k) {
        if (k == null) throw new IllegalArgumentException("argument to containsN() is null");
        if (key == null) return false;
        if (key.equals(k)) return true;
        Node node = this;
        while (node.next != null) {
          node = node.next;
          if (node.key != null && node.key.equals(k)) return true;
        }
        return false;
      }

      public Iterable<Key> keysN()  {
        Stack<Key> stack = new Stack<Key>();
        if (key == null) return stack;
        if (next == null) { stack.push(key); return stack; }
        stack.push(key);
        Node node = this;
        while (node.next != null) {
          node = node.next;
          stack.push(node.key);
        }
        return stack;
      }
    }

    private int n; // number of key-value pairs
    private int m; // hash table size
    private Node[] nt; // array of nodes
    private Function<Key, Integer> hash;
    private Class<?> kclass;
    private Class<?> vclass;

    public SCHCST() { this(1009); }

    public SCHCST(int M) { 
      // Create M linked lists.
      this.m = M;
      nt = ofDim(Node.class, m);
      for (int i = 0; i < M; i++) nt[i] = new Node();
    }

    public int size() { return n; }

    public boolean isEmpty() { return size() == 0; }

    private void setHash() { 
      if (kclass != null && kclass == Character.class)
        hash = key -> { return 11 * (char) key % m; };
        else hash = key -> { return (key.hashCode() & 0x7fffffff) % m; };
    }

    public Value get(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to get() is null");
      if (size() == 0) return null;      
      int i = hash.apply(key);
      return nt[i].getN(key);
    }
    
    public Node getNode(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to get() is null");
      if (size() == 0) return null;      
      int i = hash.apply(key);
      return nt[i].getNodeN(key);
    }

    public void put(Key key, Value val) {
      if (key == null) throw new IllegalArgumentException("first argument to put() is null");
      if (kclass == null) { kclass = key.getClass(); setHash(); }
      if (val == null) { delete(key); return; }
      if (vclass == null) vclass = val.getClass();
      int i = hash.apply(key); boolean b = false;
      if (!nt[i].containsN(key)) b = true;
      nt[i].putN(key, val, n);
      if (b) n++;
    } 

    public void delete(Key k) {
      if (k == null) throw new IllegalArgumentException("argument to delete() is null");
      if (size() == 0) return;
      int i = hash.apply(k);
      if (nt[i].containsN(k)) n--;
      nt[i].deleteN(k);
    }
    
    public void regressToStage(int e) {
      if (e < 0) return;
      Queue<Key> q  = new Queue<>(); Node node;
      for (Node x : nt) {
        if (x.entries > e) q.enqueue(x.key);
        node = x;
        while (node.next != null) {
          node = node.next;
          if (node.entries > e) q.enqueue(node.key);
        }
      }
      Iterator<Key> it = q.iterator();
      while (it.hasNext()) delete(it.next());
    }

    public boolean contains(Key key) {
      if (key == null) throw new IllegalArgumentException("argument to contains() is null");
      return get(key) != null;
    }

    public Iterable<Key> keys() {
      Queue<Key> queue = new Queue<>();
      if (!isEmpty())
        for (int i = 0; i < m; i++) 
          for (Key key : nt[i].keysN()) queue.enqueue(key);
      return queue;
    }

    public Key[] toKeyArray() {
      Queue<Key> queue = new Queue<>();
      if (!isEmpty())
        for (int i = 0; i < m; i++) 
          for (Key key : nt[i].keysN()) queue.enqueue(key);
      return queue.toArray(ofDim(kclass,0));
    }

    public void show() {
      if (isEmpty()) { System.out.println("hash table is empty"); return; }
      for (int i = 0; i < m; i++)
        for (Key key : nt[i].keysN())
          System.out.print(key+":"+get(key)+" ");
      System.out.println();
    }

    public void printNodes() {
      for (Node x : nt) System.out.println(x.allToString());
    }
  }

  public static void main(String[] args) {

    String[] ka = "E A S Y Q U T I O N".split("\\s+");
    Integer[] va =  rangeInteger(0,ka.length);

    System.out.println("M = 5");
    SCHCST<Character,Integer> ht = new SCHCST<>(5);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 U:5 Q:4 S:2 I:7 N:9 E:0 Y:3 T:6 O:8
    System.out.println("Node table: [Node.toString() prints key, entries, next.key]");
    ht.printNodes();
    System.out.println("print nodes with entries > 5:");
    Iterator<Character> it = ht.keys().iterator();
    while (it.hasNext()) {
      Character k = it.next();
      if (ht.getNode(k).entries > 5) System.out.print(ht.getNode(k)+" ");
    }
    System.out.println();
    
    System.out.println("\nreduceToStage(5) [removes all nodes with entries > 5]:");
    ht.regressToStage(5);
    System.out.println("Node table: [Node.toString() prints key, entries, next.key]");
    ht.printNodes();   
    it = ht.keys().iterator();
    while (it.hasNext()) assert ht.getNode(it.next()).entries <= 5;

  }

}

