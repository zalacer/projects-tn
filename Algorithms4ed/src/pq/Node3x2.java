package pq;

public class Node3x2<Key> {
  int N1; Key data1; Node3x2<Key> parent1, left1, right1; // min
  int N2; Key data2; Node3x2<Key> parent2, left2, right2; // max
  public Node3x2(Key d, int n) { data1 = d; N1 = n; data2 = d; } // min
  public Node3x2(Key d, int n, String max) { data2 = d; N2 = n; } // max used only to
  @Override public String toString() {                            // distinguish constructors
    Key p1 = parent1 == null ? null : parent1.data1;
    Key l1 = left1 ==   null ? null : left1.data1;
    Key r1 = right1 ==  null ? null : right1.data1;
    Key p2 = parent2 == null ? null : parent2.data2;
    Key l2 = left2 ==   null ? null : left2.data2;
    Key r2 = right2 ==  null ? null : right2.data2;
    return "d1="+data1+": N1="+N1+": p1="+p1+": l1="+l1+": r1="+r1+""
        +"|d2="+data2+": N2="+N2+": p2="+p2+": l2="+l2+": r2="+r2; 
  }
  public static void main(String[] args) {

  }

}
