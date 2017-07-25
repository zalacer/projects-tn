package ex32;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;

//import st.BSTxEx3206Ex3207B;
import st.BSTX;

/* p416
  3.2.7  Add to BST a recursive method avgCompares() that computes 
  the average number of compares required by a random search hit 
  in a given BST (the internal path length of the tree divided by 
  its size, plus one). Develop two implementations: a recursive 
  method (which takes linear time and space proportional to the 
  height), and a method like size() that adds a field to each node 
  in the tree (and takes linear space and constant time per query).

  The methods are implemented in BSTX. The recursive method is named 
  avgComparesR to distinguish it from the one that runs in constant 
  time named avgCompares. 

  avgComparesR returns pathCompares() divided by size(). pathCompares()
  returns 0 if root == null else recursively computes the sum of all path 
  lengths inclusively from root to every other node that is done by 
  summing each node's level+1 over all nodes (but not using Node.level 
  that was introduced for avgCompares.) It's recursive structure is similar
  to another method named printLevels(). 
  
    public double avgComparesR() { 
        if (root == null) return 0;
        return pathCompares()*1./size(); 
    }
    
    private long pathCompares() {
      long c = 0;
      for (int i = 1; i < height()+2; i++) 
        c += pathCompares(root, i)*i;
      return c;
    }
    
    private long pathCompares(Node node, int level) {
      if (node == null || level < 1) return 0;
      long c = 0, d = 0;
      if (level == 1) return 1; 
      c = pathCompares(node.left, level - 1);
      d = pathCompares(node.right, level - 1);
      return c + d;
    }
    
  Yes it would have been slightly more efficient to define level as 
  the current level+1 in the first place, but plain level is the more
  fundamental quantity and could have other uses in the future.  

  The level field was originally added to Node for avgCompares() along 
  with a Node constructor intializing it that's used in 
  put(Node, Key, Value, long), where the long argument is the level used
  in that constructor to properly create a new Node. A global long field 
  named pathCompares representing the sum of the number of compares 
  required by search (i.e. get()) to find each node from root was added 
  to the BSTX class. put(), delete(), deleteMin() and deleteMax() were 
  modified to update the pathCompares field on node addition or removal
  and syncronized with adjustments to Node levels by the delete methods 
  with decrementLevels(). avgCompares() simply returns pathCompares 
  divided by size() so it appears to take constant time, however there 
  must be and is overhead to maintain pathCompares as mentioned.
  
    public double avgCompares() { 
      if (root == null) return 0;
      return pathCompares*1./size(); 
    }

  Additionally, I added avgCompares() as defined at
  
    https://github.com/nagajyothi/AlgorithmsByRobertSedgewick/blob/master/Searching/BinarySearchTrees/BST.java
  
  that is:
  
    public double avgCompares(){
      return length(root)/1.0/size() + 1;
    }
      
  with the modification that when the BST is empty it returns 0 and renamed
  it avgCompares2 as follows: 
       
    public double avgCompares2() {
      if (root == null) return 0;
      return length(root)/1.0/size() + 1;
    }
    
  and where length for a Node x is defined as follows in put:
  
      x.length = length(x.left) + length(x.right) + size(x) - 1;
    
  It's included in the tests below along with avgCompares and avgComparesR.

 */

public class Ex3207BSTavgComparesRecursiveAndIterative {

  public static void main(String[] args) {

    SecureRandom r = new SecureRandom();
    int n = 1001;
    Integer[] a = rangeInteger(0,n); Integer[] b = a.clone(); 
    shuffle(a,r); 
    BSTX<Integer,Integer> bst = new BSTX<>();

    for (int j = 0; j < a.length; j++) {
      bst.put(a[j], b[j]);
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);
    }

    while (!bst.isEmpty()) {
      bst.deleteMax();
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);
    }

    bst = new BSTX<>(a,b);

    assert bst.avgCompares() == bst.avgComparesR();
    assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);

    assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);

    while (!bst.isEmpty()) {
      bst.deleteMin();
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14); 
    }

    bst = new BSTX<>(a,b);
    shuffle(b,r);

    for (Integer i : b) {
      bst.delete(i);
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);
    }

    bst = new BSTX<>(a,b);
    shuffle(b,r);

    while (!bst.isEmpty()) {
      bst.deleteRandom();
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);  
    }

    bst = new BSTX<>(a,b);
    shuffle(b,r);

    while (!bst.isEmpty()) {
      int x = r.nextInt(4);
      switch (x) {
        case 0: bst.deleteMax(); break;
        case 1: bst.deleteMin(); break;
        case 2: bst.delete(bst.toInOrderArray()[r.nextInt(bst.size())]); break;
        case 3: bst.deleteRandom();
      }
      assert bst.avgCompares() == bst.avgComparesR();
      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);
    }

  }

}

