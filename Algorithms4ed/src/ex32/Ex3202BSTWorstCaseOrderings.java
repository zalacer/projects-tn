package ex32;

import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import st.BSTX;

/* p416
  3.2.2  Inserting the keys in the order A X C S E R H into an initially 
  empty BST gives a worst-case tree where every node has one null link, 
  except one at the bottom, which has two null links. Give five other 
  orderings of these keys that produce worst-case trees.
  
  There are 64 worst cases out of 5040 permutations for a rate of 1.27%.
  The worst cases are listed and drawn below.
  
*/
public class Ex3202BSTWorstCaseOrderings {
  
  public static void main(String[] args) {
    
    String[] a = "A X C S E R H".split("\\s+");
    Integer[] b = rangeInteger(0, a.length);
    BSTX<String,Integer> bst = new BSTX<>(a,b);
    System.out.println("bst:"); bst.printTree();
    /*  bst:
         |      /-----X
         |      |      |      /-----S
         |      |      |      |      |      /-----R
         |      |      |      |      |      |      \-----H
         |      |      |      |      \-----E
         |      |      \-----C
         \-----A
   */

    System.out.println("\n"+bst.isWorstCase()); //true
    
    int[] c = range(0,a.length), d; String[] e; 
    List<String[]> list = new ArrayList<>();
    Iterator<int[]> it = permutations(c);
    while(it.hasNext()) {
      d = it.next();
      e = new String[a.length];
      for (int i = 0; i < d.length; i++) e[i] = a[d[i]];
      bst = new BSTX<>(e,b);
      if (bst.isWorstCase()) list.add(e);   
    }
    System.out.println("\n#worst cases="+list.size()+"\n");
    int f = 1;
    for (String[] s : list) {
      bst = new BSTX<>(s,b); 
      System.out.println("worst case "+(f++)+": "+arrayToString(s,81,1,1));
      bst.printTree();
      System.out.println();
    }
/*
    #worst cases=64
    
    worst case 1: [A,X,C,S,E,R,H]
    |      /-----X
    |      |      |      /-----S
    |      |      |      |      |      /-----R
    |      |      |      |      |      |       \-----H
    |      |      |      |       \-----E
    |      |       \-----C
     \-----A
    
    worst case 2: [A,X,C,S,E,H,R]
    |      /-----X
    |      |      |      /-----S
    |      |      |      |      |              /-----R
    |      |      |      |      |      /-----H
    |      |      |      |       \-----E
    |      |       \-----C
     \-----A
    
    worst case 3: [A,X,C,S,R,H,E]
    |      /-----X
    |      |      |      /-----S
    |      |      |      |       \-----R
    |      |      |      |              \-----H
    |      |      |      |                     \-----E
    |      |       \-----C
     \-----A
    
    worst case 4: [A,X,C,S,R,E,H]
    |      /-----X
    |      |      |      /-----S
    |      |      |      |       \-----R
    |      |      |      |             |      /-----H
    |      |      |      |              \-----E
    |      |       \-----C
     \-----A
    
    worst case 5: [A,X,C,E,H,R,S]
    |      /-----X
    |      |      |                              /-----S
    |      |      |                      /-----R
    |      |      |              /-----H
    |      |      |      /-----E
    |      |       \-----C
     \-----A
    
    worst case 6: [A,X,C,E,H,S,R]
    |      /-----X
    |      |      |                      /-----S
    |      |      |                      |       \-----R
    |      |      |              /-----H
    |      |      |      /-----E
    |      |       \-----C
     \-----A
    
    worst case 7: [A,X,C,E,S,H,R]
    |      /-----X
    |      |      |              /-----S
    |      |      |              |      |      /-----R
    |      |      |              |       \-----H
    |      |      |      /-----E
    |      |       \-----C
     \-----A
    
    worst case 8: [A,X,C,E,S,R,H]
    |      /-----X
    |      |      |              /-----S
    |      |      |              |       \-----R
    |      |      |              |              \-----H
    |      |      |      /-----E
    |      |       \-----C
     \-----A
    
    worst case 9: [A,X,S,R,H,E,C]
    |      /-----X
    |      |       \-----S
    |      |              \-----R
    |      |                     \-----H
    |      |                            \-----E
    |      |                                   \-----C
     \-----A
    
    worst case 10: [A,X,S,R,H,C,E]
    |      /-----X
    |      |       \-----S
    |      |              \-----R
    |      |                     \-----H
    |      |                           |      /-----E
    |      |                            \-----C
     \-----A
    
    worst case 11: [A,X,S,R,C,H,E]
    |      /-----X
    |      |       \-----S
    |      |              \-----R
    |      |                    |      /-----H
    |      |                    |      |       \-----E
    |      |                     \-----C
     \-----A
    
    worst case 12: [A,X,S,R,C,E,H]
    |      /-----X
    |      |       \-----S
    |      |              \-----R
    |      |                    |              /-----H
    |      |                    |      /-----E
    |      |                     \-----C
     \-----A
    
    worst case 13: [A,X,S,C,R,E,H]
    |      /-----X
    |      |       \-----S
    |      |             |      /-----R
    |      |             |      |      |      /-----H
    |      |             |      |       \-----E
    |      |              \-----C
     \-----A
    
    worst case 14: [A,X,S,C,R,H,E]
    |      /-----X
    |      |       \-----S
    |      |             |      /-----R
    |      |             |      |       \-----H
    |      |             |      |              \-----E
    |      |              \-----C
     \-----A
    
    worst case 15: [A,X,S,C,E,H,R]
    |      /-----X
    |      |       \-----S
    |      |             |                      /-----R
    |      |             |              /-----H
    |      |             |      /-----E
    |      |              \-----C
     \-----A
    
    worst case 16: [A,X,S,C,E,R,H]
    |      /-----X
    |      |       \-----S
    |      |             |              /-----R
    |      |             |              |       \-----H
    |      |             |      /-----E
    |      |              \-----C
     \-----A
    
    worst case 17: [A,C,E,H,R,S,X]
    |                                              /-----X
    |                                      /-----S
    |                              /-----R
    |                      /-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 18: [A,C,E,H,R,X,S]
    |                                      /-----X
    |                                      |       \-----S
    |                              /-----R
    |                      /-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 19: [A,C,E,X,H,R,S]
    |                      /-----X
    |                      |      |              /-----S
    |                      |      |      /-----R
    |                      |       \-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 20: [A,C,E,H,X,R,S]
    |                              /-----X
    |                              |      |      /-----S
    |                              |       \-----R
    |                      /-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 21: [A,C,E,H,X,S,R]
    |                              /-----X
    |                              |       \-----S
    |                              |              \-----R
    |                      /-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 22: [A,C,E,X,H,S,R]
    |                      /-----X
    |                      |      |      /-----S
    |                      |      |      |       \-----R
    |                      |       \-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 23: [A,C,E,X,S,H,R]
    |                      /-----X
    |                      |       \-----S
    |                      |             |      /-----R
    |                      |              \-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 24: [A,C,E,X,S,R,H]
    |                      /-----X
    |                      |       \-----S
    |                      |              \-----R
    |                      |                     \-----H
    |              /-----E
    |      /-----C
     \-----A
    
    worst case 25: [A,C,X,E,S,R,H]
    |              /-----X
    |              |      |      /-----S
    |              |      |      |       \-----R
    |              |      |      |              \-----H
    |              |       \-----E
    |      /-----C
     \-----A
    
    worst case 26: [A,C,X,E,S,H,R]
    |              /-----X
    |              |      |      /-----S
    |              |      |      |      |      /-----R
    |              |      |      |       \-----H
    |              |       \-----E
    |      /-----C
     \-----A
    
    worst case 27: [A,C,X,E,H,S,R]
    |              /-----X
    |              |      |              /-----S
    |              |      |              |       \-----R
    |              |      |      /-----H
    |              |       \-----E
    |      /-----C
     \-----A
    
    worst case 28: [A,C,X,E,H,R,S]
    |              /-----X
    |              |      |                      /-----S
    |              |      |              /-----R
    |              |      |      /-----H
    |              |       \-----E
    |      /-----C
     \-----A
    
    worst case 29: [A,C,X,S,R,E,H]
    |              /-----X
    |              |       \-----S
    |              |              \-----R
    |              |                    |      /-----H
    |              |                     \-----E
    |      /-----C
     \-----A
    
    worst case 30: [A,C,X,S,R,H,E]
    |              /-----X
    |              |       \-----S
    |              |              \-----R
    |              |                     \-----H
    |              |                            \-----E
    |      /-----C
     \-----A
    
    worst case 31: [A,C,X,S,E,H,R]
    |              /-----X
    |              |       \-----S
    |              |             |              /-----R
    |              |             |      /-----H
    |              |              \-----E
    |      /-----C
     \-----A
    
    worst case 32: [A,C,X,S,E,R,H]
    |              /-----X
    |              |       \-----S
    |              |             |      /-----R
    |              |             |      |       \-----H
    |              |              \-----E
    |      /-----C
     \-----A
    
    worst case 33: [X,S,R,H,E,C,A]
     \-----X
            \-----S
                   \-----R
                          \-----H
                                 \-----E
                                        \-----C
                                               \-----A
    
    worst case 34: [X,S,R,H,E,A,C]
     \-----X
            \-----S
                   \-----R
                          \-----H
                                 \-----E
                                       |      /-----C
                                        \-----A
    
    worst case 35: [X,S,A,R,H,E,C]
     \-----X
            \-----S
                  |      /-----R
                  |      |       \-----H
                  |      |              \-----E
                  |      |                     \-----C
                   \-----A
    
    worst case 36: [X,S,R,H,A,E,C]
     \-----X
            \-----S
                   \-----R
                          \-----H
                                |      /-----E
                                |      |       \-----C
                                 \-----A
    
    worst case 37: [X,S,R,A,H,E,C]
     \-----X
            \-----S
                   \-----R
                         |      /-----H
                         |      |       \-----E
                         |      |              \-----C
                          \-----A
    
    worst case 38: [X,S,R,A,C,E,H]
     \-----X
            \-----S
                   \-----R
                         |                      /-----H
                         |              /-----E
                         |      /-----C
                          \-----A
    
    worst case 39: [X,S,R,A,C,H,E]
     \-----X
            \-----S
                   \-----R
                         |              /-----H
                         |              |       \-----E
                         |      /-----C
                          \-----A
    
    worst case 40: [X,S,R,A,H,C,E]
     \-----X
            \-----S
                   \-----R
                         |      /-----H
                         |      |      |      /-----E
                         |      |       \-----C
                          \-----A
    
    worst case 41: [X,S,R,H,A,C,E]
     \-----X
            \-----S
                   \-----R
                          \-----H
                                |              /-----E
                                |      /-----C
                                 \-----A
    
    worst case 42: [X,S,A,R,H,C,E]
     \-----X
            \-----S
                  |      /-----R
                  |      |       \-----H
                  |      |             |      /-----E
                  |      |              \-----C
                   \-----A
    
    worst case 43: [X,S,A,R,C,H,E]
     \-----X
            \-----S
                  |      /-----R
                  |      |      |      /-----H
                  |      |      |      |       \-----E
                  |      |       \-----C
                   \-----A
    
    worst case 44: [X,S,A,R,C,E,H]
     \-----X
            \-----S
                  |      /-----R
                  |      |      |              /-----H
                  |      |      |      /-----E
                  |      |       \-----C
                   \-----A
    
    worst case 45: [X,S,A,C,R,E,H]
     \-----X
            \-----S
                  |              /-----R
                  |              |      |      /-----H
                  |              |       \-----E
                  |      /-----C
                   \-----A
    
    worst case 46: [X,S,A,C,R,H,E]
     \-----X
            \-----S
                  |              /-----R
                  |              |       \-----H
                  |              |              \-----E
                  |      /-----C
                   \-----A
    
    worst case 47: [X,S,A,C,E,H,R]
     \-----X
            \-----S
                  |                              /-----R
                  |                      /-----H
                  |              /-----E
                  |      /-----C
                   \-----A
    
    worst case 48: [X,S,A,C,E,R,H]
     \-----X
            \-----S
                  |                      /-----R
                  |                      |       \-----H
                  |              /-----E
                  |      /-----C
                   \-----A
    
    worst case 49: [X,A,S,C,E,R,H]
     \-----X
           |      /-----S
           |      |      |              /-----R
           |      |      |              |       \-----H
           |      |      |      /-----E
           |      |       \-----C
            \-----A
    
    worst case 50: [X,A,S,C,E,H,R]
     \-----X
           |      /-----S
           |      |      |                      /-----R
           |      |      |              /-----H
           |      |      |      /-----E
           |      |       \-----C
            \-----A
    
    worst case 51: [X,A,S,C,R,H,E]
     \-----X
           |      /-----S
           |      |      |      /-----R
           |      |      |      |       \-----H
           |      |      |      |              \-----E
           |      |       \-----C
            \-----A
    
    worst case 52: [X,A,S,C,R,E,H]
     \-----X
           |      /-----S
           |      |      |      /-----R
           |      |      |      |      |      /-----H
           |      |      |      |       \-----E
           |      |       \-----C
            \-----A
    
    worst case 53: [X,A,S,R,C,E,H]
     \-----X
           |      /-----S
           |      |       \-----R
           |      |             |              /-----H
           |      |             |      /-----E
           |      |              \-----C
            \-----A
    
    worst case 54: [X,A,S,R,C,H,E]
     \-----X
           |      /-----S
           |      |       \-----R
           |      |             |      /-----H
           |      |             |      |       \-----E
           |      |              \-----C
            \-----A
    
    worst case 55: [X,A,S,R,H,C,E]
     \-----X
           |      /-----S
           |      |       \-----R
           |      |              \-----H
           |      |                    |      /-----E
           |      |                     \-----C
            \-----A
    
    worst case 56: [X,A,S,R,H,E,C]
     \-----X
           |      /-----S
           |      |       \-----R
           |      |              \-----H
           |      |                     \-----E
           |      |                            \-----C
            \-----A
    
    worst case 57: [X,A,C,E,S,R,H]
     \-----X
           |                      /-----S
           |                      |       \-----R
           |                      |              \-----H
           |              /-----E
           |      /-----C
            \-----A
    
    worst case 58: [X,A,C,E,S,H,R]
     \-----X
           |                      /-----S
           |                      |      |      /-----R
           |                      |       \-----H
           |              /-----E
           |      /-----C
            \-----A
    
    worst case 59: [X,A,C,E,H,S,R]
     \-----X
           |                              /-----S
           |                              |       \-----R
           |                      /-----H
           |              /-----E
           |      /-----C
            \-----A
    
    worst case 60: [X,A,C,E,H,R,S]
     \-----X
           |                                      /-----S
           |                              /-----R
           |                      /-----H
           |              /-----E
           |      /-----C
            \-----A
    
    worst case 61: [X,A,C,S,R,E,H]
     \-----X
           |              /-----S
           |              |       \-----R
           |              |             |      /-----H
           |              |              \-----E
           |      /-----C
            \-----A
    
    worst case 62: [X,A,C,S,R,H,E]
     \-----X
           |              /-----S
           |              |       \-----R
           |              |              \-----H
           |              |                     \-----E
           |      /-----C
            \-----A
    
    worst case 63: [X,A,C,S,E,H,R]
     \-----X
           |              /-----S
           |              |      |              /-----R
           |              |      |      /-----H
           |              |       \-----E
           |      /-----C
            \-----A
    
    worst case 64: [X,A,C,S,E,R,H]
     \-----X
           |              /-----S
           |              |      |      /-----R
           |              |      |      |       \-----H
           |              |       \-----E
           |      /-----C
            \-----A

*/    
  }
  
}

