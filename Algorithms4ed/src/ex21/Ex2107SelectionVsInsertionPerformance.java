package ex21;


/* p264
  2.1.7  Which method runs faster for an array in reverse order, selection sort 
  or insertion sort?
  
  Selection sort is relatively much more efficient at sorting an array in reverse 
  order compared to Insertion sort because it accomplishes the sort with just N 
  exchanges while the latter does 2N. After updating sort.SortCompare with a test
  for this, running it resulted in "Selection is 1.5 times faster than Insertion" 
  when Selection was run before Insertion" and "Insertion is 0.6 times faster than 
  Selection" when running Insertion before Selection.

*/

public class Ex2107SelectionVsInsertionPerformance {
  
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
