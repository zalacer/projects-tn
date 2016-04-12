package ch02.ooprogramming;

import java.util.Arrays;
import java.util.Iterator;

import utils.IterableQueue;

// 17. Provide an iterator—an object that yields the elements of the queue in turn—for the
// queue of the preceding class. Make Iterator a nested class with methods next
// and hasNext. Provide a method iterator() of the Queue class that yields a
// Queue.Iterator. Should Iterator be static or not?

// I found it not desirable to make Iterator static. It worked when static for a single 
// Queue instance but that had the side-effect of requiring the private LinkedList to be 
// static which does not allow there to be multiple Queues each with different data.

public class Ch0217IterableQueue {

  public static void main(String[] args) {

    IterableQueue q1 = new IterableQueue(
        Arrays.asList(new String[] {"one", "two", "three"}));
    System.out.println(q1); // Queue(one, two, three)
    Iterator<String> it = q1.iterator();
    System.out.println(it.getClass().getName()); // utils1.IterableQueue$QueueIterator
    System.out.println(it.getClass().getSimpleName()); // QueueIterator
    while(it.hasNext()) {
      System.out.println(it.next());
    }
    //  one
    //  two
    //  three
    System.out.println(q1); // Queue()

  }

}
