package ex42;

import graph.EuclidianDigraph;
import graph.DigraphGeneratorX;

/* p600
  4.2.31 Euclidean digraphs. Modify your solution to Exercise 4.1.37 to create 
  an API EuclideanDigraph for graphs whose vertices are points in the plane, so 
  that you can work with graphical representations.
  
  This time I didn't bother specifying an API but took graph.EuclidianGraph and hacked
  it into graph.EuclidianDigraph, since that's what the instructions indicate. Some 
  notable modifications include making adj and and indegree Seqs to accomodate cases when 
  the number of vertices is unknown when the graph is initially created. Otherwise the 
  class includes all the methods in DigraphX joined with all the constructors from
  EuclidianDigraph trivially modified when necessary and requiring creation of 
  TransitiveClosureED and addition of an additional constructor supporting EuclidianDigraph 
  in DirectedCycleX and KosarajuSharirSCCX.
  
 */  

public class Ex4231EuclidianDigraphs {
  
  public static void main(String[] args) {
    
    // this constructor generates random vertex coordinates
    // all constructors are demonstrated in graph.EuclidianDigraph.main();
    EuclidianDigraph g = new EuclidianDigraph(DigraphGeneratorX.complete(7));
    g.show();     

  }

}



