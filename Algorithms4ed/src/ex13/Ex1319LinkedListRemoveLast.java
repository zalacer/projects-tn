package ex13;

import ds.LinkedList;

//  1.3.19  Give a code fragment that removes the last node in a linked list whose first node
//  is  first 

//  public Item removeLast() { // Remove item from the end of the list.
//    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
//    Node node = first;
//    Node previous = null;
//    Item item = null;
//    while (node.next != null) {
//      previous = node;
//      node = node.next;
//      item = node.item;
//    }
//    if (previous == null) {
//      first = previous;
//    } else previous.next = null;
//    last = previous;
//    if (isEmpty()) last = null;
//    N--;
//    return item;
//  }

public class Ex1319LinkedListRemoveLast {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=1
    System.out.println("getLast="+ll.getLast()); //getLast=5
    
    ll.removeLast();
    System.out.println(ll); //LinkedList(1,2,3,4)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=1
    System.out.println("getLast="+ll.getLast()); //getLast=4
    
    

  }

}
