package ex33;

import static v.ArrayUtils.*;

import java.util.Iterator;

import st.BSTX;
import st.TwoThreeTree;

/* p449
  3.3.6  Find the probability that each of the 2-3 trees in Exercise 
  3.3.5 is the result of the insertion of N random distinct keys into 
  an initially empty tree. 
 */             

public class Ex3306ProbabilityOf23TreeShapes {

  public static void probabilities() {
    System.out.println("N   shape                 probability");
    for (int i = 1; i < 11; i++) {
      int[] a = range(0,i), b; double sum, l;
      Integer[] c = rangeInteger(0,i), d = new Integer[i];
      BSTX<String,Integer> bst = new BSTX<>();
      TwoThreeTree<Integer,Integer> ttt;
      Iterator<int[]> it = permutations(a);
      while (it.hasNext()) {
        b = it.next();
        d = (Integer[])box(b);
        ttt = new TwoThreeTree<>(d,c);
        String s = ttt.shape();
        if (bst.contains(s)) bst.put(s, bst.get(s)+1);   
        else {
          bst.put(s,1);
        }
      }
      sum = 0;
      Iterator<String> is = bst.inOrder().iterator();
      while (is.hasNext()) sum += bst.get(is.next());
      is = bst.inOrder().iterator();
      boolean first = true;
      while (is.hasNext()) {
        String k = is.next();
        l = bst.get(k)/sum;
        if (first) {
          System.out.printf("%-2d  %-20s  %5.3f\n", i, k, l); 
          first = false;
        } else System.out.printf("    %-20s  %5.3f\n", k, l); 
      } 
    }
  }

  public static void main(String[] args) {

    probabilities();
/*
    N   shape                 probability
    1   :1;                   1.000
    2   :2;                   1.000
    3   :1;:1;1;              1.000
    4   :1;:1;2;              1.000
    5   :1;:2;2;              0.400
        :2;:1;1;1;            0.600
    6   :2;:1;1;2;            1.000
    7   :1;:1;1;:1;1;1;1;     0.429
        :2;:1;2;2;            0.571
    8   :1;:1;1;:1;1;1;2;     0.857
        :2;:2;2;2;            0.143
    9   :1;:1;1;:1;1;2;2;     0.714
        :1;:1;2;:1;1;1;1;1;   0.286
    10  :1;:1;1;:1;2;2;2;     0.286
        :1;:1;2;:1;1;1;1;2;   0.714
*/

  }

}

