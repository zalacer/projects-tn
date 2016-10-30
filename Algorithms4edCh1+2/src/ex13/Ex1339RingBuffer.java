package ex13;

import static v.ArrayUtils.pa;

import java.util.Iterator;

import ds.RingBuffer;

//  p169
//  1.3.39 Ring buffer. A ring buffer, or circular queue, is a FIFO data structure of a fixed
//  size N. It is useful for transferring data between asynchronous processes or for storing
//  log files. When the buffer is empty, the consumer waits until data is deposited; when the
//  buffer is full, the producer waits to deposit data. Develop an API for a  RingBuffer and
//  an implementation that uses an array representation (with circular wrap-around).

// RingBuffer API
// RingBuffer implements LIFO semantics with add and remove operations and is not resizable.
// All constructors and methods listed below are public.
// class signature: public class RingBuffer<T> implements Iterable<T>
// RingBuffer() : create an empty RingBuffer with capacity capacity (currently 9)
// RingBuffer(int length) : create an empty RingBuffer with capacity length
// RingBuffer(T[] b): create a RingBuffer initialized with elements of b and capacity b.length
// int size() : return the number of elements in the RingBuffer
// boolean empty(): return true if the RingBuffer has no elements else return false
// boolean isFull(): return true if the RingBuffer is using all available capacity else false
// int remaining(): return the current amount of unused capacity
// void add(T t): add the element t to the end of the RingBuffer
// int add(T[] ta): add at most ta.length elements from ta consecutively to the end of the 
//                  RingBuffer and return the number added
// T remove(): remove an element from the beginning of the RingBuffer and return it
// T[] remove(int k): remove at most k elements from the beginning of the RingBuffer and
//                     return them in a T[] of length at most k
// T peek(): return the first element of the RingBuffer, if possible, without removing it,
//            or throw a NoSuchElementException
// T peek(int k): return the kth element from the beginning of the RingBuffer, if possible, 
//                without removing it, or throw a NoSuchElementException
// Iterator<T> iterator(): return an Iterator<T>
// T[] toArray() : return the contents of the RingBuffer as a T[]
// Object[] getArray(): return the entire underlying array of the RingBuffer (for debugging)
// int hashCode(): calculate and return the RingBuffer's hashCode
// boolean equals(Object obj): compare this RingBuffer with obj for equality
// String toString(): return a string representation of this RingBuffer

public class Ex1339RingBuffer {
  
  public static void main(String[] args) {
    // this demonstrates RingBuffer's constructors, adding a single element, bulk 
    // addition of elements from an array, removing a single element, bulk removal,
    // of a range of element starting from a given index, isEmpty(), isFull(),
    // remaining(), size(), peek with no args, peek of a specific index, iterator(),
    // toArray(), getArray(), hashCode(), equals() and toString().
    
    Integer[] ia = {1,2,3,4,5,6,7,8,9};
    RingBuffer<Integer> r = new RingBuffer<Integer>(); // 0 arg constructor
    System.out.println(r.isEmpty()); //true
    System.out.println(r.getArray().length); //9 = default initialization capacity
    for(Integer i : ia) r.add(i); //add elements
    System.out.println(r.isEmpty()); //false
    System.out.println(r.isFull()); //true
    System.out.println(r.size()); //9
    System.out.println(r); //RingBuffer(1,2,3,4,5,6,7,8,9)
    System.out.println(r.toString()); //RingBuffer(1,2,3,4,5,6,7,8,9)
    System.out.println();
    
    RingBuffer<Integer> r2 = new RingBuffer<Integer>(ia); // array arg constructor
    System.out.println(r2); //RingBuffer(1,2,3,4,5,6,7,8,9)
    System.out.println(r.equals(r2)); //true
    System.out.println(r.hashCode()); //-1631064427
    System.out.println(r2.hashCode()); //-1631064427
    RingBuffer<Integer> r3 = new RingBuffer<Integer>(19); // int arg constructor
    System.out.println(r3.remaining()); //19
    r3.add(31); r3.add(97); r3.add(52);
    System.out.println(r3.hashCode()); //-938891195
    System.out.println(r3.equals(r)); //false
    System.out.println(r3.equals(r2)); //false
    System.out.println();
    
    System.out.println(r.remove()); //1
    System.out.println(r.size()); //8
    System.out.println(r); //RingBuffer(2,3,4,5,6,7,8,9)
    pa(r.getArray()); //Object[null,2,3,4,5,6,7,8,9]
    r.add(10);
    System.out.println(r); //RingBuffer(2,3,4,5,6,7,8,9,10)
    pa(r.getArray()); //Object[10,2,3,4,5,6,7,8,9]
    System.out.println(r.remove()); //2
    System.out.println(r); //RingBuffer(3,4,5,6,7,8,9,10)
    pa(r.getArray());  //Object[10,null,3,4,5,6,7,8,9]
    r.add(11);
    System.out.println(r); //RingBuffer(3,4,5,6,7,8,9,10,11)
    pa(r.getArray()); //Object[10,11,3,4,5,6,7,8,9]
    System.out.println();
    
    Iterator<Integer> it = r.iterator();
    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println();
    //3 4 5 6 7 8 9 10 11
    System.out.println();
    
    System.out.println(r.peek()); //3
    System.out.println(r.peek(1)); //3
    System.out.println(r.peek(5)); //7
    System.out.println(r.peek(r.size())); //11
    System.out.println();
    
    for (int i = 0; i < 7; i++) r.remove();
    System.out.println(r); //RingBuffer(10,11)
    pa(r.getArray()); //Object[10,11,null,null,null,null,null,null,null]
    System.out.println(r.remaining()); //7
    System.out.println(r.size()); //2
    assert (r.getArray().length - r.size()) == r.remaining();
    System.out.println(r.add(new Integer[]{12,13,14,15,16,17,18,19,20,21})); //7
    System.out.println(r); //RingBuffer(10,11,12,13,14,15,16,17,18)
    Object[] d1 = r.remove(5);
    pa(d1); //Object[10,11,12,13,14]
    System.out.println(r); //RingBuffer(15,16,17,18)
    Integer[] ra = r.toArray(1);
    pa(ra); //Integer[15,16,17,18]
 

  }

}
