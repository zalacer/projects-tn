package ex23;

import static v.ArrayUtils.*;

import java.util.function.Consumer;

import analysis.Timer;
import edu.princeton.cs.algs4.StdRandom;


public class Ex2330QuickSortWithAndWithoutShuffling {

  /* p307  
  2.3.30 Corner cases. Test quicksort on large nonrandom arrays of the kind
  described in Exercises 2.1.35 and 2.1.36 both with and without the initial
  random shuffle. How does shuffling affect its performance for these arrays?
  
  For arrays of lengths 10^6 and over shuffling reduces performance sometimes
  by nearly or more than a factor of two with poisson, geometric and discrete
  distributions but still by a significant amount for uniform and gaussian
  distributions and it reduces performance for the three types of nonuniform 
  data especially those with half 1s/half 0s and half 0s/half the remainder 
  1s/half the remainder 2s, etc.
  
  */
  
  public static void sortDistsDoublingTest(
      String alg, int n, int trials, String dist1, String dist2, double[]...params) {
    // do doubling tests of sort for alg with inital array length n comparing
    // dist1 and dist2
    double previous1 = 0; double previous2 = 0;
    if (params == null || params.length == 0) {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist1, false);
      previous2 = sortDistTimeTrial(alg, n/2, trials, dist2, true);
    } else {
      previous1 = sortDistTimeTrial(alg, n/2, trials, dist1, false, params[0]);
      previous2 = sortDistTimeTrial(alg, n/2, trials, dist2, true, params[1]);
    }
    double time1; double time2; double ratio1; double ratio2;
    System.out.println(
        "  Doubling test for "+alg+"sort comparing "+dist1+" vs. shuffled "+dist2+" with "+trials+" trials");
    System.out.println("             N       time1       time2        ratio1       ratio2");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time1 = sortDistTimeTrial(alg, i, trials, dist1, false);
      } else {
        time1 = sortDistTimeTrial(alg, i, trials, dist1, false, params[0]);
      }
      ratio1 = time1/previous1;
      if (params == null || params.length == 0) {
        time2 = sortDistTimeTrial(alg, i, trials, dist2, true);
      } else {
        time2 = sortDistTimeTrial(alg, i, trials, dist2, true, params[1]);
      }
      ratio2 = time2/previous2;
      System.out.printf("  %12d     %7.0f     %7.0f     %8.3f      %8.3f\n", 
          i, time1, time2, ratio1, ratio2);
      previous1 = time1; previous2 = time2;
    }
  }

  public static double sortDistTimeTrial(
      String alg, int n, int trials, String distribution, boolean shuffle, double...params) {
    // return the average time for sorting a Double array of distribution 
    // with length n by the sort algorithm alg over trials. 
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "quick"    : con = (Double[] g) -> sort.Quicks.quickVCoM3T9F3v2(g,19,false); break;
    case "default"  : throw new IllegalArgumentException("sortDistTimeTrial: alg not recognized");
    }
    long[] times = new long[trials];
    Double[] a = null;
    Timer t = new Timer();
    for (int i = 0; i < trials; i++) {
      StdRandom.setSeed(System.currentTimeMillis());
      a = generateDataArray(distribution, n, params);
      if (shuffle) StdRandom.shuffle(a);
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
  
  public static void sortNonuniformVsShuffledDoublingTest(
      String alg, int n, int trials, int nonUniformType,  double[]...params) {
    // do doubling tests of sort for alg with inital array length n comparing
    // performance for dist vs. nonUniformType data
    double previous1 = 0; double previous2 = 0;
    if (params == null || params.length == 0) {
      previous1 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, false);
      previous2 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, true);
    } else {
      previous1 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, false, params[0]);
      previous2 = sortNonuniformTimeTrial(alg, n/2, trials, nonUniformType, true, params[0]);
    }
    double time1; double time2; double ratio1; double ratio2;
    System.out.println(
        "  Doubling test for "+alg+"sort comparing nonuniform type "+nonUniformType+" unshuffled vs. shuffled "+trials+" trials");
    System.out.println("             N       time1       time2        ratio1        ratio2");
    for (int i = n; true; i = 2*i) {
      if (params == null || params.length == 0) {
        time1 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, false);
      } else {
        time1 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, false, params[0]);
      }
      ratio1 = time1/previous1;
      if (params == null || params.length == 0) {
        time2 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, true);
      } else {
        time2 = sortNonuniformTimeTrial(alg, i, trials, nonUniformType, true, params[0]);
      }
      ratio2 = time2/previous2;
      System.out.printf("  %12d     %7.0f     %7.0f     %8.3f      %8.3f\n", 
          i, time1, time2, ratio1, ratio2);
      previous1 = time1; previous2 = time2;
    }
  }
  
  public static double sortNonuniformTimeTrial(
      String alg, int n, int trials, int nonUniformType, boolean shuffled, double...params) {
    // return the average time for sorting a Double array of nonUniformType 
    // with length n by the sort algorithm alg averaged over trials.
    // the nonUniformTypes are:
    // 1 - generated by getNonUniformArray1(int)
    // 2 - generated by getNonUniformArray2(int)
    // 3 - generated by getNonUniformArray3(int)
    Consumer<Double[]> con = null;
    switch (alg) {
    case "insertion": con = (Double[] g) -> sort.Insertion.sort(g); break;
    case "selection": con = (Double[] g) -> sort.Selection.sort(g); break;
    case "shell"    : con = (Double[] g) -> sort.Shell.sort(g)    ; break;
    case "quick"    : con = (Double[] g) -> sort.Quicks.quickVCoM3T9F3v2(g,19,false); break;
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
      if (shuffled) StdRandom.shuffle(a);
      t.reset();
      con.accept(a);
      times[i] = t.num();
    }
    return mean(times);
  }
  
  public static Double[] getNonUniformArray1(int n) {
    // return a double array with half 0s and half 1s
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
    // integer values from min to max all randomized
    Double[] d = new Double[n];
    for (int i = 0; i < n/2; i++) d[i] = 0.;
    StdRandom.setSeed(System.currentTimeMillis());
    for (int i = n/2; i < n; i++) d[i] = 1.*StdRandom.uniform(min, max);
    return d;
  }
 
  public static void main(String[] args) {
    
    // In the tests below algorithm "quick" is sort.Quicks.quickVCoM3T9F3v2 with
    // cutoff 19 and inner shuffling disabled. time1 and ratio1 are without
    // external shuffling; time2 and ratio2 are with external shuffling. External
    // shuffling is not included in time2 values. All arrays used have Double keys.
   
    sortDistsDoublingTest("quick", 1024, 50, "uniform", "uniform");
/*
    Doubling test for quicksort comparing uniform vs. shuffled uniform with 50 trials
               N       time1       time2        ratio1       ratio2
            1024           0           0        0.455         1.750
            2048           3           4        8.533        12.643
            4096           7           6        2.742         1.638
            8192           2           1        0.222         0.197
           16384           2           2        1.538         2.140
           32768           5           5        2.208         2.246
           65536          11          12        2.155         2.179
          131072          26          29        2.257         2.430
          262144          60          73        2.314         2.504
          524288         137         172        2.293         2.367
         1048576         315         402        2.303         2.339
         2097152         727         901        2.307         2.241

*/
    
    sortDistsDoublingTest("quick", 1024, 50, "gaussian", "gaussian");
/*
    Doubling test for quicksort comparing gaussian vs. shuffled gaussian with 50 trials
               N       time1       time2        ratio1       ratio2
            1024           0           0        0.643         1.417
            2048           1           2        1.833         5.882
            4096           6           6        9.303         2.940
            8192           2           1        0.365         0.187
           16384           2           3        1.062         2.309
           32768           5           6        2.185         2.173
           65536          11          12        2.188         2.174
          131072          26          29        2.267         2.422
          262144          60          73        2.317         2.506
          524288         137         171        2.288         2.349
         1048576         311         391        2.271         2.288
         2097152         702         893        2.261         2.281
*/
    
    double[][] params = new double[2][]; 
//    params[0] = new double[]{3.}; params[1] = new double[]{3.};
    sortDistsDoublingTest("quick", 1024, 50, "poisson", "poisson", params);
/*    
    Doubling test for quicksort comparing poisson vs. shuffled poisson with 50 trials
               N       time1       time2        ratio1       ratio2
            1024           0           0        0.273         2.000
            2048           0           0        2.500         1.750
            4096           1           1        1.800         1.786
            8192           2           4        2.778         7.000
           16384           7           6        4.507         1.697
           32768           5           4        0.710         0.697
           65536           3           4        0.671         0.874
          131072           8          13        2.354         3.453
          262144          19          31        2.554         2.475
          524288          43          71        2.220         2.301
         1048576          90         157        2.104         2.202
         2097152         189         325        2.086         2.073
         4194304         381         714        2.022         2.196
    
*/    
    
//    params[0] = new double[]{.5}; params[1] = new double[]{.5};
    sortDistsDoublingTest("quick", 1024, 50, "geometric", "geometric", params);
/*    
  Doubling test for quicksort comparing geometric vs. shuffled geometric with 50 trials
             N       time1       time2        ratio1       ratio2
          1024           0           0        0.167         1.667
          2048           0           0        2.750         3.200
          4096           0           1        1.727         1.563
          8192           1           3        1.895         5.400
         16384           6           7        8.444         2.430
         32768          12           5        1.964         0.817
         65536           6           3        0.516         0.578
        131072           6          10        0.964         3.077
        262144          14          28        2.380         2.931
        524288          32          63        2.229         2.264
       1048576          67         137        2.136         2.160
       2097152         136         298        2.025         2.182
    
*/    
    
    params[0] = new double[]{.12,.22,.32,.22,.12}; 
    params[1] = new double[]{.12,.22,.32,.22,.12};
    sortDistsDoublingTest("quick", 1024, 3, "discrete", "discrete", params);
/*
    Doubling test for quicksort comparing discrete vs. shuffled discrete with 3 trials
               N       time1       time2        ratio1       ratio2
            1024           1           1        0.105      Infinity
            2048           1           0        1.000         0.000
            4096           0           0        0.000      Infinity
            8192           1           1     Infinity         3.000
           16384           2           3        2.000         3.333
           32768           4           8        1.833         2.500
           65536           8           9        2.273         1.040
          131072          31          74        3.680         8.538
          262144         118         106        3.837         1.432
          524288         135          71        1.144         0.673
         1048576          75         158        0.554         2.220
         2097152         149         287        2.000         1.815
         4194304         302         638        2.022         2.220
         8388608         615        1359        2.035         2.131
        16777216        1303        3284        2.120         2.416
    
*/    
    // this tests sorting double arrays with half 0s and half 1s
    sortNonuniformVsShuffledDoublingTest("quick", 1024, 50, 1);
/*
    Doubling test for quicksort comparing nonuniform type 1 unshuffled vs. shuffled 50 trials
               N       time1       time2        ratio1        ratio2
            1024           0           0        0.095         2.250
            2048           0           0        5.000         1.333
            4096           0           1        1.600         2.083
            8192           1           1        1.875         1.240
           16384           1           1        1.533         2.161
           32768           2          10        2.087         7.821
           65536           9          17        4.875         1.601
          131072           3           7        0.321         0.411
          262144           6          20        1.987         2.945
          524288          12          49        2.020         2.424
         1048576          24         109        1.962         2.215
         2097152          48         229        2.037         2.097
         4194304          99         495        2.058         2.162
         8388608         192        1076        1.941         2.175

*/
  
    // this tests sorting double arrays with half 0s, half the remainder 1s, 
    // half the remainder 2s, etc.
    sortNonuniformVsShuffledDoublingTest("quick", 1024, 50, 2);
/*   
    Doubling test for quicksort comparing nonuniform type 2 unshuffled vs. shuffled 50 trials
               N       time1       time2        ratio1        ratio2
            1024           0           0        0.200         1.200
            2048           0           0        2.000         1.667
            4096           0           1        2.400         3.400
            8192           1           1        1.042         1.324
           16384           1           2        2.440         1.733
           32768          11          13        8.770         8.359
           65536           8           7        0.748         0.531
          131072           5           8        0.670         1.168
          262144           7          25        1.257         3.059
          524288          14          55        2.045         2.228
         1048576          27         126        1.962         2.294
         2097152          56         276        2.082         2.185
    
*/    
    
    // this tests sorting double arrays with half the data 0s and half random 
    // integer values from min to max all randomized
    params[0] = new double[]{0,26}; // min is 0 and max is 26
    sortNonuniformVsShuffledDoublingTest("quick", 1024, 50, 3, params);
/*    
    Doubling test for quicksort comparing nonuniform type 3 unshuffled vs. shuffled 50 trials
               N       time1       time2        ratio1        ratio2
            1024           0           0        0.269         4.500
            2048           0           0        2.286         2.556
            4096           1           1        1.625         1.348
            8192           1           2        1.538         3.290
           16384           8           8        9.950         4.098
           32768          15           3        1.897         0.337
           65536           4           4        0.253         1.426
          131072           6          10        1.628         2.537
          262144          14          31        2.280         3.080
          524288          35          71        2.444         2.260
         1048576          82         170        2.380         2.391
         2097152         171         352        2.072         2.076
    
*/    
    
    
  }

}

