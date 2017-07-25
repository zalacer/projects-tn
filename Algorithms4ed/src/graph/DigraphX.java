package graph;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.NoSuchElementException;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;
import v.Tuple2C;

//from http://algs4.cs.princeton.edu/42digraph/Digraph.java

public class DigraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;           // number of vertices in this digraph
  private int E;                 // number of edges in this digraph
  private BagX<Integer>[] adj;    // adj[v] = adjacency list for vertex v
  private int[] indegree;        // indegree[v] = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  private int count; // strongly connected components count
  private Seq<Seq<Integer>> scc; // strongly connected components
  private int[] id; // strong component ids

  public DigraphX(int V) {
    if (V < 0) throw new IllegalArgumentException("DigraphX: V < 0");
    this.V = V;
    this.E = 0;
    indegree = new int[V];
    adj = fill(V, () -> new BagX<Integer>());
  }
  
  public DigraphX(int v, int e, int[] x) {
    // constructor for RandomSparseDigraph
    if (v < 0) throw new IllegalArgumentException("DigraphX: v < 0");
    if (e < 0) throw new IllegalArgumentException("DigraphX: e < 0");
    if (x.length < 2*e) throw new IllegalArgumentException("GraphX constructor: "
        + "the length of x is < 2*e");
    V = v;
    indegree = new int[V];
    adj = fill(V, () -> new BagX<Integer>());
    if (V == 0) return;
    for (int i = 0; i < 2*e; i+=2) addEdge(x[i],x[i+1]);
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public DigraphX(int V, int E) {
    // ErdosRenyi constructor for ex4232
    if (V < 0) throw new IllegalArgumentException("DigraphX: V < 0");
    if (E < 0) throw new IllegalArgumentException("DigraphX: E < 0");
    this.V = V;
    indegree = new int[V];
    adj = fill(V, () -> new BagX<Integer>());
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 117331; i++) r.nextInt(E);
    for (int i = 0; i < E; i++) addEdge(r.nextInt(V),r.nextInt(V));
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }

  public DigraphX(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException("DigraphX: V < 0");
      indegree = new int[V];
      adj = fill(V, () -> new BagX<Integer>());
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException("DigraphX: E < 0");
      for (int i = 0; i < E; i++) {
        int v = in.readInt();
        int w = in.readInt();
//        System.out.println("adding edge "+v+" "+w);
        addEdge(v, w); 
      }
    }
    catch (NoSuchElementException e) {
      throw new IllegalArgumentException("DigraphX: invalid input format:" + e);
    }
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }

  public DigraphX(DigraphX G) {
    this(G.V());
    this.E = G.E();
    for (int v = 0; v < V; v++)
      this.indegree[v] = G.indegree(v);
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<Integer> reverse = new Stack<Integer>();
      for (int w : G.adj[v]) reverse.push(w);
      for (int w : reverse) adj[v].add(w);
    }
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public DigraphX(int v, String s) {
    if (v < 0) throw new IllegalArgumentException("DigraphX: v is < 0");
    V = v;
    if (s == null) throw new IllegalArgumentException("DigraphX: s is null");
    if (!s.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException(
        "DigraphX: s doesn't contain only integers and whitespace");
    indegree = new int[V];
    if (v == 0) return;
    String[] sa = s.split("\\s+");
    int[] ia = new int[sa.length];
    for (int i = 0; i < ia.length; i++) ia[i] = Integer.parseInt(sa[i]);
    int len = ia.length % 2 == 0 ? ia.length : ia.length-1, c = 0;
    if (len == 0) throw new IllegalArgumentException(
        "DigraphX: insufficient data in s");
    Tuple2C<Integer,Integer>[] ta = ofDim(Tuple2C.class,len/2);
    for (int i = 0; i < len-1; i+=2)  ta[c++] = new Tuple2C<Integer,Integer>(ia[i],ia[i+1]);
    adj = fill(V, () -> new BagX<Integer>());
    for (Tuple2C<Integer,Integer> t : ta) {
      validateVertex(t._1); validateVertex(t._2);
      addEdge(t._1,t._2);      
    }
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public int V() { return V; }

  public int E() { return E; }
  
  public BagX<Integer>[] adj() { return adj; }
 
  public Stack<Integer> cycle() { return cycle; }
  
  public int[] id() { return id; }
  
  public int[] indegree() { return indegree; }
  
  public int parallelEdgeCount() { 
    allParallelEdges();
    return parallelEdges == null ? 0 : parallelEdges.size(); }
  
  public Seq<Tuple2<Integer,Integer>> parallelEdges() {
    return allParallelEdges();
  }
  
  public int count() { return count; }
  
  public Seq<Seq<Integer>> scc() { return scc; }
  
  public int selfLoopCount() { 
    allSelfLoops();
    return selfLoops == null ? 0 : selfLoops.size(); 
  }
  
  public Seq<Integer> selfLoops() { return allSelfLoops(); }
  
  public void search() {
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }

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
    adj[v].add(w);
//    System.out.println("adj["+v+"]="+adj[v]);
    indegree[w]++;
    E++;
  }

  public Iterable<Integer> adj(int v) { validateVertex(v); return adj[v]; }

  public int outdegree(int v) { validateVertex(v); return adj[v].size(); }
  
  public int maxIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int max = Integer.MIN_VALUE;
    for (int i : indegree) if (i > max) max = i;
    return max; 
  }
  
  public int minIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int min = Integer.MAX_VALUE;
    for (int i : indegree) if (i < min) min = i;
    return min; 
  }
  
  public double avgIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    double sum = 0;
    for (int i : indegree) sum += i;
    return sum/V; 
  }
  
  public int maxOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int max = Integer.MIN_VALUE;
    for (BagX<Integer> b : adj) { int s = b.size(); if (s > max) max = s; }
    return max; 
  }
  
  public int minOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int min = Integer.MAX_VALUE;
    for (BagX<Integer> b : adj) { int s = b.size(); if (s < min) min = s; }
    return min; 
  }
  
  public double avgOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    double sum = 0;
    for (BagX<Integer> b : adj) sum += b.size();
    return sum/V; 
  }

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

  public DigraphX reverse() {
    DigraphX reverse = new DigraphX(V);
    for (int v = 0; v < V; v++) for (int w : adj(v)) reverse.addEdge(w, v);
    return reverse;
  }
  
  Iterable<Integer> sources() {
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (indegree[i] == 0) s.add(i);
    return s;  
  }
  
  Iterable<Integer> sinks() {
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (adj[i].isEmpty()) s.add(i);
    return s;
  }
  
  public boolean isMap() {
    for (int i = 0; i < V; i++) if (adj[i].size() != 1) return false;
    return true;
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
    DigraphX d = new DigraphX(in);
    StdOut.println(d);
    par(d.adj());
    System.out.println("hasSelfLoop="+d.hasSelfLoop());
    if (d.hasSelfLoop()) System.out.println("selfLoops="+d.selfLoops());
    else System.out.println("graph has no self-loops");
    System.out.println("allSelfLoops="+d.allSelfLoops());
    System.out.println("selfLoopCount="+d.selfLoopCount());
    System.out.println("hasParallelEdge="+d.hasParallelEdge());
    if (d.hasParallelEdge()) System.out.println("parallelEdges="+d.parallelEdges());
    else System.out.println("graph has no parallel edges");
    System.out.println("allParallelEdges="+d.allParallelEdges());
    System.out.println("hasCycle="+d.hasCycle());
    if (d.hasCycle()) System.out.println("cycle="+d.cycle());
    else System.out.println("graph doesn't have a cycle");

  }

}
