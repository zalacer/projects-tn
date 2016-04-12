package utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Iterator;

// Ch02.17. Provide an iterator—an object that yields the elements of the queue in 
// turn—for the queue of the preceding class. Make Iterator a nested class with methods 
// next and hasNext. Provide a method iterator() of the Queue class that yields a
// Queue.Iterator. Should Iterator be static or not?

//I found it not desirable to make Iterator static. It worked when static for a single 
//Queue instance but that had the side-effect of requiring the private LinkedList to be 
//static which does not allow there to be multiple Queues each with different data.

public class IterableQueue {
  private class Node {
    String value;

    Node(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "Node(" +value+ ")";
    }
  }

  private LinkedList<Node> ll = new LinkedList<Node>();

  private class QueueIterator implements Iterator<String> {
    @Override
    public boolean hasNext() {
      return Objects.isNull(ll.peek()) ? false : true;
    }

    @Override
    public String next() {
      return ll.remove().value;
    }       
  }

  public IterableQueue() {};

  public IterableQueue(Collection<? extends String> c) {
    for (String s: c) {
      ll.add(new Node(s));
    }
  }

  public boolean add(String s) {
    return ll.add(new Node(s));
  }

  public String remove() {
    return ll.remove().value;
  }

  public Iterator<String> iterator() {
    return new QueueIterator();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Queue(");
    if (ll.size() == 0) {
      builder.append(")");
      return builder.toString();
    }           
    for(int i = 0; i < ll.size() - 1; i++)
      builder.append(ll.get(i).value +", ");
    builder.append(ll.get(ll.size() - 1).value +")");
    return builder.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ll == null) ? 0 : ll.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IterableQueue other = (IterableQueue) obj;
    if (ll == null) {
      if (other.ll != null)
        return false;
    } else if (!ll.equals(other.ll))
      return false;
    return true;
  }

}
