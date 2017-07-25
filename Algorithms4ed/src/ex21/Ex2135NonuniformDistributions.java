package ex21;

import static v.ArrayUtils.*;

import java.util.function.Consumer;

import analysis.Timer;
import edu.princeton.cs.algs4.StdRandom;

/* p268
  2.1.35 Nonuniform distributions. Write a client that generates test data by randomly
  ordering objects using other distributions than uniform, including the following:
 *  Gaussian
 *  Poisson
 *  Geometric
 *  Discrete (see Exercise 2.1.28 for a special case)
  Develop and test hypotheses about the effect of such input on the performance of the
  algorithms in this section.
  
  What I see from running some tests is that shellsort performance for standard gaussian
  distribution is very close to that for uniform distribution, but it can be much faster
  for sorting data with poisson, geometric and discrete distributions with poisson the
  fastest for the few cases I tried.
 */

public class Ex2135NonuniformDistributions {
  
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
    case "default"  : throw new IllegalArgumentException("sortDistTimeTrial: alg not recognized");
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

//    sortDistsDoublingTest("shell", 1024, 3, "uniform", "uniform");
/*        
 Doubling test for shellsort comparing uniform vs. uniform with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           1           1        2.000         1.000
          2048           1           1        0.750         1.333
          4096           2           2        2.000         1.500
          8192           2           2        1.167         1.167
         16384           7           6        2.857         2.429
         32768          17          15        2.550         2.706
         65536          48          39        2.843         2.522
        131072          91          90        1.876         2.336
        262144         272         265        3.000         2.934
        524288         723         663        2.657         2.503
       1048576        1605        1594        2.221         2.403
       2097152        3948        3969        2.459         2.491
*/
    
//    sortDistsDoublingTest("shell", 1024, 3, "uniform", "gaussian");
/*
      Doubling test for shellsort comparing uniform vs. gaussian with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           1           1        1.000         1.000
          2048           1           1        0.500         0.667
          4096           1           1        1.000         1.500
          8192           2           2        3.000         2.333
         16384           5           5        2.667         2.286
         32768          19          12        3.500         2.313
         65536          30          29        1.607         2.378
        131072          86          85        2.856         2.886
        262144         260         256        3.039         3.020
        524288         637         646        2.448         2.525
       1048576        1599        1623        2.509         2.514
       2097152        3908        3958        2.444         2.439
*/   
    
//  sortDistsDoublingTest("selection", 1024, 3, "uniform", "gaussian");
/*
        Doubling test for selectionsort comparing uniform vs. gaussian with 3 trials
             N       time1       time2        ratio1       ratio2
          1024          16           0        0.431         0.000
          2048           5           5        0.319      Infinity
          4096          52          42       10.400         7.813
          8192         140         130        2.699         3.120
         16384         458         374        3.264         2.879
         32768        1560        1565        3.407         4.182
         65536        6557        6651        4.203         4.249
*/
  
//  sortDistsDoublingTest("insertion", 1024, 3, "uniform", "gaussian");
/*
        Doubling test for insertionsort comparing uniform vs. gaussian with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           0           5        0.000         0.938
          2048          10          11     Infinity         2.133
          4096          31          36        3.000         3.406
          8192         135         130        4.366         3.587
         16384         541         556        3.998         4.269
         32768        2319        2324        4.287         4.178
         65536        9714        9709        4.188         4.177
*/

    double[][] params = new double[2][]; 
    params[0] = new double[0]; params[1] = new double[]{3.};
//    sortDistsDoublingTest("shell", 1024, 3, "uniform", "poisson", params);
/*
      Doubling test for shellsort comparing uniform vs. poisson with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           1           1        1.333         0.667
          2048           1           1        0.750         1.500
          4096           2           1        1.667         0.667
          8192           2           1        1.400         2.000
         16384           7           3        3.000         2.000
         32768          13           6        1.857         2.375
         65536          32          11        2.436         1.684
        131072          91          35        2.863         3.281
        262144         257         122        2.838         3.476
        524288         673         208        2.615         1.707
       1048576        1677         478        2.491         2.300
       2097152        3958        1044        2.360         2.186
       4194304       10279        2321        2.597         2.223
*/
    
//  sortDistsDoublingTest("selection", 1024, 3, "uniform", "poisson", params);
/*
      Doubling test for selectionsort comparing uniform vs. poisson with 3 trials
             N       time1       time2        ratio1       ratio2
          1024          16           0        1.516         0.000
          2048          10          16        0.660      Infinity
          4096          21          36        2.032         2.319
          8192          89         177        4.222         4.862
         16384         375         842        4.226         4.768
         32768        1587        3702        4.236         4.395
         65536        6501       15101        4.096         4.079
*/
    
//  sortDistsDoublingTest("insertion", 1024, 3, "uniform", "poisson", params);
/*
      Doubling test for insertionsort comparing uniform vs. poisson with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           5           0        0.516         0.000
          2048          10           5        1.938      Infinity
          4096          31          26        3.032         4.875
          8192         130         104        4.149         4.000
         16384         536         427        4.121         4.103
         32768        2241        1773        4.184         4.156
         65536        9470        7139        4.225         4.026

*/
    params[0] = new double[0]; params[1] = new double[]{.5};
//    sortDistsDoublingTest("shell", 1024, 3, "uniform", "geometric", params);
/*
      Doubling test for shellsort comparing uniform vs. geometric with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           1           1        0.800         1.000
          2048           1           1        1.000         1.000
          4096           2           0        1.750         0.500
          8192           3           1        1.286         3.000
         16384           6           2        1.889         2.000
         32768          14           5        2.412         2.500
         65536          31           9        2.268         1.867
        131072          94          29        3.043         3.107
        262144         264         102        2.795         3.506
        524288         709         180        2.688         1.770
       1048576        1688         398        2.381         2.213
       2097152        3981         864        2.359         2.170
       4194304       10348        1911        2.599         2.211
*/ 
  
//  sortDistsDoublingTest("selection", 1024, 3, "uniform", "geometric", params);
/*
      Doubling test for selectionsort comparing uniform vs. geometric with 3 trials
             N       time1       time2        ratio1       ratio2
          1024          16           5        1.469         0.516
          2048          10          10        0.660         1.938
          4096          21          52        2.032         5.032
          8192          93         245        4.444         4.705
         16384         385        1097        4.125         4.484
         32768        1597        4670        4.148         4.257
         65536        7234       19744        4.530         4.228

*/

//  sortDistsDoublingTest("insertion", 1024, 3, "uniform", "geometric", params);
/*
      Doubling test for insertionsort comparing uniform vs. geometric with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           0           5        0.000      Infinity
          2048          10           5     Infinity         1.000
          4096          31          16        3.000         3.000
          8192         130          83        4.194         5.208
         16384         536         343        4.123         4.116
         32768        2226        1404        4.154         4.093
         65536        9683        6062        4.349         4.318
*/

    params[0] = new double[0]; params[1] = new double[]{.12,.22,.32,.22,.12};
//    sortDistsDoublingTest("shell", 1024, 3, "uniform", "discrete", params);
/*    
      Doubling test for shellsort comparing uniform vs. discrete with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           2           0        1.667         0.333
          2048           1           0        0.600         1.000
          4096           1           0        1.333         0.000
          8192           2           1        1.500      Infinity
         16384           5           2        2.500         3.000
         32768          13           5        2.533         2.667
         65536          30           9        2.342         1.688
        131072          87          30        2.921         3.370
        262144         260          87        3.004         2.868
        524288         695         189        2.671         2.172
       1048576        1590         430        2.286         2.277
       2097152        3889         947        2.446         2.201
       4194304       10475        2074        2.694         2.190
*/    

//  sortDistsDoublingTest("selection", 1024, 3, "uniform", "discrete", params);
/*  
      Doubling test for selectionsort comparing uniform vs. discrete with 3 trials
             N       time1       time2        ratio1       ratio2
          1024          16           5        3.133         0.516
          2048          10          10        0.660         1.938
          4096          21          47        2.000         4.548
          8192          94         213        4.532         4.532
         16384         375         920        4.000         4.321
         32768        1566        3854        4.180         4.187
         65536        7628       16425        4.871         4.262

*/ 
    
//  sortDistsDoublingTest("insertion", 1024, 3, "uniform", "discrete", params);
/*  
      Doubling test for insertionsort comparing uniform vs. discrete with 3 trials
             N       time1       time2        ratio1       ratio2
          1024           0           5        0.000         1.067
          2048          10           0     Infinity         0.000
          4096          31          26        3.000      Infinity
          8192         130          99        4.194         3.808
         16384         551         411        4.241         4.152
         32768        2387        1633        4.330         3.972
         65536       10198        6573        4.272         4.026

*/     
    
    sortDistDoublingTest("shell", 1024, 10, "poisson", 3.);
/*
      Doubling test of shellsort with poisson for 10 trials
             N          time          ratio
          1024             1          1.333
          2048             0          0.500
          4096             1          1.750
          8192             1          1.429
         16384             2          2.300
         32768             5          2.174
         65536            11          2.280
        131072            34          3.018
        262144           116          3.358
        524288           211          1.828
       1048576           471          2.229
       2097152          1033          2.195
       4194304          2245          2.174
*/ 
  
  }

}
