package st;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import exceptions.InvalidDataException;
import exceptions.InvalidSTException;
import ds.Queue;
import ds.Stack;
import v.Tuple2;

// based on st.BSTX for ex3503

@SuppressWarnings("unused")
public class BinarySearchSET<X extends Comparable<X>> implements OrderedSET<X> {
  private Node root;             // root of BST
  private X successor = null;
  private X predecessor = null;
  private long compares = 0;
  private long pathCompares = 0; // total compares to reach all nodes from root
  private SecureRandom rnd = new SecureRandom(); // for delete
  private Class<?> xclass = null; // X class

  private class Node {
    private X x;           // sorted by key
    private Node left, right;  // left and right subtrees
    private int size;          // number of nodes in subtree
    private int height = 0;
    private int length = 0;    // ex3207: from https://github.com/nagajyothi/AlgorithmsByRobertSedgewick/blob/master/Searching/BinarySearchTrees/BST.java
                               // and used in avgCompares2() 
                               // and put(Node,Key,Value,long)   
    private long level = 0;    // used to compute internal path length

    public Node(){}
    
    public Node(X x, int size) {
      this.x = x;
      this.size = size;
    }
    
    public Node(X x, int size, long level) {
      this.x = x;
      this.size = size;
      this.level = level;
    }
    
    public void setLevel(long l) { level = l; }

    public String completeToString() {
      String s = "("+x+":"+level+":"+size+":"+height; 
      String s1 = left == null ? "null" : ""+left.x;
      String s2 = right == null ? "null" : ""+right.x;
      return s + ":"+s1+":"+s2+":)";
    }
    
    @Override public String toString() { return x.toString(); }

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
      printNodeX();
      if (left != null) {
          left.printNode(false, indent + (isRight ? "|      " : "       "),dash);
      }
    }
    
    private void printNodeX() {
      if (x == null) {
        System.out.print("<null>");
      } else {
        System.out.print(x.toString());
      }
      System.out.println();
    }
  }

  public BinarySearchSET() { setRndSeed(); }

  public BinarySearchSET(X[] xa) {
    if (xa == null || xa.length == 0) return;
    setRndSeed();
    if (xa == null || xa.length == 0) return;
    xclass = xa.getClass().getComponentType();
    int n = xa.length; int c = 0;
    X[] ta = ofDim(xa.getClass().getComponentType(), n);
    for (int i = 0; i < n; i++) if (xa[c] != null) ta[c] = xa[c];
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    for (int  i = 0; i < n; i++) add(ta[i]);
  }
  
  public BinarySearchSET(X[] xa, boolean balanced) {
    if (xa == null || xa.length == 0) return;
    setRndSeed();
    if (xa == null || xa.length == 0) return;
    xclass = xa.getClass().getComponentType();
    int n = xa.length; int c = 0;
    X[] ta = ofDim(xa.getClass().getComponentType(), n);
    for (int i = 0; i < n; i++) if (xa[c] != null) ta[c] = xa[c];
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    if (balanced) {
      Arrays.sort(ta); 
      int start = 0, end = n-1;
      arrayToBalancedBST(ta,start, end);
    } else {
      for (int  i = 0; i < n; i++) add(ta[i]);
    }
  }
  
  public BinarySearchSET(BinarySearchSET<X> bst) {
    if (bst == null || bst.isEmpty()) return;
    X[] xa = bst.toLevelOrderArray();
    for (int i = 0; i < xa.length; i++) add(xa[i]);  
  }
  
  void arrayToBalancedBST(X[] xa, int start, int end) {
    // assumes xa is sorted
    if (start > end) return;
    int mid = (start + end) / 2;
    add(xa[mid]);
    arrayToBalancedBST(xa, start, mid-1);
    arrayToBalancedBST(xa, mid+1, end);
  }
  
  public Node getRoot() { return root; }
  
  public void setCompares(long h) { compares = h; }
  
  public long getCompares() { return compares; }

  @Override public boolean isEmpty() { return root == null; }
  
  public void setRndSeed() { rnd.setSeed(System.currentTimeMillis()); }
  
  public void setRndSeed(long s) { rnd.setSeed(s); }

  @Override public int size() { if (root == null) return 0; return root.size; }
  
  public int size(Node n) { if (n == null) return 0; else return n.size; }
  
  public int size(X x) { if (x == null) return 0; else return getNode(x).size; }
  
  public int size(X lo, X hi) {
    // return number of keys between lo and hi inclusive
    if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

    if (lo.compareTo(hi) > 0) { compares++; return 0; }
    compares++;
    if (contains(hi)) return rank(hi) - rank(lo) + 1;
    else              return rank(hi) - rank(lo);
  }
  
  public int sizeR() { return sizeR(root); }
  
  public int sizeR(Node node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return 1;
    int l = sizeR(node.left);
    int r = sizeR(node.right);
    return 1 + l + r;
  }
  
  public int sizeI() { return sizeI(root); }
  
  public int sizeI(Node node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return 1;
    Queue<Node> q = new Queue<>();
    q.enqueue(node);
    int h = 0;
    while (true) {
      int c = q.size();
      if (c == 0) return h;
      while (c > 0) {
        h++;
        Node n = q.peek();
        q.dequeue();
        if (n.left != null) q.enqueue(n.left);
        if (n.right != null) q.enqueue(n.right);
        c--;
      }
    }
  }
    
  public int heightR() { return heightR(root); }
  
  public int heightR(Node node) {
    // recursive
    if (node == null) return 0;
    if (node.left == null  && node.right == null) return 0;
    int lh = height(node.left);
    int rh = height(node.right);
    return lh > rh ? lh+1 : rh+1;
  }
  
  public int heightI() { return heightI(root); }
  
  public int heightI(Node node) {
    if (node == null) return 0;
    Queue<Node> q = new Queue<>();
    q.enqueue(node);
    int h = -1;
    while (true) {
      int c = q.size();
      if (c == 0) return h;
      h++;
      while (c > 0) {
        Node n = q.peek();
        q.dequeue();
        if (n.left != null) q.enqueue(n.left);
        if (n.right != null) q.enqueue(n.right);
        c--;
      }
    }
  }

  public int height() { return height(root); } // a 1-node tree has height 0 
  
  public int height(Node n) { if(n == null) return 0; return n.height; }
  
  public int height(X x) { if (x == null) return 0; return height(getNode(x)); }
  
  public int length(Node n) { if(n == null) return 0; return n.length; }
 
  public int length(X x) { if(x == null) return 0; return length(getNode(x)); }

  @Override public boolean contains(X x) {
    if (x == null) throw new IllegalArgumentException();
    return get(x);
  }

  private boolean get(X x) { return get(root, x); }

  private boolean get(Node n, X x) {
    if (n == null || x == null || root == null) return false;
    int cmp = x.compareTo(n.x); compares++;
    if      (cmp < 0) return get(n.left, x);
    else if (cmp > 0) return get(n.right, x);
    else              return true;
  }
  
  public Node getNode(X x) { return getNode(root, x); }
  
  private Node getNode(Node n, X x) {
    // for use with lca
    if (n == null || x == null || root == null) return null;
    int cmp = x.compareTo(n.x); compares++;
    if      (cmp < 0) return getNode(n.left, x);
    else if (cmp > 0) return getNode(n.right, x);
    else              return n;
  }
  
  @Override public void add(X x) {
    if (x == null) throw new IllegalArgumentException();
    if (xclass == null) xclass = x.getClass();
    root = add(root, x, 0);
//    assert check();
  }

  private Node add(Node n, X x, long level) {
    if (n == null) { pathCompares+=(level+1); return new Node(x, 1, level); }
    int cmp = x.compareTo(n.x); compares++;
    if      (cmp < 0) n.left  = add(n.left, x, n.level+1);
    else if (cmp > 0) n.right = add(n.right, x, n.level+1);
//    else             n.val   = val;
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
    return n;
  }
  
  public void addIter(X x) {
    // this does't fix sizes for which a parent field in Node would be useful
    Node z = new Node(x, 1);
    if (root == null) { root = z;  return; }

    Node parent = null, n = root;
    while (n != null) {
        parent = n;
        int cmp = x.compareTo(n.x);
        if      (cmp < 0) n = n.left;
        else if (cmp > 0) n = n.right;
        else return;
    }
    int cmp = x.compareTo(parent.x);
    if (cmp < 0) parent.left  = z;
    else         parent.right = z;
  }
  
  private void decrementLevels(Node n) {
    // for use in delete, deleteMin, deleteMax, rotateLeft, rotateRight
    if (n == null || n.level == 0) return;
    n.level--; pathCompares--;
    if (n.left != null) decrementLevels(n.left);
    if (n.right != null)  decrementLevels(n.right);
  }
  
  private void incrementLevels(Node n) {
    // for use in rotateLeft and rotateRight
    if (n == null || n.level == 0) return;
    n.level++; pathCompares++;
    if (n.left != null) decrementLevels(n.left);
    if (n.right != null)  decrementLevels(n.right);
  }

  public void deleteMin() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMin(root);
    assert check();
  }

  private Node deleteMin(Node n) {
    if (n.left == null) {
      if (n == root) { decrementLevels(n.right); pathCompares--; }
      else decrementLevels(n);
      return n.right;
    }
    n.left = deleteMin(n.left);
    pathCompares--;
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
    return n;
  }
  
  private Node deleteMinx(Node n) {
    // the original deleteMin(Node) for use in delete.
    if (root == null) throw new NoSuchElementException();
    if (n.left == null) return n.right;
    n.left = deleteMinx(n.left);
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
    return n;
  }
  
  public void deleteMax() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMax(root);
    assert check();
  }

  private Node deleteMax(Node n) {
    if (n.right == null) {
      if (n == root) { decrementLevels(n.left); pathCompares--; }
      else decrementLevels(n);
      return n.left;
    }
    n.right = deleteMax(n.right);
    pathCompares--; 
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
    return n;
  }
  
  private Node deleteMaxx(Node n) {
    // the original deleteMax(Node) for use in delete.
    if (n.right == null) return n.left;
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
    return n;
  }

  @Override public void delete(X x) {
    if (root == null) throw new NoSuchElementException();
    if (x == null) throw new NullPointerException("x is null");
    root = delete(root, x);
  }
  
  private Node delete(Node n, X x) {
    if (n == null) return null;
    int cmp = x.compareTo(n.x); compares++;
    if      (cmp < 0) n.left  = delete(n.left,  x);
    else if (cmp > 0) n.right = delete(n.right, x);
    else {
      if (n.left  == null  && n.right == null) { 
        pathCompares -= n.level+1; 
        return null; 
      }
      if (n.left  == null) { 
        pathCompares -= n.level+1; 
        decrementLevels(n.right);
        return n.right; 
      }
      if (n.right == null) { 
        pathCompares -= n.level+1;
        decrementLevels(n.left);
        return n.left; 
      }
      // n has 2 children
      Node t = n;
      // replace n with its successor
      Node min = min(t.right); decrementLevels(min);
      n.x = min.x;
      n.right = deleteMinx(t.right); pathCompares -= min.level+1;
      n.left = t.left;
    } 
    n.size = 1 + size(n.left) + size(n.right);
    if (n == root && n.size == 1) n.height = 0;
    if (n.left == null && n.right == null) n.height = 0;
    else {
      int lh = n.left == null ? 0 : n.left.height;
      int rh = n.right == null ? 0 : n.right.height;
      n.height = 1 + Math.max(lh,rh);
    }
    n.length = length(n.left) + length(n.right) + size(n) - 1;
//    System.out.println("delete return "+n);
    return n;
  }
  
  public void deleteRandom() {
    // delete a random node except if size()==1 then delete root.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.x); return; }
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()); int c = 0; X x = null;
    Iterator<X> it = inOrder().iterator();
    while (it.hasNext()) { x = it.next(); if (c++ == r) break; }
    delete(x);
  }
  
  public void delete(int i) {
    // delete the i mod size()th inOrder Node.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.x); return; }
    int j = i % size(), c = 0; X x = null;
    Iterator<X> it = inOrder().iterator();
    while (it.hasNext()) { x = it.next(); if (c++ == i) break; }
    delete(x);
  }
  
  @Override public X min() {
    if (root == null) throw new NoSuchElementException();
    return min(root).x;
  } 

  private Node min(Node n) { 
    if (n.left == null) return n; 
    else return min(n.left); 
  } 

  @Override public X max() {
    if (root == null) throw new NoSuchElementException();
    return max(root).x;
  } 

  private Node max(Node n) {
    if (n.right == null) return n; 
    else return max(n.right); 
  } 

  @Override  public X floor(X x) {
    if (x == null) throw new IllegalArgumentException("argument to floor() is null");
    if (isEmpty()) throw new NoSuchElementException("called floor() with empty symbol table");
    Node n = floor(root, x);
    if (n == null) return null;
    else return n.x;
  } 
  
  private Node floor(Node n, X x) {
    if (n == null) return null;
    int cmp = x.compareTo(n.x);
    if (cmp == 0) return n;
    if (cmp <  0) return floor(n.left, x);
    Node t = floor(n.right, x); 
    if (t != null) return t;
    else return n; 
  } 

  @Override public X ceiling(X x) {
    // return smallest key >= key
    if (x == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node n = ceiling(root, x);
    if (n == null) return null;
    else return n.x;
  }
  
  private Node ceiling(Node n, X x) {
    if (n == null) return null;
    int cmp = x.compareTo(n.x);
    if (cmp == 0) return n;
    if (cmp < 0) { 
      Node t = ceiling(n.left, x); 
      if (t != null) return t;
      else return n; 
    } 
    return ceiling(n.right, x); 
  } 
  
  public void flattenRightPreOrdered() {
    if (root == null || size() < 2) return;
    flattenRightPreOrdered(root); 
  }
  
  public void flattenRightPreOrdered(Node n) {
    // flattens x to preordered right spine
    // http://www.programcreek.com/2013/01/leetcode-flatten-binary-tree-to-linked-list/
    if (n == null || n.size < 2) return;
    Stack<Node> stack = new Stack<>();
    Node p = n;
    while(p != null || !stack.isEmpty()){
      if(p.right != null) { stack.push(p.right); }
      if(p.left != null) { p.right = p.left; p.left = null; }
      else if (stack.size() > 0) p.right = stack.pop();
      p = p.right;
    }
  }
  
  public void flattenRightPreOrdered2() {
    if (root == null || size() < 2) return;
    flattenRightPreOrdered2(root);
  }
  
  public void flattenRightPreOrdered2(Node n) { 
    // flattens x to preordered right spine
    //http://qa.geeksforgeeks.org/3976/flattening-a-binary-tree
    if (n == null || n.size < 2) return;
    if (n == null) return; 
    Node node = n;
    while (node != null) {
      // Attaches the right sub-tree to the rightmost leaf of the left sub-tree:
      if (node.left != null) {
        Node rightMost = node.left;
        while (rightMost.right != null) rightMost = rightMost.right;
        rightMost.right = node.right;
        // Makes the left sub-tree to the right sub-tree:
        node.right = node.left;
        node.left = null;
      }
      // Flatten the rest of the tree:
      node = node.right;
    }     
  }
  
  public long ipl() {
    // return the internal path length of this as defined at
    // http://mathworld.wolfram.com/InternalPathLength.html
    // doing it using an iterator so only one method is needed.
    long ipl = 0;
    Iterator<X> it = preOrder().iterator();
    while (it.hasNext()) ipl += getNode(it.next()).level;
    return ipl;
  }
  
  public long ipltest() {
    // return the internal path length of this iff size() is 
    // 1 less than a power of 2. result is good iff tree is
    // perfectly balanced.
    // for Ex3320.
    // https://www.wolframalpha.com/input/?i=sum+x%282^x%29
    // http://mathworld.wolfram.com/InternalPathLength.html
    int y = size() +1;
    if ((y & (y - 1)) == 0) {
      int h = height();
      return (long)(2*(pow(2,h)*(h-1) + 1));
    }
    return -1L;  
  }
  
  public long internalPathLength() { return ipl(); }
  
  public long epl() {
    // return the external path length of this using the relation
    // externalPathLength = internalPathLength + 2*N
    // shown at http://mathworld.wolfram.com/InternalPathLength.html
    return ipl() + 2*size();
  }
  
  public long externalPathLength() { return epl(); }
 
  public Node rotateLeft(Node n) {
    // rotate n to the left and return the rotated subtree
    // modified from AVLTreeST.rotateLeft(Node)
    if (n.right == null) return null;
    Node y = n.right;
    n.right = y.left;
    y.left = n;
    y.size = n.size;
    n.size = 1 + size(n.left) + size(n.right);
    n.height = 1 + Math.max(height(n.left), height(n.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));
    y.level--; decrementLevels(y.right);
    n.level++; incrementLevels(n.left);
    return y;
  }
  
  public Node rotateRight(Node n) {
    // rotate n to the right and return the rotated subtree.
    // modified from AVLTreeST.rotateRight(Node)
    if (n == null) return null;
    if (n.left == null) return n;
    Node y = n.left;
    n.left = y.right;
    y.right = n;
    y.size = n.size;
    n.size = 1 + size(n.left) + size(n.right);
    n.height = 1 + Math.max(height(n.left), height(n.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));
    y.level--; decrementLevels(y.left);
    n.level++; incrementLevels(n.right);
    return y;
  }
  
  public void rotateRight() {
    root = rotateRight(root);
  }
  
  public void flattenLeft() {
    // transforms this to a left spine using rotations.
    if (size() < 2) return;
    flattenLeft(root, null);
    long c = 1; root.level = 0;
    Node n = root;
    while (n.right != null) {
      n = n.right; 
      n.setLevel(c++);
    }
  }
  
  public void flattenLeft(Node n, Node p) {
    // transforms n to a left spine using rotations.
    // p is the parent of n or root or null the case of n == root.
    if (n == null || size() < 2) return;
    if (n == p && n != root) throw new IllegalArgumentException(
        "flattenLeft: n == p but n != root");
    if (p == null && n != root) throw new IllegalArgumentException(
        "flattenLeft: p is null but n != root");
    Node o; boolean d;
    if (n == root) {  
     while (root.right != null) root = rotateLeft(root);
     flattenLeft(root.left, root);
    } else {
      if (!(p.left != null && p.left.x == n.x 
          || p.right != null && p.right.x == n.x))
        throw new IllegalArgumentException(
          "flattenLeft: p ("+p.x+") isn't the parent of n ("+n.x+")"); 
      o = n;
      while (o.right != null)  {
        d = p.left == o ? true : false;
        o = rotateLeft(o); 
        if (d) p.left = o; else p.right = o;
      }
      flattenLeft(o.left, o);
    }
  }
  
  public X[] flatten() { return flattenRight(); }
  
  public X[] flattenRight() {
    // transforms this to a right spine using rotations.
    if (size() < 2) return null;
    List<X> list = new ArrayList<>();
    flattenRight(root, null, list);
    long c = 1; root.level = 0;
    Node n = root;
    while (n.right != null) {
      n = n.right; 
      n.setLevel(c++);
    }
    return reverse(list.toArray(ofDim(xclass, 0)));
  }
  
  public void flattenRight(Node n, Node p, List<X> list) {
    // transforms n to a right spine using rotations.
    // p is the parent of n or root or null the case of n == root.
    if (n == p && n != root) throw new IllegalArgumentException(
        "flattenRight: n == p but n != root");
    if (n == null || size() < 2) return;
    if (p == null && n != root) throw new IllegalArgumentException(
        "flattenRight: p is null but n != root");
    Node o; boolean d;
    if (n == root) {  
     while (root.left != null) {
       root = rotateRight(root);
       list.add(root.x);
     }
     flattenRight(root.right, root, list);
    } else {
      if (!(p.left != null && p.left.x == n.x 
          || p.right != null && p.right.x == n.x))
        throw new IllegalArgumentException(
          "flattenRight: p ("+p.x+") isn't the parent of n ("+n.x+")"); 
      o = n;
      while (o.left != null)  {
        d = p.right == o ? true : false;
        o = rotateRight(o);
        list.add(o.x);
        if (d) p.right = o; else p.right = o;
      }
      flattenRight(o.right, o, list);
    }
  }
  
  public void unFlatten(X[] xa) { unFlattenRight(xa); }
  
  public void unFlattenRight(X[] xa) {
    // unflatten this, provided it's a right spine, from data gathered
    // while converting it to a right spine using flattenRight
    if (root == null || size() < 2 || xa.length == 0) return;
    if (!isRightSpine()) throw new InvalidSTException();
    Node n, p; X k; int c = 0;
    LOOP:
    while (c < xa.length) {
      k = root.x;
      if (k == xa[c]) {
        root = rotateLeft(root);
       if (++c == xa.length) break;
      }
      n = root;
      while (n.right != null) {
        p = n;
        n = n.right;
        k = n.x;
        if (k == xa[c]) {
          n = rotateLeft(n); 
          p.right = n;
          if (++c == xa.length) break LOOP;
        }     
      }
    }  
  }
  
  public boolean isLeftSpine() {
    if (root == null || size() < 2) return true;
    Node n = root;
    if (n.right != null) return false;
    while (n.left != null) {
      n = n.left;
      if (n.right != null) return false;  
    }
    return true;
  }
  
  public boolean isRightSpine() {
    if (root == null || size() < 2) return true;
    Node n = root;
    if (n.left != null) return false;
    while (n.right != null) {
      n = n.right;
      if (n.left != null) return false;  
    }
    return true;
  }
  
  public X[] preSucKeys(X x) {
    // return an array containing the inorder predecessor and successor of x.
    // the inorder predecessor of x is the X in this strictly smaller than x.
    // the inorder successor of x is the X in this strictly greater than x.
    // in either case if there is no such X null is returned.  
    if (root == null) return null;
    predecessor = null; successor = null;
    preSuc(root, x);
    X[] ps = ofDim(root.x.getClass(), 2);
    ps[0] = predecessor; ps[1] = successor;
    return ps; 
  }
  
  public X preKey(X x) {
    // return the inorder predecessor of x in this..
    // the inorder predecessor of x is the X in this strictly smaller than x.
    // if there is no such X null is returned.  
    if (root == null) return null;
    predecessor = null;
    preSuc(root, x);
    return predecessor; 
  }
  
  public X sucKey(X x) {
    // return the inorder successor of x in this.
    // the inorder successor of x is the X in this strictly greater than x.
    // if there is no such X null is returned.  
    if (root == null) return null;
    successor = null;
    preSuc(root, x);
    return successor; 
  }
  
  private void preSuc(Node n, X x) {
    // update the inorder predecessor and successor fields to be for n.x.
    // the predecessor of x is the greatest X in this strictly smaller than x.
    // the successor of x is the smallest X in this strictly greater than x.
    // in either case if there is no such X null is returned.  
    if (n == null)  return ;
    // If x is present at z
    if (n.x == x) {
      // the maximum value in left subtree is predecessor
      if (n.left != null) {
        Node tmp = n.left;
        while (tmp.right != null) tmp = tmp.right;
        predecessor = tmp.x;
      }
      // the minimum value in right subtree is successor
      if (n.right != null) {
        Node tmp = n.right ;
        while (tmp.left != null) tmp = tmp.left ;
        successor = tmp.x;
      }
      return ;
    }
    // If n.x is greater than x, go to left subtree
    if (n.x.compareTo(x) > 0) {
      compares++;
      successor = n.x;
      preSuc(n.left, x) ;
    } else { // go to right subtree
      compares++;
      predecessor = n.x;
      preSuc(n.right, x) ;
    }
  }
  
  public Node[] preSuc(X x) {
    // return an array containing the inorder predecessor and successor Nodes of x.
    // the inorder predecessor of x is the node in this with key strictly smaller than x.key.
    // the inorder successor of x is the node in this with key strictly greater than x.key.
    // in either case if there is no such Node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    // implementation is done using a Key arg in order to find predecessors or successors of 
    // a Key greater than max() or less than min() respectively.
    if (root == null) return null;
    if (x == null) return null;
    predecessor = null; successor = null;
    preSuc(root, x);
    Node p = getNode(predecessor); Node s = getNode(successor);
    if (p == null && s == null) return null;
    Node[] ps = ofDim(Node.class, 2); ps[0] = p; ps[1] = s;
    return ps; 
  }
  
  public Node pre(X x) {
    // return the inorder predecessor Node of x in this.
    // the predecessor of x is the node in this with x-field strictly smaller than x.
    // if there is no such node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    if (root == null) return null;
    if (x == null) return null;
    predecessor = null;
    preSuc(root, x);
    return getNode(predecessor);
  }
  
  public Node suc(X x) {
    // return the inorder successor Node of x in this.
    // the successor of x is the node in this with x-field strictly greater than x.
    // if there is no such Node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    if (root == null) return null;
    if (x == null) return null;
    successor = null;
    preSuc(root, x);
    return getNode(successor);
  }

  public X select(int k) {
    // return kth smallest X (key of rank k)
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node n = select(root, k);
    return n.x;
  }

  private Node select(Node n, int k) {
    if (n == null) return null; 
    int t = size(n.left); 
    if      (t > k) return select(n.left,  k); 
    else if (t < k) return select(n.right, k-t-1); 
    else            return n; 
  }

  public int rank(X x) {
    // return number of keys < key
    if (x == null) throw new NullPointerException();
    return rank(x, root);
  } 

  private int rank(X x, Node n) {
    if (x == null) throw new NullPointerException();
    if (n == null) return 0; 
    int cmp = x.compareTo(n.x); compares++;
    if      (cmp < 0) return rank(x, n.left); 
    else if (cmp > 0) return 1 + size(n.left) + rank(x, n.right); 
    else              return size(n.left); 
  } 
  
  private Iterable<X> keys() {
    // inorder traversal
    return keys(min(), max());
  }

  private Iterable<X> keys(X lo, X hi) {
    // inorder traversal
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<X> queue = new Queue<>();
    keys(root, queue, lo, hi);
    return queue;
  }  
  
  private void keys(Node n, Queue<X> queue, X lo, X hi) { 
    if (n == null) return; 
    int cmplo = lo.compareTo(n.x); compares++;
    int cmphi = hi.compareTo(n.x); compares++; 
    if (cmplo < 0) keys(n.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(n.x); 
    if (cmphi > 0) keys(n.right, queue, lo, hi); 
  }
  
  public Iterator<X> keysIterator() { return keys().iterator(); }

  public boolean check() {
    if (!isBST()) System.out.println("Not in symmetric order");
    if (!isSizeConsistent()) System.out.println("Subtree counts not consistent");
    if (!isRankConsistent()) System.out.println("Ranks not consistent");
    return isBST() && isSizeConsistent() && isRankConsistent();
  }

  // does this binary tree satisfy symmetric order?
  // Note: this test also ensures that data structure is a binary tree since order is strict
  private boolean isBST() {
    return isBST(root, null, null);
  }

  // is the tree rooted at x a BST with all keys strictly between min and max
  // (if min or max is null, treat as empty constraint)
  // Credit: Bob Dondero's elegant solution
  private boolean isBST(Node n, X min, X max) {
    if (n == null) return true;
    if (min != null && n.x.compareTo(min) <= 0) { compares++; return false; }
    compares++;
    if (max != null && n.x.compareTo(max) >= 0) { compares++; return false; }
    compares++;
    return isBST(n.left, min, n.x) && isBST(n.right, n.x, max);
  }

  // are the size fields correct?
  private boolean isSizeConsistent() { return isSizeConsistent(root); }
  private boolean isSizeConsistent(Node n) {
    if (n == null) return true;
    if (n.size != size(n.left) + size(n.right) + 1) return false;
    return isSizeConsistent(n.left) && isSizeConsistent(n.right);
  }
  
  public int nodeHeight() { 
    if (root == null) return -1;
    return root.height; 
  }
  
  public int nodeHeight(Node n) { 
    if (n == null) return -1;
    return n.height; 
  }
  
  public int nodeHeight(X x) {
    if (x == null) return -1;
    Node n = getNode(x);
    if (n == null) return -1;
    return n.height; 
  }
  
  public long getPathCompares() { return pathCompares; }
  
  public double avgCompares() { 
    if (root == null) return 0;
    return pathCompares*1./size(); 
  }
  
  public double avgComparesR() { 
    if (root == null) return 0;
    return pathCompares()*1./size(); 
  }
  
  public double avgCompares2(){
    // modified from https://github.com/nagajyothi/AlgorithmsByRobertSedgewick/blob/master/Searching/BinarySearchTrees/BST.java
    if (root == null) return 0;
    return length(root)/1.0/size() + 1;
  }
  
  public double optCompares(int N) { return log(N); }
  
  private long pathCompares() {
    if (root == null) return 0; 
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
  
  public X getRandomKey() {
    if (root == null) throw new NoSuchElementException();
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(height()+1), t, c = 0;
    Node n = root;
    while (c < r) {
      t = rnd.nextInt(size(n.left)+size(n.right)+1);
      if (t > size(n.left)) {
        n = n.right; 
      } else {
        if (n.left != null) n = n.left;
      }
      c++;
    }
    return n.x;
  }
  
  public X getUniformRandomKey() {
    if (root == null) throw new NoSuchElementException();
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()), c = 0; X x;
    Iterator<X> it = inOrder().iterator();
    while (it.hasNext()) { x = it.next(); if (c++ == r) return x;}
    return null;
  }
  
   public X getKey(Node n) {
     if (n == null) throw new IllegalArgumentException("getKey: Node arg is null");
     return n.x;
   }
   
   public int getHeight(X x) {
     if (x == null) throw new IllegalArgumentException("getHeight: X arg is null");
     return getNode(x).height;
   }
   
   public int getMaxLevel() {
     int max = 0; int l;
     Iterator<X> it = inOrder().iterator();
     while (it.hasNext()) {
       X x = it.next();
       l = getLevel(x);
       if (l > max) max = l;
     }
     return max;
   }

  public int getLevel(X x) { 
    // return the level of the node with key k 
    // in root or -1 if no such node is found.
    return getLevelPlus1(root, x, 1)-1; 
  }
  
  public int getLevel(Node n, X x) {
    // return the level of the node with key k 
    // in x or 0 if no such node is found.
    return getLevelPlus1(n, x, 1)-1; 
  }
  
  private int getLevelPlus1(Node n, X x, int level) {
    // return the level of the node with key k in x 
    // or 0 if a node with k isn't found.
    if (n == null) return 0;
    if (n.x.equals(x)) return level;
    int next = getLevelPlus1(n.left, x, level+1);
    if (next != 0) return next;
    next = getLevelPlus1(n.right, x, level+1);
    return next;
  }
  
  public int getLevel(Node n) { 
    // return the level of y in root or -1 if a 
    // node with key.equals(y.key) isn't found.
    return getLevelPlus1(root, n, 1)-1; 
  }
  
  public int getLevel(Node n, Node o) {
    // return the level of o in n or -1 if a
    // node with x.equals(o.x) isn't found.
    return getLevelPlus1(n, o, 1)-1; 
  }
  
  private int getLevelPlus1(Node n, Node o, int level) {
    // return the level of o in n or 0 if a node
    // with x.equals(o.x) isn't found.
    if (n == null) return 0;
    if (n.x.equals(o.x)) return level;
    int next = getLevelPlus1(n.left, o, level+1);
    if (next != 0) return next;
    next = getLevelPlus1(n.right, o, level+1);
    return next;
  }
  
  public boolean isBestCase() {
    // return true iff this is best case defined as all non-leaf
    // nodes have 2 children and all leaves are at the same depth
    // else return false. definition from
    // http://pages.cs.wisc.edu/~vernon/cs367/notes/9.BST.html.
    Iterator<X> it = preOrder().iterator();
    boolean f = false; int l = 0; // leaf level
    while (it.hasNext()) {
      Node n = getNode(it.next());
      if (!f && n.left == null && n.right == null) { 
        l = getLevel(n); f = true; continue; 
      }
      if (f && n.left == null && n.right == null) {
        if (getLevel(n) != l) return false;
        else continue;
      }
      if (n.left == null && n.right != null || n.right == null && n.left != null)
        return false;   
    }
    return true;
  }
  
  public boolean isWorstCase()  {
    // return true if this is worst case defined as every node 
    // has at least 1 null link.
    Iterator<X> it = preOrder().iterator();
    while (it.hasNext()) {
      Node n = getNode(it.next());
      if (n.left != null && n.right != null) return false;
    }
    return true;    
  }
  
  private Node lca(Node n, Node o, Node p) {
    // return the Node that's lowest common ancestor of o and p in n.
    if(n == null || o == null || p == null) return null;
    if(n.x.equals(o.x) || n.x.equals(p.x)) return n;
    Node left = lca(n.left, o, p);
    Node right = lca(n.right, o, p);
    if(left != null && right != null) return n;
    if(left == null)
    return right;
    else return left;  
  }
  
  public X lca(X a, X b) {
    // return the key of the lowest common ancestor 
    // of the nodes with keys a and b in this.
    Node n = lca(root, getNode(a), getNode(b));
    if (n == null) return null;
    return n.x;
  }

  // check that ranks are consistent
  private boolean isRankConsistent() {
    if (root == null) return true;
    for (int i = 0; i < size(); i++)
      if (i != rank(select(i))) return false;
    for (X x : keys()) {
      if (x.compareTo(select(rank(x))) != 0) { compares++; return false; }
      compares++;
    }
    return true;
  }
  
  public BinarySearchSET<X> union(BinarySearchSET<X> that) {
    if (that == null) return null;
    BinarySearchSET<X> set = new BinarySearchSET<>();
    for (X x : this) { set.add(x); }
    for (X x : that) { set.add(x); }
    return set;
  }
  
  public BinarySearchSET<X> intersection(BinarySearchSET<X> that) {
    if (that == null) return null;
    BinarySearchSET<X> set = new BinarySearchSET<>();
    if (this.size() < that.size()) {
        for (X x : this) { if (that.contains(x)) set.add(x); }
    } else for (X x : that) { if (this.contains(x)) set.add(x); }
    return set;
  }
  
  public Iterable<X> preOrder() {
    // return a preordered iterable of the keys in this bst.
    Queue<X> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> stack = new Stack<>();
    stack.push(root);
    while(!stack.isEmpty()) {
      Node n = stack.pop();
      q.enqueue(n.x);
      if (n.right != null) { stack.push(n.right); }
      if (n.left  != null) { stack.push(n.left); }
    }
    return q;
  }
  
  public X[] toPreOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = preOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }
  
  public void showPreOrder() {
    showPreOrder(root);
    System.out.println();
  }

  public void showPreOrderR() {
    // print the key:values in this bst in preorder recursively.
    showPreOrderR(root);
    System.out.println();
  }
  
  public void showPreOrderR(Node n) {
    // print the key:values in x in preorder recursively.
    if(n ==  null) return;
    System.out.print(n.x+" ");
    showPreOrderR(n.left);
    showPreOrderR(n.right);
  }
  
  public void showPreOrder(Node n) {
    if(n == null) return;
    Stack<Node> stack = new Stack<>();
    stack.push(n);
    while(!stack.isEmpty()) {
      Node o = stack.pop();
      System.out.print(o.x+" ");
      if (o.right != null) { stack.push(o.right); }
      if (o.left  != null) { stack.push(o.left); }
    }
  }
  
  public Iterable<X> inOrder() {
    // return an inordered iterable of the keys in this bst.
    Queue<X> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> s = new Stack<Node>();
    Node n = root;
    while(!s.isEmpty() || n!=null) {
      if (n != null) {
        s.push(n);
        n = n.left;
      } else {
        n = s.pop();
        q.enqueue(n.x);
        n = n.right;
      }
    }
    return q;
  }
  
  @Override public Iterator<X> iterator() { return inOrder().iterator(); }
  
  public Iterator<X> inOrderIterator() { return inOrder().iterator(); }
  
  public X[] toInOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = inOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }
  
  public void show() { showInOrder(); }
  
  public void showInOrder() {
    showInOrder(root);
    System.out.println();
  }
  
  public void showInOrder(Node n) {
    if(n == null) return;
    Stack<Node> s = new Stack<>();
    Node currentNode=n;
    while(!s.isEmpty() || currentNode!=null) {
      if(currentNode!=null) {
        s.push(currentNode);
        currentNode=currentNode.left;
      } else {
        Node o=s.pop();
        System.out.print(o.x+" ");
        currentNode=n.right;
      }
    }
  }
  
  public void showInOrderR() {
    showInOrderR(root);
    System.out.println();
  }
  
  public void showInOrderR(Node n) {
    if(n ==  null) return;
    showInOrderR(n.left);
    System.out.print(n.x+" ");
    showInOrderR(n.right);
  }
  
  public Iterable<X> reverseInOrder() {
    // return a reverse inordered iterable of the keys in this bst.
    Stack<X> q = new Stack<>();
    if (root == null) return q;
    Stack<Node> s = new Stack<Node>();
    Node n = root;
    while(!s.isEmpty() || n!=null) {
      if (n != null) {
        s.push(n);
        n = n.left;
      } else {
        n = s.pop();
        q.push(n.x);
        n = n.right;
      }
    }
    return q;
  }
  
  public Iterator<X> reverseInOrderIterator() { return reverseInOrder().iterator(); }
  
  public X[] toReverseInOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = reverseInOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }
  
  public Iterable<X> postOrder() {
    // return a postordered iterable of the keys in this bst.
    Queue<X> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> s = new Stack<>();
    Node current = root;
    while (true) { 
      if (current != null) {
        if (current.right != null) s.push(current.right);
        s.push(current );
        current = current.left;
        continue;
      }
      if (s.isEmpty()) return q;
      current = s.pop();
      if(current.right != null && ! s.isEmpty() && current.right == s.peek()) {
        s.pop();
        s.push(current);
        current = current.right;
      } else {
        q.enqueue(current.x);
        current = null;
      }
    }
  }
  
  public Iterator<X> postOrderIterator() { return postOrder().iterator(); }
  
  public X[] toPostOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = postOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }
  
  public void showPostOrderR() {
    showPostOrderR(root);
    System.out.println();
  }
 
  public void showPostOrderR(Node n) {
    if(n !=  null) {
      showPostOrderR(n.left);
      showPostOrderR(n.right);
      System.out.print(n.x+" ");
    }
  }

  public void showPostOrder() {
    showPostOrder(root);
    System.out.println();
  }
  
  public void showPostOrder(Node n) {
    if(n == null) return;    
    Stack<Node> s = new Stack<>();
    Node current = n;
    while (true) { 
      if (current != null) {
        if (current.right != null) s.push(current.right);
        s.push(current );
        current = current.left;
        continue;
      }
      if(s.isEmpty()) return;
      current = s.pop();
      if(current.right != null && ! s.isEmpty() && current.right == s.peek()) {
        s.pop();
        s.push(current);
        current = current.right;
      } else {
        System.out.print(current.x+" ");
        current = null;
      }
    }
  }
  
  public Iterable<X> spiralOrder() {
    // return a spiral-ordered iterable of the keys in this bst.
    // spiral order is like level-order but reversed every other level.
    Queue<X> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> stack = new Stack<>();
    stack.push(root);  
    boolean d = false;
    while(!stack.isEmpty()) {
      Stack<Node> tstack = new Stack<>();  
      while(!stack.isEmpty()) {
        Node tnode = stack.pop();
        q.enqueue(tnode.x);
        if(!d) {
          if(tnode.left != null) 
            tstack.push(tnode.left);
          if(tnode.right != null) 
            tstack.push(tnode.right);
        } else {
          if(tnode.right != null) 
            tstack.push(tnode.right);
          if(tnode.left != null) 
            tstack.push(tnode.left);
        }
      }
      d = !d; 
      stack = tstack; 
    }
    return q;
  }
  
  public Iterator<X> spiralOrderIterator() { return spiralOrder().iterator(); }

  public X[] toSpiralOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = spiralOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }
  
  public void showSpiralOrder() {
    showSpiralOrder(root);
    System.out.println();
  }
  
  public void showSpiralOrder(Node n) {
    if(n == null) return;
    Stack<Node> stack = new Stack<>();
    stack.push(n);  
    boolean d = false;
    while(!stack.isEmpty()) {
      Stack<Node> tstack = new Stack<>();  
      while(!stack.isEmpty()) {
        Node tnode = stack.pop();
        System.out.print(tnode.x+" ");
        if(!d) {
          if(tnode.left != null) 
            tstack.push(tnode.left);
          if(tnode.right != null) 
            tstack.push(tnode.right);
        } else {
          if(tnode.right != null) 
            tstack.push(tnode.right);
          if(tnode.left != null) 
            tstack.push(tnode.left);
        }
      }
      d = !d; 
      stack = tstack; 
    }
  }
  
  public Iterable<X> levelOrder() {
    // return a level-ordered iterable of the keys in this bst.
    Queue<X> keys = new Queue<>();
    Queue<Node> queue = new Queue<>();
    queue.enqueue(root);
    while (!queue.isEmpty()) {
      Node n = queue.dequeue();
      if (n == null) continue;
      keys.enqueue(n.x);
      queue.enqueue(n.left);
      queue.enqueue(n.right);
    }
//    par(keys.toArray());
    return keys;
  }
  
  public Iterator<X> levelOrderIterator() { return levelOrder().iterator(); }
  
  public X[] toLevelOrderArray() {
    if (size() == 0) {
      if (xclass != null) return ofDim(xclass, 0);
      else return null;
    }
    X[] xa = ofDim(xclass, size());
    Iterator<X> it = levelOrder().iterator(); int c = 0;
    while (it.hasNext()) xa[c++] = it.next();
    return xa;  
  }

  public void showLevelOrder() {
    // print the key:values in this bst in level order.
    if (root == null) { System.out.println("bst empty"); return; }
    Iterator<X> it = levelOrder().iterator(); X x;
    while (it.hasNext()) { x = it.next(); System.out.print(x+" "); }
    System.out.println();    
  }

  public void printTree(int...w) {
    if (root == null) { System.out.println("<empty tree>"); return; }
    if (w == null || w.length == 0) root.printNode(false, "");
    else root.printNode(false, "", w[0]);
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
      return node.x + " ";
    } else if (level > 1) {
      String leftStr = printLevel(node.left, level - 1);
      String rightStr = printLevel(node.right, level - 1);
      return leftStr + rightStr;
    }
    else  return "";
  }
  
  @Override // based only on preordered Keys
  public int hashCode() { return Objects.hash((Object[])toPreOrderArray()); }
  
  @Override
  public boolean equals(Object obj) {
    // after initial tests equality is based on preordered Keys.
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("rawtypes")
    BinarySearchSET other = (BinarySearchSET) obj;
    if (Arrays.equals(toPreOrderArray(), other.toPreOrderArray())) return true;
    return false;
  }

  @Override
  public String toString() {
    if (root == null) return "()";
    X[] a = toPreOrderArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++) {
      sb.append(getNode(a[i])+" ");
    }
    return sb.substring(0,sb.length()-1).toString();
  }
  
  public static <X extends Comparable<X>> void transform(BinarySearchSET<X> a, BinarySearchSET<X> b) {
    // transform a to b provided they have the same key sets.
    // changes a but doesn't change b.
    if (a == null && b == null) return;
    if (a == null && b != null ||a != null && b == null) throw new IllegalArgumentException(
        "transform: the two BSTXs are not both null or not not null");
    if (a.size() != b.size()) throw new IllegalArgumentException(
        "transform: the two BSTXs don't have the same size");
    if (!Arrays.equals(a.toInOrderArray(), b.toInOrderArray()))
      throw new IllegalArgumentException(
          "transform: the two BSTXs don't have the same key sets");
    BinarySearchSET<X> bb = new BinarySearchSET<>(b);
    a.flatten();
    a.unFlatten(bb.flatten());
    assert a.equals(b);
  }

  public static void main(String[] args) {
    
    System.out.println("BinarySearchSET demo:");

    BinarySearchSET<Integer> set1 = new BinarySearchSET<>();
    for (int i = 1; i < 11; i++) for (int j = 0; j < i; j++) set1.add(i);
    System.out.println("set1 = "+set1);

    BinarySearchSET<Integer> set2 = new BinarySearchSET<>();
    for (int i = 6; i < 16; i++) for (int j = 0; j < i; j++) set2.add(i);
    System.out.println("set2 = "+set2);

    BinarySearchSET<Integer> union1 = set1.union(set2);
    System.out.println("set1 union set2 = "+union1);

    for (int i = 1; i < 16; i++) assert union1.contains(i);

    BinarySearchSET<Integer> intersection1 = set1.intersection(set2);
    System.out.println("set1 intersect set2 = "+intersection1);

    for (int i = 6; i < 11; i++) assert intersection1.contains(i);
    
    System.out.println("set1 min = "+set1.min());
    
    System.out.println("set2 max = "+set2.max());
    
    BinarySearchSET<Integer> set3 = new BinarySearchSET<>();
    int[] a = range(0,12,3); for (int i : a) set3.add(i);
    System.out.println("set3 = "+set3);
    
    System.out.println("ceiling of 5 in set3 = "+set3.ceiling(5));
    
    System.out.println("floor of 7 in set3 = "+set3.floor(7));

  }
}

