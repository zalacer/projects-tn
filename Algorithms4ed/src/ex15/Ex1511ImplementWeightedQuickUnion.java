package ex15;

import static v.ArrayUtils.arrayToString;

/*
  p.236
  1.5.11  Implement weighted quick-find, where you always change the  id[] entries of
  the smaller component to the identifier of the larger component. How does this change
  affect performance?
  
  I think you mean weighted quick-union since there's no weighted quick-find mentioned
  in the text.
  
  It's odd to ask this now since 1.5.4 requires an implementation of it. There's no
  significant difference between by cutting and pasting from the text or using your code 
  from the downloads for the text or getting it online.
  
  For weighted quick-union the order of growth of both find and union is lgN for N sites
  according to the table on p231. This is an improvement over quick-union when tree
  heights are > lgN, since it has tree height order of growth for both according to the 
  same table.
  
 */

public class Ex1511ImplementWeightedQuickUnion {

  public static interface UF {
    // interface for UF (union-find) classes
    public void union(int p, int q);
    public int find(int p);
    public boolean connected(int p, int q);
    public int count();
    public void printArrays();
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

  public static void main(String[] args) {
    
    UF uf = new WeightedQuickUnionUF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
    System.out.println("WeightedQuickUnionUF:");
    uf.printArrays();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("\n"+p + " " + q);
      System.out.println("count="+uf.count());
      uf.printArrays();
    }
 
/*    
  WeightedQuickUnionUF:
  sz[1,1,1,1,1,1,1,1,1,1]
  id[0,1,2,3,4,5,6,7,8,9]
  
  9 0
  count=9
  sz[1,1,1,1,1,1,1,1,1,2]
  id[9,1,2,3,4,5,6,7,8,9]     1 2 3 4 5 6 7 8 9 
                                              |
                                              0
  3 4
  count=8
  sz[1,1,1,2,1,1,1,1,1,2]
  id[9,1,2,3,3,5,6,7,8,9]     1 2 3 5 6 7 8 9
                                  |         |
                                  4         0
  5 8
  count=7
  sz[1,1,1,2,1,2,1,1,1,2]
  id[9,1,2,3,3,5,6,7,5,9]     1 2 3 5 6 7 9
                                  | |     |
                                  4 8     0
  7 2
  count=6
  sz[1,1,1,2,1,2,1,2,1,2]
  id[9,1,7,3,3,5,6,7,5,9]     1 3 5 6 7 9
                                | |   | |
                                4 8   2 0
  2 1
  count=5
  sz[1,1,1,2,1,2,1,3,1,2]
  id[9,7,7,3,3,5,6,7,5,9]     3 5 6  7   9
                              | |   / \  |
                              4 8  1   2 0
  5 7
  count=4
  sz[1,1,1,2,1,2,1,5,1,2]
  id[9,7,7,3,3,7,6,7,5,9]     3 6  7   9
                              |   /|\  |
                              4  1 5 2 0
                                   |
                                   8
  0 3
  count=3
  sz[1,1,1,2,1,2,1,5,1,4]
  id[9,7,7,9,3,7,6,7,5,9]     6   7     9
                                 /|\   / \
                                1 5 2 0   3
                                  |       |
                                  8       4
  4 2
  count=2
  sz[1,1,1,2,1,2,1,9,1,4]
  id[9,7,7,9,3,7,6,7,5,7]     6      7
                                    /|\
                                   / | \
                                  / /|  \
                                 1 2 5   9
                                     |  / \
                                     8 0   3
                                           |
                                           4
                                        
*/          
    
    
 
  }

}
