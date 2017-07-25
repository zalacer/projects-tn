package pq;

import static v.ArrayUtils.*;

//@SuppressWarnings("unused")
public class MinMaxPQLinked<Key extends Comparable<? super Key>> {
  public MinPQLinked<Key> pqmin;
  public MaxPQLinked<Key> pqmax;

  public MinMaxPQLinked(){
    pqmin = new MinPQLinked<Key>();
    pqmax = new MaxPQLinked<Key>();
  };
  
  public MinMaxPQLinked(Key[] z){
    pqmin = new MinPQLinked<Key>(z);
    Node3x2<Key>[] a = pqmin.toNodeArray();
    pqmax = new MaxPQLinked<Key>(a);
    assert pqmax.isMaxHeap();
    assert pqmin.isMinHeap();
  }
  
  public void insert(Key k) {
    Node3x2<Key> node = new Node3x2<Key>(k, 1);
    pqmin.insert(node);
    pqmax.insert(node);
  }
  
  public int size() {
    return pqmin.size();
  }
  
  public boolean isEmpty() {
    return pqmin.isEmpty();
  }
  
  public boolean isMinHeap() {
    return pqmin.isMinHeap();
  }
  
  public boolean isMaxHeap() {
    return pqmax.isMaxHeap();
  }
  
  public boolean isMinMaxHeap() {
    return pqmin.isMinHeap() && pqmax.isMaxHeap();
  }
  
   public void showMin() { pqmin.show(); }
   
   public void showMax() { pqmax.show(); }
   
   public void show() { 
     System.out.print("min: "); pqmin.show(); 
     System.out.print("max: "); pqmax.show(); }
   
   public void showMinNodes() {
     Node3x2<Key>[] a = pqmin.toNodeArray();
     for (Node3x2<Key> n : a) System.out.println(n);
   }
   
   public void showMaxNodes() {
     Node3x2<Key>[] a = pqmax.toNodeArray();
     for (Node3x2<Key> n : a) System.out.println(n);
   }
   
   public void showNodes() {
     Node3x2<Key>[] a = pqmin.toNodeArray();
     System.out.println("min (one Node3x2 per line:");
     for (Node3x2<Key> n : a) System.out.println(n);
     a = pqmax.toNodeArray();
     System.out.println("max: (one Node3x2 per line");
     for (Node3x2<Key> n : a) System.out.println(n);
   }
   
   public void showMinLevels() { pqmin.showLevels(); }
   
   public void showMaxLevels() { pqmax.showLevels(); }
   
   public void showLevels() { 
     System.out.println("min levels:");
     pqmin.showLevels(); 
     System.out.println("max levels:");
     pqmax.showLevels(); 
   }
   
   public Key[] toMinArray() { return pqmin.toArray(); }
   
   public Key[] toMaxArray() { return pqmax.toArray(); }
   
   public Key[][] toArrays() {
     Key[][] a = ofDim(pqmin.getKeyClass(), 2, size());
     a[0] =  pqmin.toArray(); a[1] = pqmax.toArray();
     return a;
   }
   
   public Node3x2<Key>[] toMinNodeArray() { return pqmin.toNodeArray(); }
   
   public Node3x2<Key>[] toMaxNodeArray() { return pqmax.toNodeArray(); }
   
   public Node3x2<Key>[][] toNodeArrays() {
     Node3x2<Key>[][] a = ofDim(Node3x2.class, 2, size());
     a[0] =  pqmin.toNodeArray(); a[1] = pqmax.toNodeArray();
     return a;
   }
  
  
  public static void main(String[] args) {
    
    Integer[] a ={1,2,3,4,5}; // rangeInteger(1,21); 
    MinMaxPQLinked<Integer> pq = new MinMaxPQLinked<>(a);
    System.out.println(pq.isMinMaxHeap());
//    pa(pq.pqmin.toNodeArray(),80,1,1);
    pq.show();
    pq.insert(6);
    pq.show();
    pq.showNodes();
    
  }

}
