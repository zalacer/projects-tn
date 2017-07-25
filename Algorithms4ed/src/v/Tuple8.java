package v;

import java.util.Objects;

public final class Tuple8<S,T,U,V,W,X,Y,Z> implements Cloneable {
  public S _1;
  public T _2; 
  public U _3; 
  public V _4;
  public W _5;
  public X _6;
  public Y _7;
  public Z _8;
  
  public Tuple8() {}
  
  public Tuple8(S s, T t, U u, V v, W w, X x, Y y, Z z) {
    this._1 = s; this._2 = t; this._3 = u; this._4 = v; 
    this._5 = w; this._6 = x; this._7 = y; this._8 = z; 
  }
  
  @SuppressWarnings("unchecked")
  public Tuple8<S,T,U,V,W,X,Y,Z> clone() throws CloneNotSupportedException {
    // this return a shallow copy, i.e. copies _1, _2, _3, _4, _5, _6, _7, _8
    return (Tuple8<S,T,U,V,W,X,Y,Z>) super.clone(); 
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8);
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
    Tuple8 other = (Tuple8) obj;
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
    if (_6 == null) {
      if (other._6 != null)
        return false;
    } else if (!_6.equals(other._6))
      return false;
    if (_7 == null) {
      if (other._7 != null)
        return false;
    } else if (!_7.equals(other._7))
      return false;
    if (_8 == null) {
      if (other._8 != null)
        return false;
    } else if (!_8.equals(other._8))
      return false;
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+","+_3+","+_4+","+_5+","+_6+","+_7+","+_8+")";
  }

}


