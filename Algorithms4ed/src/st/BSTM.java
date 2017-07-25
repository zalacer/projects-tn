package st;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import exceptions.InvalidSTException;
import ds.Queue;
import ds.Stack;
import v.Tuple2;

// based on BSTX for ex3509

public class BSTM<Key extends Comparable<? super Key>, Value> {
  private Node root;             // root of BST
  private Key successor = null;
  private Key predecessor = null;
  private long compares = 0;
  private long pathCompares = 0; // total compares to reach all nodes from root
  private SecureRandom rnd = new SecureRandom(); // for delete
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = null; // Value class
  private boolean dups = true;

  private class Node {
    private Key k;             // sorted by key
    private Value v;           // last value put
    private int c = 0;         // count of Values for key == #Keys == k
    private Node left, right;  // left and right subtrees
    private int size;          // number of unique nodes in subtree
    private int dSize;         // number of nodes in substree including duplicates
    private int height = 0;
    private int length = 0;    // ex3207: from https://github.com/nagajyothi/AlgorithmsByRobertSedgewick/blob/master/Searching/BinarySearchTrees/BST.java
                               // and used in avgCompares2() 
                               // and put(Node,Key,Value,long)   
    private long level = 0;    // used to compute internal path length
    
    public Node(Key k, Value v, int c, int size, int dSize) {
      this.k = k;
      this.v = v;
      this.c = c;
      this.size = size;
      this.dSize = dSize;
    }
    
    public Node(Key k, Value v, int c, int size, int dSize, long level) {
      this.k = k;
      this.v = v;
      this.c = c;
      this.size = size;
      this.dSize = dSize;
      this.level = level;
    }
    
    public void setLevel(long x) { level = x; }

    public String toString2() {
      return "("+k+","+v+","+c+","+size+","+dSize+")";
    }
        
    @Override public String toString() {
      String s = "("+k+":"+level+":"+size+":"+height; 
      String s1 = left == null ? "null" : ""+left.k;
      String s2 = right == null ? "null" : ""+right.k;
      return s + ":"+s1+":"+s2+":)";
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
      if (k == null) {
        System.out.print("<null>");
      } else {
        System.out.print(k.toString());
      }
      System.out.println();
    }
  }

  public BSTM() { setRndSeed(); }
  
  public BSTM(boolean dups) {  
    setRndSeed(); 
    this.dups = dups;
  }

  public BSTM(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    setRndSeed();
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    Value[] vz = ofDim(vclass, n); c = 0;
    for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }
  
  public BSTM(Key[] ka, Value[] va, boolean balanced) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    setRndSeed();
    int n = Math.min(ka.length, va.length); int c = 0;
    Tuple2<Key,Value> ta[] = ofDim(Tuple2.class,n);
    for (int i = 0; i < n; i++) 
      if (ka[c] != null && va[c] != null) 
        ta[c] = new Tuple2<Key,Value>(ka[c],va[c++]);
    if (c == 0) return;
    ta = take(ta,c); n = ta.length;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    Value[] vz = ofDim(vclass, n); c = 0;
    if (balanced) {
      Comparator<Tuple2<Key,Value>> comp = (t1,t2) -> { return t1._1.compareTo(t2._1); };
      Arrays.sort(ta,comp);
      for (int i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
      int start = 0, end = n-1;
      arrayToBalancedBST(kz, vz, start, end);
    } else {
      for (int  i = 0; i < n; i++) { kz[i] = ta[i]._1; vz[i] = ta[i]._2; }
      for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
    }
  }
  
  public BSTM(BSTM<Key,Value> bst) {
    if (bst == null || bst.isEmpty()) return;
    Key[] ka = bst.toLevelOrderArray();
    for (int i = 0; i < ka.length; i++) put(ka[i], bst.get(ka[i]));  
  }
  
  void arrayToBalancedBST(Key[] ka, Value[] va, int start, int end) {
    // assumes ka is sorted
    if (start > end) return;
    int mid = (start + end) / 2;
    put(ka[mid], va[mid]);
    arrayToBalancedBST(ka, va, start, mid-1);
    arrayToBalancedBST(ka, va, mid+1, end);
  }
  
  public boolean getDups() { return dups; }
  
  public void setDups(boolean dups) { this.dups = dups; }
  
  public Node getRoot() { return root; }
  
  public void setCompares(long h) { compares = h; }
  
  public long getCompares() { return compares; }

  public boolean isEmpty() { return root == null; }
  
  public void setRndSeed() { rnd.setSeed(System.currentTimeMillis()); }
  
  public void setRndSeed(long s) { rnd.setSeed(s); }

  public int size() { if (root == null) return 0; return root.size; }
  
  public int dSize() { if (root == null) return 0; return root.dSize; }
  
  public int size(Node x) { if (x == null) return 0; else return x.size; }
  
  public int dSize(Node x) { if (x == null) return 0; else return x.dSize; }
  
  public int size(Key k) { if (k == null) return 0; else return getNode(k).size; }
  
  public int dSize(Key k) { if (k == null) return 0; else return getNode(k).dSize; }
  
  public int size(Key lo, Key hi) {
    // return number of unique keys between lo and hi inclusive
    if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to size() is null");
    if (lo.compareTo(hi) > 0) { compares++; return 0; }
    compares++;
    if (contains(hi)) return rank(hi) - rank(lo) + 1;
    else              return rank(hi) - rank(lo);
  }
  
  public int dSize(Key lo, Key hi) {
    // return number of keys between lo and hi inclusive
    if (lo == null) throw new IllegalArgumentException("first argument to dSize() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to dSize() is null");
    if (lo.compareTo(hi) > 0) { compares++; return 0; }
    compares++;
    if (contains(hi)) return dRank(hi) - dRank(lo) + 1;
    else              return dRank(hi) - dRank(lo);
  }
  
  public int sizeR() { return sizeR(root); }
  
  public int sizeR(Node node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return 1;
    int l = sizeR(node.left);
    int r = sizeR(node.right);
    return 1 + l + r;
  }
  
  public int dSizeR() { return dSizeR(root); }
  
  public int dSizeR(Node node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return node.c;
    int l = dSizeR(node.left);
    int r = dSizeR(node.right);
    return node.c + l + r;
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
        Node x = q.peek();
        q.dequeue();
        if (x.left != null) q.enqueue(x.left);
        if (x.right != null) q.enqueue(x.right);
        c--;
      }
    }
  }
  
  public int dSizeI() { return dSizeI(root); }
  
  public int dSizeI(Node node) {
    if (node == null) return 0;
    if (node.left == null && node.right == null) return node.c;
    Queue<Node> q = new Queue<>();
    q.enqueue(node);
    int h = 0;
    while (true) {
      int c = q.size();
      if (c == 0) return h;
      while (c > 0) {
        Node x = q.peek();
        h += x.c;
        q.dequeue();
        if (x.left != null) q.enqueue(x.left);
        if (x.right != null) q.enqueue(x.right);
        c-=x.c;
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
        Node x = q.peek();
        q.dequeue();
        if (x.left != null) q.enqueue(x.left);
        if (x.right != null) q.enqueue(x.right);
        c--;
      }
    }
  }

  public int height() { return height(root); } // a 1-node tree has height 0 
  
  public int height(Node x) { if(x == null) return 0; return x.height; }
  
  public int height(Key k) { if (k == null) return 0; return height(getNode(k)); }
  
  public int length(Node x) { if(x == null) return 0; return x.length; }
 
  public int length(Key k) { if(k == null) return 0; return length(getNode(k)); }

  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException();
    return get(key) != null;
  }

  public Value get(Key key) { return get(root, key); }
  
  private Value get(Node x, Key key) {
    if (x == null || key == null || root == null) return null;
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) return get(x.left, key);
    else if (cmp > 0) return get(x.right, key);
    else              return x.v;
  }
  
  public int getKeyCount(Key key) {
    Node node = getNode(root, key);
    return node == null ? 0 : node.c;
  }
  
  public Node getNode(Key key) { return getNode(root, key); }
  
  private Node getNode(Node x, Key key) {
    // for use with lca
    if (x == null || key == null || root == null) return null;
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) return getNode(x.left, key);
    else if (cmp > 0) return getNode(x.right, key);
    else              return x;
  }
  
  public void put(Key key, Value val) {
    if (key == null) throw new IllegalArgumentException();
    if (kclass == null) kclass = key.getClass();
    if (val == null) { delete(key); return; }
    if (vclass == null) vclass = val.getClass();
    root = put(root, key, val, 0);
    assert check();
  }

  private Node put(Node x, Key key, Value val, long level) {
    if (x == null) { pathCompares+=(level+1); return new Node(key,val,1,1,1,level); }
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) x.left  = put(x.left,  key, val, x.level+1);
    else if (cmp > 0) x.right = put(x.right, key, val, x.level+1);
    else { x.v = val; if (dups) x.c++; }
    x.size = 1 + size(x.left) + size(x.right);
    x.dSize = x.c + dSize(x.left) + dSize(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    x.length = length(x.left) + length(x.right) + size(x) - 1;
    return x;
  }
  
  public void putIter(Key key, Value val) {
    // this does't fix sizes for which a parent field in Node would be useful
    Node z = new Node(key, val, 1, 1, 1);
    if (root == null) { root = z;  return; }

    Node parent = null, x = root;
    while (x != null) {
        parent = x;
        int cmp = key.compareTo(x.k);
        if      (cmp < 0) x = x.left;
        else if (cmp > 0) x = x.right;
        else { x.v = val; x.c++; return; }
    }
    int cmp = key.compareTo(parent.k);
    if (cmp < 0) parent.left  = z;
    else         parent.right = z;
  }
  
  private void decrementLevels(Node x) {
    // for use in delete, deleteMin, deleteMax, rotateLeft, rotateRight
    if (x == null || x.level == 0) return;
    x.level--; pathCompares--;
    if (x.left != null) decrementLevels(x.left);
    if (x.right != null)  decrementLevels(x.right);
  }
  
  private void incrementLevels(Node x) {
    // for use in rotateLeft and rotateRight
    if (x == null || x.level == 0) return;
    x.level++; pathCompares++;
    if (x.left != null) decrementLevels(x.left);
    if (x.right != null)  decrementLevels(x.right);
  }

  public void deleteMin() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMin(root);
    assert check();
  }

  private Node deleteMin(Node x) {
    if (x.left == null) {
      if (x == root) { decrementLevels(x.right); pathCompares--; }
      else decrementLevels(x);
      return x.right;
    }
    x.left = deleteMin(x.left);
    pathCompares--;
    x.size = 1 + size(x.left) + size(x.right);
    x.dSize = x.c + dSize(x.left) + dSize(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    x.length = length(x.left) + length(x.right) + size(x) - 1;
    return x;
  }
  
  private Node deleteMinx(Node x) {
    // the original deleteMin(Node) for use in delete.
    if (root == null) throw new NoSuchElementException();
    if (x.left == null) return x.right;
    x.left = deleteMinx(x.left);
    x.size = 1 + size(x.left) + size(x.right);
    x.dSize = x.c + dSize(x.left) + dSize(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    x.length = length(x.left) + length(x.right) + size(x) - 1;
    return x;
  }
  
  public void deleteMax() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMax(root);
    assert check();
  }

  private Node deleteMax(Node x) {
    if (x.right == null) {
      if (x == root) { decrementLevels(x.left); pathCompares--; }
      else decrementLevels(x);
      return x.left;
    }
    x.right = deleteMax(x.right);
    pathCompares--; 
    x.size = 1 + size(x.left) + size(x.right);
    x.dSize = x.c + dSize(x.left) + dSize(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    x.length = length(x.left) + length(x.right) + size(x) - 1;
    return x;
  }

  public void delete(Key key) {
    if (root == null) throw new NoSuchElementException();
    if (key == null) throw new NullPointerException("key is null");
    root = delete(root, key);
  }
  
  private Node delete(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) x.left  = delete(x.left,  key);
    else if (cmp > 0) x.right = delete(x.right, key);
    else {
      if (x.left  == null  && x.right == null) { 
        pathCompares -= x.level+1; 
        return null; 
      }
      if (x.left  == null) { 
        pathCompares -= x.level+1; 
        decrementLevels(x.right);
        return x.right; 
      }
      if (x.right == null) { 
        pathCompares -= x.level+1;
        decrementLevels(x.left);
        return x.left; 
      }
      // x has 2 children
      Node t = x;
      // replace x with its successor
      Node min = min(t.right); decrementLevels(min);
      x.k = min.k; x.v = min.v; x.c = min.c;
      x.right = deleteMinx(t.right); pathCompares -= min.level+1;
      x.left = t.left;
    } 
    x.size = 1 + size(x.left) + size(x.right);
    x.dSize = x.c + dSize(x.left) + dSize(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    x.length = length(x.left) + length(x.right) + size(x) - 1;
//    System.out.println("delete return "+x);
    return x;
  }
  
  public void deleteRandom() {
    // delete a random node except if size()==1 then delete root.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.k); return; }
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()); int c = 0; Key k = null;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == r) break; }
    delete(k);
  }
  
  public void deleteMod(int i) {
    // delete the i mod size()th inOrder Node.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.k); return; }
    int c = 0; Key k = null;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == i) break; }
    delete(k);
  }
  
  public Key min() {
    if (root == null) throw new NoSuchElementException();
    return min(root).k;
  } 

  private Node min(Node x) { 
    if (x.left == null) return x; 
    else return min(x.left); 
  } 

  public Key max() {
    if (root == null) throw new NoSuchElementException();
    return max(root).k;
  } 

  private Node max(Node x) {
    if (x.right == null) return x; 
    else return max(x.right); 
  } 

  public Key floor(Key key) {
    //returns largest key <= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = floor(root, key);
    if (x == null) return null;
    else return x.k;
  } 

  private Node floor(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.k); compares++;
    if (cmp == 0) return x;
    if (cmp <  0) return floor(x.left, key);
    Node t = floor(x.right, key); 
    if (t != null) return t;
    else return x; 
  } 

  public Key ceiling(Key key) {
    // return smallest key >= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = ceiling(root, key);
    if (x == null) return null;
    else return x.k;
  }

  private Node ceiling(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.k); compares++;
    if (cmp == 0) return x;
    if (cmp < 0) { 
      Node t = ceiling(x.left, key); 
      if (t != null) return t;
      else return x; 
    } 
    return ceiling(x.right, key); 
  }
  
  public void flattenRightPreOrdered() {
    if (root == null || size() < 2) return;
    flattenRightPreOrdered(root); 
  }
  
  public void flattenRightPreOrdered(Node x) {
    // flattens x to preordered right spine
    // http://www.programcreek.com/2013/01/leetcode-flatten-binary-tree-to-linked-list/
    if (x == null || x.size < 2) return;
    Stack<Node> stack = new Stack<>();
    Node p = x;
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
  
  public void flattenRightPreOrdered2(Node x) { 
    // flattens x to preordered right spine
    //http://qa.geeksforgeeks.org/3976/flattening-a-binary-tree
    if (x == null || x.size < 2) return;
    Node node = x;
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
    Iterator<Key> it = preOrder().iterator();
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
    int x = size() +1;
    if ((x & (x - 1)) == 0) {
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
  
  public void rotateLeft() { root = rotateLeft(root); }
  
  public BSTM<Key, Value> rotateLeftAndReturn() { 
    root = rotateRight(root); return this;
  }
 
  public Node rotateLeft(Node x) {
    // rotate x to the left and return the rotated subtree
    // modified from AVLTreeST.rotateLeft(Node)
    if (x.right == null) return null;
    Node y = x.right;
    x.right = y.left;
    y.left = x;
    y.size = x.size;
    x.size = 1 + size(x.left) + size(x.right);
    x.height = 1 + Math.max(height(x.left), height(x.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));
    y.level--; decrementLevels(y.right);
    x.level++; incrementLevels(x.left);
    return y;
  }
  
  public void rotateRight() { root = rotateRight(root); }
  
  public BSTM<Key, Value> rotateRightAndReturn() { 
    root = rotateRight(root); return this;
  }
  
  public Node rotateRight(Node x) {
    // rotate x to the right and return the rotated subtree.
    // modified from AVLTreeST.rotateRight(Node)
    if (x == null) return null;
    if (x.left == null) return x;
    Node y = x.left;
    x.left = y.right;
    y.right = x;
    y.size = x.size;
    x.size = 1 + size(x.left) + size(x.right);
    x.height = 1 + Math.max(height(x.left), height(x.right));
    y.height = 1 + Math.max(height(y.left), height(y.right));
    y.level--; decrementLevels(y.left);
    x.level++; incrementLevels(x.right);
    return y;
  }

  public void flattenLeft() {
    // transforms this to a left spine using rotations.
    if (size() < 2) return;
    flattenLeft(root, null);
    long c = 1; root.level = 0;
    Node x = root;
    while (x.right != null) {
      x = x.right; 
      x.setLevel(c++);
    }
  }
  
  public void flattenLeft(Node x, Node p) {
    // transforms x to a left spine using rotations.
    // p is the parent of x or root or null the case of x == root.
    if (x == null || size() < 2) return;
    if (x == p && x != root) throw new IllegalArgumentException(
        "flattenLeft: x == p but x != root");
    if (p == null && x != root) throw new IllegalArgumentException(
        "flattenLeft: p is null but x != root");
    Node n; boolean d;
    if (x == root) {  
     while (root.right != null) root = rotateLeft(root);
     flattenLeft(root.left, root);
    } else {
      if (!(p.left != null && p.left.k == x.k 
          || p.right != null && p.right.k == x.k))
        throw new IllegalArgumentException(
          "flattenLeft: p ("+p.k+") isn't the parent of x ("+x.k+")"); 
      n = x;
      while (n.right != null)  {
        d = p.left == n ? true : false;
        n = rotateLeft(n); 
        if (d) p.left = n; else p.right = n;
      }
      flattenLeft(n.left, n);
    }
  }
  
  public Key[] flatten() { return flattenRight(); }
  
  public Key[] flattenRight() {
    // transforms this to a right spine using rotations.
    if (size() < 2) return null;
    List<Key> list = new ArrayList<>();
    flattenRight(root, null, list);
    long c = 1; root.level = 0;
    Node x = root;
    while (x.right != null) {
      x = x.right; 
      x.setLevel(c++);
    }
    return reverse(list.toArray(ofDim(kclass, 0)));
  }
  
  public void flattenRight(Node x, Node p, List<Key> list) {
    // transforms x to a right spine using rotations.
    // p is the parent of x or root or null the case of x == root.
    if (x == p && x != root) throw new IllegalArgumentException(
        "flattenRight: x == p but x != root");
    if (x == null || size() < 2) return;
    if (p == null && x != root) throw new IllegalArgumentException(
        "flattenRight: p is null but x != root");
    Node n; boolean d;
    if (x == root) {  
     while (root.left != null) {
       root = rotateRight(root);
       list.add(root.k);
     }
     flattenRight(root.right, root, list);
    } else {
      if (!(p.left != null && p.left.k == x.k 
          || p.right != null && p.right.k == x.k))
        throw new IllegalArgumentException(
          "flattenRight: p ("+p.k+") isn't the parent of x ("+x.k+")"); 
      n = x;
      while (n.left != null)  {
        d = p.right == n ? true : false;
        n = rotateRight(n);
        list.add(n.k);
        if (d) p.right = n; else p.right = n;
      }
      flattenRight(n.right, n, list);
    }
  }
  
  public void unFlatten(Key[] ka) { unFlattenRight(ka); }
  
  public void unFlattenRight(Key[] ka) {
    // unflatten this, provided it's a right spine, from data gathered
    // while converting it to a right spine using flattenRight
    if (root == null || size() < 2 || ka.length == 0) return;
    if (!isRightSpine()) throw new InvalidSTException();
    Node x, p; Key k; int c = 0;
    LOOP:
    while (c < ka.length) {
      k = root.k;
      if (k == ka[c]) {
        root = rotateLeft(root);
       if (++c == ka.length) break;
      }
      x = root;
      while (x.right != null) {
        p = x;
        x = x.right;
        k = x.k;
        if (k == ka[c]) {
          x = rotateLeft(x); 
          p.right = x;
          if (++c == ka.length) break LOOP;
        }     
      }
    }  
  }
  
  public boolean isLeftSpine() {
    if (root == null || size() < 2) return true;
    Node x = root;
    if (x.right != null) return false;
    while (x.left != null) {
      x = x.left;
      if (x.right != null) return false;  
    }
    return true;
  }
  
  public boolean isRightSpine() {
    if (root == null || size() < 2) return true;
    Node x = root;
    if (x.left != null) return false;
    while (x.right != null) {
      x = x.right;
      if (x.left != null) return false;  
    }
    return true;
  }
  
  public Key[] preSucKeys(Key key) {
    // return an array containing the inorder predecessor and successor of key.
    // the inorder predecessor of k is the key in this strictly smaller than k.
    // the inorder successor of k is the key in this strictly greater than k.
    // in either case if there is no such key null is returned.  
    if (root == null) return null;
    predecessor = null; successor = null;
    preSuc(root, key);
    Key[] ps = ofDim(root.k.getClass(), 2);
    ps[0] = predecessor; ps[1] = successor;
    return ps; 
  }
  
  public Key preKey(Key key) {
    // return the inorder predecessor of key in this..
    // the inorder predecessor of k is the key in this strictly smaller than k.
    // if there is no such key null is returned.  
    if (root == null) return null;
    predecessor = null;
    preSuc(root, key);
    return predecessor; 
  }
  
  public Key sucKey(Key key) {
    // return the inorder successor of key in this.
    // the inorder successor of k is the key in this strictly greater than k.
    // if there is no such key null is returned.  
    if (root == null) return null;
    successor = null;
    preSuc(root, key);
    return successor; 
  }
  
  private void preSuc(Node z, Key key) {
    // update the inorder predecessor and successor fields to be for key in z.
    // the predecessor of k is the greatest key in this strictly smaller than z.
    // the successor of k is the smallest key in this strictly greater than x.
    // in either case if there is no such key null is returned.  
    if (z == null)  return ;
    // If key is present at z
    if (z.k == key) {
      // the maximum value in left subtree is predecessor
      if (z.left != null) {
        Node tmp = z.left;
        while (tmp.right != null) tmp = tmp.right;
        predecessor = tmp.k;
      }
      // the minimum value in right subtree is successor
      if (z.right != null) {
        Node tmp = z.right ;
        while (tmp.left != null) tmp = tmp.left ;
        successor = tmp.k;
      }
      return ;
    }
    // If key is smaller than z's key, go to left subtree
    if (z.k.compareTo(key) > 0) {
      compares++;
      successor = z.k;
      preSuc(z.left, key) ;
    } else { // go to right subtree
      compares++;
      predecessor = z.k;
      preSuc(z.right, key) ;
    }
  }
  
  public Node[] preSuc(Key key) {
    // return an array containing the inorder predecessor and successor Nodes of x.
    // the inorder predecessor of x is the node in this with key strictly smaller than x.key.
    // the inorder successor of x is the node in this with key strictly greater than x.key.
    // in either case if there is no such Node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    // implementation is done using a Key arg in order to find predecessors or successors of 
    // a Key greater than max() or less than min() respectively.
    if (root == null) return null;
    if (key == null) return null;
    predecessor = null; successor = null;
    preSuc(root, key);
    Node p = getNode(predecessor); Node s = getNode(successor);
    if (p == null && s == null) return null;
    Node[] ps = ofDim(Node.class, 2); ps[0] = p; ps[1] = s;
    return ps; 
  }
  
  public Node pre(Key key) {
    // return the inorder predecessor Node of x in this.
    // the predecessor of x is the node in this with key strictly smaller than x.key.
    // if there is no such node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    if (root == null) return null;
    if (key == null) return null;
    predecessor = null;
    preSuc(root, key);
    return getNode(predecessor);
  }
  
  public Node suc(Key key) {
    // return the inorder successor Node of x in this.
    // the successor of x is the node in this with key strictly greater than x.key.
    // if there is no such Node null is returned.
    // ("node in this" means node directly or indirectly linked to root.)
    if (root == null) return null;
    if (key == null) return null;
    successor = null;
    preSuc(root, key);
    return getNode(successor);
  }

  public Key select(int k) {
    // return kth smallest key (key of rank k)
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = select(root, k);
    return x.k;
  }

  private Node select(Node x, int k) {
    if (x == null) return null; 
    int t = size(x.left); 
    if      (t > k) return select(x.left,  k); 
    else if (t < k) return select(x.right, k-t-1); 
    else            return x; 
  }
  
  public Key dSelect(int k) {
    if (k < 0 || k >= dSize()) throw new IllegalArgumentException();
    Node x = dSelect(root, k);
    return x.k;
  }
  
  private Node dSelect(Node x, int k) {
    if (x == null) return null; 
    int t = dSize(x.left);
    if (k > t && k < t+x.c) return x;
    else if (t > k) return dSelect(x.left,  k); 
    else if (t < k) return dSelect(x.right, k-t-x.c); 
    else            return x; 
  }
  
  public int rank(Key key) {
    // return number of keys < key
    if (key == null) throw new IllegalArgumentException("argument to rank() is null");
    return rank(key, root);
  }

  private int rank(Key key, Node x) {
    if (x == null) return 0; 
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) return rank(key, x.left); 
    else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right); 
    else              return size(x.left); 
  }
  
  public int dRank(Key key) {
    // return number of keys < key
    if (key == null) throw new IllegalArgumentException("argument to dRank() is null");
    return dRank(key, root);
  }
  
  private int dRank(Key key, Node x) {
    if (x == null) return 0; 
    int cmp = key.compareTo(x.k); compares++;
    if      (cmp < 0) return dRank(key, x.left); 
    else if (cmp > 0) return x.c + dSize(x.left) + dRank(key, x.right); 
    else              return dSize(x.left); 
  }
  
  public Iterable<Key> keys() {
    // inorder traversal
    return keys(min(), max());
  }

  public Iterable<Key> keys(Key lo, Key hi) {
    // inorder traversal
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<Key> queue = new Queue<Key>();
    keys(root, queue, lo, hi);
    return queue;
  } 

  private void keys(Node x, Queue<Key> queue, Key lo, Key hi) { 
    if (x == null) return; 
    int cmplo = lo.compareTo(x.k); compares++;
    int cmphi = hi.compareTo(x.k); compares++; 
    if (cmplo < 0) keys(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.k); 
    if (cmphi > 0) keys(x.right, queue, lo, hi); 
  }
  
  public Iterable<Key> allKeys() {
    // inorder traversal including virtual duplicates
    return allKeys(min(), max());
  }

  public Iterable<Key> allKeys(Key lo, Key hi) {
    // inorder traversal including virtual duplicates
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<Key> queue = new Queue<Key>();
    allKeys(root, queue, lo, hi);
    return queue;
  } 

  private void allKeys(Node x, Queue<Key> queue, Key lo, Key hi) { 
    //  including virtual duplicates
    if (x == null) return; 
    int cmplo = lo.compareTo(x.k); compares++;
    int cmphi = hi.compareTo(x.k); compares++; 
    if (cmplo < 0) allKeys(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) for (int i = 0; i < x.c; i++) queue.enqueue(x.k); 
    if (cmphi > 0) allKeys(x.right, queue, lo, hi); 
  }
  
  public Iterable<Key> keys2(Key lo, Key hi) {
    // inorder traversal
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<Key> queue = new Queue<Key>();
    keys2(root, queue, lo, hi);
    return queue;
  } 

  private void keys2(Node x, Queue<Key> queue, Key lo, Key hi) { 
    if (x == null) return; 
    int cmplo = x.k.compareTo(lo); compares++;
    int cmphi = x.k.compareTo(hi); compares++; 
    if (cmplo > 0) keys2(x.left, queue, lo, hi); 
    if (cmplo >= 0 && cmphi <= 0) queue.enqueue(x.k); 
    if (cmphi < 0) keys2(x.right, queue, lo, hi); 
  } 

  public boolean check() {
    if (!isBST()) System.out.println("Not in symmetric order");
    if (!isSizeConsistent())  System.out.println("sizes not consistent");
    if (!isRankConsistent())  System.out.println("Ranks not consistent");
    if (!isDsizeConsistent()) System.out.println("dSizes not consistent");
    if (!isDrankConsistent()) System.out.println("dRanks not consistent");
    return isBST() && isSizeConsistent() && isRankConsistent()
        && isDsizeConsistent() && isDrankConsistent();
  }
  
  // are the size fields correct?
  private boolean isSizeConsistent() { return isSizeConsistent(root); }
  
  private boolean isSizeConsistent(Node x) {
    if (x == null) return true;
    if (x.size != size(x.left) + size(x.right) + 1) return false;
    return isSizeConsistent(x.left) && isSizeConsistent(x.right);
  }
  
  public boolean isDsizeConsistent() { return isDsizeConsistent(root); }
  
  private boolean isDsizeConsistent(Node x) {
    if (x == null) return true;
    if (x.dSize != dSize(x.left) + dSize(x.right) + x.c) return false;
    return isDsizeConsistent(x.left) && isDsizeConsistent(x.right);
  }
  
  // check that ranks are consistent
  private boolean isRankConsistent() {
    if (root == null) return true;
    for (int i = 0; i < size(); i++)
      if (i != rank(select(i))) return false;
    for (Key key : keys()) {
      if (key.compareTo(select(rank(key))) != 0) { compares++; return false; }
      compares++;
    }
    return true;
  }
  
  public boolean isDrankConsistent() {
    if (root == null) return true;
    for (int i = 0; i < dSize(); i++) {
      int c = getNode(dSelect(i)).c; 
      for (int j = 0; j < c; j++)
        if (i != dRank(dSelect(i+j))) return false;
      i += c-1;
    }
    for (Key key : keys()) {
      if (key.compareTo(dSelect(dRank(key))) != 0) { compares++; return false; }
      compares++;
    }
    return true;
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
    if (x == null) return true;
    if (min != null && x.k.compareTo(min) <= 0) { compares++; return false; }
    compares++;
    if (max != null && x.k.compareTo(max) >= 0) { compares++; return false; }
    compares++;
    return isBST(x.left, min, x.k) && isBST(x.right, x.k, max);
  }
  
  public int nodeHeight() { 
    if (root == null) return -1;
    return root.height; 
  }
  
  public int nodeHeight(Node x) { 
    if (x == null) return -1;
    return x.height; 
  }
  
  public int nodeHeight(Key k) {
    if (k == null) return -1;
    Node x = getNode(k);
    if (x == null) return -1;
    return x.height; 
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
    return x.k;
  }
  
  public Key getUniformRandomKey() {
    if (root == null) throw new NoSuchElementException();
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()), c = 0; Key k;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == r) return k;}
    return null;
  }
  
   public Key getKey(Node x) {
     if (x == null) throw new IllegalArgumentException("getKey: Node arg is null");
     return x.k;
   }
   
   public int getHeight(Key k) {
     if (k == null) throw new IllegalArgumentException("getHeight: Key arg is null");
     return getNode(k).height;
   }
   
   public int getMaxLevel() {
     int max = 0; int l;
     Iterator<Key> it = inOrder().iterator();
     while (it.hasNext()) {
       Key k = it.next();
       l = getLevel(k);
       if (l > max) max = l;
     }
     return max;
   }

  public int getLevel(Key k) { 
    // return the level of the node with key k 
    // in root or -1 if no such node is found.
    return getLevelPlus1(root, k, 1)-1; 
  }
  
  public int getLevel(Node x, Key k) {
    // return the level of the node with key k 
    // in x or 0 if no such node is found.
    return getLevelPlus1(x, k, 1)-1; 
  }
  
  private int getLevelPlus1(Node x, Key k, int level) {
    // return the level of the node with key k in x 
    // or 0 if a node with k isn't found.
    if (x == null) return 0;
    if (x.k.equals(k)) return level;
    int next = getLevelPlus1(x.left, k, level+1);
    if (next != 0) return next;
    next = getLevelPlus1(x.right, k, level+1);
    return next;
  }
  
  public int getLevel(Node y) { 
    // return the level of y in root or -1 if a 
    // node with key.equals(y.key) isn't found.
    return getLevelPlus1(root, y, 1)-1; 
  }
  
  public int getLevel(Node x, Node y) {
    // return the level of y in x or -1 if a
    // node with key.equals(y.key) isn't found.
    return getLevelPlus1(x, y, 1)-1; 
  }
  
  private int getLevelPlus1(Node x, Node y, int level) {
    // return the level of y in x or 0 if a node
    // with key.equals(y.key) isn't found.
    if (x == null) return 0;
    if (x.k.equals(y.k)) return level;
    int next = getLevelPlus1(x.left, y, level+1);
    if (next != 0) return next;
    next = getLevelPlus1(x.right, y, level+1);
    return next;
  }
  
  public boolean isBestCase() {
    // return true iff this is best case defined as all non-leaf
    // nodes have 2 children and all leaves are at the same depth
    // else return false. definition from
    // http://pages.cs.wisc.edu/~vernon/cs367/notes/9.BST.html.
    Iterator<Key> it = preOrder().iterator();
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
    Iterator<Key> it = preOrder().iterator();
    while (it.hasNext()) {
      Node n = getNode(it.next());
      if (n.left != null && n.right != null) return false;
    }
    return true;    
  }
  
  private Node lca(Node x, Node y, Node z) {
    // return the Node that's lowest common ancestor of y and z in x.
    if(x == null || y == null || z == null) return null;
    if(x.k.equals(y.k) || x.k.equals(z.k)) return x;
    Node left = lca(x.left, y, z);
    Node right = lca(x.right, y, z);
    if(left != null && right != null) return x;
    if(left == null)
    return right;
    else return left;  
  }
  
  public Key lca(Key a, Key b) {
    // return the key of the lowest common ancestor 
    // of the nodes with keys a and b in this.
    Node x = lca(root, getNode(a), getNode(b));
    if (x == null) return null;
    return x.k;
  }
  
  public Iterable<Key> preOrder() {
    // return a preordered iterable of the keys in this bst.
    Queue<Key> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> stack = new Stack<>();
    stack.push(root);
    while(!stack.isEmpty()) {
      Node n = stack.pop();
      q.enqueue(n.k);
      if (n.right != null) { stack.push(n.right); }
      if (n.left  != null) { stack.push(n.left); }
    }
    return q;
  }
  
  public Key[] toPreOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = preOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
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
  
  public void showPreOrderR(Node x) {
    // print the key:values in x in preorder recursively.
    if(x ==  null) return;
    System.out.print(x.k+":"+x.v+" ");
    showPreOrderR(x.left);
    showPreOrderR(x.right);
  }
  
  public void showPreOrder(Node x) {
    if(x == null) return;
    Stack<Node> stack = new Stack<>();
    stack.push(x);
    while(!stack.isEmpty()) {
      Node n = stack.pop();
      System.out.print(n.k+":"+n.v+" ");
      if (n.right != null) { stack.push(n.right); }
      if (n.left  != null) { stack.push(n.left); }
    }
  }
  
  public Iterable<Key> inOrder() {
    // return an inordered iterable of the keys in this bst.
    Queue<Key> q = new Queue<Key>();
    if (root == null) return q;
    Stack<Node> s = new Stack<Node>();
    Node x = root;
    while(!s.isEmpty() || x!=null) {
      if (x != null) {
        s.push(x);
        x = x.left;
      } else {
        x = s.pop();
        q.enqueue(x.k);
        x = x.right;
      }
    }
    return q;
  }
  
  public Key[] toInOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = inOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }
  
  public Key[] toAllInOrderArray() {
    // includes virtual duplicates
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, dSize());
    Iterator<Key> it = allKeys().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }
  
  public void show() { showInOrder(); }
  
  public void showInOrder() {
    showInOrder(root);
    System.out.println();
  }
  
  public void showInOrder(Node x) {
    if(x == null) return;
    Stack<Node> s = new Stack<>();
    Node currentNode=x;
    while(!s.isEmpty() || currentNode!=null) {
      if(currentNode!=null) {
        s.push(currentNode);
        currentNode=currentNode.left;
      } else {
        Node n=s.pop();
        System.out.print(n.k+":"+n.v+" ");
        currentNode=n.right;
      }
    }
  }
  
  public void showInOrderR() {
    showInOrderR(root);
    System.out.println();
  }
  
  public void showInOrderR(Node x) {
    if(x ==  null) return;
    showInOrderR(x.left);
    System.out.print(x.k+":"+x.v+" ");
    showInOrderR(x.right);
  }
  
  public Iterable<Key> reverseInOrder() {
    // return a reverse inordered iterable of the keys in this bst.
    Stack<Key> q = new Stack<Key>();
    if (root == null) return q;
    Stack<Node> s = new Stack<Node>();
    Node x = root;
    while(!s.isEmpty() || x!=null) {
      if (x != null) {
        s.push(x);
        x = x.left;
      } else {
        x = s.pop();
        q.push(x.k);
        x = x.right;
      }
    }
    return q;
  }
  
  public Key[] toReverseInOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = reverseInOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }
  
  public Iterable<Key> postOrder() {
    // return a postordered iterable of the keys in this bst.
    Queue<Key> q = new Queue<>();
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
        q.enqueue(current.k);
        current = null;
      }
    }
  }
  
  public Key[] toPostOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = postOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }
  
  public void showPostOrderR() {
    showPostOrderR(root);
    System.out.println();
  }
 
  public void showPostOrderR(Node x) {
    if(x !=  null) {
      showPostOrderR(x.left);
      showPostOrderR(x.right);
      System.out.print(x.k+":"+x.v+" ");
    }
  }

  public void showPostOrder() {
    showPostOrder(root);
    System.out.println();
  }
  
  public void showPostOrder(Node x) {
    if(x == null) return;    
    Stack<Node> s = new Stack<>();
    Node current = x;
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
        System.out.print(current.k+":"+current.v+" ");
        current = null;
      }
    }
  }
  
  public Iterable<Key> spiralOrder() {
    // return a spiral-ordered iterable of the keys in this bst.
    // spiral order is like level-order but reversed every other level.
    Queue<Key> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> stack = new Stack<>();
    stack.push(root);  
    boolean d = false;
    while(!stack.isEmpty()) {
      Stack<Node> tstack = new Stack<>();  
      while(!stack.isEmpty()) {
        Node tnode = stack.pop();
        q.enqueue(tnode.k);
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
  
  public Key[] toSpiralOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = spiralOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }
  
  public void showSpiralOrder() {
    showSpiralOrder(root);
    System.out.println();
  }
  
  public void showSpiralOrder(Node x) {
    if(x == null) return;
    Stack<Node> stack = new Stack<>();
    stack.push(x);  
    boolean d = false;
    while(!stack.isEmpty()) {
      Stack<Node> tstack = new Stack<>();  
      while(!stack.isEmpty()) {
        Node tnode = stack.pop();
        System.out.print(tnode.k+":"+tnode.v+" ");
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
  
  public Iterable<Key> levelOrder() {
    // return a level-ordered iterable of the keys in this bst.
    Queue<Key> keys = new Queue<>();
    Queue<Node> queue = new Queue<>();
    queue.enqueue(root);
    while (!queue.isEmpty()) {
      Node x = queue.dequeue();
      if (x == null) continue;
      keys.enqueue(x.k);
      queue.enqueue(x.left);
      queue.enqueue(x.right);
    }
//    par(keys.toArray());
    return keys;
  }
  
  public Key[] toLevelOrderArray() {
    if (size() == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = levelOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }

  public void showLevelOrder() {
    // print the key:values in this bst in level order.
    if (root == null) { System.out.println("bst empty"); return; }
    Iterator<Key> it = levelOrder().iterator(); Key k;
    while (it.hasNext()) { k = it.next(); System.out.print(k+":"+get(k)+" "); }
    System.out.println();    
  }

  @SafeVarargs
  public final void printTree(int...x) {
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
      return node.k + " ";
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
    // after initial tests equality is based on preordered Keys ignoring
    // values and fields.
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("rawtypes")
    BSTM other = (BSTM) obj;
    if (Arrays.equals(toPreOrderArray(), other.toPreOrderArray())) return true;
    return false;
  }

  @Override
  public String toString() {
    if (root == null) return "()";
    Key[] a = toPreOrderArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++) {
      sb.append(getNode(a[i])+" ");
    }
    return sb.substring(0,sb.length()-1).toString();
  }
  
  public String tos() {
    if (root == null) return "{}";
    Key[] a = toInOrderArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < a.length; i++) {
      sb.append(getNode(a[i]).toString2()+",");
    }
    return sb.replace(sb.length()-1,sb.length(),"}").toString();
  }
  
  public static <K extends Comparable<? super K>, V> void transform(BSTM<K,V> a, BSTM<K,V> b) {
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
    BSTM<K,V> bb = new BSTM<K,V>(b);
    a.flatten();
    a.unFlatten(bb.flatten());
    assert a.equals(b);
  }

  public static void main(String[] args) {

//    Integer[] a = new Integer[]{8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 20, 15};
//    Integer[] b = rangeInteger(0,a.length);
//    BSTxEx3206Ex3207<Integer, Integer> bst = new BSTxEx3206Ex3207<>();
//    for (int i = 0; i < a.length; i++) bst.put(a[i],b[i]);
//    System.out.println("bst:");
//    bst.printTree();
///*  bst:
//                             /----- 20
//                             |       \----- 15
//                     /----- 14
//                     |       \----- 13
//             /----- 12
//             |       |       /----- 11
//             |       \----- 10
//             |               \----- 9
//     \----- 8
//             |               /----- 7
//             |       /----- 6
//             |       |       \----- 5
//             \----- 4
//                     |       /----- 3
//                     \----- 2
//                             \----- 1     
//*/ 
//    System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares()); //54
//    System.out.println("avgCompares="+bst.avgCompares());   //3.375
//    System.out.println("avgComparesR="+bst.avgComparesR()); //3.375
//    bst.setCompares(0);
//    bst.get(7); 
//    System.out.println(bst.getCompares()); //4
//    System.out.println("getLevel(7)="+bst.getLevel(7)); //3
//    System.out.println(bst.getNode(7).toString()); //(key=7, val=10, size=1, height=0, level=3)
//    System.out.println(bst.getLevel(bst.getRoot())); //0
//    bst.printLevels();
//    Iterator<Integer> it = bst.levelOrder().iterator();
//    while (it.hasNext()) {
//      Integer x = it.next();
////      System.out.println(x+" "+bst.getLevel(x));
//      System.out.println(bst.getNode(x));
//    }
//    
//    System.out.println("\n");
//    
//    Integer[] a = new Integer[]{8,4,12,2,6,10,14,1,3,5,7,9,11,13,20,15};
//    Integer[] b = rangeInteger(0,a.length);
//    Iterator<Integer> it;
//    BSTxEx3206Ex3207<Integer, Integer> bst = new BSTxEx3206Ex3207<>(a,b);
//    bst.printTree();
//    System.out.println("pathCompares = "+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();
//    bst.deleteMin();
//    System.out.println("pathCompares = "+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree();

//    Integer[] c = new Integer[]{8,4,12,2,6,10,14,1,3,5,7,9,11,13,20,15};
//    Integer[] d = rangeInteger(0,c.length);
//    Integer[] e =  c.clone();
//    SecureRandom r = new SecureRandom();
//    shuffle(c,r);
//    par(c);
//    shuffle(e,r);
//    par(e);
//    c = new Integer[]{7,8,10,2,1,3,9,6,20,15,5,13,12,4,14,11};
//    e = new Integer[]{20,8,11,6,10,1,12,15,14,13,7,3,9,2,4,5};
//    c = new Integer[]{13,12,7,10,20,15,5,9,11,3,6,14,8,4,2,1};
//    e = new Integer[]{4,5,6,1,7,2,12,3,9,10,15,11,13,20,8,14};
    
//    BSTxEx3206Ex3207<Integer, Integer> bst2 = new BSTxEx3206Ex3207<>(c,d);
//    bst2.printTree(); System.out.println();
//    Iterator<Integer> it = bst2.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst2.getNode(it.next())+" "); System.out.println();
//    System.out.println("pathCompares="+bst2.getPathCompares());

//    bst2.delete(20); System.out.println("\ndeleting 20\n");
//    it = bst2.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst2.getNode(it.next())+" "); System.out.println();
//    System.out.println(bst2.avgCompares()+" "+bst2.avgComparesR());
//    System.out.println("pathCompares="+bst2.getPathCompares());
//    bst2.printTree(); System.out.println();
//    System.out.println(bst2.avgCompares()+" "+bst2.avgComparesR());
//    int x = 7;
//    System.out.print("preSuc "+x+" ");par(bst2.preSuc(x));
//    bst2.delete(x); System.out.println("\ndeleting "+x+"\n");
//    it = bst2.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst2.getNode(it.next())+" "); System.out.println();
//    System.out.println(bst2.avgCompares()+" "+bst2.avgComparesR());
//    System.out.println("pathCompares="+bst2.getPathCompares());
//    bst2.printTree(); System.out.println();  
        
//  p = 6;
//  System.out.println("deleting "+6); bst.delete(6);
//  it = bst.levelOrder().iterator();
//  while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//  bst.printTree(); System.out.println();
//  System.out.println("pathCompares="+bst.getPathCompares());
//  System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//  it = bst.levelOrder().iterator();
//  while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//  assert bst.avgCompares() == bst.avgComparesR();
//
//  System.exit(0);
    
//    p = 3; 
//    System.out.println("deleting "+p); bst.delete(p);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    bst.printTree(); System.out.println();    
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//
//    p = 4;
//    System.out.println("deleting "+p); bst.delete(p);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//
//    p = 6;
//    System.out.println("deleting "+6); bst.delete(6);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//
//    p = 14;
//    System.out.println("deleting "+p); bst.delete(p);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    System.exit(0);
//    
//    p = 11;
//    System.out.println("deleting "+p); bst.delete(p);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    System.exit(0);
//
//    p = 20;
//    System.out.println("deleting "+p); bst.delete(p);
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    System.exit(0);
//
//    p = 12;
//    System.out.println("deleting "+p); bst.delete(p);
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    System.exit(0);
//
//    p = 10;
//    System.out.println("deleting "+p); bst.delete(p);
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    it = bst.levelOrder().iterator();
//    System.exit(0);
  
//  while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");    
//    for (Integer i : e) {
//      System.out.println("deleting "+i); bst.delete(i); 
//      System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    }
//    
//    Integer[] u = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
//    Integer[] v = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
//    Iterator<Integer> it;
//    BSTM<Integer, Integer> bst = new BSTM<>(u,v);
//    
    // testing size and height implementations
//     bst = new BSTX<>();
//    // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//    // System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//    assert bst.size() == bst.sizeI();
//    assert bst.size() == bst.sizeR();
//    assert bst.height() == bst.heightI();
//    assert bst.height() == bst.heightR();
//    System.out.println("put cycle");
//    for (int i = 0; i< u.length; i++) {
//      bst.put(u[i], v[i]);
//      // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//      assert bst.size() == bst.sizeI();
//      assert bst.size() == bst.sizeR();
//      // System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//      assert bst.height() == bst.heightI();
//      assert bst.height() == bst.heightR();
//    }
//    System.out.println("delete cycle");
//    while(!bst.isEmpty()) {
//      bst.deleteMax();
//      // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//      assert bst.size() == bst.sizeI();
//      assert bst.size() == bst.sizeR();
//      // System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//      assert bst.height() == bst.heightI();
//      assert bst.height() == bst.heightR();     
//    }
//    System.exit(0);
//    
//    bst.printTree(); System.out.println();
//    System.out.println("size="+bst.size());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//    
//    for (int i = 0; i < v.length; i++) {
//      System.out.println("deleting "+v[i]); bst.delete(v[i]);
//      System.out.println("size="+bst.size());
//      System.out.println("pathCompares="+bst.getPathCompares());
//      //System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//      System.out.println("compares: "+bst.avgCompares()+" "+bst.avgComparesR()+" "+bst.avgCompares2());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//      bst.printTree(); System.out.println();
//      System.out.println("pathCompares="+bst.getPathCompares());
//      System.out.println("compares: "+bst.avgCompares()+" "+bst.avgComparesR()+" "+bst.avgCompares2());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//      assert bst.avgCompares() == bst.avgComparesR();
//      assert abs(bst.avgCompares() - bst.avgCompares2()) < pow(10,-14);
//    }
    
    String[] sa = "E A S Y Q U T I O N".split("\\s+");
    Character[] ca = new Character[sa.length];
    for (int i = 0; i < sa.length; i++) ca[i] = sa[i].charAt(0);
    Integer[] ia = rangeInteger(0,sa.length);
    BSTM<Character, Integer> h = new BSTM<>(ca,ia);
    System.out.print("h = "+h.tos()+"\n");
    System.out.println("rank('O') = "+h.rank('O'));
    System.out.println("dRank('O') = "+h.dRank('O'));
    
    h.printTree();
    for (Character key : h.keys()) System.out.print(key+" "); System.out.println();
//    for (Character key : h.keys()) System.out.print(h.size(h.getNode(key))+" "); System.out.println();
//    for (Character key : h.keys()) System.out.print(h.dSize(h.getNode(key))+" "); System.out.println();
    for (Character key : h.keys()) System.out.print(h.getNode(key).toString2()+" "); System.out.println();
    h.put('O', 13); h.put('O', 14);
    for (Character key : h.keys()) System.out.print(h.getNode(key).toString2()+" "); System.out.println();

//    System.exit(0);
    
//    System.out.println("h.size() = "+h.size());
//    System.out.println("h.dSize() = "+h.dSize());
//    System.out.println("h.sizeR() = "+h.sizeR());
//    System.out.println("h.sizeI() = "+h.sizeI());
//    System.out.println("h.dSizeR() = "+h.dSizeR());
//    System.out.println("h.dSizeI() = "+h.dSizeI());
    System.out.println("h.put('E', 11); h.put('E', 12);");
    h.put('E', 11); h.put('E', 12);
    System.out.print("h = "+h.tos()+"\n");

    System.out.println("rank('O') = "+h.rank('O'));
    System.out.println("dRank('O') = "+h.dRank('O'));
    System.out.println("h.put('O', 13); h.put('O', 14); h.put('O', 15);");
    h.put('O', 13); h.put('O', 14); h.put('O', 15);
    System.out.print("h = "+h.tos()+"\n");

    System.out.println("rank('O') = "+h.rank('O'));
    System.out.println("dRank('O') = "+h.dRank('O'));
    
    System.out.print("h = "+h.tos()+"\n");
    for (Character key : h.keys()) System.out.print(h.dRank(key)+" "); System.out.println();
    System.out.println("");
//    h.printTree();
    
    System.out.println("dSelect(2) = "+h.dSelect(3));
    System.out.println("dSelect(3) = "+h.dSelect(3));
    System.out.println("dSelect(4) = "+h.dSelect(3));
    System.out.println("select(4) = "+h.select(4));
    System.out.println("dSelect(6) = "+h.dSelect(6));
    
    System.out.print("h = "+h.tos()+"\n");
    for (Character key : h.allKeys()) System.out.print(key+" "); System.out.println();
    
//    for (Character key : h.keys()) {
//      System.out.print(key+":h.dRank(key) = "+h.dRank(key)+"\t");
//      System.out.println(key+":h.rank(key) = "+h.rank(key));
//      System.out.println("h.dSelect(h.dRank(key)) = "+h.dSelect(h.dRank(key)));
//      System.out.println(key+":"+h.dSelect(h.dRank(key)));
//    }
//    System.out.println("h.dSelect(14) = "+h.dSelect(14));
      
    for (int i = 0; i < h.dSize(); i++) System.out.print(h.dRank(h.dSelect(i))+" ");
    System.out.println();
    
    System.out.println("h.size('A', 'Q') = "+h.size('A', 'Q'));
    System.out.println("h.dSize('A', 'Q') = "+h.dSize('A', 'Q'));

//      if (i != dRank(dSelect(i))) return false;
    
//    System.out.println("h.dRank('I') = "+h.dRank('I'));
//    System.out.println("h.dSelect(h.dRank('I')) = "+h.dSelect(h.dRank('I')));
//    
//    
//    System.out.println("isDrankConsistent() = "+h.isDrankConsistent());
//    System.out.println("isDsizeConsistent() = "+h.isDsizeConsistent());
    

//    System.out.print("h = "+h.tos()+"\n");
//    System.out.println("h.size() = "+h.size());
//    System.out.println("h.dSize() = "+h.dSize());
//    System.out.println("h.sizeR() = "+h.sizeR());
//    System.out.println("h.sizeI() = "+h.sizeI());
//    System.out.println("h.dSizeR() = "+h.dSizeR());
//    System.out.println("h.dSizeI() = "+h.dSizeI());
//    System.out.println("h.delete('E')"); h.delete('E');
//    System.out.print("h = "+h.tos()+"\n");
//    System.out.println("h.size() = "+h.size());
//    System.out.println("h.dSize() = "+h.dSize());
//    System.out.println("h.sizeR() = "+h.sizeR());
//    System.out.println("h.sizeI() = "+h.sizeI());
//    System.out.println("h.dSizeR() = "+h.dSizeR());
//    System.out.println("h.dSizeI() = "+h.dSizeI());

    
//    System.out.println("h:");h.printTree();
//    BSTM<Character, Integer> h2 = h.rotateRightAndReturn();
//    transform(h,h2);
//    System.out.println("h2:");h.printTree();
//    System.out.println("h:");h.printTree();
//    System.out.print("h2 = "+h2.tos()+"\n");
//    System.out.print("h = "+h.tos()+"\n");

    

  }
}

