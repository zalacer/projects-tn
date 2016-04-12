package ch10.concurrency;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// 20. Describe two different ways in which the queue class below can fail to 
// contain the correct elements.

// this queue (Queue) shown below is more problematic than the stack in ex19 
// because its methods are more complex and provide creater opportunity 
// for interference with concurrent threads

// scenario 1: thread 1 and 2 attempt to add an object to the Queue simultaneously. 
// Both see that head is null, and attempt to set it to n and succeed. The problem
// arises when thread 2 overwrites the newValue assigned to (n = tail).value by 
// thread 1

// scenario 2: similarly if they both see that head is not null, one thread can 
// overwrite the new tail's value set by another thread.

// scenario 3:  the same value can appear to be removed twice if two threads enter 
// remove() at the same time and access the same head and return its value before
// either has reset it to head.next causing duplicate processing.

// demos of Queue performance errors produced with pure insertion and pure removal 
// are included below followed by demos with ConcurrentQueue

public class Ch1020QueueImplementation {

  public static class Queue {
    class Node {Object value; Node next;}
    private Node head;
    private Node tail;
    public int size = 0;
    public void add(Object newValue) {
      Node n = new Node();
      if (head == null) head = n;
      else tail.next = n;
      tail = n;
      tail.value = newValue;
      size++;
    }
    public Object remove() {
      if (head == null) return null;
      Node n = head;
      head = n.next;
      size--;
      return n.value;
    }
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Queue[");
      Node current = head;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          builder.append(current.value);
          c++;
          break;
        } else {
          builder.append(current.value+",");
          c++;
        }
        current = current.next;
      }
      builder.append("]"+", count="+c);
      return builder.toString();
    }

  }

  public static class ConcurrentQueue {
    class Node {Object value; Node next;}
    private Node head;
    private Node tail;
    public int size = 0;
    public synchronized void add(Object newValue) {
      Node n = new Node();
      if (head == null) head = n;
      else tail.next = n;
      tail = n;
      tail.value = newValue;
      size++;
    }
    public synchronized Object remove() {
      if (head == null) return null;
      Node n = head;
      head = n.next;
      size--;
      return n.value;
    }
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Queue[");
      Node current = head;
      int c = 0;
      while(true) {
        if (Objects.isNull(current)) break;
        if (Objects.isNull(current.next)) {
          builder.append(current.value);
          c++;
          break;
        } else {
          builder.append(current.value+",");
          c++;
        }
        current = current.next;
      }
      builder.append("]"+", count="+c);
      return builder.toString();
    }

  }

  public static void main(String[] args) throws InterruptedException {

    Queue q1 = new Queue();
    for (int i = 0; i < 1000; i++) {
      q1.add(i);
    }

    ExecutorService executor = Executors.newCachedThreadPool();
    // test 0: starting with Queue of 1000 elements 0 to 999
    for (int i = 0; i < 10; i++) {
      executor.execute(() -> {
        for(int j = 0; j < 100; j++) q1.remove();
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("q1:"+q1);                 
    // should result in empty queue however also got the following in
    // separate trials:
    // Queue[993,994,995,996,997,998,999] , count=4
    // Queue[926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,
    //          946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,
    //          965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,
    //          984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999], count=72
    // Queue[977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,
    //          997,998,999], count=20
    // Queue[995,996,997,998,999], count=5 
    // Queue[962,963,964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,
    //           982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999], count=38
    // Queue[937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,
    //           957,958,959,960,961,962,963,964,965,966,967,968,969,970,971,972,973,974,975,
    //          976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,
    //          995,996,997,998,999], count=63
    // Queue[989,990,991,992,993,994,995,996,997,998,999], count=11

    Queue q2 = new Queue();
    ExecutorService executor2 = Executors.newCachedThreadPool();
    // test 1: starting with empty Queue
    for (int i = 0; i < 2; i++) {
      executor2.execute(() -> {q2.add(1);q2.add(2);q2.add(3);});
      executor2.execute(() -> {q2.add(4);q2.add(5);q2.add(6);});
      executor2.execute(() -> {q2.add(7);q2.add(8);q2.add(9);});
    }

    executor2.shutdown();
    executor2.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("q2:"+q2);
    // should result in a queue of 19 elements 1-9 twice, however also got
    // the following in separate trials:
    //Queue[4,5,6,2,3,1,2,3,7,8,9,4,5,6,7,8,9], count=17
    //Queue[4,2,6,3,7,8,9,1,2,3,4,5,6,7,8,9], count=16
    //Queue[1,5,3,7,8,9,1,2,3,7,8,9,4,5,6], count=15

    // tests with ConcurrentQueue

    ConcurrentQueue q3 = new ConcurrentQueue();
    for (int i = 0; i < 1000; i++) {
      q2.add(i);
    }

    ExecutorService executor3 = Executors.newCachedThreadPool();
    // test 0: starting with Queue of 1000 elements 0 to 999
    for (int i = 0; i < 10; i++) {
      executor3.execute(() -> {
        for(int j = 0; j < 100; j++) q3.remove();
      });
    }

    executor3.shutdown();
    executor3.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("q3:"+q3); 
    // Queue[], count=0

    ConcurrentQueue q4 = new ConcurrentQueue();
    ExecutorService executor4 = Executors.newCachedThreadPool();
    // test 1: starting with empty Queue
    for (int i = 0; i < 2; i++) {
      executor4.execute(() -> {q4.add(1);q4.add(2);q4.add(3);});
      executor4.execute(() -> {q4.add(4);q4.add(5);q4.add(6);});
      executor4.execute(() -> {q4.add(7);q4.add(8);q4.add(9);});
    }

    executor4.shutdown();
    executor4.awaitTermination(10, TimeUnit.MINUTES);
    System.out.println("q4:"+q4); 
    //Queue[1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7,8,9], count=18

  }

}
