package utils;

import java.util.Collection;
import java.util.LinkedList;

// Ch02.16. Implement a class Queue, an unbounded queue of strings. Provide methods add,
// adding at the tail, and remove, removing at the head of the queue. Store elements
// as a linked list of nodes. Make Node a nested class. Should it be static or not?

public class Queue {
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

  public Queue() {};

  public Queue(Collection<? extends String> c) {
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
    Queue other = (Queue) obj;
    if (ll == null) {
      if (other.ll != null)
        return false;
    } else if (!ll.equals(other.ll))
      return false;
    return true;
  }




}
