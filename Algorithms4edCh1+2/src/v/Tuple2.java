package v;

import java.util.Objects;

public final class Tuple2<K,V> implements Cloneable {
  public K _1; 
  public V _2; 
    
  public Tuple2(){}
  
  public Tuple2(K k, V v) {
    this._1 = k; this._2 = v;
  }
  
  public Tuple2(Tuple2<K,V> t) {
    this._1 = t._1; this._2 = t._2;
  }
  
  public Tuple2<V,K> swap(Tuple2<K,V> t) {
    return new Tuple2<V,K>(t._2,t._1);
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2<K,V> clone() throws CloneNotSupportedException  {
    // this return a shallow copy, i.e. copies _1 and _2
    return (Tuple2<K,V>) super.clone();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("rawtypes")
    Tuple2 other = (Tuple2) obj;
    if (_1 == null) {
      if (other._1 != null)
        return false;
    } else if (!_1.equals(other._1))
      return false;
    if (_2 == null) {
      if (other._2 != null)
        return false;
    } else if (!_2.equals(other._2))
      return false;
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+")";
  }

}


