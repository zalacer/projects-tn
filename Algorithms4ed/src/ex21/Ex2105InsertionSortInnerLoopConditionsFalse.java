package ex21;


/* p264
  2.1.5  For each of the two conditions in the inner for loop in insertion sort 
  (Algorithm 2.2), describe an array of N items where that condition is always 
  false when the loop terminates.
  
  Here is the code:
   public static <T extends Comparable<? super T>> void sort(T[] a) { 
     int N = a.length;
     for (int i = 1; i < N; i++) 
       for (int j = i; j > 0 && less(a[j], a[j-1]); j--) // inner loop
         exch(a, j, j-1);
   }
   
   When the array is already sorted sort() a[j] is always > a[j-1] including when sort() 
   exits. Even when the array only ends with its greatest element, sort exits with 
   a[j] > a[j-1].  Only when the array is already sorted does sort() exit with j = 0;
   that applies to arrays of zero or one element or all elements with the same value
   according to the implementation of Comparator.compareTo().
   See demos below.
  
*/

public class Ex2105InsertionSortInnerLoopConditionsFalse {
  
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
