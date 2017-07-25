package graph;

import static v.ArrayUtils.*;

import java.util.Arrays;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;
import v.Tuple2;

// Eager version of DijkstraSP from text p655 with modifications and additions 
// including use of DirectedEdgeX and EdgeWeightedDigraphX and a constuctor for 
// adding an offset to all edge weights plus supplementary methods from DijkstraSPX.
// note the variation in relax between this and DijkstraSPX.

@SuppressWarnings("unused")
public class DijkstraSPBX {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int V;

  public DijkstraSPBX(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraSPBX: EdgeWeightedDigraphX is null");
    G = g;
    source = s;
    V = g.V();
    edgeTo = new DirectedEdgeX[V];
    distTo = new double[V];
    pq = new IndexMinPQ<Double>(V);
    for (int v = 0; v < V; v++) distTo[v] = Double.POSITIVE_INFINITY;
    distTo[s] = 0.0;
    pq.insert(s, 0.0);
    while (!pq.isEmpty()) relax(g, pq.delMin());
  }
  
  public DijkstraSPBX(EdgeWeightedDigraphX g, int s, double offset) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraSPBX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges()) {
      e.setW(e.w()+offset);
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight after applying offset");
    }
    G = g;
    source = s;
    V = g.V();
    edgeTo = new DirectedEdgeX[V];
    distTo = new double[V];
    pq = new IndexMinPQ<Double>(V);
    for (int v = 0; v < V; v++) distTo[v] = Double.POSITIVE_INFINITY;
    distTo[s] = 0.0;
    pq.insert(s, 0.0);
    while (!pq.isEmpty()) relax(g, pq.delMin());
  }

  //  constructor from DijkstraSPX
  //  public DijkstraSPBX(EdgeWeightedDigraphX g, int s) {
  //    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
  //    for (DirectedEdgeX e : g.edges())
  //      if (e.weight() < 0) throw new IllegalArgumentException(
  //          "DijkstraSPX: edge " + e + " has negative weight");
  //    G = g;
  //    source = s;
  //    V = G.V();
  //    distTo = new double[V];
  //    edgeTo = new DirectedEdgeX[V];
  //    validateVertex(s);
  //    for (int v = 0; v < V; v++) distTo[v] = Double.POSITIVE_INFINITY;
  //    distTo[s] = 0.0;
  //    // relax vertices in order of distance from s
  //    pq = new IndexMinPQ<Double>(V);
  //    pq.insert(s, distTo[s]);
  //    while (!pq.isEmpty()) {
  //      int v = pq.delMin();
  //      for (DirectedEdgeX e : G.adj(v)) relax(e);
  //    }
  //    // check optimality conditions
  //    assert check();
  //  }
  //  

  private void relax(EdgeWeightedDigraphX G, int v) {
    for(DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (distTo[w] > distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
        else pq.insert(w, distTo[w]);
      }
    }
  }

  // relax from DijkstraSPX
  //  private void relax(DirectedEdgeX e) {
  //    // relax edge e and update pq if changed
  //    int u = e.u(), v = e.v();
  //    if (distTo[v] > distTo[u] + e.weight()) {
  //      distTo[v] = distTo[u] + e.weight();
  //      edgeTo[v] = e;
  //      if (pq.contains(v)) pq.decreaseKey(v, distTo[v]);
  //      else pq.insert(v, distTo[v]);
  //    }
  //  }

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
    StringBuilder sb = new StringBuilder();
    NonWeightedDirectedEdgeX[] nwde = nwedgeTo();
    String[] r = new String[nwde.length];
    for (int i = 0; i < nwde.length; i++) r[i] = i+"|"+nwde[i];
    return r;
  }

  public Seq<DirectedEdgeX> sptEdges() { return new Seq<>(edgeTo); }

  public boolean hasPathTo(int v) {
    // return true if exists path from source to v else false
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
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
    for (int u = 0; u < V; u++) {
      for (DirectedEdgeX e : G.adj(u)) {
        int v = e.to();
        if (distTo[u] + e.weight() < distTo[v]) {
          System.err.println("edge " + e + " not relaxed");
          return false;
        }
      }
    }

    // check that all edges e = u->v on SPT satisfy distTo[v] == distTo[u] + e.weight()
    for (int v = 0; v < V; v++) {
      if (edgeTo[v] == null) continue;
      DirectedEdgeX e = edgeTo[v];
      int u = e.from();
      if (v != e.to()) return false;
      if (distTo[u] + e.w() != distTo[v]) {
        System.err.println("edge " + e + " on shortest path not tight");
        return false;
      }
    }
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {

    //    In in = new In(args[0]);
    //    EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
    //    int s = Integer.parseInt(args[1]);
    //
    //    // compute shortest paths
    //    DijkstraSP sp = new DijkstraSP(G, s);
    //
    //
    //    // print shortest path
    //    for (int t = 0; t < G.V(); t++) {
    //        if (sp.hasPathTo(t)) {
    //            StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
    //            for (DirectedEdge e : sp.pathTo(t)) {
    //                StdOut.print(e + "   ");
    //            }
    //            StdOut.println();
    //        }
    //        else {
    //            StdOut.printf("%d to %d         no path\n", s, t);
    //        }
    //    }

    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    G.removeLastVertex();
    System.out.println("G.V = "+G.V());
    int V = G.V();
    //    G = G.reverse();
    int s =  0; //Integer.parseInt(args[1]);
    //    double offset = -G.minWeight()+.01;
    //    System.out.println("offset = "+offset);
    double offset = 0;
    // compute shortest paths
    DijkstraSPBX sp = new DijkstraSPBX(G, s, offset);

    System.out.println("\nedgeTo: ");
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
    String title = "tinyEWD.txt with vertex 7 removed|SPT from 0 (SPT edges are black)";
    System.out.println("sp.sptEdges() = "+sp.sptEdges());
    System.out.print("sp.parentEdges() = "); par(sp.parentEdges());
    E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title,E.midPoints());

  }

}
