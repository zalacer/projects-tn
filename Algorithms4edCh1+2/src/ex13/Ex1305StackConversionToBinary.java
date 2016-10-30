package ex13;

import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;

//  1.3.5  What does the following code fragment print when  N is  50 ? Give a high-level
//  description of what it does when presented with a positive integer  N .
//  Stack<Integer> stack = new Stack<Integer>();
//  while (N > 0)
//  {
//  stack.push(N % 2);
//  N = N / 2;
//  }
//  for (int d : stack) StdOut.print(d);
//  StdOut.println();
//  Answer : Prints the binary representation of  N ( 110010 when  N is  50 ).

public class Ex1305StackConversionToBinary {

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
      Iterator<Item> it = this.iterator();
      while(it.hasNext()) sb.append(""+it.next()+"");
      return sb.substring(0, sb.length()-1)+")";
    }
  }

  public static void main(String[] args) {
    Stack<Integer> stack = new Stack<Integer>();
    int N = 50;
    while (N > 0)
    {
      stack.push(N % 2);
      N = N / 2;
    }
    for (int d : stack) StdOut.print(d);
    StdOut.println(); //110010 = 50 base 2
  }
 
}
