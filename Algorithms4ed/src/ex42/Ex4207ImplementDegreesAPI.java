package ex42;

/* p596
  4.2.7  The indegree of a vertex in a digraph is the number of directed edges 
  that point to that vertex. The outdegree of a vertex in a digraph is the 
  number of directed edges that emanate from that vertex. No vertex is reachable 
  from a vertex of outdegree 0, which is called a sink; a vertex of indegree 0, 
  which is called a source, is not reachable from any other vertex. A digraph 
  where self-loops are allowed and every vertex has outdegree 1 is called a map 
  (a function from the set of integers from 0 to V–1 onto itself). Write a program  
  Degrees.java that implements the following API:
  
  public class Degrees
  Degrees(Digraph G)  constructor
  int indegree(int v)  indegree of  v
  int outdegree(int v)  outdegree of  v
  Iterable<Integer> sources()  sources
  Iterable<Integer> sinks()  sinks
  boolean isMap()  is  G a map?
  
  Degrees is implemented in graph.Degrees.
  
 */                                                   

public class Ex4207ImplementDegreesAPI {

  public static void main(String[] args) {
    
  }

}



