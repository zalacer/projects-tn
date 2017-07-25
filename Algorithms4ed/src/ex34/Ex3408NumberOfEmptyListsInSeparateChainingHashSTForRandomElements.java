package ex34;

import static java.lang.Math.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import st.SeparateChainingHashSTX;

/* p480
  3.4.8  How many empty lists do you expect to see when you insert N keys 
  into a hash table with SeparateChainingHashST, for N=10, 10^2, 10^3, 
  10^4, 10^5, and 10^6? Hint : See Exercise 2.5.31.
  
  Using SeparateChainingHashST dated Nov 2 2016 from 
    http://algs4.cs.princeton.edu/34hash/SeparateChainingHashST.java.html 
  I expect 0 or close to 0 because resizing creates a new HashST that
  hashes mod the doubled M that should disperse the existing keys relatively
  uniformly over the doubled number of lists plus the new HashST has about 
  5 times more keys than lists. Since resizing doubles M when N = 10*M , 
  after resizing 20% or more keys must be added until N gets to a power of 
  10 making empty lists even less likely to occur.
 
  More precisely, converting the prediction formula derived for Exercise 2.5.31
  to predict the number of unique buckets (i.e. lists) after hashing keys with
  0x7fffffff and for a given bound for random number generation and number of 
  keys, using bounds of various orders of magnitudes, predictUniqueBuckets() 
  predicts the number of empty lists should be zero for all required orders of 
  magnitude of N.  However, since random numbers are used and neither Java Random
  nor SecureRandom nore SecureRandom.getInstanceStrong() are perfectly uniform, 
  according on results from Exercise 2.5.31, there can be small random deviations 
  from this prediction, necessarily on the positive size, usually of a small 
  fraction of a percent of N. Experimentally this is generally confirmed by the 
  results of the tests below with exceptions including one empty list when N is 
  10 and 100 that can occur even when the random integer bound is only 10 but 
  most frequently occurs when it's a million or more. 
  
  predictUniqueBuckets() is based on use of keys that are positive integers or 0 
  for which each is its own hash that's unique over all integers so as the numbers 
  of unique such keys exceeds the number of buckets the probability of empty buckets 
  decreases and only if the former is less than the latter is there guaranteed to be 
  any empty bucket. Additionlly as the upper bound of each key increases a greater 
  fraction is more likely to be unique since they are selected from a greater range 
  implying that a relatively smaller number of keys tends to populate all buckets with 
  higher probability, but of course not necessarily. This is a definately qualitative 
  and somewhat crude analysis, but the implementation based on it appears to work in 
  practice, because the number of keys usually far exceeds the number of buckets
  when N is a power of ten.
  
*/             

public class Ex3408NumberOfEmptyListsInSeparateChainingHashSTForRandomElements {
  
  public static long predictUniqueBuckets(long m, long n) {
    // return the predicted number of unique buckets in a sequence of n
    // integers ranging from 0 to m-1 generated with random uniformity
    // after hashing with 0x7fffffff (and without modularization).
    if (m < 1 || n < 1) throw new IllegalArgumentException(
        "predictUniqueBuckets: m and n must both be > 0");
    return (long)floor(1.*m*(1.-pow(1.-(1./m),n)));
  }
    
  public static void frequencyofEmptyLists(int bound) throws NoSuchAlgorithmException {
    // print statistics on the number of empty lists in a SeparateChainingHashSTX
    // populated with random integer keys against the upper bound of those integers 
    // and the number of keys.
    // SeparateChainingHashSTX is the same as SeparateChainingHashST with the addition
    // of emptyLists() that returns the number of empty lists.
    // bound is the bound for random number generation.
    SecureRandom r = SecureRandom.getInstanceStrong();
    int n = 10, actualNumberOfLists, c = 0, actualNumberOfEmptyLists; 
    long M = (long)pow(2,31), predictUniqueBuckets, predictedNumberOfLists;
    SeparateChainingHashSTX<Integer,Integer> ht;
 
    if (bound == Integer.MAX_VALUE)
      System.out.println("using Integer.MAX_VALUE as the bound for random number generation:");
    else System.out.println("using "+bound+" as the bound for random number generation:");
         
    while (n <= (int)pow(10,6)) {
      ht = new SeparateChainingHashSTX<>();
      c = 0;
      while (c < n) {
        ht.put(r.nextInt(bound),1);
        c++;
      }
      System.out.println("\nN = "+n);
      actualNumberOfEmptyLists = ht.emptyLists();
      System.out.println("actualNumberOfEmptyLists = "+actualNumberOfEmptyLists); 
      actualNumberOfLists = ht.getM();
      System.out.println("actualNumberOfLists = "+actualNumberOfLists);
      System.out.println("%emptyLists = "+(1.*actualNumberOfEmptyLists/actualNumberOfLists));
      predictUniqueBuckets = predictUniqueBuckets(M, n);
      predictedNumberOfLists =  predictUniqueBuckets < actualNumberOfLists 
          ? actualNumberOfLists - predictUniqueBuckets : 0; 
      System.out.println("predictedNumberOfLists = "+predictedNumberOfLists);
      n *= 10;
    }
    System.out.println();
  }
  
  public static void pause(int sec) {
    try {
      Thread.sleep(sec*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    int[] a = {16, 128, 1024, 16384, 131072, 1048576, Integer.MAX_VALUE};
    for (int i : a ) { frequencyofEmptyLists(a[i]); pause(3); }
 
/*
    using 16 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using 128 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 8
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using 1024 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 64
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using 16384 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 1024
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 2048
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 2048
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using 131072 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 1
    actualNumberOfLists = 4
    %emptyLists = 0.25
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 1
    actualNumberOfLists = 16
    %emptyLists = 0.0625
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 1024
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 8192
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16384
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using 1048576 as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 1
    actualNumberOfLists = 4
    %emptyLists = 0.25
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 1024
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 38
    actualNumberOfLists = 16384
    %emptyLists = 0.0023193359375
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 65536
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    using Integer.MAX_VALUE as the bound for random number generation:
    
    N = 10
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 4
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 16
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 1000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 128
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 10000
    actualNumberOfEmptyLists = 0
    actualNumberOfLists = 1024
    %emptyLists = 0.0
    predictedNumberOfLists = 0
    
    N = 100000
    actualNumberOfEmptyLists = 36
    actualNumberOfLists = 16384
    %emptyLists = 0.002197265625
    predictedNumberOfLists = 0
    
    N = 1000000
    actualNumberOfEmptyLists = 68
    actualNumberOfLists = 131072
    %emptyLists = 5.18798828125E-4
    predictedNumberOfLists = 0
*/    
  }
 

}

