package graph;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.awt.Color;
import java.awt.Font;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import st.HashSET;
import v.Tuple2;
import v.Tuple2C;

@SuppressWarnings("unused")
public class EuclidianDigraph {;
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int DEFAULTLEN = 17;
  private int V;  // number of vertices in this digraph
  private int E;  // number of edges in this digraph
  private Seq<BagX<Integer>> adj;
  private Seq<Tuple2<Double,Double>> coords;
  private Seq<Integer> indegree; // indegree.get(v) = indegree of vertex v
  private Stack<Integer> cycle; // hasParallelEdges()
  private Seq<Integer> selfLoops;
  private Seq<Tuple2<Integer,Integer>> parallelEdges;
  private int[] id;             // id[v] = id of strong component containing v
  private int count;            // number of strongly-connected components
  private Seq<Seq<Integer>> scc; // strongly connected components
  public boolean validate  = true;
  
  
  public EuclidianDigraph() {
    V = E = 0;
    adj = Seq.fill(DEFAULTLEN, ()->new BagX<Integer>());
    indegree = Seq.fill(DEFAULTLEN,()->0);
    coords = new Seq<Tuple2<Double,Double>>(DEFAULTLEN);
    for (int i = 0; i < DEFAULTLEN; i++) coords.add(new Tuple2<Double,Double>());
    validate = false;
//    System.out.println(
//        "insert edges with insertEdge(int,int) and coordinates with addCoords(int,double,double)"
//        + "\nvertices without coordinates won't be graphed"
//        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }
  
  public EuclidianDigraph(int v) {
    if (v < 0) throw new IllegalArgumentException(
        "EuclidianGraph constructor: Number of vertices must be nonnegative");
    V = v; E = 0;
    adj = Seq.fill(V, ()->new BagX<Integer>());
    indegree = Seq.fill(V,()->0);
    coords = new Seq<Tuple2<Double,Double>>(V);
    for (int i = 0; i < V; i++) coords.add(new Tuple2<Double,Double>()); 
//    System.out.println(
//        "add edges with addEdge(int,int) and coordindates with addCoords(int,double,double)"
//        + "\nvertices without coordinates won't be graphed"
//        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }
  
  public EuclidianDigraph(int v, int e, int[][] edges, double[][] xy) {
    if (v <= 0) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "v is <= 0");
    if (e < 0) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "e is < 0");
    if (xy == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: xy is null");
    if (xy.length < v) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "the length of xy is < v");
    for (int i = 0; i < v; i++)
      if (xy[i].length < 2) throw new IllegalArgumentException("EuclidianGraph constructor: "
          + " the length of xy["+i+"] is < 2");
    if (edges.length < e) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "the length of edges is < e");
    for (int i = 0; i < e; i++) {
      if (edges[i].length < 2) throw new IllegalArgumentException("EuclidianGraph constructor: "
          + " the length of edges["+i+"] is < 2");   
      for (int j = 0; j < 2; j++) 
        if (edges[i][j] < 0 || edges[i][j] > v-1)
          throw new IllegalArgumentException("EuclidianGraph constructor: "
              + "an element of edges is out of range");
    }
    V = v;
    coords = new Seq<Tuple2<Double,Double>>(V);
    for (int i = 0; i < v; i++) coords.add(new Tuple2<Double,Double>(xy[i][0],xy[i][1]));
    adj = Seq.fill(V, ()->new BagX<Integer>());
    indegree = Seq.fill(V,()->0);
    validate = false; // already did validation of all vertices in edges above
    for (int i = 0; i < e; i++)  addEdge(edges[i][0],edges[i][1]);      
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components(); 
    id = sccx.id();
  }
  
  public EuclidianDigraph(int v, int e, String edges, String coords) {
    if (v <= 0) throw new IllegalArgumentException("EuclidianGraph constructor: v is <= 0");
    if (e < 0) throw new IllegalArgumentException("EuclidianGraph constructor: e is < 0"); 
    if (edges == null) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "edges is null");
    if (coords == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: coords is null");
    if (!edges.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException(
        "EuclidianGraph constructor: edges doesn't contain only integers and whitespace");  
    if (!coords.matches("(([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)|\\s+)+")) 
      throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "coords doesn't contain only numbers convertible to doubles and whitespace");
    V = v;
    String[] ea = edges.split("\\s+");
    if (ea.length < 2*e) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "String edges has fewer edges than int e");
    int[] ia = new int[2*e];
    for (int i = 0; i < ia.length; i++) {
      int vertex = Integer.parseInt(ea[i]);
      if (vertex < 0 || vertex > v-1) throw new IllegalArgumentException(
          "EuclidianGraph constructor: an element in e is out of range");
      ia[i] = vertex;
    }
    int ilen = ia.length % 2 == 0 ? ia.length : ia.length-1, k = 0;
    Tuple2<Integer,Integer>[] te = ofDim(Tuple2.class,ilen/2);
    for (int i = 0; i < ilen-1; i+=2)  te[k++] = new Tuple2<Integer,Integer>(ia[i],ia[i+1]);
    
    String[] da = coords.split("\\s+");
    double[] ja = new double[da.length];
    for (int i = 0; i < ja.length; i++) ja[i] = Double.parseDouble(da[i]);
    int jlen = ja.length % 2 == 0 ? ja.length : ja.length-1;
    if (jlen < 2*v) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "String coords contains fewer than 2*v doubles");
    if (jlen > 2*v) ja = take(ja,2*v);
    k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,v);
    for (int i = 0; i < jlen-1; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);
    
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    adj = Seq.fill(V, ()->new BagX<Integer>());
    indegree = Seq.fill(V,()->0);
    validate = false; // already did validation of all vertices in e above
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public EuclidianDigraph(int v, String edges, String coords) {
    if (v <= 0) throw new IllegalArgumentException("EuclidianGraph constructor: v is <= 0"); 
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: edges is null");
    if (coords == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: coords is null");
    if (!edges.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException(
        "EuclidianGraph constructor: edges doesn't contain only integers and whitespace");  
    if (!coords.matches("(([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)|\\s+)+")) 
      throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "coords doesn't contain only numbers convertible to doubles and whitespace");
    V = v;
    String[] ea = edges.split("\\s+");
    int[] ia = new int[ea.length];
    for (int i = 0; i < ia.length; i++) {
      int vertex = Integer.parseInt(ea[i]);
      if (vertex < 0 || vertex > v-1) throw new IllegalArgumentException(
          "EuclidianGraph constructor: an element in e is out of range");
      ia[i] = vertex;
    }
    int ilen = ia.length % 2 == 0 ? ia.length : ia.length-1, k = 0;
    Tuple2<Integer,Integer>[] te = ofDim(Tuple2.class,ilen/2);
    for (int i = 0; i < ilen-1; i+=2)  te[k++] = new Tuple2<Integer,Integer>(ia[i],ia[i+1]);
    
    String[] da = coords.split("\\s+");
    double[] ja = new double[da.length];
    for (int i = 0; i < ja.length; i++) ja[i] = Double.parseDouble(da[i]);
    int jlen = ja.length % 2 == 0 ? ja.length : ja.length-1;
    if (jlen < 2*v) throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "String coords contains fewer than 2*v doubles");
    if (jlen > 2*v) ja = take(ja,2*v);
    k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,v);
    for (int i = 0; i < jlen-1; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);
    
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    adj = Seq.fill(v, ()->new BagX<Integer>());
    indegree = Seq.fill(v,()->0);
    validate = false; // already did validation of all vertices in e above
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public EuclidianDigraph(String edges, String coords) {
    // determines V as the max vertex in edges
    if (edges == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: edges is null");
    if (coords == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: coords is null");
    if (!edges.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException(
        "EuclidianGraph constructor: edges doesn't contain only integers and whitespace");  
    if (!coords.matches("(([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)|\\s+)+")) 
      throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "coords doesn't contain only numbers convertible to doubles and whitespace");
    String[] ea = edges.split("\\s+");
    int[] ia = new int[ea.length];
    int v = -1;
    for (int i = 0; i < ia.length; i++) {
      int vertex = Integer.parseInt(ea[i]);
      if (vertex > v) v = vertex;
      ia[i] = vertex;
    }
    V = v+1;
    int ilen = ia.length % 2 == 0 ? ia.length : ia.length-1, k = 0;
    Tuple2<Integer,Integer>[] te = ofDim(Tuple2.class,ilen/2);
    for (int i = 0; i < ilen-1; i+=2)  te[k++] = new Tuple2<Integer,Integer>(ia[i],ia[i+1]);
    
    String[] da = coords.split("\\s+");
    double[] ja = new double[da.length];
    for (int i = 0; i < ja.length; i++) ja[i] = Double.parseDouble(da[i]);
    int jlen = ja.length % 2 == 0 ? ja.length : ja.length-1;
    if (jlen < 2*V) 
      System.out.println("warning: "+((2*V-jlen)/2)+" vertices have no coordinates");
    else if (jlen > 2*V) ja = take(ja,2*V);
    k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    adj = Seq.fill(V, ()->new BagX<Integer>());
    indegree = Seq.fill(V,()->0);
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }

  public EuclidianDigraph(In in) {
    // the format of the input file should be:
    // 1. an integer on the first line for V
    // 2. and integer on the second line for E
    // 3. two or more integers on zero or more lines for edges totaling to E, for example:
    //    a. 1 5 assigns one edge between vertices 1 and 5
    //    b. 1 5 3 7 assigns three edges 1-5, 1-3, 1-7
    // 4. an even number of two or more doubles on one or more lines for coordinate 
    //    pairs totaling to V
    // Empty lines are allowed and lines that don't match what's expected are skipped
    // V is set to the first integer encountered
    // E is set to the second integer encountered on a line after that containing V
    if (in == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: in == null");
    String s; final Pattern p = Pattern.compile("\\d+"); 
    try {
      while (in.hasNextLine()) {
        s = in.readLine();
        if (!s.matches(".*\\d+.*")) continue;
        Matcher m = p.matcher(s);
        if (m.find()) { V =  new Integer(m.group()); break; }
        else continue;
      }
      if (V < 0) throw new IllegalArgumentException(
          "EuclidianGraph constructor: number of vertices in a Graph must be nonnegative");
      adj = Seq.fill(V, ()->new BagX<Integer>());
      indegree = Seq.fill(V,()->0);
      int E = 0;
      while (in.hasNextLine()) {
        s = in.readLine();
        if (!s.matches(".*\\d+.*")) continue;
        Matcher m = p.matcher(s);
        if (m.find()) { E =  new Integer(m.group()); break; }
        else continue;
      }
      if (E < 0) throw new IllegalArgumentException(
          "EuclidianGraph constructor: number of edges in a Graph must be nonnegative");
      int c = 0,v,w,i; String[] sa;
      while (in.hasNextLine() && c < E) {
        // read exactly E edges but multiple edges may be assigned to a given vertex
        // on one line
        s = in.readLine();
        if (!s.matches("(\\d+|\\s+)+")) continue;
        sa = s.split("\\s+");
        if (sa.length < 2) continue;
        v = Integer.parseInt(sa[0]); validateVertex(v); 
        i = 1;      
        while (c < E && i < sa.length) {
          w = Integer.parseInt(sa[i]); 
          validateVertex(w);
          addEdge(v, w);    
          i++; c++;
        }
      }
      c = 0; Seq<Tuple2<Double,Double>> seq = new Seq<Tuple2<Double,Double>>(); 
      Tuple2<Double,Double> t; double d1,d2;
      while (in.hasNextLine() && c < V) {
        // read exactly V pairs of double coordinates 
        // an even number of such coordinates are read on a line
        // doubles may be in exponential 
        s = in.readLine();
        if (!s.matches("(([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)|\\s+)+")) {
          continue;
        }
        sa = s.split("\\s+");
        int len = sa.length % 2 == 0 ? sa.length : sa.length-1;
        if (sa.length < 2) continue;
        i = 0;
        while (c < V && i < sa.length-1) {
          d1 = Double.parseDouble(sa[i]); 
          d2 = Double.parseDouble(sa[i+1]);
          t = new Tuple2<Double,Double>(d1,d2);
          seq.add(t);
          i+=2; c++;
        }
      }
      if (c < V) System.out.println("warning: only "+c+" pairs of coordinates "
          + "have been assigned for "+V+" vertices");
      coords = seq;
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(
          "EuclidianGraph constructor: invalid input format", e);
    }
    DirectedCycleX dc = new DirectedCycleX(this);
    cycle = dc.getCycle();
    KosarajuSharirSCCX sccx = new KosarajuSharirSCCX(this);
    count = sccx.count();
    scc = sccx.components();
    id = sccx.id();
  }
  
  public EuclidianDigraph(EuclidianDigraph G) {
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    V = G.V;
    E = G.E;
    adj = G.adj;
    coords = G.coords;
    indegree = G.indegree;
    cycle = G.cycle;
    selfLoops = G.selfLoops;
    parallelEdges = G.parallelEdges;
    count = G.count();
    scc = G.scc;
    id = G.id;
  }
  
  public EuclidianDigraph(DigraphX G, String coords) {
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    if (coords == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: coords == null");
    if (!coords.matches("(([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)|\\s+)+")) 
      throw new IllegalArgumentException("EuclidianGraph constructor: "
        + "coords doesn't contain only numbers convertible to doubles and whitespace");
    V = G.V();
    E = G.E();
    adj = new Seq<BagX<Integer>>(G.adj());
    String[] da = coords.split("\\s+");
    indegree = new Seq<Integer>((Integer[]) box(G.indegree()));
    cycle = G.cycle();
    selfLoops = G.selfLoops();
    parallelEdges = G.parallelEdges();
    count = G.count();
    scc = G.scc();
    id  = G.id();
    double[] ja = new double[da.length];
    for (int i = 0; i < ja.length; i++) ja[i] = Double.parseDouble(da[i]);
    int jlen = ja.length % 2 == 0 ? ja.length : ja.length-1;
    if (jlen < 2*V) 
      System.out.println("warning: "+((2*V-jlen)/2)+" vertices have no coordinates");
    else if (jlen > 2*V) ja = take(ja,2*V);
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
  }
  
  public EuclidianDigraph(DigraphX G) {
    // create EuclidianGraph from G using random coords in the range 1.-99.
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    this.V = G.V();
    this.E = G.E();
    this.adj = new Seq<BagX<Integer>>(G.adj());
    indegree = new Seq<Integer>((Integer[]) box(G.indegree()));
    cycle = G.cycle();
    selfLoops = G.selfLoops();
    parallelEdges = G.parallelEdges();
    count = G.count();
    scc = G.scc();
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 130905; i++) r.nextDouble();
    double[] ja = r.doubles(2*V,1,100).toArray();
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
  }

  public int V() { return V; }

  public int E() { return E; }
  
  public Seq<BagX<Integer>> adj() { return adj; }
  
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

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("validateVertex: v out of bounds");
  }
  
  @SafeVarargs
  private final void validateVertex(int...x) {
    if (x == null || x.length == 0) return;
    for (int i = 0; i < x.length; i++)
      if (x[i] < 0 || x[i] >= V)
        throw new IllegalArgumentException("validateVertex: "+x[i]+" v out of bounds");
  }

  public void addEdge(int v, int w) {
    if (validate) validateVertex(v,w);
    adj.get(v).add(w);
    indegree.set(w,indegree.get(w)+1);
    E++;
  }

  public Iterable<Integer> adj(int v) { validateVertex(v); return adj.get(v); }

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
    for (BagX<Integer> b : adj) { int s = b.size(); if (s > max) max = s; }
    return max; 
  }
  
  public int minOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    int min = Integer.MAX_VALUE;
    for (BagX<Integer> b : adj) { int s = b.size(); if (s < min) min = s; }
    return min; 
  }
  
  public double avgOutdegree() {
    if (V == 0 || E == 0 || adj == null) return 0;
    double sum = 0;
    for (BagX<Integer> b : adj) sum += b.size();
    return sum/V; 
  }

  public int indegree(int v) { validateVertex(v); return indegree.get(v); }
  
  public boolean hasEdge(int v, int w) {
    validateVertex(v,w);
    for (int x : adj.get(v)) if (x == w) return true;
    return false;
  }
  
  public boolean hasSelfLoop() {
    for (int v = 0; v < V; v++) for (int w : adj.get(v))
      if (v == w) return true;
    return false;
  }
  
  public Seq<Integer> allSelfLoops() {
    // each self-loop is represented as an individual vertex v 
    // with the implication that edge v->v exists
    Seq<Integer> sl = new Seq<Integer>();
    for (int v = 0; v < V; v++)
      for (int w : adj.get(v)) if (v == w) sl.add(v);
    if (!sl.isEmpty()) selfLoops = sl;
    return sl;
  }

  public boolean hasParallelEdge() {
    // 2 edges are parallel if they connect the same ordered pair of vertices
    // self-loops are excluded as parallel edges since other methods identify them
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      for (int w : adj.get(v)) {
        if (v != w && m[w]) {
          parallelEdges = new Seq<Tuple2<Integer,Integer>>(new Tuple2<>(v,w));
          return true;
        }
        m[w] = true;
      }
      for (int w : adj.get(v))  m[w] = false;
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
    for (int v = 0; v < V; v++) {
      for (int w : adj.get(v)) {
        if (v != w && m[w]) p.add(new Tuple2<Integer,Integer>(v,w));
        m[w] = true;
      }
      for (int w : adj.get(v)) m[w] = false;
    }
    if (!p.isEmpty()) parallelEdges = p;
    return p;
  }
  
  public EuclidianDigraph reverse() {
    EuclidianDigraph reverse = new EuclidianDigraph(V);
    for (int u = 0; u < V; u++) for (int v : adj.get(u)) reverse.addEdge(u, v);
    return reverse;
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

//  public String toString() {
//    StringBuilder s = new StringBuilder();
//    s.append(V + " vertices, " + E + " edges " + NEWLINE);
//    for (int v = 0; v < V; v++) {
//      s.append(String.format("%d: ", v));
//      for (int w : adj[v]) {
//        s.append(String.format("%d ", w));
//      }
//      s.append(NEWLINE);
//    }
//    return s.toString();
//  }
  
//  public void addEdge(int v, int w) {
//    // use this then V has been set finally, E has been initialzed to 
//    // half its final value and all coordinates have been assigned
//    validateVertex(v); validateVertex(w);
//    E++;
//    adj.get(v).add(w);
//    adj.get(w).add(v);
//  }
  
  public boolean insertEdge(int v, int w) {
    // use this when V has been set but not finally, and the number of edges isn't set
    if (validate) validateVertex(v,w);
    int max = Math.max(v, w); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<Integer>());
    adj.get(v).add(w);
    s = indegree.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) indegree.add(0);
    indegree.set(w,indegree.get(w)+1);
    E++;
    return true;
  }
  
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
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("V = "+V+", E = "+E+NEWLINE);
    s.append(adj+NEWLINE);
    s.append(coords+NEWLINE);
    return s.toString();
  }
  
  public static final Color DarkOrange = new Color(255, 149, 0);
  
  public static void drawEdge(Tuple2<Double,Double> a, Tuple2<Double,Double> b, double w, 
      double h, double mod) {
    // modified from 
    // https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
    // draws a line with arrowhead from point a to mod distance before point b to allow space 
    // for a  text bubble around the latter.
    // the tuples a and b represent points, d is the width of the arrowead and h is its height
    // mod is the amount by which the end of the line is truncated.
    // used in showd()
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
  
  public void show() {
    // plots a digraph with unlabelled vertices
    // for general plotting of Digraphs
    // not for use with demos in RandomGridDigraph.main()
    double mod = .65;
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
    StdDraw.setPenRadius(.0014);
    for(int i = 0; i < V; i++)
      for (int j : adj.get(i)) drawEdge(coords.get(i),coords.get(j),.6,.6,mod);
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
      for (int j : adj.get(i)) drawEdge(coords.get(i),coords.get(j),1,1,mod);
    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
    StdDraw.setPenRadius(.1);
    for (int i = 0; i < points.length; i++) points[i].draw();
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
  }
  
  public void showGrid() {
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
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(.002);
    for(int i = 0; i < V; i++)
      for (int j : adj.get(i)) drawEdge(coords.get(i),coords.get(j),.07,.07,mod);
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
      for (int j : adj.get(i)) {
        Tuple2<Integer,Integer> t1 = new Tuple2<>(i,j);
        Tuple2<Integer,Integer> t2 = new Tuple2<>(j,i);
        if (!(extraEdges.contains(t1) || extraEdges.contains(t2)))
          drawEdge(coords.get(i),coords.get(j),.07,.07,mod);
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
      for (int j : adj.get(i)) {
        Tuple2<Integer,Integer> t1 = new Tuple2<>(i,j);
        Tuple2<Integer,Integer> t2 = new Tuple2<>(j,i);
        if (!(extraEdges.contains(t1) || extraEdges.contains(t2) 
            || pEdges.contains(t1)  || pEdges.contains(t2))) 
          drawEdge(coords.get(i),coords.get(j),.07,.07,mod);
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
      
//    SecureRandom r = new SecureRandom();
//    for (int i = 0 ; i < 100000; i++) r.nextDouble();
    // da has length 100
    final double[] da = new double[]{3.4966589854058445,69.42536412249684,21.73020389849091,
        51.23675907681606,59.97487768552062,56.8757420017543,7.921634042722501,
        37.41078512476867,79.41920739225044,9.63829740591865,89.6287047065898,
        95.52390826650708,24.174299591265655,35.62524014982281,32.16029763466064,
        92.88434189012891,54.15460379469761,5.552148078326123,91.36133409033728,
        67.62953270697744,43.73657088313533,13.413106794909258,97.00406365354091,
        8.460381154603146,64.37456985924275,94.65720565301648,39.52822335898523,
        94.31305551153444,85.33003631517695,49.37812829660677,87.0722932048014,
        75.7597164437562,81.27799250026936,93.17164682629343,82.10029245181619,
        30.215562443043122,19.202501403143287,23.348608096126885,44.98316254409433,
        56.579012082130404,84.50849764670708,25.732021697368914,6.154442759267563,
        40.137057357855966,41.261199553536585,83.03392066962651,37.54829322357091,
        29.768881026481075,36.60315102647703,1.5366055496682152,55.26894827623502,
        23.637476305779437,90.54673406111488,57.23067226043962,68.16693690875462,
        74.91742348978013,44.278918360599484,70.38360237160099,91.98473299387392,
        84.49072079354482,99.24497344278083,14.022885532321391,87.97240868857634,
        66.17892962483029,78.64751651462335,85.3424664322218,11.962545649270043,
        48.75415895149473,86.31395109782214,75.76879751262365,91.3003473881968,
        69.54737102563503,70.83486080390259,62.723786858508056,55.75093912848075,
        25.020967781211795,85.83150019571836,31.62286526240297,77.80430789274224,
        2.3700659099081256,7.674831179550397,81.87822828041828,65.85401339435136,
        5.931465122859836,71.34660423746894,41.98441272320196,4.089937427340149,
        32.56986711033446,62.64267546553868,61.6936759651506,87.88352793853946,
        31.532132744277593,66.0854041338319,14.686283381971199,58.85696705100387,
        95.79005553144451,32.445873313023576,28.652063547454116,26.597442802314003,
        17.441980756936417};
       
    // edges are from tinyDG.txt: 13 vertices, 22 edges 
    final String edg = "4 2 2 3 3 2 6 0 0 1 2 0 11 12 12 9 9 10 9 11 7 "
    +"9 10 12 11 4 4 3 3 5 6 8 8 6 5 4 0 5 6 4 6 9 7 6";
    
    final String das = arrayToString(take(da,26),900,1,1).replaceAll("\\[|\\]","")
        .replaceAll(","," ");
   
    String[] ed = edg.split("\\s+"); int c = 0;
    int[] ea = new int[ed.length];
    for (int i = 0; i < ea.length; i++) ea[i] = new Integer(ed[i]);
    
    System.out.println("EuclidianGraph constructor testing:\n");
    
    EuclidianDigraph g;
    
    System.out.println("1: testing new EuclidianGraph()");
    g = new EuclidianDigraph();
    while(c < ea.length) g.insertEdge(ea[c++],ea[c++]);
    int V = g.V(); c = 0;
    for (int i = 0; i < V; i++) g.addCoords(i,da[c++],da[c++]);
    g.trim();
    System.out.println(g); 
    g.show();
    pause(1);
    
    System.out.println("2: testing new EuclidianGraph(EuclidianGraph)");
    EuclidianDigraph g2 = new EuclidianDigraph(g);
    System.out.println(g2);
    g.show();
    pause(1);

    System.out.println("3: testing new EuclidianGraph(In)");
    In in = new In("tinyEuclidianGraph.txt");
    g = new EuclidianDigraph(in);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("4: testing new EuclidianGraph(int)");
    g = new EuclidianDigraph(13);
    c = 0;
    while(c < ea.length) g.addEdge(ea[c++],ea[c++]);
    V = g.V(); c = 0;
    for (int i = 0; i < V; i++) g.addCoords(i,da[c++],da[c++]);
    g.trim();
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("5: testing new EuclidianGraph(int,int,int[][],double[][])");
    int[][] edi = new int[22][2]; c = 0;
    for (int i = 0; i < 44; i+=2) edi[c++] = new int[]{ea[i],ea[i+1]};
    double[][] dai = new double[13][2]; c = 0;
    for (int i = 0; i < 26; i+=2) dai[c++] = new double[]{da[i],da[i+1]};
    g = new EuclidianDigraph(13, 22, edi, dai);
    System.out.println(g);
    g.show();
    pause(1);
   
    System.out.println("6: testing new EuclidianGraph(int,int,String,String)");
    g = new EuclidianDigraph(13, ed.length/2, edg, das);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("7: testing new EuclidianGraph(int,String,String)");
    g = new EuclidianDigraph(13, edg, das);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("8: testing new EuclidianGraph(String,String)");
    g = new EuclidianDigraph(edg, das);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("9: testing new EuclidianGraph(DigraphX,String)");
    g = new EuclidianDigraph(new DigraphX(13,edg),das);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("10: testing new EuclidianGraph(DigraphX)");
    g = new EuclidianDigraph(DigraphGeneratorX.strong(55,101,5));
    System.out.println(g);
    System.out.println(g);
    g.show();
    pause(1);

    System.out.println("11: testing new EuclidianGraph(DigraphX)");
    g = new EuclidianDigraph(DigraphGeneratorX.dag(6,4));
    System.out.println(g);
    g.show();

    System.out.println("12: testing new EuclidianGraph(ErdosRenyiGraph)");
    g = new EuclidianDigraph(new DigraphX(12,21));
    System.out.println("g.hasSelfLoop = "+g.hasSelfLoop());
    System.out.println("selfLoop = "+g.selfLoops());
    
    g.show();
    pause(1);

  }

}
