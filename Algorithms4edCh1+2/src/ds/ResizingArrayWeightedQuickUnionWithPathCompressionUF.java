package ds;

import static v.ArrayUtils.arrayToString;
import static v.ArrayUtils.take;

// Exercise 1.5.20 p238

public class ResizingArrayWeightedQuickUnionWithPathCompressionUF {
  // this class does path compression in union()
  private int[] id; 
  /* the ith location in id is the ith site while it's value is a site in the same 
     component given by its root which is a site with a value equal to its index in 
     id as implemented in find, i.e. if id[i] = i then i is a root == component */
  private int[] sz; // size of component for roots (site indexed)
  private int count; // number of components 
  private int active; // tracks the number of active indices in id and sz
  
  public ResizingArrayWeightedQuickUnionWithPathCompressionUF() {
   // create id and sz arrays of length 2 and all elements initialized to 0
    id = new int[2];
    sz = new int[2];
    active = 0;
    count = 0;
  }
  
  public ResizingArrayWeightedQuickUnionWithPathCompressionUF(int N) {
    /* initialize id array with N components in increasing numerical order starting
     * with 0 up to N-1. since they are components, each is its own root, i.e. for 
     * each 0 <= i < N, id[i] = i.
     * same name, i.e. is its own root */
    if (N <= 0) throw new IllegalArgumentException(
        "QuickUnionUF constructor: N must be >= 1");
    count = N;
    id = new int[N];
    for (int i = 0; i < N; i++) {
      id[i] = i;
    }
    sz = new int[N];
    for (int i = 0; i < N; i++) sz[i] = 1;
    active = N;
  }
  
  public void active() {
    // for debugging
    System.out.println("active="+active);
  }
  
  public void idLength() {
    // for debugging
    System.out.println("id.length="+id.length);
  }
  
  public void szLength() {
    // for debugging
    System.out.println("sz.length="+sz.length);
  }
  
  private void resize() {
    // double the length the id and sz arrays when active == id.length
    int newLength = 0;
    if (active == id.length) {
      newLength = 2*id.length;
    } else return;
    int[] temp = new int[newLength];
    for (int i = 0; i < id.length; i++) temp[i] = id[i];
    id = temp;
    temp = new int[newLength];
    for (int i = 0; i < sz.length; i++) temp[i] = sz[i];
    sz = temp;
  }
  
  public int add() {
    // add a site
    if (active == id.length) resize();
    int e = active++;
    id[e] = e;
    sz[e] = 1;
    count++;
    return e;
  }
  
  public int[] add(int b) {
    // bulk add b sites
    if (b < 1) throw new IllegalArgumentException("add/newSite: b must be > 0");
    int[] r = new int[b]; int c = 0;
    for (int i = 0; i < b; i++) {
      if (active == id.length) resize();
      int e = active++;
      id[e] = e;
      sz[e] = 1;
      count++;
      r[c++] = e;
    }
    return r;
  }
  
  public int newSite() {
    // add a site
    return add();
  }
  
  public int[] newSite(int b) {
    // bulk add b sites
    return add(b);
  }
  
  public int count() { return count; }

  public boolean connected(int p, int q) {
    if (p < 0 || p >= active) 
      throw new IllegalArgumentException("connected: p is out of range");
    if (q < 0 || q >= active) 
      throw new IllegalArgumentException("connected: q is out of range");
    return find(p) == find(q); 
  }

  public int find(int p) {
    // Find the root of p, i.e. its component.
    if (p < 0 || p >= active) 
      throw new IllegalArgumentException("find: p is out of range");
    while (p != id[p]) p = id[p];
    return p;
  }
  
  public void union(int p, int q) {
    /* put p and q into the same component.
       after all input pairs have been processed, components are identified  
       as those indices i in id such that id[i] == i and also called roots. */
    if (p < 0 || p >= active) 
      throw new IllegalArgumentException("union: p is out of range");
    if (q < 0 || q >= active) 
      throw new IllegalArgumentException("union: q is out of range");
    int i = find(p);
    int j = find(q);
    if (i == j) return;
    int[] a = {p,q}; int t; // for path compression
    // Make smaller root point to larger one.
    if (sz[i] < sz[j]) {
      id[i] = j; sz[j] += sz[i]; 
      // path compression
      for (int x : a)
        while (true) {
          if (id[x] == j) break;
          t = id[x]; id[x] = j; x = t;
        }
    } else { 
      id[j] = i; sz[i] += sz[j];
      // path compression
      for (int x : a)
        while (true) {
          if (id[x] == i) break;
          t = id[x]; id[x] = i; x = t;
        }
    } 
    count--;
  }
  
  public void printArray() {
    // for debugging prints the entire arrays
    System.out.println("sz"+arrayToString(sz,5000,1,1));
    System.out.println("id"+arrayToString(id,5000,1,1));
  }
  
  public void printArrays() {
    // prints only the active portion of the arrays
    System.out.println("sz"+arrayToString(take(sz,active),5000,1,1));
    System.out.println("id"+arrayToString(take(id,active),5000,1,1));
  }
  
  public static void main(String[] args) {

  }

}
