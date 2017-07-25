package ex41;

import graph.GraphEx4105;

/* p558
  4.1.5  Modify Graph to disallow parallel edges and self-loops.
  
  This is done in GraphEx4105 that began as a copy of Graph then altered by:
  1. Adding numberOfSelfLoops() from graph.GraphClient.
  2. Integrating hasSelfLoop() and hasParallelEdges() from graph.Cycle.
  3. Integrating most of graph.CC to track connected components for monitoring.
  4. Modifying addEdge() to not add parallel edges.
  5. Modifying the In and graph copy constructors to not add self-loop edges
  6. Adding a new method named removeEdge() to remove edges.
  7. Adding a while (hasParallelEdges()) loop at the end of the In and graph copy 
     constructors to remove all parallel edges.
     
  A demo of this is in GraphEx4105.main() shown below for input from tinyGex3.txt
  that contains the following:
  12
  21
  8 4
  2 3
  1 11
  0 6
  3 6
  10 3
  7 11
  7 8
  11 8
  2 0
  6 2
  5 2
  5 10
  8 1
  4 1
  1 8   // directly added parallel edge
  3 10  // directly added parallel edge
  2 5   // directly added parallel edge
  9 9   // directly added self-loop
  7 7   // directly added self-loop
  5 5   // directly added self-loop

 */

public class Ex4105ModifyGraph2DisallowParallelEdgesAndSelfLoops {

  public static void main(String[] args) {
    
    GraphEx4105.main(new String[]{"tinyGex3.txt"});
 
  }

}

