package ds;

import static v.ArrayUtils.take;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResizingIntArrayStack implements Iterable<Integer> {
  private int[] a = new int[1];
  private int N = 0; 

  public boolean isEmpty() { return N == 0; }

  public int size() { return N; }

  private void resize(int max) { 
    // Move stack to a new array of size max.
    int[] temp = new int[max];
    for (int i = 0; i < N; i++)
      temp[i] = a[i];
    a = temp;
  }

  public void push(int item) { 
    // Add item to top of stack.
    if (N == a.length) resize(2*a.length);
    a[N++] = item;
  }

  public int pop() { 
    // Remove item from top of stack.
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    int item = a[--N];
    a[N] = 0;
    if (N > 0 && N == a.length/4) resize(a.length/2);
    return item;
  }

  public Iterator<Integer> iterator() { 
    return new ReverseArrayIterator(); 
  }

  private class ReverseArrayIterator implements Iterator<Integer> { // Support LIFO iteration.
    private int i = N;
    public boolean hasNext() { return i > 0; }
    public Integer next() { 
      if (!hasNext()) throw new NoSuchElementException();
      return a[--i]; 
    }
    public void remove() { }
  }

  public void clear() {
    a =  new int[1]; 
    N = 0;
  }

  public int[] toArray() {
    return take(a,N);
  }

  public int[] toEntireArray() {
    return a;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Stack(");
    for (int i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {

  }

}

