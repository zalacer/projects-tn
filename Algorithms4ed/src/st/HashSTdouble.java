package st;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import ds.Queue;
import exceptions.LinearProbingHashConfigurationException;

// based on st.LinearProbingHashST for ex3504

public class HashSTdouble {
  private static final int INIT_CAPACITY = 4;
  private static final int SEARCHHITSAMPLESIZE = 503;

  private int n;           // number of key-value pairs in the symbol table
  private int m;           // size of linear probing table
  private double[] keys;
  private double[] vals;
  private double NULL = Double.NEGATIVE_INFINITY;
  private int probes = 0;
  private int ss = SEARCHHITSAMPLESIZE;

  public HashSTdouble() {this(INIT_CAPACITY); }

  public HashSTdouble(int capacity) {
    m = capacity;
    n = 0;
    keys = fillDouble(m, ()->NULL);
    vals = fillDouble(m, ()->NULL);
  }
  
  public double getNULL() { return NULL; }
  
  public void setNULL(double x) { NULL = x; }
  
  public int probes() { return probes; }
  
  public void zeroProbes() {probes = 0; }
  
  public int getM() { return m; }
  
  public double alpha() { return new Double(String.format("%1.4f", 1.*n/m)); }

  public int size() { return n; }

  public boolean isEmpty() { return size() == 0; }

  public boolean contains(double key) { return get(key) != NULL; }

  private int hash(double key) { return (new Double(key).hashCode() & 0x7fffffff) % m; }

  private void resize(int capacity) {
    HashSTdouble temp = new HashSTdouble(capacity);
    for (int i = 0; i < m; i++)
      if (keys[i] != NULL) temp.put(keys[i], vals[i]);
    keys = temp.keys;
    vals = temp.vals;
    m    = temp.m;
  }

  public void put(double key, double val) {
    if (key == NULL) throw new IllegalArgumentException("first argument to put() is NULL");
    if (val == NULL) { delete(key); return; }
    if (n >= m/2) resize(2*m);
    int i;
    for (i = hash(key); keys[i] != NULL; i = (i + 1) % m)
      if (keys[i] == key) { vals[i] = val; return; }
    keys[i] = key;
    vals[i] = val;
    n++;
//    System.out.println("end put");
  }

  public double get(double key) {
    if (key == NULL) throw new IllegalArgumentException("argument to get() is NULL");
    for (int i = hash(key); keys[i] != NULL; i = (i + 1) % m) {
      if (keys[i] == key) {
        probes++;
        return vals[i];
      }
      probes++;
    }
    probes++;
    return NULL;
  }

  public void delete(double key) {
    if (key == NULL) throw new IllegalArgumentException("argument to delete() is NULL");
    if (!contains(key)) return;
    int i = hash(key);
    while (key != keys[i]) i = (i + 1) % m;
    keys[i] = NULL;
    vals[i] = NULL;
    i = (i + 1) % m;
    while (keys[i] != NULL) {
      double keyToRehash = keys[i];
      double valToRehash = vals[i];
      keys[i] = NULL;
      vals[i] = NULL;
      n--;
      put(keyToRehash, valToRehash);
      i = (i + 1) % m;
    }
    n--;
    if (n > 0 && n <= m/8) resize(m/2);
    assert check();
  }

  public Iterable<Double> keys() {
    Queue<Double> queue = new Queue<>();
    for (int i = 0; i < m; i++)
      if (keys[i] != NULL) queue.enqueue(keys[i]);
    return queue;
  }

  private boolean check() {
    if (m < 2*n) {
      System.err.println("Hash table size m = " + m + "; array size n = " + n);
      return false;
    }
    for (int i = 0; i < m; i++) {
      if (keys[i] == NULL) continue;
      else if (get(keys[i]) != vals[i]) {
        System.err.println("get[" + keys[i] + "] = "
            + get(keys[i]) + "; vals[i] = " + vals[i]);
        return false;
      }
    }
    return true;
  }
  
  public double searchHitAvgCost() {
    // calculate and return the average search hit by sampling.
    if (m == 0 || n == 0 || keys == null || keys.length == 0) return -1;
    if (m < n) throw new LinearProbingHashConfigurationException("m < n");
    int i = 0, j = 0; double sum = 0;
    if (n <= ss) {
      while (i < m && j < n) {
        if (keys[i] != NULL) {
          zeroProbes();
          get(keys[i]);
          sum += probes();
          j++;
        }
        i++;        
      }
      return sum/j;
    } else {
      // reservoir sampling
      SecureRandom r = new SecureRandom();
      r.setSeed(System.currentTimeMillis());
      // fill the reservoir
      int[] a = new int[ss];  i = 0; 
      while (i < m && j < ss) {
        if (keys[i] != NULL) {
          a[j++] = i;
        }
        i++;
      }
      // update the reservoir
      while (i < m  && j < n) {
        int c = r.nextInt(Integer.MAX_VALUE) % (j+1);
        if (keys[i] != NULL) {
          if (c < ss) a[c] = i;
          j++;
        }
        i++;
      }
      // use reservoir elements to reference keys 
      // to compute search get hit cost
      sum = 0;
      for (i = 0; i < ss; i++) {
          zeroProbes();
          get(keys[a[i]]);
          sum += probes();
      }
      return sum/ss;    
    }
  }
  
  public double searchMissAvgCost() {
    // calculate search miss average cost based on the discussion of 
    // Proposition M in the text on page 473.
    double sum = sumAsArithmeticSeries(clusterLengths());
//    for (int i : cl) sum += cl[i]*cl[i];
    return 1 + (1.*(m-n) + sum)/m;
  }

  public int[] clusterLengths() {
    // return an array containing the nonzero cluster lengths in z
    // where clusters are contiguous elements separated by nulls.
    if (keys == null) throw new IllegalArgumentException("clusterLengths: keys is null");
    int i = 0, c = 0; Queue<Integer> q = new Queue<>();
    while (i < keys.length) {
      if (keys[i] ==  NULL) {
        if (c != 0) { q.enqueue(c); c = 0; }
      } else c++;
      i++;
    }
    if (c != 0) q.enqueue(c);
    return (int[]) unbox(q.toArray(1));
  }
  
  public static double sumAsArithmeticSeries(int[] a) {
    // return the sum of the elements in a where each represents
    // an arithmetic series, i.e. 3 represents {1+2+3}
    if (a == null) throw new IllegalArgumentException("sumofArithmeticSeries: arg is null");
    double sum = 0;
    for (int i : a) sum += (1.*i*(i+1))/2;
    return sum;
  }
  public static void main(String[] args) { 
    
    SecureRandom r = new SecureRandom();
    HashSTdouble h = new HashSTdouble();
    
    int c = 0;
    while (c < 1000) {
      h.put(r.nextDouble()*1000, r.nextDouble()*1000); 
      c++;
    }
    
    System.out.println("table size = "+h.getM());
    System.out.println("number of keys = "+h.size());
    System.out.println("alpha = "+h.alpha());
    System.out.println("searchHitAvgCost = "+h.searchHitAvgCost());
    System.out.println("searchMissAvgCost = "+h.searchMissAvgCost());

   }
}
