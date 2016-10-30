package ex23;

import static sort.Quicks.quickIntRd;
import static sort.Quicks.quickDblRd;
import static analysis.Log.lg;
import static analysis.Log.log;
import static sort.Quicks.bestDbl;
import static sort.Quicks.bestInt;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@SuppressWarnings("unused")
public class Ex2313QuickSortRecursiveDepth {

  /* p304  
  2.3.13  What is the recursive depth of quicksort, in the best, worst, and average cases?
  This is the size of the stack that the system needs to keep track of the recursive calls. See
  Exercise 2.3.20 for a way to guarantee that the recursive depth is logarithmic in the
  worst case.
  
  Worst case recursion depth is N-1, where N is the length of the array.
  Best case is lg(N).
  For random data the average case is log(1000,base 4/3).
  Ref: https://en.wikipedia.org/wiki/Quicksort
       http://www.iti.fh-flensburg.de/lang/algorithmen/sortieren/quick/quicken.htm
       
  Several examples are provided below.
  
  */ 
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    Random r2 = SecureRandom.getInstanceStrong();
    
    /* quickDblR and quickIntR implement algo2.5 without shuffling and are 
       instrumented to increment rec[0] on entry to sort(a,lo,hi) and decrement 
       it on exit from that method. just after rec[0] is incremented, if it's 
       greater than max[0] then the latter is set to rec[0]. after sorting is 
       done max[0] is returned. max therefore measures the max depth of the 
       stack excluding calls to partition() given that exch(O) and less() are 
       inlined. bestInt(int[]) and bestDbl(double[]) take an int[] or double[] 
       and should convert it into a best case for quicksort and are based on 
       http://algs4.cs.princeton.edu/23quicksort/QuickBest.java.html. */
    
    // best case
    par(quickDblRd(bestDbl(range(1.,1001.)))); // 9; lg(1000) == 9.966
    
    // worst cases
    par(quickDblRd(range(1.,1001.))); // 999
    par(quickDblRd(range(1000.,0.))); // 999

    // average case
    par(quickDblRd(r2.doubles(10000).toArray())); // 29,28,35,31; log(10000,4./3) == 32.016
   
    // odd cases
    par(quickIntRd(bestInt(range(1,1001)))); // 511; this is odd and should be a best case
    par(quickIntRd(fillInt(1000, ()->5))); // 511; this is odd and should be a worst case  
    par(quickDblRd(fillDouble(1000, ()->5.))); // 9; this is odd and should be a worst case 

  }

}

