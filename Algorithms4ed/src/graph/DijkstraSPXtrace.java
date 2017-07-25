package graph;

import static analysis.Draw.BLACK;
import static v.ArrayUtils.fillDouble;
import static v.ArrayUtils.ofDim;
import static v.ArrayUtils.range;
import static v.ArrayUtils.unbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import pq.IndexMinPQ;
import v.Tuple2;
import v.Tuple3;
import v.Tuple5;
import v.Tuple9;

// from DijkstraSPX for exercises 4.4.6, 4.4.10 and 4.4.46

@SuppressWarnings("unused")
public class DijkstraSPXtrace {
  private DirectedEdgeX[] edgeTo;  // edgeTo[v] = last edge on shortest s->v path
  private double[] distTo;         // distTo[v] = distance  of shortest s->v path
  private boolean[] marked;        // true if weight != Double.POSITIVE_INFINITY
  private IndexMinPQ<Double> pq;   // priority queue of vertices
  private Seq<DirectedEdgeX> spte;  // SPT edges
  private transient Seq<DirectedEdgeX> inel;  // invalid edges
  private transient Seq<Integer> sptv;        // SPT vertices
  private transient DirectedEdgeX nextEdge;   // next edge to put in SPT
  private Seq<Tuple9<Integer,DirectedEdgeX,Seq<Integer>,Seq<Integer>,Seq<DirectedEdgeX>,Seq<DirectedEdgeX>,DirectedEdgeX[],double[],boolean[]>> data;
  /*  Tuple9 fields in data Seq<Tuple9<>
   *  id  type                contents
   *  1   Integer             current vertex in relax call or processing tag if negative
   *  2   DirectedEdgeX       next edge to be added to the SPT
   *  3   Seq<Integer>        vertices in IndexMinPQ (pq)
   *  4   Seq<Integer>        vertices in SPT
   *  5   Seq<DirectedEdgeX>  edges in SPT (spte)
   *  6   Seq<DirectedEdgeX>  ineligible edges (inel)
   *  7   DirectedEdgeX[]     edgeTo
   *  8   double[]            distTo
   *  9   boolean[]           marked
   */
  private transient EdgeWeightedDigraphX G; // input graph
  private int source;
  private int V;
  private transient boolean animate = false;

  public DijkstraSPXtrace(EdgeWeightedDigraphX g, int s) {
    // constructor for trace using traceModTinyEWDex4406() and traceModTinyEWDex4410()
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    marked = new boolean[V];
    spte = new Seq<>();
    inel = new Seq<>();
    sptv = new Seq<>();
    pq = new IndexMinPQ<Double>(V);
    data = new Seq<>();
    data.add(new Tuple9<>(-2,null,new Seq<Integer>(),sptv.clone(),
        spte.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
    distTo[s] = 0.0;
    sptv.add(s);
    data.add(new Tuple9<>(-1,null,new Seq<Integer>(pq.toArray().clone()),sptv.clone(),
        spte.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
    pq.insert(s, distTo[s]);
    while (!pq.isEmpty()) {
      relax(G, pq.delMin()); // Add closest vertex to source (vs. tree for Prim's Mst)
    }
    boolean check = check();
    if (!check) 
      System.out.println("SPT doesn't satisfy optimality conditions for source "+s);
  }

  public DijkstraSPXtrace(EdgeWeightedDigraphX g, int s, boolean animate) {
    // constructor for animation using animateDijkstraEx4446
    if (!animate) return;
    else this.animate = animate;
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    marked = new boolean[V];
    spte = new Seq<>();
    inel = new Seq<>();
    sptv = new Seq<>();
    pq = new IndexMinPQ<Double>(V);
    distTo[s] = 0.0;
    sptv.add(s);
    pq.insert(s, distTo[s]);
    while (!pq.isEmpty()) {
      relax(G, pq.delMin()); // Add closest vertex to source (vs. tree for Prim's MST)
    }
    boolean check = check();
    if (!check) 
      System.out.println("SPT doesn't satisfy optimality conditions for source "+s);
  }

  private void relax(EdgeWeightedDigraphX G, int u) {
    marked[u] = true;
    if (u != source) sptv.add(u);
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.to();
      if (marked[v]) { // e is ineligible.
        if (!spte.contains(e)) inel.add(e);
        continue; 
      }
      if (distTo[v] > distTo[u] + e.weight()) {
        distTo[v] = distTo[u] + e.weight();
        edgeTo[v] = e;
        if (pq.contains(v)) pq.decreaseKey(v, distTo[v]);
        else pq.insert(v, distTo[v]);
      }
    }
    if (nextEdge != null) spte.add(nextEdge);
    nextEdge = nextEdge();
    if (!animate) 
      data.add(new Tuple9<>(u,nextEdge,new Seq<Integer>(pq.toArray().clone()),sptv.clone(),
          spte.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
  }

  private DirectedEdgeX nextEdge() {
    // return the next edge to go into the SPT else null
    double min = Double.POSITIVE_INFINITY;
    DirectedEdgeX ne = null;
    Iterator<Integer> it = pq.iterator();
    while (it.hasNext()) {
      int i = it.next();
      if (distTo(i) < min) {
        min = distTo(i);
        ne = edgeTo[i];
      }
    }
    return ne;
  }

  public double[] distTo() { return distTo; } ;

  public double distTo(int v) {
    // return length of shortest path from source to v 
    validateVertex(v);
    return distTo[v];
  }

  public DirectedEdgeX[] edgeTo() { return edgeTo; }

  public Seq<DirectedEdgeX> spte() { return spte; }

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
    //    System.out.println("SPT satisfies optimality conditions");
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void dispose(Draw[] draw) {
    if (draw == null) return;
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

  public static void dispose(Seq<Draw> draw) {
    if (draw == null) return;
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

  public static Seq<Draw> traceModTinyEWDex4406() {
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);

    //reverse edge 0->2: ex4405 and ex4406 requirement
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
    } else {
      System.out.println("could not find edge 0->2");
    }

    int V = G.V();
    int source = 2; // ex4405 and ex4406 requirement

    DijkstraSPXtrace D = new DijkstraSPXtrace(G, source);    

    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);

    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");

    Seq<Draw> drawings = new Seq<>();

    int c = 0;
    for (int i = 0; i < D.data.size()-1; i++)
      drawings.add(E.showDijkstraSPXtrace(labels,D.data.get(i),"Step "+c++));
    drawings.add(E.showDijkstraSPXtrace(labels,D.data.get(c),"Final"));
    return drawings;
  }

  public static Seq<Draw> traceModTinyEWDex4410() {
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);

    boolean vertex7removed = G.removeLastVertex();
    if (vertex7removed == false) System.out.println("vertex 7 couldn't be removed");

    // add reverse of all remaining edges
    for (DirectedEdgeX e : G.edgeSeq()) G.addEdge(e.reverse());   

    int V = G.V();
    int source = 0;

    DijkstraSPXtrace D = new DijkstraSPXtrace(G, source);    

    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,93.5,77.5,70,37,85};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G,coords);

    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");

    Seq<Draw> drawings = new Seq<>();

    int c = 0;
    for (int i = 0; i < D.data.size()-1; i++)
      drawings.add(E.showDijkstraSPXtrace(labels,D.data.get(i),"Step "+c++));
    drawings.add(E.showDijkstraSPXtrace(labels,D.data.get(c),"Final"));
    return drawings;
  }

  public static void animateDijkstraEx4446(String ewd, String coordinates) {
    if (ewd == null) throw new IllegalArgumentException("String ewd is null");
    if (coordinates == null) throw new IllegalArgumentException("String coordinates is null");

    In in = new In(ewd);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    int V = G.V();
    
    Tuple2<Seq<Tuple2<Double,Double>>,double[]> t = coordsSeqMinMax(coordinates);
    
    Seq<Tuple2<Double,Double>> coords = t._1;
    double xmin = t._2[0], xmax = t._2[1], ymin = t._2[2], ymax = t._2[3];
    
    // if xmin != 0 adjust it to 0, same for ymin
    if (xmin != 0 || ymin != 0)
      if (xmin != 0 && ymin != 0)
        for (Tuple2<Double,Double> tup : coords) tup.set(tup._1 - xmin, tup._2 - ymin);
      else if (xmin != 0) 
        for (Tuple2<Double,Double> tup : coords) tup.set1(tup._1 - xmin);
      else 
        for (Tuple2<Double,Double> tup : coords) tup.set2(tup._2 - ymin);
    
    EuclidianEdgeWeightedDigraph E = new EuclidianEdgeWeightedDigraph(G, coords);

    // StdDraw settings
    double sfx = xmax*.05, sfy = ymax*.05; // 5% margins
    StdDraw.setCanvasSize(900, 900);
    StdDraw.setXscale(-sfx, xmax+sfx);
    StdDraw.setYscale(-sfy, ymax+sfy);
    StdDraw.setPenColor(BLACK);
    StdDraw.setPenRadius(.005);

    // eliminate drawing duplicate edges
    HashSet<DirectedEdgeX> edges = new HashSet<>();

    // interleave sources in increasing order with decreasing order
    Seq<Integer> sources = Seq.interleave(Seq.range(0,V/2),Seq.range(V/2,V).reverse());
    int k = 0, s = 0;    
    Tuple2<Double,Double> t1,t2;    
    DijkstraSPXtrace D = null; 

    // compute and draw edges of all pairs shortest paths
    while (k < V) { 
      s = sources.get(k++);
      D = new DijkstraSPXtrace(G, s, true);
      for (DirectedEdgeX e : D.spte) {
        if (edges.contains(e)) continue;
        t1 = coords.get(e.from());
        t2 = coords.get(e.to());
        StdDraw.line(t1._1, t1._2, t2._1, t2._2);
        edges.add(e);
      }
    }
    
    System.out.println(ewd.replace("EWD.txt", "")+" map done");
  }
  
  public static Tuple2<Seq<Tuple2<Double,Double>>,double[]> coordsSeqMinMax(String coords) {
    // read coordinates file coords and return its coordinates as a Seq<Tuple2<Double,Double> 
    // and an array of its minx, maxx, miny and maxy in a Tuple2<Seq<Double,Double>,double[]>    

    if (coords == null) throw new IllegalArgumentException("String coords is null");

    double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY;
    double ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;   
    double x, y;   
    Seq<Tuple2<Double,Double>> seq = new Seq<>();   
    Scanner sc = null;
    
    try {
      sc = new Scanner(new File(coords));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    while (sc.hasNextLine()) {
      String l = sc.nextLine();
      String[] a = l.split("\\s+");
      if (a.length != 3) continue;
      x = Double.parseDouble(a[1]); 
      y = Double.parseDouble(a[2]);
      seq.add(new Tuple2<>(x,y));
      if (x < xmin) xmin = x;
      if (x > xmax) xmax = x;
      if (y < ymin) ymin = y;
      if (y > ymax) ymax = y;
    } 
    
    sc.close();
    
    return new Tuple2<>(seq, new double[]{xmin,xmax,ymin,ymax});
  }

  // for one time use
//  public static void updateOldenburgEdges() {
//    // OldenburgEdges.txt is from https://www.cs.utah.edu/~lifeifei/research/tpq/OL.cedge
//    // see https://www.cs.utah.edu/~lifeifei/SpatialDataset.htm for more information
//    // remove first EdgeID column in OldenburgEdges.txt and write to OldenburgEWD.txt
//    Seq<String> seq = new Seq<>();
//    String f = "OldenburgEdges.txt";
//    Scanner sc = null;
//    try {
//      sc = new Scanner(new File(f));
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//    while (sc.hasNextLine()) {
//      String l = sc.nextLine();
//      String[] a = l.split("\\s+");
//      if (a.length != 4) continue;
//      seq.add(a[1]+" "+a[2]+" "+a[3]);
//    }
//    try {
//      Files.write(Paths.get("OldenburgEWD.txt"), seq);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }  
//  }

  public static double[] oldenburgCoordsArray() {
    // read OldenburgCoords.txt and scale down x and y coords by a factor of 10
    // since their min == 0 and max == 10000 and return as a double[] in order of
    // increasing vertices
    double x, y;
    Seq<Double> seq = new Seq<>();
    String f = "OldenburgCoords.txt";
    Scanner sc = null;
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (sc.hasNextLine()) {
      String l = sc.nextLine();
      String[] a = l.split("\\s+");
      if (a.length != 3) continue;
      x = Double.parseDouble(a[1]); y = Double.parseDouble(a[2]);
//      x /= 10.; y /= 10.;
      seq.add(x); seq.add(y);
    }   
    return (double[])unbox(seq.to());
  }

  public static double[] oldenburgMinMaxCoords() {
    // OldenburgCoords.txt is from https://www.cs.utah.edu/~lifeifei/research/tpq/OL.cnode
    // see https://www.cs.utah.edu/~lifeifei/SpatialDataset.htm for more information
    // find min and max coords in OldenburgCoordsOrig.txt and return them in an array
    // found min and max are 0 and 10000 for x and y
    double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY, x, y;
    double ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
    int v;
    String f = "OldenburgCoords.txt";
    Scanner sc = null;
    try {
      sc = new Scanner(new File(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (sc.hasNextLine()) {
      String l = sc.nextLine();
      String[] a = l.split("\\s+");
      if (a.length != 3) continue;
      v = Integer.parseInt(a[0]);
      x = Double.parseDouble(a[1]);
      //      if (x == 0.) System.out.println("x = "+x+" v = "+v);
      //      if (x == 10000.) System.out.println("x = "+x+" v = "+v);
      if (x < xmin) xmin = x;
      if (x > xmax) xmax = x;
      y = Double.parseDouble(a[2]);
      //      if (y == 0.) System.out.println("y = "+y+" v = "+v);
      //      if (y == 10000.) System.out.println("y = "+y+" v = "+v);
      if (y < ymin) ymin = y;
      if (y > ymax) ymax = y;
    }
    return new double[]{xmin,xmax,ymin,ymax};
  }

  public static void testPlot() {
    // test scaling for Oldenburg plot
    StdDraw.setCanvasSize(900, 900); 
    StdDraw.setXscale(-50,1050);
    StdDraw.setYscale(-50,1050);
    StdDraw.setPenColor(BLACK);
    StdDraw.setPenRadius(.01);
    StdDraw.point(0,0);
    StdDraw.point(10,10);
    StdDraw.point(100,100);
    StdDraw.point(900,900);
    StdDraw.point(1000,1000);
  }

  public static void main(String[] args) {

//    Seq<Draw> draw = traceModTinyEWDex4406(); dispose(draw);
    // Seq<Draw> draw = traceModTinyEWDex4410(); dispose(draw);
     animateDijkstraEx4446("OldenburgEWD.txt", "OldenburgCoords.txt");

  }

}
