package st;

import static analysis.Log.lg;
import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ds.BagQ;
//import ds.Queue;
import exceptions.InvalidDataException;
import v.Tuple2;
import v.Tuple3;

//based on RedBlackBSTM to allow duplicate keys for ex3510 and retaining
//all values for each key in a separate Bag.
//for ex3521.

public class RedBlackBSTB<Key extends Comparable<Key>, Value> {
  // from http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java
  private static final boolean RED = true;
  private static final boolean BLACK = false;
  private Node root; // root of the BST
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = null; // Value class

  private class Node {
    private Key key; // key
    private BagQ<Value> b; // bag to contain all values
    private int c; // count of Values for key == #Keys == k, c == q.size()
    private Node left, right; // links to left and right subtrees
    private boolean color; // color of parent link
    private int size; // subtree count
    private int dSize; // number of nodes in subtree including duplicates

    public Node(Key key, BagQ<Value> b, boolean color, int c, int size, int dSize) {
      this.key = key;
      this.b = b;
      this.color = color;
      this.c = c;
      this.size = size;
      this.dSize = dSize;
    }

    public void printNode(boolean isRight, String indent, int... dash) {
      if (right != null) {
        right.printNode(true, indent + (isRight ? "       " : "|      "));
      }
      System.out.print(indent);
      if (isRight) {
        System.out.print(" /");
      } else {
        System.out.print(" \\");
      }
      String dashes = dash == null || dash.length == 0 ? repeat('-', 5) : repeat('-', dash[0]);
      System.out.print(dashes);
      printNodeKey();
      if (left != null) {
        left.printNode(false, indent + (isRight ? "|      " : "       "), dash);
      }
    }

    private void printNodeKey() {
      if (key == null) {
        System.out.print("<null>");
      } else {
        if (color)
          System.out.print(key.toString() + "(red)");
        else
          System.out.print(key.toString());
      }
      System.out.println();
    }

    @Override
    public String toString() {
      return "(" + key + "," + arrayToString(b.toArray(), 90, 1, 1) + "," + c + "," + size + "," + dSize + ")";
    }
  }

  public RedBlackBSTB() {
  }

  public RedBlackBSTB(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0)
      return;
    int n = Math.min(ka.length, va.length);
    int c = 0;
    Tuple2<Key, Value> ta[] = ofDim(Tuple2.class, n);
    for (int i = 0; i < n; i++)
      if (ka[c] != null && va[c] != null)
        ta[c] = new Tuple2<Key, Value>(ka[c], va[c++]);
    if (c == 0)
      return;
    ta = take(ta, c);
    n = ta.length;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    Value[] vz = ofDim(vclass, n);
    c = 0;
    for (int i = 0; i < n; i++) {
      kz[i] = ta[i]._1;
      vz[i] = ta[i]._2;
    }
    for (int i = 0; i < n; i++)
      put(kz[i], vz[i]);
  }

  // is node x red; false if x is null ?
  private boolean isRed(Node x) {
    if (x == null)
      return false;
    return x.color == RED;
  }

  public int size() {
    if (root == null)
      return 0;
    return root.size;
  }

  public int dSize() {
    if (root == null)
      return 0;
    return root.dSize;
  }

  public int size(Node x) {
    if (x == null)
      return 0;
    return x.size;
  }

  public int dSize(Node x) {
    if (x == null)
      return 0;
    else
      return x.dSize;
  }

  public int size(Key lo, Key hi) {
    if (lo == null)
      throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null)
      throw new IllegalArgumentException("second argument to size() is null");
    if (lo.compareTo(hi) > 0)
      return 0;
    if (contains(hi))
      return rank(hi) - rank(lo) + 1;
    else
      return rank(hi) - rank(lo);
  }

  public int dSize(Key lo, Key hi) {
    // return number of keys between lo and hi inclusive
    if (lo == null)
      throw new IllegalArgumentException("first argument to dSize() is null");
    if (hi == null)
      throw new IllegalArgumentException("second argument to dSize() is null");
    if (lo.compareTo(hi) > 0)
      return 0;
    if (contains(hi))
      return dRank(hi) - dRank(lo) + 1;
    else
      return dRank(hi) - dRank(lo);
  }

  public boolean isEmpty() {
    return root == null;
  }

  public Value get(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to get() is null");
    BagQ<Value> q = get(root, key);
    return q == null || q.isEmpty() ? null : q.peek();
  }

  public Value[] getValues(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to get() is null");
    BagQ<Value> q = get(root, key);
    if (q == null)
      return null;
    if (vclass != null)
      return q.toArray();
    return (Value[]) q.toArray();
  }

  public BagQ<Value> getBag(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to get() is null");
    return get(root, key);
  }

  private BagQ<Value> get(Node x, Key key) {
    while (x != null) {
      int cmp = key.compareTo(x.key);
      if (cmp < 0)
        x = x.left;
      else if (cmp > 0)
        x = x.right;
      else
        return x.b;
    }
    return null;
  }

  public int getKeyCount(Key key) {
    Node node = getNode(root, key);
    return node == null ? 0 : node.c;
  }

  public Node getNode(Key key) {
    return getNode(root, key);
  }

  private Node getNode(Node x, Key key) {
    if (root == null || x == null || key == null)
      return null;
    while (x != null) {
      int cmp = key.compareTo(x.key);
      if (cmp < 0)
        x = x.left;
      else if (cmp > 0)
        x = x.right;
      else
        return x;
    }
    return null;
  }

  public boolean contains(Key key) {
    return get(key) != null;
  }

  public void put(Key key, Value val) {
    if (key == null)
      throw new IllegalArgumentException("first argument to put() is null");
    if (kclass == null)
      kclass = key.getClass();
    if (val == null) {
      delete(key);
      return;
    }
    if (vclass == null)
      vclass = val.getClass();
    root = put(root, key, val);
    root.color = BLACK;
    assert check();
  }

  // insert the key-value pair in the subtree rooted at h
  private Node put(Node h, Key key, Value val) {
    if (h == null) {
      Node nd = new Node(key, new BagQ<Value>(), RED, 1, 1, 1);
      nd.b.add(val);
      return nd;
    }

    int cmp = key.compareTo(h.key);
    if (cmp < 0)
      h.left = put(h.left, key, val);
    else if (cmp > 0)
      h.right = put(h.right, key, val);
    else {
      h.b.add(val);
      h.c++;
    }

    // fix-up any right-leaning links
    if (isRed(h.right) && !isRed(h.left))
      h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left))
      h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right))
      flipColors(h);
    h.size = size(h.left) + size(h.right) + 1;
    h.dSize = dSize(h.left) + dSize(h.right) + h.c;

    return h;
  }

  public void deleteMin() {
    if (isEmpty())
      throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMin(root);
    if (!isEmpty())
      root.color = BLACK;
    assert check();
  }

  // delete the key-value pair with the minimum key rooted at h
  private Node deleteMin(Node h) {
    if (h.left == null)
      return null;

    if (!isRed(h.left) && !isRed(h.left.left))
      h = moveRedLeft(h);

    h.left = deleteMin(h.left);
    return balance(h);
  }

  public void deleteMax() {
    if (isEmpty())
      throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMax(root);
    if (!isEmpty())
      root.color = BLACK;
    assert check();
  }

  // delete the key-value pair with the maximum key rooted at h
  private Node deleteMax(Node h) {
    if (isRed(h.left))
      h = rotateRight(h);

    if (h.right == null)
      return null;

    if (!isRed(h.right) && !isRed(h.right.left))
      h = moveRedRight(h);

    h.right = deleteMax(h.right);

    return balance(h);
  }

  public void delete(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to delete() is null");
    if (!contains(key))
      return;

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = delete(root, key);
    if (!isEmpty())
      root.color = BLACK;
    assert check();
  }

  // delete the key-value pair with the given key rooted at h
  private Node delete(Node h, Key key) {
    // assert get(h, key) != null;

    if (key.compareTo(h.key) < 0) {
      if (!isRed(h.left) && !isRed(h.left.left))
        h = moveRedLeft(h);
      h.left = delete(h.left, key);
    } else {
      if (isRed(h.left))
        h = rotateRight(h);
      if (key.compareTo(h.key) == 0 && (h.right == null))
        return null;
      if (!isRed(h.right) && !isRed(h.right.left))
        h = moveRedRight(h);
      if (key.compareTo(h.key) == 0) {
        Node x = min(h.right);
        h.key = x.key;
        h.b = x.b;
        // h.val = get(h.right, min(h.right).key);
        // h.key = min(h.right).key;
        h.right = deleteMin(h.right);
      } else
        h.right = delete(h.right, key);
    }
    return balance(h);
  }

  // make a left-leaning link lean to the right
  private Node rotateRight(Node h) {
    // assert (h != null) && isRed(h.left);
    Node x = h.left;
    h.left = x.right;
    x.right = h;
    x.color = x.right.color;
    x.right.color = RED;
    x.size = h.size;
    x.dSize = h.dSize;
    h.size = size(h.left) + size(h.right) + 1;
    h.dSize = dSize(h.left) + dSize(h.right) + h.c;
    return x;
  }

  // make a right-leaning link lean to the left
  private Node rotateLeft(Node h) {
    // assert (h != null) && isRed(h.right);
    Node x = h.right;
    h.right = x.left;
    x.left = h;
    x.color = x.left.color;
    x.left.color = RED;
    x.size = h.size;
    x.dSize = h.dSize;
    h.size = size(h.left) + size(h.right) + 1;
    h.dSize = dSize(h.left) + dSize(h.right) + h.c;
    return x;
  }

  // flip the colors of a node and its two children
  private void flipColors(Node h) {
    // h must have opposite color of its two children
    // assert (h != null) && (h.left != null) && (h.right != null);
    // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
    //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
    h.color = !h.color;
    h.left.color = !h.left.color;
    h.right.color = !h.right.color;
  }

  // Assuming that h is red and both h.left and h.left.left
  // are black, make h.left or one of its children red.
  private Node moveRedLeft(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

    flipColors(h);
    if (isRed(h.right.left)) {
      h.right = rotateRight(h.right);
      h = rotateLeft(h);
      flipColors(h);
    }
    return h;
  }

  // Assuming that h is red and both h.right and h.right.left
  // are black, make h.right or one of its children red.
  private Node moveRedRight(Node h) {
    // assert (h != null);
    // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
    flipColors(h);
    if (isRed(h.left.left)) {
      h = rotateRight(h);
      flipColors(h);
    }
    return h;
  }

  // restore red-black tree invariant
  private Node balance(Node h) {
    // assert (h != null);

    if (isRed(h.right))
      h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left))
      h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right))
      flipColors(h);

    h.size = size(h.left) + size(h.right) + 1;
    h.dSize = dSize(h.left) + dSize(h.right) + h.c;
    return h;
  }

  public int height() {
    return height(root);
  }

  private int height(Node x) {
    if (x == null)
      return -1;
    return 1 + Math.max(height(x.left), height(x.right));
  }

  public Key min() {
    if (isEmpty())
      throw new NoSuchElementException("called min() with empty symbol table");
    return min(root).key;
  }

  // the smallest key in subtree rooted at x; null if no such key
  private Node min(Node x) {
    // assert x != null;
    if (x.left == null)
      return x;
    else
      return min(x.left);
  }

  public Key max() {
    if (isEmpty())
      throw new NoSuchElementException("called max() with empty symbol table");
    return max(root).key;
  }

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) {
    // assert x != null;
    if (x.right == null)
      return x;
    else
      return max(x.right);
  }

  public Key floor(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to floor() is null");
    if (isEmpty())
      throw new NoSuchElementException("called floor() with empty symbol table");
    Node x = floor(root, key);
    if (x == null)
      return null;
    else
      return x.key;
  }

  // the largest key in the subtree rooted at x less than or equal to the given key
  private Node floor(Node x, Key key) {
    if (x == null)
      return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0)
      return x;
    if (cmp < 0)
      return floor(x.left, key);
    Node t = floor(x.right, key);
    if (t != null)
      return t;
    else
      return x;
  }

  public Key ceiling(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to ceiling() is null");
    if (isEmpty())
      throw new NoSuchElementException("called ceiling() with empty symbol table");
    Node x = ceiling(root, key);
    if (x == null)
      return null;
    else
      return x.key;
  }

  // the smallest key in the subtree rooted at x greater than or equal to the given key
  private Node ceiling(Node x, Key key) {
    if (x == null)
      return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0)
      return x;
    if (cmp > 0)
      return ceiling(x.right, key);
    Node t = ceiling(x.left, key);
    if (t != null)
      return t;
    else
      return x;
  }

  public Key select(int k) {
    if (k < 0 || k >= size())
      throw new IllegalArgumentException();
    Node x = select(root, k);
    return x.key;
  }

  // the key of rank k in the subtree rooted at x
  private Node select(Node x, int k) {
    // assert x != null;
    // assert k >= 0 && k < size(x);
    int t = size(x.left);
    if (t > k)
      return select(x.left, k);
    else if (t < k)
      return select(x.right, k - t - 1);
    else
      return x;
  }

  public Key dSelect(int k) {
    if (k < 0 || k >= dSize())
      throw new IllegalArgumentException();
    Node x = dSelect(root, k);
    return x.key;
  }

  private Node dSelect(Node x, int k) {
    if (x == null)
      return null;
    int t = dSize(x.left);
    if (k > t && k < t + x.c)
      return x;
    else if (t > k)
      return dSelect(x.left, k);
    else if (t < k)
      return dSelect(x.right, k - t - x.c);
    else
      return x;
  }

  public int rank(Key key) {
    if (key == null)
      throw new IllegalArgumentException("argument to rank() is null");
    return rank(key, root);
  }

  // number of keys less than key in the subtree rooted at x
  private int rank(Key key, Node x) {
    if (x == null)
      return 0;
    int cmp = key.compareTo(x.key);
    if (cmp < 0)
      return rank(key, x.left);
    else if (cmp > 0)
      return 1 + size(x.left) + rank(key, x.right);
    else
      return size(x.left);
  }

  public int dRank(Key key) {
    // return number of keys < key
    if (key == null)
      throw new IllegalArgumentException("argument to dRank() is null");
    return dRank(key, root);
  }

  private int dRank(Key key, Node x) {
    if (x == null)
      return 0;
    int cmp = key.compareTo(x.key);
    if (cmp < 0)
      return dRank(key, x.left);
    else if (cmp > 0)
      return x.c + dSize(x.left) + dRank(key, x.right);
    else
      return dSize(x.left);
  }

  public Iterable<Key> keys() {
    if (isEmpty())
      return new BagQ<Key>();
    return keys(min(), max());
  }

  public Iterable<Key> keys(Key lo, Key hi) {
    if (lo == null)
      throw new IllegalArgumentException("1st argument to keys() is null");
    if (hi == null)
      throw new IllegalArgumentException("2st argument to keys() is null");

    BagQ<Key> queue = new BagQ<Key>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    keys(root, queue, lo, hi);
    return queue;
  }
  
  public Tuple2<Key,Value>[] rangeLookup(Key a, Key b) {
    if (a == null) throw new IllegalArgumentException("1st argument to rangeLookup() is null");
    if (b == null) throw new IllegalArgumentException("2nd argument to rangeLookup() is null");
    if (kclass == null || vclass == null) return ofDim(Tuple2.class,0);
    Iterable<Key> it = null;
    int comp = a.compareTo(b);
    if (comp <= 0) it = keys(a,b);
    else it = keys(b,a);
    Key[] ka = toArray(it.iterator());
    BagQ<Tuple2<Key,Value>> q = new BagQ<>();
    for (int i = 0; i < ka.length; i++) {
      Key k = ka[i];
      Node n = getNode(k);
      Value[] va = n.b.toArray();
      for (int j = 0; j < va.length; j++) {
        q.add(new Tuple2<Key,Value>(k,va[j]));
      }
    }
    return q.toArray();
  }

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void keys(Node x, BagQ<Key> queue, Key lo, Key hi) {
    if (x == null)
      return;
    int cmplo = lo.compareTo(x.key);
    int cmphi = hi.compareTo(x.key);
    if (cmplo < 0)
      keys(x.left, queue, lo, hi);
    if (cmplo <= 0 && cmphi >= 0)
      queue.add(x.key);
    if (cmphi > 0)
      keys(x.right, queue, lo, hi);
  }

  public Iterable<Key> allKeys() {
    // inorder traversal including virtual duplicates
    return allKeys(min(), max());
  }

  public Iterable<Key> allKeys(Key lo, Key hi) {
    // inorder traversal including virtual duplicates
    if (lo == null)
      throw new IllegalArgumentException();
    if (hi == null)
      throw new IllegalArgumentException();
    BagQ<Key> queue = new BagQ<Key>();
    allKeys(root, queue, lo, hi);
    return queue;
  }

  private void allKeys(Node x, BagQ<Key> queue, Key lo, Key hi) {
    //  including virtual duplicates
    if (x == null)
      return;
    int cmplo = lo.compareTo(x.key);
    int cmphi = hi.compareTo(x.key);
    if (cmplo < 0)
      allKeys(x.left, queue, lo, hi);
    if (cmplo <= 0 && cmphi >= 0)
      for (int i = 0; i < x.c; i++)
        queue.add(x.key);
    if (cmphi > 0)
      allKeys(x.right, queue, lo, hi);
  }

  public boolean check() {
    if (!isBST())
      System.out.println("Not in symmetric order");
    if (!isSizeConsistent())
      System.out.println("Subtree counts not consistent");
    if (!isRankConsistent())
      System.out.println("Ranks not consistent");
    if (!isDsizeConsistent())
      System.out.println("dSizes not consistent");
    //    if (!isDrankConsistent()) System.out.println("dRanks not consistent");
    if (!is23())
      System.out.println("Not a 2-3 tree");
    if (!isBalanced())
      System.out.println("Not balanced");
    //    return isBST() && isSizeConsistent() && isRankConsistent() && is23() 
    //        && isDsizeConsistent() && isBalanced();
    return isBST() && isSizeConsistent() && isRankConsistent() && isDsizeConsistent() && isDrankConsistent() && is23()
        && isBalanced();
  }

  // does this binary tree satisfy symmetric order?
  // Note: this test also ensures that data structure is a binary tree since order is strict
  private boolean isBST() {
    return isBST(root, null, null);
  }

  // is the tree rooted at x a BST with all keys strictly between min and max
  // (if min or max is null, treat as empty constraint)
  // Credit: Bob Dondero's elegant solution
  private boolean isBST(Node x, Key min, Key max) {
    if (x == null)
      return true;
    if (min != null && x.key.compareTo(min) <= 0)
      return false;
    if (max != null && x.key.compareTo(max) >= 0)
      return false;
    return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
  }

  // are the size fields correct?
  private boolean isSizeConsistent() {
    return isSizeConsistent(root);
  }

  private boolean isSizeConsistent(Node x) {
    if (x == null)
      return true;
    if (x.size != size(x.left) + size(x.right) + 1)
      return false;
    return isSizeConsistent(x.left) && isSizeConsistent(x.right);
  }

  public boolean isDsizeConsistent() {
    return isDsizeConsistent(root);
  }

  private boolean isDsizeConsistent(Node x) {
    if (x == null)
      return true;
    if (x.dSize != dSize(x.left) + dSize(x.right) + x.c)
      return false;
    return isDsizeConsistent(x.left) && isDsizeConsistent(x.right);
  }

  // check that ranks are consistent
  private boolean isRankConsistent() {
    for (int i = 0; i < size(); i++)
      if (i != rank(select(i)))
        return false;
    for (Key key : keys())
      if (key.compareTo(select(rank(key))) != 0)
        return false;
    return true;
  }

  public boolean isDrankConsistent() {
    if (root == null)
      return true;
    for (int i = 0; i < dSize(); i++) {
      int c = getNode(dSelect(i)).c;
      for (int j = 0; j < c; j++)
        if (i != dRank(dSelect(i + j)))
          return false;
      i += c - 1;
    }
    for (Key key : keys()) {
      if (key.compareTo(dSelect(dRank(key))) != 0)
        return false;
    }
    return true;
  }

  // Does the tree have no red right links, and at most one (left)
  // red links in a row on any path?
  private boolean is23() {
    return is23(root);
  }

  private boolean is23(Node x) {
    if (x == null)
      return true;
    if (isRed(x.right))
      return false;
    if (x != root && isRed(x) && isRed(x.left))
      return false;
    return is23(x.left) && is23(x.right);
  }

  // do all paths from root to leaf have same number of black edges?
  private boolean isBalanced() {
    int black = 0; // number of black links on path from root to min
    Node x = root;
    while (x != null) {
      if (!isRed(x))
        black++;
      x = x.left;
    }
    return isBalanced(root, black);
  }

  // does every path from the root to a leaf have the given number of black links?
  private boolean isBalanced(Node x, int black) {
    if (x == null)
      return black == 0;
    if (!isRed(x))
      black--;
    return isBalanced(x.left, black) && isBalanced(x.right, black);
  }

  public void shiftRight() {
    if (size() < 2)
      return;
    shiftRight(root, null);
  }

  public void shiftRight(Node x, Node p) {
    //  System.out.println("args x="+x+" p="+p);
    //  System.out.println("st"); printTree(); System.out.println();
    //  System.out.println(this);
    //  showInOrder();
    //  if (x == p) return;
    if (x == p && x != root)
      throw new IllegalArgumentException("shiftRight: x == p but x != root");
    if (x == null || size() < 2)
      return;
    if (p == null && x != root)
      throw new IllegalArgumentException("shiftRight: p is null but x != root");
    //  System.out.println("st"); printTree(); System.out.println();
    //  System.out.println(this);
    Node n;
    int d;
    if (x == root) {
      while (root.left != null) {
        root = rotateRight(root);
      }
      //   System.out.println("root="+root);
      if (root.right == null)
        return;
      // System.out.println("st"); printTree(); System.out.println();
      // System.out.println(this);
      shiftRight(root.right, root);
    } else {
      //    System.out.println("x="+x+" p="+p);
      //    System.out.println(this);
      if (!(p.left != null && p.left.key == x.key || p.right != null && p.right.key == x.key))
        throw new IllegalArgumentException("shiftRight: p (" + p.key + ") isn't the parent of x (" + x.key + ")");
      d = p.left == x ? 0 : 1;
      n = x;
      boolean first = true;
      if (n.left != null) {
        while (n.left != null) {
          n = rotateRight(n);
          if (first) {
            d = p.left == n ? 0 : 1;
            if (d == 0)
              p.left = n;
            else
              p.right = n;
            //          System.out.println("d="+d);

            first = false;
          } else {
            //          System.out.println("x="+x+" p="+p);
            //          System.out.println(this);
            if (!(n.left != null && n.left.key == x.key || n.right != null && n.right.key == x.key))
              throw new InvalidDataException("shiftRight: n (" + n.key + ") isn't the parent of x (" + x.key + ")");
            d = n.left == n ? 0 : 1;
            if (d == 0)
              n.left = n;
            else
              n.right = n;
            //          System.out.println("d="+d);
          }

        }
      }
      if (n.right == null)
        return;
      //    System.out.println("n="+n+" n.right="+n.right+" p="+p);
      shiftRight(n.right, n);
    }
  }

  public void printTree(int... x) {
    if (root == null) {
      System.out.println("<empty tree>");
      return;
    }
    if (x == null || x.length == 0)
      root.printNode(false, "");
    else
      root.printNode(false, "", x[0]);
  }

  public void printLevels() {
    if (isEmpty())
      return;
    for (int i = 1; i < height() + 2; i++) {
      System.out.print("Level " + (i - 1) + ": ");
      String levelNodes = printLevel(root, i);
      System.out.print(levelNodes + "\n");
    }
  }

  private String printLevel(Node node, int level) {
    if (node == null)
      return "";
    if (level == 1) {
      return node.key + " ";
    } else if (level > 1) {
      String leftStr = printLevel(node.left, level - 1);
      String rightStr = printLevel(node.right, level - 1);
      return leftStr + rightStr;
    } else
      return "";
  }

  public RedBlackBSTB<Integer, Integer> pathLengths() {
    return pathLengths(root);
  }

  public RedBlackBSTB<Integer, Integer> pathLengths(Node node) {
    List<Key> list = new ArrayList<>();
    RedBlackBSTB<Integer, Integer> map = new RedBlackBSTB<>();
    pathLengths(node, list, map, 0);
    return map;
  }

  private void pathLengths(Node node, List<Key> list, RedBlackBSTB<Integer, Integer> map, int pathLen) {
    if (node == null)
      return;
    list.add(pathLen, node.key);
    pathLen++;
    if (node.left == null && node.right == null) {
      // adding 1 to the final pathLen to get expected answer for ex3324
      if (map.contains(pathLen + 1))
        map.put(pathLen + 1, map.get(pathLen + 1) + 1);
      else
        map.put(pathLen + 1, 1);
    } else {
      pathLengths(node.left, list, map, pathLen);
      pathLengths(node.right, list, map, pathLen);
    }
  }

  public void printPaths() {
    printPaths(root);
  }

  public void printPaths(Node node) {
    //http://www.geeksforgeeks.org/given-a-binary-tree-print-all-root-to-leaf-paths/
    List<Key> list = new ArrayList<>();
    printPaths(node, list, 0);
  }

  private void printPaths(Node node, List<Key> list, int pathLen) {
    //http://www.geeksforgeeks.org/given-a-binary-tree-print-all-root-to-leaf-paths/
    if (node == null)
      return;
    /* append this node to the path array */
    list.add(pathLen, node.key);
    pathLen++;
    /* it's a leaf, so print the path that led to here */
    if (node.left == null && node.right == null)
      printList(list, pathLen);
    else {
      /* otherwise try both subtrees */
      printPaths(node.left, list, pathLen);
      printPaths(node.right, list, pathLen);
    }
  }

  public void printList(List<Key> list, int len) {
    int i;
    for (i = 0; i < len; i++)
      System.out.print(list.get(i) + " ");
    System.out.println();
  }

  public void printArray(Key[] a, int len) {
    int i;
    for (i = 0; i < len; i++)
      System.out.print(a[i] + " ");
    System.out.println();
  }

  public Key[] toInOrderArray() {
    if (size() == 0) {
      if (kclass != null)
        return ofDim(kclass, 0);
      else
        return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = keys().iterator();
    int c = 0;
    while (it.hasNext())
      ka[c++] = it.next();
    return ka;
  }

  public Key[] toAllInOrderArray() {
    // includes virtual duplicates
    if (size() == 0) {
      if (kclass != null)
        return ofDim(kclass, 0);
      else
        return null;
    }
    Key[] ka = ofDim(kclass, dSize());
    Iterator<Key> it = allKeys().iterator();
    int c = 0;
    while (it.hasNext())
      ka[c++] = it.next();
    return ka;
  }

  public void show() {
    Iterator<Key> it = keys().iterator();
    while (it.hasNext()) {
      Key k = it.next();
      System.out.println(k + ":" + get(k) + " ");
    }
    System.out.println();
  }

  public String tos() {
    if (root == null)
      return "{}";
    Key[] a = toInOrderArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++) {
      sb.append(getNode(a[i]) + ",");
    }
    return sb.replace(sb.length() - 1, sb.length(), "}").toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Iterator<Key> it = keys().iterator();
    while (it.hasNext()) {
      Key k = it.next();
      sb.append(k + ":" + get(k) + ",");
    }
    return sb.deleteCharAt(sb.length() - 1).append("}").toString();
  }

  public static void redBlackTreeWorstCaseTest(int size) {
    // print a map of Integer array permutations to a Tuple2 of
    // path length and number of occurences of path length for
    // all permutations of range(0,size) each permutation is used 
    // as the input Key array for an st.RedBlackBSTX iff any 
    // such permutation results in a tree with at least one path
    // of floor(2*lg(size)) length where path length is computed 
    // as the number of nodes in a path from root to a null "leaf".
    int n = size;
    RedBlackBSTB<String, Tuple3<Integer, Integer, Integer>> mapr = new RedBlackBSTB<>();
    Integer[] a = rangeInteger(0, n);
    Integer[] b = a.clone(), c = new Integer[n];
    int wc = 0, tot = 0, maxlen = 0;
    Iterator<int[]> it = permutations(range(0, n));
    while (it.hasNext()) {
      int[] d = it.next();
      c = (Integer[]) box(d);
      RedBlackBSTB<Integer, Integer> st = new RedBlackBSTB<>(c, b);
      RedBlackBSTB<Integer, Integer> map = st.pathLengths();
      maxlen = (int) (Math.floor(2 * lg(st.size())));
      tot = 0;
      if (map.contains(maxlen)) {
        wc = map.get(maxlen); // worst case
        Iterator<Integer> ii = map.keys().iterator();
        while (ii.hasNext())
          tot += map.get(ii.next());
        ii = map.keys().iterator();
        if (1.0 * wc / tot >= .5) {
          while (ii.hasNext()) {
            Integer x = ii.next();
            mapr.put(arrayToString(c, 999, 1, 1), new Tuple3<Integer, Integer, Integer>(x, map.get(x), tot));
          }
        }
      }
    }
    if (mapr.size() > 0) {
      System.out.println("mapr.size = " + mapr.size());
      System.out.println("tot = " + tot);
      System.out.println("maxlen = " + maxlen);
      Iterator<String> is = mapr.keys().iterator();
      while (is.hasNext()) {
        String s = is.next();
        System.out.println(s + ": " + mapr.get(s));
      }
    }
  }

  public static void main(String[] args) {

    //    int n = 2;
    //    RedBlackBSTM<String,Tuple3<Integer,Integer,Integer>> mapr = new RedBlackBSTM<>();
    //    Integer[] a = rangeInteger(0,n);
    //    Integer[] b = a.clone(), c = new Integer[n]; int wc = 0, tot = 0, maxlen = 0;
    //    Iterator<int[]> it = permutations(range(0,n));
    //    while (it.hasNext()) {
    //      int[] d = it.next();
    //      c = (Integer[]) box(d);
    //      RedBlackBSTM<Integer, Integer> st = new RedBlackBSTM<>(c,b);
    //      RedBlackBSTM<Integer,Integer> map = st.pathLengths();
    //      maxlen = (int)(Math.floor(2*lg(st.size())));
    //      tot = 0;
    //      if (map.contains(maxlen)) {
    //        wc = map.get(maxlen); // worst case
    //        Iterator<Integer> ii = map.keys().iterator();
    //        while (ii.hasNext()) tot += map.get(ii.next());
    //        ii = map.keys().iterator();
    //        if (1.0*wc/tot >= .5) {
    //          while (ii.hasNext()) { 
    //            Integer x = ii.next();
    //            mapr.put(arrayToString(c,999,1,1),
    //              new Tuple3<Integer,Integer,Integer>(x,map.get(x),tot));
    //          }
    //        }
    //      }
    //    }
    //    if (mapr.size() > 0) {
    //      System.out.println("mapr.size = "+mapr.size());
    //      System.out.println("tot = "+tot);
    //      System.out.println("maxlen = "+maxlen);
    //      Iterator<String> is = mapr.keys().iterator();
    //      while (is.hasNext()) {
    //        String s = is.next();
    //        System.out.println(s+": "+mapr.get(s));
    //      }
    //    }
    //    
    ////    System.exit(0);   
    //    
    //    System.out.println();
    //    
    //    Integer[] u = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    //    Integer[] v = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    //    u = new Integer[]{0,3,2,1,4}; //(8,-1);
    //    v = rangeInteger(0,v.length);
    ////    Iterator<Integer> it;
    //    RedBlackBSTM<Integer, Integer> st = new RedBlackBSTM<>(u,v);
    //    System.out.println("size = "+st.size());
    //    System.out.println("height = "+st.height());
    //    st.printTree();
    //    System.out.println("levels:");
    //    st.printLevels();
    //    System.out.println("paths:");
    //    st.printPaths();
    //    System.out.print("pathLengths map: ");
    //    RedBlackBSTM<Integer,Integer> map = st.pathLengths();
    //    System.out.println(map);
    //    System.out.println("2lgN = "+2*lg(st.size()));
    //
    //    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    //    Character[] ca = new Character[sa.length];
    //    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    //    Integer[] ia = rangeInteger(0,sa.length);
    //    RedBlackBSTM<Character, Integer> h = new RedBlackBSTM<>(ca,ia);
    //    System.out.print("h = "+h.tos()+"\n");
    //    h.printTree();
    //    for (Character key : h.keys()) System.out.print(key+" "); System.out.println();
    //    for (Character key : h.keys()) System.out.print(h.size(h.getNode(key))+" "); System.out.println();
    //    for (Character key : h.keys()) System.out.print(h.dSize(h.getNode(key))+" "); System.out.println();
    //    for (Character key : h.keys()) System.out.print(h.getNode(key)+" "); System.out.println();

    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++)
      ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0, sa.length);
    RedBlackBSTB<Character, Integer> h = new RedBlackBSTB<>(ca, ia);
    System.out.print("h = " + h.tos() + "\n");
    System.out.println("rank('O') = " + h.rank('O'));
    System.out.println("dRank('O') = " + h.dRank('O'));

    h.printTree();
    for (Character key : h.keys())
      System.out.print(key + " ");
    System.out.println();
    //    for (Character key : h.keys()) System.out.print(h.size(h.getNode(key))+" "); System.out.println();
    //    for (Character key : h.keys()) System.out.print(h.dSize(h.getNode(key))+" "); System.out.println();
    for (Character key : h.keys())
      System.out.print(h.getNode(key) + " ");
    System.out.println();
    h.put('O', 13);
    h.put('O', 14);
    for (Character key : h.keys())
      System.out.print(h.getNode(key) + " ");
    System.out.println();

    //    System.exit(0);

    //    System.out.println("h.size() = "+h.size());
    //    System.out.println("h.dSize() = "+h.dSize());
    //    System.out.println("h.sizeR() = "+h.sizeR());
    //    System.out.println("h.sizeI() = "+h.sizeI());
    //    System.out.println("h.dSizeR() = "+h.dSizeR());
    //    System.out.println("h.dSizeI() = "+h.dSizeI());
    System.out.println("h.put('E', 11); h.put('E', 12);");
    h.put('E', 11);
    h.put('E', 12);
    System.out.print("h = " + h.tos() + "\n");

    System.out.println("rank('O') = " + h.rank('O'));
    System.out.println("dRank('O') = " + h.dRank('O'));
    System.out.println("h.put('O', 13); h.put('O', 14); h.put('O', 15);");
    h.put('O', 13);
    h.put('O', 14);
    h.put('O', 15);
    System.out.print("h = " + h.tos() + "\n");

    System.out.print("h.getValues('E') = ");
    par(h.getValues('E'));
    h.getBag('E').show();
    System.out.print("h.getValues('O') = ");
    par(h.getValues('O'));
    h.getBag('O').show();

    System.out.println("rank('O') = " + h.rank('O'));
    System.out.println("dRank('O') = " + h.dRank('O'));

    System.out.print("h = " + h.tos() + "\n");
    for (Character key : h.keys())
      System.out.print(h.dRank(key) + " ");
    System.out.println();
    System.out.println("");
    //    h.printTree();

    System.out.println("dSelect(2) = " + h.dSelect(2));
    System.out.println("dSelect(3) = " + h.dSelect(3));
    System.out.println("dSelect(4) = " + h.dSelect(3));
    System.out.println("select(4) = " + h.select(4));
    System.out.println("dSelect(6) = " + h.dSelect(6));

    System.out.print("h = " + h.tos() + "\n");
    for (Character key : h.allKeys())
      System.out.print(key + " ");
    System.out.println();

    System.out.println("check() = " + h.check());

  }
}
