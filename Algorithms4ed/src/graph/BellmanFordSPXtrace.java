package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import analysis.Draw;
import ds.Queue;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;
import v.Tuple7;
import v.Tuple8;
import v.Tuple9;

@SuppressWarnings("unused")
public class BellmanFordSPXtrace {
  private double[] distTo;               // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;        // edgeTo[v] = last edge on shortest s->v path
  private boolean[] marked;              // true if weight != Double.POSITIVE_INFINITY
  private boolean[] onQueue;             // onQueue[v] = is v currently on the queue?
  private Queue<Integer> queue;          // queue of vertices to relax
  private int cost;                      // number of calls to relax()
  private Iterable<DirectedEdgeX> cycle; // negative cycle (or null if no such cycle)
  private EdgeWeightedDigraphX G;
  private int V;
  private int source;
  private transient Seq<DirectedEdgeX> proc;  // processed edges
  private transient Seq<DirectedEdgeX> inel;  // invalid edges
  private transient Seq<Integer> sptv;        // SPT vertices
  private Seq<Tuple8<Integer,Seq<Integer>,Seq<Integer>,Seq<DirectedEdgeX>,Seq<DirectedEdgeX>,DirectedEdgeX[],double[],String>> data;
  /*  Tuple8 fields in data Seq<Tuple8<>
   *  ref  type                contents
   *  _1   Integer             current vertex in relax call or processing tag if negative
   *  _2   Seq<Integer>        vertices in queue
   *  _3   Seq<Integer>        vertices in SPT
   *  _4   Seq<DirectedEdgeX>  processed edges (proc)
   *  _5   Seq<DirectedEdgeX>  ineligible edges (inel)
   *  _6   DirectedEdgeX[]     edgeTo
   *  _7   double[]            distTo
   *  _8   String              negative cycle
   */

  public BellmanFordSPXtrace(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "BellmanFordSPX: EdgeWeightedDigraphX == null");
    if (s < 0) throw new IllegalArgumentException(
        "BellmanFordSPX: source < 0");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    distTo  = new double[V];
    edgeTo  = new DirectedEdgeX[V];
    marked = new boolean[V];
    onQueue = new boolean[V];
    proc = new Seq<>();
    inel = new Seq<>();
    sptv = new Seq<>();
    for (int v = 0; v < V; v++)
      distTo[v] = Double.POSITIVE_INFINITY;
    queue = new Queue<Integer>();
    data = new Seq<>();
    data.add(new Tuple8<>(-(source+2),new Seq<Integer>(),sptv.clone(),proc.clone(),
        inel.clone(),edgeTo.clone(),distTo.clone(),null));
    distTo[s] = 0.0;
    queue.enqueue(s);
    onQueue[s] = true;
    sptv.add(s);
    data.add(new Tuple8<>(-1,new Seq<Integer>(queue.toArray(1)),sptv.clone(),
        proc.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),null));
    while (!queue.isEmpty() && !hasNegativeCycle()) {
      int v = queue.dequeue();
      onQueue[v] = false;
      relax(G, v);
    }
    assert check(G, s);
  }
  
  private String[] cedges(DirectedEdgeX[] dedges) {
    Function<DirectedEdgeX,String> dstr = (e)->{return (e == null) ? "null" : e.toString2();};
    if (allNull(dedges)) return fill(dedges.length,()->"null");
    else return map(dedges,dstr);
  }
  
  private void relax(EdgeWeightedDigraphX G, int u) {
    if (u != source) sptv.add(u);
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.to();
      if (distTo[v] > distTo[u] + e.weight()) {
        distTo[v] = distTo[u] + e.weight();
        if (edgeTo[v] != null && !inel.contains(edgeTo[v])) inel.add(edgeTo[v]);
        edgeTo[v] = e;
        if (!onQueue[v]) {
          queue.enqueue(v);
          onQueue[v] = true;
        }
      }     
      if (!proc.contains(e)) proc.add(e);      
      if (cost++ % G.V() == 0) findNegativeCycle();     
      if (hasNegativeCycle()) break;
    }
    String cyc = cycle == null ? null : str(cycle);  
    data.add(new Tuple8<>(u,new Seq<Integer>(queue.toArray(1)),sptv.clone(),
        proc.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),cyc));
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
  
  public String str(Iterable<DirectedEdgeX> s) {
    return convert(s).mkString(">");
  }

  public Seq<Integer> convert(Iterable<DirectedEdgeX> edges) {
    // return Seq<Integer> of vertices in edges in order
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeX");
      seq.add(e.from());
      seq.add(e.to());
    }
    seq = seq.uniquePreservingOrder();
    seq.add(seq.get(0));
    return seq;
  }
  
  public static void dispose(Seq<Draw> draw) {
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
  
  public static Seq<Draw> traceBellmanFord(String graph, int source) {
    if (graph == null) throw new IllegalArgumentException(
        "traceBellmanFord : graph String is null");
    if (source < 0 ) throw new IllegalArgumentException(
        "traceBellmanFord : source vertex < 0");
    In in = new In(graph);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    if (source > V-1 ) throw new IllegalArgumentException(
        "traceBellmanFord : source vertex < G.V()-1 == "+(V-1));    

//    System.out.println("G:\n"+G);

    BellmanFordSPXtrace B = new BellmanFordSPXtrace(G, source);
    
//    System.out.println("\nedgeTo: ");
//    for (DirectedEdgeX de : B.edgeTo()) System.out.println(de);
//
//    System.out.println("\ndistTo: ");
//    for (double d : B.distTo()) System.out.println(d);
   
    // define coordinates for EuclidianEdgeWeightedDigraph
    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);

    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
      
    Seq<Draw> drawings = new Seq<>();

    int c = 0;
    for (int i = 0; i < B.data.size()-1; i++)
      drawings.add(E.showBellmanFordSPXtrace(labels,B.data.get(i),"Step "+c++));
    drawings.add(E.showBellmanFordSPXtrace(labels,B.data.get(c),"Final"));
    return drawings;
  }
  
  public static Seq<Draw> traceBellmanFordTinyEWDReverse02Ex4421() {
    // specialized version of traceBellmanFord for ex4421
    String graph = "tinyEWD.txt";
    int source = 2;
    In in = new In(graph);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    if (source > V-1 ) throw new IllegalArgumentException(
        "traceBellmanFord : source vertex < G.V()-1 == "+(V-1));
    
    // reverse edge 0->2
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
//      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
    } else {
//      System.out.println("could not find edge 0->2");
    }

//    System.out.println("G:\n"+G);

    BellmanFordSPXtrace B = new BellmanFordSPXtrace(G, source);
    
//    System.out.println("\nedgeTo: ");
//    for (DirectedEdgeX de : B.edgeTo()) System.out.println(de);
//
//    System.out.println("\ndistTo: ");
//    for (double d : B.distTo()) System.out.println(d);
   
    // define coordinates for EuclidianEdgeWeightedDigraph
    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);

    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
      
    Seq<Draw> drawings = new Seq<>();

    int c = 0;
    for (int i = 0; i < B.data.size()-1; i++)
      drawings.add(E.showBellmanFordSPXtrace(labels,B.data.get(i),"Step "+c++));
    drawings.add(E.showBellmanFordSPXtrace(labels,B.data.get(c),"Final"));
    return drawings;
  }

  public static void main(String[] args) {
    
//    Seq<Draw> draw = traceBellmanFord("tinyEWD.txt", 1); 
//    dispose(draw);
    
    Seq<Draw> draw = traceBellmanFordTinyEWDReverse02Ex4421();
    dispose(draw);
    
    System.exit(0);

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
    
    In in = new In("mediumEWD.txt");
//    In in = new In("tinyEWD.txt");
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
    BellmanFordSPXtrace sp = new BellmanFordSPXtrace(G, s);
    if (sp.hasNegativeCycle()) {
      System.out.println("negative cycle = "+sp.str(sp.cycle));
      for (DirectedEdgeX e : sp.negativeCycle())
        StdOut.println(e.toString2());
    }
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
