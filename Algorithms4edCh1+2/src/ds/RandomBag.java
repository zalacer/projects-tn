package ds;

import static java.util.Arrays.copyOf;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

//  p168
//  1.3.34  Random bag. A random bag stores a collection of items and supports the fol-
//  lowing API:
//  public class RandomBag<Item> implements Iterable<Item>
//  RandomBag()  create an empty random bag
//  boolean isEmpty()  is the bag empty?
//  int size()  number of items in the bag
//  void add(Item item)  add an item
//  API for a generic random bag
//  Write a class  RandomBag that implements this API. Note that this API is the same as for
//  Bag , except for the adjective random, which indicates that the iteration should provide
//  the items in random order (all N ! permutations equally likely, for each iterator). Hint :
//  Put the items in an array and randomize their order in the iterator’s constructor.

// starting with class Bag from text p155
public class RandomBag<Item> implements Iterable<Item> {
  private Node first; // first node in list
  private int N;
  
  public RandomBag(){};
  
  public RandomBag(Item[] items) { 
    for (Item i : items) this.add(i);
  }
  
  private class Node {
    Item item;
    Node next;
  }
  
  public void add(Item item) { // same as push() in Stack
    Node oldfirst = first;
    first = new Node();
    first.item = item;
    first.next = oldfirst;
    N++;
  }
  
  public boolean isEmpty() { return N == 0; }
  
  public int size() { return N; }
  
  public Item[] toFifoArray() {
    // returns items in bag in FIFO order in an array
    @SuppressWarnings("unchecked")
    Item[] a = (Item[]) new Object[N];
    int aindex = 0;
    Iterator<Item> it = fifoIterator();
    while (it.hasNext()) a[aindex++] = it.next();
    return a;
  }
  
  @SafeVarargs
  public final Item[] toFifoArray(Item...item) {
    // returns items in bag in FIFO order in an array
    if (item.length == 0) throw new IllegalArgumentException("toArray: item length "
        + "must be > 0");
    Item[] a = copyOf(item, size());
    Iterator<Item> it = fifoIterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  public Item[] toArray() {
    // returns items in bag in randomized order in an array
    @SuppressWarnings("unchecked")
    Item[] a = (Item[]) new Object[N];
    int i = 0;
    ListIterator lit = new ListIterator();
    while (lit.hasNext()) a[i++] = lit.next();
    return a;
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...item) {
    // returns items in bag in randomized order in an array  
    if (item.length == 0) throw new IllegalArgumentException("toArray: item length "
        + "must be > 0");
    Item[] a = copyOf(item, size());
    Iterator<Item> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  public Iterator<Item> iterator() { 
    // this is a random iterator
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(793095727);
    Item[] items = (Item[]) toArray();
    shuffle(items, r);
    return Arrays.stream(items).iterator();
  }
  
  public static <T> void shuffle(T[] a, Random r) {
    if (a == null || r == null) throw new IllegalArgumentException("shuffle "
        + "both the array and the Random argument must not be null");
    int len = a.length;
    for (int i = 0; i < len; i++) {
      int n = i + r.nextInt(len-i);
      T t = a[i];
      a[i] = a[n];
      a[n] = t;
    }
  }
  
  public Iterator<Item> fifoIterator() { return new ListIterator(); }
  
  private class ListIterator implements Iterator<Item> {
    // FIFO iterator for testing
    private Node current = first;
    public boolean hasNext() { return current != null; }
    public void remove() { }
    public Item next(){
      Item item = current.item;
      current = current.next;
      return item;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
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
    @SuppressWarnings("rawtypes")
    RandomBag other = (RandomBag) obj;
    if (N != other.N)
      return false;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    return true;
  }
  
  public String toFifoString() {
    StringBuilder sb = new StringBuilder();
    sb.append("RandomBag(");
    if (isEmpty()) return sb.append(")").toString();
    Iterator<Item> it = fifoIterator();
    while (it.hasNext()) sb.append(""+it.next()+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  @Override
  public String toString() {
    // returns a string representation of list with randomized order of nodes
    StringBuilder sb = new StringBuilder();
    sb.append("RandomBag(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }

  public static void main(String[] args) {
    

  }

}
