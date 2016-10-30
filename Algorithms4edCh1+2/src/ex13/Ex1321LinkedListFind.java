package ex13;

import ds.LinkedList;

//  1.3.21  Write a method  find() that takes a linked list and a string  key as arguments
//  and returns  true if some node in the list has  key as its item field,  false otherwise.

//  public static <T> boolean find(LinkedList<T> ll, T it) {
//    //returns true if ll contains it else false
//    return ll.contains(it);
//  }
//  
//  public boolean contains(Item it) {
//    // returns true if it is in this list else false
//    if (isEmpty()) return false;
//    Node node = first;
//    if (node.item.equals(it)) return true;
//    while (node.next != null) {
//      node = node.next;
//      if (node.item.equals(it)) return true;
//    }
//    return false;
//  }

public class Ex1321LinkedListFind {

  public static void main(String[] args) {
    
    String[] sa = {"1","2","3","4","5"};
    LinkedList<String> ll = new LinkedList<String>(sa);
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    for (String s : sa) System.out.println(LinkedList.find(ll, s));
    //  true
    //  true
    //  true
    //  true
    //  true
    System.out.println(LinkedList.find(ll, "not here")); //false


  
    
  }

}
