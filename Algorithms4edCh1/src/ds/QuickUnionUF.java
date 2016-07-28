package ds;

import static v.ArrayUtils.arrayToString;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/*
  Based on http://algs4.cs.princeton.edu/15uf/QuickUnionUF.java.html modifed for 
  exercise 1.5.2 on p235 of Algorithms 4ed PDF version.
  
  This implementation uses quick union. Initializing a data structure with N 
  sites takes linear time. Afterwards, the union, find, and connected
  operations take linear time (in the worst case) and the count operation takes 
  constant time.
 */

@SuppressWarnings("unused")
public class QuickUnionUF {
  private int[] parent;  // parent[i] = parent of i
  private int count;     // number of components
  private int parentArrayAccesses = 0; // counter for id accesses after initialization

  public QuickUnionUF(int N) {
    parent = new int[N];
    count = N;
    for (int i = 0; i < N; i++) {
      parent[i] = i;
    }
  }

  public int count() {
    return count;
  }

  public int find(int p) {
    validate(p);
    while (p != parent[p]) {
      p = parent[p];
      parentArrayAccesses+=2;
    }
    parentArrayAccesses++;
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
    parent[rootP] = rootQ;
    parentArrayAccesses++;
    count--;
  }
  
  public void printParentArrayAccesses() {
    System.out.println(parentArrayAccesses);
  }
  
  public void printParentArray() {
    System.out.println("parent"+arrayToString(parent,500,1,1));
  }
  
  public static void main(String[] args) {
//    int N = StdIn.readInt();
//    QuickUnionUF uf = new QuickUnionUF(N);
//    while (!StdIn.isEmpty()) {
//      int p = StdIn.readInt();
//      int q = StdIn.readInt();
//      if (uf.connected(p, q)) continue;
//      uf.union(p, q);
//      StdOut.println(p + " " + q);
//    }
//    StdOut.println(uf.count() + " components");
    
    QuickUnionUF uf = new QuickUnionUF(10);
    String[] sa = "0-1 0-2 0-3 0-4 0-5 0-6 0-7 0-8 0-9".split("\\s+");
    String[] d; int p; int q;
    uf.printParentArrayAccesses(); // counting accesses after initialization
    uf.printParentArray();
    System.out.println();
    for (String s : sa) {
      d = s.split("-");
      p = Integer.parseInt(d[0]); 
      q = Integer.parseInt(d[1]);
      if (uf.connected(p, q)) continue;
      uf.union(p, q);
      System.out.println("union("+p+", "+q+")");
      uf.printParentArrayAccesses();
      uf.printParentArray();
      System.out.println();
/*      
      0
      parent[0,1,2,3,4,5,6,7,8,9]
      
      union(0, 1)
      5
      parent[1,1,2,3,4,5,6,7,8,9]
      
      union(0, 2)
      14
      parent[1,2,2,3,4,5,6,7,8,9]
      
      union(0, 3)
      27
      parent[1,2,3,3,4,5,6,7,8,9]
      
      union(0, 4)
      44
      parent[1,2,3,4,4,5,6,7,8,9]
      
      union(0, 5)
      65
      parent[1,2,3,4,5,5,6,7,8,9]
      
      union(0, 6)
      90
      parent[1,2,3,4,5,6,6,7,8,9]
      
      union(0, 7)
      119
      parent[1,2,3,4,5,6,7,7,8,9]
      
      union(0, 8)
      152
      parent[1,2,3,4,5,6,7,8,8,9]
      
      union(0, 9)
      189
      parent[1,2,3,4,5,6,7,8,9,9]     9-8-7-6-5-4-3-2-1-0
                                      
        
        
        
*/
      
    }
  }


}

