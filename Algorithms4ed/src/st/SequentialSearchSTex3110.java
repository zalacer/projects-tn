package st;

import static v.ArrayUtils.*;
import ds.Queue;

public class SequentialSearchSTex3110<Key, Value> {
  private int n; 
  private Node first;
  private int equals = 0; // counter for number of invocations of equals()

  private class Node {
    Key key; Value val; Node next;
    public Node(Key key1, Value val1, Node next1)  {
      key = key1; val  = val1; next = next1; }
  }

  public SequentialSearchSTex3110(){}
  
  public SequentialSearchSTex3110(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length); int c = 0; 
    Key[] kz = ofDim(ka.getClass().getComponentType(), n);
    for (int  i = 0; i < n; i++) {
      if (c == n) break;
      if (ka[i] != null) { kz[c++] = ka[i]; }
    }
    if (c == 0) return;
    if (c < n) { n = c; kz = take(kz,c); }
    Value[] vz = ofDim(va.getClass().getComponentType(), n); c = 0;
    for (int  i = 0; i < n; i++) {
      if (c == n) break;
      if (va[i] != null) { vz[c++] = va[i]; }
    }
    if (c == 0) return;
    if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }

  public int size() { return n; }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(Key key) {
    if (key == null) throw new NullPointerException();
    return get(key) != null;
  }

  public Value get(Key key) {
    if (key == null) throw new NullPointerException(); 
    for (Node x = first; x != null; x = x.next)
      if (key.equals(x.key)) return x.val;
    return null;
  }

  public void put(Key key, Value val) {
    if (key == null) throw new NullPointerException(); 
    if (val == null) { delete(key);  return; }
    for (Node x = first; x != null; x = x.next) {
      if (key.equals(x.key)) { x.val = val; equals++; return; }
      equals++;
    }
    first = new Node(key, val, first);
    n++;
  }

  public void delete(Key key) {
    if (key == null) throw new NullPointerException(); 
    first = delete(first, key);
  }

  private Node delete(Node x, Key key) {
    if (x == null) return null;
    if (key.equals(x.key)) { n--; return x.next; }
    x.next = delete(x.next, key);
    return x;
  }

  public Iterable<Key> keys()  {
    Queue<Key> queue = new Queue<Key>();
    Node x = first; while(x != null) { queue.enqueue(x.key); x = x.next; }
    return queue;
  }
  
  public String trace(Key k, Value v) {
    StringBuilder sb = new StringBuilder();
    if (n == 0) return "{}";
    sb.append(k+"   "); 
    String val = ""+v;
    if (val.length() == 1) sb.append(" "+v+"      ");
    else  sb.append(v+"      ");
//    Node x = first; sb.append("    |"+first.key+":"+first.val+"|");
//    if (n == 1) return sb.toString();
    Node x = first;
    while(x != null)  { 
      sb.append("|"+x.key+":"+x.val);
      String vl = ""+x.val;
      if (vl.length() == 1) sb.append("|-->");
      else sb.append("|->");
      x = x.next; 
      }
    return sb.substring(0,sb.length()-3);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (n == 0) return "{}";
    sb.append("{"); Node x = first;
    while(x != null) { sb.append(x.key+":"+x.val+","); x = x.next; }
    return sb.substring(0,sb.length()-1)+"}";
  }
  
  public int getEquals() {return equals; }

  public static void main(String[] args) {
    
    String[] k = "E A S Y Q U E S T I O N".split("\\s+");
    Integer[] v = rangeInteger(0, k.length);
    SequentialSearchSTex3110<String,Integer> st = new SequentialSearchSTex3110<>(k,v);
    System.out.println(st);
    //{E:0,A:1,S:2,Y:3,Q:4,U:5,E:6,S:7,T:8,I:9,O:10,N:11}
    System.out.println(st.size()); //10

  }
}
