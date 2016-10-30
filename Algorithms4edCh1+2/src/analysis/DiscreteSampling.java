package analysis;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.range;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DiscreteSampling {

  // from ex2435
  public static class Sample {
    // this class takes a double[] of frequencies corresponding to the 
    // indices and returns a random index based on that.
    private int[] indices;
    private double[] freq;
        
    @SafeVarargs
    Sample(double...z) {
      if (z == null || z.length == 0) throw new IllegalArgumentException(
          "no input data provided");
      indices = range(0, z.length);
      freq = z;         
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
        throw new exceptions.InvalidDataException("indices or freq cannot be null "
            + "and must have the same length");
      // create and fill the sums array which contains the successive partial
      // sums of the frequencies in freq[].
      int n = indices.length;
      double[] sums = new double[n];
      sums[0] = freq[0];
      for (int i = 1; i < n; ++i) sums[i] = sums[i-1] + freq[i];
      // sums[n-1] is sum of all frequencies. generate a random number
      // from 1 to this sum.
      Random r = null;
      try {
        r = SecureRandom.getInstanceStrong();
      } catch (NoSuchAlgorithmException e) {}
      if (r == null) r = new Random(System.currentTimeMillis());

      // since the frequency values are doubles, 1 isn't added to the value 
      // of v as in SampleX.
      double v = r.nextDouble()*sums[n-1];
//      if (v > indices.length) return indices[indices.length-1];
      // find index of ceiling of r in the sums array. since the frequency values
      // are doubles, 1 isn't added to the value of index as in SampleX.
      int index = ceiling(sums, v, 0, n - 1);
      return indices[index];
    }
    
    public  void change(int i, double v) {
      if (i < 0 || i > indices.length-1) {
        System.err.println("index "+i+"is out of range-no change made");
        return;
      }
      freq[i] = v;
    }   
  }
  
  // from ex2435
  public static class SampleX<T extends Comparable<? super T>> {
    // this class takes raw data of type T, determines the frequencies 
    // of each value and selects a random value based on that.
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
      // element in it >= r. y must be sorted in ascening order.
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
      
      System.out.println("testing Sample:\n");
      
      // testing Sample instantiation
      Sample s = new Sample(0.,1.,2.,60.,1.);
      
      // testing random()
      System.out.println("random="+s.random());    
      Map<Integer,Long> map = new HashMap<>();
      for (int i = 0; i < 10000; i++) map.merge(s.random(), 1L, Long::sum);
      System.out.println(map);
      long min = Long.MAX_VALUE;
      for (int k : map.keySet()) if (map.get(k) < min) min = map.get(k);
      System.out.println("normalized frequencies:");
      for (int k : map.keySet())
        System.out.println(k+" : "+Math.round(((double)map.get(k))/min));
      System.out.println(); 
      
      // testing change()
      s.change(0, 250.);
      System.out.println("changed freqency of index 0 from 0 to 250.");
      
      // testing random() after change()
      System.out.println("random="+s.random());   
      map.clear();
      for (int i = 0; i < 10000; i++) map.merge(s.random(), 1L, Long::sum);
      System.out.println(map);
      min = Long.MAX_VALUE;
      for (int k : map.keySet()) if (map.get(k) < min) min = map.get(k);
      System.out.println("normalized frequencies:");
      for (int k : map.keySet())
        System.out.println(k+" : "+Math.round(((double)map.get(k))/min));
  /*
      random=3
      {1=161, 2=312, 3=9367, 4=160}
      normalized frequencies:
      1 : 1
      2 : 2
      3 : 59
      4 : 1
      
      changed freqency of index 0 from 0 to 250.
      random=0
      {0=7956, 1=40, 2=59, 3=1905, 4=40}
      normalized frequencies:
      0 : 199
      1 : 1
      2 : 1
      3 : 48
      4 : 1
  */  
      
      System.out.println("\ntesting SampleX:\n");
      
      SampleX<Integer> sx = new SampleX<>(1,3,3,3,3,3,3,2,2,4);
      // for data points {1,2,3,4} the counts are: {1,2,6,1}.
      // Sample.random returns random results based on these counts used as frequencies.
      System.out.println(sx.random());
      Map<Integer,Long> mapx = new HashMap<>();
      for (int i = 0; i < 10000; i++) mapx.merge(sx.random(), 1L, Long::sum);
      System.out.println(mapx);
      System.out.println("normalized frequencies:");
      for (Integer i : mapx.keySet())
        System.out.println(i+" : "+Math.round((double)mapx.get(i)/mapx.get(1)));
  /*
      random = 3
      {1=1026, 2=1989, 3=6037, 4=948}
      1 : 1
      2 : 2
      3 : 6
      4 : 1
  
  */  
          
  }
    
}
