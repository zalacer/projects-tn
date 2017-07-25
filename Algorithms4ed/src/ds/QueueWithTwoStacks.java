package ds;

import static v.ArrayUtils.*;
import java.util.Iterator;
import java.util.NoSuchElementException;


// Ex1.4.27, p211-212
// based on http://algs4.cs.princeton.edu/13stacks/QueueWithTwoStacks.java

public class QueueWithTwoStacks<Item> implements Iterable<Item> {
  // for this queue enqueueing is done to rear and dequeueing is from  front
  // when peek or dequeue is called and front is empty all in rear are moved 
  // to front. each queue operation takes a constant amortized number of stack 
  // operations
  private Stack<Item> front; 
  private Stack<Item> rear; 

  public QueueWithTwoStacks() {
    front = new Stack<Item>();
    rear = new Stack<Item>();
  }
  
  @SafeVarargs
  public QueueWithTwoStacks(Item...items) {
    front = new Stack<Item>();
    rear = new Stack<Item>();
    if (items != null && items.length > 0)
      for (int i = items.length-1; i > -1; i--) front.push(items[i]);  
  }
  
  private void moveToFront() {
    // relocate all items in rear to front which reverses
    // their order a 2nd time so they come out FIFO
    while (!rear.isEmpty()) front.push(rear.pop());
  }

  public boolean isEmpty() {
    // return true only if rear and front are empty else return false
    return rear.isEmpty() && front.isEmpty();
  }

  public int size() {
    // return the total number of items in rear and front
    return rear.size() + front.size();     
  }

  public Item peek() {
    // return the head of front without popping it and after
    // relocating all items from rear to front if it's empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    if (front.isEmpty()) moveToFront();
    return front.peek();
  }

  public void enqueue(Item item) {
    //push item to the top of rear
    rear.push(item);
  }

  public Item dequeue() {
    // remove and return the head of front after relocating
    // all items from rear to front if it's empty
    if (isEmpty()) throw new NoSuchElementException("Queue underflow");
    if (front.isEmpty()) moveToFront();
    return front.pop();
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
    return append(front.toArray(), reverse(rear.toArray()));
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...items) {
    return append(front.toArray(items), reverse(rear.toArray(items)));
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((front == null) ? 0 : front.hashCode());
    result = prime * result + ((rear == null) ? 0 : rear.hashCode());
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
    QueueWithTwoStacks other = (QueueWithTwoStacks) obj;
    if (front == null) {
      if (other.front != null)
        return false;
    } else if (!front.equals(other.front))
      return false;
    if (rear == null) {
      if (other.rear != null)
        return false;
    } else if (!rear.equals(other.rear))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("QueueWithTwoStacks(");
    Object[] a = toArray();
    if (a.length==0) return sb.append(")").toString();
    for (Object i : a) sb.append(i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {

  }
}

