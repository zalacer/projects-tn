package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;

// from DijkstraSP using DirectedEdgeX plus extra constuctors for 
// alternate relax method adding an offset to all edge weights

public class DijkstraSPX {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int V;
  boolean quiet = false;
  private long relaxations = 0;
  private long inserts = 0;  // pq.insert counter
  private long delmins = 0;  // pq.delMin counter
  private long chgpris = 0;   // pq.decreaseKey counter

  public DijkstraSPX(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[s] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      int v = pq.delMin(); delmins++;
//      System.out.print("pq.delMin="+v+" pq="); par(pq.toArray());
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
//    assert check();
  }
  
  public DijkstraSPX(EdgeWeightedDigraphX g, int s, String quiet) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    if (quiet.equals("quiet") || quiet.equals("q")) this.quiet = true;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[s] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      int v = pq.delMin(); delmins++;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSPX(EdgeWeightedDigraphX g, int s, boolean altRelax) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    distTo[s] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    if (altRelax)
      while (!pq.isEmpty()) {
        int v = pq.delMin();
        for (DirectedEdgeX e : G.adj(v)) altRelax(e);
      }
    else 
      while (!pq.isEmpty()) {
        int v = pq.delMin(); delmins++;
        for (DirectedEdgeX e : G.adj(v)) relax(e);
      }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSPX(EdgeWeightedDigraphX g, int s, double offset) {
    // for Ex4401, add offset to each edges weight before computing the SPT
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges()) {
      e.setW(e.w()+offset);
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight after applying offset");
    }
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];
    distTo[s] = 0.0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<Double>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      Integer[] pqa = pq.toArray();
      double[] pqw = new double[pqa.length];
      for (int i = 0; i < pqa.length; i++) pqw[i] = pq.keyOf(pqa[i]);
//      par(pqa); par(pqw); System.out.println(); 
      int v = pq.delMin(); delmins++;
      for (DirectedEdgeX e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }

  private void relax(DirectedEdgeX e) {
    // relax edge e using > and update pq if changed
    relaxations++;
    int u = e.u(), v = e.v();
    if (distTo[v] > distTo[u] + e.weight()) {
      distTo[v] = distTo[u] + e.weight();
      edgeTo[v] = e;
      if (pq.contains(v)) { 
        pq.changeKey(v, distTo[v]); 
        chgpris++;
//        System.out.print("chpris: "+e.to()+",w="+distTo[v]+
//            "; pq.minKey="+pq.minKey()+", pq.minIndex="+pq.minIndex()
//            +" pq="); par(pq.toArray());
      }
      else { pq.insert(v, distTo[v]); inserts++; }
    }
  }
  
  private void altRelax(DirectedEdgeX e) {
    // relax edge e using >= and update pq if changed
    int u = e.u(), v = e.v();
    if (distTo[v] >= distTo[u] + e.weight()) {
      distTo[v] = distTo[u] + e.weight();
      edgeTo[v] = e;
      if (pq.contains(v)) { 
        pq.changeKey(v, distTo[v]); 
        chgpris++;
      }
      else { pq.insert(v, distTo[v]); inserts++; }
    }
  }
  
  public long relaxations() { return relaxations; }
  
  public long inserts() { return inserts; }
  
  public long delmins() { return delmins; }
  
  public long chgpris() { return chgpris; } 
  
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
    // return a Seq of all shortest paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) paths.add(seqTo(i));
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeX>> allPathsMono() {
    // return a Seq of all shortest monotonic or empty-if-non-monotonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++)
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeX>> justMonoPaths() {
    // return a Seq of all shortest monotonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) 
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeX>> allPathsBitonic() {
    // return a Seq of all shortest bitonic or empty-if-non-monotonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++)
      if (isBitonicPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeX>> justBitonicPaths() {
    // return a Seq of all shortest bitonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) 
      if (isBitonicPath(seqTo(i))) paths.add(seqTo(i));
    return paths;
  }
  
  public double diameter() {
    double max = Double.NEGATIVE_INFINITY;
    for (Seq<DirectedEdgeX> s : allPaths()) if (s.size() > max) max= s.size();
    return max;
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
    if (!quiet) System.out.println("SPT satisfies optimality conditions");
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  public double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }
  
  public static void dispose(Draw[] draw) {
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
  
  public static Seq<Seq<Seq<DirectedEdgeX>>> allMonoPaths(EdgeWeightedDigraphX G) {
    Seq<Seq<Seq<DirectedEdgeX>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraSPX(G,i)).justMonoPaths());
    return r;
  }
  
  public static Seq<Seq<Seq<DirectedEdgeX>>> allBitonicPaths(EdgeWeightedDigraphX G) {
    Seq<Seq<Seq<DirectedEdgeX>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraSPX(G,i)).justBitonicPaths());
    return r;
  }
  
  public static void printAllDijkstraSP(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSP(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSP(EdgeWeightedDigraphX G, int source) {
    DijkstraSPX sp = new DijkstraSPX(G,source);
    sp.print(sp.allPaths());
  }
  
  public static void printAllDijkstraSPMono(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSPMono(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSPMono(EdgeWeightedDigraphX G, int source) {
    DijkstraSPX sp = new DijkstraSPX(G,source);
    sp.print(sp.justMonoPaths());
  }
  
  public static void printAllDijkstraSPBitonic(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSPBitonic(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSPBitonic(EdgeWeightedDigraphX G, int source) {
    DijkstraSPX sp = new DijkstraSPX(G,source);
    sp.print(sp.justBitonicPaths());
  }
  
  @SafeVarargs // for ex4430 
  public static Seq<Seq<Seq<Integer>>> allPairsDijkstra(EdgeWeightedDigraphX G,
      Boolean...noisy) { return allDijkstraSP(G,noisy); } 
 
  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSP(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPX D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPX(G,i);
      relaxations += D.relaxations();
      inserts += D.inserts();
      delmins += D.delmins();
      chgpris += D.chgpris();
      allp = D.allPaths().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      s.add(allp);
      if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
        System.out.println();
        pprint(allp,i);
        System.out.println();
      }
    }
    if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
      System.out.println("relaxations = "+relaxations);
      System.out.println("inserts = "+inserts);
      System.out.println("delmins = "+delmins);
      System.out.println("chgpris = "+chgpris);
    }
    return s;
  }
  
  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPMono(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPX D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPX(G,i);
      relaxations += D.relaxations();
      inserts += D.inserts();
      delmins += D.delmins();
      chgpris += D.chgpris();
//      allp = D.allPathsMono().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      allp = D.justMonoPaths().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      s.add(allp);
      if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
        System.out.println();
        pprint(allp,i);
      }
    }
    System.out.println();
    if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
      System.out.println("relaxations = "+relaxations);
      System.out.println("inserts = "+inserts);
      System.out.println("delmins = "+delmins);
      System.out.println("chgpris = "+chgpris);
    }
    return s;
  }
  
  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPBitonic(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPX D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPX(G,i);
      relaxations += D.relaxations();
      inserts += D.inserts();
      delmins += D.delmins();
      chgpris += D.chgpris();
//      allp = D.allPathsBitonic().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      allp = D.justBitonicPaths().map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      s.add(allp);
      if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
        System.out.println();
        pprint(allp,i);
      }
    }
    System.out.println();
    if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
      System.out.println("relaxations = "+relaxations);
      System.out.println("inserts = "+inserts);
      System.out.println("delmins = "+delmins);
      System.out.println("chgpris = "+chgpris);
    }
    return s;
  }
  
  public static Seq<Integer> convert2(Iterable<DirectedEdgeX> edges) {
    // return Seq<Integer> of vertices in edges if it's a path else null
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    Integer lastTo = null;
    for (DirectedEdgeX e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeX");
      if (lastTo != null && e.from() != lastTo.intValue())
        throw new IllegalArgumentException("convert: edges isn't a path");
      seq.add(e.from());
      seq.add(e.to());
      lastTo = e.to();
    }
    return seq.uniquePreservingOrder();
  }
  
  public static void pprint(Seq<Seq<Integer>> p, int i) {
    // pretty-print p
    if (p == null) throw new IllegalArgumentException("pprint: Seq is null");
    for (int j = 0; j < p.size(); j++) {
      Seq<Integer> s = p.get(j);
      if (0 < s.size()) System.out.println(s.head()+">"+s.last()+": "+s.mkString(">"));
      else if (i == j) System.out.println(i+">"+j+":");
      else System.out.println(i+">"+j+": no path");
    }  
  }
  
  public void print(Seq<Seq<DirectedEdgeX>> paths) {
    if (paths == null) { System.out.println("null"); return; }
    for (int i = 0; i < paths.size(); i++) {
      Seq<DirectedEdgeX> p = paths.get(i);
      if (i != source && p.size() == 0) 
        System.out.println(source+">"+i+": no path");
      else System.out.println(source+">"+i+": ("
          +String.format("%.2f",weight(p))+") "+str(p));      
    }
  }
  
  public static String str(Seq<DirectedEdgeX> p) {
    StringBuilder sb = new StringBuilder();
    for (DirectedEdgeX e : p) sb.append(e + "   ");
    return sb.toString();
  }
  
  public static boolean isSimplePath(Seq<DirectedEdgeX> path) {
    // return true iff path is nonnull, nonempty, contains no null DirectedEdgeX,
    // no vertex is repeated in all its DirectedEdgeXs excluding duplicates that
    // link successive edges and every subsequence of two DirectedEdgeXs in it,
    // <edge1,edge2>, are linked, i.e. edge1.to() == edge2.from(), else return false
    if (path == null || path.isEmpty() || path.hasNull()) return false;
    Set<Integer> set = new HashSet<>();
    DirectedEdgeX[] d = path.to();
    for (int i = 0; i < d.length-1; i++) {
      if (d[i].to() != d[i+1].from()) return false;
      set.add(d[i].from());
      if (set.contains(d[i].to())) return false;
    }
    if (set.contains(d[d.length-1].to())) return false;
    return true;
  }
  
  public static boolean isMonoPath(Seq<DirectedEdgeX> path) {
    // return true iff path is nonnull, nonempty, simple and the weights of 
    // successive edges in it are strictly increasing or decreasing 
    if (path == null) throw new IllegalArgumentException("isMonoPath: Seq is null");
    if (path.size() < 2) return false;
    if (!isSimplePath(path)) return false;
    double[] d = (double[])unbox(path.map(x->x.w()).to()); int i;
    if (d[0] == d[1]) return false;
    if (d[0] > d[1]) for (i = 2; i < d.length; i++) if(d[i-1] <= d[i]) return false;
    if (d[0] < d[1]) for (i = 2; i < d.length; i++) if(d[i-1] >= d[i]) return false;
    return true;
  }
  
  public static boolean isBitonicPath(Seq<DirectedEdgeX> path) {
    // return true iff path is nonnull, nonempty, simple and the weights of 
    // successive edges in it flip exactly once from strictly increasing to 
    // strictly decreasing
    if (path == null) throw new IllegalArgumentException("isBitoPath: Seq is null");
    if (path.size() < 3) return false;
    if (!isSimplePath(path)) return false;
    boolean flip = false;
    double[] d = (double[])unbox(path.map(x->x.w()).to()); int i,j;
    if (d[0] >= d[1]) return false;
    else { // d[0] < d[1] 
      L: for (i = 2; i < d.length; i++) {
        if(d[i-1] == d[i]) return false;
        if(d[i-1] > d[i]) {
          flip = true;
          for (j = ++i; j < d.length; j++) if(d[j-1] <= d[j]) return false;
          break L;
        }
      }
    }
    return flip;
  }

  public static void main(String[] args) {
    
//    In in = new In(args[0]);
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
//    int s = Integer.parseInt(args[1]);
    
    
//    allDijkstraSPMono(G,true);
//    System.out.println();
//    printAllDijkstraSPMono(G);
    
//    allPairsMono(G,true);
//    
//    System.exit(0);
  
    int s = 1;
    // compute shortest paths
    DijkstraSPX sp = new DijkstraSPX(G, s, "q");


    // print shortest path
    System.out.println(""+s+":");
    for (int t = 0; t < G.V(); t++) {
        if (sp.hasPathTo(t)) {
            StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
            for (DirectedEdgeX e : sp.pathTo(t)) {
                StdOut.print(e + "   ");
            }
            StdOut.println();
        }
        else {
            StdOut.printf("%d to %d         no path\n", s, t);
        }
    }
    
    /*  tinyEWD.txt 
    reverse 0->2 and make weight of 7->5 + 5->1 + 1->3= 7-3 == 0.39
    by setting weight 7->5 = 0.20, weight 5->1 = 0.09 and weight of 1->3 = 0.10
    8
    15
    4 5 0.35
    5 4 0.35
    4 7 0.37
    5 7 0.28
    7 5 0.28 change weight to 0.20
    5 1 0.32 change weight to 0.09
    0 4 0.38
    0 2 0.26 reverse to 2 0 0.26
    7 3 0.39
    1 3 0.29 change weight to 0.10
    2 7 0.34
    6 2 0.40
    3 6 0.52
    6 0 0.58
    6 4 0.93   
*/
//    In in = new In("tinyEWD.txt");
//    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
// 
//    // reverse edge 0->2
//    DirectedEdgeX x = G.findEdge(0, 2);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.reverse());
//      G.search();
//      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
//    } else {
//      System.out.println("could not find edge 0->2");
//    }
//    
//    // change weight of 7->5 to 0.20
//    x = G.findEdge(7, 5);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.20);
//      System.out.println("edge(7->5,0.28) replaced with edge(7->5,0.20");
//    } else {
//      System.out.println("could not find edge 7->5");
//    }
//    
//    // change weight of 5->1 to 0.09
//    x = G.findEdge(5, 1);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.09);
//      G.search();
//      System.out.println("edge(5->1,0.32) replaced with edge(5->1,0.09");
//    } else {
//      System.out.println("could not find edge 5->1");
//    }
//    
//    // change weight of 1->3 to 0.10
//    x = G.findEdge(1, 3);
//    if (x != null) { 
//      G.removeEdge(x); 
//      G.addEdge(x.from(),x.to(),0.10);
//      G.search();
//      System.out.println("edge(1,3,0.29) replaced with edge(1,3,0.10");
//    } else {
//      System.out.println("could not find edge 1->3");
//    }
//    
//    int V = G.V();
//    System.out.println("\nEdgeWeightedDigraphX G:\n"+G);
//    
//    int source = 2; // ex4405 requirement
//    
//    // define coordinates for EuclidianEdgeWeightedDigraphs
//    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
//    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
//    for (int i = 0; i < 2*V-1; i+=2) 
//      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
//    
//    // define vertex labels for EuclidianEdgeWeightedDigraphs
//    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
//    
//    EuclidianEdgeWeightedDigraph E;
//    String title;
//    
//    Draw[] draw = new Draw[2];
//    
//    // using the normal relax() with > comparison
//    DijkstraSPX sp1 = new DijkstraSPX(G, source, false);    
//    System.out.println("SP edgeTo using normal relax(): ");
//    for (DirectedEdgeX de : sp1.edgeTo()) System.out.println(de);
//    E = new EuclidianEdgeWeightedDigraph(G,coords);
//    title = "modified TinyEWD.txt using normal relax()|SPT from 2 (SPT edges are black)";
//    draw[0] = E.showEx44040507(2.2,sp1.sptEdges(),labels,sp1.parentEdges(),title,E.midPoints());
//    System.out.println();
//    
//    // using altRelax() with >= comparison
//    DijkstraSPX sp2 = new DijkstraSPX(G, source, true);
//    System.out.println("edgeTo using altRelax(): ");
//    for (DirectedEdgeX de : sp2.edgeTo()) System.out.println(de);
//    E = new EuclidianEdgeWeightedDigraph(G,coords);
//    title = "modified TinyEWD.txt using altRelax()|SPT from 2 (SPT edges are black)";
//    draw[1] = E.showEx44040507(2.2,sp2.sptEdges(),labels,sp2.parentEdges(),title,E.midPoints());
//    
//    System.out.println();
//    dispose(draw);

  
  }

}
