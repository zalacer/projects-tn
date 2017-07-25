package graph;

import static analysis.Draw.BLACK;
import static analysis.Draw.CYAN;
import static analysis.Draw.LIGHT_GRAY;
import static analysis.Draw.RED;
import static analysis.Draw.WHITE;
import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.awt.Color;
import java.awt.Font;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import analysis.Draw;
import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import st.HashSET;
import v.Tuple2;
import v.Tuple2C;
import v.Tuple4;
import v.Tuple7;
import v.Tuple8;
import v.Tuple9;

@SuppressWarnings("unused")
public class EuclidianEdgeWeightedDigraph {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int DEFAULTLEN = 17;
  private int V;  // number of vertices in this digraph
  private int E;  // number of edges in this digraph
  private Seq<BagX<DirectedEdgeX>> adj;
  private Seq<Tuple2<Double,Double>> coords;
  private Seq<Integer> indegree; // indegree.get(v) = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  private int[] id;             // id[v] = id of strong component containing v
  private int count;            // number of strongly-connected components
  private Seq<Seq<Integer>> scc; // strongly connected components
  public transient boolean validate = true;
  public boolean euclidianWeights = false;

  public EuclidianEdgeWeightedDigraph() {
    V = E = 0;
    adj = Seq.fill(DEFAULTLEN, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(DEFAULTLEN,()->0);
    coords = new Seq<>(V);
    validate = false;
    //    System.out.println(
    //        "insert edges with insertEdge(int,int) and coordinates with addCoords(int,double,double)"
    //        + "\nvertices without coordinates won't be graphed"
    //        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }

  public EuclidianEdgeWeightedDigraph(int V) {
    if (V < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: Number of vertices must be nonnegative");
    this.V = V; E = 0;
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    coords = Seq.fill(V, ()->new Tuple2<>());
    //    System.out.println(
    //        "add edges with addEdge(int,int) and coordindates with addCoords(int,double,double)"
    //        + "\nvertices without coordinates won't be graphed"
    //        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }

  public EuclidianEdgeWeightedDigraph(int V, int E) {
    this(V);
    // create random edges with random weights + random coordinates
    if (E < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: Number of edges must be nonnegative");
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    coords = new Seq<>();
    for (int i = 0; i < E; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      double weight = 0.01 * StdRandom.uniform(100);
      DirectedEdgeX e = new DirectedEdgeX(v, w, weight);
      validate = false;
      addEdge(e);
      validate = true;
    }
    for (int i = 0; i < V; i++) {
      double x = StdRandom.uniform(1,100);
      double y = StdRandom.uniform(1,100);
      coords.add(new Tuple2<>(x,y));
    }
    search();
  }

  public EuclidianEdgeWeightedDigraph(int V, String edges, double[] coordinates) {
    if (V < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: Number of vertices must be nonnegative");
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: edges is null");
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    if (coordinates.length < 2*V) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates.length is < 2*V ("+(2*V)+")");
    this.V = V; this.E = 0;
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    coords = new Seq<>();
    for (int i = 0; i < 2*V; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordinates[i],coordinates[i+1]));
    if (edges != null) {
      String[] edgs = edges.split("\\s+");      
      int u,v; double w;
      Seq<DirectedEdgeX> edgSeq = new Seq<>();
      for (int i = 0; i < edgs.length-2; i+=3) {
        u = Integer.parseInt(edgs[i]);
        v = Integer.parseInt(edgs[i+1]);
        w = Double.parseDouble(edgs[i+2]);
        DirectedEdgeX e = new DirectedEdgeX(u,v,w);
        addEdge(e);
      }
      search();
    }   
  }

  public EuclidianEdgeWeightedDigraph(int V, Seq<DirectedEdgeX> edges, 
      Seq<Tuple2<Double,Double>> coordinates) {
    if (V < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: Number of vertices must be nonnegative");
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: edges is null");
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates double[] is null");
    if (coordinates.size() < V) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates.size is < V ("+V+")");
    this.V = V; this.E = 0;
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    coords = coordinates.take(V);
    if (edges != null) {
      for (DirectedEdgeX de : edges) addEdge(de);
      search();
    }   
  }

  public EuclidianEdgeWeightedDigraph(int V, Iterable<DirectedEdgeX> edges, 
      Iterable<Tuple2<Double,Double>> coordinates) {
    if (V < 0) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: Number of vertices must be nonnegative");
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: edges is null");
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    Seq<Tuple2<Double,Double>> coordSeq = new Seq<>(coordinates);
    this.V = V; this.E = 0;
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    coords = new Seq<>();
    Iterator<Tuple2<Double,Double>> it = coordinates.iterator();
    int c = 0;
    while (c < V) { 
      if (!it.hasNext()) throw new IllegalArgumentException(
          "EuclidianEdgeWeightedDigraph: coordinates.size is < V ("+V+")");
      coords.add(it.next()); c++; 
    }
    if (edges != null) {
      for (DirectedEdgeX de : edges) addEdge(de);
      search();
    }   
  }

  public EuclidianEdgeWeightedDigraph(In in, Seq<Tuple2<Double,Double>> coordinates) {
    if (in == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: In is null");
    this.V = in.readInt();
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    if (coordinates.size() < V) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates.size is < V ("+V+")");
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertex(u,v);
      double w = in.readDouble();
      validate = false;
      addEdge(new DirectedEdgeX(u, v, w));
      validate = true;
    }
    coords = coordinates.take(V);
    search();
  }
  
  public EuclidianEdgeWeightedDigraph(In in, Seq<Tuple2<Double,Double>> coordinates,
      String ew) {
    // create an EuclidianEdgeWeightedDigraph with edge weights equal to euclidian
    // distance between from and to vertices overriding existing weights
    if (in == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: In is null");
    this.V = in.readInt();
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    if (coordinates.size() < V) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates.size is < V ("+V+")");
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    indegree = Seq.fill(V,()->0);
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertex(u,v);
      double w = in.readDouble();
      validate = false;
      addEdge(new DirectedEdgeX(u, v, w));
      validate = true;
    }
    coords = coordinates.take(V);
    // adjust edge weights to be based on euclidian distances between vertices
    for (DirectedEdgeX e : edges()) e.setW(euclidianDistance(e.from(),e.to()));
    search();
  }

  public EuclidianEdgeWeightedDigraph(EdgeWeightedDigraphX G, Seq<Tuple2<Double,Double>> coordinates) {
    if (G == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: EdgeWeightedDigraphX is null");
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    this.V = G.V();
    this.E = G.E();
    indegree = new Seq<>();
    for (int i : G.indegree()) indegree.add(i);
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeX> reverse = new Stack<>();
      for (DirectedEdgeX e : G.adj(v)) reverse.push(e);
      for (DirectedEdgeX e : reverse) adj.get(v).add(e);
    }
    coords = coordinates;
    search();
  }
  
  public EuclidianEdgeWeightedDigraph(EdgeWeightedDigraphX G, Seq<Tuple2<Double,Double>> coordinates,
      String ew) {
    // create an EuclidianEdgeWeightedDigraph with edge weights equal to euclidian
    // distance between from and to vertices overriding existing weights
    if (G == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: EdgeWeightedDigraphX is null");
    if (coordinates == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: coordinates is null");
    this.V = G.V();
    this.E = G.E();
    indegree = new Seq<>();
    for (int i : G.indegree()) indegree.add(i);
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeX> reverse = new Stack<>();
      for (DirectedEdgeX e : G.adj(v)) reverse.push(e);
      for (DirectedEdgeX e : reverse) adj.get(v).add(e);
    }
    coords = coordinates;
    // adjust edge weights to be based on euclidian distances between vertices
    for (DirectedEdgeX e : edges()) e.setW(euclidianDistance(e.from(),e.to()));
    search();
  }

  public EuclidianEdgeWeightedDigraph(EuclidianEdgeWeightedDigraph G) {
    if (G == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: EdgeWeightedDigraphX is null");
    this.V = G.V();
    this.E = G.E();
    indegree = G.indegree.clone();
    for (int i : G.indegree()) indegree.add(i);
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeX> reverse = new Stack<>();
      for (DirectedEdgeX e : G.adj(v)) reverse.push(e);
      for (DirectedEdgeX e : reverse) adj.get(v).add(e);
    }
    coords = new Seq<>(G.coords.size());
    for (Tuple2<Double,Double> t : G.coords) coords.add(new Tuple2<>(t));
    search();
  }
  
  public EuclidianEdgeWeightedDigraph(EuclidianEdgeWeightedDigraph G, String ew) {
    // create an EuclidianEdgeWeightedDigraph with edge weights equal to euclidian
    // distance between from and to vertices overriding existing weights
    if (G == null) throw new IllegalArgumentException(
        "EuclidianEdgeWeightedDigraph: EdgeWeightedDigraphX is null");
    this.V = G.V();
    this.E = G.E();
    indegree = G.indegree.clone();
    for (int i : G.indegree()) indegree.add(i);
    adj = Seq.fill(V, ()->new BagX<DirectedEdgeX>());
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<DirectedEdgeX> reverse = new Stack<>();
      for (DirectedEdgeX e : G.adj(v)) reverse.push(e);
      for (DirectedEdgeX e : reverse) adj.get(v).add(e);
    }
    coords = new Seq<>(G.coords.size());
    for (Tuple2<Double,Double> t : G.coords) coords.add(new Tuple2<>(t));
    // adjust edge weights to be based on euclidian distances between vertices
    for (DirectedEdgeX e : edges()) e.setW(euclidianDistance(e.from(),e.to()));
    search();
  }

  public int V() { return V; }

  public int E() { return E; }

  public Seq<BagX<DirectedEdgeX>> adj() { return adj; }

  public Seq<Tuple2<Double,Double>> coords() { return coords; }

  public Stack<Integer> cycle() { return cycle; }

  public Seq<Integer> indegree() { return indegree; }

  public int parallelEdgeCount() { 
    allParallelEdges();
    return parallelEdges == null ? 0 : parallelEdges.size(); }

  public Seq<Tuple2<Integer,Integer>> parallelEdges() {
    return allParallelEdges();
  }

  public int count() { return count; }

  public Seq<Seq<Integer>> scc() { return scc; }

  public int selfLoopCount() { 
    allSelfLoops();
    return selfLoops == null ? 0 : selfLoops.size(); 
  }

  public Seq<Integer> selfLoops() { return allSelfLoops(); }
  
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

  public void addEdge(DirectedEdgeX e) {
    int u = e.from();
    int v = e.to();
    if (validate) validateVertex(u,v);
    adj.get(u).add(e);
    indegree.set(v, indegree.get(v)+1);
    E++;
  }

  public void addEdge(int x, int y, double d) {
    //use this when V has been set finally (not using final)
    if (validate) validateVertex(x,y);
    DirectedEdgeX e = new DirectedEdgeX(x,y,d);
    adj.get(x).add(e);
    indegree.set(y, indegree.get(y)+1);
    E++;
  }

  public boolean insertEdge(int u, int v, double w) {
    // use this when V has been set but not finally, and the number of edges isn't set
    if (validate) validateVertex(u,v);
    int max = Math.max(u,v); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<DirectedEdgeX>());
    DirectedEdgeX e = new DirectedEdgeX(u,v,w);
    adj.get(u).add(e);
    s = indegree.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) indegree.add(0);
    indegree.set(v,indegree.get(v)+1);
    E++;
    return true;
  }

  public boolean insertEdge(DirectedEdgeX e) {
    // use this when V has been set but not finally, and the number of edges isn't set
    int u = e.u(), v = e.v();
    if (validate) validateVertex(u,v);
    int max = Math.max(u,v); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<DirectedEdgeX>());
    adj.get(u).add(e);
    s = indegree.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) indegree.add(0);
    indegree.set(v,indegree.get(v)+1);
    E++;
    return true;
  }

  public Iterable<DirectedEdgeX> adj(int v) { validateVertex(v); return adj.get(v); }

  public int outdegree(int v) { validateVertex(v); return adj.get(v).size(); }

  public int maxIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int max = Integer.MIN_VALUE;
    for (int i : indegree) if (i > max) max = i;
    return max; 
  }

  public int minIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int min = Integer.MAX_VALUE;
    for (int i : indegree) if (i < min) min = i;
    return min; 
  }

  public double avgIndegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    double sum = 0;
    for (int i : indegree) sum += i;
    return sum/V; 
  }

  public int maxOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int max = Integer.MIN_VALUE;
    for (BagX<DirectedEdgeX> b : adj) { int s = b.size(); if (s > max) max = s; }
    return max; 
  }

  public int minOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int min = Integer.MAX_VALUE;
    for (BagX<DirectedEdgeX> b : adj) { int s = b.size(); if (s < min) min = s; }
    return min; 
  }

  public double avgOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    double sum = 0;
    for (BagX<DirectedEdgeX> b : adj) sum += b.size();
    return sum/V; 
  }

  public int indegree(int v) { validateVertex(v); return indegree.get(v); }

  public boolean hasSelfLoop() {
    for (int u = 0; u < V; u++) 
      for (DirectedEdgeX e : adj.get(u))
        if (u == e.to()) return true;
    return false;
  }

  public Seq<Integer> allSelfLoops() {
    // each self-loop is represented as an individual vertex v 
    // with the implication that edge v->v exists
    Seq<Integer> sl = new Seq<Integer>();
    for (int u = 0; u < V; u++)
      for (DirectedEdgeX e  : adj.get(u)) if (u == e.to()) sl.add(u);
    if (!sl.isEmpty()) selfLoops = sl;
    return sl;
  }

  public boolean hasParallelEdge() {
    // 2 edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    boolean[] m = new boolean[V];
    for (int u = 0; u < V; u++) {
      for (DirectedEdgeX e : adj.get(u)) {
        int v = e.to();
        if (u != v && m[v]) {
          parallelEdges = new Seq<Tuple2<Integer,Integer>>(new Tuple2<>(u,v));
          return true;
        }
        m[v] = true;
      }
      for (DirectedEdgeX e  : adj.get(u))  m[e.to()] = false;
    }
    return false;
  }

  public Seq<Tuple2<Integer,Integer>> allParallelEdges() {
    // Two edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    // each pair of parallel edges is represented as a single edge u->v with the
    // implication that another such edge exists
    Seq<Tuple2<Integer,Integer>> p = new Seq<>();
    boolean[] m = new boolean[V];
    for (int u = 0; u < V; u++) {
      for (DirectedEdgeX e  : adj.get(u)) {
        int v = e.to();
        if (u != v && m[v]) p.add(new Tuple2<Integer,Integer>(u,v));
        m[v] = true;
      }
      for (DirectedEdgeX e  : adj.get(u)) m[e.to()] = false;
    }
    if (!p.isEmpty()) parallelEdges = p;
    return p;
  }

  public EuclidianEdgeWeightedDigraph reverse() {
    // return a new EuclidianEdgeWeightedDigraph with u and v interchanged
    // in each edge from this
    EuclidianEdgeWeightedDigraph reverse = new EuclidianEdgeWeightedDigraph(V);
    for (int v = 0; v < V; v++) 
      for (DirectedEdgeX d : adj(v)) reverse.addEdge(d.reverse());
    return reverse;
  }

  public Iterable<DirectedEdgeX> edges() {
    BagX<DirectedEdgeX> list = new BagX<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeX e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }

  public Seq<DirectedEdgeX> edgeSeq() {
    Seq<DirectedEdgeX> list = new Seq<>();
    for (int v = 0; v < V; v++) {
      for (DirectedEdgeX e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }

  public HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints() {
    // map of edges with reverse edges for graphing
    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = new HashMap<>();
    boolean done = false;
    for(int i = 0; i < V; i++) {
      for (DirectedEdgeX e : edgeSeq()) {
        if (e != null) {
          // if e has a reverse edge cut its length by 2
          for (DirectedEdgeX f : edgeSeq()) {
            if (f == null || f.equals(e)) continue;
            done = false;
            if (f.from() == e.to() && f.to() == e.from()) {
              double avgXfrom = (coords.get(e.from())._1 + coords.get(e.to())._1)/2;
              double avgYfrom = (coords.get(e.from())._2 + coords.get(e.to())._2)/2;
              Tuple2<Double,Double> modFrom = new Tuple2<>(avgXfrom,avgYfrom);
              midPoints.put(f,modFrom); midPoints.put(e,modFrom);
              done = true;
              break;
            }         
          }
        }
      }
    }
    return midPoints;
  }

  Iterable<Integer> sources() {
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (indegree.get(i) == 0) s.add(i);
    return s;  
  }

  Iterable<Integer> sinks() {
    Seq<Integer> s = new Seq<>();
    for (int i = 0; i < V; i++) if (adj.get(i).isEmpty()) s.add(i);
    return s;
  }

  public boolean isMap() {
    for (int i = 0; i < V; i++) if (adj.get(i).size() != 1) return false;
    return true;
  }

  public boolean hasCycle() { return cycle != null; }

  public void addCoords(int v, double x, double y) {
    // assign or reassign coordinates to a vertex
    validateVertex(v);
    if (coords == null) coords = new Seq<Tuple2<Double,Double>>(2*v);
    else if (coords.size() < v) {
      int s = adj.size();
      for (int i = 0; i < 2*v-s; i++) coords.add(new Tuple2<Double,Double>());
    }
    coords.get(v).add(x,y);
  }

  public void updateX(int v, double x) {
    // update the x coordinate of a vertex
    validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return;
    }
    coords.get(v).add1(x);
  }

  public void updateY(int v, double y) {
    // update the y coordinate of a vertex
    validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return;
    }
    coords.get(v).add2(y);
  }

  public boolean removeCoordinates(int v) {
    // remove the coordinates of a vertex
    validateVertex(v);
    if (coords == null || coords.size() < v) {
      System.out.println("vertex v doesn't exist");
      return false;
    }
    coords.delete(v);
    return true;
  }

  public void search() {
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }

  public void trim() {    
    if (adj != null && adj.size() > V) adj.deleteRange(V, adj.size()); 
    if (coords != null && coords.size() > V) coords.deleteRange(V, coords.size());
    if (indegree != null && indegree.size() > V) indegree.deleteRange(V, indegree.size());
  }

  private double[] minMaxWeights() {
    double min =  Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    for (DirectedEdgeX d : edges()) {
      double x = d.w();
      if (x < min) min = x;
      if (x > max) max = x;
    }
    return new double[]{min,max};
  }

  public double minWeight() { 
    double[] m = minMaxWeights();
    return m[0]; 
  }

  public double maxWeight() { 
    double[] m = minMaxWeights();
    return m[1]; 
  }


  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("V = "+V+", E = "+E+NEWLINE);
    s.append(adj+NEWLINE);
    s.append(coords+NEWLINE);
    return s.toString();
  }

  public static final Color DarkCyan = new Color(0,139,139);

  public static final Color DarkOrange = new Color(255, 149, 0);

  public static final Color PastelRed = new Color(255, 105, 97);

  public static final Color DarkPastelRed = new Color(194, 59, 34);



  public static void drawEdge(Tuple2<Double,Double> a, Tuple2<Double,Double> b, double w, 
      double h, double mod) {
    // this is for use with StdDraw.
    // modified from 
    // https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    // draws a line with arrowhead from point a to mod distance before point b to allow space 
    // for a  text bubble around the latter.
    // the tuples a and b represent points, d is the width of the arrowead and h is its height
    // mod is the amount by which the end of the line is truncated.
    // used in showd()

    // adjust mod when x coords are close to improve arrow position
    if (Math.abs(a._1 - b._1)<.15) mod *= 1.5; // experimentally determined
    double x1 = a._1, y1 = a._2, x2 = b._1, y2 = b._2;
    double dx = x2 - x1, dy = y2 - y1;
    double D = Math.sqrt(dx*dx + dy*dy);
    double sin = dy/D, cos = dx/D;

    // shorten the line at the end by mod
    double xmod = abs(mod*cos), ymod = abs(mod*sin);
    x2 = x2 > x1 ? x2 - xmod : x2 + xmod;
    y2 = y2 > y1 ? y2 - ymod : y2 + ymod;

    // recaculate line parameters
    dx = x2 - x1; dy = y2 - y1;
    D = Math.sqrt(dx*dx + dy*dy);
    double xm = D - w, xn = xm, ym = h, yn = -h, x;
    sin = dy/D; cos = dx/D;

    // calculate parameters for arrowhead
    x = xm*cos - ym*sin + x1;
    ym = xm*sin + ym*cos + y1;
    xm = x;

    x = xn*cos - yn*sin + x1;
    yn = xn*sin + yn*cos + y1;
    xn = x;

    double[] xpoints = {x2, xm, xn};
    double[] ypoints = {y2, ym, yn};

    StdDraw.line(x1, y1, x2, y2);
    StdDraw.filledPolygon(xpoints, ypoints);
  }

  public static void drawEdge(Tuple2<Double,Double> a, Tuple2<Double,Double> b, double w, 
      double h, double mod, Draw d) {
    // this takes a Draw argument d and isn't for use with StdDraw.
    // modified from 
    // https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    // draws a line with arrowhead from point a to mod distance before point b to allow space 
    // for a  text bubble around the latter.
    // the tuples a and b represent points, d is the width of the arrowead and h is its height
    // mod is the amount by which the end of the line is truncated.
    // used in showd()

    // adjust mod when x coords are close to improve arrow position
    //    if (Math.abs(a._1 - b._1)<.15) mod *= 1.5; // experimentally determined
    double x1 = a._1, y1 = a._2, x2 = b._1, y2 = b._2;
    double dx = x2 - x1, dy = y2 - y1;
    double D = Math.sqrt(dx*dx + dy*dy);
    double sin = dy/D, cos = dx/D;

    // shorten the line at the end by mod
    double xmod = abs(mod*cos), ymod = abs(mod*sin);
    x2 = x2 > x1 ? x2 - xmod : x2 + xmod;
    y2 = y2 > y1 ? y2 - ymod : y2 + ymod;

    // recaculate line parameters
    dx = x2 - x1; dy = y2 - y1;
    D = Math.sqrt(dx*dx + dy*dy);
    double xm = D - w, xn = xm, ym = h, yn = -h, x;
    sin = dy/D; cos = dx/D;

    // calculate parameters for arrowhead
    x = xm*cos - ym*sin + x1;
    ym = xm*sin + ym*cos + y1;
    xm = x;

    x = xn*cos - yn*sin + x1;
    yn = xn*sin + yn*cos + y1;
    xn = x;

    double[] xpoints = {x2, xm, xn};
    double[] ypoints = {y2, ym, yn};

    d.line(x1, y1, x2, y2);
    d.filledPolygon(xpoints, ypoints);
  }

  public void show() {
    // plots a digraph with unlabelled vertices
    // for general plotting of Digraphs
    // not for use with demos in RandomGridDigraph.main()
    double mod = .8;
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
    StdDraw.setCanvasSize(950, 950); double sf = .25;
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
    StdDraw.setPenRadius(.0014);
    for(int i = 0; i < V; i++)
      for (DirectedEdgeX e : adj.get(i)) 
        drawEdge(coords.get(i),coords.get(e.to()),.6,.6,mod);
    StdDraw.setPenRadius(.02);
    StdDraw.setPenColor(StdDraw.RED);
    for (int i = 0; i < points.length; i++) points[i].draw();
    StdDraw.setPenColor(StdDraw.BLACK);
  }

  public void showLabelled(double mod, String[] labels) {
    // for ex4229 and used in analysis.ArithmeticExpressionEvaluation
    // plots a digraph with labelled vertices
    // mod = 3.35 works with PenRadius = .1 for points
    if (labels.length != V) throw new IllegalArgumentException(
        "showd: labels.length != V == "+V);
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
    StdDraw.setCanvasSize(800, 800); double sf = .15;
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
      for (DirectedEdgeX e : adj.get(i)) 
        drawEdge(coords.get(i),coords.get(e.to()),1,1,mod);
    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
    StdDraw.setPenRadius(.07);
    for (int i = 0; i < points.length; i++) points[i].draw();
    StdDraw.setPenColor(StdDraw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 15);
    StdDraw.setFont(font);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        StdDraw.text(x, y, labels[i]);
      }
    }
  }

  public Draw showEx44040507(double mod, Seq<DirectedEdgeX> sptEdges, String[] labels,
      String[] parentEdges, String title, HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints) {
    // plots an EuclidianEdgeWeightedDigraph with labelled vertices
    // mod 2.2 works with PenRadius .004 for drawEdge with w = 2 and h = 1
    if (labels.length != V) throw new IllegalArgumentException(
        "showLabelled: labels.length != V == "+V);

    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(600, 600); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));

    // draw title text
    d.setPenRadius(3.);
    d.setPenColor(Draw.BLACK);
    Font font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    String[] titles = title.split("\\|");
    double indent = 5, yaxis = 95;
    for (int i = 0; i < titles.length; i++)
      d.textLeft(indent, yaxis-=3, titles[i]);

    //  // draw all edges not in sptEdges
    //  d.setPenColor(DarkOrange);
    //  d.setPenRadius(.004);
    //    Seq<DirectedEdgeX> reverseEdges = new Seq<>();
    //    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = new HashMap<>();
    //    boolean done = false;
    //    for(int i = 0; i < V; i++) {
    //      for (DirectedEdgeX e : adj.get(i)) {
    //        if (e != null) {
    //          if (sptEdges.contains(e)) continue;
    //          // if e has a reverse edge in sptEdges cut its length by 2
    //          for (DirectedEdgeX f : sptEdges) {
    //            if (f == null) continue;
    //            done = false;
    //            if (f.from() == e.to() && f.to() == e.from()) {
    //              double avgXfrom = (coords.get(e.from())._1 + coords.get(e.to())._1)/2;
    //              double avgYfrom = (coords.get(e.from())._2 + coords.get(e.to())._2)/2;
    //              Tuple2<Double,Double> modFrom = new Tuple2<>(avgXfrom,avgYfrom);
    //              drawEdge(modFrom,coords.get(e.to()),2,1,mod,d);
    //              midPoints.put(f,modFrom);
    //              done = true;
    //              break;
    //            }         
    //          }
    //          if (!done) drawEdge(coords.get(i),coords.get(e.to()),2,1,mod,d);
    //        }
    //      }
    //    }

    //draw all edges not in sptEdges
    d.setPenColor(DarkOrange);
    d.setPenRadius(.004);
    midPoints = midPoints();
    for (DirectedEdgeX e : edgeSeq()) {
      if (e != null) {
        if (sptEdges.contains(e)) continue;
        if (midPoints.containsKey(e))
          drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
        else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
      }
    }

    // draw sptEdges
    d.setPenColor(Draw.BLACK);
    for (DirectedEdgeX e : sptEdges) {
      // if e has a reverse edge not in sptEdges, cut its length by 2
      if (e != null) {
        if (midPoints.containsKey(e))
          drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
        else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
      }
    }

    Tuple2<Double,Double> t; double x, y;

    // draw LIGHT_GRAY filled circles centered at coords of vertices
    d.setPenRadius(.002);
    for (int i = 0; i < V; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(LIGHT_GRAY);
        d.filledCircle(x, y, 2);
      }
    }

    // draw black unfilled circles centered at vertex coords
    d.setPenColor(BLACK);
    for (int i = 0; i < V; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.circle(x, y, 2);
      }
    }

    // draw labels at vertex coords
    d.setPenColor(BLACK);
    font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    for (int i = 0; i < V; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.text(x, y, labels[i]);
      }
    }

    // draw Parent-Link Representation text
    d.setPenRadius(3.);
    d.setPenColor(Draw.BLACK);
    font = new Font("Arial", Font.BOLD, 20);
    d.setFont(font);
    yaxis = 50;
    d.textLeft(indent, yaxis-=3, "Parent-Link Representation:");
    for (String s : parentEdges) d.textLeft(indent, yaxis-=3, s);
    return d;
  }

  public Draw showDijkstraSPXtrace(String[] labels, 
      Tuple9<Integer,DirectedEdgeX,Seq<Integer>,Seq<Integer>,Seq<DirectedEdgeX>,
      Seq<DirectedEdgeX>,DirectedEdgeX[],double[],boolean[]> data, String title) {
    // for ex4406 and called by Dijkstra.SPXTrace.traceModTinyEWDex4406 
    // uses edu.princeton.cs.algs4.Draw since it can display multiple drawings at a 
    // time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: labels == null");    
    if (labels.length != V) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: labels.length != V == "+V);
    if (data == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data == null");
    if (title == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: title == null");
    if (data._1 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._1 == null");
    // data._2 may be null
    if (data._3 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._4 == null");
    if (data._4 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._4 == null");
    if (data._5 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._5 == null");
    if (data._6 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._6 == null");
    if (data._7 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._7 == null");
    // data._8 && data._9 aren't used
    if (data._8 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._8 == null");
    if (data._9 == null) throw new IllegalArgumentException(
        "showDijkstraSPXtrace: data._9 == null");
    Integer v               = data._1;
    DirectedEdgeX pqMinEdge = data._2;
    Seq<Integer> pq         = data._3;
    Seq<Integer> sptvv      = data._4;
    Seq<DirectedEdgeX> spte = data._5;
    Seq<DirectedEdgeX> inel = data._6;
    DirectedEdgeX[] edgeTo  = data._7;
    double[] distTo         = data._8;
    boolean[] marked        = data._9;

    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = this.midPoints();

    Seq<String> spts = new Seq<>();
    for (DirectedEdgeX e : spte) spts.add(e.toString2());

    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2;  Tuple4<Double,Double,Double,Double> t4, t4r;
    double x,y; 
    double mod = 2.2; // adjustment for drawEdge

    // set pqMin coords
    Tuple4<Double,Double,Double,Double> pqMin = null;
    if (pqMinEdge != null) {
      t = coords.get(pqMinEdge.u());
      t2 = coords.get(pqMinEdge.v());
      pqMin = new Tuple4<>(t._1, t._2, t2._1, t2._2);
    }

    // get other edges in edgeTo and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cpq4 = new HashSET<>(); // edgeTo edges
    for (DirectedEdgeX e : edgeTo) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (pqMin != null && t4.equals(pqMin)) continue;
        cpq4.add(t4);
      }
    }

    // get ineligible edges from inel and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cinel4 = new HashSET<>(); 
    for (DirectedEdgeX e : inel) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (pqMin != null && t4.equals(pqMin)) continue;
        cinel4.add(t4);
      }
    }

    // get SPT edges, draw them in black and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    HashSET<Tuple2<Double,Double>> spt = new HashSET<>(); // spt vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> spt4 = new HashSET<>(); // spt edges
    for (DirectedEdgeX e : spte) {
      t = coords.get(e.from());
      t2 = coords.get(e.to());
      if (midPoints.containsKey(e))
        drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
      else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
      spt.add(t); spt.add(t2);
      spt4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
    }

    // draw all other edges except pqMinEdge
    Seq<DirectedEdgeX> cross = new Seq<>(); // crossing edges
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (DirectedEdgeX e : adj.get(i))  {
        int j = e.to();
        t2 = coords.get(j);
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (spt4.contains(t4) || e.equals(pqMinEdge)) continue;
        if (cinel4.contains(t4) || spt.contains(t) && spt.contains(t2)) {
          d.setPenColor(LIGHT_GRAY);  // ineligible edge
          if (midPoints.containsKey(e))
            drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
          else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
        } else if (cpq4.contains(t4)) {
          d.setPenColor(DarkCyan);    // crossing edge
          if (midPoints.containsKey(e))
            drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
          else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
          cross.add(e);
        } else {
          d.setPenColor(DarkOrange);  // unprocessed edge
          if (midPoints.containsKey(e))
            drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
          else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
        }
      }
    }

    // draw pqMinEdge in red if nonnull and title isn't "Final"
    d.setPenColor(RED);
    d.setPenRadius(.004);
    if (pqMinEdge != null && !title.equals("Final")) {
      if (midPoints.containsKey(pqMinEdge))
        drawEdge(midPoints.get(pqMinEdge),coords.get(pqMinEdge.to()),2,1,mod,d);
      else drawEdge(coords.get(pqMinEdge.from()),coords.get(pqMinEdge.to()),2,1,mod,d);
    }

    // draw  filled circles at coords of vertices
    // white if in SPT else gray
    HashSET<Tuple2<Double,Double>> sptv = new HashSET<>(); 
    for (Integer i : sptvv) sptv.add(coords.get(i));
    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        d.setPenColor(WHITE);
        if (sptv.contains(t)) d.filledCircle(x, y, 2);
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
    d.textLeft(26,64,"vertex in SPT");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in SPT");

    d.setPenRadius(.006);
    d.setPenColor(BLACK);
    d.line(19.5, 60.5, 23.25, 60.5);
    d.textLeft(26,60.5,"edge in SPT");    

    d.setPenColor(DarkOrange);
    d.line(54.75, 60.5, 58.5, 60.5);
    d.setPenColor(BLACK);
    d.textLeft(61,60.5, "unprocessed edge");

    d.setPenColor(RED);
    d.line(19.5, 57, 23.25, 57);
    d.setPenColor(BLACK);
    d.textLeft(26,57,"next edge for SPT");

    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 57, 58.5, 57);
    d.setPenColor(BLACK);
    d.textLeft(61,57,"ineligible edge");

    d.setPenColor(DarkCyan);
    d.line(19.5, 53.5, 23.25, 53.5);
    d.setPenColor(BLACK);
    d.textLeft(26,53.5,"crossing edge");

    // draw content text
    double indent = 19.4, yaxis = 51;
    if (v > -1 && !title.equals("Final"))
      d.textLeft(indent, yaxis-=4, "current vertex in call to relax():  "+v);
    d.textLeft(indent, yaxis-=4, "IndexMinPQ:  "+pq.toString());
    d.textLeft(indent, yaxis-=4, "SPT vertices: "+sptvv.toString().replaceAll(",",", "));   
    d.textLeft(indent, yaxis-=4, "SPT edges:    "+spts.toString().replaceAll(",",", "));
    if (v > -3) {
      d.textLeft(indent, yaxis-=4, String.format("    %-8s    %s", "edgeTo[]", "distTo[]"));
      for (int i = 0; i < V; i++) {
        if (pq.contains(i)) {
          if (edgeTo[i] != null && cross.contains(edgeTo[i])) d.setPenColor(DarkCyan);
          else d.setPenColor(RED);
        }
        else d.setPenColor(BLACK);
        if (edgeTo[i] == null) 
          d.textLeft(indent,yaxis-=3,i+"  "
              + String.format("%-13s    %s","null", distTo[i]));  
        else d.textLeft(indent,yaxis-=3,i+"  "
            + String.format("%-10s   %3.2f",edgeTo[i].toString(), distTo[i]));
      }
    }
    if (title.equals("Final")) {
      d.textLeft(indent, yaxis-=3, "");
      d.textLeft(indent, yaxis-=3, "Processing stops here since the IndexMinPQ is empty.");
    }
    return d;
  }

  public Draw showBellmanFordSPXtrace(String[] labels, 
      Tuple8<Integer,Seq<Integer>,Seq<Integer>,Seq<DirectedEdgeX>,Seq<DirectedEdgeX>,
      DirectedEdgeX[],double[],String> data, String title) {
    // for ex4421; uses edu.princeton.cs.algs4.Draw since it can display 
    // multiple drawings at a time in sequence
    // plots an EuclidianGraphX with title, labelled vertices and text content
    if (labels == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: labels == null");    
    if (labels.length != V) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: labels.length != V == "+V);
    if (data == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data == null");
    if (title == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: title == null");
    if (data._1 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._1 == null");
    if (data._2 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._2 == null");
    if (data._3 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._3 == null");
    if (data._4 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._3 == null");
    if (data._5 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._4 == null");
    if (data._6 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._5 == null");
    if (data._7 == null) throw new IllegalArgumentException(
        "showBellmanFordSPXtrace: data._6 == null");
    // data._8 may be null

    /*  Tuple8 fields in data Seq<Tuple8<>
     *  ref  type                contents
     *  _1   Integer             current vertex in relax call or processing tag if negative
     *  _2   Seq<Integer>        vertices in queue
     *  _3   Seq<Integer>        vertices in SPT (sptvv)
     *  _4   Seq<DirectedEdgeX>  processed edges (proc)
     *  _5   Seq<DirectedEdgeX>  ineligible edges (inel)
     *  _6   DirectedEdgeX[]     edgeTo
     *  _7   double[]            distTo
     *  _8   String              negative cycle
     */

    Integer v               = data._1;
    Seq<Integer> queue      = data._2;
    Seq<Integer> sptvv      = data._3;
    Seq<DirectedEdgeX> proc = data._4;
    Seq<DirectedEdgeX> inel = data._5;
    DirectedEdgeX[] edgeTo  = data._6;
    double[] distTo         = data._7;
    String ncycle           = data._8;

    HashMap<DirectedEdgeX,Tuple2<Double,Double>> midPoints = this.midPoints();

    double maxx, maxy, minx, miny; maxx = maxy = 100; minx = miny = 0;
    Draw d = new Draw(title);
    d.setCanvasSize(950, 950); double sf = .25;
    d.setXscale(minx+(sf/maxx-minx), maxx-(sf/maxx-minx));
    d.setYscale(miny+(sf/maxy-miny), maxy-(sf/maxy-miny));
    int n = coords.size() < V ? coords.size() : V;
    Tuple2<Double,Double> t,t2;  Tuple4<Double,Double,Double,Double> t4, t4r;
    double mod = 2.2; // adjustment for drawEdge

    // get ineligible edges from inel and save their coords
    HashSET<Tuple4<Double,Double,Double,Double>> cinel4 = new HashSET<>(); 
    //    System.out.println("inel = "+inel);
    for (DirectedEdgeX e : inel) {
      if (e != null) {
        t = coords.get(e.u());
        t2 = coords.get(e.v());
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        cinel4.add(t4);
      }
    }

    // get nonnull edgeTo edges, draw them in black and save their coords
    d.setPenColor(BLACK);
    d.setPenRadius(.004);
    HashSET<Tuple2<Double,Double>> spt = new HashSET<>(); // spt vertex coords
    HashSET<Tuple4<Double,Double,Double,Double>> spt4 = new HashSET<>(); // spt edges
    for (DirectedEdgeX e : edgeTo) {
      if (e != null) {
        t = coords.get(e.from());
        t2 = coords.get(e.to());
        if (midPoints.containsKey(e))
          drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
        else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
        spt.add(t); spt.add(t2);
        spt4.add(new Tuple4<>(t._1, t._2, t2._1, t2._2));
      }
    }

    // draw all other edges
    d.setPenRadius(.004);
    for(int i = 0; i < V; i++) {
      t = coords.get(i);
      for (DirectedEdgeX e : adj.get(i))  {
        int j = e.to();
        t2 = coords.get(j);
        t4 = new Tuple4<>(t._1, t._2, t2._1, t2._2);
        if (spt4.contains(t4)) continue;
        else if(inel.contains(e) || proc.contains(e)) {
          d.setPenColor(LIGHT_GRAY);       
          if (midPoints.containsKey(e))
            drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
          else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
        } else {
          d.setPenColor(DarkOrange);       // unprocessed edge
          if (midPoints.containsKey(e))
            drawEdge(midPoints.get(e),coords.get(e.to()),2,1,mod,d);
          else drawEdge(coords.get(e.from()),coords.get(e.to()),2,1,mod,d);
        }
      }
    }

    double x,y; 
    HashSET<Tuple2<Double,Double>> qt = new HashSET<>();
    for (Integer i : queue) qt.add(coords.get(i));
    d.setPenRadius(.002);
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        if (qt.contains(t)) {
          d.setPenColor(PastelRed);
          d.filledCircle(x, y, 2);
        }
        else if (spt.contains(t)) {
          d.setPenColor(WHITE);
          d.filledCircle(x, y, 2);
        }
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
    d.textLeft(26,64,"vertex in SPT");

    d.setPenColor(LIGHT_GRAY);
    d.filledCircle(56.5, 64, 2);
    d.setPenColor(BLACK);
    d.circle(56.5, 64, 2);
    d.text(56.5, 64, "v");
    d.textLeft(61,64,"vertex not in SPT");

    d.setPenColor(PastelRed);
    d.filledCircle(21.5, 58.5, 2);
    d.setPenColor(BLACK);
    d.circle(21.5, 58.5, 2);
    d.text(21.5, 58.5, "v");
    d.textLeft(26,58.5,"vertex in Queue");

    d.setPenRadius(.006);
    d.setPenColor(BLACK);
    d.line(54.75, 58.5, 58.5, 58.5);
    if (title.equals("Final") && ncycle == null) d.textLeft(61,58.5,"edge in SPT");
    else d.textLeft(61,58.5,"edge in edgeTo[]");

    d.setPenColor(DarkOrange);
    d.line(19.5, 54, 23.25, 54);
    d.setPenColor(BLACK);
    d.textLeft(26,54,"unprocessed edge");

    d.setPenColor(LIGHT_GRAY);
    d.line(54.75, 54, 58.5, 54);
    d.setPenColor(BLACK);
    d.textLeft(61,54,"ineligible edge");  

    // draw content text
    double indent = 19.4, yaxis = 51;
    if ((v > -1 && ncycle == null && !title.equals("Final")) 
        || (ncycle != null && title.equals("Final")))
      d.textLeft(indent, yaxis-=4, "current vertex in call to relax():  "+v);
    if (title.equals("Step 0"))
      d.textLeft(indent, yaxis-=4, String.format("%-12s    %s", 
          "Queue:  "+queue.toString(), "source = "+(-v-2)));
    else d.textLeft(indent, yaxis-=4, "Queue:  "+queue.toString());
    
    d.textLeft(indent, yaxis-=4, String.format("    %-8s    %s", "edgeTo[]", "distTo[]"));
    for (int i = 0; i < V; i++) {
      if (queue.contains(i)) d.setPenColor(DarkPastelRed);
      else d.setPenColor(BLACK);
      if (edgeTo[i] == null) 
        d.textLeft(indent,yaxis-=3,i+"  "
            + String.format("%-13s    %s","null", distTo[i]));  
      else d.textLeft(indent,yaxis-=3,i+"  "
          + String.format("%-10s   %3.2f",edgeTo[i].toString(), distTo[i]));
    }

    if (title.equals("Final")) {
      d.setPenColor(BLACK);
      d.textLeft(indent, yaxis-=2, "");
      if (ncycle != null) {
        d.textLeft(indent, yaxis-=3, "Processing stops here since a negative cycle was found.");
        d.textLeft(indent, yaxis-=3, "The negative cycle is: "+ncycle);
      } else
        d.textLeft(indent, yaxis-=3, "Processing stops here since the Queue is empty.");
    }
    return d;
  }

  public void showGrid() {
    // for demos in RandomGridDigraph and EdgeWeightedDigraphGrid
    double mod = .07;
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
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (DirectedEdgeX e : adj.get(i)) 
        drawEdge(coords.get(i),coords.get(e.to()),.07,.07,mod);
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    int n = coords.size() < V ? coords.size() : V;
    Point2D[] points = new Point2D[n];
    Tuple2<Double,Double> t; double x, y;
    for (int i = 0; i < n; i++) {
      t = coords.get(i);
      if (t != null && t._1 != null && t._2 != null) {
        x = t._1; y = t._2;
        points[i] = new Point2D(x, y);
        points[i].draw();
      }
    }
  }

  public void showGrid(HashSET<Tuple2<Integer,Integer>> extraEdges) {
    // only for demos in RandomGridDigraph.main() 
    double mod = .07;
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
      for (DirectedEdgeX e : adj.get(i)) {
        Tuple2<Integer,Integer> t1 = new Tuple2<>(i,e.to());
        if (!(extraEdges.contains(t1)))
          drawEdge(coords.get(i),coords.get(e.to()),.07,.07,mod);
      }
    }
    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.setPenRadius(.003);
    for (Tuple2<Integer,Integer> t2 : extraEdges) {
      drawEdge(coords.get(t2._1),coords.get(t2._2),.07,.07,mod);
    }
    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw();
  }

  public void showGrid(HashSET<Tuple2<Integer,Integer>> extraEdges,
      HashSET<Tuple2<Integer,Integer>> pEdges) {
    // only for demos in RandomGridDigraph.main() 
    double mod = .07;
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
      for (DirectedEdgeX e : adj.get(i)) {
        Tuple2<Integer,Integer> t1 = new Tuple2<>(i,e.to());
        if (!(extraEdges.contains(t1) || pEdges.contains(t1))) 
          drawEdge(coords.get(i),coords.get(e.to()),.07,.07,mod);
      }
    }
    StdDraw.setPenColor(StdDraw.GREEN);
    StdDraw.setPenRadius(.003);
    for (Tuple2<Integer,Integer> t2 : extraEdges)
      if (!pEdges.contains(t2)) drawEdge(coords.get(t2._1),coords.get(t2._2),.07,.07,mod);

    StdDraw.setPenColor(DarkOrange);
    for (Tuple2<Integer,Integer> t2 : pEdges)
      drawEdge(coords.get(t2._1),coords.get(t2._2),.07,.07,mod);

    StdDraw.setPenColor(StdDraw.RED);
    StdDraw.setPenRadius(.02);
    for (int i = 0; i < n; i++) points[i].draw(); 
  }

  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {

    //  V and edges are from tinyEWD.txt that has same geometry as tineyEWG.txt
    int V = 8;
    String edges = "4 5 0.35  5 4 0.35  4 7 0.37  5 7 0.28  7 5 0.28 "
        + "5 1 0.32  0 4 0.38  0 2 0.26  7 3 0.39  1 3 0.29  2 7 0.34 "
        + "6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";

    String[] edgs = edges.split("\\s+");

    int u,v; double w;
    Seq<DirectedEdgeX> edgSeq = new Seq<>();
    for (int i = 0; i < edgs.length-2; i+=3) {
      u = Integer.parseInt(edgs[i]);
      v = Integer.parseInt(edgs[i+1]);
      w = Double.parseDouble(edgs[i+2]);
      DirectedEdgeX e = new DirectedEdgeX(u,v,w);
      edgSeq.add(e);
    }
    System.out.println("edgSeq="+edgSeq);

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

    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", "); 

    System.out.println("EuclidianEdgeWeightedDigraph constructor testing:\n");

    EuclidianEdgeWeightedDigraph g;

    System.out.println("1: testing new EuclidianEdgeWeightedDigraph()");
    g = new EuclidianEdgeWeightedDigraph();
    for (DirectedEdgeX de : edgSeq) g.insertEdge(de);
    assert V == g.V(); 
    g.coords = coords;
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("2: testing new EuclidianEdgeWeightedDigraph(int)");
    g = new EuclidianEdgeWeightedDigraph(V);
    for (DirectedEdgeX de : edgSeq) g.addEdge(de);
    assert V == g.V(); 
    g.coords = coords;
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("3: testing new EuclidianEdgeWeightedDigraph(int, int)");
    System.out.println("   (create random edges with random weights + random coordinates)");
    g = new EuclidianEdgeWeightedDigraph(V,19);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);


    System.out.println("4: testing new EuclidianEdgeWeightedDigraph(int, "
        + "String,double[])");
    g = new EuclidianEdgeWeightedDigraph(V,edges,coordAr);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("5: testing new EuclidianEdgeWeightedDigraph(int, "
        + "Seq<DirectedEdgeX>, Seq<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedDigraph(V,edgSeq,coords);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("6: testing new EuclidianEdgeWeightedDigraph(int, "
        + "Iterable<DirectedEdgeX>, Iterable<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedDigraph(V,edgSeq,coords);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("7: testing new EuclidianEdgeWeightedDigraph(In, "
        + "Seq<Tuple2<Double,Double>>)");
    g = new EuclidianEdgeWeightedDigraph(new In("tinyEWD.txt"),coords);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("8: testing new EuclidianEdgeWeightedDigraph("
        + "EdgeWeightedDigraph G, Seq<Tuple2<Double,Double>>)");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(new In("tinyEWD.txt"));
    g = new EuclidianEdgeWeightedDigraph(G, coords);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);
    pause(1);

    System.out.println("9: testing new EuclidianEdgeWeightedDigraph("
        + "EuclidianEdgeWeightedDigraph G)");
    g = new EuclidianEdgeWeightedDigraph(g);
    assert V == g.V();
    System.out.println(g); 
    g.showLabelled(2.5,labels);

  }

}
