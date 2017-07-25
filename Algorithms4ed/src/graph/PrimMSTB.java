package graph;

import static v.ArrayUtils.par;

import ds.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;

// eager prim from text page 622

//@SuppressWarnings("unused")
public class PrimMSTB {
  private EdgeX[] edgeTo; // shortest edge from tree vertex
  private double[] distTo; // distTo[w] = edgeTo[w].weight()
  private boolean[] marked; // true if v on tree
  private IndexMinPQ<Double> pq; // eligible crossing edges
  
  public PrimMSTB(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "PrimMSTB: EdgeWeightedGraphX is null");
//    if (!G.isConnected()) throw new IllegalArgumentException(
//        "PrimMSTB: EdgeWeightedGraphX isn't connected");
    edgeTo = new EdgeX[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    pq = new IndexMinPQ<Double>(G.V());
    distTo[0] = 0.0;
    pq.insert(0, 0.0); // Initialize pq with 0, weight 0.
    while (!pq.isEmpty()) visit(G, pq.delMin()); // Add closest vertex to tree.
  }
  
  public PrimMSTB(EdgeWeightedGraphX G, int start) {
    // initialize pq with vertex start instead of always 0
    if (G == null) throw new IllegalArgumentException(
        "PrimMSTB: EdgeWeightedGraphX is null");
    if (start < 0) throw new IllegalArgumentException(
        "PrimMSTB: start < 0");
    if (start > G.V()-1) throw new IllegalArgumentException(
        "PrimMSTB: start > G.V()-1");
//    if (!G.isConnected()) throw new IllegalArgumentException(
//        "PrimMSTB: EdgeWeightedGraphX isn't connected");
    edgeTo = new EdgeX[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    pq = new IndexMinPQ<Double>(G.V());
    distTo[start] = 0.0;
    pq.insert(start, 0.0); // Initialize pq with vertex start, weight 0.
    while (!pq.isEmpty()) visit(G, pq.delMin()); // Add closest vertex to tree.
  }
  
  private void visit(EdgeWeightedGraphX G, int v) { 
    // Add v to tree; update data structures.
    marked[v] = true;
    for (EdgeX e : G.adj(v)) {
      int w = e.other(v);
      if (marked[w]) continue; // v-w is ineligible.
      if (e.weight() < distTo[w]) { 
        // EdgeX e is new best connection from tree to w.
        edgeTo[w] = e;
        distTo[w] = e.weight();
        if (pq.contains(w)) pq.changeKey(w, distTo[w]);
        else pq.insert(w, distTo[w]);
      }
    }
    System.out.println("v = "+v);
    par(edgeTo); System.out.println();
  }
  
//  public Iterable<Edge> edges() // See Exercise 4.3.21.
//  public double weight() // See Exercise 4.3.31.
  
  public Iterable<EdgeX> edges() {
    // from PrimMST
    Queue<EdgeX> mst = new Queue<>();
    for (int v = 0; v < edgeTo.length; v++) {
      EdgeX e = edgeTo[v];
      if (e != null) {
        mst.enqueue(e);
      }
    }
    return mst;
  }
  
  public double weight() {
    // from PrimMST
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
    PrimMSTB mst = new PrimMSTB(G);
    for (EdgeX e : mst.edges()) {
        StdOut.println(e);
    }
    StdOut.printf("%.5f\n", mst.weight());
    
//    EdgeWeightedGraphX g = new EdgeWeightedGraphX(3);
//    g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(0,2,2); 
//    
//    for (int i = 0; i < 3; i++)  {
//      System.out.println("start = "+i+":");
//      PrimMSTB p = new PrimMSTB(g,i);
//      for (EdgeX e : p.edges()) StdOut.println(e);
//      System.out.printf("%.5f\n", p.weight());
//      System.out.println();
//    }
    

  }

}
