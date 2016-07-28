package analysis;

import static analysis.Log.lg;
import static java.lang.Math.*;

import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
//import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdOut;
import ds.FixedCapacityStackOfInts;
import ds.FixedCapacityStack;
import ds.Stack;
import ds.ResizingArrayStack;
import ds.IntStack;
import ds.ResizingIntArrayStack;

public class DoublingTest {

  public static int MAX = 1000000;
  
  public static double estimateRunTime(int rtm, int m , double r, int n) {
    // return the estimated running time for data size n given the actual
    // running time rtm for data size m and the doubling ratio r
    // based on the formula derived in exercise 1.4.9 p209
    return rtm*pow(1.0*n/m, lg(r));
  }
  
  public static double intStackTimeTrial(int N) { 
    int n = 100000;
    @SuppressWarnings("unused") int x;
    IntStack s = new IntStack();
    Timer t = new Timer();
    for (int j = 0; j < 1000; j++) {
      for (int i = 0; i < n; i++) s.push(i);
      for (int i = 0; i < n; i++) x = s.pop();
    }
    return t.finish();
  }
  
  public static double stackIntegerTimeTrial(int N) { 
    int n = 100000;
    @SuppressWarnings("unused") int x;
    Stack<Integer> s = new Stack<Integer>();
    Timer t = new Timer();
    for (int j = 0; j < 1000; j++) {
      for (int i = 0; i < n; i++) s.push(i);
      for (int i = 0; i < n; i++) x = s.pop();
    }
    return t.finish();
  }
  
  public static double resizingIntArrayStackTimeTrial(int N) { 
    int n = 100000;
    @SuppressWarnings("unused") int x;
    ResizingIntArrayStack s = new ResizingIntArrayStack();
    Timer t = new Timer();
    for (int j = 0; j < 1000; j++) {
      for (int i = 0; i < n; i++) s.push(i);
      for (int i = 0; i < n; i++) x = s.pop();
    }
    return t.finish();
  }
  
  public static double resizingArrayStackIntegerTimeTrial(int N) { 
    int n = 100000; 
    @SuppressWarnings("unused") int x;
    ResizingArrayStack<Integer> s = new ResizingArrayStack<Integer>();
    Timer t = new Timer();
    for (int j = 0; j < 1000; j++) {
      for (int i = 0; i < n; i++) s.push(i);
      for (int i = 0; i < n; i++) x = s.pop();
    }
    return t.finish();
  }
  
  public static void intStackVsResizingIntArrayStackDoublingRatio() {
    int n = 125;
    double prev1 = resizingIntArrayStackTimeTrial(125);
    double prev2 = intStackTimeTrial(125);
    double prev = prev2/prev1;
    System.out.println("IntStack vs ResizingArrayStack Doubling Ratios:");
    System.out.println("    N      ratio/previous-ratio");
    double time1, time2, time;
    while (true) {
      n += n;
      time1 = resizingIntArrayStackTimeTrial(n);
      time2 = intStackTimeTrial(n);
      time = time2/time1; 
      System.out.printf("%7d %7.0f \n", n, time/prev);
      prev = time;
    }
  }
  
  public static void stackIntegerVsResizingArrayStackIntegerDoublingRatio() {
    int n = 125;
    double prev1 = resizingArrayStackIntegerTimeTrial(125);
    double prev2 = stackIntegerTimeTrial(125);
    double prev = prev2/prev1;
    System.out.println("StackInteger vs ResizingArrayStackInteger Doubling Ratios:");
    System.out.println("    N      ratio/previous-ratio");
    double time1, time2, time;
    while(true) {
      n += n;
      time1 = resizingArrayStackIntegerTimeTrial(n);
      time2 = stackIntegerTimeTrial(n);
      time = time2/time1; 
      System.out.printf("%7d %7.0f \n", n, time/prev);
      prev = time;
    }
  }
  
  public static void stackIntegerVsResizingIntArrayStackDoublingRatio() {
    int n = 125;
    double prev1 = resizingIntArrayStackTimeTrial(125);
    double prev2 = stackIntegerTimeTrial(125);
    double prev = prev2/prev1;
    System.out.println("StackInteger vs ResizingArrayStackInteger Doubling Ratios:");
    System.out.println("    N      ratio/previous-ratio");
    double time1, time2, time;
    while(true) {
      n += n;
      time1 = resizingIntArrayStackTimeTrial(n);
      time2 = stackIntegerTimeTrial(n);
      time = time2/time1; 
      System.out.printf("%7d %7.0f \n", n, time/prev);
      prev = time;
    }
  }
  
  // text p177 along with main
  public static double threeSumTimeTrial(int N) { 
    // Time ThreeSum.count() for N random 6-digit ints.
    //    int MAX = 1000000; //1000000
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = StdRandom.uniform(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = ThreeSum.count(a);
    return t.finish();
  }
  
  // text p192
  public static void threeSumDoublingRatio() {
    double prev = threeSumTimeTrial(125);
    System.out.println("threeSum doubling ratios:");
    System.out.println("    N      time   time/prev");
    for (int N = 250; true; N += N) {
      double time = threeSumTimeTrial(N);
//      System.out.printf("%yd %7.3f ", N, time);
//      System.out.printf("%5.3f\n", time/prev);
      System.out.printf("%7d %7.0f ", N, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double threeSumFastTimeTrial(int N) { 
    // Time ThreeSum.count() for N random 6-digit ints.
    //    int MAX = 1000000; //1000000
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = StdRandom.uniform(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = ThreeSumFast.count(a);
    return t.finish();
  }
  
  // text p192
  public static void threeSumFastDoublingRatio() {
    double prev = threeSumFastTimeTrial(125);
    System.out.println("threeSumFast doubling ratios:");
    System.out.println("    N      time   time/prev");
    for (int N = 250; true; N += N) {
      double time = threeSumFastTimeTrial(N);
//      System.out.printf("%yd %7.3f ", N, time);
//      System.out.printf("%5.3f\n", time/prev);
      System.out.printf("%7d %7.0f ", N, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double twoSumTimeTrial(int N) { 
    //    int MAX = 1000000; //1000000
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = StdRandom.uniform(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = TwoSum.count(a);
    return t.finish();
  }
  
  public static void twoSumDoublingRatio() {
    double prev = twoSumTimeTrial(125);
    System.out.println("twoSum doubling ratios:");
    System.out.println("    N      time   time/prev");
    for (int N = 250; true; N += N) {
      double time = twoSumTimeTrial(N);
//      System.out.printf("%yd %7.3f ", N, time);
//      System.out.printf("%5.3f\n", time/prev);
      System.out.printf("%7d %7.0f ", N, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double twoSumFastTimeTrial(int N) { 
    //    int MAX = 1000000; //1000000
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = StdRandom.uniform(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = TwoSumFast.count(a);
    return t.finish();
  }
  
  // text p192
  public static void twoSumFastDoublingRatio() {
    double prev = twoSumFastTimeTrial(125);
    System.out.println("twoSumFast doubling ratios:");
    System.out.println("    N      time   time/prev");
    for (int N = 250; true; N += N) {
      double time = twoSumFastTimeTrial(N);
//      System.out.printf("%yd %7.3f ", N, time);
//      System.out.printf("%5.3f\n", time/prev);
      System.out.printf("%7d %7.0f ", N, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double timeTrial(int N, BiFunction<Integer,Integer,Integer> f) { 
    // Time ThreeSum.count() for N random 6-digit ints.
    int MAX = 1000000; //1000000
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = f.apply(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = ThreeSum.count(a);
    return t.finish();
  }

  public static double timeTrial(int n, IntConsumer f) { 
    for (int i = 0; i < n; i++)
      f.accept(i);
    Timer t = new Timer();
    return t.finish();
  }

  public static double timeTrial(int n, IntFunction<Integer> f) { 
    @SuppressWarnings("unused")
    int j = 0;
    for (int i = 0; i < n; i++)
      j = f.apply(i);
    Timer t = new Timer();
    return t.finish();
  }
  
  public static double timeTrialFixedCapacityStackOfIntsPush(int n) { 
    FixedCapacityStackOfInts s = new FixedCapacityStackOfInts(n);
    Timer t = new Timer();
    for (int i = 0; i < n; i++)
      s.push(i);
    return t.finish();
  }
  
  public static void doublingRatioFixedCapacityStackOfIntsPush() {
    int n = 32000;
    double prev = timeTrialFixedCapacityStackOfIntsPush(32000);
    System.out.println("      n       time  time/prev");
    while(n <= 131072000) {
      n = 2*n;
      double time = timeTrialFixedCapacityStackOfIntsPush(n);
      System.out.printf("%11d %5.0f ", n, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double timeTrialFixedCapacityStackOfIntsPop(int n) { 
    FixedCapacityStackOfInts s = new FixedCapacityStackOfInts(n);
    for (int i = 0; i < n; i++)
      s.push(i);
    Timer t = new Timer();
    for (int i = 0; i < n; i++)
      s.pop();
    return t.finish();
  }
  
  public static void doublingRatioFixedCapacityStackOfIntsPop() {
    int n = 32000;
    double prev = timeTrialFixedCapacityStackOfIntsPop(32000);
    System.out.println("      n       time  time/prev");
    while(n <= 131072000) {
      n = 2*n;
      double time = timeTrialFixedCapacityStackOfIntsPush(n);
      System.out.printf("%11d %5.0f ", n, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double timeTrialFixedCapacityStackPush(int n) { 
    FixedCapacityStack<Integer> s = new FixedCapacityStack<Integer>(n);
    Timer t = new Timer();
    for (int i = 0; i < n; i++)
      s.push(i);
    return t.finish();
  }
  
  public static void doublingRatioFixedCapacityStackPush() {
    int n = 32000;
    double prev = timeTrialFixedCapacityStackPush(32000);
    System.out.println("      n        time   time/prev");
    while(n <= 131072000) {
      n = 2*n;
      double time = timeTrialFixedCapacityStackPush(n);
      System.out.printf("%11d %7.0f ", n, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static double timeTrialFixedCapacityStackPop(int n) { 
    FixedCapacityStack<Integer> s = new FixedCapacityStack<Integer>(n);
    for (int i = 0; i < n; i++)
      s.push(i);
    Timer t = new Timer();
    for (int i = 0; i < n; i++)
      s.pop();
    return t.finish();
  }
  
  public static void doublingRatioFixedCapacityStackPop() {
    int n = 32000;
    double prev = timeTrialFixedCapacityStackPop(32000);
    System.out.println("      n        time   time/prev");
    while(n <= 131072000) {
      n = 2*n;
      double time = timeTrialFixedCapacityStackPush(n);
      System.out.printf("%11d %7.0f ", n, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static void doublingRatio(BiFunction<Integer,Integer,Integer> f) {
    double prev = timeTrial(125, f);
    for (int N = 250; true; N += N) {
      double time = timeTrial(N, f);
      System.out.printf("%6d %7.3f ", N, time);
      System.out.printf("%5.3f\n", time/prev);
      prev = time;
    }
  }
  
  public static void doublingRatio(IntConsumer f) {
    double prev = timeTrial(125, f);
    for (int N = 250; N <= 1000000; N += N) {
      double time = timeTrial(N,f);
      System.out.printf("%6d %7.3f ", N, time);
      System.out.printf("%5.3f\n", time/prev);
      prev = time;
    }
  }

  public static void doublingRatioLog() {
    double prev = threeSumTimeTrial(125);
    for (int N = 250; true; N += N) {
      double time = threeSumTimeTrial(N);
      StdOut.printf("%7.3f %7.3f ", log(N), log(time));
      StdOut.printf("%5.3f\n", log(time/prev));
      prev = time;
    }
  }

  public static void drawData() {
    int N = 250;
    StdDraw.setPenRadius(.01);
    StdDraw.setXscale(0, 9*N);
    StdDraw.setYscale(0,60);
    for (;true; N += N) {
      double time = threeSumTimeTrial(N);
      StdDraw.point(N/10, time);
    }
  }

  public static void drawLogData() {
    int N = 250;
    StdDraw.setPenRadius(.01);
    StdDraw.setXscale(0, 10);
    StdDraw.setYscale(0, 10);
    for (;true; N += N) {
      double time = threeSumTimeTrial(N);
      StdDraw.point(log(N)-5, log(time)+5);
    }

    ////    int N = 250;
    //    StdDraw.setXscale(0, N);
    //    StdDraw.setYscale(0, N*N);
    //    StdDraw.setPenRadius(.01);
    //    for (int i = 1; i <= N; i++) {
    //      StdDraw.point(i, i); // bottom
    //      StdDraw.point(i, i*Math.log(i)); //middle
    //      StdDraw.point(i, i*i); // top
    //    }
  }

  public static void main(String[] args) {
    
//    int n = 10000000;
//    double d = timeTrialFixedCapacityStackOfIntsPush(n);
//    System.out.println(d);
//    FixedCapacityStackOfInts s = new FixedCapacityStackOfInts(n);
//    for (int i = 0; i < n; i++)
//      s.push(i);
    
//    doublingRatioFixedCapacityStackOfIntsPush();
    //      n       time  time/prev
    //      64000     2   1.000
    //     128000     1   0.500
    //     256000     3   3.000
    //     512000     1   0.333
    //    1024000     2   2.000
    //    2048000     3   1.500
    //    4096000     2   0.667
    //    8192000     5   2.500
    //   16384000     9   1.800
    //   32768000    22   2.444
    //   65536000    38   1.727
    //  131072000    73   1.921
    //  262144000   140   1.918

//    doublingRatioFixedCapacityStackOfIntsPop();
    //      n       time  time/prev
    //      64000     2   1.000
    //     128000     1   0.500
    //     256000     2   2.000
    //     512000     1   0.500
    //    1024000     6   6.000
    //    2048000     1   0.167
    //    4096000     3   3.000
    //    8192000     5   1.667
    //   16384000     8   1.600
    //   32768000    18   2.250
    //   65536000    37   2.056
    //  131072000    71   1.919
    //  262144000   141   1.986

//    doublingRatioFixedCapacityStackPush();
    //      n        time   time/prev
    //      64000       5   1.000
    //     128000       5   1.000
    //     256000       6   1.200
    //     512000       7   1.167
    //    1024000     113   16.143
    //    2048000      67   0.593
    //    4096000    1123   16.761
    //    8192000    4660   4.150
    //   16384000     644   0.138
    //   32768000    3255   5.054
    //   65536000   47016   14.444
    
    doublingRatioFixedCapacityStackPop();
    //      n        time   time/prev
    //      64000       5   2.500
    //     128000       3   0.600
    //     256000       3   1.000
    //     512000       9   3.000
    //    1024000      27   3.000
    //    2048000      56   2.074
    //    4096000    1082   19.321
    //    8192000    4535   4.191
    //   16384000     625   0.138
    //   32768000    2909   4.654
    //   65536000   45165   15.526
    
    
//    double r = timeTrial(100, (x,y)->StdRandom.uniform(x,y));
//    System.out.println(r);
//    
//    doublingRatio((x,y)->StdRandom.uniform(x,y));

    //    // Print table of running times.
    //    for (int N = 250; true; N += N)
    //    { // Print time for problem size N.
    //      double time = timeTrial(N);
    //      StdOut.printf("%7d %5.1f\n", N, time);
    //    }
    //doublingRatio();
    //    drawData();
    //    drawLogData();
    //    doublingRatioLog();

  }

  // using main orig for MAX = 1000
  //  250   0.0
  //  500   0.0
  // 1000   0.1
  // 2000   0.7
  // 4000   5.2
  // 8000  40.8

  // using doublingRatio with MAX = 1000000
  //   250   0.014 2.800
  //   500   0.012 0.857
  //  1000   0.086 7.167
  //  2000   0.650 7.558
  //  4000   5.103 7.851
  //  8000  40.317 7.901
  // 16000 322.427 7.997

  //using doublingRatioLog with MAX = 1000000
  //  5.521  -4.828 0.470
  //  6.215  -3.507 1.322
  //  6.908  -2.216 1.290
  //  7.601  -0.143 2.074
  //  8.294   1.972 2.115
  //  8.987   3.929 1.957

}
