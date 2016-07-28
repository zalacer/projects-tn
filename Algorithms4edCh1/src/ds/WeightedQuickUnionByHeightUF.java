package ds;

import static v.ArrayUtils.arrayToString;

// re Exercise 1.5.14 p237

public class WeightedQuickUnionByHeightUF {
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

  public static void main(String[] args) {

  }

}
