package st;

public interface UnorderedMultiST<K> {
  // for Ex3122
  // based on API for a generic basic symbol table p363 Algorithms4ed
  // but without Value and get(Key) is private and returns boolean.
  // not naming this ST to avoid conflict with existing classes.
  public void put(K k);
  public void delete(K k);
  public boolean contains(K k);
  public boolean isEmpty();
  public int size();
  public Iterable<K> keys();  
}
