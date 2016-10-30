package ex14;

import static java.lang.Math.*;
import static analysis.DoublingTest.*;
import static analysis.ThreeSum.*;
import static analysis.Log.*;

/*
  1.4.41 Running times. Estimate the amount of time it would take to run TwoSumFast ,
  TwoSum , ThreeSumFast and ThreeSum on your computer to solve the problems for a file
  of 1 million numbers. Use  DoublingRatio to do so.
  
  1.4.9  Give a formula to predict the running time of a program for a problem of size N
  when doubling experiments have shown that the doubling factor is 2**b and the running
  time for problems of size N0 (N-subscript-0) is T.
 */

@SuppressWarnings("unused")
public class Ex1441RunningTimes {
  
  public static double estimateRunTime(int rtm, int m , double r, int n) {
    // return the estimated running time for data size n given the actual
    // running time rtm for data size m and the doubling ratio r.
    // this was derived in exercise 1.4.9.
    return rtm*pow(1.0*n/m, lg(r));
  }

  public static void main(String[] args) {
    
    //twoSumFastDoublingRatio();
    //twoSumFast doubling ratios:
    //      N      time   time/prev
    //      250       1   0.125
    //      500       0   0.000
    //     1000       0     NaN
    //     2000       1   Infinity
    //     4000       2   2.000
    //     8000       2   1.000
    //    16000       4   2.000
    //    32000       9   2.250
    //    64000      15   1.667
    //   128000      32   2.133
    //   256000      67   2.094
    //   512000     118   1.761
    //  1024000     216   1.831
    //  2048000     394   1.824
    //  4096000     791   2.008
    //  8192000    1552   1.962
    // 16384000    3016   1.943
    // 32768000    5838   1.936
    // 65536000   11380   1.949
    //131072000   22515   1.978
    //262144000   44649   1.983
    
    //System.out.println("twoSumFast estimated run time for 1M numbers");
    //System.out.println(estimateRunTime(4, 16000, 2, 1000000)); //250.0
    
    //twoSumDoublingRatio();
    //  twoSum doubling ratios:
    //    N      time   time/prev
    //    250       1   1.000
    //    500       1   1.000
    //   1000       3   3.000
    //   2000       3   1.000
    //   4000       9   3.000
    //   8000      15   1.667
    //  16000      61   4.067
    //  32000     242   3.967
    //  64000     961   3.971
    // 128000    4031   4.195
    // 256000   15510   3.848

    //System.out.println("twoSum estimated run time for 1M numbers");
    //System.out.println(estimateRunTime(15510, 256000, 4, 1000000)); //236663.818359375
    
    //threeSumFastDoublingRatio();
    // threeSumFast doubling ratios:
    //  N      time   time/prev
    //  250       3   0.300
    //  500       9   3.000
    // 1000      29   3.222
    // 2000     117   4.034
    // 4000     492   4.205
    // 8000    2049   4.165
    //16000    8644   4.219
    
    //System.out.println("threeSumFast estimated run time for 1M numbers");
    //System.out.println(estimateRunTime(8644, 16000, 4, 1000000)); //3.3765625E7
    
    //threeSumDoublingRatio();
    // threeSum doubling ratios:
    //   N      time   time/prev
    //   250       4   0.800
    //   500      52   13.000
    //  1000      97   1.865
    //  2000     656   6.763
    //  4000    5174   7.887
    //  8000   40443   7.817
    // 16000  323509   7.999
    
    //System.out.println(estimateRunTime(40443, 8000, 8, 16000)); //323544.0
    
    //System.out.println("threeSum estimated run time for 1M numbers");
    //System.out.println(estimateRunTime(323509, 16000, 8, 1000000)); //7.8981689453125E10
    
  }

}
