package ex21;

import java.awt.Font;

import static v.ArrayUtils.*;

import java.util.Random;
import java.util.function.Consumer;

import analysis.Timer;
import edu.princeton.cs.algs4.StdDraw;

/* p268
  2.1.32 Plot running times. Write a client that uses  StdDraw to plot the average running
  times of the algorithm for random inputs and various values of the array size. You may
  add one or two more command-line arguments. Strive to design a useful tool.
 */

public class Ex2132PlotRunningTimes {

  public static void plotAvgRunningTimes(String alg, int[] lengths, int trials) {
    // generate and plot the average running times for using alg to sort Double random
    // arrays with lengths specified in the lengths array, where for each length the mean 
    // of sorting trials number of arrays of that length is plotted and the points are 
    // plotted in the same order as in the lengths array.
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }

    double[] times = new double[lengths.length];

    for (int k = 0; k < lengths.length; k++) {
      long[] tmptimes = new long[trials];
      Random r = null;
      Double[] a = null;
      Timer t = new Timer();
      for (int i = 0; i < trials; i++) {
        r = new Random(System.currentTimeMillis());
        a = new Double[lengths[k]];
        for (int j = 0; j < a.length; j++) a[j] = r.nextDouble();
        t.reset();
        con.accept(a);
        tmptimes[i] = t.num();
      }
      times[k] = mean(tmptimes);
    }
    StdDraw.setCanvasSize(700, 700);
    double minY = min(times);
    double maxY = max(times);
    double maxYscaled = 1.05*maxY;
    double minYscaled = .95*minY;
    int maxX = times.length;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled+1);
    StdDraw.setYscale(minYscaled-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 18);
    StdDraw.setFont(font);
    StdDraw.text(maxXscaled/2, .975*maxYscaled, "Mean runtimes of "+alg
        +" sort for specified array lengths");
    StdDraw.setPenColor(StdDraw.BOOK_RED);
    for (int i = 0; i < times.length; i++) StdDraw.point(i, times[i]);
  }

  public static void plotAvgRunningTimes(String alg, int start, int factor, int m, int trials) {
    // generate and plot the average running times for using alg to sort Double arrays with 
    // lengths starting at start and increased by multipying by factor from 1 up to m times
    // (excluding m), where each array is sorted trials times and the average runtime over 
    // over seach set of trials is plotted. For example, if start == 1024, factor == 2 and 
    // times == 5 then the lengths of the arrays sorted are {1024, 1024*2, 1024*2**2, 
    // 1024*2**3, 1024*2**4}. 
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }

    int[] lengths = new int[m];
    lengths[0] = start;
    int tmpstart = start;
    for (int i = 1; i < lengths.length; i++) lengths[i] = tmpstart *= factor;
    int end = lengths[lengths.length-1];

    double[] times = new double[lengths.length];

    for (int k = 0; k < lengths.length; k++) {
      long[] tmptimes = new long[trials];
      Random r = null;
      Double[] a = null;
      Timer t = new Timer();
      for (int i = 0; i < trials; i++) {
        r = new Random(System.currentTimeMillis());
        a = new Double[lengths[k]];
        for (int j = 0; j < a.length; j++) a[j] = r.nextDouble();
        t.reset();
        con.accept(a);
        tmptimes[i] = t.num();
      }
      times[k] = mean(tmptimes);
    }
    StdDraw.setCanvasSize(700, 700);
    double minY = min(times);
    double maxY = max(times);
    double maxYscaled = 1.05*maxY;
    double minYscaled = .95*minY;
    int maxX = times.length;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled+1);
    StdDraw.setYscale(minYscaled-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 18);
    StdDraw.setFont(font);
    StdDraw.text(maxXscaled/2, .975*maxYscaled, "Mean runtimes of "+alg
        +" sort for array lengths "+start+" to "+end+" by factor "+factor);
    StdDraw.setPenColor(StdDraw.BOOK_RED);
    for (int i = 0; i < times.length; i++) StdDraw.point(i, times[i]);
  }

  public static void plotRunningTimes(String alg, int N, int trials) {
    // generate and plot the running times for sorting a Double array 
    // of length N by the sort algorithm alg trials times.
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }
    long[] times = new long[trials];
    Random r = null;
    Double[] a = null;
    Timer t = new Timer();
    for (int i = 0; i < trials; i++) {
      r = new Random(System.currentTimeMillis());
      a = new Double[N];
      for (int j = 0; j < N; j++) a[j] = r.nextDouble();
      t.reset();
      con.accept(a);
      times[i] = t.num();
    }
    StdDraw.setCanvasSize(700, 700);
    long minY = min(times);
    long maxY = max(times);
    double maxYscaled = 1.05*maxY;
    double minYscaled = .95*minY;
    int maxX = times.length;
    double maxXscaled = 1.05*maxX;
    StdDraw.setXscale(-1, maxXscaled+1);
    StdDraw.setYscale(minYscaled-(2.*maxYscaled/89.25), maxYscaled);
    StdDraw.setPenRadius(.01);
    StdDraw.setPenColor(StdDraw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    StdDraw.setFont(font);
    StdDraw.text(maxXscaled/2, .975*maxYscaled, "Runtimes of "+alg+" sort for array length "+N);
    StdDraw.setPenColor(StdDraw.BOOK_RED);
    for (int i = 0; i < times.length; i++) StdDraw.point(i, times[i]);
  }

  public static void pause(long sec) {
    try {
      Thread.sleep(sec*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    plotAvgRunningTimes("selection", new int[]{256,512,1024,2048,4096,8192,16384}, 3);
    // Ex2132MeanRuntimesOfSelectionSortForSpecifiedArrayLengths.jpg

    pause(5);

    plotAvgRunningTimes("shell", 2, 2, 21, 3);
    // Ex2132MeanRuntimesOfShellSortArrayLengths2to2097152ByFactor2.jpg

    pause(5);

    plotRunningTimes("insertion", 10000, 100); 
    //  Ex2132RuntimesOfInsertionSortArrayLength10000.jpg

  }
}
