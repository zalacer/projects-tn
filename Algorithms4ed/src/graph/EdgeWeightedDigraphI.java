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
import exceptions.InvalidDataException;
import st.HashSET;
import v.Tuple2;

public class EdgeWeightedDigraphI {
  private static final String NEWLINE = System.getProperty("line.separator");
  private int V;                // number of vertices in this digraph
  private int E;                      // number of edges in this digraph
  private BagX<DirectedEdgeI>[] adj;    // adj[v] = adjacency list for vertex v
  private int[] indegree;             // indegree[v] = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  private int[] id;             // id[v] = id of strong component containing v
  private int count;            // number of strongly-connected components
  private Seq<Seq<Integer>> scc; // strongly connected components
  public transient boolean validate  = true;

  public EdgeWeightedDigraphI(int V) {
    if (V < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphXI: Number of vertices in a Digraph must be nonnegative");
    this.V = V;
    this.E = 0;
    this.indegree = new int[V];
    adj = fill(V, ()->new BagX<DirectedEdgeI>());
  }

  public EdgeWeightedDigraphI(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphXI: Number of edges in a Digraph must be nonnegative");
    for (int i = 0; i < E; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      int weight = StdRandom.uniform(100);
      DirectedEdgeI e = new DirectedEdgeI(v, w, weight);
      validate = false;
      addEdge(e);
      validate = true;
    }
    search();
  }
  
  public EdgeWeightedDigraphI(Iterable<DirectedEdgeI> edges) {
	    if (edges == null) throw new IllegalArgumentException("String edges == null");
	    HashSET<Integer> set = new HashSET<>();
	    for (DirectedEdgeI e : edges) { set.add(e.from()); set.add(e.to()); }
	    if (set.size() == 0) {
	      V = 0;
	    } else {
	      V = set.size();
	      Integer[] vtcs = set.toArray();
	      if (min(vtcs) < 0) throw new IllegalArgumentException(
	          "EdgeWeightedDigraphXI: Queue<DirectedEdgeXI> contains a vertex < 0");
	      if (max(vtcs) > V) throw new IllegalArgumentException(
	          "EdgeWeightedDigraphXI: Queue<DirectedEdgeXI> contains a vertex > "+V);
	    }
	    this.indegree = new int[V];
	    adj = fill(V,()->new BagX<DirectedEdgeI>());
	    validate = false;
	    for (DirectedEdgeI e : edges) {
	      validateVertex(e.from(),e.to());
	      addEdge(e);
	    }
	    validate = true;
	    search();
	  }

  public EdgeWeightedDigraphI(In in) {
    this(in.readInt());
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertex(u,v);
      int w = in.readInt();
      addEdge(new DirectedEdgeI(u, v, w));
    }
    validate = true;
    search();
  }
  
  public EdgeWeightedDigraphI(In in, String vwd) {
    this(in.readInt());
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException(
        "EdgeWeightedDigraphX: Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int w1 = in.readInt();
      int v = in.readInt();
      int w2 = in.readInt();
      validateVertex(u,v);
      addEdge(new DirectedEdgeI(u, v, w1+w2));
    }
    validate = true;
    search();
  }

  public EdgeWeightedDigraphI(EdgeWeightedDigraphI G) {
    if (G == null) throw new IllegalArgumentException(
        "EdgeWeightedDigraphXI: EdgeWeightedDigraphXI G is null");
    V = G.V();
    E = G.E();
    indegree = G.indegree.clone();
    adj = fill(V, ()->new BagX<DirectedEdgeI>());
    for (int v = 0; v < V; v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeI> reverse = new Stack<>();
      for (DirectedEdgeI e : G.adj[v]) reverse.push(e);
      for (DirectedEdgeI e : reverse) adj[v].add(e.clone());
    }
//    search();
  }
  
  public EdgeWeightedDigraphI clone() { return new EdgeWeightedDigraphI(this); }

  public int V() { return V; }

  public int E() { return E; }
  
  BagX<DirectedEdgeI>[] adj() { return adj; }
   
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
      for (DirectedEdgeI e : adj[u]) {
        int v = e.to();
        if (u != v && m[v]) p.add(new Tuple2<Integer,Integer>(u,v));
        m[v] = true;
      }
      for (DirectedEdgeI e : adj[u]) m[e.to()] = false;
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
      for (DirectedEdgeI e : adj[u]) {
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
  
  private int weight(Seq<DirectedEdgeI> s) {
    if (s == null) return 0;
    int w = 0;
    for (DirectedEdgeI e : s) w += e.w();
    return w;
  }
  
  public int maxSPWeight() {
    Seq<Seq<Seq<DirectedEdgeI>>> allpaths = DijkstraSPI.allPaths(this);
    int max = 0;
    for (Seq<Seq<DirectedEdgeI>> s1 : allpaths)
      for (Seq<DirectedEdgeI> s2 : s1)
        if (weight(s2) > max) max = weight(s2);
    return max;
  }
  
  public int maxSPWeight(int source) {
    validateVertex(source);
    Seq<Seq<DirectedEdgeI>> allpaths = (new DijkstraSPI(this,source)).allPaths();
    int max = 0;
    for (Seq<DirectedEdgeI> s1 : allpaths)
        if (weight(s1) > max) max = weight(s1);
    return max;
  }
  
  public double diameter() {
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < V; i++) {
      DijkstraSPI sp = new DijkstraSPI(this, i, "q");
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

  public void addEdge(DirectedEdgeI e) {
    int u = e.from();
    int v = e.to();
    if (validate) validateVertex(u,v);
    adj[u].add(e);
    indegree[v]++;
    E++;
  }
  
  public void addEdge(int x, int y, int d) {
    if (validate) validateVertex(x,y);
    DirectedEdgeI e = new DirectedEdgeI(x,y,d);
    adj[x].add(e);
    indegree[y]++;
    E++;
  }
  
  public DirectedEdgeI findEdge(int u, int v) {
    // if there's an edge "from" u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return null;
    for (DirectedEdgeI e : adj[u]) 
      if (e.u() == u && e.v() == v) return e;
    return null;  
  }
  
  public boolean hasEdge(int u, int v) {
    // if there's an edge "from" u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    for (DirectedEdgeI e : adj[u]) 
      if (e.u() == u && e.v() == v) return true;
    return false;  
  }
  
  public boolean hasEdge(DirectedEdgeI x) {
    if (x  == null) return false;
    int u = x.u(), v = x.v();
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    // if there's an edge between u and v return true else return false
    for (DirectedEdgeI e : adj[u]) 
      if (e.u() == u && e.v() == v) return true;
    return false;  
  }
  
  public DirectedEdgeI removeEdge(int u, int v) {
    // remove an edge from this and return it if possible else return null
    DirectedEdgeI x = null; boolean udone = false;
    for (DirectedEdgeI e : adj[u]) {
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
  
  public boolean removeEdge(DirectedEdgeI x) { 
    boolean udone = adj[x.u()].remove(x);
    if (udone) E--;
    return udone;
  }
  
  public void addVertex() {
    // for ex4.4.25; add one vertex
    BagX<DirectedEdgeI>[] adj2 = ofDim(BagX.class, V+1);
    for (int i = 0; i < V; i++) adj2[i] = adj[i];
    adj2[V] = new BagX<DirectedEdgeI>();
    adj = adj2;
    int indegree2[] = new int[V+1];
    indegree = indegree2;
    for (int i = 0; i < V; i++) indegree2[i] = indegree[i];
    V++;
  }
  
  public void addVertex(int n) {
    // for ex4.4.24; add n vertices
    BagX<DirectedEdgeI>[] adj2 = ofDim(BagX.class, V+n);
    for (int i = 0; i < V; i++) adj2[i] = adj[i];
    for (int i = V-1; i < V+n; i++) adj2[i] = new BagX<DirectedEdgeI>();
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
    for (DirectedEdgeI de : edges()) 
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

  public Iterable<DirectedEdgeI> adj(int v) { validateVertex(v); return adj[v]; }

  public int outdegree(int v) { validateVertex(v); return adj[v].size(); }

  public int indegree(int v) { validateVertex(v); return indegree[v]; }
  
  public EdgeWeightedDigraphI reverse() {
    EdgeWeightedDigraphI reverse = new EdgeWeightedDigraphI(V);
    for (int v = 0; v < V; v++) 
      for (DirectedEdgeI d : adj(v)) reverse.addEdge(d.reverse());
    return reverse;
  }

  public Iterable<DirectedEdgeI> edges() {
    BagX<DirectedEdgeI> list = new BagX<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeI e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }
  
  public Seq<DirectedEdgeI> edgeSeq() {
    Seq<DirectedEdgeI> list = new Seq<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeI e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }
  
  private Comparator<DirectedEdgeI> cmp = (x,y) -> {
    // for edgeSeqSorted()
    int d = x.w() - y.w();
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
  
  public Seq<DirectedEdgeI> edgeSeqSorted() {
    // mainly for hashCode() and equals()
    Seq<DirectedEdgeI> list = new Seq<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeI e : adj(v)) {
        list.add(e);
      }
    }
    return list.sortWithComparator(cmp);
  }
  
  private int[] minMaxWeights() {
    int min =  Integer.MAX_VALUE, max = Integer.MIN_VALUE;
    for (DirectedEdgeI d : edges()) {
      int x = d.w();
      if (x < min) min = x;
      if (x > max) max = x;
    }
    return new int[]{min,max};
  }
  
  public Tuple2<Integer,DirectedEdgeI> maxWeightAndEdge() {
    int max = Integer.MIN_VALUE;
    DirectedEdgeI medge = null;
    for (DirectedEdgeI d : edges()) {
      int x = d.w();
      if (x > max) { max = x; medge = d; }
    }
    return new Tuple2<>(max,medge);
  }
  
  public int minWeight() { 
    int[] m = minMaxWeights();
    return m[0]; 
  }
  
  public int maxWeight() { 
    int[] m = minMaxWeights();
    return m[1]; 
  }
  
  public void changeMaxWeight(int x) {
    // reweight a maxWeight edge with x
    Tuple2<Integer,DirectedEdgeI> t = maxWeightAndEdge();
    if (t._1 == null) throw new InvalidDataException(
        "changeMaxWeight: maxWeight is null");
    if (t._2 == null) throw new InvalidDataException(
        "changeMaxWeight: maxWeighted edge is null");
    t._2.setW(x);
  }
  
  public void changeAllMaxWeight(int x) {
    // reweight all maxWeighted edges starting at x
    int mw = maxWeight();
    for (DirectedEdgeI e : edgeSeq()) if (e.w() == mw) e.setW(x++);
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (DirectedEdgeI e : adj[v]) {
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
    EdgeWeightedDigraphI other = (EdgeWeightedDigraphI) obj;
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
    EdgeWeightedDigraphI G = new EdgeWeightedDigraphI(in);
    StdOut.println(G);
        
    EdgeWeightedDigraphI G2 = new EdgeWeightedDigraphI(G.edges());
    StdOut.println(G2);
  }

}
