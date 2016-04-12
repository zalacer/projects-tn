package ch07.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

// 14. Write a method that produces an immutable list view of 
// the numbers from 0 to n, without actually storing the numbers.\

// If you have a view of numbers they must reside somewhere.

public class Ch0714View {

  public static <T> List<T> getView(List<T> list) {
    // create and return an unmodifiable view of list
    return Collections.unmodifiableList(list);    
  }

  public static List<Integer> getViewN(int n) {
    // create list from IntStream and return an unmodifiable view of it
    return (List<Integer>) Collections.unmodifiableList(IntStream.range(1, n+1)
        .boxed().collect(Collectors.toList()));
  }

  public static void main(String[] args) {

    // exploring characteristics of ordinary list view
    List<Integer> alist = new ArrayList<>(Arrays.asList(1,2,3));
    List<Integer> aview = getView(alist);
    System.out.println(aview);
    // [1, 2, 3]
    // aview.add(4); // java.lang.UnsupportedOperationException
    // aview.remove(2); // java.lang.UnsupportedOperationException
    System.out.println(aview.size());
    // 3
    Object[] oa = aview.toArray();
    System.out.println(Arrays.toString(oa)); 
    // [1, 2, 3]

    // looking at list view generated from IntStream
    List<Integer> aviewN = getViewN(9);
    System.out.println(aviewN);
    // [1, 2, 3, 4, 5, 6, 7, 8, 9]
    // aviewN.add(10); // java.lang.UnsupportedOperationException
    // aviewN.remove(5); // java.lang.UnsupportedOperationException
    System.out.println(aviewN.size());
    // 9
    Object[] ob = aviewN.toArray();
    System.out.println(Arrays.toString(ob)); 
    // [1, 2, 3, 4, 5, 6, 7, 8, 9]

  }

}
