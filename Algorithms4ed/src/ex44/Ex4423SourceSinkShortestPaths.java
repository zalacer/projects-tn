package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p688
  4.4.23 Source-sink shortest paths. Develop an API and implementation 
  that use a version of Dijkstra’s algorithm to solve the source-sink 
  shortest path problem on edge-weighted digraphs.
  
  (The source-sink shortest path problem is mentioned on p656.)
  
  This is implemented in graph.DijkstraSourceSinkSP that's a modified
  version of graph.DijkstraSPX that's essentially the same as 
  graph.DijkstraSourceSP that's from 
  http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/DijkstraSP.java
  that's essentially the same as ALGORITHM 4.9 Dijkstra’s shortest-paths 
  algorithm from p655 in the text.
  
  Since no API is defined for ALGORITHM 4.9, I didn't define one for 
  DijkstraSourceSinkSP. In the latter is added an additional int field
  named sink to the class and named z in its constructors. Additionally
  DijkstraSourceSinkSP includes specialized no-arg versions of hasPathTo() and 
  pathTo(). However, the main change in DijkstraSourceSinkSP from DijkstraSPX
  is termination of processing when the sink vertex is removed from the PQ
  as suggested in the text on p656. DijkstraSourceSinkSP is demonstrated below.
 
 */  

public class Ex4423SourceSinkShortestPaths {

  public static void main(String[] args) {
    
    // for each vertex in a graph as source compare all shortest paths
    // found with Dijkstra's algo to those found with the source-sink
    // version of the same
    // no output means the results are identical
    
    DijkstraSPX sp; DijkstraSourceSinkSP ssp;   
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
     
    for (int i = 0; i < G.V(); i++) {
      sp = new DijkstraSPX(G, i, "q");
      Seq<Seq<DirectedEdgeX>> sps1 = sp.allPaths(); // all shortest paths in G from i
        
      Seq<Seq<DirectedEdgeX>> sps2 = new Seq<>();
      for (int j = 0; j < G.V(); j++) {
        ssp = new DijkstraSourceSinkSP(G, i, j,"q");
        sps2.add(ssp.seqTo()); // add shortest path in G from i to j
      }
      
      assert sps1.equals(sps2);
    }

  }

}


