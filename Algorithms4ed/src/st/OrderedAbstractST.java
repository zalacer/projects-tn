package st;

public abstract class OrderedAbstractST<K extends Comparable<? super K>,V> implements OrderedST<K,V> {
  // for Ex3103
  // this is implemented to ensure existence of empty constructor 
  // even if non-empty constructors are defined later.
  OrderedAbstractST(){}
}
