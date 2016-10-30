package ex23;

import static sort.Quicks.best;
import static sort.Quicks.bestDbl;
import static sort.Quicks.bestInt;
import static sort.Quicks.quickDblRd;
import static sort.Quicks.quickIntRd;
import static sort.Quicks.quickRd;
import static v.ArrayUtils.par;
import static utils.RandomUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Ex2316MethodToProduceBestCaseArray4QuickSort {

  /* p305  
  2.3.16 Best case. Write a program that produces a best-case array (with no 
  duplicates) for  sort() in Algorithm 2.5: an array of N items with distinct 
  keys having the property that every partition will produce subarrays that differ 
  in size by at most 1 (the same subarray sizes that would happen for an array of 
  N equal keys). (For the purposes of this exercise, ignore the initial shuffle.)
  
  See http://algs4.cs.princeton.edu/23quicksort/QuickBest.java. I wrote several methods
  to extend this. Demos of some of them are below. They sort a copy of the input array, 
  return the best version of that and leave the input array unmodified. Also there are 
  versions of each that return only the best initial subarray of a given length of the 
  input array after sorting it and they have an extra int arg to specify the length of 
  the subarray.

  */ 
  
  public static void main(String[] args) throws NoSuchAlgorithmException  {

    Random r = SecureRandom.getInstanceStrong();
    
    String[] s = "BWqkGSlPDIKsQZHUuXNEiRVMyaxLhmtoAvcFTbpCJdjnwzreOfgY".split("");
    par(best(s,15)); // [H,A,C,B,F,E,G,D,L,I,K,J,N,M,O] agrees with QuickBest.java
    par(quickRd(s)); // [51] high recursive depth; best case should be lg(15)=3.9
                    // quickR does no shuffle
    
    double[] d = r.doubles(577).toArray();
    double[] dbest = bestDbl(d);
    par(quickDblRd(dbest)); // [9]; best case recursive depth
    
    int[] i = r.ints(577).toArray();
    int[] ibest = bestInt(i);
    par(quickIntRd(ibest)); // [321]; 
    
    s = randomString(577).split("");
    String[] sbest = best(s);
    par(quickRd(sbest)); // [351]
    
  }

}

