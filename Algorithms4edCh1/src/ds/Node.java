package ds;

public class Node<Item>{
  Item item;
  Node<Item> next;
  
  public Node(){};
  
  public Node(Item item, Node<Item> next) {
    this.item = item; this.next = next;
  }

  public Item item() {
    return item;
  }
  
  public Item getItem() {
    return item;
  }
  
  public Node<Item> next() {
    return next;
  }
  
  public Node<Item> getNext() {
    return next;
  }

  @Override
  public String toString() {
    return "Node("+item+")";
  }
  
  public static void main(String[] args) {
   
  }
}