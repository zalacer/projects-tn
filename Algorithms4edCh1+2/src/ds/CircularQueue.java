package ds;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

//  1.3.29  Write a Queue implementation that uses a circular linked list, which is the same
//  as a linked list except that no links are null and the value of last.next is first when-
//  ever the list is not empty. Keep only one Node instance variable ( last ).

public class CircularQueue<Item> implements Iterable<Item> {
  private Node last; // link to most recently added node
  private int N; // number of items on the queue
  
  public CircularQueue(){};
  
  @SafeVarargs
  public CircularQueue(Item...items){
    for (Item i : items) enqueue(i);
  };

  public class Node{ // nested class to define nodes
    Item item;
    Node next;
    
    public Node(){};
    
    public Node(Item item, Node next) {
      this.item = item; this.next = next;
    }

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
      // this causes infitite recursion combined with !first.equals(other.first)
      // in CircularQueue.equals(); Added additional check of Node class in the latter.
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
    private CircularQueue getOuterType() {
      return CircularQueue.this;
    }

    @Override
    public String toString() {
      return "Node("+item+")";
    }
  }
  
  public Node last() {
    return last;
  }
  
  public Node getLast() {
    return last;
  }
  
  public Node getNodeInstance(Item item) {
    return new Node(item,null);
  }
  
  public boolean isEmpty() { return last == null; } // Or: N == 0.

  public int size() { return N; }

  public void enqueue(Item item) { 
    // Add item to the end of the list.
    Node oldlast = last;
    last = new Node();
    last.item = item;
    last.next = oldlast == null ? last : oldlast.next;
    if (oldlast != null) oldlast.next = last;
    N++;
  }

  public Item dequeue() { 
    // Remove item from the beginning of the list.
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    Item item = null;
    // case for N == 1
    if (last == last.next) {
      item = last.item;
      last = null;
      N = 0;
      return item;
    }
    item = last.next.item;
    last.next = last.next.next;
    N--;
    return item;
  }
  
  public Iterator<Item> iterator() { return new ListIterator(); }

  private class ListIterator implements Iterator<Item> {
    private Node current = last.next;
    private boolean donelast = false;

    public boolean hasNext() { return current != null && !donelast; }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      Item item = current.item;
      current = current.next;
      if (current == last.next) donelast = true;
      return item;
    }
  }
  
  public Object[] toArray() {
    Object[] a = new Object[size()];
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }

  @SuppressWarnings("unchecked")
  public Item[] toArray(Item...item) {
    if (item.length == 0) throw new IllegalArgumentException("toArray: item length "
        + "must be > 0");
    Item[] a = (Item[]) Array.newInstance(item[0].getClass(), size());
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("CircularQueue(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {

  }

}
