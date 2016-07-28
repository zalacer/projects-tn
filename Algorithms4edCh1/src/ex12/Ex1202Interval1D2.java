package ex12;

import java.util.Random;

import edu.princeton.cs.algs4.Interval1D;

//  1.2.2  Write an  Interval1D client that takes an  int value N as command-line argu-
//  ment, reads N intervals (each defined by a pair of  double values) from standard input,
//  and prints all pairs that intersect.

public class Ex1202Interval1D2 {

  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]); // number of points to generate
    Interval1D[] a = new Interval1D[n];
    // assuming points will be in the unit square inclusive
    Random r = new Random();
    double r1 = 0;
    double r2 = 0;
    double r3 = 0;
    for (int i = 0; i < n; i++) {
      r1 = r.nextDouble()*100;
      r2 = r.nextDouble()*100;
      if (r1 <= r2) {
        r3=r2;
      } else {
        r3 = r1; r1 = r2;
      } 
      a[i] = new Interval1D(r1, r3);
    }
    double min = Double.POSITIVE_INFINITY;
    double d = 0;
    for (int j = 0; j < n; j++) {
      for (int k = j+1; k < n; k++) {
        if (a[j].intersects(a[k]))
          System.out.println(a[j]+","+a[k]);
        if (d < min) min = d;   
      }
    }
    System.out.println(d); // 0.6989081305190095 for 1M points
  }

}
