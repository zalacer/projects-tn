package st;

import java.util.Iterator;

public interface UnorderedSETdouble {
  // for Ex3506
  // based on API for a basic set data type p389 Algorithms4ed
  public void add(double x);
  public void delete(double x);
  public boolean contains(double c);
  public boolean isEmpty();
  public int size();
  public Iterator<Double> iterator();
}

