package graph;

//import edu.princeton.cs.algs4.In;

// from p522
public interface UndirectedGraphAPI {
  
  //  public UndirectedGraphAPI(int V){}; //create a  V -vertex graph with no edges
  //  UndirectedGraphAPI(In in){}  //read a graph (2*E+2 integers) from input stream in
  
  public int V();  //number of vertices
  public int E();  //number of edges
  public void addEdge(int v, int w);  //add edge  v-w to this graph
  public Iterable<Integer> adj(int v);  //vertices adjacent to  v
  public String toString();  //string representation

}
