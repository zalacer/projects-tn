package ds;

import static v.ArrayUtils.arrayToString;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/*
  Based on http://algs4.cs.princeton.edu/15uf/QuickFindUF.java.html modifed for 
  exercise 1.5.1 on p235 of Algorithms 4ed PDF version.
  
  This implementation uses quick find. Initializing a data structure with N sites
  takes linear time. Afterwards, the find, connected, and count operations take 
  constant time but the union operation takes linear time.
 */

public class QuickFindUF {
  private int[] id;    // id[i] = component identifier of i
  private int count;   // number of components
  private int idArrayAccesses = 0; // counter for id accesses after initialization

  public QuickFindUF(int N) {
    count = N;
    id = new int[N];
    for (int i = 0; i < N; i++)
      id[i] = i;
  }

  public int count() {
    return count;
  }

  public int find(int p) {
    validate(p);
    idArrayAccesses++;
    return id[p];
  }

  // validate that p is a valid index
  private void validate(int p) {
    int N = id.length;
    if (p < 0 || p >= N) {
      throw new IndexOutOfBoundsException("index " + p + " is not between 0 and " + (N-1));
    }
  }

  public boolean connected(int p, int q) {
    validate(p);
    validate(q);
    idArrayAccesses+=2;
    return id[p] == id[q];
  }

  public void union(int p, int q) {
    validate(p);
    validate(q);
    int pID = id[p];   // needed for correctness
    int qID = id[q];   // to reduce the number of array accesses
    idArrayAccesses+=2;
    // p and q are already in the same component
    if (pID == qID) return;

    for (int i = 0; i < id.length; i++) {
      if (id[i] == pID) id[i] = qID;
      idArrayAccesses+=2; 
    }
    count--;
  }
  
  public void printIdArrayAccesses() {
    System.out.println(idArrayAccesses);
  }
  
  public void printIdArray() {
    System.out.println("id"+arrayToString(id,500,1,1));
  }

  /**
   * Reads in a sequence of pairs of integers (between 0 and N-1) from standard input, 
   * where each integer represents some site;
   * if the sites are in different components, merge the two components
   * and print the pair to standard output.
   */
  public static void main(String[] args) {
    int N = StdIn.readInt();
    QuickFindUF uf = new QuickFindUF(N);
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

