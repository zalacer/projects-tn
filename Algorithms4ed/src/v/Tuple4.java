package v;

import java.util.Objects;

public final class Tuple4<U,V,W,X> implements Cloneable {
  public U _1; 
  public V _2; 
  public W _3;
  public X _4;
  
  public Tuple4() {}
  
  public Tuple4(U u, V v, W w, X x) {
    this._1 = u; this._2 = v; this._3 = w; this._4 = x;
  }
  
  @SuppressWarnings("unchecked")
  public Tuple4<U,V,W,X> clone() throws CloneNotSupportedException {
    // this return a shallow copy, i.e. copies _1, _2 and _3
    return (Tuple4<U,V,W,X>) super.clone();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4);
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
    Tuple4 other = (Tuple4) obj;
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
    if (_3 == null) {
      if (other._3 != null)
        return false;
    } else if (!_3.equals(other._3))
      return false;
    if (_4 == null) {
      if (other._4 != null)
        return false;
    } else if (!_4.equals(other._4))
      return false;
    return true;
  }
  
  @Override public String toString() {
    return "("+_1+","+_2+","+_3+","+_4+")";
  }

}


