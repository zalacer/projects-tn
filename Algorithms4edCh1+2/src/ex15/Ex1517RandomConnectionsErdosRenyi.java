package ex15;


import static v.ArrayUtils.*;

import java.util.Random;
import java.util.Scanner;
import analysis.Timer;

/*
  p.237
  1.5.17 Random connections. Develop a UF client ErdosRenyi that takes an integer
  value  N from the command line, generates random pairs of integers between 0 and N-1 ,
  calling  connected() to determine if they are connected and then  union() if not (as in
  our development client), looping until all sites are connected, and printing the number
  of connections generated. Package your program as a static method  count() that takes
  N as argument and returns the number of connections and a  main() that takes  N from
  the command line, calls count() , and prints the returned value.
  
 */

public class Ex1517RandomConnectionsErdosRenyi {
  
  public static void count(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(n);
    Random r = new Random(10719157);
    int p; int q; int con = 0;
    while(true) {
      p = r.nextInt(n); q = r.nextInt(n);
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    System.out.println(con);
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
  
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    if (sc.hasNextInt()) {
      Timer t = new Timer();
      count(sc.nextInt());
      t.stop();
    }
    sc.close();
    /*
      N              time
      2              4 ms
      4              5 ms
      8              5 ms
      10             5 ms
      100           14 ms
      1000          16 ms  4ms
      1024          27 ms
      10000         70 ms  21ms
      16384         62 ms
      100000       123 ms  89ms
      131072        97 ms
      1000000      924 ms  90ms
      1048576      913 ms
      10000000   15438 ms  18337 ms
      16777216   25565 ms  34504 ms
       
    */   
    
  
  }
}
