package ex42;

/* p596
  4.2.1  What is the maximum number of edges in a digraph with V vertices and no paral-
  lel edges? What is the minimum number of edges in a digraph with V vertices, none of
  which are isolated?
  
  Max is V(V-1) excluding self-edges, V^2 including self-edges. Min is V-1. The min 
  guaranteeing no isolated vertices is (V-1)(V-2)+1 considering the max number of edges
  of a disconnected digraph of V vertices is given by that of that complete subdigraph 
  of V-1 vertices plus an isolated vertex.
  
  See
  https://stackoverflow.com/questions/5058406/what-is-the-maximum-number-of-edges-in-a-directed-graph-with-n-nodes
 
 */                                                   

public class Ex4201DigraphMinMaxEdgesWithVnonIsolatedVertices {

  public static void main(String[] args) {

  }

}



