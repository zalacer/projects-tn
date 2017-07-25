package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.EdgeWeightedDigraphX;

/* p688
  4.4.30 All-pairs shortest path in graphs without negative cycles. Articulate an  
  APIlike the one implemented on page 656 for the all-pairs shortest-paths problem 
  in graphs with no negative cycles. Develop an implementation that runs a version 
  of Bellman-Ford to identify weights pi[v] such that for any edge v>w, the edge 
  weight plus the difference between pi[v] and pi[w] is nonnegative. Then use these  
  weights to reweight the graph, so that Dijkstra’s algorithm is effective for find- 
  ing allshortest paths in the reweighted graph.
  
  This is implemented as graph.BellmanFordSPX.allPairsJohnson() that's demonstrated 
  below in comparison with graph.DijkstraSPX.allPairsDijkstra() that uses only 
  Dijkstra's algo.
     
 */  

public class Ex4430AllPairsShortestPathsInGraphsWithOutNegativeCycles {

  public static void main(String[] args) {
    
    EdgeWeightedDigraphX G; Seq<Seq<Seq<Integer>>> j = null, d = null;
    
    // case 1: graph with all positive edge weights
    // allPairsJohnson and allPairsDijkstra both work
    G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    j = graph.BellmanFordSPX.allPairsJohnson(G);
    d = graph.DijkstraSPX.allPairsDijkstra(G);
    assert j.equals(d);
    
    // case2: graph with some negative edge weights but no negative cycles
    // allPairsJohnson works, allPairsDijkstra fails on negative edge weight
    G = new EdgeWeightedDigraphX(new In("tinyEWDn.txt"));
    j = graph.BellmanFordSPX.allPairsJohnson(G);
    d = graph.DijkstraSPX.allPairsDijkstra(G); // throws exception
    assert j.equals(d);

    
  }

}


