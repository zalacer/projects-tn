package ex14;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static v.ArrayUtils.mean;
import static v.ArrayUtils.stddev;
import static v.ArrayUtils.var;

import java.util.HashSet;
import java.util.Random;

/*
 1.4.44 Birthday problem. Write a program that takes an integer N from the command
  line and uses StdRandom.uniform() to generate a random sequence of integers be-
  tween 0 and N – 1. Run experiments to validate the hypothesis that the number of
  integers generated before the first repeated value is found is ~√PI*N/2.
  
  Using a form of doubling test I qualitatively validated the hypothesis statistically
  by averaging the (actual - predicted) over all N in [0, X] for X in [10, 100, 1000] 
  for sequence sizes starting at 10000 and doubled up through 20480000. This test showed
  that on average the hypotheses give results within 1 of the actual.
 */

@SuppressWarnings("unused")
public class Ex1444BirthdayProblem {
  
  public static Random r = new Random(776533187);

  public static int predicted(int n) {
    return (int) (sqrt((PI*n)/2));
  }
  
  static int findFirstRepeated(int z[]) {
    // return the index of the first repeated element in z
    // if any else return -1 if all elements are unique.
    int p = -1;
    HashSet<Integer> set = new HashSet<>();
    for (int i = 0; i < z.length; i++)
      if (set.contains(z[i])) {
        return i;
      } else set.add(z[i]);
    return -1;
  }
  
  public static int singleTestHypo(int n, int s) {
    // test hypothesis for N = n using a sequence of size s
    int[] a = r.ints(s, 0, n).toArray();
    int x = findFirstRepeated(a);
    if (x != -1) return (x - predicted(n));
    return Integer.MIN_VALUE;
  }
  
  public static double[] testHypo(int n, int s) {
    // test hypotheses for each N in [0,n) using a sequence of size s and return
    // the mean and stddev of the results in a double[]
    double[] z = new double[n-2];
    int q = 0;
    for (int i = 0; i < n-2; i++) {
      q = singleTestHypo(i+1, s);
      if (q!=Integer.MIN_VALUE) {
        z[i] = q;
      } else i--;
    }
    return new double[]{mean(z), stddev(z)};    
  }
  
  public static void doublingTestHypo(int n) {
    // test hypotheses for for each N in [0,n) using sequences with length starting
    // at 5000 and successively doubled up to 262144000 and print the results
    int m = 5000;
    double[] d = testHypo(n, m);
    double pmean = d[0]; double pstddev = d[1];
    System.out.println("Sequence size   mean     stddev   mean/previous  stddev/previous");
    while(m <= 10240000) {
      m = 2*m;
      d = testHypo(n, m);
      System.out.printf("%-15d %-+5.3f   %-+5.3f   %-+5.3f         %-+5.3f\n", m, d[0], d[1], d[0]/pmean, d[1]/pstddev);
      pmean = d[0]; pstddev = d[1];
    }
  }
  
  public static void main(String[] args) {
    
    //doublingTestHypo(10); // test hypotheses for each N in [0,10)
    //  Sequence size   mean     stddev   mean/previous  stddev/previous
    //  10000           +0.000   +1.069   NaN            +1.414
    //  20000           +0.000   +1.512   NaN            +1.414
    //  40000           +0.500   +1.414   +Infinity      +0.935
    //  80000           +0.750   +0.707   +1.500         +0.500
    //  160000          +0.375   +0.744   +0.500         +1.052
    //  320000          +0.375   +0.744   +1.000         +1.000
    //  640000          +0.125   +0.835   +0.333         +1.122
    //  1280000         +0.500   +0.756   +4.000         +0.906
    //  2560000         +0.000   +0.535   +0.000         +0.707
    //  5120000         +0.500   +1.309   +Infinity      +2.449
    //  10240000        +0.375   +0.916   +0.750         +0.700
    //  20480000        -0.250   +0.707   -0.667         +0.772
    
    //doublingTestHypo(100); // test hypotheses for each N in [0,100)
    //  Sequence size   mean     stddev   mean/previous  stddev/previous
    //  10000           +0.622   +4.759   +1.605         +1.088
    //  20000           +0.908   +4.741   +1.459         +0.996
    //  40000           +0.408   +3.809   +0.449         +0.804
    //  80000           +0.122   +4.598   +0.300         +1.207
    //  160000          -0.694   +3.939   -5.667         +0.857
    //  320000          +0.622   +4.634   -0.897         +1.177
    //  640000          +0.010   +3.949   +0.016         +0.852
    //  1280000         +0.276   +4.522   +27.000        +1.145
    //  2560000         -0.245   +4.853   -0.889         +1.073
    //  5120000         +0.612   +4.220   -2.500         +0.870
    //  10240000        +0.622   +4.354   +1.017         +1.032
    //  20480000        +0.673   +4.637   +1.082         +1.065
    //  40960000        -0.694   +4.153   -1.030         +0.896
    
    //doublingTestHypo(1000); // test hypotheses for each N in [0,1000)
    //  Sequence size   mean     stddev   mean/previous  stddev/previous
    //  10000           +0.624   +14.642  +1.097         +0.994
    //  20000           +0.354   +14.639  +0.567         +1.000
    //  40000           -0.400   +14.341  -1.130         +0.980
    //  80000           +0.425   +13.850  -1.063         +0.966
    //  160000          +0.057   +13.863  +0.134         +1.001
    //  320000          -0.206   +14.212  -3.614         +1.025
    //  640000          +0.179   +14.569  -0.869         +1.025
    //  1280000         +0.166   +13.717  +0.927         +0.942
    //  2560000         +0.623   +14.095  +3.747         +1.027
    //  5120000         -0.258   +13.993  -0.413         +0.993
    //  10240000        -0.327   +14.515  +1.268         +1.037
    //  20480000        +0.126   +13.927  -0.387         +0.959

    
  }

}
