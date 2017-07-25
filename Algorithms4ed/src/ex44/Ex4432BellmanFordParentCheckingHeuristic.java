package ex44;

import static v.ArrayUtils.*;

import java.util.Iterator;

import ds.Queue;
import edu.princeton.cs.algs4.In;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;
import graph.BellmanFordSPCGR;
import graph.BellmanFordSPX;

/* p689 
  4.4.32 Parent-checking heuristic. Modify Bellman-Ford to visit a vertex v 
  only if its SPT parent edgeTo[v] is not currently on the queue. This heur-
  istic has been reported by Cherkassky, Goldberg, and Radzik to be useful in 
  practice. Prove that it correctly computes shortest paths and that the worst-
  case running time is proportional to EV.
  
  In Bellman-FordSP edgeTo[] is a DirectedEdgeX[] while the queue is a Queue<Integer>
  so an element of edgeTo[] can't be in the queue. What Cherkassky, Goldberg, and 
  Radzik's "parent-checking heuristic" does is to relax or "scan" a vertex only if
  it's parent vertex isn't in the queue. Use of the word "parent" refers to rooted
  trees and in such "the parent of a vertex is the vertex connected to it on the path 
  to (from) the root" (i.e. it's one step closer to the root or source than the vertex).
    https://en.wikipedia.org/wiki/Tree_%28graph_theory%29#Rooted_tree
    https://en.wikipedia.org/wiki/Tree_structure#Terminology_and_properties
  Therefore when using the BellmanFordSP reprepresentation, this heuristic applies only 
  if edgeTo[v] is non-null, since only then are v and its parent, edgeTo[v].from(), in 
  the SPT, and edgeTo[v].from() is in the queue when relax(G,v) is called.
  
  Since if a parent is relaxed and its weight is decreased all its children need to
  be subsequently relaxed. Therefore there's no point to relaxing a child if its parent
  is queued up for relaxation and avoiding that can avoid reprocessing of vertices at
  the expense of determining if a vertex is in the queue. Therefore the parent-checking 
  heuristic correctly determines the SPT because it essentially does the Bellman-Ford 
  algo and has the same E*V upper bound but may require fewer relaxations and take less
  time.
  
  The Bellman-Ford algo with Cherkassky, Goldberg, and Radzik's parent-checking heuristic
  is implemented in graph.BellmanFordSPCGR and demonstrated below.
  
  The paper in which this heuristic was introduced is:
    Boris V. Cherkassky, Andrew V. Goldberg, and Tomasz Radzik. Shortest paths 
    algorithms: Theory and experimental evaluation. Mathematical Programming, 
    73(2):129–174, 1996, pages 5-6.
    
  It's available for download in PDF format from
    https://www.semanticscholar.org/paper/Shortest-paths-algorithms-Theory-and-experimental-Cherkassky-Goldberg/41dcedd4a23cffc707e6ae873c75829351113b9f
  
*/  

@SuppressWarnings("unused")
public class Ex4432BellmanFordParentCheckingHeuristic {

  public static void main(String[] args) {
    
    In in = new In("1000EWD.txt");
    int s = 0;
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    EdgeWeightedDigraphX H = new EdgeWeightedDigraphX(G);
    
    BellmanFordSPX sp = new BellmanFordSPX(G, s);
    //sp.relaxation is number of invocations of BellmanFordSPX.relax()
    System.out.println("BellmanFordSPX.relaxations = "+sp.relaxations()); 
    System.out.println("\nBellmanFordSPCGR invocations of parent-checking heuristic:");
    
    BellmanFordSPCGR spcgr = new BellmanFordSPCGR(H, s);
    //spcgr.relaxation is number of invocations of BellmanFordSPCGR.relax()
    System.out.println("\nBellmanFordSPCGR.relaxations = "+spcgr.relaxations());
/*
    BellmanFordSPX.relaxations = 1027
    
    BellmanFordSPCGR invocations of parent-checking heuristic:
    parent-checking heuristic on 363
    parent-checking heuristic on 994
    parent-checking heuristic on 33
    parent-checking heuristic on 64
    parent-checking heuristic on 837
    parent-checking heuristic on 104
    parent-checking heuristic on 421
    
    BellmanFordSPCGR.relaxations = 1017
    
*/
  }

}


