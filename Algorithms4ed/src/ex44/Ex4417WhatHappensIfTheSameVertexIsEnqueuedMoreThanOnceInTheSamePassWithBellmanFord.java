package ex44;

import ds.BagX;
import graph.BellmanFordSPX;
import graph.DigraphGeneratorX;
import graph.DigraphX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p686
  4.4.17  What happens if you allow a vertex to be enqueued more than once 
  in the same pass in the Bellman-Ford algorithm?
  Answer : The running time of the algorithm can go exponential. For example, 
  describe what happens for the complete edge-weighted digraph whose edge 
  weights are all -1.
  
  Regarding the complete edge-weighted digraph whose edge weights are all -1,
  if there is more than 1 vertex, the Bellman-Ford algorithm finds a negative 
  cycle and exits. For a  small number of vertices > 1 it does this quickly 
  but not for many vertices. The run time may increase exponentially with the 
  number of vertices. For the series of  tests with complete graphs below, the 
  number of edges increases exponentially.
  
 */  

public class Ex4417WhatHappensIfTheSameVertexIsEnqueuedMoreThanOnceInTheSamePassWithBellmanFord {

  public static void bellmanFordComplete(int V, double w, int source) {
    if (V < 1) throw new IllegalArgumentException("bellmanFord: V < 1");
    if (source < 0 || source > V-1) throw new IllegalArgumentException(
        "bellmanFordComplete: source is out of bounds");
    DigraphX complete = DigraphGeneratorX.complete(V);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
    BagX<Integer>[] adj = complete.adj();
    for (int i = 0; i < adj.length; i++) 
      for (Integer j : adj[i]) G.addEdge(new DirectedEdgeX(i,j,w));      
    //System.out.println("EdgeWeightedDigraphX G:");
    //System.out.println(G);
    int s = source;
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

    System.out.println("1 vertex");
    bellmanFordComplete(1,-1,0);
    System.out.println("\n2 vertices");
    bellmanFordComplete(2,-1,0);
    System.out.println("\n3 vertices");
    bellmanFordComplete(3,-1,0);
    System.out.println("\n11 vertices");
    bellmanFordComplete(11,-1,0);
    System.out.println("\n111 vertices");
    bellmanFordComplete(111,-1,0);
    System.out.println("\n1111 vertices (give this some time)");
    bellmanFordComplete(1111,-1,0);
//    System.out.println("\n11111 vertices (takes too long");
//    bellmanFordComplete(11111,-1,0);

  }

}


