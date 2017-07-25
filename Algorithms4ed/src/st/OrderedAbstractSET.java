package st;

public abstract class OrderedAbstractSET<X extends Comparable<X>> implements OrderedSET<X> {
  // for Ex3501
  // this is implemented to ensure existence of empty constructor 
  // even if non-empty constructors are defined later.
  OrderedAbstractSET(){}
}
