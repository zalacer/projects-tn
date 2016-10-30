package sort;

import static v.ArrayUtils.*;

public class AbstractInPlaceMerge {
  
 

  // p271
//  public static <T extends Comparable<? super T>> void merge(T[] a, int lo, int mid, int hi) {
//   // Merge a[lo..mid] with a[mid+1..hi].
//    int i = lo, j = mid+1;
//    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
//       aux[k] = a[k]; 
//    for (int k = lo; k <= hi; k++) // Merge back to a[lo..hi].
//      if (i > mid) a[k] = aux[j++];
//      else if (j > hi ) a[k] = aux[i++];
//      else if (less(aux[j], aux[i])) a[k] = aux[j++];
//      else a[k] = aux[i++];
//  }
  
  public static int[] a = null;
  public static int[] b = null;
  
  public static void merge(int[] a, int lo, int mid, int hi) {
    // Merge a[lo..mid] with a[mid+1..hi].
     int i = lo, j = mid+1;
     for (int k = lo; k <= hi; k++) b[k] = a[k]; 
     for (int k = lo; k <= hi; k++) // Merge back to a[lo..hi].
       if (i > mid) a[k] = b[j++];
       else if (j > hi ) a[k] = b[i++];
       else if (b[j] < b[i]) a[k] = b[j++];
       else a[k] = b[i++];
   }

  public static void main(String[] args) {
    
    int[] a = {1,3,5,2,4,6};
    b = new int[a.length];
    merge(a,0,2,5); 
    pa(a,-1); //[1,2,3,4,5,6]
    
    int[] c = {1,3,5,2,0,6};
    merge(c,0,2,5); 
    pa(c,-1);
  }

}
