package v;


//import java.util.Objects;

public final class Tuple2<K,V> implements Cloneable {
//  private HashFunction hf = Hashing.murmur3_32();
  public K _1; 
  public V _2; 
    
  public Tuple2(){}
  
  public Tuple2(K k, V v) {
    this._1 = k; this._2 = v;
  }
  
  public Tuple2(Tuple2<K,V> t) {
    this._1 = t._1; this._2 = t._2;
  }
  
  public void add(K k, V v) {
    _1 = k; _2 = v;
  }
  
  public void add1(K k) {
    _1 = k; 
  }
  
  public void add2(V v) {
    _2 = v; 
  }
  
  public void set(K k, V v) {
    _1 = k; _2 = v;
  }
  
  public void set1(K k) {
    _1 = k; 
  }
  
  public void set2(V v) {
    _2 = v; 
  }
  
  public Tuple2<V,K> swap(Tuple2<K,V> t) {
    return new Tuple2<V,K>(t._2,t._1);
  }
  
  @SuppressWarnings("unchecked")
  public Tuple2<K,V> clone() {
    try {
      return (Tuple2<K,V>) super.clone();
    } catch (CloneNotSupportedException e) {
      return new Tuple2<K,V>(_1, _2);
    }    
  }
  
  public boolean equalOrReverseTo(Tuple2<K,V> t) {
    if (_1.equals(t._1) && _2.equals(t._2) || _1.equals(t._2) && _2.equals(t._1))
      return true;
    return false;
  }
  
//  @Override
//  public int hashCode() {
//    return Objects.hash(_1, _2);
//  }
//  

  @Override
  public int hashCode() {
    final int prime1 = 397;
    int result = 1;
    result = prime1 * result + ((_1 == null) ? 0 : _1.hashCode());
    result = prime1 * result + ((_2 == null) ? 0 : _2.hashCode());
    return result & 0x7fffffff;
  }
    
//  public int h() {
//    return hf.newHasher().putInt(_1.hashCode()).putInt(_2.hashCode()).hash().asInt();
//  }

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


