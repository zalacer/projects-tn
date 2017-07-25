package ex13;

import static v.ArrayUtils.*;

import ds.Stack;

//  1.3.31 Implement a nested class DoubleNode for building doubly-linked lists, where
//  each node contains a reference to the item preceding it and the item following it in the
//  list ( null if there is no such item). Then implement static methods for the following
//  tasks: insert at the beginning, insert at the end, remove from the beginning, remove
//  from the end, insert before a given node, insert after a given node, and remove a given
//  node.

public class Ex1331DoubleNode {

  public static class Node<T> { // nested class to define nodes
    private T data;
    private Node<T> previous;
    private Node<T> next;

    public Node(){};

    public Node(T data) {
      this.data = data;
    }

    public Node(T data, Node<T> previous, Node<T> next) {
      this.data = data; this.previous = previous; this.next = next;
    }

    public T getData() {
      return data;
    }

    public T data() {
      return data;
    }

    public void setT(T data) {
      this.data = data;
    }

    public Node<T> getPrevious() {
      return previous;
    }

    public Node<T> previous() {
      return previous;
    }

    public void setPrevious(Node<T> previous) {
      this.previous = previous;
    }

    public Node<T> getNext() {
      return next;
    }

    public Node<T> next() {
      return next;
    }

    public void setNext(Node<T> next) {
      this.next = next;
    }

    public static Class<?> nodeClass() {
      return Node.class;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((next == null) ? 0 : next.hashCode());
      result = prime * result + ((previous == null) ? 0 : previous.hashCode());
      result = prime * result + ((data == null) ? 0 : data.hashCode());
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
      Node other = (Node) obj;
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
      if (data == null) {
        if (other.data != null)
          return false;
      } else if (!data.equals(other.data))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "Node("+data+")";
    }
    
    public static <R> Node<R> getFirst(Node<R> node) {
      // return the first node in the list containing node
      if (node == null) return null;
      while(node.previous != null) {
        node = node.previous;
      }
      return node;
    }
    
    public static <R> Node<R> getLast(Node<R> node) {
      // return the last node in the list containing node
      if (node == null) return null;
      while(node.next != null) {
        node = node.next;
      }
      return node;
    }
    
    public static <R> int getSize(Node<R> node) {
      // return the size of the list containing node
      if (node == null) return 0;
      Node<R> first = getFirst(node);
      Node<R> last = getLast(node);
      if (first == last) return 1;
      int c = 1;
      Node<R> n = first;
      while(n.next != null) {
        n = n.next;
        c++;
      }
      return c;
    }
    
    public static <R> String allDecendants(Node<R> node) {
      // return a string representation of node and all of its next nodes
      if (node == null) return null;
      StringBuilder sb = new StringBuilder();
      sb.append(node);
      while(node.next != null) {
        node = node.next;
        sb.append("->"+node);
      }
      sb.append("->null");
      return sb.toString();
    }
    
    public static <R> String allAscendants(Node<R> node) {
      // return a string representation of node and all of its previous nodes
      if (node == null) return null;
      Stack<String> stack = new Stack<String>();
      stack.push(node.toString());
      while(node.previous != null) {
        node = node.previous;
        stack.push(node.toString()+"<-");
      }
      stack.push("null<-");
      return (foldLeft(stack.toArray(), (x,y)->x+y, ""));
    }
    
    public static <R> String allcendants(Node<R> first) {
      return allDecendants(first)+"\n"+allAscendants(getLast(first))+"\n";
    }
    
    public static <R> void insertAtBeginning(Node<R> n, Node<R> node) {
      // insert node at the start of the list containing n
      if (n == null) throw new IllegalArgumentException(
          "insertAtBeginning: Node n cannot be null");
      Node<R> start = getFirst(n);
      start.previous = node;
      node.previous = null;
      node.next = start;
    }
    
    public static <R> void insertAtEnd(Node<R> n, Node<R> node) {
      // insert node at the end of the list containing n
      Node<R> end = getLast(n);
      end.next = node;
      node.previous = end;
      node.next = null;
    }
    
    public static <R> void insertBefore(Node<R> n, Node<R> node) {
      // insert node just before n
      if (n == null) throw new IllegalArgumentException(
          "insertBefore: Node n cannot be null");
      node.previous = n.previous;
      n.previous = node;
      node.previous.next = node;
      node.next = n;
    }
    
    public static <R> void insertAfter(Node<R> n, Node<R> node) {
      // insert node just after n
      if (n == null) throw new IllegalArgumentException(
          "insertAfter: Node n cannot be null");
      node.next = n.next;
      n.next = node;
      node.next.previous = node;
      node.previous = n;
    }
    
    public static <R> R removeFromBeginning(Node<R> n) {
      // remove the starting node from the chain containing n
      if (n == null) return null;
      Node<R> first = getFirst(n);
      if (first.next == null) {
        R data = first.data();
        first = null;
        return data;
      }
      first.next.previous = null;
      first.next = null;
      first.previous = null; // just in case, but should already be null
      R data = first.data();
      first = null;
      return data; // may need to use its data
    }
    
    public static <R> R removeFromEnd(Node<R> n) {
      // remove the node at the end of the list containing n
      if (n == null) return null;
      Node<R> last = getLast(n);
      last.previous.next = null;
      last.previous = null;
      last.next = null; // just in case, but should already be null
      return last.data();
    }
    
    public static <R> Object[] remove(Node<R> n) {
      // remove n from its list returing its value and the first 
      // node of that list in an Object[]
      if (n == null) return new Object[]{null,null};
      R data = n.getData();
      int size = getSize(n);
      if (size == 1) {
        return new Object[]{null, data};
      }
      Node<R> first = getFirst(n);
      Node<R> newFirst = n == first ? first.next : first;
      if (n == first) {
        removeFromBeginning(n);
      } else if (n == getLast(n)) {
        removeFromEnd(n);
      } else {
        // at this point n must be somewhere in the middle of a list with at least 3 elements
        n.next.previous = n.previous;
        n.previous.next = n.next;
      }
      return new Object[]{newFirst, data};
    }
    
    public final static <R> Node<R>[] toArray(Node<R> n) {
      if (n == null) throw new IllegalArgumentException(
          "toArray: the argument must be non null");
      Node<R> first = getFirst(n);
      int size = getSize(first);
      Node<R>[] a = ofDim(n.getClass(), size);
      if (size == 0) {
        return a; // should not happen since n would be null
      } else if (size == 1) {
        a[0] = first;
        return a;
      } else {
        a[0] = first;
        Node<R> node = first;
        int i = 1;
        while (node.next != null) {
          node = node.next;
          a[i++] = node;
        }
        return a;
      }
    }
  }

  public static void main(String[] args) {
    Integer[] ia = {1,2,3,4,5};
    @SuppressWarnings("unchecked")
    Node<Integer>[] na = (Node<Integer>[]) ofDim(Node.class, ia.length);
    for (int i = 0; i < ia.length; i++) na[i] = new Node<Integer>(ia[i]);
    pa(na); //Node[Node(1),Node(2),Node(3),Node(4),Node(5)]
    na[0].previous = null;
    na[4].next = null;
    for (int i = 0; i < na.length; i++) {
      if (i != 0) na[i].previous = na[i-1];
      if (i != na.length-1) na[i].next = na[i+1];  
    }
    
    // this prints all the decendants of na[0] then all the ascendants of Node.getLast(na[0])
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->null // decendants of na[0]
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5) // ascendants of Node.getLast(na[0])

    
    Node<Integer> node0 = new Node<Integer>(new Integer(0));
    Node.insertAtBeginning(na[0], node0);
    na = prepend(na, node0);
    System.out.println(Node.allcendants(na[0]));
    // Node(0)->Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->null
    // null<-Node(0)<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)
    
    Node<Integer> node6 = new Node<Integer>(new Integer(6));
    Node.insertAtEnd(na[0], node6);
    na = append(na, node6);
    System.out.println(Node.allcendants(na[0]));
    // Node(0)->Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->Node(6)->null
    // null<-Node(0)<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)<-Node(6)
    
    System.out.println(Node.allcendants(na[0]));
    // Node(0)->Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->Node(6)->null
    // null<-Node(0)<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)<-Node(6)
    Integer data = Node.removeFromBeginning(na[0]);
    System.out.println(data); //0);
    System.out.println(Node.allcendants(na[0]));
    // Node(0)->null
    // null<-Node(0)
    na = drop(na,1); // removes the 1st element from na
    System.out.println(Node.allcendants(na[0]));
    
    Node<Integer> last = Node.getLast(na[0]);
    System.out.println(Node.allcendants(last));
    // Node(6)->null
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)<-Node(6)
    data = Node.removeFromEnd(na[0]);
    System.out.println(data); //6
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->null
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)
    
    na = dropRight(na,1); // remove the last element in na for cleanup
    pa(na);
    Node<Integer> node9 = new Node<Integer>(new Integer(9));
    System.out.println(na[2]); //Node(3)
    Node.insertBefore(na[2], node9);
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(9)->Node(3)->Node(4)->Node(5)->null
    // null<-Node(1)<-Node(2)<-Node(9)<-Node(3)<-Node(4)<-Node(5)
    
    na = append(append(take(na,2),node9),takeRight(na,3)); // update na
    pa(na); // Node[Node(1),Node(2),Node(9),Node(3),Node(4),Node(5)]
    
    Node<Integer> node7 = new Node<Integer>(new Integer(7));
    System.out.println(na[3]); //Node(3)
    Node.insertAfter(na[3], node7);
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(9)->Node(3)->Node(7)->Node(4)->Node(5)->null
    // null<-Node(1)<-Node(2)<-Node(9)<-Node(3)<-Node(7)<-Node(4)<-Node(5)
    
    na = append(append(take(na,4),node7),takeRight(na,2)); // update na
    pa(na); // Node[Node(1),Node(2),Node(9),Node(3),Node(7),Node(4),Node(5)]
    
    Node.remove(node9);
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(3)->Node(7)->Node(4)->Node(5)->null
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(7)<-Node(4)<-Node(5)
    
    Node.remove(node7);
    System.out.println(Node.allcendants(na[0]));
    // Node(1)->Node(2)->Node(3)->Node(4)->Node(5)->null
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(4)<-Node(5)
    
    na = Node.toArray(na[0]);
    Object[] o  = null;
    pa(na); //Node[Node(1),Node(2),Node(3),Node(4),Node(5)]
    for (int i = na.length-1; i > 0; i--) {
      // remove returns an Object[] of length 2 that contains the head of the list
      // that contained the removed object and the data of the removed object
      o = Node.remove(na[i]); 
      pa(o);
      System.out.println(Node.allcendants(na[0]));
    }
    // Object[Node(1),5]
    // Node(1)->Node(2)->Node(3)->Node(4)->null
    // null<-Node(1)<-Node(2)<-Node(3)<-Node(4)
    
    // Object[Node(1),4]
    // Node(1)->Node(2)->Node(3)->null
    // null<-Node(1)<-Node(2)<-Node(3)

    // Object[Node(1),3]
    // Node(1)->Node(2)->null
    // null<-Node(1)<-Node(2)

    // Object[Node(1),2]
    // Node(1)->null
    // null<-Node(1)
    
    na = Node.toArray(na[0]);
    pa(na); // Node[Node(1)]
    
    o = Node.remove(na[0]);
    // can't remove the last remaining node within a method while it has an external
    // reference, however remove sets the head of the list resturned in Object[0] to null
    pa(o); // Object[null, 1]
    
  }

}
