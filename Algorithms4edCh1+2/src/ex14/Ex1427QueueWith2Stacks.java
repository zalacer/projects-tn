package ex14;

import static v.ArrayUtils.*;
import ds.QueueWithTwoStacks;

//  p211-212
//  1.4.27 Queue with two stacks. Implement a queue with two stacks so that each queue
//  operation takes a constant amortized number of stack operations. Hint : If you push
//  elements onto a stack and then pop them all, they appear in reverse order. If you repeat
//  this process, they’re now back in order.

public class Ex1427QueueWith2Stacks {

  public static void main(String[] args) {
    
    QueueWithTwoStacks<Integer> q = new QueueWithTwoStacks<Integer>(1,2,3,4,5);
    System.out.println(q); //QueueWithTwoStacks(1,2,3,4,5)
    q.enqueue(6);  q.enqueue(7);  q.enqueue(8);
    System.out.println(q); //QueueWithTwoStacks(1,2,3,4,5,6,7,8)
    System.out.println(q.dequeue()); //1
    System.out.println(q.dequeue()); //2
    System.out.println(q); //QueueWithTwoStacks(3,4,5,6,7,8)
    for (Integer i : q) System.out.print(i+" "); System.out.println(); //3 4 5 6 7 8 
    pa(q.toArray(1)); //Integer[3,4,5,6,7,8]
  
  }

}
