package ex42;

import graph.DigraphX;
import graph.TopologicalX;

/* p598
  4.2.25 Unique topological ordering. Design an algorithm to determine whether a 
  digraph has a unique topological ordering. Hint : A digraph has a unique topo-
  logical ordering if and only if there is a directed edge between each pair of 
  consecutive vertices in the topological order (i.e., the digraph has a Hamil-
  tonian path). If the digraph has multiple topological orderings, then a second 
  topological order can be obtained by swapping a pair of consecutive vertices.
  
  This is implemented in graph.TopologicalX.isOrderUnique() and demonstrated below.

 */                                                   

public class Ex4225UniqueTopologicalOrdering {

  public static void main(String[] args) {

    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX d = new DigraphX(13,edges);
    TopologicalX t = new TopologicalX(d);
    System.out.println("order = "+t.order());
    System.out.println("isHamiltonianPath = "+t.isHamiltonianPath());
    System.out.println("isOrderUnique = "+t.isOrderUnique());
    
    d = new DigraphX(4);
    d.addEdge(0,1); d.addEdge(1,2); d.addEdge(2,3);
    t = new TopologicalX(d);
    System.out.println("\norder = "+t.order());
    System.out.println("isHamiltonianPath = "+t.isHamiltonianPath());
    System.out.println("isOrderUnique = "+t.isOrderUnique());

    
  }

}



