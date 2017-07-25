package v;

//import java.util.Objects;

public final class Tuple2C<K extends Comparable<? super K>,V extends Comparable<? super V>>
  implements Comparable<Tuple2C<K,V>>, Cloneable {
  public K _1; 
  public V _2; 
    
  public Tuple2C(){}
  
  public Tuple2C(K k, V v) {
    this._1 = k; this._2 = v;
  }
  
  public Tuple2C(Tuple2C<K,V> t) {
    this._1 = t._1; this._2 = t._2;
  }
  
  public Tuple2C<V,K> swap(Tuple2C<K,V> t) {
    return new Tuple2C<V,K>(t._2,t._1);
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2C<K,V> clone() throws CloneNotSupportedException  {
    // this return a shallow copy, i.e. copies _1 and _2
    return (Tuple2C<K,V>) super.clone();
  }
  
//  @Override
//  public int hashCode() {
//    return Objects.hash(_1, _2);
//  }
  
  @Override
  public int hashCode() {
    final int prime1 = 397;
    int result = 1;
    result = prime1 * result + ((_1 == null) ? 0 : _1.hashCode());
    result = prime1 * result + ((_2 == null) ? 0 : _2.hashCode());
    return result & 0x7fffffff;
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
    Tuple2C other = (Tuple2C) obj;
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
  public int compareTo(Tuple2C<K, V> o) {
    int d = this._2.compareTo(o._2);
    return d != 0 ? d : _1.compareTo(o._1);
  }

}


