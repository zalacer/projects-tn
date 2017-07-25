package ds;

import static v.ArrayUtils.arrayToString;

// re Exercise 1.5.13 p237

public class WeightedQuickUnionWithHalvingPathCompressionUF {
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

public static void main(String[] args) {
  // TODO Auto-generated method stub

}

}
