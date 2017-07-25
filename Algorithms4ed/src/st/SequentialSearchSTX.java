package st;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import analysis.Timer;
import ds.Stack;
import v.Tuple2;

// based on http://algs4.cs.princeton.edu/31elementary/SequentialSearchST.java

public class SequentialSearchSTX<Key, Value> {
  private int n;           // number of key-value pairs
  private Node first;      // the linked list of key-value pairs
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = null; // Value class
  private int deleteProbes = 0;
  private int getProbes = 0;
  private int putProbes = 0;

  private class Node {
    private Key key;
    private Value val;
    private Node next;

    public Node(Key key, Value val, Node next)  {
      this.key  = key;
      this.val  = val;
      this.next = next;
    }
  }

  public SequentialSearchSTX(){}

  public SequentialSearchSTX(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    Value[] vz = ofDim(vclass, n); c = 0;
    for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }

  public int probes() { return deleteProbes+getProbes+putProbes; }

  public void zeroProbes() { deleteProbes = getProbes = putProbes = 0; }

  public int getDeleteProbes() {  return deleteProbes; }

  public int getGetProbes() {  return getProbes; }

  public int getPutProbes() {  return putProbes; }

  public int size() { return n; }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(Key key) {
    if (key == null) throw new NullPointerException("argument to contains() is null");
    return get(key) != null;
  }

  public Value get(Key key) {
    if (key == null) throw new NullPointerException("argument to get() is null"); 
    for (Node x = first; x != null; x = x.next) {
      if (key.equals(x.key)) {
        getProbes++;
        return x.val;
      }
      getProbes++;
    }
    return null;
  }

  public void put(Key key, Value val) {
    if (key == null) throw new NullPointerException("first argument to put() is null"); 
    if (val == null) { delete(key); return; }
    for (Node x = first; x != null; x = x.next) {
      if (key.equals(x.key)) {
        putProbes++;
        x.val = val;  
        return; 
      }
      putProbes++;
    }
    first = new Node(key, val, first);
    n++;
  }

  public void delete(Key key) {
    if (key == null) throw new NullPointerException("argument to delete() is null");
    if (first == null) return;
    if (first.key.equals(key)) {
      deleteProbes++;
      first = first.next;
      n--;
      return;
    }
    deleteProbes++;
    Node node = first, p;
    while (node.next != null) {
      p = node;
      node = node.next;
      if (key.equals(node.key)) {
        deleteProbes++;
        p.next = node.next;
        n--;
        node = null;
        return;
      }
      deleteProbes++;
    }     
  }

  public Iterable<Key> keys()  {
    Stack<Key> stack = new Stack<Key>();
    for (Node x = first; x != null; x = x.next)
      stack.push(x.key);
    return stack;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (n == 0) return "{}";
    sb.append("{"); Node x = first;
    while(x != null) { sb.append(x.key+":"+x.val+","); x = x.next; }
    return sb.substring(0,sb.length()-1)+"}";
  }

  public static void main(String[] args) {
    
    Timer t = new Timer();
    SecureRandom r = new SecureRandom();
    Double[] d = rangeDouble(1.,1000.);
    shuffle(d,r);
    Integer[] e = rangeInteger(1,1000);
    t = new Timer();
    SequentialSearchSTX<Double,Integer> sv = new SequentialSearchSTX<>(d,e);
    System.out.println(t.elapsed()); //0
    System.out.println("size="+sv.size());
    System.exit(0);

//    SecureRandom r = new SecureRandom();

    String[]  a = "one two three four five six seven eight nine".split("\\s+");
    Integer[] b = rangeInteger(1,10);

    SequentialSearchSTX<String,Integer> st =  new SequentialSearchSTX<>(a,b);
    Iterator<String> it = st.keys().iterator();
          while (it.hasNext()) {
            @SuppressWarnings("unused")
            String k = it.next();
           // System.out.print(k+":"+st.get(k)+" ");
          }
    System.out.println(st);
    st.delete("one");
    System.out.println(st);
    String[] c = a.clone();
    shuffle(c,r);
    par(c);
    for (String s : c) st.delete(s);
    System.out.println(st.isEmpty());





  }
}
