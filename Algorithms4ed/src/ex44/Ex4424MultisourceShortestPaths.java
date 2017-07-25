package ex44;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import graph.DijkstraSPX;
import graph.DirectedEdgeX;
import graph.EdgeWeightedDigraphX;

/* p688
  4.4.24 Multisource shortest paths. Develop an API and implementation that uses 
  Dijkstra’s algorithm to solve the multisource shortest-paths problem on edge-
  weighted digraphs with positive edge weights: given a set of sources, find a 
  shortest-paths forest that enables implementation of a method that returns to 
  clients the shortest path from any source to each vertex. Hint : Add a dummy 
  vertex with a zero-weight edge to each source, or initialize the priority queue 
  with all sources, with their distTo[] entries set to 0.
  
  The first hint is implemented below in shortestPathsFromSet() that's demonstrated.
  
 */  

public class Ex4424MultisourceShortestPaths {
  
  public static Seq<Seq<Integer>> shortestPathsFromSet(EdgeWeightedDigraphX G, 
      Set<Integer> sources) {
    // using the first hint, for each vertex in G find a path to it from a nearest 
    // vertex in sources and return those paths as a Seq<Seq<Integer> ordered 
    // by terminal vertices
    if (G == null) throw new IllegalArgumentException(
        "shortestPathsFromSet: G is null");
    if (sources == null) throw new IllegalArgumentException(
        "shortestPathsFromSet: sources is null");
    int V = G.V();
    boolean valid = false;
    for (int i : sources) if (-1 < i && i < V) { valid = true; break; }
    if (!valid) throw new IllegalArgumentException("shortestPathsFromSet: no valid sources");
    G = new EdgeWeightedDigraphX(G); // preserve the original G
    G.addVertex(); // add vertex V
    for (int i : sources)  G.addEdge(V, i, 0);
    DijkstraSPX sp = new DijkstraSPX(G, V, "q");
    return sp.allPaths().init().map(x->x.tail())
        .map(x->x.isEmpty() ? new Seq<>() : convert(x));
  }
 
  public static Seq<Integer> convert(Iterable<DirectedEdgeX> edges) {
    // return Seq<Integer> of vertices in edges if it's a path else null
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    Integer lastTo = null;
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeX");
      if (lastTo != null && e.from() != lastTo.intValue())
        throw new IllegalArgumentException("convert: edges isn't a path");
      seq.add(e.from());
      seq.add(e.to());
      lastTo = e.to();
    }
    return seq.uniquePreservingOrder();
  }
  
  public static void pprint(Seq<Seq<Integer>> p) {
    // pretty-print p
    if (p == null) throw new IllegalArgumentException("pprint: Seq is null");
    for (int i = 0; i < p.size(); i++) {
      Seq<Integer> s = p.get(i);
      if (1 < s.size()) System.out.println(s.head()+">"+s.last()+": "+s.mkString(">"));
      else System.out.println(i+">"+i+": "+s);
    }  
  }
 
  public static void main(String[] args) {
    
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    Set<Integer> sources = new HashSet<>(Arrays.asList(1,3,5));
    
    pprint(shortestPathsFromSet(G,sources));
/*
    3>0: 3>6>0
    1>1: ()
    3>2: 3>6>2
    3>3: ()
    5>4: 5>4
    5>5: ()
    3>6: 3>6
    5>7: 5>7
*/ 
    
  }

}


