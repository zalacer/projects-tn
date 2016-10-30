package sort;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.StdIn;

//p251 Algorithm 2.2

@SuppressWarnings("unused")
public class InsertionWork {
  
  public static int writes = 0;
  public static int compares = 0;

  public static <T extends Comparable<? super T>> void sort(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--) {
        exch(a, j, j-1);
      }
    }
  }

  public static void shellSort(int[] a) {
    // not functional: modifies a
    int n = a.length;
    if (a == null || n == 0) return;
    int h = 1;
    while (h < n/3) h = 3*h + 1; 
    int t;
    while (h >= 1) {
      for (int i = h; i < n; i++) {
        for (int j = i; j >= h && a[j] < a[j-h]; j -= h) {
          t = a[j];
          a[j] = a[j-h];
          a[j-h] = t;
        }
      }
      h /= 3;
    }
  }

  public static void swapSort(int[] a) {
    // not functional: modifies a
    if (a == null || a.length == 0) return;
    int t;
    for (int i = 1; i < a.length; i++)
      for (int j = i; j > 0 && a[j] < a[j-1]; j--) {
        t = a[j];
        a[j] = a[j-1];
        a[j-1] = t;
      }
  }

  public static void swapSortSave(int[] a) {
    // not functional: modifies a
    if (a == null || a.length == 0)
      return;
    int t;
    for (int i = 0; i < a.length - 1; i++)
      for (int j = i + 1; j < a.length; j++)
        if (a[i] > a[j]) {
          t = a[i];
          a[i] = a[j];
          a[j] = t;
        }
  }

  public static <T extends Comparable<? super T>> void sortTest(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    int q = 0;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--) {
        exch(a, j, j-1); q = j;
      }
    }
    System.out.println("q="+q);
  }

  public static <T extends Comparable<? super T>> void sortTestShowArray(T[] a) {
    writes = 0; compares = 0;
    int N = a.length;
    // print initial array
    System.out.printf("initial %34s %s\n", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), " (NA)");
    for (int i = 1; i < N; i++) { 
      for (int j = i; j > 0 && less(a[j], a[j-1]); j--) {
        exch(a, j, j-1);
        // print array after every exch()
        System.out.printf("%-2d  %-2d  %34s  %s\n", i, j-1,  arrayToString(a, 1000, 0,0)
            .replaceAll("[\\[\\]]"," ").replaceAll(",","  "), ""+a[j]+", "+a[j-1]);
      }
    }
    // print the sorted array
    System.out.printf("        %34s\n", arrayToString(a, 1000, 0,0)
        .replaceAll("[\\[\\]]"," ").replaceAll(",","  "));
    System.out.println("selection: writes="+writes+" compares="+compares );
  }

  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    compares++;
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
    writes++;
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
    

//    Integer[] z1 = {5,4,3,2,1};
//    sortTestShowArray(z1);
    //  initial                     5  4  3  2  1   (NA)
    //  1   0                       4  5  3  2  1   5, 4
    //  2   1                       4  3  5  2  1   5, 3
    //  2   0                       3  4  5  2  1   4, 3
    //  3   2                       3  4  2  5  1   5, 2
    //  3   1                       3  2  4  5  1   4, 2
    //  3   0                       2  3  4  5  1   3, 2
    //  4   3                       2  3  4  1  5   5, 1
    //  4   2                       2  3  1  4  5   4, 1
    //  4   1                       2  1  3  4  5   3, 1
    //  4   0                       1  2  3  4  5   2, 1
    //                              1  2  3  4  5 

    //        String[] a = StdIn.readAllStrings(); // S O R T E X A M P L E
    //                                             // E A S Y Q U E S T I O N
    //                                             // a a a a a a a
    //                                             // a b c d e f g
    //        sortTestShowArray(a);
    //        //show(a); // A E E L M O P R S T X
    //                 // A E E I N O Q S S T U Y 

    /*
1  0 [O,S,R,T,E,X,A,M,P,L,E]
2  1 [O,R,S,T,E,X,A,M,P,L,E]
3  3 [O,R,S,T,E,X,A,M,P,L,E]
4  0 [E,O,R,S,T,X,A,M,P,L,E]
5  5 [E,O,R,S,T,X,A,M,P,L,E]
6  0 [A,E,O,R,S,T,X,M,P,L,E]
7  2 [A,E,M,O,R,S,T,X,P,L,E]
8  4 [A,E,M,O,P,R,S,T,X,L,E]
9  2 [A,E,L,M,O,P,R,S,T,X,E]
10 2 [A,E,E,L,M,O,P,R,S,T,X]
      A E E L M O P R S T X 

S O R T E X A M P L E
j-1=0
1    O  S  R  T  E  X  A  M  P  L  E 
j-1=1
2    O  R  S  T  E  X  A  M  P  L  E 
3    O  R  S  T  E  X  A  M  P  L  E 
j-1=3
j-1=2
j-1=1
j-1=0
4    E  O  R  S  T  X  A  M  P  L  E 
5    E  O  R  S  T  X  A  M  P  L  E 
j-1=5
j-1=4
j-1=3
j-1=2
j-1=1
j-1=0
6    A  E  O  R  S  T  X  M  P  L  E 
j-1=6
j-1=5
j-1=4
j-1=3
j-1=2
7    A  E  M  O  R  S  T  X  P  L  E 
j-1=7
j-1=6
j-1=5
j-1=4
8    A  E  M  O  P  R  S  T  X  L  E 
j-1=8
j-1=7
j-1=6
j-1=5
j-1=4
j-1=3
j-1=2
9    A  E  L  M  O  P  R  S  T  X  E 
j-1=9
j-1=8
j-1=7
j-1=6
j-1=5
j-1=4
j-1=3
j-1=2
10   A  E  E  L  M  O  P  R  S  T  X 
A E E L M O P R S T X 





E A S Y Q U E S T I O N
j-1=0
1    A  E  S  Y  Q  U  E  S  T  I  O  N 
2    A  E  S  Y  Q  U  E  S  T  I  O  N 
3    A  E  S  Y  Q  U  E  S  T  I  O  N 
j-1=3
j-1=2
4    A  E  Q  S  Y  U  E  S  T  I  O  N 
j-1=4
5    A  E  Q  S  U  Y  E  S  T  I  O  N 
j-1=5
j-1=4
j-1=3
j-1=2
6    A  E  E  Q  S  U  Y  S  T  I  O  N 
j-1=6
j-1=5
7    A  E  E  Q  S  S  U  Y  T  I  O  N 
j-1=7
j-1=6
8    A  E  E  Q  S  S  T  U  Y  I  O  N 
j-1=8
j-1=7
j-1=6
j-1=5
j-1=4
j-1=3
9    A  E  E  I  Q  S  S  T  U  Y  O  N 
j-1=9
j-1=8
j-1=7
j-1=6
j-1=5
j-1=4
10   A  E  E  I  O  Q  S  S  T  U  Y  N 
j-1=10
j-1=9
j-1=8
j-1=7
j-1=6
j-1=5
j-1=4
11   A  E  E  I  N  O  Q  S  S  T  U  Y 
A E E I N O Q S S T U Y 


1  0  A  E  S  Y  Q  U  E  S  T  I  O  N   

     */ 
  }

}
