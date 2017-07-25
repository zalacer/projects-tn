package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/EdgeWeightedGraph.java
// build parallel GraphX for testing and substitute AdjMatrixGraph later

public class EdgeWeightedAdjMatrixGraph {
  private static final String NEWLINE = System.getProperty("line.separator");
  private static final double NOEDGE = Double.NEGATIVE_INFINITY;
  private final int V;
  private int E;
  private double[][] adj;
  private boolean validate = true;
  private GraphX g;

  public EdgeWeightedAdjMatrixGraph(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
    this.V = V;
    adj = fillDouble(V,V,()->NOEDGE);
    g = new GraphX(V);
  }

  public EdgeWeightedAdjMatrixGraph(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    g = new GraphX(V);
    for (int i = 0; i < E; i++) {
      int u = StdRandom.uniform(V);
      int v = StdRandom.uniform(V);
      double w = Math.round(100 * StdRandom.uniform()) / 100.0;
      addEdge(u,v,w);
      g.addEdge(u, v);
    }
  }
  
  public EdgeWeightedAdjMatrixGraph(int V, String edges) {
    this(V);
    if (edges == null) throw new IllegalArgumentException("String edges == null");
    g = new GraphX(V);
    validate = false;
    String[] edgs = edges.split("\\s+"); int u,v; double w;
    int len = edgs.length %3 == 0 ? edgs.length : (edgs.length/3)*3;
    for (int i = 0; i < len-2; i+=3) {
      u = Integer.parseInt(edgs[i]);
      v = Integer.parseInt(edgs[i+1]);
      w = Double.parseDouble(edgs[i+2]);
      validateVertices(u,v);
      addEdge(u,v,w);
      g.addEdge(u,v);  
    }
    validate = true;
  }
  
  public EdgeWeightedAdjMatrixGraph(In in) {
    this(in.readInt());
    g = new GraphX(V);
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v  = in.readInt();
      validateVertices(u,v);
      double w = in.readDouble();
      addEdge(u,v,w);
      g.addEdge(u,v);
    }
    validate = true;
  }

  public EdgeWeightedAdjMatrixGraph(EdgeWeightedAdjMatrixGraph G) {
    V = G.V; E = G.E; adj = G.adj.clone(); 
    g = new GraphX(G.g); validate = G.validate;
  }
  
  public boolean isConnected() { g.search(); return g.count() == 1; }
  
   double[][] adj() { return adj; }
  
  public GraphX graph() { return g; }
  
  public int V() { return V; }

  public int E() { return E; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  private void validateVertices(int v, int w) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
    if (w < 0 || w >= V) throw new IllegalArgumentException("vertex "+w+" is out of bounds");
  }

  public void addEdge(int v, int w, double weight) {
    if (validate) validateVertices(v,w);
    if (adj[v][w] != NOEDGE) E++;
    adj[v][w] = weight;
    adj[w][v] = weight;
  }

  public Iterable<EdgeX> adj(int v) {
    Seq<EdgeX> s = new Seq<>();
    for (int i = 0; i < adj[v].length; i++)
      if (adj[v][i] != NOEDGE) s.add(new EdgeX(v,i,adj[v][i]));
    return s;
  }

  public int degree(int v) { 
    validateVertex(v);
    int c = 0;
    for (int i = 0; i < adj[v].length; i++) if (adj[v][i] != NOEDGE) c++;
    return c;
  }

  public Iterable<EdgeX> edges() {
    Seq<EdgeX> s = new Seq<>();
    for (int i = 0; i < adj.length; i++)
      for (int j = 0; j < adj[i].length; j++)
        if (adj[i][j] != NOEDGE) s.add(new EdgeX(i,j,adj[i][j]));
    return s;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int i = 0; i < adj.length; i++) {
      s.append(i + ": ");
      for (int j = 0; j < adj[i].length; j++) {
        if (adj[i][j] != NOEDGE) s.append(j + "  ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedAdjMatrixGraph g = new EdgeWeightedAdjMatrixGraph(in);
    System.out.print(g);
    System.out.println("isConnected = "+g.isConnected());
    System.out.println("GraphX:");
    System.out.println(g.graph());
  }

}

