package st;

public abstract class UnorderedAbstractST<K,V> implements UnorderedST<K,V> {
  // for Ex3103
  // this is implemented to ensure existence of empty constructor 
  // even if non-empty constructors are defined later.
  UnorderedAbstractST(){}
}
