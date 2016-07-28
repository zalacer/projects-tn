package ex13;

import ds.LinkedList;

//  1.3.28  Develop a recursive solution to the previous question.

//  @SafeVarargs
//  public static <T extends Comparable<T>> T maxRecursive(LinkedList<T>.Node first,
//      T...t) {
//
//    // the second argument should not be supplied on the initial invocation
//
//    if (first == null && t == null) throw new IllegalArgumentException(
//        "maxRecursive: both arguments cannot be null at once");
//        
//    T max = null;
//    if (t != null && t.length > 0) max = t[0];
//    
//    // base case 1
//    // returns null on initial invocation with first = null
//    if (first == null) return max;
//    
//    //base case 2
//    if (first.next == null) {
//      if (max != null) {
//        return first.item.compareTo(max) > 0 ? first.item : max;
//      } else return first.item;
//    }
//    
//    // set max for recursion
//    if (max == null) {
//      max = first.item;
//    } else if (first.item.compareTo(max) > 0) max = first.item;
//    
//    // do it again down the list
//    return maxRecursive(first.next, max);
//  }

public class Ex1328LinkedListMaxRecursive {

  public static void main(String[] args) {
    
    LinkedList<Integer> ll = new LinkedList<Integer>(new Integer[]{1,2,5,4,3});
    System.out.println(LinkedList.maxRecursive(ll.first())); //5
    System.out.println(LinkedList.maxRecursive(null)); //null
    //System.out.println(LinkedList.maxRecursive(null, null));
    //IllegalArgumentException: maxRecursive: both arguments cannot be null at once
 
  }

}
