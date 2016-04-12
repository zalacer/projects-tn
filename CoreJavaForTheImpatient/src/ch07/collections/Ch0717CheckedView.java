package ch07.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// 17. Demonstrate how a checked view can give an accurate 
// error report for a cause of heap pollution.

public class Ch0717CheckedView {

  public static <T> void removeElements(List<T> a) {
    Iterator<T> it = a.iterator();
    while (it.hasNext()) {
      a.remove(it.next());
    }
  }

  public static void main(String[] args) {

    // an unchecked list gives no error/exception when cast to Object which is
    // then cast to a list of another type, but after the cast accessing an element 
    // put in the unchecked list before the cast either fails at runtime with a 
    // ClassCastException when it has the second list type or gives a type mismatch
    // error at compile time when typed as the first list type.  In other words,
    // elements existing in the list before the cast are not accessible and attempts
    // to access them can cause failures after the cast.
    // In this situation the heap is polluted with a list of invalid type with no clue
    // provided regarding the cause.

    @SuppressWarnings({ "unchecked" })
    ArrayList<Integer> uncheckedList = (ArrayList<Integer>) ((Object) new ArrayList<String>(
        Arrays.asList("string1")));
    System.out.println(uncheckedList); // [string1]
    uncheckedList.add(new Integer(1)); // 
    System.out.println(uncheckedList); // [string1, 1]
    // uncheckedList.add("string"); // invalid method for arg String
    // Integer u0 = uncheckedList.get(0);
    // System.out.println(u0); // causes java.lang.ClassCastException
    // String u0 = uncheckedList.get(0); // Type mismatch: cannot convert from Integer to String
    Integer u1 = uncheckedList.get(1);
    System.out.println(u1); // 1

    // a checked list gives an exception immediately when cast to a list of another type
    // this means there won't be any unexpected failures later when attempting to access
    // elements already in the list before the cast since the latter cannot be done
    // in other words a checked list prevents heap pollution
    List<Integer> checkedList = Collections.checkedList(new ArrayList<>(), Integer.class);
    Object p = checkedList;
    @SuppressWarnings({ "unused", "unchecked" })
    ArrayList<String> t = (ArrayList<String>) p; // causes java.lang.ClassCastException

  }

}
