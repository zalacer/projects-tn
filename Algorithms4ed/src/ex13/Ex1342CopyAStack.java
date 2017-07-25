package ex13;

import ds.Stack;
import v.Tuple2;

//  1.3.42 Copy a stack. Create a new constructor for the linked-list implementation of
//  Stack so that
//  Stack<Item> t = new Stack<Item>(s);
//  makes  t a reference to a new and independent copy of the stack  s 

// this is essentially a clone of the previous solution with a variation 
// in the constructor to get the nodes in FIFO order

//  // cloning Stack constructor
//  public Stack(Stack<Item> s2) {
//    if (s2 == null) return;
//    if (s2.isEmpty()) return; // create a new empty stack
//    iclass = s2.iclass(); // this means all s2's elements, if any, are null
//    Item[] ia = (Item[]) new Object[s2.size()];
//    int c = s2.size() - 1;
//    if (iclass != null) {
//      Method mClone = getCloneMethod(iclass);
//        if (mClone != null)
//          try {
//            for (Item i : s2) ia[c--] = i == null ? null : (Item) mClone.invoke((Item) i);         
//          } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
//            c = 0;
//            for (Item i : s2) ia[c--] = i;
//          }
//    } else for (Item i : s2) ia[c--] = i;
//    for (Item i : ia) push(i);
//  }

//  public static Method getCloneMethod(Class<?> c) {
//    // returns c.clone() method if it exists else null.
//    if( c == null) throw new IllegalArgumentException("getCloneMethod: "
//        + "argument can't be null");
//    
//    Method clone = null;
//    
//    try {
//      clone = c.getDeclaredMethod("clone");
//      return clone;
//    } catch (NoSuchMethodException | SecurityException e) {} 
//    
//    try {
//      clone = c.getMethod("clone");
//      return clone;
//    } catch (NoSuchMethodException | SecurityException e) {}
//    
//    return null;
//  }

public class Ex1342CopyAStack {

  public static void main(String[] args) {
    
    // this demonstrates a Stack constructor from another Stack which creates a new
    // Stack independent of the first as far as enqueuing and dequeuing, and also 
    // clones the Items in the first queue if they have a clone() method. If that clone
    // method produces deep copies, or if the Item type is immutable such as are the
    // boxed types and Strings, the Items in the two Queues are fully independent. 
    
    Stack<Tuple2<Integer,Integer>> s1 = new Stack<Tuple2<Integer,Integer>>();
    s1.push(new Tuple2<Integer,Integer>(5,6));
    s1.push(new Tuple2<Integer,Integer>(3,4));
    s1.push(new Tuple2<Integer,Integer>(1,2));
    System.out.println(s1); //Stack((1,2),(3,4),(5,6))
    
    // creating s2 from s1
    Stack<Tuple2<Integer,Integer>> s2 = new Stack<Tuple2<Integer,Integer>>(s1);
    System.out.println(s2); //Stack((1,2),(3,4),(5,6))
    
    Tuple2<Integer,Integer> s11 = s1.pop();
    System.out.println(s11); //(1,2)
    System.out.println(s1); //Stack((3,4),(5,6)) 
    System.out.println(s2); //Stack((1,2),(3,4),(5,6))
    
    Tuple2<Integer,Integer> s21 = s2.pop();
    // q11 and q21 are equals but different objects
    System.out.println(s21); //(1,2)
    System.out.println(System.identityHashCode(s11)); //1829164700
    System.out.println(System.identityHashCode(s21)); //2018699554
    assert s11.equals(s21);
    assert s11 != s21;
      
    Tuple2<Integer,Integer> s12 = s1.pop();
    System.out.println(s12); //(3,4)
    System.out.println(s1); //Stack((5,6))
    System.out.println(s2); //Stack((3,4),(5,6))
    
    Tuple2<Integer,Integer> s13 = s1.pop();
    System.out.println(s13); //(5,6)
  
    Tuple2<Integer,Integer> s22 = s2.pop();
    System.out.println(s22); //(3,4)
    Tuple2<Integer,Integer> s23 = s2.pop();
    System.out.println(s23); //(5,6)
    
    // s12 and s22 are different but equal objects
    System.out.println(System.identityHashCode(s12)); //1311053135
    System.out.println(System.identityHashCode(s22)); //118352462
    assert s12.equals(s22);
    assert s12 != s22;
    
    // s13 and s23 are different but equal objects
    System.out.println(System.identityHashCode(s13)); //1550089733
    System.out.println(System.identityHashCode(s23)); //865113938
    assert s13.equals(s23);
    assert s13 != s23;
    
    
  }

}
