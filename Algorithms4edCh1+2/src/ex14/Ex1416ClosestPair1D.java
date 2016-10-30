package ex14;

import static v.ArrayUtils.*;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.Random;

//  p210
//  1.4.16 Closest pair (in one dimension). Write a program that, given an array a[] of N
//  double values, finds a closest pair : two values whose difference is no greater than the
//  the difference of any other pair (in absolute value). The running time of your program
//  should be linearithmic in the worst case.

public class Ex1416ClosestPair1D {
  
  public static double[] closestPair(double[] z) {
    // returns a closest pair in z as an array of length two or an empty array if not
    // running time O(NlogN)
    if (z == null || z.length < 2) return new double[0];
    if (z.length == 2) return z;
    Arrays.sort(z); // O(NlogN)
    double[] m = new double[2];
    double min = Double.POSITIVE_INFINITY;
    double d = 0;
    for (int i = 0; i < z.length-1; i++) {
      d = abs(z[i]-z[i+1]);
      if (d == 0) return new double[]{z[i], z[i+1]};
      else if (d < min) { m[0] = z[i]; m[1] = z[i+1]; min = d; }
    }
    return m;
  }
  
  public static void main(String[] args) {
    
    double[] a = new double[]{1,2,3,4,5,5.3,6,7,8,9};
    pa(closestPair(a)); //double[5.0,5.3]
    
    a = new double[]{7};
    pa(closestPair(a)); //double[]
    
    a = new double[]{1,2,3,3,4,5,5.3,6,7,8,9};
    pa(closestPair(a)); //double[3.0,3.0]
    
    Random r = new Random(907);
    a = r.doubles(100000, -10000, 10001).toArray();
    pa(closestPair(a)); //double[4194.461809163786,4194.46181196384]
    
 
  }

}
