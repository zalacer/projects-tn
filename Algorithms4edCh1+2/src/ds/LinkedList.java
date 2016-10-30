package ds;

import static java.lang.Math.abs;
import static java.lang.System.identityHashCode;
import static java.lang.reflect.Array.newInstance;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.range;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.stddev;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import analysis.Timer;
import v.ArrayUtils;

@SuppressWarnings("unused")
public class LinkedList<T> implements Iterable<T>, Cloneable {
  //derived from Queue implementation provided on p151 of the text
  private Node<T> first; // link to least recently added node
  private Node<T> last; // link to most recently added node
  private int N; // number of nodes in the list
  private int mod ; // modification counter
  
  public LinkedList(){};
  
  @SafeVarargs
  public LinkedList(T...a) {
    for (T i : a) add(i);
    mod = 0;
  }
  
  public LinkedList(Node<T> x) {
    this.first = x;
    if (first == null) {
      this.last = null;
      N = 0;
    } else if (first.next == null) {
      this.last = first;
      N = 1;
    } else {
      N++;
      Node<T> node = first;
      while (node.next != null) {
        N++;
        node = node.next;
      }
      this.last = node;
    }
    mod = 0;
  }
  
  public LinkedList(int n, String s) {
    // create a list of n nodes each with item == null
    // s can be anything since using it only to differate this constructor
    this.first = new Node<T>();
    Node<T> node = first;
    for (int i = 1; i < n; i++) {
      node.next = new Node<T>();
      node = node.next;
    }
    node.next = null; this.last = node;
    N = n; mod = 0;
  }
  
  // functional interfaces for shuffle from:
  //https://github.com/claudemartin/Recursive/blob/master/Recursive/src/ch/claude_martin/recursive/Recursive.java
  @FunctionalInterface
  public interface RecursiveBiFunction<T, U, R> {
    R apply(final T t, final U u, final BiFunction<T, U, R> self);
  }
  
  public static class Recursive<F> {
    private F f;
    public static <T, U, R> BiFunction<T, U, R> biFunction(RecursiveBiFunction<T, U, R> f) {
      final Recursive<BiFunction<T, U, R>> r = new Recursive<>();
      return r.f = (t, u) -> f.apply(t, u, r.f);
    }
  }
  
  @FunctionalInterface
  public interface Function4<A, B, C, D, E> {
    E apply(A a, B b, C c, D d);
  }
  
  // class methods
  public void classMethods() {
    // marker method
  }
  
  public Node<T> first() { return first; }
    
  public Node<T> getFirst() { return first; }
  
  public void setFirst(Node<T> first) { this.first = first; }
  
  public Node<T> last() { return last; }
  
  public Node<T> getLast() { return last; }
  
  public void setLast(Node<T> last) { this.last = last; }
  
  public int size() { return N; }
  
  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public void add(T item) { 
    // Add item to the end of the list.
    Node<T> oldlast = last;
    Node<T> newLast = new Node<T>();
    newLast.item = item;
    newLast.next = null;
    last = newLast;
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++; mod++;
  }
  
  public void prepend(T item) { 
    // Add item to the beginning of the list.
    Node<T> newFirst = new Node<T>();
    newFirst.item = item;
    newFirst.next = first;
    first = newFirst; mod++;
    N++; mod++;
  }
  
  public void prepend(Node<T> node) { 
    // Add node to the beginning of the list.
    node.next = first; 
    first = node; 
    N++; mod++;
  }
  
  public Node<T> previous(Node<T> n) {
    // return the node previous to n or null if 
    // n == null, n == first or n isn't in the list
    if (n == null ) return null;
    Node<T> previous = null; 
    Node<T> node = first;
    if (n == node) return null;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (node == n) return previous;
    }
    return null;
  }
  
  public void swap(Node<T> a, Node<T> b) {
    // swap nodes a and b and return true if possible
    // else return false
    T ia = a.item; a.item = b.item; b.item = ia;
  }
  
  public Node<T> get(int k) {
    // return the kth element counting from first which is the 0th element
    if (k < 0) throw new IllegalArgumentException("get: k must be >= 0");
    if (isEmpty()) throw new NoSuchElementException("get: LinkedList underflow");
    if (size()-1 < k) 
      throw new IndexOutOfBoundsException("get: LinkedList has max index "+(k-1));
    if (k == 0) return first;
    if (k == size()-1) return last;
    Node<T> node = first;
    int i = 0;
    while (node.next != null && i < k) {
      node = node.next;
      i++;
    }
    return node;
  }
  
  public int getIndex(Node<T> n) {
    // return the int index of n in list or -1 if list
    // is empty or n isn't in it
    if (isEmpty()) return -1;
    if (n == first) return 0;
    int size = size();
    if (size == 1) return -1;
    if (n == last) return size-1;
    if (size == 2) return -1;
    Node<T> node = first;
    int i = 1;
    while (node.next != n && i < size) {
      node = node.next;
      i++;
    }
    if (i < size-1) return i;
    else return -1;
  }
  
  public Node<T> remove(int k) {
    // remove the kth element counting from first which is the 0th element
    if (k < 0) throw new IllegalArgumentException("get: k must be >= 0");
    if (isEmpty()) throw new NoSuchElementException("get: LinkedList underflow");
    if (size()-1 < k) 
      throw new IndexOutOfBoundsException("get: LinkedList has max index "+(k-1));
    if (k == 0) return removeFirst();
    if (k == size()-1) return removeLast();
    Node<T> previous = null;
    Node<T> node = first;
    int i = 0;
    while (i < k) {
      previous = node;
      node = node.next;
      i++;
    }
    previous.next = node.next;
    N--; mod++;
    return node;
  }

  public Node<T> removeFirst() { 
    // Remove and return the first node or return null
    if (isEmpty()) return null;
    Node<T> oldFirst = first;
    first = first.next; 
    if (isEmpty()) last = null;
    N--; mod++;
    return oldFirst;
  }
  
  public Node<T> removeLast() { 
    // Remove and return last or
    // return null if isEmpty()
    if (isEmpty()) return null;
    if (size() == 1) return removeFirst();
    Node<T> oldLast = last;
    Node<T> node = first;
    Node<T> previous = null;
    while (node.next != null) {
      previous = node;
      node = node.next;
    }
    previous.next = null;
    last = previous;
    N--; mod++;
    return oldLast;
  }
  
  public Node<T> removeAfter(Node<T> node) {
    // removes the Node after node except if node, node.next is null
    // or node isn't in the list
    if (node == null || node.next == null) return null;
    Node<T> after = node.next;
    node.next = node.next.next;
    if (node.next == null) last = node;
    N--; mod++;
    return after;
  }
  
  public boolean insertAfter(Node<T> node, Node<T> after) {
    // insert after after node except if either is null
    // or node isn't in the list
    if (node == null || after == null) return false;
    after.next = node.next;
    node.next = after;
    if (after.next == null) last = after;
    N++; mod++;
    return true;
  }
  
  public boolean insertBefore(Node<T> n, Node<T> before) {
    // insert before before n and return true 
    // else return false if either is null or !contains(n)
    if (n == null || before == null) return false;
    if (n == first) { prepend(before); return true; }
    Node<T> previous = null; 
    Node<T> node = first;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (node == n) {
          previous.next = before;
          before.next = n;
          N++; mod++;
          return true;
      }
    }
    return false;
  }
  
  public Node<T> removeBefore(Node<T> n) {
    // remove and return the node before n 
    // else return null if n is null or first
    if (n == null || n == first) return null;
    Node<T> previous = null; Node<T> previous2 = null; 
    Node<T> node = first;
    while (node.next != null) {
      previous2 = previous;
      previous = node;
      node = node.next;
      if (node == n) {
        if (previous2 == null) {
         return removeFirst();
        } else {
          Node<T> p = previous;
          previous2.next = n;
          N--; mod++;
          return p;
        }
      }
    }
    return null;
  }
  
  public boolean contains(T t) {
    // returns true if it is in this list else false
    if (isEmpty()) return false;
    Node<T> node = first;
    if (node.item.equals(t)) return true;
    while (node.next != null) {
      node = node.next;
      if (node.item.equals(t)) return true;
    }
    return false;
  }
  
  public boolean contains(Node<T> n) {
    // returns true if it is in this list else false
    if (isEmpty()) return false;
    Node<T> node = first;
    if (n == first) return true;
    while (node.next != null) {
      node = node.next;
      if (n == node) return true;
    }
    return false;
  }
  
  public ListIterator<T> iterator() { return new Literator(); }

  private class Literator implements ListIterator<T> {
    private Node<T> next;
    private Node<T> current = first;
    public boolean hasNext() { return current != null; }
    public T next() {
      next = current;
      current = current.next;
      return next.item;
    }
    public void set(T t) {
      next.item = t;
    }
    public void add(T t) {
      throw new  UnsupportedOperationException();
    }
    public boolean hasPrevious() {
      throw new  UnsupportedOperationException();
    }
    public int nextIndex() {
      throw new  UnsupportedOperationException();
    }
    public T previous() {
      throw new  UnsupportedOperationException();
    }
    public int previousIndex() {
      throw new  UnsupportedOperationException();
    }
    public void remove() {
      throw new  UnsupportedOperationException();
    }
  }
  
  public Iterator<Node<T>> nodeIterator() { return new NodeIterator(); }
  
  private class NodeIterator implements Iterator<Node<T>> {
    private Node<T> next;
    private Node<T> current = first;
    public boolean hasNext() { return current != null; }
    public Node<T> next() { 
      next = current;
      current = current.next;
      return next;
    }
  }
  
  public Object clone() {
    return copy(this);
  }
 
  public Object[] toArray() {
    // return an Object[] of the items of nodes in this in order
    Object[] a = new Object[size()];
    Iterator<T> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }

  @SafeVarargs
  @SuppressWarnings("unchecked")
  public final T[] toArray(T...item) {
    if (item.length == 0) throw new IllegalArgumentException("toArray: item length "
        + "must be > 0");
    // return a T[] of the items of nodes in this in order
    T[] a = (T[]) Array.newInstance(item[0].getClass(), size());
    Iterator<T> it = iterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }
  
  @SuppressWarnings("unchecked")
  public final Node<T>[] toNodeArray() {
    Node<T>[] a = (Node<T>[]) Array.newInstance(Node.class, size());
    Iterator<Node<T>> it = nodeIterator();
    int i = 0;
    while (it.hasNext()) a[i++] = it.next();
    return a;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + N;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + ((last == null) ? 0 : last.hashCode());
    result = prime * result + Arrays.hashCode(toArray());
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
    LinkedList other = (LinkedList) obj;
    if (N != other.N)
      return false;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (last == null) {
      if (other.last != null)
        return false;
    } else if (!last.equals(other.last))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedList(");
    if (isEmpty()) return sb.append(")").toString();
    for (T i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  public String toSimpleString() {
    StringBuilder sb = new StringBuilder();
    if (isEmpty()) return sb.append("empty").toString();
    for (T i : this) sb.append(""+i+" ");
    return sb.toString();
  }
  
  // for Ex1320
  public Node<T> delete(int k) {
    return remove(k);
  }
  
  // for Ex1225
  public Node<T> getNodeInstance(T t) {
    return new Node<T>(t);
  }
  
  // static methods
  public static void staticMethods() {
    //marker method 
  }
  
  public static <T> void sortSlowAndComplicated(LinkedList<T> ll, Comparator<? super T> c) {
    // natural mergesort ll inplace based on the items in its nodes
    int expectedMod = ll.mod;
    Node<T> z = ll.first;
    int n = ll.size();
    Node<T> llast = ll.last;
    if (n < 2) return;
    if (ll == null || ll.size() == 1) return;
    // create an aux list same length as ll
    LinkedList<T> llaux = new LinkedList<T>(n,"");
    Node<T> aux = llaux.first;
    // get the runs
    Queue<Integer> runs = getIntRuns(z, n, c);
    int run1 = 0, run2 = 0; int rlen = 0; // "offset" in ll
    // dequeue two runs and continue or merge
    while (runs.size() > 1) {  
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == n) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      // extract node lists corresponding to the runs. this decomposes 
      // z=ll.first into 3 node lists if rlen==0 or 4 if rlen>0
      Node<T> a, b, e, t, tmp, atmp, btmp, etmp, m, mtmp, p, ptmp, last;
      int rtmp = rlen == 0 ? 0 : rlen-1;
      a = z; tmp = z; 
      if (rtmp > 0) {
        for (int i = 0; i < rtmp; i++) tmp=tmp.next;
        a = tmp.next; atmp = tmp.next; tmp.next = null;
        for (int i = 0; i < run1-1; i++) atmp = atmp.next;
      } else {
        a = z; atmp = z;
        for (int i = 0; i < run1-1; i++) atmp = atmp.next;
      }
      b = atmp.next; btmp = atmp.next; atmp.next = null;
      for (int i = 0; i < run2-1; i++) btmp = btmp.next;
      e = btmp.next; etmp = btmp.next; btmp.next = null;
      // merge the node lists corresponding to the runs
      m = mergeRolfe2(a,b,c); // m is the node beginning the merged list
      // rebuild z by combining the merges list with the remaining extracted lists
      if (rtmp > 0) {
        ptmp = z;
        while (ptmp.next != null) ptmp = ptmp.next;
        ptmp.next = m;
        mtmp = m;
        while (mtmp.next != null) mtmp = mtmp.next;
        mtmp.next = e;
      } else {
        mtmp = m;
        while (mtmp.next != null) mtmp = mtmp.next;
        mtmp.next = e;
        ll.first = z = m;
      }
      // enqueue a new run for the merged list and update rlen
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % n;
      // update ll.last
      for (last = z; last.next != null; last = last.next) ;
      ll.last = last;
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> void sortRolfe(LinkedList<T> ll) {
    // natural mergesort ll inplace based on the items in its nodes
    Node<T>[] work; Node<T> head, tail, parent, child;
    int j, k, len, size = ll.N;; 
    if ( size < 2 ) return;
    
    head = ll.first; 

    work = (Node<T>[])newInstance(Node.class, size+1);

    // split head into runs and put each into work
    for ( len = 0; head != null; len++ ) {
      parent = head;
      work[len] = parent;
      child = parent.next;
      while ( child != null && parent.item.compareTo(child.item) <= 0 ) {
        parent = child;
        child = parent.next;
      }
      head = child;
      parent.next = null;
    }
    work[len] = null;        // Extra null reference for odd cases
    
    for ( ; len > 1; len = (len+1)/2 ) {
      for ( j = k = 0; k < len; j++, k += 2 )
        work[j] = mergeRolfe(work[k], work[k+1]);
      work[j] = null;       // Extra null reference for odd cases
    }

    head = work[0];
    for ( tail = head; tail.next != null; tail = tail.next ) ;
    ll.first = head; ll.last = tail;
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void sortRolfe2(LinkedList<T> ll, Comparator<? super T> c) {
    // natural mergesort ll inplace based on the items in its nodes
    Node<T>[] work; Node<T> head, tail, parent, child;
    int j, k, len, size = ll.N;; 
    if ( size < 2 ) return;
    
    head = ll.first; 

    work = (Node<T>[])newInstance(Node.class, size);

    // split head into runs and put each into work
    for ( len = 0; head != null; len++ ) {
      parent = head;
      work[len] = parent;
      child = parent.next;
      while ( child != null && c.compare(parent.item, child.item) <= 0 ) {
        parent = child;
        child = parent.next;
      }
      head = child;
      parent.next = null;
    }

    for ( ; len > 1; len = (len+1)/2 ) {
      for ( j = k = 0; k < len; j++, k += 2 )
        work[j] = mergeRolfe2(work[k], work[k+1], c);
    }

    head = work[0];
    for ( tail = head; tail.next != null; tail = tail.next ) ;
    ll.first = head; ll.last = tail;
  }
  
  public static <T extends Comparable<? super T>> void sort(LinkedList<T> ll) {
    // using sortNM because it's fastest even though it takes more space
    sortNM(ll);
  }
  
  public static <T extends Comparable<? super T>> void sortNM(LinkedList<T> ll) {
    // natural mergesort with monotonically increasing runs adapted for LinkedList
    Node<T> first = ll.first;
    int n = ll.size();
    T[] z = ll.toArray(first.item);
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(), z.length); 
    Queue<Integer> runs = getRunsNM(z); int run1 = 0, run2 = 0;
    int rlen = 0; // offset in z
    while (runs.size() > 1) {
      run1 = runs.dequeue();
      // ensure one pass through z at a time with no overlap
      if (run1 + rlen == n) { runs.enqueue(run1); rlen = 0; continue; }
      run2 = runs.dequeue();
      mergeNM(z, aux, rlen, rlen+run1-1, rlen+run1+run2-1);
      runs.enqueue(run1+run2); rlen = (rlen+run1+run2) % n;
    }
    first.item = z[0];
    Node<T> node = first;
    for (int i = 1; i < z.length; i++) {
      node = node.next; node.item = z[i];
    } 
  }
 
  public static <T extends Comparable<? super T>> Node<T> mergeRolfe (Node<T> left, Node<T> right) {
    Node<T> head, current, tail;
    // from http://penguin.ewu.edu/~trolfe/NaturalMerge/ListNode.java
    // merge the lists beginning with a and b both of which should be sorted
    // by the contents of type T of their nodes
    // for use by sort(LinkedList<T>, Comparator<? super T>
    if (left == null) return right;
    if (right == null) return left;

    if (right.item.compareTo(left.item) > 0) {
      head = tail = left;  left = left.next;  
    }  else {
      head = tail = right;  right = right.next;  
    }

    while (left != null && right != null) {
      if (right.item.compareTo(left.item) > 0) {
        current = left;  left = left.next;
      } else {
        current = right; right = right.next;
      }
      current.next = null;
      tail = tail.next = current;
    }

    tail.next = left != null ? left : right;
    return head;
  }
  
  public static <T> Node<T> mergeRolfe2 (Node<T> left, Node<T> right, Comparator<? super T> c) {
    Node<T> head, current, tail;
    // from http://penguin.ewu.edu/~trolfe/NaturalMerge/ListNode.java
    // merge the lists beginning with a and b both of which should be sorted
    // by the contents of type T of their nodes
    // for use by sort(LinkedList<T>, Comparator<? super T>
    if (left == null) return right;
    if (right == null) return left;

    if (c.compare(right.item, left.item) > 0) {
      head = tail = left;  left = left.next;  
    }  else {
      head = tail = right;  right = right.next;  
    }

    while (left != null && right != null) {
      if (c.compare(right.item, left.item) > 0) {
        current = left;  left = left.next;
      } else {
        current = right; right = right.next;
      }
      current.next = null;
      tail = tail.next = current;
    }

    tail.next = left != null ? left : right;
    return head;
  }
  
  public static <T> Node<T> merge2(Node<T> a, Node<T> b, Comparator<? super T> c) {
    // from  http://javabypatel.blogspot.in/2015/12/merge-sort-linked-list.html
    // merge the lists beginning with a and b both of which should be sorted
    // by the contents of type T of their nodes
    // for use by sort(LinkedList<T>, Comparator<? super T>
    Node<T> merged = null;
    Node<T> tmp = null;
    Node<T> last = null;

    while(a != null && b != null){
      if (c.compare(a.item, b.item) <= 0) {
        tmp = new Node<T>(a.item);
        a = a.next;
      } else{
        tmp = new Node<T>(b.item);
        b = b.next;
      }
      if (merged == null) merged = tmp;
      else last.next = tmp;     
      last = tmp;
    }

    last.next = a != null ? a : b;

    return merged;
  }
  
  public static <T extends Comparable<? super T>> void mergeNMDouble(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // the standard merge for this course
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) 
      aux[k] = a[k];
      for (int k = lo; k <= hi; k++) { 
        if (i > mid) a[k] = aux[j++];
        else if (j > hi ) a[k] = aux[i++];
        else if (aux[j].compareTo(aux[i]) < 0) a[k] = aux[j++];
        else a[k] = aux[i++];
      }
  }
  
  public static <T extends Comparable<? super T>> void mergeNM(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    // the standard merge for this course
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) 
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { 
      if (i > mid) a[k] = aux[j++];
      else if (j > hi ) a[k] = aux[i++];
      else if (aux[j].compareTo(aux[i]) < 0) a[k] = aux[j++];
      else a[k] = aux[i++];
    }
  }
  
  public static <T> Queue<Integer> getIntRuns(Node<T> n, int size, Comparator<? super T> c) {
    // get runs for natural mergesort of linked node lists. each run is represented
    // by its length as an int. all the runs are returned in order in a Queue<Integer>.
    Queue<Integer> runs = new Queue<Integer>();
    Node<T> node = n;
    int i = 0, j = 0;
    while (node.next != null) {
      j = i;
      while (node.next != null && c.compare(node.next.item, node.item) > 0) {
        i++; node = node.next; 
      }
      runs.enqueue(++i - j);
      if (node.next != null) node = node.next();
    }
    if (i < size) runs.enqueue(1);
    return runs;
  }
  
  public static <T> Queue<Node<T>> getNodeRuns(Node<T> n, int size, Comparator<? super T> c) {
    // get runs for natural mergesort of linked node lists. each run is a node list 
    // represented by its head. all runs are returned in order in a Queue<Node<T>>.
    Queue<Node<T>> runs = new Queue<Node<T>>();
    Node<T> node = n, start, start2;
    int i = 0, j = 0;
    
    while (node != null && node.next != null) {
      j = i;
      start = node;
      while (start.next != null && c.compare(start.next.item, start.item) >= 0) {
        i++; start = start.next; 
      }
      runs.enqueue(node); 
      node = start.next; start.next = null;  i++;     
//      runs.enqueue(++i - j);
    }
    if (i < size) runs.enqueue(node);
    Node<T>[] v = runs.toArray(runs.peek());    
    return runs;
  }
  
  // this is the NaturalMerge getRuns for testing against the ll merge
  public static <T extends Comparable<? super T>> Queue<Integer> getRunsNM(T[] a) {
    // search for increasing runs and return their lengths in a queue. 
    // this works since they are contiguous enabling calculation of 
    // their indices for merge input. run only once and after that new   
    // run lengths are calculated and enqueued in sort().
    Queue<Integer> runs = new Queue<Integer>();
    int i = 0, c= 0;
    while(i < a.length) {
      c = i;
      if (i < a.length -1) 
        while(i < a.length -1 && less(a[i], a[i+1])) i++;
      runs.enqueue(++i - c); 
    }
//    int[] r = (int[])unbox(runs.toArray(new Integer[2]));
//    System.out.println("avgRunLength="+mean(r));
    return runs;
  }
  
//  // this merge works but hard to integrate
//  public static <T> Node<T> merge2(Node<T> a, Node<T> b, Comparator<? super T> c) {
//    // merge the lists beginning with a and b both of which should be sorted
//    // by the contents of type T of their nodes
//    // for use by sort(LinkedList<T>, Comparator<? super T>
//    Node<T> merged = null;
//    Node<T> tmp = null;
//    Node<T> last = null;
//
//    while(a != null && b != null){
//      if (c.compare(a.item, b.item) <= 0) {
//        tmp = new Node<T>(a.item);
//        a = a.next;
//      } else{
//        tmp = new Node<T>(b.item);
//        b = b.next;
//      }
//      if (merged == null) merged = tmp;
//      else last.next = tmp;     
//      last = tmp;
//    }
//
//    last.next = a != null ? a : b;
//
//    return merged;
//  }
//  
//  public static <T> void merge(
//      Node<T> z, Node<T> aux, int lo, int mid, int hi, Comparator<? super T> c) { 
//    // this is based on the standard merge for this course
//    // aux has been created with the same number of nodes as z but all empty
//    int i = lo, j = mid+1;
//    System.out.println("i="+i+" j="+j+" hi="+hi);
//    //    for (int k = lo; k <= hi; k++) 
//    //      aux[k] = a[k];
//    //    Node<T> ca = a;  for (int k = lo; k <= hi; k++) ca = ca.next;
//    
//    Node<T> cz = z, auxtmp, auxi, auxj ; 
//    
//    // copy cz to aux from lo..hi
//    aux.item = z.item; auxtmp = aux;
//    for (int k = 0; k < lo; k++) { cz = cz.next; aux = aux.next; }
//    for (int k = lo; k < hi; k++) { cz = cz.next; aux = aux.next; aux.item = cz.item;  }
//    aux = auxtmp;
//    System.out.println("size(aux)="+size(aux));
//    System.out.println("aux="+nodeListToString(aux));
//    
//    // auxi is aux positioned at lo
//    auxi = aux; 
//    for (int k = 0; k < lo; k++) auxi = auxi.next;
//    System.out.println("size(auxi)="+size(auxi));
//    System.out.println("auxi="+nodeListToString(auxi));
//    
//    // auxj is aux positioned at mid+1
//    auxj = aux;
//    for (int k = 0; k < mid+1; k++) { auxj = auxj.next; }
//    System.out.println("size(auxj)="+size(auxj));
//    System.out.println("auxj="+nodeListToString(auxj));
//    
//    // cz is z positioned at lo
//    cz = z; 
//    for (int k = 0; k < lo; k++) cz = cz.next;
//    System.out.println("size(cz)="+size(cz));
//    System.out.println("cz="+nodeListToString(cz));
//    
//    for (int k = lo; k <= hi; k++) { 
//      if (i > mid) { cz.item = auxj.item; auxj = auxj.next; j++; }
//      else if (j > hi ) { cz.item = auxi.item; auxi = auxi.next; i++;}
//      else if (!greater(auxj, auxi, c)) { cz.item = auxj.item; auxj = auxj.next; j++; }
//      else { cz.item = auxi.item; auxi = auxi.next; i++;}
//      cz = cz.next;
//      System.out.println("i="+i+" j="+j+" k="+k);
//    }
//    
//    //!greater(node, node.next, c) less(cauxj, cauxi, c)
//
//    // for reference
////    for (int k = lo; k <= hi; k++) { 
////      if (i > mid) a[k] = aux[j++];
////      else if (j > hi ) a[k] = aux[i++];
////      else if (less(aux[j], aux[i], c)) a[k] = aux[j++];
////      else a[k] = aux[i++];
////    }
//  }
  
  public static <T> LinkedList<T> copy(LinkedList<T> x) {
    // make and return a shallow copy of x. 
    Node<T> first  = x.first;
    Node<T> copy  = new Node<T>(first.item);
    Node<T> cprev = copy;
    Node<T> cnext = null;
    Node<T> node = first;
    while(node.next != null) {
      node = node.next;
      cnext = new Node<T>(node.item);
      cprev.next = cnext;
      cprev = cnext; 
    }
    cnext.next = null;
    return new LinkedList<T>(copy);
  }
  
  public static <T extends Comparable<? super T>> boolean less(Node<T> a, Node<T> b) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return a.item.compareTo(b.item) < 0;
  }
  
  public static <T> int len(Node<T> n) {
    // return the length of the list starting with n
    if (n == null) return 0;
    if (n.next == null) return 1;
    Node<T> start = n;
    int i = 1;
    while (start.next != null) { i++; start = start.next; }
    return i;
  }
  
  // this is the NaturalMerge less for testing with its merge
  public static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }
  
  public static <T extends Comparable<? super T>> boolean greater(T v, T w) { 
    return v.compareTo(w) > 0; 
  }
  
  public static <T extends Comparable<? super T>> boolean greater(Node<T> a, Node<T> b) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return a.item.compareTo(b.item) > 0;
  }
  
  public static <T extends Comparable<? super T>> boolean equalOrLess(Node<T> a, Node<T> b) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return a.item.compareTo(b.item) <= 0;
  }
  
  public static <T> boolean less(Node<T> a, Node<T> b, Comparator<? super T> c) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return c.compare(a.item, b.item) < 0;
  }
  
  public static <T> boolean greater(Node<T> a, Node<T> b, Comparator<? super T> c) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return c.compare(a.item, b.item) > 0;
  }
  
  public static <T> boolean equalOrLess(Node<T> a, Node<T> b, Comparator<? super T> c) {
    // return true if a.item.compareTo(b.item) < 0 else return false
    return c.compare(a.item, b.item) <= 0;
  }
  
  public static <T> void exch(Node<T> a, Node<T> b, Node<T> first) {
    // swap the data in nodes a and b, if possible and return true else return 
    // false, given that a and b are in the list beginning with first
    T ia = a.item; a.item = b.item; b.item = ia;    
  }
  
  public static <T> Node<T> before(Node<T> n, Node<T> first) {
    // return the node previous to n or null if n == null, 
    // n == first or n isn't in the list
    // given that n is in the list beginning with first
    if (n == null ) return null;
    Node<T> previous = null; 
    Node<T> node = first;
    if (n == node) return null;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (node == n) return previous;
    }
    return null;
  }
  
  public static <T extends Comparable<? super T>> boolean isReverseSorted(LinkedList<T> ll) {
    Node<T> node = ll.first;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (previous.item.compareTo(node.item) < 0) return false;
    }
    return true;
  }
  
  public static <T extends Comparable<? super T>> boolean isReverseSorted(Node<T> n) {
    Node<T> node = n;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (previous.item.compareTo(node.item) < 0) return false;
    }
    return true;
  }
  
  public static <T extends Comparable<? super T>> boolean isSorted(LinkedList<T> ll) {
    Node<T> node = ll.first;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (previous.item.compareTo(node.item) > 0) return false;
    }
    return true;
  }
  
  public static <T extends Comparable<? super T>> boolean isSorted(Node<T> n) {
    Node<T> node = n;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (previous.item.compareTo(node.item) > 0) return false;
    }
    return true;
  }
    
  public static <T> boolean isReverseSorted(LinkedList<T> ll, Comparator<? super T> c) {
    Node<T> node = ll.first;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (c.compare(previous.item, node.item) < 0) return false;
    }
    return true;
  }
  
  public static <T> boolean isReverseSorted(Node<T> n, Comparator<? super T> c) {
    Node<T> node = n;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (c.compare(previous.item, node.item) < 0) return false;
    }
    return true;
  }
  
  public static <T> boolean isSorted(LinkedList<T> ll, Comparator<? super T> c) {
    Node<T> node = ll.first;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (c.compare(previous.item, node.item) > 0) return false;
    }
    return true;
  }
  
  public static <T> boolean isSorted(Node<T> n, Comparator<? super T> c) {
    Node<T> node = n;  Node<T> previous;
    while (node.next != null) {
      previous = node;
      node = node.next;
      if (c.compare(previous.item, node.item) > 0) return false;
    }
    return true;
  }
  
  public static <T> boolean find(LinkedList<T> ll, T it) {
    // returns true if ll contains it else false
    return ll.contains(it);
  }
  
  public static <T extends Comparable<? super T>> T max(Node<T> first) {
    if (first == null) throw new IllegalArgumentException("max: first must be non null");
    if (first.next == null) return first.item;
    T max = first.item;
    Node<T> node = first;
    while(node.next != null) {
      node = node.next;
      if (node.item.compareTo(max) > 0) max = node.item;
    }
    return max;
  }
  
  public static <T extends Comparable<? super T>> T min(Node<T> first) {
    if (first == null) throw new IllegalArgumentException("min: first must be non null");
    if (first.next == null) return first.item;
    T min = first.item;
    Node<T> node = first;
    while(node.next != null) {
      node = node.next;
      if (node.item.compareTo(min) < 0) min = node.item;
    }
    return min;
  }
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> T maxRecursive(Node<T> first, T...t) {
    // the second argument should not be supplied on the initial invocation
    
    if (first == null && t == null) throw new IllegalArgumentException(
        "maxRecursive: both arguments cannot be null at once");
        
    T max = null;
    if (t != null && t.length > 0) max = t[0];
    
    // base case 1
    // returns null on initial invocation with first = null
    if (first == null) return max;
    
    //base case 2
    if (first.next == null) {
      if (max != null) {
        return first.item.compareTo(max) > 0 ? first.item : max;
      } else return first.item;
    }
    
    // set max for recursion
    if (max == null) {
      max = first.item;
    } else if (first.item.compareTo(max) > 0) max = first.item;
    
    // do it again down the list
    return maxRecursive(first.next, max);
  }
  
  @SafeVarargs
  public static <T extends Comparable<? super T>> T minRecursive(Node<T> first, T...t) {
    // the second argument should not be supplied on the initial invocation
    
    if (first == null && t == null) throw new IllegalArgumentException(
        "minRecursive: both arguments cannot be null at once");
        
    T min = null;
    if (t != null && t.length > 0) min = t[0];
    
    // base case 1
    // returns null on initial invocation with first = null
    if (first == null) return min;
    
    //base case 2
    if (first.next == null) {
      if (min != null) {
        return first.item.compareTo(min) < 0 ? first.item : min;
      } else return first.item;
    }
    
    // set min for recursion
    if (min == null) {
      min = first.item;
    } else if (first.item.compareTo(min) < 0) {
      min = first.item;
    }
    
    // do it again down the list
    return minRecursive(first.next, min);
  }
  
  public static <T> int remove(LinkedList<T> list, T t) {
    // remove all elements equalling t in list and return the number removed
    if (list == null || t == null) throw new IllegalArgumentException(
        "remove: all arguments must be non null");
    if (list.isEmpty()) return 0;
    int c = 0;
    Node<T> first = list.first;
    if (first.item.equals(t)) {
      list.removeFirst();
      c+= 1 + remove(list, t);
      if (list.isEmpty()) return c;
    }
    // at this point either the list is empty and processing has completed
    // or its first element doesn't match t
    
    Node<T> last = list.last;
    if (last.item.equals(t)) {
      list.removeLast();
      c+= 1 + remove(list, t);
    }
    // at this point either the list is empty and procesing has completed
    // or neither its first or last elements match t
    
    Node<T> node = list.first;
    Node<T> previous = node;
    while(node.next != null) {
      node = node.next;
      if (node.item.equals(t)) {
        list.removeAfter(previous);
        c++;
        node = previous;
      }
      previous = node;
    }
    return c;
  }
  
  public static <T> Node<T> reverse(Node<T> x) {
    // iteratively reverse the list beginning with x
    Node<T> first = x;
    Node<T> reverse = null;
    while (first != null) {
      Node<T> second = first.next;
      first.next = reverse;
      reverse = first;
      first = second;
    }
    return reverse;
  }

  public static <T> Node<T> recursiveReverse(Node<T> first) {
    if (first == null) return null;
    if (first.next == null) return first;
    Node<T> second = first.next;
    Node<T> rest = recursiveReverse(second);
    second.next = first;
    first.next = null;
    return rest;
  }
  
  public static <T> LinkedList<T> reverseArray(LinkedList<T> list) {
    return list == null ? null : 
      new LinkedList<T>(ArrayUtils.reverse(list.toArray(list.first.item)));
  }
  
  public static <T> int size(Node<T> n) {
    // return the size of the list n
    if (n == null) return -1;
    Node<T> node = n; int i = 1;
    while(node.next != null) { node = node.next; i++; }
    return i;
  }
  
  public static <T> String nodeListToString(Node<T> n) {
    // return a string representation of the list beginning with n
    StringBuffer sb = new StringBuffer();
    Node<T> tmp = n;
    while (tmp != null){
      sb.append(tmp.item+" ");
      tmp = tmp.getNext();
    }
    return sb.toString();
  }
  
  public static <T> String nodeListIdentityHashCodesToString(Node<T> n) {
    // return a string representation of the list beginning with n 
    // substituting the nodes with identity hashcodes of their items.
    // this is for debugging.
    StringBuffer sb = new StringBuffer();
    Node<T> tmp = n;
    while (tmp != null){
      sb.append(identityHashCode(tmp.item) + " ");
      tmp = tmp.getNext();
    }
    return sb.toString();
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void shuffle(LinkedList<T> ll) {
    // shuffle a LinkedList with uniform randomization
    if (ll == null || ll.size() < 2) return;
    Random q = null;
    try {
      q = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("can't get a SecureRandom strong instance, using pseudorandom");
    }
    if (q == null) q = new Random(System.currentTimeMillis());
    final Random r = q;
    
    Node<T> extra = new Node<T>(); // used in merge()
    
    Function4<Object[],Object[],Integer,Integer,Object[]> merge = (lobj, robj, ls, rs) -> {
      Node<T> left = (Node<T>)(lobj[0]); Node<T> right = (Node<T>)(robj[0]);
      Node<T> head = extra, cur = head, last = null;
      int i = 0, j = 0, llen, rlen; double d;

      while (i < ls || j < rs) {
        llen = ls - i; rlen = rs - j;
        d = r.nextDouble();
        // use probabilities based on current ls and rs to select next node for uniformity
        if (d < 1.*llen/(llen+rlen)) { cur.next = left; left = left.next; i++; }
        else { cur.next = right; right = right.next; j++; }
        cur = cur.next;  last = cur;  
      }

      return new Object[]{head.next, last};
    };
      
    BiFunction<Node<T>,Integer,Object[]> shuffleMerge = 
        Recursive.biFunction((node, size, self) -> {
      if (node == null) 
        throw new IllegalArgumentException("shuffleMerge: node can't be null");
      if (node.next == null) return new Object[]{node,node};

      Function<Node<T>,Object[]> getMiddle = (firstNode) -> {
        if (firstNode.next == null) return new Object[]{firstNode,1};
        Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
        while (fast.next != null && fast.next.next != null) {
          pslow = slow;
          slow = slow.next; c++;
          fast = fast.next.next;
        }
        return new Object[]{slow,c};
      };

      Node<T> left = node;    
      Object[] m = getMiddle.apply(left);
      Node<T> middle = (Node<T>)m[0];
      int ls = (int)m[1]; // left size
      int rs = size - ls; // right size
      Node<T> right = middle.next;
      middle.next = null;
      Object[] o = merge.apply(self.apply(left,ls), self.apply(right,rs), ls, rs);
      return o;
    });
      
    Object[] o = shuffleMerge.apply(ll.getFirst(), ll.size());
    Node<T> last = (Node<T>)(o[1]);
    if (last.next != null) last = last.next;
    ll.setFirst((Node<T>)(o[0]));  ll.setLast(last);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void shuffleEx2218(LinkedList<T> ll) {
    // shuffle a LinkedList with uniform randomization
    // for Ex2218 p286 with requirement of NlgN runtime and lgN extra space
    // same as shuffle() except creates a new Node in merge()
    if (ll == null || ll.size() < 2) return;
    Random q = null;
    try {
      q = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("can't get a SecureRandom strong instance, using pseudorandom");
    }
    if (q == null) q = new Random(System.currentTimeMillis());
    final Random r = q;
    
    Function4<Object[],Object[],Integer,Integer,Object[]> merge = (lobj, robj, ls, rs) -> {
      Node<T> left = (Node<T>)(lobj[0]); Node<T> right = (Node<T>)(robj[0]);
      Node<T> head = new Node<T>(), cur = head, last = null;
      int i = 0, j = 0, llen, rlen; double d;

      while (i < ls || j < rs) {
        llen = ls - i; rlen = rs - j;
        d = r.nextDouble();
        // use probabilities based on current ls and rs to select next node for uniformity
        if (d < 1.*llen/(llen+rlen)) { cur.next = left; left = left.next; i++; }
        else { cur.next = right; right = right.next; j++; }
        cur = cur.next;  last = cur;  
      }

      return new Object[]{head.next, last};
    };
      
    BiFunction<Node<T>,Integer,Object[]> shuffleMerge = 
        Recursive.biFunction((node, size, self) -> {
      if (node == null) 
        throw new IllegalArgumentException("shuffleMerge: node can't be null");
      if (node.next == null) return new Object[]{node,node};

      Function<Node<T>,Object[]> getMiddle = (firstNode) -> {
        if (firstNode.next == null) return new Object[]{firstNode,1};
        Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
        while (fast.next != null && fast.next.next != null) {
          pslow = slow;
          slow = slow.next; c++;
          fast = fast.next.next;
        }
        return new Object[]{slow,c};
      };

      Node<T> left = node;    
      Object[] m = getMiddle.apply(left);
      Node<T> middle = (Node<T>)m[0];
      int ls = (int)m[1]; // left size
      int rs = size - ls; // right size
      Node<T> right = middle.next;
      middle.next = null;
      Object[] o = merge.apply(self.apply(left,ls), self.apply(right,rs), ls, rs);
      return o;
    });
      
    Object[] o = shuffleMerge.apply(ll.getFirst(), ll.size());
    Node<T> last = (Node<T>)(o[1]);
    if (last.next != null) last = last.next;
    ll.setFirst((Node<T>)(o[0])); ll.setLast(last);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> void shuffleWith1Merge(LinkedList<T> ll) {
    // shuffle a LinkedList with uniform randomization doing only 1 merge
    // demonstrates that 1 merge is not enough for decent randomization
    if (ll == null || ll.size() < 2) return;
    Random q = null;
    try {
      q = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.err.println("can't get a SecureRandom strong instance using pseudorandom");
    }
    if (q == null) q = new Random(System.currentTimeMillis());
    final Random r = q;
    
    Function4<Node<T>,Node<T>,Integer,Integer,Object[]> merge = (left, right, ls, rs) -> {
      Node<T> head = new Node<T>(), cur = head, last = null;
      int i = 0, j = 0, llen, rlen; double d;
      while (i < ls || j < rs) {
        llen = ls - i; rlen = rs - j;
        d = r.nextDouble();
        // use probabilities based on current ls and rs to select next node for uniformity
        if (d < 1.*llen/(llen+rlen)) { cur.next = left; left = left.next; i++; }
        else { cur.next = right; right = right.next; j++; }
        cur = cur.next;  last = cur;  
      }
      return new Object[]{head.next, last};
    };
    
    Function<Node<T>,Object[]> getMiddle = (firstNode) -> {
      if (firstNode.next == null) return new Object[]{firstNode,1};
      Node<T> fast, slow, pslow; fast = slow = firstNode; int c = 1;
      while (fast.next != null && fast.next.next != null) {
        pslow = slow;
        slow = slow.next; c++;
        fast = fast.next.next;
      }
      return new Object[]{slow,c};
    };
    
    Node<T> left = ll.getFirst(); 
    Object[] m = getMiddle.apply(left);
    Node<T> middle = (Node<T>)m[0];
    int ls = (int)m[1]; // left size
    int rs = ll.size() - ls; // right size
    Node<T> right = middle.next;
    middle.next = null;
    Object[] o = merge.apply(left, right, ls, rs);
    Node<T> last = (Node<T>)(o[1]);
    if (last.next != null) last = last.next;
    ll.setFirst((Node<T>)(o[0]));  ll.setLast(last);
  }
  
  public static <T> void shuffleFisherYates(LinkedList<T> ll) {
    // implementation of Fisher-Yates shuffling for LinkedList
    if (ll == null || ll.size() < 1) return;
    T[] a = ll.toArray(ll.first.item);
    int size = ll.size(); Random r = null; T t; int j;
    try { r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    for (int i=size; i>1; i--) { j=r.nextInt(i); t=a[i-1]; a[i-1]=a[j]; a[j]=t; }
    ListIterator<T> it = ll.iterator(); j=0;
    while(it.hasNext()) { it.next(); it.set(a[j++]); }
  }
  
  public static void testShuffle(String alg, int n, int trials) {
    // runs shuffle on a LinkedList with Integer node values from [0,n) trials times,
    // collects and prints bias data and elmentary statistical measures of it.
    
    int[][] bias = new int[n][n];       // array of bias data
    Integer[] in = new Integer[n];      // per trial array of LinkedList node items
    int[] all = new int[n*n];           // all bias data for overall stats
    double[][] stat = new double[n][4]; // array of arrays of stat data for each position
    double[] allstat = new double[4];   // array of overall stats
    int c = 0; // counter for iterating from 0 to trials and writing data into all
    double nobias = 1.*trials/n;        // the perfect count for each node item
    double maxbias;                     // worst case bias to be calculated
    long time = 0;                      // elapsed time for all shuffles
    Timer timer = new Timer();          // the timer
    LinkedList<Integer> lla;            // the test LinkedList
    LinkedList<Integer> llb;            // a copy of lla

    while (true) {
      c++;
      lla  = new LinkedList<Integer>(rangeInteger(0,n));
      llb = copy(lla);
      timer.reset();
      switch (alg) {
        case "shuffle"            : shuffle(lla);             break;
        case "shuffleEx2218"      : shuffleEx2218(lla);       break;
        case "shuffleFisherYates" : shuffleFisherYates(lla);  break;
        case "shuffleWith1Merge"  : shuffleWith1Merge(lla);   break;
        default: throw new IllegalArgumentException("testShuffle: alg not recognized");
      }
      time += timer.num();
      in = lla.toArray(1);
      assert lla.size() == n;
      assert lla.getLast().item == in[in.length-1];
      for (int i = 0; i < in.length; i++) bias[in[i]][i]++;
      if (c == trials) break;
    }
    
    System.out.println("\nShuffling a "+n+" element list "+trials+" times using "+alg+"()");
    System.out.println("\nOriginal LinkedList = "+llb.toSimpleString());
    System.out.println("\nelapsed time = "+time+" ms");       
    System.out.println("\nbias matrix");
    for (int i = 0; i < bias.length; i++) pa(bias[i],-1);
    
    for (int i = 0; i < bias.length; i++){
      stat[i][0] = ArrayUtils.min(bias[i]); stat[i][1] = ArrayUtils.max(bias[i]); 
      stat[i][2] = mean(bias[i]); stat[i][3] = stddev(bias[i]); 
    }
    
    System.out.println("\nstats per row of bias matrix");
    System.out.println("(min max mean stddev)");
    for (int i = 0; i < stat.length; i++) pa(stat[i],-1);
    
    c = 0;
    for (int i = 0; i < bias.length; i++)
      for (int j = 0; j < bias[i].length; j++) 
        all[c++] = bias[i][j];
    
    allstat[0] = ArrayUtils.min(all); allstat[1] = ArrayUtils.max(all);
    allstat[2] = mean(all); allstat[3] = stddev(all);
    
    System.out.println("\noverall stats");
    System.out.println("(min max mean stddev)");
    pa(allstat,-1);
    
    maxbias = abs(nobias-allstat[0]) > abs(nobias-allstat[1]) 
        ? 1.*allstat[0] - nobias : 1.*allstat[1] - nobias;
        
    System.out.println("\nmax bias = "+maxbias);
    System.out.println("max bias/trials % = "+maxbias*100/trials+"\n"); 
  }
  
  public static class Node<T> {
    public T item;
    public Node<T> next;  
    public Node(){};
    public Node(T item) { this.item = item; this.next = null; }
    public Node(T item, Node<T> next) { this.item = item; this.next = next; }
    public T item() { return item; }
    public T getItem() { return item; }
    public void setItem(T item) { this.item = item; }
    public Node<T> next() { return next; }
    public Node<T> getNext() { return next; }
    public void setNext(Node<T> next) { this.next = next; }   
    @Override
    public int hashCode() { return Objects.hash(item, next); }
    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      @SuppressWarnings("rawtypes")
      Node other = (Node) obj;
      if (item == null) if (other.item != null) return false;
      else if (!item.equals(other.item)) return false;
      if (next == null) if (other.next != null) return false;
      else if (!next.equals(other.next)) return false;
      return true;
    }    
    @Override
    public String toString() { return "Node("+item+")"; }
  }
    
//  @SuppressWarnings("unused")
  public static void main(String[] args) {
    LinkedList<Integer> lk = new LinkedList<Integer>(rangeInteger(0,25));
    Integer[] b = rangeInteger(24,-1);
    ListIterator<Integer> it = lk.iterator();
    Node<Integer> node = lk.first; int c = 0;
    while(it.hasNext()) {
      it.next();
      it.set(b[c++]);
    }
    System.out.println(lk.toSimpleString());
    
//    shuffle(lk);
//    System.out.println(lk.toSimpleString());
    //shuffleEx2218
//    testShuffle(10,1000);   
//    System.exit(0);
    
//    LinkedList<Integer> ll = new LinkedList<Integer>();
//    ll.add(1); ll.add(2); ll.add(3);
//    System.out.println(ll);
//    ll.removeLast();
//    System.out.println(ll);
//    System.out.println("size="+ll.size());
//    System.out.println("getFirst="+ll.getFirst());
//    System.out.println("getLast="+ll.getLast());
//    
//    Node<Integer> n1 = new Node<Integer>(1);
//    Node<Integer> n2 = new Node<Integer>(2);
//    Node<Integer> n3 = new Node<Integer>(3);
//    Node<Integer> n4 = new Node<Integer>(4);
//    Node<Integer> n5 = new Node<Integer>(5);
//    n1.next = n2; n2.next = n3; n3.next = n4; n4.next = n5; n5.next = null;
//    
//    // testing insertBefore
//    LinkedList<Integer> ll2 = new LinkedList<Integer>(n1);
//    System.out.println(ll2); //LinkedList(1,2,3,4,5)
//    Node<Integer> n6 = new Node<Integer>(6);
//    ll2.insertBefore(n4, n6);
//    System.out.println(ll2); //LinkedList(1,2,3,6,4,5)
//    Node<Integer> n7 = new Node<Integer>(7);
//    ll2.insertBefore(n3, n7);
//    System.out.println(ll2); //LinkedList(1,2,7,3,6,4,5)
//    Node<Integer> n8 = new Node<Integer>(8);
//    ll2.insertBefore(n2, n8);
//    System.out.println(ll2); //LinkedList(1,8,2,7,3,6,4,5)
//    Node<Integer> n9 = new Node<Integer>(9);
//    ll2.insertBefore(n1, n9);
//    System.out.println(ll2); //LinkedList(9,1,8,2,7,3,6,4,5)
//    
//    // testing removeBefore
//    Node<Integer> n = ll2.removeBefore(n4);
//    System.out.println(n); //Node(6)
//    System.out.println(ll2); //LinkedList(9,1,8,2,7,3,4,5)
//    n = ll2.removeBefore(n3);
//    System.out.println(n); //Node(7)
//    System.out.println(ll2); //LinkedList(9,1,8,2,3,4,5)
//    n = ll2.removeBefore(n2);
//    System.out.println(n); //Node(8)
//    System.out.println(ll2); //LinkedList(9,1,2,7,3,4,5)
//    n = ll2.removeBefore(n1);
//    System.out.println(n); //Node(9)
//    System.out.println(ll2); //LinkedList(1,2,3,4,5)
//    
//    // testing maxRecursive and max
//    Integer max = maxRecursive(n1);
//    System.out.println(max); //5
//    max = max(n1);
//    System.out.println(max); //5
//    Integer[] ia = {5,4,3,2,1};
//    LinkedList<Integer> ll3 = new LinkedList<Integer>(ia);
//    System.out.println(ll3); //LinkedList(5,4,3,2,1)
//    max = maxRecursive(ll3.first);
//    System.out.println(max); //5
//    
//    // testing minRecursive and min
//    Integer min = min(n1);
//    System.out.println(min); //1
//    min = minRecursive(n1);  
//    System.out.println(min); //1
//    min = minRecursive(ll3.first); 
//    System.out.println(min); //1
//    min = min(ll3.first);
//    System.out.println(min); //1
//    
//    // testing previous
//    Node<Integer> previous = ll2.last;
//    System.out.printf(previous+" ");
//    while(previous!= null) {
//      previous = ll2.previous(previous);
//      System.out.print(previous+" ");
//    }
//    System.out.println();
//    //Node(5) Node(4) Node(3) Node(2) Node(1) null 
//    
//    //testing get
//    Node<Integer> ll2n2 = ll2.get(2);
//    System.out.println(ll2n2); //Node(3)
//    
//    //testing remove(int)
//    LinkedList<Integer> ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    System.out.println(ll4); //LinkedList(0,1,2,3,4)
//    Node<Integer> ll4n0 = ll4.remove(0);
//    System.out.println(ll4n0); //Node(0)
//    System.out.println(ll4); //LinkedList(1,2,3,4)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4n4 = ll4.remove(4);
//    System.out.println(ll4n4); //Node(4)
//    System.out.println(ll4); //LinkedList(0,1,2,3)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4n2 = ll4.remove(2);
//    System.out.println(ll4n2); //Node(2)
//    System.out.println(ll4); //LinkedList(0,1,3,4
//    
//    // testing getIndex(Node)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4first = ll4.first;
//    System.out.println(ll4.getIndex(ll4first)); //0
//    Integer[] ia4 =  {0,1,2,3,4};
//    for (Integer w : ia4) System.out.print(ll4.getIndex(ll4.get(w))+" ");  
//    System.out.println(); //0 1 2 3 4 
//       
//    //testing removeBefore(Node)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4n1 = ll4.removeBefore(ll4.get(2));
//    System.out.println(ll4n1); //Node(2)
//    System.out.println(ll4); //LinkedList(0,2,3,4)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4n3 = ll4.removeBefore(ll4.get(4));
//    System.out.println(ll4n3); //Node(3)
//    System.out.println(ll4); //LinkedList(0,1,2,4)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    Node<Integer> ll4nx = ll4.removeBefore(ll4.get(0));
//    System.out.println(ll4nx); //null
//    
//    //testing removeAfter(Node)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    ll4n1 = ll4.removeAfter(ll4.get(0));
//    System.out.println(ll4n1); //Node(1)
//    System.out.println(ll4); //LinkedList(0,2,3,4)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    ll4n3 = ll4.removeAfter(ll4.get(2));
//    System.out.println(ll4n2); //Node(3)
//    System.out.println(ll4); //LinkedList(0,1,2,4)
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    ll4nx = ll4.removeAfter(ll4.get(4));
//    System.out.println(ll4nx); //null
//    
//    // testing copy
//    ll4 = new LinkedList<Integer>(new Integer[]{0,1,2,3,4});
//    @SuppressWarnings("unchecked")
//    LinkedList<Integer> ll4copy = (LinkedList<Integer>) ll4.clone();
//    System.out.println(ll4); //LinkedList(0,1,2,3,4)
//    System.out.println(ll4copy); //LinkedList(0,1,2,3,4)
//    System.out.println(Arrays.equals(ll4.toArray(), ll4copy.toArray())); //true
//    
//    // testing merge
//    Node<Integer> node1 = new Node<Integer>(50001);
//    Node<Integer> node2 = new Node<Integer>(1);
//    Node<Integer> node3 = new Node<Integer>(-2);
//    Node<Integer> node4 = new Node<Integer>(8);
//    Node<Integer> node5 = new Node<Integer>(9);
//    Node<Integer> node6 = new Node<Integer>(50001);
//    Node<Integer> node7 = new Node<Integer>(1);
//
//    node3.setNext(node2);
//    node2.setNext(node1);
//    node1.next = null;
//    
//    node7.setNext(node4);
//    node4.setNext(node5);
//    node5.setNext(node6);
//    node6.next = null;
//    
//    System.out.println("x="+nodeListToString(node3));
//    System.out.println("x="+nodeListIdentityHashCodesToString(node3));
//    System.out.println("y="+nodeListToString(node7));
//    System.out.println("x="+nodeListIdentityHashCodesToString(node7));
//    Comparator<Integer> c = (x,y) -> x.compareTo(y);
////    Node<Integer> x = merge(node3, node7, c);
////    System.out.println("x="+nodeListToString(x));
////    System.out.println("x="+nodeListIdentityHashCodesToString(x));
//    
    // testing getRuns
//    Node<Integer> node1 = new Node<Integer>(50001);
//    Node<Integer> node2 = new Node<Integer>(1);
//    Node<Integer> node3 = new Node<Integer>(-2);
//    Node<Integer> node4 = new Node<Integer>(8);
//    Node<Integer> node5 = new Node<Integer>(9);
//    Node<Integer> node6 = new Node<Integer>(50001);
//    Node<Integer> node7 = new Node<Integer>(1);
//    Node<Integer> node8 = new Node<Integer>(5);
//    Node<Integer> node9 = new Node<Integer>(50001);
//    
//    node2.next = node1; node1.next = node4;
//    node4.next = node3; node3.next = node7;
//    node7.next = node5; node5.next = node6; 
//    node6.next = node9; node9.next = null;
//    node8.next = null;
//    
//    Node<Integer> node1 = new Node<Integer>(1);
//    Node<Integer> node2 = new Node<Integer>(2);
//    Node<Integer> node3 = new Node<Integer>(3);
//    Node<Integer> node4 = new Node<Integer>(4);
//    Node<Integer> node5 = new Node<Integer>(5);
//    Node<Integer> node6 = new Node<Integer>(6);
//    Node<Integer> node7 = new Node<Integer>(7);
//    Node<Integer> node8 = new Node<Integer>(8);
//    Node<Integer> node9 = new Node<Integer>(9);
//    Node<Integer> node10 = new Node<Integer>(10);
//    Node<Integer> node11 = new Node<Integer>(11);
//    Node<Integer> node12 = new Node<Integer>(12);
//    Node<Integer> node13 = new Node<Integer>(13);
//    Node<Integer> node14 = new Node<Integer>(14);
//    Node<Integer> node15 = new Node<Integer>(15);
//    Node<Integer> node16 = new Node<Integer>(16);
//    Node<Integer> node17 = new Node<Integer>(17);
//    Node<Integer> node18 = new Node<Integer>(18);
//    Node<Integer> node19 = new Node<Integer>(19);
//    Node<Integer> node20 = new Node<Integer>(20);
//    Node<Integer> tmp;
//  
//
////    System.out.println("x="+nodeListToString(node2));
////    System.out.println(getRuns(node2, 7, c));
//    
//    node1.next = node2; node2.next = node3; node3.next = node4; node4.next = node5;
//    node5.next = node6; node6.next = node7; node7.next = node8; node8.next = node9;
//    node9.next = node10; node10.next = node11; node11.next = node12;  
//    node12.next = node13; node13.next = node14; node14.next = node15; 
//    node15.next = node16; node16.next = node17; node17.next = node18;
//    node18.next = node19; node19.next = node20; node20.next = null;
//    
//    
////    Node<Integer> t, p, a, b, end, end2, end3, endnext, end2next,m ;
////    Node<Integer> atmp, atmpnext, btmp, btmpnext, tmptnext, etmp, etmpnext;
////    Node<Integer> node1tmp ;
////    int run1 = 2; int run2 = 3; int rlen = 11;
////    t = node1; tmp = t;
////    for (int i = 0; i < rlen-1; i++) tmp=tmp.next;
//////    System.out.println(nodeListToString(node1));
////    a = tmp.next; atmp = tmp.next; tmp.next = null;
//////    System.out.println(nodeListToString(node1));
////    for (int i = 0; i < 1; i++) atmp = atmp.next;
//////    System.out.println(nodeListToString(a));
////    b = atmp.next; btmp = atmp.next; atmp.next = null;
////    for (int i = 0; i < 2; i++) btmp = btmp.next;
////    end = btmp.next; etmp = btmp.next; btmp.next = null;
////    System.out.println(nodeListToString(node1));
////    System.out.println(nodeListToString(a));
////    System.out.println(nodeListToString(b));
////    System.out.println(nodeListToString(end));
////    System.out.println("\n\n");
////    
////    m = node16; node16.next = node15; node15.next = node14; 
////    node14.next = node13; node13.next = node12; node12.next = null;
////    p = node1; node1tmp = node1;
////    while (node1tmp.next != null) node1tmp = node1tmp.next;
////    node1tmp.next = m;
////    while (m.next != null) m = m.next;
////    m.next = end;
////    System.out.println(nodeListToString(node1));
////    
////    
////    
////    
////    t = node1; tmp = t;
////    for (int i = 0; i < 2; i++) tmp = tmp.next;
////    tmpnext = tmp.next;
////    a = tmp.next; end1 = tmp.next; tmp.next = null;
////    for (int i = 0; i < 2; i++) end1 = end1.next;
////    end1next = end1.next;  
////    b = end1.next; end2 = end1.next; end3 = end1.next; end1.next = null;    
////    for (int i = 0; i < 2; i++) end2 = end2.next();
////    end2next = end2.next; end2.next = null;
////    System.out.println(nodeListToString(t));
////    System.out.println(nodeListToString(a));
////    System.out.println(nodeListToString(b));
////    System.out.println(nodeListToString(tmpnext));
////    System.out.println(nodeListToString(end1next));
////    System.out.println(nodeListToString(end2next));
////
////    
////    node4.next = a; a.next = b; b.next = end2;
////    System.out.println("node4.next="+node4.next);
////    
//////    merged = merge2(a, b, c);
//////    end1 = merged; merged.next = end2;
////    
////    LinkedList<Integer> ll6 = new LinkedList<Integer>(node4);
////    System.out.println("ll6.size="+ll6.size());
////    System.out.println("size(node4)="+size(node4));
////    
////    System.out.println("ll6="+ll6);
//////    sort(ll6, c);
//////    System.out.println("ll6="+ll6);
////    
////    // getRuns verification against NaturalMerge getRuns (getRunsNM)
////    Integer[] one = new Integer[]{1};
////   //Random(113517037);
////    for (int i = 2; i < 10002; i++) {
////      Random r = new Random(System.currentTimeMillis()); 
////      Integer[] ib = rangeInteger(0,i); 
////      //    System.out.println("ib.length="+ib.length); //30
////      shuffle(ib,r);
////      //    ib = new Integer[]{8,6,19,18,7,5,11,2,12,21,13,24,9,1,28,17,15,3,23,16,22,20,4,30,29,10,14,27,26,25,24,23};
//////      pa(ib,-1);
////      LinkedList<Integer> ll5 = new  LinkedList<Integer>(ib);
//////      System.out.println(getRuns(ll5.first, ib.length, c));
//////      System.out.println(getRunsNM(ib));
////      assert Arrays.equals(
////          getRuns(ll5.first, ib.length, c).toArray(one),getRunsNM(ib).toArray(one));
////    }
//    
////    Integer[][] runs = (Integer[][])newInstance(Integer.class, 3, 2);
////    System.out.println(dim(runs)); //2
////    System.out.println(runs.length);
////    System.exit(0);
//    
////    Integer[] q = {1};
//    Comparator<Integer> c = (x,y) -> x.compareTo(y);
//    Random r; Integer[] w; Integer[] x;
//    for (int i = 2; i < 10002; i++) {
//      w = rangeInteger(1, i, 1);
////      x = w.clone();
//      r = new Random(System.currentTimeMillis());
//      ArrayUtils.shuffle(w, r);
//      LinkedList<Integer> ll  = new LinkedList<Integer>(w);
////      System.out.println("ll.size="+ll.size());
////    sortSlowAndDifficult(ll,c);
////    sort(ll,c);
//      sortNM(ll);
//      assert isSorted(ll);
//      assert w.length == ll.N;
//      Arrays.sort(w);
//      assert Arrays.equals(ll.toArray(1), w);
//    }
//   
//    w = rangeInteger(1,1000);
//    r = new Random(779537509);
////    r = new Random(System.currentTimeMillis());
//    ArrayUtils.shuffle(w, r);
////    pa(w,-1);
//    LinkedList<Integer> a  = new LinkedList<Integer>(w);
////    LinkedList<Integer> a = new LinkedList<Integer>(19,15,21,20,22,41,13,23,25,30,10,40,27,34,36,29,47,45,44,46,39,3,28,49,17,14,24,42,11,38,16,4,5,31,9,7,12,43,26,32,33,18,2,1,35,8,37,6,48);
////    System.out.println(a); System.out.println("a.length="+a.size());
////    System.out.println("len(a.first)="+len(a.first));
////    Queue<Node<Integer>> q = getRuns2(a.first,a.size(),c);
////    System.out.println("q.size="+q.size());
////    System.out.println("len(q.first)="+len(a.first));
////    for (Node<Integer> n : q) System.out.print(nodeListToString(n)+",");
////    System.out.println();
//    Timer t = new Timer();
////    sortSlowAndDifficult(a,c);
////    sort(a,c);
//    sortNM(a);
//    t.stop(); //merge2 26841 26514, merge 14886 15143
//    assert isSorted(a);
//    assert w.length == a.N;
////    System.out.println(a);
//
////    System.out.println(ll);
////    LinkedList<Integer> ll  = new LinkedList<Integer>(15,5,14,1,2,6,12,9,11,19,18,7,16,3,13,17,4,10,8);
////    LinkedList<Integer> lla  = new LinkedList<Integer>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19);
////    Node<Integer> reversed = reverse(lla.first);
////    System.out.println(nodeListToString(reversed));
// 

    
  }

}
