package ex13;

import java.util.Iterator;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

//  1.3.2  Give the output printed by  java Stack for the input
//  it was - the best - of times - - - it was - the - -

// this question apparently assumes use of the client provided for Stack in the text
// since Stack by itself won't print anything except maybe an error message

// the output is: was best times of the was the it 

public class Ex1302StackOutput {

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
  }

  public static void main(String[] args) {
    // using client code provide on p147 of the text
    Stack<String> s = new Stack<String>();
    while (!StdIn.isEmpty()) {
      String item = StdIn.readString();
      if (!item.equals("-"))
        s.push(item);
      else if (!s.isEmpty()) StdOut.print(s.pop() + " ");
    }
    StdOut.println("(" + s.size() + " left on stack)");
    // was best times of the was the it 


  }

}
