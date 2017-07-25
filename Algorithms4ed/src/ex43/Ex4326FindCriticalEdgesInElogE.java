package ex43;

import graph.KruskalCriticalEdges;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;


/* p633
  4.3.26 Critical edges. An MST edge whose deletion from the graph would cause 
  the MST weight to increase is called a critical edge. Show how to find all 
  critical edges in a graph in time proportional to E log E. Note : This question 
  assumes that edge weights are not necessarily distinct (otherwise all edges in 
  the MST are critical).

  Run Kruskal's algorithm. When it comes to an already connected edge, if the
  cycle that would be created by adding it to the MST contains another edge with
  the same weight then neither are critical. The critical edges are those in the
  MST that weren't identified as non-critical.
  
  This method is implemented for proof of concept in graph.KruskalCriticalEdges 
  that's demonstrated below.
 */  

public class Ex4326FindCriticalEdgesInElogE {

  public static void main(String[] args) {

    System.out.println("1: Using original tinyEWG.txt:");
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    KruskalCriticalEdges mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());

    System.out.println("\n2: Same as (1) after adjusting weight of 1-3 to 0.26:");
    edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.26 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    G = new EdgeWeightedGraphX(V,edges);
    mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());
    
    System.out.println("\n3: Same as (2) after removing noncrit edge 1-3:");
    edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    G = new EdgeWeightedGraphX(V,edges);
    mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());
    
    System.out.println("\n4: Same as (2) after removing noncrit edge 0-2:");
    edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  1 2 0.36  1 3 0.26 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    G = new EdgeWeightedGraphX(V,edges);
    mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());

    System.out.println("\n5: Same as (4) after removing crit edge edge 0-7:");
    edges = "4 5 0.35  4 7 0.37  5 7 0.28  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  1 2 0.36  1 3 0.26 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    G = new EdgeWeightedGraphX(V,edges);
    mst = new KruskalCriticalEdges(G);
    for (EdgeX e : mst.edges()) System.out.println(e);
    System.out.println("weight = "+mst.weight());
    System.out.println("crit = "+mst.crit());
    System.out.println("noncrit = "+mst.noncrit());

  }

}
