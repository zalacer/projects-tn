package ex13;

import ds.RandomQueue;

//  p168
//  1.3.35  Random queue. A random queue stores a collection of items and supports the
//  following API:
//  public class RandomQueue<Item>
//  RandomQueue() create an empty random queue
//  boolean isEmpty()  is the queue empty?
//  void enqueue(Item item)  add an item
//  Item dequeue()  remove and return a random item (sample without replacement)
//  Item sample() return a random item, but do not remove (sample with replacement)
//  API for a generic random queue
//  Write a class  RandomQueue that implements this API. Hint : Use an array representation
//  (with resizing). To remove an item, swap one at a random position (indexed  0 through
//  N-1 ) with the one at the last position (index  N-1 ). Then delete and return the last ob-
//  ject, as in  ResizingArrayStack . Write a client that deals bridge hands (13 cards each)
//  using RandomQueue<Card> 

public class Ex1335RandomQueue {

  public static void main(String[] args) {
    
    Integer[] ia = {1,2,3,4,5,6,7,8,9};
    RandomQueue<Integer> q = new RandomQueue<Integer>(ia);
    System.out.println(q); // RandomQueue(1,2,3,4,5,6,7,8,9)
    while(q.iterator().hasNext()) {
      System.out.println("dequeued "+q.dequeue());
      System.out.println(q); // RandomQueue(2,3,4,5,6,7,1,9)
      if (!q.isEmpty()) {
        System.out.println("sample = "+q.sample());
      } else System.out.println("queue is empty");
      System.out.println();
    }
    //  dequeued 4
    //  RandomQueue(2,3,1,5,6,7,8,9)
    //  sample = 5
    //
    //  dequeued 7
    //  RandomQueue(3,1,5,6,2,8,9)
    //  sample = 1
    //
    //  dequeued 1
    //  RandomQueue(3,5,6,2,8,9)
    //  sample = 3
    //
    //  dequeued 5
    //  RandomQueue(3,6,2,8,9)
    //  sample = 8
    //
    //  dequeued 9
    //  RandomQueue(6,2,8,3)
    //  sample = 2
    //
    //  dequeued 3
    //  RandomQueue(2,8,6)
    //  sample = 6
    //
    //  dequeued 8
    //  RandomQueue(2,6)
    //  sample = 6
    //
    //  dequeued 6
    //  RandomQueue(2)
    //  sample = 2
    //
    //  dequeued 2
    //  RandomQueue()
    //  queue is empty

   
   
       
  }

}
