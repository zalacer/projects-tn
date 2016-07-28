package ds;

import static v.ArrayUtils.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

//derived from Queue implementation provided on p151 of the text
public class DoublyLinkedList<Item> implements Iterable<Item> {
  private Node first; // link to least recently added node
  private Node last; // link to most recently added node
  private int N; // number of items on the queue
  
  public DoublyLinkedList(){};
  
  public DoublyLinkedList(Item[] a) {
    for (Item i : a) add(i);
  }
  
  public DoublyLinkedList(DoublyLinkedList<Item>.Node x) {
    this.first = x;
    if (first == null) {
      this.last = null;
      N = 0;
    } else if (first.next == null) {
      this.last = first;
      N = 1;
    } else {
      N++;
      DoublyLinkedList<Item>.Node node = first;
      while (node.next != null) {
        N++;
        node = node.next;
      }
      this.last = node;
    }
  }

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
      // in LinkedList.equals(); Added additional check of Node class in the latter.
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
    private DoublyLinkedList getOuterType() {
      return DoublyLinkedList.this;
    }

    @Override
    public String toString() {
      return "Node("+item+")";
    }
  }
  
  public Node getNodeInstance(Item item) {
    return new Node(item,null);
  }

  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public int size() { return N; }

  public void add(Item item) { // Add item to the end of the list.
    Node oldlast = last;
    last = new Node();
    last.item = item;
    last.next = null;
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++;
  }
  
  public Node get(int k) {
    // return the kth element counting from first which is the 1st element
    if (k < 1) throw new IllegalArgumentException("delete: k must be > 0");
    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
    if (size() < k) throw new NoSuchElementException("LinkedList has "
        + "fewer than "+k+" elements");
    if (k == 1) return first;
    if (k == size()) return last;
    Node node = first;
//    Node previous = null;
//    Item item = null;
    int i = 0;
    while (node.next != null && i < k-1) {
//      previous = node;
      node = node.next;
//      item = node.item;
      i++;
    }
    return node;
  }
  
  public Node first() {
    return first;
  }
    
  public Node getFirst() {
    return first;
  }
  
  public Node last() {
    return last;
  }
  
  public Node getLast() {
    return last;
  }

  public Item remove() { // Remove item from the beginning of the list.
    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
    Item item = first.item;
    first = first.next;
    if (isEmpty()) last = null;
    N--;
    return item;
  }
  
  public Item removeFirst() {
    return remove();
  }
  
  public Item removeLast() { // Remove item from the end of the list.
    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
    Node node = first;
    Node previous = null;
    Item item = null;
    while (node.next != null) {
      previous = node;
      node = node.next;
      item = node.item;
    }
    if (previous == null) {
      first = previous;
    } else previous.next = null;
    last = previous;
    if (isEmpty()) last = null;
    N--;
    return item;
  }
  
  public boolean removeAfter(Node node) {
    // removes the Node after node except if node or node.next is null
    if (node == null || node.next == null) return false;
    node.next = node.next.next;
    if (node.next == null) last = node;
    N--;
    return true;
  }
  
  public boolean insertAfter(Node node, Node after) {
    // insert after after node except if either is null
    if (node == null || after == null) return false;
    after.next = node.next;
    node.next = after;
    if (after.next == null) last = after;
    N++;
    return true;
  }
  
  public Item delete(int k) {
    // removes the kth element counting from first which is the 1st element
    if (k < 1) throw new IllegalArgumentException("delete: k must be > 0");
    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
    if (size() < k) throw new NoSuchElementException("LinkedList has "
        + "fewer than "+k+" elements");
    if (k == 1) return remove();
    if (k == size()) return removeLast();
    Node node = first;
    Node previous = null;
    Item item = null;
    int i = 0;
    while (node.next != null && i < k-1) {
      previous = node;
      node = node.next;
      item = node.item;
      i++;
    }
    if (previous == null) {
      first = previous;
    } else previous.next = previous.next.next;
    if (isEmpty()) last = null;
    N--;
    return item;
  }
  
  public boolean contains(Item it) {
    // returns true if it is in this list else false
    if (isEmpty()) return false;
    Node node = first;
    if (node.item.equals(it)) return true;
    while (node.next != null) {
      node = node.next;
      if (node.item.equals(it)) return true;
    }
    return false;
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
  
  public Object[] toArray() {
    Object[] a = new Object[size()];
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }

  @SafeVarargs
  @SuppressWarnings("unchecked")
  public final Item[] toArray(Item...item) {
    if (item.length == 0) throw new IllegalArgumentException("toArray: item length "
        + "must be > 0");
    Item[] a = (Item[]) Array.newInstance(item[0].getClass(), size());
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + ((last == null) ? 0 : last.hashCode());
    result = prime * result + Arrays.hashCode(toArray());
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
    DoublyLinkedList other = (DoublyLinkedList) obj;
    if (N != other.N)
      return false;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (last == null) {
      if (other.last != null)
        return false;
    } else if (!last.equals(other.last))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedList(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  // static methods
  
  public static <T> boolean find(DoublyLinkedList<T> ll, T it) {
    // returns true if ll contains it else false
    return ll.contains(it);
  }
  
  public static <T extends Comparable<T>> T max(DoublyLinkedList<T>.Node first) {
    if (first == null) throw new IllegalArgumentException("max: first must be non null");
    if (first.next == null) return first.item;
    T max = first.item;
    DoublyLinkedList<T>.Node node = first;
    while(node.next != null) {
      node = node.next;
      if (node.item.compareTo(max) > 0) max = node.item;
    }
    return max;
  }
  
  @SafeVarargs
  public static <T extends Comparable<T>> T maxRecursive(DoublyLinkedList<T>.Node first,
      T...t) {
    // the second argument should not be supplied on the initial invocation
    
    if (first == null && t == null) throw new IllegalArgumentException(
        "maxRecursive: both arguments cannot be null at once");
        
    T max = null;
    if (t != null && t.length > 0) max = t[0];
    
    // base case 1
    // returns null on initial invocation with first = null
    if (first == null) return max;
    
    //base case 2
    if (first.next == null) {
      if (max != null) {
        return first.item.compareTo(max) > 0 ? first.item : max;
      } else return first.item;
    }
    
    // set max for recursion
    if (max == null) {
      max = first.item;
    } else if (first.item.compareTo(max) > 0) max = first.item;
    
    // do it again down the list
    return maxRecursive(first.next, max);
  }
  
  public static <T> int remove(DoublyLinkedList<T> list, T t) {
    // remove all elements equalling t in list and return the number removed
    if (list == null || t == null) throw new IllegalArgumentException(
        "remove: all arguments must be non null");
    if (list.isEmpty()) return 0;
    int c = 0;
    DoublyLinkedList<T>.Node first = list.first;
    if (first.item.equals(t)) {
      list.removeFirst();
      c+= 1 + remove(list, t);
      if (list.isEmpty()) return c;
    }
    // at this point either the list is empty and processing has completed
    // or its first element doesn't match t
    
    DoublyLinkedList<T>.Node last = list.last;
    if (last.item.equals(t)) {
      list.removeLast();
      c+= 1 + remove(list, t);
    }
    // at this point either the list is empty and procesing has completed
    // or neither its first or last elements match t
    
    DoublyLinkedList<T>.Node node = list.first;
    DoublyLinkedList<T>.Node previous = node;
    while(node.next != null) {
      node = node.next;
      if (node.item.equals(t)) {
        list.removeAfter(previous);
        c++;
        node = previous;
      }
      previous = node;
    }
    return c;
  }
  
  public static <T> DoublyLinkedList<T>.Node iterativeReverse(DoublyLinkedList<T>.Node x) {
    DoublyLinkedList<T>.Node first = x;
    DoublyLinkedList<T>.Node reverse = null;
    while (first != null) {
      DoublyLinkedList<T>.Node second = first.next;
      first.next = reverse;
      reverse = first;
      first = second;
    }
    return reverse;
  }

  public static <T> DoublyLinkedList<T>.Node recursiveReverse(DoublyLinkedList<T>.Node first) {
    if (first == null) return null;
    if (first.next == null) return first;
    DoublyLinkedList<T>.Node second = first.next;
    DoublyLinkedList<T>.Node rest = recursiveReverse(second);
    second.next = first;
    first.next = null;
    return rest;
  }
  
  public static <T> DoublyLinkedList<T> functionalReverse(DoublyLinkedList<T> list) {
    return list == null ? null : 
      new DoublyLinkedList<T>(reverse(list.toArray(list.first.item)));
  }
  
  
  public static void main(String[] args) {
    
    DoublyLinkedList<Integer> ll = new DoublyLinkedList<Integer>();
    ll.add(1); ll.add(2); ll.add(3);
    System.out.println(ll);
    ll.removeLast();
    System.out.println(ll);
    System.out.println("size="+ll.size());
    System.out.println("getFirst="+ll.getFirst());
    System.out.println("getLast="+ll.getLast());
  }

}
