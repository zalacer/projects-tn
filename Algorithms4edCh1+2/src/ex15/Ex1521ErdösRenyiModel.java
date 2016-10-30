package ex15;

import static v.ArrayUtils.*;
import static analysis.Log.*;
import static java.lang.Math.*;

import java.util.Random;

/*
  p239
  1.5.21 Erdös-Renyi model. Use your client from Exercise 1.5.17 to test the hypothesis
  that the number of pairs generated to get one component is ~ ½NlnN.
  
  Running a test starting with N == 2 and increasing to 2**27 by powers of 2 shows that
  it's a good hypothesis, since the ratio of the number pairs to get one component to 
  ½NlnN quickly gets close to 1 and stays there.
  
 */

public class Ex1521ErdösRenyiModel {
    
  @SuppressWarnings("unused")
  public static double count(int N) {
    int n = N;
    if (n < 2) throw new IllegalArgumentException("count: N must be > 1");
    WeightedQuickUnionWithPathCompressionUF uf = 
        new WeightedQuickUnionWithPathCompressionUF(n);
    Random r = new Random(10719157);
    int p; int q; int con = 0; int pairs = 0;
    while(true) {
      p = r.nextInt(n); q = r.nextInt(n);
      pairs++;
      if (uf.connected(p,q)) continue;
      uf.union(p,q);
      con++;
      if (uf.count() == 1) break;
    }
    
    return 1.*pairs/(round(.5*(N)*ln(N))); 
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
 
    int k = 2; int c = 1;
    System.out.println("ratio                    power of 2");
    while (c < 28) { // run out of heap at 28
      System.out.printf("%5.19f    %d\n",count(k), c);
      k *= 2; c++;
    }

    //  ratio of pairs/½NlnN     power of 2
    //  6.0000000000000000000    1
    //  2.0000000000000000000    2
    //  1.5000000000000000000    3
    //  1.3636363636363635000    4
    //  1.5818181818181818000    5
    //  0.9624060150375939000    6
    //  1.1672025723472670000    7
    //  1.2732394366197184000    8
    //  0.8547276142767689000    9
    //  0.8771484925331079000    10
    //  1.2797131147540983000    11
    //  1.0727913120046961000    12
    //  1.3198406892627814000    13
    //  1.3072859011774178000    14
    //  0.9922159344400874000    15
    //  0.9792024963608497000    16
    //  1.1998034300039884000    17
    //  1.0905842764099676000    18
    //  1.0663046365365605000    19
    //  1.0202199314133191000    20
    //  1.0391093801175078000    21
    //  1.0038301161633360000    22
    //  1.0438462289663348000    23
    //  1.1285553829996298000    24
    //  1.0048181146051730000    25
    //  1.0437442370722105000    26
    //  1.0214213261418660000    27
 


  }
}
