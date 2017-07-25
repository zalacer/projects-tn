package st;

import static v.ArrayUtils.ofDim;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.Queue;
import ds.Stack;
import exceptions.DuplicateException;

public class SimpleBST<Key extends Comparable<Key>, Value>  {

  private class Node  {
    private Key key;
    private Value val;
    private Node left, right;

    public Node(Key k, Value v, Node l, Node r) {
      key = k;
      val = v;
      left = l;
      right = r;
    }

    public Key getKey() { return key; }
    public Value getVal() { return val; }
    public void setVal(Value v) { val = v; }
    public Node getLeft() { return left; }
    public void setLeft(Node x) { left = x; }
    public Node getRight() { return right; }
    public void setRight(Node x) { right = x; }
    @Override public String toString() { return "("+key+":"+val+")"; }

  }

  private Node root = null; // ptr to the root of the BST
  private int N = 0;
  private Class<?> kclass = null; // Key class
  private Class<?> vclass = null; // Value class

  public SimpleBST(){} // constructor

  public int size() { return N; }

  public boolean isEmpty() { return N == 0; }

  public void put(Key k, Value v) throws DuplicateException {
    if (k == null) throw new IllegalArgumentException();
    if (v == null) { delete(k); return; }
    if (kclass == null) kclass = k.getClass();
    if (vclass == null) vclass = v.getClass();
    if (root == null) { root = new Node(k, v, null, null); N++; return; }
    else put(root, k, v);
  }

  private void put(Node T, Key k, Value v) {
    // precondition: T != null
    if (T.getKey().equals(k)) throw new DuplicateException();
    if (k.compareTo(T.getKey()) < 0) {
      // add k as left child of T if it doesn't already have one
      // else insert into T's left subtree
      if (T.getLeft() == null) { 
        T.setLeft( new Node(k, v, null, null)); N++; return;
      }
      else put(T.getLeft(), k, v);
    }
    else {
      // here when k > T's key
      // insert k as right child of T if it doesn't already have one
      // else insert into T's right subtree
      if (T.getRight() == null) { 
        T.setRight(new Node(k, v, null, null)); N++; return;        
      }
      else put(T.getRight(), k, v);
    }
  }

  public void delete(Key key) {
    if (root == null) throw new NoSuchElementException();
    if (key == null) throw new NullPointerException("key is null");
    root = delete(root, key);
  }

  private Node delete(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key); 
    if      (cmp < 0) x.left  = delete(x.left,  key);
    else if (cmp > 0) x.right = delete(x.right, key);
    else { 
      if (x.right == null) { N--; return x.left; }
      if (x.left  == null) { N--; return x.right; }
      Node t = x;
      x = min(t.right);
      x.right = deleteMin(t.right);
      x.left = t.left; N--;
    }
    
    return x;
  } 
 
  public Key min() {
    if (root == null) throw new NoSuchElementException();
    return min(root).key;
  } 

  private Node min(Node x) { 
    if (x.left == null) return x; 
    else return min(x.left); 
  } 

  public Key max() {
    if (root == null) throw new NoSuchElementException();
    return max(root).key;
  } 

  private Node max(Node x) {
    if (x.right == null) return x; 
    else return max(x.right); 
  }
  
  public void deleteMax() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMax(root);
  }
  
  private Node deleteMax(Node x) {
    if (root == null) throw new NoSuchElementException();
    if (x.right == null) { N--; return x.left; }
    x.right = deleteMax(x.right);
    return x;
  }
  
  public void deleteMin() {
    if (root == null) throw new NoSuchElementException();
    root = deleteMin(root);
  }
  
  private Node deleteMin(Node x) {
    if (root == null) throw new NoSuchElementException();
    if (x.left == null) { N--; return x.right; }
    x.left = deleteMin(x.left);
    return x;
  }
  


  public Value get(Key k) {
    return get(root, k);
  }

  private Value get(Node T, Key k) {
    if (T == null) return null;
    if (T.getKey().equals(k)) return T.getVal();
    if (k.compareTo(T.getKey()) < 0) {
      // k < this node's key; look in left subtree
      return get(T.getLeft(), k);
    }
    else {
      // k > this node's key; look in right subtree
      return get(T.getRight(), k);
    }
  }

  public Node getNode(Key k) {
    return getNode(root, k);
  }

  private Node getNode(Node T, Key k) {
    if (T == null) return null;
    if (T.getKey().equals(k)) return T;
    if (k.compareTo(T.getKey()) < 0) {
      // k < this node's key; look in left subtree
      return getNode(T.getLeft(), k);
    }
    else {
      // k > this node's key; look in right subtree
      return getNode(T.getRight(), k);
    }
  }
  
  public Key getKey(Node x) {
    if (x == null) throw new NullPointerException("node is null");
    return x.getKey();
  }

  public Value getVal(Key k) {
    if (k == null) return null;
    Node node = getNode(k);
    return node.getVal();   
  }

  public void setVal(Key k, Value v) {
    if (k == null || v == null) 
      throw new NullPointerException("key or value is null");
    Node node = getNode(k);
    node.setVal(v);   
  }

  public Iterable<Key> inOrder() {
    // return an inordered iterable of the keys in this bst.
    Queue<Key> q = new Queue<>();
    if (root == null) return q;
    Stack<Node> s = new Stack<>();
    Node currentNode = root;
    while(!s.isEmpty() || currentNode!=null) {
      if(currentNode!=null) {
        s.push(currentNode);
        currentNode=currentNode.getLeft();
      } else {
        Node n=s.pop();
        q.enqueue(n.getKey());
        currentNode=n.getRight();
      }
    }
    return q;
  }

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

  public Key[] toArray() {
    if (N == 0) {
      if (kclass != null) return ofDim(kclass, 0);
      else return null;
    }
    System.out.println("size="+size());
    Key[] ka = ofDim(kclass, size());
    Iterator<Key> it = inOrder().iterator(); int c = 0;
    while (it.hasNext()) ka[c++] = it.next();
    return ka;  
  }

  @Override 
  public String toString() {
    if (root == null) return "()";
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    Iterator<Key> it = inOrder().iterator();
    while (it.hasNext()) {
      Key k = it.next();
      sb.append(k+":"+get(k)+",");
    }
    return sb.substring(0,sb.length()-1)+")"; 
  }

  public static void main(String[] args) {

    Integer[] c = {3,13, 9, 5, 8, 7,11,15, 2, 1,10, 4,12,14, 6};
    Integer[] e = {3, 4, 6,14,11,20,12,10,13, 1, 5, 2, 9, 8, 7};
    SimpleBST<Integer,Integer> bst = new SimpleBST<>();
    for (int i  = 0; i < c.length; i++) bst.put(c[i],e[i]);
    System.out.println(bst);
    // (1:1,2:13,3:3,4:2,5:14,6:7,7:20,8:11,9:6,10:5,11:12,12:9,13:4,14:8,15:10)
    System.out.println(bst.get(7)); //20
    bst.delete(7);
    System.out.println(bst.get(7)); //null
    System.out.println(bst);
    // (1:1,2:13,3:3,4:2,5:14,6:7,8:11,9:6,10:5,11:12,12:9,13:4,14:8,15:10)
    System.out.println(bst.getVal(9)); //6
    bst.setVal(9,19);
    System.out.println(bst.getVal(9)); //19
    System.out.println(bst);
    //(1:1,2:13,3:3,4:2,5:14,6:7,8:11,9:19,10:5,11:12,12:9,13:4,14:8,15:10)
  }

}
