package ex31;

import static v.ArrayUtils.*;

/* p390
  3.1.20  Complete the proof of Proposition B (show that it holds for 
  all values of N). Hint : Start by showing that C(N) is monotonic: 
  C(N) ? C(N+1) for all N > 0.
  
  The first part of Proposition B (on p383) is shown for all N = (2^n)-1
  for n > -1. This exercise it to extend it to all N.
  
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
  
  Proposition B': the worst case running time of st.BinarySearchST.rank(Key) 
  in a sorted array of length N uses (lg(N), lg(N)+1] iterations and a maximum of 
  lg(N)+1 comparisons.
  
  1. Counting iterations of the while loop starting at 0, after the Kth 
     iteration at most N/(2^K) keys remain.
  2. The worst case occurs when the search is done for a key that isn't 
     in the array because that maximizes the number of iterations.
  3. The last iteration of the worst case occurs when N/(2^K) >= 1 and
     N/(2^(K+1)) < 1. That's equivilant to 2^K <= N and 2^(K+1) > N or K in 
     (lg(N) - 1, lg(N)]. 
  4. Including the 0th iteration, the number of iterations is in (lg(N), lg(N)+1].
  4. As its coded above, each iteration uses 1 comparison.
  5. Therefore the worst case number of comparisons is in (lg(N), lg(N)+1] and 
     "Binary search in an ordered array with N keys uses no more than lg(N)+1 
     compares for a search (successful or unsuccessful)".
  *  Note that the floor of all logs should be taken and was omitted for convenience.

*/

public class Ex3120CompleteProofOfPropositionB {
  
  public static int compares;
  
  public static int rank(Integer[] keys, Integer key) {
    if (key == null) throw new NullPointerException();
    compares = 0;
    int n = keys.length;
    int lo = 0, hi = n-1; 
    while (lo <= hi) { 
      int mid = lo + (hi - lo) / 2; 
      int cmp = key.compareTo(keys[mid]);
      compares++; System.out.print("compare ");
      if      (cmp < 0) { hi = mid - 1; System.out.println(lo+" "+hi+" "+(hi-lo+1));} 
      else if (cmp > 0) { lo = mid + 1; System.out.println(lo+" "+hi+" "+(hi-lo+1));} 
      else return mid; 
    } 
    return lo;
  }

  public static void main(String[] args) {
    
    Integer[] z = rangeInteger(0,128); // 2^7 keys
    System.out.println("rank = "+rank(z,130)); 
    System.out.println("compares = "+compares); // 8
    System.out.println("rank = "+rank(z,-3));
    System.out.println("compares = "+compares); // 7
    
  }
}
