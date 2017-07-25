package ex13;

import ds.LinkedList;

//  1.3.20  Write a method  delete() that takes an  int argument  k and deletes the  k th 
//  element in a linked list, if it exists.

//  Doing this with 0 as first index for consistency with typical Java indexing.

//  public Node<T> delete(int k) {
//    // remove the kth element counting from first which is the 0th element
//    if (k < 0) throw new IllegalArgumentException("get: k must be >= 0");
//    if (isEmpty()) throw new NoSuchElementException("get: LinkedList underflow");
//    if (size()-1 < k) 
//      throw new IndexOutOfBoundsException("get: LinkedList has max index "+(k-1));
//    if (k == 0) return removeFirst();
//    if (k == size()-1) return removeLast();
//    Node<T> previous = null;
//    Node<T> node = first;
//    int i = 0;
//    while (i < k) {
//      previous = node;
//      node = node.next;
//      i++;
//    }
//    previous.next = node.next;
//    return node;
//  }

public class Ex1320LinkedListRemoveKth {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    ll.delete(2); // showing deletion of a middle element
    System.out.println(ll); //LinkedList(1,2,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    
    ll.delete(4); // showing deletion of last element
    System.out.println("\n"+ll); //LinkedList(1,2,3,4)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(4)
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    
    ll.delete(0); // showing deletion of first element when first != last
    System.out.println("\n"+ll); //LinkedList(2,3,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(2)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    ll = new LinkedList<Integer>(new Integer[]{1});
    
    ll.delete(0); // showing deletion of first element when first == last
    System.out.println(ll); //LinkedList()
    System.out.println("size="+ll.size()); //size=0
    System.out.println("getFirst="+ll.getFirst()); //getFirst=null
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=null
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});

    // ll.delete(9); // showing exception for an impossible deletion
    //  Exception in thread "main" java.util.NoSuchElementException: 
    //    LinkedList has fewer than 9 elements
    
  }

}
