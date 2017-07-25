package ex31;

import static v.ArrayUtils.*;

/* p390
  3.1.18  Prove that the rank() method in  BinarySearchST is correct. 
  
  Here is st.BinarySearchST.rank(Key);
  
    public int rank(Key key) {
      if (key == null) 
        throw new NullPointerException("argument to rank() is null"); 
      int lo = 0, hi = n-1; 
      while (lo <= hi) { 
          int mid = lo + (hi - lo) / 2; 
          int cmp = key.compareTo(keys[mid]);
          if      (cmp < 0) hi = mid - 1; 
          else if (cmp > 0) lo = mid + 1; 
          else return mid; 
      } 
      return lo;
    }
    
    proof that rank(key) return the number of keys in the symbol table 
    strictly less than key:
    
    0. this is an inductive proof with the invariant that if key is in keys[]
       then it's in a subarray of keys[i,j] where i<=j and i and j are >=0 and 
       <= n-1 where n is the length of keys. the proof validates the inductive
       hypothesis that if the invariant holds at the beginning of the call to 
       rank(), then rank() returns the index of key in keys[] if keys[] contains 
       key else it returns 0 and in both cases it returns the number of elements 
       in keys[] strictly less than the index of key, since keys[] is sorted and
       all its elements are unique, because that's exactly what the index of an
       element means for such arrays.
    1. if n == 0, rank() returns 0 since lo is set to 0 and the while loop is 
       skipped because lo is not less than or equal to hi == -1.
    2. if n == 1 and key is in keys[] it must be keys[0], lo, hi, mid and cmp are 
       0 and cmp is returned. If key is not in keys[], if cmp < 0 hi is set to -1 
       that breaks the loop and lo == 0 is returned, if cmp > 1 lo is set to 1 that 
       breaks the loop and lo == 0 is returned.
    3. in general, suppose the invariant holds on entry to rank() for keys[] of
       length n-1>0. Then if the length of keys[] is n and if 
       key == keys[lo+(hi-lo)/2] == keys[mid], cmp is 0 and rank() returns mid, the 
       index of key in keys[]. Else search is done on keys[lo,mid-1] or 
       keys[mid+1,hi]. If key.compareTo(keys[mid]) < 0 then key must be in 
       keys[lo,mid-1] if it occurs in keys[]. if key.compareTo(keys[mid]) > 0 then 
       key must be in keys[mid+1,hi] if it occurs in keys[]. Therefore both preserve 
       the invariant. In either case, since both have smaller length than keys[] 
       while satisfying the invariant, by the inductive hypothesis the one chosen 
       returns the correct result.
*/

public class Ex3118BinarySearchSTproveRankIsCorrect {
  
  public static int rank(Integer[] keys, Integer key) {
    if (key == null) 
      throw new NullPointerException("argument to rank() is null"); 
    int n = keys.length;
    int lo = 0, hi = n-1; 
    while (lo <= hi) { 
      int mid = lo + (hi - lo) / 2; 
      int cmp = key.compareTo(keys[mid]);
      if      (cmp < 0) hi = mid - 1; 
      else if (cmp > 0) lo = mid + 1; 
      else return mid; 
    } 
    return lo;
  }
   
  public static void main(String[] args) {
    
    Integer[] z = rangeInteger(0,6);
    System.out.println(rank(z,0)); //0
   
  }

}
