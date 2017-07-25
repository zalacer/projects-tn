package ex44;

/* p688
  4.4.27 Shortest paths in Euclidean graphs. Adapt our APIs to speed up Dijkstra’s 
  algorithm in the case where it is known that vertices are points in the plane.
  
  Based on information from the 3rd edition of Algorithms in Java, Part 5, Chapter
  21.5 on Euclidian Networks*, source-sink shortest paths using Dijkstra’s algo on
  EuclidianEdgeWeightedDigraph's with edge weights given by Euclidian distances 
  between vertices is implemented in graph.DijkstraSourceSinkSPE with reliance on
  a constructor in graph.EuclidianEdgeWeightedDigraph that creates the required input
  digraph by overriding existing edge weights using distances calculated from vertex
  coordinates. 
  
  Modifications in DijkstraSourceSinkSPE compared to graph.DijkstraSourceSinkSP are:
  1: the priority (weight) of the source is initialized to its Euclidian distance from 
     the sink instead of 0. i.e. distTo[source] = euclidianDistance(source,sink);
  2. the priority of an edge e in relax() is given by its priority in DijkstraSourceSinkSPE
     plus euclidianDistance(e.to(),sink) - euclidianDistance(e.from(),sink).
  3. addition of the public double euclidianDistance(int u, int v) method (that is also
     added to EuclidianEdgeWeightedDigraph).  
     
  DijkstraSourceSinkSPE is demonstrated below.  Evidence that it's more efficient than
  the regular Dijkstra source-sink algo are edgeTo[] entries excluding that for the source
  remaining null and distTo[] entries with Infinity values remaining after processing.
  
  * the full reference is:
    Algorithms in Java, 3ed; Part 5: Graph Algorithms; Robert Sedgewick; Addison Wesley; 
    2003; ISBN: 0-201-36121-3.
    Chapter 25 of this reference is available online at
      http://www.informit.com/articles/article.aspx?p=169575&seqNum=6

 */  

public class Ex4427ShortestPathsInEuclidianGraphs {

  public static void main(String[] args) {
    
    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    // this determines the source-sink path from 0->5 in tinyEWD after resetting all
    // its edge weights to the Euclidian distances between vertices in each edge
    graph.DijkstraSourceSinkSPE.sourceSink("tinyEWD.txt", coordAr, 0, 5);
/*
    shortest path from 0 to 5: (48.17) 0>4 24.67  4>5 23.50  
    
    vertex  edgeTo[]      distTo[]
    0       null          28.347133893923033
    1       null          Infinity
    2       0>2 13.76     51.930236564284414
    3       null          Infinity
    4       0>4 24.67     48.168005532082304
    5       4>5 23.50     48.168005532082304
    6       null          Infinity
    7       4>7 21.57     63.827501385087174
*/ 
  }

}


