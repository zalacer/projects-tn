package st;

import java.util.Iterator;

public interface UnorderedSET<X> extends Iterable<X> {
  // for Ex3501
  // based on API for a basic set data type p389 Algorithms4ed
  public void add(X x);
  public void delete(X x);
  public boolean contains(X c);
  public boolean isEmpty();
  public int size();
  public Iterator<X> iterator();
}

