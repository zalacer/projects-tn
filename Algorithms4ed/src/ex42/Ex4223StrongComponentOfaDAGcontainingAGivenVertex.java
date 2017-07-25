package ex42;

/* p598
  4.2.23 Strong component. Describe a linear-time algorithm for computing the strong
  connected component containing a given vertex v. On the basis of that algorithm, de-
  scribe a simple quadratic algorithm for computing the strong components of a digraph.
  
  To get the strong component containing v find all vertices reachable from v using
  dfs or bfs, similarly find all vertices that can reach v using bfs or dfs on the
  reverse graph, then the strong component is the intersection of the two sets of
  vertices. To get all strong components repeat this process on a vertex not in the
  union of all components already found. When no such vertices remain all components
  have been found.

 */                                                   

public class Ex4223StrongComponentOfaDAGcontainingAGivenVertex {

  public static void main(String[] args) {

  }

}



