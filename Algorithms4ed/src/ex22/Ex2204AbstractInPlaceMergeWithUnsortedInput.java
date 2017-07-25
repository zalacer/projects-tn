package ex22;

public class Ex2204AbstractInPlaceMergeWithUnsortedInput {
  
/* p284
  2.2.4  Does the abstract in-place merge produce proper output if and only if the two
  input subarrays are in sorted order? Prove your answer, or provide a counterexample.
  
  from p271: Abstract in-place merge
  public static void merge(Comparable[] a, int lo, int mid, int hi)
  { // Merge a[lo..mid] with a[mid+1..hi].
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];      // condition 1: left subarray exhausted
      else if (j > hi ) a[k] = aux[i++]; // condition 2: right subarray exhausted            
      else if (less(aux[j], aux[i]))     // condition 3: current key in left subarray is
       a[k] = aux[j++]; // condition 3   //   greater than current key in right subarray
      else a[k] = aux[i++];              // condition 4: current key in left subarray is
                                         //   less than current key in right subarray
  }
  
  0. "The method" refers to the abstract in-place merge method on p271.
  
  1. The proper result of the method is the array a with sorted subarray a[lo..hi]
     and a[0..lo-1] and a[hi+1..a.length-1] unchanged.
     
  2. From the code, aux[lo..hi] is a copy of a[lo..hi]] before any changes have
     been made to the latter.
     
  3. Suppose aux[lo..mid] is not sorted. Then there is some lo <= u < mid such that 
     aux[u] > aux[u+1], i.e. less(aux[u],aux[u+1]) is false. The values of these elements 
     must be set to some a[v] and a[w] where lo <= v < hi, lo < w <= hi and v < w by 
     conditions 4 or 2, since those are the only conditions that assign values from 
     aux[lo..mid] and neither can change the relative order of these values because they
     both monatonically increase i while k is also monatonically increased and nowhere in 
     the method is either decreased. Therefore the resulting subarray a[lo..mid] will not 
     be sorted.
     
  4. Suppose aux[mid+1..hi] is not sorted. Then there is some mid+1 <= u < hi such that 
     aux[u] > aux[u+1], i.e. less(aux[u],aux[u+1]) is false. The values of these elements 
     must be set to some a[v] and a[w] where lo <= v < hi, lo < w < = hi and v < w by 
     conditions 3 or 1, since those are the only conditions that assign values from 
     aux[mid+1..hi] and neither can change the relative order of these values because they
     both monatonically increase j while k is also monatonically increased and nowhere in 
     the method is either decreased. Therefore the resulting subarray a[lo..hi] will not 
     be sorted.
     
  5. Suppose aux[lo..mid] is sorted and aux[mid+1..hi] is sorted. Then while neither is
     exhausted, conditions 3 and 4 sorts their elements into a[lo..hi] and if either is 
     exhausted the other's elements are copied into a[lo..hi] in order by condition 1 or
     2 with the result that a[lo..hi] is sorted.
     
  6. The method doesn't touch elements in a outside of a[lo..hi].
  
  7. Therefore the method produces proper output if and only if the two input subarrays
     are in sorted order.
*/
  
  public static void main(String[] args) {

  }

}
