package ex11;

import java.awt.Font;
import java.util.function.Supplier;
import java.util.stream.Stream;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

//  1.1.32 Histogram. Suppose that the standard input stream is a sequence of  double
//  values. Write a program that takes an integer N and two  double values l and r from the
//  command line and uses  StdDraw to plot a histogram of the count of the numbers in the
//  standard input stream that fall in each of the N intervals defined by dividing (l , r)
//  into N equal-sized intervals.

// I did this by using StdRandom.pareto(alpha) to generated a stream of doubles
// and packaged everything in a static method that takes a boolean minMax argument
// which if true replaces the command line l and r with the min and max of the 
// generated data.

public class Ex1132HistogramFromStream {
  
  private static double alphanow = 0.; // for plot annotation
  private static String supplier = ""; // for plot annotation

  public static Supplier<Double> paretoSup(double alpha) {
    alphanow = alpha;
    supplier = "pareto";
    return () -> StdRandom.pareto(alpha);
  }

  public static void plotSamplingHistogram(Supplier<Double> sup, long limit,
      int N,  double l, double r, boolean minMax) {
    // Stream limit number of data points data from StdRandom.pareto(alpha)
    // and collect in a double[]. For each data point, bin it into an int[N] 
    // for intervals defined by (r-l)/N and plot the results as a histogram.
    // sup: the data supplier
    // limit: total number of pareto data points to collect
    // N: number of bins in which to accumulate data points
    // l: left bound of the range of data points to bin
    //    if l is negative it is assigned the min of the generated data points
    // r: right bound of the range of data points to bin
    //    if r is negative it is assigned the max of the generated data points

    assert limit>0; assert N>0; 
    if (!minMax) assert r>l;

    double[] a = Stream.generate(sup).limit(limit)
        .mapToDouble(Double::doubleValue).toArray();

    if (minMax) {
      double amax = 0.; double amin = 0.;
      for (double d : a) {
        if (d > amax) amax = d;
        if (d < amin) amin = d;
      }
      if (amin == amax) {
        System.out.println("min and max of generated data are equal");
        return;
      }
      l = amin;
      r = amax;
    }

    double interval = (r - l)/N;

    int[] b = new int[N];
    for (double d : a) {
      if (d >= l && d < l+interval) b[0]++;
      for (int i = 1; i < N-1; i++)
        if (d >= l+interval*i && d < l+interval*(i+1)) b[i]++;
      if (d >= l+interval*(N-1) && d <= l+interval*(N)) b[N-1]++;  
    }

    int bmax = 0;
    for (int i = 1; i < N-1; i++)
      if (b[i] > bmax) bmax = b[i];

    StdDraw.setCanvasSize(800,800);
    StdDraw.setXscale(-.1, 1.);
    StdDraw.setYscale(-1900., bmax*2);
    StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
    StdDraw.text(0.5, bmax*1.9, supplier+"("+alphanow+") sampling");
    StdDraw.setFont();
    for (int i = 0; i < N; i++) {
      double hw = 0.3/N;  // half width, width = 0.6N
      double hh = b[i]/2; // half height, height = b[i]
      double x = 1.0*i/N; // x,y is center
      double y = hh;  // place center at half height
      StdDraw.filledRectangle(x, y, hw, hh);
      // annotate drawing with text
      if (i % 5 == 0) StdDraw.text(x, -900., ""+i+" ");
      if (i == 0) StdDraw.text(0.085, bmax*1.5, "max="+(int)y*2);
      if (i > 0 && i < 4) StdDraw.text(-.05, y*2, ""+(int)y*2);
    }
  }

  public static void main(String[] args) {
    
    // this plots best for alpha from 2.5-4. and sometimes needs to be
    // run several times before it gives the best plot, at least with 
    // Eclipse Mars1 and Java 8.
    plotSamplingHistogram(paretoSup(3),1000000,50,0.,0.,true);

  }

}
