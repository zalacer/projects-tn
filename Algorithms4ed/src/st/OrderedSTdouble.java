package st;

public interface OrderedSTdouble {
  // for Ex3505
  // based on API for a generic ordered symbol table p366 Algorithms4ed
  public void put(double k, double v);
  public Double get(Double k);
  public void delete(double k);
  public boolean contains(double k);
  public boolean isEmpty();
  public int size();
  public double min();
  public double max();
  public Double floor(double k);
  public Double ceiling(double k);
  public int rank(double k);
  public double select(double k);
  public void deleteMin();
  public void deleteMax();
  int size(double lo, double hi);
  public Iterable<Double> keys(double lo, double hi);  
  public Iterable<Double> keys();  
}

