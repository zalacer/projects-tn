package pq;

import static analysis.Log.lg;
import static java.lang.Math.pow;
import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.range;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Random;

import ds.Queue;

/* 
  for ex2421
  based on https://stackoverflow.com/questions/31257243/how-do-i-implement-a-priority-queue-with-explicit-links-using-a-triply-linked-d
  explicit constructors added:
    RandomLinkedPQ()
    RandomLinkedPQ(Key[])
  methods added:
    exch(Node,Node)
    delRandom()
    findCeil(long[],long,int,int)
    getLastNode()
    getRandom()
    height()
    height(Node)
    isMaxHeap()
    less(Key,Key)
    less(Node,Node)
    levelToSB()
    randElement(long[],long,int,int)
    showLevel(Node,int)
    showLevels()
    show()
    toArray()
  methods rewritten or updated:
    sink(Node) - rewrote to conform to the implementation on p316 adapted to PQs with
                 triply linked nodes.
    swim(Node) - revised to use less(Node, Node) and exch(Node,Node).
*/

public class RandomLinkedMaxPQ<Key extends Comparable<? super Key>>{
 
  private class Node{
    int N; Key data; Node parent, left, right;
    public Node(Key d, int n) { data = d; N = n; }
    @Override public String toString() { return "("+data+","+N+")"; }
  }
  
  private Node root;
  private Node lastInserted = null;
  private Class<?> keyclass = null;
  
  public RandomLinkedMaxPQ(){}
   
  public RandomLinkedMaxPQ(Key[] z){
    if (z == null || z.length == 0) return;
    for (int i = 0; i < z.length; i++) {
      if (z[i] == null) continue; // null Keys not allowed
      insert(z[i]);
    }
  }

  // private methods
   
  private int size(Node x){
    if(x == null) return 0;
    return x.N;
  }
 
  private void swim(Node x){
    if(x == null) return;
    if(x.parent == null) return; // we're at root
    if (less(x.parent, x)) { exch(x, x.parent); swim(x.parent); }
  }
  
  private void sink(Node x){
    if(x == null) return;
    Node node;
    if(x.left == null && x.right == null) return;
    else if(x.right == null) {
      node = x.left;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    } else if(x.left == null){
      node = x.right;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    } else {
      node = x.left;
      if (less(x.left, x.right)) node = x.right;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    }
  }
  
  private boolean less(Node a, Node b) {
    return a.data.compareTo(b.data) < 0;
  }
  
  private boolean less(Key a, Key b) {
    return a.compareTo(b) < 0;
  }
  
  private void exch(Node a, Node b) {
    Key swap = a.data; a.data = b.data; b.data = swap;
  }
  
  private Node resetLastInserted(Node x){
    if (x == null) return null;
    if (x.left == null && x.right == null) return x;
    if (size(x.right) < size(x.left)) return resetLastInserted(x.left);
    else return resetLastInserted(x.right);
  }
  
  private void levelToSB(Node node, int h, StringBuilder sb) {
    // accumulate in a StringBuilder the data of nodes at level h 
    // from node where node is at relative level 1.
    // for showLevel() and showLevels().
    if (node == null || h < 1 || h > height(node)) return;
    if (h == 1) sb.append(node.data+" ");
    if (h > 1) {
      levelToSB(node.left, h-1, sb);
      levelToSB(node.right, h-1, sb);
    }
  }
  
  public static int findCeil(long z[], long r, int l, int h) {
    // return the ceiling of r in z where ceiling means the least element
    // in a >= r.
    // for getRandom() and delRandom()
    // from http://www.geeksforgeeks.org/random-number-generator-in-arbitrary-probability-distribution-fashion/
    int mid;
    while (l < h) {
      mid = (l+h)/2;
      if (r > z[mid]) l = mid + 1;
      else h = mid;
    }
    return (z[l] >= r) ? l : -1;
  }
  
  public static int randElement(int z[], long freq[], int n) {
    // return a random element from z[] according the the frequencies in freq[].
    // for getRandom() and delRandom()
    // from http://www.geeksforgeeks.org/random-number-generator-in-arbitrary-probability-distribution-fashion/
    if (z == null || freq == null || z.length != freq.length)
      throw new IllegalArgumentException("randElement: z[] and freq[] mustn't "
          + "be null and must have the same length.");
    // create and fill prefix array which contains the successive partial
    // sums of the frequencies in freq[]. 
    long[] prefix = new long[n];
    prefix[0] = freq[0];
    for (int i = 1; i < n; ++i) prefix[i] = prefix[i-1] + freq[i];
    // prefix[n-1] is sum of all frequencies. generate a random number
    // with value from 1 to this sum.
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
            
    long v = (long)(r.nextDouble()*prefix[n-1]) + 1;

    // find index of ceiling of r in prefix array
    int indexc = findCeil(prefix, v, 0, n - 1);
    return z[indexc];
  }
  
  private Node insert(Node x, Key data){
    if(x == null){
      lastInserted = new Node(data, 1);
      return lastInserted;
    }
    
    if (keyclass == null) keyclass = data.getClass();
    
    // compare left and right sizes see where to go
    int leftSize = size(x.left);
    int rightSize = size(x.right);

    if(leftSize <= rightSize){
      // go to left
      Node inserted = insert(x.left, data);
      x.left = inserted;
      inserted.parent = x;
    } else{
      // go to right
      Node inserted = insert(x.right, data);
      x.right = inserted;
      inserted.parent = x;
    }
    x.N = size(x.left) + size(x.right) + 1;
    return x;
  }
 
  // public methods
  public void insert(Key data){
    if (data == null) return;
    if (keyclass == null) keyclass = data.getClass();
    root = insert(root, data);
    swim(lastInserted);
  }
  
  public Key getRandom(){
    // return a Key from a random Node without removing it.
    // from root go down a random number of levels taking random left/right 
    // turns and return the Key in the Node found.
    if(root == null) return null;
    int size = size();
    if (size == 1) return delMax();
    int lgsize = (int)Math.ceil(lg(size()));
    boolean pow2 = (size & (size - 1)) == 0; // size is a power of 2 if pow2==true
    if (pow2) lgsize++; 
    long[] freq = new long[lgsize];
    int sum = 0; // sum of number of positions in all levels when filled
    for (int i = 0; i < lgsize; i++) sum+=(int)pow(2,i);
    // populate freq[]
    for (int i = 0; i < freq.length-1; i++) freq[i] = (int)pow(2,i);
    // set freq[freq.length-1] according to actual number of nodes that should be in it
    if (pow2) freq[freq.length-1] = 1;
    else freq[freq.length-1] = (long)pow(2,lgsize-1) - sum + size;
    int[] levels = range(0,lgsize);
    // pick level randomly and weighted by number of nodes at each level
    int level = randElement(levels, freq, lgsize);
    if (level == 0) return delMax();
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    int u; Node k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left != null) l = k.left;
        else if (k.right != null) l = k.right;
        else { l = k; break; }
      } else {
        if (k.right != null) l = k.right;
        else if (k.left != null) l = k.left;
        else { l = k; break; }
      }
      k = l;
    }
    return k.data;
  }
  
  public Key delRandom(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    // return a the Key in a random Node and remove the Node.
    // from root go down a random number of levels taking random left/right 
    // turns, remove the Node found and return the Key that was in it.
    // this the same as getRandom() up to locating the random Node.
    if(root == null) return null;
    int size = size();
    if (size == 1) return delMax();
    int lgsize = (int)Math.ceil(lg(size()));
    boolean pow2 = (size & (size - 1)) == 0; // size is a power of 2 if pow2==true.
    if (pow2) lgsize++; 
    long[] freq = new long[lgsize];
    int sum = 0; // sum of number of positions in all levels when filled.
    for (int i = 0; i < lgsize; i++) sum+=(int)pow(2,i);
    // populate freq[]
    for (int i = 0; i < freq.length-1; i++) freq[i] = (int)pow(2,i);
    // set freq[freq.length-1] according to actual number of nodes that should be in it.
    if (pow2) freq[freq.length-1] = 1;
    else freq[freq.length-1] = (long)pow(2,lgsize-1) - sum + size;
    int[] levels = range(0,lgsize);
    // pick level randomly and weighted by number of nodes at each level.
    int level = randElement(levels, freq, lgsize);
    if (level == 0) return delMax();
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    int u; Node k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left != null) l = k.left;
        else if (k.right != null) l = k.right;
        else break;
      } else {
        if (k.right != null) l = k.right;
        else if (k.left != null) l = k.left;
        else break;
      }
      k = l;
    }
    // this begins the part not included in getRandom() and is much like delMax().
    // remove the found node and return its data.
    if (k == max()) return delMax();
    else {
      exch(k, lastInserted); // exchange data fields.
      Node lastInsParent = lastInserted.parent;
      Key lastInsData = lastInserted.data; // k's data originally.
      if(lastInserted == lastInsParent.left) lastInsParent.left = null;
      else lastInsParent.right = null;
      Node traverser = lastInserted;
      while(traverser != null) { traverser.N--; traverser = traverser.parent; }
      lastInserted = resetLastInserted(root);
      sink(k);
      return lastInsData;
    }
  }
  
  public Key max(){
    if(root == null) return null;
    return root.data;
  }
  
  public Key delMax(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    if(size() == 1){
      Key data = root.data;
      root = null;
      return data;
    }
    exch(root, lastInserted);
    Node lastInsParent = lastInserted.parent;
    Key lastInsData = lastInserted.data;
    if(lastInserted == lastInsParent.left) lastInsParent.left = null;
    else lastInsParent.right = null;
    Node traverser = lastInserted;
    while(traverser != null) { traverser.N--; traverser = traverser.parent; }
    lastInserted = resetLastInserted(root);
    sink(root);
    return lastInsData;
  }
  
  public int size(){
    return size(root);
  }
  
  public boolean isEmpty(){
    return size() == 0;
  }
  
  public boolean isMaxHeap() {
    return isMaxHeap(root);
  }

  public boolean isMaxHeap(Node x) {
    if (x == null || size(x) == 1) return true;
    else if (size(x) == 2) {
      if (x.right != null) return false;
      if (less(x.data, x.left.data)) return false;
      return isMaxHeap(x.left);
    }
    else {
      if (size(x) < 3 || x.left == null || x.right == null) return false;
      if (less(x.data,x.left.data) || less(x.data, x.right.data)) return false;
      return isMaxHeap(x.left) && isMaxHeap(x.right);
    }
  }
  
  public int height() {
    // return the number of nodes along the longest path from root 
    // to its farthest leaf including root
    if (isEmpty()) return 0;
    else return height(root);
  }
  
  public int height(Node node) {
    // return the number of nodes along the longest path from node 
    // to its farthest leaf including node
    if (node==null)  return 0;
    else  {
      int lheight = height(node.left);
      int rheight = height(node.right);
      if (lheight > rheight) return(lheight+1);
      else return(rheight+1);
    }
  }
  
  public Key[] toArray() {
    // convert root to Key[] in level order
    int size = size();
    Key [] a = ofDim(keyclass, size);
    if (size == 0) return a;
    if (size == 1) { a[0] = root.data; return a; }
    Node node; int c = 0;
    Queue<Node> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      a[c++] = node.data;
      if (node.left != null) q.enqueue(node.left);
      if (node.right != null) q.enqueue(node.right);
    }
    return a;
  }
  
  public Node[] toNodeArray() {
    // convert root to Key[] in level order
    int size = size();
    Node [] a = ofDim(Node.class, size);
    if (size == 0) return a;
    if (size == 1) { a[0] = root; return a; }
    Node node; int c = 0;
    Queue<Node> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      a[c++] = node;
      if (node.left != null) q.enqueue(node.left);
      if (node.right != null) q.enqueue(node.right);
    }
    return a;
  }
  
  public void show() {
    // print Keys in level order
    int size = size();
    if (size == 0) return;
    if (size == 1) System.out.println(root.data);
    Node node;
    Queue<Node> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      System.out.print(node.data+" ");
      if (node.left != null) q.enqueue(node.left);
      if (node.right != null) q.enqueue(node.right);
    }
    System.out.println();
  }
   
  public void showLevel(Node node, int h) {
    // print data of nodes at level h from node where node is at level 1
    StringBuilder sb = new StringBuilder();
    levelToSB(node, h, sb);
    System.out.println(sb.toString());
  }
  
  public void showLevels() {
    // print data of nodes at level h from root where root is at level 1
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < height(root)+1; i++) {
      levelToSB(root, i, sb); sb.append("\n");
    }
    System.out.print(sb.toString());
  }

  public Node getLastNode() {
    return lastInserted;
  }

  public static void main(String[] args) {
    
    RandomLinkedMaxPQ<Integer> pq; Integer[] a;
    Random r = new Random(System.currentTimeMillis());
//    a = rangeInteger(1,21);
//    shuffle(a,r);
//    pq = new RandomLinkedMaxPQ<>(a);
//    while (!pq.isEmpty()) System.out.print(pq.delMax()+" ");
//    System.out.println();
//    System.exit(0);
    
    a =  rangeInteger(1,10000); int size, size2;
    shuffle(a,r);
//    a = new Integer[]{8,4,6,2,3,7,1,9,5};
//    a = new Integer[]{1,2,3,4};
//    a = new Integer[]{4,2,3,1};
//    a = new Integer[]{4,6,9,3,2,8,1,5,7};
//    a = new Integer[]{9,8, 7, 5, 4, 6, 1, 3, 2};
//    a = new Integer[]{6,8,9,3,5,2,4,1,7};
    a = new Integer[]{1,2,3,4,5,6,7,8,9};
//    par(a);
    pq = new RandomLinkedMaxPQ<>(a);
    pq.showLevels();
    System.out.print("level 2: "); pq.showLevel(pq.root, 2);
    System.out.println("height="+pq.height());
    size = pq.size();
    System.out.println("size="+pq.size());
    System.out.println();
    while (!pq.isEmpty()) {
      System.out.println("random="+pq.delRandom());
      size2 = pq.size();
      assert size == size2+1;
      size = size2;
      System.out.println("pq.height="+pq.height());
      pq.showLevels();
      System.out.println();
    }
    
//    pq.insert(1); pq.insert(1); pq.insert(1); 
//    System.out.println(pq.root);
//    System.out.println(pq.root.left);
//    System.out.println(pq.root.right);
//    System.out.println(pq.isMaxHeap());
//    System.out.println(pq.size());
//    System.out.println(pq.isMaxHeap());
//    pq.show();
//    par(pq.toArray());
//    par(pq.toNodeArray());
//    System.out.println(pq.getLeastNode());
//    while (!pq.isEmpty()) {
//      System.out.println("removed "+pq.delRandom());
////      System.out.println(pq.isMaxHeap());
//      pq.show();
//      System.out.println("size="+pq.size());
//    }
//    System.out.println(pq.delRandom());
//    System.out.println("size="+pq.size());
//    pq.show();
    
//    pq.showLevels();
//    for (Integer i : a) pq.insert(i);
//    System.out.println(pq.root.data);
//    System.out.println(pq.size());
//    System.out.println(pq.delRandom());
//    System.out.println(pq.isMaxHeap());
//    System.out.println(pq.size());
//    System.out.println(pq.delMax());
//    System.out.println(pq.isMaxHeap());
//    System.out.println(pq.root.right);
//    System.out.println(pq.root.right.left);
//    System.out.println(pq.root.left);
//    System.out.println(pq.root.left.left);
//    System.out.println(pq.root.left.right);
    
    
//    for (int i = 0; i < a.length; i++)
//      System.out.print(pq.delMax()+" ");
//    System.out.println();
//    System.out.println(pq.isEmpty());
    
    
    
    
  }

}