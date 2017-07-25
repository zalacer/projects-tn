package ex43;

import static graph.EdgeWeightedGraphX.testFixMstAfterEdgeAddition;

import graph.EdgeWeightedGraphX;

/* p632
   4.3.15 Given an MST for an edge-weighted graph G and a new edge e, describe 
   how to find an MST of the new graph in time proportional to V.
   
   Add the new edge to the MST of G then remove the heaviest edge in the cycle 
   created by that.
   
   This is implemented with 
     graph.EdgeWeightedGraphX.fixMstAfterEdgeAddition(EdgeWeightedGraphX ewg,
       EdgeWeightedGraphX mst, EdgeX edgeAdded)
   and tested below for addition of each unused edge with the lightest weight in a 
   graph using EdgeWeightedGraph.testFixMstAfterEdgeAddition(EdgeWeightedGraphX g).
   
   This implementation uses DFS to find the cycle in the MST with the added edge
   taking time and space proportional to V+E (according to Proposition C on p546),
   however, since the MST is a tree with an added edge, V == E and V+E == 2V so
   time is proportional to V.
     
 */  

public class Ex4315FindNewMSTofEdgeWeightedGraphAfterAddingAnEdgeInTimeProportionalToV {
  
  public static void main(String[] args) {

    // V and edges from http://algs4.cs.princeton.edu/43mst/tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    testFixMstAfterEdgeAddition(new EdgeWeightedGraphX(V,edges));
    //fixMstAfterEdgeAddition() worked after inserting each of 12 unused edges

  
  }

}


