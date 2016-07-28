package ex13;

import ds.Steque;

//  1.3.32 Steque. A stack-ended queue or steque is a data type that supports push, pop, and
//  enqueue. Articulate an API for this ADT. Develop a linked-list-based implementation.

//  Steque API for linked-list implementation:
//  public class Steque<Item> implements Iterator<Item>
//  Steque(): constructor to create an empty steque 
//  Steque(Item[]): constructor to create a steque pushing items from an array
//  Steque(Steque<Item): constructor to create a steque from another
//  void push(Item item): add an item  to the top
//  Item pop(): remove the top item
//  void enqueue(Item item): add an item to the bottom
//  Item peek(): return the first item without removing it
//  Item pook(): return the last item without removing it
//  boolean isEmpty(): is the steque empty ?
//  int size(): number of items on the steque
//  Iterator<Item> iterator(); returns an Iterator<Item> for the steque
//  Item[] toArray(Item...): return Item[] corresponding to steque contents
//  Object[] toArray(): return Object[] of Items corresponding to steque contents
//  int hashCode(): return the hashcode of the steque
//  boolean equals(Object): return value of equality comparison with another object
//  String toString(): return a string representation of the steque


public class Ex1332Steque {

  public static void main(String[] args) {
    
    Integer[] ia = {1,2,3,4,5};
    Steque<Integer> st = new Steque<Integer>(ia);
    System.out.println(st); //Steque(1,2,3,4,5)
    System.out.println(st.size()); //5
    System.out.println(st.first()); //1
    System.out.println(st.last()); //5
    st.push(6);
    System.out.println(st); //Steque(6,1,2,3,4,5)
    System.out.println(st.size()); //6
    System.out.println(st.first()); //6
    System.out.println(st.last()); //5
    st.enqueue(7);
    System.out.println(st); //Steque(6,1,2,3,4,5,7)
    System.out.println(st.size()); //7
    System.out.println(st.first()); //6
    System.out.println(st.last()); //7

    st.pop(); st.pop(); st.pop(); st.enqueue(9); st.pop(); st.push(11);
    System.out.println(st); //Steque(11,4,5,7,9)
    System.out.println(st.size()); //5
    System.out.println(st.first()); //11
    System.out.println(st.last()); //9


  }

}
