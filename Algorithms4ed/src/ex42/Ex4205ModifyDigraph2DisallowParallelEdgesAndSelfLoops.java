package ex42;

import graph.DigraphX;
import graph.DigraphEx4205;

/* p596
  4.2.5  Modify  Digraph to disallow parallel edges and self-loops.
  
  From http://algs4.cs.princeton.edu/42digraph/:
  "Two edges are parallel if they connect the same ordered pair of vertices." 
  
  This is done in graph.DigraphEx4205 and demonstrated below in comparison
  with graph.DigraphX using a modified version of tinyDGex2.txt data.
  
 */                                                   

public class Ex4205ModifyDigraph2DisallowParallelEdgesAndSelfLoops {

  public static void main(String[] args) {
    
    // edges modified from tinyDGex2.txt and stored in tinyDGex3.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 "
        +"8 1 4 1 9 9 9 9 9 9 7 7 7 7 3 10 10 3";
    
    System.out.println("DigraphX test showing self-loops and parallel-edges:");   
    DigraphX d = new DigraphX(12, edges);
    System.out.println(d);
    System.out.println("hasSelfLoop="+d.hasSelfLoop());
    // each self-loop is represented by a single vertex v implying v->v
    System.out.println("allSelfLoops="+d.allSelfLoops());
    System.out.println("selfLoopCount="+d.selfLoopCount());
    System.out.println("hasParalleledge="+d.hasParallelEdge());
    System.out.println("allParallelEdges="+d.allParallelEdges());
    // count pairs of parallel edges
    System.out.println("parallelEdgeCount="+d.parallelEdgeCount());
    System.out.println("hasCycle="+d.hasCycle());
    if (d.hasCycle()) System.out.println("cycle="+d.cycle());    
    
    System.out.println("\nDigraphEx4205 showing no self-loops or parallel-edges:");   
    DigraphEx4205 f = new DigraphEx4205(12, edges);
    System.out.println(f);
    System.out.println("hasSelfLoop="+f.hasSelfLoop());
    System.out.println("allSelfLoops="+f.allSelfLoops());
    // each self-loop is represented by a single vertex v implying v->v
    System.out.println("selfLoopCount="+f.selfLoopCount());
    System.out.println("hasParallelEdge="+f.hasParallelEdge());
    System.out.println("allParallelEdges="+f.allParallelEdges());
    // count pairs of parallel edges
    System.out.println("parallelEdgeCount="+f.parallelEdgeCount());
    System.out.println("hasCycle="+f.hasCycle());
    if (f.hasCycle()) System.out.println("cycle="+f.cycle());
/*
    DigraphX test showing self-loops and parallel-edges:
    12 vertices, 23 edges 
    0: 6 
    1: 11 
    2: 0 3 
    3: 10 10 6 
    4: 1 
    5: 10 2 
    6: 2 
    7: 7 7 8 11 
    8: 1 4 
    9: 9 9 9 
    10: 3 3 
    11: 8 
    
    hasSelfLoop=true
    allSelfLoops=(7,7,9,9,9)
    selfLoopCount=5
    hasParalleledge=true
    allParallelEdges=((3,10),(10,3))
    parallelEdgeCount=2
    hasCycle=true
    cycle=(2,0,6,2)
    
    DigraphEx4205 showing no self-loops or parallel-edges:
    12 vertices, 15 edges 
    0: 6 
    1: 11 
    2: 0 3 
    3: 6 
    4: 1 
    5: 10 2 
    6: 2 
    7: 8 11 
    8: 1 4 
    9: 
    10: 3 
    11: 8 
    
    hasSelfLoop=false
    allSelfLoops=()
    selfLoopCount=0
    hasParallelEdge=false
    allParallelEdges=()
    parallelEdgeCount=0
    hasCycle=true
    cycle=(2,0,6,2)

*/  
    
  }

}



