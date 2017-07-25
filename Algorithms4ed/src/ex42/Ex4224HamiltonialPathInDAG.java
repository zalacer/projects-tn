package ex42;

import graph.DigraphX;
import graph.TopologicalX;

/* p598
  4.2.24 Hamiltonian path in DAGs. Given a DAG, design a linear-time algorithm to
  determine whether there is a directed path that visits each vertex exactly once.
  
  Answer: Compute a topological sort and check if there is an edge between each con-
  secutive pair of vertices in the topological order.
  
  This is implemented in graph.TopologicalX.isHamiltonianPath() and demonstrated below.

 */                                                   

public class Ex4224HamiltonialPathInDAG {

  public static void main(String[] args) {

    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX d = new DigraphX(13,edges);
    TopologicalX t = new TopologicalX(d);
    System.out.println("order = "+t.order());
    System.out.println("isHamiltonianPath = "+t.isHamiltonianPath());
    
    d = new DigraphX(4);
    d.addEdge(0,1); d.addEdge(1,2); d.addEdge(2,3);
    t = new TopologicalX(d);
    System.out.println("\norder = "+t.order());
    System.out.println("isHamiltonianPath = "+t.isHamiltonianPath());
    
    
  }

}



