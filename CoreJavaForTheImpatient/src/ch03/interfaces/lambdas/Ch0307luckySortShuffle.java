package ch03.interfaces.lambdas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// 7. Implement the method void luckySort(ArrayList<String>
// strings, Comparator<String> comp) that keeps calling
// Collections.shuffle on the array list until the elements are in increasing
// order, as determined by the comparator.

public class Ch0307luckySortShuffle {

  public static Comparator<String> getStringComparator() {
    return (x, y) -> x.compareTo(y);
  }

  public static void luckySort(ArrayList<String> strings, Comparator<String> comp) {
    int count = 0;
    ArrayList<String> sorted = new ArrayList<>();
    sorted.addAll(strings);
    Collections.sort(sorted, comp);

    while (! strings.equals(sorted)) {
      Collections.shuffle(strings);
      count++;
    }
    System.out.println("sorted in "+count+" tries");
  }

  public static void main(String[] args) {

    ArrayList<String> x = new ArrayList<String>();
    x.add("c"); x.add("a"); x.add("b");
    luckySort(x, getStringComparator());
    // sorted in 4 tries
    // sorted in 8 tries
    // sorted in 2 tries
    // sorted in 3 tries

  }

}
