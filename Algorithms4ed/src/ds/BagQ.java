package ds;

import java.util.Arrays;

// based on http://algs4.cs.princeton.edu/13stacks/Bag.java

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BagQ<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of bag
    private Node<Item> last;     // end of bag
    private int n;               // number of elements in bag
    private Class<?> iclass;

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    public BagQ() {}
    
    public BagQ(Item[] a) {
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
    
    public void add(Item item) { // Add item to the end of the list.
      if (item == null) throw new IllegalArgumentException("add: item is null");
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
    
    @Override
    public String toString() {
      if (isEmpty()) return "()";
      Item[] oa = toArray();
//      Arrays.sort(oa)
      StringBuilder sb = new StringBuilder("(");
      for (Object i : oa) sb.append(i+",");
      return sb.replace(sb.length()-1, sb.length(),")").toString(); 
    }
    
    public void show() {
      if (isEmpty()) return;
      Item[] oa = toArray();
      Arrays.sort(oa);
      StringBuilder sb = new StringBuilder();
      for (Object i : oa) sb.append(i+",");
      System.out.println(sb.delete(sb.length()-1, sb.length()));
    }

 
    public static void main(String[] args) {
      
        BagQ<String> bag = new BagQ<String>();

        System.out.println("size of bag = " + bag.size());
        for (String s : bag) {
            System.out.println(s);
        }
    }

}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/

