package ex44;

import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.EdgeWeightedDigraphX;

/* p685
  4.4.8  The diameter of a digraph is the length of the maximum-length 
  shortest path connecting two vertices. Write a  DijkstraSP client that 
  finds the diameter of a given EdgeWeightedDigraph that has nonnegative 
  weights.
  
  I put diameter() methods in graph.DijkstraSPX and graph.EdgeWeightedDigraphX. 
  They are demoed below.
 */  

public class Ex4408DiameterOfEdgeWeightedDigraph {
  
  public static double diameter(String graph) {
    In in = new In(graph);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < V; i++) {
      DijkstraSPX sp = new DijkstraSPX(G, i, "quiet");
      double d = sp.diameter();
      if (d > max) max = d;
    }
    return max;  
  }
  
  public static EdgeWeightedDigraphX ewd(String graph) {
    In in = new In(graph);
    return new EdgeWeightedDigraphX(in);
  }
  
  public static double diameter(EdgeWeightedDigraphX ewd) { return ewd.diameter(); }

	public static void main(String[] args) {
	  
	  System.out.println(diameter("tinyEWD.txt"));        //5.0
	  System.out.println(diameter("mediumEWD.txt"));      //16.0
	  
	  System.out.println(diameter(ewd("tinyEWD.txt")));   //5.0
	  System.out.println(diameter(ewd("mediumEWD.txt"))); //16.0

	}

}


