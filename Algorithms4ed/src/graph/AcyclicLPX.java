package graph;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class AcyclicLPX {
  private double[] distTo;          // distTo[v] = distance  of longest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on longest s->v path
  private int V;

  public AcyclicLPX(EdgeWeightedDigraphX G, int s) {
    if (s < 0) throw new IllegalArgumentException("AcyclicLPX: s < 0");
    if (G == null) throw new IllegalArgumentException("AcyclicLPX: G == null");
    V = G.V();
    distTo = new double[V];
    edgeTo = new DirectedEdgeX[V];
    validateVertex(s);
    for (int v = 0; v < V; v++)
      distTo[v] = Double.NEGATIVE_INFINITY;
    distTo[s] = 0.0;
    TopologicalX topological = new TopologicalX(G);
    if (!topological.hasOrder()) throw new IllegalArgumentException("Digraph is not acyclic.");
    for (int v : topological.order()) for (DirectedEdgeX e : G.adj(v)) relax(e);
  }

  private void relax(DirectedEdgeX e) {
    int v = e.from(), w = e.to();
    if (distTo[w] < distTo[v] + e.weight()) {
      distTo[w] = distTo[v] + e.weight();
      edgeTo[w] = e;
    }       
  }

  public double distTo(int v) { validateVertex(v); return distTo[v]; }

  public boolean hasPathTo(int v) {
    validateVertex(v);
    return distTo[v] > Double.NEGATIVE_INFINITY;
  }

  public Iterable<DirectedEdgeX> pathTo(int v) {
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<DirectedEdgeX> path = new Stack<>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
      path.push(e);
    }
    return path;
  }
  
  public Seq<DirectedEdgeX> seqTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo(v)) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
    return new Seq<>(path);
  }
  
  public Seq<Seq<DirectedEdgeX>> allPaths() {
    // return a Seq of all shortest paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) paths.add(seqTo(i));
    return paths;
  }
  
  public Seq<DirectedEdgeX> longestPath() {
    int max = 0; Seq<DirectedEdgeX> longest = null;
    for (Seq<DirectedEdgeX> p : allPaths()) {
      if (longest == null) { longest = p; max = p.size(); }
      else if (p.size()>max) { longest = p; max = p.size(); }
    }
    return longest;
  }

  private void validateVertex(int v) {
    int V = distTo.length;
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    int s = Integer.parseInt(args[1]);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);

    AcyclicLPX lp = new AcyclicLPX(G, s);

    for (int v = 0; v < G.V(); v++) {
      if (lp.hasPathTo(v)) {
        StdOut.printf("%d to %d (%.2f)  ", s, v, lp.distTo(v));
        for (DirectedEdgeX e : lp.pathTo(v)) {
          StdOut.print(e + "   ");
        }
        StdOut.println();
      }
      else {
        StdOut.printf("%d to %d         no path\n", s, v);
      }
    }
  }
}
