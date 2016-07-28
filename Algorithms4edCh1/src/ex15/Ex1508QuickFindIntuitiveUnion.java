package ex15;

import static v.ArrayUtils.pa;

/*
 1.5.8  Give a counterexample that shows why this intuitive implementation of union()
 for quick-find is not correct:
 
    public void union(int p, int q) {
      if (connected(p, q)) return;
      // Rename p’s component to q’s name.
      for (int i = 0; i < id.length; i++)
        if (id[i] == id[p]) id[i] = id[q];
      count--;
    }
    
    The problem with this is that the for loop can change the value of id[i] while
    it's running so that after it's been changed further elements (sites) that initially
    matched id[p] no longer match it and aren't changed.
    
    Suppose id == [0,1,2,4,4,8,6,2,8,0], then for input pair (2,1), p==2 and id[p]==2 so id[2] 
    is changed to id[1]===1, but now id[p]==1 and it no longer equals id[7]==2 and the result 
    is id==[0,1,1,4,4,8,6,2,8,0], that, as a tree looks like:
        0 1 4 6 8
        9 2 3   5
          7
    and has component count==5.
    
    If (5,7) is input, p==5 and id[p]=8 so it's changed to id[q]==id[7]==2, but since now 
    id[p] = 2 nothing else that matches it will be changed because id[p]==id[q], in particular 
    id[8]==8 is left as dangling component and the result is id == [0,1,1,4,4,2,6,2,8,0], that, 
    as a tree looks like:
        0  1  4 6 8 
        9  2  3
          7,5
    and which is assigned component count==5-1==4 by union by actually has 5 components.
    
    A full trace of this is shown below for the same input as given in exercises 1.5.1-1,5.3
    using IntuitiveQuickFindUF and preceded with a trace using QuickFindUF for reference where 
    both these classes are included below.
 
 */

public class Ex1508QuickFindIntuitiveUnion {

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
