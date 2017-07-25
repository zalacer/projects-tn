package v;

import java.util.Objects;

public final class Tuple2K<K extends Comparable<? super K>,V>
  implements Comparable<Tuple2K<K,V>>, Cloneable {
  public K _1; 
  public V _2; 
    
  public Tuple2K(){}
  
  public Tuple2K(K k, V v) {
    this._1 = k; this._2 = v;
  }
  
  public Tuple2K(Tuple2K<K,V> t) {
    this._1 = t._1; this._2 = t._2;
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2K<K,V> clone() throws CloneNotSupportedException  {
    // this return a shallow copy, i.e. copies _1 and _2
    return (Tuple2K<K,V>) super.clone();
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
    Tuple2K other = (Tuple2K) obj;
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

  @Override
  public int compareTo(Tuple2K<K, V> o) {
    return this._1.compareTo(o._1);
  }

}


