package ex22;

import static v.ArrayUtils.*;

import static java.lang.reflect.Array.newInstance;

public class Ex2208NumberOfComparesWhenSkippingMergeUnlessNeeded {

/* p284
  2.2.8  Suppose that Algorithm 2.4 is modified to skip the call on merge() whenever
  a[mid] <= a[mid+1]. Prove that the number of compares used to mergesort a sorted
  array is linear.
  
  After the modification, if the array is already sorted merge() won't be called at
  all and the number of compares made in it will be zero, however the recursive sort
  method is called 2N-1 times in N-1 of which a compare is used to determine if the 
  subarrays are sorted. The other N calls of sort are when it's called on individual
  elements for which hi == lo and sort returns before doing the comparison to determine
  if merge should be run. If you want to count ALL the comparisons, initially topDownSm 
  compares the input array to null and the array length to 2, then since sort is called
  2N-1 times it does 2N-1 hi/lo comparisons and calls the comparison for merge N-1
  times for a total of 3N ~ N comparisons, except when N is 0 or 1 the number of compares
  is a constant 2.
  
  Algorithms for calculating the the number of calls to sort() in topDown() and 
  topDownSm(), the number of comparisons in topDownSm.sort() to determine if merge() should 
  run or the number of calls to merge in topDown() and the total number of comparisons
  done in topDownSm() for a sorted array are implemented below.
   
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
    long[] compares = new long[1];
    compares[0]+=2;
    @SuppressWarnings("unchecked")
    T[] aux = (T[]) newInstance(z.getClass().getComponentType(),N); 
    Consumer5<T[],T[],Integer,Integer,Integer> merge = (a,b,lo,mid,hi) -> {
      int i = lo, j = mid+1;
      for (int k = lo; k <= hi; k++) {
        compares[0]++;
        b[k] = a[k];
      }
      compares[0]++;
      for (int k = lo; k <= hi; k++) {
        compares[0]++;
        if (i > mid) { a[k] = b[j++]; compares[0]++; }
        else if (j > hi ) { a[k] = b[i++]; compares[0]+=2; }
        else if (b[j].compareTo(b[i]) < 0) { a[k] = b[j++]; compares[0]+=2; }
        else { a[k] = b[i++]; compares[0]++; }
      }
    };
    Consumer4<T[],T[],Integer,Integer> sort = Recursive.consumer4((a,b,lo,hi,self) -> {
      if (hi <= lo) { compares[0]++; return; }
      compares[0]++;
      int mid = lo + (hi - lo)/2;
      self.accept(a, b, lo, mid);
      self.accept(a, b, mid+1, hi);
      // skip merge when subarrays are in order
      if(a[mid].compareTo(a[mid+1])>0) {
        compares[0]++;
        merge.accept(a, b, lo, mid, hi);
      }
      compares[0]++;
    });
    sort.accept(z, aux, 0, N - 1);
    return compares[0];
  }
  
  public static long numberOfSortCalls(int n) {
    // for topDown and topDownSm
    if (n < 2) return 0;
    long sum = n; int m = n;
    while(m > 1) { 
      if (m %2 == 1) sum++;
      sum += m/=2;
    }
    return sum;
  }
 
  public static long numberOfComparisonsForMergeInTopDownSm(int n) {
    // returns the number of comparisons in topDownSm.sort() to determine 
    // if merge should run.
    if (n < 2) return 0;
    return numberOfSortCalls(n) - n;
  }
  
  public static long numberOfComparesForTopDownSmWithSortedArray(int n) {
    // return the total number of comparisons made by topDownSm for
    // a sorted array of length n
    return numberOfSortCalls(n) + numberOfComparisonsForMergeInTopDownSm(n) + 2;
  }
  
  public static long numberOfCallsToMergeInTopDown(int n) {
    // return the number of times merge()is executed in topDown.
    return numberOfSortCalls(n) - n;
  }
  
  public static void main(String[] args) {
   
    for (int i = 1; i < 10001; i++) {
      Integer[] a = rangeInteger(0,i); // sorted array [0..i-1]
      long compares = topDownSm(a);
      assert compares == numberOfComparesForTopDownSmWithSortedArray(i);
    }
        
  }
    
}
