package dsuf;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/*
  This implementation uses weighted quick union by size (without path compression).
  Initializing a data structure with N sites takes linear time. Afterwards, the union, 
  find, and connected operations  take logarithmic time (in the worst case) and the
  count operation takes constant time.
 */

public class WeightedQuickUnionUF {
  private int[] parent;   // parent[i] = parent of i
  private int[] size;     // size[i] = number of sites in subtree rooted at i
  private int count;      // number of components

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
    }
    else {
      parent[rootQ] = rootP;
      size[rootP] += size[rootQ];
    }
    count--;
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
