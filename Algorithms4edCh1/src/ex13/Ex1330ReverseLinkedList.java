package ex13;

import ds.LinkedList;

//  1.3.30  Write a function that takes the first  Node in a linked list as argument and (de-
//  structively) reverses the list, returning the first  Node in the result.
//
//  Iterative solution : To accomplish this task, we maintain references to three consecutive
//  nodes in the linked list, reverse, first, and second.At each iteration, we extract the node
//  first from the original linked list and insert it at the beginning of the reversed list.
//  We maintain the invariant that first is the first node of what’s left of the original list,
//  second is the second node of what’s left of the original list, and  reverse is the first
//  node of the resulting reversed list.
//
//    public Node reverse(Node x) {
//      Node first = x;
//      Node reverse = null;
//      while (first != null) {
//        Node second = first.next;
//        first.next = reverse;
//        reverse = first;
//        first = second;
//      }
//      return reverse;
//   }
//
//  When writing code involving linked lists, we must always be careful to properly handle
//  the exceptional cases (when the linked list is empty, when the list has only one or two
//  nodes) and the boundary cases (dealing with the first or last items). This is usually
//  much trickier than handling the normal cases.
//
//  Recursive solution : Assuming the linked list has N nodes, we recursively reverse the last
//  N – 1 nodes, and then carefully append the first node to the end.
//
//    public Node reverse(Node first) {
//      if (first == null) return null;
//      if (first.next == null) return first;
//      Node second = first.next;
//      Node rest = reverse(second);
//      second.next = first;
//      first.next = null;
//      return rest;
//   }

//  a functional way to reverse a linked list:
//  public static <T> LinkedList<T> functionalReverse(LinkedList<T> list) {
//     return list == null ? null : new LinkedList<T>(reverse(list.toArray(list.first.item)));
//  }

//  public static <T> T[] reverse(T[] a) {
//    if (a == null) return null;
//    if (a.length == 0) return Arrays.copyOf(a,0);
//    T[] b = ofDim(a.getClass().getComponentType(), a.length); 
//    if (b.length < 2) return b;
//    int n = b.length;
//    for (int i = 0; i < n/2; i++) {
//      b[i] = a[n-1-i];
//      b[n-i-1] = a[i];
//    } 
//    if (n % 2 == 1) b[n/2] = a[n/2];
//    return b;
//  }

//  public static <T> T[] ofDim(Class<?> c, int n) {
//    if (c == null || n < 0) throw new IllegalArgumentException("ofDim: n must be > 0 "
//        + "and c must not be null");
//    return (T[]) Array.newInstance(c, n);
//  }

public class Ex1330ReverseLinkedList {

  public static void main(String[] args) {
    Integer[] ia = {1,2,3,4,5};
    LinkedList<Integer> ll = new LinkedList<Integer>(ia);
    System.out.println(ll); //LinkedList(1,2,3,4,5)
    System.out.println(ll.getFirst()); //Node(1)
    System.out.println(ll.getLast()); //Node(5)
    System.out.println(ll.size()+"\n"); //5
    
    LinkedList<Integer>.Node llfirst = ll.get(1);
    System.out.println(llfirst); //Node(1)
    LinkedList<Integer>.Node rNode = LinkedList.iterativeReverse(llfirst);
    LinkedList<Integer> llr = new LinkedList<Integer>(rNode);
    System.out.println(llr); //LinkedList(5,4,3,2,1)
    System.out.println(llr.getFirst()); //Node(5)
    System.out.println(llr.getLast()); //Node(1)
    System.out.println(llr.size()); //5
    System.out.println(ll+"\n"); //LinkedList(1)
    
    ll = new LinkedList<Integer>(ia);
    llfirst = ll.get(1);
    rNode = LinkedList.recursiveReverse(llfirst);
    System.out.println(llr); //LinkedList(5,4,3,2,1)
    System.out.println(llr.getFirst()); //Node(5)
    System.out.println(llr.getLast()); //Node(1)
    System.out.println(llr.size()); //5
    System.out.println(ll+"\n"); //LinkedList(1)
    
    ll = new LinkedList<Integer>(ia);
    // functionalReverse leaves ll unchanged
    llr = LinkedList.reverse(ll);
    System.out.println(llr); //LinkedList(5,4,3,2,1)
    System.out.println(llr.getFirst()); //Node(5)
    System.out.println(llr.getLast()); //Node(1)
    System.out.println(llr.size()); //5
    System.out.println(ll+"\n"); //LinkedList(1,2,3,4,5)
    
  }

}
