package ex32;

import static v.ArrayUtils.*;

import java.util.Arrays;

import st.BSTX;

/* p418
  3.2.23  Is delete() commutative? (Does deleting x, then 
  y give the same result as deleting y, then x ?)
  
  No. An example where it isn't commutative is given below.
  
 */             

public class Ex3223IsDeleteCommutative {

  public static void main(String[] args) {
    
    String[] u = "BADC".split("");
    Integer[] v = rangeInteger(0,u.length);
    BSTX<String, Integer> bst = new BSTX<>(u,v);
    System.out.println("bst"); 
    bst.printTree(); System.out.println("\n");
    
    bst.delete("A"); bst.delete("B");
    String[] w = bst.toPreOrderArray();
    System.out.println("bst after deleting A then B:"); 
    bst.printTree(); System.out.println("\n");

    bst = new BSTX<>(u,v);
    bst.delete("B"); bst.delete("A");
    String[] x = bst.toPreOrderArray();
    System.out.println("bst after deleting B then A:"); 
    bst.printTree(); System.out.println("\n");
    
    assert !Arrays.equals(w,x);
 
/*
    bst
    |       /-----D
    |      |       \-----C
     \-----B
            \-----A
    
    
    bst after deleting A then B:
     \-----D
            \-----C
    
    
    bst after deleting B then A:
    |       /-----D
     \-----C

*/

    }

}

