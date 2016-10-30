package ex22;

import static v.ArrayUtils.*;
import static java.lang.reflect.Array.newInstance;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Ex2227MergeSortSubarrayLengths {

  /* p287
  2.2.27 Subarray lengths. Run mergesort for large random arrays, and make an 
  empirical determination of the average length of the other subarray when the 
  first subarray exhausts, as a function of N (the sum of the two subarray sizes 
  for a given merge).
 
  For array length 1M I got 0.486 for random Doubles and 0.419 for random Integers
  both with all distinct elements.  The tests are included below.
  
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
 
  public static <T extends Comparable<? super T>> double topDown(T[] z) {
    // in-place topDown mergesort. returns the avg other subarray length
    // when first exhausts divided by N.
    if (z == null) return 0;
    int N = z.length; if (N < 2) return 0;
    double[] lens = {0}; // accumulator for remaining subarray lengths/N
    int[] c = {0}; // counter for number of additions to lens[0]
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      boolean lex = false; boolean rex = false;
      int i = lo, j = mid+1;
      for (int k = lo; k <= hi; k++) 
        b[k] = a[k];
      for (int k = lo; k <= hi; k++) {
        if (i > mid) {
          // exhaustion of left subarray
          if (lex == false) { lens[0]+=((1.*(hi-j+1))/(hi-lo+1)); c[0]++; lex = true; }
          a[k] = b[j++];
        }
        else if (j > hi ) {
          // exhaustion of right subarray
          if (rex == false) { lens[0]+=((1.*(i-lo+1))/(hi-lo+1)); c[0]++; rex = true; }
          a[k] = b[i++];
        }
        else if (b[j].compareTo(b[i]) < 0) a[k] = b[j++];
        else a[k] = b[i++];
      }
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) return;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      merge.accept(a, b, lo, mid, hi); 
    });
    sort.accept(z, aux, 0, N - 1);
    System.out.println(lens[0]);
    System.out.println(c[0]);
    if (lens[0] == 0) return 0;
    else return (1.*lens[0]/c[0]);
  }
  
  public static void main(String[] args) throws NoSuchAlgorithmException  {
    
  Random r = SecureRandom.getInstanceStrong();
  Integer[] a; Double[] b; int n = 1000000;

  a = rangeInteger(0,n);
  shuffle(a,r);
  System.out.println(topDown(a));
  // n = 100000,  0.4184018460255364, 0.4175313779449561
  // n = 1000000, 0.419442363131896,  0.4193898993479125 
  b = new Double[n];
  for (int i = 0; i < n; i++) b[i] = r.nextDouble();
  System.out.println(topDown(a));
  // n = 100000,  0.44580680974267656, 0.44580680974267656
  // n = 1000000, 0.48640742022413563, 0.48640742022413563
  int dups = 0;
  for (int i = 0; i < n-1; i++) 
    if (b[i].doubleValue() == b[i+1].doubleValue()) dups++;
  System.out.println("dups="+dups); 
  // n = 100000,  0
  // n = 1000000, 0
  System.out.println("dups%="+((100.*dups)/n));
  // n = 100000,  0.0
  // n = 1000000, 0.0

  }
  
}

