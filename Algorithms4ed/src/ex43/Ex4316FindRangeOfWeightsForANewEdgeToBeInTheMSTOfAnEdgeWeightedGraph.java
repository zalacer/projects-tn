package ex43;

import static graph.EdgeWeightedGraphX.findMaxWeight4EdgeToBeAddedToTheMST;

import graph.EdgeWeightedGraphX;
import graph.EdgeX;

/* p632
  4.3.16  Given an MST for an edge-weighted graph G and a new edge e, write 
  a program that determines the range of weights for which e is in an MST.
  
  This is implemented in 
    EdgeWeightedGraph.findMaxWeight4EdgeToBeAddedToTheMST(EdgeWeightedGraphX,EdgeX)
  and demonstrated below. It returns Math.nextDown() of the edge with the greatest
  weight in the cycle formed in the MST of the graph by the addition of the new edge.
  so that a new edge with the same vertices and equal or lesser weight than this will 
  be in the MST.
     
 */  

public class Ex4316FindRangeOfWeightsForANewEdgeToBeInTheMSTOfAnEdgeWeightedGraph {
  
  public static void main(String[] args) {

    // V and edges from http://algs4.cs.princeton.edu/43mst/tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    EdgeX edge = new EdgeX(0,1,0);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    EdgeWeightedGraphX mst = G.mst();
    System.out.println("G's original MST:\n"+mst.toString2());
    
    // findMaxWeight4EdgeToBeAddedToTheMST returns Math.nextUp() of the greatest
    // weight of the edges in the cycle formed by addition of edge to the MST
    double w = findMaxWeight4EdgeToBeAddedToTheMST(G,edge);
    System.out.println("w = "+w); //0.18999999999999997
    edge = new EdgeX(0,1,w);
    G.addEdge(edge);
    EdgeWeightedGraphX mst2 = G.mst();
    System.out.println("G's MST after Edge(+0-1,0.18999999999999997) has been added\n"
        + mst2.toString2()+"\n");
    
    assert !mst.equals(mst2);
    
    // now show that if w is incremented to Math.nextUp(Math.nextUp(w)) the edge won't 
    // be in the MST since then w is greatest weight in the cycle formed by the addition
    // of the edge
    w = Math.nextUp(Math.nextUp(w));
    System.out.println("w = "+w); //0.19000000000000003
    edge = new EdgeX(0,1,w);
    G = new EdgeWeightedGraphX(V,edges);
    G.addEdge(edge);
    EdgeWeightedGraphX mst3 = G.mst();
    System.out.println("G's MST after Edge(+0-1,0.19000000000000003) has been added\n"
        + mst3.toString2());
  
    assert !(mst2.equals(mst3));
  
    assert mst.equals(mst3);

  
  }

}


