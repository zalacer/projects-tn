package st;

public interface OrderedSTint {
  // for Ex3505
  // based on API for a generic ordered symbol table p366 Algorithms4ed
  public void put(int k, int v);
  public Integer get(Integer k);
  public void delete(int k);
  public boolean contains(int k);
  public boolean isEmpty();
  public int size();
  public int min();
  public int max();
  public Integer floor(int k);
  public Integer ceiling(int k);
  public int rank(int k);
  public int select(int k);
  public void deleteMin();
  public void deleteMax();
  int size(int lo, int hi);
  public Iterable<Integer> keys(int lo, int hi);  
  public Iterable<Integer> keys();  
}

