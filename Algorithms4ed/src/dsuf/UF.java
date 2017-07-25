package dsuf;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/*
  This implementation uses weighted quick union by rank with path compression
  by halving. Initializing a data structure with N sites takes linear time.
  Afterwards, the union, find, and connected operations take logarithmic time 
  (in the worst case) and the count operation takes constant time. Moreover, 
  the amortized time per union, find, and connected operation has inverse 
  Ackermann complexity.
*/

public class UF {

  private int[] parent;  // parent[i] = parent of i
  private byte[] rank;   // rank[i] = rank of subtree rooted at i (never more than 31)
  private int count;     // number of components

  public UF(int N) {
    if (N < 0) throw new IllegalArgumentException();
    count = N;
    parent = new int[N];
    rank = new byte[N];
    for (int i = 0; i < N; i++) {
      parent[i] = i;
      rank[i] = 0;
    }
  }

  public int find(int p) {
    validate(p);
    while (p != parent[p]) {
      parent[p] = parent[parent[p]];    // path compression by halving
      p = parent[p];
    }
    return p;
  }

  public int count() {
    return count;
  }

  public boolean connected(int p, int q) {
    return find(p) == find(q);
  }

  public void union(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    if (rootP == rootQ) return;

    // make root of smaller rank point to root of larger rank
    if      (rank[rootP] < rank[rootQ]) parent[rootP] = rootQ;
    else if (rank[rootP] > rank[rootQ]) parent[rootQ] = rootP;
    else {
      parent[rootQ] = rootP;
      rank[rootP]++;
    }
    count--;
  }

  // validate that p is a valid index
  private void validate(int p) {
    int N = parent.length;
    if (p < 0 || p >= N) {
      throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (N-1));  
    }
  }

  /**
   * Reads in a an integer <tt>N</tt> and a sequence of pairs of integers
   * (between <tt>0</tt> and <tt>N-1</tt>) from standard input, where each integer
   * in the pair represents some site;
   * if the sites are in different components, merge the two components
   * and print the pair to standard output.
   */
  public static void main(String[] args) {
    int N = StdIn.readInt();
    UF uf = new UF(N);
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
