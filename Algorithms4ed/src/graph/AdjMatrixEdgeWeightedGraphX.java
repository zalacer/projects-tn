package graph;

//import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import ds.Seq;
import edu.princeton.cs.algs4.StdRandom;
import exceptions.InvalidDataException;

// an EdgeWeightedGraph using an adjacency matrix for dense graphs
// based on AdjMatrixEdgeWeightedDigraph
// Parallel edges are disallowed; self-loops are allowed.

public class AdjMatrixEdgeWeightedGraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;
  private int E;
  private EdgeX[][] adj;
  private transient boolean validate = true;
  private transient GraphX g;

  public AdjMatrixEdgeWeightedGraphX(int V) {
    if (V < 0) throw new IllegalArgumentException("number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    this.adj = new EdgeX[V][V];
    g = new GraphX(V);
  }

  public AdjMatrixEdgeWeightedGraphX(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException("number of edges must be nonnegative");
    if (E > V*V) throw new IllegalArgumentException("too many edges");
    g = new GraphX(V);
    // can be inefficient, doesn't work for E close to V*V
    while (this.E != E) {
      int u = StdRandom.uniform(V);
      int v = StdRandom.uniform(V);
      if (u == v) continue;
      double w = Math.round(100 * StdRandom.uniform()) / 100.0;
      validate = false;
      if (!contains(u,v)) addEdge(new EdgeX(u, v, w));
      g.search();
    }
  }

  public int V() { return V; }

  public int E() { return E; }

  public void addEdge(EdgeX e) {
    int u = e.u(), v = e.v();
    if (validate) validateVertices(u,v);
    if (adj[u][v] == null) { 
      E++; 
      adj[u][v] = e;
      adj[v][u] = e;
      g.addEdge(u,v);
    }
    System.out.println("E = "+E);
  }
  
  public boolean contains(int u, int v) {
    if (u < 0 || u >= V || v < 0 || v >= V) return false;
    else if (Objects.isNull(adj[u][v]) && Objects.isNull(adj[v][u])) return false;
    else if (Objects.isNull(adj[u][v]) && Objects.nonNull(adj[v][u])
        || Objects.nonNull(adj[u][v]) && Objects.isNull(adj[v][u]))
        throw new InvalidDataException("contains: adj["+u+"]["+v+"] and "
            + "adj["+v+"]["+u+"] aren't both null or both non-null");
    else if (Objects.nonNull(adj[u][v]) && Objects.nonNull(adj[v][u]) 
        && !adj[u][v].equals(adj[v][u]))
        throw new InvalidDataException("contains: adj["+u+"]["+v+"] and "
        + "adj["+v+"]["+v+"] are both non-null but aren't equal");
    else return true;
  }
  
  public boolean hasEdge(int u, int v) { return contains(u,v); }
  
  public EdgeX findEdge(int u, int v, Iterable<EdgeX> itbl) {
    // return the first EdgeX with u and v vertices in itbl else return null
    for (EdgeX e : itbl) 
      if (e.u() == u && e.v() == v || e.u() == v && e.v() == u) return e;
    return null;
  }
  
  public EdgeX removeEdge(int u, int v) {
    if (!contains(u,v)) return null;
    EdgeX e = adj[u][v];
    adj[u][v] = adj[v][u] = null;
    E--;
    return e;
  }
  
  public Iterable<EdgeX> adj(int v) {
    validateVertex(v);
    return new AdjIterator(v);
  }

  // support iteration over graph vertices
  private class AdjIterator implements Iterator<EdgeX>, Iterable<EdgeX> {
    private int u, v = 0;
    public AdjIterator(int u) { this.u = u; }
    public Iterator<EdgeX> iterator() { return this; }
    public boolean hasNext() {
      while (v < V) { if (adj[u][v] != null) return true; v++; }
      return false;
    }
    public EdgeX next() {
      if (!hasNext()) throw new NoSuchElementException();
      return adj[u][v++];
    }
  }
  
  public Seq<EdgeX> edgeSeq() {
    Seq<EdgeX> s = new Seq<>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (EdgeX e : adj(v)) {
        if (e.other(v) > v) s.add(e);
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) s.add(e);
          selfLoops++;
        }
      }
    }
    return s;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (EdgeX e : adj(v)) s.append(e + "  ");
      s.append(NEWLINE);
    }
    return s.toString();
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  private void validateVertices(int u, int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
    if (u < 0 || u >= V) throw new IllegalArgumentException("vertex "+u+" is out of bounds");
  }

  public static void main(String[] args) {
    int V = 5;
    int E = 12;
    AdjMatrixEdgeWeightedGraphX G = new AdjMatrixEdgeWeightedGraphX(V, E);
    System.out.println(G);
    for (EdgeX e : G.edgeSeq()) System.out.println(e);
  }

}
