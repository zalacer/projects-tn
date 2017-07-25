package ex44;

/* p690
  4.4.40 Bottleneck SPT. Show that an MST of an undirected graph is equiv-
  alent to a bottleneck SPT of the graph: For every pair of vertices v and 
  w, it gives the path connecting them whose longest edge is as short as 
  possible.
  
  Assuming an undirected edge-weighted graph has edges with positive weights,
  suppose an MST for it is calculated using Kruskal's algo that processes edges 
  in order increasing weights and succesively adds minimal weight crossing edges 
  (Proposition O, page 624) that are also minimal for their pairs of vertices 
  due to the processing order. Therefore each edge in the resulting MST is as 
  light as possible for its vertices so that's true of the heaviest edge and the 
  MST is equivalent to a bottleneck SPT. This result applies for MSTs calculated 
  using the cut property(Proposition J, p606) to add minimal weight crossing 
  edges and for methods that can be demonstrated to produce identical MSTs.

 */  

public class Ex4440BottleneckSPT {

  public static void main(String[] args) {

  }

}


