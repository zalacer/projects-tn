package graph;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// same as DijkstraSPXV2 but using AdjMatrixEdgeWeightedDigraphXs instead of
// EdgeWeightedDigraphXs.

// for ex4.4.26.

public class DijkstraSPXV2AM {
  private DirectedEdgeX[] edgeTo; 
  private double[] distTo; 
  private boolean[] marked; 
  private AdjMatrixEdgeWeightedDigraphX G;
  private int V;
  private int numberMarked = 0;
  
  public DijkstraSPXV2AM(AdjMatrixEdgeWeightedDigraphX G, int source) {
    if (G == null) throw new IllegalArgumentException(
        "DijkstraSPXV2: EdgeWeightedGraphX is null");
    this.G = G;
    V = G.V(); 
    edgeTo = new DirectedEdgeX[G.V()];
    distTo = new double[V];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    distTo[source] = 0.0;
    boolean first = true;
    while (numberMarked < V) {
      if (first) { relax(source); first = false; }
      else {
        int x = findClosestNonTreeVertex();
        if (x == -1) break;
        else relax(x);
      }
    }
  }
  
  private void relax(int u) { 
    marked[u] = true; numberMarked++;
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.to();
      if (marked[v]) continue; // u-v is ineligible.
      if (distTo[v] > distTo[u] + e.weight()) {
        distTo[v] = distTo[u] + e.weight();
        edgeTo[v] = e;
      }
    }
  }
  
  private int findClosestNonTreeVertex() {
    // replaces priority queue
    int v = -1; double minDist = Double.POSITIVE_INFINITY;
    for (int i = 0; i < V; i++) {
      if (marked[i] == false && distTo[i] < minDist) {
        minDist = distTo[i]; v = i;
      }
    }
    return v;
  }

  public double[] distTo() { return distTo; } ;
  
  public double distTo(int v) {
    // return length of shortest path from source to v 
    validateVertex(v);
    return distTo[v];
  }
  
  public DirectedEdgeX[] edgeTo() { return edgeTo; }
    
  public Seq<DirectedEdgeX> sptEdges() { return new Seq<>(edgeTo); }

  public boolean hasPathTo(int v) {
    // return true if exists path from source to v else false
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
  }

  public Iterable<DirectedEdgeX> pathTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()])
      path.push(e);
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
  
  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  public static void main(String[] args) {
    In in = new In("tinyEWD.txt");
    AdjMatrixEdgeWeightedDigraphX G = new AdjMatrixEdgeWeightedDigraphX(new EdgeWeightedDigraphX(in));
    int source = 0;
    DijkstraSPXV2AM sp = new DijkstraSPXV2AM(G,source);
    // print shortest path
    for (int t = 0; t < G.V(); t++) {
        if (sp.hasPathTo(t)) {
            StdOut.printf("%d to %d (%.2f)  ", source, t, sp.distTo(t));
            for (DirectedEdgeX e : sp.pathTo(t)) {
                StdOut.print(e + "   ");
            }
            StdOut.println();
        }
        else {
            StdOut.printf("%d to %d         no path\n", source, t);
        }
    }

  }

}
