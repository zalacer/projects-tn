package graph;

import static analysis.Draw.*;
import static v.ArrayUtils.*;

import java.awt.Color;
import java.awt.Font;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import analysis.Draw;
import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import st.HashSET;
import v.Tuple2;
import v.Tuple4;
import v.Tuple6;
import v.Tuple9;

// ex4137
//implemented using Seq<BagX<Integer>> instead of BagX<Integer>[] for adj

//@SuppressWarnings("unused")
public class EuclidianGraph {
  private final String NEWLINE = System.getProperty("line.separator");
  private final int DEFAULTLEN = 17;
  private int V;
  private int E;
  private Seq<BagX<Integer>> adj;
  private Seq<Tuple2<Double,Double>> coords;
  private transient boolean[] marked;
  private transient int[] edgeTo;
  private transient int[] id; // component ids
  private transient int[] size;  // size[id] = number of vertices in given component
  private transient int count = 0; // number of connected components
  private transient Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()
  private transient Stack<Integer> selfLoop = null;
  private transient Stack<Integer> parallelEdges = null;
  private transient boolean validate = true;
  
  
  public EuclidianGraph() {
    V = E = 0;
    adj = Seq.fill(DEFAULTLEN, ()->new BagX<Integer>());
    coords = Seq.fill(DEFAULTLEN, ()->new Tuple2<Double,Double>());
    validate = false;
//    System.out.println(
//        "insert edges with insertEdge(int,int) and coordinates with addCoords(int,double,double)"
//        + "\nvertices without coordinates won't be graphed"
//        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }
  
  public EuclidianGraph(int v) {
    if (v < 0) throw new IllegalArgumentException(
        "EuclidianGraph constructor: Number of vertices must be nonnegative");
    V = v; E = 0;
    adj = Seq.fill(V, ()->new BagX<Integer>());
    coords = Seq.fill(V, ()->new Tuple2<Double,Double>());
//    System.out.println(
//        "add edges with addEdge(int,int) and coordindates with addCoords(int,double,double)"
//        + "\nvertices without coordinates won't be graphed"
//        + "\nfinally run trim() to remove unused entries at the ends of adj and coords");
  }
  
  public EuclidianGraph(int v, int e, int[][] edges, double[][] xy) {
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
        if (edges[i][j] < 0 || edges[i][j] > 100)
          throw new IllegalArgumentException("EuclidianGraph constructor: "
              + "an element of edges is out of range");
    }
    V = v;
    adj = Seq.fill(V, ()->new BagX<Integer>());
    coords = new Seq<Tuple2<Double,Double>>(V);
    for (int i = 0; i < V; i++) coords.add(new Tuple2<Double,Double>(xy[i][0],xy[i][1]));
    validate = false; // already did validation of all vertices in edges above
    for (int i = 0; i < e; i++)  addEdge(edges[i][0],edges[i][1]);      
    search();
  }
  
  public EuclidianGraph(int v, int e, String edges, String coords) {
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
      if (vertex < 0 || vertex > v -1) throw new IllegalArgumentException(
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
    
    adj = Seq.fill(V, ()->new BagX<Integer>());
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    validate = false; // already did validation of all vertices in e above
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    search();
  }
  
  public EuclidianGraph(int v, String edges, String coords) {
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
      if (vertex < 0 || vertex > v -1) throw new IllegalArgumentException(
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
    
    adj = Seq.fill(V, ()->new BagX<Integer>());
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    validate = false; // already did validation of all vertices in e above
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    search();
  }
  
  public EuclidianGraph(String edges, String coords) {
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
    adj = Seq.fill(V, ()->new BagX<Integer>());
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
    validate = false; // vertex validations implicitly done above
    for (Tuple2<Integer,Integer> t : te) addEdge(t._1,t._2);      
    search();
  }

  public EuclidianGraph(In in) {
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
    search();
  }
  
  public EuclidianGraph(EuclidianGraph G) {
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    V = G.V();
    E = G.E();
    adj = G.adj();
    coords = G.coords();
    marked = G.marked();
    id = G.id();
    size = G.size();
    count = G.count();
    cycle = G.cycle();
    validate = G.validate();
  }
  
  public EuclidianGraph(GraphX G, String coords) {
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
    marked = G.marked();
    id = G.id();
    size = G.size();
    count = G.count();
    cycle = G.cycle();
    String[] da = coords.split("\\s+");
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
  
  public EuclidianGraph(GraphX G) {
    // create EuclidianGraph from G using random coords in the range 1.-99.
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    this.V = G.V();
    this.E = G.E();
    this.adj = new Seq<BagX<Integer>>(G.adj());
    this.marked = G.marked();
    this.id = G.id();
    this.size = G.size();
    this.count = G.count();
    this.cycle = G.cycle();
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 130905; i++) r.nextDouble();
    double[] ja = r.doubles(2*V,1,100).toArray();
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
  }
  
  public EuclidianGraph(GraphSB G) {
    // create EuclidianGraph from G using random coords in the range 1.-99.
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    this.V = G.V();
    this.E = G.E();
    this.adj = G.adj();
    this.marked = G.marked();
    this.id = G.id();
    this.size = G.size();
    this.count = G.count();
    this.cycle = G.cycle();
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 130905; i++) r.nextDouble();
    double[] ja = r.doubles(2*V,1,100).toArray();
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
  }
  
  public EuclidianGraph(ErdosRenyiGraph G) {
    // create EuclidianGraph from G using random coords in the range 1.-99.
    if (G == null) throw new IllegalArgumentException(
        "EuclidianGraph constructor: G == null");
    this.V = G.V();
    this.E = G.E();
    this.adj = new Seq<BagX<Integer>>(G.adj());
    this.marked = G.marked();
    this.id = G.id();
    this.size = G.size();
    this.count = G.count();
    this.cycle = G.cycle();
    SecureRandom r = new SecureRandom(); r.setSeed(System.currentTimeMillis());
    for (int i = 0; i < 100000; i++) r.nextDouble();
    double[] ja = r.doubles(2*V,1,100).toArray();
    int k = 0;
    Tuple2<Double,Double>[] td = ofDim(Tuple2.class,V);
    for (int i = 0; i < 2*V; i+=2)  td[k++] = new Tuple2<Double,Double>(ja[i],ja[i+1]);   
    this.coords = new Seq<Tuple2<Double,Double>>(td);  
  }
  
  
  public int V() { return V; }

  public int E() { return E; }
    
  public void setE(int e) { E = e; }
  
  public void setV(int v) { V = v; }

  public Seq<BagX<Integer>> adj() { return adj.clone(); }
  
  public void setAdj(Seq<BagX<Integer>> seq) { adj = seq; }
  
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
      for (int j : adj.get(i)) a[i][j] = 1;
    return a;
  }

  public int count() {
    // return the number of connected components
    return count; }

  private void validateVertex(int v) {
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
  }

  private void dfs(int v) {
    marked[v] = true;
    id[v] = count;
    size[count]++;
    for (int w : adj.get(v)) if (!marked[w]) dfs(w);
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
    for (int w : adj(v)) {
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

  public void addEdge(int v, int w) {
    // use this when V has been set finally, E has been initialzed to 
    // half its final value and all coordinates have been assigned
    if (validate) { validateVertex(v); validateVertex(w); }
    E++;
    adj.get(v).add(w);
    adj.get(w).add(v);
  }
  
  public boolean insertEdge(int v, int w) {
    // use this when V has been set but not finally, and the number of edges isn't set
    if (validate) { validateVertex(v); validateVertex(w); }
    int max = Math.max(v, w); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<Integer>());
    adj.get(v).add(w);
    adj.get(w).add(v);
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
    BagX<Integer> bv = adj.get(v); boolean fw = false; int iw = -1;  
    BagX<Integer> bw = adj.get(w); boolean fv = false; int iv = -1;
    int c = 0;
    for (Integer i : bv) {
      if (i != null && i == w) { iw = c; fw = true; break; }
      c++;
    }
    if (fw == false) return false;
    c = 0;
    for (Integer i : bw) {
      if (i != null && i == v) { iv = c; fv = true; break; }
      c++;
    }
    if (fv == false) return false;
    bv.remove(iw); bw.remove(iv);
    E--;
    return true;
  }
  
  public Iterable<Integer> adj(int v) {
    validateVertex(v);
    return adj.get(v);
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

  public boolean hasEdge(int v, int w) {
    if (validate) { validateVertex(v); validateVertex(w); }
    if (adj.get(v).contains(w)) return true;
    return false;
  }
  
  public boolean isEdge(int v, int w) {
    if (adj.size() > v-1) return false;
    if (adj.get(v).contains(w)) return true;
    return false;
  }

  public int numberOfSelfLoops() {
    // from GraphClient
    int count = 0;
    for (int v = 0; v < V; v++)
      for (int w : adj.get(v))
        if (v == w) count++;
    return count/2; // each edge counted twice
  }

  public boolean hasSelfLoop() {
    // from Cycle
    for (int v = 0; v < V; v++) {
      for (int w : adj.get(v)) {
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
      for (int w : adj.get(v)) {
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
      for (int w : adj.get(v)) {
        m[w] = false;
      }
    }
    return false;
  }


  
  public EuclidianGraph clone() { return new EuclidianGraph(this); }
  
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
      for (int j : adj.get(i)) points[i].drawTo(points[j]);
    
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
      for (int j : adj.get(i)) points[i].drawTo(points[j]);
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
      for (int j : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(j);
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
      for (int j : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(j);
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
      for (int j : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
        t2 = coords.get(j);
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
      for (int j : adj.get(i)) { //coords.get(i)._1.drawTo(points[j]);
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
      for (int j : adj.get(i)) points[i].drawTo(points[j]);
    
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
      for (int j : adj.get(i)) points[i].drawTo(points[j]);
    
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
      for (int j : adj.get(i)) 
        if (!(extraEdges.contains(new Tuple2<>(i,j)))) 
          points[i].drawTo(points[j]);

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
      for (int j : adj.get(i)) {
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
   
//    System.out.println("da.length="+da.length);
    
/*  normal output for tinyGex3.txt
    12 vertices, 21 edges 
    [(2,6),(8,4,8,11),(5,5,6,0,3),(10,10,6,2),(1,8),(5,5,2,10,2),(2,3,0),
     (7,7,8,11),(1,1,11,7,4),(9,9),(3,5,3),(8,7,1)]
*/
    // edges from tinyGex3.txt
    final String edg = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 "
          +"6 2 5 2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    // ed has length 42
    String[] ed = edg.split("\\s+"); int c = 0;
//    System.out.println("ed.length="+ed.length);
    int[] ea = new int[ed.length];
    for (int i = 0; i < ea.length; i++) ea[i] = new Integer(ed[i]);
    
    final String das = arrayToString(take(da,24),900,1,1).replaceAll("\\[|\\]","")
        .replaceAll(","," ");
    
    System.out.println("EuclidianGraph constructor testing:\n");
    
    EuclidianGraph g;
    
    System.out.println("1: testing new EuclidianGraph()");
    g = new EuclidianGraph();
    while(c < ea.length) g.insertEdge(ea[c++],ea[c++]);
    int V = g.V(); c = 0;
    for (int i = 0; i < V; i++) g.addCoords(i,da[c++],da[c++]);
    g.trim();
    System.out.println(g);
    g.search(); // counts CC, sets id and count
    System.out.print("id"); par(g.id());
    System.out.println("count="+g.count());
    System.out.println("avgDegree="+g.avgDegree());
    System.out.println("numberOfSelfLoops="+g.numberOfSelfLoops());
    System.out.println("adj="+g.adj());
    System.out.println("hasSelfLoop="+g.hasSelfLoop());
    if (g.hasSelfLoop()) System.out.println("selfLoop="+g.selfLoop());
    System.out.println("hasParallelEdges="+g.hasParallelEdges());
    if (g.hasParallelEdges()) System.out.println("parallelEdges="+g.parallelEdges()+"\n");
    
    System.out.println("2: testing new EuclidianGraph(EuclidianGraph)");
    EuclidianGraph g2 = new EuclidianGraph(g);
    System.out.println(g2);
    
    System.out.println("3: testing new EuclidianGraph(In)");
    In in = new In("tinyEuclidianGraph.txt");
    g = new EuclidianGraph(in);
    System.out.println(g);
    
    System.out.println("4: testing new EuclidianGraph(int)");
    g = new EuclidianGraph(12);
    c = 0;
    while(c < ea.length) g.addEdge(ea[c++],ea[c++]);
    V = g.V(); c = 0;
    for (int i = 0; i < V; i++) g.addCoords(i,da[c++],da[c++]);
    g.trim();
    System.out.println(g);
    
    System.out.println("5: testing new EuclidianGraph(int,int,int[][],double[][])");
    int[][] edi = new int[21][2]; c = 0;
    for (int i = 0; i < 42; i+=2) edi[c++] = new int[]{ea[i],ea[i+1]};
    double[][] dai = new double[12][2]; c = 0;
    for (int i = 0; i < 24; i+=2) dai[c++] = new double[]{da[i],da[i+1]};
    g = new EuclidianGraph(12, 21, edi, dai);
    System.out.println(g);
        
    System.out.println("6: testing new EuclidianGraph(int,int,String,String)");
    g = new EuclidianGraph(12, ed.length/2, edg, das);
    System.out.println(g);
    
    System.out.println("7: testing new EuclidianGraph(int,String,String)");
    g = new EuclidianGraph(12, edg, das);
    System.out.println(g);

    System.out.println("8: testing new EuclidianGraph(String,String)");
    g = new EuclidianGraph(edg, das);
    System.out.println(g);
    
    System.out.println("9: testing new EuclidianGraph(GraphX,String)");
    g = new EuclidianGraph(new GraphX(12,edg),das);
    System.out.println(g);

    System.out.println("10: testing new EuclidianGraph(GraphX)");
    g = new EuclidianGraph(GraphGeneratorX.completeBipartite(3, 7));
    System.out.println(g);
    
    System.out.println("11: testing new EuclidianGraph(ErdosRenyiGraph)");
    g = new EuclidianGraph(new ErdosRenyiGraph(12,21));
    System.out.println(g);
    g.show();
    
  }

}
