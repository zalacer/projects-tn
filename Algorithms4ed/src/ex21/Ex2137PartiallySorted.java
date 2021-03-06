package ex21;

import static v.ArrayUtils.*;
import static java.lang.Math.*;

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.Consumer;

import analysis.Timer;
import edu.princeton.cs.algs4.StdRandom;

/* p269
  2.1.37 Partially sorted. Write a client that generates partially sorted arrays, 
  including the following:
  * 95 percent sorted, last percent random values
  * All entries within 10 positions of their final place in the array
  * Sorted except for 5 percent of the entries randomly dispersed throughout the array
  Develop and test hypotheses about the effect of such input on the performance of the
  algorithms in this section.
  
  The third method appears to be the most effective for impeding sorting but the first
  method is almost as good and could be critically easier to implement, but is also 
  somewhat easier to detect for large arrays.
 */

public class Ex2137PartiallySorted {

  @FunctionalInterface
  public static interface TriConsumer {
    void accept(Double[] d, int i, int j);
  }

  @FunctionalInterface
  public static interface TriIntConsumer {
    void accept(int[] d, int i, int j);
  }

  public static void partiallySortedDoublingTest(
      String alg, int n, int trials, int partiallySortedType, double...params) {
    // do doubling test of sort for alg with arrays of partiallySortedType 
    // starting at length n
    if (params == null || params.length < 1)
      throw new IllegalArgumentException("partiallySortedDoublingTest: "
          + "params must be nonnull with length >= 1 ");

    double previous = 0; 
    previous = partiallySortedTimeTrial(alg, n/2, trials, partiallySortedType, params);

    String sig = null;
    switch (partiallySortedType) {
    case 1: sig = (100-params[0])+"/"+params[0];  break;
    case 2: sig = "distance "+ params[0];         break;
    case 3: sig =  (100-params[0])+"/"+params[0]; break;
    }
    
    double time; double ratio; 
    System.out.println("  Doubling test of "+alg+"sort with partiallySortedType"
        +partiallySortedType+" "+sig+" for "+trials+" trials");
    System.out.println("             N          time          ratio");
    for (int i = n; true; i = 2*i) {
      time = partiallySortedTimeTrial(alg, i, trials, partiallySortedType, params);
      ratio = time/previous;
      System.out.printf("  %12d       %7.0f       %8.3f\n", i, time, ratio);
      previous = time;
    }
  }

  public static void distVsPartiallySortedDoublingTest(
      String alg, int n, int trials, String dist, int partiallySortedType, double[]...params) {
    // do doubling tests of sort for alg with inital arrays of length n comparing
    // performance for dist vs. partiallySortedType data
    if (params == null || params.length < 2 || params[1].length < 1)
      throw new IllegalArgumentException("sortDistVsPartiallySortedDoublingTest: "
          + "params must be nonnull with length >= 2 and params[].length must be >= 1");

    double previous1 = 0; double previous2 = 0;
    double time1; double time2; double ratio1; double ratio2;

    previous1 = sortDistTimeTrial(alg, n/2, trials, dist, params[0]);
    previous2 = partiallySortedTimeTrial(alg, n/2, trials, partiallySortedType, params[1]);
    
    String sig = null;
    switch (partiallySortedType) {
    case 1: sig = (100-params[1][0])+"/"+params[1][0];  break;
    case 2: sig = "distance "+ params[1][0];            break;
    case 3: sig =  (100-params[1][0])+"/"+params[1][0]; break;
    }

    System.out.println("  Doubling test for "+alg+"sort comparing "+dist
        +" vs. partiallySortedType"+partiallySortedType+" "+sig+" with "+trials+" trials");
    System.out.println("             N       time1       time2        ratio1        ratio2");
    for (int i = n; true; i = 2*i) {
      time1 = sortDistTimeTrial(alg, i, trials, dist, params[0]);
      ratio1 = time1/previous1;
      time2 = partiallySortedTimeTrial(alg, i, trials, partiallySortedType, params[1]);
      ratio2 = time2/previous2;
      System.out.printf("  %12d     %7.0f     %7.0f     %8.3f      %8.3f\n", 
          i, time1, time2, ratio1, ratio2);
      previous1 = time1; previous2 = time2;
    }
  }

  public static double partiallySortedTimeTrial(
      String alg, int n, int trials, int partiallySortedType, double...params) {
    // return the average time for sorting a Double array of partiallySortedType 
    // with length n by the sort algorithm alg over trials.
    // the partiallySortedTypes are:
    // 1 - generated by getPartiallySortedArray1(int)
    // 2 - generated by getPartiallySortedArray2(int)
    // 3 - generated by getPartiallySortedArray2(int)
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : 
      throw new IllegalArgumentException("sortDistTimeTrial: alg not recognized");
    }
    if (partiallySortedType < 1 || partiallySortedType > 3)
      throw new IllegalArgumentException("partiallySortedTimeTrial: partiallySortedType type "
          +" must be 1, 2 or 3");
    if (params == null || params.length == 0)
      throw new IllegalArgumentException("partiallySortedTimeTrial: partiallySortedType type "
          +partiallySortedType +" specified but parameter not provided");
    long[] times = new long[trials];
    Double[] a = null;
    Timer t = new Timer();
    for (int i = 0; i < trials; i++) {
      StdRandom.setSeed(System.currentTimeMillis());
      switch (partiallySortedType) {
      case 1: a = getPartiallySortedArray1(n, params[0]);       break;
      case 2: a = getPartiallySortedArray2(n, (int) params[0]); break;
      case 3: a = getPartiallySortedArray3(n, params[0]);       break;
      }
      t.reset();
      con.accept(a);
      times[i] = t.num();
    }
    return mean(times);
  }

  public static Double[] getPartiallySortedArray1(int n, double p) {
    // return a double array 100-p percent sorted and the last p percent random values 
    if (n < 2) throw new IllegalArgumentException(
        "getPartiallySortedArray1: n must be > 0");
    if (p < 0 || p > 100) throw new IllegalArgumentException(
        "getPartiallySortedArray1: p must be > -1 and < 101");
    int pcnt = (int) round((p*n)/100);
    Double[] d = new Double[n - pcnt];
    StdRandom.setSeed(System.currentTimeMillis());
    for (int i = 0; i < d.length; i++) d[i] = StdRandom.uniform();
    Arrays.sort(d);
    Double[] e = new Double[pcnt];
    for (int i = 0; i < e.length; i++) e[i] = StdRandom.uniform();
    return append(d,e);
  }

  public static Double[] getPartiallySortedArray2(int n, int p) {
    // return a double array with all entries within p positions of their 
    // final place in the array
    if (n < 2) throw new IllegalArgumentException(
        "getPartiallySortedArray2: n must be > 0");
    if (p < 0 || p > n) throw new IllegalArgumentException(
        "getPartiallySortedArray2: p must be > -1 and < n+1");
    Double[] d = new Double[n];
    StdRandom.setSeed(System.currentTimeMillis());
    for (int i = 0; i < d.length; i++) d[i] = StdRandom.uniform();
    Arrays.sort(d);
    // now randomly swap elements within distance p of each other 
    TriConsumer swap = (Double[] a, int i, int j) -> {Double t = a[i]; a[i] = a[j]; a[j] = t;};
    BitSet b = new BitSet(); int[] q; int u;
    for (int i = 0; i < d.length; i++) {
      if (!b.get(i)) { // calculate actual range (exclusive upper bound)
        u = (d.length - 1 - i < p) ? d.length - i : i+p+1; 
        //        if (u == 1) break;
        q = range(i+1, u); StdRandom.shuffle(q);
        for (int j = 0; j < q.length; j++) {
          if (!b.get(q[j])) {
            swap.accept(d, i, q[j]);
            b.set(q[j]); b.clear(i, i+1);
            break;
          }
        }
      }
    }
    return d;
  }

  public static int[] getPartiallySortedIntArray2(int n, int p) {
    // return an int array with all entries within p positions of their 
    // final place in the array
    if (n < 2) throw new IllegalArgumentException(
        "getPartiallySortedArray2: n must be > 0");
    if (p < 0 || p > n) throw new IllegalArgumentException(
        "getPartiallySortedArray2: p must be > -1 and < n+1");
    int[] d = range(0,n); 
    // now swap elements within distance p of each other
    TriIntConsumer swap = (int[] a, int i, int j) -> {int t = a[i]; a[i] = a[j]; a[j] = t;};
    BitSet b = new BitSet(); int[] q; int u;
    for (int i = 0; i < d.length; i++) {
      if (!b.get(i)) {  // calculate actual range (exclusive upper bound)
        u = (d.length - 1 - i < p) ? d.length : i+p+1;
        // if (u - i == 1) break;
        q = range(i+1, u); StdRandom.shuffle(q);
        for (int j = 0; j < q.length; j++) {
          if (!b.get(q[j])) {
            swap.accept(d, i, q[j]);
            b.set(q[j]); b.clear(i, i+1);
            break;
          }
        }
      }
    }
    return d;
  }

  public static Double[] getPartiallySortedArray3(int n, double p) {
    // return a double array sorted except for p percent of the entries 
    // randomly dispersed throughout the array
    if (n < 2) throw new IllegalArgumentException(
        "getPartiallySortedArray3: n must be > 0");
    if (p < 0 || p > n) throw new IllegalArgumentException(
        "getPartiallySortedArray3: p must be > -1 and < 101");
    Double[] d = new Double[n];
    StdRandom.setSeed(System.currentTimeMillis());
    for (int i = 0; i < d.length; i++) d[i] = StdRandom.uniform();
    Arrays.sort(d);
    int pcnt = (int) round((p*n)/100);
    for (int i = 0; i < pcnt; i++) d[StdRandom.uniform(0, n)] = StdRandom.uniform();
    return d;
  }

  public static void sortNonuniformDoublingTest(
      String alg, int n, int trials, int nonUniformType, double...params) {
    // do doubling test of sort for alg with inital array of dist with length n
    double previous = 0; 
    if (params == null || params.length == 0) {
      previous = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType);
    } else {
      previous = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, params);
    }
    double time; double ratio; 
    System.out.println("  Doubling test of "+alg+"sort with nonuniform type "
        +nonUniformType+" for "+trials+" trials");
    System.out.println("             N          time          ratio");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time = sortNonuniformTimeTrial(alg, i, trials, nonUniformType);
      } else {
        time = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, params);
      }
      ratio = time/previous;
      System.out.printf("  %12d       %7.0f       %8.3f\n", i, time, ratio);
      previous = time;
    }
  }

  public static void sortDistVsNonuniformDoublingTest(
      String alg, int n, int trials, String dist, int nonUniformType,  double[]...params) {
    // do doubling tests of sort for alg with inital array length n comparing
    // performance for dist vs. nonUniformType data
    double previous1 = 0; double previous2 = 0;
    if (params == null || params.length == 0) {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist);
      previous2 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType);
    } else {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist, params[0]);
      previous2 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, params[1]);
    }
    double time1; double time2; double ratio1; double ratio2;
    System.out.println(
        "  Doubling test for "+alg+"sort comparing "+dist+" vs. nonuniform type "+nonUniformType+" with "+trials+" trials");
    System.out.println("             N       time1       time2        ratio1        ratio2");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time1 = sortDistTimeTrial(alg, i, trials, dist);
      } else {
        time1 = sortDistTimeTrial(alg, i, trials, dist, params[0]);
      }
      ratio1 = time1/previous1;
      if (params == null || params.length == 0) {
        time2 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType);
      } else {
        time2 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, params[1]);
      }
      ratio2 = time2/previous2;
      System.out.printf("  %12d     %7.0f     %7.0f     %8.3f      %8.3f\n", 
          i, time1, time2, ratio1, ratio2);
      previous1 = time1; previous2 = time2;
    }
  }

  public static double sortNonuniformTimeTrial(
      String alg, int n, int trials, int nonUniformType, double...params) {
    // return the average time for sorting a Double array of nonUniformType 
    // with length n by the sort algorithm alg over trials.
    // the nonUniformTypes are:
    // 1 - generated by getNonUniformArray1(int)
    // 2 - generated by getNonUniformArray2(int)
    // 3 - generated by getNonUniformArray3(int)
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : 
      throw new IllegalArgumentException("sortDistTimeTrial: alg not recognized");
    }
    long[] times = new long[trials];
    Double[] a = null;
    Timer t = new Timer();
    for (int i = 0; i < trials; i++) {
      StdRandom.setSeed(System.currentTimeMillis());
      switch (nonUniformType) {
      case 1: a = getNonUniformArray1(n); break;
      case 2: a = getNonUniformArray2(n); break;
      case 3: 
        if (params != null && params.length > 1) {
          a = getNonUniformArray3(n, (int) params[0], (int) params[1]); break;
        } else throw new IllegalArgumentException(
            "sortDistTimeTrial: nonuniform type 3 specified but parameters not provided");
      default: 
        throw new IllegalArgumentException("sortDistTimeTrial: nonUniformType not recognized");
      }
      t.reset();
      con.accept(a);
      times[i] = t.num();
    }
    return mean(times);
  }

  public static Double[] getNonUniformArray1(int n) {
    // return a double array with half 0's and half 1's randomized
    Double[] d = new Double[n];
    for (int i = 0; i < n/2; i++) d[i] = 0.;
    for (int i = n/2; i < n; i++) d[i] = 1.;
    return d;
  }

  public static Double[] getNonUniformArray2(int n) {
    // return a double array with half 0s, half the remainder 1s, 
    // half the remainder 2s, etc.
    Double[] d = new Double[n];
    int m = n; int sum = 0; double v = -0.;
    while(sum < n) {
      m = m % 2 == 0 ? m/2 : m/2+1;
      if (sum + m > n) m = n - sum;
      for (int i = sum; i < sum+m; i++) d[i] = v;
      sum += m; v++;
    }
    return d;
  }

  public static Double[] getNonUniformArray3(int n, int min, int max) {
    // return a double array with half the data is 0s, half random 
    // int values from min to max
    Double[] d = new Double[n];
    for (int i = 0; i < n/2; i++) d[i] = 0.;
    StdRandom.setSeed(System.currentTimeMillis());
    for (int i = n/2; i < n; i++) d[i] = 1.*StdRandom.uniform(min, max);
    return d;
  }

  public static void sortDistDoublingTest(
      String alg, int n, int trials, String dist, double...params) {
    // do doubling test of sort for alg with inital array of dist with length n
    double previous = 0; 
    if (params == null || params.length == 0) {
      previous = sortDistTimeTrial(alg, n/2, trials, dist);
    } else {
      previous = sortDistTimeTrial(alg, n/2, trials, dist, params);
    }
    double time; double ratio; 
    System.out.println(
        "  Doubling test of "+alg+"sort with "+dist+" for "+trials+" trials");
    System.out.println("             N          time          ratio");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time = sortDistTimeTrial(alg, i, trials, dist);
      } else {
        time = sortDistTimeTrial(alg, i, trials, dist, params);
      }
      ratio = time/previous;
      System.out.printf("  %12d       %7.0f       %8.3f\n", i, time, ratio);
      previous = time;
    }
  }

  public static void sortDistsDoublingTest(
      String alg, int n, int trials, String dist1, String dist2, double[]...params) {
    // do doubling tests of sort for alg with inital array length n comparing
    // dist1 and dist2
    double previous1 = 0; double previous2 = 0;
    if (params == null || params.length == 0) {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist1);
      previous2 = sortDistTimeTrial(alg, n/2, trials, dist2);
    } else {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist1, params[0]);
      previous2 = sortDistTimeTrial(alg, n/2, trials, dist2, params[1]);
    }
    double time1; double time2; double ratio1; double ratio2;
    System.out.println(
        "  Doubling test for "+alg+"sort comparing "+dist1+" vs. "+dist2+" with "+trials+" trials");
    System.out.println("             N       time1       time2        ratio1       ratio2");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time1 = sortDistTimeTrial(alg, i, trials, dist1);
      } else {
        time1 = sortDistTimeTrial(alg, i, trials, dist1, params[0]);
      }
      ratio1 = time1/previous1;
      if (params == null || params.length == 0) {
        time2 = sortDistTimeTrial(alg, i, trials, dist2);
      } else {
        time2 = sortDistTimeTrial(alg, i, trials, dist2, params[1]);
      }
      ratio2 = time2/previous2;
      System.out.printf("  %12d     %7.0f     %7.0f     %8.3f      %8.3f\n", 
          i, time1, time2, ratio1, ratio2);
      previous1 = time1; previous2 = time2;
    }
  }

  public static double sortDistTimeTrial(
      String alg, int n, int trials, String distribution, double...params) {
    // return the average time for sorting a Double array of distribution 
    // with length n by the sort algorithm alg over trials. 
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "default"  : throw new IllegalArgumentException("sort: alg not recognized");
    }
    long[] times = new long[trials];
    Double[] a = null;
    Timer t = new Timer();
    for (int i = 0; i < trials; i++) {
      StdRandom.setSeed(System.currentTimeMillis());
      a = generateDataArray(distribution, n, params);
      t.reset();
      con.accept(a);
      times[i] = t.num();
    }
    return mean(times);
  }

  public static Double[] generateDataArray(String distribution, int n, double...params) {
    Double[] z = null;
    switch (distribution) {
    case "uniform"   : 
      z = getUniformDoubleArray(n);
      break;
    case "gaussian"  : 
      if (params != null && params.length >= 2) {
        z = getGaussianDoubleArray(n, params[0], params[1]);
      } else {
        z = getGaussianDoubleArray(n);
      } 
      break;
    case "poisson"   : 
      if (params != null && params.length >= 1) {
        return getPoissonDoubleArray(n, params[0]);
      } else throw new IllegalArgumentException(
          "generateDataArray: poisson specified but parameter not provided");
    case "geometric" : 
      if (params != null && params.length >= 1) {
        z = getGeometricDoubleArray(n, params[0]);
      } else throw new IllegalArgumentException(
          "generateDataArray: geometric specified but parameter not provided");
      break;
    case "discrete"  : 
      if (params != null && params.length >= 1) {
        z = getDiscreteDoubleArray(n, params);
      } else throw new IllegalArgumentException(
          "generateDataArray: discrete specified but parameter not provided");
      break;
    case "default"   : 
      throw new IllegalArgumentException("generateDataArray: distribution not recognized");
    }
    return z;
  }

  public static Double[] getUniformDoubleArray(int n) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = StdRandom.uniform();
    return d;
  }

  public static Double[] getGaussianDoubleArray(int n) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = StdRandom.gaussian();
    return d;
  }

  public static Double[] getGaussianDoubleArray(int n, double mu, double sigma) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = StdRandom.gaussian(mu, sigma);
    return d;
  }

  public static Double[] getPoissonDoubleArray(int n, double mean) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = 1.*StdRandom.poisson(mean);
    return d;
  }

  public static Double[] getGeometricDoubleArray(int n, double success) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = 1.*StdRandom.geometric(success);
    return d;
  }

  public static Double[] getDiscreteDoubleArray(int n, double[] probabilities) {
    Double[] d = new Double[n];
    for (int i = 0; i < n; i++) d[i] = 1.*StdRandom.discrete(probabilities);
    return d;
  }

  public static void main(String[] args) {
    
    double[][] params = new double[2][];
    params[0] = new double[0]; params[1] = new double[]{5};
//    distVsPartiallySortedDoublingTest("shell", 1024, 3, "uniform", 1, params);
/*  Doubling test for shellsort comparing uniform vs. partiallySortedType1 95.0/5.0 with 3 trials
             N       time1       time2        ratio1        ratio2
        1024           1           0        1.000         1.000
        2048           1           1        1.000         2.000
        4096           1           1        1.333         1.500
        8192           3           2        2.500         2.000
       16384           8           5        2.400         2.500
       32768          16          13        2.000         2.533
       65536          32          21        2.000         1.684
      131072          87          63        2.729         2.953
      262144         261         192        2.992         3.048
      524288         649         397        2.482         2.066
     1048576        1605        1161        2.474         2.927
     2097152        3933        2857        2.450         2.461 */

    params[1] = new double[]{10};
//    distVsPartiallySortedDoublingTest("shell", 1024, 3, "uniform", 2, params);
/*  Doubling test for shellsort comparing uniform vs. partiallySortedType2 distance 10.0 with 3 trials
             N       time1       time2        ratio1        ratio2
          1024           1           0        0.750         1.000
          2048           0           1        0.333         2.000
          4096           2           1        6.000         1.500
          8192           3           1        1.500         0.667
         16384           8           4        2.556         5.500
         32768          15           5        1.957         1.455
         65536          32          10        2.156         1.938
        131072         109          27        3.361         2.613
        262144         260          70        2.393         2.593
        524288         650         208        2.501         2.976
       1048576        1653         455        2.541         2.186
       2097152        4261         806        2.578         1.769 */      
    
    params[1] = new double[]{5};
//    distVsPartiallySortedDoublingTest("shell", 1024, 3, "uniform", 3, params);
/*  Doubling test for shellsort comparing uniform vs. partiallySortedType3 95.0/5.0 with 3 trials
             N       time1       time2        ratio1        ratio2
          1024           1           0        1.000         0.500
          2048           1           1        1.000         2.000
          4096           1           1        1.333         1.500
          8192           2           3        1.750         2.667
         16384           7           5        2.857         1.750
         32768          15          15        2.200         3.214
         65536          31          23        2.136         1.556
        131072          89          74        2.851         3.157
        262144         252         197        2.821         2.674
        524288         630         507        2.500         2.575
       1048576        1682        1245        2.670         2.453
       2097152        3943        2905        2.344         2.334 */   

  }

}
