package sort;

import static v.ArrayUtils.*;

import java.awt.Font;
import java.util.Random;
import java.awt.Color;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

//p251 Algorithm 2.2

@SuppressWarnings("unused")
public class Insertion {

  public static int writes = 0;
  public static double exchanges = 0;
  public static double compares = 0;
  
  // from http://www.rapidtables.com/web/color/html-color-codes.htm
  public static final Color FIREBRICK = new Color(178, 34, 34);

  public static <T extends Comparable<? super T>> int sort(T[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
    }
    return writes;
  }
  
  public static <T extends Comparable<? super T>> int[] indirectSort(T[] a) { 
    // re ex2527: "sort" z by indices of sorted a[] 
    int N = a.length;
    int[] z = range(0,a.length);
    for (int i = 1; i < N; i++) { 
      // Insert z[i] as a[i] would be inserted
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
        exchInt(z, j, j-1);
    }
    return z;
  }
  
  public static <T extends Comparable<? super T>> double[] sort2(T[] a) { 
    // Sort a[] into increasing order.
    exchanges = 0; compares = 0;
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
    }
    return new double[]{exchanges,compares};
  }
  
  public static int insertionSortInt(int[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && lessInt(a[j], a[j-1]); j--)
        exchInt(a, j, j-1);
    }
    return writes;
  }
  
  //exercise 2.1.28 p267
  public static <T extends Comparable<? super T>> double[] insertionTestHypo2128(T[] a) {
    exchanges = 0; compares = 0;
    int N = a.length;
    // print initial array
//    System.out.printf("initial %34s %s\n", arrayToString(a, 1000, 0,0)
//        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), " (NA)");
    for (int i = 1; i < N; i++) { 
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--) {
        exch(a, j, j-1);
        // print array after every exch()
//        System.out.printf("%-2d  %-2d  %34s  %s\n", i, j-1,  arrayToString(a, 1000, 0,0)
//            .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), ""+a[j]+", "+a[j-1]);
      }
    }
    // print the sorted array
//    System.out.printf("        %34s\n", arrayToString(a, 1000, 0,0)
//        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "));
//    System.out.println("insertion: writes="+writes+" compares="+compares );
    return new double[]{exchanges,compares};
  }
  
  // combined Ex2.1.24-25 p267
  public static <T extends Comparable<? super T>> int sortWithSentinelAndWithoutExchanges(T[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length; T t;
    // put smallest first
    for (int i = N - 1; i > 0; i--) if (less(a[i], a[i-1])) exch(a, i, i-1);
    for (int i = 1; i < N; i++) { 
      t = a[i];
      int j = i;
      while (less(t, a[j - 1])) a[j] = a[--j];
      a[j] = t;
    }
    return writes;
  }
  
  // Ex2.1.25 p267
  public static <T extends Comparable<? super T>> int sortWithoutExchanges(T[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length; T t;
    for (int i = 1; i < N; i++) { 
      t = a[i];
      int j = i;
      while (j > 0 && less(t, a[j - 1])) a[j] = a[--j];
      a[j] = t;
    }
    return writes;
  }
  
  public static <T extends Comparable<? super T>> double[] sortWithoutExchanges2(T[] a) { 
    // Sort a[] into increasing order.
    exchanges = 0; compares = 0;
    int N = a.length; T t;
    for (int i = 1; i < N; i++) { 
      t = a[i];
      int j = i;
      while (j > 0 && less(t, a[j - 1])) a[j] = a[--j];
      a[j] = t;
    }
    return new double[]{exchanges, compares};
  }
  
  // Ex2.1.24 p267
  public static <T extends Comparable<? super T>> int sortWithSentinel(T[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length; T t;
    // put smallest first
    for (int i = N - 1; i > 0; i--) if (less(a[i], a[i-1])) exch(a, i, i-1);
    for (int i = 2; i < N; i++) 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; less(a[j], a[j-1]); j--) exch(a, j, j-1);
    return writes;
  }

  // exercise 2.1.17 p265
  public static <T extends Comparable<? super T>> int visualSort(T[] a) { 
    // Sort a[] into increasing order.
    if (Number.class.isAssignableFrom(a.getClass().getComponentType()))
      return numberSort((Number[]) a);
    writes = 0;
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
    }
    return writes;
  }

  // exercise 2.1.17 p265
  public static <U extends Number> int numberSort(U[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length;
    double[] z = new double[a.length];
    for (int i = 1; i < N; i++) z[i] = a[i].doubleValue();
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && z[j] < z[j-1]; j--) {
        doubleExch(z, j, j-1);
        visualShow(z);
      }
    }
    return writes;
  }
  
  //exercise 2.1.18 p265
  public static <T extends Comparable<? super T>> int visualSort2(T[] a) { 
    // Sort a[] into increasing order.
    if (Number.class.isAssignableFrom(a.getClass().getComponentType()))
      return numberSort2((Number[]) a);
    writes = 0;
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
        exch(a, j, j-1);
    }
    return writes;
  }
  
  //exercise 2.1.18 p265
  public static <U extends Number> int numberSort2(U[] a) { 
    // Sort a[] into increasing order.
    writes = 0;
    int N = a.length;
    double[] z = new double[a.length];
    for (int i = 1; i < N; i++) z[i] = a[i].doubleValue();
    initializeVisual2(z);  
    for (int i = 1; i < N; i++) { 
      int j = i;
      while (j > 0 && z[j] < z[j-1]) {
        doubleExch(z, j, j-1);
        j--;
      }
      visualShow2(z,i,j);
    }
    return writes;
  }

  //  the original insertion sort method
  //  public static <T extends Comparable<? super T>> void sort(T[] a) { 
  //    // Sort a[] into increasing order.
  //    int N = a.length;
  //    for (int i = 1; i < N; i++) { 
  //      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
  //      for (int j = i; j > 0 && less(a[j], a[j-1]); j--)
  //        exch(a, j, j-1);
  //    }
  //  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    compares++;
    return v.compareTo(w) < 0; 
  }
  
  private static boolean lessInt(int v, int w) { 
    compares++;
    return v < w;
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
    writes++; exchanges++;
  }
  
  private static void exchInt(int[] a, int i, int j) { 
    int t = a[i]; a[i] = a[j]; a[j] = t;
    writes++;
  }

  private static void doubleExch(double[] a, int i, int j) { 
    double t = a[i]; a[i] = a[j]; a[j] = t;
    writes++;
  }

  //  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
  //    T t = a[i]; a[i] = a[j]; a[j] = t;
  //  }

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

  // exercise 2.1.17 p265
  private static void visualShow(double[] a) {
    // show heights of each element in array while sorting with visualSort(Number[])
    int max = (int) Math.ceil(max(a));
    int n = a.length;
    StdDraw.setXscale(0, n);
    StdDraw.setYscale(0, max);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.005);
    Font font = new Font("Arial", Font.BOLD, 16);
    StdDraw.setFont(font);
    StdDraw.clear();
    for (int k = 0; k < a.length; k++) {
      StdDraw.line(k, 0, k, a[k]);
    }
    StdDraw.textLeft(1.*n/10, 1.*max*7/9, "Insertion Sort");
    StdDraw.show();
    StdDraw.show(1);
  }
  
  //exercise 2.1.18 p265
  // http://algs4.cs.princeton.edu/21elementary/InsertionBars.java with mods
  private static void initializeVisual2(double[] a) {
    int n = a.length;
    StdDraw.setCanvasSize(200, 900);
    StdDraw.setXscale(-1, n+1);
    Font font = new Font("SansSerif", Font.PLAIN, 20);
    StdDraw.setFont(font);
    StdDraw.setPenRadius(0.006);
  }

  //exercise 2.1.18 p265
  // http://algs4.cs.princeton.edu/21elementary/InsertionBars.java with mods
  private static void visualShow2(double[] a, int i, int j) {
    StdDraw.setYscale(-a.length + i + 1, i+.5);
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.textLeft(1.5*a.length/10, i, "Insertion Sort");
    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
    for (int k = 0; k < j; k++) StdDraw.line(k, 0, k, a[k]*0.6);
    StdDraw.setPenColor(FIREBRICK);
    StdDraw.line(j, 0, j, a[j]*0.6);
    StdDraw.setPenColor(StdDraw.BLACK);
    for (int k = j+1; k <= i; k++) StdDraw.line(k, 0, k, a[k]*0.6);
    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
    for (int k = i+1; k < a.length; k++) StdDraw.line(k, 0, k, a[k]*0.6);
  }

  public static void main(String[] args) {

    Random r = new Random(10730119);
    Integer[] w = rangeInteger(1, 10000, 1);
    shuffle(w, r); 
    Integer[] x = w.clone();
    pa(sort2(w),-1);                 //[2.4890929E7,2.4900918E7]
    assert isSorted(w);
    pa(sortWithoutExchanges2(x),-1); //[0.0,2.4900918E7]
    assert isSorted(x);              
    

    //    String[] a = StdIn.readAllStrings(); // S O R T E X A M P L E
    //                                         // E A S Y Q U E S T I O N
    //                                         // a a a a a a a
    //                                         // a b c d e f g
    //    show(a); // A E E L M O P R S T X
    //             // A E E I N O Q S S T U Y 
    //    System.out.println(exchanges);

  }

}
