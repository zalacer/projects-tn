package ex15;

import static v.ArrayUtils.*;
import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import analysis.Timer;

/*
  p239
  1.5.23 Compare quick-find with quick-union for Erdös-Renyi model. Develop a perfor-
  mance-testing client that takes an int value T from the command line and performs
  T trials of the following experiment: Use your client from Exercise 1.5.17 to generate
  random connections.
  
  I already knew quick-union is slow and in comparison with quick-find.
  
 */

public class Ex1523ErdösRenyiQuickFindVsQuickUnion {
  
  public static int[] z = null;
  
  public static void generateConnections(int N) {
    // save connections made by QuickFindUF for comparison testing against QuickUnionUF
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    Random r = new Random(10719157);
    UF uf = new QuickFindUF(n);
    int p; int q;
    List<Integer> l = new ArrayList<Integer>();
    while(true) {
      p = r.nextInt(n); q = r.nextInt(n);
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      l.add(p); l.add(q);
      if (uf.count() == 1) break;
    }
    z = (int[]) unbox(l.toArray(new Integer[0]));
  }
  
  public static int countWithoutPregeneratedConnections(int N, String u) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    Random r = new Random(10719157);
    UF uf = null;
    switch (u) {
    case "QuickFindUF": uf = new QuickFindUF(n); break;
    case "QuickUnionUF": uf = new QuickUnionUF(n); break;
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
 
  public static int countWithPregeneratedConnections(int N, String u) 
      throws UFInvalidInputException {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    if (Objects.isNull(z)) throw new UFInvalidInputException("count: z array is null");
    UF uf = null;
    switch (u) {
    case "QuickFindUF": uf = new QuickFindUF(n); break;
    case "QuickUnionUF": uf = new QuickUnionUF(n); break;
    default: 
      throw new IllegalArgumentException("count: u unrecognized UF class name");
    }
    int p; int q; int con = 0;
    for (int i = 0; i < z.length-1; i++) {
      p = z[i]; q = z[i+1];
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    return con; 
  }
  
  public static double[] countTimeTrialWithoutPregeneratedConnections(int n, String u) {
    Timer t = new Timer();
    int con = countWithoutPregeneratedConnections(n, u);
    double time = t.finish();
    return new double[]{con,time};
  }
  
  public static double[] countTimeTrialWithPregeneratedConnections(int n, String u) 
      throws UFInvalidInputException {
    Timer t = new Timer();
    int con = countWithPregeneratedConnections(n, u);
    double time = t.finish();
    return new double[]{con,time};
  }

  public static void comparisonOfRunTimesWithoutPregeneratedConnections(
      int t, String u1, String u2) {
    int n = 1024;
    double[] d = new double[2];
    double[] tim = new double[t];
    
    int c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrialWithoutPregeneratedConnections(n, u1);
      tim[c++] = d[1];
    }
    double pqfavgtim = mean(tim);
    
    c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrialWithoutPregeneratedConnections(n, u2);
      tim[c++] = d[1];
    }
    double pquavgtim = mean(tim);
    
    double pratio = pqfavgtim/pquavgtim;
    
    System.out.println(u1+"/"+u2+" run time ratios without pregenerated connections");
    System.out.println("      N       ratio     ratio/prev");

    double qfavgtim = 0; double quavgtim = 0; double ratio = 0;

    while (n < (int) pow(2,30)) {
      n*=2; 
      c = 0;
      for (int i = 0; i < t; i++) {
        d = countTimeTrialWithoutPregeneratedConnections(n, u1);
        tim[c++] = d[1];
      }
      qfavgtim = mean(tim);
//      System.out.println("qfavgtim="+qfavgtim);
      c = 0;
      for (int i = 0; i < t; i++) {
        d = countTimeTrialWithoutPregeneratedConnections(n, u2);
        tim[c++] = d[1];
      }
      quavgtim = mean(tim);
//      System.out.println("quavgtim="+quavgtim);
      
      ratio = qfavgtim/quavgtim;
      
      System.out.printf("  %7d   %7.3f ", n, ratio);
      System.out.printf("    %5.3f\n", ratio/pratio);
      
      pratio = ratio;
    }
  }

  public static void comparisonOfRunTimesWithPregeneratedConnections(
      int t, String u1, String u2) throws UFInvalidInputException {
    int n = 1024;
    double[] d = new double[2];
    double[] tim = new double[t];   
    generateConnections(n);
    
    int c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrialWithPregeneratedConnections(n, u1);
      tim[c++] = d[1];
    }
    double pqfavgtim = mean(tim);
    
    c = 0;
    for (int i = 0; i < t; i++) {
      d = countTimeTrialWithPregeneratedConnections(n, u2);
      tim[c++] = d[1];
    }
    double pquavgtim = mean(tim);
    
    double pratio = pqfavgtim/pquavgtim;
    
    System.out.println(u1+"/"+u2+" run time ratios with pregenerated connections");
    System.out.println("      N       ratio     ratio/prev");

    double qfavgtim = 0; double quavgtim = 0; double ratio = 0;

    while (n < (int) pow(2,30)) {
      n*=2; 
      c = 0;
      generateConnections(n);
      for (int i = 0; i < t; i++) {
        d = countTimeTrialWithPregeneratedConnections(n, u1);
        tim[c++] = d[1];
      }
      qfavgtim = mean(tim);
//      System.out.println("qfavgtim="+qfavgtim);
      c = 0;
      for (int i = 0; i < t; i++) {
        d = countTimeTrialWithPregeneratedConnections(n, u2);
        tim[c++] = d[1];
      }
      quavgtim = mean(tim);
//      System.out.println("quavgtim="+quavgtim);
      
      ratio = qfavgtim/quavgtim;
      
      System.out.printf("  %7d   %7.3f ", n, ratio);
      System.out.printf("    %5.3f\n", ratio/pratio);
      
      pratio = ratio;
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
  
  public static class UFInvalidInputException extends Exception {
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

  public static void main(String[] args) throws UFInvalidInputException {
    
    Scanner sc = new Scanner(System.in);
    if (sc.hasNextInt())
      comparisonOfRunTimesWithPregeneratedConnections(
          sc.nextInt(), "QuickFindUF", "QuickUnionUF");
    sc.close();
    
    //  QuickFindUF/QuickUnionUF run time ratios without pregenerated connections
    //    N       ratio     ratio/prev
    //   2048     0.565     0.226
    //   4096     0.465     0.822
    //   8192     0.270     0.581
    //  16384     0.162     0.601
    //  32768     0.186     1.147
    //  65536     0.161     0.865
    
    //  QuickFindUF/QuickUnionUF run time ratios with pregenerated connections
    //    N       ratio     ratio/prev
    //   2048     0.625     1.250
    //   4096     0.651     1.041
    //   8192     0.768     1.181
    //  16384     0.693     0.901
    //  32768     0.689     0.994
    //  65536     0.674     0.979
    
  }
}
