package sort;

import edu.princeton.cs.algs4.StdIn;

//p245

public class Example {
  
  public static <T extends Comparable<? super T>> void sort(T[] a) { 
    /* See Algorithms 2.1, 2.2, 2.3, 2.4, 2.5, or 2.7. */ 
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) { 
    return v.compareTo(w) < 0; 
  }

  @SuppressWarnings("unused")
  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t; 
  }

  public static <T extends Comparable<? super T>> boolean isSorted(T[] a) { 
    // Test whether the array entries are in order.
    for (int i = 1; i < a.length; i++)
      if (less(a[i], a[i-1])) return false;
    return true;
  }
  
  private static <T extends Comparable<? super T>> void show(T[] a) { 
    // Print the array, on a single line.
    for (int i = 0; i < a.length; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public static void main(String[] args) { 
    // Read strings from standard input, sort them, and print.
    String[] a = StdIn.readAllStrings();
    sort(a);
    assert isSorted(a);
    show(a);
  }
}
