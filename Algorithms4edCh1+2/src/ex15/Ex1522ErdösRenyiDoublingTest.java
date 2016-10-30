package ex15;

import static v.ArrayUtils.*;
import static java.lang.Math.*;

import java.util.Random;

import analysis.Timer;


/*
  p239
  1.5.22 Doubling test for Erdös-Renyi model. Develop a performance-testing client that
  takes an  int value T from the command line and T trials of the following experiment: 
  Use your client from Exercise 1.5.17 to generate random connections, using UnionFind to 
  determine connectivity as in our development client, looping until all sites are connected. 
  For each  N , print the value of  N , the average number of connections processed, and the 
  ratio of the running time to the previous. Use your program to validate the hypotheses in 
  the text that the running times for quick-find and quick-union are quadratic and weighted 
  quick-union is near-linear.
 */

public class Ex1522ErdösRenyiDoublingTest {

  public static int count(int N, String u) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    Random r = new Random(10719157);
    UF uf = null;
    switch (u) {
    case "QuickFindUF": uf = new QuickFindUF(n); break;
    case "QuickUnionUF": uf = new QuickUnionUF(n); break;
    case "WeightedQuickUnionUF": uf = new WeightedQuickUnionUF(n); break;
    default: 
      throw new IllegalArgumentException("count: u unrecognized UF class name");
    }
    int p; int q; int con = 0; 
    while(true) {
      p = r.nextInt(n); q = r.nextInt(n);
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    return con; 
  }

  public static double[] countTimeTrial(int n, String u) {
    Timer t = new Timer();
    int con = count(n, u);
    double time = t.finish();
    return new double[]{con,time};
  }

  public static void countDoublingRatio(int t, String u) {
    int n = 1024;
    double[] d = new double[2];
    double[] con = new double[t];
    double[] tim = new double[t];
    int c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrial(n, u);
      con[c] = d[0]; 
      tim[c++] = d[1];
    }
    @SuppressWarnings("unused")
    double pavgcon = mean(con);
    double pavgtim = mean(tim);

    System.out.println(u+" doubling ratios");
    System.out.println("      N        con     time    time/prev");

    double avgcon = 0; double avgtim = 0;

    while (n < (int) pow(2,30)) {
      n*=2; 
      c = 0;
      for (int i = 0; i < t; i++) {
        d = countTimeTrial(n, u);
        con[c] = d[0]; tim[c++] = d[1];
      }
      avgcon = mean(con);
      avgtim = mean(tim);
      System.out.printf("  %7d   %7.0f %7.0f ", n, avgcon, avgtim);
      System.out.printf("   %5.3f\n", avgtim/pavgtim);
      pavgcon = avgcon; pavgtim = avgtim;
    }
  }

  public static interface UF {
    // interface for UF (union-find) classes
    public void union(int p, int q);
    public int find(int p);
    public boolean connected(int p, int q);
    public int count();
  }

  public static class QuickFindUF implements UF {
    private int[] id; // the ith location in id is the ith 
                      // component while it's value is a site
    // if id[i] = j then site j is in component i
    private int count; // number of components

    public QuickFindUF(int N) { 
      if (N <= 0) 
        throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
      // initialize component id array so that each site 
      // is in the component with the same number.
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) { return find(p) == find(q); }

    public int find(int p) {
      if (p < 0 || p >= id.length)
        throw new IllegalArgumentException("find: p is out of range");
      return id[p]; 
    }

    public void union(int p, int q) {
      // put p and q into the same component.
      // after all input pairs have been processed, components are identified  
      // as those indices i in id such that id[i] == i.
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) 
        throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pID = find(p);
      int qID = find(q);
      // nothing to do if p and q are already in the same component.
      if (pID == qID) return;
      // rename p’s component to q’s name.
      for (int i = 0; i < id.length; i++) if (id[i] == pID) id[i] = qID;
      count--;
    }
  }

  public static class QuickUnionUF implements UF {
    private int[] id; // the ith location in id is the ith site while it's value is a site
    // in the same component given by its root which is a site with a
    // value equal to its index in id as implemented in find, i.e.
    // if id[i] = i then i is a root == component
    private int count; // number of components

    public QuickUnionUF(int N) {
      // initialize component id array so that each site is in the component with the same name,
      // i.e. is its own root
      if (N <= 0) 
        throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) 
        throw new IllegalArgumentException(
          "connected: p and q must both be >= 0 and <= id.length");
      return find(p) == find(q); 
    }

    public int find(int p) {
      // Find the root of p, i.e. its component.
      if (p < 0 || p >= id.length) 
        throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) p = id[p];
      return p;
    }

    public void union(int p, int q) {
      // Give p and q the same root, i.e. component
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) 
        throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pRoot = find(p);
      int qRoot = find(q);
      if (pRoot == qRoot) return;
      id[pRoot] = qRoot;
      count--;
    }
  }

  public static class WeightedQuickUnionUF implements UF {
    private int[] id; // the ith location in id is the ith site while it's value is a site
    // in the same component given by its root which is a site with a
    // value equal to its index in id as implemented in find, i.e.
    // if id[i] = i then i is a root == component
    private int[] sz; // size of component for roots (site indexed)
    private int count; // number of components

    public WeightedQuickUnionUF(int N) { 
      if (N <= 0) 
        throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
      // initialize id array so that each site is in the component with the same 
      // number, i.e. each site is its own root.
      // initialize sz array so each component has size 1
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
      sz = new int[N];
      for (int i = 0; i < N; i++) sz[i] = 1;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) { return find(p) == find(q); }

    public int find(int p) {
      // Follow links to find a root
      if (p < 0 || p >= id.length) 
        throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) p = id[p];
      return p;
    }

    public void union(int p, int q) {
      // put p and q into the same component.
      // after all input pairs have been processed, components are identified  
      // as those indices i in id such that id[i] == i.
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) 
        throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int i = find(p);
      int j = find(q);
      if (i == j) return;
      // Make smaller root point to larger one.
      if (sz[i] < sz[j]) { id[i] = j; sz[j] += sz[i]; }
      else { id[j] = i; sz[i] += sz[j]; }
      count--;
    }

    public void printArrays() {
      System.out.println("sz"+arrayToString(sz,500,1,1));
      System.out.println("id"+arrayToString(id,500,1,1));
    }
  }

  public static void main(String[] args) {

    // countDoublingRatio(10,"QuickFindUF");
    //    QuickFindUF doubling ratios
    //    N        con     time    time/prev
    //   2048      2047       5    0.600
    //   4096      4095      14    3.111
    //   8192      8191      55    3.893
    //  16384     16383     232    4.248
    //  32768     32767     980    4.233
    //  65536     65535    4044    4.127
    // 131072    131071   16227    4.013
    // 262144    262143   65451    4.033
    // 524288    524287  264342    4.039 // takes too long for more data

    // countDoublingRatio(10,"QuickUnionUF");
    //    QuickUnionUF doubling ratios
    //    N        con     time    time/prev
    //   2048      2047      12    3.000
    //   4096      4095      34    2.792
    //   8192      8191     202    6.015
    //  16384     16383    1651    8.194
    //  32768     32767    6725    4.073
    //  65536     65535   32659    4.012 // takes too long for more data

    countDoublingRatio(10,"WeightedQuickUnionUF");
    // WeightedQuickUnionUF doubling ratios
    //    N        con     time    time/prev
    //   2048      2047       2    0.800
    //   4096      4095       3    1.500
    //   8192      8191       4    1.167
    //  16384     16383       9    2.571
    //  32768     32767      15    1.611
    //  65536     65535      19    1.310
    // 131072    131071      57    2.974
    // 262144    262143     124    2.186
    // 524288    524287     296    2.397
    //1048576   1048575     967    3.265
    //2097152   2097151    2729    2.824
    //4194304   4194303    6364    2.332
    //8388608   8388607   15448    2.251

  }
}
