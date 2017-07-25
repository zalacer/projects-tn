package ex32;

import static v.ArrayUtils.*;

import st.BSTX;

/* p418
  3.2.19  Draw the sequence of BSTs that results when you delete the keys 
  from the tree of Exercise 3.2.1, one by one, by successively deleting 
  the key at the root.
 */             

public class Ex3219DrawBSTsequenceWhenDeletingRoot {

  public static void main(String[] args) {
    
    String[] a = "E A S Y Q U E S T I O N".split("\\s+");
    Integer[] b = rangeInteger(0, a.length);
    BSTX<String,Integer> bst = new BSTX<>(a,b);
    System.out.println("bst"); bst.printTree(); 
    
    while (!bst.isEmpty()) {
      String k = bst.getKey(bst.getRoot());
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
    
    after deleting E
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |              \-----O
    |      |                     \-----N
     \-----I
            \-----A
    
    after deleting I
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |              \-----O
     \-----N
            \-----A
    
    after deleting N
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
     \-----O
            \-----A
    
    after deleting O
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
     \-----Q
            \-----A
    
    after deleting Q
    bst
    |       /-----Y
    |      |       \-----U
    |      |              \-----T
     \-----S
            \-----A
    
    after deleting S
    bst
    |       /-----Y
    |      |       \-----U
     \-----T
            \-----A
    
    after deleting T
    bst
    |       /-----Y
     \-----U
            \-----A
    
    after deleting U
    bst
     \-----Y
            \-----A
    
    after deleting Y
    bst
     \-----A
    
    after deleting A
    bst
    <empty tree>

*/
    }

}

