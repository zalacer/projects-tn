package graph;

import ds.Queue;
import ds.UF;
import edu.princeton.cs.algs4.In;
import pq.MinPQ;

// from code for ALGORITHM 4.8 on page 627 in the text with
// addition of eagerWeight and weight (lazyWeight)

public class KruskalMSTB {
  private Queue<EdgeX> mst;
  private boolean showEagerWeight = false;
  private transient double eagerWeight = 0;
  
  public KruskalMSTB(EdgeWeightedGraphX G) {
    mst = new Queue<EdgeX>();
    MinPQ<EdgeX> pq = new MinPQ<EdgeX>(G.edges());
    UF uf = new UF(G.V());
    while (!pq.isEmpty() && mst.size() < G.V()-1) {
      EdgeX e = pq.delMin(); // Get min weight edge on pq
      int v = e.either(), w = e.other(v); // and its vertices.
      if (uf.connected(v, w)) continue; // Ignore ineligible edges.
//      System.out.println(v+" isn't connected to "+w);
      uf.union(v, w); // Merge components.
      mst.enqueue(e); // Add edge to mst.
      eagerWeight += e.w();
    }
  }
  
  public KruskalMSTB(EdgeWeightedGraphX G, boolean showEagerWeights) {
    showEagerWeight = showEagerWeights;
    mst = new Queue<EdgeX>();
    MinPQ<EdgeX> pq = new MinPQ<EdgeX>(G.edges());
    UF uf = new UF(G.V());
    while (!pq.isEmpty() && mst.size() < G.V()-1) {
      EdgeX e = pq.delMin(); // Get min weight edge on pq
      int v = e.either(), w = e.other(v); // and its vertices.
      if (uf.connected(v, w)) continue; // Ignore ineligible edges.
//      System.out.println(v+" isn't connected to "+w);
      uf.union(v, w); // Merge components.
      mst.enqueue(e); // Add edge to mst.
      eagerWeight += e.w();
      if (showEagerWeight) System.out.println("eagerWeight = "+eagerWeight);
    }
  }
  
  public Iterable<EdgeX> edges() { return mst; }
  
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
    KruskalMSTB k = new KruskalMSTB(G,true);
    System.out.println("\nMST:");
    for (EdgeX e : k.edges()) {
        System.out.println(e);
    }
    System.out.println("eagerWeight = "+k.eagerWeight());
    System.out.println("lazyWeight = "+k.lazyWeight());
  }

/*
    eagerWeight = 0.16
    eagerWeight = 0.33
    eagerWeight = 0.52
    eagerWeight = 0.78
    eagerWeight = 1.06
    eagerWeight = 1.4100000000000001
    eagerWeight = 1.81
    
    MST:
    (0-7,0.16000)
    (2-3,0.17000)
    (1-7,0.19000)
    (0-2,0.26000)
    (5-7,0.28000)
    (4-5,0.35000)
    (6-2,0.40000)
    eagerWeight = 1.81
    lazyWeight = 1.81
*/
  
}
