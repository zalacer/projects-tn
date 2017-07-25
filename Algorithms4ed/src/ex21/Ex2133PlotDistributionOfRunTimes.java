package ex21;

import static v.ArrayUtils.max;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.min;
import static v.ArrayUtils.unbox;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import analysis.Timer;
import edu.princeton.cs.algs4.StdDraw;

/* p267
  2.1.33 Distribution. Write a client that enters into an infinite loop running  sort() on
  arrays of the size given as the third command-line argument, measures the time taken
  for each run, and uses StdDraw to plot the average running times. A picture of the 
  distribution of the running times should emerge.
 */

public class Ex2133PlotDistributionOfRunTimes {
  
  public static void plotUniverseOfMeanRuntimesForFixedN(String alg, int trials, int N) {
    // in an infinite loop plot the average run time of sorting trials number of 
    // random double arrays of length N by alg.
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }

    List<Double> list = new ArrayList<>();
    double[] times = null;
    long[] tmptimes = null;
    
    StdDraw.setCanvasSize(700, 700);
    StdDraw.setPenRadius(.01);
    Font font = new Font("Arial", Font.BOLD, 18);
    StdDraw.setFont(font);
    
    while (true) {
      tmptimes = new long[trials];
      Random r = null;
      Double[] a = null;
      Timer t = new Timer();
      for (int i = 0; i < trials; i++) {
        r = new Random(System.currentTimeMillis());
        a = new Double[N];
        for (int j = 0; j < a.length; j++) a[j] = r.nextDouble();
        t.reset();
        con.accept(a);
        tmptimes[i] = t.num();
      }
      list.add(mean(tmptimes));
      times = (double[])unbox(list.toArray(new Double[0]));
      
      double maxY = max(times);
      double minY = min(times);
      double maxYscaled = 1.05*maxY;
      double minYscaled = .95*minY;
      int maxX = times.length;
      double maxXscaled = 1.05*maxX;
      StdDraw.setXscale(-1, maxXscaled+1);
      StdDraw.setYscale(minYscaled-(2.*maxYscaled/89.25), maxYscaled);
      StdDraw.clear();
      StdDraw.setPenColor(StdDraw.BLACK);
      StdDraw.text(maxXscaled/2, .975*maxYscaled, "Universe of Mean runtimes of "+alg
          +" sort for array length "+N);
      StdDraw.setPenColor(StdDraw.BOOK_RED);
      for (int i = 0; i < times.length; i++) StdDraw.point(i, times[i]);
      StdDraw.show();
      StdDraw.show(20);
    }
  }

  public static void main(String[] args) {
    
    plotUniverseOfMeanRuntimesForFixedN("shell", 3, 16384);
    // Ex2133UniverseOfMeanRuntimesOfShellSortForArrayLength16384.jpg

  }
}
