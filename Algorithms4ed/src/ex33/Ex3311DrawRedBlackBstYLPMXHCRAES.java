package ex33;

import static v.ArrayUtils.*;

import st.RedBlackBSTX;

/* p450
  3.3.10  Draw the red-black BST that results when you insert items with the keys
  Y L P M X H C R A E S in that order into an initially empty tree.
 */             

public class Ex3311DrawRedBlackBstYLPMXHCRAES {


  public static void main(String[] args) {
    
    String[] sa = "Y L P M X H C R A E S".split("\\s+");
    Integer[] ia = rangeInteger(0,sa.length);
    RedBlackBSTX<String,Integer> st = new RedBlackBSTX<>(sa,ia);
    System.out.println("st"); st.printTree();
/*
    st
    |              /-----Y
    |       /-----X
    |      |       \-----S
    |      |              \-----R(red)
     \-----P
           |       /-----M
            \-----L
                  |       /-----H
                  |      |       \-----E(red)
                   \-----C(red)
                          \-----A

*/

  }

}

