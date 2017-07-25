package graph;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.BagX;
import ds.Stack;
import edu.princeton.cs.algs4.In;

public class GraphEx4106 {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;
  private int E;
  private BagX<Integer>[] adj;
  private boolean[] marked;  
  private int[] id; // component ids
  private int count = 0; // number of connected components
  private Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()

  public GraphEx4106(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    adj = ofDim(BagX.class,V);
    for (int v = 0; v < V; v++) {
      adj[v] = new BagX<Integer>();
    }
  }
  
  public GraphEx4106(int v, int e, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("GraphEx4106 constructor "
        + "v is < 0");
    if (e < 0) throw new IllegalArgumentException("GraphEx4106 constructor "
        + "e is < 0");
    if (x.length < e) throw new IllegalArgumentException("GraphEx4106 constructor "
        + "the length of x is < e");
    V = v;
//    System.out.print("x="); par(x);
    if (v == 0) return;
    adj = ofDim(BagX.class,v);
    for (int i = 0; i < v; i++)  adj[i] = new BagX<Integer>();
    for (int i = 0; i < e; i++) {
      if (x[i].length < 2) throw new IllegalArgumentException("GraphEx4106 constructor "
          + "the number of elements in x["+i+"] is < 2");
      int a = x[i][0], b = x[i][1];
      validateVertex(a); validateVertex(b);
      addEdge(a,b);      
    }
  }

  public GraphEx4106(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException("number of vertices in a Graph must be nonnegative");
      adj = ofDim(BagX.class,V);
      for (int v = 0; v < V; v++) adj[v] = new BagX<Integer>();
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException("number of edges in a Graph must be nonnegative");
      for (int i = 0; i < E; i++) {
        int v = in.readInt();
        int w = in.readInt();
        validateVertex(v);
        validateVertex(w);
        addEdge(v, w); 
      }
    }
    catch (NoSuchElementException e) {
      throw new IllegalArgumentException("invalid input format in Graph constructor", e);
    }
    // count connected components from p544 / CC
    marked = new boolean[V];
    id = new int[V];
    for (int s = 0; s < V; s++) if (!marked[s]) { dfs(s); count++; }
  }

  public GraphEx4106(GraphEx4106 G) {
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
  }

  public int V() { return V; }

  public int E() { return E; }

  public BagX<Integer>[] adj() { return adj.clone(); }

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
    adj[w].add(v);
  }

  public boolean removeEdge(int v, int w) {
    validateVertex(v); validateVertex(w);
    BagX<Integer> bv = adj[v]; boolean fw = false; int iw = -1;  
    BagX<Integer> bw = adj[w]; boolean fv = false; int iv = -1;
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
    GraphEx4106 g = null;
    while(p.hasNext()) {
      int[] a = p.next();
      int[][] b = new int[x.length][];
      for (int i = 0; i < a.length; i++) b[i] = x[a[i]];
//      par(b);
      g = new GraphEx4106(v,e,b);
      par(g.adj());
    }  
  }

  public static void main(String[] args) {
    // 0-1, 1-2, 2-3, 3-0
    int[][] x = new int[4][];
    x[0] = new int[]{0,1};
    x[1] = new int[]{1,2};
    x[2] = new int[]{2,3};
    x[3] = new int[]{3,0};
    generateAllPermutations(4,4,x);

//    In in = new In(args[0]);
//    GraphEx4106 G = new GraphEx4106(in);
//    StdOut.println(G);
//    System.out.println("avgDegree="+G.avgDegree());
//    System.out.println("avgDegree2="+G.avgDegree2());
//    System.out.println("numberOfSelfLoops="+G.numberOfSelfLoops());
//    System.out.println("count="+G.count());
//    System.out.print("id"); par(G.id());
//    System.out.print("adj="); par(G.adj());
//    System.out.println("hasSelfLoop="+G.hasSelfLoop());
//    if (G.hasSelfLoop()) System.out.println("selfLoop="+G.selfLoop());
//    System.out.println("hasParallelEdges="+G.hasParallelEdges());
//    if (G.hasParallelEdges()) System.out.print("parallelEdges="+G.parallelEdges()); 
  }

}
