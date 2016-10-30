package sort;

import static v.ArrayUtils.arrayToString;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

@SuppressWarnings("unused")
public class BubbleWork {

    private BubbleWork() { }

    public static <Key extends Comparable<Key>> void sort(Key[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int exchanges = 0;
            for (int j = n-1; j > i; j--) {
                if (less(a[j], a[j-1])) {
                    exch(a, j, j-1);
                    exchanges++;
                }
            }
            if (exchanges == 0) break;
        }
    }
    
    public static <Key extends Comparable<Key>> void sortPrint(Key[] a) {
      int n = a.length;
      System.out.printf("initial %34s %s\n", arrayToString(a, 1000, 0,0)
          .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), " (NA)");
      for (int i = 0; i < n; i++) {
          int exchanges = 0;
          for (int j = n-1; j > i; j--) {
              if (less(a[j], a[j-1])) {
                  exch(a, j, j-1);
                  exchanges++;
                  System.out.printf("%-2d  %-2d  %34s  %s\n", i, j-1,  arrayToString(a, 1000, 0,0)
                      .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), ""+a[j]+", "+a[j-1]);
              }
          }
          if (exchanges == 0) break;
      }
  }

    private static <Key extends Comparable<Key>> boolean less(Key v, Key w) {
        return v.compareTo(w) < 0;
    }

    private static <Key extends Comparable<Key>> void exch(Key[] a, int i, int j) {
        Key swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    private static <Key extends Comparable<Key>> void show(Key[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }


    public static void main(String[] args) {
      
      Integer[] ia = {5,4,3,2,1};
      sortPrint(ia);
      
      
      
//        String[] a = StdIn.readAllStrings();
//        BubbleWork.sort(a);
//        show(a);
    }
}

