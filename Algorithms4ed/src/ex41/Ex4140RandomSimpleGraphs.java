package ex41;

import graph.RandomSimpleGraph;

/* p564
  4.1.40 Random simple graphs. Write a program RandomSimpleGraph that 
  takes integer values V and E from the command line and produces, with 
  equal likelihood, each of the possible simple graphs with V vertices 
  and E edges.
  
  Since this is the section on undirected graphs and simple graphs are defined
  for them in this section (p518) I assume this exercise calls for random
  undirected simple graphs for which the max number of edges is V*(V-1)/2.
  
  The implementation is graph.RandomSimpleGraph and demonstrated below.
  
 */                                                   

public class Ex4140RandomSimpleGraphs {

  public static void main(String[] args) {

    RandomSimpleGraph R = new RandomSimpleGraph(7,9);
    System.out.println(R);
    
    
  }

}



