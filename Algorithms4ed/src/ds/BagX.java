package ds;

import java.util.Arrays;
import java.util.Comparator;

// based on http://algs4.cs.princeton.edu/13stacks/Bag.java

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BagX<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of bag
    private Node<Item> last;     // end of bag
    private int n;               // number of elements in bag
    private Class<?> iclass;

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    public BagX() {}
    
    public BagX(Item[] a) {
      if (a == null) return;
      iclass = a.getClass().getComponentType();
      for (int i = 0; i < a.length; i++) if (a[i] != null) add(a[i]);
    }

    public boolean isEmpty() { return first == null; }

    public int size() { return n;  }

//    public void add(Item item) {
//        Node<Item> oldfirst = first;
//        first = new Node<Item>();
//        first.item = item;
//        first.next = oldfirst;
//        n++;
//    }
    
    public int count(Item x) {
      // return the number of occurrences of x in this
      if (isEmpty()) return 0;
      int c = 0;
      Iterator<Item> it = iterator();
      if (x == null) while (it.hasNext()) { if (it.next() == null) c++; }
      if (x != null) while (it.hasNext()) { if (x.equals(it.next())) c++; }
      return c;
    }
    
    public void add(Item item) { // add item to the head of the list     
      if (item == null) throw new IllegalArgumentException("add: item is null");
      Node<Item> oldfirst = first;
      first = new Node<Item>();
      first.item = item;
      first.next = oldfirst;
      n++;
      if (iclass == null) iclass = item.getClass();
    }
    
    public void enqueue(Item item) { // Add item to the end of the list.
      if (item == null) throw new IllegalArgumentException("enqueue: item is null");
      Node<Item> oldlast = last;
      last = new Node<Item>();
      last.item = item;
      last.next = null;
      if (isEmpty()) first = last;
      else oldlast.next = last;
      n++;
      if (iclass == null) iclass = item.getClass();
    }
    
    public Node<Item> remove(int k) {
      // remove the kth element counting from first which is the 0th element
      if (k < 0) throw new IllegalArgumentException("get: k must be >= 0");
      if (isEmpty()) throw new NoSuchElementException("get: LinkedList underflow");
      if (size()-1 < k) 
        throw new IndexOutOfBoundsException("get: LinkedList has max index "+(k-1));
      if (k == 0) return removeFirst();
      if (k == size()-1) return removeLast();
      Node<Item> previous = null;
      Node<Item> node = first;
      int i = 0;
      while (i < k) {
        previous = node;
        node = node.next;
        i++;
      }
      previous.next = node.next;
      n--;
      return node;
    }
    
    public boolean remove(Item x) {
      // if x is in this remove its first occurrence
      if (isEmpty()) return false;
      if (first.item.equals(x)) { removeFirst(); return true; }
      Node<Item> node = first;
      int i = 0;
      while (i < n) {
        i++;
        node = node.next;
        if (node.item.equals(x)) { remove(i); return true; }
      }
      return false;
    }
    
    public Node<Item> removeFirst() { 
      // Remove and return the first node or return null
      if (isEmpty()) return null;
      Node<Item> oldFirst = first;
      first = first.next; 
      if (isEmpty()) last = null;
      n--;
      return oldFirst;
    }
    
    public Node<Item> removeLast() { 
      // Remove and return last or
      // return null if isEmpty()
      if (isEmpty()) return null;
      if (size() == 1) return removeFirst();
      Node<Item> oldLast = last;
      Node<Item> node = first;
      Node<Item> previous = null;
      while (node.next != null) {
        previous = node;
        node = node.next;
      }
      previous.next = null;
      last = previous;
      n--;
      return oldLast;
    }

    public Item peek() {
      if (first == null) return null;
      return first.item;
    }
    
    public boolean contains(Item x) {
      if (isEmpty()) return false;
      Node<Item> node = first;
      if (node.item == x || node.item.equals(x)) return true;
      while (node.next != null) {
        node = node.next;
        if (node.item == x || node.item.equals(x)) return true;
      }
      return false;
    }

    public Iterator<Item> iterator()  { return new ListIterator(); }

    private class ListIterator implements Iterator<Item> {
        private Node<Item> current = first;

        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }
    
    @SuppressWarnings("unchecked")
    public Item[] toArray() { 
      if (isEmpty()) return (Item[])(new Object[0]);
      return v.ArrayUtils.toArray(iterator(),iclass); 
    }
    
    @SuppressWarnings("unchecked")
    public Item[] toSortedArray(Comparator<Item> c) { 
      if (isEmpty()) return (Item[])(new Object[0]);
      Item[] a = v.ArrayUtils.toArray(iterator(),iclass);
      Arrays.sort(a,c);
      return a;
    }
    
    public Seq<Item> toSeq() {
      if (isEmpty()) return new Seq<Item>();
      return new Seq<Item>(toArray());
    }
    
    public boolean containsAll(BagX<Item> b) {
      // used in hashCode()
      for (Item i : b) if (!contains(i)) return false;
      return true;
    }
    
    @Override
    public String toString() {
      if (isEmpty()) return "()";
      Item[] oa = toArray();
      StringBuilder sb = new StringBuilder("(");
      for (Object i : oa) sb.append(i+",");
      return sb.replace(sb.length()-1, sb.length(),")").toString(); 
    }
    
    public String showToString() {
      if (isEmpty()) return "";
      Item[] oa = toArray();
      StringBuilder sb = new StringBuilder();
      for (Object i : oa) sb.append(i+",");
      return sb.delete(sb.length()-1, sb.length()).toString();
    }
    
    public void show() {
      if (isEmpty()) return;
      Item[] oa = toArray();
      StringBuilder sb = new StringBuilder();
      for (Object i : oa) sb.append(i+",");
      System.out.println(sb.delete(sb.length()-1, sb.length()));
    }
    
    @Override
    public int hashCode() {
      int hashCode = 1;
      for (Item i: this)
        hashCode = 31*hashCode + (i==null ? 0 : i.hashCode());
      return hashCode;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      BagX other = (BagX) obj;
      if (first == null) {
        if (other.first != null) return false;
      } else if (!first.equals(other.first)) return false;
      if (iclass == null) {
        if (other.iclass != null) return false;
      } else if (!iclass.equals(other.iclass)) return false;
      if (last == null) {
        if (other.last != null) return false;
      } else if (!last.equals(other.last)) return false;
      if (n != other.n) return false;
      return containsAll(other);
    }
    

    public static void main(String[] args) {
      
        BagX<String> bag = new BagX<String>();

        System.out.println("size of bag = " + bag.size());
        for (String s : bag) {
            System.out.println(s);
        }
    }


    
    

}
