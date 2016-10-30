package ex12;

import static java.lang.Double.MIN_VALUE;
import static java.lang.Math.random;
import edu.princeton.cs.algs4.Point2D;

//p114
//  1.2.1  Write a  Point2D client that takes an integer value N from the command line,
//  generates N random points in the unit square, and computes the distance separating
//  the closest pair of points.

public class Ex1201Point2D {

  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]); // number of points to generate
    Point2D[] a = new Point2D[n];
    // assuming points will be in the unit square inclusive
    for (int i = 0; i < n; i++) {
      a[i] = new Point2D(random()*(1+MIN_VALUE), random()*(1+MIN_VALUE));
    }
    double min = Double.POSITIVE_INFINITY;
    double d = 0;
    for (int j = 0; j < n; j++) {
      for (int k = j+1; k < n; k++) {
//        System.out.println(random()*(1+MIN_VALUE));
        d = a[j].distanceTo(a[k]);
        if (d < min) min = d;   
      }
    }
    System.out.println(d); // 0.6989081305190095 for 1M points
  }

}
