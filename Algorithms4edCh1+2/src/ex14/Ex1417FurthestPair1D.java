package ex14;

import static v.ArrayUtils.*;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.Random;

//  p210
//  1.4.17 Farthest pair (in one dimension). Write a program that, given an array  a[] of N
//  double values, finds a farthest pair : two values whose difference is no smaller than the
//  the difference of any other pair (in absolute value). The running time of your program
//  should be linear in the worst case.

public class Ex1417FurthestPair1D {
  
  public static double[] furthestPair(double[] z) {
    // returns a furthest pair in z as an array of length two or an empty array if not
    // running time O(NlogN)
    if (z == null || z.length < 2) return new double[0];
    if (z.length == 2) return z;
    Arrays.sort(z); // O(NlogN)
    double[] m = new double[2];
    double max = Double.NEGATIVE_INFINITY;
    double d = 0;
    for (int i = 0; i < z.length-1; i++) {
      d = abs(z[i]-z[i+1]);
      if (d == Double.POSITIVE_INFINITY) return new double[]{z[i], z[i+1]};
      else if (d > max) { m[0] = z[i]; m[1] = z[i+1]; max = d; }
    }
    return m;
  }
  
  public static void main(String[] args) {
    
    double[] a = new double[]{1,2,3,3.5,4,5,5.3,7,8,9};
    pa(furthestPair(a)); //double[5.3,7.0]
    
    a = new double[]{7};
    pa(furthestPair(a)); //double[]   
    
    Random r = new Random(797);
    a = r.doubles(100000, -10000, 10001).toArray();
    pa(furthestPair(a)); //double[8617.085371701512,8619.165458604351]
    
 
  }

}
