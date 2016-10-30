package ex15;

import static v.ArrayUtils.*;
import static utils.PrimeUtils.*;
import static java.lang.Math.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.Scanner;

import analysis.Timer;
import ds.RandomBag;

/*
  p239
   1.5.25 Doubling test for random grids. Develop a performance-testing client that takes
  an  int value T from the command line and performs T trials of the following experie-
  ment: Use your client from Exercise 1.5.18 to generate the connections in an  N-by-N
  square grid, randomly oriented and in random order, then use UnionFind to determine
  connectivity as in our development client, looping until all sites are connected. For each
  N, print the value of N, the average number of connections processed, and the ratio of
  the running time to the previous. Use your program to validate the hypotheses in the
  text that the running times for quick-find and quick-union are quadratic and weighted
  quick-union is near-linear. Note : As  N doubles, the number of sites in the grid increases
  by a factor of 4, so expect a doubling factor of 16 for quadratic and 4 for linear.
 */

@SuppressWarnings("unused")
public class Ex1525DoublingTestForRandomGrids {
 /* 
   Using primes and primeIterator to create a new Random with a new seed every time 
   connections are generated with generateRandomGridCons(int) which is called by 
   countTimeTrial(int, String) before it begins timing. The point of this is to get 
   random variation between trials which are averaged without the performance degredation 
   of SecureRandom.getInstanceStrong(), which is explicitly used only to generate one int 
   in randomPrime(), however note that RandomBag.toArray(Item), which is invoked at the 
   end of generateRandomGridCons(int), uses SecureRandom.getInstanceStrong() and it may
   be useful to modify that similarly to reduce the run times of tests.
*/
  
  public static Connection[] w = null;
  public static int[] primes = primes();
  public static OfInt primeIterator = iterator(primes());
  public static Random r = new Random(randomPrime());
  
  public static int randomPrime() {
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    // assumes primeIterator.hasNext() == true
    if (r == null) r = new Random(primeIterator.nextInt());
    return primes[r.nextInt(primes.length)];
  }
  
  public static class Connection {
    public int p;
    public int q;
    public Connection(int p, int q) { 
      this.p = p; this.q = q; 
     }
    @Override
    public String toString() {
      return p+"-"+q;
    }
  }
  
  public static Connection[] generateRandomGridCons(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("generate: N must be > 1");
    //                                       adjacent    diagonal
    Connection[] c = ofDim(Connection.class, 2*n*(n-1) + 2*(n-1)*(n-1));
    r = new Random(randomPrime());
    int[][] z = new int[n][n];
    int offset = 0;
    for (int i = 0; i < n; i++) {
      z[i] = range(offset, offset+n);
      offset = n*(i+1);
    }
    
    int k = 0; // index for c
    
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n-1; j++)
        if (r.nextInt(2) == 0) { // adjacent sideways right or left
          c[k++] = new Connection(z[i][j], z[i][j+1]);
        } else {
          c[k++] = new Connection(z[i][j+1], z[i][j]);
        }
    
    for (int i = 0; i < n-1; i++) 
      for (int j = 0; j < n; j++) {
        if (r.nextInt(2) == 0) { // adjacent down or up
          c[k++] = new Connection(z[i][j], z[i+1][j]);
        } else {
          c[k++] = new Connection(z[i+1][j], z[i][j]);
        }
        
        if (j < n-1) { // right diagonal down or left diagonal up
          if (r.nextInt(2) == 0) {
            c[k++] = new Connection(z[i][j], z[i+1][j+1]);
          } else {
            c[k++] = new Connection(z[i+1][j+1], z[i][j]);
          }
        }
        
        if (j > 0) {// left diagonal down or right diagonal up
          if (r.nextInt(2) == 0) {
            c[k++] = new Connection(z[i][j], z[i+1][j-1]);
          } else {
            c[k++] = new Connection(z[i+1][j-1], z[i][j]);
          }
        }
      }

    RandomBag<Connection> bag = new  RandomBag<Connection>(c);
    return bag.toArray(c[0]);      
  }
 
  public static int count(int N, String u) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    if (Objects.isNull(w)) throw new UFInvalidInputException("count: w array is null");
    UF uf = null;
    switch (u) {
    case "QuickFindUF": uf = new QuickFindUF(2*n*(n-1) + 2*(n-1)*(n-1) - 1); break;
    case "QuickUnionUF": uf = new QuickUnionUF(2*n*(n-1) + 2*(n-1)*(n-1) - 1); break;
    case "WeightedQuickUnionUF": 
      uf = new WeightedQuickUnionUF(2*n*(n-1) + 2*(n-1)*(n-1) - 1); break;
    case "WeightedQuickUnionWithPathCompressionUF": 
      uf = new WeightedQuickUnionWithPathCompressionUF(2*n*(n-1) + 2*(n-1)*(n-1) - 1); break;
    default: 
      throw new IllegalArgumentException("count: u unrecognized UF class name");
    }
    int p; int q; int con = 0;
    for (int i = 0; i < w.length-1; i++) {
      p = w[i].p; q = w[i].q;
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    return con; 
  }
  
  public static double[] countTimeTrial(int n, String u) {
    w = generateRandomGridCons(n);
    Timer t = new Timer();
    int con = count(n, u);
    double time = t.finish();
    return new double[]{con,time};
  }

  public static void printdoublingRatios(int t, String u) {
    int n = 32;
    double[] d = new double[2];
    double[] con = new double[t];
    double[] tim = new double[t];
    int c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrial(n, u);
      con[c] = d[0]; 
      tim[c++] = d[1];
    }
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
      if (N <= 0) throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
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
     if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
     while (p != id[p]) p = id[p];
     return p;
    }

    public void union(int p, int q) {
      // put p and q into the same component.
      // after all input pairs have been processed, components are identified  
      // as those indices i in id such that id[i] == i.
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
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
  
  public static class WeightedQuickUnionWithPathCompressionUF implements UF {
    // this class does path compression in union()
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
       component given by its root which is a site with a value equal to its index in 
       id as implemented in find, i.e. if id[i] = i then i is a root == component */
    private int[] sz; // size of component for roots (site indexed)
    private int count; // number of components 
    
    public WeightedQuickUnionWithPathCompressionUF(int N) {
      /* initialize component id array so that each site is in the component with the 
        same name, i.e. is its own root */
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) {
        id[i] = i;
      }
      sz = new int[N];
      for (int i = 0; i < N; i++) sz[i] = 1;
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
      /* put p and q into the same component.
         after all input pairs have been processed, components are identified  
         as those indices i in id such that id[i] == i and also called roots. */
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) 
        throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int i = find(p);
      int j = find(q);
      if (i == j) return;
      int[] a = {p,q}; int t; // for path compression
      // Make smaller root point to larger one.
      if (sz[i] < sz[j]) {
        id[i] = j; sz[j] += sz[i]; 
        // path compression
        for (int x : a)
          while (true) {
            if (id[x] == j) break;
            t = id[x]; id[x] = j; x = t;
          }
      } else { 
        id[j] = i; sz[i] += sz[j];
        // path compression
        for (int x : a)
          while (true) {
            if (id[x] == i) break;
            t = id[x]; id[x] = i; x = t;
          }
      } 
      count--;
    }
    
    public void printArray() {
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }
  
  public static class UFInvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UFInvalidInputException() {};
    public UFInvalidInputException(String msg) { super(msg); }
    public UFInvalidInputException(Throwable cause) { super(cause); }
    public UFInvalidInputException(String message, Throwable cause) { super(message, cause); }
    public UFInvalidInputException(String message, Throwable cause, boolean enableSuppression, 
        boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }

  public static void main(String[] args) {
    
    printdoublingRatios(10, "QuickFindUF");
    
    printdoublingRatios(10, "QuickUnionUF");
    
    printdoublingRatios(10, "WeightedQuickUnionUF");
    
    printdoublingRatios(10, "WeightedQuickUnionWithPathCompressionUF");
    
    Scanner sc = new Scanner(System.in);
    if (sc.hasNextInt())
      printdoublingRatios(sc.nextInt(), "WeightedQuickUnionUF");
    sc.close();
    
 
    
  }
}
