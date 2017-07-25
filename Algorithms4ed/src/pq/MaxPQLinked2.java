package pq;

import static java.lang.System.identityHashCode;
import static analysis.Log.lg;
import static java.lang.Math.pow;
import static v.ArrayUtils.*;

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
    isMaxHeap()
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
public class MaxPQLinked2<Key extends Comparable<? super Key>> {
 
  private Node3x2<Key> root;
  private Node3x2<Key> lastInserted = null;
  private Class<?> keyclass = null;
  
  public MaxPQLinked2(){}
   
  public MaxPQLinked2(Key[] z){
    if (z == null || z.length == 0) return;
    for (Key x : z) {
      if (x == null) continue; // null Keys not allowed
      insert(x);
    }
  }

  @SafeVarargs
  public MaxPQLinked2(Node3x2<Key>...z){
    if (z == null) return;
    for (Node3x2<Key> x : z) {
      if (z == null) continue;
      insert(x);
    }
  }
  // private methods
   
  private int size(Node3x2<Key> x){
    if(x == null) return 0;
    return x.N2;
  }
 
  private void swim(Node3x2<Key> x){
    if(x == null) return;
    if(x.parent2 == null) return; // we're at root
    if (less(x.parent2, x)) { exch(x, x.parent2); swim(x.parent2); }
  }
  
  private void sink(Node3x2<Key> x){
    if(x == null || (x.left2 == null && x.right2 == null)) return;
    Node3x2<Key> node;
    if(x.right2 == null) {
      node = x.left2;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    } else if(x.left2 == null) {
      node = x.right2;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    } else {
      node = x.left2;
      if (less(x.left2, x.right2)) node = x.right2;
      if (!less(x, node)) return;
      exch(node, x); sink(node);
    }
  }
  
  private Node3x2<Key> getRoot(Node3x2<Key> x) {
    if(x == null || x.parent2 == null) return x;
    Node3x2<Key> node = x;
    while (node.parent2 != null) node = node.parent2;
    return node;
  }
  
  private boolean less(Node3x2<Key> a, Node3x2<Key> b) {
    return a.data2.compareTo(b.data2) < 0;
  }
  
  private boolean less(Key a, Key b) {
    return a.compareTo(b) < 0;
  }
  
  private void exch(Node3x2<Key> a, Node3x2<Key> b) {
    Key swap = a.data2; a.data2 = b.data2; b.data2 = swap;
  }
  
  private void exch2(Node3x2<Key> a, Node3x2<Key> b) {
    // exchange all "2" fields between a and b
    Key swap = a.data2; a.data2 = b.data2; b.data2 = swap;
    Node3x2<Key>node = a.parent2; a.parent2 = b.parent2; b.parent2 = node;
    node = a.left2; a.left2 = b.left2; b.left2 = node;
    node = a.right2; a.right2 = b.right2; b.right2 = node;
    int n = a.N2; a.N2 = b.N2; b.N2 = n;
  }
  
  private Node3x2<Key> resetLastInserted(Node3x2<Key> x){
    if (x == null) return null;
    if (x.left2 == null && x.right2 == null) return x;
    if (size(x.right2) < size(x.left2)) return resetLastInserted(x.left2);
    else return resetLastInserted(x.right2);
  }
  
  private void levelToSB(Node3x2<Key> node, int h, StringBuilder sb) {
    // accumulate in a StringBuilder the data of nodes at level h 
    // from node where node is at relative level 1.
    // for showLevel() and showLevels().
    if (node == null || h < 1 || h > height(node)) return;
    if (h == 1) sb.append(node.data2+" ");
    if (h > 1) {
      levelToSB(node.left2, h-1, sb);
      levelToSB(node.right2, h-1, sb);
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
      lastInserted = new Node3x2<Key>(data, 1, "max");
      return lastInserted;
    }
    
    if (keyclass == null) keyclass = data.getClass();
    
    if (x.left2 == null) {
      Node3x2<Key> inserted = insert(x.left2, data);
      x.left2 = inserted;
      inserted.parent2 = x;
    } else if (x.right2 == null) {
      Node3x2<Key> inserted = insert(x.right2, data);
      x.right2 = inserted;
      inserted.parent2 = x;
    } else {
      // compare left and right sizes see where to go
      int leftSize = size(x.left2);
      int rightSize = size(x.right2);

      if(leftSize <= rightSize){
        // go to left
        Node3x2<Key> inserted = insert(x.left2, data);
        x.left2 = inserted;
        inserted.parent2 = x;
      } else{
        // go to right
        Node3x2<Key> inserted = insert(x.right2, data);
        x.right2 = inserted;
        inserted.parent2 = x;
      }
    }
    x.N2 = size(x.left2) + size(x.right2) + 1;
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
    assert isMaxHeap(root);
  }
  
  public void insert(Node3x2<Key> x){
    if(x == null) return;
//    System.out.println("x="+x);
    if (keyclass == null && x.data2 != null) keyclass = x.data2.getClass();
    if (root == null ) {
//      System.out.println("root is null");
      root = x; x.N2 = 1; x.parent2 = x.left2 = x.right2 = null; lastInserted = root;
//      System.out.println("root="+root);
    }
    else {
      if (keyclass == null && x.data2 != null) keyclass = x.data2.getClass();
      Node3x2<Key> inserted = null;
      Node3x2<Key> parent = getParent4NextInsertion();
//      System.out.println("parent="+parent);
      if (parent.left2 == null) { parent.left2 = x; inserted = parent.left2; }
      else if (parent.right2 == null) { parent.right2 = x; inserted = parent.right2; }
      else throw new NoNullChildException("Node "+parent+" with identityHashCode "
          +identityHashCode(parent)+" doesn't have a null child");
      inserted.N2 = 1; inserted.left2 = inserted.right2 = null; inserted.parent2 = parent;
      parent.N2++;  lastInserted = inserted;
//      par(toArray()); 
      while (parent.parent2 != null) { parent = parent.parent2; parent.N2++; }
//      System.out.println("here1");
      swim(lastInserted);
      assert isMaxHeap();
//      System.out.println("last");
    }        
  }
  
  private Node3x2<Key> getParent4NextInsertion(Node3x2<Key> node) {
    // get best Node as parent for next inserted node.
    // strategy is to fill higher levels before lower levels and
    // fill a given level 
    // for insert(Node3x2<Key>).
    if (node != null && size(node) == 1) return node;
    if (node.left2 == null) return node;
    if (node.right2 == null) return node;
    int leftSize = size(node.left2);
    int rightSize = size(node.right2);
    node = leftSize <= rightSize ? node.left2 : node.right2;
    return getParent4NextInsertion(node); 
  }
  
  private Node3x2<Key> getParent4NextInsertion( ) {
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
    int u; Node3x2<Key> k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left2 != null) l = k.left2;
        else if (k.right2 != null) l = k.right2;
        else { l = k; break; }
      } else {
        if (k.right2 != null) l = k.right2;
        else if (k.left2 != null) l = k.left2;
        else { l = k; break; }
      }
      k = l;
    }
    return k.data2;
  }
  
  public Key delRandom(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    // return a the Key in a random Node3<Key> and remove the Node3<Key>.
    // from root go down a random number of levels taking random left/right 
    // turns, remove the Node3<Key> found and return the Key that was in it.
    // this the same as getRandom() up to locating the random Node3<Key>.
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
    int u; Node3x2<Key> k = root, l = null;
    for (int i = 1; i < level; i++) {
      u = r.nextInt(2);
      if (u == 0) {
        if (k.left2 != null) l = k.left2;
        else if (k.right2 != null) l = k.right2;
        else break;
      } else {
        if (k.right2 != null) l = k.right2;
        else if (k.left2 != null) l = k.left2;
        else break;
      }
      k = l;
    }
    // this begins the part not included in getRandom() and is much like delMax().
    // remove the found node and return its data.
    if (k == max()) return delMax();
    else {
      exch(k, lastInserted); 
      Node3x2<Key> lastInsParent = lastInserted.parent2;
      Key lastInsData = lastInserted.data2; // k's data originally.
      if(lastInserted == lastInsParent.left2) lastInsParent.left2 = null;
      else lastInsParent.right2 = null;
      Node3x2<Key> traverser = lastInserted;
      while(traverser != null) { traverser.N2--; traverser = traverser.parent2; }
      lastInserted = resetLastInserted(root);
      sink(k);
      return lastInsData;
    }
  }
  
  public Key max(){
    if(root == null) return null;
    return root.data2;
  }
  
  public Key delMax(){
    if (isEmpty()) throw new NoSuchElementException("PQ underflow");
    if(size() == 1){
      Key data = root.data2;
      root = null;
      return data;
    }
    exch2(root, lastInserted);
    Node3x2<Key> lastInsParent = lastInserted.parent2;
    Key lastInsData = lastInserted.data2;
    if(lastInserted == lastInsParent.left2) lastInsParent.left2 = null;
    else lastInsParent.right2 = null;
    Node3x2<Key> traverser = lastInserted;
    while(traverser != null) { traverser.N2--; traverser = traverser.parent2; }
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

  public boolean isMaxHeap(Node3x2<Key> x) {
    if (x == null || size(x) == 1) return true;
    else if (size(x) == 2) {
      if (x.right2 != null) return false;
      if (less(x.data2, x.left2.data2)) return false;
      return isMaxHeap(x.left2);
    }
    else {
      if (size(x) < 3 || x.left2 == null || x.right2 == null) return false;
      if (less(x.data2,x.left2.data2) || less(x.data2, x.right2.data2)) return false;
      return isMaxHeap(x.left2) && isMaxHeap(x.right2);
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
      int lheight = height(node.left2);
      int rheight = height(node.right2);
      if (lheight > rheight) return(lheight+1);
      else return(rheight+1);
    }
  }
  
  public Key[] toArray() {
    // convert root to Key[] in level order
    int size = size();
    Key [] a = ofDim(keyclass, size);
    if (size == 0) return a;
    if (size == 1) { a[0] = root.data2; return a; }
    Node3x2<Key> node; int c = 0;
    Queue<Node3x2<Key>> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      a[c++] = node.data2;
      if (node.left2 != null) q.enqueue(node.left2);
      if (node.right2 != null) q.enqueue(node.right2);
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
      if (node.left2 != null) q.enqueue(node.left2);
      if (node.right2 != null) q.enqueue(node.right2);
    }
    return a;
  }
  
  
  
  public void show() {
    // print Keys in level order
    int size = size();
    if (size == 0) return;
    if (size == 1) System.out.println(root.data2);
    Node3x2<Key> node;
    Queue<Node3x2<Key>> q = new Queue<>(root);
    while (!q.isEmpty()) {
      node = q.dequeue();
      System.out.print(node.data2+" ");
      if (node.left2 != null) q.enqueue(node.left2);
      if (node.right2 != null) q.enqueue(node.right2);
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
    
//    Node3<Integer> v = new Node3<>(0,2);
//    Node3<Integer> x = new Node3<>(1,3);
//    Node3<Integer> y = new Node3<>(2,1);
//    Node3<Integer> z = new Node3<>(3,1);
//    v.parent = null;
//    x.parent = v; x.left = y; x.right = z;
//    y.parent = x; y.left = null; y.right = null;
//    z.parent = x; z.left = null; z.right = null;
//    x.parent = x.left = x.right = null;
//    System.out.println(x.parent+" "+x.left+" "+x.right);
//    System.exit(0);

    
    
    MaxPQLinked2<Integer> pq; Integer[] a;
    Random r = new Random(System.currentTimeMillis());
    a = rangeInteger(1,21);
//    a = rangeInteger(1,7);
    shuffle(a,r);
//    a = new Integer[]{6,5,4,3,2,1};
    a = new Integer[]{1,2,3,4,5};
    par(a);
    pq = new MaxPQLinked2<>(a);
    Node3x2<Integer>[] na = pq.toNodeArray();
//    par(na);
    MaxPQLinked2<Integer> pq2 = new MaxPQLinked2<Integer>(na);
    pq2.show();
    pq2.showLevels();
//    par(pq2.toNodeArray());
//    par(na);
//    Integer[] ka = pq.toArray();
//    par(ka);
////    Node3<Integer> root = pq.root;
//    pq.showLevels();
//    System.out.println("nextP="+pq.getParent4NextInsertion(pq.root));
    System.exit(0);
    
    shuffle(na,r);
    while (!pq.isEmpty()) System.out.print(pq.delMax()+" ");
    System.out.println();
    System.exit(0);
    
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
    pq = new MaxPQLinked2<>(a);
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
//    par(pq.toNode3<Key>Array());
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