package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;
import v.Tuple2;

// modification of DijkstraSPX for one source-sink

@SuppressWarnings("unused")
public class DijkstraSourceSinkSP {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int sink;
  private int V;
  boolean quiet = false;
  private int relaxations = 0;

  public DijkstraSourceSinkSP(EdgeWeightedDigraphX g, int s, int t) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[source] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(source, distTo[source]);
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      if (v == sink) break;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSourceSinkSP(EdgeWeightedDigraphX g, int s, int t, String quiet) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    if (quiet.equals("quiet") || quiet.equals("q")) this.quiet = true;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[source] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(source, distTo[source]);
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      if (v == sink) break;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSourceSinkSP(EdgeWeightedDigraphX g, int s, int t, boolean altRelax) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    distTo[source] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(source, distTo[source]);
    if (altRelax)
      while (!pq.isEmpty()) {
        int v = pq.delMin();
        if (v == sink) break;
        for (DirectedEdgeX e : G.adj(v)) altRelax(e);
      }
    else 
      while (!pq.isEmpty()) {
        int v = pq.delMin();
        if (v == sink) break;
        for (DirectedEdgeX e : G.adj(v)) relax(e);
      }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSourceSinkSP(EdgeWeightedDigraphX g, int s, int t, double offset) {
    // for Ex4401, add offset to each edges weight before computing the SPT
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges()) {
      e.setW(e.w()+offset);
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight after applying offset");
    }
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    distTo[source] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(source, distTo[source]);
    while (!pq.isEmpty()) {
      Integer[] pqa = pq.toArray();
      double[] pqw = new double[pqa.length];
      for (int i = 0; i < pqa.length; i++) pqw[i] = pq.keyOf(pqa[i]);
//      par(pqa); par(pqw); System.out.println(); 
      int v = pq.delMin();
      if (v == sink) break;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }

  private void relax(DirectedEdgeX e) {
    // relax edge e using > and update pq if changed
    relaxations++;
    int from = e.from(), to = e.to();
    double p = distTo[from] + e.weight();
    if (distTo[to] > p) {
      distTo[to] = p;
      edgeTo[to] = e;
      if (pq.contains(to)) pq.decreaseKey(to, distTo[to]);
      else pq.insert(to, distTo[to ]);
    }
  }
  
  private void altRelax(DirectedEdgeX e) {
    // relax edge e using >= and update pq if changed
    int u = e.u(), v = e.v();
    if (distTo[v] >= distTo[u] + e.weight()) {
      distTo[v] = distTo[u] + e.weight();
      edgeTo[v] = e;
      if (pq.contains(v)) pq.changeKey(v, distTo[v]);
      else pq.insert(v, distTo[v]);
    }
  }
  
  public int relaxations() { return relaxations; }
  
  public double[] distTo() { return distTo; } ;
  
  public double distTo(int v) {
    // return length of shortest path from source to v 
    validateVertex(v);
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
  
  public boolean hasPathTo() {
    // return true if exists path from source to sink else false
    return distTo[sink] < Double.POSITIVE_INFINITY;
  }

  public boolean hasPathTo(int v) {
    // return true if exists path from source to v else false
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
  }
  
  public Iterable<DirectedEdgeX> pathTo() {
    // return a shortest path from source to sink if possible else returns null
    if (!hasPathTo()) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[sink]; e != null; e = edgeTo[e.from()])
      path.push(e);
    return path;
  }

  public Iterable<DirectedEdgeX> pathTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()])
      path.push(e);
    return path;
  }
  
  public Seq<DirectedEdgeX> seqTo() {
    // return a shortest path from source to sink if possible else return an empty Seq
    Seq<DirectedEdgeX> seq = new Seq<>();
    if (!hasPathTo()) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[sink]; e != null; e = edgeTo[e.from()]) path.push(e);
    return new Seq<>(path);
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

  private boolean check() {
    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    int s = source;
    // check that edge weights are nonnegative
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
    for (int v = 0; v < V; v++) {
      if (v == s) continue;
      if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
        System.err.println("distTo[] and edgeTo[] inconsistent");
        return false;
      }
    }

    // check that all edges e = u->v satisfy distTo[v] <= distTo[u] + e.weight()
//    for (int u = 0; u < V; u++) {
//      for (DirectedEdgeX e : G.adj(u)) {
//        int v = e.to();
//        if (distTo[u] + e.weight() < distTo[v]) {
//          System.err.println("edge " + e + " not relaxed");
//          return false;
//        }
//      }
//    }
    
    Seq<DirectedEdgeX> spath = seqTo();

    // check that all edges in seqTo() satisfy distTo[v] == distTo[u] + e.weight()
    for (int v = 0; v < V; v++) {
      if (edgeTo[v] == null) continue;
      DirectedEdgeX e = edgeTo[v];
      if (!spath.contains(e)) continue;
      int u = e.from();
      if (v != e.to()) return false;
      if (distTo[u] + e.w() != distTo[v]) {
        System.err.println("edge " + e + " on shortest path not tight");
        return false;
      }
    }
    if (!quiet) System.out.println("source-sink path satisfies optimality conditions");
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  public static void dispose(Draw[] draw) {
    System.out.println("enter any key to dispose of drawings");
    try {
      System.in.read();
      for (Draw d : draw) if (d != null) d.frame().dispose();
      System.exit(0);
    } catch (IOException e) {
      for (Draw d : draw)  if (d != null) d.frame().dispose();
      System.exit(0);
    }
  }
  
  public static void main(String[] args) {
    
//    In in = new In(args[0]);
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//    int source = Integer.parseInt(args[1]);
//    int sink = Integer.parseInt(args[2]);
    int source = 0, sink = 1;    
    // compute shortest paths
    DijkstraSourceSinkSP sp = new DijkstraSourceSinkSP(G, source, sink, "q");
    
    System.out.println("relaxations="+sp.relaxations());
    System.out.print("edgeTo="); par(sp.edgeTo);
    System.out.print("distTo="); par(sp.distTo);
    Seq<DirectedEdgeX> path = sp.seqTo();
    System.out.println("path = "+path);
    
    System.exit(0);

    // print source-sink path
    for (int t = 0; t < G.V(); t++) {
        if (t != sink) continue;
        if (sp.hasPathTo(t)) {
            StdOut.printf("%d to %d (%.2f)  ", source, t, sp.distTo(t));
            for (DirectedEdgeX e : sp.pathTo(t)) {
                StdOut.print(e + "   ");
            }
            StdOut.println();
        }
        else {
            StdOut.printf("%d to %d         no path\n", source, t);
        }
    }
    
    /*  tinyEWD.txt 
    reverse 0->2 and make weight of 7->5 + 5->1 + 1->3= 7-3 == 0.39
    by setting weight 7->5 = 0.20, weight 5->1 = 0.09 and weight of 1->3 = 0.10
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
//    In in = new In("tinyEWD.txt");
//    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
// 
//    // reverse edge 0->2
//    DirectedEdgeX x = G.findEdge(0, 2);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.reverse());
//      G.search();
//      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
//    } else {
//      System.out.println("could not find edge 0->2");
//    }
//    
//    // change weight of 7->5 to 0.20
//    x = G.findEdge(7, 5);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.20);
//      System.out.println("edge(7->5,0.28) replaced with edge(7->5,0.20");
//    } else {
//      System.out.println("could not find edge 7->5");
//    }
//    
//    // change weight of 5->1 to 0.09
//    x = G.findEdge(5, 1);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.09);
//      G.search();
//      System.out.println("edge(5->1,0.32) replaced with edge(5->1,0.09");
//    } else {
//      System.out.println("could not find edge 5->1");
//    }
//    
//    // change weight of 1->3 to 0.10
//    x = G.findEdge(1, 3);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.10);
//      G.search();
//      System.out.println("edge(1,3,0.29) replaced with edge(1,3,0.10");
//    } else {
//      System.out.println("could not find edge 1->3");
//    }
//    
//    int V = G.V();
//    System.out.println("\nEdgeWeightedDigraphX G:\n"+G);
//    
//    int source = 2; // ex4405 requirement
//    
//    // define coordinates for EuclidianEdgeWeightedDigraphs
//    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
//    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
//    for (int i = 0; i < 2*V-1; i+=2) 
//      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
//    
//    // define vertex labels for EuclidianEdgeWeightedDigraphs
//    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
//    
//    EuclidianEdgeWeightedDigraph E;
//    String title;
//    
//    Draw[] draw = new Draw[2];
//    
//    // using the normal relax() with > comparison
//    DijkstraSourceSinkSP sp1 = new DijkstraSourceSinkSP(G, source, false);    
//    System.out.println("SP edgeTo using normal relax(): ");
//    for (DirectedEdgeX de : sp1.edgeTo()) System.out.println(de);
//    E = new EuclidianEdgeWeightedDigraph(G,coords);
//    title = "modified TinyEWD.txt using normal relax()|SPT from 2 (SPT edges are black)";
//    draw[0] = E.showEx44040507(2.2,sp1.sptEdges(),labels,sp1.parentEdges(),title,E.midPoints());
//    System.out.println();
//    
//    // using altRelax() with >= comparison
//    DijkstraSourceSinkSP sp2 = new DijkstraSourceSinkSP(G, source, true);
//    System.out.println("edgeTo using altRelax(): ");
//    for (DirectedEdgeX de : sp2.edgeTo()) System.out.println(de);
//    E = new EuclidianEdgeWeightedDigraph(G,coords);
//    title = "modified TinyEWD.txt using altRelax()|SPT from 2 (SPT edges are black)";
//    draw[1] = E.showEx44040507(2.2,sp2.sptEdges(),labels,sp2.parentEdges(),title,E.midPoints());
//    
//    System.out.println();
//    dispose(draw);


  }

}
