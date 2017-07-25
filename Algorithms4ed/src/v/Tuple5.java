package v;

import java.util.Objects;

public final class Tuple5<U,V,W,X,Y> implements Cloneable {
  public U _1; 
  public V _2; 
  public W _3;
  public X _4;
  public Y _5;
  
  public Tuple5() {}
  
  public Tuple5(U u, V v, W w, X x, Y y) {
    this._1 = u; this._2 = v; this._3 = w; this._4 = x; this._5 = y;
  }
  
  @SuppressWarnings("unchecked")
  public Tuple5<U,V,W,X,Y> clone() throws CloneNotSupportedException {
    // this return a shallow copy, i.e. copies _1, _2 and _3
    return (Tuple5<U,V,W,X,Y>) super.clone();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4, _5);
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
    Tuple5 other = (Tuple5) obj;
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
    if (_5 == null) {
      if (other._5 != null)
        return false;
    } else if (!_5.equals(other._5))
      return false;
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+","+_3+","+_4+","+_5+")";
  }

}


