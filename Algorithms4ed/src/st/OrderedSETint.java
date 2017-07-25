package st;

import java.util.Iterator;

public interface OrderedSETint {
  // for Ex3507
  // based on API for a basic set data type p389 Algorithms4ed
  public void add(int x);
  public void delete(int x);
  public boolean contains(int c);
  public boolean isEmpty();
  public int size();
  public Iterator<Integer> iterator();
  public int max();
  public int min();
  public Integer ceiling(int x);
  public Integer floor(int x);
}

