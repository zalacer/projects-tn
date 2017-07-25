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
  4.4.4  Draw the SPT for source 0 of the edge-weighted digraph obtained 
  by deleting vertex 7 from tinyEWD.txt (see page 644), and give the par-
  ent-link representation of the SPT. Answer the question for the same graph 
  with all edges reversed.
  
  This is done with 3 drawings that include the SPTs and parent-link repre-
  sentations and can be displayed by running main below. The first drawing
  is for unmodified tinyEWD.txt, the second for tinyEWD.txt after removing 
  vertex 7 and associated edges and the third for tinyEWD.txt after removing
  vertex 7 and associated edges and reversing all the remaining edges.
 */  

public class Ex4404ShowSource0SPTofTinyEWDAfterRemovingVertex7AndAfterReversingAllEdges {

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
    
    Draw[] draw = ofDim(Draw.class,3);

    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    

    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));

    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");

    DijkstraSPX sp;
    EuclidianEdgeWeightedDigraph E;
    String title; 
    int source = 0;

    sp = new DijkstraSPX(G, source, "quiet");
    E = new EuclidianEdgeWeightedDigraph(G,coords);
    title = "tinyEWD.txt without modification|SPT from 0 (SPT edges are black)";
    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = E.midPoints();
    draw[0] = E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title, midPoints);

    boolean vertex7removed = G.removeLastVertex();
    if (vertex7removed == false) System.out.println("vertex 7 couldn't be removed");
    V = G.V();
    coords = coords.take(V);
    labels = take(labels,V);
    sp = new DijkstraSPX(G, source, "quiet");
    E = new EuclidianEdgeWeightedDigraph(G,coords);
    title = "tinyEWD.txt with vertex 7 removed|SPT from 0 (SPT edges are black)";
    midPoints = E.midPoints();
    draw[1] = E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title, midPoints);

    G = G.reverse();
    sp = new DijkstraSPX(G, source, "quiet");
    E = new EuclidianEdgeWeightedDigraph(G,coords);
    title = "tinyEWD.txt with vertex 7 removed and edges reversed|"
        +"SPT from 0 (SPT edges are black)";
    midPoints = E.midPoints();
    draw[2] = E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title, midPoints);
    
    dispose(draw);

  }

}


