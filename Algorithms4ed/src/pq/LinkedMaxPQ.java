package pq;

import static v.ArrayUtils.*;
import java.util.Random;

// from https://stackoverflow.com/questions/31257243/how-do-i-implement-a-priority-queue-with-explicit-links-using-a-triply-linked-d
public class LinkedMaxPQ<Key extends Comparable<Key>>{
  private class Node{
    int N;
    Key data;
    Node parent, left, right;
    public Node(Key data, int N){
      this.data = data; this.N = N;
    }
    @Override public String toString() {
      return "("+data+","+N+")";
    }
  }
  // fields
  public Node root;
  private Node lastInserted;
  // constructor
   public LinkedMaxPQ(){}
  //helper methods
  private int size(Node x){
    if(x == null) return 0;
    return x.N;
  }
 
  
  private void swim(Node x){
    if(x == null) return;
    if(x.parent == null) return; // we're at root
    int cmp = x.data.compareTo(x.parent.data);
    if(cmp > 0){
      swapNodeData(x, x.parent);
      swim(x.parent);
    }
  }
  private void sink(Node x){
    if(x == null) return;
    Node swapNode;
    if(x.left == null && x.right == null){
      return;
    }
    else if(x.left == null){
      swapNode = x.right;
      int cmp = x.data.compareTo(swapNode.data);
      if(cmp < 0)
        swapNodeData(swapNode, x);
    } else if(x.right == null){
      swapNode = x.left;
      int cmp = x.data.compareTo(swapNode.data);
      if(cmp < 0)
        swapNodeData(swapNode, x);
    } else{
      int cmp = x.left.data.compareTo(x.right.data);
      if(cmp >= 0){
        swapNode = x.left;
      } else{
        swapNode = x.right;
      }
      int cmpParChild = x.data.compareTo(swapNode.data);
      if(cmpParChild < 0) {
        swapNodeData(swapNode, x);
        sink(swapNode);
      }
    }
  }
  private void swapNodeData(Node x, Node y){
    Key temp = x.data;
    x.data = y.data;
    y.data = temp;
  }
  private Node insert(Node x, Key data){
    if(x == null){
      lastInserted = new Node(data, 1);
      return lastInserted;
    }
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
  private Node resetLastInserted(Node x){
    if(x == null) return null;
    if(x.left == null && x.right == null) return x;
    if(size(x.right) < size(x.left))return resetLastInserted(x.left);
    else                            return resetLastInserted(x.right);
  }
  // public methods
  public void insert(Key data){
    root = insert(root, data);
    swim(lastInserted);
  }
  public Key max(){
    if(root == null) return null;
    return root.data;
  }
  public Key delMax(){
    if(size() == 1){
      Key ret = root.data;
      root = null;
      return ret;
    }
    swapNodeData(root, lastInserted);
    Node lastInsParent = lastInserted.parent;
    Key lastInsData = lastInserted.data;
    if(lastInserted == lastInsParent.left){
      lastInsParent.left = null;
    } else{
      lastInsParent.right = null;
    }

    Node traverser = lastInserted;

    while(traverser != null){
      traverser.N--;
      traverser = traverser.parent;
    }

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

  private boolean isMaxHeap(Node x) {
    if (x == null || size(x) == 1) return true;
    else if (size(x) == 2) {
      if (x.right != null) return false;
      if (x.data.compareTo(x.left.data)<0) return false;
      return isMaxHeap(x.left);
    }
    else {
      if (size(x) < 3 || x.left == null || x.right == null) return false;
      if (x.data.compareTo(x.left.data)<0 || x.data.compareTo(x.right.data)<0)
        return false;
      return isMaxHeap(x.left) && isMaxHeap(x.right);
    }
  }

  
  public static void main(String[] args) {
    
    LinkedMaxPQ<Integer> pq;
    Random r = new Random(System.currentTimeMillis());
    Integer[] a =  rangeInteger(1,21);
    shuffle(a,r);
    
//    a = new Integer[]{4,2,3,1,5};
    a = new Integer[]{1,2,3,4};
    a = new Integer[]{4,2,3,1};
//    par(a);
    pq = new LinkedMaxPQ<>();
//    pq.insert(1); pq.insert(1); pq.insert(1); 
//    System.out.println(pq.root);
//    System.out.println(pq.root.left);
//    System.out.println(pq.root.right);
//    System.out.println(pq.isMaxHeap());

    for (Integer i : a) pq.insert(i);
//    System.out.println(pq.isMaxHeap());
//    System.out.println(pq.delMax());
//    System.out.println(pq.isMaxHeap());
    System.out.println(pq.root);
    System.out.println(pq.root.right);
    System.out.println(pq.root.right.left);
    System.out.println(pq.root.left);
    System.out.println(pq.root.left.left);
    System.out.println(pq.root.left.right);
    
    
//    for (int i = 0; i < a.length; i++)
//      System.out.print(pq.delMax()+" ");
//    System.out.println();
//    System.out.println(pq.isEmpty());
    
    
    
    
  }

}