package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/TransitiveClosure.java

@SuppressWarnings("unused")
public class TransitiveClosureEWDI {
  private DirectedDFSX[] tc;  // tc[v] = reachable from v
  private EdgeWeightedDigraphI G;
  private int V;
//  private BagX<Integer>[] adj;
  private int edgesAdded = 0;

  public TransitiveClosureEWDI(EdgeWeightedDigraphI g) {
    if (g == null) throw new IllegalArgumentException(
        "TransitiveClosureEWDI: EdgeWeightedDigraphXI is null");
    G = g; V = g.V(); //adj = g.adj();
    tc = new DirectedDFSX[V];
    for (int v = 0; v < V; v++) tc[v] = new DirectedDFSX(g, v);
    updateGraph();
  }
  
  public TransitiveClosureEWDI(EdgeWeightedDigraphI g, String noUpdate) {
    if (g == null) throw new IllegalArgumentException(
        "TransitiveClosureEWDI: EdgeWeightedDigraphXI is null");
    G = g; V = g.V(); //adj = g.adj();
    tc = new DirectedDFSX[V];
    for (int v = 0; v < V; v++) tc[v] = new DirectedDFSX(g, v);
  }

  public boolean reachable(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    return tc[v].marked(w);
  }

  private void validateVertex(int v) {
    int V = tc.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  public void updateGraph() {
    // edges added if any have a weight of 1
    for (int i = 0; i < V; i++) 
      for (int j = 0; j < V; j++) 
        if (reachable(i,j)) {
          Iterable<DirectedEdgeI> itbl = G.adj(i);
          boolean contains = false;
          for (DirectedEdgeI e : itbl) if (e.to() == j) { contains = true; break; }
          if (contains == false) { G.addEdge(i,j,1); edgesAdded++; }
        }
  }
  
  public int edgesAdded() { return edgesAdded; }
  
  public EdgeWeightedDigraphI graph() { return G; }

  public static void main(String[] args) {
//    In in = new In(args[0]);
    EdgeWeightedDigraphI G = new EdgeWeightedDigraphI(6);
    G.addEdge(0,1,19);  G.addEdge(1,2,34);  G.addEdge(2,3,27); 
    G.addEdge(3,4,17); G.addEdge(4,5,39);
    System.out.println("G:\n"+G);
    System.out.println("G.E="+G.E());
    int V = G.V();
    TransitiveClosureEWDI tc = new TransitiveClosureEWDI(G);
    System.out.println("edgesAdded="+tc.edgesAdded());
//    DigraphX H = tc.graph(); 
//    System.out.println("H.E="+H.E());
    System.out.println("G.E="+G.E());

    int edges = 0;
    // print header
    StdOut.print("     ");
    for (int v = 0; v < V; v++)
      StdOut.printf("%3d", v);
    StdOut.println();
    StdOut.println("--------------------------------------------");

    // print transitive closure
    for (int v = 0; v < V; v++) {
      StdOut.printf("%3d: ", v);
      for (int w = 0; w < V; w++) {
        if (tc.reachable(v, w)) { StdOut.printf("  T"); edges++; }
        else                    StdOut.printf("   ");
      }
      StdOut.println();
    }
    System.out.println("edges="+edges);
    System.out.println("G:\n"+G);

    
    
  }

}
