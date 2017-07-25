package st;

public interface OrderedST<K extends Comparable<? super K>,V> {
  // for Ex3103
  // based on API for a generic ordered symbol table p366 Algorithms4ed
  public void put(K k, V v);
  public V get(K k);
  public void delete(K k);
  public boolean contains(K k);
  public boolean isEmpty();
  public int size();
  public K min();
  public K max();
  public K floor(K k);
  public K ceiling(K k);
  public int rank(K k);
  public K select(int k);
  public void deleteMin();
  public void deleteMax();
  int size(K lo, K hi);
  public Iterable<K> keys(K lo, K hi);  
  public Iterable<K> keys();  
}

