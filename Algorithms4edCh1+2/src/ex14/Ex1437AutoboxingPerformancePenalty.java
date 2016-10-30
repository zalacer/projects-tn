package ex14;

import analysis.Timer;
import ds.FixedCapacityStack;
import ds.FixedCapacityStackOfInts;

/*
  p214 
  1.4.37 Autoboxing performance penalty. Run experiments to determine the perfor-
  mance penalty on your machine for using autoboxing and auto-unboxing. Develop an
  implementation FixedCapacityStackOfInts and use a client such as  DoublingRatio
  to compare its performance with the generic FixedCapacityStack<Integer> , for a
  large number of  push() and  pop() operations.
  
  Based on the results shown below, there is clearly a large overhead for autoboxing
  and autounboxing to the extent that it took too long to test FixedCapacityStack
  for stacks with sizes above 65536000 while with FixedCapacityStackOfInts there was
  no long wait to test stacks with sizes up to 262144000 and beyond except that my
  JVM heap ran out of space over that. The time ratios were not always increasing 
  apparently due to JVM optimization such as caching ints which also affects Integers
  constructed from them, and they were more erratic for FixedCapacityStack than for 
  FixedCapacityStackOfInts. The pop tests are especially susceptible to int caching
  optimization because just prior to timing the pops all the ints/Integers to be popped
  were pushed into the stack. Maybe its possible to circumvent that by pushing a large
  number of other ints into another stack thereby flooding the cache with them before 
  popping the first. Reliable java performance testing is difficult with an ordinary 
  JVM because it does so much automatically when it wants.
  
*/
  public class Ex1437AutoboxingPerformancePenalty {
    
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
    
  public static void main(String[] args) {
    
  System.out.println("time is in ms for all tests");  
   
  System.out.println("\nFixedCapacityStackOfInts push performance for stacks of size n:");
  doublingRatioFixedCapacityStackOfIntsPush();
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

  System.out.println("\nFixedCapacityStackOfInts pop performance for stacks of size n:");
  doublingRatioFixedCapacityStackOfIntsPop();
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

  System.out.println("\nFixedCapacityStack push performance for stacks of size n:");
  doublingRatioFixedCapacityStackPush();
  //    n        time   time/prev
  //    64000       5   1.000
  //   128000       5   1.000
  //   256000       6   1.200
  //   512000       7   1.167
  //  1024000     113   16.143
  //  2048000      67   0.593
  //  4096000    1123   16.761
  //  8192000    4660   4.150
  // 16384000     644   0.138
  // 32768000    3255   5.054
  // 65536000   47016   14.444
  
  System.out.println("\nFixedCapacityStack pop performance for stacks of size n:");
  doublingRatioFixedCapacityStackPop();
  //    n        time   time/prev
  //    64000       5   2.500
  //   128000       3   0.600
  //   256000       3   1.000
  //   512000       9   3.000
  //  1024000      27   3.000
  //  2048000      56   2.074
  //  4096000    1082   19.321
  //  8192000    4535   4.191
  // 16384000     625   0.138
  // 32768000    2909   4.654
  // 65536000   45165   15.526

  }

}
