package pq;

public class Node3<Key> {
  int N; Key data; Node3<Key> parent, left, right;
  public Node3(Key d, int n) { data = d; N = n; }
//  @Override public String toString() { return "("+data+","+N+")"; }
  @Override public String toString() {
    Key p = parent == null ? null : parent.data;
    Key l = left == null ? null : left.data;
    Key r = right == null ? null : right.data;
    return "(d="+data+", n="+N+", p="+p+", l="+l+", r="+r+")"; 
  }
  public static void main(String[] args) {

  }

}
