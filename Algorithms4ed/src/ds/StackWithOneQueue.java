package ds;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

//p212
//1.4.28 Stack with a queue. Implement a stack with a single queue so that each stack
//operations takes a linear number of queue operations. Hint : To delete an item, get all
//of the elements on the queue one at a time, and put them at the end, except for the last
//one which you should delete and return. (This solution is admittedly very inefficient.)

public class StackWithOneQueue<Item> implements Iterable<Item> {
  // in this implementation the tail of the queue is equivilant to the head of the
  // stack. popping is done by dequeing and enqueueing all items except the last
  // (for the queue == most recently added node) then dequeuing and returing the it; 
  // peeking is done similarly except after returning the last its enqueued; pushing 
  // is done just by enqueing.
  private Queue<Item> q;
  
  public StackWithOneQueue() {
    q = new Queue<Item>();
  }
  
  @SafeVarargs
  public StackWithOneQueue(Item...items) {
    q = new Queue<Item>(items);
  }
  
  public boolean isEmpty() { return q.isEmpty(); } 
  
  public void clear() { q.clear(); }

  public int size() { return q.size(); }

  public void push(Item item) { 
    // Add item to top of stack == end of queue
    q.enqueue(item);
  }

  public Item pop() { // Remove item from top of stack == end of queue
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    int s = size();
    for (int i = 0; i < s-1; i++) q.enqueue(q.dequeue());
    return q.dequeue();
  }

  public Item peek() {
    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
    int s = size();
    for (int i = 0; i < s-1; i++) q.enqueue(q.dequeue());
    Item item = q.dequeue();
    q.enqueue(item);
    return item;
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
 
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((q == null) ? 0 : q.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    StackWithOneQueue other = (StackWithOneQueue) obj;
    if (q == null) {
      if (other.q != null)
        return false;
    } else if (!q.equals(other.q))
      return false;
    return true;
  }

  public Object[] toArray() {
    return reverse(q.toArray());
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return reverse(q.toArray(items));
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("StackWithOneQueue(");
    if (isEmpty()) sb.append(")");
    Object[] a = toArray();
    for (Object i : a) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    
 

  }

}

