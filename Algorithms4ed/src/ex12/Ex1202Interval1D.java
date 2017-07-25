package ex12;

import static v.ArrayUtils.*;

import java.util.Scanner;

import edu.princeton.cs.algs4.Interval1D;

//  1.2.2  Write an  Interval1D client that takes an  int value N as command-line argu-
//  ment, reads N intervals (each defined by a pair of  double values) from standard input,
//  and prints all pairs that intersect.

public class Ex1202Interval1D {

  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]); // number of intervals to generate
    Scanner sc = new Scanner(System.in);
    int count = 0;
    double[] da = new double[2*n];
    while (count <= 2*n) {
      if (sc.hasNextDouble()) 
        da[count] = sc.nextDouble();
      count++;
    }
    sc.close();
    pa(da);
    Interval1D[] a = new Interval1D[n];
    double r1 = 0;
    double r2 = 0;
    double r3 = 0;
    count = 0;
    for (int i = 0; i < 2*n-1; i+=2) {
      r1 = da[i]; r2 = da[i+1];
      if (r1 <= r2) {
        r3=r2;
      } else {
        r3 = r1; r1 = r2;
      } 
      a[count++] = new Interval1D(r1, r3);
    }
    pa(a);
    for (int j = 0; j < n; j++)
      for (int k = j+1; k < n; k++) {
        if (a[j]!=null && a[k]!=null) {
          if (a[j].intersects(a[k]))
            System.out.println(a[j]+","+a[k]);
        }
      }
  }
}

//  0 5 7 9 4 6 3 8 5.5 6.5 // input
//  double[0.0,5.0,7.0,9.0,4.0,6.0,3.0,8.0,5.5,6.5] // double[] da
//  Interval1D[[0.0,5.0],[7.0,9.0],[4.0,6.0],[3.0,8.0],[5.5,6.5]] Interval1D[] a
//  [0.0, 5.0],[4.0, 6.0] // intersections
//  [0.0, 5.0],[3.0, 8.0]
//  [7.0, 9.0],[3.0, 8.0]
//  [4.0, 6.0],[3.0, 8.0]
//  [4.0, 6.0],[5.5, 6.5]
//  [3.0, 8.0],[5.5, 6.5]
