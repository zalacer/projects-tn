package ex43;

import edu.princeton.cs.algs4.StdOut;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;
import graph.KruskalMSTB;
import graph.PrimMSTB;

/* p631
  4.3.5 Show that the greedy algorithm is valid even when edge weights are 
  not distinct.
  
  All the greedy algorithm requires is that edges selected for the MST are 
  non-decreasing (as enforced by use of IndexMinPQ or minPQ). In particular 
  they don't have to be strictly decreasing, but if they aren't then the MST 
  is not certainly unique, however the multiset of weights in it certainly is 
  (proved at https://cs.stackexchange.com/questions/2204/do-the-minimum-spanning-trees-of-a-weighted-graph-have-the-same-number-of-edges).
  
  As a side note Prof. Jeff Erickson of the University of Illinois at Urbana-
  Champaign gives a simple method for consistently breaking ties for edges with 
  the same weights in 
      http://jeffe.cs.illinois.edu/teaching/algorithms/notes/20-mst.pdf
  by defining an Edge comparison function as follows:
    (in EdgeX.class that has fields int u,v; and double w)
    public int compareTo(EdgeX that) {
      if (w != that.w) return Double.compare(w, that.w);
      if (min(u,v) < min(that.v,that.w)) return -1;
      if (min(u,v) > min(that.v,that.w)) return 1;
      if (max(u,v) < max(that.v,that.w)) return -1;
      if (max(u,v) > max(that.v,that.w)) return 1;
      return 0; // this and that are identical
    }
  or an equivalent Comparator(EdgeX) such as EdgeWeightedGraphX.edgCmp
 */  

public class Ex4305ShowGreedyMSTAlgoValidEvenIfEdgeWeightsNotDistinct {
  
  public static void main(String[] args) {
    
    EdgeWeightedGraphX g = new EdgeWeightedGraphX(8);
    g.addEdge(0,1,1); g.addEdge(0,3,1); g.addEdge(1,4,1); g.addEdge(2,3,1);
    g.addEdge(3,4,1); g.addEdge(2,5,1); g.addEdge(3,6,1); g.addEdge(4,7,1);
    g.addEdge(5,6,1); g.addEdge(6,7,1);
    
    System.out.println("KruskalMSTB:");
    KruskalMSTB k = new KruskalMSTB(g);
    for (EdgeX e : k.edges()) System.out.println(e);
    System.out.printf("%.5f\n", k.weight());
    
    System.out.println("\nPrimMSTB:");
    PrimMSTB p = new PrimMSTB(g);
    for (EdgeX e : p.edges()) StdOut.println(e);
    System.out.printf("%.5f\n", p.weight());
    
  }

}
/*
using this as the graph for compatibility with EdgeWeightedGraphX:

      0-1
      | |
    2-3-4
    | | |
    5-6-7    

using PrimMSTB:

  start with 0:
      0-1
      | 
    2-3-4
    | | |
    5 6 7
    
  start with 1:
      0-1
      | |
    2-3 4
    | |
    5 6-7

  start with 2:
      0-1
      | 
    2-3-4
    |  
    5-6-7
    
  start with 3:
      0-1
      | 
    2-3-4
      | 
    5-6-7 
    
  start with 4:
      0-1
        |
    2 3-4
    |   |
    5-6-7  
    
  start with 5:
      0-1
      | 
    2 3-4
    | | 
    5-6-7   
    
  start with 6:
      0 1
      | |
    2-3 4
      | |
    5-6-7  

 start with 7:
      0 1
      | |
    2-3 4
      | |
    5-6-7        

*/

