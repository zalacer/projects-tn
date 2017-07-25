package ex41;

/* p558
  4.1.1  What is the maximum number of edges in a graph with V vertices 
  and no parallel edges? What is the minimum number of edges in a graph 
  with V vertices, none of which are isolated?
  
  The max number of edges is V*(V-1)/2 if the graph is undirected and 
  self-loops aren't allowed.
  
  The min number of edges for no isolated vertices is V-1 and that occurs
  for a simple path and star but not a cycle.
  
  The min number of edges guaranteeing no isolated vertices is (V-1)*(V-2)+1
  since the max number of edges in a disconnected graph with V vertices is
  (V-1)(V-2)/2 based on a graph including a complete subgraph of V-1 vertices
  plus an isolated vertex.
  
  See:
  https://math.stackexchange.com/questions/17747/why-a-complete-graph-has-fracnn-12-edges
  https://cs.stackexchange.com/questions/7373/how-many-edges-must-a-graph-with-n-vertices-have-in-order-to-guarantee-that-it-i
  
 */

public class Ex4101MaxAndMinNumberOfEdges {

  public static void main(String[] args) {

  }

}

