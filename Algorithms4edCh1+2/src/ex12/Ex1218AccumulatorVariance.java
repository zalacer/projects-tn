package ex12;

import static edu.princeton.cs.algs4.StdStats.*;

//  1.2.18 Variance for accumulator. Validate that the following code, which adds the
//  methods  var() and  stddev() to  Accumulator , computes both the mean and variance
//  of the numbers presented as arguments to  addDataValue() 
//    public static class Accumulator {
//      private double m;
//      private double s;
//      private int N;
//      public void addDataValue(double x) {
//        N++;
//        s = s + 1.0 * (N-1) / N * (x - m) * (x - m);
//        m = m + (x - m) / N;
//      }
//      public double mean() {
//        return m; 
//      }
//      public double var() {
//        return s/(N - 1); 
//      }
//      public double stddev() {
//        return Math.sqrt(this.var()); 
//      }
//    }


//  This implementation is less susceptible to roundoff error than the straightforward im-
//  plementation based on saving the sum of the squares of the numbers.

public class Ex1218AccumulatorVariance {

  public static class Accumulator {
    private double m;
    private double s;
    private int N;
    
    public void addDataValue(double x) {
      N++;
      s = s + 1.0 * (N-1) / N * (x - m) * (x - m);
      m = m + (x - m) / N;
    }
    
    public double mean() {
      return m; 
    }

    public double var() {
      return s/(N - 1); 
    }

    public double stddev() {
      return Math.sqrt(this.var()); 
    }
  }

  public static void main(String[] args) {
    Accumulator a = new Accumulator();
    double[] b = new double[100];
    for (int i = 1; i < 101; i++) {
      double v = i+(double)i/10;
      a.addDataValue(v);
      b[i-1] = v;
    }
    System.out.println(a.mean());             //55.549999999999926
    System.out.println(mean(b));              //55.55
    System.out.println(a.var());              //1018.4166666666677
    System.out.println(var(b));               //1018.4166666666666
    System.out.println(varp(b));              //1008.2325
    System.out.println(a.stddev());           //31.912641173470234
    System.out.println(stddev(b));            //31.912641173470217
    System.out.println(stddevp(b));           //31.75267705249433
    System.out.println();
    
    a = new Accumulator();
    b = new double[3];
    for (int i = 1; i < b.length; i++) {
      double v = i+(double)i/10;
      a.addDataValue(v);
      b[i-1] = v;
    }
    System.out.println(a.mean());             //1.6500000000000001
    System.out.println(mean(b));              //1.1
    System.out.println(a.var());              //0.6050000000000001
    System.out.println(var(b));               //1.2100000000000002
    System.out.println(varp(b));              //0.8066666666666668
    System.out.println(a.stddev());           //0.7778174593052023
    System.out.println(stddev(b));            //1.1
    System.out.println(stddevp(b));           //0.8981462390204987
  }


}
