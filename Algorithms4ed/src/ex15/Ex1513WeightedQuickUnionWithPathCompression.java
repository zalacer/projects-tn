package ex15;

import static v.ArrayUtils.arrayToString;

import java.io.FileNotFoundException;

/*
  p.237
  1.5.13 Weighted quick-union with path compression. Modify weighted quick-union
  (Algorithm 1.5) to implement path compression, as described in Exercise 1.5.12.
  Give a sequence of input pairs that causes this method to produce a tree of height 4.
  Note : The amortized cost per operation for this algorithm is known to be bounded by a
  function known as the inverse Ackermann function and is less than 5 for any conceivable
  practical value of N.
  
  WeightedQuickUnionWithPathCompressionUF is the modified weighted quick-union class with 
  path compression and is included below. WeightedQuickUnionWithHalvingPathCompressionUF,
  which does path compression by halving in find(), is also included for comparison.
  
  A sequence of input pairs that causes WeightedQuickUnionWithPathCompressionUF to produce
  a tree a height 4 can be developed by designing it to create a worst-case tree of size
  2**4 following the method mentioned on p227 and shown on p229. Such a sequence is:
    "0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8"

 */

public class Ex1513WeightedQuickUnionWithPathCompression {

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
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }
  
  public static class WeightedQuickUnionWithHalvingPathCompressionUF {
    // this class does path compression by halving in find()
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
       component given by its root which is a site with a value equal to its index in 
       id as implemented in find, i.e. if id[i] = i then i is a root == component */
    private int[] sz; // size of component for roots (site indexed)
    private int count; // number of components 
    
    public WeightedQuickUnionWithHalvingPathCompressionUF(int N) {
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
      // Find the root of p and do path compression by halving as in UF.java
      if (p < 0 || p >= id.length) 
        throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) {
        id[p] = id[id[p]];    // path compression by halving
        p = id[p];
      }
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
      // Make smaller root point to larger one.
      if (sz[i] < sz[j]) { 
        id[i] = j;  sz[j] += sz[i];
      } else {
        id[j] = i; sz[i] += sz[j];
      }
      count--;
    }
    
    public void printArray() {
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }
  
  public static void main(String[] args) throws FileNotFoundException {
    
  /* The tests below show that for input 
         "0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8"
     WeightedQuickUnionWithPathCompressionUF and WeightedQuickUnionWithHalvingPathCompressionUF
     produce identical results with a tree of height 4 at the end.
  */
    WeightedQuickUnionWithPathCompressionUF uf = null;
    WeightedQuickUnionWithHalvingPathCompressionUF uf2 = null;
    String[] sa; String[] d; int p; int q;
    
    uf = new WeightedQuickUnionWithPathCompressionUF(16);
    sa = "0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8".split("\\s+");
    System.out.println("WeightedQuickUnionWithPathCompressionUF producing tree height 4:");
    System.out.println("using input \"(0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8)\"");
    System.out.println("=====================================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }
    System.out.println("\n");

    uf2 = new WeightedQuickUnionWithHalvingPathCompressionUF(16);
    sa = "0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8".split("\\s+");
    System.out.println("WeightedQuickUnionWithHalvingPathCompressionUF producing tree height 4:");
    System.out.println("using input \"(0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8)\"");
    System.out.println("=====================================================================================");
    uf2.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      uf2.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf2.count());
      uf2.printArray();
    }
    System.out.println("\n"); 
     
    uf = new WeightedQuickUnionWithPathCompressionUF(10);
    sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    System.out.println("WeightedQuickUnionWithPathCompressionUF:");
    System.out.println("using input \"(9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2)\"");
    System.out.println("=====================================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }
    System.out.println("\n");
    
/*
    WeightedQuickUnionWithPathCompressionUF producing tree height 4:
    using input "(0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8)"
    =====================================================================================
    id[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
    
    0 1
    count=15
    id[0,0,2,3,4,5,6,7,8,9,10,11,12,13,14,15]   0 2 3 4 5 6 7 8 9 10 11 12 13 14 15
                                                1
    2 3
    count=14
    id[0,0,2,2,4,5,6,7,8,9,10,11,12,13,14,15]   0 2 4 5 6 7 8 9 10 11 12 13 14 15
                                                1 3
    4 5
    count=13
    id[0,0,2,2,4,4,6,7,8,9,10,11,12,13,14,15]   0 2 4 6 7 8 9 10 11 12 13 14 15
                                                1 3 5
    6 7
    count=12
    id[0,0,2,2,4,4,6,6,8,9,10,11,12,13,14,15]   0 2 4 6 8 9 10 11 12 13 14 15
                                                1 3 5 7
    0 2
    count=11
    id[0,0,0,2,4,4,6,6,8,9,10,11,12,13,14,15]    0  4 6 8 9 10 11 12 13 14 15
                                                1,2 5 7
                                                  3
    4 6
    count=10
    id[0,0,0,2,4,4,4,6,8,9,10,11,12,13,14,15]    0   4  8 9 10 11 12 13 14 15
                                                1,2 5,6 
                                                  3   7
    0 4
    count=9
    id[0,0,0,2,0,4,4,6,8,9,10,11,12,13,14,15]     0     8 9 10 11 12 13 14 15
                                                1,2, 4
                                                  3 5,6
                                                      7
    8 9
    count=8
    id[0,0,0,2,0,4,4,6,8,8,10,11,12,13,14,15]     0     8 10 11 12 13 14 15
                                                1,2, 4  9
                                                  3 5,6
                                                      7
    10 11
    count=7
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,13,14,15]     0     8 10 12 13 14 15
                                                1,2, 4  9 11
                                                  3 5,6
                                                      7
    12 13
    count=6
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,12,14,15]     0     8 10 12 14 15
                                                1,2, 4  9 11 13
                                                  3 5,6
                                                      7
    14 15
    count=5
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,12,14,14]     0      8  10  12  14
                                                -------  -  --  --  --
                                                1,2, 4   9  11  13  15
                                                  3 5,6
                                                      7
    8 10
    count=4
    id[0,0,0,2,0,4,4,6,8,8,8,10,12,12,14,14]       0      8    12  14
                                                -------  ----  --  --
                                                1,2, 4   9,10  13  15
                                                  3 5,6    11
                                                      7
    12 14
    count=3
    id[0,0,0,2,0,4,4,6,8,8,8,10,12,12,12,14]      0        8     12
                                                -------  ----  -----   
                                                1,2, 4   9,10  13,14
                                                  3 5,6    11     15
                                                      7
    8 12
    count=2
    id[0,0,0,2,0,4,4,6,8,8,8,10,8,12,12,14]       0          8
                                                -------  ----------
                                                1,2, 4   9,10, 12
                                                  3 5,6    11 13,14
                                                      7          15
    0 8
    count=1                                                          binomial coefficients
    id[0,0,0,2,0,4,4,6,0,8,8,10,8,12,12,14]             0               1
                                                -------------------
                                                1,2, 4,     8           4
                                                  3 5,6  9,10, 12       6
                                                      7    11 13,14     4
                                                                 15     1
    
    
    WeightedQuickUnionWithHalvingPathCompressionUF producing tree height 4:
    using input "(0-1 2-3 4-5 6-7 0-2 4-6 0-4 8-9 10-11 12-13 14-15 8-10 12-14 8-12 0-8)"
    =====================================================================================
    id[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
    
    0 1
    count=15
    id[0,0,2,3,4,5,6,7,8,9,10,11,12,13,14,15]   0 2 3 4 5 6 7 8 9 10 11 12 13 14 15
                                                1
    2 3
    count=14
    id[0,0,2,2,4,5,6,7,8,9,10,11,12,13,14,15]   0 2 4 5 6 7 8 9 10 11 12 13 14 15
                                                1 3
    4 5
    count=13
    id[0,0,2,2,4,4,6,7,8,9,10,11,12,13,14,15]   0 2 4 6 7 8 9 10 11 12 13 14 15
                                                1 3 5
    6 7
    count=12
    id[0,0,2,2,4,4,6,6,8,9,10,11,12,13,14,15]   0 2 4 6 8 9 10 11 12 13 14 15
                                                1 3 5 7
    0 2
    count=11
    id[0,0,0,2,4,4,6,6,8,9,10,11,12,13,14,15]    0  4 6 8 9 10 11 12 13 14 15
                                                1,2 5 7
                                                  3
    4 6
    count=10
    id[0,0,0,2,4,4,4,6,8,9,10,11,12,13,14,15]    0   4  8 9 10 11 12 13 14 15
                                                1,2 5,6 
                                                  3   7
    0 4
    count=9
    id[0,0,0,2,0,4,4,6,8,9,10,11,12,13,14,15]     0     8 9 10 11 12 13 14 15
                                                1,2, 4
                                                  3 5,6
                                                      7
    8 9
    count=8
    id[0,0,0,2,0,4,4,6,8,8,10,11,12,13,14,15]     0     8 10 11 12 13 14 15
                                                1,2, 4  9
                                                  3 5,6
                                                      7
    10 11
    count=7
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,13,14,15]     0     8 10 12 13 14 15
                                                1,2, 4  9 11
                                                  3 5,6
                                                      7
    12 13
    count=6
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,12,14,15]     0     8 10 12 14 15
                                                1,2, 4  9 11 13
                                                  3 5,6
                                                      7
    14 15
    count=5
    id[0,0,0,2,0,4,4,6,8,8,10,10,12,12,14,14]     0      8  10  12  14
                                                -------  -  --  --  --
                                                1,2, 4   9  11  13  15
                                                  3 5,6
                                                      7
    8 10
    count=4
    id[0,0,0,2,0,4,4,6,8,8,8,10,12,12,14,14]       0      8    12  14
                                                -------  ----  --  --
                                                1,2, 4   9,10  13  15
                                                  3 5,6    11
                                                      7
    12 14
    count=3
    id[0,0,0,2,0,4,4,6,8,8,8,10,12,12,12,14]      0        8     12
                                                -------  ----  -----   
                                                1,2, 4   9,10  13,14
                                                  3 5,6    11     15
                                                      7
    8 12
    count=2
    id[0,0,0,2,0,4,4,6,8,8,8,10,8,12,12,14]       0          8
                                                -------  ----------
                                                1,2, 4   9,10, 12
                                                  3 5,6    11 13,14
                                                      7          15
    0 8
    count=1                                                          binomial coefficients
    id[0,0,0,2,0,4,4,6,0,8,8,10,8,12,12,14]             0               1
                                                -------------------
                                                1,2, 4,     8           4
                                                  3 5,6  9,10, 12       6
                                                      7    11 13,14     4
                                                                 15     1
  
   
    WeightedQuickUnionWithPathCompressionUF:
    using input "(9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2)"
    =====================================================================================
    id[0,1,2,3,4,5,6,7,8,9]
    
    9 0
    count=9
    id[9,1,2,3,4,5,6,7,8,9]
    
    3 4
    count=8
    id[9,1,2,3,3,5,6,7,8,9]
    
    5 8
    count=7
    id[9,1,2,3,3,5,6,7,5,9]
    
    7 2
    count=6
    id[9,1,7,3,3,5,6,7,5,9]
    
    2 1
    count=5
    id[9,7,7,3,3,5,6,7,5,9]
    
    5 7
    count=4
    id[9,7,7,3,3,7,6,7,5,9]
    
    0 3
    count=3
    id[9,7,7,9,3,7,6,7,5,9]
    
    4 2
    count=2
    id[9,7,7,7,7,7,6,7,5,7]
                                   
*/
  }
}
