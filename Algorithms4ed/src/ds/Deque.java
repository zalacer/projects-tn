package ds;

import static java.util.Arrays.copyOf;
import static v.ArrayUtils.foldLeft;
import static v.ArrayUtils.ofDim;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

//1.3.33 Deque. A double-ended queue or deque (pronounced “deck”) is like a stack or
//a queue but supports adding and removing items at both ends. A deque stores a collec-
//tion of items and supports the following API:
//API for a generic double-ended queue:
//public class Deque<Item> implements Iterable<Item>          done
//Deque()  create an empty deque                              done
//boolean isEmpty()  is the deque empty?                      done
//int size()  number of items in the deque                    done
//void pushLeft(Item item)  add an item to the left end
//void pushRight(Item item)  add an item to the right end
//Item popLeft()  remove an item from the left end
//Item popRight()  remove an item from the right end
//Write a class Deque that uses a doubly-linked list to implement this API and a class
//ResizingArrayDeque that uses a resizing array.

//push -> pushLeft     : add to left
//pop -> popLeft       : remove from left
//enqueue -> pushRight : add to right
//dequeue -> popLeft   : remove from left

public class Deque<Item> implements Iterable<Item> {
  private Node first; // leftmost Node in the deque 
  private Node last;  // rightmost Node in the deque 
  private int N; // number of items

  Deque(){};

  Deque(Item[] items) {
    if (items != null) for (Item i : items) pushRight(i);
  }

  Deque(Deque<? extends Item> deque) {
    if (deque != null) for (Item i : deque) pushRight(i);
  }
  
  Deque(Collection<? extends Item> c) {
    if (c != null) for (Item i : c) pushRight(i);
  }
  
  private class Node { // nested class to define nodes
    private Item item;
    private Node previous;
    private Node next;

    @SuppressWarnings("unused")
    public Node(){};

    @SuppressWarnings("unused")
    public Node(Item item) {
      this.item = item;
    }

    public Node(Item item, Node previous, Node next) {
      this.item = item; this.previous = previous; this.next = next;
    }

    public Item getItem() {
      return item;
    }

    public Item item() {
      return item;
    }

    @SuppressWarnings("unused")
    public void setItem(Item item) {
      this.item = item;
    }

    @SuppressWarnings("unused")
    public Node getPrevious() {
      return previous;
    }

    @SuppressWarnings("unused")
    public Node previous() {
      return previous;
    }

    @SuppressWarnings("unused")
    public void setPrevious(Node previous) {
      this.previous = previous;
    }

    @SuppressWarnings("unused")
    public Node getNext() {
      return next;
    }

    @SuppressWarnings("unused")
    public Node next() {
      return next;
    }

    @SuppressWarnings("unused")
    public void setNext(Node next) {
      this.next = next;
    }

    @SuppressWarnings("unused")
    public Class<?> nodeClass() {
      return Node.class;
    }
 
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((item == null) ? 0 : item.hashCode());
      result = prime * result + ((next == null) ? 0 : next.hashCode());
      result = prime * result + ((previous == null) ? 0 : previous.hashCode());
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
      @SuppressWarnings("unchecked")
      Node other = (Node) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (item == null) {
        if (other.item != null)
          return false;
      } else if (!item.equals(other.item))
        return false;
      if (next == null) {
        if (other.next != null)
          return false;
      } else if (!next.equals(other.next))
        return false;
      if (previous == null) {
        if (other.previous != null)
          return false;
      } else if (!previous.equals(other.previous))
        return false;
      return true;
    }
    
    @SuppressWarnings("rawtypes")
    private Deque getOuterType() {
      return Deque.this;
    }

    @Override
    public String toString() {
      return "Node("+item+")";
    }
    
    public Node getFirst(Node node) {
      // return the first node in the list containing node
      if (node == null) return null;
      while(node.previous != null) {
        node = node.previous;
      }
      return node;
    }
    
    public Node getLast(Node node) {
      // return the last node in the list containing node
      if (node == null) return null;
      while(node.next != null) {
        node = node.next;
      }
      return node;
    }
    
    public int getSize(Node node) {
      // return the size of the list containing node
      if (node == null) return 0;
      Node first = getFirst(node);
      Node last = getLast(node);
      if (first == last) return 1;
      int c = 1;
      Node n = first;
      while(n.next != null) {
        n = n.next;
        c++;
      }
      return c;
    }
    
    public String allDecendants(Node node) {
      // return a string representation of node and all of its next nodes
      if (node == null) return "";
      StringBuilder sb = new StringBuilder();
      sb.append(node);
      while(node.next != null) {
        node = node.next;
        sb.append("->"+node);
      }
      sb.append("->null");
      return sb.toString();
    }
    
    public String allAscendants(Node node) {
      // return a string representation of node and all of its previous nodes
      if (node == null) return "";
      Stack<String> stack = new Stack<String>();
      stack.push(node.toString());
      while(node.previous != null) {
        node = node.previous;
        stack.push(node.toString()+"<-");
      }
      stack.push("null<-");
      return (foldLeft(stack.toArray(), (x,y)->x+y, ""));
    }
    
    public String allcendants(Node n) {
      if (n == null) return "";
      Node node = getFirst(n);
      boolean sym = true;
      StringBuilder sb = new StringBuilder();
      sb.append("null<-"+node);
      Node previous = node;
      while(node.next != null) {
        previous = node;
        node = node.next;
        if (node.previous == previous) {
          sb.append("<->"+node);
        } else {
          sym = false; break;
        }
      }
      if (sym) {
        return sb.append("->null").toString();
      } else {
        return allDecendants(getFirst(node))+"\n"+allAscendants(getLast(node))+"\n";
      }
    }
    
    @SuppressWarnings("unused")
    public void insertAtBeginning(Node n, Node node) {
      // insert node at the start of the list containing n
      if (n == null) throw new IllegalArgumentException(
          "insertAtBeginning: Node n cannot be null");
      Node start = getFirst(n);
      start.previous = node;
      node.previous = null;
      node.next = start;
    }
    
    @SuppressWarnings("unused")
    public void insertAtEnd(Node n, Node node) {
      // insert node at the end of the list containing n
      Node end = getLast(n);
      end.next = node;
      node.previous = end;
      node.next = null;
    }
    
    @SuppressWarnings("unused")
    public void insertBefore(Node n, Node node) {
      // insert node just before n
      if (n == null) throw new IllegalArgumentException(
          "insertBefore: Node n cannot be null");
      node.previous = n.previous;
      n.previous = node;
      node.previous.next = node;
      node.next = n;
    }
    
    @SuppressWarnings("unused")
    public void insertAfter(Node n, Node node) {
      // insert node just after n
      if (n == null) throw new IllegalArgumentException(
          "insertAfter: Node n cannot be null");
      node.next = n.next;
      n.next = node;
      node.next.previous = node;
      node.previous = n;
    }
    
    public Item removeFromBeginning(Node n) {
      // remove the starting node from the chain containing n
      if (n == null) return null;
      Node first = getFirst(n);
      if (first.next == null) {
        Item item = first.item();
        first = null;
        return item;
      }
      first.next.previous = null;
      first.next = null;
      first.previous = null; // just in case, but should already be null
      Item item = first.item();
      first = null;
      return item; // may need to use its data
    }
    
    public Item removeFromEnd(Node n) {
      // remove the node at the end of the list containing n
      if (n == null) return null;
      Node last = getLast(n);
      last.previous.next = null;
      last.previous = null;
      last.next = null; // just in case, but should already be null
      return last.item();
    }
    
    @SuppressWarnings("unused")
    public Object[] remove(Node n) {
      // remove n from its list returing its value and the first 
      // node of that list in an Object[]
      if (n == null) return new Object[]{null,null};
      Item item = n.getItem();
      int size = getSize(n);
      if (size == 1) {
        return new Object[]{null, item};
      }
      Node first = getFirst(n);
      Node newFirst = n == first ? first.next : first;
      if (n == first) {
        removeFromBeginning(n);
      } else if (n == getLast(n)) {
        removeFromEnd(n);
      } else {
        // at this point n must be somewhere in the middle of a list with at least 3 elements
        n.next.previous = n.previous;
        n.previous.next = n.next;
      }
      return new Object[]{newFirst, item};
    }
    
    @SuppressWarnings("unused")
    public final Node[] toArray(Node n) {
      if (n == null) throw new IllegalArgumentException(
          "toArray: the argument must be non null");
      Node first = getFirst(n);
      int size = getSize(first);
      Node[] a = ofDim(n.getClass(), size);
      if (size == 0) {
        return a; // should not happen since n would be null
      } else if (size == 1) {
        a[0] = first;
        return a;
      } else {
        a[0] = first;
        Node node = first;
        int i = 1;
        while (node.next != null) {
          node = node.next;
          a[i++] = node;
        }
        return a;
      }
    }
  }
  
  public Node getFirst() {
    return first;
  }
  
  public Node first() {
    return first;
  }

  public void setFirst(Node first) {
    this.first = first;
  }

  public Node getLast() {
    return last;
  }
  
  public Node last() {
    return last;
  }

  public void setLast(Node last) {
    this.last = last;
  }

  public int getN() {
    return N;
  }
  
  public int N() {
    return N;
  }

  public void setN(int n) {
    N = n;
  }

  public boolean isEmpty() { return first == null; } // Or: N == 0.

  public int size() { return N; }
  
//  public class Node { // nested class to define nodes
//    private Item item;
//    private Node previous;
//    private Node next;

  public void pushLeft(Item item) { 
    // add an item to the left end
    if (first == null ) {
      first = new Node(item, null, null);
    } else {
      Node oldfirst = first;
      first = new Node(item, null, oldfirst);
      oldfirst.previous = first;
    }
    N++;
    if (N == 1) last = first;
  }

  public Item popLeft() { 
    // remove the item at the left end if possible
    if (isEmpty()) throw new NoSuchElementException("Deque underflow");
    Item item = first.item;
    first = first.next;
    if (first != null) first.previous = null;
    N--;
    if (N == 1) last = first;
    if (N == 0) {
      first = null; last = null;
    }
    return item;
  }

  public void pushRight(Item item) { 
    // add item at the right end
    Node oldlast = last;
    last = new Node(item, oldlast, null);
    if (isEmpty()) first = last;
    else oldlast.next = last;
    N++;
  }
  
  public Item popRight() { 
    // remove an item from the left end
    if (isEmpty()) throw new NoSuchElementException("Deque underflow");
    Item item = last.item;
    last = last.previous;
    if (last != null) last.next = null;
    N--;
    if (N == 1) first = last;
    if (N == 0) {
      first = null; last = null;
    }
    return item;
  }
  
  public String ascending() {
    if (isEmpty()) return "(empty list)";
    return first.allAscendants(first);
  }
  
  public String descending() {
    if (isEmpty()) return "(empty list)";
    return last.allDecendants(last);
  }
  
  public String cending() {
    if (isEmpty()) return "(empty list)";
    return first.allcendants(first);
  }
  
    //  from stack
//  public Item pop() { // Remove item from top of stack.
//    if (isEmpty()) throw new NoSuchElementException("Stack underflow");
//    Item item = first.item;
//    first = first.next;
//    N--;
//    return item;
//  }

    // from queue
//  public Item dequeue() { // Remove item from the beginning of the list.
//    if (isEmpty()) throw new NoSuchElementException("Deque underflow");
//    Item item = first.item;
//    first = first.next;
//    if (isEmpty()) last = null;
//    N--;
//    return item;
//  }
 
  // iterator code provided on p155 of the text - however not suitable as Stack iterator
  public Iterator<Item> iterator() { return new ListIterator(); }

  // using ListIterator to build array in toArray
  private class ListIterator implements Iterator<Item> {
    private Node current = first;

    public boolean hasNext() { return current != null; }

    public Item next() {
      if (!hasNext()) throw new NoSuchElementException();
      Item item = current.item;
      current = current.next;
      return item;
    }
  }

  public Item[] toArray() {
    @SuppressWarnings("unchecked")
    Item[] a = (Item[]) new Object[N];
    int aindex = 0;
    Iterator<Item> it = iterator();
    while (it.hasNext()) a[aindex++] = it.next();
    return a;
  }
  
  @SafeVarargs
  public final Item[] toArray(Item...items) {
    if (items.length == 0) throw new IllegalArgumentException("toArray: items length "
        + "must be > 0");
    Item[] a = (Item[]) copyOf(items, size());
    Iterator<Item> it = iterator();
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
    result = prime * result + Arrays.hashCode(toArray());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("rawtypes")
    Deque other = (Deque) obj;
    if (N != other.N) return false;
    if (first.getClass() != other.first.getClass()) return false;
    if (first == null) {
      if (other.first != null) return false;
    } else if (!first.equals(other.first)) return false;
    Object[] thisArray = this.toArray();
    @SuppressWarnings("rawtypes")
    Object[] otherArray = ((Deque) obj).toArray();
    if (thisArray == null) {
      if (otherArray != null) return false;
    } else if (!(Arrays.equals(thisArray, otherArray))) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Deque(");
    if (isEmpty()) return sb.append(")").toString();
    for (Item i : this) sb.append(""+i+",");
    return sb.substring(0, sb.length()-1)+")";
  }
  
  

  public static void main(String[] args) {
    
    Deque<Integer> d = new Deque<Integer>(new Integer[]{1,2,3,4,5});
////    System.out.println(d.cending());
//    // null<-Node(1)<->Node(2)<->Node(3)<->Node(4)<->Node(5)->null
//    System.out.println(d); //Deque(1,2,3,4,5)
//    d.pushRight(6);
//    System.out.println(d); //Deque(1,2,3,4,5,6)
//    d.pushLeft(0);
//    System.out.println(d); //Deque(0,1,2,3,4,5,6)
//    System.out.println(d.popRight()); //6
//    System.out.println(d); //Deque(0,1,2,3,4,5)
//    System.out.println(d.popLeft()); //0
//    System.out.println(d); //Deque(1,2,3,4,5)
    
    // stack operation with pushLeft and popLeft
    d = new Deque<Integer>();
    for (int i = 1; i < 6; i++) d.pushLeft(i);
    System.out.println(d); //Deque(5,4,3,2,1)
    while (!d.isEmpty()) System.out.print(d.popLeft()+" "); System.out.println(); //5 4 3 2 1 
    System.out.println(d+"\n"); //Deque()
    
    // stack operation with pushRight and popRight
    d = new Deque<Integer>();
    for (int i = 1; i < 6; i++) d.pushRight(i);
    System.out.println(d); //Deque(5,4,3,2,1)
    while (!d.isEmpty()) System.out.print(d.popRight()+" "); System.out.println(); //5 4 3 2 1 
    System.out.println(d+"\n"); //Deque()
    
  }

}

