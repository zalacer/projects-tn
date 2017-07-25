package pq;

import java.math.BigInteger;

// for ex2425

// based on http://algs4.cs.princeton.edu/24pq/CubeSum.java
/******************************************************************************
 *  http://algs4.cs.princeton.edu/24pq/CubeSum.java
 *  http://algs4.cs.princeton.edu/24pq/CubeSum.java.html
 *  Compilation:  javac CubeSum.java
 *  Execution:    java CubeSum n
 *  Dependencies: MinPQ.java
 * 
 *  Print out integers of the form a^3 + b^3 in sorted order, where
 *  0 <= a <= b <= n.
 *
 *  % java CubeSum 10
 *  0 = 0^3 + 0^3
 *  1 = 0^3 + 1^3
 *  2 = 1^3 + 1^3
 *  8 = 0^3 + 2^3
 *  9 = 1^3 + 2^3
 *  ...
 *  1729 = 9^3 + 10^3
 *  1729 = 1^3 + 12^3
 *  ...
 *  3456 = 12^3 + 12^3
 *
 *  Remarks
 *  -------
 *   - Easily extends to handle sums of the form f(a) + g(b)
 *   - Prints out a sum more than once if it can be obtained
 *     in more than one way, e.g., 1729 = 9^3 + 10^3 = 1^3 + 12^3
 *
 ******************************************************************************/

public class SumCubes implements Comparable<SumCubes> {
  private final BigInteger sum;
  private final int i;
  private final int j;

  public SumCubes(int i, int j) {
    BigInteger bi = new BigInteger(""+i);
    BigInteger bic = ((bi.multiply(bi)).multiply(bi));
    BigInteger bj = new BigInteger(""+j);
    BigInteger bjc = ((bj.multiply(bj)).multiply(bj));
    BigInteger bsum = bic.add(bjc);
    this.sum = bsum;
    this.i = i;
    this.j = j;
  }

  public int compareTo(SumCubes that) {
    return this.sum.compareTo(that.sum);
  }

  public String toString() {
    return sum + " = " + i + "^3" + " + " + j + "^3";
  }

  // this way maximizes storage requirement
  public static void printPairsWithSameCubeSum2(int n) {
    // print all pairs of ints such that a^3 + b^3 = c^3 + d^3 
    // for a, b, c, d between 0 and n
    MinPQ<SumCubes> pq = new MinPQ<SumCubes>();
    for (int i = 1; i < n; i++)
      for (int j = i; j < n; j++)
        pq.insert(new SumCubes(i, j));

    SumCubes s = null, t = null;

    if(!pq.isEmpty()) s = pq.delMin();

    while (!pq.isEmpty()) {
      t = pq.delMin();
      if (s.sum.compareTo(t.sum) == 0) {
        if (s.i == t.j && s.j == t.i) continue;
        //       System.out.println(s+" and "+t+" have same sum");
        if (s.i<s.j) System.out.print("("+s.i+","+s.j+"), ");
        else System.out.print("("+s.j+","+s.i+"), ");
        if (t.i<t.j) System.out.println("("+t.i+","+t.j+")");
        else System.out.println("("+t.j+","+t.i+")");
      }
      s = t;
    }  
  }

  // this way minimizes storage requirement
  public static void printPairsWithSameCubeSum(int n) {
    // print all pairs of ints such that a^3 + b^3 = c^3 + d^3 
    // for a, b, c, d between 0 and n

    // initialize priority queue
    MinPQ<SumCubes> pq = new MinPQ<SumCubes>();
    for (int i = 0; i <= n; i++) {
      pq.insert(new SumCubes(i, i));
    }

    SumCubes s = null, t = null;
    if(!pq.isEmpty()) s = pq.delMin();

    while (!pq.isEmpty()) {
      t = pq.delMin();
      if (s.sum.compareTo(t.sum) == 0) {
        if (s.i < t.i) {
          if (s.i<s.j) System.out.print("("+s.i+","+s.j+"), ");
          else System.out.print("("+s.j+","+s.i+"), ");
          if (t.i<t.j) System.out.println("("+t.i+","+t.j+")");
          else System.out.println("("+t.j+","+t.i+")");
        }
        else {
          if (t.i<t.j) System.out.print("("+t.i+","+t.j+"), ");
          else System.out.print("("+t.j+","+t.i+"), ");
          if (s.i<s.j) System.out.println("("+s.i+","+s.j+")");
          else System.out.println("("+s.j+","+s.i+")");
        }
      }
      s = t;
      if (s.j < n)
        pq.insert(new SumCubes(s.i, s.j + 1));
    }
  }

  public static void main(String[] args) {

    printPairsWithSameCubeSum(100);

  }

}

