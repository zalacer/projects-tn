package ds;

import static v.ArrayUtils.arrayToString;

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

@SuppressWarnings("unused")
public class UF {

  private int[] parent;  // parent[i] = parent of i
  private byte[] rank;   // rank[i] = rank of subtree rooted at i (never more than 31)
  private int count;     // number of components
  private int parentArrayAccesses = 0; // counter for id accesses after initialization
  private int rankArrayAccesses = 0; 


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
      parentArrayAccesses+=4;
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
    if (rank[rootP] < rank[rootQ]) {
      parent[rootP] = rootQ;
      parentArrayAccesses++;
      rankArrayAccesses+=2;
    } else if (rank[rootP] > rank[rootQ]) {      
      parent[rootQ] = rootP;
      parentArrayAccesses++;
      rankArrayAccesses+=4;
    } else {
      parent[rootQ] = rootP;
      rank[rootP]++;
      parentArrayAccesses++;
      rankArrayAccesses+=5;
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
  
  public void printParentArrayAccesses() {
    System.out.println("parentArrayAccesses="+parentArrayAccesses);
  }
  
  public void printRankArrayAccesses() {
    System.out.println("rankArrayAccesses="+rankArrayAccesses);
  }
  
  public void printTotalArrayAccesses() {
    System.out.println("totalArrayAccesses="+(parentArrayAccesses+rankArrayAccesses));
  }
  
  public void printArrays() {
    System.out.println("parent"+arrayToString(parent,500,1,1));
    System.out.println("rank"+arrayToString(rank,500,1,1));
  }


  /**
   * Reads in a an integer <tt>N</tt> and a sequence of pairs of integers
   * (between <tt>0</tt> and <tt>N-1</tt>) from standard input, where each integer
   * in the pair represents some site;
   * if the sites are in different components, merge the two components
   * and print the pair to standard output.
   */
  public static void main(String[] args) {
    
    UF uf = new UF(10);
    String[] sa = "9-0 3-4 5-8 7-2 2-1 5-7 0-3 4-2".split("\\s+");
    String[] d; int p; int q;
    uf.printParentArrayAccesses(); // counting accesses after initialization
    uf.printRankArrayAccesses();
    uf.printTotalArrayAccesses();
    uf.printArrays();;
    System.out.println();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("union("+p+", "+q+")");
      uf.printParentArrayAccesses();
      uf.printRankArrayAccesses();
      uf.printTotalArrayAccesses();
      uf.printArrays();;
      System.out.println();
    }
    
/*
    parentArrayAccesses=0
    rankArrayAccesses=0
    totalArrayAccesses=0
    parent[0,1,2,3,4,5,6,7,8,9]
    rank[0,0,0,0,0,0,0,0,0,0]
    
    union(9, 0)
    parentArrayAccesses=1
    rankArrayAccesses=5
    totalArrayAccesses=6
    parent[9,1,2,3,4,5,6,7,8,9]
    rank[0,0,0,0,0,0,0,0,0,1]
    
    union(3, 4)
    parentArrayAccesses=2
    rankArrayAccesses=10
    totalArrayAccesses=12
    parent[9,1,2,3,3,5,6,7,8,9]
    rank[0,0,0,1,0,0,0,0,0,1]
    
    union(5, 8)
    parentArrayAccesses=3
    rankArrayAccesses=15
    totalArrayAccesses=18
    parent[9,1,2,3,3,5,6,7,5,9]
    rank[0,0,0,1,0,1,0,0,0,1]
    
    union(7, 2)
    parentArrayAccesses=4
    rankArrayAccesses=20
    totalArrayAccesses=24
    parent[9,1,7,3,3,5,6,7,5,9]
    rank[0,0,0,1,0,1,0,1,0,1]
    
    union(2, 1)
    parentArrayAccesses=13
    rankArrayAccesses=24
    totalArrayAccesses=37
    parent[9,7,7,3,3,5,6,7,5,9]
    rank[0,0,0,1,0,1,0,1,0,1]
    
    union(5, 7)
    parentArrayAccesses=14
    rankArrayAccesses=29
    totalArrayAccesses=43
    parent[9,7,7,3,3,5,6,5,5,9]
    rank[0,0,0,1,0,2,0,1,0,1]
    
    union(0, 3)
    parentArrayAccesses=23
    rankArrayAccesses=34
    totalArrayAccesses=57
    parent[9,7,7,9,3,5,6,5,5,9]
    rank[0,0,0,1,0,2,0,1,0,2]
    
    union(4, 2)
    parentArrayAccesses=40
    rankArrayAccesses=39
    totalArrayAccesses=79
    parent[9,7,5,9,9,9,6,5,5,9]
    rank[0,0,0,1,0,2,0,1,0,3]

*/



//    int N = StdIn.readInt();
//    UF uf = new UF(N);
//    while (!StdIn.isEmpty()) {
//      int p = StdIn.readInt();
//      int q = StdIn.readInt();
//      if (uf.connected(p, q)) continue;
//      uf.union(p, q);
//      StdOut.println(p + " " + q);
//    }
//    StdOut.println(uf.count() + " components");
  }
}
