package ex33;

import static v.ArrayUtils.*;

import st.RedBlackBSTX;

/* p449
  3.3.10  Draw the red-black BST that results when you insert items with the keys
  E A S Y Q U T I O N in that order into an initially empty tree.
 */             

public class Ex3310DrawRedBlackBstEASYQUTION {


  public static void main(String[] args) {
    
    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Integer[] ia = rangeInteger(0,sa.length);
    RedBlackBSTX<String,Integer> st = new RedBlackBSTX<>(sa,ia);
    System.out.println("st"); st.printTree();
/*
    st
    |              /-----Y
    |       /-----U
    |      |       \-----T
     \-----S
           |       /-----Q
            \-----O
                  |       /-----N
                  |      |       \-----I(red)
                   \-----E(red)
                          \-----A
 
*/

  }

}

