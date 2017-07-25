package graph;

import ds.Seq;
import v.Tuple3;

// shortest ancestral path (SAP) between two vertices in a DAG is defined
// in ex4222 as a common ancestor and the two shortest paths to it.
// in sap(Digraph,int,int) a SAP is encoded as a 
//   Tuple3<Integer,Iterable<Integer>,Iterable<Integer>>
// that has a value (-1,null,null) iff there is no SAP

public class SAP {

  public static Tuple3<Integer,Iterable<Integer>,Iterable<Integer>> 
      sap(DigraphX g, int x, int y) {
    // return the SAP of x and y in G as a Tuple3 if possible else
    // return (-1,null,null). g must be a DAG.
    // the format of the output Tuple3 is:
    // .1 commonAncestor vertex
    // .2 shortest path from x to commonAncestor
    // .3 shortest path from y to common Ancestor
    if (g == null) throw new IllegalArgumentException("sap: DigraphX is null");
    int V = g.V();
    if (x < 0 || x > V-1) throw new IllegalArgumentException("sap: x is out of bounds");
    if (y < 0 || y > V-1) throw new IllegalArgumentException("sap: y is out of bounds");
    DirectedCycleX cyclefinder = new DirectedCycleX(g);
    if (cyclefinder.hasCycle()) throw new IllegalArgumentException("sap: DigraphX isn't a DAG");  
    BreadthFirstDirectedPathsX a = new BreadthFirstDirectedPathsX(g, x);
    BreadthFirstDirectedPathsX b = new BreadthFirstDirectedPathsX(g, y);
    Seq<Integer> ancs = new Seq<>(); // seq of ancestors
    for (int i = 0; i < V; i++) if (a.hasPathTo(i) && b.hasPathTo(i)) ancs.add(i);
    int n = -1; // nearest ancestor
    int min = Integer.MAX_VALUE;
    for (int anc : ancs) {
      int d = a.distTo(anc) + b.distTo(anc);
      if (d < min) { min = d; n = anc; }
    }
    if (min == Integer.MAX_VALUE) return new Tuple3<>(-1,null,null);
    return new Tuple3<>(n,a.pathTo(n),b.pathTo(n));
  }

  public static void main(String[] args) {
    
    // edges are from tinyDAG.txt (http://algs4.cs.princeton.edu/42digraph/tinyDAG.txt)
    String edges = "2 3 0 6 0 1 2 0 11 12 9 12 9 10 9 11 3 5 8 7 5 4 0 5 6 4 6 9 7 6";
    DigraphX d = new DigraphX(13,edges);
    System.out.println(sap(d,0,8));

  }

}
