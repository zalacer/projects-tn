package ex13;

import static v.ArrayUtils.pa;

import ds.ResizingArrayDeque;

//  1.3.33 Deque. A double-ended queue or deque (pronounced “deck”) is like a stack or
//  a queue but supports adding and removing items at both ends. A deque stores a collec-
//  tion of items and supports the following API:
//    API for a generic double-ended queue:
//    public class Deque<Item> implements Iterable<Item>
//    Deque()  create an empty deque
//    boolean isEmpty()  is the deque empty?
//    int size()  number of items in the deque
//    void pushLeft(Item item)  add an item to the left end
//    void pushRight(Item item)  add an item to the right end
//    Item popLeft()  remove an item from the left end
//    Item popRight()  remove an item from the right end
//    Write a class Deque that uses a doubly-linked list to implement this API and a class
//    ResizingArrayDeque that uses a resizing array.

// push -> pushLeft     : add to left
// pop -> popLeft       : remove from left
// enqueue -> pushRight : add to right
// dequeue -> popLeft   : remove from left

// for the implementations see ex13.Deque.java and ex13.ResizingArrayDeque.java
// testing of them was done below in main

public class Ex1333Deque {

  public static void main(String[] args) {
//    Integer[] ia = {1,2,3};
//    Deque<Integer> d = new Deque<Integer>(ia);
//    System.out.println(d.cending());
//    // null<-Node(1)<->Node(2)<->Node(3)->null
//    System.out.println(d); //Deque(1,2,3)
//    System.out.println("d.first="+d.first()); //Node(1)
//    System.out.println("d.last="+d.last()); //Node(3)
//    d.pushLeft(4); System.out.println(d); //Deque(4,1,2,3)
//    System.out.println("d.first="+d.first()); //Node(4)
//    System.out.println("d.last="+d.last()); //Node(3)
//    System.out.println(d.popLeft());//4
//    System.out.println(d); //Deque(1,2,3)
//    d.pushRight(5); System.out.println(d); //Deque(1,2,3,5)
//    System.out.println(d.cending());
//    // null<-Node(1)<->Node(2)<->Node(3)<->Node(5)->null
//    System.out.println(d.popRight()); //5
//    System.out.println(d); //Deque(1,2,3)
//    System.out.println(d.popLeft()); //1
//    System.out.println(d); //Deque(2,3)
//    System.out.println("d.first="+d.first()); //Node(2)
//    System.out.println("d.last="+d.last()); //Node(3)
//    System.out.println(d.popRight()); //3
//    System.out.println(d); //Deque(2)
//    System.out.println("d.first="+d.first()); //Node(2)
//    System.out.println("d.last="+d.last()); //Node(2)
//    System.out.println(d.cending());
//    // null<-Node(2)->null
//    System.out.println(d.popRight()); //2
//    System.out.println(d); //Deque()
//    d.pushRight(1); System.out.println(d); //Deque(1)
//    System.out.println(d.popLeft()); //1
//    System.out.println(d); //Deque()
//    System.out.println(d.cending()); //(empty list)
    // all output for Deque tests:
    //  null<-Node(1)<->Node(2)<->Node(3)->null
    //  Deque(1,2,3)
    //  d.first=Node(1)
    //  d.last=Node(3)
    //  Deque(4,1,2,3)
    //  d.first=Node(4)
    //  d.last=Node(3)
    //  4
    //  Deque(1,2,3)
    //  Deque(1,2,3,5)
    //  null<-Node(1)<->Node(2)<->Node(3)<->Node(5)->null
    //  5
    //  Deque(1,2,3)
    //  1
    //  Deque(2,3)
    //  d.first=Node(2)
    //  d.last=Node(3)
    //  3
    //  Deque(2)
    //  d.first=Node(2)
    //  d.last=Node(2)
    //  null<-Node(2)->null
    //  2
    //  Deque()
    //  Deque(1)
    //  1
    //  Deque()
    //  (empty list)
    
    Integer[] ia = {1,2,3};
    ResizingArrayDeque<Integer> d = new ResizingArrayDeque<Integer>(ia);
    System.out.println(d); //(1,2,3)
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //3
    pa(d.getArray()); //Integer[1,2,3]
    d.pushLeft(4); System.out.println(d); //(4,1,2,3
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //4
    pa(d.getArray()); //Object[4,1,2,3,null,null]
    System.out.println(d.popLeft());//4
    System.out.println(d); //(1,2,3)
    System.out.println("d.getFirst="+d.getFirst()); //1
    System.out.println("d.getLast="+d.getLast()); //4
    pa(d.getArray()); //Object[null,1,2,3,null,null]
    d.pushRight(5); System.out.println(d); //Deque(1,2,3,5)
    System.out.println("d.getFirst="+d.getFirst()); //1
    System.out.println("d.getLast="+d.getLast()); //5
    pa(d.getArray()); //Object[null,1,2,3,5,null]
    d.pushLeft(0); System.out.println(d); //(0,1,2,3,5)
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //5
    pa(d.getArray()); //Object[0,1,2,3,5,null]
    d.pushLeft(-1); System.out.println(d); //(-1,0,1,2,3,5)
    System.out.println("d.getFirst="+d.getFirst()); //5
    System.out.println("d.getLast="+d.getLast()); //5
    pa(d.getArray()); //Object[0,1,2,3,5,-1]
    //branch 2: pushRight into full array
    d.pushRight(6); System.out.println(d); //(-1,0,1,2,3,5,6)
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //7
    pa(d.getArray()); //Object[-1,0,1,2,3,5,6,null,null,null,null,null]
    // branch 1 pushLeft into full array
//    pa(d.getArray()); //Object[0,1,2,3,5,-1]
//    d.pushLeft(-2); System.out.println(d); //(-2,-1,0,1,2,3,5)
//    System.out.println("d.getFirst="+d.getFirst()); //0
//    System.out.println("d.getLast="+d.getLast()); //7
//    pa(d.getArray()); //Object[-2,-1,0,1,2,3,5,null,null,null,null,null]
    System.out.println();
    System.out.println(d.popRight()); //6
    System.out.println(d.popLeft()); //-1
    System.out.println(d.popRight()); //5
    System.out.println(d.popLeft()); //0
    System.out.println(d.popRight()); //3
    System.out.println(d.popLeft()); //1
    System.out.println(d); //(2)
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //1
    pa(d.getArray()); //Object[2,null,null]
    System.out.println();
    // branch 3: remove last element with popLeft
    System.out.println(d.popLeft()); //2
    System.out.println(d); //()
    System.out.println("d.getFirst="+d.getFirst()); //0
    System.out.println("d.getLast="+d.getLast()); //0
    pa(d.getArray()); //Object[null,null,null]
    System.out.println(d.isEmpty()); //true
    
//    // branch 4: remove last element with popRight
//    System.out.println(d.popRight()); //2
//    System.out.println(d); //()
//    System.out.println("d.getFirst="+d.getFirst()); //0
//    System.out.println("d.getLast="+d.getLast()); //0
//    pa(d.getArray()); //Object[null,null,null]
//    System.out.println(d.isEmpty()); //true
       
  }

}
