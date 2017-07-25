package ex41;

import graph.CycleX;
import graph.GraphX;

/* p562
  4.1.32 Parallel edge detection. Devise a linear-time algorithm to count
  the parallel edges in a graph.  
  Hint: maintain a boolean array of the neighbors of a vertex, and reuse 
  this array by only reinitializing the entries as needed.
  
  This is done with graph.CycleX.findAllParallelEdges(GraphX) that's run
  during construction instead of graph.CycleX.hasParallelEdges(GraphX).
  findAllParallelEdges excludes duplicate parallel edges (such as (8,1,8)
  if (1,8,1) was already found and self-loops extended to parallel edges
  (such as (5,5,5)) and is demonstrated below.
  
 */                                                   

public class Ex4132CountNumberOfParallelEdgesInLinearTime {

  public static void main(String[] args) {
    
    // edges are from tinyGex3.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 " 
        + "2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    GraphX g = new GraphX(12,edges);
    CycleX c = new CycleX(new GraphX(12,edges));
    System.out.println("graph edges = "+g.E());
    System.out.println("CycleX.findAllParallelEdges() inner for loop "
        + "iterations = " + c.getFapecount());
 
/*    
    parallel edges : ((1,8,1),(2,5,2),(3,10,3))
    hasSelfLoop: (5,5)
    cycle: (5,5)
    graph edges = 42
    CycleX.findAllParallelEdges() inner for loop iterations = 42
*/ 
    
  }

}



