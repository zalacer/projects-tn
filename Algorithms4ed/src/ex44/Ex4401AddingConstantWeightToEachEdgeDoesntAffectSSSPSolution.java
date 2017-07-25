package ex44;

import static graph.DirectedEdgeX.convertEdges;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;
import graph.NonWeightedDirectedEdgeX;

/* p685
  4.4.1  True or false: Adding a constant to every edge weight does not change the 
  solution to the single-source shortest-paths problem.
  
  False. Adding a constant negative weight to all edges can change the solution even 
  if all resulting edge weights are positive. This is demonstrated below.
 
 */  

public class Ex4401AddingConstantWeightToEachEdgeDoesntAffectSSSPSolution {
  
  public static Seq<Seq<DirectedEdgeX>> testAdditionOfConstantWeight(
      EdgeWeightedDigraphX G, int  source, double offset) {
    // offset is added to the weight of each edge in G before determining SSSPs
    int s = source;
    DijkstraSPX sp = new DijkstraSPX(G, s, offset);
    return sp.allPaths();
  }
  
  public static void main(String[] args) {
    
    System.out.println("test adusting weights downwards with all positive resulting weights");
    System.out.println("  shows SSSP changes for target vertices 1 and 5 from source 0");

    // baseline using edges with no weight offsets
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    double offset = 0;
    
    Seq<Seq<DirectedEdgeX>> sssp1 = testAdditionOfConstantWeight(G,0,offset);
    // convert the DirectedEdgeXs in sssp1 to NonWeightedDirectedEdgeXs in ssspnw1
    Seq<Seq<NonWeightedDirectedEdgeX>> ssspnw1 = new Seq<>();
    for (Seq<DirectedEdgeX> s : sssp1) ssspnw1.add(convertEdges(s));
    
    // now using a constant weight offset such that weights are decreased but positive
    G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    offset = -G.minWeight()+.01;

    Seq<Seq<DirectedEdgeX>> sssp2 = testAdditionOfConstantWeight(G,0,offset);
    // convert the DirectedEdgeXs in sssp2 to NonWeightedDirectedEdgeXs in ssspnw2
    Seq<Seq<NonWeightedDirectedEdgeX>> ssspnw2 = new Seq<>();
    for (Seq<DirectedEdgeX> s : sssp2) ssspnw2.add(convertEdges(s));
    
    if (ssspnw1.equals(ssspnw2)) {
      System.out.println("\nssspnw1.equals(ssspnw2)");
    } else  System.out.println("\n!ssspnw1.equals(ssspnw2)");
    
    System.out.println("\nsingle source paths from 0 with unaltered weights"
        + "\n  (using NonWeightedDirectedEdgeXs)");
    for (Seq<NonWeightedDirectedEdgeX> s : ssspnw1) System.out.println(s.toString2()); 
    
    System.out.println("\nsingle source paths from 0 with weights altered by "+offset
        +"\n  (using NonWeightedDirectedEdgeXs)");
    for (Seq<NonWeightedDirectedEdgeX> s : ssspnw2) System.out.println(s.toString2()); 
    
//    System.out.println("\nsingle source paths from 0 with unaltered weights");
//    for (Seq<DirectedEdgeX> s : sssp1) System.out.println(s.toString2()); 
//    
//    System.out.println("\nsingle source paths from 0 with weights altered by "+offset);
//    for (Seq<DirectedEdgeX> s : sssp2) System.out.println(s.toString2()); 
  
    System.out.println("\ntest adusting weights upwards by 5");
    System.out.println("  shows SSSPs remain unaltered from source 0");
    
    G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    offset = 5;
    
    Seq<Seq<DirectedEdgeX>> sssp3 = testAdditionOfConstantWeight(G,0,offset);
    // convert the DirectedEdgeXs in sssp3 to NonWeightedDirectedEdgeXs in ssspnw3
    Seq<Seq<NonWeightedDirectedEdgeX>> ssspnw3 = new Seq<>();
    for (Seq<DirectedEdgeX> s : sssp3) ssspnw3.add(convertEdges(s));
    
    if (ssspnw1.equals(ssspnw3)) {
      System.out.println("\nssspnw1.equals(ssspnw3)");
    } else  System.out.println("\n!ssspnw1.equals(ssspnw3)");

    System.out.println("\nsingle source paths from 0 with weights altered by "+offset
        +"\n  (using NonWeightedDirectedEdgeXs)");
    for (Seq<NonWeightedDirectedEdgeX> s : ssspnw3) System.out.println(s.toString2());
     
//    System.out.println("\nsingle source paths from 0 with weights altered by "+offset);
//    for (Seq<DirectedEdgeX> s : sssp3) System.out.println(s.toString2()); 
    
    System.out.println("\ntest adusting weights upwards maximally by Double.MAX_VALUE/4");
    System.out.println("  shows SSSPs remain unaltered from source 0");
    
    G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    offset = Double.MAX_VALUE/4; // dividing by 4 since that's the length of the 
                                   // longest path with zero offset - beyond this
                                   // results in double overflow
    
    Seq<Seq<DirectedEdgeX>> sssp4 = testAdditionOfConstantWeight(G,0,offset);
    // convert the DirectedEdgeXs in sssp4 to NonWeightedDirectedEdgeXs in ssspnw4
    Seq<Seq<NonWeightedDirectedEdgeX>> ssspnw4 = new Seq<>();
    for (Seq<DirectedEdgeX> s : sssp4) ssspnw4.add(convertEdges(s));
    
    if (ssspnw1.equals(ssspnw4)) {
      System.out.println("\nssspnw1.equals(ssspnw4)");
    } else  System.out.println("\n!ssspnw1.equals(ssspnw4)");

    System.out.println("\nsingle source paths from 0 with weights altered by "+offset
        +"\n  (using NonWeightedDirectedEdgeXs)");
    for (Seq<NonWeightedDirectedEdgeX> s : ssspnw4) System.out.println(s.toString2());
     
//  System.out.println("\nsingle source paths from 0 with weights altered by "+offset);
//  for (Seq<DirectedEdgeX> s : sssp4) System.out.println(s.toString2()); 
    
/*
    test adusting weights downwards with all positive resulting weights
      shows SSSP changes for target vertices 1 and 5
    
    !ssspnw1.equals(ssspnw2)
    
    single source paths from 0 with unaltered weights
      (using NonWeightedDirectedEdgeXs)
    ()
    (0->4, 4->5, 5->1)
    (0->2)
    (0->2, 2->7, 7->3)
    (0->4)
    (0->4, 4->5)
    (0->2, 2->7, 7->3, 3->6)
    (0->2, 2->7)
    
    single source paths from 0 with weights altered by -0.25
      (using NonWeightedDirectedEdgeXs)
    ()
    (0->2, 2->7, 7->5, 5->1)
    (0->2)
    (0->2, 2->7, 7->3)
    (0->4)
    (0->2, 2->7, 7->5)
    (0->2, 2->7, 7->3, 3->6)
    (0->2, 2->7)
    
    test adusting weights upwards by 5
      shows SSSPs remain unaltered
    
    ssspnw1.equals(ssspnw3)
    
    single source paths from 0 with weights altered by 5.0
      (using NonWeightedDirectedEdgeXs)
    ()
    (0->4, 4->5, 5->1)
    (0->2)
    (0->2, 2->7, 7->3)
    (0->4)
    (0->4, 4->5)
    (0->2, 2->7, 7->3, 3->6)
    (0->2, 2->7)
    
*/    

  }

}


