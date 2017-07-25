package ex44;

import static v.ArrayUtils.*;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.38 Sensitivity. Develop an  SP client that performs a sensitivity analysis 
  on the edge-weighted digraph’s edges with respect to a given pair of vertices 
  s and t: Compute a V-by-V boolean matrix such that, for every v and w, the 
  entry in row v and column w is true if v->w is an edge in the edge-weighted 
  digraph whose weight can be increased without the shortest-path length from v 
  to w being increased and is false otherwise.

  I take this to mean that the entry in row v and column w of the boolean matrix 
  is false if there is no v->w edge.

  A solution is implemented below.


 */  

public class Ex4438SensitivityAnalysis {

  public static boolean[][] sensitivity(String ewg) {
    if (ewg == null) throw new IllegalArgumentException("String ewg is null");
    In in = new In(ewg);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V(); double w,w1,w2; DirectedEdgeX e;
    boolean[][] b =  ofDimBoolean(V,V);
    for (int u = 0; u < V; u++) {
      for (int v = 0; v < V; v++) {
        if (u == v) { b[u][u] = true; continue; }
        e = G.findEdge(u, v);
        if (e == null) { b[u][v] = false; continue; }
        w = e.w();
        w1 = weight((new DijkstraSourceSinkSP(G,u,v,"q")).seqTo());
        e.setW(w+1);
        w2 = weight((new DijkstraSourceSinkSP(G,u,v,"q")).seqTo());
        e.setW(w);
        b[u][v] = w2 > w1 ? false : true;
      }
    }
    return b;
  }

  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  public static void main(String[] args) {

    boolean[][] b = sensitivity("tinyEWD.txt");
    for (boolean[] x : b) par(x);
    /*
    [true,false,false,false,false,false,false,false]
    [false,true,false,false,false,false,false,false]
    [false,false,true,false,false,false,false,false]
    [false,false,false,true,false,false,false,false]
    [false,false,false,false,true,false,false,false]
    [false,false,false,false,false,true,false,false]
    [false,false,false,false,false,false,true,false]
    [false,false,false,false,false,false,false,true]
     */

    b = sensitivity("mediumEWD.txt");
    for (int i = 0; i < b.length; i++) {
      for (int j = 0; j < b[i].length; j++) {
        if (i != j && b[i][j] == true) System.out.printf("%-3d  %-3d  %s\n",i,j,"true");
        if (i == j && b[i][j] == false) System.out.printf("%-3d  %-3d  %s\n",i,j,"false");
      }
    }
    /*
    7    71   true
    20   194  true
    40   194  true
    71   7    true
    84   106  true
    84   192  true
    106  84   true
    110  207  true
    114  204  true
    134  145  true
    143  212  true
    144  204  true
    145  134  true
    156  207  true
    156  221  true
    192  84   true
    194  20   true
    194  40   true
    204  114  true
    204  144  true
    207  110  true
    207  156  true
    212  143  true
    221  156  true
  */
  }

}


