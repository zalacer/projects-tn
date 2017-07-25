package graph;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import ds.Queue;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;
import v.Tuple7;
import v.Tuple8;
import v.Tuple9;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BellmanFordSP.java

@SuppressWarnings("unused")
public class BellmanFordSPXtrace2 {
  private double[] distTo;               // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;         // edgeTo[v] = last edge on shortest s->v path
  private boolean[] onQueue;             // onQueue[v] = is v currently on the queue?
  private Queue<Integer> queue;          // queue of vertices to relax
  private int cost;                      // number of calls to relax()
  private Iterable<DirectedEdgeX> cycle;  // negative cycle (or null if no such cycle)
  private EdgeWeightedDigraphX G;
  private int V;
  private int source;
  private transient Seq<DirectedEdgeX> spte;  // SPT edges
  private transient Seq<DirectedEdgeX> inel;  // invalid edges
  private transient Seq<Integer> sptv;        // SPT vertices
  private Seq<Tuple7<Integer,Seq<Integer>,Seq<Integer>,Seq<DirectedEdgeX>,Seq<DirectedEdgeX>,DirectedEdgeX[],double[]>> data;
  /*  Tuple7 fields in data Seq<Tuple7<>
   *  id  type                contents
   *  1   Integer             drawing index (starting at -2)
   *  2   Seq<Integer>        vertices in queue
   *  3   Seq<Integer>        vertices in SPT
   *  4   Seq<DirectedEdgeX>  edges in SPT (spte)
   *  5   Seq<DirectedEdgeX>  ineligible edges (inel)
   *  6   DirectedEdgeX[]     edgeTo
   *  7   double[]            distTo
   */

  public BellmanFordSPXtrace2(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "BellmanFordSPX: EdgeWeightedDigraphX is null");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    distTo  = new double[V];
    edgeTo  = new DirectedEdgeX[V];
    onQueue = new boolean[V];
    for (int v = 0; v < V; v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    // Bellman-Ford algorithm
    queue = new Queue<Integer>();
    data.add(new Tuple7<>(-2,new Seq<Integer>(),sptv.clone(),
        spte.clone(),inel.clone(),edgeTo.clone(),distTo.clone()));
    distTo[s] = 0.0;
    queue.enqueue(s);
    onQueue[s] = true;
    data.add(new Tuple7<>(-2,new Seq<Integer>(queue.toArray(1)),sptv.clone(),
        spte.clone(),inel.clone(),edgeTo.clone(),distTo.clone()));
    System.out.println("START");
    //DirectedEdgeX[] cedges = null, pedges = null;
    while (!queue.isEmpty() && !hasNegativeCycle()) {
//      System.out.printf("%-17s  %s\n", "queue="+arrayToString(queue.toArray(),9000,99,99),
//          "  edgeTo="+arrayToString(cedges(edgeTo),9000,99,99));
//      System.out.println(arrayToString(cedges(edgeTo),9000,99,99));
//      cedges = edgeTo.clone();
//      if (pedges != null) {
//        for (int i = 0; i < V; i++) {
//          if (pedges[i] != null && cedges[i] != null && !pedges[i].equals(cedges[i])) {
//            System.out.println(i+", "+pedges[i].toString2()+", "+cedges[i].toString2());
//          }
//        }
//      }
//      pedges = cedges;
      int v = queue.dequeue();
      onQueue[v] = false;
      relax(G, v);
    }
    assert check(G, s);
  }
  
//  private String[] cedges(DirectedEdgeX[] dedges) {
//    Function<DirectedEdgeX,String> dstr = (e)->{return (e == null) ? "null" : e.toString2();};
//    if (allNull(dedges)) return fill(dedges.length,()->"null");
//    else return map(dedges,dstr);
//  }
  
  

  // relax vertex v and put other endpoints on queue if changed
  private void relax(EdgeWeightedDigraphX G, int v) {
    // find shortest edge from v
    for (DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (distTo[w] > distTo[v] + e.weight()) {
        distTo[w] = distTo[v] + e.weight();
        edgeTo[w] = e;
        if (!onQueue[w]) {
          queue.enqueue(w);
          onQueue[w] = true;
        }
      }
      if (cost++ % G.V() == 0) {
        findNegativeCycle();
        if (hasNegativeCycle()) return;  // found a negative cycle
      }
    }
  }

  public boolean hasNegativeCycle() {
    return cycle != null;
  }


  public Iterable<DirectedEdgeX> negativeCycle() {
    return cycle;
  }
  
  public Seq<DirectedEdgeX> ncycle() { 
    Iterable<DirectedEdgeX> it = negativeCycle();
    if (it == null) return new Seq<DirectedEdgeX>();
    return new Seq<DirectedEdgeX>(it); }
  
  public double ncycleWeight() {
    Iterable<DirectedEdgeX> it = negativeCycle();
    if (it == null)  return Double.NaN;
    double w = 0;
    for (DirectedEdgeX e : it) w += e.w();
    return w;
  }
  
  public double ncycleWeight2() {
    Iterable<DirectedEdgeX> it = negativeCycle();
    if (it == null)  return Double.NaN;
    int w = 0;
    for (DirectedEdgeX e : it) {
      int w2 = (int) e.w()*100;
      if (w2 == -w) w = 0;
      else w += w2;
    }
    return 1.*w/100;
  }

  private void findNegativeCycle() {
    int V = edgeTo.length;
    EdgeWeightedDigraphX spt = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < V; v++)
      if (edgeTo[v] != null)
        spt.addEdge(edgeTo[v]);

    EdgeWeightedDirectedCycleX finder = new EdgeWeightedDirectedCycleX(spt);
    cycle = finder.cycle();
  }

  public double[] distTo() { return distTo; } ;

  public double distTo(int v) {
    validateVertex(v);
    if (hasNegativeCycle())
      throw new UnsupportedOperationException("Negative cost cycle exists");
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


  public boolean hasPathTo(int v) {
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
  }


  public Iterable<DirectedEdgeX> pathTo(int v) {
    validateVertex(v);
    if (hasNegativeCycle())
      throw new UnsupportedOperationException("Negative cost cycle exists");
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



  private boolean check(EdgeWeightedDigraphX G, int s) {
    // has a negative cycle
    if (hasNegativeCycle()) {
      double weight = 0.0;
      for (DirectedEdgeX e : negativeCycle()) {
        weight += e.weight();
      }
      if (weight >= 0.0) {
        System.err.println("error: weight of negative cycle = " + weight);
        return false;
      }
    }
    // no negative cycle reachable from source
    else {
      // check that distTo[v] and edgeTo[v] are consistent
      if (distTo[s] != 0.0 || edgeTo[s] != null) {
        System.err.println("distanceTo[s] and edgeTo[s] inconsistent");
        return false;
      }
      for (int v = 0; v < G.V(); v++) {
        if (v == s) continue;
        if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
          System.err.println("distTo[] and edgeTo[] inconsistent");
          return false;
        }
      }
      // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
      for (int v = 0; v < G.V(); v++) {
        for (DirectedEdgeX e : G.adj(v)) {
          int w = e.to();
          if (distTo[v] + e.weight() < distTo[w]) {
            System.err.println("edge " + e + " not relaxed");
            return false;
          }
        }
      }
      // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
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
    }
    //        StdOut.println("Satisfies optimality conditions");
    //        StdOut.println();
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {

    //    In in = new In(args[0]);
    //    int s = Integer.parseInt(args[1]);
    //    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    //
    //    BellmanFordSPX sp = new BellmanFordSPX(G, s);
    //
    //    // print negative cycle
    //    if (sp.hasNegativeCycle()) {
    //      for (DirectedEdgeX e : sp.negativeCycle())
    //        StdOut.println(e);
    //    }
    //
    //    // print shortest paths
    //    else {
    //      for (int v = 0; v < G.V(); v++) {
    //        if (sp.hasPathTo(v)) {
    //          StdOut.printf("%d to %d (%5.2f)  ", s, v, sp.distTo(v));
    //          for (DirectedEdgeX e : sp.pathTo(v)) {
    //            StdOut.print(e + "   ");
    //          }
    //          StdOut.println();
    //        }
    //        else {
    //          StdOut.printf("%d to %d           no path\n", s, v);
    //        }
    //      }
    //    }
    
//    In in = new In("tinyEWD.txt");
    In in = new In("tinyEWDnc.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//    G.removeVertex(7);
    // reverse 0->2
//    DirectedEdgeX x = G.findEdge(0, 2);
//    if (x != null) G.removeEdge(x); G.addEdge(x.reverse());
//    System.out.println("G:\n"+G);
//    System.out.println("V = "+G.V());
    int V = G.V();
//    G = G.reverse();
//    int s = Integer.parseInt(args[1]);
//    int s = 2; // ex4405
    int s = 0;
//    double offset = -G.minWeight()+.01;
//    System.out.println("offset = "+offset);
//    double offset = 0;
    // compute shortest paths
    BellmanFordSPXtrace2 sp = new BellmanFordSPXtrace2(G, s);
    if (sp.hasNegativeCycle()) {
      System.out.println("hasNegativeCycle");
      for (DirectedEdgeX e : sp.negativeCycle())
        StdOut.println(e.toString2());
    }
//    System.out.println("\nedgeTo: ");
//    for (DirectedEdgeX de : sp.edgeTo()) System.out.println(de);
//    
//    System.out.println("\ndistTo: ");
//    for (double d : sp.distTo()) System.out.println(d);
    
//    System.out.println("\nallPaths:");
//    Seq<Seq<DirectedEdgeX>> allPaths = sp.allPaths();
//    for (Seq<DirectedEdgeX> pth : allPaths) System.out.println(pth);

//    System.out.println("\nparentEdgeRep:");
//    sp.parentEdgeRep();
//    
//
//    System.out.println();
    
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

//    System.out.println("V = "+V);
//    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
//    for (int i = 0; i < 2*V-1; i+=2) 
//      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
//    
//    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);
//    System.out.println("coords.size = "+coords.size());
//    System.out.println("coords = "+coords);
//    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
////    String title = "tinyEWD.txt with vertex 7 removed|SPT from 0 (SPT edges are black)";
//  String title = "tinyEWD.txt with edge 0-2 reversed|SPT from 2 (SPT edges are black)";
//    System.out.println("sp.sptEdges() = "+sp.sptEdges());
//    System.out.print("sp.parentEdges() = "); par(sp.parentEdges());
//    E.showEx44040507(2.2,sp.sptEdges(),labels,sp.parentEdges(),title, E.midPoints());
    

  }

}
