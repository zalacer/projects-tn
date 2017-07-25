package pq;

import static v.ArrayUtils.*;
import java.util.ConcurrentModificationException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

//import v.Tuple2;

 // ex2403 p329
public class MaxPQOrderedLinkedList<Item> implements Iterable<Item> {
  private Node first; // top of MaxPQOrderedLinkedList (node with max item)
  private int N; // number of items
  private int opcount; // counter for fail fast iteration (text 1.3.50 p171)
  private boolean comparableTestDone = false;
  
  public MaxPQOrderedLinkedList(){};

  @SafeVarargs
  public MaxPQOrderedLinkedList(Item...items) { 
    if (items == null) { return; } // create a new empty MaxPQOrderedList
    else {
      for (Item i : items) {
        if (i != null) {
          if (!comparableTestDone) {
            if (!Comparable.class.isInstance(items[0]))
              throw new IllegalArgumentException(
                  "MaxPQOrderedList accepts Comparable Types only");
            comparableTestDone = true;
          }
          break;
        }
      }
      if (!comparableTestDone) return; // all items are null
      Arrays.sort(items);
      for (Item i : items) insertSimple(i);     
    }
  }

  public MaxPQOrderedLinkedList(MaxPQOrderedLinkedList<Item> mpqul) {
    if (mpqul == null) return; // create a new empty MaxPQOrderedList
    if (mpqul.isEmpty()) return; 
    Node first = new Node();
    Node node = first; N = 0;
    for (Item i : mpqul)
      if (i == null) continue; // skip null
      else {
        node.item = i; N++;
        node.next = new Node();
        node = node.next;
      }
    node = null;
  }

  private class Node { // nested class to define nodes
    Item item;
    Node next;
    Node(){}  Node(Item i){ item = i; }
    @Override
    public String toString() { return item.toString(); }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((item == null) ? 0 : item.hashCode());
      result = prime * result + ((next == null) ? 0 : next.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      @SuppressWarnings("unchecked")
      Node other = (Node) obj;
      // this causes infinite recursion combined with !first.equals(other.first)
      // in Stack.equals(); Added additional check of Node class in the latter.
      //      if (!getOuterType().equals(other.getOuterType()))
      //        return false;
      if (item == null) {
        if (other.item != null)
          return false;
      } else if (!item.equals(other.item))
        return false;
      if (next == null) {
        if (other.next != null)
          return false;
      } else if (!next.equals(other.next))
        return false;
      return true;
    }

    @SuppressWarnings("rawtypes")
    private MaxPQOrderedLinkedList getOuterType() {
      return MaxPQOrderedLinkedList.this;
    }
  }

  public int opcount() {
    return opcount;
  }

  public boolean isEmpty() { return N == 0; }

  public void clear() {
    first = null;
    N = 0;
  }

  public int size() { return N; }
  
  public void insertSimple(Item item) {
    // add item to beginning of list - for constructor initialization
    // basically the same as Stack.push
    if (item == null) return; // no nulls
    if (!comparableTestDone) {
      if (!Comparable.class.isInstance(item)) {
        throw new IllegalArgumentException(
            "insert: MaxPQUnorderedList accepts Comparable types only");
      }
      comparableTestDone = true;
    }
    Node oldfirst = first;
    first = new Node();
    first.item = item;
    first.next = oldfirst;
    N++;
    opcount++;
  }
  
  public void insert(Item item) {
    // add item to list in sorted order.
    if (item == null) return; // no nulls
    if (!comparableTestDone) {
      if (!Comparable.class.isInstance(item)) {
        throw new IllegalArgumentException(
            "insert: MaxPQUnorderedList accepts Comparable types only");
      }
      comparableTestDone = true;
    }
    if (equalsOrless(first.item,item)) {
      Node oldfirst = first;
      first = new Node(item);
      first.next = oldfirst;
    }
    else {
      Node node = first; Node previous; boolean done = false;
      while(node.next != null) {
        previous = node;
        node = node.next;
        if (equalsOrless(node.item,item)) {
          Node q = new Node(item);
          previous.next = q; q.next = node;
          done = true; break;
        }
      }
      if (!done) { node.next = new Node(item); node.next.next = null; }  
    }
    opcount++; N++;
  }
 
  private Item pop() { 
    // Remove item from top of MaxPQUnorderedList.
    // same as Stack.pop, used only to read in another MaxPQUnorderedList.
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    Item item = first.item;
    first = first.next; N--;
    opcount++;
    return item;
  }
  
  public Item delMax() { 
    // remove and return max item = first.item. same as pop.
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    Item item = first.item;
    first = first.next; N--;
    opcount++;
    return item;
  }
   
  public Item max() {
    if (isEmpty()) throw new NoSuchElementException("MaxPQUnorderedList underflow");
    return first.item;
  }
  
  // iterator code provided on p155 of the text - however not suitable as Stack iterator
  public Iterator<Item> iterator() { return new ListIterator(); }

  // using ListIterator to build array in toArray
  private class ListIterator implements Iterator<Item> {
    private Node current = first;
    private final int lopcount = opcount;
    private int itcount = 0;

    public boolean hasNext() {
      if (opcount != lopcount) throw new ConcurrentModificationException(
          "iterator failed in hasNext at iterator count "+itcount);
      return current != null; 
    }

    // remove is provided by default in the Java 8 Iterator API at 
    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    //      public void remove() { }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      itcount++;
      if (opcount != lopcount) throw new ConcurrentModificationException(
          "iterator failed in next at iterator count "+itcount);
      Item item = current.item;
      current = current.next;
      return item;
    }
  }

  public void cat(MaxPQOrderedLinkedList<? extends Item> s2) {
    if (s2 == null) throw new IllegalArgumentException("cat: s2 must be non null");
    while (!s2.isEmpty()) insert(s2.pop());
  }

  public Item[] toArray() {
    @SuppressWarnings("unchecked")
    Item[] a = !isEmpty() ? ofDim(first.item.getClass(), N) : (Item[]) new Object[N];
    int j = 0;
    for (Item i : this) a[j++] = i;
    return a;
  }

  @SafeVarargs
  public final Item[] toArray(Item...items) {
    if (items == null) throw new IllegalArgumentException("toArray: items must be non null");
    Item[] a = Arrays.copyOf(items, size());
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  @SuppressWarnings("unchecked")
  private boolean equalsOrless(Item it1, Item it2) {
    return ((Comparable<Item>) it1).compareTo(it2) <= 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + (comparableTestDone ? 1231 : 1237);
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + opcount;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    MaxPQOrderedLinkedList other = (MaxPQOrderedLinkedList) obj;
    if (N != other.N)
      return false;
    if (comparableTestDone != other.comparableTestDone)
      return false;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (opcount != other.opcount)
      return false;
    return true;
  }

  public String rawString() {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) return "";
    for (Item i : this) sb.append(i);
    return sb.toString();
  }

  public String reverseRawString() {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) return "";
    for (Item i : this) sb.append(i);
    return new String(reverse(sb.toString().toCharArray()));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("MaxPQOrderedLinkedList(");
    if (isEmpty()) sb.append(")");
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  public void show() {
    if (size() == 0 ) System.out.println("<nothing in pq>");
    else {
      Node node = first;
      System.out.print(node.item+" ");
      while (node.next != null) {
        node = node.next;
        System.out.print(node.item+" ");
      }
      System.out.println();
    }
  }
  
  public void show(String pq) {
    if (size() == 0 ) System.out.println("<nothing in "+pq+">");
    else {
      Node node = first;
      System.out.print(node.item+" ");
      while (node.next != null) {
        node = node.next;
        System.out.print(node.item+" ");
      }
      System.out.println();
    }
  }
  
  public static <T> MaxPQOrderedLinkedList<T> getList() {
    // MaxPQUnorderedList factory
    return new MaxPQOrderedLinkedList<T>();
  }

  public static void main(String[] args) {

    MaxPQOrderedLinkedList<Integer> pq = new MaxPQOrderedLinkedList<Integer>(1,2,3,4,5); 
    pq.show();
    System.out.println(pq.max());
    while(!pq.isEmpty()) System.out.println(pq.delMax());
    pq.show();
    
//    MaxPQOrderedLinkedList<Tuple2<Integer,Double>> pq2 = new MaxPQOrderedLinkedList<Tuple2<Integer,Double>>();
//    pq2.show();
//    pq2.insert(new Tuple2<Integer,Double>(1,3.2));
//    pq2.show();
    
//    MaxPQOrderedLinkedList<Tuple2<Integer,Double>> pq3 = 
//        new MaxPQOrderedLinkedList<Tuple2<Integer,Double>>(new Tuple2<Integer,Double>(1,3.2));
//    pq3.show();
    
  }

}

