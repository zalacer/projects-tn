package ex42;

/* p599
  4.2.30 Queue-based topological sort. Develop a topological sort implementation that
  maintains a vertex-indexed array that keeps track of the indegree of each vertex. 
  Initialize the array and a queue of sources in a single pass through all the edges, 
  as in Exercise 4.2.7. Then, perform the following operations until the source queue 
  is empty:
  *  Remove a source from the queue and label it.
  *  Decrement the entries in the indegree array corresponding to the destination
     vertex of each of the removed vertex’s edges.
  *  If decrementing any entry causes it to become 0, insert the corresponding vertex
     onto the source queue. 
       
  This is already done in http://algs4.cs.princeton.edu/42digraph/TopologicalX.java
  that's locally available at graph.TopologicalIterative.

 */  


public class Ex4230QueueBasedTopologicalSort {
  
  public static void main(String[] args) {
    
  }
  
}



