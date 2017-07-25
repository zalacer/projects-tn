package graph;

import static v.ArrayUtils.*;

import java.util.NoSuchElementException;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import st.HashSET;
import v.Tuple2;
import v.Tuple2C;

//from http://algs4.cs.princeton.edu/42digraph/Digraph.java

public class DigraphEx4205 {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;           // number of vertices in this digraph
  private int E;                 // number of edges in this digraph
  private BagX<Integer>[] adj;    // adj[v] = adjacency list for vertex v
  private int[] indegree;        // indegree[v] = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  HashSET<Tuple2<Integer,Integer>> edges;
  
  public DigraphEx4205(int V) {
    if (V < 0) throw new IllegalArgumentException("DigraphEx4205: V < 0");
    this.V = V;
    this.E = 0;
    indegree = new int[V];
    adj = ofDim(BagX.class, V);
    for (int v = 0; v < V; v++) {
      adj[v] = new BagX<Integer>();
    }
  }

  public DigraphEx4205(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException("DigraphEx4205: V < 0");
      indegree = new int[V];
      adj = ofDim(BagX.class, V);
      for (int v = 0; v < V; v++) adj[v] = new BagX<Integer>();
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException("DigraphEx4205: E < 0");
      edges = new HashSET<Tuple2<Integer,Integer>>();
      for (int i = 0; i < E; i++) {
        int v = in.readInt();
        int w = in.readInt();
        addEdge(v, w);       
      }
    }
    catch (NoSuchElementException e) {
      throw new IllegalArgumentException("DigraphEx4205: invalid input format:" + e);
    }
    DirectedCycleEx4205 dc = new DirectedCycleEx4205(this);
    cycle = dc.getCycle();
  }

  public DigraphEx4205(DigraphEx4205 G) {
    this(G.V());
    this.E = G.E();
    edges = new HashSET<Tuple2<Integer,Integer>>();
    for (int v = 0; v < V; v++)
      this.indegree[v] = G.indegree(v);
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
    DirectedCycleEx4205 dc = new DirectedCycleEx4205(this);
    cycle = dc.getCycle();
  }
  
  public DigraphEx4205(int v, String s) {
    if (v < 0) throw new IllegalArgumentException("DigraphEx4205: v is < 0");
    V = v;
    if (s == null) throw new IllegalArgumentException("DigraphEx4205: s is null");
    if (!s.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException(
        "DigraphEx4205: s doesn't contain only integers and whitespace");
    indegree = new int[V];
    if (v == 0) return;
    String[] sa = s.split("\\s+");
    int[] ia = new int[sa.length];
    for (int i = 0; i < ia.length; i++) ia[i] = Integer.parseInt(sa[i]);
    int len = ia.length % 2 == 0 ? ia.length : ia.length-1, c = 0;
    if (len == 0) throw new IllegalArgumentException(
        "DigraphEx4205: insufficient data in s");
    Tuple2C<Integer,Integer>[] ta = ofDim(Tuple2C.class,len/2);
    for (int i = 0; i < len-1; i+=2)  ta[c++] = new Tuple2C<Integer,Integer>(ia[i],ia[i+1]);
    adj = ofDim(BagX.class,V);
    for (int i = 0; i < V; i++) adj[i] = new BagX<Integer>();
    edges = new HashSET<Tuple2<Integer,Integer>>();
    for (Tuple2C<Integer,Integer> t : ta) {
      validateVertex(t._1); validateVertex(t._2);
      addEdge(t._1,t._2);      
    }
    DirectedCycleEx4205 dc = new DirectedCycleEx4205(this);
    cycle = dc.getCycle();
  }
  
  public int V() { return V; }

  public int E() { return E; }
  
  public BagX<Integer>[] adj() { return adj; }
 
  public Stack<Integer> cycle() { return cycle; }
  
  public int[] indegree() { return indegree; }
  
  public Seq<Tuple2<Integer,Integer>> parallelEdges() { return parallelEdges; }
  
  public int parallelEdgeCount() { return parallelEdges == null ? 0 : parallelEdges.size(); }
  
  public int selfLoopCount() { return selfLoops == null ? 0 : selfLoops.size(); }
  
  public Seq<Integer> selfLoops() { return selfLoops; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("validateVertex: v out of bounds");
  }
  
  @SafeVarargs
  private final void validateVertex(int...x) {
    if (x == null || x.length == 0) return;
    for (int i = 0; i < x.length; i++)
      if (x[i] < 0 || x[i] >= V)
        throw new IllegalArgumentException("validateVertex: "+x[i]+" v out of bounds");
  }

  public void addEdge(int v, int w) {
    validateVertex(v,w);
    Tuple2<Integer,Integer> t1 = new Tuple2<>(v,w);
    Tuple2<Integer,Integer> t2 = new Tuple2<>(w,v);
    if (v == w || edges.containsAny(t1,t2)) return;
    adj[v].add(w);
    edges.insert(t1,t2);
    indegree[w]++;
    E++;
  }

  public Iterable<Integer> adj(int v) { validateVertex(v); return adj[v]; }

  public int outdegree(int v) { validateVertex(v); return adj[v].size(); }

  public int indegree(int v) { validateVertex(v); return indegree[v]; }
  
  public boolean hasEdge(int v, int w) {
    validateVertex(v,w);
    for (int x : adj[v]) if (x == w) return true;
    return false;
  }
  
  public boolean hasSelfLoop() {
    for (int v = 0; v < V; v++) for (int w : adj[v])
      if (v == w) { selfLoops = new Seq<Integer>(v); return true; }
    return false;
  }
  
  public Seq<Integer> allSelfLoops() {
    // each self-loop is represented as an individual vertex v 
    // with the implication that edge v->v exists
    Seq<Integer> sl = new Seq<Integer>();
    for (int v = 0; v < V; v++)
      for (int w : adj[v]) if (v == w) sl.add(v);
    if (!sl.isEmpty()) selfLoops = sl;
    return sl;
  }

  public boolean hasParallelEdge() {
    // 2 edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      for (int w : adj[v]) {
        if (v != w && m[w]) {
          parallelEdges = new Seq<Tuple2<Integer,Integer>>(new Tuple2<>(v,w));
          return true;
        }
        m[w] = true;
      }
      for (int w : adj[v])  m[w] = false;
    }
    return false;
  }
  
  public Seq<Tuple2<Integer,Integer>> allParallelEdges() {
    // Two edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    // each pair of parallel edges is represented as a single edge u->v with the
    // implication that another such edge exists
    Seq<Tuple2<Integer,Integer>> p = new Seq<>();
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      for (int w : adj[v]) {
        if (v != w && m[w]) p.add(new Tuple2<Integer,Integer>(v,w));
        m[w] = true;
      }
      for (int w : adj[v]) m[w] = false;
    }
    if (!p.isEmpty()) parallelEdges = p;
    return p;
  }

  public DigraphEx4205 reverse() {
    DigraphEx4205 reverse = new DigraphEx4205(V);
    for (int v = 0; v < V; v++) for (int w : adj(v)) reverse.addEdge(w, v);
    return reverse;
  }
  
  public boolean hasCycle() { return cycle != null; }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " vertices, " + E + " edges " + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(String.format("%d: ", v));
      for (int w : adj[v]) {
        s.append(String.format("%d ", w));
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    DigraphEx4205 d = new DigraphEx4205(in);
    StdOut.println(d);
    par(d.adj());
    System.out.println("hasSelfLoop="+d.hasSelfLoop());
    if (d.hasSelfLoop()) System.out.println("selfLoops="+d.selfLoops());
    System.out.println("allSelfLoops="+d.allSelfLoops());
    System.out.println("selfLoopCount="+d.selfLoopCount());
    System.out.println("hasParallelEdge="+d.hasParallelEdge());
    if (d.hasParallelEdge()) System.out.println("parallelEdges="+d.parallelEdges());
    System.out.println("allParallelEdges="+d.allParallelEdges());
    System.out.println("hasCycle="+d.hasCycle());
//    if (d.hasCycle()) 
      System.out.println("cycle="+d.cycle());

  }

}
