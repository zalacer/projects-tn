package ds;

import static v.ArrayUtils.arrayToString;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/*
  Based on http://algs4.cs.princeton.edu/15uf/WeightedQuickUnionUF.java.html modifed for 
  exercise 1.5.3 on p235 of Algorithms 4ed PDF version.
  
  This implementation uses weighted quick union by size (without path compression).
  Initializing a data structure with N sites takes linear time. Afterwards, the union, 
  find, and connected operations  take logarithmic time (in the worst case) and the
  count operation takes constant time.
 */

public class WeightedQuickUnionUF {
  private int[] parent;   // parent[i] = parent of i
  private int[] size;     // size[i] = number of sites in subtree rooted at i
  private int count;      // number of components
  private int parentArrayAccesses = 0; // counter for id accesses after initialization
  private int sizeArrayAccesses = 0;

  public WeightedQuickUnionUF(int N) {
    count = N;
    parent = new int[N];
    size = new int[N];
    for (int i = 0; i < N; i++) {
      parent[i] = i;
      size[i] = 1;
    }
  }


  public int count() {
    return count;
  }

  public int find(int p) {
    validate(p);
    while (p != parent[p])
      p = parent[p];
    parentArrayAccesses+=2;
    return p;
  }

  // validate that p is a valid index
  private void validate(int p) {
    int N = parent.length;
    if (p < 0 || p >= N) {
      throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (N-1));  
    }
  }

  public boolean connected(int p, int q) {
    return find(p) == find(q);
  }

  public void union(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    if (rootP == rootQ) return;

    // make smaller root point to larger one
    if (size[rootP] < size[rootQ]) {
      parent[rootP] = rootQ;
      size[rootQ] += size[rootP];
    } else {
      parent[rootQ] = rootP;
      size[rootP] += size[rootQ];
    }
    sizeArrayAccesses+=4;
    parentArrayAccesses++;
    count--;
  }
  
  public void printParentArrayAccesses() {
    System.out.println("parentArrayAccesses="+parentArrayAccesses);
  }
  
  public void printSizeArrayAccesses() {
    System.out.println("sizeArrayAccesses="+sizeArrayAccesses);
  }
  
  public void printTotalArrayAccesses() {
    System.out.println("totalArrayAccesses="+(parentArrayAccesses+sizeArrayAccesses));
  }
  
  public void printArrays() {
    System.out.println("size"+arrayToString(size,500,1,1));
    System.out.println("parent"+arrayToString(parent,500,1,1));
  }

  /**
   * Reads in a sequence of pairs of integers (between 0 and N-1) from standard input, 
   * where each integer represents some object;
   * if the sites are in different components, merge the two components
   * and print the pair to standard output.
   */
  public static void main(String[] args) {
    int N = StdIn.readInt();
    WeightedQuickUnionUF uf = new WeightedQuickUnionUF(N);
    while (!StdIn.isEmpty()) {
      int p = StdIn.readInt();
      int q = StdIn.readInt();
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      StdOut.println(p + " " + q);
    }
    StdOut.println(uf.count() + " components");
  }

}
