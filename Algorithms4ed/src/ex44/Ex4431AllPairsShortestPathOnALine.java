package ex44;

import static v.ArrayUtils.*;

import java.util.Iterator;

import graph.EdgeWeightedGraphGeneratorX;
import graph.EdgeWeightedGraphX;
import graph.EulerianPathEWG;

/* p689 
  4.4.31 All-pairs shortest path on a line. Given a weighted line graph (undirected 
  connected graph, all vertices of degree 2, except two endpoints which have degree 
  1), devise an algorithm that preprocesses the graph in linear time and can return 
  the distance of the shortest path between any two vertices in constant time.
  
  An algo for this is implemented and demonstrated below.

 */  

public class Ex4431AllPairsShortestPathOnALine {

  public static void distances(EdgeWeightedGraphX G) {
    // print the distances between all pairs of distinct vertices in G if it's
    // a non-null simple trail
    if (G == null) throw new IllegalArgumentException("graph is null");
    if (!G.isSimpleTrail()) throw new IllegalArgumentException("graph isn't a simple trail");
    int[][] a = (new EulerianPathEWG(G)).trailAndInvertedIndex();
    System.out.println("simpleTrail = "+arrayToString(a[0],900,1));
    System.out.println("invertedIndex = "+arrayToString(a[1],900,1));
    System.out.println("vertex1  vertex2  distance");
    Iterator<int[]> pairs = combinations(range(0,G.V()),2);
    while (pairs.hasNext()) {
      int[] n = pairs.next();
      System.out.printf("%-7d  %-7d  %s\n", n[0], n[1], Math.abs(a[1][n[0]]-a[1][n[1]]));
    }
  }


  public static void main(String[] args) {

    distances(EdgeWeightedGraphGeneratorX.path(31));

  }

}


