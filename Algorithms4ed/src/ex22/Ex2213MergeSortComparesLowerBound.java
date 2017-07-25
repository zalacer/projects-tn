package ex22;

import static sort.BlockMerge.isSorted;
import static sort.BlockMerge.sort;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.util.Random;

public class Ex2213MergeSortComparesLowerBound {

/* p285
  2.2.13 Lower bound for average case. Prove that the expected number of compares used
  by any compare-based sorting algorithm must be at least ~N lg N (assuming that all
  possible orderings of the input are equally likely). Hint: The expected number of com-
  pares is at least the external path length of the compare tree (the sum of the lengths 
  of the paths from the root to all leaves), which is minimized when it is balanced.
  
  This was the conclusion of the proof of Proposition I on p281, namely that ~NlgN is a
  lower bound on the number of compares for any compare-based sorting algorithm.  What
  we know is that compare trees are binary trees; they have at least N! leaves for an array
  of length N and a properly working sort method since it must function correctly for all 
  possible input permutations; and a binary tree of height h can have no more than 2**h 
  nodes. From the formula for the binary tree's number of nodes, a lower bound on the height
  is therefore lg(N!) which is approximately NlgN using Stirling's approximation.
*/ 

  public static void main(String[] args) {

    Random r; Integer[] w;
    
    for (int i = 2; i < 1002; i++) {
      w = rangeInteger(1, i, 1);
      r = new Random(System.currentTimeMillis());
      shuffle(w, r); 
      sort(w);
      assert isSorted(w);
    }

  }

}
