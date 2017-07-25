package ex43;

import edu.princeton.cs.algs4.StdOut;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;
import graph.KruskalMSTB;
import graph.PrimMSTB;

/* p631
  4.3.2  Draw all of the MSTs of the graph depicted below 
  (all edge weights are equal).
  
  the graph:
                1-2   (10 edges)
                | |
              3-4-5
              | | |
              6-7-8
  
  The MSTs are all non-cyclic subgraphs with 7 edges.
    
  These are the ones found by graph.PrimMSTB by starting at each distinct vertex
  after decrementing vertices by 1, processing the new graph, and incrementing 
  the vertices in the result by 1. This shows that neither graph.PrimMSTB nor 
  graph.KruskalMSTB always find all MSTs in the graph. In fact they would do that
  for sure only if the MST is unique such as when each edge has a distinct weight.
  
 a.   1-2
      | 
    3-4-5
    | | |
    6 7 8
    
 b.   1-2
      | |
    3-4 5
    | |
    6 7-8

 c.   1-2
      | 
    3-4-5
    |  
    6-7-8
    
 d.   1-2
      | 
    3-4-5
      | 
    6-7-8 
    
 e.   1-2
        |
    3 4-5
    |   |
    6-7-8  
    
 f.   1-2
      | 
    3 4-5
    | | 
    6-7-8   
    
 g.   1 2
      | |
    3-4 5
      | |
    6-7-8  
  
 
 This is the one found by graph.KruskalMSTB starting at vertex 0
 
 b.   1-2   (same as b above)
      | |
    3-4 5    
    | |
    6 7-8    


 Another one not given above is:
  
 h.   1-2
        |
    3-4-5
        |
    6-7-8    

 There are a few more...

 */  

public class Ex4302DrawAllMSTsInTheDepictedGraph {
  
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

