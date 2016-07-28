package ex13;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import ds.ResizingArrayQueue;

//  p163
//  1.3.14  Develop a class  ResizingArrayQueueOfStrings that implements the queue
//  abstraction with a fixed-size array, and then extend your implementation to use array
//  resizing to remove the size restriction.

// p136 array resizing
// p141 Pushdown (LIFO) stack (resizing array implementation)
// p151 ALGORITHM 1.3 FIFO queue

public class Ex1314ResizingArrayQueueOfStrings {

  //p151 ALGORITHM 1.3 FIFO queue linked list implementation
  public static class Queue<Item> implements Iterable<Item> {
    private Node first; // link to least recently added node
    private Node last; // link to most recently added node
    private int N; // number of items on the queue

    private class Node { // nested class to define nodes
      Item item;
      Node next;
    }

    public boolean isEmpty() { return first == null; } // Or: N == 0.

    public int size() { return N; }

    public void enqueue(Item item) { // Add item to the end of the list.
      Node oldlast = last;
      last = new Node();
      last.item = item;
      last.next = null;
      if (isEmpty()) first = last;
      else oldlast.next = last;
      N++;
    }

    public Item dequeue() { // Remove item from the beginning of the list.
      Item item = first.item;
      first = first.next;
      if (isEmpty()) last = null;
      N--;
      return item;
    }

    // iterator code provided on p155 of the text - however not suitable as Stack iterator
    public Iterator<Item> iterator() { return new ListIterator(); }

    // using ListIterator to build array in toArray
    private class ListIterator implements Iterator<Item> {
      private Node current = first;

      public boolean hasNext() { return current != null; }

      // remove is provided by default in the Java 8 Iterator API at 
      // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
      //      public void remove() { }

      public Item next() {
        Item item = current.item;
        current = current.next;
        return item;
      }
    }
  }

  public class FixedCapacityStack<Item> {
    private Item[] a; // stack entries
    private int N; // size

    @SuppressWarnings("unchecked")
    public FixedCapacityStack(int cap) {
      a = (Item[]) new Object[cap]; 
    }

    public boolean isEmpty() { return N == 0; }

    public int size() { return N; }

    public void push(Item item) {
      a[N++] = item;
    }

    public Item pop() {
      return a[--N];
    }
  }

  // this version does not resize the array
  public static class ResizingArrayQueueOfStrings1 implements Iterable<String> {
    private String[] a;
    private int first = 0; // index of least recently added node in a
    private int last = 0; // index of most recently added node in a
    private int N = 0; // number of items on the queue

    public ResizingArrayQueueOfStrings1() {
      this.a = new String[5];
    }

    public ResizingArrayQueueOfStrings1(int capacity) {
      if (capacity < 0) throw new IllegalArgumentException("ResizingArrayQueueOfStrings1: "
          + " constructor: int argument cannot be < 0");
      this.a = new String[capacity];
    }

    public ResizingArrayQueueOfStrings1(String[] sa) {
      if (sa == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings1: "
          +" constructor: array argument cannot be null)");
      for (int i = 0; i < sa.length; i++) enqueue(sa[i]); 
    }

    public ResizingArrayQueueOfStrings1(ResizingArrayQueueOfStrings1 bqos) {
      if (bqos == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings1: "
          + "constructor: ResizingArrayQueueOfStrings1 argument cannot be null)");
      String[] sa = bqos.toArray();
      for (int i = 0; i < sa.length; i++) enqueue(sa[i]); 
    }

    public int size() {
      return N;
    }

    public int getFirst() {
      return first;
    }

    public int getLast() {
      return last;
    }

    public boolean isEmpty() {
      return N == 0;
    }

    public boolean isFull() {
      return N == a.length;
    }

    public void enqueue(String item) { // Add item to the end of the list.
      if (isFull()) throw new IllegalStateException("queue overflow");
      //      a[last++] = item;                        // add item
      //      if (last == a.length) last = 0;          // wrap-around
      //      N++;
      a[last] = item;
      last = (last + 1) % a.length; // handles wrap-around
      N++;
    }

    public String dequeue() {
      if (isEmpty())  throw new NoSuchElementException("queue underflow");
      //      String item = a[first];
      //      a[first] = null;                            // to avoid loitering
      //      N--;
      //      first++;
      //      if (first == a.length) first = 0;           // wrap-around   
      //      return item;
      String item = a[first];
      a[first] = null;
      first = (first + 1) % a.length; // handles wrap-around
      N--;
      return item;
    }

    public String peek() {
      if (isEmpty()) throw new NoSuchElementException("queue underflow");
      return a[first];
    }

    public Iterator<String> iterator() {
      return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<String> {
      private int i = 0;
      public boolean hasNext()  { return i < N; }

      public String next() {
        if (!hasNext()) throw new NoSuchElementException();
        String item = a[(i + first) % a.length];
        i++;
        return item;
      }
    }

    public String[] toArray() {
      String[] sa = new String[N];
      int i = 0;
      Iterator<String> it = this.iterator();
      while(it.hasNext()) sa[i++] = it.next();
      return sa;
    }

    public String[] getArray() {
      return a;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + N;
      result = prime * result + Arrays.hashCode(a);
      result = prime * result + first;
      result = prime * result + last;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ResizingArrayQueueOfStrings1 other = (ResizingArrayQueueOfStrings1) obj;
      if (N != other.N)
        return false;
      if (!Arrays.equals(a, other.a))
        return false;
      if (first != other.first)
        return false;
      if (last != other.last)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return Arrays.toString(toArray());
    }

  }

  // this version resizes the array
  public static class ResizingArrayQueueOfStrings implements Iterable<String> {
    private String[] a;
    private int first = 0; // index of least recently added node in a
    private int last = 0; // index of most recently added node in a
    private int N = 0; // number of items on the queue

    public ResizingArrayQueueOfStrings() {
      this.a = new String[2];
    }

    public ResizingArrayQueueOfStrings(int capacity) {
      if (capacity < 0) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
          + " constructor: int argument cannot be < 0");
      this.a = new String[capacity];
    }

    public ResizingArrayQueueOfStrings(String[] sa) {
      if (sa == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
          +" constructor: array argument cannot be null)");
      for (int i = 0; i < sa.length; i++) enqueue(sa[i]); 
    }

    public ResizingArrayQueueOfStrings(ResizingArrayQueueOfStrings bqos) {
      if (bqos == null) throw new IllegalArgumentException("ResizingArrayQueueOfStrings "
          + "constructor: ResizingArrayQueueOfStrings argument cannot be null)");
      String[] sa = bqos.toArray();
      for (int i = 0; i < sa.length; i++) enqueue(sa[i]); 
    }

    public int size() {
      return N;
    }

    public int getFirst() {
      return first;
    }

    public int getLast() {
      return last;
    }

    public boolean isEmpty() {
      return N == 0;
    }

    public boolean isFull() {
      return N == a.length;
    }

    private void resize(int newSize) {
      if (newSize < N) throw new IllegalArgumentException("resize: "
          + "newSize must be greater than the current size");
      String[] temp = new String[newSize];
      for (int i = 0; i < N; i++) {
        temp[i] = a[(first + i) % a.length];
      }
      a = temp;
      first = 0;
      last  = N;
    }

    public void enqueue(String item) { // Add item to the end of the list.
      if (N == a.length) resize(2*a.length);  
      //      a[last++] = item;                        // add item
      //      if (last == a.length) last = 0;          // wrap-around
      //      N++;
      a[last] = item;
      last = (last + 1) % a.length; // handles wrap-around
      N++;
    }

    public String dequeue() {
      if (isEmpty())  throw new NoSuchElementException("queue underflow");
      //      String item = a[first];
      //      a[first] = null;                            // to avoid loitering
      //      N--;
      //      first++;
      //      if (first == a.length) first = 0;           // wrap-around   
      //      return item;
      String item = a[first];
      a[first] = null;
      first = (first + 1) % a.length; // handles wrap-around
      N--;
      //    if (first == q.length) first = 0;           // wrap-around already done
      if (N > 0 && N == a.length/4) resize(a.length/2); 
      return item;
    }

    public String peek() {
      if (isEmpty()) throw new NoSuchElementException("queue underflow");
      return a[first];
    }

    public Iterator<String> iterator() {
      return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<String> {
      private int i = 0;
      public boolean hasNext()  { return i < N; }

      public String next() {
        if (!hasNext()) throw new NoSuchElementException();
        String item = a[(i + first) % a.length];
        i++;
        return item;
      }
    }

    public String[] toArray() {
      String[] sa = new String[N];
      int i = 0;
      Iterator<String> it = this.iterator();
      while(it.hasNext()) sa[i++] = it.next();
      return sa;
    }

    public String[] getArray() {
      return a;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + N;
      result = prime * result + Arrays.hashCode(a);
      result = prime * result + first;
      result = prime * result + last;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ResizingArrayQueueOfStrings other = (ResizingArrayQueueOfStrings) obj;
      if (N != other.N)
        return false;
      if (!Arrays.equals(a, other.a))
        return false;
      if (first != other.first)
        return false;
      if (last != other.last)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return Arrays.toString(toArray());
    }

  }
  
  // generic version of ResizingArrayQueue is datastructures.ResizingArrayQueue

  public static void main(String[] args) {
    ResizingArrayQueue<Integer> r = new ResizingArrayQueue<Integer>();
    for (int i = 1; i < 6; i++) r.enqueue(i);
    System.out.println(r); //[1, 2, 3, 4, 5]
    System.out.println("r.getFirst="+r.getFirst()); //0
    System.out.println("r.getLast="+r.getLast()); //5
    Integer[] ra = r.toArray(1);
    pa(ra); //Integer[1,2,3,4,5]
    Object[] ro = r.toArray();
    pa(ro); //Object[1,2,3,4,5]
    pa(r.getArray(),75,1,1); //[1,2,3,4,5,null,null,null]
    Random rnd = null;
    try {
      rnd = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (rnd == null) rnd = new Random(735632797);
    rnd.ints(1, 100).filter(x ->{
        if (x%2==1) r.enqueue(new Integer(x)); else r.dequeue(); return true;})
      .limit(3000).count();
    System.out.println(r.size()); //97
    System.out.println(r.getFirst()); //126
    System.out.println(r.getLast()); //95
    Integer[] ia1 = r.toArray(1);
    System.out.println("ia1.length="+ia1.length); //97
    pa(ia1,10000,1,1); //[81,59,73,69,41,99,7,49,41,65,45,99,51,7,31,77,47,43,19,99,49,93,45,97,37,67,55,65,5,47,13,45,15,19,87,81,11,11,11,3,75,3,33,99,43,95,33,71,1,13,7,49,39,7,43,91,33,47,59,81,89,35,13,93,91,41,77,21,35,19,93,49,59,55,43,87,27,39,71,77,53,5,33,27,23,59,41,11,31,99,3,93,57,15,93,75,37]
    Object[] ia2 = r.getArray();
    System.out.println("ia2.length="+ia2.length); //128
    pa(ia2,10000,1,1); //[73,69,41,99,7,49,41,65,45,99,51,7,31,77,47,43,19,99,49,93,45,97,37,67,55,65,5,47,13,45,15,19,87,81,11,11,11,3,75,3,33,99,43,95,33,71,1,13,7,49,39,7,43,91,33,47,59,81,89,35,13,93,91,41,77,21,35,19,93,49,59,55,43,87,27,39,71,77,53,5,33,27,23,59,41,11,31,99,3,93,57,15,93,75,37,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,81,59]
  
    //    ResizingArrayQueueOfStrings r = new ResizingArrayQueueOfStrings();
    //    System.out.println(r.size()); //0
    //    System.out.println(r); //[]
    //    for (int i = 1; i < 4; i++) r.enqueue(""+i);
    //    System.out.println(r.size()); //3
    //    System.out.println(r); //[1, 2, 3]
    //    pa(r.getArray(),75,1,1); //[1,2,3,null]
    //    System.out.println(r.getArray().length); //4
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //3
    //    r.enqueue(""+4); r.enqueue(""+5); 
    //    System.out.println(r.size()); //5
    //    System.out.println(r); ////[1, 2, 3, 4, 5]
    //    pa(r.getArray(),75,1,1); //[1,2,3,4,5,null,null,null]
    //    System.out.println(r.getArray().length); //8
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //5
    //    System.out.println(r.dequeue()); //1
    //    System.out.println(r.dequeue()); //2
    //    System.out.println(r.dequeue()); //3
    //    System.out.println(r.size()); //2
    //    System.out.println(r); //[4, 5]
    //    pa(r.getArray(),75,1,1); //[4,5,null,null]
    //    System.out.println(r.getArray().length); //4
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //2
    //    for (int i = 6; i < 21; i++) r.enqueue(""+i);
    //    System.out.println(r.size()); //17
    //    System.out.println(r); //[4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
    //    pa(r.getArray(),500,1,1); //[4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null]
    //    System.out.println(r.getArray().length); //32
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //17    
    //    
    //    // with default size 16
    //    ResizingArrayQueueOfStrings1 r = new ResizingArrayQueueOfStrings1();
    //    System.out.println(r.size()); //0
    //    System.out.println(r); //[]
    //    for (int i = 1; i < 6; i++) r.enqueue(""+i);
    //    System.out.println(r.size()); //5
    //    System.out.println(r); //[1, 2, 3, 4, 5]
    //    pa(r.getArray(),75,1,1); //[1,2,3,4,5,null,null,null,null,null,null,null,null,null,null,null]
    //    System.out.println(r.getArray().length); //16
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //5
    //    System.out.println(r.dequeue()); //1
    //    System.out.println(r); //[2, 3, 4, 5]
    //    System.out.println(r.dequeue()); //2
    //    System.out.println(r); //[3, 4, 5]
    //    pa(r.getArray(),75,1,1); //[null,null,3,4,5,null,null,null,null,null,null,null,null,null,null,null]
    //    System.out.println(r.getFirst()); //2
    //    System.out.println(r.getLast()); //5
    //    System.out.println(r.size()); //3
    // with default size 5
    //    ResizingArrayQueueOfStrings1 r = new ResizingArrayQueueOfStrings1();
    //    System.out.println(r.size()); //0
    //    System.out.println(r); //[]
    //    pa(r.getArray(),75,1,1); //[null,null,null,null,null]
    ////    r.dequeue(); //java.util.NoSuchElementException: queue underflow
    //    for (int i = 1; i < 6; i++) r.enqueue(""+i);
    //    System.out.println(r.size()); //5
    //    System.out.println(r); //[1, 2, 3, 4, 5]
    ////    r.enqueue(""+6); //java.lang.IllegalStateException: queue overflow
    //    for (int i = 1; i < 4; i++) r.dequeue();
    //    System.out.println(r); //[4, 5]
    //    pa(r.getArray(),75,1,1); //[null,null,null,4,5]
    //    for (int i = 6; i < 9; i++) r.enqueue(""+i);
    //    System.out.println(r); //[4, 5, 6, 7, 8]
    //    pa(r.getArray(),75,1,1); //[6,7,8,4,5]
    //    System.out.println(r.size()); //5
    //    System.out.println(r.getFirst()); //3
    //    System.out.println(r.getLast()); //3
    //    System.out.println(r.peek()); //4
    //    System.out.println(r.dequeue()); //4
    //    System.out.println(r); //[5, 6, 7, 8]
    //    pa(r.getArray(),75,1,1); //[6,7,8,null,5]
    //    System.out.println(r.getFirst()); //4
    //    System.out.println(r.getLast()); //3
    //    System.out.println(r.size()); //4
    //    System.out.println(r.dequeue()); //5
    //    System.out.println(r.size()); //3
    //    System.out.println(r.getFirst()); //0
    //    System.out.println(r.getLast()); //3
    //    System.out.println(r); //[6, 7, 8]
    //    pa(r.getArray(),75,1,1); //[6,7,8,null,null] 
    ////    System.out.println(r.dequeue()); 
    ////    r.enqueue(""+9); 
    ////    System.out.println(r); 
    ////    pa(r.getArray(),75,1,1); 
  }

}
