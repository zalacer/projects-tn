package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQY;

// based on PrimMSTBX but modified for dense graphs to find the non-tree vertex 
// with the smallest value by examining marked[] and distTo[] instead of using
// a priority queue for ex4329.

@SuppressWarnings("unused")
public class PrimMSTBXV2 {
  private EdgeX[] edgeTo; // shortest edge from tree vertex
  private double[] distTo; // distTo[w] = edgeTo[w].weight()
  private boolean[] marked; // true if v on tree
  private IndexMinPQY<Double> pq; // eligible crossing edges
  private EdgeWeightedGraphX mst;
  private int V;
  private int numberMarked = 0;
  
  public PrimMSTBXV2(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "PrimMSTBXV2: EdgeWeightedGraphX is null");
    V = G.V();
    edgeTo = new EdgeX[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    distTo[0] = 0.0;
    boolean first = true;
    while (numberMarked < V) {
      if (first) { visit(G, 0); first = false; }
      else {
        int x = findClosestNonTreeVertex();
        if (x == -1) break;
        else visit(G, x);
      }
    }
    mst();
  }
  
  private void visit(EdgeWeightedGraphX G, int v) { 
    // Add v to tree; update data structures.
    marked[v] = true; numberMarked++;
    for (EdgeX e : G.adj(v)) {
      int w = e.other(v);
      if (marked[w]) continue; // v-w is ineligible.
      if (e.weight() < distTo[w]) { 
        // EdgeX e is new best connection from tree to w.
        edgeTo[w] = e;
        distTo[w] = e.weight();
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
  
  public EdgeWeightedGraphX mst() { 
    mst = new EdgeWeightedGraphX(edges());
    return mst;  
  }
 
  public Iterable<EdgeX> edges() {
    Queue<EdgeX> mst = new Queue<>();
    for (int v = 0; v < edgeTo.length; v++) {
      EdgeX e = edgeTo[v];
      if (e != null) mst.enqueue(e);
    }
    return mst;
  }
    
  public double weight() {
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    In in = new In("tinyEWG.txt");
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
    PrimMSTBXV2 p = new PrimMSTBXV2(G);
    for (EdgeX e : p.edges()) {
        StdOut.println(e);
    }
    StdOut.printf("%.5f\n", p.weight());
    System.out.println();
    EdgeWeightedGraphX mst = p.mst;
    System.out.println("mst.V() = "+mst.V());
    System.out.println("mst.E() = "+mst.E());
    System.out.println("mst.isConnected() = "+mst.isConnected());

  }

}
