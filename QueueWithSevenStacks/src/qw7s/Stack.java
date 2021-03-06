package qw7s;

import static qw7s.Utils.getCloneMethod;
import static qw7s.Utils.ofDim;
//import static qw7s.Utils.pa;
import static qw7s.Utils.reverse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Stack<Item> implements Iterable<Item> {
  private Node first; // top of stack (most recently added node)
  private int N; // number of items
  private Class<?> iclass = null; // to be set to Item.class when possible
  private int opcount; // counter for pop and push ops for fail fast iteration

  public Stack(){};

  @SafeVarargs
  public Stack(Item...items) { 
    if (items == null) {
      return; 
    } else {
      iclass = items.getClass().getComponentType();
      for (Item i : items) push(i);
    }
  }

  @SuppressWarnings("unchecked")
  public Stack(Stack<Item> s2) {
    if (s2 == null) return; // create a new empty stack
    if (s2.isEmpty()) return; // create a new empty stack
    iclass = s2.iclass(); // if this is null then all s2's elements, if any, are null
    Item[] ia = (Item[]) new Object[s2.size()];
    int c = s2.size() - 1;
    if (iclass != null) {
      Method mClone = getCloneMethod(iclass);
      if (mClone != null)
        try {
          for (Item i : s2) ia[c--] = i == null ? null : (Item) mClone.invoke((Item) i);         
        } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
          c = 0;
          for (Item i : s2) ia[c--] = i;
        }
    } else for (Item i : s2) ia[c--] = i;
    for (Item i : ia) push(i);
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
    private Stack getOuterType() {
      return Stack.this;
    }
  }

  public Class<?> iclass() {
    return iclass;
  }

  public int opcount() {
    return opcount;
  }

  public boolean isEmpty() { return N == 0; } // Or: first == null.

  public void clear() {
    first = null;
    N = 0;
  }

  public int size() { return N; }

  public void push(Item item) { 
    // Add item to top of stack.
    Node oldfirst = first;
    first = new Node();
    first.item = item;
    first.next = oldfirst;
    N++;
    opcount++;
    if (iclass == null && item != null) iclass = item.getClass();
  }

  public Item pop() { // Remove item from top of stack.
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    Item item = first.item;
    first = first.next;
    N--;
    opcount++;
    return item;
  }

  public Item peek() {
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    return first.item;
  }
  
  public void move2Front(Item item) {
    // remove first node containing item (by equality) and add item to the top of the stack.
    // assumes the stack has been maintained so that no item in it occurs more than once.
    if (iclass == null && item != null) iclass = item.getClass();
    if (N == 0) { push(item); return; }
    if (first.item == null && item == null 
        || first.item != null && first.item.equals(item)) return;
    Node node = first;
    while (node.next != null) {
      if (node.next.item == null && item == null 
          || node.next.item != null && node.next.item.equals(item)) {
        node.next = node.next.next;
        N--;
        break;
      }
      node = node.next;
    }
    push(item);
  }

  public Iterator<Item> iterator() { return new ListIterator(); }

  private class ListIterator implements Iterator<Item> {
    private Node current = first;
    private final int lopcount = opcount;
    private int itcount = 0;

    public boolean hasNext() {
      if (opcount != lopcount) throw new ConcurrentModificationException(
          "iterator failed in hasNext at iterator count "+itcount);
      return current != null; 
    }

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

  public void cat(Stack<? extends Item> s2) {
    if (s2 == null) throw new IllegalArgumentException("cat: s2 must be non null");
    Stack<Item> s3 = new Stack<Item>();
    while (!s2.isEmpty()) s3.push(s2.pop());
    while(!s3.isEmpty()) push(s3.pop());
    s3 = null;
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
    Stack other = (Stack) obj;
    if (N != other.N) return false;
    if (first.getClass() != other.first.getClass()) return false;
    if (first == null) {
      if (other.first != null) return false;
    } else if (!first.equals(other.first)) return false;
    Object[] thisArray = this.toArray();
    @SuppressWarnings("rawtypes")
    Object[] otherArray = ((Stack) obj).toArray();
    if (thisArray == null) {
      if (otherArray != null) return false;
    } else if (!(Arrays.equals(thisArray, otherArray))) return false;
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
    sb.append("Stack(");
    if (isEmpty()) sb.append(")");
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  public static <T> Stack<T> getStack() {
    // Stack factory
    return new Stack<T>();
  }
  
}

