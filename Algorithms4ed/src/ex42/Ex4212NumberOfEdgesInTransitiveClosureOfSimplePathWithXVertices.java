package ex42;

import graph.DigraphX;
import graph.DigraphGeneratorX;
import graph.TransitiveClosureX;

/* p597
  4.2.12  How many edges are there in the transitive closure of a digraph 
  that is a simple directed path with V vertices and V–1 edges?
  
  V(V-1)/2 + V. Some examples are given below;
  
 */                                                   

public class Ex4212NumberOfEdgesInTransitiveClosureOfSimplePathWithXVertices {

  public static void main(String[] args) {
    
    // TransitiveClosureX constructor updates the input graph with added edges
    
    System.out.println("starting with 1 vertex and no edges");
    DigraphX G = new DigraphX(1);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    TransitiveClosureX tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    
    System.out.println("\nstarting with 2 vertices and 1 edge between them");
    G = new DigraphX(2);
    G.addEdge(0,1);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    
    System.out.println("\nstarting with 3 vertices and 2 edges in a path");
    G = new DigraphX(3);
    G.addEdge(0,1); G.addEdge(1,2);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    
    System.out.println("\nstarting with 4 vertices and 3 edges in a path");
    G = new DigraphX(4);
    G.addEdge(0,1); G.addEdge(1,2); G.addEdge(2,3);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E()); 
    
    System.out.println("\nstarting with 5 vertices and 4 edges in a path");
    G = new DigraphX(5);
    G.addEdge(0,1); G.addEdge(1,2); G.addEdge(2,3); G.addEdge(3,4);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E()); 
    
    System.out.println("\nstarting with 159 vertices and 158 edges in a path");
    G = DigraphGeneratorX.path(159);
    System.out.println("G.V="+G.V()+" G.E="+G.E());
    tc = new TransitiveClosureX(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
    System.out.println("G.V="+G.V()+" G.E="+G.E()); 
 
  }

}



