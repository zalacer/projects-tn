package ex32;

import static v.ArrayUtils.*;

import st.BSTX;

/* p416
  3.2.1  Draw the BST that results when you insert the keys  
  E A S Y Q U E S T I O N, in that order (associating the value  
  i with the  i th key, as per the convention in the text) into 
  an initially empty tree. How many compares are needed to build 
  the tree?
  
*/
public class Ex3201DrawBST {
  
  public static void main(String[] args) {
    
    String[] a = "E A S Y Q U E S T I O N".split("\\s+");
    Integer[] b = rangeInteger(0, a.length);
    BSTX<String,Integer> bst = new BSTX<>(a,b);
    System.out.println("compares = "+bst.getCompares()); // 1422, 28 without check() in put
    System.out.println("bst:"); bst.printTree();
    /*  bst:
         |             /-----Y
         |             |      \-----U
         |             |             \-----T
         |      /-----S
         |      |      \-----Q
         |      |             |      /-----O
         |      |             |      |      \-----N
         |      |             \-----I
         \-----E
                \-----A   
   */

    bst.showPreOrder(); // E:6 A:1 S:7 Q:4 I:9 O:10 N:11 Y:3 U:5 T:8 
    
  }
  
}

