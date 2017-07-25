package ex13;


import java.util.Iterator;

import ds.RandomQueue;

//  p168
//  1.3.36 Random iterator. Write an iterator for  RandomQueue<Item> from the previous
//  exercise that returns the items in random order.

public class Ex1336RandomQueueIterator {

  public static void main(String[] args) {
    
    Integer[] ia = {1,2,3,4,5,6,7,8,9};
    RandomQueue<Integer> q = new RandomQueue<Integer>(ia);
    Iterator<Integer> it = q.randomIterator();
    while(it.hasNext()) System.out.print(it.next()+" "); System.out.println();
    // 2 7 5 9 4 3 8 1 6 
   
   
       
  }

}
