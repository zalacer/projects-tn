package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQY;

// eager prim from text p622, essentially a clone of PrimMSTB but
// omits the PrimMSTB(EdgeWeightedGraphX G, int start) constructor
// and adds the mst() method that's used in EdgeWeightedGraphX to
// answer ex4314 and ex4315 about reconstructing the MST after
// removing or adding an edge

//@SuppressWarnings("unused")
@SuppressWarnings("unused")
public class PrimMSTBX {
  private EdgeX[] edgeTo; // shortest edge from tree vertex
  private double[] distTo; // distTo[w] = edgeTo[w].weight()
  private boolean[] marked; // true if v on tree
  private IndexMinPQY<Double> pq; // eligible crossing edges
  private EdgeWeightedGraphX mst;
  private transient boolean showEagerWeight = false;
  private transient double eagerWeight = 0;
  private transient EdgeX nextEdge; // next edge to put in MST
  private transient Seq<EdgeX> mste;   // MST edges
  private transient Seq<Integer> mstv;   // MST vertices
  
  public PrimMSTBX(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "PrimMSTB: EdgeWeightedGraphX is null");
//    if (!G.isConnected()) throw new IllegalArgumentException(
//        "PrimMSTB: EdgeWeightedGraphX isn't connected");
    edgeTo = new EdgeX[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    mste = new Seq<>();
    mstv = new Seq<>();
    pq = new IndexMinPQY<>(G.V());
    distTo[0] = 0.0;
    pq.insert(0, 0.0); // Initialize pq with 0, weight 0.
    while (!pq.isEmpty()) visit(G, pq.delMin()); // Add closest vertex to tree.
    mst();
  }
  
  public PrimMSTBX(EdgeWeightedGraphX G, boolean showEagerWeights) {
    if (G == null) throw new IllegalArgumentException(
        "PrimMSTB: EdgeWeightedGraphX is null");
//    if (!G.isConnected()) throw new IllegalArgumentException(
//        "PrimMSTB: EdgeWeightedGraphX isn't connected");
    showEagerWeight = showEagerWeights;
    edgeTo = new EdgeX[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    mste = new Seq<>();
    mstv = new Seq<>();
    pq = new IndexMinPQY<>(G.V());
    distTo[0] = 0.0;
    pq.insert(0, 0.0); // Initialize pq with 0, weight 0.
    while (!pq.isEmpty()) visit(G, pq.delMin()); // Add closest vertex to tree.
    mst();
  }
  
  private void visit(EdgeWeightedGraphX G, int v) { 
    // Add v to tree; update data structures.
    marked[v] = true;
    mstv.add(v);
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
    if (nextEdge != null) {
      eagerWeight += nextEdge.w();
      if (showEagerWeight) System.out.println("eagerWeight = "+eagerWeight);
      mste.add(nextEdge);
    }
    nextEdge = nextEdge();
//    System.out.println("v = "+v);
//    par(edgeTo); System.out.println();
  }
  
  public EdgeWeightedGraphX mst() { 
    mst = new EdgeWeightedGraphX(edges());
    return mst;  
  }
  
  private EdgeX nextEdge() {
    // return the next edge to go into the MST else null
    EdgeX min = null;
    for (int v : mstv) {
      for (EdgeX e : edgeTo) {
        if (e == null || mste.contains(e)) continue;
        int x = e.u(), y = e.v();
        if (!(x == v || y == v)) continue;
        if (x == v && marked[y] || y == v && marked[x]) continue;    
        if (min == null) min = e;
        else if (e.w() < min.w()) min = e;
      }
    }
    return min;
  }
  
  public Iterable<EdgeX> edges() {
    // from PrimMST
    Queue<EdgeX> mst = new Queue<>();
    for (int v = 0; v < edgeTo.length; v++) {
      EdgeX e = edgeTo[v];
      if (e != null) mst.enqueue(e);
    }
    return mst;
  }
  
  public double eagerWeight() { return eagerWeight; }
  
  public double lazyWeight() { return weight(); }
    
  public double weight() {
    // from PrimMST
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
    PrimMSTBX p = new PrimMSTBX(G,true);
    System.out.println("\nMST:");
    for (EdgeX e : p.edges()) StdOut.println(e);
    StdOut.printf("%.5f\n", p.weight());
    System.out.println();
    EdgeWeightedGraphX mst = p.mst;
    System.out.println("mst.V() = "+mst.V());
    System.out.println("mst.E() = "+mst.E());
    System.out.println("mst.isConnected() = "+mst.isConnected());
    System.out.println("eagerWeight = "+p.eagerWeight());
    System.out.println("lazyWeight = "+p.lazyWeight());
/*
    eagerWeight = 0.16
    eagerWeight = 0.35
    eagerWeight = 0.61
    eagerWeight = 0.78
    eagerWeight = 1.06
    eagerWeight = 1.4100000000000001
    eagerWeight = 1.81
    
    MST:
    (1-7,0.19000)
    (0-2,0.26000)
    (2-3,0.17000)
    (4-5,0.35000)
    (5-7,0.28000)
    (6-2,0.40000)
    (0-7,0.16000)
    1.81000
    
    mst.V() = 8
    mst.E() = 7
    mst.isConnected() = true
    eagerWeight = 1.81
    lazyWeight = 1.8099999999999998
*/

    
//    EdgeWeightedGraphX g = new EdgeWeightedGraphX(3);
//    g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(0,2,2); 
//    
//    System.out.println("KruskalMSTB:");
//    KruskalMSTB k = new KruskalMSTB(g);
//    for (EdgeX e : k.edges()) System.out.println(e);
//    System.out.printf("%.5f\n", k.weight());
//    
//    System.out.println("\nPrimMSTB:");
//    PrimMSTBX p = new PrimMSTBX(g);
//    for (EdgeX e : p.edges()) StdOut.println(e);
//    System.out.printf("%.5f\n", p.weight());
    

  }

}
