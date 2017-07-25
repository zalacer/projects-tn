package v;

import java.util.Objects;

public final class Tuple9<R,S,T,U,V,W,X,Y,Z> implements Cloneable {
  public R _1;
  public S _2;
  public T _3; 
  public U _4; 
  public V _5;
  public W _6;
  public X _7;
  public Y _8;
  public Z _9;
  
  public Tuple9() {}
  
  public Tuple9(R r, S s, T t, U u, V v, W w, X x, Y y, Z z) {
    this._1 = r; this._2 = s; this._3 = t; this._4 = u; 
    this._5 = v; this._6 = w; this._7 = x; this._8 = y; this._9 = z;
  }
  
  @SuppressWarnings("unchecked")
  public Tuple9<R,S,T,U,V,W,X,Y,Z> clone() throws CloneNotSupportedException {
    // this return a shallow copy, i.e. copies _1, _2, _3, _4, _5, _6, _7, _8, _9
    return (Tuple9<R,S,T,U,V,W,X,Y,Z>) super.clone(); 
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8, _9);
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
    Tuple9 other = (Tuple9) obj;
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
    if (_9 == null) {
      if (other._9 != null)
        return false;
    } else if (!_9.equals(other._9))
      return false; 
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+","+_3+","+_4+","+_5+","+_6+","+_7+","+_8+","+_9+")";
  }

}


