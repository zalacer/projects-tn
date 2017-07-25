package ex33;

import static v.ArrayUtils.*;

import st.RedBlackBSTX;

/* p449
  3.3.13  True or false: If you insert keys in increasing order into a 
  red-black BST, the tree height is monotonically increasing.
  
  Yes it increases monotonically but not strictly according to the 
  example shown below.
  
 */             

public class Ex3313RedBlackBstHeightWithMonatonicallyIncreasingKeys {


  public static void main(String[] args) {
    
    int n = 32;
    Integer[] a = rangeInteger(0,n);
    Integer[] b = rangeInteger(0,a.length);
    RedBlackBSTX<Integer,Integer> st = new RedBlackBSTX<>();
    for (int i = 0; i < n; i++) {
      st.put(a[i],b[i]);
      System.out.print(st.height()+" ");  
    }
    System.out.println();
    // 0 1 1 2 2 2 2 3 3 3 3 3 3 3 3 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 5 

  }

}

