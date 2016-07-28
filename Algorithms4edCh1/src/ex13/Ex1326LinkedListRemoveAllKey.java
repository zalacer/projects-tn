package ex13;

import ds.LinkedList;

//  1.3.26  Write a method  remove() that takes a linked list and a string key as arguments
//  and removes all of the nodes in the list that have  key as its item field.

//  public static <T> int remove(LinkedList<T> list, T t) {
//    // remove all elements equalling t in list and return the number removed
//    if (list == null || t == null) throw new IllegalArgumentException(
//        "remove: all arguments must be non null");
//    if (list.isEmpty()) return 0;
//    int c = 0;
//    LinkedList<T>.Node first = list.first;
//    if (first.item.equals(t)) {
//      list.removeFirst();
//      c+= 1 + remove(list, t);
//      if (list.isEmpty()) return c;
//    }
//    // at this point either the list is empty and processing has completed
//    // or its first element doesn't match t
//    
//    LinkedList<T>.Node last = list.last;
//    if (last.item.equals(t)) {
//      list.removeLast();
//      c+= 1 + remove(list, t);
//    }
//    // at this point either the list is empty and procesing has completed
//    // or neither its first or last elements match t
//    
//    LinkedList<T>.Node node = list.first;
//    LinkedList<T>.Node previous = node;
//    while(node.next != null) {
//      node = node.next;
//      if (node.item.equals(t)) {
//        list.removeAfter(previous);
//        c++;
//        node = previous;
//      }
//      previous = node;
//    }
//    return c;
//  }

public class Ex1326LinkedListRemoveAllKey {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    // test removing one element in the middle
    System.out.println(LinkedList.remove(ll, 3)); //1
    System.out.println(ll); //LinkedList(1,2,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    // test removing the first element
    System.out.println(LinkedList.remove(ll, 1)); //1
    System.out.println(ll); //LinkedList(2,3,4,5)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(2)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
 
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    // test removing the last element
    System.out.println(LinkedList.remove(ll, 5)); //1
    System.out.println(ll); //LinkedList(1,2,3,4)
    System.out.println("size="+ll.size()); //size=4
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(4)
    
    ll = new LinkedList<Integer>(new Integer[]{3,2,3,4,5});
    // test removing the first and middle elements
    System.out.println(LinkedList.remove(ll, 3)); //2
    System.out.println(ll); //LinkedList(2,4,5)
    System.out.println("size="+ll.size()); //size=3
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(2)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,3});
    // test removing the middle and last elements
    System.out.println(LinkedList.remove(ll, 3)); //2
    System.out.println(ll); //LinkedList(1,2,4)
    System.out.println("size="+ll.size()); //size=3
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(4)
    
    ll = new LinkedList<Integer>(new Integer[]{3,2,3,4,3});
    // test removing the first, middle and last elements
    System.out.println(LinkedList.remove(ll, 3)); //3
    System.out.println(ll); //LinkedList(2,4)
    System.out.println("size="+ll.size()); //size=2
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(2)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(4)
    
    ll = new LinkedList<Integer>(new Integer[]{3,3,3,3,4});
    // test removing all elements except the last
    System.out.println(LinkedList.remove(ll, 3)); //4
    System.out.println(ll); //LinkedList(4)
    System.out.println("size="+ll.size()); //size=1
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(4)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(4)
    
    ll = new LinkedList<Integer>(new Integer[]{2,3,3,3,3});
    // test removing all elements except the first
    System.out.println(LinkedList.remove(ll, 3)); //4
    System.out.println(ll); //LinkedList(2)
    System.out.println("size="+ll.size()); //size=1
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(2)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(2)
    
    ll = new LinkedList<Integer>(new Integer[]{3,3,3,3,3});
    // test removing all elements
    System.out.println(LinkedList.remove(ll, 3)); //5
    System.out.println(ll); //LinkedList()
    System.out.println("size="+ll.size()); //size=0
    System.out.println("getFirst="+ll.getFirst()); //getFirst=null
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=null
    
    ll = new LinkedList<Integer>(new Integer[]{1,2,3,4,5});
    // test removing no elements
    System.out.println(LinkedList.remove(ll, 7)); //0
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println("size="+ll.size()); //size=5
    System.out.println("getFirst="+ll.getFirst()); //getFirst=Node(1)
    System.out.println("getLast="+ll.getLast()+"\n"); //getLast=Node(5)
    
    
  }

}
