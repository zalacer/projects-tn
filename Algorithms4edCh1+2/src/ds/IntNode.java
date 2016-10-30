package ds;

public class IntNode {
    int item;
    IntNode next;
    
    public IntNode(){};
    
    public IntNode(int item, IntNode next) {
      this.item = item; this.next = next;
    }

    public int item() {
      return item;
    }
    
    public int getItem() {
      return item;
    }
    
    public IntNode next() {
      return next;
    }
    
    public IntNode getNext() {
      return next;
    }
    
    @Override
    public String toString() {
      return "Node("+item+")";
    }
    
    public static void main(String[] args) {

    }
    
  }


