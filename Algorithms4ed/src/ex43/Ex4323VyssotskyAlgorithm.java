package ex43;

import ds.Seq;
import ds.Stack;
import graph.EdgeWeightedGraphX;
import graph.EdgeX;

/* p633
  4.3.23 Vyssotsky’s algorithm. Develop an implementation that computes the MST by
  applying the cycle property (see Exercise 4.3.8) repeatedly: Add edges one at a 
  time to a putative tree, deleting a maximum-weight edge on the cycle if one is 
  formed. Note :  This method has received less attention than the others that we 
  consider because of the comparative difficulty of maintaining a data structure 
  that supports efficient implementation of the “delete the maximum-weight edge on 
  the cycle’’ operation.
  
  This is implemented as a static method below and as a class at graph.VyssotskyMST. 
  It works very similarly to Kruskal's algorithm except all the edges are sorted only 
  once (at the beginning) and instead of using UF to test preexisting connectivity 
  between the vertices in the current edge, the edge is inserted into the prospective 
  MST that's then tested for cyclicity and if it contains a cycle its largest edge is 
  is identified and removed from the prospective MST. This leads to apparently more
  complex code, however Kruskal's algorithm has it's complexities if you include the
  internals of weighted quick union by rank with path compression by halving (UF.java).
  All in all perhaps my implementation of Vyssotsky’s algorithm is more deeply trans-
  parent than graph.KruskalMSTB.
 */  

public class Ex4323VyssotskyAlgorithm {
  
  public static Seq<EdgeX> vyssotskyMST(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException("vyssotskyMST: G == null");
    int V = G.V();
    Seq<EdgeX> edges = (new Seq<>(G.edges())).sort();
    Seq<EdgeX> mst = new Seq<>(); 
    int i = 0;
    while(mst.size() < V-1 && i < edges.size()) {
      mst.add(edges.get(i++));
      EdgeWeightedGraphX g = new EdgeWeightedGraphX(V,mst);
      Stack<Integer> cycleStack = g.graph().cycle();
      if (!(cycleStack == null || cycleStack.size() < 2)) {
        Seq<Integer> cycle = new Seq<>(cycleStack);
        EdgeX max = null, e = null;
        for (int j = 0; j < cycle.size()-1; j++) {
          e = g.findEdge(cycle.get(j), cycle.get(j+1), G.adj(cycle.get(j)));
          if (e != null) {
            if (max == null) max = e;
            else if (e.w() > max.w()) max = e;
          }
        }
        if (max != null) mst.remove(g.removeEdge(max.u(),max.v()));
      }
    }
    return mst;
  }
  
  public static double weight(Seq<EdgeX> seq) {
    double weight = 0.0;
    for (EdgeX e : seq) weight += e.weight();
    return weight;
  }

  public static void main(String[] args) {
    
    // V and edges for tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93"; 
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    Seq<EdgeX> mst = vyssotskyMST(G);
    System.out.println("MST:");
    for (EdgeX e : mst) System.out.println(e);
    System.out.println("weight: "+weight(mst));
/*   
    MST:
    (0-7,0.16000)
    (2-3,0.17000)
    (1-7,0.19000)
    (0-2,0.26000)
    (5-7,0.28000)
    (4-5,0.35000)
    (6-2,0.40000)
    weight: 1.81    
*/    
     
  }

}


