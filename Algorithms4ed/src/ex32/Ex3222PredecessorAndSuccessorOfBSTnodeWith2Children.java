package ex32;

/* p418
  3.2.22  Prove that if a node in a BST has two children, its successor 
  has no left child and its predecessor has no right child.
  
  Let the node in question be named x. It's successor is min(x.right) and
  if it had a left child it wouldn't be the minimum. It's predecessor is
  max(x.right) and if it had a right child it wouldn't be the maximum.
  Therefore x's successor has no left child and its predecessor has no
  right child.
  
 */             

public class Ex3222PredecessorAndSuccessorOfBSTnodeWith2Children {

  public static void main(String[] args) {

    }

}

