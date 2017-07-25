package graph;

import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.par;
import static v.ArrayUtils.range;

import java.util.Arrays;

// from http://algs4.cs.princeton.edu/44sp/LazyDijkstraSP.java

import java.util.Comparator;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.MinPQ;
import v.Tuple2;

public class LazyDijkstraSPX {
  private boolean[] marked;        // has vertex v been relaxed?
  private double[] distTo;         // distTo[v] = length of shortest s->v path
  private DirectedEdgeX[] edgeTo;   // edgeTo[v] = last edge on shortest s->v path
  private MinPQ<DirectedEdgeX> pq;  // PQ of fringe edges
  private EdgeWeightedDigraphX G;
  private int V;

  private class ByDistanceFromSource implements Comparator<DirectedEdgeX> {
    public int compare(DirectedEdgeX e, DirectedEdgeX f) {
      double dist1 = distTo[e.from()] + e.weight();
      double dist2 = distTo[f.from()] + f.weight();
      return Double.compare(dist1, dist2);
    }
  }
  
//  private class ByDistanceFromSource2 implements Comparator<DirectedEdgeX> {
//    public int compare(DirectedEdgeX e, DirectedEdgeX f) {
//      double dist1 = distTo[e.from()] + e.weight();
//      double dist2 = distTo[f.from()] + f.weight();
//      int c1 = Double.compare(dist1, dist2);
//      if (c1 == 0)  System.out.println("equal weights");
//      if (e.from() != f.from()) return Integer.compare(e.from(), f.from());
//      else if (e.to() != f.to()) return Integer.compare(e.to(), f.to());
//      else {
//        System.out.println("equal edges");
//        return 0;
//      }
//    }
//  }
//  
//  private class ByDistanceFromSource3 implements Comparator<DirectedEdgeX> {
//    public int compare(DirectedEdgeX e, DirectedEdgeX f) {
//      double dist1 = distTo[e.from()] + e.weight();
//      double dist2 = distTo[f.from()] + f.weight();
//      int c1 = Double.compare(dist1, dist2);
//      if (c1 == 0)  System.out.println("equal weights");
//      if (e.from() != f.from()) return Integer.compare(f.from(), e.from());
//      else if (e.to() != f.to()) return Integer.compare(f.to(), e.to());
//      else {
//        System.out.println("equal edges");
//        return 0;
//      }
//    }
//  }

  // single-source shortest path problem from s
  public LazyDijkstraSPX(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "LazyDijkstraSPX: EdgeWeightedDigraphX is null");
    G = g;
    V = G.V();
    for (DirectedEdgeX e : g.edges()) { 
      if (e.weight() < 0)
        throw new IllegalArgumentException("edge " + e + " has negative weight");
    }

    pq = new MinPQ<DirectedEdgeX>(new ByDistanceFromSource());
    marked = new boolean[V];
    edgeTo = new DirectedEdgeX[V];
    distTo = new double[V];

    // initialize
    for (int v = 0; v < V; v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    distTo[s] = 0.0;
    relax(G, s);

    // run Dijkstra's algorithm
    while (!pq.isEmpty()) {
      par(pq.toArray2());
      System.out.println("min = "+pq.min());
      DirectedEdgeX e = pq.delMin();
      @SuppressWarnings("unused")
      int v = e.from(), w = e.to();
      if (!marked[w]) relax(g, w);   // lazy, so w might already have been relaxed
    }

    // check optimality conditions
    assert check(g, s);
  }

  // relax vertex v
  private void relax(EdgeWeightedDigraphX G, int v) {
    marked[v] = true;
    for (DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (distTo[w] >= distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        pq.insert(e);
      }
    }
  }
  
  public void relaxEqualOrGreater(EdgeWeightedDigraphX G, int v) {
    marked[v] = true;
    for (DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (distTo[w] >= distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        pq.insert(e);
      }
    }
  }


  public double[] distTo() { return distTo; } ;

  // length of shortest path from s to v, infinity if unreachable
  public double distTo(int v) {
    return distTo[v];
  }
  
  public DirectedEdgeX[] edgeTo() { return edgeTo; }

  public NonWeightedDirectedEdgeX[] nwedgeTo() {
    NonWeightedDirectedEdgeX[] nwde = ofDim(NonWeightedDirectedEdgeX.class, edgeTo.length);
    for (int i = 0; i < edgeTo.length; i++) 
      if( edgeTo[i] == null) nwde[i] = null;
      else nwde[i] = edgeTo[i].toNonWeightedDirectedEdgeX();
    return nwde;
  }
  
  public void parentEdgeRep() {
    NonWeightedDirectedEdgeX[] nwde = nwedgeTo();
    for (int i = 0; i < nwde.length; i++) System.out.println(i+"|"+nwde[i]);
  }
  
  public String[] parentEdges() {
    NonWeightedDirectedEdgeX[] nwde = nwedgeTo();
    String[] r = new String[nwde.length];
    for (int i = 0; i < nwde.length; i++) r[i] = i+"|"+nwde[i];
    return r;
  }
  
  public Seq<DirectedEdgeX> sptEdges() { return new Seq<>(edgeTo); }


  // is there a path from s to v?
  public boolean hasPathTo(int v) {  
    return marked[v];
  }

  // return view of shortest path from s to v, null if no such path
  public Iterable<DirectedEdgeX> pathTo(int v) {
    if (!hasPathTo(v)) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
      path.push(e);
    }
    return path;
  }
  
  public Seq<DirectedEdgeX> seqTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo(v)) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
    return new Seq<>(path);
  }
  
  public Seq<Seq<DirectedEdgeX>> allPaths() {
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) paths.add(seqTo(i));
    return paths;
  }


  // check optimality conditions: either 
  // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
  // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
  private boolean check(EdgeWeightedDigraphX G, int s) {

    // all edge are nonnegative
    for (DirectedEdgeX e : G.edges()) {
      if (e.weight() < 0) {
        System.err.println("negative edge weight detected");
        return false;
      }
    }

    // check that distTo[v] and edgeTo[v] are consistent
    if (distTo[s] != 0.0 || edgeTo[s] != null) {
      System.err.println("distTo[s] and edgeTo[s] inconsistent");
      return false;
    }
    for (int v = 0; v < G.V(); v++) {
      if (v == s) continue;
      if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
        System.err.println("distTo[s] and edgeTo[s] inconsistent");
        return false;
      }
    }

    // check that all edges e = v->w satisfy dist[w] <= dist[v] + e.weight()
    for (int v = 0; v < G.V(); v++) {
      for (DirectedEdgeX e : G.adj(v)) {
        int w = e.to();
        if (distTo[v] + e.weight() < distTo[w]) {
          System.err.println("edge " + e + " not relaxed");
          return false;
        }
      }
    }

    // check that all edges e = v->w on SPT satisfy dist[w] == dist[v] + e.weight()
    for (int w = 0; w < G.V(); w++) {
      if (edgeTo[w] == null) continue;
      DirectedEdgeX e = edgeTo[w];
      int v = e.from();
      if (w != e.to()) return false;
      if (distTo[v] + e.weight() != distTo[w]) {
        System.err.println("edge " + e + " on shortest path not tight");
        return false;
      }
    }
    StdOut.println("Satisfies optimality conditions");
    StdOut.println();
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
//    In in = new In(args[0]);
//    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//
//    // print graph
//    StdOut.println("Graph");
//    StdOut.println("--------------");
//    StdOut.println(G);
//
//    System.exit(0);
//
//    // run Dijksra's algorithm from vertex 0
//    int s = 0;
//    LazyDijkstraSPX spt = new LazyDijkstraSPX(G, s);
//    StdOut.println();
//
//    StdOut.println("Shortest paths from " + s);
//    StdOut.println("------------------------");
//    for (int v = 0; v < G.V(); v++) {
//      if (spt.hasPathTo(v)) {
//        StdOut.printf("%d to %d (%.2f)  ", s, v, spt.distTo(v));
//        for (DirectedEdgeX e : spt.pathTo(v)) {
//          StdOut.print(e + "   ");
//        }
//        StdOut.println();
//      }
//      else {
//        StdOut.printf("%d to %d         no path\n", s, v);
//      }
//    }
    
/*  tinyEWD.txt 
    reverse 0->2 and make weight of 7->5 + 5->1 + 1->3= 7-3 == 0.39
    by weight 7->5 = 0.20, weight 5->1 = 0.09 and weight of 1->3 = 0.10
    8
    15
    4 5 0.35
    5 4 0.35
    4 7 0.37
    5 7 0.28
    7 5 0.28 change weight to 0.20
    5 1 0.32 change weight to 0.09
    0 4 0.38
    0 2 0.26 reverse to 2 0 0.26
    7 3 0.39
    1 3 0.29 change weight to 0.10
    2 7 0.34
    6 2 0.40
    3 6 0.52
    6 0 0.58
    6 4 0.93   

 
*/
    
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//    G.removeVertex(7);
    
    // reverse edge 0->2
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
      System.out.println("edge 0->2 replaced with 2->0");
    } else {
      System.out.println("could not find edge 0->2");
    }
    
    // change weight of 7->5 to 0.20
    x = G.findEdge(7, 5);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.20);
      System.out.println("edge(7,5,0.28) replaced with edge(7,5,0.20");
    } else {
      System.out.println("could not find edge 7->5");
    }
    // change weight of 5->1 to 0.09
    x = G.findEdge(5, 1);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.09);
      G.search();
      System.out.println("edge(5,1,0.32) replaced with edge(5,1,0.09");
    } else {
      System.out.println("could not find edge 5-1");
    }
    
    // change weight of 1->3 to 0.10
    x = G.findEdge(1, 3);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.from(),x.to(),0.10);
      G.search();
      System.out.println("edge(1,3,0.29) replaced with edge(1,3,0.10");
    } else {
      System.out.println("could not find edge 1->3");
    }
    
    
    int V = G.V();
    System.out.println("G.V = "+V);
//    G = G.reverse();

//    int s = Integer.parseInt(args[1]);
    int s = 2; // ex4405
//    double offset = -G.minWeight()+.01;
//    System.out.println("offset = "+offset);
//    double offset = 0;
    // compute shortest paths
    LazyDijkstraSPX sp = new LazyDijkstraSPX(G, s);
    
    System.out.println("edgeTo: ");
    for (DirectedEdgeX de : sp.edgeTo()) System.out.println(de);
    
    System.out.println("\ndistTo: ");
    for (double d : sp.distTo()) System.out.println(d);
    
    System.out.println("\nallPaths:");
    Seq<Seq<DirectedEdgeX>> allPaths = sp.allPaths();
    for (Seq<DirectedEdgeX> pth : allPaths) System.out.println(pth);

    System.out.println("\nparentEdgeRep:");
    sp.parentEdgeRep();
    

    System.out.println();
    
    // original
    //double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,92.5,77.5,70,37,85};
    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
    
//    double[][] coordAr2 = new double[V][2];
//    coordAr2[0] = new double[]{45,63.5};
//    coordAr2[1] = new double[]{42.5,81};
//    coordAr2[2] = new double[]{58,68};
//    coordAr2[3] = new double[]{58,79.5};
//    coordAr2[4] = new double[]{21.5,56};
//    coordAr2[5] = new double[]{21.6,79.5};
//    coordAr2[6] = new double[]{77.5,56};
//    coordAr2[7] = new double[]{37,71};

    System.out.println("V = "+V);
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);
    System.out.println("coords.size = "+coords.size());
    System.out.println("coords = "+coords);
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
//    String title = "tinyEWD.txt with vertex 7 removed|SPT from 0 (SPT edges are black)";
  String title = "tinyEWD.txt with edge 0-2 reversed|SPT from 2 (SPT edges are black)";
    System.out.println("sp.sptEdges() = "+sp.sptEdges());
    System.out.print("sp.parentEdges() = "); par(sp.parentEdges());
    E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title,E.midPoints());
 
 /* with 0->2 reversed
 edge 0->2 replaced with 2->0

edgeTo: 
2->0  0.26
5->1  0.32
null
7->3  0.39
0->4  0.38
7->5  0.28
3->6  0.52
2->7  0.34

    

 */ 
  }

}
