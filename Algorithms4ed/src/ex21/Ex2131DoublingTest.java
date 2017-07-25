package ex21;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.util.Random;
import java.util.function.Consumer;

import analysis.Timer;

/* p268
  2.1.31 Doubling test. Write a client that performs a doubling test for sort algorithms.
  Start at  N equal to 1000, and print N, the predicted number of seconds, the actual 
  number of seconds, and the ratio as N doubles. Use your program to validate that 
  insertion sort and selection sort are quadratic for random inputs, and formulate and 
  test a hypothesis for shellsort.

  My tests show that selection and insertion sort could be quadratic but the doubling tests
  don't really show convergence and I don't have time for or interest in more testing. Based 
  on a doubling test for shell sort a reasonable hypothesis is that it has subquadratic 
  running time and the test substantiates that. 

 */

public class Ex2131DoublingTest {

  public static double sortTimeTrial(String alg, int N, int trials) {
    // return the average time for sorting a Double array of length N
    // by the sort algorithm alg over trials. 
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
    return mean(times);
  }

  public static void sortDoublingTest(String alg, int N, int trials) {
    // do doubling tests of sort for alg with inital array length N
    // times are predicted as a linear extrapolation of the previous two times
    double[] previous = {0, sortTimeTrial(alg, N/2, trials)};
    double time; int c = 0; double predicted; double ratio;
    System.out.println("     Doubling test for "+alg+" sort");
    System.out.println("          N        predicted    time     ratio");
    for (int i = N; true; i = 2*i) {
      time = sortTimeTrial(alg, i, trials);
      ratio = time/previous[1];
      if (c > 1) { // calculate linear extrapolation of previous two times
        predicted = (previous[1]/previous[0])*previous[1];
      } else predicted = -1;
      if (predicted == -1) {
        System.out.printf("  %12d   %7.0f     %7s     %5.3f\n", i, time, "NA", ratio);
      } else System.out.printf("  %12d   %7.0f     %7.0f     %5.3f\n", i, predicted, time, ratio);
      previous[0] = previous[1]; previous[1] = time;
      //      pa(previous);
      c++;
    }
  }

  public static void sortDoublingTestPowerHypothesis(String alg, int N, int trials) {
    // do doubling tests of sort for alg with inital array length N
    // times are predicted as an extrapolaton of time ~ kN**b based on the previous two times
    double[] previous = {0, sortTimeTrial(alg, N/2, trials)};
    double time; int c = 0; double predicted; double ratio; double k = 0; double b = 0;
    System.out.println("     Doubling test power hypothesis for "+alg+" sort");
    System.out.println("          N        predicted    time     ratio       k          b");
    for (int i = N; true; i = 2*i) {
      time = sortTimeTrial(alg, i, trials);
      ratio = time/previous[1];
      if (c > 1) { // calculate exponential extrapolation of previous two times
        b = (log(previous[1]/previous[0]))/log(i/4);
        k = previous[1]/pow(i/2, b);
        predicted = k * pow(i, b);
      } else predicted = -1;
      if (predicted == -1) {
        System.out.printf("  %12d   %7.0f     %7s     %5.3f     %7.3f      %5.3f\n",
            i, time, "NA", ratio, k, b);
      } else System.out.printf("  %12d   %7.0f     %7.0f     %5.3f     %7.3f      %5.3f\n",
          i, predicted, time, ratio, k, b);
      previous[0] = previous[1]; previous[1] = time;
      //      pa(previous);
      c++;
    }

  }


  public static void main(String[] args) {

    //    sortDoublingTest("selection", 1000, 3);

    //    Doubling test for selection sort
    //    N        predicted    time     ratio
    //    1000        19          NA     0.606
    //    2000         6          NA     0.316
    //    4000         2          22     3.722
    //    8000        83          88     3.925
    //   16000       344         395     4.502
    //   32000      1777        1549     3.926
    //   64000      6082        6352     4.100
    //  128000     26039       41918     6.600
    //  256000    276639      260324     6.210
    //  512000   1616698     1198830     4.605

    //    Doubling test for insertion sort
    //    N        predicted    time     ratio
    //    1000         5          NA     0.571
    //    2000         9          NA     1.625
    //    4000        14          34     3.885
    //    8000       131         131     3.901
    //   16000       512         537     4.089
    //   32000      2196        2356     4.387
    //   64000     10337        9697     4.116
    //  128000     39914       53489     5.516   

    //    Doubling test for shell sort
    //    N        predicted    time     ratio
    //    1000         2          NA     2.000
    //    2000         1          NA     0.667
    //    4000         1           3     2.000
    //    8000         5           3     1.000
    //   16000         3           5     1.875
    //   32000         9          13     2.600
    //   64000        34          29     2.256
    //  128000        66          90     3.068
    //  256000       276         259     2.881
    //  512000       747         639     2.464
    // 1024000      1575        1615     2.527
    // 2048000      4082        3823     2.367
    // 4096000      9051        9981     2.610
    // 8192000     26054       24911     2.496
    //16384000     62174       65718     2.638
    //32768000    173376      188749     2.872

  }
}
