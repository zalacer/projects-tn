package ex44;

import static v.ArrayUtils.range;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p689 
  4.4.36 Neighbors. Develop an SP client that finds all vertices within a given 
  distance d of a given vertex in a given edge-weighted digraph. The running time 
  of your method should be proportional to the size of the subgraph induced by 
  those vertices and the vertices incident on them, or V (to initialize data 
  structures), whichever is larger.
  
  I'm assuming that "distance" means weight.
  This is done below optimized for code clarity and convenience vs. "performance".

 */  

public class Ex4436Neighbors {

  public static Seq<Integer> neighbors(String ewd, int v, double w) {
    // assuming G has no negative cycles return a sequence all vertices in G that
    // are within weight w >= 0 of v. i.e return the terminal vertices of paths  
    // from v to other vertices with total weight < w  and the source vertices of 
    // paths from other vertices to v with total weight < w .
    if (ewd == null) throw new IllegalArgumentException("String ewd is null");
    In in = new In(ewd);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    if (v < 0 || v > G.V() -1) throw new IllegalArgumentException("v is out of bounds");
    if (w < 0) throw new IllegalArgumentException("w is out of bounds");
    Seq<Integer> r = new Seq<>();
    // from v as source find shortest paths within w
    DijkstraSPX D = new DijkstraSPX(G,v,"q");
    Seq<Seq<DirectedEdgeX>> p = D.allPaths();
    for (Seq<DirectedEdgeX> s : p) 
      if (s != null && !s.isEmpty() && weight(s) < w) r.add(s.last().to());
    // from all other vertices find paths to v within w
    for (int i : range(0,G.V())) {
      if (i == v) continue;
      DijkstraSourceSinkSP S = new DijkstraSourceSinkSP(G,i,v,"q");
      Seq<DirectedEdgeX> s = S.seqTo();
      if (s != null && !s.isEmpty() && weight(s) < w) r.add(i); 
    }
    return r.unique(); 
  }

  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  public static void main(String[] args) {
    double[] a = {.1,.3,.4,.65,.8,.9,1.,1.7};
    for (double w : a) System.out.println(neighbors("tinyEWD.txt",1,w));
    /*
    ()
    (3)
    (3,5)
    (3,5,7)
    (3,4,5,7)
    (3,4,5,6,7)
    (2,3,4,5,6,7)
    (0,2,3,4,5,6,7)
    */

  }

}


