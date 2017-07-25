package pq;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.DoublyLinkedList2;
import ds.Node2;
import ds.Seq;
import v.ArrayUtils;
import v.Tuple2;

// bucket priority queue with integer values and keys based on
//   Algorithms and Data Structures: The Basic Toolbox, Mehlhorn, Kurt; Sanders, Peter 
//   (2008), Springer, ISBN 978-3-540-77977-3, section 10.4.1
//   https://www.springer.com/us/book/9783540779773
// and https://www.cs.ucsb.edu/~suri/cs231/ShortestPaths.pdf, pages 40-41
// ex4445

public class BucketMinPQWithDoublyLinkedList2  {
  private final int maxN;          // maximum number of elements
  private int n;                   // current number of elements
  private final int maxC;          // max cost of an element
  private int minC;                // current min cost
  private DoublyLinkedList2<Integer>[] a; // the values by cost buckets (lists)
  private Node2<Integer>[] nodes;          // the keys: keys[i] = cost of i
  
  public BucketMinPQWithDoublyLinkedList2(int maxN, int maxC) {
    if (maxN < 0) throw new IllegalArgumentException();
    this.maxN = maxN;
    this.maxC = maxC;
    this.minC = 0;
    n = 0;
    a = fill(maxC+1, ()->new DoublyLinkedList2<Integer>());
    nodes = ofDim(Node2.class, maxN);
  }
  
  public void insert(int v, int k) {
    nodes[v] = new Node2<Integer>(v,k);
    a[k % (maxC+1)].add(nodes[v]);
    n++;
  }
  
  public void decreaseKey(int v, int k) {
    Integer p = nodes[v].getW();
//    System.out.println("decreaseKey("+v+","+k+")");
//    System.out.println("a[p % (maxC+1)] = "+a[p % (maxC+1)]);
    a[p % (maxC+1)].remove(nodes[v]);
    a[k % (maxC+1)].add(nodes[v]);
    nodes[v].setW(k);
  }
  
  public int delMin() {
//    System.out.println("delMIn minC = "+minC);
//    System.out.print("delMin a[]:"); par(a);
    while (minC <= maxC+1) {
      if (minC == maxC+1) minC = 0;
      if (a[minC % (maxC+1)].isEmpty()) minC++;
      else break;
    }
//    System.out.println("delMIn new minC = "+minC);

//    System.out.println("a[minC % (maxC+1)].isEmpty = "+a[minC % (maxC+1)].isEmpty());
//    System.out.println("delMin a[minC % (maxC+1)] = "+a[minC % (maxC+1)]);
//    System.out.print("delMin a[]:"); par(a);
    Node2<Integer> node = a[minC % (maxC+1)].removeFirst();
//    System.out.println("delMin f = "+f);
//    System.out.println("delMin nodes.length = "+nodes.length);
//    System.out.print("delMin a[]:"); par(a);
    int v = node.getV();
    nodes[v] = null;
    n--;
    return v;
  }
  
  public Integer keyOf(int v) {
    if (v < 0 || v > maxN-1) throw new IllegalArgumentException(
        "keyOf: v out of bounds");
    return nodes[v].w();
  }
  
  public boolean isEmpty() { return n == 0; }
  
  public boolean contains(int i) {
    if (i < 0 || i > maxN-1) return false;
    return nodes[i] != null;
  }
  
  public int size() { return n; }
  
  public Iterator<Integer> iterator() { return new BucketIterator(); }
  
  private class BucketIterator implements Iterator<Integer> {
    // iterator over vertices only
    private BucketMinPQWithDoublyLinkedList2 copy;

    public BucketIterator() {
        copy = new BucketMinPQWithDoublyLinkedList2(maxN, maxC);
        for (int i = 0; i < maxN; i++)
          if (nodes[i] != null) copy.insert(i, nodes[i].v());
    }

    public boolean hasNext()  { return !copy.isEmpty();                     }
    public void remove()      { throw new UnsupportedOperationException();  }

    public Integer next() {
        if (!hasNext()) throw new NoSuchElementException();
        return copy.delMin();
    }  
  }
  
  public Integer[] toArray() { 
    return isEmpty() ? new Integer[0] : ArrayUtils.toArray(iterator()); 
  }
    
  public Seq<Tuple2<Integer,Seq<Integer>>> toSeq() {
    Seq<Tuple2<Integer,Seq<Integer>>> s = new Seq<>();
    for (int i = 0; i < maxC+1; i++)
      if (!a[i].isEmpty()) s.add(new Tuple2<>(i,new Seq<>(a[i].toArray())));    
    return s;
  }

  public static void main(String[] args) {
    
    BucketMinPQWithDoublyLinkedList2 pq = new BucketMinPQWithDoublyLinkedList2(15, 2);
    for (int i = 0; i < 5; i++) pq.insert(i, 0);
    for (int i = 5; i < 10; i++) pq.insert(i, 1);
    for (int i = 10; i < 15; i++) pq.insert(i, 2);
//    Seq<Tuple2<Integer,Seq<Integer>>> s = pq.toSeq();
//    for (Tuple2<Integer,Seq<Integer>> t : s) System.out.println(t._1+": "+t._2);
    System.out.println(pq.size()); //15
    System.out.println(pq.delMin()); //0
    System.out.println(pq.size()); //14
//    s = pq.toSeq();
//    for (Tuple2<Integer,Seq<Integer>> t : s) System.out.println(t._1+": "+t._2);
    while (!pq.isEmpty()) System.out.print(pq.delMin()+" "); System.out.println("\n");
    
    pq = new BucketMinPQWithDoublyLinkedList2(15, 3);
    for (int i = 0; i < 5; i++) pq.insert(i, 1);
    for (int i = 5; i < 10; i++) pq.insert(i, 2);
    for (int i = 10; i < 15; i++) pq.insert(i, 3);
//    s = pq.toSeq();
//    for (Tuple2<Integer,Seq<Integer>> t : s) System.out.println(t._1+": "+t._2);
    for (int i = 0; i < 15; i++) pq.decreaseKey(i, pq.keyOf(i)-1);
//    s = pq.toSeq();
//    for (Tuple2<Integer,Seq<Integer>> t : s) System.out.println(t._1+": "+t._2);
//    par(pq.toArray());
  }

}
