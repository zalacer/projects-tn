package ex13;

import ds.LinkedList;
import ds.LinkedList.Node;

//  1.3.24  Write a method  removeAfter() that takes a linked-list  Node as argument and
//  removes the node following the given one (and does nothing if the argument or the next
//  field in the argument node is null).

//  public Node<T> removeAfter(Node<T> node) {
//    // removes the Node after node except if node, node.next is null
//    // or node isn't in the list
//    if (node == null || node.next == null) return null;
//    Node<T> after = node.next;
//    node.next = node.next.next;
//    if (node.next == null) last = node;
//    N--;
//    return after;
//  }

public class Ex1324LinkedListRemoveAfter {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    Node<Integer> node3 = ll.get(3);
    System.out.println(node3);  //Node(3)   
    if (ll.removeAfter(node3).item() == 4) System.out.println("node after node3 removed");
    // node after node3 removed
    System.out.println(ll); //LinkedList(1,2,3,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    if (ll.removeAfter(node3).item() == 5) System.out.println("node after node3 removed");
    // node after node3 removed
    System.out.println(ll); //LinkedList(1,2,3)
    System.out.println("size="+ll.size()); //size=3
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(3)
    
    Node<Integer> node2 = ll.get(2);
    System.out.println(node2);  //Node(2)
    if (ll.removeAfter(node2).item() == 3) System.out.println("node after node2 removed");
    // node after node2 removed
    System.out.println(ll); //LinkedList(1,2)
    System.out.println("size="+ll.size()); //size=2
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(2)
    
    Node<Integer> node1 = ll.get(1);
    System.out.println(node1);  //Node(1)
    if (ll.removeAfter(node1).item() == 2) System.out.println("node after node1 removed");
    // node after node1 removed
    System.out.println(ll); //LinkedList(1)
    System.out.println("size="+ll.size()); //size=1
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(1)
    
    if (ll.removeAfter(node1) != null) {
      System.out.println("node after node1 removed");
    } else System.out.println("node after node1 not removed");
    // node after node1 not removed     
  }

}
