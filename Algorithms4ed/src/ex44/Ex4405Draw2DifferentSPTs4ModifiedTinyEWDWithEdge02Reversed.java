package ex44;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import analysis.Draw;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;
import graph.EuclidianEdgeWeightedDigraph;
import v.Tuple2;

/* p685
  4.4.5 Change the direction of edge 0->2 in tinyEWD.txt (see page 644). Draw two 
  different SPTs that are rooted at 2 for this modified edge-weighted digraph.
  
  This is demonstrated below, however since simply reversing 0->2 didn't create 
  multiple different SPTs, the weights of 7-5, 5->1 and 1->3 were adjusted so their
  sum equals the weight of 7->3 to create 2 different SPTs that can be instantiated
  using different versions of relax() including the normal version and another called
  altRelax() that does >= comparisons instead of strictly >.
 */  

public class Ex4405Draw2DifferentSPTs4ModifiedTinyEWDWithEdge02Reversed {

  public static void dispose(Draw[] draw) {
    System.out.println("enter any key to dispose of drawings");
    try {
      System.in.read();
      for (Draw d : draw) if (d != null) d.frame().dispose();
      System.exit(0);
    } catch (IOException e) {
      for (Draw d : draw)  if (d != null) d.frame().dispose();
      System.exit(0);
    }
  }
  
  public static void main(String[] args) {
    
  /* outline of changes to EdgeWeightedDigraphX created from tinyEWD.txt:
     reverse 0->2 and make weights of 7->5 + 5->1 + 1->3= 7-3 == 0.39
     by setting weight 7->5 = 0.20, weight 5->1 = 0.09 and weight of 1->3 = 0.10
    8
    15
    4 5 0.35
    5 4 0.35
    4 7 0.37
    5 7 0.28
    7 5 0.28 //change weight to 0.20
    5 1 0.32 //change weight to 0.09
    0 4 0.38
    0 2 0.26 //reverse to 2 0 0.26
    7 3 0.39
    1 3 0.29 //change weight to 0.10
    2 7 0.34
    6 2 0.40
    3 6 0.52
    6 0 0.58
    6 4 0.93   
 */
    
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
 
    // reverse edge 0->2
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
    } else {
      System.out.println("could not find edge 0->2");
    }
    
    // change weight of 7->5 to 0.20
    x = G.findEdge(7, 5);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.20);
      System.out.println("edge(7->5,0.28) replaced with edge(7->5,0.20");
    } else {
      System.out.println("could not find edge 7->5");
    }
    
    // change weight of 5->1 to 0.09
    x = G.findEdge(5, 1);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.09);
      G.search();
      System.out.println("edge(5->1,0.32) replaced with edge(5->1,0.09");
    } else {
      System.out.println("could not find edge 5->1");
    }
    
    // change weight of 1->3 to 0.10
    x = G.findEdge(1, 3);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.10);
      G.search();
      System.out.println("edge(1,3,0.29) replaced with edge(1,3,0.10");
    } else {
      System.out.println("could not find edge 1->3");
    }
    
    int V = G.V();
    System.out.println("\nEdgeWeightedDigraphX G:\n"+G);
    
    int source = 2; // ex4405 requirement
    
    // define coordinates for EuclidianEdgeWeightedDigraphs
    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    
    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
    
    EuclidianEdgeWeightedDigraph E;
    String title;   
    Draw[] draw = new Draw[2];
    
    // using the normal relax() with > comparison
    DijkstraSPX sp1 = new DijkstraSPX(G, source, false);    
    System.out.println("SP edgeTo using normal relax(): ");
    for (DirectedEdgeX de : sp1.edgeTo()) System.out.println(de);
    E = new EuclidianEdgeWeightedDigraph(G,coords);
    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = E.midPoints();
    title = "modified TinyEWD.txt using normal relax()|SPT from 2 (SPT edges are black)";
    draw[0] = E.showEx44040507(2.2,sp1.sptEdges(),labels,sp1.parentEdges(),title,midPoints);
    System.out.println();
    
    // using altRelax() with >= comparison
    DijkstraSPX sp2 = new DijkstraSPX(G, source, true);
    System.out.println("edgeTo using altRelax(): ");
    for (DirectedEdgeX de : sp2.edgeTo()) System.out.println(de);
    E = new EuclidianEdgeWeightedDigraph(G,coords);
    title = "modified TinyEWD.txt using altRelax()|SPT from 2 (SPT edges are black)";
    draw[1] = E.showEx44040507(2.2,sp2.sptEdges(),labels,sp2.parentEdges(),title,midPoints);
    System.out.println();

    dispose(draw);

  }

}


