package ds;

import static v.ArrayUtils.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

//p212
//1.4.29 Steque with two stacks. Implement a steque with two stacks so that each steque
//operation (see Exercise 1.3.32) takes a constant amortized number of stack operations.

public class StequeWithTwoStacks<Item> implements Iterable<Item> {
  // push: if s2.isEmpty run s1.push else pop all items from s2 pushing them to s1
  //       and then push the new item to s1.
  //  pop: if !s1.isEmpty return s1.pop else pop all items except the last from s2 
  //       pushing them to s1 and remove and return the last item with s2.pop.
  // peek: if !s1.isEmpty return s1.peek else pop all items except the last from s2
  //       pushing them to s1, pop and save the last, then push it to s1 and return it.
  // pook: if s1.isEmpty return s2.peek else pop all items except the last from s1
  //       pushing them to s2, pop and save the last, then push it to s2 and return it.
  // enqueue: if !s1.isEmpty then first pop all items from s1 pushing them to s2, push
  //          the new item to the empty s1 and pop all items from s2 pushing them to s1.       
  
  private Stack<Item> s1;
  private Stack<Item> s2;

  public StequeWithTwoStacks() {
    s1 = new Stack<Item>();
    s2 = new Stack<Item>();
  }
  
  @SafeVarargs
  public StequeWithTwoStacks(Item...items) {
    s1 = new Stack<Item>(items);
    s2 = new Stack<Item>();
  }

  public boolean isEmpty() { return s1.isEmpty() && s2.isEmpty(); } 

  public int size() { return s1.size() + s2.size(); }

  public void push(Item item) { 
    // add item to the top
    if (s2.isEmpty()) {
      s1.push(item);
    } else {
      int s = s2.size();
      for (int i = 0; i < s; i++) s1.push(s2.pop());
      s1.push(item);
    }
  }

  public Item pop() { 
    // remove and return the top item
    if (isEmpty()) throw new NoSuchElementException("Steque underflow");
    if(!s1.isEmpty()) {
      return s1.pop();
    } else {
      int s = s2.size();
      for (int i = 0; i < s - 1; i++)  s1.push(s2.pop());
      return s2.pop();
    }
  }

    public Item peek() {
      // return the top item without removing it
      if (isEmpty()) throw new NoSuchElementException("Steque underflow");
      if(!s1.isEmpty()) {
        return s1.peek();
      } else {
        int s = s2.size();
        for (int i = 0; i < s - 1; i++)  s1.push(s2.pop());
        Item item = s2.pop();
        s1.push(item);
        return item;
      }
    }

    public Item pook() {
      // return the bottom item without removing it
      if (isEmpty()) throw new NoSuchElementException("Steque underflow");
      if (s1.isEmpty()) {
        return s2.peek();
      } else {
        int s = s1.size();
        for (int i = 0; i < s - 1; i++)  s2.push(s1.pop());
        Item item = s1.pop();
        s2.push(item);
        return item;
      }
    }

    public void enqueue(Item item) { 
      // add item to the bottom
      int s;
      if (!s1.isEmpty()) {
        s = s1.size();
        for (int i = 0; i < s; i++)  s2.push(s1.pop());
      }
      s1.push(item);
      s = s2.size();
      for (int i = 0; i < s; i++) s1.push(s2.pop()); 
    }

    public Iterator<Item> iterator() {
      return new ArrayIterator();
    }
    
    private class ArrayIterator implements Iterator<Item> {
      private Object[] a = toArray();
      private int i = 0;
      private int n = a.length;
      public boolean hasNext() { return i < n; }
      @SuppressWarnings("unchecked")
      public Item next() {
        if (!hasNext()) throw new NoSuchElementException();
        return (Item) a[i++]; 
      }
    }
    
    public Object[] toArray() {
      return append(s1.toArray(), reverse(s2.toArray()));
    }
    
    @SafeVarargs
    public final Item[] toArray(Item...items) {
      return append(s1.toArray(items), reverse(s2.toArray(items)));
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("StequeWithTwoStacks(");
      Object[] a = toArray();
      if (a.length==0) return sb.append(")").toString();
      for (Object i : a) sb.append(i+",");
      return sb.substring(0, sb.length()-1)+")";
    }

    public static void main(String[] args) {

    }

  }

