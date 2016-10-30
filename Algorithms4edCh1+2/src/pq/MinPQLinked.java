package pq;

import static analysis.Log.lg;
import static java.lang.Math.pow;
import static java.lang.System.identityHashCode;
import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.pa;
import static v.ArrayUtils.range;
import static v.ArrayUtils.rangeInteger;
import static v.ArrayUtils.shuffle;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Random;

import ds.Queue;
import exceptions.NoNullChildException;

/* 
  for ex2421
  based on https://stackoverflow.com/questions/31257243/how-do-i-implement-a-priority-queue-with-explicit-links-using-a-triply-linked-d
  explicit constructors added:
    RandomLinkedPQ()
    RandomLinkedPQ(Key[])
  methods added:
    exch(Node3<Key>,Node3<Key>)
    delRandom()
    findCeil(long[],long,int,int)
    getLastNode3<Key>()
    getRandom()
    height()
    height(Node3<Key>)
    isMinHeap()
    less(Key,Key)
    less(Node3<Key>,Node3<Key>)
    levelToSB()
    randElement(long[],long,int,int)
    showLevel(Node3<Key>,int)
    showLevels()
    show()
    toArray()
  methods rewritten or updated:
    sink(Node3<Key>) - rewrote to conform to the implementation on p316 adapted to PQs with
                 triply linked nodes.
    swim(Node3<Key>) - revised to use less(Node3<Key>, Node3<Key>) and exch(Node3<Key>,Node3<Key>).
*/

@SuppressWarnings("unused")
public class MinPQLinked<Key extends Comparable<? super Key>> {
  
  private Node3x2<Key> root;
  private Node3x2<Key> lastInserted = null;
  private Class<?> keyclass = null;
  
  public MinPQLinked(){}
   
  public MinPQLinked(Key[] z){
    if (z == null || z.length == 0) return;
    for (int i = 0; i < z.length; i++) {
      if (z[i] == null) continue; // null Keys not allowed
      insert(z[i]);
    }
  }

  // private methods
   
  private int size(Node3x2<Key> x){
    if(x == null) return 0;
    return x.N1;
  }
 
  private void swim(Node3x2<Key> x){
    if(x == null) return;
    if(x.parent1 == null) return; // we're at root
    if (greater(x.parent1, x)) { exch(x, x.parent1); swim(x.parent1); }
  }
  
  private void sink(Node3x2<Key> x){
    if(x == null) return;
    Node3x2<Key> node;
    if(x.left1 == null && x.right1 == null) return;
    else if(x.right1 == null) {
      node = x.left1;
      if (!greater(x, node)) return;
      exch(node, x); sink(node);
    } else if(x.left1 == null){
      node = x.right1;
      if (!greater(x, node)) return;
      exch(node, x); sink(node);
    } else {
      node = x.left1;
      if (greater(x.left1, x.right1)) node = x.right1;
      if (!greater(x, node)) return;
      exch(node, x); sink(node);
    }
  }
  
//  private boolean less(Node3<Key> a, Node3<Key> b) {
//    return a.data.compareTo(b.data) < 0;
//  }
//  
//  private boolean less(Key a, Key b) {
//    return a.compareTo(b) < 0;
//  }
  
  private boolean greater(Node3x2<Key> a, Node3x2<Key> b) {
    return a.data1.compareTo(b.data1) > 0;
  }
  
  private boolean greater(Key a, Key b) {
    return a.compareTo(b) > 0;
  }
  
  private void exch(Node3x2<Key> a, Node3x2<Key> b) {
    Key swap = a.data1; a.data1 = b.data1; b.data1 = swap;
  }
  
  private Node3x2<Key> resetLastInserted(Node3x2<Key> x){
    if (x == null) return null;
    if (x.left1 == null && x.right1 == null) return x;
    if (size(x.right1) < size(x.left1)) return resetLastInserted(x.left1);
    else return resetLastInserted(x.right1);
  }
  
  private void levelToSB(Node3x2<Key> node, int h, StringBuilder sb) {
    // accumulate in a StringBuilder the data of nodes at level h 
    // from node where node is at relative level 1.
    // for showLevel() and showLevels().
    if (node == null || h < 1 || h > height(node)) return;
    if (h == 1) sb.append(node.data1+" ");
    if (h > 1) {
      levelToSB(node.left1, h-1, sb);
      levelToSB(node.right1, h-1, sb);
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
  
  private Node3x2<Key> insert(Node3x2<Key> x, Key data){
    if(x == null){
      lastInserted = new Node3x2<Key>(data, 1);
      return lastInserted;
    }
    
    if (keyclass == null) keyclass = data.getClass();
    
    // compare left and right sizes see where to go
    int leftSize = size(x.left1);
    int rightSize = size(x.right1);

    if(leftSize <= rightSize){
      // go to left
      Node3x2<Key> inserted = insert(x.left1, data);
      x.left1 = inserted;
      inserted.parent1 = x;
    } else{
      // go to right
      Node3x2<Key> inserted = insert(x.right1, data);
      x.right1 = inserted;
      inserted.parent1 = x;
    }
    x.N1 = size(x.left1) + size(x.right1) + 1;
    return x;
  }
 
  // public methods
  
  public Class<?> getKeyClass() {
    return keyclass;
  }
  
  public void insert(Key data){
    if (data == null) return;
    if (keyclass == null) keyclass = data.getClass();
    root = insert(root, data);
    swim(lastInserted);
    assert isMinHeap();
  }
  
  public void insert(Node3x2<Key> x){
    if(x == null) return;
//    System.out.println("x="+x);
    if (keyclass == null && x.data1 != null) keyclass = x.data1.getClass();
    if (root == null ) {
//      System.out.println("root is null");
      root = x; x.N1 = 1; x.parent1 = x.left1 = x.right1 = null; lastInserted = root;
//      System.out.println("root="+root);
    }
    else {
      if (keyclass == null && x.data1 != null) keyclass = x.data1.getClass();
      Node3x2<Key> inserted = null;
      Node3x2<Key> parent = getParent4NextInsertion();
//      System.out.println("parent="+parent);
      if (parent.left1 == null) { parent.left1 = x; inserted = parent.left1; }
      else if (parent.right1 == null) { parent.right1 = x; inserted = parent.right1; }
      else throw new NoNullChildException("Node "+parent+" with identityHashCode "
          +identityHashCode(parent)+" doesn't have a null child");
      inserted.N1 = 1; inserted.left1 = inserted.right1 = null; inserted.parent1 = parent;
      parent.N1++;  lastInserted = inserted;
//      par(toArray()); 
      while (parent.parent1 != null) { parent = parent.parent1; parent.N1++; }
//      System.out.println("here1");
      swim(lastInserted);
      assert isMinHeap();
//      System.out.println("last");
    }        
  }
  
  private Node3x2<Key> getParent4NextInsertion(Node3x2<Key> node) {
    // get best Node as parent for next inserted node.
    // strategy is to fill higher levels before lower levels and
    // fill a given level 
    // for insert(Node3x2<Key>).
    if (node != null && size(node) == 1) return node;
    if (node.left1 == null) return node;
    if (node.right1 == null) return node;
    int leftSize = size(node.left1);
    int rightSize = size(node.right1);
    node = leftSize <= rightSize ? node.left1 : node.right1;
    return getParent4NextInsertion(node); 
  }
  
  public Node3x2<Key> getParent4NextInsertion( ) {
    return getParent4NextInsertion(root);
  }
  
  private static int randElement(int z[], long freq[], int n) {
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
  
  public Key getRandom(){
    // return a Key from a random Node3<Key> without removing it.
    // from root go down a random number of levels taking random left/right 
    // turns and return the Key in the Node3<Key> found.
    if(root == null) return null;
    int size = size();
    if (size == 1) return delMin();
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
    if (level == 0) return delMin();
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    int u; Node3x2<Key> k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left1 != null) l = k.left1;
        else if (k.right1 != null) l = k.right1;
        else { l = k; break; }
      } else {
        if (k.right1 != null) l = k.right1;
        else if (k.left1 != null) l = k.left1;
        else { l = k; break; }
      }
      k = l;
    }
    return k.data1;
  }
  
  public Key delRandom(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    // return a the Key in a random Node3<Key> and remove the Node3<Key>.
    // from root go down a random number of levels taking random left/right 
    // turns, remove the Node3<Key> found and return the Key that was in it.
    // this the same as getRandom() up to locating the random Node3<Key>.
    if(root == null) return null;
    int size = size();
    if (size == 1) return delMin();
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
    if (level == 0) return delMin();
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(System.currentTimeMillis());
    int u; Node3x2<Key> k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left1 != null) l = k.left1;
        else if (k.right1 != null) l = k.right1;
        else break;
      } else {
        if (k.right1 != null) l = k.right1;
        else if (k.left1 != null) l = k.left1;
        else break;
      }
      k = l;
    }
    // this begins the part not included in getRandom() and is much like delMax().
    // remove the found node and return its data.
    if (k == min()) return delMin();
    else {
      exch(k, lastInserted); // exchange data fields.
      Node3x2<Key> lastInsParent = lastInserted.parent1;
      Key lastInsData = lastInserted.data1; // k's data originally.
      if(lastInserted == lastInsParent.left1) lastInsParent.left1 = null;
      else lastInsParent.right1 = null;
      Node3x2<Key> traverser = lastInserted;
      while(traverser != null) { traverser.N1--; traverser = traverser.parent1; }
      lastInserted = resetLastInserted(root);
      sink(k);
      return lastInsData;
    }
  }
  
  public Key min(){
    if(root == null) return null;
    return root.data1;
  }
  
  public Key delMin(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    if(size() == 1){
      Key data = root.data1;
      root = null;
      return data;
    }
    exch(root, lastInserted);
    Node3x2<Key> lastInsParent = lastInserted.parent1;
    Key lastInsData = lastInserted.data1;
    if(lastInserted == lastInsParent.left1) lastInsParent.left1 = null;
    else lastInsParent.right1 = null;
    Node3x2<Key> traverser = lastInserted;
    while(traverser != null) { traverser.N1--; traverser = traverser.parent1; }
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
  
  public boolean isMinHeap() {
    return isMinHeap(root);
  }

  public boolean isMinHeap(Node3x2<Key> x) {
    if (x == null || size(x) == 1) return true;
    else if (size(x) == 2) {
      if (x.right1 != null) return false;
      if (greater(x.data1, x.left1.data1)) return false;
      return isMinHeap(x.left1);
    }
    else {
      if (size(x) < 3 || x.left1 == null || x.right1 == null) return false;
      if (greater(x.data1,x.left1.data1) || greater(x.data1, x.right1.data1)) return false;
      return isMinHeap(x.left1) && isMinHeap(x.right1);
    }
  }
  
  public int height() {
    // return the number of nodes along the longest path from root 
    // to its farthest leaf including root
    if (isEmpty()) return 0;
    else return height(root);
  }
  
  public int height(Node3x2<Key> node) {
    // return the number of nodes along the longest path from node 
    // to its farthest leaf including node
    if (node==null)  return 0;
    else  {
      int lheight = height(node.left1);
      int rheight = height(node.right1);
      if (lheight > rheight) return(lheight+1);
      else return(rheight+1);
    }
  }
  
  public Key[] toArray() {
    // convert root to Key[] in level order
    int size = size();
    Key [] a = ofDim(keyclass, size);
    if (size == 0) return a;
    if (size == 1) { a[0] = root.data1; return a; }
    Node3x2<Key> node; int c = 0;
    Queue<Node3x2<Key>> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      a[c++] = node.data1;
      if (node.left1 != null) q.enqueue(node.left1);
      if (node.right1 != null) q.enqueue(node.right1);
    }
    return a;
  }
  
  public Node3x2<Key>[] toNodeArray() {
    // convert root to Key[] in level order
    int size = size();
    Node3x2<Key> [] a = ofDim(Node3x2.class, size);
    if (size == 0) return a;
    if (size == 1) { a[0] = root; return a; }
    Node3x2<Key> node; int c = 0;
    Queue<Node3x2<Key>> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      a[c++] = node;
      if (node.left1 != null) q.enqueue(node.left1);
      if (node.right1 != null) q.enqueue(node.right1);
    }
    return a;
  }
  
  public void show() {
    // print Keys in level order
    int size = size();
    if (size == 0) return;
    if (size == 1) System.out.println(root.data1);
    Node3x2<Key> node;
    Queue<Node3x2<Key>> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      System.out.print(node.data1+" ");
      if (node.left1 != null) q.enqueue(node.left1);
      if (node.right1 != null) q.enqueue(node.right1);
    }
    System.out.println();
  }
   
  public void showLevel(Node3x2<Key> node, int h) {
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
  
  public void showNodes() {
    pa(toNodeArray(),80,1,1);
  }

  public Node3x2<Key> getLastNode() {
    return lastInserted;
  }

  public static void main(String[] args) {
    
    MinPQLinked<Integer> pq; Integer[] a;
    Random r = new Random(System.currentTimeMillis());
//    a = rangeInteger(1,21);
//    shuffle(a,r);
    a = new Integer[]{1,2,3,4,5};
    pq = new MinPQLinked<>(a);
    System.out.println(pq.isMinHeap());
    while (!pq.isEmpty()) System.out.print(pq.delMin()+" ");
    System.out.println();
    System.exit(0);
    
    a =  rangeInteger(1,10000); int size, size2;
//    shuffle(a,r);
    
//    a = new Integer[]{8,4,6,2,3,7,1,9,5};
//    a = new Integer[]{1,2,3,4};
//    a = new Integer[]{4,2,3,1};
//    a = new Integer[]{4,6,9,3,2,8,1,5,7};
//    a = new Integer[]{9,8, 7, 5, 4, 6, 1, 3, 2};
//    a = new Integer[]{6,8,9,3,5,2,4,1,7};
    a = new Integer[]{1,2,3,4,5,6,7,8,9};
//    par(a);
    pq = new MinPQLinked<>(a);
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
//    System.out.println(pq.isMinHeap());
//    System.out.println(pq.size());
//    System.out.println(pq.isMinHeap());
//    pq.show();
//    par(pq.toArray());
//    par(pq.toNode3<Key>Array());
//    System.out.println(pq.getLeastNode());
//    while (!pq.isEmpty()) {
//      System.out.println("removed "+pq.delRandom());
////      System.out.println(pq.isMinHeap());
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
//    System.out.println(pq.isMinHeap());
//    System.out.println(pq.size());
//    System.out.println(pq.delMax());
//    System.out.println(pq.isMinHeap());
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