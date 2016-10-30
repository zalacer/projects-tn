package ex22;

//import static v.ArrayUtils.*;
import static java.lang.reflect.Array.newInstance;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Ex2224SortTestImprovement {

  /* p287
  2.2.24 Sort-test improvement. Run empirical studies for large randomly ordered 
  arrays to study the effectiveness of the modification described in Exercise 2.2.8 
  for random data. In particular, develop a hypothesis about the average number of 
  times the test (whether an array is sorted) succeeds, as a function of N (the 
  original array size for the sort).
  
  My hypotheses is that the test succeeds 2.93±0.01% of N for large N based on results
  below. Best cases are when N == 2^p and worst cases are when it's prime, but it 
  doesn't matter when N is 1M and over.
 
   */ 

  @FunctionalInterface
  public static interface Consumer4<A,B,C,D> {
    void accept(A a, B b, C c, D d);
  }

  @FunctionalInterface
  public static interface Consumer5<A,B,C,D,E> {
    void accept(A a, B b, C c, D d, E e);
  }
  
  @FunctionalInterface
  public interface RecursiveConsumer4<A,B,C,D> {
    void accept(final A a,final B b,final C c,final D d,final Consumer4<A,B,C,D> self);
  }
  
  //based on: https://github.com/claudemartin/Recursive/blob/master/Recursive/src/ch/claude_martin/recursive/Recursive.java
  public static class Recursive<F> {
    private F f;
    public static <A,B,C,D> Consumer4<A,B,C,D> consumer4(RecursiveConsumer4<A,B,C,D> f) {
      final Recursive<Consumer4<A,B,C,D>> r = new Recursive<>();
      return r.f = (a,b,c,d) -> f.accept(a,b,c,d,r.f);
    }
  }
  
  public static <T extends Comparable<? super T>> long topDownSm(T[] z) {
    // in-place topDown mergesort that skips merge when subarrays are in order
    // returns the total number compares made in all merges.
    if (z == null) return 1;
    int N = z.length; if (N < 2) return 2;
    long[] succeeds = new long[1];
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      for (int k = lo; k <= hi; k++) b[k] = a[k];
      for (int k = lo; k <= hi; k++) 
        if (i > mid) a[k] = b[j++];
        else if (j > hi ) a[k] = b[i++];
        else if (b[j].compareTo(b[i]) < 0) a[k] = b[j++];
        else a[k] = b[i++];
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      // skip merge when subarrays are in order
      if(a[mid].compareTo(a[mid+1])>0) merge.accept(a, b, lo, mid, hi);
      else succeeds[0]++;
    });
    sort.accept(z, aux, 0, N - 1);
    return succeeds[0];
  }
  
  public static void succeeds() throws NoSuchAlgorithmException {
    Random r = SecureRandom.getInstanceStrong();
    Double[] d; double data, mean;
//  int[] len = {16, 128, 1024, 16384, 131072, 1048576}; // powers of 2
//  String[] lens = {"2^4", "2^7", "2^10", "2^14", "2^17", "2^20"};
//    int[] len = {10, 100, 1000, 10000, 100000, 1000000}; // powers of 9
//    String[] lens = {"10", "100", "1K", "10K", "100K", "1M"};
    int[] len = {11, 101, 1009, 10007, 100003, 1000003}; // primes
    String[] lens = {"11", "101", "1009", "10007", "100003", "1000003"};

    System.out.println("    N            succeeds/N ");
    for (int k = 0; k < len.length; k++) {
      data = 0;
      d = new Double[len[k]]; 
      int trials = len[k] <= 1000 ? 500 : len[k] <= 10000 ? 100 : len[k] <= 100000 ? 20 : 4;
      for (int i = 0; i < trials; i++) {   
        for (int j = 0; j < len[k]; j++) d[j] = r.nextDouble();
        data += topDownSm(d);
      }
      mean = (data/trials)/len[k];
      System.out.printf("    %-6s       %10.3f\n", lens[k], mean);
    }
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException {
    
    succeeds();
    
/*  all lengths a power of 2
    N            succeeds/N 
    2^4               0.291
    2^7               0.293
    2^10              0.293
    2^14              0.294
    2^17              0.293
    2^20              0.293

    all lengths a power of 2
    N            succeeds/N 
    2^4               0.298
    2^7               0.294
    2^10              0.292
    2^14              0.294
    2^17              0.294
    2^20              0.294

    all lengths a power of 10
    N            succeeds/N 
    10                0.288
    100               0.286
    1K                0.293
    10K               0.288
    100K              0.286
    1M                0.292
    
    all lengths prime
    N            succeeds/N 
    11                0.287
    101               0.288
    1009              0.294
    10007             0.287
    100003            0.287
    1000003           0.292
    
 
*/
    
  }
  
}

