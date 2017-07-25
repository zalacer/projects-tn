package ex33;

import static v.ArrayUtils.*;

import st.RedBlackBSTX;

/* p450
  3.3.14  Draw the red-black BST that results when you insert letters  
  A through  K in order into an initially empty tree, then describe what 
  happens in general when trees are built by insertion of keys in 
  ascending order (see also the figure in the text).
  
  The tree fills up each level successively before going to the next. 
  I don't get a long chain of red links as shown in the text. See the figure 
  below. Maybe I'm using an improved version of RedBlackBST since I just got 
  it from http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html 
  dated Wed Nov 2 05:25:05 EDT 2016 that's 20 days ago. 
 */             

public class Ex3314DrawRedBlackBstForKeysAthruK {


  public static void main(String[] args) {
    
    int[] sa = range(65,76);
    Integer[] ia = rangeInteger(0,sa.length);
    RedBlackBSTX<Character,Integer> st = new RedBlackBSTX<>();
    for (int i = 0; i < sa.length; i++) st.put((char)sa[i],ia[i]);
    System.out.println("st"); st.printTree();
/*
    st
    |              /-----K
    |       /-----J
    |      |       \-----I
     \-----H
           |              /-----G
           |       /-----F
           |      |       \-----E
            \-----D(red)
                  |       /-----C
                   \-----B
                          \-----A
*/


  }

}

