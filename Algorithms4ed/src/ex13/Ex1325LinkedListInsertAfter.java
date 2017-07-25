package ex13;

import ds.LinkedList.Node;
import ds.LinkedList;

//  1.3.25  Write a method  insertAfter() that takes two linked-list  Node arguments and
//  inserts the second after the first on its list (and does nothing if either argument is null).

//  public boolean insertAfter(Node<T> node, Node<T> after) {
//    // insert after after node except if either is null
//    // or node isn't in the list
//    if (node == null || after == null) return false;
//    after.next = node.next;
//    node.next = after;
//    if (after.next == null) last = after;
//    N++;
//    return true;
//  }

public class Ex1325LinkedListInsertAfter {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    Node<Integer> node3 = ll.get(3);
    Node<Integer> node9 = ll.getNodeInstance(9);
    if (ll.insertAfter(node3, node9)) System.out.println("node 9 inserted after node3");
    // node 9 inserted after node3
    System.out.println(ll); //LinkedList(1,2,3,9,4,5)
    System.out.println("size="+ll.size()); //size=6
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    Node<Integer> node5 = ll.get(6);
    System.out.println(node5);
    Node<Integer> node6 = ll.getNodeInstance(6);
    if (ll.insertAfter(node5, node6)) System.out.println("node 6 inserted after node5");
    // node 6 inserted after node5
    System.out.println(ll); //LinkedList(1,2,3,9,4,5,6)
    System.out.println("size="+ll.size()); //size=7
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(6)
    
    ll.removeAfter(node3);
    ll.insertAfter(node6, ll.getNodeInstance(7));
    ll.insertAfter(node6.next(), ll.getNodeInstance(8));
    ll.insertAfter(node6.next().next(), node9);
    System.out.println(ll); //LinkedList(1,2,3,4,5,6,7,8,9)
    System.out.println("size="+ll.size()); //size=9
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(9)
 
  }

}
