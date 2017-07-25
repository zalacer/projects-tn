package st;

import java.util.Iterator;

public interface UnorderedSETint {
  // for Ex3506
  // based on API for a basic set data type p389 Algorithms4ed
  public void add(int x);
  public void delete(int x);
  public boolean contains(int c);
  public boolean isEmpty();
  public int size();
  public Iterator<Integer> iterator();
}

