package ex13;

import java.util.Iterator;
import java.util.NoSuchElementException;

//  1.3.7  Add a method  peek() to  Stack that returns the most recently inserted item on
//  the stack (without popping it).

public class Ex1307StackPeek {

  // using implementation provided on p133 of the text
  public static class Stack<Item> implements Iterable<Item> {
    private Node first; // top of stack (most recently added node)
    private int N; // number of items

    private class Node { // nested class to define nodes
      Item item;
      Node next;
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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Stack(");
      for (Item i : this) sb.append(""+i+",");
      return sb.substring(0, sb.length()-1)+")";
    }
  }

  //using implementation provided on p151 of the text
  public static class Queue<Item> implements Iterable<Item> {
    private Node first; // link to least recently added node
    private Node last; // link to most recently added node
    private int N; // number of items on the queue

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

    public Item dequeue() { // Remove item from the beginning of the list.
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      Item item = first.item;
      first = first.next;
      if (isEmpty()) last = null;
      N--;
      return item;
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

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Queue(");
      for (Item i : this) sb.append(""+i+",");
      return sb.substring(0, sb.length()-1)+")";
    }
  }

  public static void main(String[] args) {
    
    Stack<String> s = new Stack<String>();
    for (int i = 1; i < 6; i++) s.push(""+i);
    System.out.println(s.peek()); //5
    

  }

}
