package ex34;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.function.Function;

import ds.Queue;
import ds.Stack;

/* p480
  3.4.2  Develop an alternate implementation of SeparateChainingHashST 
  that directly uses the linked-list code from SequentialSearchST 
  
  This is implemented below in class SCHNST that stands for Separate Chaining
  Hash Node Symbol Table. It uses the Node class from SequentialSearchST.Node 
  extended with methods getN(), putN(), deleteN(), containsN() and keysN(). 
  The main thing to realize about the implementation of these methods is that
  they are written assuming that they are executed only for the nodes directly
  referenced in the node table nt[] that's initialized with nodes having null
  key, value and next and these nodes are never deleted. SCHNST gives the same
  results as SCHST in the previous exercise as demonstrated below.

 */             

public class Ex3402SeparateChainingHashAlternateImplementationDirectlyUsingLinkedListCode {

  private static class SCHNST<Key,Value> {

    private class Node {
      private Key key;
      private Value val;
      private Node next;

      public Node(){};

      public Node(Key key, Value val, Node next)  {
        this.key  = key;
        this.val  = val;
        this.next = next;
      }
      
      @Override public String toString() {
        if (next == null)
          return "("+key+",null)";
        return "("+key+","+next.key+")";
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

      public void putN(Key k, Value v) {
        if (k == null) throw new NullPointerException("first argument to putN() is null"); 
        if (v == null) { deleteN(key); return; }
        if (key == null) { key = k; val = v; return; }
        if (key.equals(k)) val = v;
        Node node = this;
        while (node.next != null) {
          node = node.next;
          if (node.key.equals(k)) { node.val = v; return; }
        }
        Node nextNode = new Node(key,val,next);
        key = k; val = v; next = nextNode;
      }

      public void deleteN(Key k) {
        if (k == null) throw new NullPointerException("argument to deleteN() is null");
        if (key == null) return;
        if (key.equals(k)) {
          if (next == null) { key = null; val = null;  return; }
          key = next.key; val = next.val;
          @SuppressWarnings("unused")
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

    public SCHNST() { this(1009); }

    public SCHNST(int M) { 
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

    public void put(Key key, Value val) {
      if (key == null) throw new IllegalArgumentException("first argument to put() is null");
      if (kclass == null) { kclass = key.getClass(); setHash(); }
      if (val == null) { delete(key); return; }
      if (vclass == null) vclass = val.getClass();
      int i = hash.apply(key);
      if (!nt[i].containsN(key)) n++;
      nt[i].putN(key, val);
    }

    public void delete(Key k) {
      if (k == null) throw new IllegalArgumentException("argument to delete() is null");
      if (size() == 0) return;
      int i = hash.apply(k);
      if (nt[i].containsN(k)) n--;
      nt[i].deleteN(k);
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
    SCHNST<Character,Integer> ht = new SCHNST<>(5);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 U:5 Q:4 S:2 I:7 N:9 E:0 Y:3 T:6 O:8
    System.out.println("Node table:");
    ht.printNodes(); 

    System.out.println("\nM = 1009:");
    ht = new SCHNST<>(); // M = 1009
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 E:0 I:7 N:9 O:8 Q:4 S:2 T:6 U:5 Y:3 

    System.out.println("\nM = 10:");
    ht = new SCHNST<>(10);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // Q:4 S:2 I:7 T:6 A:1 U:5 N:9 E:0 Y:3 O:8
    System.out.println("Node table:");
    ht.printNodes(); 
    
    System.out.println("\nM = 11:");
    ht = new SCHNST<>(11);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // E:0 A:1 S:2 Y:3 Q:4 U:5 T:6 I:7 O:8 N:9 
    System.out.println("Node table:");
    ht.printNodes();
    
    System.out.println("\nM = 223:");
    ht = new SCHNST<>(223);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // S:2 T:6 U:5 A:1 Y:3 E:0 I:7 N:9 O:8 Q:4 

    System.out.println("\nM = 523:");
    ht = new SCHNST<>(523);
    for (int i = 0; i < ka.length; i++) ht.put(ka[i].charAt(0), va[i]);
    ht.show(); // A:1 E:0 I:7 N:9 O:8 Q:4 S:2 T:6 U:5 Y:3
    
    ht.delete('A');
    System.out.println("\nafter deleting 'A':");
    ht.show();
    
    ht.delete('Y');
    System.out.println("\nafter deleting 'Y':");
    ht.show();
    
    ht.delete('Q');
    System.out.println("\nafter deleting 'Q':");
    ht.show();
    
    System.out.println("\nkeys()");
    Iterator<Character> it = ht.keys().iterator();
    while (it.hasNext()) System.out.print(it.next()+" ");
    System.out.println();
    
    System.out.println("\ntoKeyArray():");
    par(ht.toKeyArray()); 

    System.out.println("\ncontains('O'): "+ht.contains('O')); // true
    System.out.println("contains('Z'): "+ht.contains('Z')); // false
    

  }

}

