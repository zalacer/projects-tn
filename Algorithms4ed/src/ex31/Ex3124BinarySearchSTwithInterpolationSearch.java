package ex31;

import static analysis.Log.lg;
import static java.lang.Math.floor;
import static v.ArrayUtils.*;
import st.BinaryInterpolationSearchSTex3124;

/* p391
  3.1.24 Interpolation search. Suppose that arithmetic operations 
  are allowed on keys (for example they may be Double or Integer 
  values). Write a version of binary search that mimics the process 
  of looking near the beginning of a dictionary when the word begins 
  with a letter near the beginning of the alphabet. Specifically, if 
  x is the key value sought, lo is the key value of the first key in 
  the table, and hi is the key value of the last key in the table, 
  look first at floor((x - keys[lo])/(hi - lo)), not at key with the halfway 
  index. Test your implementation against BinarySearchST for 
  FrequencyCounter using SearchCompare.
  
  This is implemented in st.BinaryInterpolationSearchSTex3124 with demo below
  showing that interpolation search greatly improves performance by reducing 
  the number of iterations in the looop in rank().
  
*/

@SuppressWarnings("unused")
public class Ex3124BinarySearchSTwithInterpolationSearch {
  
  public static void main(String[] args) {
    
    Double[] keys = rangeDouble(0.,10000.);
    Integer[] values = rangeInteger(0, keys.length);
    
    BinaryInterpolationSearchSTex3124<Double,Integer> st = 
        new BinaryInterpolationSearchSTex3124<>(keys,values);
  
    // using standard binary search
    System.out.println("st.rankOrig="+st.rankOrig(9999.));     // 9999
    System.out.println("iterations="+st.getIterations()+"\n"); // 15
        
    // using interpolation search
    System.out.println("st.rank="+st.rank(9999.));             // 9999
    System.out.println("iterations="+st.getIterations());      // 1
    
  
  }
}
