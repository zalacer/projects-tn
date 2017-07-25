package ex43;

import edu.princeton.cs.algs4.StdOut;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;
import graph.EuclidianGraph;
import graph.KruskalMSTB;
import graph.PrimMSTB;

/* p631
  4.3.6 Give the MST of the weighted graph obtained by deleting vertex  
  7 from tinyEWG.txt (see page 604).
    
  tinyEWG.txt contains:
  8
  16
  4 5 0.35
  4 7 0.37
  5 7 0.28
  0 7 0.16
  1 5 0.32
  0 4 0.38
  2 3 0.17
  1 7 0.19
  0 2 0.26
  1 2 0.36
  1 3 0.29
  2 7 0.34
  6 2 0.40
  3 6 0.52
  6 0 0.58
  6 4 0.93
  
  removing references to 7 in it gives:
  7
  11
  4 5 0.35
  1 5 0.32
  0 4 0.38
  2 3 0.17
  0 2 0.26
  1 2 0.36
  1 3 0.29
  6 2 0.40
  3 6 0.52
  6 0 0.58
  6 4 0.93
  
  constructing EdgeWeightedGraphX with the following String
  edges = "4 5 0.35 1 5 0.32 0 4 0.38 2 3 0.17 0 2 0.26 1 2 0.36 1 3 0.29 "
        + "6 2 0.40 3 6 0.52 6 0 0.58 6 4 0.93";
  
  results (from running main()):
  
    KruskalMSTB:
    2-3 0.17000
    0-2 0.26000
    1-3 0.29000
    1-5 0.32000
    4-5 0.35000
    6-2 0.40000
    1.79000
    
    PrimMSTB:
    1-3 0.29000
    0-2 0.26000
    2-3 0.17000
    4-5 0.35000
    1-5 0.32000
    6-2 0.40000
    1.79000
  
 */  

public class Ex4306GiveTheMSTofTheGraphObtainedByDeletingVertex7FromTinyEWG {
  
  public static void main(String[] args) {
    
    String edges = "4 5 0.35 1 5 0.32 0 4 0.38 2 3 0.17 0 2 0.26 1 2 0.36 1 3 0.29 "
        + "6 2 0.40 3 6 0.52 6 0 0.58 6 4 0.93";
    
    EdgeWeightedGraphX g = new EdgeWeightedGraphX(7, edges);

    System.out.println("KruskalMSTB:");
    KruskalMSTB k = new KruskalMSTB(g);
    for (EdgeX e : k.edges()) System.out.println(e);
    System.out.printf("%.5f\n", k.weight());
    
    System.out.println("\nPrimMSTB:");
    PrimMSTB p = new PrimMSTB(g);
    for (EdgeX e : p.edges()) StdOut.println(e);
    System.out.printf("%.5f\n", p.weight());
    
    EuclidianGraph eu = new EuclidianGraph(g.graph());
    eu.show();
    
  }

}
/*
    KruskalMSTB:
    2-3 0.17000
    0-2 0.26000
    1-3 0.29000
    1-5 0.32000
    4-5 0.35000
    6-2 0.40000
    1.79000
    
    PrimMSTB:
    1-3 0.29000
    0-2 0.26000
    2-3 0.17000
    4-5 0.35000
    1-5 0.32000
    6-2 0.40000
    1.79000

*/

