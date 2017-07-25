package ex21;


/* p264
  2.1.6  Which method runs faster for an array with all keys identical, 
  selection sort or insertion sort?
  
  Insertion sort should run faster because it doesn't execute exch() when 
  all keys are identical while insertion sort runs it N times for an array 
  of length N (to exchange each element with itself).
  
  When benchmarking them with sort.SortCompare (downloaded from
  http://algs4.cs.princeton.edu/21elementary/SortCompare.java) modified to
  test identical Integer keys, with Insertion configured to be tested before
  Selection the result was "Insertion is 1.6 times faster than Selection"
  and when Selection was configured to be tested before Insertion the 
  result was "Selection is 0.6 times faster than Insertion". Both tests
  had 5 trials using Integer arrays filled with 10**5 nines.
  
*/

public class Ex2106SelectionVsInsertionPerformance {
  
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
    System.out.println("q==j=="+q);
  }
  
  public static <T extends Comparable<? super T>> void sortTest2(T[] a) { 
    // Sort a[] into increasing order.
    int N = a.length;
    for (int i = 1; i < N; i++) { 
      // Insert a[i] among a[i-1], a[i-2], a[i-3]... ..
      for (int j = i; j > 0 && lessTest(a[j], a[j-1]); j--) {
        exch(a, j, j-1);
      }
    }
  }
  
  private static <T extends Comparable<? super T>> boolean less(T v, T w) {
    return v.compareTo(w) < 0; 
  }
  
  private static <T extends Comparable<? super T>> boolean lessTest(T v, T w) {
    System.out.println("less: a[j]="+v+", a[j-1]="+w);
    return v.compareTo(w) < 0; 
  }

  private static <T extends Comparable<? super T>> void exch(T[] a, int i, int j) { 
    T t = a[i]; a[i] = a[j]; a[j] = t;
  }

  public static void main(String[] args) {
    
    Integer[] z = {1,2,3,4,5};
    sortTest(z); // exits with q == j == 0
    
    System.out.println();
    
    sortTest2(z); // a[j] is always > a[j-1]
    //  less: a[j]=2, a[j-1]=1
    //  less: a[j]=3, a[j-1]=2
    //  less: a[j]=4, a[j-1]=3
    //  less: a[j]=5, a[j-1]=4
    
    System.out.println();
    
    Integer[] y = {5,4,3,2,1,6};
    sortTest2(y); // ends  a[j]=6 > a[j-1]=5
   
    System.out.println();
    
    Integer[] x = {3,2,1};
    sortTest(x); //q==j==1
  }
}
