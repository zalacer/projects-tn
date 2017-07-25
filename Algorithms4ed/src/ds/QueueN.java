package ds;

import static v.ArrayUtils.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Queue with null Items allowed

//starting with implementation provided on p151 of the text
public class QueueN<Item> implements Iterable<Item> {
  private Node first; // link to least recently added node
  private Node last; // link to most recently added node
  private int N; // number of items on the queue
  private Class<?> iclass = null; // to be set to Item.class when possible
  
  public QueueN(){}
  
//  public Queue(Item[] items) {
//    if (items == null) {
//      return; // create a new empty Queue
//    } else {
//      iclass = items.getClass().getComponentType();
//      for (Item i : items) enqueue(i);
//    }
//  }
  
  @SafeVarargs
  public QueueN(Item...items) {
    if (items == null) {
      return; // create a new empty Queue
    } else {
      iclass = items.getClass().getComponentType();
      for (Item i : items) enqueue(i);
    }
  }
  
  @SuppressWarnings("unchecked")
  public QueueN(QueueN<Item> q2) {
    // construct a Queue from a Queue
    if (q2 == null) throw new IllegalArgumentException("Queue constructor: "
        + "cannot construct a new Queue from a null Queue");
    if (q2.isEmpty()) return; // create a new empty queue
    iclass = q2.iclass(); // if q2.iclass() == null all q2's elements are null
    if (iclass != null) {
      Method mClone = getCloneMethod(iclass);
        if (mClone != null)
          try {
            for (Item i : q2) 
              if (i == null) {
                enqueue(null);
              } else enqueue((Item) mClone.invoke((Item) i));
          } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
            first = null; // in case something got enqueued, start from scratch
            for (Item i : q2) enqueue(i);
          }
    } else for (Item i : q2) enqueue(i);
  }

  private class Node { // nested class to define nodes
    Item item;
    Node next;
  }
  
  public Class<?> iclass() {
    return iclass;
  }

  public void setIclass(Class<?> iclass) {
    this.iclass = iclass;
  }

  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public int size() { return N; }

  public void enqueue(Item item) { // Add item to the end of the list.
//    if (item == null) throw new IllegalArgumentException("enqueue: item is null");
    Node oldlast = last;
    last = new Node();
    last.item = item;
    last.next = null;
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++;
    if (iclass == null && item != null) iclass = item.getClass();
  }

  public Item dequeue() { // Remove item from the beginning of the list.
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    Item item = first.item;
    first = first.next;
    if (isEmpty()) last = null;
    N--;
    return item;
  }
  
  public Item peek() {
    if (isEmpty()) throw new NoSuchElementException("queue underflow");
    return first.item;
  }
  
  public void clear() {
    first = null;
  }

  // iterator code provided on p155 of the text
  public Iterator<Item> iterator() { return new ListIterator(); }

  private class ListIterator implements Iterator<Item> {
    private Node current = first;

    public boolean hasNext() { return current != null; }

    // remove is provided by default in the Java 8 Iterator API at 
    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    //      public void remove() { }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      Item item = current.item;
      current = current.next;
      return item;
    }
  }
  
  public void cat(QueueN<? extends Item> q2) {
    if (q2 == null) throw new IllegalArgumentException("cat: q2 must be non null");
    while (!q2.isEmpty()) enqueue(q2.dequeue());
  }
  
  public Object[] toArray() {
    // return the Items in this Queue in an Object[] from first to last
    Object[] a = new Object[size()];
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }

  @SafeVarargs
  public final Item[] toArray(Item...items) {
    // returns a new array exactly filled with the items in this Queue from first to last
    if (items == null) throw new IllegalArgumentException("toArray: items must be non null");
    Item[] a = Arrays.copyOf(items, size());
    int c = 0;
    for (Item i : this) a[c++] = i;
    return a;
  }
  
  public Item[] toArray(Class<? extends Item> c) {
    // returns a new array exactly filled with the items in this Queue from first to last
    if (c == null) throw new IllegalArgumentException("toArray: c must be non null");
    Item[] a = ofDim(c, size());
    int d = 0;
    for (Item i : this) a[d++] = i;
    return a;
  }
  
//  public Item[] toArray(Class<? extends Item> c) {
//    // returns a new array exactly filled with the items in this Queue from first to last
//    if (c == null) throw new IllegalArgumentException("toArray: c must be non null");
//    Item[] a = ofDim(c, size());
//    int d = 0;
//    for (Item i : this) a[d++] = i;
//    return a;
//  }
  
  public void show() {
    if (isEmpty()) return;
    Object[] oa = toArray();
    Arrays.sort(oa);
    StringBuilder sb = new StringBuilder();
    for (Object i : oa) sb.append(i+",");
    System.out.println(sb.delete(sb.length()-1, sb.length()));
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Queue(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    
    QueueN<Integer> q = new QueueN<Integer>(1,2,3,4,5);
    System.out.println(q);
    for (Integer i : q) System.out.print(i+" "); System.out.println();

  }

}
