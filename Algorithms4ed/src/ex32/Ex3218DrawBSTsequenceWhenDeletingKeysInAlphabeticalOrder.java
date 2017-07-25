package ex32;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.BSTX;

/* p418
  3.2.17  Draw the sequence of BSTs that results when you delete the keys from
  the tree of Exercise 3.2.1, one by one, in the order they were inserted.
 */             

public class Ex3218DrawBSTsequenceWhenDeletingKeysInAlphabeticalOrder {

  public static void main(String[] args) {
    
    String[] a = "E A S Y Q U E S T I O N".split("\\s+");
    Integer[] b = rangeInteger(0, a.length);
    BSTX<String,Integer> bst = new BSTX<>(a,b);
    System.out.println("bst"); bst.printTree(); 
    
    Iterator<String> it = bst.inOrder().iterator(); String k;
    while (it.hasNext()) {
      k = it.next();
      bst.delete(k);
      System.out.println("\nafter deleting "+k);
      System.out.println("bst"); bst.printTree(); 
    }
/*
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |             |       /-----O
    |      |             |      |       \-----N
    |      |              \-----I
     \-----E
            \-----A
    
    after deleting A
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |             |       /-----O
    |      |             |      |       \-----N
    |      |              \-----I
     \-----E
    
    after deleting E
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
            \-----Q
                  |       /-----O
                  |      |       \-----N
                   \-----I
    
    after deleting I
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
            \-----Q
                   \-----O
                          \-----N
    
    after deleting N
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
            \-----Q
                   \-----O
    
    after deleting O
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
            \-----Q
    
    after deleting Q
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
    
    after deleting S
    bst
     \-----Y
            \-----U
                   \-----T
    
    after deleting T
    bst
     \-----Y
            \-----U
    
    after deleting U
    bst
     \-----Y
    
    after deleting Y
    bst
    <empty tree>
*/
    }

}

