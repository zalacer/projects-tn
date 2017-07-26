package st;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import analysis.Timer;
import ds.Queue;
import ds.QueueN;
import exceptions.InvalidDataException;
import v.Tuple2;

public class RedBlackBSTD<Value> {
  // from RedBlackBSX.java using Double keys.
  private static final boolean RED   = true;
  private static final boolean BLACK = false;
  private Node root;     // root of the BST
  private Class<?> kclass = Double.class; // Key class
  private Class<?> vclass = null; // Value class
  private double b; // for relable()
//  private int d; // for keys();
  
  private class Node {
    private Double key;           // key
    private Value val;         // associated data
    private Node left, right;  // links to left and right subtrees
    private boolean color;     // color of parent link
    private int size;          // subtree count

    public Node(Double key, Value val, boolean color, int size) {
      this.key = key;
      this.val = val;
      this.color = color;
      this.size = size;
    }
    
    public void printNode(boolean isRight, String indent, int...dash) {
      if (right != null) {
          right.printNode(true, indent + (isRight ? "       " : "|      "));
      }
      System.out.print(indent);
      if (isRight) {
        System.out.print(" /");
      } else {
        System.out.print(" \\");
      }
      String dashes = dash==null || dash.length == 0 ? repeat('-',5) : repeat('-',dash[0]);
      System.out.print(dashes);
      printNodeKey();
      if (left != null) {
          left.printNode(false, indent + (isRight ? "|      " : "       "),dash);
      }
    }
    
    private void printNodeKey() {
      if (key == null) {
        System.out.print("<null>");
      } else {
        if (color) System.out.print(key.toString()+"(red)");
        else System.out.print(key.toString());
      }
      System.out.println();
    }   
  }

  public RedBlackBSTD(){}
  
  public RedBlackBSTD(Double[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Double,Value> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Double,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Double[] kz = ofDim(kclass, n);
    Value[] vz = ofDim(vclass, n); c = 0;
    for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }

  // is node x red; false if x is null ?
  private boolean isRed(Node x) {
    if (x == null) return false;
    return x.color == RED;
  }

  // number of nodes in subtree rooted at x; 0 if x is null
  private int size(Node x) {
    if (x == null) return 0;
    return x.size;
  }
  
  public Class<?> getVclass() { return vclass; };

  public int size() { return size(root); }

  public boolean isEmpty() { return root == null; }

  public Value get(Double key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    return get(root, key);
  }

  private Value get(Node x, Double key) {
    while (x != null) {
      int cmp = key.compareTo(x.key);
      if      (cmp < 0) x = x.left;
      else if (cmp > 0) x = x.right;
      else              return x.val;
    }
    return null;
  }

  public boolean contains(Double key) { return get(key) != null; }

  public void put(Double key, Value val) {
    if (key == null) throw new IllegalArgumentException("first argument to put() is null");
//    if (val == null) {
//      delete(key);
//      return;
//    }
    if (vclass == null && val != null) vclass = val.getClass();
    root = put(root, key, val);
    root.color = BLACK;
    assert check();
  }

  // insert the key-value pair in the subtree rooted at h
  private Node put(Node h, Double key, Value val) { 
    if (h == null) return new Node(key, val, RED, 1);

    int cmp = key.compareTo(h.key);
    if      (cmp < 0) h.left  = put(h.left,  key, val); 
    else if (cmp > 0) h.right = put(h.right, key, val); 
    else              h.val   = val;

    // fix-up any right-leaning links
    if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
    if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
    if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
    h.size = size(h.left) + size(h.right) + 1;

    return h;
  }

  public void deleteMin() {
    if (isEmpty()) throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMin(root);
    if (!isEmpty()) root.color = BLACK;
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
    if (isEmpty()) throw new NoSuchElementException("BST underflow");

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = deleteMax(root);
    if (!isEmpty()) root.color = BLACK;
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

  public void delete(Double key) { 
    if (key == null) throw new IllegalArgumentException("argument to delete() is null");
    if (!contains(key)) return;

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = delete(root, key);
    if (!isEmpty()) root.color = BLACK;
    assert check();
  }

  // delete the key-value pair with the given key rooted at h
  private Node delete(Node h, Double key) { 
    // assert get(h, key) != null;

    if (key.compareTo(h.key) < 0)  {
      if (!isRed(h.left) && !isRed(h.left.left))
        h = moveRedLeft(h);
      h.left = delete(h.left, key);
    }
    else {
      if (isRed(h.left))
        h = rotateRight(h);
      if (key.compareTo(h.key) == 0 && (h.right == null))
        return null;
      if (!isRed(h.right) && !isRed(h.right.left))
        h = moveRedRight(h);
      if (key.compareTo(h.key) == 0) {
        Node x = min(h.right);
        h.key = x.key;
        h.val = x.val;
        // h.val = get(h.right, min(h.right).key);
        // h.key = min(h.right).key;
        h.right = deleteMin(h.right);
      }
      else h.right = delete(h.right, key);
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
    h.size = size(h.left) + size(h.right) + 1;
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
    h.size = size(h.left) + size(h.right) + 1;
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
    if (isRed(h.right))                      h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right))     flipColors(h);
    h.size = size(h.left) + size(h.right) + 1;
    return h;
  }

  public int height() {
    return height(root);
  }
  
  private int height(Node x) {
    if (x == null) return -1;
    return 1 + Math.max(height(x.left), height(x.right));
  }

  public Double minR() {
    if (isEmpty()) throw new NoSuchElementException("called minR() with empty symbol table");
    return minR(root).key;
  } 

  // the smallest key in subtree rooted at x; null if no such key
  private Node minR(Node x) { 
    // assert x != null;
    if (x.left == null) return x; 
    else                return minR(x.left); 
  }
  
  public Double min() {
    if (isEmpty()) throw new NoSuchElementException("called min() with empty symbol table");
    return min(root).key;
  }
  
  private Node min(Node x) { 
    // assert x != null;
    if (x.left == null) return x;
    while (x.left != null) x = x.left;
    return x;
  }

  public Double maxR() {
    if (isEmpty()) throw new NoSuchElementException("called maxR() with empty symbol table");
    return maxR(root).key;
  } 

  // the largest key in the subtree rooted at x; null if no such key
  private Node maxR(Node x) { 
    // assert x != null;
    if (x.right == null) return x; 
    else                 return maxR(x.right); 
  }
  
  public Double max() {
    if (isEmpty()) throw new NoSuchElementException("called max() with empty symbol table");
    return max(root).key;
  } 

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) { 
    // assert x != null;
    if (x.right == null) return x; 
    while (x.right != null) x = x.right;
    return x;
  } 

  public Double floorR(Double key) {
    if (key == null) throw new IllegalArgumentException("argument to floorR() is null");
    if (isEmpty()) throw new NoSuchElementException("called floorR() with empty symbol table");
    Node x = floorR(key, root);
    if (x == null) return null;
    else           return x.key;
  }    

  // the largest key in the subtree rooted at x less than or equal to the given key
  private Node floorR(Double key, Node x) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0) return x;
    if (cmp < 0)  return floorR(key, x.left);
    Node t = floorR(key, x.right);
    if (t != null) return t; 
    else           return x;
  }
  
  public Double floor(Double key) {
    if (key == null) throw new IllegalArgumentException("argument to floor() is null");
    if (isEmpty()) throw new NoSuchElementException("called floor() with empty symbol table");
    Node x = floor(key, root);
    if (x == null) return null;
    else           return x.key;
  }
  
  private Node floor(Double key, Node x) {
    Node y = null;
    while (x != null) {
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0) { y = x; x = x.right; }
        else { x = x.left;}
    }
    return y;
  }

  public Double ceilingR(Double key) {
    if (key == null) throw new IllegalArgumentException("argument to ceilingR() is null");
    if (isEmpty()) throw new NoSuchElementException("called ceilingR() with empty symbol table");
    Node x = ceilingR(key, root);
    if (x == null) return null;
    else           return x.key;  
  }

  // the smallest key in the subtree rooted at x greater than or equal to the given key
  private Node ceilingR(Double key, Node x) {  
    if (x == null) return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0) return x;
    if (cmp > 0)  return ceilingR(key, x.right);
    Node t = ceilingR(key, x.left);
    if (t != null) return t; 
    else           return x;
  }
  
  public Double ceiling(Double key) {
    if (key == null) throw new IllegalArgumentException("argument to ceiling() is null");
    if (isEmpty()) throw new NoSuchElementException("called ceiling() with empty symbol table");
    Node x = ceiling(key, root);
    if (x == null) return null;
    else           return x.key;  
  }
  
  private Node ceiling(Double key, Node x) {
    Node y = null;
    while (x != null) {
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) { y = x; x = x.left; }
        else { x = x.right;}
    }
    return y;
  }

  public Double selectR(int k) {
    if (root == null) return null;
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = selectR(root, k);
    return x.key;
  }

  // the key of rank k in the subtree rooted at x
  private Node selectR(Node x, int k) {
    // assert x != null;
    // assert k >= 0 && k < size(x);
    int t = size(x.left); 
    if      (t > k) return selectR(x.left,  k); 
    else if (t < k) return selectR(x.right, k-t-1); 
    else            return x; 
  }
  
  public Double select(int k) {
    if (root == null) return null;
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = select(root, k);
    return x.key;
  }
  
  private  Node select(Node x, int k ) {
    Node t = x;
    int r = (t.left !=  null ? size(x.left) : 0 ); 
    while (k != r ) {          
      if ( k < r ) {
        r = t.left.right != null ? r - size(t.left.right) - 1 : r - 1;
        t = t.left;
      } else {
        r = t.right.left != null ? r + size(t.right.left) + 1 : r + 1;
        t = t.right;
      }
    }
    return t;
 }
 

  public int rankR(double key) { return rankR(key, root); } 

  // number of keys less than key in the subtree rooted at x
  private int rankR(double key, Node x) {
    if (x == null) return 0; 
    double cmp = key - x.key; 
    if      (cmp < 0) return rankR(key, x.left); 
    else if (cmp > 0) return 1 + size(x.left) + rankR(key, x.right); 
    else              return size(x.left); 
  } 
  
  public int rank(double key) { return rank(key, root); }
  
  private int rank(double key, Node x) {
    if (x == null) return 0; 
    int rank = 0;
    while (x != null) {
      double cmp = key - x.key; 
      if (cmp < 0) x = x.left;
      else if (cmp > 0) { rank += 1 + size(x.left); x = x.right; }
      else return rank + size(x.left);
    }
    return rank;
  }

  public Iterable<Double> keys() {
    if (isEmpty()) return new Queue<Double>();
    return keys(min(), max());
  }
  
  public Iterator<Double> iterator() { return keys().iterator(); }
  
  public Double[] toKeyArray() { return v.ArrayUtils.toArray(iterator()); }
  
  public Iterator<Double> iterator(double lo, double hi) { return keys(lo, hi).iterator(); }
  
  public Double[] toKeyArray(double lo, double hi) { return v.ArrayUtils.toArray(iterator(lo,hi)); }

  public Double[] toKeyArray(int lo, int hi) {  
    return v.ArrayUtils.toArray(iterator(select(lo), select(hi))); }
  
  public Iterable<Double> keys(double lo, double hi) {
    Queue<Double> queue = new Queue<>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    keys(root, queue, lo, hi);
    return queue;
  } 

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void keys(Node x, Queue<Double> queue, double lo, double hi) { 
    if (x == null) return; 
    double cmplo = lo - x.key; 
    double cmphi = hi - x.key; 
    if (cmplo < 0) keys(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key); 
    if (cmphi > 0) keys(x.right, queue, lo, hi); 
  }

  public Iterable<Value> values() {
    if (isEmpty()) return new QueueN<Value>();
    return values(min(), max());
  }
  
  public Iterator<Value> valerator() { return values().iterator(); }
  
  public Value[] toValArray() { return v.ArrayUtils.toArray(valerator()); }
  
  public Iterator<Value> valerator(double lo, double hi) { return values(lo, hi).iterator(); }
  
  public Value[] toValArray(double lo, double hi) { return v.ArrayUtils.toArray(valerator(lo,hi)); }
  
  public Value[] toValArray(int lo, int hi) {  
    return v.ArrayUtils.toArray(valerator(select(lo), select(hi))); }

  public Iterable<Value> values(double lo, double hi) {
    QueueN<Value> queue = new QueueN<>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    values(root, queue, lo, hi);
    return queue;
  }
  
  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void values(Node x, QueueN<Value> queue, double lo, double hi) { 
    if (x == null) return; 
    double cmplo = lo - x.key; 
    double cmphi = hi - x.key; 
    if (cmplo < 0) values(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.val); 
    if (cmphi > 0) values(x.right, queue, lo, hi); 
  }
   
  public Iterable<Tuple2<Double,Value>> entries() {
    if (isEmpty()) return new Queue<Tuple2<Double,Value>>();
    return entries(min(), max());
  }
  
  public Iterator<Tuple2<Double,Value>> enterator() { return entries().iterator(); }
  
  public Tuple2<Double,Value>[] toEntryArray() { return v.ArrayUtils.toArray(enterator()); }
  
  public Iterator<Tuple2<Double,Value>> enterator(double lo, double hi) { 
    return entries(lo, hi).iterator(); }
  
  public Tuple2<Double,Value>[] toEntryArray(double lo, double hi) { 
    return v.ArrayUtils.toArray(enterator(lo,hi)); }
  
  public Tuple2<Double,Value>[] toEntryArray(int lo, int hi) { 
    return v.ArrayUtils.toArray(enterator(select(lo),select(hi))); }

  public Iterable<Tuple2<Double,Value>> entries(Double lo, Double hi) {
    if (lo == null) throw new IllegalArgumentException("first argument to entries() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to entries() is null");

    Queue<Tuple2<Double,Value>> queue = new Queue<>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    entries(root, queue, lo, hi);
    return queue;
  } 

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void entries(Node x, Queue<Tuple2<Double,Value>> queue, Double lo, Double hi) { 
    if (x == null) return; 
    int cmplo = lo.compareTo(x.key); 
    int cmphi = hi.compareTo(x.key); 
    if (cmplo < 0) entries(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(new Tuple2<Double,Value>(x.key,x.val)); 
    if (cmphi > 0) entries(x.right, queue, lo, hi); 
  }
    
  public void relable(double start) {
    if (isEmpty()) return;
    b = start;
    relable(min(), max());
  }
  
  public void relable(Double lo, double start) {
    if (isEmpty()) return;
    if (lo == null) throw new IllegalArgumentException("first argument to relable() is null");
    double hi = max();
    b = start;
    relable(root, lo, hi);
  }
  
  private void relable(Double lo, Double hi) {
    if (lo == null) throw new IllegalArgumentException("first argument to relable() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to relable() is null");
    relable(root, lo, hi);
  }
  
  private void relable(Node x, Double lo, Double hi) { 
    if (x == null) return; 
    int cmplo = lo.compareTo(x.key); 
    int cmphi = hi.compareTo(x.key); 
    if (cmplo < 0) relable(x.left, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) x.key = b++;; 
    if (cmphi > 0) relable(x.right, lo, hi); 
  }
 
  public int size(Double lo, Double hi) {
    if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

    if (lo.compareTo(hi) > 0) return 0;
    if (contains(hi)) return rank(hi) - rank(lo) + 1;
    else              return rank(hi) - rank(lo);
  }
  
  private boolean check() {
    if (!isBST())            System.out.println("Not in symmetric order");
    if (!isSizeConsistent()) System.out.println("Subtree counts not consistent");
    if (!isRankConsistent()) System.out.println("Ranks not consistent");
    if (!is23())             System.out.println("Not a 2-3 tree");
    if (!isBalanced())       System.out.println("Not balanced");
    return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
  }

  // does this binary tree satisfy symmetric order?
  // Note: this test also ensures that data structure is a binary tree since order is strict
  private boolean isBST() {
    return isBST(root, null, null);
  }

  // is the tree rooted at x a BST with all keys strictly between min and max
  // (if min or max is null, treat as empty constraint)
  // Credit: Bob Dondero's elegant solution
  private boolean isBST(Node x, Double min, Double max) {
    if (x == null) return true;
    if (min != null && x.key.compareTo(min) <= 0) return false;
    if (max != null && x.key.compareTo(max) >= 0) return false;
    return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
  } 

  // are the size fields correct?
  private boolean isSizeConsistent() { return isSizeConsistent(root); }
  private boolean isSizeConsistent(Node x) {
    if (x == null) return true;
    if (x.size != size(x.left) + size(x.right) + 1) return false;
    return isSizeConsistent(x.left) && isSizeConsistent(x.right);
  } 

  // check that ranks are consistent
  private boolean isRankConsistent() {
    for (int i = 0; i < size(); i++)
      if (i != rank(select(i))) return false;
    for (Double key : keys())
      if (key.compareTo(select(rank(key))) != 0) return false;
    return true;
  }

  // Does the tree have no red right links, and at most one (left)
  // red links in a row on any path?
  private boolean is23() { return is23(root); }
  private boolean is23(Node x) {
    if (x == null) return true;
    if (isRed(x.right)) return false;
    if (x != root && isRed(x) && isRed(x.left))
      return false;
    return is23(x.left) && is23(x.right);
  } 

  // do all paths from root to leaf have same number of black edges?
  private boolean isBalanced() { 
    int black = 0;     // number of black links on path from root to min
    Node x = root;
    while (x != null) {
      if (!isRed(x)) black++;
      x = x.left;
    }
    return isBalanced(root, black);
  }

  // does every path from the root to a leaf have the given number of black links?
  private boolean isBalanced(Node x, int black) {
    if (x == null) return black == 0;
    if (!isRed(x)) black--;
    return isBalanced(x.left, black) && isBalanced(x.right, black);
  }
  
  public void shiftRight() {
    if (size() < 2) return;
    shiftRight(root, null);
  }
  
  public void shiftRight(Node x, Node p) {
    //  System.out.println("args x="+x+" p="+p);
    //  System.out.println("st"); printTree(); System.out.println();
    //  System.out.println(this);
    //  showInOrder();
    //  if (x == p) return;
    if (x == p && x != root) throw new IllegalArgumentException(
        "shiftRight: x == p but x != root");
    if (x == null || size() < 2) return;
    if (p == null && x != root) throw new IllegalArgumentException(
        "shiftRight: p is null but x != root");
    //  System.out.println("st"); printTree(); System.out.println();
    //  System.out.println(this);
    Node n; int d;
    if (x == root) {  
      while (root.left != null) {
        root = rotateRight(root);
      }
      //   System.out.println("root="+root);
      if (root.right == null) return;
      // System.out.println("st"); printTree(); System.out.println();
      // System.out.println(this);
      shiftRight(root.right, root);
    } else {
      //    System.out.println("x="+x+" p="+p);
      //    System.out.println(this);
      if (!(p.left != null && p.left.key == x.key 
          || p.right != null && p.right.key == x.key))
        throw new IllegalArgumentException(
            "shiftRight: p ("+p.key+") isn't the parent of x ("+x.key+")"); 
      d = p.left == x ? 0 : 1;
      n = x; boolean first = true;
      if (n.left != null) {
        while (n.left != null)  { 
          n = rotateRight(n); 
          if (first) {
            d = p.left == n ? 0 : 1;
            if (d == 0) p.left = n; else p.right = n;
            //          System.out.println("d="+d);

            first = false;
          } else {
            //          System.out.println("x="+x+" p="+p);
            //          System.out.println(this);
            if (!(n.left != null && n.left.key == x.key 
                || n.right != null && n.right.key == x.key))
              throw new InvalidDataException(
                  "shiftRight: n ("+n.key+") isn't the parent of x ("+x.key+")"); 
            d = n.left == n ? 0 : 1;
            if (d == 0) n.left = n; else n.right = n;
            //          System.out.println("d="+d);
          }

        }
      }
      if (n.right == null) return;
      //    System.out.println("n="+n+" n.right="+n.right+" p="+p);
      shiftRight(n.right, n);
    } 
  }

  public void printTree(int...x) {
    if (root == null) { System.out.println("<empty tree>"); return; }
    if (x == null || x.length == 0) root.printNode(false, "");
    else root.printNode(false, "", x[0]);
  }
  
  public void printLevels() {
    if (isEmpty()) return;
    for (int i = 1; i < height()+2; i++) {
      System.out.print("Level " + (i-1) + ": ");
      String levelNodes = printLevel(root, i);
      System.out.print(levelNodes + "\n");
    }
  }

  private String printLevel(Node node, int level) {
    if (node == null) return "";
    if (level == 1) {
      return node.key + " ";
    } else if (level > 1) {
      String leftStr = printLevel(node.left, level - 1);
      String rightStr = printLevel(node.right, level - 1);
      return leftStr + rightStr;
    }
    else  return "";
  }
  
  public RedBlackBSTX<Integer,Integer> pathLengths() { return pathLengths(root); }
  
  public RedBlackBSTX<Integer,Integer> pathLengths(Node node) {
    List<Double> list = new ArrayList<>();
    RedBlackBSTX<Integer,Integer> map = new RedBlackBSTX<>();
    pathLengths(node, list, map, 0);
    return map;
  }
  
  private void pathLengths(Node node, List<Double> list, 
      RedBlackBSTX<Integer,Integer> map, int pathLen) {
    if (node == null) return;
    list.add(pathLen,node.key);
    pathLen++;
    if (node.left == null && node.right == null) {
      // adding 1 to the final pathLen to get expected answer for ex3324
      if (map.contains(pathLen+1)) 
        map.put(pathLen+1, map.get(pathLen+1)+1);
      else map.put(pathLen+1, 1);
    } else {
      pathLengths(node.left, list, map, pathLen);
      pathLengths(node.right, list, map, pathLen);
    }
  }
  
  public void printPaths() { printPaths(root); }
  
  public void printPaths(Node node) {
    //http://www.geeksforgeeks.org/given-a-binary-tree-print-all-root-to-leaf-paths/
    List<Double> list = new ArrayList<>();
    printPaths(node, list, 0);
  }

  private void printPaths(Node node, List<Double> list, int pathLen) {
    //http://www.geeksforgeeks.org/given-a-binary-tree-print-all-root-to-leaf-paths/
    if (node == null) return;
    /* append this node to the path array */
    list.add(pathLen,node.key);
    pathLen++;
    /* it's a leaf, so print the path that led to here  */
    if (node.left == null && node.right == null)
      printList(list, pathLen);
    else {
      /* otherwise try both subtrees */
      printPaths(node.left, list, pathLen);
      printPaths(node.right, list, pathLen);
    }
  }
  
  public void printList(List<Double> list, int len) {
    int i;
    for (i = 0; i < len; i++) System.out.print(list.get(i) + " ");
    System.out.println();
  }
  
  public void printArray(Double[] a, int len) {
      int i;
      for (i = 0; i < len; i++) System.out.print(a[i] + " ");
      System.out.println();
  }
  
  public void show() {
    Iterator<Double> it = keys().iterator();
    while (it.hasNext()) {
      Double k = it.next();
      System.out.println(k+":"+get(k)+" ");
    }
    System.out.println();
  }
  
  @Override public String toString() {
    if (root == null) return "{}";
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Iterator<Double> it = keys().iterator();
    while (it.hasNext()) {
      Double k = it.next();
      sb.append(k+":"+get(k)+",");
    }
    return sb.deleteCharAt(sb.length()-1).append("}").toString();
  }
 
  public static void main(String[] args) {
    
    Timer t = new Timer();
//    Double[] c = rangeDouble(1.,1000000.);
//    System.out.println(t.elapsed()); //60
//    System.exit(0);
    
    SecureRandom r = new SecureRandom();
    Double[] a = rangeDouble(1.,50.);
    shuffle(a,r);
    Integer[] b = rangeInteger(1,50);
    t = new Timer();
    RedBlackBSTD<Integer> st = new RedBlackBSTD<>(a,b);
    System.out.println(t.elapsed()); //412, 358, 359
    System.out.println(st.size());
//    System.exit(0);

    System.out.println(st);
    st.relable(2.5);
    System.out.println(st);
    st.relable(0.);
    System.out.println(st);
//    System.out.println(st.selectR(1));
//    System.out.println(st.select(1));
//    for (int i = 0; i < a.length; i++)
//      System.out.println(a[i]+" "+st.selectR(i)+" "+st.select(i));
//    System.out.println(st.rankR(11.1)+" "+st.rank(11.1));
//    for (int i = 0; i < a.length; i++)
//      System.out.println(a[i]+" "+st.rankR(a[i])+" "+st.rank(a[i]));
//    System.out.println(st.ceilingR(3.));
//    System.out.println(st.ceiling(3.));
//    System.out.println(st.floorR(.9));
//    System.out.println(st.floor(.9));
    System.out.println(st.maxR());
    System.out.println(st.max());
    System.out.println(st.minR());
    System.out.println(st.min());
  }
}
