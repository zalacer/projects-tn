package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/TransitiveClosure.java

@SuppressWarnings("unused")
public class TransitiveClosureED {
  private DirectedDFSX[] tc;  // tc[v] = reachable from v
  private EuclidianDigraph G;
  private int V;
//  private BagX<Integer>[] adj;
  private int edgesAdded = 0;

  public TransitiveClosureED(EuclidianDigraph g) {
    if (g == null) throw new IllegalArgumentException("TransitiveClosureX: DigraphX is null");
    G = g; V = g.V(); //adj = g.adj();
    tc = new DirectedDFSX[V];
    for (int v = 0; v < V; v++) tc[v] = new DirectedDFSX(g, v);
    updateGraph();
  }
  
  public TransitiveClosureED(EuclidianDigraph g, String noUpdate) {
    if (g == null) throw new IllegalArgumentException("TransitiveClosureX: DigraphX is null");
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
    for (int i = 0; i < V; i++) 
      for (int j = 0; j < V; j++) 
        if (reachable(i,j) && !G.adj().get(i).contains(j)) { G.addEdge(i,j); edgesAdded++; }
  }
  
  public int edgesAdded() { return edgesAdded; }
  
  public EuclidianDigraph graph() { return G; }

  public static void main(String[] args) {
//    In in = new In(args[0]);
    EuclidianDigraph G = new EuclidianDigraph(6);
    G.addEdge(0,1);  G.addEdge(1,2);  G.addEdge(2,3); G.addEdge(3,4); G.addEdge(4,5);
    System.out.println("G.E="+G.E());
    int V = G.V();
    TransitiveClosureED tc = new TransitiveClosureED(G);
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
    
    
  }

}
