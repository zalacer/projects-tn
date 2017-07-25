package ex32;

import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import st.BSTX;

/* p416
  3.2.3  Give five orderings of the keys A X C S E R H that, when 
  inserted into an initially empty BST, produce the best-case tree.

  from http://algs4.cs.princeton.edu/32bst/:
    Solution. Any sequence that inserts H first; C before A and E; 
    S before R and X.
    
  from http://pages.cs.wisc.edu/~vernon/cs367/notes/9.BST.html:
    In the best case, all nodes have 2 children, and all leaves are 
    at the same depth. (Meaning that all nodes that aren't leaves 
    have 2 children and all leaves are at the same level.)
    
  There are 80 best cases out of 5040 permutations for a rate of 1.59%.
  The best cases are listed and drawn below.
*/

public class Ex3203BSTBestCaseOrderings {
  
  public static void main(String[] args) {
    
    String[] a = "A X C S E R H".split("\\s+"), e;
    Integer[] b = rangeInteger(0, a.length);
    List<String[]> list = new ArrayList<>();
    BSTX<String,Integer> bst; int[] d;
    Iterator<int[]> it = permutations(range(0, a.length));
    while(it.hasNext()) {
      d = it.next();
      e = new String[a.length];
      for (int i = 0; i < d.length; i++) e[i] = a[d[i]];
      bst = new BSTX<>(e,b);
      if (bst.isBestCase()) list.add(e);   
    }
    System.out.println("\n#best cases="+list.size()+"\n");
    int f = 1;
    for (String[] s : list) {
      bst = new BSTX<>(s,b); 
      System.out.println("best case "+(f++)+": "+arrayToString(s,81,1,1));
      bst.printTree();
      System.out.println();
    }
    
    /*  #best cases=80

    best case 1: [H,C,E,A,S,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 2: [H,C,E,A,S,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 3: [H,C,A,E,S,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 4: [H,C,A,E,S,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 5: [H,C,A,S,E,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 6: [H,C,A,S,E,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 7: [H,C,A,S,R,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 8: [H,C,A,S,R,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 9: [H,C,A,S,X,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 10: [H,C,A,S,X,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 11: [H,C,S,A,X,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 12: [H,C,S,A,X,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 13: [H,C,S,A,R,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 14: [H,C,S,R,A,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 15: [H,C,S,R,A,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 16: [H,C,S,A,R,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 17: [H,C,S,A,E,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 18: [H,C,S,A,E,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 19: [H,C,S,E,A,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 20: [H,C,S,E,A,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 21: [H,C,S,E,R,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 22: [H,C,S,R,E,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 23: [H,C,E,S,R,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 24: [H,C,E,S,A,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 25: [H,C,E,S,A,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 26: [H,S,R,C,E,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 27: [H,S,C,R,E,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 28: [H,S,C,E,R,A,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 29: [H,S,C,E,A,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 30: [H,S,C,E,A,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 31: [H,S,C,A,E,X,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 32: [H,S,C,A,E,R,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 33: [H,S,C,A,R,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 34: [H,S,C,R,A,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 35: [H,S,R,C,A,E,X]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 36: [H,S,R,C,A,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 37: [H,S,C,R,A,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 38: [H,S,C,A,R,X,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 39: [H,S,C,A,X,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 40: [H,S,C,A,X,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 41: [H,S,C,X,A,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 42: [H,S,C,X,A,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 43: [H,S,C,X,R,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 44: [H,S,C,R,X,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 45: [H,S,R,C,X,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 46: [H,S,R,C,X,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 47: [H,S,C,R,X,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 48: [H,S,C,X,R,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 49: [H,S,C,X,E,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 50: [H,S,C,X,E,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 51: [H,S,C,E,X,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 52: [H,S,C,E,X,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 53: [H,S,C,E,R,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 54: [H,S,C,R,E,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 55: [H,S,R,C,E,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 56: [H,C,E,S,X,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 57: [H,C,E,S,X,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 58: [H,C,E,S,R,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 59: [H,C,S,R,E,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 60: [H,C,S,E,R,X,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 61: [H,C,S,E,X,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 62: [H,C,S,E,X,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 63: [H,C,S,X,E,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 64: [H,C,S,X,E,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 65: [H,C,S,X,R,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 66: [H,C,S,R,X,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 67: [H,C,S,R,X,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 68: [H,C,S,X,R,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 69: [H,C,S,X,A,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 70: [H,C,S,X,A,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 71: [H,S,X,C,E,A,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 72: [H,S,X,C,E,R,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 73: [H,S,X,C,R,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 74: [H,S,X,R,C,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 75: [H,S,R,X,C,E,A]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 76: [H,S,R,X,C,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 77: [H,S,X,R,C,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 78: [H,S,X,C,R,A,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 79: [H,S,X,C,A,R,E]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
    
    best case 80: [H,S,X,C,A,E,R]
    |              /-----X
    |       /-----S
    |      |       \-----R
     \-----H
           |       /-----E
            \-----C
                   \-----A
  */    
  }
  
}

