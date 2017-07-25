package graph;

import static analysis.Draw.*;
import static v.ArrayUtils.*;

import java.awt.Color;
import java.awt.Font;
import java.security.SecureRandom;

import analysis.Draw;
import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import st.HashSET;
import v.Tuple2;
import v.Tuple4;
import v.Tuple6;
import v.Tuple9;

// ex4330
// based on EuclidianGraph

//@SuppressWarnings("unused")
public class EuclidianEdgeWeightedGraph {
  private final String NEWLINE = System.getProperty("line.separator");
  private final int DEFAULTLEN = 17;
  private int V;
  private int E;
  private Seq<BagX<EdgeX>> adj;
  private Seq<Tuple2<Double,Double>> coords;
  private transient boolean[] marked;
  private transient int[] edgeTo;
  private transient int[] id; // component ids
  private transient int[] size;  // size[id] = number of vertices in given component
  private transient int count = 0; // number of connected components
  private transient Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()
  private transient Stack<Integer> selfLoop = null;
  private transient Stack<Integer> parallelEdges = null;
  boolean validate = true; // toggle vertex validation in addEdge, isEdge, hasEdge
  // insertEdge, addCoords, removeCoordinates, updateX, updateY  

  // updated from EuclidianGraph for ex4330
  
  public EuclidianEdgeWeightedGraph() {
    V = E = 0;
    adj = Seq.fill(DEFAULTLEN, ()->new BagX<EdgeX>());
    coords = Seq.fill(DEFAULTLEN, ()->new Tuple2<Double,Double>());
    validate = false;
    //    System.out.println(
    //        "insert edges with insertEdge(int,int,double) and coordinates with addCoords(int,double,double)"
    //        + "\nvertices without coordinates won't be graphed"
    //        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }

  public EuclidianEdgeWeightedGraph(int v) {
    if (v < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph : Number of vertices must be nonnegative");
    V = v; E = 0;
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    coords = Seq.fill(V, ()->new Tuple2<Double,Double>());
    //    System.out.println(
    //        "add edges with addEdge(int,int,double) and coordindates with addCoords(int,double,double)"
    //        + "\nvertices without coordinates won't be graphed"
    //        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }

  public EuclidianEdgeWeightedGraph(int v, int e) {
    if (v < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph : Number of vertices must be nonnegative");
    if (e < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph : Number of edges must be nonnegative");   
    V = v; E = e;
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    for (int i = 0; i < E; i++) {
      int m = StdRandom.uniform(V);
      int n = StdRandom.uniform(V);
      if (m == n || hasAnyEdge(m,n)) continue;
      double w = Math.round(100 * StdRandom.uniform()) / 100.0;
      EdgeX edg = new EdgeX(m,n,w);
      addEdge(edg);
    }
    search();
    // create C random coord pairs in the range 1.-99.
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 130905; i++) r.nextDouble();
    double[] ja = r.doubles(2*V,1,100).toArray();
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    coords = new Seq<>(td);  
    //    System.out.println(
    //        "add edges with addEdge(int,int,double) and coordindates with addCoords(int,double,double)"
    //        + "\nvertices without coordinates won't be graphed"
    //        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }

  public EuclidianEdgeWeightedGraph(int V, String edges, double[][] xy) {
    if (V < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph : Number of vertices must be nonnegative");
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph: String edges == null");
    if (xy.length < V) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedGraph : the length of xy is < v");
    for (int i = 0; i < V; i++)
      if (xy[i].length < 2) throw new IllegalArgumentException(
          "EuclidianEdgeWeightedGraph : the length of xy["+i+"] is < 2");
    this.V = V; E = 0;
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    validate = false;
    String[] edgs = edges.split("\\s+"); int u,v; double w;
    int len = edgs.length %3 == 0 ? edgs.length : (edgs.length/3)*3;
    for (int i = 0; i < len-2; i+=3) {
      u = Integer.parseInt(edgs[i]);
      v = Integer.parseInt(edgs[i+1]);
      w = Double.parseDouble(edgs[i+2]);
      validateVertices(u,v);
      EdgeX e = new EdgeX(u,v,w);
      addEdge(e);
    }
    search();
    validate = true;
    coords = new Seq<Tuple2<Double,Double>>(V);
    for (int i = 0; i < V; i++) 
      coords.add(new Tuple2<Double,Double>(xy[i][0],xy[i][1]));
  }

  public EuclidianEdgeWeightedGraph(int v, Iterable<EdgeX> edges, Iterable<Tuple2<Double,Double>> xy) {
    if (v <= 0) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : v is <= 0");
    if (edges == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : edges is null");
    if (xy == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : xy is null");
    Seq<Tuple2<Double,Double>> crds = new Seq<>(xy);
    if (crds.size() < v) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : the size of xy is < v");
    for (int i = 0; i < v; i++) {
      Tuple2<Double,Double> t = crds.get(i);
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 1st component of Tuple2 "+i+" in xy is null");
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 2nd component of Tuple2 "+i+" in xy is null");
    }
    V = v;
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    validate = false;
    for (EdgeX e : edges) {
      validateVertices(e.u(),e.v());
      addEdge(e);
    }
    search();
    validate = true;
    coords = crds;
  }
  
  public EuclidianEdgeWeightedGraph(Iterable<EdgeX> edges, Iterable<Tuple2<Double,Double>> xy) {
    if (edges == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : edges is null");
    HashSET<Integer> set = new HashSET<>();
    for (EdgeX e : edges) { set.add(e.u()); set.add(e.v()); }
    if (set.size() == 0) {
      V = 0;
    } else {
      V = set.size();
      Integer[] vtcs = set.toArray();
      if (min(vtcs) < 0) throw new IllegalArgumentException(
          "EuclidianEdgeWeightedGraph: Iterable<EdgeX> contains a vertex < 0");
      if (max(vtcs) > V) throw new IllegalArgumentException(
          "EuclidianEdgeWeightedGraph: Iterable<EdgeX> contains a vertex > "+V);
    }
    if (xy == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : xy is null");
    Seq<Tuple2<Double,Double>> crds = new Seq<>(xy);
    if (crds.size() < V) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : the size of xy is < v");
    for (int i = 0; i < V; i++) {
      Tuple2<Double,Double> t = crds.get(i);
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 1st component of Tuple2 "+i+" in xy is null");
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 2nd component of Tuple2 "+i+" in xy is null");
    }
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    validate = false;
    for (EdgeX e : edges)  addEdge(e);
    search();
    validate = true;
    coords = crds;
  }
  
  public EuclidianEdgeWeightedGraph(In in, Iterable<Tuple2<Double,Double>> xy) {
    V = in.readInt();
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertices(u,v);
      double w = in.readDouble();
      EdgeX e = new EdgeX(u,v,w);
      addEdge(e);
    }
    validate = true;
    search();
    if (xy == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : xy is null");
    Seq<Tuple2<Double,Double>> crds = new Seq<>(xy);
    if (crds.size() < V) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : the size of xy is < v");
    for (int i = 0; i < V; i++) {
      Tuple2<Double,Double> t = crds.get(i);
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 1st component of Tuple2 "+i+" in xy is null");
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 2nd component of Tuple2 "+i+" in xy is null");
    }
    coords = crds;
  }
  
  public EuclidianEdgeWeightedGraph(EdgeWeightedGraphX G, Iterable<Tuple2<Double,Double>> xy) {
    if (G == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph: G is null");
    if (xy == null) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : xy is null");
    V = G.V();
    Seq<Tuple2<Double,Double>> crds = new Seq<>(xy);
    if (crds.size() < V) throw new IllegalArgumentException("EuclidianEdgeWeightedGraph : the size of xy is < G.V()");
    for (int i = 0; i < V; i++) {
      Tuple2<Double,Double> t = crds.get(i);
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 1st component of Tuple2 "+i+" in xy is null");
      if (t._1 == null) throw new IllegalArgumentException(
          "IllegalArgumentException: the 2nd component of Tuple2 "+i+" in xy is null");
    }
    E = G.E();
    adj = Seq.fill(V, ()->new BagX<EdgeX>());
    for (int v = 0; v < G.V(); v++) {
      Stack<EdgeX> reverse = new Stack<EdgeX>();
      for (EdgeX e : G.adj(v)) reverse.push(e);
      for (EdgeX e : reverse) adj.get(v).add(e);
    }
    search();
    coords = crds;
  }

  public int V() { return V; }

  public int E() { return E; }

  public void setE(int e) { E = e; }

  public void setV(int v) { V = v; }

  public Seq<BagX<EdgeX>> adj() { return adj.clone(); }

  public void setAdj(Seq<BagX<EdgeX>> seq) { adj = seq; }

  public Seq<Tuple2<Double,Double>> coords() { return coords.clone(); }

  public Stack<Integer> cycle() { 
    findCycle();
    return cycle; 
  }

  public Stack<Integer> selfLoop() { 
    hasSelfLoop();
    return selfLoop; 
  }

  public Iterable<Integer> parallelEdges() { 
    hasParallelEdges(); 
    return parallelEdges; 
  }

  public boolean[] marked() { return marked == null ? null : marked.clone(); }

  public void setMarked(boolean[] ba) { marked = ba; }

  public int[] id() { return id == null ? null : id.clone(); }

  public void setId(int[] ia) { id = ia; }

  public int[] size() { return size == null ? null : size.clone(); }

  public void setSize(int[] ia) { size = ia; }

  public boolean validate() { return validate; }

  public int[][] hArray() {
    // for hamiltonian cycle detection using HamiltonianCycle
    int[][] a = new int[V][V];
    for (int i = 0; i< V; i++) 
      for (EdgeX e : adj.get(i)) a[i][e.other(i)] = 1;
    return a;
  }

  public int count() {
    // return the number of connected components
    return count; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  private void validateVertices(int u, int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
    if (u < 0 || u >= V) throw new IllegalArgumentException("vertex "+u+" is out of bounds");
  }

  private void dfs(int v) {
    marked[v] = true;
    id[v] = count;
    size[count]++;
    for (EdgeX e : adj.get(v))
      if (!marked[e.other(v)]) dfs(e.other(v));
  } 

  public void search() {
    // count connected components from p544 / CC
    count = 0;    
    marked = new boolean[V];
    id = new int[V];
    size = new int[V];
    for (int s = 0; s < V; s++) if (!marked[s]) { dfs(s); count++; }
  }

  private void dfs2(int u, int v) {
    // from CycleX; used by findCycle()
    marked[v] = true;
    for (EdgeX e : adj(v)) {
      int w = e.other(v);
      // short circuit if cycle already found
      if (cycle != null) return;
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs2(v, w);
      }
      // check for cycle (but disregard reverse of edge leading to v)
      else if (w != u) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) {
          cycle.push(x);
        }
        cycle.push(w);
        cycle.push(v);
      }
    }
  }

  public boolean findCycle() {
    // from CycleX; excluding self-loops and parallel edges
    marked = new boolean[V];
    edgeTo = new int[V];
    for (int v = 0; v < V; v++) if (!marked[v]) dfs2(-1, v);
    return cycle == null ? false : true;
  }

  public boolean connected(int v, int w) { return id[v] == id[w]; }

  public void addEdge(int u, int v, double w) {
    // use this when V has been set finally, E has been initialzed to 
    // half its final value and all coordinates have been assigned
    if (validate) { validateVertex(u); validateVertex(v); }
    EdgeX e = new EdgeX(u,v,w);
    adj.get(u).add(e);
    adj.get(v).add(e);
    E++;
  }

  public void addEdge(EdgeX e) {
    // use this when V has been set finally, E has been initialzed to 
    // half its final value and all coordinates have been assigned
    if (e == null) throw new IllegalArgumentException("addEdge: EdgeX is null");
    int u = e.u(), v = e.v();
    if (validate) { validateVertex(u); validateVertex(v); }
    adj.get(u).add(e);
    adj.get(v).add(e);
    E++;
  }

  public boolean insertEdge(int u, int v, double w) {
    // use this when V has been set but not finally, and the number of edges isn't set
    if (validate) { validateVertex(u); validateVertex(v); }
    int max = Math.max(u, v); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<EdgeX>());
    EdgeX e = new EdgeX(u,v,w);
    adj.get(u).add(e);
    adj.get(v).add(e);
    E++;
    return true;
  }

  public boolean insertEdge(EdgeX e) {
    // use this when V has been set but not finally, and the number of edges isn't set
    if (e == null) throw new IllegalArgumentException("addEdge: EdgeX is null");
    int u = e.u(), v = e.v();
    if (validate) { validateVertex(u); validateVertex(v); }
    int max = Math.max(u, v); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<EdgeX>());
    adj.get(u).add(e);
    adj.get(v).add(e);
    E++;
    return true;
  }

  public void addCoords(int v, double x, double y) {
    // assign or reassign coordinates to a vertex
    if (validate) validateVertex(v);
    if (coords == null) coords = new Seq<Tuple2<Double,Double>>(2*v);
    else if (coords.size() < v) {
      int s = adj.size();
      for (int i = 0; i < 2*v-s; i++) coords.add(new Tuple2<Double,Double>());
    }
    coords.get(v).add(x,y);
  }

  public void updateX(int v, double x) {
    // update the x coordinate of a vertex
    if (validate) validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return;
    }
    coords.get(v).add1(x);
  }

  public void updateY(int v, double y) {
    // update the y coordinate of a vertex
    if (validate) validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return;
    }
    coords.get(v).add2(y);
  }

  public boolean removeCoordinates(int v) {
    // remove the coordinates of a vertex
    if (validate) validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return false;
    }
    coords.delete(v);
    return true;
  }

  public void trim() {    
    if (adj != null && adj.size() > V) adj.deleteRange(V, adj.size()); 
    if (coords != null && coords.size() > V) coords.deleteRange(V, coords.size()); 
  }

  public boolean removeEdge(int v, int w) {
    // remove edge between v and w if it exists and is represented by
    // entries in both adj[v] and adj[w]. return true only if the edge
    // is found and removed else return false.
    validateVertex(v); validateVertex(w);
    int max = Math.max(v, w);
    if (adj == null || adj.size() < max) throw new IllegalArgumentException(
        "removeEdge: vertex "+max+" hasn't been added yet");
    BagX<EdgeX> bv = adj.get(v); boolean fw = false; int iw = -1;  
    BagX<EdgeX> bw = adj.get(w); boolean fv = false; int iv = -1;
    int c = 0, i;
    for (EdgeX e : bv) {
      i = e.other(v);
      if (i == w) { iw = c; fw = true; break; }
      c++;
    }
    if (fw == false) return false;
    c = 0;
    for (EdgeX e : bw) {
      i = e.other(w);
      if (i == v) { iv = c; fv = true; break; }
      c++;
    }
    if (fv == false) return false;
    bv.remove(iw); bw.remove(iv);
    E--;
    return true;
  }
  
  public void removeEdge(EdgeX x) { 
    int u = x.u(), v = x.v();
    adj.get(u).remove(x);
    adj.get(v).remove(x);
    E--;
  }

  public Iterable<EdgeX> adj(int v) { validateVertex(v); return adj.get(v); }

  public Iterable<EdgeX> edges() {
    BagX<EdgeX> list = new BagX<>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (EdgeX e : adj(v)) {
        if (e.other(v) > v) {
          list.add(e);
        }
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) list.add(e);
          selfLoops++;
        }
      }
    }
    return list;
  }

  public Seq<EdgeX> edgeSeq() {
    Seq<EdgeX> s = new Seq<>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (EdgeX e : adj(v)) {
        if (e.other(v) > v) s.add(e);
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) s.add(e);
          selfLoops++;
        }
      }
    }
    return s;
  }

  public int degree(int v) {
    validateVertex(v);
    return adj.get(v).size();
  }

  public int maxDegree() {
    int max = 0, d = 0;
    for (int v = 0; v < V; v++) { d = degree(v); if (d > max) max = d; }
    return max;
  }

  public double avgDegree() { return 1. * E / V; }

  public boolean isEdge(int u, int v) {
    if (validate) validateVertices(u,v);
    for (EdgeX e : adj.get(u)) if (e.contains(v)) return true;
    return false;
  }

  public boolean hasEdge(int u, int v) { return hasEdge(u,v); }

  public boolean hasAnyEdge(int u, int v) {
    // if there's any direct edge between u to v return true else return false
    for (EdgeX e : adj.get(u)) 
      if (e.u() == u && e.v() == v || e.u() == v && e.v() == u) return true;
    return false;  
  }

  public int numberOfSelfLoops() {
    // from GraphClient
    int count = 0;
    for (int v = 0; v < V; v++)
      for (EdgeX e : adj.get(v)) 
        if (e.u() == e.v()) count++;
    return count/2; // each edge counted twice
  }

  public boolean hasSelfLoop() {
    // from Cycle
    for (int v = 0; v < V; v++) {
      for (EdgeX e : adj.get(v)) {
        int w = e.other(v);
        if (v == w) {
          selfLoop = new Stack<Integer>();
          selfLoop.push(v);
          selfLoop.push(v);
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasParallelEdges() {
    // from Cycle
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      // check for parallel edges incident to v
      for (EdgeX e : adj.get(v)) {
        int w = e.other(v);
        if (m[w]) {
          parallelEdges = new Stack<Integer>();
          parallelEdges.push(v);
          parallelEdges.push(w);
          parallelEdges.push(v);
          return true;
        }
        m[w] = true;
      }
      // reset so marked[v] = false for all v
      for (EdgeX e : adj.get(v)) m[e.other(v)] = false;
    }
    return false;
  }

  public boolean hasCycle() {
    return hasSelfLoop() || hasParallelEdges(); 
  }


  //  public EuclidianEdgeWeightedGraph clone() { return new EuclidianEdgeWeightedGraph(this); }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("V = "+V+", E = "+E+NEWLINE);
    s.append(adj+NEWLINE);
    s.append(coords+NEWLINE);
    //    for (int v = 0; v < V; v++) {
    //      s.append(v + ": ");
    //      for (int w : adj.get(v)) {
    //        s.append(w + " ");
    //      }
    //      s.append(NEWLINE);
    //    }
    return s.toString();
  }

  public static final Color DarkerGray = new Color(97,97,97);

  public void show() {
    double maxx, maxy; maxx = maxy = Double.NEGATIVE_INFINITY;
    double minx, miny; minx = miny = Double.POSITIVE_INFINITY;
    for (Tuple2<Double,Double> t : coords) {
      if (t != null) {
        if (t._1 != null) {
          if (t._1 > maxx) maxx = t._1;
          if (t._1 < minx) minx = t._1;
        }
        if (t._2 != null) {
          if (t._2 > maxy) maxy = t._2;
          if (t._2 < miny) miny = t._2;
        }       
      }
    }   
    StdDraw.setCanvasSize(800, 800); double sf = .25;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (EdgeX e : adj.get(i)) points[i].drawTo(points[e.other(i)]);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public void showLabelled(String[] labels, String[] content) {
    // for ex4318; uses StdDraw and is suitable for only one drawing at a time
    // plots an EuclidianGraphX with labelled vertices and text content
    if (labels.length != V) throw new IllegalArgumentException(
        "showLabelled: labels.length != V == "+V);
    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    StdDraw.setCanvasSize(900, 900); double sf = .25;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (EdgeX e : adj.get(i)) points[i].drawTo(points[e.other(i)]);
    StdDraw.setPenColor(StdDraw.WHITE);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        StdDraw.filledCircle(x, y, 2);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        StdDraw.circle(x, y, 2);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    StdDraw.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        StdDraw.text(x, y, labels[i]);
      }
    }

    StdDraw.textLeft(5,90,"step 1");

    font = new Font("Arial", Font.BOLD, 16);
    StdDraw.setFont(font);
    int indent = 10, yaxis = 64;

    for (String s : content) {
      StdDraw.textLeft(indent, yaxis-=2, s);
    }
  }

  public void showLabelled2(String[] labels, String[] content, String title) {
    // for ex4318; uses edu.princeton.cs.algs4.Draw since it can display 
    // multiple drawings at a time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels.length != V) throw new IllegalArgumentException(
        "showLabelled2: labels.length != V == "+V);
    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2; 
    double x,y; 
    Tuple4<Double,Double,Double,Double> pqMin = null;
    boolean first = true;

    // assign pqMin; get other edges in MinPQ and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cpq4 = new HashSET<>(); // pq edges
    if (content[0].length() > 0) {
      String[] cpqs = content[0].replaceAll("\\)|\\(", "").split(",");
      for (String s : cpqs) {
        String[] sedge = s.split("-");
        t = coords.get(Integer.parseInt(sedge[0]));
        t2 = coords.get(Integer.parseInt(sedge[1]));
        if (first) { 
          pqMin = new Tuple4<>(t._1, t._2, t2._1, t2._2);
          first = false;    
        } 
        cpq4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
        cpq4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
      }
    }

    // get MST vertices after 1 pass through while loop and save their coords
    HashSET<Tuple2<Double,Double>> mstv = new HashSET<>(); 
    if (content[1].length() > 0) {
      String[] sa = content[1].replaceAll("\\)|\\(", "").split(",");
      System.out.print("sa="); par(sa);
      for (String s : sa) mstv.add(coords.get(Integer.parseInt(s)));
    }

    System.out.println("mstv="+mstv);

    // get MST edges after 1 pass through while loop,draw them in thick black
    // and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.02);
    HashSET<Tuple2<Double,Double>> mst = new HashSET<>(); // mst vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> mst4 = new HashSET<>(); // mst edges
    if (content[2].length() > 0) {
      String[] msts = content[2].replaceAll("\\)|\\(", "").split(",");
      System.out.print("msts="); par(msts);
      for (String s : msts) {
        String[] sedge = s.split("-");
        t = coords.get(Integer.parseInt(sedge[0]));
        t2 = coords.get(Integer.parseInt(sedge[1]));
        d.line(t._1, t._2, t2._1, t2._2);
        mst.add(t); mst.add(t2);
        mst4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
        mst4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
      }
    }

    // draw all other edges except pqMin
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (EdgeX e : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(e.other(i));
        Tuple4<Double,Double,Double,Double> t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (mst4.contains(t4)) continue;         
        else if (mst.contains(t) && mst.contains(t2)) {
          d.setPenColor(LIGHT_GRAY);  // invalid edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else if (cpq4.contains(t4)) {
          d.setPenColor(RED);         // crossing edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else {
          d.setPenColor(BLACK);       // unprocessed edge
          d.line(t._1, t._2, t2._1, t2._2);
        }
      }
    }

    // draw pqMin edge in thick red
    d.setPenColor(RED);
    d.setPenRadius(.02);
    if (!title.equals("Final")) d.line(pqMin._1, pqMin._2, pqMin._3, pqMin._4);    

    // draw  filled circles at coords of vertices
    // white if in MST else gray

    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(WHITE);
        if (mstv.contains(t)) d.filledCircle(x, y, 2);
        else {
          d.setPenColor(LIGHT_GRAY);
          d.filledCircle(x, y, 2);
        }
      }
    }

    // draw black unfilled circles at vertex coords
    d.setPenColor(BLACK);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.circle(x, y, 2);
      }
    }

    // draw labels at vertex coords
    d.setPenColor(BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.text(x, y, labels[i]);
      }
    }

    // draw title text
    d.textLeft(5,95,title); 

    // draw graph legend  
    d.setPenRadius(.002);
    d.setPenColor(BLACK);
    d.circle(21.5, 64, 2);
    d.text(21.5, 64, "v");
    d.textLeft(26,64,"vertex in MST");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in MST");

    d.setPenRadius(.02);
    d.setPenColor(RED);
    d.line(20, 60.5, 23, 60.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,60.5,"edge at top of MinPQ");

    d.setPenRadius(.02);
    d.setPenColor(BLACK);
    d.line(55, 60.5, 58, 60.5);    
    d.setPenRadius(.004);
    d.textLeft(61,60.5,"edge int MST");

    d.setPenRadius(.006);
    d.setPenColor(RED);
    d.line(19.75, 57, 23.5, 57);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,57,"crossing edge");

    d.setPenRadius(.006);
    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 57, 58.5, 57);
    d.setPenRadius(.004);
    d.setPenColor(BLACK);
    d.textLeft(61,57,"ineligible edge");

    d.setPenRadius(.006);
    d.line(19.75, 53.5, 23.5, 53.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,53.5,"unprocessed edge");

    // draw content text
    int indent = 20, yaxis = 50;
    for (int i = 3; i < content.length; i++)
      d.textLeft(indent, yaxis-=3, content[i]);
  }

  public Draw showLazyPrimMSTtrace(String[] labels, String[] content, String title) {
    // for ex4318; uses edu.princeton.cs.algs4.Draw since it can display 
    // multiple drawings at a time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels == null) throw new IllegalArgumentException(
        "showLazyPrimMSTtrace: labels == null");
    if (content == null) throw new IllegalArgumentException(
        "showLazyPrimMSTtrace: content == null");
    if (title == null) throw new IllegalArgumentException(
        "showLazyPrimMSTtrace: title == null"); 
    if (labels.length != V) throw new IllegalArgumentException(
        "showLazyPrimMSTtrace: labels.length != V == "+V);
    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2; 
    double x,y; 
    Tuple4<Double,Double,Double,Double> pqMin = null;
    boolean first = true;

    // assign pqMin; get other edges in MinPQ and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cpq4 = new HashSET<>(); // pq edges
    if (content[0].length() > 0) {
      String[] cpqs = content[0].replaceAll("\\)|\\(", "").split(",");
      for (String s : cpqs) {
        String[] sedge = s.split("-");
        t = coords.get(Integer.parseInt(sedge[0]));
        t2 = coords.get(Integer.parseInt(sedge[1]));
        if (first) { 
          pqMin = new Tuple4<>(t._1, t._2, t2._1, t2._2);
          first = false;    
        } 
        cpq4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
        cpq4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
      }
    }

    // get MST vertices after 1 pass through while loop and save their coords
    HashSET<Tuple2<Double,Double>> mstv = new HashSET<>(); 
    if (content[1].length() > 0) {
      String[] sa = content[1].replaceAll("\\)|\\(", "").split(",");
      for (String s : sa) mstv.add(coords.get(Integer.parseInt(s)));
    }

    // get MST edges after 1 pass through while loop,draw them in thick black
    // and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.02);
    HashSET<Tuple2<Double,Double>> mst = new HashSET<>(); // mst vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> mst4 = new HashSET<>(); // mst edges
    if (content[2].length() > 0) {
      String[] msts = content[2].replaceAll("\\)|\\(", "").split(",");
      for (String s : msts) {
        String[] sedge = s.split("-");
        t = coords.get(Integer.parseInt(sedge[0]));
        t2 = coords.get(Integer.parseInt(sedge[1]));
        d.line(t._1, t._2, t2._1, t2._2);
        mst.add(t); mst.add(t2);
        mst4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
        mst4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
      }
    }

    // draw all other edges except pqMin
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (EdgeX e : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(e.other(i));
        Tuple4<Double,Double,Double,Double> t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (mst4.contains(t4)) continue;         
        else if (mst.contains(t) && mst.contains(t2)) {
          d.setPenColor(LIGHT_GRAY);  // invalid edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else if (cpq4.contains(t4)) {
          d.setPenColor(RED);         // crossing edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else {
          d.setPenColor(BLACK);       // unprocessed edge
          d.line(t._1, t._2, t2._1, t2._2);
        }
      }
    }

    // draw pqMin edge in thick red
    d.setPenColor(RED);
    d.setPenRadius(.02);
    if (pqMin != null && !title.equals("Final")) d.line(pqMin._1, pqMin._2, pqMin._3, pqMin._4);    

    // draw  filled circles at coords of vertices
    // white if in MST else gray

    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(WHITE);
        if (mstv.contains(t)) d.filledCircle(x, y, 2);
        else {
          d.setPenColor(LIGHT_GRAY);
          d.filledCircle(x, y, 2);
        }
      }
    }

    // draw black unfilled circles at vertex coords
    d.setPenColor(BLACK);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.circle(x, y, 2);
      }
    }

    // draw labels at vertex coords
    d.setPenColor(BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.text(x, y, labels[i]);
      }
    }

    // draw title text
    d.textLeft(5,95,title); 

    // draw graph legend  
    d.setPenRadius(.002);
    d.setPenColor(BLACK);
    d.circle(21.5, 64, 2);
    d.text(21.5, 64, "v");
    d.textLeft(26,64,"vertex in MST");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in MST");

    d.setPenRadius(.02);
    d.setPenColor(RED);
    d.line(20, 60.5, 23, 60.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,60.5,"edge at top of MinPQ");

    d.setPenRadius(.02);
    d.setPenColor(BLACK);
    d.line(55, 60.5, 58, 60.5);    
    d.setPenRadius(.004);
    d.textLeft(61,60.5,"edge in MST");

    d.setPenRadius(.006);
    d.setPenColor(RED);
    d.line(19.5, 57, 23.25, 57);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,57,"crossing edge");

    d.setPenRadius(.006);
    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 57, 58.5, 57);
    d.setPenRadius(.004);
    d.setPenColor(BLACK);
    d.textLeft(61,57,"ineligible edge");

    d.setPenRadius(.006);
    d.line(19.5, 53.5, 23.25, 53.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,53.5,"unprocessed edge");

    // draw content text
    double indent = 19.4, yaxis = 50;
    for (int i = 3; i < content.length; i++)
      d.textLeft(indent, yaxis-=3, content[i]);
    if (title.equals("Final")) {
      d.textLeft(indent, yaxis-=3, "");
      d.textLeft(indent, yaxis-=3, "Processing stops here since the MinPQ is empty.");
    }
    return d;
  }

  public Draw showEagerPrimMSTtrace(String[] labels, 
      Tuple9<Integer,EdgeY,Seq<Integer>,Seq<Integer>,Seq<EdgeY>,
      Seq<EdgeY>,EdgeY[],double[],boolean[]> data, String title) {
    // for ex4318; uses edu.princeton.cs.algs4.Draw since it can display 
    // multiple drawings at a time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: labels == null");    
    if (labels.length != V) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: labels.length != V == "+V);
    if (data == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data == null");
    if (title == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: title == null");
    if (data._1 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._1 == null");
    // data._2 may be null
    if (data._3 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._4 == null");
    if (data._4 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._4 == null");
    if (data._5 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._5 == null");
    if (data._6 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._6 == null");
    if (data._7 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._7 == null");
    // data._8 && data._9 aren't used
    if (data._8 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._8 == null");
    if (data._9 == null) throw new IllegalArgumentException(
        "showEagerPrimMSTtrace: data._9 == null");
    Integer v          = data._1;
    EdgeY pqMinEdge    = data._2;
    Seq<Integer> pq    = data._3;
    Seq<Integer> mstvv = data._4;
    Seq<EdgeY> mste    = data._5;
    Seq<EdgeY> inel    = data._6;
    EdgeY[] edgeTo     = data._7;
    @SuppressWarnings("unused")
    double[] distTo    = data._8;
    @SuppressWarnings("unused")
    boolean[] marked   = data._9;

    // convert mste to Seq<String> for final output
    Seq<String> msts = new Seq<>();
    for (EdgeY e : mste) msts.add(e.toString3());

    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2;  Tuple4<Double,Double,Double,Double> t4, t4r;
    double x,y; 

    // set pqMin coords
    Tuple4<Double,Double,Double,Double> pqMin = null;
    if (pqMinEdge != null) {
      t = coords.get(pqMinEdge.u());
      t2 = coords.get(pqMinEdge.v());
      pqMin = new Tuple4<>(t._1, t._2, t2._1, t2._2);
    }

    // get other edges in edgeTo and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cpq4 = new HashSET<>(); // edgeTo edges
    for (EdgeY e : edgeTo) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        t4r = new Tuple4<>(t2._1, t2._2, t._1, t._2);
        if (pqMin != null && t4.equals(pqMin) && t4r.equals(pqMin)) continue;
        cpq4.add(t4);
        cpq4.add(t4r);
      }
    }

    // get ineligible edges from inel and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cinel4 = new HashSET<>(); 
    for (EdgeY e : inel) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        t4r = new Tuple4<>(t2._1, t2._2, t._1, t._2);
        if (pqMin != null && t4.equals(pqMin) && t4r.equals(pqMin)) continue;
        cinel4.add(t4);
        cinel4.add(t4r);
      }
    }

    // get MST vertices after 1 pass through while loop and save their coords
    HashSET<Tuple2<Double,Double>> mstv = new HashSET<>(); 
    for (Integer i : mstvv) mstv.add(coords.get(i));

    // get MST edges and draw them in thick black
    // and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.02);
    HashSET<Tuple2<Double,Double>> mst = new HashSET<>(); // mst vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> mst4 = new HashSET<>(); // mst edges
    for (EdgeY e : mste) {
      t = coords.get(e.u());
      t2 = coords.get(e.v());
      d.line(t._1, t._2, t2._1, t2._2);
      mst.add(t); mst.add(t2);
      mst4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
      mst4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
    }

    // draw all other edges except pqMin
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (EdgeX e : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(e.other(i));
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (mst4.contains(t4)) continue;         
        else if (mst.contains(t) && mst.contains(t2) || cinel4.contains(t4)) {
          d.setPenColor(LIGHT_GRAY);  // ineligible edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else if (cpq4.contains(t4)) {
          d.setPenColor(RED);         // crossing edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else {
          d.setPenColor(BLACK);       // unprocessed edge
          d.line(t._1, t._2, t2._1, t2._2);
        }
      }
    }

    // draw pqMin edge in thick red if nonnull and title isn't "Final"
    d.setPenColor(RED);
    d.setPenRadius(.02);
    if (pqMin != null && !title.equals("Final")) d.line(pqMin._1, pqMin._2, pqMin._3, pqMin._4);    

    // draw  filled circles at coords of vertices
    // white if in MST else gray    
    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(WHITE);
        if (mstv.contains(t)) d.filledCircle(x, y, 2);
        else {
          d.setPenColor(LIGHT_GRAY);
          d.filledCircle(x, y, 2);
        }
      }
    }

    // draw black unfilled circles at vertex coords
    d.setPenColor(BLACK);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.circle(x, y, 2);
      }
    }

    // draw labels at vertex coords
    d.setPenColor(BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.text(x, y, labels[i]);
      }
    }

    // draw title text
    d.textLeft(5,95,title); 

    // draw graph legend  
    d.setPenRadius(.002);
    d.setPenColor(BLACK);
    d.circle(21.5, 64, 2);
    d.text(21.5, 64, "v");
    d.textLeft(26,64,"vertex in MST");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in MST");

    d.setPenRadius(.02);
    d.setPenColor(RED);
    d.line(20, 60.5, 23, 60.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,60.5,"next edge for MST");

    d.setPenRadius(.02);
    d.setPenColor(BLACK);
    d.line(55, 60.5, 58, 60.5);    
    d.setPenRadius(.004);
    d.textLeft(61,60.5, "edge in MST");

    d.setPenRadius(.006);
    d.setPenColor(RED);
    d.line(19.5, 57, 23.25, 57);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,57,"crossing edge");

    d.setPenRadius(.006);
    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 57, 58.5, 57);
    d.setPenRadius(.004);
    d.setPenColor(BLACK);
    d.textLeft(61,57,"ineligible edge");

    d.setPenRadius(.006);
    d.line(19.5, 53.5, 23.25, 53.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,53.5,"unprocessed edge");

    // draw content text
    double indent = 19.4, yaxis = 51;
    if (v > -1 && !title.equals("Final"))
      d.textLeft(indent, yaxis-=4, "current vertex in call to visit():  "+v);
    d.textLeft(indent, yaxis-=4, "IndexMinPQ:  "+pq.toString());
    //    d.textLeft(indent, yaxis-=3, "");
    d.textLeft(indent, yaxis-=4, "MST vertices: "+mstvv.toString().replaceAll(",",", "));
    d.textLeft(indent, yaxis-=4, "MST edges:    "+msts.toString().replaceAll(",",", "));
    //    d.textLeft(indent, yaxis-=3, "");
    if (v > -1 && !title.equals("Final")) {
      d.textLeft(indent, yaxis-=4, "edgeTo[]:");
      for (int i = 0; i < V; i++)
        if (edgeTo[i] == null) d.textLeft(indent, yaxis-=3, ""+i);
        else d.textLeft(indent,yaxis-=3,i+"  "+edgeTo[i].toString().replaceAll(",",", "));
    }
    if (title.equals("Final")) {
      d.textLeft(indent, yaxis-=3, "");
      d.textLeft(indent, yaxis-=3, "Processing stops here since the IndexMinPQ is empty.");
    }
    return d;
  }

  public Draw showKruskalMSTtrace(String[] labels, 
      Tuple6<EdgeY,Seq<Integer>,Seq<EdgeY>,Seq<EdgeY>,Seq<EdgeY>,Seq<EdgeY>> data, String title) {
    // for ex4318; uses edu.princeton.cs.algs4.Draw since it can display 
    // multiple drawings at a time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: labels == null");    
    if (labels.length != V) throw new IllegalArgumentException(
        "showKruskalMSTtrace: labels.length != V == "+V);
    if (data == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data == null");
    if (title == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: title == null");
    // data._1 may be null
    if (data._2 == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data._2 == null");
    if (data._3 == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data._4 == null");
    if (data._4 == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data._4 == null");
    if (data._5 == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data._5 == null");
    if (data._6 == null) throw new IllegalArgumentException(
        "showKruskalMSTtrace: data._6 == null");
    EdgeY         pqMinEdge = data._1; // edge at top of minPQ
    Seq<Integer>  mstvv     = data._2; // vertices in MST
    Seq<EdgeY>    mste      = data._3; // edges in MST
    Seq<EdgeY>    inel      = data._4; // ineligible edges
    Seq<EdgeY>    edges     = data._5; // all edges
    Seq<EdgeY>    pqe       = data._6; // minPQ contents

    // convert mste to Seq<String> for final output
    Seq<String> msts = new Seq<>();
    for (EdgeY e : mste) msts.add(e.toString3());

    // convert pqe to Seq<String> for final output
    Seq<String> pqs = new Seq<>();
    for (EdgeY e : pqe) pqs.add(e.toString4());

    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2;  Tuple4<Double,Double,Double,Double> t4, t4r;
    double x,y; 

    // set pqMin coords
    Tuple4<Double,Double,Double,Double> pqMin = null;
    if (pqMinEdge != null) {
      t = coords.get(pqMinEdge.u());
      t2 = coords.get(pqMinEdge.v());
      pqMin = new Tuple4<>(t._1, t._2, t2._1, t2._2);
    }

    // get other edges and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cpq4 = new HashSET<>(); // edgeTo edges
    for (EdgeY e : edges) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        t4r = new Tuple4<>(t2._1, t2._2, t._1, t._2);
        if (pqMin != null && t4.equals(pqMin) && t4r.equals(pqMin)) continue;
        cpq4.add(t4);
        cpq4.add(t4r);
      }
    }

    // get ineligible edges from inel and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cinel4 = new HashSET<>(); 
    for (EdgeY e : inel) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        t4r = new Tuple4<>(t2._1, t2._2, t._1, t._2);
        if (pqMin != null && t4.equals(pqMin) && t4r.equals(pqMin)) continue;
        cinel4.add(t4);
        cinel4.add(t4r);
      }
    }

    // get MST vertices after 1 pass through while loop and save their coords
    HashSET<Tuple2<Double,Double>> mstv = new HashSET<>(); 
    for (Integer i : mstvv) mstv.add(coords.get(i));

    // get MST edges and draw them in thick black
    // and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.02);
    HashSET<Tuple2<Double,Double>> mst = new HashSET<>(); // mst vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> mst4 = new HashSET<>(); // mst edges
    for (EdgeY e : mste) {
      t = coords.get(e.u());
      t2 = coords.get(e.v());
      d.line(t._1, t._2, t2._1, t2._2);
      mst.add(t); mst.add(t2);
      mst4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
      mst4.add(new Tuple4<>(t2._1, t2._2, t._1, t._2));
    }

    // draw all other edges except pqMin
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (EdgeX e : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        int j = e.other(i);
        t2 = coords.get(j);
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (mst4.contains(t4)) continue;         
        else if (cinel4.contains(t4)) {
          d.setPenColor(LIGHT_GRAY);  // ineligible edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else if (mstvv.contains(i) && !mstvv.contains(j) 
            || mstvv.contains(j) && !mstvv.contains(i)) {
          d.setPenColor(RED);         // crossing edge
          d.line(t._1, t._2, t2._1, t2._2);
        } else {
          d.setPenColor(BLACK);       // unprocessed edge
          d.line(t._1, t._2, t2._1, t2._2);
        }
      }
    }

    // draw pqMin edge in thick red if nonnull and title isn't "Final"
    d.setPenColor(RED);
    d.setPenRadius(.02);
    if (pqMin != null) d.line(pqMin._1, pqMin._2, pqMin._3, pqMin._4);    

    // draw  filled circles at coords of vertices
    // white if in MST else gray    
    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(WHITE);
        if (mstv.contains(t)) d.filledCircle(x, y, 2);
        else {
          d.setPenColor(LIGHT_GRAY);
          d.filledCircle(x, y, 2);
        }
      }
    }

    // draw black unfilled circles at vertex coords
    d.setPenColor(BLACK);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.circle(x, y, 2);
      }
    }

    // draw labels at vertex coords
    d.setPenColor(BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.text(x, y, labels[i]);
      }
    }

    // draw title text
    d.textLeft(5,95,title); 

    // draw graph legend  
    d.setPenRadius(.002);
    d.setPenColor(BLACK);
    d.circle(21.5, 64, 2);
    d.text(21.5, 64, "v");
    d.textLeft(26,64,"vertex in MST");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in MST");

    d.setPenRadius(.02);
    d.setPenColor(RED);
    d.line(20, 60.5, 23, 60.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,60.5,"edge at top of MinPQ");

    d.setPenRadius(.02);
    d.setPenColor(BLACK);
    d.line(55, 60.5, 58, 60.5);    
    d.setPenRadius(.004);
    d.textLeft(61,60.5, "edge in MST");

    d.setPenRadius(.006);
    d.setPenColor(RED);
    d.line(19.5, 57, 23.25, 57);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,57,"crossing edge");

    d.setPenRadius(.006);
    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 57, 58.5, 57);
    d.setPenRadius(.004);
    d.setPenColor(BLACK);
    d.textLeft(61,57,"rejected edge");

    d.setPenRadius(.006);
    d.line(19.5, 53.5, 23.25, 53.5);
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    d.textLeft(26,53.5,"unprocessed edge");

    // draw content text
    double indent = 19.4, yaxis = 51;
    if (pqe.isEmpty())  d.textLeft(indent, yaxis-=3, "minPQ: (is empty");
    else {
      if (title.equals("Step 1"))
        d.textLeft(indent, yaxis-=3, "minPQ: (contains all edges)");
      else d.textLeft(indent, yaxis-=3, "minPQ:");
      d.setPenRadius(.003);
      for (String s : pqs) d.textLeft(indent, yaxis-=2, s.substring(1,s.length()-1));
      d.setPenRadius(.004);
    }
    d.textLeft(indent, yaxis-=3, "");
    d.textLeft(indent, yaxis-=3, "MST vertices: "+mstvv.toString().replaceAll(",",", "));
    d.textLeft(indent, yaxis-=3, "MST edges:    "+msts.toString().replaceAll(",",", "));
    if (title == "Final") {
      d.textLeft(indent, yaxis-=3, "");
      d.textLeft(indent, yaxis-=3, "Processing stops here since the number of edges in the");
      d.textLeft(indent, yaxis-=3, "MST is one less than the number of vertices in the graph.");
    }
    return d; 
  }

  public void showCC() {
    // show up to 5 connected components
    search();
    if (count > 5) { show(); return; }
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 115117; i++) r.nextDouble();
    int a, b, c, d, e, sign; int[] counts; double x,y; double[] xa, ya;
    Tuple2<Double,Double>[] zero, one, two, three, four;  
    Seq<Tuple2<Double,Double>> ords = new Seq<>();  
    if (count == 2) {
      counts = new int[2];
      for (int i = 0; i < id.length; i++) 
        if (id[i] == 0) counts[0]++; else counts[1]++;
      zero = ofDim(Tuple2.class, counts[0]);
      xa = r.doubles(zero.length,20,80).toArray();
      ya = r.doubles(zero.length,52,90).toArray();
      for (int i = 0; i < zero.length; i++) zero[i] = new Tuple2<Double,Double>(xa[i], ya[i]);
      one = ofDim(Tuple2.class, counts[1]);
      xa = r.doubles(one.length,20,80).toArray();
      ya = r.doubles(one.length,2,40).toArray();
      for (int i = 0; i < one.length; i++) one[i] = new Tuple2<Double,Double>(xa[i], ya[i]);
      a = b = 0;
      for (int i = 0; i < id.length; i++)
        if (id[i] == 0) ords.add(zero[a++]);
        else ords.add(one[b++]);
      coords = ords;
    } else if (count == 3) {     
      counts = new int[3];
      for (int i = 0; i < id.length; i++) 
        if (id[i] == 0) counts[0]++; 
        else if (id[i] == 1)  counts[1]++;
        else counts[2]++;
      zero = ofDim(Tuple2.class, counts[0]);
      xa = r.doubles(zero.length,1,18.75).toArray();
      ya = r.doubles(zero.length,1,18.75).toArray();
      for (int i = 0; i < zero.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 50. + xa[i] : 50. - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 78.125 - ya[i] : 78.125 + ya[i];
        zero[i] = new Tuple2<Double,Double>(x, y);
      }
      one = ofDim(Tuple2.class, counts[1]);
      xa = r.doubles(one.length,1,18.75).toArray();
      ya = r.doubles(one.length,1,18.75).toArray();
      for (int i = 0; i < one.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 21.875 - xa[i] : 21.875 + xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 21.875 - ya[i] : 21.875 + ya[i];
        one[i] = new Tuple2<Double,Double>(x, y);
      }
      two = ofDim(Tuple2.class, counts[2]);
      xa = r.doubles(two.length,1,18.75).toArray();
      ya = r.doubles(two.length,1,18.75).toArray();
      for (int i = 0; i < two.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 78.125 - xa[i] : 78.125 + xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 21.875 + ya[i] : 21.875 - ya[i];
        two[i] = new Tuple2<Double,Double>(x, y);
      }
      a = b = c = 0;
      for (int i = 0; i < id.length; i++)
        if (id[i] == 0) ords.add(zero[a++]);
        else if (id[i] == 1) ords.add(one[b++]);
        else ords.add(two[c++]);
      coords = ords;
    } else if (count == 4) {     
      counts = new int[4];
      for (int i = 0; i < id.length; i++) 
        if (id[i] == 0) counts[0]++; 
        else if (id[i] == 1)  counts[1]++;
        else if (id[i] == 2)  counts[2]++;
        else counts[3]++;
      zero = ofDim(Tuple2.class, counts[0]);
      xa = r.doubles(zero.length,1,18.75).toArray();
      ya = r.doubles(zero.length,1,18.75).toArray();
      for (int i = 0; i < zero.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 21.875 + xa[i] : 21.875 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 78.125 - ya[i] : 78.125 + ya[i];
        zero[i] = new Tuple2<Double,Double>(x, y);
      }
      one = ofDim(Tuple2.class, counts[1]);
      xa = r.doubles(one.length,1,18.75).toArray();
      ya = r.doubles(one.length,1,18.75).toArray();
      for (int i = 0; i < one.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 78.125 + xa[i] : 78.125 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 78.125 - ya[i] : 78.125 + ya[i];
        one[i] = new Tuple2<Double,Double>(x, y);
      }
      two = ofDim(Tuple2.class, counts[2]);
      xa = r.doubles(two.length,1,18.75).toArray();
      ya = r.doubles(two.length,1,18.75).toArray();
      for (int i = 0; i < two.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 21.875 - xa[i] : 21.875 + xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 21.875 + ya[i] : 21.875 - ya[i];
        two[i] = new Tuple2<Double,Double>(x, y);
      }
      three = ofDim(Tuple2.class, counts[3]);
      xa = r.doubles(three.length,1,18.75).toArray();
      ya = r.doubles(three.length,1,18.75).toArray();
      for (int i = 0; i < three.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 78.125 + xa[i] : 78.125 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 21.875 - ya[i] : 21.875 + ya[i];
        three[i] = new Tuple2<Double,Double>(x, y);
      }
      a = b = c = d = 0;
      for (int i = 0; i < id.length; i++)
        if (id[i] == 0) ords.add(zero[a++]);
        else if (id[i] == 1) ords.add(one[b++]);
        else if (id[i] == 2) ords.add(two[c++]);
        else ords.add(three[d++]);
      coords = ords;
    }  else if (count == 5) {     
      counts = new int[5];
      for (int i = 0; i < id.length; i++) 
        if (id[i] == 0) counts[0]++; 
        else if (id[i] == 1)  counts[1]++;
        else if (id[i] == 2)  counts[2]++;
        else if (id[i] == 3)  counts[3]++;
        else counts[4]++;
      zero = ofDim(Tuple2.class, counts[0]);
      xa = r.doubles(zero.length,1,15).toArray();
      ya = r.doubles(zero.length,1,15).toArray();
      for (int i = 0; i < zero.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 19.875 + xa[i] : 19.875 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 80.125 - ya[i] : 80.125 + ya[i];
        zero[i] = new Tuple2<Double,Double>(x, y);
      }
      one = ofDim(Tuple2.class, counts[1]);
      xa = r.doubles(one.length,1,15).toArray();
      ya = r.doubles(one.length,1,15).toArray();
      for (int i = 0; i < one.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 80.125 + xa[i] : 80.125 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 80.125 - ya[i] : 80.125 + ya[i];
        one[i] = new Tuple2<Double,Double>(x, y);
      }
      two = ofDim(Tuple2.class, counts[2]);
      xa = r.doubles(two.length,1,15).toArray();
      ya = r.doubles(two.length,1,15).toArray();
      for (int i = 0; i < two.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 19.875 - xa[i] : 19.875 + xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 19.875 + ya[i] : 19.875 - ya[i];
        two[i] = new Tuple2<Double,Double>(x, y);
      }
      three = ofDim(Tuple2.class, counts[3]);
      xa = r.doubles(three.length,1,15).toArray();
      ya = r.doubles(three.length,1,15).toArray();
      for (int i = 0; i < three.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 80.125 + xa[i] : 80.125 - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 19.875 - ya[i] : 19.875 + ya[i];
        three[i] = new Tuple2<Double,Double>(x, y);
      }
      four = ofDim(Tuple2.class, counts[4]);
      xa = r.doubles(four.length,1,15).toArray();
      ya = r.doubles(four.length,1,15).toArray();
      for (int i = 0; i < four.length; i++) {
        sign = r.nextInt(2);
        x = sign == 0 ? 48. + xa[i] : 48. - xa[i];
        sign = r.nextInt(2);
        y  = sign == 0 ? 48. - ya[i] : 48. + ya[i];
        four[i] = new Tuple2<Double,Double>(x, y);
      }
      a = b = c = d = e = 0;
      for (int i = 0; i < id.length; i++)
        if (id[i] == 0) ords.add(zero[a++]);
        else if (id[i] == 1) ords.add(one[b++]);
        else if (id[i] == 2) ords.add(two[c++]);
        else if (id[i] == 3) ords.add(three[d++]);
        else ords.add(four[e++]);
      coords = ords;
    }
    double maxx, maxy; maxx = maxy = Double.NEGATIVE_INFINITY;
    double minx, miny; minx = miny = Double.POSITIVE_INFINITY;
    for (Tuple2<Double,Double> t : coords) {
      if (t != null) {
        if (t._1 != null) {
          if (t._1 > maxx) maxx = t._1;
          if (t._1 < minx) minx = t._1;
        }
        if (t._2 != null) {
          if (t._2 > maxy) maxy = t._2;
          if (t._2 < miny) miny = t._2;
        }       
      }
    }   
    StdDraw.setCanvasSize(800, 800); double sf = 10.;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (EdgeX ed : adj.get(i)) points[i].drawTo(points[ed.other(i)]);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(0.03);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public void showGrid() {
    // used in RandomGridGraph.main(), etc.
    double maxx, maxy; maxx = maxy = Double.NEGATIVE_INFINITY;
    double minx, miny; minx = miny = Double.POSITIVE_INFINITY;
    for (Tuple2<Double,Double> t : coords) {
      if (t != null) {
        if (t._1 != null) {
          if (t._1 > maxx) maxx = t._1;
          if (t._1 < minx) minx = t._1;
        }
        if (t._2 != null) {
          if (t._2 > maxy) maxy = t._2;
          if (t._2 < miny) miny = t._2;
        }       
      }
    }   
    StdDraw.setCanvasSize(800, 800); double sf = .25;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (EdgeX e : adj.get(i)) points[i].drawTo(points[e.other(i)]);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public void showGrid(HashSET<Tuple2<Integer,Integer>> extraEdges) {
    // used in RandomGridGraph.main(), etc.
    double maxx, maxy; maxx = maxy = Double.NEGATIVE_INFINITY;
    double minx, miny; minx = miny = Double.POSITIVE_INFINITY;
    for (Tuple2<Double,Double> t : coords) {
      if (t != null) {
        if (t._1 != null) {
          if (t._1 > maxx) maxx = t._1;
          if (t._1 < minx) minx = t._1;
        }
        if (t._2 != null) {
          if (t._2 > maxy) maxy = t._2;
          if (t._2 < miny) miny = t._2;
        }       
      }
    }   
    StdDraw.setCanvasSize(800, 800); double sf = .25;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (EdgeX e : adj.get(i)) {
        int j = e.other(i);
        if (!(extraEdges.contains(new Tuple2<>(i,j)))) 
          points[i].drawTo(points[j]);
      }

    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.setPenRadius(.003);
    for (Tuple2<Integer,Integer> t2 : extraEdges) 
      points[t2._1].drawTo(points[t2._2]);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public void showGrid(HashSET<Tuple2<Integer,Integer>> extraEdges, 
      HashSET<Tuple2<Integer,Integer>> pEdges) {
    // used in RandomGridGraph.main(), etc.
    double maxx, maxy; maxx = maxy = Double.NEGATIVE_INFINITY;
    double minx, miny; minx = miny = Double.POSITIVE_INFINITY;
    for (Tuple2<Double,Double> t : coords) {
      if (t != null) {
        if (t._1 != null) {
          if (t._1 > maxx) maxx = t._1;
          if (t._1 < minx) minx = t._1;
        }
        if (t._2 != null) {
          if (t._2 > maxy) maxy = t._2;
          if (t._2 < miny) miny = t._2;
        }       
      }
    }   
    StdDraw.setCanvasSize(800, 800); double sf = .25;
    StdDraw.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    StdDraw.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
      }
    }
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++) {
      for (EdgeX e : adj.get(i)) {
        int j = e.other(i);
        Tuple2<Integer,Integer> t1 = new Tuple2<>(i,j);
        Tuple2<Integer,Integer> t2 = new Tuple2<>(j,i);
        if (!(extraEdges.contains(t1) || extraEdges.contains(t2)
            || pEdges.contains(t1) || pEdges.contains(t2)))
          points[i].drawTo(points[j]);
      }
    }

    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.setPenRadius(.003);
    for (Tuple2<Integer,Integer> t2 : extraEdges) {
      if (!(pEdges.contains(t2))) 
        points[t2._1].drawTo(points[t2._2]);
    }

    StdDraw.setPenColor(StdDraw.ORANGE);
    StdDraw.setPenRadius(.003);
    for (Tuple2<Integer,Integer> t2 : pEdges) points[t2._1].drawTo(points[t2._2]);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public static void main(String[] args) {
    
    // V and edges for tinyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
        + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
        + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    String[] edgs = edges.split("\\s+");

    int u,v; double w;
    Seq<EdgeX> edgSeq = new Seq<>();
    for (int i = 0; i < edgs.length-2; i+=3) {
      u = Integer.parseInt(edgs[i]);
      v = Integer.parseInt(edgs[i+1]);
      w = Double.parseDouble(edgs[i+2]);
      EdgeX e = new EdgeX(u,v,w);
      edgSeq.add(e);
    }

    double[] coordAr = {45,77.5,42.5,95,58,82,58,93.5,21.5,70,21.6,92.5,77.5,70,37,85};

    double[][] coordAr2 = new double[V][2];
    coordAr2[0] = new double[]{45,77.5};
    coordAr2[1] = new double[]{42.5,95};
    coordAr2[2] = new double[]{58,82};
    coordAr2[3] = new double[]{58,93.5};
    coordAr2[4] = new double[]{21.5,70};
    coordAr2[5] = new double[]{21.6,92.5};
    coordAr2[6] = new double[]{77.5,70};
    coordAr2[7] = new double[]{37,85};

    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < V; i++) 
      coords.add(new Tuple2<Double,Double>(coordAr2[i][0],coordAr2[i][1])); 

    System.out.println("EuclidianGraph constructor testing:\n");

    EuclidianEdgeWeightedGraph g;

    System.out.println("1: testing new EuclidianEdgeWeightedGraph()");
    g = new EuclidianEdgeWeightedGraph();
    for (EdgeX e : edgSeq) g.insertEdge(e);
    int c = 0;
    for (int i = 0; i < coordAr.length-1; i+=2) g.addCoords(c++,coordAr[i],coordAr[i+1]);
    System.out.println(g);
    g.search();
    System.out.println("cycle = "+g.cycle());
    System.out.println("parallelEdges = "+g.parallelEdges());
    System.out.println("selfLoop = "+g.selfLoop()+"\n");

    System.out.println("2: testing new EuclidianEdgeWeightedGraph(int)");
    g = new EuclidianEdgeWeightedGraph(V);
    for (EdgeX e : edgSeq) g.insertEdge(e);
    c = 0;
    for (int i = 0; i < coordAr.length-1; i+=2) g.addCoords(c++,coordAr[i],coordAr[i+1]);
    System.out.println(g);

    System.out.println("3: testing new EuclidianEdgeWeightedGraph(int,int)  {this generates random coordinates}");
    g = new EuclidianEdgeWeightedGraph(7,15);
    System.out.println(g);

    System.out.println("4: testing new EuclidianEdgeWeightedGraph(int,String,double[][])");
    g = new EuclidianEdgeWeightedGraph(V, edges, coordAr2);
    System.out.println(g);

    System.out.println("5: testing new EuclidianEdgeWeightedGraph(int v,Iterable<EdgeX>,Iterable<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedGraph(V, edgSeq, coords);
    System.out.println(g);

    System.out.println("6: testing new EuclidianEdgeWeightedGraph(Iterable<EdgeX>,Iterable<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedGraph(edgSeq, coords);
    System.out.println(g);
    
    System.out.println("7: testing new EuclidianEdgeWeightedGraph(In,Iterable<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedGraph(new In("tinyEWG.txt"), coords);
    System.out.println(g);
    
    System.out.println("8: testing new EuclidianEdgeWeightedGraph(EdgeWeightedGraph,Iterable<Tuple2<Double,Double>>)");
    EdgeWeightedGraphX ewg = new EdgeWeightedGraphX(new In("tinyEWG.txt"));
    g = new EuclidianEdgeWeightedGraph(ewg, coords);
    System.out.println(g);
    g.show();
    
  }

}
