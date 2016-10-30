package ex25;

import static sort.Insertion.indirectSort;
import static v.ArrayUtils.show;

/* p357
  2.5.27 Sorting parallel arrays. When sorting parallel arrays, it is useful 
  to have a version of a sorting routine that returns a permutation, say  
  index[], of the indices in sorted order. Add a method indirectSort() to  
  Insertion that takes an array of  Comparable objects  a[] as argument, but 
  instead of rearranging the entries of a[] returns an integer array index[] 
  so that a[index[0]] through a[index[N-1]] are the items in ascending order.
 */

public class Ex2527InsertionIndirectSortOfIndices {     

  public static void main(String[] args) {
            
    Integer[] a = {5,4,3,2,1};
    int[] idr = indirectSort(a); 
    show(idr); // 4 3 2 1 0 

  }

}


