package graph;

import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.HashMap;

import ds.Queue;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;

// based on http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BellmanFordSP.java
// with DirectedEdgeX substituted for DirectedEdge

@SuppressWarnings("unused")
public class BellmanFordSPX {
  private double[] distTo;               // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;         // edgeTo[v] = last edge on shortest s->v path
  private boolean[] onQueue;             // onQueue[v] = is v currently on the queue?
  private Queue<Integer> queue;          // queue of vertices to relax
  private int cost;                      // number of calls to relax()
  private Iterable<DirectedEdgeX> cycle;  // negative cycle (or null if no such cycle)
  private EdgeWeightedDigraphX G;
  private int V;
  private int source;
  private long relaxations = 0;

  public BellmanFordSPX(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "BellmanFordSPX: EdgeWeightedDigraphX is null");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo  = fillDouble(V,()-> Double.POSITIVE_INFINITY);
    distTo[s] = 0.0;
    edgeTo  = new DirectedEdgeX[V];
    onQueue = new boolean[V];
    // Bellman-Ford algorithm
    queue = new Queue<Integer>();
    queue.enqueue(s);
    onQueue[s] = true;
    while (!queue.isEmpty() && !hasNegativeCycle()) {
      int v = queue.dequeue();
      onQueue[v] = false;
      relax(G, v);
    }
    assert check(G, s);
  }
  
  public BellmanFordSPX(EdgeWeightedDigraphX g) {
    // ex4443 
    if (g == null) throw new IllegalArgumentException(
        "BellmanFordSPX: EdgeWeightedDigraphX is null");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    distTo  = new double[V];
    edgeTo  = new DirectedEdgeX[V];
    onQueue = new boolean[V];
    queue = new Queue<Integer>();
    for (int v = 0; v < V; v++) {
      queue.enqueue(v);
      onQueue[v] = true;
    }   
    while (!queue.isEmpty() && !hasNegativeCycle()) {
      int v = queue.dequeue();
      onQueue[v] = false;
      relax(G, v);
    }
  }

  public BellmanFordSPX(EdgeWeightedDigraphX g, int s, boolean iterative) {
    // ex4444 
    if (g == null) throw new IllegalArgumentException(
        "BellmanFordSPX: EdgeWeightedDigraphX is null");
    if (!iterative) return;
    G = new EdgeWeightedDigraphX(g);
    V = G.V(); int E = G.E();
    distTo  = fillDouble(V,()->Double.POSITIVE_INFINITY);
    distTo[s] = 0.0;
    boolean relaxed = false;
    edgeTo  = new DirectedEdgeX[V];
    for (int i = 0; i < V; i++) {
      relaxed = false;
      for (DirectedEdgeX e : G.edges()) {
        relaxations++;
        int u = e.from(), v = e.to(); double w = e.w();
        if (distTo[v] > distTo[u] + w) {
          distTo[v] = distTo[u] + w;
          edgeTo[v] = e;
          relaxed = true;
        }
      }
      if (!relaxed) break;
    }
    System.out.println("V="+V+" E="+E);
    findNegativeCycle();
    if (hasNegativeCycle()) {
      assert relaxations == V*E;
      System.out.println("relaxations = "+relaxations+" == V*E");
      System.out.println("negative cycle = "+str(cycle));
    }
    else {
      System.out.println("relaxations="+relaxations());
      System.out.println("no negative cycle");
      System.out.println("shortest paths from source "+s+":");
      int i = -1;
      for (Seq<DirectedEdgeX> p : allPaths()) {
        if (++i == s) continue;
        else if (p.isEmpty()) System.out.println(s+"->"+i+": ()");
        else System.out.println(s+"->"+i+": "+strinit(p));
      }
    }
  }
  
  // relax vertex v and put other endpoints on queue if changed
  private void relax(EdgeWeightedDigraphX G, int v) {
    relaxations++;
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
//        if (hasNegativeCycle()) return;  // found a negative cycle
      }
    }
  }
  
  

  public boolean hasNegativeCycle() {
    return cycle != null;
  }

  public long relaxations() { return relaxations; }

  public Iterable<DirectedEdgeX> negativeCycle() {
    return cycle;
  }
  
  public Iterable<DirectedEdgeX> cycle() {
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
  
  public String strinit(Iterable<DirectedEdgeX> s) {
    return convert(s).init().mkString(">");
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
  
  public static Seq<Integer> convert2(Iterable<DirectedEdgeX> edges) {
    // return Seq<Integer> of vertices in edges if it's a path else null
    if (edges == null) throw new IllegalArgumentException("convert2: edges is null");
    Seq<Integer> seq = new Seq<>();
    Integer lastTo = null;
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert2: edges contains a null DirectedEdgeX");
      if (lastTo != null && e.from() != lastTo.intValue())
        throw new IllegalArgumentException("convert2: edges isn't a path");
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
  
  @SafeVarargs
  public static final Seq<Seq<Seq<Integer>>> allPairsJohnson(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    BellmanFordSPX B; Seq<DirectedEdgeX> edges;
    EdgeWeightedDigraphX H; DijkstraSPX D;
    Seq<Seq<Integer>> allp;
    for (int i = 0; i < V; i++) {
      B = new BellmanFordSPX(G,i);
      if (B.hasNegativeCycle()) throw new IllegalArgumentException(
          "negative cycle detected for source vertex "+i);
      edges = new Seq<>();
      for (DirectedEdgeX d : G.edges()) 
        edges.add(new DirectedEdgeX(d.u(),d.v(),d.w()+B.distTo[d.u()]-B.distTo[d.v()]));
      H = new EdgeWeightedDigraphX(edges);
      D = new DijkstraSPX(H,i,"q");
      allp = D.allPaths().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      s.add(allp);
      if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
        System.out.println();
        pprint(allp);  
      }
    }
    return s;
  }
  
  

  public static void main(String[] args) {

//        In in = new In(args[0]);
        In in = new In("tinyEWD.txt");
//        int s = Integer.parseInt(args[1]);
        int s = 0;
        EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//        allPairsJohnson(G);
//        System.out.println("V="+G.V()+" E="+G.E());
    
        BellmanFordSPX sp = new BellmanFordSPX(G,0,true);
//        System.out.println("relaxations="+sp.relaxations());
//        
//        sp.findNegativeCycle();
//        if (sp.hasNegativeCycle()) 
//          System.out.println("negative cycle = "+sp.str(sp.cycle));
//        else System.out.println("no negative cycle");
    
//        // print negative cycle
//        if (sp.hasNegativeCycle()) {
//          System.out.println("negative cycle:");
//          System.out.println(sp.str(sp.cycle));
//          for (DirectedEdgeX e : sp.negativeCycle())
//            StdOut.println(e);
//        }
//    
//        // print shortest paths
//        else {
//          for (int v = 0; v < G.V(); v++) {
//            if (sp.hasPathTo(v)) {
//              StdOut.printf("%d to %d (%5.2f)  ", s, v, sp.distTo(v));
//              for (DirectedEdgeX e : sp.pathTo(v)) {
//                StdOut.print(e + "   ");
//              }
//              StdOut.println();
//            }
//            else {
//              StdOut.printf("%d to %d           no path\n", s, v);
//            }
//          }
//        }
//        
//        System.out.println("relaxations = "+sp.relaxations());
    
//    In in = new In("tinyEWD.txt");
//    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
////    G.removeVertex(7);
//    DirectedEdgeX x = G.findEdge(0, 2);
//    if (x != null) G.removeEdge(x); G.addEdge(x.reverse());
//    System.out.println("G.V = "+G.V());
//    int V = G.V();
////    G = G.reverse();
////    int s = Integer.parseInt(args[1]);
//    int s = 2; // ex4405
////    double offset = -G.minWeight()+.01;
////    System.out.println("offset = "+offset);
////    double offset = 0;
//    // compute shortest paths
//    BellmanFordSPX sp = new BellmanFordSPX(G, s);
//    
//    System.out.println("\nedgeTo: ");
//    for (DirectedEdgeX de : sp.edgeTo()) System.out.println(de);
//    
//    System.out.println("\ndistTo: ");
//    for (double d : sp.distTo()) System.out.println(d);
//    
////    System.out.println("\nallPaths:");
////    Seq<Seq<DirectedEdgeX>> allPaths = sp.allPaths();
////    for (Seq<DirectedEdgeX> pth : allPaths) System.out.println(pth);
//
//    System.out.println("\nparentEdgeRep:");
//    sp.parentEdgeRep();
//    
//
//    System.out.println();
//    
//    // original
//    //double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,92.5,77.5,70,37,85};
//    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
//    
////    double[][] coordAr2 = new double[V][2];
////    coordAr2[0] = new double[]{45,63.5};
////    coordAr2[1] = new double[]{42.5,81};
////    coordAr2[2] = new double[]{58,68};
////    coordAr2[3] = new double[]{58,79.5};
////    coordAr2[4] = new double[]{21.5,56};
////    coordAr2[5] = new double[]{21.6,79.5};
////    coordAr2[6] = new double[]{77.5,56};
////    coordAr2[7] = new double[]{37,71};
//
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
