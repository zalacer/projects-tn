package ex32;

import static v.ArrayUtils.*;

import st.BSTXI;

/* p418
  3.2.15  Give the sequences of nodes examined when the methods in  
  BST are used to compute each of the following quantities for the 
  tree drawn at right.
  
    a. floor("Q")                     E
    b. select(5)                     / \  
    c. ceiling("Q")                 D   Q---- 
    d. rank("J")                   /   /     \
    e. size("D", "T")             A   J       T
    f. keys("D", "T")                  \     /
                                        M   S
    
    a. E Q (using floorRprint(Key) and floorRprint(Node,Key))
    b. E Q (using selectRprint(Key) and selectRprint(Node,int))
    c. E Q (using ceilingRprint(Key) and ceilingRprint(Node,Key))
    d. E D Q J (using rankRprint(Key), rankRprint(Key,Node) and 
       sizePrint(Node) that does Node.size lookup)
    e. E Q T E D Q J T S E D A (using sizePrint(Key,Key), rankRprint(Key),  
       rankRprint(Key,Node), containsPrint(Key), getRprint(Key), 
       getRprint(Node,Key) and sizePrint(Node))
    f. E D Q J M T S (using keysRprint(Key,Key) and 
       keysRprint(Node,Queue<Key>,Key,Key),
       
    Demos of the functions referenced are below.
    
 */             

public class Ex3215SequencesOfNodesUsedByGivenMethods {

  public static void main(String[] args) {

    String[] u = "E D Q A J T M S".split("\\s+");
    Integer[] v = rangeInteger(0,9);
    BSTXI<String, Integer> bst = new BSTXI<>(u,v);
    System.out.print("inorder:    ");par(bst.toInOrderArray());    // [A,D,E,J,M,Q,S,T]
    System.out.print("preorder:   ");par(bst.toPreOrderArray());   // [E,D,A,Q,J,M,T,S]
    System.out.print("levelorder: ");par(bst.toLevelOrderArray()); // [E,D,Q,A,J,T,M,S]
    System.out.println("\nbst");
    bst.printTree();
    /*  |              /-----T
        |             |       \-----S
        |       /-----Q
        |      |      |       /-----M
        |      |       \-----J
         \-----E
                \-----D
                       \-----A  
    */
    System.out.println("\noperation            sequence of nodes examined");
    System.out.println("-------------------- --------------------------");
    System.out.print("floor(\"Q\")           ");
    bst.floorRprint("Q");     // E Q 
    
    System.out.print("select(5)            ");
    bst.selectRprint(5);      // E Q
    
    System.out.print("ceiling(\"Q\")         ");
    bst.ceilingRprint("Q");   // E Q
    
    System.out.print("rank(\"J\")            ");
    bst.rankRprint("J");      // E D Q J (using sizePrint(Node) 
                              // that does Node.size lookup)
    System.out.print("\nsize(\"D\",\"T\")        ");
    bst.sizePrint("D","T");   // E Q T E D Q J T S E D A (using rankRprint(Key), 
                              // containsPrint(Key), getRprint(Key) and 
                              // sizePrint(Key) (that does Node.size lookup))
    System.out.print("\nkeysRprint(\"D\",\"T\")  ");
    bst.keysRprint("D", "T"); // E D Q J M T S  

/*
inorder:    [A,D,E,J,M,Q,S,T]
preorder:   [E,D,A,Q,J,M,T,S]
levelorder: [E,D,Q,A,J,T,M,S]

bst
|              /-----T
|             |       \-----S
|       /-----Q
|      |      |       /-----M
|      |       \-----J
 \-----E
        \-----D
               \-----A

operation            sequence of nodes examined
-------------------- --------------------------
floor("Q")           E Q 
select(5)            E Q 
ceiling("Q")         E Q 
rank("J")            E D Q J 
size("D","T")        E Q T E D Q J T S E D A 
keysRprint("D","T")  E D Q J M T S 
    
*/  

  }

}

