package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import analysis.Draw;
import ds.Queue;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.MinPQ;

/* for ex4318, based on LazyPrimMST from text p619
   1. show initial graph, list of vertices and list of edges
   2. after initial visit() and subsequently after each while 
      loop iteration, show:
      1. updated graph 
         1. vertices marked as in or not in MST
         2. edges marked as at top of MinPQ, in MST, crossing, 
            ineligible or unprocessed
      2. updated minPQ
         1. edges marked as new, ineligible or unmarked (crossing)
      3. list of MST vertices
      4. list of MST edges
   3. 
 
*/
@SuppressWarnings("unused")
public class LazyPrimMSTtrace {
  private boolean[] marked; // MST vertices
  private Queue<EdgeY> mst; // MST edges
  private Seq<String> mste;   // MST edges
  private Seq<String> mstv;   // MST vertices
  private Seq<EdgeY> ppq;  // previous pq
  private MinPQ<EdgeY> pq; // crossing (and ineligible) edges
  private Seq<Seq<String>> out; // trace output
  private int V;
  private int E;
  private transient EdgeWeightedGraphY G;
  private transient GraphX g;

  public LazyPrimMSTtrace(EdgeWeightedGraphY G) {
    if (G == null) throw new IllegalArgumentException(
        "LazyPrimMSTtrace: EdgeWeightedGraphY is null");
    if (!G.isConnected()) throw new IllegalArgumentException(
        "LazyPrimMSTtrace: EdgeWeightedGraphY isn't connected");
    this.G = G;
    V = G.V();
    E = G.E();
    g = G.graph();
    out = new Seq<Seq<String>>();
    pq = new MinPQ<EdgeY>();
    marked = new boolean[G.V()];
    mst = new Queue<EdgeY>();
    mste = new Seq<String>();
    mstv = new Seq<String>();
    ppq = new Seq<EdgeY>();
    Seq<EdgeY> cpq;
    Seq<String> cpqs;
    Seq<String> cout = new Seq<>();
    cout.addArray("","","");
    cout.add("Graph vertices: "+(Seq.range(0,V).toString()));
    if (E == 0) cout.add("Graph edges: ()");
    else {
      cout.add("Graph edges: ");
      Seq<EdgeY> edgs = new Seq<>(toArray(G.edges().iterator()));
      Seq<String> edges = new Seq<>();
      for (EdgeY e : edgs) edges.add(e.toString4());
      Iterator<Seq<String>> it = edges.reverseGrouped(4);
      while (it.hasNext()) {
        String x = it.next().toString().replaceAll(",",", ");
        cout.add(x.substring(1,x.length()-1));
      }
    }
    cout.add("");
    cout.add("MST vertices: "+mstv.toString().replaceAll(",",", "));
    cout.add("MST edges:    "+mste.toString().replaceAll(",",", "));
    out.add(cout);
    visit(G, 0); // assumes G is connected (see Exercise 4.3.22)
    while (!pq.isEmpty()) {
      cpq = new Seq<EdgeY>(pq.toArray2());
      cpqs = new Seq<String>();
      if (pq.isEmpty()) cpqs.add(""); 
      else for (EdgeY e : pq) cpqs.add(e.toString3());
      cout = new Seq<String>();
      cout.add(cpqs.toString());
      if (mstv.isEmpty()) cout.add("");
      else cout.add(mstv.toString());
      if (mste.isEmpty()) cout.add("");
      else cout.add(mste.toString());
      cout.add("MinPQ: (+ new; x ineligible)");
      for (EdgeY x : pq.toArray2()) {
        if (!ppq.contains(x)) cout.add("+ "+x.toString4());
        else if (marked[x.u()] && marked[x.v()]) cout.add("x "+x.toString4()); 
        else cout.add("   "+x.toString4());
      }
      ppq = cpq;
      cout.add("");
      cout.add("MST vertices: "+mstv.toString().replaceAll(",",", "));
      cout.add("MST edges:    "+mste.toString().replaceAll(",",", "));
      out.add(cout);
      EdgeY e = pq.delMin(); // Get lowest-weight
      int v = e.either(), w = e.other(v); // edge from pq.
      if (marked[v] && marked[w]) continue; // Skip if ineligible.
      mst.enqueue(e); // Add edge to MST.
      mste.add(e.toString3());
      if (!marked[v]) visit(G, v); // Add vertex to tree
      if (!marked[w]) visit(G, w); // (either v or w).
    }
  }

  private void visit(EdgeWeightedGraphY G, int v) { 
    // mark v and add to pq all edges from v to unmarked vertices.
    marked[v] = true; // put vertex on the mst
    mstv.add(""+v);
    for (EdgeY e : G.adj(v)) if (!marked[e.other(v)]) pq.insert(e);
  }
  
  public Iterable<EdgeY> edges()  { return mst; }
  
  public double weight() { // See Exercise 4.3.31.
    // from PrimMST
    double weight = 0.0;
    for (EdgeY e : edges()) weight += e.weight();
    return weight;
  }
  
  public Seq<Seq<String>> out() { return out; }
  
  public Seq<String> mste() { return mste; }

  public Seq<String> mstv() { return mstv; }

  public void trace() {
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", "); 
    int c = 1;
    EuclidianGraph eg = new EuclidianGraph(g);
    eg.addCoords(0,45,77.5);
    eg.addCoords(1,42.5,95);
    eg.addCoords(2,56,85);
    eg.addCoords(3,58,93.5);
    eg.addCoords(4,21.5,70);
    eg.addCoords(5,21.6,92.5);
    eg.addCoords(6,77.5,70);
    eg.addCoords(7,37,85);
    
    Seq<String> last = new Seq<>();
    last.add("");
    if (mste.isEmpty()) last.add("");
    else last.add(mste.toString());
    last.add("MinPQ: (is empty)");
    last.add("");
    last.add("final MST = "+mste.toString());
    
    for (Seq<String> s : out) eg.showLazyPrimMSTtrace(labels,s.to(),"Step "+c++);
    eg.showLazyPrimMSTtrace(labels,last.to(),"Final");
  }

  public static Seq<Draw> traceTinyEWG() {
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    EdgeWeightedGraphY G = new EdgeWeightedGraphY(V,edges);
    
    LazyPrimMSTtrace L = new LazyPrimMSTtrace(G);
    
    EuclidianGraph eg = new EuclidianGraph(L.g);
    eg.addCoords(0,45,77.5);
    eg.addCoords(1,42.5,95);
    eg.addCoords(2,58,82);
    eg.addCoords(3,58,93.5);
    eg.addCoords(4,21.5,70);
    eg.addCoords(5,21.6,92.5);
    eg.addCoords(6,77.5,70);
    eg.addCoords(7,37,85);
    
    Seq<Draw> drawings = new Seq<>();
    
    int c = 0;
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(","); 
    for (Seq<String> s : L.out) 
      drawings.add(eg.showLazyPrimMSTtrace(labels,s.to(),"Step "+c++));
    
    Seq<String> last = new Seq<>();
    last.add("");
    if (L.mstv().isEmpty()) last.add("");
    else last.add(L.mstv().toString());
    if (L.mste().isEmpty()) last.add("");
    else last.add(L.mste().toString());
    if (L.pq.isEmpty()) {
      last.add("MinPQ: ()");
    } else {
      last.add("MinPQ: (+ new; x ineligible)");
      for (EdgeY x : L.pq.toArray2()) {
        if (!L.ppq.contains(x)) last.add("+ "+x.toString4());
        else if (L.marked[x.u()] && L.marked[x.v()]) last.add("x "+x.toString4()); 
        else last.add("   "+x.toString());
      }
    }
    last.add("");
    last.add("MST vertices: "+L.mstv().toString().replaceAll(",",", "));
    last.add("MST edges:    "+L.mste().toString().replaceAll(",",", "));
    drawings.add(eg.showLazyPrimMSTtrace(labels,last.to(),"Final"));
    return drawings;
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
  
  public static void main(String[] args) {
    
    Seq<Draw> draw = traceTinyEWG();  
    dispose(draw);
    
//    In in = new In(args[0]);
//    EdgeWeightedGraphY G = new EdgeWeightedGraphY(in);
//    LazyPrimMSTtrace L = new LazyPrimMSTtrace(G);
//    L.trace();
//    System.out.println();
//    for (EdgeY e : mst.edges()) System.out.println(e);
//    StdOut.printf("%.5f\n", mst.weight());
//    System.out.println();
//    System.out.println("trace:");
//    for (Seq<String> seq : mst.out()) {
//      for (String s : seq) System.out.println(s);
//      System.out.println();
//    }
    
    

  }

}
