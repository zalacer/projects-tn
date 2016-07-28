package ds;

import static v.ArrayUtils.take;

import java.util.Iterator;
import java.util.NoSuchElementException;

// using implementation provided on p141 of the text
public class ResizingArrayStack<Item> implements Iterable<Item> {
  @SuppressWarnings("unchecked")
  private Item[] a = (Item[]) new Object[1]; // stack items
  private int N = 0; // number of items

  public boolean isEmpty() { return N == 0; }

  public int size() { return N; }

  private void resize(int max) { 
    // Move stack to a new array of size max.
    @SuppressWarnings("unchecked")
    Item[] temp = (Item[]) new Object[max];
    for (int i = 0; i < N; i++)
      temp[i] = a[i];
    a = temp;
  }

  public void push(Item item) { 
    // Add item to top of stack.
    if (N == a.length) resize(2*a.length);
    a[N++] = item;
  }

  public Item pop() { 
    // Remove item from top of stack.
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
    public Item next() { 
      if (!hasNext()) throw new NoSuchElementException();
      return a[--i]; 
    }
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

  public static void main(String[] args) {

  }

}

