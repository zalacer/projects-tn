package ex13;

import static v.ArrayUtils.*;

import java.util.Iterator;

import ds.Stack;

//  1.3.50 Fail-fast iterator. Modify the iterator code in  Stack to immediately throw a
//  java.util.ConcurrentModificationException if the client modifies the collection
//  (via  push() or  pop() ) during iteration? b).
//  
//  Solution: Maintain a counter that counts the number of  push() and  pop() operations.
//  When creating an iterator, store this value as an Iterator instance variable. Before
//  each call to  hasNext() and  next() , check that this value has not changed since con-
//  struction of the iterator; if it has, throw the exception.

// java.util.Stack synchronizes pop, peek and search and extends Vector which is array
// based and also synchronizes most of it methods including its ListIterator while also
// detecting comodification's with a counter. Looks like too many cooks over the years.

// after implementing ConcurrentModificationExceptions in Stack as suggested, multithreaded
// tests show that 

public class Ex1350FailFastIterator {

  public static void main(String[] args) {
    
    int[] i = {0};
    Stack<Integer> s = new Stack<Integer>(fill(1000000, () -> i[0]++));
    System.out.println(s.size()); //1000000
    //System.out.println(s); //Stack(9,8,7,6,5,4,3,2,1,0) for fill(10, () -> i[0]++)
    Iterator<Integer> it = s.iterator();
    Integer[] x = {0}; Integer[] y = {0};
    Thread[] threads = new Thread[2];
    threads[0] = new Thread(() -> { while (it.hasNext()) x[0] = it.next(); });
    threads[1] = new Thread(() -> { while(!s.isEmpty()) y[0] = s.pop(); });
    for (Thread t : threads) t.start();
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    
    // results of trials show iterator death on ConcurrentModificationException 
    // after 107-580 iterations

    System.out.println(s); //Stack() always
    System.out.println(s.size()); //0 always
    System.out.println(s.opcount()); //2000000 always due to 1000000 enqueue ops for stack
                                     //initialization plus 1000000 pop ops
    System.out.println(x[0]); //999776,999752,999512,999668,999744  this shows the iterator
                              //thread died after for example 1000000-999744 = 256 iterations
    System.out.println(y[0]); //0,0,0,0,0 this just means the pop thread continued to the end
                              // after the iterator thread died 
    
    // ConcurrentModificationException: iterator failed in next at iterator count 352
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 328
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 273
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 344
    // ConcurrentModificationException: iterator failed in next at iterator count 580
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 273
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 362
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 511
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 536
    // ConcurrentModificationException: iterator failed in next at iterator count 219
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 255
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 225
    // ConcurrentModificationException: iterator failed in next at iterator count 490
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 257
    // ConcurrentModificationException: iterator failed in hasNext at iterator count 107
    
  }

}
