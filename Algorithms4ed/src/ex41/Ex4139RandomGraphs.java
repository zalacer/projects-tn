package ex41;

import static v.ArrayUtils.par;

import graph.ErdosRenyiGraph;

/* p564
  4.1.39 Random graphs. Write a program ErdosRenyiGraph that takes 
  integer values V and E from the command line and builds a graph 
  by generating E random pairs of integers between 0 and V-1. 
  Note: This generator produces self-loops and parallel edges.

 */                                                   

public class Ex4139RandomGraphs {

  public static void main(String[] args) {
    
    ErdosRenyiGraph G = new ErdosRenyiGraph(12,21);
    System.out.println(G);
    System.out.print("marked="); par(G.marked());
    System.out.println("avgDegree="+G.avgDegree());
    System.out.println("numberOfSelfLoops="+G.numberOfSelfLoops());
    System.out.println("count="+G.count());
    System.out.print("id"); par(G.id());
    System.out.print("adj="); par(G.adj());
    System.out.println("hasSelfLoop="+G.hasSelfLoop());
    if (G.hasSelfLoop()) System.out.println("selfLoop="+G.selfLoop());
    System.out.println("hasParallelEdges="+G.hasParallelEdges());
    if (G.hasParallelEdges()) System.out.print("parallelEdges="+G.parallelEdges());
    
/*
    E=21
    12 vertices, 21 edges 
    0: 2 6 0 0 
    1: 6 1 1 
    2: 2 2 0 11 10 
    3: 6 3 3 9 10 7 10 
    4: 10 
    5: 9 8 
    6: 1 3 0 
    7: 9 8 3 
    8: 7 5 9 
    9: 7 3 9 9 5 8 
    10: 3 2 3 4 
    11: 2 
    
    marked=[true,true,true,true,true,true,true,true,true,true,true,true]
    avgDegree=1.75
    numberOfSelfLoops=5
    count=1
    id[0,0,0,0,0,0,0,0,0,0,0,0]
    adj=[(2,6,0,0),(6,1,1),(2,2,0,11,10),(6,3,3,9,10,7,10),(10),(9,8),(1,3,0),(9,8,3),(7,5,9),(7,3,9,9,5,8),(3,2,3,4),(2)]
    hasSelfLoop=true
    selfLoop=(0,0)
    hasParallelEdges=true
    parallelEdges=(0,0,0)    
*/    
  }

}



