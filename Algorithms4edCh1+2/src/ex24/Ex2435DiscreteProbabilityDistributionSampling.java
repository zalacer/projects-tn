package ex24;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.par;
import static v.ArrayUtils.range;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import exceptions.InvalidDataException;

/* p334
  2.4.35 Sampling from a discrete probability distribution. Write a class 
  Sample with a constructor that takes an array p[] of double values as 
  argument and supports the following two operations: 
  1. random() - return an index  i with probability p[i]/T (where T is the 
     sum of the numbers in  p[].
  2. change(i, v) - change the value of p[i] to  v.

  Hint: Use a complete binary tree where each node has implied weight  p[i]. 
  Store in each node the cumulative weight of all the nodes in its subtree. 
  To generate a random index, pick a random number between  0 and  T and use 
  the cumulative weights to determine which branch of the subtree to explore. 
  When updating p[i], change all of the weights of the nodes on the path 
  from the root to i. Avoid explicit pointers, as we do for heaps.

 */

public class Ex2435DiscreteProbabilityDistributionSampling {

  public static class Sample {
    /* this class had a  heap in it but that became superfluous and was removed. 
       This class is essentially the same as DiscreteSample without using an 
       explicit index array. For explanatory comments on the code see the latter.
   */
     
    private double[] z; // the input array
    private double[] sums; // increasing partial sums of input data

    @SafeVarargs
    Sample(double...y) {
      if (y == null || y.length == 0) 
        throw new IllegalArgumentException("no input data provided");
      z = y;
      sums = new double[z.length]; double sum = 0;      
      for (int i = 0; i < z.length; ++i) {
        sum+=z[i]; sums[i] = sum;
      }
    }
    
    public int random() {
      Random r = null;
      try {
        r = SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      double v = r.nextDouble()*sums[sums.length-1];
      int index = ceiling(sums, v, 0, sums.length - 1);
      return index;
    }
    
    public void change(int c, double x) {
      if (c < 0 || c > z.length-1) {
        System.err.println("invalid index: valid indices are in 0-"+(z.length-1));
        return;
      }
      z[c] = x; double sum = c == 0 ? 0 : sums[c-1];
      for (int i = c; i < z.length; ++i) {
        sum+=z[i]; sums[i] = sum;
      }
    }
    
    private static int ceiling(double y[], double r, int lo, int hi) {
      int mid;
      while (lo < hi) { 
        mid = (lo + hi)/2;
        if (r > y[mid]) lo = mid + 1;
        else hi = mid;
      }
      return (y[lo] >= r) ? lo : -1;
    }
    
    public void printSums() {
      par(sums);
    }

  }

  public static class DiscreteSample {
    // this class takes a double[] of frequencies corresponding to its 
    // indices, its random() method returns a random index based the
    // frequencies and its change() method allows changing the frequency
    // of an index.
    private int[] indices;
    private double[] freq;
    private double[] sums;
    private int n;

    @SafeVarargs
    DiscreteSample(double...z) {
      if (z == null || z.length == 0) throw new IllegalArgumentException(
          "no input data provided");
      indices = range(0, z.length);
      freq = z; n = z.length;
      // create and fill the sums array which contains the successive 
      // partial sums of the frequencies in freq[].
      sums = new double[n];
      sums[0] = freq[0];
      for (int i = 1; i < n; ++i) sums[i] = sums[i-1] + freq[i];
    }

    public static int ceiling(double y[], double r, int lo, int hi) {
      // return the ceiling of r in y where ceiling means the
      // least element in it >= r or the last element if all are <r.
      // y must be sorted in ascending order.
      int mid;
      while (lo < hi) { 
        mid = (lo + hi)/2;
        if (r > y[mid]) lo = mid + 1;
        else hi = mid;
      }
      return (y[lo] >= r) ? lo : -1;
    }

    public int random() {
      // return a random index from indices according to its values
      // regarded as frequencies.
      if (indices == null || freq == null || indices.length != freq.length)
        throw new InvalidDataException("indices or freq cannot be null "
            + "and must have the same length");
      // sums[n-1] is sum of all frequencies. generate a random number
      // from 1 to this sum.
      Random r = null;
      try {
        r = SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());
      double v = r.nextDouble()*sums[n-1];
      // find index of ceiling of r in the sums array. since the frequency values
      // are doubles, 1 isn't added to the value of index as in SampleX.
      int index = ceiling(sums, v, 0, n - 1);
      return indices[index];
    }

    public  void change(int c, double v) {
      if (c < 0 || c > indices.length-1) {
        System.err.println("index "+c+"is out of range-no change made");
        return;
      }
      freq[c] = v; double sum = c == 0 ? 0 : sums[c-1];
      for (int i = c; i < n; ++i) {
        sum+=freq[i]; sums[i] = sum;
      }
    }
    
    public void printSums() {
      par(sums);
    }
  }

  public static class SampleX<T extends Comparable<? super T>> {
    // this class takes raw data of type T, determines the frequencies 
    // of each value and selects a random value based on that.
    // using a hashmap for variety but it has a technical use too.
    private Map<T,Long> map; // map of data points to their counts used as frequencies
    private T[] unique;      // sorted array of unique data points
    private long[] freq;     // array of data frequencies (counts) in same order as unique
    private Class<?> tclass; // actual class of T if needed

    SampleX() { map = new HashMap<>(); }

    @SafeVarargs
    SampleX(T...z) {
      if (z == null || z.length == 0) throw new IllegalArgumentException(
          "no input data provided");
      tclass = z.getClass().getComponentType();
      map = new HashMap<>(); boolean added = false;
      for (int i = 0; i < z.length; i++)
        if (z[i] == null) continue; 
        else {
          map.merge(z[i], 1L, Long::sum); added = true;
          if (tclass == null) tclass = z[i].getClass();
        }
      if (added) build();
    }

    public void add(T t) {
      if (t == null) {System.out.println("null not allowed as data"); return; }
      map.merge(t, 1L, Long::sum);
      if (tclass == null) tclass = t.getClass();
      build();
    }

    @SafeVarargs
    public final void bulkAdd(T...y) {
      if (y == null || y.length == 0) { 
        System.out.println("null not allowed as data"); return; }
      boolean added = false;
      for (int i = 0; i < y.length; i++)
        if (y[i] == null) continue; 
        else {
          map.merge(y[i], 1L, Long::sum);
          if (tclass == null) tclass = y[i].getClass();
          added = true;
        }
      if (added) build();
    }

    private final void build() {
      unique = map.keySet().toArray(ofDim(tclass,0));
      freq = new long[unique.length];
      for (int i = 0; i < unique.length; i++) freq[i] = map.get(unique[i]);
    }

    public static int ceiling(long y[], long r, int lo, int hi) {
      // return the ceiling of r in y where ceiling means the least
      // element in it >= r or the last element if all are <r. 
      // y must be sorted in ascening order.
      int mid;
      while (lo < hi) { 
        mid = (lo + hi)/2;
        if (r > y[mid]) lo = mid + 1;
        else hi = mid;
      }
      return (y[lo] >= r) ? lo : -1;
    }

    public T random() {
      // return a random element from z[] according the frequencies in freq[].
      // the efficiency of this can be improved regarding building the sums array
      if (map.size() == 0) { System.out.println("no data to show"); return null; }
      if (unique == null || freq == null || unique.length != freq.length) build();
      // create and fill the sums array which contains the successive partial
      // sums of the frequencies in freq[].
      int n = unique.length;
      long[] sums = new long[n];
      sums[0] = freq[0];
      for (int i = 1; i < n; ++i) sums[i] = sums[i-1] + freq[i];
      // sums[n-1] is sum of all frequencies. generate a random number
      // from 1 to this sum.
      Random r = null;
      try {
        r = SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());

      long v = (long)(r.nextDouble()*sums[n-1]) + 1;

      // find index of ceiling of r in the sums array
      int index = ceiling(sums, v, 0, n - 1);
      return unique[index];
    }

    public void clear() { map.clear(); unique = null; freq = null; }

    public void remove(int d) {
      if (d < 1) return;
      if (map.size() == 0) { System.out.println("nothing to remove"); return; }
      if (unique == null) build();
      int c = 0; boolean removed, someremoved = false;
      while (true) {
        removed = false;
        for (int i = 0; i < unique.length; ++i) {
          if (map.get(unique[i])>0) {
            map.merge(unique[i], -1L, Long::sum);
            removed = true;
            someremoved = true;
          }
          if (++c == d) break;
        }
        if (removed == false) {
          System.out.println(c+" keys removed; nothing else to remove");
          break;
        }
      }
      if (someremoved) build();
    }

    public void remove(T key, int d) {
      if (d < 1) return;
      if (map.size() == 0) { System.out.println("nothing to remove"); return; }
      if (unique == null) build();
      int c = 0; boolean someremoved = false;
      LOOP:
        while (true) {
          for (int i = 0; i < d; ++i) {
            if (map.get(unique[i])>0) {
              map.merge(unique[i], -1L, Long::sum);
              c++; someremoved = true;
              if (c == d) break LOOP;
            } else break LOOP;
          }
        }
      if (c < d)  { 
        System.out.println(
            "there were only "+c+" instances of "+key+" to remove");
      }
      if (someremoved) build();
    }

    public void showMap() {
      System.out.println(map);
    }

    public void showUnique() {
      if (map.size() == 0) { System.out.println("unique no data to show"); return; }
      if (unique == null ) build();
      for (int i = 0; i < unique.length; ++i)
        System.out.print(unique[i]+" ");
      System.out.println();
    }

    public void showFreq() {
      if (map.size() == 0) { System.out.println("freq no data to show"); return; }
      if (freq == null) build();
      for (int i = 0; i < freq.length; ++i)
        System.out.print(freq[i]+" ");
      System.out.println();
    }

    public T[] uniqueArray() {
      if (map.size() == 0)  { System.out.println("no data to show"); return null; }
      if (unique == null ) build();
      return unique.clone();  
    }

    public long[] freqArray() {
      if (map.size() == 0)  { System.out.println("no data to show"); return null; }
      if (freq == null) build();
      return freq.clone();  
    }
  }

  public static void main(String[] args) {
    
    double[] a = new double[]{9.,8.,7.,8.,9.};
    
    System.out.println("testing Sample:\n");
    System.out.println("testing Sample instantiation:");
    Sample s = new Sample(a);
    System.out.println("the array of partial sums of the input array:");
    s.printSums();
        
    System.out.println("\ntesting Sample.random():");
    System.out.println(s.random());
    System.out.println(s.random()); 
    System.out.println(s.random());
    
    System.out.println("\ntesting Sample.change():");
    for (int i = 0; i < a.length; i++) {
      double save = a[i]; double w = 99999.;
      s.change(i,w);
      assert s.random() == i;
      s.change(i,save); 
    } 

    /*  
      testing Sample:
      
      testing Sample instantiation:
      the array of partial sums of the input array:
      [9.0,17.0,24.0,32.0,41.0]

      testing Sample.random():
      4
      1
      3
      
      testing Sample.change():

     */
    
    System.out.println("\ntesting DiscreteSample:\n");

    System.out.println("testing DiscreteSample instantiation:");
    DiscreteSample d = new DiscreteSample(0.,1.,2.,60.,1.);
    System.out.println("the array of partial sums of the input array:");
    d.printSums();
    
    System.out.println("\ntesting DiscreteSample.random():");
    System.out.println("random="+d.random());    
    Map<Integer,Long> map = new HashMap<>();
    System.out.println("running random() 10000 times:");
    for (int i = 0; i < 10000; i++) map.merge(d.random(), 1L, Long::sum);
    System.out.println(map);
    long min = Long.MAX_VALUE;
    for (int k : map.keySet()) if (map.get(k) < min) min = map.get(k);
    System.out.println("normalized frequencies:");
    for (int k : map.keySet())
      System.out.println(k+" : "+Math.round(((double)map.get(k))/min));

    System.out.println("\ntesting DiscreteSample.change():");
    d.change(0, 250.);
    System.out.println("changed freqency of index 0 from 0 to 250");

    System.out.println("testing random() after change():");
    System.out.println("random="+d.random());   
    map.clear();
    System.out.println("running random() 10000 times:");
    for (int i = 0; i < 10000; i++) map.merge(d.random(), 1L, Long::sum);
    System.out.println(map);
    min = Long.MAX_VALUE;
    for (int k : map.keySet()) if (map.get(k) < min) min = map.get(k);
    System.out.println("normalized frequencies:");
    for (int k : map.keySet())
      System.out.println(k+" : "+Math.round(((double)map.get(k))/min));
    /*
      testing DiscreteSample:
           
      testing DiscreteSample instantiation:
      the array of partial sums of the input array:
      [0.0,1.0,3.0,63.0,64.0]
      
      testing DiscreteSample.random():
      random=3
      running random() 10000 times:
      {1=161, 2=312, 3=9367, 4=160}
      normalized frequencies:
      1 : 1
      2 : 2
      3 : 59
      4 : 1
      
      testing DiscreteSample.change():
      changed freqency of index 0 from 0 to 250.
      random=0
      running random() 10000 times:
      {0=7965, 1=37, 2=69, 3=1899, 4=30}
      normalized frequencies:
      0 : 266
      1 : 1
      2 : 2
      3 : 63
      4 : 1
     */  

    System.out.println("\ntesting SampleX:\n");
    System.out.println("testing SampleX instantiation:");
    SampleX<Integer> sx = new SampleX<>(1,3,3,3,3,3,3,2,2,4);
    // for data points {1,2,3,4} the counts are: {1,2,6,1}.
    // Sample.random returns random results based on these counts used as frequencies.
    System.out.println("the map of data to counts");
    sx.showMap();
    System.out.println("\ntesting SampleX.random() 10000 times:");
    System.out.println(sx.random());
    Map<Integer,Long> mapx = new HashMap<>();
    for (int i = 0; i < 10000; i++) mapx.merge(sx.random(), 1L, Long::sum);
    System.out.println(mapx);
    System.out.println("normalized frequencies:");
    for (Integer i : mapx.keySet())
      System.out.println(i+" : "+Math.round((double)mapx.get(i)/mapx.get(1)));
    /*
      testing SampleX:
      
      testing SampleX instantiation:
      the map of data to counts
      {1=1, 2=2, 3=6, 4=1}
      
      testing SampleX.random() 10000 times:
      random = 3
      {1=1026, 2=1989, 3=6037, 4=948}
      normalized frequencies:
      1 : 1
      2 : 2
      3 : 6
      4 : 1

     */  

  }

}
