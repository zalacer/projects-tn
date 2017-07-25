package ex43;

/* p631
  4.3.8 Prove the following, known as the cycle property: Given any cycle in an 
  edge-weighted graph (all edge weights distinct), the edge of maximum weight in 
  the cycle does not belong to the MST of the graph.
  
  In an edge-weighted graph with MST mst, let c be a cycle with edge of maximum
  weight max. If max isn't in mst we are done, so suppose that max is in mst. c 
  must have an edge e that isn't in mst because mst is a tree that can't contain 
  a cycle by definition (https://en.wikipedia.org/wiki/Tree_%28graph_theory%29).
  Create a new MST mst' in the graph by replacing max with e in mst. By assumption
  the weight of e must be less than that of max, so the sum of weights of the edges
  in mst' must be less than that of mst and the latter cannot be a MST. Therefore
  by contradiction the edge of maximum weight of any cycle in an edge-weighted graph
  does not belong to the MST of the graph.
    
 */  

public class Ex4308ProveTheCycleProperty4EdgeWeightedGraphs {
  
  public static void main(String[] args) {
    
  }

}


