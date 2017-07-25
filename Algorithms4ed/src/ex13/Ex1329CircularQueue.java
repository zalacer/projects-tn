package ex13;

import java.util.Iterator;

import ds.CircularQueue;

//  1.3.29  Write a Queue implementation that uses a circular linked list, which is the same
//  as a linked list except that no links are null and the value of last.next is first when-
//  ever the list is not empty. Keep only one Node instance variable ( last ).

// see ds.CircularQueue.java for the code for the circular queue implementation

public class Ex1329CircularQueue {

  public static void main(String[] args) {
    // this constructor enqueues items in order of increasing index
    CircularQueue<Integer> cq = new CircularQueue<Integer>(1,2,3,4,5);
    System.out.println(cq); //CircularQueue(1,2,3,4,5)
    System.out.println(cq.getLast()); //Node(5)
    System.out.println(cq.size()+"\n"); //5
    
    Iterator<Integer> it = cq.iterator();
    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println("\n");
    // 1 2 3 4 5
    
    System.out.println(cq.dequeue()); //1
    System.out.println(cq); //CircularQueue(2,3,4,5)
    System.out.println(cq.getLast()); //Node(5)
    System.out.println(cq.size()+"\n"); //4
    
    System.out.println(cq.dequeue()); //2
    System.out.println(cq); //CircularQueue(3,4,5)
    System.out.println(cq.getLast()); //Node(5)
    System.out.println(cq.size()+"\n"); //3
    
    System.out.println(cq.dequeue()); //3
    System.out.println(cq); //CircularQueue(4,5)
    System.out.println(cq.getLast()); //Node(5)
    System.out.println(cq.size()+"\n"); //2
    
    System.out.println(cq.dequeue()); //4
    System.out.println(cq); //CircularQueue(5)
    System.out.println(cq.getLast()); //Node(5)
    System.out.println(cq.size()+"\n"); //1
    
    System.out.println(cq.dequeue()); //1
    System.out.println(cq); //CircularQueue()
    System.out.println(cq.getLast()); //null
    System.out.println(cq.size()+"\n"); //0
    
    // System.out.println(cq.dequeue());
    // java.util.NoSuchElementException: Queue underflow
    
    cq.enqueue(9);
    System.out.println(cq); //CircularQueue(9)
    System.out.println(cq.getLast()); //Node(9)
    System.out.println(cq.size()+"\n"); //1
    
    cq.enqueue(10);
    System.out.println(cq); //CircularQueue(9,10)
    System.out.println(cq.getLast()); //Node(10)
    System.out.println(cq.size()+"\n"); //2
    
    cq.enqueue(11);
    System.out.println(cq); //CircularQueue(9,10,11)
    System.out.println(cq.getLast()); //Node(11)
    System.out.println(cq.size()+"\n"); //3
   
  }

}
