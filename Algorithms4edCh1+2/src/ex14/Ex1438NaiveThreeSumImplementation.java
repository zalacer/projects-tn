package ex14;

import edu.princeton.cs.algs4.StdRandom;
import analysis.Timer;

/*
  p214 
  1.4.38 Naive 3-sum implementation. Run experiments to evaluate the following 
  implementation of the inner loop of ThreeSum :
  
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        for (int k = 0; k < N; k++)
          if (i < j && j < k)
            if (a[i] + a[j] + a[k] == 0)
              cnt++;
                
  Do so by developing a version of DoublingTest that computes the ratio of the
  running times of this program and ThreeSum.
  
  Test results below show that naive sum's running times increase faster than three sum's 
  by as much as a factor of 4 per iteration, but its highly variable depending on the 
  numbers generated and other possible factors such as the order in which the tests are run.
  I only ran it long enough once to see if the ratio between the two approached a limit
  where it seemed to converge to 4.  Here is the output for that test:
  
     N     naive/3sum    ratio/previousRatio = current naive/3sum / previous naive/3sum
     250   3.250         1.625
     500   5.000         2.500
    1000   7.494         3.747
    2000   7.834         3.917
    4000   7.941         3.970
    8000   7.970         3.985 
    
  Other results shown below make that seem unlikely that it converges to 4 for all input data 
  or else it takes much longer getting there.
  
*/

  public class Ex1438NaiveThreeSumImplementation {
    
    public static void naiveToThreeSumDoublingRatio1() {
      // this prints the ratio of naiveSum to threeSum and 
      // computes naiveSum before threeSum on each iteration
      double prev1 = naiveSumTimeTrial(125);
      double prev2 = threeSumTimeTrial(125);
      double prevr = prev1/prev2;
      System.out.println("   N     naive/3sum    ratio/previousRatio");
      for (int n = 250; true; n += n) {
        double time1 = naiveSumTimeTrial(n);
        double time2 = threeSumTimeTrial(n);
        System.out.printf("%6d   %5.3f         %5.3f\n", n, time1/time2, time1/(time2*prevr));
        prev1 = time1;
        prev2 = time2;
      }
    }
    
    public static void naiveToThreeSumDoublingRatio2() {
      // this prints the ratio of naiveSum to threeSum and 
      // computes threeSum before naiveSum on each iteration
      double prev2 = threeSumTimeTrial(125);
      double prev1 = naiveSumTimeTrial(125);
      double prevr  = prev1/prev2;
      System.out.println("   N     naive/3sum    ratio/previousRatio");
      for (int n = 250; true; n += n) {
        double time2 = threeSumTimeTrial(n);
        double time1 = naiveSumTimeTrial(n);
        System.out.printf("%6d   %5.3f         %5.3f\n", n, time1/time2, time1/(time2*prevr));
        prev2 = time2;
        prev1 = time1;
      }
    }
    
    public static int threeSumCount(int[] a) { 
      // count triples that sum to 0.
      int N = a.length;
      int cnt = 0;
      for (int i = 0; i < N; i++)        //1
        for (int j = i+1; j < N; j++)    //~N
          for (int k = j+1; k < N; k++)  //~(N**2)/2 
            if (a[i] + a[j] + a[k] == 0) //~(N**3)/6
              cnt++;
      return cnt;
    }
    
    public static double threeSumTimeTrial(int N) { 
      // time threeSumCount() for N random 6-digit ints.
      int MAX = 100000;
      int[] a = new int[N];
      for (int i = 0; i < N; i++)
        a[i] = StdRandom.uniform(-MAX, MAX);
      Timer t = new Timer();
      @SuppressWarnings("unused")
      int cnt = threeSumCount(a);
      return t.finish();
    }
    
    public static void threeSumDoublingRatio() {
      double prev = threeSumTimeTrial(125);
      System.out.println("   N      time   time/prev");
      for (int n = 250; true; n += n) {
        double time = threeSumTimeTrial(n);
        System.out.printf("%6d %7.0f   ", n, time);
        System.out.printf("%5.3f\n", time/prev);
        prev = time;
      }
    }
    
    public static int naiveSumCount(int[] a) { 
      // count triples that sum to 0.
      int N = a.length;
      int cnt = 0;
      for (int i = 0; i < N; i++)        
        for (int j = 0; j < N; j++)   
          for (int k = 0; k < N; k++) 
            if (i < j && j < k)
              if (a[i] + a[j] + a[k] == 0) 
                cnt++;
      return cnt;
    }
  
    public static double naiveSumTimeTrial(int N) { 
      // time naiveSumCount() for N random 6-digit ints.
      int MAX = 100000;
      int[] a = new int[N];
      for (int i = 0; i < N; i++)
        a[i] = StdRandom.uniform(-MAX, MAX);
      Timer t = new Timer();
      @SuppressWarnings("unused")
      int cnt = naiveSumCount(a);
      return t.finish();
    }
    
    public static void naiveSumDoublingRatio() {
      double prev = naiveSumTimeTrial(125);
      System.out.println("   N      time   time/prev");
      for (int n = 250; true; n += n) {
        double time = naiveSumTimeTrial(n);
        System.out.printf("%6d %7.0f   ", n, time);
        System.out.printf("%5.3f\n", time/prev);
        prev = time;
      }
    }
    
  public static void main(String[] args) {
    
    naiveToThreeSumDoublingRatio1();           // computes naiveSum before threeSum
    //  N     naive/3sum    ratio/previousRatio  // 3 consecutive trials
    //  250   1.917         0.639                // ran garbage collector before each trial
    //  500   8.182         2.727
    // 1000   6.071         2.024
    // 2000   6.196         2.065
    // 4000   6.267         2.089 
    //
    //  N     naive/3sum    ratio/previousRatio
    //  250   1.182         0.295
    //  500   3.182         0.795
    // 1000   3.298         0.825
    // 2000   3.312         0.828
    // 4000   3.294         0.823
    //
    //  N     naive/3sum    ratio/previousRatio
    //  250   1.000         0.400
    //  500   2.958         1.183
    // 1000   4.075         1.630
    // 2000   3.951         1.580
    // 4000   3.940         1.576
    
    //naiveToThreeSumDoublingRatio2();           // computes threeSum before naiveSum
    //  N     naive/3sum    ratio/previousRatio  // 3 consecutive trials
    //  250   1.900         0.844                // ran garbage collector before each trial
    //  500   6.000         2.667
    // 1000   7.619         3.386
    // 2000   6.175         2.744
    // 4000   6.239         2.773 // maybe converging to 2.8 or even 3 except it went above that
    //
    //  N     naive/3sum    ratio/previousRatio
    //  250   2.111         1.173
    //  500   8.455         4.697
    // 1000   6.095         3.386
    // 2000   6.206         3.448
    // 4000   6.271         3.484 // maybe converging to 4 but it already went above that
    //
    //  N     naive/3sum    ratio/previousRatio
    //  250   1.636         0.491
    //  500   2.750         0.825
    // 1000   3.897         1.169
    // 2000   3.148         0.944
    // 4000   3.157         0.947 // maybe converging to 1 but it already went above that

    
//    threeSumDoublingRatio();
    //  N      time   time/prev
    //  250      14   2.800
    //  500      41   2.929
    // 1000     205   5.000
    // 2000    1559   7.605
    // 4000   11243   7.212 
    
//    naiveSumDoublingRatio();
    //  N      time   time/prev
    //  250      28   2.800
    //  500      90   3.214
    // 1000     691   7.678
    // 2000    4819   6.974
    // 4000   33725   6.998
  
    
    
    
  }

}
