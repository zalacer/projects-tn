package graph;

import ds.Queue;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.MinPQ;
import st.HashSET;

// based on KruskalMSTB but uses Seq<HashSET<Integer>> instead of UF

public class KruskalMSTHashSET {
  private Queue<EdgeX> mst;
  Seq<HashSET<Integer>> c; // connected components
  
  public KruskalMSTHashSET(EdgeWeightedGraphX G) {
    mst = new Queue<EdgeX>();
    MinPQ<EdgeX> pq = new MinPQ<EdgeX>(G.edges());
    c = new Seq<>();
    LP: while (!pq.isEmpty() && mst.size() < G.V()-1) {
      EdgeX e = pq.delMin(); // Get min weight edge on pq
      int v = e.either(), w = e.other(v); // and its vertices.
      for (int i = 0; i < c.size(); i++)
        if (c.get(i).containsAll(v,w)) continue LP; // Ignore ineligible edges.
      // Merge components
      Stack<Integer> d = new Stack<>(); boolean b = true;
      for (int i = 0; i < c.size(); i++) {
        if (c.get(i).containsAny(v,w)) {
          c.get(i).addArray(v,w);
          for (int j = i+1; j < c.size(); j++) {
            if (c.get(j).containsAny(v,w) ) {
              c.get(i).addIterable(c.get(j));
              d.push(j);
            }
          }
          for (int k : d) c.delete(k);
          b = false;
          break;
        }       
      }
      // end merge
      if (b) c.add(new HashSET<>(v,w));
      mst.enqueue(e); // Add edge to mst.
      System.out.println(c);
    }
  }
  public Iterable<EdgeX> edges() { return mst; }

  public double weight() { // See Exercise 4.3.31.
    // from PrimMST
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
    KruskalMSTHashSET mst = new KruskalMSTHashSET(G);
    for (EdgeX e : mst.edges()) {
        StdOut.println(e);
    }
    StdOut.printf("%.5f\n", mst.weight());
  }
  
/*
    (0-7,0.16000)
    (2-3,0.17000)
    (1-7,0.19000)
    (0-2,0.26000)
    (5-7,0.28000)
    (4-5,0.35000)
    (6-2,0.40000)
    1.81000  
*/  
  
}
