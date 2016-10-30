package ex14;

import static java.lang.Math.*;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
//import java.util.security.;

/*
 1.4.45 Coupon collector problem. Generating random integers as in the previous exer-
  cise, run experiments to validate the hypothesis that the number of integers generated
  before all possible values are generated is ~N*H(N).
  
  In https://en.wikipedia.org/wiki/Coupon_collector's_problem it say that H(N) is the
  expected number of tries per coupon and is the Nth harmonic number. The expected
  number of tries to collect all N coupons is E(T) = n*H(N) = nlogn + gamma*n + 1/2 + o(n),
  where gamma = 0.5572156649 (Euler-Mascheroni constant).
  
  Tests I did validate the hypothesis insofar as the results obtained show that N*H(N)+1 
  is within 1 of the the actual average sequence length of initial subsequences containing
  all possible values randomly generated for values in the ranges [1,10], [1,100] and [1,1000].
  There is no proving the hypothesis experimentally due to its statistical nature, there
  is not need to do so since it's mathematically proven, and my tests actually validate the 
  operation of my computing system at least as much as the hypothesis. Incidentally, I got
  better but slower results by using SecureRandom vs Random.
 */

@SuppressWarnings("unused")
public class Ex1445CouponCollectorProblem {
  
  public static Random r = new Random(10703963);
  
  public static double harmonic(int n) {
    //return the value of the nth harmonic number
    if (n == 1) return 1;
    double sum = 0;
    for (int i = 1; i <= n; i++) sum += 1.0 / i;
    return sum;
  }
  
  public static boolean containsRange(int n) {
    // return true if all ints in [1,n] are in the array of length ceil(n*harmonic(n))
    // populated with ints in [1,n] generated below, else return false. this method
    // was used for initial testing.
    int[] h = r.ints((int)(harmonic(n)*n+1), 1, n+1).toArray();
    return  Arrays.equals(unique(h), range(1,n+1));
  }
  
  public static int findMinLengthContainingRange(int[] z, int n) {
    // return one plus the minimum index i in z at which take(z,i) includes 
    // range[1,n] else return -1 if z doesn't contain all elements in that range.
    BitSet b = new BitSet();
    for (int i = 0; i < z.length; i++)
      if (!b.get(z[i])) {
        b.set(z[i]);
        if (b.cardinality() == n) return i+1;
      }
    return -1;
  }
  
  public static int generateDataPoint(int n) {
    // generate a data point with value (actual - predicted)) where actual
    // is obtained with findMinLengthContainingRange and predicted is 
    // n*harmonic(n)+1. if the hypotheses is true, many data points such 
    // as this should average to zero.
    int h = (int) (harmonic(n)*n);
    int[] z; int actual;
    while (true) {
      z = r.ints(100*h, 1, n+1).toArray();
      // using 100*h for 99% probability of findMinLengthContainingRange not returning -1
      // by Markov inequality (https://en.wikipedia.org/wiki/Coupon_collector's_problem#Calculating_the_expectation)
      actual = findMinLengthContainingRange(z, n);
      if (actual == -1) continue;
      return (actual - 1 - h);
    }
  }
  
  public static double[] testHypo(int n) {
    // collect data using generateDataPoint over 100 iterations for each integer
    // in [1,n] then calculate and report the mean and stddev of all the data
    // in a double[] which is returned.
    int c = 0; int d = 0;
    int[] z = new int[n*100];
    for (int i = 1; i < n+1; i++) {
      for (int j = 0; j < 100; j++) {
        z[c++] = generateDataPoint(i);
      }
    }
    return new double[]{mean(z), stddev(z)};
  }


  public static void main(String[] args) {
    
    //r = new SecureRandom();
              //   N                mean       stddev
    //pa(testHypo(10));   //double[-0.598, 6.533841679065015]  using SecureRandom
                          //double[-0.677, 6.567023435072912]  using Random 
    
    //pa(testHypo(100));  //double[-0.0152, 74.20440686616806] using SecureRandom
                          //double[-0.1817, 73.95248191959627] using Random
        
    //pa(testHypo(1000)); //double[-0.35307, 737.6983378861229] using SecureRandom
                          //double[2.822757242757243, 741.0962900457554] using Random

  }

}
