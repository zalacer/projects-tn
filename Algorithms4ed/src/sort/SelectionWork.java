package sort;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.StdIn;

//p249 Algorithm 2.1

@SuppressWarnings("unused")
public class SelectionWork {
  
  public static int writes = 0;
  public static int compares = 0;
  public static int exchanges = 0;
  public static int minupdates = 0;
  public static int c = 1;

  public static <T extends Comparable<? super T>> void sort(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length; // array length
    for (int i = 0; i < N; i++) { 
      // Exchange a[i] with smallest entry in a[i+1...N).
      int min = i; // index of minimal entr.
      for (int j = i+1; j < N; j++)
        if (less(a[j], a[min])) min = j;
      exch(a, i, min);
    }
  }
  
  public static <T extends Comparable<? super T>> void sortTestMinUpdates(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length; // array length
    for (int i = 0; i < N; i++) { 
      // Exchange a[i] with smallest entry in a[i+1...N).
      int min = i; // index of minimal entr.
      for (int j = i+1; j < N; j++)
        if (less(a[j], a[min])) {
          min = j;
          minupdates++;
        }
      exch(a, i, min);
    }
  }
  
  public static <T extends Comparable<? super T>> void sortTestShowArray(T[] a) { 
    // Sort a[] into increasing order.
    writes = 0; compares = 0;
    int N = a.length; // array length
    System.out.printf("initial %34s %s\n", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), " (NA)");
    for (int i = 0; i < N; i++) { 
      // Exchange a[i] with smallest entry in a[i+1...N).
      System.out.println("i="+i);
      int min = i; // index of minimal entr.
      for (int j = i+1; j < N; j++)
        if (less(a[j], a[min])) {
          min = j;
          minupdates++;
        }
      exch(a, i, min);
      System.out.printf("%-2d  %-2d  %34s  %s\n", i, min,  arrayToString(a, 1000, 0,0)
          .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), ""+a[i]+", "+a[min]);
//      System.out.printf("%-2d  %s\n", c++, arrayToString(a, 1000, 0,0));
    }
    System.out.printf("        %34s\n", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "));
    System.out.println("selection: writes="+writes+" compares="+compares );
  }
  
  public static <T extends Comparable<? super T>> void sortOptimized(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length; // array length
    for (int i = 0; i < N; i++) { 
      // Exchange a[i] with smallest entry in a[i+1...N).
      int min = i; // index of minimal entr.
      for (int j = i+1; j < N; j++)
        if (less(a[j], a[min])) min = j;
      if (min != i) {
        exch(a, i, min);
        System.out.println("exchanging "+i+" with "+min);
      }
    }
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    compares++;
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
    writes++; exchanges++;
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
    
    Integer[] z = {1,0,1,0,1,0};
    sortTestShowArray(z);
    
//    Integer[] w1 = {5,4,3,2,1};
//    sortTestShowArray(w1);
    //  1   [1,4,3,2,5]
    //  2   [1,2,3,4,5]
    //  3   [1,2,3,4,5]
    //  4   [1,2,3,4,5]
    //  5   [1,2,3,4,5]


    
//    Integer[] z = {5,3,7,2,6,1,4};
//    sort(z);
//    show(z); // 1 2 3 4 5 6 7
//    System.out.println();
    
//    minupdates = 0;
//    Integer[] z1 = {7,6,5,4,3,2,1};
//    sortTestMinUpdates(z1);
//    System.out.println(minupdates); //12
//    minupdates = 0;
//    z1 = new Integer[]{7,1,2,3,4,5,6,};
//    sortTestMinUpdates(z1);
//    System.out.println(minupdates); //6
//    minupdates = 0;
//    Integer[] z2 = new Integer[]{16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
//    sortTestMinUpdates(z2); 
//    System.out.println(minupdates); //64
//    minupdates = 0;
//    Integer[] z3 = new Integer[]{8,7,6,5,4,3,2,1};
//    sortTestMinUpdates(z3); 
//    System.out.println(minupdates); //16
//    minupdates = 0;
//    Integer[] z4 = new Integer[]{6,5,4,3,2,1};
//    sortTestMinUpdates(z4); 
//    System.out.println(minupdates); //9
//    minupdates = 0;
//    Integer[] z5 = new Integer[]{5,4,3,2,1};
//    sortTestMinUpdates(z5); 
//    System.out.println(minupdates); //6
//    minupdates = 0;
//    z1 = new Integer[]{1,2,3,4,5,6,7};
//    sortTestMinUpdates(z1);
//    System.out.println(minupdates); //0
    
    // for length 8: 7 + 5 + 3 + 1 = 16 = (3+1)**2
    // for length 16: 15+13+11+9+7+5+3+1 = 64 = (7+1)**2
   
    // for length 7 = 2(1/2)*3(3+1) = 12
    // 6 + 4 + 2
    // 7 6 5 4 3 2 1  1 6 5 4 3 2 7   1 2 5 4 3 6 7  1 2 3 4 5 6 7
    // N-1 + N-3 + N-5 // for N == 7
    // N-1 + N-2-1 + N-2-2-1 = Sigma(0,(int)lg(N-1), N-1-2**i) + 1
//                           = Sigma(0,(int)lg(N-1), 2**i) - 1
//                          ~= ((2**lg(N-1)+1) - 1) == (2**lg(2N-2)) - 1
//                                                  == 2N - 3
//    exchanges = 0;
//    
//    String[] a = StdIn.readAllStrings(); // S O R T E X A M P L E
//                                         // E A S Y Q U E S T I O N
//                                         // a a a a a a a
//                                         // a b c d e f g
//    sortTestShowArray(a);
    //  E A S Y Q U E S T I O N
    //  1   [A,E,S,Y,Q,U,E,S,T,I,O,N]
    //  2   [A,E,S,Y,Q,U,E,S,T,I,O,N]
    //  3   [A,E,E,Y,Q,U,S,S,T,I,O,N]
    //  4   [A,E,E,I,Q,U,S,S,T,Y,O,N]
    //  5   [A,E,E,I,N,U,S,S,T,Y,O,Q]
    //  6   [A,E,E,I,N,O,S,S,T,Y,U,Q]
    //  7   [A,E,E,I,N,O,Q,S,T,Y,U,S]
    //  8   [A,E,E,I,N,O,Q,S,T,Y,U,S]
    //  9   [A,E,E,I,N,O,Q,S,S,Y,U,T]
    //  10  [A,E,E,I,N,O,Q,S,S,T,U,Y]
    //  11  [A,E,E,I,N,O,Q,S,S,T,U,Y]
    //  12  [A,E,E,I,N,O,Q,S,S,T,U,Y]
//    show(a); // A E E L M O P R S T X
//             // A E E I N O Q S S T U Y 
//    System.out.println(exchanges);

  }

}
