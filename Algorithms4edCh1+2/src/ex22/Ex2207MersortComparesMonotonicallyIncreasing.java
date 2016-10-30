package ex22;

import static sort.TopDownMerge.comparesInversionSearchIntArrays;

public class Ex2207MersortComparesMonotonicallyIncreasing {

/* p284
  2.2.7  Show that the number of compares used by mergesort is monotonically 
  increasing (C(N+1) > C(N) for all N > 0).
  
  If you mean while sorting, each recursion of the sort(a, aux, lo, hi) method (code
  given below) until hi <= lo does a merge each of which does at least one comparison 
  because each merges a subarray of length at least 2 and inspection of the sort code 
  shows when that's true lo <= mid and mid < hi which means that in at least the first 
  iteration of the second for loop in merge(a, aux, lo, mid, hi) (code given below) 
  conditions 1 and 2 both fail and condition 3 is invoked causing a comparison. 
  
   top-down sort (unoptimized):
    private static <T extends Comparable<? super T>> void 
        sort(T[] a, T[] aux, int lo, int hi) { 
    // Sort a[lo..hi].
    if (hi <= lo) return;
    int mid = lo + (hi - lo)/2;
    sort(a, aux, lo, mid); // Sort left half.
    sort(a, aux, mid+1, hi); // Sort right half.
    merge(a, aux, lo, mid, hi); 
  }
  
  top-down merge (unoptimized):
    public static <T extends Comparable<? super T>> void merge(
      T[] a, T[] aux, int lo, int mid, int hi) { 
    int i = lo, j = mid+1;
    for (int k = lo; k <= hi; k++) // Copy a[lo..hi] to aux[lo..hi].
      aux[k] = a[k];
    for (int k = lo; k <= hi; k++) { // Merge back to a[lo..hi].
      if (i > mid) a[k] = aux[j++];      // condition 1
      else if (j > hi ) a[k] = aux[i++]; // condition 2
      else if (less(aux[j], aux[i]))     // condition 3
        a[k] = aux[j++];
      else a[k] = aux[i++];              // condition 4
    }
  
  However if you mean it absolutely, then it's false in general that the number of
  comparisons increases monotonically with increasing array length. In fact it may
  decrease with increasing array length.
  
  Testing shows that even for insertions of a single element there are many cases
  where the number of compares is less for the resulting longer array compared to the 
  shorter starting array. For examples:
     (using top-down mergesort unoptimized in any way)
  1. mergesorting [4,2,8,10,6] requires 8 compares but [5,4,2,8,10,6] needs only 7
     and the latter is constructed by prepending 5 to the former.
  2. mergesorting [2,8,10,12,4,6] requires 11 compares but [2,8,10,12,4,6,1] needs
     only 10 and the latter is constructed by appending 1 to the former.
  3. mergesorting [4,2,8,10,6] requires 8 compares but [4,2,1,8,10,6] needs only 7
     and the latter is constructed by inserting 1 between indices 1 and 2 in the former.
     
  For more thousands more examples run comparesInversionSearchIntArrays() in main.
  
*/
  
  public static void main(String[] args) {

    comparesInversionSearchIntArrays();
    
  }
    
}
