package ex44;

import edu.princeton.cs.algs4.In;
import graph.AdjMatrixEdgeWeightedDigraphX;
import graph.DijkstraSPX;
import graph.DijkstraSPXV2;
import graph.DijkstraSPXV2AM;
import graph.EdgeWeightedDigraphX;

/* p688
  4.4.26 Single-source shortest paths in dense graphs. Develop a version of Dijkstra’s
  algorithm that can find the SPT from a given vertex in a dense edge-weighted digraph 
  in time proportional to V^2 . Use an adjacency-matrix representation (see Exercise 
  4.4.3 and Exercise 4.3.29).
  
  Step 1:
  Taking the second hint, a O(V^2) solution is implemented without using a priority queue
  or adjacenty-matrix as graph.DijkstraSPXV2 obtained by converting the solution to Exer-
  cise 4.3.29, graph.PrimMSTBXV2, to use EdgeWeightedDigraphXs and weight-priorities based 
  on distance from the source instead of distance to the tree, renaming visit() to relax 
  and replacing Prim supporting methods with Dijkstra supporting methods. DijkstraSPXV2 is 
  demonstrated below.
  
  Step2:
  Implemented a solution using an adjacency-matrix representation as graph.DijkstraSPXV2AM
  by replacing EdgeWeightedDigraphX in DijkstraSPXV2 with AdjMatrixEdgeWeightedDigraphX.
  DijkstraSPXV2AM is demonstrated below.
  
 */  

public class Ex4426SingleSourceShortestPathsInDenseGraphs {

  public static void main(String[] args) {
    
    // no output means it works
    
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    AdjMatrixEdgeWeightedDigraphX A = new AdjMatrixEdgeWeightedDigraphX(G);
    DijkstraSPX sp; DijkstraSPXV2 v2; DijkstraSPXV2AM am;
    for (int i = 0; i < G.V(); i++) {
      sp = new DijkstraSPX(G,i,"q");
      v2 = new DijkstraSPXV2(G,i);
      am = new DijkstraSPXV2AM(A,i);
      assert sp.allPaths().equals(v2.allPaths());
      assert v2.allPaths().equals(am.allPaths());
    }

  }

}


