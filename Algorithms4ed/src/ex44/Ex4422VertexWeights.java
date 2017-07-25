package ex44;

/* p688
  4.4.22 Vertex weights. Show that shortest-paths computations in 
  edge-weighted digraphs with nonnegative weights on vertices (where 
  the weight of a path is defined to be the sum of the weights of the 
  vertices) can be handled by building an edge-weighted digraph that 
  has weights on only the edges.
  
  One way is to reduce the SPT computation to the usual Dijkstra algo
  by modifying the weight function through addition of the the weight of 
  the to() vertex of each edge. Another way is to convert vertex weights
  to edge weights by splitting each weighted verted into two, forming a
  weighted edge from them with the weight of the original vertex, and
  coalescing them to a single vertex after the SPT is computed.
  These approaches are described at
  https://stackoverflow.com/questions/10453053/graph-shortest-path-with-vertex-weight
 
 */  

public class Ex4422VertexWeights {

  public static void main(String[] args) {

  }

}


