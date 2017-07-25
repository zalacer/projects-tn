package ex31;

import static analysis.Log.lg;
import static java.lang.Math.floor;

/* p391
  3.1.23 Analysis of binary search. Prove that the maximum number of 
  compares used  for a binary search in a table of size N is precisely 
  the number of bits in the binary rep  resentation of N, because the 
  operation of shifting 1 bit to the right converts the binary
  representation of N into the binary representation of floor(N/2).
  
  In a solution for Exercise 3.1.20 it was shown that the max (worst
  case) for the rank(Key) method of a st.BinarySearchST of size(N) is
  lg(N)+1, that really should be floor(lg(N)) + 1 which also gives the
  number of bits in N for N > 0. In other words, 
  for N > 0, N >> (int)floor(lg(N)+1) == 0. Demo below.
 
*/

public class Ex3123BinarySearchStMaxComparesIsNumberOfBitsInN {
  
  public static void main(String[] args) {
    
    int N = 1;
    while (N < Integer.MAX_VALUE-8)
      assert N >> (int)floor(lg(N++)+1) == 0;
  
  }
}
