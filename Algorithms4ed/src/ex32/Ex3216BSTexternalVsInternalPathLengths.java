package ex32;

/* p418
  3.2.16  Define the external path length of a tree to be the sum of the number 
  of nodes on the paths from the root to all null links. Prove that the differ-
  ence between the external and internal path lengths in any binary tree with N
  N nodes is 2N (see Proposition C).
  
  Suppose the internal path length is I, the external path length is E and the
  latter is calculated using an extended BST that is a BST derived from a normal 
  BST by adding special nodes wherever a null subtree is present and E is the  
  sum of all the path lengths from root to each of the special nodes.
  ( http://mathworld.wolfram.com/ExtendedBinaryTree.html
    http://mathworld.wolfram.com/InternalPathLength.html
    http://mathworld.wolfram.com/ExternalPathLength.html )
  
  Then by induction:
  1. Base case: When the BST has no node is size N = 0, I = E = 0 and E = I + 2N.
  2. Hypothesis: Assume E = I + 2N for all BSTs with N nodes for any N >= 0.
  3. Induction step: Consider a BST with N+1 nodes and one node is removed.
     Then the BST has N nodes and by the hypothesis the relation E' = I' + 2N 
     between its updated internal path length I' and its updated external path 
     length E' holds by assumption. If the depth* of the removed node was D, then 
     I = I' + D and E = E' + D + 2, where I and E are the internal and external 
     path lengths of the BST when it had N+1 nodes, since attached to the added 
     node are two special nodes in the extended BST and only special nodes count 
     for E. Combining these relations, E = I' + 2N + D + 2 = I + 2(N+1). QED.
    
    * Depth is defined as the number of nodes from node A along an existing path
      down to node C including all nodes on the path even A and B. For example in 
      the tree  A
                 \
                  B
                   \
                    C           
      the depth of C from A is 3. It's defined this way because get(Key) does a 
      comparison for all the nodes in that path when searching for C from A.
  
 */             

public class Ex3216BSTexternalVsInternalPathLengths {

  public static void main(String[] args) {

  }

}

