package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/KosarajuSharirSCC.java

/******************************************************************************
 *  % java KosarajuSharirSCC tinyDG.txt
 *  5 strong components
 *  1 
 *  0 2 3 4 5 
 *  9 10 11 12 
 *  6 8 
 *  7
 */
public class KosarajuSharirSCCX {
  private boolean[] marked;     // marked[v] = has vertex v been visited?
  private int[] id;             // id[v] = id of strong component containing v
  private int count;            // number of strongly-connected components
  private int V;

  public KosarajuSharirSCCX(DigraphX G) {
    // compute reverse postorder of reverse graph
    if (G == null) throw new IllegalArgumentException("KosarajuSharirSCCX: G is null");
    V = G.V();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G.reverse());
    // run DFS on G, using reverse postorder to guide calculation
    marked = new boolean[V];
    id = new int[V];
    for (int v : dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
    assert check(G);
  }

  public KosarajuSharirSCCX(EuclidianDigraph G) {
    // compute reverse postorder of reverse graph
    if (G == null) throw new IllegalArgumentException("KosarajuSharirSCCX: G is null");
    V = G.V();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G.reverse());
    // run DFS on G, using reverse postorder to guide calculation
    marked = new boolean[V];
    id = new int[V];
    for (int v : dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
    assert check(G);
  }
  
  public KosarajuSharirSCCX(EdgeWeightedDigraphX G) {
    // compute reverse postorder of reverse graph
    if (G == null) throw new IllegalArgumentException("KosarajuSharirSCCX: G is null");
    V = G.V();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G.reverse());
    // run DFS on G, using reverse postorder to guide calculation
    marked = new boolean[V];
    id = new int[V];
    for (int v : dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
    assert check(G);
  }
  
  public KosarajuSharirSCCX(EdgeWeightedDigraphI G) {
    // compute reverse postorder of reverse graph
    if (G == null) throw new IllegalArgumentException("KosarajuSharirSCCX: G is null");
    V = G.V();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G.reverse());
    // run DFS on G, using reverse postorder to guide calculation
    marked = new boolean[V];
    id = new int[V];
    for (int v : dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
    assert check(G);
  }
  
  public KosarajuSharirSCCX(EuclidianEdgeWeightedDigraph G) {
    // compute reverse postorder of reverse graph
    if (G == null) throw new IllegalArgumentException("KosarajuSharirSCCX: G is null");
    V = G.V();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G.reverse());
    // run DFS on G, using reverse postorder to guide calculation
    marked = new boolean[V];
    id = new int[V];
    for (int v : dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
    assert check(G);
  }

  private void dfs(DigraphX G, int v) { 
    marked[v] = true;
    id[v] = count;
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
  }
  
  private void dfs(EuclidianDigraph G, int v) { 
    marked[v] = true;
    id[v] = count;
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
  }
  
  private void dfs(EdgeWeightedDigraphX G, int u) { 
    marked[u] = true;
    id[u] = count;
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }
  
  private void dfs(EdgeWeightedDigraphI G, int u) { 
    marked[u] = true;
    id[u] = count;
    for (DirectedEdgeI e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }
  
  private void dfs(EuclidianEdgeWeightedDigraph G, int u) { 
    marked[u] = true;
    id[u] = count;
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }

  public int count() { return count; }

  public boolean stronglyConnected(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    return id[v] == id[w];
  }

  public int id(int v) { validateVertex(v); return id[v]; }
  
  public Seq<Seq<Integer>> components() {
    if (count == 0) return new Seq<Seq<Integer>>();
    Seq<Seq<Integer>> c = new Seq<>();
    for (int i = 0; i < count; i++) c.add(new Seq<Integer>());
    for (int v = 0; v < V; v++) c.get(id[v]).add(v);
    return c;
  }
  
  public int[] id() { return id; };

  // does the id[] array contain the strongly connected components?
  private boolean check(DigraphX G) {
    TransitiveClosureX tc = new TransitiveClosureX(G,"noUpdate");
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
          return false;
      }
    }
    return true;
  }
  
  private boolean check(EuclidianDigraph G) {
    TransitiveClosureED tc = new TransitiveClosureED(G,"noUpdate");
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
          return false;
      }
    }
    return true;
  }
  
  private boolean check(EdgeWeightedDigraphX G) {
    TransitiveClosureEWDX tc = new TransitiveClosureEWDX(G,"noUpdate");
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
          return false;
      }
    }
    return true;
  }
  
  private boolean check(EdgeWeightedDigraphI G) {
    TransitiveClosureEWDI tc = new TransitiveClosureEWDI(G,"noUpdate");
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
          return false;
      }
    }
    return true;
  }
  
  private boolean check(EuclidianEdgeWeightedDigraph G) {
    TransitiveClosureEEWD tc = new TransitiveClosureEEWD(G,"noUpdate");
    for (int v = 0; v < G.V(); v++) {
      for (int w = 0; w < G.V(); w++) {
        if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
          return false;
      }
    }
    return true;
  }

  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
    //mediumDG.txt, tinyDGfromEWD.txt, tinyDG.txt, tinyEWD.txt
    In in = new In(args[0]);
//    DigraphX G = new DigraphX(in);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    KosarajuSharirSCCX scc = new KosarajuSharirSCCX(G);

    // number of connected components
    int m = scc.count();
    StdOut.println(m + " strong components");

    // compute list of vertices in each strong component
    Queue<Integer>[] components = ofDim(Queue.class, m);
    for (int i = 0; i < m; i++) {
      components[i] = new Queue<Integer>();
    }
    for (int v = 0; v < G.V(); v++) {
      components[scc.id(v)].enqueue(v);
    }

    // print results
    for (int i = 0; i < m; i++) {
      for (int v : components[i]) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
    
    System.out.println("components="+scc.components());


  }

}
