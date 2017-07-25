package graph;

import static v.ArrayUtils.*;

import java.security.SecureRandom;

import ds.BagX;
import ds.Stack;
import edu.princeton.cs.algs4.StdOut;

public class ErdosRenyiGraph {
  private static final String NEWLINE = System.getProperty("line.separator");
  private int V;
  private int E;
  private BagX<Integer>[] adj;
  private boolean[] marked;  
  private int[] id; // component ids
  private int[] size;  // size[id] = number of vertices in given component
  private int count = 0; // number of connected components
  private Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()
  
  
  public ErdosRenyiGraph(int v, int e) { //, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "v is < 0");
    if (e < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "e is < 0");
    V = v;
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    int[] x = r.ints(2*e,0,v).toArray();
//    System.out.print("x="); par(x);
    if (v == 0) return;
    adj = ofDim(BagX.class,v);
    for (int i = 0; i < v; i++)  adj[i] = new BagX<Integer>();
    for (int i = 0; i < x.length-1; i+=2) {
      int a = x[i], b = x[i+1];
      addEdge(a,b);      
    }
    System.out.println("E="+E);
    search();
  }

  public int V() { return V; }

  public int E() { return E; }
  
  public void setE(int e) { E = e; }
  
  public void setV(int v) { V = v; }

  public BagX<Integer>[] adj() { return adj.clone(); }
  
  public void setAdj(BagX<Integer>[] ba) { adj = ba; }
  
  public boolean[] marked() { return marked == null ? null : marked.clone(); }
  
  public void setMarked(boolean[] ba) { marked = ba; }
  
  public int[] id() { return id == null ? null : id.clone(); }
  
  public void setId(int[] ia) { id = ia; }
  
  public int[] size() { return size == null ? null : size.clone(); }
  
  public void setSize(int[] ia) { size = ia; }
  
  public Stack<Integer> cycle() { return cycle; }
  
  public int[][] hArray() {
    // for hamiltonian cycle detection using HamiltonianCycle
    int[][] a = new int[V][V];
    for (int i = 0; i< V; i++) 
      for (int j : adj[i]) a[i][j] = 1;
    return a;
  }

  public int count() {
    // return the number of connected components
    return count; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
  }

  private void dfs(int v) {
    marked[v] = true;
    id[v] = count;
    size[count]++;
    for (int w : adj[v]) if (!marked[w]) dfs(w);
  } 
  
  public void search() {
    // count connected components from p544 / CC
    count = 0;    
    marked = new boolean[V];
    id = new int[V];
    size = new int[V];
    for (int s = 0; s < V; s++) if (!marked[s]) { dfs(s); count++; }
  }

  public boolean connected(int v, int w) { return id[v] == id[w]; }

  public void addEdge(int v, int w) {
    //validateVertex(v);
    validateVertex(w);
    E++;
    adj[v].add(w);
    adj[w].add(v);
  }

  public boolean removeEdge(int v, int w) {
    // remove edge between v and w if it exists and is represented by
    // entries in both adj[v] and adj[w]. return true only if the edge
    // is found and removed else return false.
    validateVertex(v); validateVertex(w);
    BagX<Integer> bv = adj[v]; boolean fw = false; int iw = -1;  
    BagX<Integer> bw = adj[w]; boolean fv = false; int iv = -1;
    int c = 0;
    for (Integer i : bv) {
      if (i != null && i == w) { iw = c; fw = true; break; }
      c++;
    }
    if (fw == false) return false;
    c = 0;
    for (Integer i : bw) {
      if (i != null && i == v) { iv = c; fv = true; break; }
      c++;
    }
    if (fv == false) return false;
    bv.remove(iw); bw.remove(iv);
    E--;
    return true;
  }
  
  public Iterable<Integer> adj(int v) {
    validateVertex(v);
    return adj[v];
  }

  public int degree(int v) {
    validateVertex(v);
    return adj[v].size();
  }

  public int maxDegree() {
    int max = 0, d = 0;
    for (int v = 0; v < V; v++) { d = degree(v); if (d > max) max = d; }
    return max;
  }

  public double avgDegree() { return 1. * E / V; }

  public boolean hasEdge(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    for (int x : adj[v]) if (x == w) return true;
    return false;
  }

  public int numberOfSelfLoops() {
    // from GraphClient
    int count = 0;
    for (int v = 0; v < V; v++)
      for (int w : adj(v))
        if (v == w) count++;
    return count/2; // each edge counted twice
  }

  public boolean hasSelfLoop() {
    // from Cycle
    for (int v = 0; v < V; v++) {
      for (int w : adj[v]) {
        if (v == w) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(v);
          return true;
        }
      }
    }
    return false;
  }

  public Iterable<Integer> selfLoop() { return cycle; }

  public boolean hasParallelEdges() {
    // from Cycle
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      // check for parallel edges incident to v
      for (int w : adj[v]) {
        if (m[w]) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(w);
          cycle.push(v);
          return true;
        }
        m[w] = true;
      }
      // reset so marked[v] = false for all v
      for (int w : adj[v]) {
        m[w] = false;
      }
    }
    return false;
  }

  public boolean hasCycle() { return hasParallelEdges(); }

  public Iterable<Integer> parallelEdges() { return cycle; }

  public Integer[] parallelEdgesArray() { return cycle.toArray(1); }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " vertices, " + E + " edges " + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (int w : adj[v]) {
        s.append(w + " ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }
  
  public static void main(String[] args) {

    ErdosRenyiGraph G = new ErdosRenyiGraph(12,21);
    StdOut.println(G);
    System.out.print("marked="); par(G.marked());
    System.out.println("avgDegree="+G.avgDegree());
    System.out.println("numberOfSelfLoops="+G.numberOfSelfLoops());
    System.out.println("count="+G.count());
    System.out.print("id"); par(G.id());
    System.out.print("adj="); par(G.adj());
    System.out.println("hasSelfLoop="+G.hasSelfLoop());
    if (G.hasSelfLoop()) System.out.println("selfLoop="+G.selfLoop());
    System.out.println("hasParallelEdges="+G.hasParallelEdges());
    if (G.hasParallelEdges()) System.out.print("parallelEdges="+G.parallelEdges());
  }

}
