package ex13;

import java.util.Iterator;

//  1.3.6  What does the following code fragment do to the queue  q ?
//  Stack<String> stack = new Stack<String>();
//  while (!q.isEmpty())
//  stack.push(q.dequeue());
//  while (!stack.isEmpty())
//  q.enqueue(stack.pop());

// overall it reverses the order of elements in the queue

public class Ex1306StackReversal {

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
      Item item = first.item;
      first = first.next;
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
    
    Queue<String> q = new Queue<String>();
    for (int i = 1; i < 6; i++) q.enqueue(""+i);
    System.out.println(q); // Queue(1,2,3,4,5)
    Stack<String> s = new Stack<String>();
    while (!q.isEmpty()) s.push(q.dequeue());
    while (!s.isEmpty()) q.enqueue(s.pop());
    System.out.println(q); // Queue(5,4,3,2,1)

  }

}
