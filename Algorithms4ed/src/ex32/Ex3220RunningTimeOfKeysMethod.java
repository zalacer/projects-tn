package ex32;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.BSTX;

/* p418
  3.2.20  Prove that the running time of the two-argument keys() in a BST 
  with N nodes is at most proportional to the tree height plus the number 
  of keys in the range.

   keys(Key,Key) adds constant overhead before calling 
   keys(Node,Queue<Key>,Key,Key) and then returns a Queue, so its 
   run time order of growth is determined by the latter.
   Here is the code for keys(Node,Queue<Key>,Key,Key):

        private void keys(Node x, Queue<Key> queue, Key lo, Key hi) { 
   1      if (x == null) return; 
   2      int cmplo = lo.compareTo(x.key); compares++;
   3      int cmphi = hi.compareTo(x.key); compares++; 
   4      if (cmplo < 0) keys(x.left, queue, lo, hi); 
   5      if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key); 
   6      if (cmphi > 0) keys(x.right, queue, lo, hi); 
        } 

   Lines 2-4 and 6 zone in on the range of interest that can have a width of at
   most the tree height. For each node found with a key in the range line 5 
   executes. In total the worst case must be at most height + number of keys in 
   the range.

   Another way of looking at it is to suppose the range is one key, then the
   cost is 2*(level(key)+1) using level+1 as a measure of depth since it starts
   at 0 and it's multiplied by two because there are two compares (on lines 2-3)
   for each recursion.  This formula is demonstrated below. When the level is
   at its max then level+1 is the height of the tree. Then to get n keys an
   upper bound cost for a given BST and range is:
   
   Iterator<Key> = keys(lo,hi).iterator();
   while (it.hasNext()) {
     Key k = it.next; 
     sum += 2*(getLevel(k)+1);
   }
   
   At worst this formula is ~ treeHeight * N assuming that all keys in the range 
   are at the max level.
   
   For a given tree and range it's an upper bound since for an individual key 
   singlet (keys(x,x)) search starts at root while keys() takes advantage of 
   proximity of keys within the range and never regresses to root. The efficiency 
   of keys() can be better by at least almost a factor of 4 compared to running it 
   on singlets for all keys in the range as shown below.
   
 */             

@SuppressWarnings("unused")
public class Ex3220RunningTimeOfKeysMethod {

  public static void main(String[] args) {

    Integer[] u = {3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    Integer[] v = rangeInteger(0,u.length);
    BSTX<Integer, Integer> bst = new BSTX<>(u,v);
    System.out.println("maxLevel="+bst.getMaxLevel()); // 6
    System.out.println("bst");bst.printTree(); System.out.println();
    /*    
    bst
    |                     /-----20
    |              /-----15
    |             |       \-----14
    |       /-----13
    |      |      |              /-----12
    |      |      |       /-----11
    |      |      |      |       \-----10
    |      |       \-----9
    |      |             |       /-----8
    |      |             |      |       \-----7
    |      |             |      |              \-----6
    |      |              \-----5
    |      |                     \-----4
     \-----3
            \-----2
                   \-----1

     */
    
    long sum = 0;
    for (int i = 0; i < u.length; i++) {
      bst.setCompares(0);
      Iterable<Integer> ir = bst.keys(u[i],u[i]);
      //System.out.println(u[i]+" "+bst.getCompares()+" "+bst.getLevel(u[i]));
      sum += 2*(bst.getLevel(u[i])+1);
      assert bst.getCompares() == 2*(bst.getLevel(u[i])+1);
    }

    bst.setCompares(0); long keysCompares = 0;    
    Iterable<Integer> ir = bst.keys(bst.min(),bst.max());
    keysCompares = bst.getCompares();
    
    System.out.println("sum="+sum+" keysCompares="+keysCompares);
    // sum=126 keysCompares=32
  }


}

