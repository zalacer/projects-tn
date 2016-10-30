package ex13;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.Scanner;

//  1.3.4  Write a stack client  Parentheses that reads in a text stream from standard input
//  and uses a stack to determine whether its parentheses are properly balanced. For ex-
//  ample, your program should print true for  [()]{}{[()()]()} and  false for [(])  


public class Ex1304StackParenthesisClient {

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

  public static void main(String[] args) {
    // determine if all parentheses in System.in are balanced or not
    // prints "balanced" or "unbalanced"
    // non-parentheses chars are ignored
    // based on the fact that if parentheses are balanced then pop of stack just after
    // reading a closing parenthesis should return a matching opening parenthesis and
    // the stack should be empty after all processing all input
    String[] openParen = {"(","[","{"}; // this is sorted ascending
    String[] closeParen = {")","]","}"}; // this is sorted ascending
    Stack<String> s = new Stack<String>();
    Scanner sc = new Scanner(System.in);
    sc.useDelimiter(""); //read System.in a char at a time as String
    while(sc.hasNext()) {
      String item = sc.next();
      if (in(openParen,item)) s.push(item);
      else if (in(closeParen,item) && !s.isEmpty()) {
        String r = s.pop();
        if (indexOf(closeParen,item) != indexOf(openParen,r)) {
          System.out.println("unbalanced (early detection)");
          sc.close();
          return;
        }
      }
    }
    if (!s.isEmpty()) {
      System.out.println("unbalanced (stack not empty)");
      sc.close();
      return;
    } else {
      System.out.println("balanced (stack empty)");
      sc.close();
      return;
    }
  }
  
//  for [()]{}{[()()]()} => balanced (stack empty)
//  for [(]) => unbalanced (early detection)

}
