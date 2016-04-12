package ch07.collections;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// 5. Implement a method 
//   public static void swap(List<?> list, int i, int j) 
// that swaps elements in the usual way when the type of list implements
// the RandomAccess interface, and that minimizes the cost of visiting
// the positions at index i and j if it is not.


public class Ch0705ListSwap {
  
//  @SuppressWarnings({"rawtypes", "unchecked"})
//  public static void swap(List<?> list, int i, int j) {
//      // instead of using a raw type here, it's possible to capture
//      // the wildcard but it will require a call to a supplementary
//      // private method
//      final List l = list;
//      l.set(i, l.set(j, l.get(i)));
//  }
  
  // swap captures the List generic type wildcard for swapHelper 
  // see https://docs.oracle.com/javase/tutorial/java/generics/capture.html
  public static void swap(List<?> list, int i , int j) {
    swapHelper(list, i, j); // captures the wildcard
  }
  
  private static <T> void swapHelper(List<T> list, int i, int j) {
    // this is what Collections.swap does, see
    // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/util/Collections.java#Collections.swap%28java.util.List%2Cint%2Cint%29
    final List<T> l = list;
    l.set(i, l.set(j, l.get(i)));
  }
  
  public static void swapWithSingleEndedSequentialAccess(List<?> list, int i , int j) {
    swapHelperWithSingleEndedSequentialAccess(list, i, j);
  }
  
  private static <T> void swapHelperWithSingleEndedSequentialAccess(List<T> list, int i, int j) {
    // this would be for a singly linked list indexed only from the beginning
    final List<T> l = list;
    if (i <= j) {
      l.set(i, l.set(j, l.get(i)));
    } else {
      l.set(j, l.set(i, l.get(j)));
    }
  }

  public static void swapWithDoubleEndedSequentialAccess(List<?> list, int i , int j) {
    swapHelperWithDoubleEndedSequentialAccess(list, i, j);
  }
  
  private static <T> void swapHelperWithDoubleEndedSequentialAccess(List<T> list, int i, int j) {
    // this is for a doubly linked list such as LinkedList with which the
    // "operations that index into the list will traverse the list from the
    //  beginning or the end, whichever is closer to the specified index." 
    // see https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html
    final List<T> l = list;
    int size = list.size();
    int imin = i <= size - i - 1 ? i : size - i - 1;
    int jmin = j <= size - j - 1 ? j : size - j - 1;
    if (imin <= jmin) {
      l.set(i, l.set(j, l.get(i)));
    } else {
      l.set(j, l.set(i, l.get(j)));
    }
  }

  public static void main(String[] args) {
    
    ArrayList<Integer> al;
    LinkedList<Integer> ll;
    
    al = new ArrayList<>();
    assert al instanceof RandomAccess;

    ll = new LinkedList<>();
    assert ! (ll instanceof RandomAccess);
    
    al = new ArrayList<>(Arrays.asList(1,2,3));
    swap(al,0,2);
    System.out.println(al); // [3, 2, 1]
    
    ll = new LinkedList<>(Arrays.asList(1,2,3));
    swapWithDoubleEndedSequentialAccess(ll,0,2);
    System.out.println(ll);
    
    al = new ArrayList<>(Arrays.asList(1,2,3));
    Collections.swap(al,0,2);
    System.out.println(al); // [3, 2, 1]

    ll = new LinkedList<>(Arrays.asList(1,2,3));
    Collections.swap(ll,0,2);
    System.out.println(ll); // [3, 2, 1]
 
    Instant start, stop;
    Duration timeElapsed;
    long millis;
    
    ll = new LinkedList<>(IntStream.range(0,1000000).boxed().collect(Collectors.toList()));
    
    // swapWithDoubleEndedSequentialAccess test 500001,1
    start = Instant.now();
    for (int i = 1; i < 10000; i++)
      swapWithDoubleEndedSequentialAccess(ll,500001,1);
    stop = Instant.now();
    timeElapsed = Duration.between(start, stop);
    millis = timeElapsed.toMillis();
    System.out.println("swapWithDoubleEndedSequentialAccess millis:"+millis); //14815
    
    ll = new LinkedList<>(IntStream.range(0,1000000).boxed().collect(Collectors.toList()));
    
    // swap test 500001,1
    start = Instant.now();
    for (int i = 1; i < 10000; i++)
      swap(ll,500001,1);
    stop = Instant.now();
    timeElapsed = Duration.between(start, stop);
    millis = timeElapsed.toMillis();
    System.out.println("swap millis:"+millis); //29595
    ll = new LinkedList<>(IntStream.range(0,1000000).boxed().collect(Collectors.toList()));
    
    ll = new LinkedList<>(IntStream.range(0,1000000).boxed().collect(Collectors.toList()));
    
    // Collections.swap test 500001,1
    start = Instant.now();
    for (int i = 1; i < 10000; i++)
      Collections.swap(ll,500001,1);
    stop = Instant.now();
    timeElapsed = Duration.between(start, stop);
    millis = timeElapsed.toMillis();
    System.out.println("swap millis:"+millis); //29542

    // These tests show that swapWithDoubleEndedSequentialAccess can run twice as fast for
    // a LinkedList compared to swap and Collections.swap. However, for cases where both 
    // elements to be swapped are about the same distance from an end of such a list, it
    // provides no advantage.

  }

}
