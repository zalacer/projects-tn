package ex25;

import static v.ArrayUtils.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/* p358
  2.5.31 Duplicates. Write a client that takes integers M, N and T as 
  command-line arguments, then uses the code given in the text to perform
  T trials of the following experiment: Generate N random int values 
  between 0 and M – 1 and count the number of duplicates. Run your program 
  for T = 10 and N = 10^3, 10^4, 10^5, and 10^6, with M = N/2, N and 2N.
  Probability theory says that the number of duplicates should be about
  (1 – e^-N/M). Print a table to help you confirm that your experiments 
  validate that formula.
  
  1 – e^-N/M can't be the correct formula for the probable number of duplicates 
  because it ranges from 0-1, while for an array of length N filled with random 
  ints from [0..N/2) there will surely be more than 1 duplicate and probably 
  close to N/2.
  
  N(1 – e^-N/M) is a correct formula, however I prefer to use N(1-(1-1/M)^N-1),
  because it can be simply derived as an extension of the birthday problem as 
  shown at https://math.stackexchange.com/questions/35791/birthday-problem-expected-number-of-collisions
  that is also available in this project at Ex2531BirthdayProblemCollisions.pdf.
  
  Test results show Java's Random, SecureRandom and SecureRandom.getInstanceStrong 
  all generate about 35-47% fewer than expected duplicates using N(1-(1-1/M)^N-1) 
  to calculate what's expected.
    
 */

public class Ex2531RandomDuplicatesOfInts { 
  
  public static int duplicates(int[] z) {
    if (z == null || z.length < 2) return 0;
    Arrays.sort(z); int d = 0;
    for (int i = 0; i < z.length-1; i++) if (z[i] == z[i+1]) d++;
    return d;
  }
  
  public static int predict(int m, int n) {
    if (m < 1 || n < 1) throw new IllegalArgumentException(
        "predict: m and n must both be > 0");
    // n(1-(1-1/m)^n-1)
    // return (int)Math.ceil((n*(1-Math.pow(Math.E, -1.*n/m))));
    return (int)Math.ceil(1.*n*(1.-Math.pow(1.-(1./m), n-1)));
  }
  
  public static int testPRNG(int m, int n, int t) {
    if (m < 1 || n < 1 || t < 1) throw new IllegalArgumentException(
        "testPRNG: m, n and t must all be > 0");
    Random r;
    int[] z = new int[n]; int[] y = new int[t];
    for (int i = 0; i < t; i++) {
      r = new Random(System.currentTimeMillis());
      for (int j = 0; j < n; j++) z[j] = r.nextInt(m);
      y[i] = duplicates(z); 
    }
    return (int)Math.round(mean(y));  
  }
  
  public static int testSRNG(int m, int n, int t) {
    if (m < 1 || n < 1 || t < 1) throw new IllegalArgumentException(
        "testSRNG: m, n and t must all be > 0");
    SecureRandom r = new SecureRandom();
    int[] z = new int[n]; int[] y = new int[t];
    for (int i = 0; i < t; i++) {
      for (int j = 0; j < n; j++) z[j] = r.nextInt(m);
      y[i] = duplicates(z); 
    }
    return (int)Math.round(mean(y));  
  }
  
  public static int testSecureRandomStrong(int m, int n, int t) {
    if (m < 1 || n < 1 || t < 1) throw new IllegalArgumentException(
        "testSRNG: m, n and t must all be > 0");
    SecureRandom r;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      System.out.println("can't create SecureRandom strong instance");
      return -1;
    }
    int[] z = new int[n]; int[] y = new int[t];
    for (int i = 0; i < t; i++) {
      for (int j = 0; j < n; j++) z[j] = r.nextInt(m);
      y[i] = duplicates(z); 
    }
    return (int)Math.round(mean(y));  
  }
  
  public static void testPRNG(int t) {
    int[] na = {1000,10000,100000,1000000}; 
    int[] ma = new int[3]; int d, p; double e;
    System.out.println("PRNG duplicates vs. predicted "+t+" trials");
    System.out.println("N        M        duplicates  predicted  %difference");
    for (int i = 0; i < na.length; i++) {
      ma[0] = na[i]/2;  ma[1] = na[i]; ma[2] = 2*na[i];
      for (int j = 0; j < ma.length; j++) {
        d = testPRNG(ma[j], na[i], t);
        p = predict(ma[j], na[i]);
        e = (100.*d-p)/p - 100.;     
        System.out.printf("%-7d  %-7d  %-7d     %-7d    %+5.3f\n", na[i], ma[j], d, p, e);    
      }
    }
    System.out.println();
  }
  
  public static void testSRNG(int t) {
    int[] na = {1000,10000,100000,1000000}; 
    int[] ma = new int[3]; int d, p; double e;
    System.out.println("SRNG duplicates vs. predicted "+t+" trials");
    System.out.println("N        M        duplicates  predicted  %difference");
    for (int i = 0; i < na.length; i++) {
      ma[0] = na[i]/2;  ma[1] = na[i]; ma[2] = 2*na[i];
      for (int j = 0; j < ma.length; j++) {
        d = testSRNG(ma[j], na[i], t);
        p = predict(ma[j], na[i]);
        e = (100.*d-p)/p - 100.;     
        System.out.printf("%-7d  %-7d  %-7d     %-7d    %+5.3f\n", na[i], ma[j], d, p, e);    
      }
    }
    System.out.println();
  }
  
  public static void testSecureRandomStrong(int t) {
    int[] na = {1000,10000,100000,1000000}; 
    int[] ma = new int[3]; int d, p; double e;
    System.out.println("SecureRandomStrong duplicates vs. predicted "+t+" trials");
    System.out.println("N        M        duplicates  predicted  %difference");
    for (int i = 0; i < na.length; i++) {
      ma[0] = na[i]/2;  ma[1] = na[i]; ma[2] = 2*na[i];
      for (int j = 0; j < ma.length; j++) {
        d = testSecureRandomStrong(ma[j], na[i], t);
        if (d == -1) return;
        p = predict(ma[j], na[i]);
        e = (100.*d-p)/p - 100.;     
        System.out.printf("%-7d  %-7d  %-7d     %-7d    %+5.3f\n", na[i], ma[j], d, p, e);    
      }
    }
    System.out.println();
  }

  public static void main(String[] args) {
    
    testPRNG(10);
    testSRNG(10);
    testSecureRandomStrong(10);
   
    /* PRNG duplicates vs. predicted 10 trials
    N        M        duplicates  predicted  %difference
    1000     500      563         865        -35.913
    1000     1000     375         632        -41.665
    1000     2000     218         394        -45.670
    10000    5000     5667        8647       -35.463
    10000    10000    3693        6322       -42.585
    10000    20000    2142        3935       -46.565
    100000   50000    56767       86467      -35.348
    100000   100000   36739       63212      -42.880
    100000   200000   21280       39347      -46.917
    1000000  500000   567597      864665     -35.356
    1000000  1000000  367848      632121     -42.807
    1000000  2000000  213028      393470     -46.859
    
    SRNG duplicates vs. predicted 10 trials
    N        M        duplicates  predicted  %difference
    1000     500      568         865        -35.335
    1000     1000     371         632        -42.297
    1000     2000     209         394        -47.954
    10000    5000     5679        8647       -35.324
    10000    10000    3683        6322       -42.743
    10000    20000    2144        3935       -46.515
    100000   50000    56743       86467      -35.376
    100000   100000   36803       63212      -42.778
    100000   200000   21313       39347      -46.833
    1000000  500000   567660      864665     -35.349
    1000000  1000000  367754      632121     -42.822
    1000000  2000000  213215      393470     -46.812
    
    SecureRandomStrong duplicates vs. predicted 10 trials
    N        M        duplicates  predicted  %difference
    1000     500      571         865        -34.988
    1000     1000     368         632        -42.772
    1000     2000     215         394        -46.431
    10000    5000     5680        8647       -35.312
    ...*/    
  }

}


