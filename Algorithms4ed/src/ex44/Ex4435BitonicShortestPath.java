package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p689 
  4.4.35 Bitonic shortest path. Given a digraph, find a bitonic shortest path 
  from s to every other vertex (if one exists). A path is bitonic if there is 
  an intermediate vertex v such that the edges on the path from s to  v are 
  strictly increasing and the edges on the path from  v to  t are strictly 
  decreasing. The path should be simple (no repeated vertices).

  This is implemented in graph.DijkstraBitonicA by relaxing the edges in
  ascending order and graph.DijkstraBitonicD by doing it in descending order
  but with no difference in the results for tinyEWG (mediumEWG and 1000EWG were
  not tested). For comparison, similar results were obtained by filtering the 
  output of the regular Dijkstra algo. The results show that using the Dijkstra 
  algo on sorted edges while filtering for bitonicity during relaxation is not
  generally capable of finding all shortest bitonic paths, and it may also find 
  some bitonic shortest paths (see discussion in the previous exercise on the 
  difference between these types of paths), but far from all of latter based on 
  the results of an all paths DFS method.
  
  In the graph package classes used in the demonstration below, viz.
    graph.DijkstraSPX
    graph.DijkstraBitonicA
    graph.DijkstraBitonicD
    graph.AllPathsEWG 
  bitonic path is operationally defined according to this exercise's statement in 
  isBitonicPath(Seq<DirectedEdgeX>).

 */  

public class Ex4435BitonicShortestPath {

  public static void compareSPBitonicMethods(String ewg) {
    In in = new In(ewg);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    System.out.println("results for "+ewg+":");
    System.out.println("=========================================================================================");
    /*
    graph.DijkstraSPX.allDijkstraSPMono() uses a conventional DijkstraSP algo to 
      find shortest paths for all distinct pairs of vertices and filters them for
      for monotonicity after all relaxations

    graph.DijkstraMonotonicA.allMonoPaths() uses a DijkstraSP algo in which the
      outgoing edges from each vertex are first sorted by increasing weight and
      relax filters for monotonicity if shortest path testing passes

    graph.DijkstraMonotonicD.allMonoPaths() uses a DijkstraSP algo in which the
      outgoing edges from each vertex are first sorted by decreasing weight and
      relax filters for monotonicity if shortest path testing passes

    graph.AllPathsEWG.allLightestMonoPaths() calculates all paths between all
      distinct pairs of vertices and then filters the paths for each such pair
      first for monotonicity, then for least weight, then for least size if 
      multiple paths have the same least weight and in that case prints a message
      containing all such paths
     */ 
    Seq<Seq<Seq<DirectedEdgeX>>> m = graph.DijkstraSPX.allBitonicPaths(G);
    Seq<Seq<Seq<DirectedEdgeX>>> a = graph.DijkstraBitonicA.allBitonicPaths(G);
    Seq<Seq<Seq<DirectedEdgeX>>> d = graph.DijkstraBitonicD.allBitonicPaths(G);
    Seq<Seq<DirectedEdgeX>>      l = graph.AllPathsEWG.allLightestBitonicPaths(G)._2;

    // comparisons and printing are done excluding empty placeholder subSeqs

    if (a.equals(d)) System.out.println("Dijkstra with ascending and descending edges give identical results\n");
    else {
      System.out.println("differences between Dijkstra with ascending edges (<<) and Dijkstra with decending edges (>>)");
      printDiffs(a,d);
    }

    if (a.equals(m)) System.out.println("Dijkstra with ascending edges gives identical results as regular Dijkstra filtered\n");
    else {
      System.out.println("differences between Dijkstra with ascending edges (<<) and regular Dikjstra filtered (>>)");
      printDiffs(a,m);
    }

    if (!a.equals(d)) {
      if (d.equals(m)) System.out.println("Dijkstra with descending edges gives identical results as regular Dijkstra filtered\n");
      else {
        System.out.println("differences between Dijkstra with descending edges (<<) and regular Dikjstra filtered (>>)");
        printDiffs(d,m);
      }
    }

    if (a.flatten().equals(l)) System.out.println("Dijkstra with ascending edges gives identical results as all paths DFS filtered\n");
    else {
      System.out.println("differences between Dijkstra with ascending edges (<<) and all paths DFS filtered (>>)");
      printDiffs2(a,l);
    }

    if (!a.equals(d)) {
      if (d.flatten().equals(l)) System.out.println("Dijkstra with decending edges gives identical results as all paths DFS filtered\n");
      else {
        System.out.println("differences between Dijkstra with ascending edges (<<) and all paths DFS filtered (>>)");
        printDiffs2(d,l);
      }
    }  
  }

  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  public static String str(Seq<DirectedEdgeX> p) {
    StringBuilder sb = new StringBuilder();
    for (DirectedEdgeX e : p) sb.append(e + "   ");
    return sb.toString();
  }

  public static void printDiffs(Seq<Seq<Seq<DirectedEdgeX>>> s1, 
      Seq<Seq<Seq<DirectedEdgeX>>> s2) {
    Seq<Seq<DirectedEdgeX>> f1 = s1.clone().flatten();
    Seq<Seq<DirectedEdgeX>> f2 = s2.clone().flatten();
    Seq<Seq<DirectedEdgeX>> f1c = f1.clone();
    Seq<Seq<DirectedEdgeX>> f2c = f2.clone();    
    f1c.removeAll(f2);
    f1c.removeIf(x->x.isEmpty());
    f2c.removeAll(f1);
    f2c.removeIf(x->x.isEmpty());
    if (!f1c.isEmpty())  {
      for (Seq<DirectedEdgeX> p : f1c)
        System.out.println("<< "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
    }
    if (!f2c.isEmpty()) {
      for (Seq<DirectedEdgeX> p : f2c)
        System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
    }
    System.out.println();
  }

  public static void printDiffs2(Seq<Seq<Seq<DirectedEdgeX>>> s1, 
      Seq<Seq<DirectedEdgeX>> s2) {
    Seq<Seq<DirectedEdgeX>> f1 = s1.clone().flatten();
    Seq<Seq<DirectedEdgeX>> f2 = s2.clone();
    Seq<Seq<DirectedEdgeX>> f1c = f1.clone();
    Seq<Seq<DirectedEdgeX>> f2c = f2.clone();
    f1c.removeAll(f2);
    f1c.removeIf(x->x.isEmpty());
    f2c.removeAll(f1);
    f2c.removeIf(x->x.isEmpty());
    if (!f1c.isEmpty())  {
      for (Seq<DirectedEdgeX> p : f1c)
        System.out.println("<< "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
    }
    if (!f2c.isEmpty()) {
      for (Seq<DirectedEdgeX> p : f2c)
        System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
    }
    System.out.println();
  }

  public static void print(Seq<Seq<Seq<DirectedEdgeX>>> s) {
    for (Seq<Seq<DirectedEdgeX>> r : s)
      for (Seq<DirectedEdgeX> p : r) {
        if (p.isEmpty()) continue;
        System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
      }
  }
  
  public static void print2(Seq<Seq<DirectedEdgeX>> s) {
      for (Seq<DirectedEdgeX> p : s) {
        if (p.isEmpty()) continue;
        System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
            +"("+String.format("%3.2f",weight(p))+") "+str(p)); 
      }
  }

  public static void main(String[] args) {

    //  Seq<String> g = new Seq<>("tinyEWD.txt","mediumEWD.txt","1000EWG.txt");
    //  for (String graph : g) compareSPBitonicMethods(graph);

    compareSPBitonicMethods("tinyEWD.txt");

    /*
    results for tinyEWD.txt:
    =========================================================================================
    Dijkstra with ascending and descending edges give identical results
    
    differences between Dijkstra with ascending edges (<<) and regular Dikjstra filtered (>>)
    << 3>1: (2.12) 3>6  0.52   6>4  0.93   4>5  0.35   5>1  0.32   
    << 3>5: (1.80) 3>6  0.52   6>4  0.93   4>5  0.35   
    >> 4>2: (1.68) 4>7  0.37   7>3  0.39   3>6  0.52   6>2  0.40   
    
    differences between Dijkstra with ascending edges (<<) and all paths DFS filtered (>>)
    >> 0>1: (3.11) 0>2  0.26   2>7  0.34   7>3  0.39   3>6  0.52   6>4  0.93   4>5  0.35   5>1  0.32   
    >> 0>5: (0.88) 0>2  0.26   2>7  0.34   7>5  0.28   
    >> 1>4: (1.77) 1>3  0.29   3>6  0.52   6>0  0.58   0>4  0.38   
    >> 2>1: (2.85) 2>7  0.34   7>3  0.39   3>6  0.52   6>4  0.93   4>5  0.35   5>1  0.32   
    >> 2>4: (2.21) 2>7  0.34   7>3  0.39   3>6  0.52   6>0  0.58   0>4  0.38   
    >> 2>5: (2.53) 2>7  0.34   7>3  0.39   3>6  0.52   6>4  0.93   4>5  0.35   
    >> 3>2: (1.36) 3>6  0.52   6>0  0.58   0>2  0.26   
    >> 3>4: (1.48) 3>6  0.52   6>0  0.58   0>4  0.38   
    >> 3>7: (1.82) 3>6  0.52   6>4  0.93   4>7  0.37   
    >> 4>2: (1.68) 4>7  0.37   7>3  0.39   3>6  0.52   6>2  0.40   
    >> 5>2: (1.59) 5>7  0.28   7>3  0.39   3>6  0.52   6>2  0.40   
    >> 5>4: (2.15) 5>7  0.28   7>3  0.39   3>6  0.52   6>0  0.58   0>4  0.38   
    >> 7>1: (2.51) 7>3  0.39   3>6  0.52   6>4  0.93   4>5  0.35   5>1  0.32   
    >> 7>3: (0.89) 7>5  0.28   5>1  0.32   1>3  0.29   
    >> 7>4: (1.87) 7>3  0.39   3>6  0.52   6>0  0.58   0>4  0.38   
    >> 7>5: (2.19) 7>3  0.39   3>6  0.52   6>4  0.93   4>5  0.35   
    */

  }

}


