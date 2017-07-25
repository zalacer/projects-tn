package ds;

//for use in DoublyLinkedList2 for ex4445

public class Node2<Item> {
  private Item v;
  private Item w;
  private Node2<Item> next;
  private Node2<Item> prev;
  
  public Node2() {}

  public Node2(Item v) {
    this.v = v;
  }
  
  public Node2(Item v, Item w) {
    this.v = v;
    this.w = w;
  }
  
  public Node2(Item v, Item w, Node2<Item> next, Node2<Item> prev) {
    this.v = v;
    this.w = w;
    this.next = next;
    this.prev = prev;
  }
  
  public Item getV() { return v; }
  
  public Item v() { return v; }
  
  public void setV(Item v) { this.v = v; }
  
  public Item getW() { return w; }
  
  public Item w() { return w; }

  public void setW(Item w) { this.w = w; }
  
  public Node2<Item> getNext() { return next; }
  
  public Node2<Item> next() { return next; }

  public void setNext(Node2<Item> next) { this.next = next; }

  public Node2<Item> getPrev() { return prev; }
  
  public Node2<Item> prev() { return prev; }

  public void setPrev(Node2<Item> prev) { this.prev = prev; }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((next == null) ? 0 : next.hashCode());
    result = prime * result + ((prev == null) ? 0 : prev.hashCode());
    result = prime * result + ((v == null) ? 0 : v.hashCode());
    result = prime * result + ((w == null) ? 0 : w.hashCode());
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
    Node2 other = (Node2) obj;
    if (next == null) {
      if (other.next != null)
        return false;
    } else if (!next.equals(other.next))
      return false;
    if (prev == null) {
      if (other.prev != null)
        return false;
    } else if (!prev.equals(other.prev))
      return false;
    if (v == null) {
      if (other.v != null)
        return false;
    } else if (!v.equals(other.v))
      return false;
    if (w == null) {
      if (other.w != null)
        return false;
    } else if (!w.equals(other.w))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Node2("+v+","+w+","+next+","+prev+")";
  }

  public static void main(String[] args) {

  }

}
