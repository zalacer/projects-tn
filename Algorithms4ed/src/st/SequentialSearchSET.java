package st;

import static v.ArrayUtils.*;

import java.util.Iterator;
import ds.Stack;

// based on st.SequentialSearchSTX for ex3502

public class SequentialSearchSET<X> implements UnorderedSET<X> {
  private int n = 0;              // number of keys
  private Node first;             // the linked list of key-value pairs
  private Class<?> xclass = null; // class of X

  private class Node {
    private X x;
    private Node next;

    public Node(X x, Node next)  {
      this.x  = x;
      this.next = next;
    }
  }

  public SequentialSearchSET(){}

  public SequentialSearchSET(X[] xa) {
    if (xa == null || xa.length == 0) return;
    xclass = xa.getClass().getComponentType();
    int n = xa.length; int c = 0;
    X[] ta = ofDim(xa.getClass().getComponentType(), n);
    for (int i = 0; i < n; i++) if (xa[c] != null) ta[c] = xa[c];
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    for (int  i = 0; i < n; i++) add(ta[i]);
  }

  public int size() { return n; }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(X x) {
    if (x == null) throw new NullPointerException("argument to contains() is null");
    return get(x);
  }

  private boolean get(X x) {
    if (x == null) throw new NullPointerException("argument to get() is null"); 
    for (Node node = first; node != null; node = node.next)
      if (x.equals(node.x)) return true;
    return false;
  }

  public void add(X x) {
    if (x == null) throw new NullPointerException("add: argument is null"); 
    if (xclass == null) xclass = x.getClass();
    for (Node node = first; node != null; node = node.next)
      if (x.equals(node.x)) return; 
    first = new Node(x,first);
    n++;
  }

  public void delete(X x) {
    if (x == null) throw new NullPointerException("delete: argument is null");
    if (first == null) return;
    if (first.x.equals(x)) {
      first = first.next;
      n--;
      return;
    }
    Node node = first, p;
    while (node.next != null) {
      p = node;
      node = node.next;
      if (x.equals(node.x)) {
        p.next = node.next;
        n--;
        node = null;
        return;
      }
    }     
  }

  public Iterator<X> iterator()  {
    Stack<X> stack = new Stack<X>();
    for (Node node = first; node != null; node = node.next)
      stack.push(node.x);
    return stack.iterator();
  }

  public SequentialSearchSET<X> union(SequentialSearchSET<X> that) {
    if (that == null) return null;
    SequentialSearchSET<X> set = new SequentialSearchSET<>();
    for (X x : this) { set.add(x); }
    for (X x : that) { set.add(x); }
    return set;
  }

  public SequentialSearchSET<X> intersection(SequentialSearchSET<X> that) {
    if (that == null) return null;
    SequentialSearchSET<X> set = new SequentialSearchSET<>();
    if (this.size() < that.size()) {
      for (X x : this) { if (that.contains(x)) set.add(x); }
    } else for (X x : that) { if (this.contains(x)) set.add(x); }
    return set;
  }

  @Override public int hashCode() {
    int h = 0;
    Iterator<X> it = iterator();
    while (it.hasNext()) {
      X x = it.next();
      h += x == null ? 0 : x.hashCode();
    }
    return h;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" }) 
  @Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SequentialSearchSET other = (SequentialSearchSET) obj;
    if (size() != other.size()) return false;
    Iterator<X> it = iterator();
    while (it.hasNext()) if (!other.contains(it.next())) return false;
    return true;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    Iterator<X> it = iterator();
    while (it.hasNext()) sb.append(it.next()+",");
    sb.replace(sb.length()-1, sb.length(),")");
    return sb.toString();
  }

  public static void main(String[] args) {

    System.out.println("SequentialSearchSET demo:");
    
    SequentialSearchSET<Integer> set1 = new SequentialSearchSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) set1.add(i);
    System.out.println("set1 = "+set1);

    SequentialSearchSET<Integer> set2 = new SequentialSearchSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) set2.add(i);
    System.out.println("set2 = "+set2);

    SequentialSearchSET<Integer> union1 = set1.union(set2);
    System.out.println("set1 union set2 = "+union1);

    for (int i = 1; i < 16; i++) assert union1.contains(i);

    SequentialSearchSET<Integer> intersection1 = set1.intersection(set2);
    System.out.println("set1 intersect set2 = "+intersection1);

    for (int i = 6; i < 11; i++) assert intersection1.contains(i);

  }
}
