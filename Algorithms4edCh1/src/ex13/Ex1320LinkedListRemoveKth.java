package ex13;

import ds.LinkedList;

//  1.3.20  Write a method  delete() that takes an  int argument  k and deletes the  k th ele-
//  ment in a linked list, if it exists.

//  public Item delete(int k) {
//    // removes the kth element counting from first which is the 1st element
//    if (k < 1) throw new IllegalArgumentException("delete: k must be > 0");
//    if (isEmpty()) throw new NoSuchElementException("LinkedList underflow");
//    if (size() < k) throw new NoSuchElementException("LinkedList has "
//        + "fewer than "+k+" elements");
//    if (k == 1) return remove();
//    if (k == size()) return removeLast();
//    Node node = first;
//    Node previous = null;
//    Item item = null;
//    int i = 1;
//    while (node.next != null && i < k) {
//      previous = node;
//      node = node.next;
//      item = node.item;
//      i++;
//    }
//    if (previous == null) {
//      first = previous;
//    } else previous.next = previous.next.next;
//    if (isEmpty()) last = null;
//    N--;
//    return item;
//  }

public class Ex1320LinkedListRemoveKth {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=1
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=5
    
    ll.delete(3); // showing deletion of a middle element
    System.out.println(ll); //LinkedList(1,2,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=1
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=5
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    
    ll.delete(5); // showing deletion of last element
    System.out.println("\n"+ll); //LinkedList(1,2,3,4)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=1
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=4
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    
    ll.delete(1); // showing deletion of first element when first != last
    System.out.println("\n"+ll); //LinkedList(2,3,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=2
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=5
    
    ll = new LinkedList<Integer>(new Integer[]{1});
    
    ll.delete(1); // showing deletion of first element when first == last
    System.out.println("\n"+ll); //LinkedList()
    System.out.println("size="+ll.size()); //size=0
    System.out.println("getFirst="+ll.getFirst()); //getFirst=null
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=null
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});

    // ll.delete(9); // showing exception for an impossible deletion
    //  Exception in thread "main" java.util.NoSuchElementException: 
    //    LinkedList has fewer than 9 elements
    
  }

}
