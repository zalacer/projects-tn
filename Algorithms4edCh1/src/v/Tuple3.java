package v;

import java.util.Objects;

public final class Tuple3<U,V,W> implements Cloneable {
  U _1; V _2; W _3;
  
  public Tuple3() {}
  
  public Tuple3(U u, V v, W w) {
    this._1 = u; this._2 = v; this._3 = w;
  }
  
  @SuppressWarnings("unchecked")
  public Tuple3<U,V,W> clone() throws CloneNotSupportedException {
    // this return a shallow copy, i.e. copies _1, _2 and _3
    return (Tuple3<U,V,W>) super.clone();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(_1, _2, _3);
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
    Tuple3 other = (Tuple3) obj;
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
    return true;
  }

  @Override public String toString() {
    return "("+_1+","+_2+","+_3+")";
  }

}


