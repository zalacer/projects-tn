package ex13;

import ds.Queue;
import v.Tuple2;

//  1.3.41 Copy a queue. Create a new constructor so that
//  Queue<Item> r = new Queue<Item>(q);
//  makes  r a reference to a new and independent copy of the queue  q . You should be able
//  to push and pop from either  q or  r without influencing the other. Hint : Delete all of the
//  elements from  q and add these elements to both  q and r.

//  // cloning Queue constructor
//  public Queue(Queue<Item> q2) {
//    // construct a Queue from a Queue
//    if (q2 == null) throw new IllegalArgumentException("Queue constructor: "
//        + "cannot construct a new Queue from a null Queue");
//    if (q2.isEmpty()) return; // create a new empty queue
//    iclass = q2.iclass(); // if q2.iclass() == null all q2's elements are null
//    if (iclass != null) {
//      Method mClone = getCloneMethod(iclass);
//        if (mClone != null)
//          try {
//            for (Item i : q2) 
//              if (i == null) {
//                enqueue(null);
//              } else enqueue((Item) mClone.invoke((Item) i));
//          } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
//            first = null; // in case something got enqueued, start from scratch
//            for (Item i : q2) enqueue(i);
//          }
//    } else for (Item i : q2) enqueue(i);
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

public class Ex1341CopyAQueue {

  public static void main(String[] args) {
    
    // this demonstrates a Queue constructor from another Queue which creates a new
    // Queue independent of the first as far as enqueuing and dequeuing, and also 
    // clones the Items in the first queue if they have a clone() method. If that clone
    // method produces deep copies, or if the Item type is immutable such as are the
    // boxed types and Strings, the Items in the two Queues are fully independent. 
    
    Queue<Tuple2<Integer,Integer>> q1 = new Queue<Tuple2<Integer,Integer>>();
    q1.enqueue(new Tuple2<Integer,Integer>(1,2));
    q1.enqueue(new Tuple2<Integer,Integer>(3,4));
    q1.enqueue(new Tuple2<Integer,Integer>(5,6));
    System.out.println(q1); //Queue((1,2),(3,4),(5,6))
    
    // creating q2 from q1
    Queue<Tuple2<Integer,Integer>> q2 = new Queue<Tuple2<Integer,Integer>>(q1);
    System.out.println(q2); //Queue((1,2),(3,4),(5,6))
    
    Tuple2<Integer,Integer> q11 = q1.dequeue();
    System.out.println(q11); //(1,2)
    System.out.println(q1); //Queue((3,4),(5,6)) 
    System.out.println(q2); //Queue((1,2),(3,4),(5,6))
    
    Tuple2<Integer,Integer> q21 = q2.dequeue();
    // q11 and q21 are equals but different objects
    System.out.println(q21); //(1,2)
    System.out.println(System.identityHashCode(q11)); //2018699554
    System.out.println(System.identityHashCode(q21)); //1829164700
    assert q11.equals(q21);
    assert q11 != q21;
      
    Tuple2<Integer,Integer> q12 = q1.dequeue();
    System.out.println(q12); //(3,4)
    System.out.println(q1); //Queue((5,6))
    System.out.println(q2); //Queue((3,4),(5,6))
    
    Tuple2<Integer,Integer> q13 = q1.dequeue();
    System.out.println(q13); //(5,6)
  
    Tuple2<Integer,Integer> q22 = q2.dequeue();
    System.out.println(q22); //(3,4)
    Tuple2<Integer,Integer> q23 = q2.dequeue();
    System.out.println(q23); //(5,6)
    
    // q12 and q22 are different but equal objects
    System.out.println(System.identityHashCode(q12)); //118352462
    System.out.println(System.identityHashCode(q22)); //1311053135
    assert q12.equals(q22);
    assert q12 != q22;
    
    // q13 and q23 are different but equal objects
    System.out.println(System.identityHashCode(q13)); //1550089733
    System.out.println(System.identityHashCode(q23)); //865113938
    assert q13.equals(q23);
    assert q13 != q23;
    
    
  }

}
