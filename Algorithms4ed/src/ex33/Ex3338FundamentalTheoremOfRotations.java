package ex33;

import static st.BSTX.transform;
import static v.ArrayUtils.par;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.security.SecureRandom;

import st.BSTX;

/* p452
  3.3.38 Fundamental theorem of rotations. Show that any BST can be 
  transformed into any other BST on the same set of keys by a sequence 
  of left and right rotations.
  
  Say there are two BSTs with the same set of keys and they are named bst1
  and bst2. Both can be flattened to a right spine using a sequence of
  right rotations. bst1 can be transformed to bst2 by flattening bst1 to
  a right spine and applying to that the inverse of the sequence of 
  rotations used to flatten bst2 to a right spine. The inverse of a sequence 
  of rotations means that their order is reversed and each rotation is inverted. 
  If this is confusing and some of the terminology isn't familiar, then in more
  detail:
  
  1. A left rotation is the reverse or inverse of a right rotation.
     In particular if node = rotateLeft(node), then node = rotateRight(node)
     restores the tree to it's original configuration, provided links from
     node's parents to it are set correctly each time, and likewise upon 
     switching left and right.
     
  2. A spine of a binary tree is the maximal path from root that consists 
     only of left edges or only of right edges (from
     https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-046j-introduction-to-algorithms-sma-5503-fall-2005/assignments/ps4sol.pdf
     that's available in this project at BinaryTreeSpineDefinition-ps4sol-mit.edu.pdf.)      
     This implies that a tree has two spines, a left spine and a right spine and one
     may be null or both can be null if the tree has one or no nodes. Practically
     speaking a spine is the ordered sequence of nodes along a spine path.
     
  3. Flattening a binary tree means can mean tranforming it so that all its
     nodes are in its left or right spine. While there are easy ways to do 
     this efficiently, it can be done purely with rotations as demonstrated 
     in BSTX.flattenLeft() and BSTX.flattenRight. (Two easier ways to do this
     are BSTX.flattenRightPreOrdered() and BSTX.flattenRightPreOrdered2(),
     however usings rotations makes it especially simple to reverse the
     the flattening as demonstrated in BSTX.unflatten(Key[])).
     
  4. A use for all this is to convert a BST to another provided they have the
     same key sets. Given two BSTs, bst1 and bst2, with the same key sets, 
     a procedure for transforming bst1 to bst2 is:
     a. Make a copy of bst2 named bst3. That can be done easily by using 
        the constructor in BSTX that takes another BSTX.
     b. Convert bst3 to a right spine using BSTX.flatten() that returns a Key[]
        of all the keys of nodes returned from rotations done during flattening 
        in reverse order.
     c. Convert bst1 to a right spine using BSTX.flatten().
     d. Unflatten bst1 using BSTX.unFlatten(Key[]) with the Key[] returned from
        flattening bst3.
     e. Now bst1 is structurally the same as bst2 that can be shown by comparing
        their preordered keys or using BSTX.equals().
     This procedure is encapsulated in the static method BSTX.transform(BSTX,BSTX)
     and is demonstrated below.
     
  5. To enable rotations in BSTX, rotateLeft() and rotateRight() from 
     http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/AVLTreeST.java
     were added to it and modified to update Node levels. These rotation methods are 
     essentially the same as those in http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.  
     AVLTreeST.java and RedBlackBST.java are in this project in the st package.
     
 */             

public class Ex3338FundamentalTheoremOfRotations {

  public static void main(String[] args) {
    
    SecureRandom r = new SecureRandom();
    Integer[] a = rangeInteger(1,10);
    shuffle(a,r);
    System.out.print("input array of keys for st = "); par(a);
    BSTX<Integer,Integer> st = new BSTX<>(a,rangeInteger(0,a.length));
    System.out.println("st"); st.printTree(); System.out.println();
    
    System.out.print("input array of keys for st2 = ");par(a);
    BSTX<Integer,Integer> st2 = new BSTX<>(a,rangeInteger(0,a.length));
    assert st2.equals(st);
    System.out.println("st2 (is the same as st)"); st.printTree(); System.out.println();
    
    System.out.println("demonstration of flattening st2 to a right spine:");
    Integer[] ka = st2.flatten();
    System.out.println("st2 after flattening"); st2.printTree(); System.out.println();

    System.out.print("reverse ordered keys gathered when flattening st2 = "); par(ka); // [3,1]
    
    System.out.println("\ndemonstration of unflattening st2 from a right spine "
        +"using the keys gathered when flattening it:");
    st2.unFlatten(ka);
    System.out.println("unflattened st2"); st2.printTree(); System.out.println();
    assert st2.equals(st);  
    System.out.println("unflattened st2 == st\n");
 
    System.out.println("demonstration of flattening a different BST and then flattening it "
        + "with the keys gathered while flattening bst2:");
    shuffle(a,r);
    System.out.print("input array of keys for st3 = "); par(a);
    BSTX<Integer,Integer> st3 = new BSTX<>(a,rangeInteger(0,a.length));
    System.out.println("st3"); st3.printTree(); System.out.println();
    st3.flatten();
    st3.unFlatten(ka);
    System.out.println("unflattened st3"); st3.printTree(); System.out.println();
    assert st3.equals(st);
    System.out.println("unflattened st3 == st\n");
    
    System.out.println("demonstration of BSTX.transform() on a 4th BST with the same "
        +"keys as but different structure than the first:");
    shuffle(a,r);
    System.out.print("input array of keys for st4 = "); par(a);
    BSTX<Integer,Integer> st4 = new BSTX<>(a,rangeInteger(0,a.length));
    System.out.println("st4"); st4.printTree(); System.out.println();
    transform(st4, st);
    System.out.println("st4 after transformation:"); st4.printTree(); System.out.println();
    assert st4.equals(st);
    System.out.println("transformed st4 == st");
 
  }

}

