package ds;

import static v.ArrayUtils.*;
import java.util.ConcurrentModificationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

// starting with implementation provided on p133 of the text
public class Stack<Item> implements Iterable<Item> {
  private Node first; // top of stack (most recently added node)
  private int N; // number of items
  private Class<?> iclass = null; // to be set to Item.class when possible
  private int opcount; // counter for pop and push ops for fail fast iteration (text 1.3.50 p171)

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
  
  @SafeVarargs
  public Stack(boolean reverse, Item...items) { 
    if (items == null) {
      return; 
    } else {
      iclass = items.getClass().getComponentType();
      if (reverse == true) 
        for (int i = items.length-1; i > -1; i--) push(items[i]);
      else for (Item item : items) push(item);
    }
  }

  @SuppressWarnings("unchecked")
  public Stack(Stack<Item> s2) {
    if (s2 == null) return;
    if (s2.isEmpty()) return; // create a new empty stack
    iclass = s2.iclass(); // if this is null then all s2's elements are null
    Item[] ia = (Item[]) s2.toArray();
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

  public static void main(String[] args) {

    //    Stack<Integer> s = new Stack<Integer>(1,2,3,4,5);
    //    System.out.println(s);
    //    for (Integer i : s) System.out.print(i+" "); System.out.println();

    Stack<Integer> s1 = new Stack<Integer>(3,2,1); //1,2,3
    System.out.println(s1); //Stack(1,2,3)
    System.out.println(s1.size()); //3
    int s = s1.size();
    for (int i = 0; i < s; i++) s1.pop();
    System.out.println(s1.isEmpty()); //true
    System.out.println(s1);
    System.out.println("s1="+s1.toString()); //s1=Stack(1,2,3)
    Stack<Integer> s2 = new Stack<Integer>();  
    Stack<Integer> s3 = new Stack<Integer>(4,5,6); // 6 5 4
    System.out.println("s3="+s3.toString()); //s3=Stack(6,5,4)

    // get all items in s1 in reverse stack order
    //    while (!s1.isEmpty()) s2.push(s1.pop());
    //    while (!s3.isEmpty()) s1.push(s3.pop());
    //    while (!s2.isEmpty()) s1.push(s2.pop());   
    //    System.out.println("s1="+s1.toString()); //s1=Stack(1,2,3,4,5,6)
    //    System.out.println(s1.pop()); //1

    // get all items in s3 in stack order
    while (!s3.isEmpty()) s2.push(s3.pop());
    while (!s1.isEmpty()) s3.push(s1.pop());
    while (!s2.isEmpty()) s3.push(s2.pop());   
    System.out.println("s3="+s3.toString()); //s3=Stack(6,5,4,3,2,1)
      System.out.println(s3.pop()); //6
      
    s1 = new Stack<Integer>();
    System.out.println(s1);
    pa(s1.toArray());
    
    s2 = new Stack<Integer>();
    s2.push(1);  s2.push(2); s2.push(null); 
    System.out.println(s2);
    s3 = new Stack<Integer>(s2);
    System.out.println(s3);
    System.out.println(s2.iclass);
  

  }

}

