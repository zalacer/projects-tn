package ds;

// for use in DoublyLinkedList3 for ex4445

public class Node3 {
  private int v;
  private int w;
  private Node3 next;
  private Node3 prev;
  
  public Node3() {}

  public Node3(int v) {
    this.v = v;
  }
  
  public Node3(int v, int w) {
    this.v = v;
    this.w = w;
  }
  
  public Node3(int v, int w, Node3 next, Node3 prev) {
    this.v = v;
    this.w = w;
    this.next = next;
    this.prev = prev;
  }
  
  public int getV() { return v; }
  
  public int v() { return v; }
  
  public void setV(int v) { this.v = v; }
  
  public int getW() { return w; }
  
  public int w() { return w; }

  public void setW(int w) { this.w = w; }
  
  public Node3 getNext() { return next; }
  
  public Node3 next() { return next; }

  public void setNext(Node3 next) { this.next = next; }

  public Node3 getPrev() { return prev; }
  
  public Node3 prev() { return prev; }

  public void setPrev(Node3 prev) { this.prev = prev; }

  @Override
  public String toString() {
    return "Node2("+v+","+w+","+next+","+prev+")";
  }

  public static void main(String[] args) {

  }

}
