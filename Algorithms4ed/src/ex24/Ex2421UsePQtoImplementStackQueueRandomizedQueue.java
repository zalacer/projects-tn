package ex24;

import static v.ArrayUtils.*;

import java.util.Comparator;
import java.util.NoSuchElementException;

import pq.MaxPQ;
import pq.RandomLinkedMaxPQ;
import v.Tuple2;

/* p331
  2.4.21 Elementary data structures. Explain how to use a priority queue to 
  implement the stack, queue, and randomized queue data types from Chapter 1.

  For Stack and Queue use a MaxPQ of a two element data structure or class 
  with 2 fields where one element/field is a Number type such as Integer, 
  Long or Double and the other is Key. For examples Object[Long,Key] and 
  Tuple2<Long,Key>. Use a custom comparator depending only on the first
  component as in examples below. Items inserted into the MaxPQ will be 
  ordered according to their first component which will be incremented or 
  decremented with every Item inserted or added during initialization from an 
  array using a counter.  For Stack increment it and use delMax() in the 
  implementation of pop. For Queue decrement it and use delMax() in the 
  implementation of dequeue. 
  
  For a RandomQueue a good approach is to use a RandomPQ implemented with 
  explicitly linked nodes such as pq.RandomLinkedMaxPQ, as suggested in Web 
  Questions 24 at http://algs4.cs.princeton.edu/24pq/ :
   
    24. Randomized queue. Implement a RandomQueue so that each operation is 
        guaranteed to take at most logarithmic time. Hint: can't afford array 
        doubling. No easy way with linked lists to locate a random element in 
        O(1) time. Instead, use a complete binary tree with explicit links. 
  
  Example comparators:

  public static Comparator<Object[]> oc = (a,b) -> { 
    return ((Long)a[0]).compareTo((Long)b[0]); };

  public static Comparator<Tuple2<Long,?>> tc = (a,b) -> {
    return a._1.compareTo(b._1); };

  Below are demos of Stack, Queue and RandomQueue using a PQ.

 */

public class Ex2421UsePQtoImplementStackQueueRandomizedQueue {
  
  public static class Stack<Key> {
    private MaxPQ<Tuple2<Long,Key>> pq = null;
    private int N = 0;
    private long index = 0;
    private Comparator<Tuple2<Long,Key>> tc = (a,b) -> {
      return a._1.compareTo(b._1); };

    Stack(){ pq = new MaxPQ<Tuple2<Long,Key>>(tc); }

    Stack(Key[] a) {
      if (a == null || a.length == 0) { 
        pq = new MaxPQ<Tuple2<Long,Key>>(tc); return; }
      Tuple2<Long,Key>[] ta = ofDim(Tuple2.class, a.length);
      for (int i = 0; i < a.length; i++)
        if (a[i] != null) {
          ta[i] = new Tuple2<Long,Key>(index++, a[i]); }
      pq = new MaxPQ<Tuple2<Long,Key>>(tc, ta);
      N = pq.size();
    }

    public boolean isEmpty() {
      return pq.isEmpty();
    }

    public int size() {
      return pq.size();
    }

    public void push(Key k) { 
      pq.insert(new Tuple2<Long,Key>(index++, k));
      N++;   
    }

    public Key pop() {
      if (isEmpty()) throw new NoSuchElementException("Stack underflow");
      Tuple2<Long,Key> t = pq.delMax();
      N--;
      return t._2;
    }

    public void show() {
      Tuple2<Long,Key>[]  ta = pq.toArray();
      for (int i = 0; i < ta.length; i++) System.out.print(ta[i]._2+" ");
      System.out.println();
    }

    public Key[] toArray() {
      Tuple2<Long,Key>[]  ta = pq.toArray();
      Key[] la = ofDim(ta[0]._2.getClass(), ta.length);
      for (int i = 0; i < ta.length; i++) la[i] = ta[i]._2;
      return la;
    }
  }

  public static class Queue<Key> {
    private MaxPQ<Tuple2<Long,Key>> pq = null;
    private int N = 0;
    private long index = 0;
    private Comparator<Tuple2<Long,Key>> tc =(a,b)->{
      return a._1.compareTo(b._1);};

    Queue(){ pq = new MaxPQ<Tuple2<Long,Key>>(tc); }

    Queue(Key[] a) {
      if (a == null || a.length == 0) { 
        pq = new MaxPQ<Tuple2<Long,Key>>(tc); return; }
      Tuple2<Long,Key>[] ta = ofDim(Tuple2.class, a.length);
      for (int i = 0; i < a.length; i++)
        if (a[i] != null) {
          ta[i] = new Tuple2<Long,Key>(index--, a[i]); }
      pq = new MaxPQ<Tuple2<Long,Key>>(tc, ta);
      N = pq.size();
    }

    public boolean isEmpty() {
      return pq.isEmpty();
    }

    public int size() {
      return pq.size();
    }

    public void enqueue(Key k) { 
      pq.insert(new Tuple2<Long,Key>(index--, k));
      N++;   
    }

    public Key dequeue() {
      if (isEmpty()) throw new NoSuchElementException("Queue underflow");
      Tuple2<Long,Key> t = pq.delMax();
      N--;
      return t._2;
    }

    public void show() {
      Tuple2<Long,Key>[]  ta = pq.toArray();
      for (int i = 0; i < ta.length; i++) System.out.print(ta[i]._2+" ");
      System.out.println();
    }

    public Key[] toArray() {
      Tuple2<Long,Key>[]  ta = pq.toArray();
      Key[] la = ofDim(ta[0]._2.getClass(), ta.length);
      for (int i = 0; i < ta.length; i++) la[i] = ta[i]._2;
      return la;
    }
  }

  public static class RandomQueue<Key extends Comparable<? super Key>> {
    private RandomLinkedMaxPQ<Key> pq = null;
    private int N = 0;

    RandomQueue() { 
      pq = new RandomLinkedMaxPQ<Key>(); 
    }

    RandomQueue(Key[] a) {
      if (a == null || a.length == 0) { 
        pq = new RandomLinkedMaxPQ<Key>(); return; }
      pq = new RandomLinkedMaxPQ<Key>(a);
      N = pq.size();
    }

    public boolean isEmpty() {
      return pq.isEmpty();
    }

    public int size() {
      return pq.size();
    }

    public void enqueue(Key k) { 
      pq.insert(k);
      N++;   
    }

    public Key dequeue() {
      if (isEmpty()) throw new NoSuchElementException("queue underflow");
      N--;
      return pq.delRandom();
    }

    public void show() {
      pq.show();
    }
    
    public void showLevels() {
      pq.showLevels();
    }
    

    public Key[] toArray() {
      return pq.toArray();
    }
  }


  public static void main(String[] args) {
    
    // Stack demo
    String[] sa = "ABCDEFGHIJK".split("");
    Stack<String> s = new Stack<>(sa);
    s.show(); //K J G I E F C H D B A 
    System.out.println("popped "+s.pop()); //K
    s.show(); //J I G H E F C A D B 
    s.push("L");
    s.show(); //L J G H I F C A D B E 
    System.out.println();
    
    // Queue demo
    sa = "ABCDEFGHIJK".split("");
    Queue<String> q = new Queue<>(sa);
    q.show(); //A B C D E F G H I J K 
    System.out.println("dequeued "+q.dequeue()); //A
    q.show(); //B D C H E F G K I J
    System.out.println("dequeued "+q.dequeue()); //B
    q.show(); //C D F H E J G K I
    q.enqueue("L");
    q.show(); //C D F H E J G K I L 
    System.out.println();
    
    // RandomQueue demo
    sa = "HCGIJDFKEAB".split("");
    RandomQueue<String> rq = new RandomQueue<>(sa);
    System.out.println("levels:"); rq.showLevels(); System.out.println();
    rq.show(); //K J I H D G F C A E B 
    System.out.println("dequeued "+rq.dequeue()); //D
    rq.show(); //K J I H B G F C A E
    rq.enqueue("W");
    rq.show(); //W J K H B G I C A E F
    System.out.println("dequeued "+rq.dequeue()); //H
    System.out.println("dequeued="+rq.dequeue()); //K
    System.out.println("dequeued="+rq.dequeue()); //G
    rq.showLevels();
    rq.show(); //W J I F B E A C 
  }

}
