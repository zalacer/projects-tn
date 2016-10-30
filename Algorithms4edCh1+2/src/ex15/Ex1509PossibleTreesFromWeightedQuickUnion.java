package ex15;

import static v.ArrayUtils.pa;

/*
  p.235
  1.5.9  Draw the tree corresponding to the  id[] array depicted at
  right. Can this be the result of running weighted quick-union?
  Explain why this is impossible or give a sequence of operations
  that results in this array.
  
  id[1,1,3,1,5,6,1,3,4,5]
  
        1
       /|\
      / | \
     0  3  6
       / \  \
      2   7  5
            / \
           4   9  
           |
           8
           
  Re. proposition H on p229 the depth of any node (component) built by weighted quick-union
  for 10 sites is at most lg(10) < 4, but the tree corresponding to id[1,1,3,1,5,6,1,3,4,5]
  has depth 4, therefore it can't be the result of running weighted quick-union.         
           
  Proposition H. The depth of any node in a forest built by weighted quick-union for
  N sites is at most lg N.
  Proof:  We prove a stronger fact by (strong) induction: The height of every tree of
  size k in the forest is at most lg k. The base case follows from the fact that the tree
  height is 0 when k is 1. By the inductive hypothesis, assume that the tree height of a
  tree of size i is at most lg i for all i < k. When we combine a tree of size i with a tree
  of size j with i ? j and i ? j = k, we increase the depth of each node in the smaller set
  by 1, but they are now in a tree of size i ? j = k, so the property is preserved because
  1+ lg i = lg(i ? i ) ? lg(i ? j ) = lg k.              
        
 */

public class Ex1509PossibleTreesFromWeightedQuickUnion {

  public static interface UF {
    // interface for UF (union-find) classes
    public void union(int p, int q);
    public int find(int p);
    public boolean connected(int p, int q);
    public int count();
    public void printArray();
  }

  public static class QuickFindUF implements UF {
    private int[] id; // the ith location in id is the ith component while it's value is a site
    // if id[i] = j then site j is in component i
    private int count; // number of components

    public QuickFindUF(int N) { 
      if (N <= 0) throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
      // initialize component id array so that each site is in the component with the same number.
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) { return find(p) == find(q); }

    public int find(int p) {
     if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      return id[p]; 
    }

    public void union(int p, int q) {
      // put p and q into the same component.
      // after all input pairs have been processed, components are identified  
      // as those indices i in id such that id[i] == i.
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
          "union: p and q must both be >= 0 and <= id.length");
      int pID = find(p);
      int qID = find(q);
      // nothing to do if p and q are already in the same component.
      if (pID == qID) return;
      // rename p’s component to q’s name.
      for (int i = 0; i < id.length; i++) if (id[i] == pID) id[i] = qID;
      count--;
    }
    
    public void printArray() {
      pa(id);
    }
  }

  public static class IntuitiveQuickFindUF implements UF {
    // this UF version has the intuitive find() method given in the exercise statement
    private int[] id; // the ith location in id is the ith component while it's value is a site
    // if id[i] = j then site j is in component i
    private int count; // number of components

    public IntuitiveQuickFindUF(int N) { 
      if (N <= 0) throw new IllegalArgumentException("QuickFindUF constructor: N must be >= 1");
      // initialize component id array so that each site is in the component with the same number.
      count = N;
      id = new int[N];
      for (int i = 0; i < N; i++) id[i] = i;
    }

    public int count() { return count; }

    public boolean connected(int p, int q) { return find(p) == find(q); }

    public int find(int p) {
     if (p < 0 || p >= id.length) throw new IllegalArgumentException("find: p is out of range");
      return id[p]; 
    }

//    public void union(int p, int q) {
//      // put p and q into the same component.
//      // after all input pairs have been processed, components are identified  
//      // as those indices i in id such that id[i] == i.
//      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
//          "union: p and q must both be >= 0 and <= id.length");
//      int pID = find(p);
//      int qID = find(q);
//      // nothing to do if p and q are already in the same component.
//      if (pID == qID) return;
//      // rename p’s component to q’s name.
//      for (int i = 0; i < id.length; i++) if (id[i] == pID) id[i] = qID;
//      count--;
//    }
    
    public void union(int p, int q) {
      // this is the intuitive find() as given in the exercise statement
      if (p < 0 || p >= id.length || q < 0 || q >= id.length) throw new IllegalArgumentException(
        "union: p and q must both be >= 0 and <= id.length");
      if (connected(p, q)) return;
      // Rename p’s component to q’s name.
      for (int i = 0; i < id.length; i++)
        if (id[i] == id[p]) id[i] = id[q];
      count--;
    }
    
    public void printArray() {
      pa(id);
    }
    
  }

  public static void main(String[] args) {
    
    UF uf = new QuickFindUF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
//    System.out.println("QuickFindUF:");
//    uf.printArray();
//    for (String s : sa) {
//      d = s.split("-");
//      p = Integer.parseInt(d[0]); 
//      q = Integer.parseInt(d[1]);
//      if (uf.connected(p, q)) continue;
//      uf.union(p, q);
//      System.out.println("count="+uf.count());
//      System.out.println(p + " " + q);
//      uf.printArray();
//    }
//    System.out.println(uf.count() + " components");
//    System.out.println();
    
    uf = new IntuitiveQuickFindUF(10);
    System.out.println("IntuitiveQuickFindUF");
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
    System.out.println(uf.count() + " components");

/*
    QuickFindUF:
    int[0,1,2,3,4,5,6,7,8,9]
    9 0
    int[0,1,2,3,4,5,6,7,8,0]
    3 4
    int[0,1,2,4,4,5,6,7,8,0]
    5 8
    int[0,1,2,4,4,8,6,7,8,0]
    7 2
    int[0,1,2,4,4,8,6,2,8,0]        0 1 2 4 6 8
                                    9   7 3   5
    2 1
    int[0,1,1,4,4,8,6,1,8,0] X      0  1  4 6 8
                                    9 2,7 3   5
    5 7 
    int[0,1,1,4,4,1,6,1,1,0]        0    1    4 6
    0 3                             9 2,5,7,8 3 
    
    int[4,1,1,4,4,1,6,1,1,4]
    4 2
    int[1,1,1,1,1,1,6,1,1,1]
    2 components
    
    IntuitiveQuickFindUF
    int[0,1,2,3,4,5,6,7,8,9]
    9 0
    int[0,1,2,3,4,5,6,7,8,0]
    3 4
    int[0,1,2,4,4,5,6,7,8,0]
    5 8
    int[0,1,2,4,4,8,6,7,8,0]
    
    count=6
    7 2
    int[0,1,2,4,4,8,6,2,8,0]        0 1 2 4 6 8
                                    9   7 3   5
    count=5
    2 1                             
    int[0,1,1,4,4,8,6,2,8,0] X      0 1 4 6 8       here 8 is referenced by 5
                                    9 2 3   5
    count=4                           7
    5 7                                                          
    int[0,1,1,4,4,2,6,2,8,0]        0  1  4 6 8     now 8 is unreferenced (except by itself)
                                    9  2  3         until the end and count is incorrect
                                      7,5
    0 3
    int[4,1,1,4,4,2,6,2,8,0]
    4 2
    int[1,1,1,1,1,2,6,2,8,0]        
    2 components                            1 6 8   actually have 3 components
                                           /|
                                          / | 
                                         / /|
                                        / / | 
                                       / / /| 
                                      / / / / 
                                     0 2 3 4   
                                    / / \
                                   9 5   7
                                   
  Here is a trace of running IntuitiveQuickFindUF showing its output from count() for every pair:
  
  IntuitiveQuickFindUF
  int[0,1,2,3,4,5,6,7,8,9]
  
  9 0
  count=9
  int[0,1,2,3,4,5,6,7,8,0]
  
  3 4
  count=8
  int[0,1,2,4,4,5,6,7,8,0]
  
  5 8
  count=7
  int[0,1,2,4,4,8,6,7,8,0]
  
  7 2
  count=6
  int[0,1,2,4,4,8,6,2,8,0]
  
  2 1
  count=5
  int[0,1,1,4,4,8,6,2,8,0]
  
  5 7
  count=4
  int[0,1,1,4,4,2,6,2,8,0]
  
  0 3
  count=3
  int[4,1,1,4,4,2,6,2,8,0]
  
  4 2
  count=2
  int[1,1,1,1,1,2,6,2,8,0]
  2 components


*/ 
    
    
  
  }

}
