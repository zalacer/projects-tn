package ex32;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.BSTX;

/* p418
  3.2.21  Add a BST method  randomKey() that returns a random key from the 
  symbol table in time proportional to the tree height, in the worst case.
  
  This is implemented in st.BSTX.  Here's the method where rnd is defined globally as
     
     private SecureRandom rnd = new SecureRandom();
  
     public Key getRandomKey() {
      if (root == null) throw new NoSuchElementException();
      rnd.setSeed(System.currentTimeMillis());
      int r = rnd.nextInt(height()+1), t, c = 0;
      Node x = root;
      while (c < r) {
        t = rnd.nextInt(size(x.left)+size(x.right)+1);
        if (t > size(x.left)) {
          x = x.right;
        } else {
          if (x.left != null) x = x.left;
        }
        c++;
      }
      return x.key;
    }
    
  This method is reasonably fast and covers all keys but not uniformly.
     
  Another method that I coded previously is uniform but takes time ~ N:
     
    public Key getUniformRandomKey() {
      if (root == null) throw new NoSuchElementException();
      rnd.setSeed(System.currentTimeMillis());
      int r = rnd.nextInt(size()), c = 0; Key k;
      Iterator<Key> it = inOrder().iterator();
      while (it.hasNext()) { k = it.next(); if (c++ == r) return k;}
      return null;
    }
    
    Both methods are demonstrated below.
  
 */             

public class Ex3221BSTRandomKeyMethod {

  public static void main(String[] args) {
    
    Integer[] u = {3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    Integer[] v = rangeInteger(0,u.length);
    BSTX<Integer, Integer> bst = new BSTX<>(u,v);
    System.out.println("bst"); bst.printTree(); System.out.println("\n");
            
    BSTX<Integer,Integer> st = new BSTX<>();    
    int c = 0;
    while (c < 10001) {
      int x = bst.getRandomKey();
      if (st.contains(x)) st.put(x,st.get(x)+1); // accumulate frequencies in st
      else st.put(x,1);
      c++;
    }

    BSTX<Integer,Integer> st2 = new BSTX<>();     
    c = 0;
    while (c < 10001) {
      int x = bst.getUniformRandomKey();
      if (st2.contains(x)) st2.put(x,st2.get(x)+1);
      else st2.put(x,1);
      c++;
    }
    
    Iterator<Integer> it = bst.inOrder().iterator();
    System.out.println("Comparison of freqency distributions of getRandomKey() vs. getUniformRandomKey():");
    System.out.println("key  getRandomKey() frequency  getUniformRandomKey() frequency");
    System.out.println("---  ------------------------  -------------------------------");
    while (it.hasNext()) {
      Integer x = it.next();
      System.out.printf("%-2d   %-4d                      %-4d\n", x, st.get(x), st2.get(x));      
    }
    
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
    
    
    Comparison of freqency distributions of getRandomKey() vs. getUniformRandomKey():
    key  getRandomKey() frequency  getUniformRandomKey() frequency
    ---  ------------------------  -------------------------------
    1    1346                      589 
    2    267                       639 
    3    1425                      631 
    4    729                       630 
    5    559                       628 
    6    355                       636 
    7    346                       594 
    8    395                       622 
    9    913                       614 
    10   601                       628 
    11   310                       637 
    12   311                       625 
    13   1137                      670 
    14   709                       600 
    15   242                       644 
    20   356                       614 

*/

    }

}

