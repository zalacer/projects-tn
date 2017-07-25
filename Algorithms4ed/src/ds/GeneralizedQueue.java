package ds;

import static java.util.Arrays.copyOf;
import java.util.Iterator;
import java.util.NoSuchElementException;

//p168
//1.3.38 Delete  k th element. Implement a class that supports the following API:
//public class GeneralizedQueue<Item>
//GeneralizedQueue()  create an empty queue
//boolean isEmpty()  is the queue empty?
//void insert(Item x)  add an item
//Item delete(int k)  delete and return the  kth least recently inserted item
//API for a generic generalized queue
//First, develop an implementation that uses an array implementation, and then develop
//one that uses a linked-list implementation. Note : the algorithms and data structures
//that we introduce in Chapter 3 make it possible to develop an implementation that
//can guarantee that both  insert() and  delete() take time prortional to the logarithm
//of the number of items in the queue—see Exercise 3.5.27.

//I am interpreting "kth least recently inserted item" to mean the kth from the oldest
//or last which is the oldest, 1st oldest or 1st least recently inserted item.

public class GeneralizedQueue<Item> implements Iterable<Item> {
  private Node first; // link to least recently added node
  private Node last; // link to most recently added node
  private int N; // number of items on the queue
  
  public GeneralizedQueue(){}
  
  public GeneralizedQueue(Item[] items) {
    if (items != null)
      for (Item i : items) enqueue(i);    
  }

  private class Node { // nested class to define nodes
    Item item;
    Node next;
  }

  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public int size() { return N; }

  public void enqueue(Item item) { // Add item to the end of the list.
    Node oldlast = last;
    last = new Node();
    last.item = item;
    last.next = null;
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++;
  }
  
  public void insert(Item item) {
     enqueue(item);
  }

  public Item dequeue() { // Remove item from the beginning of the list.
    if (isEmpty()) throw new NoSuchElementException("dequeue: queue underflow");
    Item item = first.item;
    first = first.next;
    if (isEmpty()) last = null;
    N--;
    return item;
  }
  
  public Item delete(int k) {
    // delete and return the kth least recently inserted item where the last
    // node is the 1st such item
    if (isEmpty()) throw new NoSuchElementException("delete: queue underflow");
    if (k < 1) throw new IllegalArgumentException("remove: k must be > 0");
    if (size() < k) throw new NoSuchElementException(
        "remove: kth from last element does not exist");
    if (k == size() ) {
      return dequeue();
    } else {
      Node node = first;
      for (int i = 0; i < size()-k-1; i++) node = node.next;
      Item item = node.next.item;
      node.next = node.next.next;
      N--;
      return item;
    }
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
      Item item = current.item;
      current = current.next;
      return item;
    }
  }
  
//  public Item[] toArray() {
//    @SuppressWarnings("unchecked")
//    Item[] a = (Item[]) new Object[size()];
//    Iterator<Item> it = iterator();
//    int i = 0;
//    while (it.hasNext()) a[i++] = it.next();
//    return a;
//  }

  @SuppressWarnings("unchecked")
  public Item[] toArray(Item...items) {
    if (items == null) throw new IllegalArgumentException("toArray: items must be non null");
    Item[] a = copyOf(items, size());
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("GeneralizedQueue(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    
    

  }

}
