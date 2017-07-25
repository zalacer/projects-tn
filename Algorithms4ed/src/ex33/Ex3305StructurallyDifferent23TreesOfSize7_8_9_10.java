package ex33;

import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import st.BSTX;
import st.TwoThreeTree;

/* p449
  3.3.5  The figure at right shows all the structurally different 2-3 trees with N
  keys, for N from 1 up to 6 (ignore the order of the subtrees). Draw all the
  structurally different trees for N = 7, 8, 9, and 10.
  
  For this exercise I took a 2-3 tree implementation from GitHub using
  https://github.com/search and hacked it as little as possible to implement
  what's necessary for this exercise. Namely I added shape() and shape(Node, int)
  methods, modelled after BSTX.printLevels(), but instead returning a string 
  representing the shape of the tree simply encoded as follows:
  
  2-3 tree structural shape representation
  ------------------------------------------------
  1. ":" means begin a level.
  2. "1;" signifies a 2-node since it has one key.
  3. "2;" signifies a 3-node since it has two keys.
  4. example: ":2;:1;2;2;" means a 3-node at level 0 and a two-node and 2 
     3-nodes at level 1 for a total of 4 nodes and 2 + 1 + 2 + 2 = 7 keys.
  
  In processing shapes shape(Node int) sorts all the 2-and-3-nodes on each level
  in order to put them in a standard format ignoring the order of subtrees as 
  requested in the exercise statement and allowing easier comparison and grouping 
  of them. The reason for the semicolons is to enable easy splitting of the shape
  strings for each level in order to sort them and also to eliminate ambiguity
  that may arise with a consecutive series of 1's and 2's.  However, by not sorting
  it would be possible to do a more detailed analysis.
  
  From this point I did the usual -- generate all permutations of int[] arrays of
  of the form range(0,N), feed each to a 2-3 tree constructor and put its shape 
  string and up to several key array input examples into a BSTX for N from 1 through 
  10 and print it all out. That's implemented in shapes() below with output from a run
  below it.
  
  It's easy to test an individual input sequence given in the table below by drawing it
  using https://www.cs.usfca.edu/~galles/visualization/BTree.html with Max. Degree = 3,
  but I've found it far easier to handle encoded shapes than drawing them even using that
  website.
 */             

public class Ex3305StructurallyDifferent23TreesOfSize7_8_9_10 {

  public static void shapes() {
    System.out.println("N   shape                     " 
        + "examples of ordered input keys producing shape");
    for (int i = 1; i < 11; i++) {
      int[] a = range(0,i), b;
      Integer[] c = rangeInteger(0,i), d = new Integer[i];
      List<String> list;
      BSTX<String,List<String>> bst = new BSTX<>();
      TwoThreeTree<Integer,Integer> ttt;
      Iterator<int[]> it = permutations(a);
      while (it.hasNext()) {
        b = it.next();
        d = (Integer[])box(b);
        ttt = new TwoThreeTree<>(d,c);
        String s = ttt.shape();
        if (bst.contains(s)) bst.get(s).add(arrayToString(b,99,1,1));   
        else {
          list = new ArrayList<>(); list.add(arrayToString(b,99,1,1));
          bst.put(s,list);
        }
      }
      boolean first = true;
      Iterator<String> is = bst.inOrder().iterator();
      while (is.hasNext()) {
        String k = is.next();
        List<String> l = bst.get(k);
        if (first) {
          if (l.size() >= 3)
            System.out.printf("%-2d  %-25s %-120s\n", i, k, l.get(0)+" "+l.get(1)+" "+l.get(2));
          else if (l.size() >= 2)
            System.out.printf("%-2d  %-25s %-120s\n", i, k, l.get(0)+" "+l.get(1));
          else if (l.size() >= 1)
            System.out.printf("%-2d  %-25s %-120s\n", i, k, l.get(0));
          else System.out.printf("%-2d  %-25s\n", i, k);
          first = false;
        } else {
          if (l.size() >= 3)
            System.out.printf("    %-25s %-120s\n", k, l.get(0)+" "+l.get(1)+" "+l.get(2));
          else if (l.size() >= 2)
            System.out.printf("    %-25s %-120s\n", k, l.get(0)+" "+l.get(1));
          else if (l.size() >= 1)
            System.out.printf("    %-25s %-120s\n", k, l.get(0));
          else System.out.printf("    %-25s\n", k);
        }         
      }
    }
  }

  public static void main(String[] args) {

    shapes();
/*
  N   shape                     examples of ordered input keys producing shape
  1   :1;                       [0]                                                                                                                     
  2   :2;                       [0,1] [1,0]                                                                                                             
  3   :1;:1;1;                  [0,1,2] [0,2,1] [2,0,1]                                                                                                 
  4   :1;:1;2;                  [0,1,2,3] [0,1,3,2] [0,3,1,2]                                                                                           
  5   :1;:2;2;                  [3,0,2,1,4] [3,0,2,4,1] [0,3,2,4,1]                                                                                     
      :2;:1;1;1;                [0,1,2,3,4] [0,1,2,4,3] [0,1,4,2,3]                                                                                     
  6   :2;:1;1;2;                [0,1,2,3,4,5] [0,1,2,3,5,4] [0,1,2,5,3,4]                                                                               
  7   :1;:1;1;:1;1;1;1;         [0,1,2,3,4,5,6] [0,1,2,3,4,6,5] [0,1,2,3,6,4,5]                                                                         
      :2;:1;2;2;                [5,0,1,2,4,3,6] [5,0,1,2,4,6,3] [0,5,1,2,4,6,3]                                                                         
  8   :1;:1;1;:1;1;1;2;         [0,1,2,3,4,5,6,7] [0,1,2,3,4,5,7,6] [0,1,2,3,4,7,5,6]                                                                   
      :2;:2;2;2;                [3,0,2,6,1,5,4,7] [3,0,2,6,1,5,7,4] [3,0,2,1,6,5,7,4]                                                                   
  9   :1;:1;1;:1;1;2;2;         [7,0,1,2,3,4,6,5,8] [7,0,1,2,3,4,6,8,5] [0,7,1,2,3,4,6,8,5]                                                             
      :1;:1;2;:1;1;1;1;1;       [0,1,2,3,4,5,6,7,8] [0,1,2,3,4,5,6,8,7] [0,1,2,3,4,5,8,6,7]                                                             
  10  :1;:1;1;:1;2;2;2;         [5,0,1,2,4,8,3,7,6,9] [5,0,1,2,4,8,3,7,9,6] [5,0,1,2,4,3,8,7,9,6]                                                       
      :1;:1;2;:1;1;1;1;2;       [0,1,2,3,4,5,6,7,8,9] [0,1,2,3,4,5,6,7,9,8] [0,1,2,3,4,5,6,9,7,8]                                                       

*/

  }

}

