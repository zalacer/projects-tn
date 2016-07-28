package ex15;

import static analysis.Log.*;
import static v.ArrayUtils.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
  p.237
  1.5.14 Weighted quick-union by height. Develop a UFimplementation that uses the
  same basic strategy as weighted quick-union but keeps track of tree height and always
  links the shorter tree to the taller one. Prove a logarithmic upper bound on the height
  of the trees for N sites with your algorithm.
  
  WeightedQuickUnionByHeightUF is the modified weighted quick-union by height class and 
  is included below.
  
  Proposition T: An upper bound on the height of trees for N sites produced by 
                 WeightedQuickUnionByHeightUF is lg(N).
               
  Proof by induction:            
  1. Base cases: 
     a. For N=1, there is one site with height 0 and lg(1) = 0.
     b. For N=2, there are two sites that for maximum height could be merged into one 
        tree with height 1 and lg(2) = 1.
     c. For N=3, there are three sites that for maximum height could be merged first by 
        merging two of them into a tree of height 1 and then merging that with the remaining
        site producing a tree of height 1, since the shorter tree is always merged into the 
        taller by WeightedQuickUnionByHeightUF, and lg(3) > 1.
     d. For N=4, for maximum height two trees of height 1 could be formed and then merged 
        into a tree of height 2, and lg(4) = 2. 
     e. For N=5 the situation of N=3 essentially repeats. As for N=4, for maximum height two 
        trees of height 1 could be merged into a tree of height 2 into which is merged the 
        remaining site resulting in a tree of height 2, and lg(5) > 2.   
  2. Suppose an upper bound on the tree height for N/2 sites is lg(N/2). For N sites, to get
     maximum height two trees of N/2 sites and max height lg(N/2) could be merged and the 
     resulting tree would have a max height of 1+lg(N/2) = lg(N).
  3. Therefore, for WeightedQuickUnionByHeightUF, lg(N) is an upper bound on tree height for 
     trees produced from N sites.
  
  I tested this experimentally and found WeightedQuickUnionByHeightUF created no trees with 
  height > lg(size) when taking input from mediumUF.txt and largeUF.txt. The test class is 
  WeightedQuickUnionByHeightInstrumentedUF which is WeightedQuickUnionByHeight modified with 
  added functionality in union() to report when a new larger tree has a height greater than 
  the lg of its size along with the ordinal number of the pair for which this occurred.
  
 */

public class Ex1514WeightedQuickUnionByHeight {

  public static class WeightedQuickUnionByHeightUF {
    // this class does path compression in union()
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
       component given by its root which is a site with a value equal to its index in 
       id as implemented in find, i.e. if id[i] = i then i is a root == component */
    public byte[] ht; /* height of trees (site indexed). the max height is < 31 since 
                         for a weighted quick-union tree with 2**x nodes, x is the 
                         height, but when x==31 2**x > Integer.Max_Value == (2**31)-1 */ 
    private int count; // number of components
    
    public WeightedQuickUnionByHeightUF(int N) {
      /* initialize component id array so that each site is in the component with the 
        same name, i.e. is its own root */
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N]; ht = new byte[N];
      for (int i = 0; i < N; i++) {
        id[i] = i;
      }
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
      // Make smaller root point to larger one.
      if (ht[i] < ht[j]) { // ht[j] doesn't increase when shorter tree i is merged with it 
        id[i] = j;
      } else if (ht[j] < ht[i]) { // ht[i] doesn't increase when shorter tree j is merged with it 
        id[j] = i;
      } else { // now ht[i] is incremented by 1 since j is a tree of the same height under it
        id[j] = i; ht[i]++;
      }
      count--;
    }
    
    public void printArray() {
      System.out.println("ht"+arrayToString(ht,5000,1,1));
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }
  
  public static class WeightedQuickUnionByHeightInstrumentedUF {
    // same as WeightedQuickUnionByHeightUF but instrumented with int[] sz for
    // verification that an upper bound of tree height is lg(N)
    private int[] id; 
    /* the ith location in id is the ith site while it's value is a site in the same 
       component given by its root which is a site with a value equal to its index in 
       id as implemented in find, i.e. if id[i] = i then i is a root == component */
    public byte[] ht; /* height of trees (site indexed). the max height is < 31 since 
                         for a weighted quick-union tree with 2**x nodes, x is the 
                         height, but when x==31 2**x > Integer.Max_Value == (2**31)-1 */
    private int[] sz; // size of component for roots (site indexed)
    private int count; // number of components
    private int pairCount; // counter for the number of input pairs processed
    
    public WeightedQuickUnionByHeightInstrumentedUF(int N) {
      /* initialize component id array so that each site is in the component with the 
        same name, i.e. is its own root */
      if (N <= 0) throw new IllegalArgumentException("QuickUnionUF constructor: N must be >= 1");
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) {
        id[i] = i;
      }
      ht = new byte[N];
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
      pairCount++;
      int i = find(p);
      int j = find(q);
      if (i == j) return;
      // Make smaller root point to larger one.
      if (ht[i] < ht[j]) { // ht[j] doesn't increase when shorter tree i is merged with it 
        id[i] = j; sz[j] += sz[i]; 
      } else if (ht[j] < ht[i]) { // ht[i] doesn't increase when shorter tree j is merged with it 
        id[j] = i; sz[i] += sz[j];
      } else { // now ht[i] is incremented by 1 since j is a tree of the same height under it
        id[j] = i; ht[i]++; sz[i] += sz[j];
        // test if new larger tree has height > lg(size)
        if (ht[i] > lg(sz[i])) 
          System.out.println("pair="+pairCount+"; tree "+i+": height="+ht[i]+", size="+sz[i]);
      }
      count--;
    }
    
    public void printArray() {
      System.out.println("ht"+arrayToString(ht,5000,1,1));
      System.out.println("id"+arrayToString(id,5000,1,1));
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    
    WeightedQuickUnionByHeightInstrumentedUF uf = null;
    Scanner sc; String[] d; int p; int q; 
    
    // test of tree too high using WeightedQuickUnionByHeightInstrumentedUF with largeUF.txt
    sc = new Scanner(new File("largeUF.txt"));
    if (sc.hasNextLine()) {
      uf = new WeightedQuickUnionByHeightInstrumentedUF(Integer.parseInt(sc.nextLine().trim()));  
    } else System.exit(0);
    System.out.println("test of tree too high using WeightedQuickUnionByHeightInstrumentedUF with largeUF.txt:");
    System.out.println("=======================================================================================");
    while(sc.hasNextLine()) {
      d = sc.nextLine().trim().split("\\s+");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      uf.union(p, q);
    }
    sc.close();
//    // no ouput
//    
    // test of tree too high using WeightedQuickUnionByHeightInstrumentedUF with mediumUF.txt
    sc = new Scanner(new File("mediumUF.txt"));
    if (sc.hasNextLine()) {
      uf = new WeightedQuickUnionByHeightInstrumentedUF(Integer.parseInt(sc.nextLine().trim()));  
    } else System.exit(0);
    System.out.println("test of tree too high using WeightedQuickUnionByHeightInstrumentedUF with mediumUF.txt:");
    System.out.println("=======================================================================================");
    while(sc.hasNextLine()) {
      d = sc.nextLine().trim().split("\\s+");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      uf.union(p, q);
    }
    sc.close();
    // no output

//    WeightedQuickUnionByHeightUF uf2 = new WeightedQuickUnionByHeightUF(10);
//    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
//    System.out.println("WeightedQuickUnionByHeightUF:");
//    System.out.println("using input \"(9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2)\"");
//    uf2.printArray();
//    for (String s : sa) {
//      d = s.split("-");
//      p = Integer.parseInt(d[0]); 
//      q = Integer.parseInt(d[1]);
//      if (uf2.connected(p, q)) continue;
//      uf2.union(p, q);
//      System.out.println("\n"+p + " " + q);
//      System.out.println("count="+uf2.count());
//      uf2.printArray();
//    }
//    
//  WeightedQuickUnionByHeightUF uf2 = new WeightedQuickUnionByHeightUF(10);
//  String[] sa = "0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9".split("\\s+");
//  System.out.println("WeightedQuickUnionByHeightUF:");
//  System.out.println("using input \"(0-1 1-2 2-3 3-4 4-5 5-6 6-7 7-8 8-9)\"");
//  uf2.printArray();
//  for (String s : sa) {
//    d = s.split("-");
//    p = Integer.parseInt(d[0]); 
//    q = Integer.parseInt(d[1]);
//    if (uf2.connected(p, q)) continue;
//    uf2.union(p, q);
//    System.out.println("\n"+p + " " + q);
//    System.out.println("count="+uf2.count());
//    uf2.printArray();
//  }
    
/*
    WeightedQuickUnionByHeightUF:
    using input "(9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2)"
    ht[0,0,0,0,0,0,0,0,0,0]
    id[0,1,2,3,4,5,6,7,8,9]
    
    9 0
    count=9
    ht[0,0,0,0,0,0,0,0,0,1]
    id[9,1,2,3,4,5,6,7,8,9]
    
    3 4
    count=8
    ht[0,0,0,1,0,0,0,0,0,1]
    id[9,1,2,3,3,5,6,7,8,9]
    
    5 8
    count=7
    ht[0,0,0,1,0,1,0,0,0,1]
    id[9,1,2,3,3,5,6,7,5,9]
    
    7 2
    count=6
    ht[0,0,0,1,0,1,0,1,0,1]
    id[9,1,7,3,3,5,6,7,5,9]
    
    2 1
    count=5
    ht[0,0,0,1,0,1,0,1,0,1]
    id[9,7,7,3,3,5,6,7,5,9]
    
    5 7
    count=4
    ht[0,0,0,1,0,2,0,1,0,1]
    id[9,7,7,3,3,5,6,5,5,9]
    
    0 3
    count=3
    ht[0,0,0,1,0,2,0,1,0,2]
    id[9,7,7,9,3,5,6,5,5,9]
    
    4 2
    count=2
    ht[0,0,0,1,0,2,0,1,0,3]
    id[9,7,7,9,3,9,6,5,5,9]     6        9
                                       0,3,  5
                                         4  7, 8
                                           1,2
*/  
  
  
  
  }
}
