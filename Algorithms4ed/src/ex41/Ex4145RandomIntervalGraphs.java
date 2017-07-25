package ex41;

import static graph.RandomIntervalGraph.createRandomIntervalGraph;

import graph.SymbolGraphSB;

/* p564
  4.1.45 Random interval graphs. Consider a collection of V intervals on the real 
  line (pairs of real numbers). Such a collection defines an interval graph with 
  one vertex corresponding to each interval, with edges between vertices if the 
  corresponding intervals intersect (have any points in common). Write a program 
  that generates V random intervals in the unit interval, all of length d, then 
  builds the corresponding interval graph. Hint: Use a BST.
  
  This is done in graph.RandomIntervalGraph.createRandomIntervalGraph(int,double)
  and demonstrated below.
  
 */                                                   

public class Ex4145RandomIntervalGraphs {

  public static void main(String[] args) {

    SymbolGraphSB sg =  createRandomIntervalGraph(199,.0201);
    System.out.println(sg);
    
  }

}



