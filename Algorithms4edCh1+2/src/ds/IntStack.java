package ds;

import static v.ArrayUtils.*;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

// this is implemented for exercise 1.4.35 Time costs for pushdown stacks on p213
public class IntStack implements Iterable<Integer> {
  private Node first; // top of stack (most recently added node)
  private int N; // number of items
  private int opcount; // counter for pop and push ops for fail fast iteration (text 1.3.50 p171)

  public IntStack(){};

  @SafeVarargs
  public IntStack(int...items) { 
    if (items == null) {
      return; 
    } else {
      for (int i : items) push(i);
    }
  }

  private class Node {
    int item;
    Node next;
    public Node(int item, Node next) {
      this.item = item; this.next = next;
    }
    @Override
    public String toString() {
      return ""+item;
    }
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

  public void push(int item) { 
    // Add item to top of stack.
    Node oldfirst = first;
    first = new Node(item, oldfirst);
    N++;
    opcount++;
  }

 
  public int pop() { // Remove item from top of stack.
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    int item = first.item;
    first = first.next;
    N--;
    opcount++;
    return item;
  }

  public int peek() {
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    return first.item;
  }
  
  public Iterator<Integer> iterator() { return new ListIterator(); }

  private class ListIterator implements Iterator<Integer> {
    private Node current = first;
    private final int lopcount = opcount;
    private int itcount = 0;

    public boolean hasNext() {
      if (opcount != lopcount) throw new ConcurrentModificationException(
          "iterator failed in hasNext at iterator count "+itcount);
      return current != null; 
    }
    
    public Integer next() {
      if (!hasNext()) throw new NoSuchElementException();
      itcount++;
      if (opcount != lopcount) throw new ConcurrentModificationException(
          "iterator failed in next at iterator count "+itcount);
      int item = current.item;
      current = current.next;
      return item;
    }
  }

  public int[] toArray() {
    int[] a = new int[N];
    int j = 0;
    for (int i : this) a[j++] = i;
    return a;
  }

  public String rawString() {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) return "";
    for (int i : this) sb.append(i);
    return sb.toString();
  }

  public String reverseRawString() {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) return "";
    for (int i : this) sb.append(i);
    return new String(reverse(sb.toString().toCharArray()));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Stack(");
    if (isEmpty()) sb.append(")");
    for (int i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  public static IntStack getStack() {
    // Stack factory
    return new IntStack();
  }

  public static void main(String[] args) {

   

  }

}

