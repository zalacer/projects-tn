package ex35;

import static v.ArrayUtils.*;

import st.RedBlackBSTM;

/* p507
  3.5.10  Modify RedBlackBST to keep duplicate keys in the tree. Return 
  any value associated with the given key for get(), and remove all 
  nodes in the tree that have keys equal to the given key for delete().
  
  This is implemented as st.RedBlackBSTM using the same approach as for
  BSTM as described in the previous exercise solution.

  A few basic methods of RedBlackBSTM are demonstrated below.

 */

public class Ex3510ModifyRedBlackBST2KeepDuplicateKeys {

  public static void main(String[] args) {

    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0,sa.length);
    
    RedBlackBSTM<Character, Integer> h1 = new RedBlackBSTM<>(ca,ia);
    
    System.out.println("h1"); h1.printTree();
    System.out.println("\n(Nodes are printed in the format (key,val,c,size,dSize))\n");
    System.out.print("h1 = "+h1.tos()+"\n");
    System.out.println("check() = "+h1.check());
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("put('E', 11)"); h1.put('E', 11); 
    System.out.print("h1 = "+h1.tos()+"\n"); 
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("h.delete('E')"); h1.delete('E');
    System.out.print("h1 = "+h1.tos()+"\n"); 
    System.out.println("get('E') = "+h1.get('E'));
    System.out.println("count of 'E' = "+h1.getKeyCount('E'));
    System.out.println("h1"); h1.printTree();
    System.out.println("check() = "+h1.check());

/*
    h1
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
    
    (Nodes are printed in the format (key,val,c,size,dSize))
    
    h1 = (A,1,1,1,1),(E,0,1,4,4),(I,7,1,1,1),(N,9,1,2,2),(O,8,1,6,6),(Q,4,1,1,1),
      (S,2,1,10,10),(T,6,1,1,1),(U,5,1,3,3),(Y,3,1,1,1)}
    check() = true
    get('E') = 0
    count of 'E' = 1
    put('E', 11)
    h1 = (A,1,1,1,1),(E,11,2,4,5),(I,7,1,1,1),(N,9,1,2,2),(O,8,1,6,7),(Q,4,1,1,1),
      (S,2,1,10,11),(T,6,1,1,1),(U,5,1,3,3),(Y,3,1,1,1)}
    get('E') = 11
    count of 'E' = 2
    h.delete('E')
    h1 = (A,1,1,1,1),(I,7,2,3,4),(N,9,1,1,1),(O,8,1,5,6),(Q,4,1,1,1),(S,2,1,9,10),
      (T,6,1,1,1),(U,5,1,3,3),(Y,3,1,1,1)}
    get('E') = null
    count of 'E' = 0
    h1
    |              /-----Y
    |       /-----U
    |      |       \-----T
     \-----S
           |       /-----Q
            \-----O
                  |       /-----N
                   \-----I(red)
                          \-----A
    check() = true

*/

  }

}

