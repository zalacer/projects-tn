package ex44;

import edu.princeton.cs.algs4.In;
import graph.BellmanFordSPX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p686
  4.4.15 What happens to Bellman-Ford if there is a negative cycle on the path 
  from s to v and then you call pathTo(v)? 
  
  It throws an UnsupportedOperationException as demonstrated below.

 */  

public class Ex4415BellmanFordNegativeCycleHandling {

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
    // print pathTo(6)
    System.out.println("\npathTo(6):");
    Iterable<DirectedEdgeX> pathTo6 = sp.pathTo(6);
    for (DirectedEdgeX e : pathTo6) System.out.print(e + "   "); System.out.println();
  }

  public static void main(String[] args) {

    bellmanFord("tinyEWDnc.txt",0);

  }

}


