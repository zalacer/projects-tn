package v;

import java.util.Objects;

public final class Interval<L,R> implements Cloneable {
  public L left; 
  public R right; 
    
  public Interval(){}
  
  public Interval(L l, R r) {
    this.left= l; this.right = r;
  }
  
  public Interval(Interval<L,R> t) {
    this.left = t.left; this.right = t.right;
  }
  
  public void setLeft(L l) { left = l; }
  
  public L getLeft() { return left; }
  
  public void setRight(R r) { right = r; }
  
  public R getRight() { return right; }


  
  public Interval<R,L> swap(Interval<L,R> t) {
    return new Interval<R,L>(t.right,t.left);
  }
  
  @SuppressWarnings("unchecked")
  public Interval<L,R> clone() throws CloneNotSupportedException  {
    // this return a shallow copy, i.e. copies _1 and _2
    return (Interval<L,R>) super.clone();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(left, right);
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
    Interval other = (Interval) obj;
    if (left == null) {
      if (other.left != null)
        return false;
    } else if (!left.equals(other.left))
      return false;
    if (right == null) {
      if (other.right != null)
        return false;
    } else if (!right.equals(other.right))
      return false;
    return true;
  }

  @Override public String toString() {
    return "("+left+","+right+")";
  }

}


