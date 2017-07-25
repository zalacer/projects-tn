package graph;

import static v.ArrayUtils.*;

import java.util.Comparator;

import ds.BagX;
import ds.Seq;
import ds.Stack;

// from http://algs4.cs.princeton.edu/44sp/EdgeWeightedDigraph.java
// using DirectedEdgeX

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import st.HashSET;
import v.Tuple2;

public class EdgeWeightedDigraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private int V;                // number of vertices in this digraph
  private int E;                      // number of edges in this digraph
  private BagX<DirectedEdgeX>[] adj;    // adj[v] = adjacency list for vertex v
  private int[] indegree;             // indegree[v] = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  private int[] id;             // id[v] = id of strong component containing v
  private int count;            // number of strongly-connected components
  private Seq<Seq<Integer>> scc; // strongly connected components
  public transient boolean validate  = true;

  public EdgeWeightedDigraphX(int V) {
    if (V < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of vertices in a Digraph must be nonnegative");
    this.V = V;
    this.E = 0;
    this.indegree = new int[V];
    adj = fill(V, ()->new BagX<DirectedEdgeX>());
  }

  public EdgeWeightedDigraphX(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of edges in a Digraph must be nonnegative");
    for (int i = 0; i < E; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      double weight = 0.01 * StdRandom.uniform(100);
      DirectedEdgeX e = new DirectedEdgeX(v, w, weight);
      validate = false;
      addEdge(e);
      validate = true;
    }
    search();
  }
  
  public EdgeWeightedDigraphX(Iterable<DirectedEdgeX> edges) {
	    if (edges == null) throw new IllegalArgumentException("String edges == null");
	    HashSET<Integer> set = new HashSET<>();
	    for (DirectedEdgeX e : edges) { set.add(e.from()); set.add(e.to()); }
	    if (set.size() == 0) {
	      V = 0;
	    } else {
	      V = set.size();
	      Integer[] vtcs = set.toArray();
	      if (min(vtcs) < 0) throw new IllegalArgumentException(
	          "EdgeWeightedDigraphX: Queue<DirectedEdgeX> contains a vertex < 0");
	      if (max(vtcs) > V) throw new IllegalArgumentException(
	          "EdgeWeightedDigraphX: Queue<DirectedEdgeX> contains a vertex > "+V);
	    }
	    this.indegree = new int[V];
	    adj = fill(V,()->new BagX<DirectedEdgeX>());
	    validate = false;
	    for (DirectedEdgeX e : edges) {
	      validateVertex(e.from(),e.to());
	      addEdge(e);
	    }
	    validate = true;
	    search();
	  }

  public EdgeWeightedDigraphX(In in) {
    this(in.readInt());
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertex(u,v);
      double w = in.readDouble();
      addEdge(new DirectedEdgeX(u, v, w));
    }
    validate = true;
//    search();
  }
  
  public EdgeWeightedDigraphX(In in, String vwd) {
    this(in.readInt());
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      double w1 = in.readDouble();
      int v = in.readInt();
      double w2 = in.readDouble();
      validateVertex(u,v);
      addEdge(new DirectedEdgeX(u, v, w1+w2));
    }
    validate = true;
    search();
  }

  public EdgeWeightedDigraphX(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: EdgeWeightedDigraphXG is null");
    V = G.V();
    E = G.E();
    indegree = G.indegree.clone();
    adj = fill(V, ()->new BagX<DirectedEdgeX>());
    for (int v = 0; v < V; v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeX> reverse = new Stack<>();
      for (DirectedEdgeX e : G.adj[v]) reverse.push(e);
      for (DirectedEdgeX e : reverse) adj[v].add(e.clone());
    }
    search();
  }
  
  public EdgeWeightedDigraphX clone() { return new EdgeWeightedDigraphX(this); }

  public int V() { return V; }

  public int E() { return E; }
  
  BagX<DirectedEdgeX>[] adj() { return adj; }
   
  public Stack<Integer> cycle() { return cycle; }
  
  public int[] id() { return id; }
  
  public int[] indegree() { return indegree; }
  
  public int parallelEdgeCount() { 
    allParallelEdges();
    return parallelEdges == null ? 0 : parallelEdges.size(); }
  
  public Seq<Tuple2<Integer,Integer>> parallelEdges() {
    return allParallelEdges();
  }
  
  public Seq<Tuple2<Integer,Integer>> allParallelEdges() {
    // Two edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    // each pair of parallel edges is represented as a single edge u->v with the
    // implication that another such edge exists
    Seq<Tuple2<Integer,Integer>> p = new Seq<>();
    boolean[] m = new boolean[V];
    for (int u = 0; u < V; u++) {
      for (DirectedEdgeX e : adj[u]) {
        int v = e.to();
        if (u != v && m[v]) p.add(new Tuple2<Integer,Integer>(u,v));
        m[v] = true;
      }
      for (DirectedEdgeX e : adj[u]) m[e.to()] = false;
    }
    if (!p.isEmpty()) parallelEdges = p;
    return p;
  }
  
  public int count() { return count; }
  
  public Seq<Seq<Integer>> scc() { return scc; }
  
  public int selfLoopCount() { 
    allSelfLoops();
    return selfLoops == null ? 0 : selfLoops.size(); 
  }
  
  public Seq<Integer> selfLoops() { return allSelfLoops(); }
  
  public Seq<Integer> allSelfLoops() {
    // each self-loop is represented as an individual vertex v 
    // with the implication that edge v->v exists
    Seq<Integer> sl = new Seq<Integer>();
    for (int u = 0; u < V; u++)
      for (DirectedEdgeX e : adj[u]) {
        int v = e.to();
        if (u == v) sl.add(u);
      }
    if (!sl.isEmpty()) selfLoops = sl;
    return sl;
  }

  public void search() {
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public double diameter() {
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < V; i++) {
      DijkstraSPX sp = new DijkstraSPX(this, i, "quiet");
      double d = sp.diameter();
      if (d > max) max = d;
    }
    return max;  
  }
  
  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  @SafeVarargs
  private final void validateVertex(int...x) {
    if (x == null || x.length == 0) return;
    for (int i = 0; i < x.length; i++)
      if (x[i] < 0 || x[i] >= V)
        throw new IllegalArgumentException("validateVertex: "+x[i]+" v out of bounds");
  }

  public void addEdge(DirectedEdgeX e) {
    int u = e.from();
    int v = e.to();
    if (validate) validateVertex(u,v);
    adj[u].add(e);
    indegree[v]++;
    E++;
  }
  
  public void addEdge(int x, int y, double d) {
    if (validate) validateVertex(x,y);
    DirectedEdgeX e = new DirectedEdgeX(x,y,d);
    adj[x].add(e);
    indegree[y]++;
    E++;
  }
  
  public DirectedEdgeX findEdge(int u, int v) {
    // if there's an edge "from" u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return null;
    for (DirectedEdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v) return e;
    return null;  
  }
  
  public boolean hasEdge(int u, int v) {
    // if there's an edge "from" u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    for (DirectedEdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v) return true;
    return false;  
  }
  
  public boolean hasEdge(DirectedEdgeX x) {
    if (x  == null) return false;
    int u = x.u(), v = x.v();
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    // if there's an edge between u and v return true else return false
    for (DirectedEdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v) return true;
    return false;  
  }
  
  public DirectedEdgeX removeEdge(int u, int v) {
    // remove an edge from this and return it if possible else return null
    DirectedEdgeX x = null; boolean udone = false;
    for (DirectedEdgeX e : adj[u]) {
      if (e.u() == u && e.v() == v) {
        x = e;
        udone = adj[u].remove(x);
        if (!udone) {
          System.out.println("couldn't remove edge "+x+" from adj["+u+"]");
          return null;
        }
      }
    }
    if (udone) { E--; return x; }
    return null;
  }
  
  public boolean removeEdge(DirectedEdgeX x) { 
    boolean udone = adj[x.u()].remove(x);
    if (udone) E--;
    return udone;
  }
  
  public void addVertex() {
    // for ex4.4.25; add one vertex
    BagX<DirectedEdgeX>[] adj2 = ofDim(BagX.class, V+1);
    for (int i = 0; i < V; i++) adj2[i] = adj[i];
    adj2[V] = new BagX<DirectedEdgeX>();
    adj = adj2;
    int indegree2[] = new int[V+1];
    indegree = indegree2;
    for (int i = 0; i < V; i++) indegree2[i] = indegree[i];
    V++;
  }
  
  public void addVertex(int n) {
    // for ex4.4.24; add n vertices
    BagX<DirectedEdgeX>[] adj2 = ofDim(BagX.class, V+n);
    for (int i = 0; i < V; i++) adj2[i] = adj[i];
    for (int i = V-1; i < V+n; i++) adj2[i] = new BagX<DirectedEdgeX>();
    adj = adj2;
    int indegree2[] = new int[V+n];
    for (int i = 0; i < V; i++) indegree2[i] = indegree[i];
    indegree = indegree2;
    V+=n;
  }
  
  public boolean removeLastVertex() {
    // for ex4.4.4, works only for removing vertex V-1
    int x = V-1;
    boolean r = false;
    for (DirectedEdgeX de : edges()) 
      if (de.from() == x || de.to() == x) { 
        r = removeEdge(de);
        if (r == false) {
          System.err.println("removeLastVertex: cannot remove edge "+de);
          return false;
        }
      }
    adj = take(adj,x);
    indegree = take(indegree,x);
    V--;
    search();
    return r;
  }

  public Iterable<DirectedEdgeX> adj(int v) { validateVertex(v); return adj[v]; }

  public int outdegree(int v) { validateVertex(v); return adj[v].size(); }

  public int indegree(int v) { validateVertex(v); return indegree[v]; }
  
  public EdgeWeightedDigraphX reverse() {
    EdgeWeightedDigraphX reverse = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < V; v++) 
      for (DirectedEdgeX d : adj(v)) reverse.addEdge(d.reverse());
    return reverse;
  }

  public Iterable<DirectedEdgeX> edges() {
    BagX<DirectedEdgeX> list = new BagX<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeX e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }
  
  public Seq<DirectedEdgeX> edgeSeq() {
    Seq<DirectedEdgeX> list = new Seq<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeX e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }
  
  private Comparator<DirectedEdgeX> cmp = (x,y) -> {
    // for edgeSeqSorted()
    double d = x.w() - y.w();
    if (d < 0) return -1;
    if (d > 0) return 1;
    int i = x.from() - y.from();
    if (i < 0) return -1;
    if (i > 0) return 1;
    i = x.to() - y.to();
    if (i < 0) return -1;
    if (i > 0) return 1;
    return 0;
  };
  
  public Seq<DirectedEdgeX> edgeSeqSorted() {
    // mainly for hashCode() and equals()
    Seq<DirectedEdgeX> list = new Seq<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeX e : adj(v)) {
        list.add(e);
      }
    }
    return list.sortWithComparator(cmp);
  }
  
  private double[] minMaxWeights() {
    double min =  Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (DirectedEdgeX d : edges()) {
      double x = d.w();
      if (x < min) min = x;
      if (x > max) max = x;
    }
    return new double[]{min,max};
  }
  
  public double minWeight() { 
    double[] m = minMaxWeights();
    return m[0]; 
  }
  
  public double maxWeight() { 
    double[] m = minMaxWeights();
    return m[1]; 
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (DirectedEdgeX e : adj[v]) {
        s.append(e + "  ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + E;
    result = prime * result + V;
    result = prime * edgeSeqSorted().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EdgeWeightedDigraphX other = (EdgeWeightedDigraphX) obj;
    if (E != other.E)
      return false;
    if (V != other.V)
      return false;
    if (!edgeSeqSorted().equals(other.edgeSeqSorted()))
      return false;  
    return true;
  }

  public static void main(String[] args) {
    In in = new In("tinyEWG.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    StdOut.println(G);
        
    EdgeWeightedDigraphX G2 = new EdgeWeightedDigraphX(G.edges());
    StdOut.println(G2);
  }

}
