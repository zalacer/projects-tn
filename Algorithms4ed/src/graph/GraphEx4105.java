package graph;

import static v.ArrayUtils.par;

import java.util.NoSuchElementException;

import ds.BagQ;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// modified from Graph for ex4105 to eliminate self-loops and parallel edges

public class GraphEx4105 {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;
  private int E;
  private BagQ<Integer>[] adj;
  private boolean[] marked;  
  private int[] id; // component ids
  private int count = 0; // number of connected components
  private Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()

  @SuppressWarnings("unchecked")
  public GraphEx4105(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    adj = (BagQ<Integer>[]) new BagQ[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new BagQ<Integer>();
    }
  }

  @SuppressWarnings("unchecked")
  public GraphEx4105(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException("number of vertices in a Graph must be nonnegative");
      adj = (BagQ<Integer>[]) new BagQ[V];
      for (int v = 0; v < V; v++) {
        adj[v] = new BagQ<Integer>();
      }
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException("number of edges in a Graph must be nonnegative");
      for (int i = 0; i < E; i++) {
        int v = in.readInt();
        int w = in.readInt();
        validateVertex(v);
        validateVertex(w);
        if (v != w) addEdge(v, w); // do not add self-loop
      }
    }
    catch (NoSuchElementException e) {
      throw new IllegalArgumentException("invalid input format in Graph constructor", e);
    }
    // count connected components from p544 / CC
    marked = new boolean[V];
    id = new int[V];
    for (int s = 0; s < V; s++) if (!marked[s]) { dfs(s); count++; }
    while (hasParallelEdges()) {
      Integer[] p = parallelEdgesArray();
      //          System.out.print("parallelEdgesArray="); par(p);
      removeEdge(p[0],p[1]);
    }
  }

  public GraphEx4105(GraphEx4105 G) {
    this(G.V());
    this.E = G.E();
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<Integer> reverse = new Stack<Integer>();
      for (int w : G.adj[v]) {
        reverse.push(w);
      }
      for (int w : reverse) {
        adj[v].add(w);
      }
    }
    // count connected components from p544 / CC
    marked = new boolean[V];
    id = new int[V];
    for (int s = 0; s < V; s++) if (!marked[s]) { dfs(s); count++; }
    while (hasParallelEdges()) {
      Integer[] p = parallelEdgesArray();
      //          System.out.print("parallelEdgesArray="); par(p);
      removeEdge(p[0],p[1]);
    }
  }

  public int V() { return V; }

  public int E() { return E; }

  public BagQ<Integer>[] adj() { return adj.clone(); }

  public int[] id() { return id.clone(); }

  public int count() {
    // number of connected components
    return count; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
  }

  private void dfs(int v) {
    marked[v] = true;
    id[v] = count;
    for (int w : adj(v)) if (!marked[w]) dfs(w);
  }

  public boolean connected(int v, int w) { return id[v] == id[w]; }

  public void addEdge(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    E++;
    adj[v].add(w);
    //adj[w].add(v); do not add parallel edge
  }

  public boolean removeEdge(int v, int w) {
    validateVertex(v); validateVertex(w);
    BagQ<Integer> bv = adj[v]; boolean fw = false; int iw = -1;  
    BagQ<Integer> bw = adj[w]; boolean fv = false; int iv = -1;
    int c = 0;
    for (int i : bv) {
      if (i == w) { iw = c; fw = true; break; }
      c++;
    }
    if (fw == false) return false;
    c = 0;
    for (int i : bw) {
      if (i == v) { iv = c; fv = true; break; }
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

  public double avgDegree2() {
    double sum = 0;
    for (int v = 0; v < V; v++) sum += degree(v);
    return sum/V;
  }

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
      for (int w : adj(v)) {
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
      for (int w : adj(v)) {
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
      for (int w : adj(v)) {
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

    In in = new In(args[0]);
    GraphEx4105 G = new GraphEx4105(in);
    StdOut.println(G);
    System.out.println("avgDegree="+G.avgDegree());
    System.out.println("avgDegree2="+G.avgDegree2());
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
