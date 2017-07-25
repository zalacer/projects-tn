package ex33;

import static v.ArrayUtils.*;

import st.BSTX;

/* p450
  3.3.20  Compute the internal path length in a perfectly balanced 
  BST of N nodes, when N is a power of 2 minus 1.
  
  It's 2*((2^h)*(h-1)+1) where h is the height of the tree.
  Thanks to WolframAlpha for the partial sum formula, see
    https://www.wolframalpha.com/input/?i=sum+x%282^x%29
  Also I used the definition of internal path length at
    http://mathworld.wolfram.com/InternalPathLength.html
    
  This formula is demonstrated below.
  
 */             

public class Ex3320InternalPathLengthOfPerfectlyBalancedBST {

  public static void main(String[] args) {

    Integer[] u = rangeInteger(1,128);
    Integer[] v = rangeInteger(0,u.length);
    BSTX<Integer, Integer> bst = new BSTX<>(u,v,true);
    System.out.println("internalPathLength="+bst.ipl());
    System.out.println("internalPathLengthUsingFormula="+bst.ipltest());
    assert (u.length+1 & u.length) == 0;
    assert bst.ipl() == bst.ipltest();
    System.out.println("bst"); bst.printTree();

  }

}

