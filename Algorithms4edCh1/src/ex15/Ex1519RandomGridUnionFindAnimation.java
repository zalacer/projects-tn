package ex15;

import static v.ArrayUtils.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import edu.princeton.cs.algs4.StdDraw;
import ds.RandomBag;

/*
  p238
  1.5.19 Animation. Write a RandomGrid client (see Exercise 1.5.18) that uses
  UnionFind as in our development client to check connectivity and uses StdDraw to
  draw the connections as they are processed.
 */

@SuppressWarnings("unused")
public class Ex1519RandomGridUnionFindAnimation {
  
  public static class Connection {
    int p;
    int q;
    public Connection(int p, int q) { 
      this.p = p; this.q = q; 
     }
    @Override
    public String toString() {
      return p+"-"+q;
    }
  }
  
  public static Connection[] generate(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("generate: N must be > 1");
    Random r = null;
    try {
      r = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {}
    if (r == null) r = new Random(10730059);
    
    //                                       adjacent    diagonal
    Connection[] c = ofDim(Connection.class, 2*n*(n-1) + 2*(n-1)*(n-1));
    
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
  
  public static class WeightedQuickUnionWithPathCompressionUF {
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
      System.out.println("sz"+arrayToString(sz,5000,1,1));
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }
 
  public static void generateRandomGrid(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    Connection[] ca = generate(n);
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(2*n*(n-1) + 2*(n-1)*(n-1) - 1);
    StdDraw.setXscale(0, n-1);
    StdDraw.setYscale(0, n-1);
    StdDraw.setPenRadius(.1/n+.001/(n*n));
    StdDraw.setPenColor(StdDraw.BLACK);
    String sa[]; int p; int q;
    Iterator<Connection> it = (Iterator<Connection>) iterator(generate(n));
    while (it.hasNext()) {
      sa = it.next().toString().split("-");
      p = Integer.parseInt(sa[0]);
      q = Integer.parseInt(sa[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p,q);
      StdDraw.line(p/n, p%n, q/n, q%n);
      StdDraw.show();
      StdDraw.show(200/n);
    }
  }
  
  public static void main(String[] args) {
  
    generateRandomGrid(31);
    
/* You can use the code below to take input from System.in, but when running it in 
   Eclipse on my system it doesn't put the drawing window in the foreground 
   automatically and for small n the drawing will be completed before it can be put 
   in the foreground manually and the animation will be missed.
*/

//    Scanner sc = new Scanner(System.in);
//    if (sc.hasNextInt()) {
//      int n = sc.nextInt();
//      System.out.println("N="+n);
//      generateRandomGrid(n);
//    }
//    sc.close();
  
  }
}
