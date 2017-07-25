package ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

// from 
// https://stackoverflow.com/questions/1098117/can-one-do-a-for-each-loop-in-java-in-reverse-order

public class Reversed<T> implements Iterable<T> {
  private final List<T> original;

  public Reversed(List<T> original) {
    this.original = original;
  }

  public Iterator<T> iterator() {
    final ListIterator<T> i = original.listIterator(original.size());
    return new Iterator<T>() {
      public boolean hasNext() { return i.hasPrevious(); }
      public T next() { return i.previous(); }
      public void remove() { i.remove(); }
    };
  }

  public static <T> Reversed<T> reversed(List<T> original) {
    return new Reversed<T>(original);
  }
  
  public static void main(String[] args) {
    
    List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5));
    for (Integer i : list) System.out.print(i+" "); System.out.println();
    for (Integer i : reversed(list)) System.out.print(i+" "); System.out.println();
    for (Integer i : list) System.out.print(i+" "); System.out.println();
    list.clear();
    System.out.println(list.isEmpty());
    
  }
}