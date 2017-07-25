package ds;

import static v.ArrayUtils.ofDim;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Steque<Item> implements Iterable<Item> {
  private Node first; // head of the steque (not necessary most recently added node)
  private Node last;  // tail of the steque (not necessary least recently added node)
  private int N; // number of items
  private Class<?> iclass = null; // to be set to Item.class when possible

  public Steque(){};

  @SafeVarargs
  public Steque(Item...items) {
      if (items == null) {
        return; 
      } else {
        for (Item i : items) push(i);
      }
//      for (int i = items.length-1; i > -1; i--) this.push(items[i]);
  }

  public Steque(Steque<Item> steque) {
    if (steque != null) {
      Item[] items = steque.toArray();
      for (int i = items.length-1; i > -1; i--) this.push(items[i]);
    }
  }

  private class Node { // nested class to define nodes
    Item item;
    Node next;
    @Override
    public String toString() {
      return item.toString();
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
    private Steque getOuterType() {
      return Steque.this;
    }
  }
  
  public Node getFirst() {
    return first;
  }
  
  public Node first() {
    return first;
  }

  public void setFirst(Node first) {
    this.first = first;
  }

  public Node getLast() {
    return last;
  }
  
  public Node last() {
    return last;
  }

  public void setLast(Node last) {
    this.last = last;
  }

  public int getN() {
    return N;
  }
  
  public int N() {
    return N;
  }

  public void setN(int n) {
    N = n;
  }

  public Class<?> iclass() {
    return iclass;
  }
  
  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public int size() { return N; }

  public void push(Item item) { // Add item to top of steque.
    Node oldfirst = first;
    first = new Node();
    first.item = item;
    first.next = oldfirst;
    N++;
    if (N == 1) last = first;
    if (iclass == null && item != null) iclass = item.getClass();
  }

  public Item pop() { // Remove item from top of steque.
    if (isEmpty()) throw new NoSuchElementException("Steque underflow");
    Item item = first.item;
    first = first.next;
    N--;
    if (N == 1) last = first;
    return item;
  }

  public Item peek() {
    if (isEmpty()) throw new NoSuchElementException("Steque underflow");
    return first.item;
  }
  
  public Item pook() {
    if (isEmpty()) throw new NoSuchElementException("Steque underflow");
    return last.item;
  }
  
  public void enqueue(Item item) { // Add item to the end of the list.
    Node oldlast = last;
    last = new Node();
    last.item = item;
    last.next = null;
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++;
    if (iclass == null && item != null) iclass = item.getClass();
  }

  // iterator code provided on p155 of the text - however not suitable as Stack iterator
  public Iterator<Item> iterator() { return new ListIterator(); }

  // using ListIterator to build array in toArray
  private class ListIterator implements Iterator<Item> {
    private Node current = first;

    public boolean hasNext() { return current != null; }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      Item item = current.item;
      current = current.next;
      return item;
    }
  }
  
  public void cat(Steque<? extends Item> stq2) {
    if (stq2 == null) throw new IllegalArgumentException("cat: stq2 must be non null");
    while (!stq2.isEmpty()) enqueue(stq2.pop());
  }

  public Item[] toArray() {
    @SuppressWarnings("unchecked")
    Item[] a = iclass != null ? ofDim(iclass, N) : (Item[]) new Object[N];
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + Arrays.hashCode(toArray());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("rawtypes")
    Steque other = (Steque) obj;
    if (N != other.N) return false;
    if (first.getClass() != other.first.getClass()) return false;
    if (first == null) {
      if (other.first != null) return false;
    } else if (!first.equals(other.first)) return false;
    Object[] thisArray = this.toArray();
    @SuppressWarnings("rawtypes")
    Object[] otherArray = ((Steque) obj).toArray();
    if (thisArray == null) {
      if (otherArray != null) return false;
    } else if (!(Arrays.equals(thisArray, otherArray))) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Steque(");
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    
    Steque<Integer>  s = new Steque<Integer>(1,2,3,4,5);
    System.out.println(s); //Steque(5,4,3,2,1)
    s.push(6);
    System.out.println(s); //Steque(6,5,4,3,2,1)
    System.out.println(s.pop()); //6
    System.out.println(s); //Steque(5,4,3,2,1)
    s.enqueue(7);
    System.out.println(s); 
    

  }

}

