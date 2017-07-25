package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.BellmanFordSPX;
import graph.DijkstraSPX;
import graph.DirectedEdgeX;
import graph.EdgeX;
import graph.EdgeWeightedDigraphX;
import graph.EdgeWeightedGraphX;

/* p686
  4.4.16 Suppose that we convert an EdgeWeightedGraph into an EdgeWeightedDigraph
  by creating two DirectedEdge objects in the EdgeWeightedDigraph (one in each 
  direction) for each Edge in the EdgeWeightedGraph (as described for Dijkstra’s 
  algorithm in the Q&A on page 684) and then use the Bellman-Ford algorithm. 
  Explain why this approach fails spectacularly.
  
  Testing shows that BellmanFordSPX computes the same shortest paths as DijkstraSPX
  algo for tinyEWD.txt and mediumEWD.txt after converting their EdgeXs to DirectedEdgeXs 
  as stated above and demonstrated below.
  
 */  

public class Ex4416BellmanFordOnEdgeWeightedGraphByConversion2EdgeWeightedDigraph {

  public static Seq<Seq<DirectedEdgeX>> bellmanFord(String ewg, int source) {
    if (ewg == null) throw new IllegalArgumentException("bellmanFord: ewg is null");
    In in = new In(ewg);
    EdgeWeightedGraphX g = new EdgeWeightedGraphX(in);
    int V = g.V(), s = source;
    if (s < 0 || s > V-1) throw new IllegalArgumentException("bellmanFord: source is out of bounds");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (EdgeX e : g.edges()) {
      G.addEdge(new DirectedEdgeX(e.u(),e.v(),e.w()));
      G.addEdge(new DirectedEdgeX(e.v(),e.u(),e.w()));
    }
    //System.out.println("EdgeWeightedDigraphX G:");
    //System.out.println(G);
    // compute shortest paths
    BellmanFordSPX sp = new BellmanFordSPX(G, s);
    // print negative cycle
    if (sp.hasNegativeCycle()) {
      System.out.println("\nbellmanFord negative cycle:");
      for (DirectedEdgeX e : sp.negativeCycle()) System.out.println(e);
    }
    else {
      System.out.println("\nbellmanFord shortest paths:");
      for (int v = 0; v < G.V(); v++)
        if (sp.hasPathTo(v)) {
          System.out.printf("%d to %d (%5.2f)  ", s, v, sp.distTo(v));
          for (DirectedEdgeX e : sp.pathTo(v)) System.out.print(e + "   ");
          System.out.println();
        }
        else System.out.printf("%d to %d           no path\n", s, v);
    }
    return sp.allPaths();
  }
  
  public static Seq<Seq<DirectedEdgeX>> dijkstra(String ewg, int source) {
    if (ewg == null) throw new IllegalArgumentException("dijkstra: ewg is null");
    In in = new In(ewg);
    EdgeWeightedGraphX g = new EdgeWeightedGraphX(in);
    int V = g.V(), s = source;
    if (s < 0 || s > V-1) throw new IllegalArgumentException("dijkstra: source is out of bounds");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    for (EdgeX e : g.edges()) {
      G.addEdge(new DirectedEdgeX(e.u(),e.v(),e.w()));
      G.addEdge(new DirectedEdgeX(e.v(),e.u(),e.w()));
    }
    //System.out.println("\nEdgeWeightedDigraphX G:");
    //System.out.println(G);
    // compute shortest paths
    DijkstraSPX sp = new DijkstraSPX(G, s, "quiet");
    System.out.println("\ndijkstra shortest paths:");
    // print shortest paths
    for (int t = 0; t < G.V(); t++)
      if (sp.hasPathTo(t)) {
        System.out.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
        for (DirectedEdgeX e : sp.pathTo(t)) System.out.print(e + "   ");
        System.out.println();
      }
      else System.out.printf("%d to %d         no path\n", s, t);
    return sp.allPaths();
  }

  public static void main(String[] args) {

    System.out.println("testing with tinyEWG.txt:");
    Seq<Seq<DirectedEdgeX>> bp = bellmanFord("tinyEWG.txt",0);
    Seq<Seq<DirectedEdgeX>> dp = dijkstra("tinyEWG.txt",0);
    if (!bp.equals(dp)) System.err.println("\nbellmanFord paths != dijksta paths");
    else System.out.println("\nbellmanFord paths == dijksta paths");
    
    System.out.println("\ntesting with mediumEWG.txt:");
    bp = bellmanFord("mediumEWG.txt",0);
    dp = dijkstra("mediumEWG.txt",0);
    if (!bp.equals(dp)) System.err.println("\nbellmanFord paths != dijksta paths");
    else System.out.println("\nbellmanFord paths == dijksta paths\n");

  }

}


