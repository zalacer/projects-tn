package ex14;

import edu.princeton.cs.algs4.StdRandom;
import analysis.Timer;

/*
  p214 
  1.4.39 Improved accuracy for doubling test. Modify DoublingRatio to take a second
  command-line argument that specifies the number of calls to make to  timeTrial() for
  each value of  N . Run your program for 10, 100, and 1,000 trials and comment on the
  precision of the results.

  I wrote the code and started a test with 10 repetitions and ran it up to N = 16000.
  Oddly, only in this test and not in two that I ran with 1 repetition, time/prev 
  decreased from N=4000 to N=8000 and then it went up to 8.454 for N=16000. I suspect 
  that the pseudorandom number generator used in StdRandom skews results and think that 
  SecureRandom.getInstanceStrong() might do better. I don't have time for more 3-sum 
  testing.
  
  PS: For this exercise I refactored the DoublingTest class on p192 of the 4ed text into
  a method since its easier to work with in Eclipse.

 */

public class Ex1439ImprovedAccuracyForDoublingTest {

  public static void doublingRatio(int calls) {
    // run timeTrial(N) calls times and average results
    if (calls <= 0) throw new IllegalArgumentException("doublingRatio: calls must be > 0");
    double sum = 0;
    int N = 125;
    for (int i = 0; i < calls; i++) sum+=timeTrial(N);
    double prev = sum/calls;
    System.out.println("    N      time   time/prev");
    while (true) {
      sum = 0;
      N += N;
      for (int i = 0; i < calls; i++) sum+=timeTrial(N);
      double time = sum/calls;
      System.out.printf("%7d %7.0f ", N, time);
      System.out.printf("  %5.3f\n", time/prev);
      prev = time;
    }
  }

  public static double timeTrial(int N) { 
    // Time ThreeSum.count() for N random 6-digit ints.
    int MAX = 1000000; // MAX is now a global static variable
    int[] a = new int[N];
    for (int i = 0; i < N; i++)
      a[i] = StdRandom.uniform(-MAX, MAX);
    Timer t = new Timer();
    @SuppressWarnings("unused")
    int cnt = ThreeSum.count(a);
    return t.finish();
  }

  public static class ThreeSum {
    public static int count(int[] a) { 
      // Count triples that sum to 0.
      int N = a.length;
      int cnt = 0;
      for (int i = 0; i < N; i++)        //1
        for (int j = i+1; j < N; j++)    //~N
          for (int k = j+1; k < N; k++)  //~(N**2)/2 
            if (a[i] + a[j] + a[k] == 0) //~(N**3)/6
              cnt++;
      return cnt;
    }
  }

  public static void main(String[] args) {
      
    //doublingRatio(1);
    //  N      time   time/prev
    //  250       4   0.667
    //  500      29   7.250
    // 1000      85   2.931
    // 2000     654   7.694
    // 4000    5100   7.798
    // 8000   40581   7.957
    //
    //  N      time   time/prev
    //  250      11   2.200
    //  500      11   1.000
    // 1000      84   7.636
    // 2000     651   7.750
    // 4000    5131   7.882
    // 8000   40386   7.871
    
    //doublingRatio(10);
    //  N      time   time/prev
    //  250       3   3.100
    //  500      14   4.581
    // 1000      93   6.549
    // 2000     733   7.876
    // 4000    5715   7.802
    //16000  341423   8.454
    
    doublingRatio(100);

    doublingRatio(1000); 
    
  }

}
