package ex13;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

//  1.3.8  Give the contents and size of the array for DoublingStackOfStrings with the
//  input
//  it was - the best - of times - - - it was - the - -

// I think you mean ResizingArrayStack since "DoublingStackOfStrings" isn't in the text.
// Based on stack clients given in the text so far, I assume that when a stack client reads "-" 
// it's supposed to pop and all other tokens should be pushed.

// At the end of processing, the contents of the array are [it,null] and its length is 2,
// however the contents of the stack is just "it" and N = 1.


public class Ex1308ResizingArrayStack {

  // using implementation provided on p141 of the text
  public static class ResizingArrayStack<Item> implements Iterable<Item> {
    @SuppressWarnings("unchecked")
    private Item[] a = (Item[]) new Object[1]; // stack items
    private int N = 0; // number of items

    public boolean isEmpty() { return N == 0; }

    public int size() { return N; }

    private void resize(int max) { // Move stack to a new array of size max.
      @SuppressWarnings("unchecked")
      Item[] temp = (Item[]) new Object[max];
      for (int i = 0; i < N; i++)
        temp[i] = a[i];
      a = temp;
    }

    public void push(Item item) { // Add item to top of stack.
      if (N == a.length) resize(2*a.length);
      a[N++] = item;
    }

    public Item pop() { // Remove item from top of stack.
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      Item item = a[--N];
      a[N] = null; // Avoid loitering (see text).
      if (N > 0 && N == a.length/4) resize(a.length/2);
      return item;
    }

    public Iterator<Item> iterator() { return new ReverseArrayIterator(); }
    
    private class ReverseArrayIterator implements Iterator<Item> { // Support LIFO iteration.
      private int i = N;
      public boolean hasNext() { return i > 0; }
      public Item next() { return a[--i]; }
      public void remove() { }
    }
    
    @SuppressWarnings("unchecked")
    public void clear() {
      a = (Item[]) new Object[1]; 
      N = 0;
    }
    
    public Item[] toArray() {
      return take(a,N);
    }
    
    public Item[] toEntireArray() {
      return a;
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

    ResizingArrayStack<String> s = new ResizingArrayStack<String>();
    for (int i = 1; i < 6; i++) s.push(""+i);
    System.out.println(s); // Stack(5,4,3,2,1)
    s.clear();
    String [] test = "it was - the best - of times - - - it was - the - -".split("\\s+");
    for (String t : test) {
      if (t.equals("-") && !s.isEmpty()) s.pop();
      else s.push(t);
    }
    System.out.println(s);
    System.out.println(arrayToString(s.toArray(),75,1,1)); // [it]
    System.out.println(arrayToString(s.toEntireArray(),75,1,1)); // [it,null]
  }

}
