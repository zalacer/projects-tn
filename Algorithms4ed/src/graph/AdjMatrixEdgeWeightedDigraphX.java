package graph;


// from AdjMatrixEdgeWeightedDigraph.java by replacing DirectedEdge with DirectedEdgeX

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class AdjMatrixEdgeWeightedDigraphX {
  private static final String NEWLINE = System.getProperty("line.separator");

  private final int V;
  private int E;
  private DirectedEdgeX[][] adj;

  public AdjMatrixEdgeWeightedDigraphX(int V) {
    if (V < 0) throw new IllegalArgumentException("number of vertices must be nonnegative");
    this.V = V;
    this.E = 0;
    this.adj = new DirectedEdgeX[V][V];
  }

  public AdjMatrixEdgeWeightedDigraphX(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException("number of edges must be nonnegative");
    if (E > V*V) throw new IllegalArgumentException("too many edges");

    // can be inefficient
    while (this.E != E) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      double weight = Math.round(100 * StdRandom.uniform()) / 100.0;
      addEdge(new DirectedEdgeX(v, w, weight));
    }
  }
  
  public AdjMatrixEdgeWeightedDigraphX(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    V = G.V(); 
    if (G.E() > V*V) throw new IllegalArgumentException("too many edges");
    this.adj = new DirectedEdgeX[V][V];
    for (DirectedEdgeX e : G.edges()) addEdge(e);
  }

  public int V() { return V; }

  public int E() { return E; }
  
  public void addEdge(DirectedEdgeX e) {
    int v = e.from();
    int w = e.to();
    validateVertex(v);
    validateVertex(w);
    if (adj[v][w] == null) {
      E++;
      adj[v][w] = e;
    }
  }

  public Iterable<DirectedEdgeX> adj(int v) {
    validateVertex(v);
    return new AdjIterator(v);
  }

  // support iteration over graph vertices
  private class AdjIterator implements Iterator<DirectedEdgeX>, Iterable<DirectedEdgeX> {
    private int v;
    private int w = 0;

    public AdjIterator(int v) {
      this.v = v;
    }

    public Iterator<DirectedEdgeX> iterator() {
      return this;
    }

    public boolean hasNext() {
      while (w < V) {
        if (adj[v][w] != null) return true;
        w++;
      }
      return false;
    }

    public DirectedEdgeX next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return adj[v][w++];
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (DirectedEdgeX e : adj(v)) {
        s.append(e + "  ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    AdjMatrixEdgeWeightedDigraphX G = new AdjMatrixEdgeWeightedDigraphX(V, E);
    StdOut.println(G);
  }

}
