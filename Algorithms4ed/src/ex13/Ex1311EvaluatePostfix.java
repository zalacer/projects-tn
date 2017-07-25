package ex13;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.princeton.cs.algs4.StdOut;

//  1.3.11  Write a program  EvaluatePostfix that takes a postfix expression from stan-
//  dard input, evaluates it, and prints the value. (Piping the output of your program from
//  the previous exercise to this program gives equivalent behavior to  Evaluate 

// ref: https://en.wikipedia.org/wiki/Reverse_Polish_notation#Postfix_algorithm

@SuppressWarnings("unused")
public class Ex1311EvaluatePostfix {

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

  // Dijkstra’s Two-Stack Algorithm for Expression Evaluation p129
  // with minor adjustment substituting for loop for while.
  public static void evaluate(String[] sa) {
    Stack<String> ops = new Stack<String>();
    Stack<Double> vals = new Stack<Double>();
//    while (!StdIn.isEmpty()) { // Read token, push if operator.
    for (String s : sa) {
//      String s = StdIn.readString();
      if (s.equals("(")) ;
      else if (s.equals("+")) ops.push(s);
      else if (s.equals("-")) ops.push(s);
      else if (s.equals("*")) ops.push(s);
      else if (s.equals("/")) ops.push(s);
      else if (s.equals("sqrt")) ops.push(s);
      else if (s.equals(")")) { // Pop, evaluate, and push result if token is ")".
        String op = ops.pop();
        double v = vals.pop();
        if (op.equals("+")) v = vals.pop() + v;
        else if (op.equals("-")) v = vals.pop() - v;
        else if (op.equals("*")) v = vals.pop() * v;
        else if (op.equals("/")) v = vals.pop() / v;
        else if (op.equals("sqrt")) v = Math.sqrt(v);
        vals.push(v);
      } // Token not operator or paren: push double value.
      else vals.push(Double.parseDouble(s));
    }
    StdOut.println(vals.pop());
  }
  
  public static String addLeftParens(String[] sa) {
    Stack<String> ops = new Stack<String>();
    Stack<Double> vals = new Stack<Double>();
    Stack<String> out = new Stack<String>();
    String e = ""; // represents values for out
    for (String s : sa) {
      if (s.equals("(")) ;
      else if (s.equals("+")) ops.push(s);
      else if (s.equals("-")) ops.push(s);
      else if (s.equals("*")) ops.push(s);
      else if (s.equals("/")) ops.push(s);
      else if (s.equals("sqrt")) ops.push(s);
      else if (s.equals(")")) { // Pop, evaluate, and push result if token is ")".
        String op = ops.pop();
        double v = vals.pop();
        String f = out.pop();
        if (op.equals("+")) {
          v = vals.pop() + v;
          e = "( "+out.pop()+" + "+f+" )";
        }
        else if (op.equals("-")) {
          v = vals.pop() - v;
          e = "( "+out.pop()+" - "+f+" )";
        }
        else if (op.equals("*")) {
          v = vals.pop() * v;
          e = "( "+out.pop()+" * "+f+" )";
        }
        else if (op.equals("/")) {
          v = vals.pop() / v;
          e = "( "+out.pop()+" / "+f+" )";
        }
        else if (op.equals("sqrt")) {
          v = Math.sqrt(v);
          e = "( sqrt "+f+" )";
        }
        out.push(e);
        vals.push(v);
      } // Token not operator or paren: push double value.
      else {
        out.push(s);
        vals.push(Double.parseDouble(s));
      }
    }
    //StdOut.println(vals.pop());
    return out.pop();
  }
  
  public static String toPostfix(String[] sa) {
    Stack<String> ops = new Stack<String>();
    Stack<Double> vals = new Stack<Double>();
    Stack<String> out = new Stack<String>();
    String e = ""; // represents values for out
    for (String s : sa) {
      if (s.equals("(")) ;
      else if (s.equals("+")) ops.push(s);
      else if (s.equals("-")) ops.push(s);
      else if (s.equals("*")) ops.push(s);
      else if (s.equals("/")) ops.push(s);
      else if (s.equals("sqrt")) ops.push(s);
      else if (s.equals(")")) { // Pop, evaluate, and push result if token is ")".
        String op = ops.pop();
        double v = vals.pop();
        String f = out.pop();
        if (op.equals("+")) {
          v = vals.pop() + v;
          e = "( "+out.pop()+" "+f+" + )";
        }
        else if (op.equals("-")) {
          v = vals.pop() - v;
          e = "( "+out.pop()+" "+f+" - )";
        }
        else if (op.equals("*")) {
          v = vals.pop() * v;
          e = "( "+out.pop()+" "+f+" * )";
        }
        else if (op.equals("/")) {
          v = vals.pop() / v;
          e = "( "+out.pop()+" "+f+" / )";
        }
        else if (op.equals("sqrt")) {
          v = Math.sqrt(v);
          e = "( "+f+" sqrt )";
        }
        out.push(e);
        vals.push(v);
      } // Token not operator or paren: push double value.
      else {
        out.push(s);
        vals.push(Double.parseDouble(s));
      }
    }
    //StdOut.println(vals.pop());
    return out.pop();
  }
  
  public static double evaluatePostfix(String[] sa) {
    Stack<Double> vals = new Stack<Double>();
    String[] opb = {"+","-","*","/"}; // binary ops
    String[] opu = {"sqrt"}; // unary ops
    String[] paren = {"(", ")"};
    double w1; double w2;
    for (String s : sa) {
      System.out.println(vals);
      if (in(paren,s)); //ignore parens
      else if (in(opb,s)) {
        w1 = vals.pop(); w2 = vals.pop();
        switch (s) {
        case "+": vals.push(w2 + w1); break;
        case "-": vals.push(w2 - w1); break;
        case "*": vals.push(w2 * w1); break;
        case "/": vals.push(w2 / w1); break;
        }
      }
      else if (in(opu,s) && s.equals("sqrt")) vals.push(Math.sqrt(vals.pop()));
      else vals.push(Double.parseDouble(s));
    }
    return vals.pop();
  }
  
  public static void main(String[] args) {
    String input = "1 + 2 ) * 3 - 4 ) * 5 - 6 ) ) )";
    evaluate(input.split("\\s+")); //3.0
    String[] sa = input.split("\\s+");
    System.out.println(addLeftParens(sa));
    // ( ( 1 + 2 ) * ( ( 3 - 4 ) * ( 5 - 6 ) ) )
    System.out.println(toPostfix(sa));
    // ( ( 1 2 + ) ( ( 3 4 - ) ( 5 6 - ) * ) * )
    String input2 = toPostfix(sa);
    String[] sa2 = input2.split("\\s+");
    System.out.println(evaluatePostfix(sa2)); //3.0
    
    // standard input version - uncomment next 4 lines to run
//    Scanner sc = new Scanner(System.in);
//    StringBuilder sb = new StringBuilder();
//    while(sc.hasNext()) sb.append(sc.next()); sc.close();
//    System.out.println(evaluatePostfix(sb.toString().split("")));
    //// using input string "( ( 1 2 + ) ( ( 3 4 - ) ( 5 6 - ) * ) * )"
    //// output is 3.0
  }

}
