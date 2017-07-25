package ex13;

import ds.LinkedList;

//  1.3.27  Write a method  max() that takes a reference to the first node in a linked list as
//  argument and returns the value of the maximum key in the list. Assume that all keys are
//  positive integers, and return  0 if the list is empty.

//  public static <T extends Comparable<T>> T max(LinkedList<T>.Node first) {
//    if (first == null) throw new IllegalArgumentException("max: first must be non null");
//    if (first.next == null) return first.item;
//    T max = first.item;
//    LinkedList<T>.Node node = first;
//    while(node.next != null) {
//      node = node.next;
//      if (node.item.compareTo(max) > 0) max = node.item;
//    }
//    return max;
//  }

public class Ex1327LinkedListMax {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,5,4,3});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println(LinkedList.max(ll.first())); //5
 
 
  }

}
