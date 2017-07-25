package ex44;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import exceptions.BotchedOperationException;
import graph.DijkstraSourceSinkSP;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p690
  4.4.37 Critical edges. Develop an algorithm for finding an edge whose removal 
  causes maximal increase in the shortest-paths length from one given vertex to 
  another given vertex in a given edge-weighted digraph.
  
  Methods for finding a critical edge by length and by weight are below.
  
 */  

public class Ex4437CriticalEdges {

  public static DirectedEdgeX criticalEdgeByLength(String ewg, int u, int v) {
    // assuming G has no negative cycles return an edge in it removal of 
    // which causes the greatest shortest path length increase from u to 
    // v or return null if no such edge can be found.
    if (ewg == null) throw new IllegalArgumentException("String ewg is null");
    In in = new In(ewg);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    if (u < 0 || u > G.V() -1) throw new IllegalArgumentException("u is out of bounds");
    if (v < 0 || v > G.V() -1) throw new IllegalArgumentException("v is out of bounds");
    // find shortest path from u to v and its size
    DijkstraSourceSinkSP S = new DijkstraSourceSinkSP(G,u,v,"q");
    Seq<DirectedEdgeX> sp = S.seqTo();
    if (sp.isEmpty()) return null;
    int spsize = sp.size();
    Seq<DirectedEdgeX> max = null, sp2;
    DirectedEdgeX r = null;
    boolean ok = false;
    for (DirectedEdgeX e : sp) {
      ok = G.removeEdge(e);
      if (!ok) throw new IllegalArgumentException("cannot remove edge "+e+" from graph");
      sp2 = (new DijkstraSourceSinkSP(G,u,v,"q")).seqTo();
      if (sp2.size() <= spsize) continue;
      if (max == null || sp2.size() > max.size()) { max = sp2; r = e; }
      G.addEdge(e);
    }
    return r;
  }
  
  public static DirectedEdgeX criticalEdgeByWeight(String ewd, int u, int v) {
    // assuming G has no negative cycles return an edge in it removal of 
    // which causes the greatest shortest path weight increase from u to 
    // v or return null if no such edge can be found.
    if (ewd == null) throw new IllegalArgumentException("String ewg is null");
    In in = new In(ewd);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    if (u < 0 || u > G.V() -1) throw new IllegalArgumentException("u is out of bounds");
    if (v < 0 || v > G.V() -1) throw new IllegalArgumentException("v is out of bounds");
    // find shortest path from u to v and its size
    DijkstraSourceSinkSP S = new DijkstraSourceSinkSP(G,u,v,"q");
    Seq<DirectedEdgeX> sp = S.seqTo();
    if (sp.isEmpty()) return null;
    double w = weight(sp);
    Seq<DirectedEdgeX> max = null, sp2;
    DirectedEdgeX r = null;
    boolean ok = false;
    for (DirectedEdgeX e : sp) {
      ok = G.removeEdge(e);
      if (!ok) throw new BotchedOperationException("cannot remove edge "+e+" from graph");
      sp2 = (new DijkstraSourceSinkSP(G,u,v,"q")).seqTo();
      if (sp2.isEmpty()) continue;
      double w2 = weight(sp2);
      if (w2 <= w) continue;
      if (max == null || w2 > weight(max)) { max = sp2; r = e; }
      G.addEdge(e);
    }
    return r;
  }
  
  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  public static void main(String[] args) {
    
    System.out.println(criticalEdgeByLength("mediumEWD.txt",99,17));
    System.out.println(criticalEdgeByWeight("mediumEWD.txt",99,17));
    /*
    99>6  0.11
    41>17  0.11
    */

  }

}


