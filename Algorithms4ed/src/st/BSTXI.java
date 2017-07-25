package st;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;

import ds.Queue;
import ds.Stack;
//import edu.princeton.cs.algs4.StdIn;

// based on http://algs4.cs.princeton.edu/32bst/BST.java
// this class used for exercises 3206, 3207

@SuppressWarnings("unused")
public class BSTXI<Key extends Comparable<Key>, Value> {
  private Node root;             // root of BST
  private Key successor = null;
  private Key predecessor = null;
  private long compares = 0;
  private long pathCompares = 0; // total compares to reach all nodes from root
  private SecureRandom rnd = new SecureRandom(); // for delete
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = null; // Value class

  private class Node {
    private Key key;           // sorted by key
    private Value val;         // associated data
    private Node left, right;  // left and right subtrees
    private int size;          // number of nodes in subtree
    private int height = 0;
    private long level = 0;

    public Node(Key key, Value val, int size) {
      this.key = key;
      this.val = val;
      this.size = size;
    }
    
    public Node(Key key, Value val, int size, long level) {
      this.key = key;
      this.val = val;
      this.size = size;
      this.level = level;
    }

//    @Override public String toString() {
//      return "("+key+":"+val+")";
//    }
    
    @Override public String toString() {
      return "("+key+":"+level+":"+size+":"+height+")";
    }
    
//    @Override
//    public String toString() {
//      StringBuilder builder = new StringBuilder();
//      builder.append("(key=");
//      builder.append(key);
//      builder.append(", val=");
//      builder.append(val);
//      builder.append(", size=");
//      builder.append(size);
//      builder.append(", height=");
//      builder.append(height);
//      builder.append(", level=");
//      builder.append(level);
//      builder.append(")");
//      return builder.toString();
//    }

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
        System.out.print(key.toString());
      }
      System.out.println();
    }
  }

  public BSTXI() { setRndSeed(); }

  public BSTXI(Key[] ka, Value[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    setRndSeed();
    int n = Math.min(ka.length, va.length); int c = 0;
    kclass = ka.getClass().getComponentType();
    vclass = va.getClass().getComponentType();
    Key[] kz = ofDim(kclass, n);
    for (int  i = 0; i < n; i++) {
      if (c == n) break;
      if (ka[i] != null) { kz[c++] = ka[i]; }
    }
    if (c == 0) return;
    if (c < n) { n = c; kz = take(kz,c); }
    Value[] vz = ofDim(vclass, n); c = 0;
    for (int  i = 0; i < n; i++) {
      if (c == n) break;
      if (va[i] != null) { vz[c++] = va[i]; }
    }
    if (c == 0) return;
    if (c < n) { n = c; kz = take(kz,c); vz = take(vz,c); }
    for (int  i = 0; i < n; i++) put(kz[i], vz[i]);
  }
  
  public Node getRoot() { return root; }
  
  public void setCompares(long h) { compares = h; }
  
  public long getCompares() { return compares; }

  public boolean isEmpty() { return root == null; }
  
  public void setRndSeed() { rnd.setSeed(System.currentTimeMillis()); }
  
  public void setRndSeed(long s) { rnd.setSeed(s); }

  public int size() { if (root == null) return 0; return root.size; }
  
  public int size(Node x) { if (x == null) return 0; else return x.size; }
  // prints keys traversed for ex3215
  
  public int sizePrint() {
    // prints keys traversed for ex3215
    if (root == null) return 0; 
    return root.size; 
  }
  
  public int sizePrint(Node x) {
    // prints keys traversed for ex3215
    if (x == null) return 0; 
    System.out.print(x.key+" ");
    return x.size; 
  }
  
  public int size(Key k) { if (k == null) return 0; else return getNode(k).size; }
  
  public int size(Key lo, Key hi) {
    // return number of keys between lo and hi inclusive
    if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

    if (lo.compareTo(hi) > 0) { compares++; return 0; }
    compares++;
    if (contains(hi)) return rank(hi) - rank(lo) + 1;
    else              return rank(hi) - rank(lo);
  }
  
  public int sizePrint(Key lo, Key hi) {
    // prints keys traversed for ex3215
    if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
    if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

    if (lo.compareTo(hi) > 0) { compares++; return 0; }
    compares++;
    if (containsPrint(hi)) return rankRprint(hi) - rankRprint(lo) + 1;
    else              return rankRprint(hi) - rankRprint(lo);
  }
  
  public int sizeR() { return sizeR(root); }
  
  public int sizeR(Node node) {
    // prints keys traversed for ex3215
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
        Node x = q.peek();
        q.dequeue();
        if (x.left != null) q.enqueue(x.left);
        if (x.right != null) q.enqueue(x.right);
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
  
  public boolean contains(Key key) {
    if (key == null) throw new IllegalArgumentException();
    return get(key) != null;
  }
  
  public boolean containsPrint(Key key) {
    if (key == null) throw new IllegalArgumentException();
    return getRprint(key) != null;
  }
  
  public Value get(Key key) {
    // iterative
    Node x = root;
    while (x != null) {
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x = x.left;
        else if (cmp > 0) x = x.right;
        else return x.val;
    }
    return null;
  }

  public Value getR(Key key) { return getR(root, key); } 

  private Value getR(Node x, Key key) {
    // recursive
    if (x == null || key == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) return getR(x.left, key);
    else if (cmp > 0) return getR(x.right, key);
    else              return x.val;
  }
  
  public Value getRprint(Key key) { return getRprint(root, key); } 

  private Value getRprint(Node x, Key key) {
    // recursive
    if (x == null || key == null) return null;
    System.out.print(x.key+" ");
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) return getRprint(x.left, key);
    else if (cmp > 0) return getRprint(x.right, key);
    else              return x.val;
  }
  
  public Node getNode(Key key) {
    // iterative
    Node x = root;
    while (x != null) {
        int cmp = key.compareTo(x.key); compares++;
        if      (cmp < 0) x = x.left;
        else if (cmp > 0) x = x.right;
        else return x;
    }
    return null;
  }
  
  public Node getNodeR(Key key) { return getNodeR(root, key); }
  
  private Node getNodeR(Node x, Key key) {
    // for use with lca, recursive
    if (x == null || key == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) return getNodeR(x.left, key);
    else if (cmp > 0) return getNodeR(x.right, key);
    else              return x;
  }
  
  public void put(Key key, Value val) {
    // iterative
    if (key == null) throw new IllegalArgumentException();
    if (val == null) { delete(key); return; }
    if (kclass == null) kclass = key.getClass();
    if (vclass == null) vclass = val.getClass();
    Node z = new Node(key, val, 1);
    if (root == null) { root = z;  pathCompares++; return; }
    Node parent = null, x = root;  int l = 0;
    Set<Node> set = new HashSet<>(); Iterator<Node> it;
    
    while (x != null) {
      parent = x; parent.height = 0;
      it = set.iterator();
      while (it.hasNext()) it.next().height++;
      int cmp = key.compareTo(x.key); compares++;
      if (cmp < 0) {
        x = x.left; l++;
        if (x != null) { parent.size++; set.add(parent); }
      } else if (cmp > 0) { 
        x = x.right; l++; 
        if (x != null) { parent.size++; set.add(parent); }
      } else {
        x.val = val; x.level = l; pathCompares+=(l+1);
        return; 
      }
    }
    
    it = set.iterator();
    while (it.hasNext()) it.next().height++;
    z.level = l; pathCompares+=(l+1); parent.size++; parent.height++;

    int cmp = key.compareTo(parent.key);
    if (cmp < 0) parent.left  = z;
    else         parent.right = z;
  }

  public void putR(Key key, Value val) {
    // recursive
    if (key == null) throw new IllegalArgumentException();
    if (val == null) { delete(key); return; }
    if (kclass == null) kclass = key.getClass();
    if (vclass == null) vclass = val.getClass();
    root = putR(root, key, val, 0);
    assert check();
  }
  
  private Node putR(Node x, Key key, Value val, long level) {
    // recursive
    if (x == null) { pathCompares+=(level+1); return new Node(key, val, 1, level); }
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) x.left  = putR(x.left,  key, val, x.level+1);
    else if (cmp > 0) x.right = putR(x.right, key, val, x.level+1);
    else              x.val   = val;
    x.size = 1 + size(x.left) + size(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    return x;
  }
  
  private void decrementLevels(Node x) {
    // for use in delete, deleteMin, deleteMax
    if (x == null || x.level == 0) return;
    x.level--; pathCompares--;
    if (x.left != null) decrementLevels(x.left);
    if (x.right != null)  decrementLevels(x.right);
  }
  
//  private void fixLevels(Node x) {
//    if (x == null || x.level == 0 ) return;  
//    if (x.left != null) {
//      pathCompares += x.level+1-x.left.level; 
//      x.left.level = x.level+1;
//      fixLevels(x.left);
//    }
//    if (x.right != null) {
//      pathCompares += x.level+1-x.right.level; 
//      x.right.level = x.level+1;
//      fixLevels(x.right);
//    }
//  }

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
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    return x;
  }
  
  private Node deleteMinx(Node x) {
    // the original deleteMin(Node) for use in delete.
    if (root == null) throw new NoSuchElementException();
    if (x.left == null) return x.right;
    x.left = deleteMinx(x.left);
    x.size = 1 + size(x.left) + size(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
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
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    return x;
  }
  
  private Node deleteMaxx(Node x) {
    // the original deleteMax(Node) for use in delete.
    if (x.right == null) return x.left;
    x.size = 1 + size(x.left) + size(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
    return x;
  }

  public void delete(Key key) {
    if (root == null) throw new NoSuchElementException();
    if (key == null) throw new NullPointerException("key is null");
    root = delete(root, key);
  }
  
  private Node delete(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
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
      x.key = min.key; x.val = min.val; 
      x.right = deleteMinx(t.right); pathCompares -= min.level+1;
      x.left = t.left;
    } 
    x.size = 1 + size(x.left) + size(x.right);
    if (x == root && x.size == 1) x.height = 0;
    if (x.left == null && x.right == null) x.height = 0;
    else {
      int lh = x.left == null ? 0 : x.left.height;
      int rh = x.right == null ? 0 : x.right.height;
      x.height = 1 + Math.max(lh,rh);
    }
//    System.out.println("delete return "+x);
    return x;
  }
  
  public void deleteRandom() {
    // delete a random node except if size()==1 then delete root.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.key); return; }
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()); int c = 0; Key k = null;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == r) break; }
    delete(k);
  }
  
  public void delete(int i) {
    // delete the i mod size()th inOrder Node.
    if (root == null) throw new NoSuchElementException();
    if (size() == 1) { delete(root.key); return; }
    int j = i % size(), c = 0; Key k = null;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == i) break; }
    delete(k);
  }
  
//  private Node deleteVernon(Node x, Key key) {
//    //http://pages.cs.wisc.edu/~vernon/cs367/notes/9.BST.html#delete
//    if (x == null) return null;
//    if (key.equals(x.key)) {
//      // x is the node to be removed
//      if (x.left == null && x.right == null) return null;
//      if (x.left == null) return x.right;
//      if (x.right == null) return x.left;
//      // x has 2 children
//      Node tmp = min(x.right);
//      // copy key field from tmp to T
//      x.key = tmp.key;
//      // delete tmp from T's right subtree and return
//      x.right = delete(x.right, tmp.key);
//      return x;
//    }
//    else {
//      if (key.compareTo(x.key) < 0) x.left = delete(x.left, key);
//      else x.right = delete(x.right, key);
//      return x;
//    }
//    
//  }
  
  private Node deleteOrig(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) x.left  = delete(x.left,  key);
    else if (cmp > 0) x.right = delete(x.right, key);
    else { 
      if (x.right == null) return x.left;
      if (x.left  == null) return x.right;
      Node t = x;
      x = min(t.right);
      x.right = deleteMin(t.right);
      x.left = t.left;
    } 
    x.size = size(x.left) + size(x.right) + 1;
    x.height = 1 + Math.max(height(x.right), height(x.left));
    System.out.println("delete return "+x);
    return x;
  } 
  
//  private Node deleteNew(Node x, Key key) {
//    if (x == null) return null;
//    int cmp = key.compareTo(x.key); compares++;
//    if      (cmp < 0) x.left  = delete(x.left,  key);
//    else if (cmp > 0) x.right = delete(x.right, key);
//    else { 
//      if (x.right == null) { 
//        pathCompares -= (x.level+1);
//        return x.left; 
//      }
//      if (x.left  == null) { 
//        pathCompares -= (x.level+1); 
//        return x.right; 
//      }
//      System.out.println("min");
//      Node t = x; long xlevel = x.level; System.out.println("t="+t);
//      x = min(t.right);  System.out.println("new x="+x);
//      x.right = deleteMinx(t.right);
//      System.out.println("after delmin: xlevel="+xlevel+" x.level="+x.level);
////      pathCompares += (xlevel - 2*x.level);
//      x.left = t.left;
//      x.level = xlevel;
//      fixLevels(x);
//    } 
//    x.size = size(x.left) + size(x.right) + 1;
//    System.out.println("delete return "+x);
//    return x;
//  }
//
//  private Node delete(Node x, Key key) {
//    if (x == null) return null;
////    long l1 = 0;
//    int cmp = key.compareTo(x.key); compares++;
//    if (x.left == null && x.right == null && cmp != 0) return null;
//    if (cmp < 0 && x.left != null) {
//      ma = true;
//      System.out.println("x.left="+x.left);
//      x.left = delete(x.left,  key); pathCompares--; 
//    }
//    else if (cmp > 0 && x.right != null) { 
//      ma = true;
//      System.out.println("x.right="+x.right);
//      x.right = delete(x.right, key); pathCompares--; }
//    else {
////      l1 = x.level;
//      if (x.right == null) {
//        if (x.left != null) {
//          System.out.println("dec pcomps x.left");
//          decrementLevels(x.left); pathCompares-=x.left.level+1; 
//        }
//        return x.left; 
//      } 
//      if (x.left  == null) { 
//        if (x.right != null) { 
//          System.out.println("dec pcomps x.right");
//          decrementLevels(x.right); pathCompares-=x.right.level+1; 
//        }
//        return x.right; 
//      } 
//      Node t = x; long l2 = t.right.level;
////      System.out.println("del run min()");
//      x = min(t.right); pathCompares -= l2; 
//      x.right = deleteMin(t.right);
//      x.left = t.left;
//    } 
//    x.size = size(x.left) + size(x.right) + 1;
//    if (x.left == null && x.right == null) x.height = 0;
//    else {
//      int lh = x.left == null ? 0 : x.left.height;
//      int rh = x.right == null ? 0 : x.right.height;
//      x.height = 1 + Math.max(lh,rh);
//    }
////    x.level = l1;
//    System.out.println("del rtn "+x);
//    return x;
//  }

  public Key min() {
    if (root == null) throw new NoSuchElementException();
    return min(root).key;
  }
  
  private Node min(Node x) {
    if (x == null) return null;
    while(x.left != null) x = x.left;
    return x;
  }
  
  public Key minR() {
    if (root == null) throw new NoSuchElementException();
    return minR(root).key;
  }

  private Node minR(Node x) { 
    if (x == null) return null;
    if (x.left == null) return x; 
    else return min(x.left); 
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
  
  public Key maxR() {
    if (root == null) throw new NoSuchElementException();
    return maxR(root).key;
  } 

  private Node maxR(Node x) {
    if (x == null) return null;
    if (x.right == null) return x; 
    else return maxR(x.right); 
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

  public Key floorR(Key key) {
    //returns largest key <= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = floorR(root, key);
    if (x == null) return null;
    else return x.key;
  } 

  private Node floorR(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if (cmp == 0) return x;
    if (cmp <  0) return floorR(x.left, key);
    Node t = floorR(x.right, key); 
    if (t != null) return t;
    else return x; 
  }
  
  public Key floorRprint(Key key) {
    // prints keys traversed for ex3215
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = floorRprint(root, key);
    System.out.println();
    if (x == null) return null;
    else return x.key;
  } 
  
  private Node floorRprint(Node x, Key key) {
    // prints keys traversed for ex3215
    System.out.print(x.key+" ");
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if (cmp == 0) return x;
    if (cmp <  0) return floorRprint(x.left, key);
    Node t = floorRprint(x.right, key); 
    if (t != null) return t;
    else return x;
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

  public Key ceilingR(Key key) {
    // return smallest key >= key
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = ceilingR(root, key);
    if (x == null) return null;
    else return x.key;
  }

  private Node ceilingR(Node x, Key key) {
    System.out.print(x.key+" ");
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if (cmp == 0) return x;
    if (cmp < 0) { 
      Node t = ceilingR(x.left, key); 
      if (t != null) return t;
      else return x; 
    } 
    return ceilingR(x.right, key); 
  }
  
  public Key ceilingRprint(Key key) {
    // prints keys traversed for ex3215
    if (key == null) throw new IllegalArgumentException();
    if (root == null) throw new NoSuchElementException();
    Node x = ceilingRprint(root, key);
    System.out.println();
    if (x == null) return null;
    else return x.key;
  }

  private Node ceilingRprint(Node x, Key key) {
    // prints keys traversed for ex3215
    System.out.print(x.key+" ");
    if (x == null) return null;
    int cmp = key.compareTo(x.key); compares++;
    if (cmp == 0) return x;
    if (cmp < 0) { 
      Node t = ceilingRprint(x.left, key); 
      if (t != null) return t;
      else return x; 
    } 
    return ceilingRprint(x.right, key); 
  }
   
  public Key[] preSucKeys(Key key) {
    // return an array containing the inorder predecessor and successor of key.
    // the inorder predecessor of k is the key in this strictly smaller than k.
    // the inorder successor of k is the key in this strictly greater than k.
    // in either case if there is no such key null is returned.  
    if (root == null) return null;
    predecessor = null; successor = null;
    preSuc(root, key);
    Key[] ps = ofDim(root.key.getClass(), 2);
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
    // the predecessor of k is the key in this strictly smaller than z.
    // the successor of k is the key in this strictly greater than x.
    // in either case if there is no such key null is returned.  
    if (z == null)  return ;
    // If key is present at z
    if (z.key == key) {
      // the maximum value in left subtree is predecessor
      if (z.left != null) {
        Node tmp = z.left;
        while (tmp.right != null) tmp = tmp.right;
        predecessor = tmp.key;
      }
      // the minimum value in right subtree is successor
      if (z.right != null) {
        Node tmp = z.right ;
        while (tmp.left != null) tmp = tmp.left ;
        successor = tmp.key;
      }
      return ;
    }
    // If key is smaller than z's key, go to left subtree
    if (z.key.compareTo(key) > 0) {
      compares++;
      successor = z.key;
      preSuc(z.left, key) ;
    } else { // go to right subtree
      compares++;
      predecessor = z.key;
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
  

  public Key selectR(int k) {
    // return kth smallest key (key of rank k)
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = selectR(root, k);
    return x.key;
  }

  private Node selectR(Node x, int k) {
    if (x == null) return null; 
    int t = size(x.left); 
    if      (t > k) return selectR(x.left,  k); 
    else if (t < k) return selectR(x.right, k-t-1); 
    else            return x; 
  }
  
  public Key selectRprint(int k) {
    // prints keys traversed for ex3215
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = selectRprint(root, k);
    System.out.println();
    return x.key;
  }

  private Node selectRprint(Node x, int k) {
    System.out.print(x.key+" ");
    if (x == null) return null; 
    int t = size(x.left); 
    if      (t > k) return selectRprint(x.left,  k); 
    else if (t < k) return selectRprint(x.right, k-t-1); 
    else            return x; 
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

  public int rankR(Key key) {
    // return number of keys < key
    if (key == null) throw new IllegalArgumentException();
    return rankR(key, root);
  } 

  private int rankR(Key key, Node x) {
    if (x == null) return 0; 
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) return rankR(key, x.left); 
    else if (cmp > 0) return 1 + size(x.left) + rankR(key, x.right); 
    else              return size(x.left); 
  }
  
  public int rankRprint(Key key) {
    // prints keys traversed for ex3215
    if (key == null) throw new IllegalArgumentException();
//    System.out.println();
    return rankRprint(key, root);
  } 

  private int rankRprint(Key key, Node x) {
    System.out.print(x.key+" ");
    if (x == null) return 0; 
    int cmp = key.compareTo(x.key); compares++;
    if      (cmp < 0) return rankRprint(key, x.left); 
    else if (cmp > 0) return 1 + sizePrint(x.left) + rankRprint(key, x.right); 
    else              return sizePrint(x.left); 
  }
  
  public Iterable<Key> keys() {
    // inorder traversal
    Stack<Node> stack = new Stack<Node>();
    Queue<Key> queue = new Queue<Key>();
    Node x = root;
    while (x != null || !stack.isEmpty()) {
      if (x != null) {
        stack.push(x);
        x = x.left;
      }
      else {
        x = stack.pop();
        queue.enqueue(x.key);
        x = x.right;
      }
    }
    return queue;
  }
 
  public Iterable<Key> keysR() {
    // inorder traversal
    return keysR(min(), max());
  }

  public Iterable<Key> keysR(Key lo, Key hi) {
    // inorder traversal
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<Key> queue = new Queue<Key>();
    keysR(root, queue, lo, hi);
    return queue;
  } 

  private void keysR(Node x, Queue<Key> queue, Key lo, Key hi) { 
    if (x == null) return; 
    int cmplo = lo.compareTo(x.key); compares++;
    int cmphi = hi.compareTo(x.key); compares++; 
    if (cmplo < 0) keysR(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key); 
    if (cmphi > 0) keysR(x.right, queue, lo, hi); 
  } 
  
  public Iterable<Key> keysRprint(Key lo, Key hi) {
    // prints keys traversed for ex3215
    if (lo == null) throw new IllegalArgumentException();
    if (hi == null) throw new IllegalArgumentException();
    Queue<Key> queue = new Queue<Key>();
    keysRprint(root, queue, lo, hi);
    System.out.println();
    return queue;
  } 

  private void keysRprint(Node x, Queue<Key> queue, Key lo, Key hi) {
    // prints keys traversed for ex3215
    if (x == null) return;
    System.out.print(x.key+" ");
    int cmplo = lo.compareTo(x.key); compares++;
    int cmphi = hi.compareTo(x.key); compares++; 
    if (cmplo < 0) keysRprint(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key);
    if (cmphi > 0) keysRprint(x.right, queue, lo, hi); 
  } 

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
  private boolean isBST(Node x, Key min, Key max) {
    if (x == null) return true;
    if (min != null && x.key.compareTo(min) <= 0) { compares++; return false; }
    compares++;
    if (max != null && x.key.compareTo(max) >= 0) { compares++; return false; }
    compares++;
    return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
  }

  // are the size fields correct?
  private boolean isSizeConsistent() { return isSizeConsistent(root); }
  private boolean isSizeConsistent(Node x) {
    if (x == null) return true;
    if (x.size != size(x.left) + size(x.right) + 1) return false;
    return isSizeConsistent(x.left) && isSizeConsistent(x.right);
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
  
  public double optCompares(int N) { return log(N); }
  
  private long pathCompares() {
    if (root == null) return 0; 
    long c = 0;
    for (int i = 1; i < heightI()+2; i++) 
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
    if (root == null) return null;
    rnd.setSeed(System.currentTimeMillis());
    int r = rnd.nextInt(size()); int c = 0; Key k;
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) { k = it.next(); if (c++ == r) return k;}
    return null;
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
    if (x.key.equals(k)) return level;
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
    if (x.key.equals(y.key)) return level;
    int next = getLevelPlus1(x.left, y, level+1);
    if (next != 0) return next;
    next = getLevelPlus1(x.right, y, level+1);
    return next;
  }
  
  public boolean isBestCase() {
    // return true iff this is best case defined as all non-leaf
    // nodes have 2 children and all leaves are at the same depth
    // else return false. definition from
    //   http://pages.cs.wisc.edu/~vernon/cs367/notes/9.BST.html.
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
    if(x.key.equals(y.key) || x.key.equals(z.key)) return x;
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
    return x.key;
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
  
  public Iterable<Key> preOrder() {
    // return a preordered iterable of the keys in this bst.
    Queue<Key> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> stack = new Stack<>();
    stack.push(root);
    while(!stack.isEmpty()) {
      Node n = stack.pop();
      q.enqueue(n.key);
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
    System.out.print(x.key+":"+x.val+" ");
    showPreOrderR(x.left);
    showPreOrderR(x.right);
  }
  
  public void showPreOrder(Node x) {
    if(x == null) return;
    Stack<Node> stack = new Stack<>();
    stack.push(x);
    while(!stack.isEmpty()) {
      Node n = stack.pop();
      System.out.print(n.key+":"+n.val+" ");
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
        q.enqueue(x.key);
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
        System.out.print(n.key+":"+n.val+" ");
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
    System.out.print(x.key+":"+x.val+" ");
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
        q.push(x.key);
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
        q.enqueue(current.key);
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
      System.out.print(x.key+":"+x.val+" ");
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
        System.out.print(current.key+":"+current.val+" ");
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
        q.enqueue(tnode.key);
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
        System.out.print(tnode.key+":"+tnode.val+" ");
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
      keys.enqueue(x.key);
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
    
  // prettyPrintTree and associated methods below is under dev and is based on
  // https://codereview.stackexchange.com/questions/78145/prettyprint-a-binary-tree
  
  public void prettyPrintTree() {
    LinkedList<Node> queueOfNodes = new LinkedList<>();
    int height = getMaximumHeight(root);
    //System.out.println("getMaximumHeight="+height);
    //System.out.println("height="+height());
    int level = 0;
    int noOfNodesAtCurrentHeight = 0;
    queueOfNodes.add(root);
    while (!queueOfNodes.isEmpty() && level < height) {
      noOfNodesAtCurrentHeight = ((int) Math.pow(2, level));
      printNodes(queueOfNodes, noOfNodesAtCurrentHeight, height - level);
      printBranches(queueOfNodes, noOfNodesAtCurrentHeight, height - level);
      for (int i = 0; i < noOfNodesAtCurrentHeight; i++)
        queueOfNodes.remove();
      level++;
    }
  }
  
  private void printBranches(LinkedList<Node> queueOfNodes,
      int noOfNodesAtCurrentHeight, int height) {
    if (height <= 1) return;
    StringBuilder brachesAtHeight = new StringBuilder();

    String startSpace = getStartingSpace(height);
    // startSpace.substring(0, startSpace.length());
    String leftRightSpace = getSpaceBetweenLeftRightBranch(height);
    String rightLeftSpace = getSpaceBetweenRightLeftBranch(height);
    brachesAtHeight.append(startSpace.substring(0, startSpace.length() - 1));
    for (int i = 0; i < noOfNodesAtCurrentHeight; i++)
      brachesAtHeight.append("/").append(leftRightSpace).append("\\").append(rightLeftSpace);
    brachesAtHeight.substring(0, brachesAtHeight.length() - rightLeftSpace.length());
    System.out.println(brachesAtHeight.toString());
  }

  private String getSpaceBetweenLeftRightBranch(int height) {
    int noOfNodesBetweenLeftRightBranch = ((int) Math.pow(2, height - 1) - 1);
    StringBuilder spaceBetweenLeftRightStringBuilder = new StringBuilder();
    for (int i = 0; i < noOfNodesBetweenLeftRightBranch; i++)
      spaceBetweenLeftRightStringBuilder.append("  ");
    return spaceBetweenLeftRightStringBuilder.toString();
  }

  private String getSpaceBetweenRightLeftBranch(int height) {
    int noOfNodesBetweenLeftRightBranch = (int) Math.pow(2, height - 1);
    StringBuilder spaceBetweenLeftRightStringBuilder = new StringBuilder();
    for (int i = 0; i < noOfNodesBetweenLeftRightBranch; i++)
      spaceBetweenLeftRightStringBuilder.append("  ");
    return spaceBetweenLeftRightStringBuilder.toString();
  }
 
  private void printNodes(LinkedList<Node> queueOfNodes,
      int noOfNodesAtCurrentHeight, int height) {
    StringBuilder nodesAtHeight = new StringBuilder();
    String startSpace = getStartingSpace(height);
    String spaceBetweenTwoNodes = getSpaceBetweenTwoNodes(height);
    String underScore = getUnderScores(height);
    nodesAtHeight.append(startSpace);
    //System.out.println("noOfNodesAtCurrentHeight="+noOfNodesAtCurrentHeight);
    int idx = Math.min(noOfNodesAtCurrentHeight, queueOfNodes.size());
    for (int i = 0; i < idx; i++) {
      Node node = (Node) queueOfNodes.get(i);
      if (node == null) continue;
      queueOfNodes.add(node.left);
      queueOfNodes.add(node.right);
      nodesAtHeight.append(underScore);
      nodesAtHeight.append(String.format("%2s", node.key));
      nodesAtHeight.append(underScore);
      nodesAtHeight.append(spaceBetweenTwoNodes);
    }
    nodesAtHeight.substring(0, nodesAtHeight.length() - spaceBetweenTwoNodes.length());
    System.out.println(nodesAtHeight.toString());
  }
  
  private String getUnderScores(int height) {
    int noOfElementsToLeft = ((int) Math.pow(2, height) - 1) / 2;
    int noOfUnderScores = noOfElementsToLeft - ((int) Math.pow(2, height - 1) / 2);
    StringBuilder underScoreStringBuilder = new StringBuilder();
    // No. of underscores added everytime is the width of every node value
    for (int i = 0; i < noOfUnderScores; i++) underScoreStringBuilder.append("__");
    return underScoreStringBuilder.toString();
  }
  
  private String getSpaceBetweenTwoNodes(int height) {
    int noOfNodesInSubTreeOfNode = ((int) Math.pow(2, height - 1)) / 2;
    /** Sum of spaces of the subtrees of nodes + the parent node */
    int noOfSpacesBetweenTwoNodes = noOfNodesInSubTreeOfNode * 2 + 1;
    StringBuilder spaceBetweenNodesStringBuilder = new StringBuilder();
    for (int i = 0; i < noOfSpacesBetweenTwoNodes; i++) {
      spaceBetweenNodesStringBuilder.append("  ");
    }
    return spaceBetweenNodesStringBuilder.toString();
  }
  
  private String getStartingSpace(int height) {
    int noOfSpaces = ((int) Math.pow(2, height - 1)) / 2;
    StringBuilder startSpaceStringBuilder = new StringBuilder();
    // No. of spaces added everytime is the width of every node value
    for (int i = 0; i < noOfSpaces; i++) startSpaceStringBuilder.append("  ");
    return startSpaceStringBuilder.toString();
  }
  
  private int getMaximumHeight(Node node) {
    if (node == null) return 0;
    int leftHeight = getMaximumHeight(node.left);
    int rightHeight = getMaximumHeight(node.right);
    return (leftHeight > rightHeight) ? leftHeight + 1 : rightHeight + 1;
  }
  
  // end of prettyprint methods

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
    
//    c = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
//    e = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
//    Iterator<Integer> it; int p;
//    BSTXI<Integer, Integer> bst = new BSTXI<>(c,d);
//    
//    bst.printTree(); System.out.println();
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//    
//    for (int i = 0; i < e.length; i++) {
//      System.out.println("deleting "+e[i]); bst.delete(e[i]);
//      System.out.println("pathCompares="+bst.getPathCompares());
//      System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//      bst.printTree(); System.out.println();
//      System.out.println("pathCompares="+bst.getPathCompares());
//      System.out.println("compares: "+bst.avgCompares()+" "+bst.avgComparesR()+" "+bst.avgCompares2());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//      assert bst.avgCompares() == bst.avgComparesR();
////      assert bst.avgCompares() == bst.avgCompares2();
//    }
        
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
    
    Integer[] u = new Integer[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    Integer[] v = new Integer[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
    Iterator<Integer> it; int p;
//    u = new Integer[]{2,1,3}; v = new Integer[]{1,2,3};
    BSTXI<Integer, Integer> bst = new BSTXI<>(u,v);
//    System.out.println(bst.getRoot());
//    System.out.println(bst.getRoot().left);
//    System.out.println(bst.getRoot().left.left);
//    System.out.println(bst.getRoot().left.left.left);
//    System.exit(0);

    // testing min() vs. minR()
//    while (!bst.isEmpty()) {
//      assert bst.min() == bst.minR();
//      bst.deleteMin();
//    }
    
    // testing max() vs. maxR()
//    while (!bst.isEmpty()) {
//      assert bst.max() == bst.maxR();
//      bst.deleteMax();
//    }
    
    // testing keys(), keysR(), inOrder()
//    Integer[] a1 = new Integer[bst.size()], a2 = a1.clone(), a3 = a1.clone();  
//    Iterator<Integer> it1 = bst.keys().iterator(); p = 0;
//    while (it1.hasNext()) a1[p++] = it1.next();
//    Iterator<Integer> it2 = bst.keysR().iterator(); p = 0;
//    while (it2.hasNext()) a2[p++] = it2.next();
//    Iterator<Integer> it3 = bst.inOrder().iterator(); p = 0;
//    while (it3.hasNext()) a3[p++] = it3.next();
//    assert Arrays.equals(a1, a2);
//    assert Arrays.equals(a1, a3);
    
    // testing floor() vs. floorR()
//    it = bst.keys().iterator(); Integer x;
//    while (it.hasNext()) {
//      x = it.next();
//      assert bst.floor(x) == bst.floorR(x);
//    }
//    int[] ax = range(16,20);
//    for (int i = 0; i < ax.length; i++) {
//      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
//      assert bst.floor(ax[i]) == 15;
//    }
//    ax = range(21,26);
//    for (int i = 0; i < ax.length; i++) {
//      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
//      assert bst.floor(ax[i]) == 20;
//    }
//    ax = range(-9,1);
//    for (int i = 0; i < ax.length; i++) {
//      assert bst.floor(ax[i]) == bst.floorR(ax[i]);
//      assert bst.floor(ax[i]) == null;
//    }
    
    // test ceiling() vs. ceilingR()
//    it = bst.keys().iterator(); Integer y;
//    while (it.hasNext()) {
//      y = it.next();
//      assert bst.ceiling(y) == bst.ceilingR(y);
//    }
//    int[] ay = range(16,20);
//    for (int i = 0; i < ay.length; i++) {
//      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
//      assert bst.ceiling(ay[i]) == 20;
//    }
//    ay = range(21,26);
//    for (int i = 0; i < ay.length; i++) {
//      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
//      assert bst.ceiling(ay[i]) == null;
//    }
//    ay = range(-9,1);
//    for (int i = 0; i < ay.length; i++) {
//      assert bst.ceiling(ay[i]) == bst.ceilingR(ay[i]);
//      assert bst.ceiling(ay[i]) == 1;
//    }    
    
    // test reverseInOrder()
//    it = bst.reverseInOrder().iterator();
//    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println();
    
    // testing rank() vs. rankR()
//    it = bst.keys().iterator(); Integer z;
//    while (it.hasNext()) {
//      z = it.next();
//      assert bst.rank(z) == bst.rankR(z);
//    }
    
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
        
    System.exit(0);

    
    // testing size and height implementations
//    bst = new BSTXI<>();
//    // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//    // System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//    assert bst.size() == bst.sizeI();
//    assert bst.size() == bst.sizeR();
//    //assert bst.height() == bst.heightI();
//    assert bst.heightI() == bst.heightR();
//    System.out.println("put cycle");
//    for (int i = 0; i< u.length; i++) {
//      bst.put(u[i], v[i]);
//      // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//      assert bst.size() == bst.sizeI();
//      assert bst.size() == bst.sizeR();
//      System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//      //assert bst.height() == bst.heightI();
//      assert bst.heightI() == bst.heightR();
//    }
//    System.out.println("delete cycle");
//    while(!bst.isEmpty()) {
//      bst.deleteMax();
//      // System.out.println(bst.size()+" "+bst.sizeR()+" "+bst.sizeI());
//      assert bst.size() == bst.sizeI();
//      assert bst.size() == bst.sizeR();
//      // System.out.println(i+": "+bst.height()+" "+bst.heightR()+" "+bst.heightI());
//      //assert bst.height() == bst.heightI();
//      assert bst.heightI() == bst.heightR();     
//    }
//    System.exit(0);
    
//    bst.printTree(); System.out.println();
//    System.out.println("size="+bst.size());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//    System.out.println("pathCompares="+bst.getPathCompares());
//    it = bst.levelOrder().iterator();
//    while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");   
//    for (int i = 0; i < v.length; i++) {
//      System.out.println("deleting "+v[i]); bst.delete(v[i]);
//      System.out.println("size="+bst.size());
//      System.out.println("pathCompares="+bst.getPathCompares());
//      System.out.println(bst.avgCompares()+" "+bst.avgComparesR());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println();
//      bst.printTree(); System.out.println();
//      System.out.println("pathCompares="+bst.getPathCompares());
//      //System.out.println("compares: "+bst.avgCompares()+" "+bst.avgComparesR()+" "+bst.avgCompares2());
//      System.out.println("compares: "+bst.avgCompares()+" "+bst.avgComparesR());
//      it = bst.levelOrder().iterator();
//      while (it.hasNext()) System.out.print(bst.getNode(it.next())+" "); System.out.println("\n");
//      assert bst.avgCompares() == bst.avgComparesR();
//    }
        
  }
}

