package ex43;

import graph.PrimMSTBXV2;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;


/* p634
  4.3.29 Dense graphs. Develop an implementation of Prim’s algorithm that 
  uses an eager approach (but not a priority queue) and computes the MST 
  using V^2 edge-weight comparisons.
  
  Hint from http://algs4.cs.princeton.edu/43mst/ exercise 19:
  Suppose that you implement an eager version of Prim's algorithm but instead 
  of using a priority queue to find the next vertex to add to the tree, you 
  scan through all V entries in the distTo[] array to find the non-tree vertex 
  with the smallest value. What would be the order of growth of the worst-case 
  running time for the eager version of Prim's algorithm on graphs with V ver-
  tices and E edges? When would this method be appropriate, if ever? Defend your 
  answer. Solution. Prim's algorithm would run in time proportional to V^2, that
  is optimal for dense graphs.
  
  This method is implemented in graph.PrimMSTBXV2 and demonstrated below.
  
 */  

public class Ex4329EagerPrimAlgo4DenseGraphsUsingVsquaredEdgeWeightComparisons {

  public static void main(String[] args) {

    // V and edges from original tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    PrimMSTBXV2 p = new PrimMSTBXV2(G);
    for (EdgeX e : p.edges()) System.out.println(e);
    System.out.printf("%.5f\n", p.weight());
    System.out.println();
    EdgeWeightedGraphX mst = p.mst();
    System.out.println("mst.V() = "+mst.V());
    System.out.println("mst.E() = "+mst.E());
/*
    (1-7,0.19000)
    (0-2,0.26000)
    (2-3,0.17000)
    (4-5,0.35000)
    (5-7,0.28000)
    (6-2,0.40000)
    (0-7,0.16000)
    1.81000
    
    mst.V() = 8
    mst.E() = 7
    
*/
  }

}


