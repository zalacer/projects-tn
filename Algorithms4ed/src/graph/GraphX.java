package graph;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import st.HashSET;
import v.Tuple2;
import v.Tuple2C;

public class GraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private int V;
  private int E;
  private BagX<Integer>[] adj;
  private boolean[] marked;
  private transient int[] edgeTo;
  private transient int[] id; // component ids
  private transient int[] size;  // size[id] = number of vertices in given component
  private transient int count = 0; // number of connected components
  private transient Stack<Integer> cycle = null; 
  private transient Stack<Integer> selfLoop = null;
  private transient Stack<Integer> parallelEdges = null;

  public GraphX(int V) {
    if (V < 0) throw new IllegalArgumentException(
        "GraphX constructor: Number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    adj = fill(V, () -> new BagX<Integer>());
  }
  
  public GraphX(int v, int e, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "v is < 0");
    if (e < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "e is < 0");
    if (x.length < e) throw new IllegalArgumentException("GraphX constructor: "
        + "the length of x is < e");
    V = v;
    if (v == 0) return;
    adj = fill(V, () -> new BagX<Integer>());
    for (int i = 0; i < e; i++) {
      if (x[i].length < 2) throw new IllegalArgumentException("GraphX constructor: "
          + "the number of elements in x["+i+"] is < 2");
      int a = x[i][0], b = x[i][1];
      validateVertex(a); validateVertex(b);
      addEdge(a,b);      
    }
    search();
  }
  
  public GraphX(int v, int e, int[] x) {
    // constructor for RandomSparseGraph
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "v is < 0");
    if (e < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "e is < 0");
    if (x.length < 2*e) throw new IllegalArgumentException("GraphX constructor: "
        + "the length of x is < 2*e");
    V = v;
    adj = fill(V, () -> new BagX<Integer>());
    if (v == 0) return;
    for (int i = 0; i < 2*e; i+=2) addEdge(x[i],x[i+1]);      
    search();
  }
  
  public GraphX(int v, String s) {
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: v is < 0"); 
    if (s == null) throw new IllegalArgumentException("GraphX constructor: s is null");
    if (!s.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException("GraphX constructor: "
        + "s doesn't contain only integers and whitespace");
    if (v == 0) return;
    String[] sa = s.split("\\s+");
    int[] ia = new int[sa.length];
    for (int i = 0; i < ia.length; i++) ia[i] = Integer.parseInt(sa[i]);
    V = v;
    int len = ia.length % 2 == 0 ? ia.length : ia.length-1, c = 0;
    if (len == 0) throw new IllegalArgumentException("GraphX constructor: insufficient data in s");
    Tuple2C<Integer,Integer>[] ta = ofDim(Tuple2C.class,len/2);
    for (int i = 0; i < len-1; i+=2)  ta[c++] = new Tuple2C<Integer,Integer>(ia[i],ia[i+1]);
    adj = fill(V, () -> new BagX<Integer>());
    for (Tuple2C<Integer,Integer> t : ta) {
      validateVertex(t._1); validateVertex(t._2);
      addEdge(t._1,t._2);      
    }
    E = 0; for (BagX<Integer> bag : adj) E += bag.size(); E /= 2;
    search();
  }

  public GraphX(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException(
          "GraphX constructor: number of vertices in a Graph must be nonnegative");
      adj = fill(V, () -> new BagX<Integer>());
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException(
          "GraphX constructor: number of edges in a Graph must be nonnegative");
      int c = 0,v,w,i; String[] sa;
      while (in.hasNextLine()) {
        sa = in.readLine().split("\\s");
        if (sa.length < 2) continue;
        v = Integer.parseInt(sa[0]); validateVertex(v); 
        i = 1;      
        while (c < E && i < sa.length) {
          w = Integer.parseInt(sa[i]); 
          validateVertex(w);
          addEdge(v, w);    
          i++; c++;
        }
      }
      E = c;
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("GraphX constructor: invalid input format", e);
    }
    search();
  }

  public GraphX(GraphX G) {
    this(G.V());
    this.E = G.E();
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<Integer> reverse = new Stack<Integer>();
      for (int w : G.adj[v]) reverse.push(w);
      for (int w : reverse) adj[v].add(w);
    }
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
  
  public Stack<Integer> cycle() { 
    findCycle();
    return cycle; 
  }
    
  public Stack<Integer> selfLoop() { 
    hasSelfLoop();
    return selfLoop; 
  }
  
  public Iterable<Integer> parallelEdges() { 
    hasParallelEdges(); 
    return parallelEdges; 
  }
  
  public Seq<Tuple2<Integer,Integer>> edges() {
    // retaining at most one self loop per vertex
    HashSET<Tuple2<Integer,Integer>> h = new HashSET<>();
    Tuple2<Integer,Integer> t0,t1;
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (int e : adj(v)) {
        if (e != v) {
          t0 = new Tuple2<>(v,e); t1 = new Tuple2<>(e,v); 
          if (!(h.contains(t0) || h.contains(t1))) h.add(t0);
        }
        // only add one copy of each self loop
        else if (selfLoops % 2 == 0) { h.add(new Tuple2<>(v,v)); selfLoops++; }       
      }
    }
    return new Seq<>(h);
  }
  
  public Seq<Tuple2<Integer,Integer>> allEdges() {
    HashSET<Tuple2<Integer,Integer>> h = new HashSET<>();
    for (int v = 0; v < V; v++) {
      for (int e : adj(v)) {
        if (e < v) h.add(new Tuple2<>(e,v));
        else h.add(new Tuple2<>(v,e));
      }
    }
    return new Seq<>(h);
  }
  
  public Seq<Tuple2<Integer,Integer>> edgesWithNoSelfLoops() {
    HashSET<Tuple2<Integer,Integer>> h = new HashSET<>();
    Tuple2<Integer,Integer> t0,t1;
    for (int v = 0; v < V; v++) {
      for (int e : adj(v)) {
        if (e != v) {
          t0 = new Tuple2<>(v,e); t1 = new Tuple2<>(e,v); 
          if (!(h.contains(t0) || h.contains(t1))) h.add(t0);
        }
      }
    }
    return new Seq<>(h);
  }
  
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
    // used by search to find connected components
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
    validateVertex(v);
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
    search();
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
          selfLoop = new Stack<Integer>();
          selfLoop.push(v);
          selfLoop.push(v);
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasParallelEdges() {
    // from Cycle
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      // check for parallel edges incident to v
      for (int w : adj[v]) {
        if (m[w]) {
          parallelEdges = new Stack<Integer>();
          parallelEdges.push(v);
          parallelEdges.push(w);
          parallelEdges.push(v);
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
  
  public boolean findCycle() {
    // from CycleX; excluding self-loops and parallel edges
    marked = new boolean[V];
    edgeTo = new int[V];
    for (int v = 0; v < V; v++) if (!marked[v]) dfs2(-1, v);
    return cycle == null ? false : true;
  }
  
  private void dfs2(int u, int v) {
    // from CycleX; used by findCycle()
    marked[v] = true;
    for (int w : adj(v)) {
      // short circuit if cycle already found
      if (cycle != null) return;
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs2(v, w);
      }
      // check for cycle (but disregard reverse of edge leading to v)
      else if (w != u) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) {
          cycle.push(x);
        }
        cycle.push(w);
        cycle.push(v);
      }
    }
  }
  
  public int[] degreeSequence() {
    // for demonstrating nonisomorphism with another graph 
    // https://en.wikipedia.org/wiki/Degree_%28graph_theory%29#Degree_sequence
    int[] d = new int[V];
    for (int i = 0; i > V; i++) d[i] = adj[i].size();
    Arrays.sort(d);
    return reverse(d);   
  }
  
  public long degreeSequenceSum() { return sum(degreeSequence()); }

  public boolean hasCycle() {
    return hasSelfLoop() || hasParallelEdges(); 
  }



  public Integer[] parallelEdgesArray() { 
    hasParallelEdges(); 
    return parallelEdges.toArray(1); 
  }

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
//    if (hasSelfLoop()) s.append("has self-loop");
//    else s.append("doesn't have self-loop");
//    s.append(NEWLINE);
//    if (hasParallelEdges()) s.append("has parallel edges");
//    else s.append("doesn't have parallel edges");
//    s.append(NEWLINE);
    return s.toString();
  }
  
  public Integer[] semiSortedAdj() {
    Seq<Integer> seq = new Seq<>();
    for (int i = 0; i < adj.length; i++) {
      Seq<Integer> seq2 = new Seq<>();
      for (Integer e : adj[i]) seq2.add(e);
      seq.addAll(seq2.sorted());
    }
    return seq.to();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + E;
    result = prime * result + V;
    result = prime * result + java.util.Arrays.hashCode(semiSortedAdj());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    GraphX other = (GraphX) obj;
    if (E != other.E) return false;
    if (V != other.V) return false;
    if (!java.util.Arrays.equals(semiSortedAdj(), other.semiSortedAdj()))
      return false;
    return true;
  }
  
  public static void generateAllPermutations(int v, int e, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("generateAllPermutations "
        +"v is < 0");
    if (e < 0) throw new IllegalArgumentException("generateAllPermutations "
        +"e is < 0");
    if (x.length < e) throw new IllegalArgumentException("generateAllPermutations "
        + "the length of x is < e"); 
    for (int i = 0; i < x.length; i++) if (x[i].length < 2) 
      throw new IllegalArgumentException("generateAllPermutations "
          + "the number of elements in x["+i+"] is < 2");
    int[] r = range(0,x.length);  Iterator<int[]> p = permutations(r); 
    GraphX g = null;
    while(p.hasNext()) {
      int[] a = p.next();
      int[][] b = new int[x.length][];
      for (int i = 0; i < a.length; i++) b[i] = x[a[i]];
//      par(b);
      g = new GraphX(v,e,b);
      par(g.adj());
    }  
  }
  
  public static boolean isomorphic(GraphX g, GraphX h) {
    // better to compare degree lists 
    // see www.ms.uky.edu/~csima/ma111/GraphsLecture2.pdf
    // or compare preorders and inorders
    // however neither are mentioned in https://en.wikipedia.org/wiki/Graph_isomorphism_problem
    // according to https://math.stackexchange.com/questions/471220/prove-two-graphs-are-isomorphic
    // "some k-regular graphs with the same number of vertices" have the same degree lists
    // perhaps possible to use their adjency matrices, see
    // https://en.wikipedia.org/wiki/Adjacency_matrix#Isomorphism_and_invariants
    // https://stackoverflow.com/questions/12763275/how-would-you-verify-that-two-graphs-are-the-same/12763347#12763347
    // The graph isomorphism problem is one of few standard problems in computational complexity 
    //  theory belonging to NP, but not known to belong to either of its well-known (and, if 
    // P ≠ NP, disjoint) subsets: P and NP-complete. It is one of only two, out of 12 total, 
    // problems listed in Garey & Johnson (1979) whose complexity remains unresolved, the other 
    // being integer factorization.
    if (g.V() != h.V() || g.E() != h.E()) return false;
    if (g.hasSelfLoop() != h.hasSelfLoop()) return false;
    if (g.hasParallelEdges() != h.hasParallelEdges()) return false;
    if (g.hasCycle() != h.hasCycle()) return false;
    if (!Arrays.equals(g.degreeSequence(), h.degreeSequence())) return false;
    if (g.count() != h.count()) return false;   
    BipartiteX bpg = new BipartiteX(g), bph = new BipartiteX(h);
    if (bpg.isBipartite() != bph.isBipartite()) return false;
    BiconnectedX bcg = new BiconnectedX(g), bch = new BiconnectedX(g);
    if (bcg.articulationPoints().length != bch.articulationPoints().length) return false;
    CycleX cg = new CycleX(g), ch = new CycleX(h);
    if (!Arrays.equals(cg.parallelEdgeSequence(),ch.parallelEdgeSequence())) return false;
    // probably isomorphic
    return true;
  }

  public static void main(String[] args) {
    // 0-1, 1-2, 2-3, 3-0
//    int[][] x = new int[4][];
//    x[0] = new int[]{0,1};
//    x[1] = new int[]{1,2};
//    x[2] = new int[]{2,3};
//    x[3] = new int[]{3,0};
//    generateAllPermutations(4,4,x);
//    System.exit(0);

//    In in = new In(args[0]);
//    In in = new In(); // read from stdin
//    GraphX G = new GraphX(in);
    // edges from tinyGex3.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 "
          +"6 2 5 2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    GraphX G = new GraphX(12,edges), H = new GraphX(12,edges);
    System.out.println("isomorphism of G and H test:");
    System.out.println("isomorphic(G,H)="+isomorphic(G,H));
    System.out.println("\nG:");
    StdOut.println(G);
    System.out.print("id="); par(G.id()); // connected components
    System.out.println("count="+G.count()); // number of connected components
    System.out.println("avgDegree="+G.avgDegree());
    System.out.println("numberOfSelfLoops="+G.numberOfSelfLoops());
    System.out.print("adj="); par(G.adj());
    System.out.println("hasSelfLoop="+G.hasSelfLoop());
    if (G.hasSelfLoop()) System.out.println("selfLoop="+G.selfLoop());
    System.out.println("hasParallelEdges="+G.hasParallelEdges());
    if (G.hasParallelEdges()) System.out.print("parallelEdges="+G.parallelEdges());
  }

}
