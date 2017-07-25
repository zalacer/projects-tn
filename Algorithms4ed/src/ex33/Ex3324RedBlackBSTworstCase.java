package ex33;

import static analysis.Log.lg;
import static v.ArrayUtils.*;

import java.util.Iterator;

import st.RedBlackBSTX;
import v.Tuple3;

/* p451
  3.3.24 Worst case for red-black BSTs. Show how to construct a 
  red-black BST demonstrating that, in the worst case, almost all 
  the paths from the root to a null link in a red-black BST of N 
  nodes are of length 2 lg N.
  
  In testing all permutations of input arrays of the form range(0,N)
  for N in [2,11] with st.RedBlackBSTX and computing path lengths by 
  adding one to the number of nodes in a path from root to a leaf node, 
  testing  shows that for N = [3..5] all trees are worst case and for 
  all other N no trees are worst case using a cutoff of 50% to quantify 
  "almost all", that is at least half the paths in a tree must have the 
  worst case length in order for it to count as a worst case tree. When 
  the cutoff is .9 (90%) then all trees with N = 3 and 48/120 trees with 
  N = 5 are worst case.
  
  In order to construct a worst case red-black tree of size 5 with .9
  cutoff do it so that it has just two paths each of which ends with a
  red leaf.
  
  If path length were defined as just the number of nodes in a path from
  root to a leaf, there are no worst case red-black trees, at least when
  using st.RedBlackBSTX. The definition of path length was changed to get 
  some results.
 
  In conclusion, red-black trees are supposed to be self-balancing and
  and this experiment demonstrates that quality.
  
  The test method I used is given below. 
 */             

public class Ex3324RedBlackBSTworstCase {
  
  public static void redBlackTreeWorstCaseTest(int size, double cutoff) {
    // print a map of Integer array permutations to a Tuple3 of
    // path length, number of occurences of path length and the total
    // number of paths for trees constructed from all permutations of
    // range(0,size) where each permutation is used as the input Key 
    // array for an st.RedBlackBSTX iff any such permutation results in 
    // a tree with at least one worst case tree defined as a tree with
    // a proportion of worst case paths equal to or greater than cutoff
    // and a worst case path is defined as one with length greater or
    // equal to floor(2*lg(size)) where path length is computed as one 
    // plus the number of nodes in a path from root to a leaf node.
    int n = size;
    RedBlackBSTX<String,Tuple3<Integer,Integer,Integer>> mapr = new RedBlackBSTX<>();
    Integer[] a = rangeInteger(0,n);
    Integer[] b = a.clone(), c = new Integer[n]; 
    int wc = 0, tot = 0, maxlen = 0;
    Iterator<int[]> it = permutations(range(0,n));
    while (it.hasNext()) {
      int[] d = it.next();
      c = (Integer[]) box(d);
      RedBlackBSTX<Integer, Integer> st = new RedBlackBSTX<>(c,b);
      RedBlackBSTX<Integer,Integer> map = st.pathLengths();
      maxlen = (int)(Math.floor(2*lg(st.size())));
      tot = 0;
      if (map.contains(maxlen)) {
        wc = map.get(maxlen); // worst case
        Iterator<Integer> ii = map.keys().iterator();
        while (ii.hasNext()) tot += map.get(ii.next());
        ii = map.keys().iterator();
        if (1.0*wc/tot >= cutoff) {
          while (ii.hasNext()) { 
            Integer x = ii.next();
            mapr.put(arrayToString(c,999,1,1),
              new Tuple3<Integer,Integer,Integer>(x,map.get(x),tot));
          }
        }
      }
    }
    if (mapr.size() > 0) {
      System.out.println("number of worst case trees = "+mapr.size()
          +"/"+factorial(n));
      System.out.println("maxlen = "+maxlen);
      Iterator<String> is = mapr.keys().iterator();
      System.out.println("             path    number of   total number");
      System.out.println("key array    length  such paths  of paths ");
      while (is.hasNext()) {
        String s = is.next(); 
        Tuple3<Integer,Integer,Integer> t3 = mapr.get(s);
        System.out.printf("%-12s %-3d     %-3d         %-4d\n", 
            s, t3._1, t3._2, t3._3);
      }
    } else {
      System.out.println("there is no qualifying worst case tree");
    }
  }

  public static void main(String[] args) {

    // print data for worst case red-black trees of size 5 and
    // cutoff requirement of 90% or more worst case paths
    redBlackTreeWorstCaseTest(5, .9);
    
  }

}

