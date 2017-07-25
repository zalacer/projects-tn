package ds;

import static v.ArrayUtils.par;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;


// based on http://algs4.cs.princeton.edu/13stacks/DoublyLinkedList.java
// but using external Node3 class
// for BucketMinPQWithDoublyLinkedList3
// ex4445

public class DoublyLinkedList3 implements Iterable<Integer> {
  // iterator for v values in Node3s
  private int n;        // number of elements on list
  private Node3 pre;     // sentinel before first item
  private Node3 post;    // sentinel after last item

  public DoublyLinkedList3() {
    pre  = new Node3();
    post = new Node3();
    pre.setNext(post);
    post.setPrev(pre);
  }

  public boolean isEmpty()    { return n == 0; }
  public int size()           { return n;      }

  public void add(int v, int w) {
    // add Node with v and w to the end of the list
    Node3 last = post.prev();
    Node3 x = new Node3();
    x.setV(v);
    x.setW(w);
    x.setNext(post);
    x.setPrev(last);
    post.setPrev(x);
    last.setNext(x);
    n++;
  }
  
  public void add(Node3 node) {
    // add node to the end of the list
    Node3 last = post.prev();
    node.setNext(post);
    node.setPrev(last);
    post.setPrev(node);
    last.setNext(node);
    n++;
  }

  public Node3 removeFirst() {
    if (isEmpty()) return null;
    Node3 first = pre.next();
    pre.setNext(first.next());
    first.next().setPrev(pre);
    n--;
    return first;
  }

  public Node3 removeLast() {
    if (isEmpty()) return null;
    Node3 last = post.prev();
    post.setPrev(last.prev());
    last.prev().setNext(post);
    n--;
    return last;
  }
  
  public boolean remove(Node3 node) {
    if (node == null || isEmpty()) {
      System.out.println("can't remove node ="+node);
      return false;
    }
    if (node.prev() != null) node.prev().setNext(node.next());
    if (node.next() != null) node.next().setPrev(node.prev());
    n--;
    return true;
  }

  public ListIterator<Integer> iterator()  { return new DoublyLinkedListIterator(); }

  // assumes no calls to DoublyLinkedList.add() during iteration
  private class DoublyLinkedListIterator implements ListIterator<Integer> {
    private Node3 current       = pre.next();  // the node that is returned by next()
    private Node3 lastAccessed  = null;      // the last node to be returned by prev() or next()
    // reset to null upon intervening remove() or add()
    private int index = 0;

    public boolean hasNext()      { return index < n; }
    public boolean hasPrevious()  { return index > 0; }
    public int previousIndex()    { return index - 1; }
    public int nextIndex()        { return index;     }

    public Integer next() {
      if (!hasNext()) throw new NoSuchElementException();
      lastAccessed = current;
      int v = current.v();
      //Item w = current.w();
      current = current.next(); 
      index++;
      return v;
    }

    public Integer previous() {
      if (!hasPrevious()) throw new NoSuchElementException();
      current = current.prev();
      index--;
      lastAccessed = current;
      return current.v();
    }

    // replace the item of the element that was last accessed by next() or previous()
    // condition: no calls to remove() or add() after last call to next() or previous()
    public void set(Integer v) {
      if (lastAccessed == null) throw new IllegalStateException();
      lastAccessed.setV(v);
    }

    // remove the element that was last accessed by next() or previous()
    // condition: no calls to remove() or add() after last call to next() or previous()
    public void remove() { 
      if (lastAccessed == null) throw new IllegalStateException();
      Node3 x = lastAccessed.prev();
      Node3 y = lastAccessed.next();
      x.setNext(y);
      y.setPrev(x);
      n--;
      if (current == lastAccessed)
        current = y;
      else
        index--;
      lastAccessed = null;
    }

    // add element to list 
    public void add(Integer v) {
      Node3 x = current.prev();
      Node3 y = new Node3();
      Node3 z = current;
      y.setV(v);
      x.setNext(y);
      y.setNext(z);
      z.setPrev(y);
      y.setPrev(x);
      n++;
      index++;
      lastAccessed = null;
    }

  }

  public Integer[] toArray() { 
    Integer[] a = new Integer[n];
    int c = 0;
    Iterator<Integer> it = iterator();
    while (it.hasNext()) a[c++] = it.next();
    return a;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    for (Integer item : this)
      s.append(item + " ");
    return s.toString();
  }

  // a test client
  public static void main(String[] args) {
    int n = 5; //Integer.parseInt(args[0]);

    DoublyLinkedList3 list = null;
    list = new DoublyLinkedList3();   
    for (int i = 0; i < n; i++) list.add(new Node3(i));
    System.out.println(list);
    par(list.toArray());
    for (int i = n-1; i > -1; i--) {
      list.removeLast();
      System.out.println(i+": "+list);
    }

    par(list.toArray());


    //    // add elements 1, ..., n
    //    System.out.println(n + " random integers between 0 and 99");
    //    list = new DoublyLinkedList<Integer>();
    //    for (int i = 0; i < n; i++)
    //      list.add(StdRandom.uniform(100));
    //    System.out.println(list);
    //    System.out.println();
    //
    //    ListIterator<Integer> iterator = list.iterator();
    //
    //    // go forwards with next() and set()
    //    System.out.println("add 1 to each element via next() and set()");
    //    while (iterator.hasNext()) {
    //      int x = iterator.next();
    //      iterator.set(x + 1);
    //    }
    //    System.out.println(list);
    //    System.out.println();
    //
    //    // go backwards with previous() and set()
    //    System.out.println("multiply each element by 3 via previous() and set()");
    //    while (iterator.hasPrevious()) {
    //      int x = iterator.previous();
    //      iterator.set(x + x + x);
    //    }
    //    System.out.println(list);
    //    System.out.println();
    //
    //
    //    // remove all elements that are multiples of 4 via next() and remove()
    //    System.out.println("remove elements that are a multiple of 4 via next() and remove()");
    //    while (iterator.hasNext()) {
    //      int x = iterator.next();
    //      if (x % 4 == 0) iterator.remove();
    //    }
    //    System.out.println(list);
    //    System.out.println();
    //
    //
    //    // remove all even elements via previous() and remove()
    //    System.out.println("remove elements that are even via previous() and remove()");
    //    while (iterator.hasPrevious()) {
    //      int x = iterator.previous();
    //      if (x % 2 == 0) iterator.remove();
    //    }
    //    System.out.println(list);
    //    System.out.println();
    //
    //
    //    // add elements via next() and add()
    //    System.out.println("add elements via next() and add()");
    //    while (iterator.hasNext()) {
    //      int x = iterator.next();
    //      iterator.add(x + 1);
    //    }
    //    System.out.println(list);
    //    System.out.println();
    //
    //    // add elements via previous() and add()
    //    System.out.println("add elements via previous() and add()");
    //    while (iterator.hasPrevious()) {
    //      int x = iterator.previous();
    //      iterator.add(x * 10);
    //      iterator.previous();
    //    }
    //    System.out.println(list);
    //    System.out.println();
  }
}


