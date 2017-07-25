package st;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import analysis.Timer;
import ds.Stack;
import v.Tuple2;
import st.RedBlackSETD;

// based on SequentialSearchST with RedBlackSETD values for SeparateChainingHashSTSETD

public class SequentialSearchSTSETD<Key> {
  private int n;           // number of key-value pairs
  private Node first;      // the linked list of key-value pairs
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = RedBlackSETD.class; // Value class
  private int deleteProbes = 0;
  private int getProbes = 0;
  private int putProbes = 0;

  private class Node {
    private Key key;
    private RedBlackSETD val;
    private Node next;

    public Node(Key key, RedBlackSETD val, Node next)  {
      this.key  = key;
      this.val  = val;
      this.next = next;
    }
    
    public Node(Key key, double d, Node next)  {
      this.key  = key;
      this.next = next;
      this.val  = new RedBlackSETD(d);
    }
    
  }

  public SequentialSearchSTSETD(){}

  public SequentialSearchSTSETD(Key[] ka, RedBlackSETD[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,RedBlackSETD> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (va[c] != null) ta[c] = new Tuple2<Key,RedBlackSETD>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    RedBlackSETD[] vz = ofDim(vclass, n); c = 0;
    for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }
  
  public SequentialSearchSTSETD(Key[] ka, Double[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Double> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (va[c] != null) ta[c] = new Tuple2<Key,Double>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    Double[] vz = ofDim(Double.class, n); c = 0;
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
//    if (key == null) throw new NullPointerException("argument to contains() is null");
    return get(key) != null;
  }

  public RedBlackSETD get(Key key) {
//    if (key == null) throw new NullPointerException("argument to get() is null"); 
    for (Node x = first; x != null; x = x.next) {
      if (key != null && key.equals(x.key) || key == null && x.key == null) {
        getProbes++;
        return x.val;
      }
      getProbes++;
    }
    return null;
  }
  
  public void put(Key key, Double d) {
//    if (key == null) throw new NullPointerException("key argument to put() is null");
    if (kclass == null && key != null) kclass = key.getClass();
    if (d == null) throw new NullPointerException("double argument to put() is null"); 
    for (Node x = first; x != null; x = x.next) {
      if (key != null && key.equals(x.key) || key == null && x.key == null) {
        putProbes++;
        if (x.val != null) x.val.put(d);
        else x.val = new RedBlackSETD(d);
        return; 
      }
      putProbes++;
    }
    first = new Node(key, d, first);
    n++;
  }

  public void put(Key key, RedBlackSETD val) {
//    if (key == null) throw new NullPointerException("key argument to put() is null");
    if (kclass == null && key != null) kclass = key.getClass();
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
//    if (key == null) throw new NullPointerException("argument to delete() is null");
    if (first == null) return;
    if (first.key != null && first.key.equals(key) || first.key == null && key == null) {
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
      if (node.key != null && key.equals(node.key) ||  node.key == null && key == null) {
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
    Double[] d = rangeDouble(0.,5.);
    shuffle(d,r);
    RedBlackSETD[] e = ofDim(RedBlackSETD.class, 10);
    e[0] = new RedBlackSETD(rangeDouble(0.,3.));
    e[1] = new RedBlackSETD(rangeDouble(3.,6.));
    e[2] = new RedBlackSETD(rangeDouble(6.,9.));
    e[3] = new RedBlackSETD(rangeDouble(9.,12.));
    e[4] = new RedBlackSETD(rangeDouble(12.,15.));
    t = new Timer();
    SequentialSearchSTSETD<Double> sv = new SequentialSearchSTSETD<>(d,e);
    System.out.println(t.elapsed()); //0
    System.out.println("size="+sv.size());
//    System.exit(0);

//    SecureRandom r = new SecureRandom();

    String[]  a = "one two three four five six seven eight nine".split("\\s+");

    e[5] = new RedBlackSETD(rangeDouble(15.,18.));
    e[6] = new RedBlackSETD(rangeDouble(18.,21.));
    e[7] = new RedBlackSETD(rangeDouble(21.,24.));
    e[8] = new RedBlackSETD(rangeDouble(24.,27.));
    
    SequentialSearchSTSETD<String> st =  new SequentialSearchSTSETD<>(a,e);
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
