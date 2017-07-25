package st;

import static analysis.Log.lg;
import static v.ArrayUtils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ds.Queue;
import exceptions.InvalidDataException;

// based on st.RedBlackBSTX for ex305

public class STintdouble implements OrderedSTintdouble {
  private static final boolean RED   = true;
  private static final boolean BLACK = false;
  private Node root;     // root of the BST

  private class Node {
    private int key;           // key
    private double val;         // associated data
    private Node left, right;  // links to left and right subtrees
    private boolean color;     // color of parent link
    private int size;          // subtree count

    public Node(int key, double val, boolean color, int size) {
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
      if (color) System.out.print(key+"(red)");
      else System.out.print(key);
      System.out.println();
    }   
  }

  public STintdouble(){}
  
  public STintdouble(int[] ka, double[] va) {
    if (ka == null || va == null || ka.length == 0 || va.length == 0) return;
    int n = Math.min(ka.length, va.length);
    for (int  i = 0; i < n; i++) put(ka[i], va[i]);
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

  @Override public int size() { return size(root); }

  @Override public boolean isEmpty() { return root == null; }

  @Override public Double get(Integer key) {
    if (key == null) throw new IllegalArgumentException("argument to get() is null");
    return get(root, key);
  }

  private Double get(Node x, int key) {
    while (x != null) {
      int cmp = key - x.key;
      if      (cmp < 0) x = x.left;
      else if (cmp > 0) x = x.right;
      else              return x.val;
    }
    return null;
  }

  @Override public boolean contains(int key) { return get(key) != null; }

  @Override public void put(int key, double val) {
    root = put(root, key, val);
    root.color = BLACK;
    assert check();
  }

  // insert the key-value pair in the subtree rooted at h
  private Node put(Node h, int key, double val) { 
    if (h == null) return new Node(key, val, RED, 1);

    int cmp = key - h.key;
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

  @Override public void deleteMax() {
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

  @Override public void delete(int key) { 
    if (!contains(key)) return;

    // if both children of root are black, set root to red
    if (!isRed(root.left) && !isRed(root.right))
      root.color = RED;

    root = delete(root, key);
    if (!isEmpty()) root.color = BLACK;
    assert check();
  }

  // delete the key-value pair with the given key rooted at h
  private Node delete(Node h, int key) { 
    // assert get(h, key) != null;

    if (key - h.key < 0)  {
      if (!isRed(h.left) && !isRed(h.left.left))
        h = moveRedLeft(h);
      h.left = delete(h.left, key);
    }
    else {
      if (isRed(h.left))
        h = rotateRight(h);
      if (key - h.key == 0 && (h.right == null)) return null;
      if (!isRed(h.right) && !isRed(h.right.left))
        h = moveRedRight(h);
      if (key - h.key == 0) {
        Node x = min(h.right);
        h.key = x.key;
        h.val = x.val;
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

  @Override public int min() {
    if (isEmpty()) throw new NoSuchElementException("called min() with empty symbol table");
    return min(root).key;
  } 

  // the smallest key in subtree rooted at x; null if no such key
  private Node min(Node x) { 
    // assert x != null;
    if (x.left == null) return x; 
    else                return min(x.left); 
  } 

  @Override public int max() {
    if (isEmpty()) throw new NoSuchElementException("called max() with empty symbol table");
    return max(root).key;
  } 

  // the largest key in the subtree rooted at x; null if no such key
  private Node max(Node x) { 
    // assert x != null;
    if (x.right == null) return x; 
    else                 return max(x.right); 
  } 

  @Override public Integer floor(int key) {
    if (isEmpty()) throw new NoSuchElementException("called floor() with empty symbol table");
    Node x = floor(root, key);
    if (x == null) return null;
    else           return x.key;
  }    

  // the largest key in the subtree rooted at x less than or equal to the given key
  private Node floor(Node x, int key) {
    if (x == null) return null;
    int cmp = key - x.key;
    if (cmp == 0) return x;
    if (cmp < 0)  return floor(x.left, key);
    Node t = floor(x.right, key);
    if (t != null) return t; 
    else           return x;
  }

  @Override public Integer ceiling(int key) {
    if (isEmpty()) throw new NoSuchElementException("called ceiling() with empty symbol table");
    Node x = ceiling(root, key);
    if (x == null) return null;
    else           return x.key;  
  }

  // the smallest key in the subtree rooted at x greater than or equal to the given key
  private Node ceiling(Node x, int key) {  
    if (x == null) return null;
    int cmp = key - x.key;
    if (cmp == 0) return x;
    if (cmp > 0)  return ceiling(x.right, key);
    Node t = ceiling(x.left, key);
    if (t != null) return t; 
    else           return x;
  }

  @Override public int select(int k) {
    if (k < 0 || k >= size()) throw new IllegalArgumentException();
    Node x = select(root, k);
    return x.key;
  }

  // the key of rank k in the subtree rooted at x
  private Node select(Node x, int k) {
    // assert x != null;
    // assert k >= 0 && k < size(x);
    int t = size(x.left); 
    if      (t > k) return select(x.left,  k); 
    else if (t < k) return select(x.right, k-t-1); 
    else            return x; 
  } 

  @Override public int rank(int key) {
    return rank(key, root);
  } 

  // number of keys less than key in the subtree rooted at x
  private int rank(int key, Node x) {
    if (x == null) return 0; 
    int cmp = key - x.key; 
    if      (cmp < 0) return rank(key, x.left); 
    else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right); 
    else              return size(x.left); 
  } 

  @Override public Iterable<Integer> keys() {
    if (isEmpty()) return new Queue<Integer>();
    return keys(min(), max());
  }

  @Override public Iterable<Integer> keys(int lo, int hi) {
    Queue<Integer> queue = new Queue<>();
    // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
    keys(root, queue, lo, hi);
    return queue;
  } 

  // add the keys between lo and hi in the subtree rooted at x
  // to the queue
  private void keys(Node x, Queue<Integer> queue, int lo, int hi) { 
    if (x == null) return; 
    int cmplo = lo - x.key; 
    int cmphi = hi - x.key; 
    if (cmplo < 0) keys(x.left, queue, lo, hi); 
    if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key); 
    if (cmphi > 0) keys(x.right, queue, lo, hi); 
  } 

  @Override public int size(int lo, int hi) {
    if (lo - hi > 0) return 0;
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
  private boolean isBST(Node x, Integer min, Integer max) {
    if (x == null) return true;
    if (min != null && x.key - min <= 0) return false;
    if (max != null && x.key - max >= 0) return false;
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
    for (Integer key : keys())
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
  
  public STint pathLengths() { return pathLengths(root); }
    
  public STint pathLengths(Node node) {
    List<Integer> list = new ArrayList<>();
    STint map = new STint();
    pathLengths(node, list, map, 0);
    return map;
  }
  
  private void pathLengths(Node node, List<Integer> list, 
      STint map, int pathLen) {
    if (node == null) return;
    list.add(pathLen,node.key);
    pathLen++;
    if (node.left == null && node.right == null) {
      // adding 1 to the final pathLen to get expected answer for ex3324
      if (map.contains(pathLen+1)) 
        map.put(pathLen+1, map.get(pathLen+1)+1);
      //map.merge(pathLen+1, 1, Integer::sum);
      else map.put(pathLen+1, 1);
    } else {
      pathLengths(node.left, list, map, pathLen);
      pathLengths(node.right, list, map, pathLen);
    }
  }
  
  public void printPaths() { printPaths(root); }
  
  public void printPaths(Node node) {
    //http://www.geeksforgeeks.org/given-a-binary-tree-print-all-root-to-leaf-paths/
    List<Integer> list = new ArrayList<>();
    printPaths(node, list, 0);
  }

  private void printPaths(Node node, List<Integer> list, int pathLen) {
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
  
  public void printList(List<Integer> list, int len) {
    int i;
    for (i = 0; i < len; i++) System.out.print(list.get(i) + " ");
    System.out.println();
  }
  
  public void printArray(int[] a, int len) {
      int i;
      for (i = 0; i < len; i++) System.out.print(a[i] + " ");
      System.out.println();
  }
  
  public void show() {
    Iterator<Integer> it = keys().iterator();
    while (it.hasNext()) {
      int k = it.next();
      System.out.println(k+":"+get(k)+" ");
    }
    System.out.println();
  }
  
  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Iterator<Integer> it = keys().iterator();
    while (it.hasNext()) {
      int k = it.next();
      sb.append(k+":"+get(k)+",");
    }
    return sb.deleteCharAt(sb.length()-1).append("}").toString();
  }
  
  public static void main(String[] args) {
    
    int[] u = new int[]{3,13,9,5,8,7,11,15,2,1,10,4,12,14,20,6};
    double[] v = new double[]{3,4,6,14,11,20,12,10,13,1,5,2,9,8,15,7};
//    u = new int[]{0,3,2,1,4}; //(8,-1);
//    v = range(0,v.length);
    STintdouble st = new STintdouble(u,v);
    System.out.println("size = "+st.size());
    System.out.println("height = "+st.height());
    st.printTree();
    System.out.println("levels:");
    st.printLevels();
    System.out.println("paths:");
    st.printPaths();
    System.out.print("pathLengths map: ");
    STint map = st.pathLengths();
    System.out.println(map);
    System.out.println("2lgN = "+2*lg(st.size()));

  }
}

