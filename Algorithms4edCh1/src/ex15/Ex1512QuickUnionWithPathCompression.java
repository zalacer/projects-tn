package ex15;

import static v.ArrayUtils.*;

/*
  p.237
  1.5.12 Quick-union with path compression. Modify quick-union (page 224) to include
  path compression, by adding a loop to union() that links every site on the paths from
  p and  q to the roots of their trees to the root of the new tree. Give a sequence of input
  pairs that causes this method to produce a path of length 4. Note : The amortized cost
  per operation for this algorithm is known to be logarithmic.

  QuickUnionWithPathCompressionUF initialized with N=11 produces a component with path length
  4 from input "10-9 9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2". A trace of this is given below.

  On p231 of the text an implementation of path compression is given that involves modifying
  find only.

  UF.java does path compression by halving in find. A demo of that is given below in
  QuickUnionWithHalvingPathCompressionUF.

 */

public class Ex1512QuickUnionWithPathCompression {

  public static interface UF {
    // interface for UF (union-find) classes
    public void union(int p, int q);
    public int find(int p);
    public boolean connected(int p, int q);
    public int count();
    public void printArray();
  }

  public static class QuickUnionWithPathCompressionUF implements UF {
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
       component given by its root which is a site with a value equal to its index in 
       id as implemented in find, i.e. if id[i] = i then i is a root == component */
    private int count; // number of components 

    public QuickUnionWithPathCompressionUF(int N) {
      // initialize component id array so that each site is in the component with the 
      // same name, i.e. is its own root
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "connected: p and q must both be >= 0 and <= id.length");
      return find(p) == find(q); 
    }

    public int find(int p) {
      // Find the root of p, i.e. its component.
      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) p = id[p];
      return p;
    }

    public void union(int p, int q) {
      // Give p and q the same root, i.e. component
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pRoot = find(p);
      int qRoot = find(q);
      if (pRoot == qRoot) return;
      int[] a = {p,q};
      int t;
      for (int x : a)
        while (true) {
          if (id[x] == qRoot) break;
          t = id[x]; id[x] = qRoot; x = t;
        }
      count--;
    }

    public void printArray() {
      System.out.println("id"+arrayToString(id,500,1,1));
    }
  }

  public static class QuickUnionWithHalvingPathCompressionUF implements UF {
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
    component given by its root which is a site with a value equal to its index in 
    id as implemented in find, i.e. if id[i] = i then i is a root == component */
    private int count; // number of components

    public QuickUnionWithHalvingPathCompressionUF(int N) {
      // initialize component id array so that each site is in the component with the 
      // same name, i.e. is its own root
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "connected: p and q must both be >= 0 and <= id.length");
      return find(p) == find(q); 
    }

    //    public int find(int p) {
    //      // Find the root of p, i.e. its component.
    //      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
    //      while (p != id[p]) p = id[p];
    //      return p;
    //    }

    public int find(int p) {
      // from UF.java
      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) {
        id[p] = id[id[p]];    // path compression by halving
        p = id[p];
      }
      return p;
    }

    public void union(int p, int q) {
      // Give p and q the same root, i.e. component
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pRoot = find(p);
      int qRoot = find(q);
      if (pRoot == qRoot) return;
      id[pRoot] = qRoot;
      count--;
    }

    public void printArray() {
      System.out.println("id"+arrayToString(id,500,1,1));
    }
  }

  public static class QuickUnionUF implements UF {
    private int[] id;
    /* the ith location in id is the ith site while it's value is a site in the same 
    component given by its root which is a site with a value equal to its index in 
    id as implemented in find, i.e. if id[i] = i then i is a root == component */
    private int count; // number of components

    public QuickUnionUF(int N) {
      // initialize component id array so that each site is in the component with the 
      // same name, i.e. is its own root
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) {
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "connected: p and q must both be >= 0 and <= id.length");
      return find(p) == find(q); 
    }

    public int find(int p) {
      // Find the root of p, i.e. its component.
      if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      while (p != id[p]) p = id[p];
      return p;
    }

    public void union(int p, int q) {
      // Give p and q the same root, i.e. component
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pRoot = find(p);
      int qRoot = find(q);
      if (pRoot == qRoot) return;
      id[pRoot] = qRoot;
      count--;
    }

    public void printArray() {
      System.out.println("id"+arrayToString(id,500,1,1));
    }

  }
  
  public static void main(String[] args) {
    
    UF uf = null; String[] sa; String[] d; int p; int q;
    
    uf = new QuickUnionWithPathCompressionUF(11);
    sa = "10-9 9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+"); 
    System.out.println("QuickUnionWithPathCompressionUF demo producing a path of length 4:");
    System.out.println("(using input \"10-9 9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2\")");
    System.out.println("==================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }
    System.out.println("\n");

    uf = new QuickUnionWithPathCompressionUF(10);
    sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    System.out.println("QuickUnionWithPathCompressionUF demo:");
    System.out.println("(using input \"9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2\")");
    System.out.println("==================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }
    System.out.println("\n");
 
    uf = new QuickUnionWithHalvingPathCompressionUF(10);
    sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    System.out.println("QuickUnionWithHalvingPathCompressionUF demo:");
    System.out.println("(using input \"9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2\")");
    System.out.println("==================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }
    System.out.println("\n");
    
    uf = new QuickUnionUF(10);
    sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    System.out.println("QuickUnionUF demo:");
    System.out.println("(using input \"9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2\")");
    System.out.println("==================================================================");
    uf.printArray();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArray();
    }

 /*    
    QuickUnionWithPathCompressionUF demo producing a path of length 4:
    (using input "10-9 9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2")
    ==================================================================
    10 9
    count=10
    id[0,1,2,3,4,5,6,7,8,9,9]      0 1 2 3 4 5 6 7 8 9 
                                                     10
    9 0
    count=9
    id[0,1,2,3,4,5,6,7,8,0,9]      0 1 2 3 4 5 6 7 8 
                                   9
                                  10
    3 4
    count=8
    id[0,1,2,4,4,5,6,7,8,0,9]      0 1 2 3 5 6 7 8 
                                   9     4
                                  10
    5 8
    count=7
    id[0,1,2,4,4,8,6,7,8,0,9]      0 1 2 3 6 7 8 
                                   9     4     5
                                  10
    7 2                   
    count=6
    id[0,1,2,4,4,8,6,2,8,0,9]      0 1 2 3 6 8 
                                   9   7 4   5
                                  10
    2 1
    count=5
    id[0,1,1,4,4,8,6,2,8,0,9]      0 1 3 6 8 
                                   9 2 4   5
                                  10 7
    5 7
    count=4
    id[0,1,1,4,4,1,6,1,1,0,9]      0     1     4  6
                                   9  2,5,7,8  3
                                  10 
    0 3
    count=3
    id[4,1,1,4,4,1,6,1,1,0,9]        1      4   6
                                  2,5,7,8  0,3
                                           9
                                          10
    4 2
    count=2
    id[4,1,1,4,1,1,6,1,1,0,9]           1         6
                                {2, 4, 5, 7, 8}
                                   0,3
                                   9
                                  10     


    QuickUnionWithPathCompressionUF demo:
    (using input "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2")
    ==================================================================
    id[0,1,2,3,4,5,6,7,8,9]

    9 0
    count=9
    id[0,1,2,3,4,5,6,7,8,0]     0 1 2 3 4 5 6 7 8
                                9
    3 4
    count=8
    id[0,1,2,4,4,5,6,7,8,0]     0 1 2 4 5 6 7 8
                                9     3
    5 8
    count=7
    id[0,1,2,4,4,8,6,7,8,0]     0 1 2 4 6 7 8
                                9     3     5
    7 2
    count=6
    id[0,1,2,4,4,8,6,2,8,0]     0 1 2 4 6 8
                                9   7 3   5
    2 1
    count=5
    id[0,1,1,4,4,8,6,2,8,0]     0 1 4 6 8
                                9 2 3   5
                                  7
    5 7
    count=4
    id[0,1,1,4,4,1,6,1,1,0]     0     1     4  6
                                9  2,5,7,8  3
    0 3
    count=3
    id[4,1,1,4,4,1,6,1,1,0]        1      4  6
                                2,5,7,8  0,3
                                         9

    4 2
    count=2
    id[4,1,1,4,1,1,6,1,1,0]           1         6
                               {2, 4, 5, 7, 8}
                                  0,3
                                  9     


    QuickUnionWithHalvingPathCompressionUF demo:
    (using input "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2")
    ==================================================================
    id[0,1,2,3,4,5,6,7,8,9]

    9 0
    count=9
    id[0,1,2,3,4,5,6,7,8,0]     0 1 2 3 4 5 6 7 8
                                9
    3 4
    count=8
    id[0,1,2,4,4,5,6,7,8,0]     0 1 2 4 5 6 7 8
                                9     3
    5 8
    count=7
    id[0,1,2,4,4,8,6,7,8,0]     0 1 2 4 6 7 8
                                9     3     5
    7 2
    count=6
    id[0,1,2,4,4,8,6,2,8,0]     0 1 2 4 6 8
                                9   7 3   5
    2 1
    count=5
    id[0,1,1,4,4,8,6,2,8,0]     0 1 4 6 8
                                9 2 3   5
                                  7
    5 7
    count=4
    id[0,1,1,4,4,8,6,1,1,0]     0    1    4 6
                                9  2,7,8  3
                                       5
    0 3
    count=3
    id[4,1,1,4,4,8,6,1,1,0]       1     4  6
                                2,7,8  0,3
                                    5  9
    4 2
    count=2
    id[4,1,1,4,1,8,6,1,1,0]          1        6
                                {2, 4, 7, 8}
                                   0,3    5
                                   9


    QuickUnionUF demo:
    (using input "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2")
    ==================================================================
    id[0,1,2,3,4,5,6,7,8,9]

    9 0
    count=9
    id[0,1,2,3,4,5,6,7,8,0]     0 1 2 3 4 5 6 7 8
                                9
    3 4
    count=8
    id[0,1,2,4,4,5,6,7,8,0]     0 1 2 4 5 6 7 8
                                9     3
    5 8
    count=7
    id[0,1,2,4,4,8,6,7,8,0]     0 1 2 4 6 7 8
                                9     3     5 

    7 2
    count=6
    id[0,1,2,4,4,8,6,2,8,0]     0 1 2 4 6 8
                                9   7 3   5
    2 1
    count=5
    id[0,1,1,4,4,8,6,2,8,0]     0 1 4 6 8
                                9 2 3   5
                                  7
    5 7
    count=4
    id[0,1,1,4,4,8,6,2,1,0]     0   1   4  6
                                9  2,8  3 
                                   7 5
    0 3
    count=3
    id[4,1,1,4,4,8,6,2,1,0]      1    4   6
                                2,8  0,3 
                                7 5  9
    4 2
    count=2
    id[4,1,1,4,1,8,6,2,1,0]          1      6
                                {2,  4,  8}
                                 7 {0,3} 5
                                    9
     */          

  }

}
