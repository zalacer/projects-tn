package graph;

import static v.ArrayUtils.*;

import java.io.IOException;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;
import v.Tuple2;

// modification of DijkstraSPX for one source-sink in a EuclidianEdgeWeightedDigraph
// with edge weights based on Euclidian distances between vertices

public class DijkstraSourceSinkSPE {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;   // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EuclidianEdgeWeightedDigraph G;
  private Seq<Tuple2<Double,Double>> coords;
  private int source;
  private int sink;
  private int V;
  
  public DijkstraSourceSinkSPE(EuclidianEdgeWeightedDigraph E, int s, int t) {
    // assumes that E has euclidian weights
    if (E == null) throw new IllegalArgumentException(
        "DijkstraSPX: EuclidianEdgeWeightedDigraph is null");
    for (DirectedEdgeX e : E.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = E;
    V = E.V();
    coords = E.coords();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[source] = euclidianDistance(source,sink);
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(source, distTo[source]);
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      if (v == sink) break;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
  }  
  
  private void relax(DirectedEdgeX e) {
    // relax edge e using > and update pq if changed
    int from = e.u(), to = e.v();
    double p = distTo[from] + e.weight() + euclidianDistance(to,sink) - euclidianDistance(from,sink);
    if (distTo[to] > p) {
      distTo[to] = p;
      edgeTo[to] = e;
      if (pq.contains(to)) pq.decreaseKey(to, distTo[to]);
      else pq.insert(to, distTo[to]);
    }
  }
  
  public double[] distTo() { return distTo; } ;
  
  public double distTo(int v) {
    // return length of shortest path from source to v 
    validateVertex(v);
    return distTo[v];
  }
  
  public DirectedEdgeX[] edgeTo() { return edgeTo; }
  
  public EuclidianEdgeWeightedDigraph graph() { return G; };

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
    // return a shortest path from source to sink if possible else returns null
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
  
  public double euclidianDistance(int u, int v) {
    // return the Euclidian distance between u and v
    validateVertex(u,v);
    double x = coords.get(u)._1 - coords.get(v)._1; 
    double y = coords.get(u)._2 - coords.get(v)._2;
    return Math.sqrt(x*x + y*y);
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  @SafeVarargs
  private final void validateVertex(int...x) {
    if (x == null || x.length == 0) return;
    for (int i = 0; i < x.length; i++)
      if (x[i] < 0 || x[i] >= V)
        throw new IllegalArgumentException("validateVertex: vertex "+x[i]+" is out of bounds");
  }
  
  public void printSouceSinkPath() {
    for (int t = 0; t < G.V(); t++) {
      if (t != sink) continue;
      if (this.hasPathTo(t)) {
          StdOut.printf("shortest path from %d to %d: (%.2f) ", source, t, this.distTo(t));
          for (DirectedEdgeX e : this.pathTo(t)) {
              StdOut.print(e + "  ");
          }
          StdOut.println();
      }
      else StdOut.printf("no path from %d to %d\n", source, t);
    }
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
  
  public static void sourceSink(String EdgeWeightedDigraphXfileName, 
      double[] coordinates, int source, int sink) {
    if (EdgeWeightedDigraphXfileName == null) 
      throw new IllegalArgumentException("EdgeWeightedDigraphXfileName is null");
    if (coordinates == null) throw new IllegalArgumentException("coordinates is null");
    In in = new In(EdgeWeightedDigraphXfileName);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    if (source < 0 || source > V-1) throw new IllegalArgumentException("source is invalid");
    if (sink < 0 || sink > V-1) throw new IllegalArgumentException("sink is invalid");
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordinates[i],coordinates[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords,"euclidianWeights");
    DijkstraSourceSinkSPE sp = new DijkstraSourceSinkSPE(E, source, sink);
    // print the source-sink path or "no path"
    for (int t = 0; t < G.V(); t++) {
      if (t != sink) continue;
      if (sp.hasPathTo(t)) {
        System.out.printf("shortest path from %d to %d: (%.2f) ", source, t, sp.distTo(t));
          for (DirectedEdgeX e : sp.pathTo(t)) System.out.print(e + "  ");
          System.out.println();
      }
      else System.out.printf("no path from %d to %d\n", source, t);
    }
    // print the resulting edgeTo[] and distTo[] arrays
    String[] sedgeTo = map(sp.edgeTo(),x->x==null?"null":x.toString());
    System.out.println("\nvertex  edgeTo[]      distTo[]");
    for (int i = 0; i < V; i++) {
      System.out.printf("%-7d %-12s  %s\n", i, sedgeTo[i].toString(), sp.distTo[i]);
    }
    System.out.println();
  }
  
  public static void main(String[] args) {
    
//    In in = new In(args[0]);
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords,"euclidianWeights");
    System.out.println("E:\n"+E);
//    int source = Integer.parseInt(args[1]);
//    int sink = Integer.parseInt(args[2]);
    int source = 5;
    int sink = 0;
    
    // compute shortest paths
    DijkstraSourceSinkSPE sp = new DijkstraSourceSinkSPE(E, source, sink);
    
    EuclidianEdgeWeightedDigraph F = sp.graph();
    System.out.println("F:\n"+F);
    
    String[] sedgeTo = map(sp.edgeTo(),x->x==null?"null":x.toString());
        
//    par(sp.distTo());
//    par(sp.edgeTo());
//    System.out.println();
    
    System.out.println("\nvertex  edgeTo[]      distTo[]");
    for (int i = 0; i < V; i++) {
      System.out.printf("%-7d %-12s  %s\n", i, sedgeTo[i].toString(), sp.distTo[i]);
    }
    System.out.println();


    // print source-sink path
    for (int t = 0; t < G.V(); t++) {
        if (t != sink) continue;
        if (sp.hasPathTo(t)) {
            StdOut.printf("shortest path from %d to %d: (%.2f) ", source, t, sp.distTo(t));
            for (DirectedEdgeX e : sp.pathTo(t)) {
                StdOut.print(e + "  ");
            }
            StdOut.println();
        }
        else {
            StdOut.printf("no path from %d to %d\n", source, t);
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
//    EuclidianEdgeWeightedDigraph G = new EuclidianEdgeWeightedDigraph(in);
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
//    System.out.println("\nEuclidianEdgeWeightedDigraph G:\n"+G);
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
