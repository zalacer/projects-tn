package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Seq;
import ds.UF;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.MinPQ;

// Ex4326

@SuppressWarnings("unused")
public class KruskalCriticalEdges {
  private Queue<EdgeX> mst;
  private EdgeWeightedGraphX msg;
  private EdgeWeightedGraphX msh;
  private Seq<EdgeX> crit;
  private Seq<EdgeX> noncrit;

  
  public KruskalCriticalEdges(EdgeWeightedGraphX G) {
    crit = new Seq<>(); noncrit = new Seq<>();
    msg = new EdgeWeightedGraphX(G.V());
    mst = new Queue<EdgeX>();
    MinPQ<EdgeX> pq = new MinPQ<EdgeX>(G.edges());
    UF uf = new UF(G.V());
    while (!pq.isEmpty() && mst.size() < G.V()-1) {
      EdgeX e = pq.delMin(); // Get min weight edge on pq
      int v = e.either(), w = e.other(v); // and its vertices.
      if (uf.connected(v, w)) {
        msh = new EdgeWeightedGraphX(msg);
        msh.addEdge(e);
        Seq<EdgeX> edges = msh.edgeSeq();
        GraphX g = msh.graph();
        Integer[] cycle = g.cycle().toArray(1);
        for (int i = 0; i < cycle.length-1; i++) {
          int x = cycle[i], y = cycle[i+1];
          if (x == v && y == w || x == w && y == v) continue;
          for (EdgeX f : edges) {
            if (f.w() == e.w() &&  (x == f.u() && y == f.v() || x == f.v() && y == f.u())) {
              noncrit.add(e); noncrit.add(f);
            }
          }       
        }    
        continue; // Ignore ineligible edges.
      }
      uf.union(v, w); // Merge components.
      mst.enqueue(e); // Add edge to mst.
      msg.addEdge(e);
      crit.add(e);
    }
    for (EdgeX e : noncrit) crit.delete(e);
  }
  public Iterable<EdgeX> edges() { return mst; }
  
  public Seq<EdgeX> crit() { return crit; }
  
  public Seq<EdgeX> noncrit() { return noncrit; }

  public double weight() { // See Exercise 4.3.31.
    // from PrimMST
    double weight = 0.0;
    for (EdgeX e : edges()) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    
    int V = 8;
//    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
//       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
//       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.26 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    KruskalCriticalEdges mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());
    
    
//    In in = new In(args[0]);
//    EdgeWeightedGraphX G = new EdgeWeightedGraphX(in);
//    KruskalCriticalEdges mst = new KruskalCriticalEdges(G);
//    for (EdgeX e : mst.edges()) {
//        StdOut.println(e);
//    }
//    StdOut.printf("%.5f\n", mst.weight());
//    System.out.println("crit = "+mst.crit());
//    System.out.println("noncrit = "+mst.noncrit());

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
