package st;

import static v.ArrayUtils.*;

import ds.Queue;

import v.ArrayUtils;

public class OrderedSequentialSearchST<K extends Comparable<? super K>,V> extends OrderedAbstractST<K,V> {
  // for Ex3103
  private int n;           // number of key-value pairs
  private Node first;      // head of the linked list of key-value pairs

  private class Node {
    K k; V v; Node next;
    public Node(K k1, V v1, Node next1) { k  = k1; v  = v1; next = next1; }
    @Override public String toString() {return "("+k+","+v+")"; }
  }

  public OrderedSequentialSearchST(){}

  public OrderedSequentialSearchST(K[] ka, V[] va) {
    if (ka == null || va == null) throw new NullPointerException(
        "OrderedSequentialSearchST constructor array argument is null");
    n = Math.min(ka.length, va.length);
    if (n == 0) return;
    Node[] nodes = ofDim(Node.class, n);
    for (int i = n-1; i > -1; i--) {
      if (i < n-1)  nodes[i] = new Node(ka[i], va[i], nodes[i+1]);
      else nodes[i] = new Node(ka[i], va[i], null);
    }
    first = nodes[0];      
  }

  @Override
  public void put(K k, V v) {
    if (k == null) throw new NullPointerException("first argument to put() is null"); 
    if (v == null) { delete(k); return; }
    // this is what makes it ordered.
    if (n == 0) { first = new Node(k, v, null); n = 1; return; }
    Node x = first, p = null;
    while (x!= null && x.k.compareTo(k) < 0) { p = x; x = x.next; }
    if (x == null) { p.next = new Node(k, v, null); n++; return; }
    if(x.k.equals(k)) { x.v = v; return; }
    if (x == first) { first = new Node(k, v, first); n++; return; };
    p.next = new Node(k, v, p.next); n++;
  }

  @Override
  public V get(K k) {
    if (k == null) throw new NullPointerException("argument to get() is null");
    if (n == 0) return null;
    Node x = first;
    while (x != null && x.k.compareTo(k) < 0) x = x.next;
    if (x == null) return null;
    if (x.k.equals(k)) return x.v;
    return null;
  }

  @Override
  public void delete(K k) {
    if (k == null) throw new NullPointerException("argument to delete() is null");
    if (n == 0) return;
    if (n == 1) { first = first.next; n = 0; return; }
    Node x = first, p = null;
    while (x != null && x.k.compareTo(k) < 0) { p = x; x = x.next; }
    if (x == null) return;
    if (x.k.equals(k)) { p.next = p.next.next; n--; }
  }

  @Override
  public boolean contains(K k) {
    if (k == null) throw new NullPointerException("argument to contains() is null");
    return get(k) != null;
  }

  @Override
  public boolean isEmpty() {
    return n == 0;
  }

  @Override
  public int size() {
    return n;
  }

  @Override
  public K min() {
    if (n == 0) return null;
    return first.k;
  }

  @Override
  public K max() {
    if (n == 0) return null;
    if (n == 1) return first.k;
    Node x = first, p = null;
    while (x != null) { p = x; x = x.next; }
    return p.k;
  }

  //    @Override
  //    public K floor(K k) {
  //      // return largest k less than or equal to k.
  //      if (k == null) throw new NullPointerException("argument to floor() is null");
  //      if (n == 0) return null;
  //      if (n == 1) return first.k.compareTo(k) <= 0 ? first.k : null;
  //      Node x = first, p = null;
  //      while (x != null && x.k.compareTo(k) <= 0) { p = x; x = x.next; }
  //      if (x == first) return first.k.compareTo(k) <= 0 ? first.k : null;
  //      return p.k;
  //    }

  @Override
  public K floor(K k) {
    // return largest k less than or equal to k.
    if (k == null) throw new NullPointerException("argument to floor() is null");
    if (n == 0) return null;
    if (n == 1) return first.k.compareTo(k) <= 0 ? first.k : null;
    Node x = first, p = null;
    while (x != null && x.k.compareTo(k) <= 0) { p = x; x = x.next; }
    if (p == null) return null; 
    return p.k;
  }

  @Override
  public K ceiling(K k) {
    // return smallest key greater than or equal to key
    if (k == null) throw new NullPointerException("argument to floor() is null");
    if (n == 0) return null;
    if (n == 1) return first.k.compareTo(k) >= 0 ? first.k : null;
    Node x = first;
    while (x != null && x.k.compareTo(k) < 0) x = x.next;
    if (x == null) return null;
    if (x == first) return first.k.compareTo(k) >= 0 ? first.k : null;
    return x.k;
  }

  @Override
  public int rank(K k) {
    // return the number of keys smaller than k
    if (k == null) throw new NullPointerException("argument to rank() is null");
    if (n == 0) return 0;
    if (n == 1) return first.k.compareTo(k) < 0 ? 1 : 0;
    Node x = first; int rank = 0;
    while (x != null && x.k.compareTo(k) < 0) { rank++; x = x.next; }
    return rank;
  }

  @Override
  public K select(int r) {
    // return key of rank r
    if (r < 0 || n == 0 || r >= n) return null;
    if (n == 1) return r == 0 ? first.k : null;
    Node x = first;
    for(int i = 0 ; i < r ; i++) x = x.next;
    return x.k;
  }

  @Override
  public void deleteMin() {
    // remove the node with the min k.
    if (first != null) { first = first.next; n--; }
  }

  @Override
  public void deleteMax() {
    // remove the node with the max k.
    if (n == 0) return;
    if (n == 1) { first = null; n = 0; return; }
    Node x = first, p = null, pp = null;
    while (x != null) { pp = p; p = x; x = x.next; }
    pp.next = null; n--;
  }

  @Override
  public int size(K lo, K hi) {
    // return the number of keys in  [lo..hi]
    if (lo == null || hi == null ) throw new NullPointerException(
        "argument to size(K,K) is null");
    //      if (get(lo) == null || get(hi) == null) throw new IllegalArgumentException(
    //          "size(lo, hi): lo and hi aren't both valid keys");
    if (!(lo.compareTo(hi) <= 0)) throw new IllegalArgumentException(
        "size(lo, hi): lo isn't less than or equal to hi");
    if (n == 1) {
      if (first.k.compareTo(lo) >= 0 && first.k.compareTo(hi) <= 0)
        return 1;
      else return 0;
    }
    int c = 0;
    Node x = first;
    while (x != null && x.k.compareTo(lo) < 0) x = x.next;
    if (x.next == null || x.k.compareTo(hi) > 0) return 0;
    // this way lo and hi don't need to exist to count all keys !<lo..!>hi
    while (x != null && x.k.compareTo(hi) <= 0) { c++; x = x.next; };
    return c;
  }

  @Override
  public Iterable<K> keys(K lo, K hi) {
    // return an Iterable containing all ks in [lo..hi] in sorted order;
    if (lo == null || hi == null ) throw new NullPointerException(
        "argument to size(K,K) is null");
    //      if (get(lo) == null || get(hi) == null) throw new IllegalArgumentException(
    //          "keys(lo, hi): lo and hi aren't both valid keys");
    if (!(lo.compareTo(hi) <= 0)) throw new IllegalArgumentException(
        "keys(lo, hi): lo less than or equal to hi");
    if (n == 0) return null;
    Queue<K> q = new Queue<K>();
    if (n == 1) {
      if (first.k.compareTo(lo) >= 0 && first.k.compareTo(hi) <= 0) { 
        q.enqueue(first.k); return q; 
      }
      else return null;
    }
    Node x = first;
    while (x != null && x.k.compareTo(lo) < 0) x = x.next;
    if (x == null || x.k.compareTo(hi) > 0) return null;
    // this way lo and hi don't need to exist to get all keys !<lo..!>hi
    while (x != null && x.k.compareTo(hi) <= 0) { q.enqueue(x.k); x = x.next; };  
    return q;
  }

  @Override
  public Iterable<K> keys() {
    // return an Iterable containing all Ks in sorted order;
    if (n == 0) return null;
    Queue<K> q = new Queue<K>();
    Node x = first;
    while (x != null) { q.enqueue(x.k); x = x.next; }
    return q;
  }
  
  public K[] keyArray() {
    // return an Iterable containing all Ks in sorted order;
    if (n == 0) return null;
    Queue<K> q = new Queue<K>();
    Node x = first;
    while (x != null) { q.enqueue(x.k); x = x.next; }
    return q.toArray(ofDim(first.k.getClass(),0));
  }

  public void show() {
    if (n == 0) { System.out.println("<nothing in st>"); return; }
    Node x = first;
    for (int i = 0; i < n; i++) {
      System.out.print(x.k+"="+x.v+" "); x = x.next;
    }
    System.out.println();
  }

  @Override
  public String toString() {
    if (n == 0) return "()";
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    Node x = first;
    for (int i = 0; i < n-1; i++) {
      sb.append(x.k+"="+x.v+","); x = x.next;
    }
    sb.append(x.k+"="+x.v+")");
    return sb.toString();
  }  


  public static int rank(Integer[] keys, Integer key, int lo, int hi) {
    // adapted from p380
    if (hi < lo) return lo;
    int mid = lo + (hi - lo) / 2;
    int cmp = key.compareTo(keys[mid]);
    if (cmp < 0)
      return rank(keys, key, lo, mid-1);
    else if (cmp > 0)
      return rank(keys, key, mid+1, hi);
    else return mid;
  }

  public static void main(String[] args) {

    String[] s = "a b c d e f g h i j k l m n".split("\\s+");
    Integer[] z = rangeInteger(1,s.length+1);
    OrderedSequentialSearchST<Integer,String> st = new OrderedSequentialSearchST<>(z,s);
    System.out.print("st.show "); st.show();
    System.out.println("st.toString "+st);
    //1=a 2=b 3=c 4=d 5=e 6=f 7=g 8=h 9=i 10=j 11=k 12=l 13=m 14=n 15=o 16=p 17=q 
    System.out.println("st.min "+st.min()); //1
    System.out.println("st.max "+st.max()); //17

    System.out.print("keys "); ArrayUtils.show(z);
    System.out.print("values ");  ArrayUtils.show(s);

    st.put(17,"q"); st.put(16,"p");  st.put(15,"o");

    System.out.print("get[1..17] ");
    for (int i = 1; i < 18; i++) System.out.print(st.get(i)+" "); System.out.println();
    //a b c d e f g h i j k l m n o p q 

    System.out.print("rank[1..17] ");
    for (int i = 1; i < 18; i++) System.out.print(st.rank(i)+" "); System.out.println();
    //0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

    System.out.print("select[0..17] ");
    for (int i = 0; i < 18; i++) System.out.print(st.select(i)+" "); System.out.println();
    //2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 null 

    System.out.print("floor[1..17] ");
    for (int i = 1; i < 18; i++) System.out.print(st.floor(i)+" "); System.out.println();
    //1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17

    System.out.print("ceiling[1..17] ");
    for (int i = 1; i < 18; i++) System.out.print(st.ceiling(i)+" "); System.out.println();
    //1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 

    System.out.println("size[5..11] "+st.size(5,11)); //7

    System.out.print("keys queue[5..11] ");
    Queue<Integer> q = (Queue<Integer>)st.keys(5,11);
    par(q.toArray()); //keys queue[5..11] [5,6,7,8,9,10,11]

    System.out.print("keys queue  ");
    q = (Queue<Integer>)st.keys();
    par(q.toArray()); //keys queue  [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17]

    System.out.println(st.isEmpty()); //false
    System.out.println(st.size()); //17

    System.out.print("st.show "); st.show();
    st.deleteMin(); System.out.print("delMin; st.show "); st.show();
    st.deleteMax(); System.out.print("delMax; st.show "); st.show();
    System.out.println("st.toString "+st);
    System.out.print("st.keyArray "); par(st.keyArray());

    System.out.println();

    s = "a b".split("\\s+");
    System.out.println("s.length="+s.length);
    z = rangeInteger(1,s.length+1);
    st = new OrderedSequentialSearchST<>(z,s);
    System.out.println("st.toString "+st);
    st.deleteMax(); System.out.print("delMax; st.show "); st.show();
    System.out.print("st.toString "+st);
    st.deleteMax(); System.out.print("delMax; st.show "); st.show();
    System.out.print("st.toString "+st);

    System.out.println();

    s = "a b".split("\\s+");
    System.out.println("s.length="+s.length);
    z = rangeInteger(1,s.length+1);
    st = new OrderedSequentialSearchST<>(z,s);
    System.out.println("st.toString "+st);
    st.deleteMin(); System.out.print("delMin; st.show "); st.show();
    System.out.println("st.toString "+st);
    st.deleteMin(); System.out.print("delMin; st.show "); st.show();
    System.out.println("st.toString "+st);



    //    Integer[] a = rangeInteger(1,6);
    //    par(a); //[1,2,3,4,5]
    //    System.out.println(rank(a, 1, 0, a.length-1)); //0
    //    System.out.println(rank(a, 2, 0, a.length-1)); //1
    //    System.out.println(rank(a, 5, 0, a.length-1)); //4
    //    System.out.println(rank(a, 6, 0, a.length-1)); //5
    //    System.out.println(rank(a, 7, 0, a.length-1)); //5
  }

}