package ex41;

import static graph.RandomSparseGraph.createRandomSparseGraph;

import graph.EuclidianGraph;
import graph.GraphX;

/* p564
  4.1.41 Random sparse graphs. Write a program RandomSparseGraph to generate
  random sparse graphs for a well-chosen set of values of V and E such that 
  you can use it to run meaningful empirical tests on graphs drawn from the 
  Erdös-Renyi model.

  The implementation is graph.RandomSparseGraph.createRandomSparseGraph(int) 
  and is demonstrated below.
 
 */                                                   

public class Ex4141RandomSparseGraphs {

  public static void main(String[] args) {

    GraphX g = createRandomSparseGraph(7);
    System.out.println(g);
    EuclidianGraph e = new EuclidianGraph(g);
    e.show();
    
  }

}



