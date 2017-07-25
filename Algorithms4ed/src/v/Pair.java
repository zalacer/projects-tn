package v;

import java.util.Objects;

public final class Pair<L,R> implements Cloneable {
  public L left; 
  public R right; 
    
  public Pair(){}
  
  public Pair(L l, R r) {
    this.left= l; this.right = r;
  }
  
  public Pair(Pair<L,R> t) {
    this.left = t.left; this.right = t.right;
  }
  
  public void setLeft(L l) { left = l; }
  
  public L getLeft() { return left; }
  
  public void setRight(R r) { right = r; }
  
  public R getRight() { return right; }


  
  public Pair<R,L> swap(Pair<L,R> t) {
    return new Pair<R,L>(t.right,t.left);
  }
  
  @SuppressWarnings("unchecked")
  public Pair<L,R> clone() throws CloneNotSupportedException  {
    // this return a shallow copy, i.e. copies _1 and _2
    return (Pair<L,R>) super.clone();
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
    Pair other = (Pair) obj;
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


