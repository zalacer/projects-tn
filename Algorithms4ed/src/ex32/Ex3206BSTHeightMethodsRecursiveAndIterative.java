package ex32;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.BSTX;

/* p416
  3.2.6  Add to  BST a method  height() that computes the height of 
  the tree. Develop two implementations: a recursive method (which 
  takes linear time and space proportional to the height), and a 
  method like  size() that adds a field to each node in the tree (and
  takes linear space and constant time per query).

  This is implemented in st.BSTX by adding a height field to Node, 
  coding put(Node,Key,Value) to set the height of new nodes similarly 
  to how it updates size, adding the same code to delete(Node,Key), 
  deleteMax(Node) and deleteMin(Node) and adding methods to get the height 
  of the tree, of any node and of any node referenced by key, namely 
  nodeHeight(), nodeHeight(Node) and nodeHeight(Key). I also added another
  height(Key) method to get a Node's height referenced by key. These 
  implementations are demonstrated by the tests below.

 */

public class Ex3206BSTHeightMethodsRecursiveAndIterative {

  public static void main(String[] args) {

    Integer[] a = new Integer[]{8,4,12,2,6,10,14,1,3,5,7,9,11,13,20,15};
    Integer[] b = rangeInteger(0,a.length);
    Iterator<Integer> it;
    BSTX<Integer,Integer> bst = new BSTX<>();

    for (int j = 0; j < a.length; j++) {
      bst.put(a[j], b[j]);
      assert bst.height() == bst.nodeHeight();
      it = bst.inOrder().iterator();
      while(it.hasNext()) {
        Integer k = it.next();
        assert bst.height(bst.getNode(k)) == bst.nodeHeight(bst.getNode(k));
        assert bst.height(bst.getNode(k)) == bst.nodeHeight(k);
        assert bst.height(k) == bst.nodeHeight(k);
      }
    }

    int c = 0;
    while (bst.size() > 1) {
      c++;
      if (c%3==0) bst.deleteMax();
      if (c%3==1) bst.deleteMin();
      if (c%3==2) bst.delete(bst.getUniformRandomKey());
      assert bst.height() == bst.nodeHeight();
      it = bst.inOrder().iterator();
      while(it.hasNext()) {
        Integer k = it.next();
        assert bst.height(bst.getNode(k)) == bst.nodeHeight(bst.getNode(k));
        assert bst.height(bst.getNode(k)) == bst.nodeHeight(k);
        assert bst.height(k) == bst.nodeHeight(k);
      }
    }

  }

}

