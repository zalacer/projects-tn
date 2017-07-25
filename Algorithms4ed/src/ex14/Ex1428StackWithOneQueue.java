package ex14;

import static v.ArrayUtils.*;
import ds.StackWithOneQueue;

//  p212
//  1.4.28 Stack with a queue. Implement a stack with a single queue so that each stack
//  operations takes a linear number of queue operations. Hint : To delete an item, get all
//  of the elements on the queue one at a time, and put them at the end, except for the last
//  one which you should delete and return. (This solution is admittedly very inefficient.)

//  I guess pop is meant instead of delete.

public class Ex1428StackWithOneQueue {

  public static void main(String[] args) {
    
    StackWithOneQueue<Integer> s = new StackWithOneQueue<Integer>(1,2,3,4,5);
    System.out.println(s); //StackWithOneQueue(5,4,3,2,1)
    s.push(6); s.push(7); s.push(8);
    System.out.println(s); //StackWithOneQueue(8,7,6,5,4,3,2,1)
    System.out.println(s.pop()); //8
    System.out.println(s.pop()); //7
    System.out.println(s); //StackWithOneQueue(6,5,4,3,2,1)
    for (Integer i : s) System.out.print(i+" "); System.out.println(); // 6 5 4 3 2 1 
    pa(s.toArray(1)); //Integer[6,5,4,3,2,1]
  
  }

}
