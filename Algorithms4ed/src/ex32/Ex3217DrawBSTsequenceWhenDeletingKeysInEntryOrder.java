package ex32;

import static v.ArrayUtils.*;

import st.BSTX;

/* p418
  3.2.17  Draw the sequence of BSTs that results when you delete the keys from
  the tree of Exercise 3.2.1, one by one, in the order they were inserted.
 */             

public class Ex3217DrawBSTsequenceWhenDeletingKeysInEntryOrder {

  public static void main(String[] args) {
    
    String[] a = "E A S Y Q U E S T I O N".split("\\s+");
    Integer[] b = rangeInteger(0, a.length);
    BSTX<String,Integer> bst = new BSTX<>(a,b);
    System.out.println("bst"); bst.printTree(); 
    
    for (int i = 0; i < a.length; i++) {
      bst.delete(a[i]);
      System.out.println("\nafter deleting "+a[i]);
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
    
    after deleting A
    bst
    |              /-----Y
    |             |       \-----U
    |             |              \-----T
    |       /-----S
    |      |       \-----Q
    |      |              \-----O
    |      |                     \-----N
     \-----I
    
    after deleting S
    bst
    |              /-----Y
    |             |       \-----U
    |       /-----T
    |      |       \-----Q
    |      |              \-----O
    |      |                     \-----N
     \-----I
    
    after deleting Y
    bst
    |              /-----U
    |       /-----T
    |      |       \-----Q
    |      |              \-----O
    |      |                     \-----N
     \-----I
    
    after deleting Q
    bst
    |              /-----U
    |       /-----T
    |      |       \-----O
    |      |              \-----N
     \-----I
    
    after deleting U
    bst
    |       /-----T
    |      |       \-----O
    |      |              \-----N
     \-----I
    
    after deleting E
    bst
    |       /-----T
    |      |       \-----O
    |      |              \-----N
     \-----I
    
    after deleting S
    bst
    |       /-----T
    |      |       \-----O
    |      |              \-----N
     \-----I
    
    after deleting T
    bst
    |       /-----O
    |      |       \-----N
     \-----I
    
    after deleting I
    bst
     \-----O
            \-----N
    
    after deleting O
    bst
     \-----N
    
    after deleting N
    bst
    <empty tree>

*/
    }


}

