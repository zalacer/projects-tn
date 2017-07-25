package ex44;

import edu.princeton.cs.algs4.In;
import graph.BellmanFordSPX;
import graph.DijkstraSPX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p686
  4.4.14 Show the paths that would be discovered by the two strawman 
  approaches described on page 668 for the example tinyEWDn.txt shown 
  on that page.

  Strawman 1 is demonstrated below. Strawman 2 isn't demonstrated because
  it isn't well defined and the discussion of it on p668 proves that the
  attempt to define it is futile because Dijkstra’s algorithm cannot be
  adapted to handle digraphs with negative weights. The correct results
  using BellmanFordSPX are also given below for comparison.

 */  

public class Ex4414PathsDiscoveredByStrawmanApproachesOnPage668 {

  public static void strawman1(String ewd, int source) {
    // add the absolute value of the most negative edge weight to all edge
    // weights and find shortest paths using eager Diskstra SP algo
    if (ewd == null) throw new IllegalArgumentException("strawman1: ewd is null");
    In in = new In(ewd);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V(), s = source;
    if (s < 0 || s > V-1) throw new IllegalArgumentException("strawman1: source is out of bounds");
    // adjust weights of all edges of G
    double minWeight = Math.abs(G.minWeight());
    for (DirectedEdgeX  e : G.edgeSeq()) e.setW(e.w()+minWeight);
    // compute shortest paths
    DijkstraSPX sp = new DijkstraSPX(G, s, "quiet");
    System.out.println("strawman1 shortest paths:");
    System.out.println("(add absolute value of least weight to all edges)");
    // print shortest paths
    for (int t = 0; t < G.V(); t++)
      if (sp.hasPathTo(t)) {
        System.out.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
        for (DirectedEdgeX e : sp.pathTo(t)) System.out.print(e + "   ");
        System.out.println();
      }
      else System.out.printf("%d to %d         no path\n", s, t);
  }

  public static void bellmanFord(String ewd, int source) {
    if (ewd == null) throw new IllegalArgumentException("bellmanFord: ewd is null");
    In in = new In(ewd);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V(), s = source;
    if (s < 0 || s > V-1) throw new IllegalArgumentException("bellmanFord: source is out of bounds");
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
  }

  public static void main(String[] args) {

    strawman1("tinyEWDn.txt",0); 
    bellmanFord("tinyEWDn.txt",0);

  }

}


