package ex13;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

//  1.3.12  Write an iterable  Stack client that has a static method  copy() that takes a stack
//  of strings as argument and returns a copy of the stack. Note : This ability is a prime
//  example of the value of having an iterator, because it allows development of such func-
//  tionality without changing the basic API.

public class Ex1312StackCopyClient {

  // using implementation provided on p133 of the text
  public static class Stack<Item> implements Iterable<Item> {
    private Node first; // top of stack (most recently added node)
    private int N; // number of items
    
    Stack(){};
    
    Stack(Item[] items) { 
      for (int i = items.length-1; i > -1; i--) this.push(items[i]);
    }
    
    Stack(Stack<Item> stack) {
      Item[] items = stack.toArray();
      for (int i = items.length-1; i > -1; i--) this.push(items[i]);
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
        // this causes infitite recursion combined with !first.equals(other.first)
        // in Stack.equals(); Added additional check of Node class in the latter.
//        if (!getOuterType().equals(other.getOuterType()))
//          return false;
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

    public boolean isEmpty() { return first == null; } // Or: N == 0.

    public int size() { return N; }

    public void push(Item item) { // Add item to top of stack.
      Node oldfirst = first;
      first = new Node();
      first.item = item;
      first.next = oldfirst;
      N++;
    }

    public Item pop() { // Remove item from top of stack.
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      Item item = first.item;
      first = first.next;
      N--;
      return item;
    }

    public Item peek() {
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      return first.item;
    }

    // iterator code provided on p155 of the text - however not suitable as Stack iterator
    public Iterator<Item> iterator() { return new ListIterator(); }

    // using ListIterator to build array in toArray
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
    
    public Item[] toArray() {
      @SuppressWarnings("unchecked")
      Item[] a = (Item[]) new Object[N];
      int aindex = 0;
      ListIterator lit = new ListIterator();
      while (lit.hasNext()) a[aindex++] = lit.next();
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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Stack(");
      for (Item i : this) sb.append(""+i+",");
      return sb.substring(0, sb.length()-1)+")";
    }
  }
  
  public static class IterableStackClient<T> implements Iterable<T> {
    private Stack<T> stack;
    
    IterableStackClient(Stack<T> stack) {
      this.stack = stack;
    }
        
    public Stack<T> copy() {
      return new Stack<T>(stack.toArray());
    }
    
    public Iterator<T> iterator() {
      return Arrays.stream(copy().toArray()).iterator();
    }
    
  }
  
  public static <T> Iterable <T> stackCLient(Stack<T> stack) {
    return (new IterableStackClient<T>(stack));
  }
  
  public static <T> Stack<T> cloneStack(Stack<T> stack) {
    // this essentially serializes and deserializes stack to the point of equals
    return new Stack<T>(stack.toArray());
  }
 
  public static void main(String[] args) {
    
    String[] sa = {"one", "two", "three"};
    Stack<String> stack = new Stack<String>(sa); // added a new constructor to Stack
    System.out.println(stack); //Stack(one,two,three)
    pa(stack.toArray()); //Object[one,two,three]
    Iterator<String> its = stack.iterator(); // this iterates stack directly
    while(its.hasNext()) System.out.print(its.next()+" "); System.out.println();
    // one two three 
    IterableStackClient<String> isc = new IterableStackClient<String>(stack);
    its = isc.iterator(); // this iterates a copy of stack
    while(its.hasNext()) System.out.print(its.next()+" "); System.out.println();
    // one two three 
    Iterable<String> itbls = stackCLient(stack);
    its = itbls.iterator(); // this iterates a copy of stack
    while(its.hasNext()) System.out.print(its.next()+" "); System.out.println();
    // one two three 
    Stack<String> stackClone = cloneStack(stack);
    System.out.println(stackClone); //Stack(one,two,three)
    its = stackClone.iterator();
    while(its.hasNext()) System.out.print(its.next()+" "); System.out.println();
    // one two three 
    System.out.println(stack == stackClone); //false
    System.out.println(stack.equals(stackClone)); //true

  }

}
