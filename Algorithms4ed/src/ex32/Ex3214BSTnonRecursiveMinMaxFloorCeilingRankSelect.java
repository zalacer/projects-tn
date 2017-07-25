package ex32;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Iterator;

import st.BSTXI;

/* p418
  3.2.14  Give nonrecursive implementations of min(), max(), 
  floor(), ceiling(), rank() and select().

  These methods are implemented in st.BSTXI as follows:

  public Key min() {
    if (root == null) throw new NoSuchElementException();
    return min(root).key;
  }

  private Node min(Node x) {
    if (x == null) return null;
    while(x.left != null) x = x.left;
    return x;
  }

  public Key max() {
    if (root == null) throw new NoSuchElementException();
    return max(root).key;
  } 

  private Node max(Node x) {
    if (x == null) return null;
    while(x.right != null) x = x.right;
    return x;
  }

  public Key floor(Key key) {
    // iteratively returns largest key <= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Iterator<Key> it = reverseInOrder().iterator();
    Key x;
    while (it.hasNext()) {
      x = it.next(); 
      int cmp = key.compareTo(x); compares++;
      if (cmp >= 0) return x;
    }
    return null;
  }

  public Key ceiling(Key key) {
    // iteratively returns smallest key >= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Iterator<Key> it = keys().iterator();
    Key x;
    while (it.hasNext()) {
      x = it.next(); 
      int cmp = key.compareTo(x); compares++;
      if (cmp <= 0) return x;;
    }
    return null;
  }

  public int rank(Key key) {
    // iteratively return the number of keys < key
    Iterator<Key> it = inOrder().iterator(); int c = 0;
    while (it.hasNext()) {
      if (key.compareTo(it.next()) <= 0) return c;
      c++;
    }
    return c;
  }

  public Key select(int m) {
    // iteratively return kth smallest key (key of rank k)
    if (m < 0 || m >= size()) throw new IllegalArgumentException();
    Iterator<Key> it = inOrder().iterator(); int c = 0; Key k;
    while (it.hasNext()) {
      k = it.next();
      if (c == m) return k;
      c++;
    }
    return null;
  }
  
  The recursive equivilants of the methods above are named minR, maxR, floorR, ceilingR,
  rankR and selectR in st.BSTXI. Tests comparing these methods with their recurive
  equivilants are provided below.

 */

public class Ex3214BSTnonRecursiveMinMaxFloorCeilingRankSelect {

  public static void main(String[] args) {

    Integer[] u = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    Integer[] v = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    Iterator<Integer> it; int p;
    BSTXI<Integer, Integer> bst = new BSTXI<>(u,v);

    // testing min() vs. minR()
    while (!bst.isEmpty()) {
      assert bst.min() == bst.minR();
      bst.deleteMin();
    }

    // testing max() vs. maxR()
    bst = new BSTXI<>(u,v);
    while (!bst.isEmpty()) {
      assert bst.max() == bst.maxR();
      bst.deleteMax();
    }

    // testing keys(), keysR()
    bst = new BSTXI<>(u,v);
    Integer[] a1 = new Integer[bst.size()], a2 = a1.clone();  
    Iterator<Integer> it1 = bst.keys().iterator(); p = 0;
    while (it1.hasNext()) a1[p++] = it1.next();
    Iterator<Integer> it2 = bst.keysR().iterator(); p = 0;
    while (it2.hasNext()) a2[p++] = it2.next();
    assert Arrays.equals(a1, a2);

    // testing floor() vs. floorR()
    it = bst.keys().iterator(); Integer x;
    while (it.hasNext()) {
      x = it.next();
      assert bst.floor(x) == bst.floorR(x);
    }
    int[] ax = range(16,20);
    for (int i = 0; i < ax.length; i++) {
      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
      assert bst.floor(ax[i]) == 15;
    }
    ax = range(21,26);
    for (int i = 0; i < ax.length; i++) {
      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
      assert bst.floor(ax[i]) == 20;
    }
    ax = range(-9,1);
    for (int i = 0; i < ax.length; i++) {
      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
      assert bst.floor(ax[i]) == null;
    }

    // test ceiling() vs. ceilingR()
    it = bst.keys().iterator(); Integer y;
    while (it.hasNext()) {
      y = it.next();
      assert bst.ceiling(y) == bst.ceilingR(y);
    }
    int[] ay = range(16,20);
    for (int i = 0; i < ay.length; i++) {
      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
      assert bst.ceiling(ay[i]) == 20;
    }
    ay = range(21,26);
    for (int i = 0; i < ay.length; i++) {
      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
      assert bst.ceiling(ay[i]) == null;
    }
    ay = range(-9,1);
    for (int i = 0; i < ay.length; i++) {
      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
      assert bst.ceiling(ay[i]) == 1;
    }    

    // testing rank() vs. rankR()
    it = bst.keys().iterator(); Integer z;
    while (it.hasNext()) {
      z = it.next();
      assert bst.rank(z) == bst.rankR(z);
    }

    // testing select() vs. selectR()
    for (int i = 0; i < bst.size(); i++) {
      assert bst.select(i) == bst.selectR(i);
    }

    // testing select() and rank()
    it = bst.keys().iterator(); Integer w;
    while (it.hasNext()) {
      w = it.next();
      assert w.equals(bst.select(bst.rank(w)));
    }

  }

}

