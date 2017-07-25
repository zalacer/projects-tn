package ex44;

import edu.princeton.cs.algs4.In;
import graph.BellmanFordSPX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.43 Negative cycle detection. Suppose that we add a constructor to Algorithm 
  4.11 that differs from the constructor given only in that it omits the second 
  argument and that it initializes all  distTo[] entries to 0. Show that, if a 
  client uses that constructor, a client call to hasNegativeCycle() returns true 
  if and only if the graph has a negative cycle (and negativeCycle() returns that 
  cycle).
  
  Answer : Consider a digraph formed from the original by adding a new source with 
  an edge of weight 0 to all the other vertices. After one pass, all  distTo[] 
  entries are 0, and finding a negative cycle reachable from that source is the 
  same as finding a negative cycle anywhere in the original graph.
  
  The suggested Algorithm 4.11 (Bellman-Ford) constructor is demonstrated below.
 
 */  

public class Ex4443NegativeCycleDetection {
  
  public static void hasNegativeCycle(String ewg) {
    if (ewg == null) throw new IllegalArgumentException("ewg is null");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In(ewg));
    BellmanFordSPX sp = new BellmanFordSPX(G);
    if (sp.hasNegativeCycle()) System.out.println("negative cycle: "+sp.str(sp.cycle()));
    else System.out.println("no negative cycle");
  }
  
  public static void main(String[] args) {
    
    hasNegativeCycle("tinyEWD.txt");   // no negative cycle
    hasNegativeCycle("tinyEWDnc.txt"); // negative cycle: 4>5>4

  }

}


