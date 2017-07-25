package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p689 
  4.4.34 Monotonic shortest path. Given a weighted digraph, find a monotonic 
  shortest path from s to every other vertex. A path is monotonic if the 
  weight of every edge on  the path is either strictly increasing or strictly 
  decreasing. The path should be simple (no repeated vertices). Hint : Relax 
  edges in ascending order and find a best path; then relax edges in descending 
  order and find a best path.
  
  This is implemented in graph.DijkstraMonotonicA by relaxing the edges in
  ascending order and graph.DijkstraMonotonicB by doing it in descending order
  but with no difference in the results for tinyEWG, mediumEWG and 1000EWG. For
  comparison, similar results were obtained by filtering the output of the regular
  Dijkstra algo. The results show that using the Dijkstra algo on sorted edges 
  while filtering for monotonicity during relaxation is capable of finding all 
  shortest monotonic paths, however it may also find some monotonic shortest paths 
  (see discussion below on the difference between these types of paths), but far 
  from all of latter based on the results of an all paths DFS method.
 
  Discussion:
  I'm assuming this definition of strictly monotonic path means it has at least
  two edges since otherwise it's tonicity is indeterminate. Paths of exactly two
  edges having unequal weights are always strictly monotonic and if they have
  equal weights they're strictly nonmonotonic.

  Based on the phrasing of this exercise statement and the hint, I suppose it means
  to find the subset of monotonic paths in a graph out of its set of shortest paths
  since that subset may appropriately be called "monotonic shortest paths" in which,
  if "shortest" and "monotonic" are functions, "shortest" acts on "paths" and "mono-
  tonic" acts on "shortest"("paths"). This can be done trivially by filtering the 
  results of the usual Dijkstra SP algo after they're produced, but doing the filter-
  ing while calculating the shortest paths isn't obviously easy or correct.

  "Monotonic shortest paths" could also mean the shortest monotonic paths, but since 
  that wasn't precisely stated and the Dijkstra algorithm doesn't appear to be neces-
  sarily consistently or completely applicable to finding monotonic paths that aren't 
  intrinsically shortest, this doesn't seem to be the intended meaning, yet it could be 
  of interest for some applications and could be done reliably by filtering the results 
  of an all-paths algo, e.g. graph.AllPathsEWG.allPaths(EdgeWeightedDigraphX), for mono-
  tonicity. 
  
  In the graph package classes used in the demonstration below, viz.
    graph.DijkstraSPX
    graph.DijkstraMonotonicA
    graph.DijkstraMonotonicD
    graph.AllPathsEWG 
  monotonic path is operationally defined according to this exercise's statement in 
  isMonoPath(Seq<DirectedEdgeX>).

 */  

public class Ex4434MonotonicShortestPath {
  
  public static void compareSPMonoMethods(String ewg) {
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
    Seq<Seq<Seq<DirectedEdgeX>>> m = graph.DijkstraSPX.allMonoPaths(G);
    Seq<Seq<Seq<DirectedEdgeX>>> a = graph.DijkstraMonotonicA.allMonoPaths(G);
    Seq<Seq<Seq<DirectedEdgeX>>> d = graph.DijkstraMonotonicD.allMonoPaths(G);
    Seq<Seq<DirectedEdgeX>>      l = graph.AllPathsEWG.allLightestMonoPaths(G)._2;
    
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

  public static void main(String[] args) {
    
//    Seq<String> g = new Seq<>("tinyEWD.txt","mediumEWD.txt","1000EWG.txt");
//    for (String graph : g) compareSPMonoMethods(graph);
    
    compareSPMonoMethods("tinyEWD.txt");
    
    /*
    results for tinyEWD.txt:
    =========================================================================================
    Dijkstra with ascending and descending edges give identical results
    
    differences between Dijkstra with ascending edges (<<) and regular Dikjstra filtered (>>)
    << 2>4: (2.18) 2>7  0.34   7>3  0.39   3>6  0.52   6>4  0.93   
    
    differences between Dijkstra with ascending edges (<<) and all paths DFS filtered (>>)
    >> 0>4: (2.44) 0>2  0.26   2>7  0.34   7>3  0.39   3>6  0.52   6>4  0.93   
    >> 4>5: (0.65) 4>7  0.37   7>5  0.28   
    >> 4>7: (0.63) 4>5  0.35   5>7  0.28   
    >> 5>0: (1.77) 5>7  0.28   7>3  0.39   3>6  0.52   6>0  0.58   
    >> 5>4: (2.12) 5>7  0.28   7>3  0.39   3>6  0.52   6>4  0.93   
    >> 5>6: (1.19) 5>7  0.28   7>3  0.39   3>6  0.52   
    >> 5>7: (0.72) 5>4  0.35   4>7  0.37   
    >> 6>1: (1.60) 6>4  0.93   4>5  0.35   5>1  0.32   
    >> 6>2: (0.84) 6>0  0.58   0>2  0.26   
    >> 6>3: (1.89) 6>4  0.93   4>5  0.35   5>1  0.32   1>3  0.29   
    >> 6>4: (0.96) 6>0  0.58   0>4  0.38   
    */

  }

}


