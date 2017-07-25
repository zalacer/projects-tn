package ex44;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p688
  4.4.25 Shortest path between two subsets. Given a digraph with positive 
  edge weights, and two distinguished subsets of vertices S and T, find a 
  shortest path from any vertex in S to any vertex in T. Your algorithm 
  should run in time proportional to E log V, in the worst case.
  
  This is implemented in and demonstrated with minPath() below that extends
  the method used in the solution of the previous exercise by adding an edge 
  with weight 0 from each vertex in T to a dummy vertex t, as well as adding
  edges with weight 0 from another dummy vertex s to each vertex in S, then
  using graph.DijkstraSourceSinkSP to find the shortest path between s and t
  and returning it after removing all edges containing a dummy vertex.
  
 */  

public class Ex4425ShortestPathBetween2Subsets {
  
  public static Seq<DirectedEdgeX> minPath(EdgeWeightedDigraphX G, Set<Integer> sources,
      Set<Integer> targets) {
    // return a shortest path between sources and targets as a Seq<DirectedEdgeX>
    if (G == null) throw new IllegalArgumentException("minPath: G is null");
    if (G.V() == 0) throw new IllegalArgumentException("minPath: G has no vertices");
    if (sources == null) throw new IllegalArgumentException("minPath: sources is null");
    if (sources.isEmpty()) throw new IllegalArgumentException("minPath: sources is empty");
    if (targets == null) throw new IllegalArgumentException("minPath: targets is null");
    if (targets.isEmpty()) throw new IllegalArgumentException("minPath: targets is empty");
    int V = G.V();
    boolean valid = false;
    for (int i : sources) if (-1 < i && i < V) { valid = true; break; }
    if (!valid) throw new IllegalArgumentException("minPath: no valid sources");
    valid = false;
    for (int i : targets) if (-1 < i && i < V) { valid = true; break; }
    if (!valid) throw new IllegalArgumentException("minPath: no valid targets");
    G = new EdgeWeightedDigraphX(G); // preserve the original G
    G.addVertex(2); // add 2 vertices to G, V and V+1
    for (int i : sources)  G.addEdge(V, i, 0);
    for (int i : targets)  G.addEdge(i, V+1, 0);
    DijkstraSourceSinkSP ssp = new DijkstraSourceSinkSP(G, V, V+1,"q");
    Seq<DirectedEdgeX> r = ssp.seqTo();
    return 1 < r.size() ? r.init().tail() : r;
  }
  
  public static void main(String[] args) {
    
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    Set<Integer> sources = new HashSet<>(Arrays.asList(1,3,5));
    Set<Integer> targets = new HashSet<>(Arrays.asList(2,4,7));

    System.out.println(minPath(G,sources,targets));
    // (5>7  0.28)

/*
 
*/ 
  }

}


