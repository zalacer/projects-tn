package graph;

import ds.Queue;
//import ds.Seq;
import edu.princeton.cs.algs4.In;
import pq.MinPQ;

// text p619

public class LazyPrimMST {
  private boolean[] marked; // MST vertices
  private Queue<EdgeX> mst; // MST edges
  private MinPQ<EdgeX> pq; // crossing (and ineligible) edges
  private boolean showEagerWeight = false;
  private transient double eagerWeight = 0;

  public LazyPrimMST(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "LazyPrimMST: EdgeWeightedGraphX is null");
    if (!G.isConnected()) throw new IllegalArgumentException(
        "LazyPrimMST: EdgeWeightedGraphX isn't connected");
    pq = new MinPQ<EdgeX>();
    marked = new boolean[G.V()];
    mst = new Queue<EdgeX>();
    visit(G, 0); // assumes G is connected (see Exercise 4.3.22)
    while (!pq.isEmpty()) {
      EdgeX e = pq.delMin(); // Get lowest-weight
      int v = e.either(), w = e.other(v); // edge from pq.
      if (marked[v] && marked[w]) continue; // Skip if ineligible.
      mst.enqueue(e); // Add edge to tree.
      eagerWeight += e.w();
      if (!marked[v]) visit(G, v); // Add vertex to tree
      if (!marked[w]) visit(G, w); // (either v or w).
    }
  }
  
  public LazyPrimMST(EdgeWeightedGraphX G, boolean showEagerWeights) {
    if (G == null) throw new IllegalArgumentException(
        "LazyPrimMST: EdgeWeightedGraphX is null");
    if (!G.isConnected()) throw new IllegalArgumentException(
        "LazyPrimMST: EdgeWeightedGraphX isn't connected");
    showEagerWeight = showEagerWeights;
    pq = new MinPQ<EdgeX>();
    marked = new boolean[G.V()];
    mst = new Queue<EdgeX>();
    visit(G, 0); // assumes G is connected (see Exercise 4.3.22)
    while (!pq.isEmpty()) {
      EdgeX e = pq.delMin(); // Get lowest-weight
      int v = e.either(), w = e.other(v); // edge from pq.
      if (marked[v] && marked[w]) continue; // Skip if ineligible.
      mst.enqueue(e); // Add edge to tree.
      eagerWeight += e.w();
      if (showEagerWeight) System.out.println("eagerWeight = "+eagerWeight);
      if (!marked[v]) visit(G, v); // Add vertex to tree
      if (!marked[w]) visit(G, w); // (either v or w).
    }
  }

  private void visit(EdgeWeightedGraphX G, int v) { 
    // mark v and add to pq all edges from v to unmarked vertices.
    marked[v] = true; // put vertex on the mst
    for (EdgeX e : G.adj(v)) if (!marked[e.other(v)]) pq.insert(e);
  }
  
  public Iterable<EdgeX> edges()  { return mst; }
  
  public double eagerWeight() { return eagerWeight; }
  
  public double lazyWeight() { return weight(); }
  
  public double weight() {
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }
  
  public static void main(String[] args) {
    
    In in = new In(args[0]);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
    LazyPrimMST l = new LazyPrimMST(G,true);
    System.out.println("\nMST:");
    for (EdgeX e : l.edges()) System.out.println(e);
    System.out.println("eagerWeight = "+l.eagerWeight());
    System.out.println("lazyWeight = "+l.lazyWeight());
/*
    eagerWeight = 0.16
    eagerWeight = 0.35
    eagerWeight = 0.61
    eagerWeight = 0.78
    eagerWeight = 1.06
    eagerWeight = 1.4100000000000001
    eagerWeight = 1.81
    
    MST:
    (0-7,0.16000)
    (1-7,0.19000)
    (0-2,0.26000)
    (2-3,0.17000)
    (5-7,0.28000)
    (4-5,0.35000)
    (6-2,0.40000)
    eagerWeight = 1.81
    lazyWeight = 1.81
*/
  }

}
