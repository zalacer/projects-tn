package graph;

import static java.lang.Math.pow;
import static v.ArrayUtils.*;

import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;

// same as DijkstraSPX but with integer weights using EdgeWeightedDigraphI
// instead of EdgeWeightedDigraphI
// for comparison testing with DijkstraSPBucketPQ
// ex4445

public class DijkstraSPI {
  private int[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeI[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Integer> pq;    // priority queue of vertices
  private EdgeWeightedDigraphI G;
  private int source;
  private int V;
  boolean quiet = false;
  private long relaxations = 0;
  private long inserts = 0;  // pq.insert counter
  private long delmins = 0;  // pq.delMin counter
  private long chgpris = 0;   // pq.decreaseKey counter

  public DijkstraSPI(EdgeWeightedDigraphI g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraSPI: EdgeWeightedDigraphI is null");
    for (DirectedEdgeI e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPI: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphI(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillInt(V,()->Integer.MAX_VALUE);
    edgeTo = new DirectedEdgeI[V];    
    distTo[s] = 0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      int v = pq.delMin(); delmins++;
//      System.out.print("pq.delMin="+v+" pq="); par(pq.toArray());
      for (DirectedEdgeI e : G.adj(v)) relax(e);
    }
    // check optimality conditions
//    assert check();
  }
  
  public DijkstraSPI(EdgeWeightedDigraphI g, int s, String quiet) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphI is null");
    for (DirectedEdgeI e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphI(g);
    V = G.V();
    validateVertex(s);
    source = s;
    if (quiet.equals("quiet") || quiet.equals("q")) this.quiet = true;
    distTo = fillInt(V,()->Integer.MAX_VALUE);
    edgeTo = new DirectedEdgeI[V];    
    distTo[s] = 0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      int v = pq.delMin(); delmins++;
      for (DirectedEdgeI e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSPI(EdgeWeightedDigraphI g, int s, boolean altRelax) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphI is null");
    for (DirectedEdgeI e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphI(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillInt(V,()->Integer.MAX_VALUE);
    edgeTo = new DirectedEdgeI[V];
    distTo[s] = 0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    if (altRelax)
      while (!pq.isEmpty()) {
        int v = pq.delMin();
        for (DirectedEdgeI e : G.adj(v)) altRelax(e);
      }
    else 
      while (!pq.isEmpty()) {
        int v = pq.delMin(); delmins++;
        for (DirectedEdgeI e : G.adj(v)) relax(e);
      }
    // check optimality conditions
    assert check();
  }
  
  public DijkstraSPI(EdgeWeightedDigraphI g, int s, int offset) {
    // for Ex4401, add offset to each edges weight before computing the SPT
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphI is null");
    for (DirectedEdgeI e : g.edges()) {
      e.setW(e.w()+offset);
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight after applying offset");
    }
    G = new EdgeWeightedDigraphI(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillInt(V,()->Integer.MAX_VALUE);
    edgeTo = new DirectedEdgeI[V];
    distTo[s] = 0;
    // relax vertices in order of distance from s
    pq = new IndexMinPQ<>(V);
    pq.insert(s, distTo[s]);
    inserts++;
    while (!pq.isEmpty()) {
      Integer[] pqa = pq.toArray();
      double[] pqw = new double[pqa.length];
      for (int i = 0; i < pqa.length; i++) pqw[i] = pq.keyOf(pqa[i]);
//      par(pqa); par(pqw); System.out.println(); 
      int v = pq.delMin(); delmins++;
      for (DirectedEdgeI e : G.adj(v)) relax(e);
    }
    // check optimality conditions
    assert check();
  }

  private void relax(DirectedEdgeI e) {
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
  
  private void altRelax(DirectedEdgeI e) {
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
  
  public int[] distTo() { return distTo; } ;
  
  public int distTo(int v) {
    // return length of shortest path from source to v 
    validateVertex(v);
    return distTo[v];
  }
  
  public DirectedEdgeI[] edgeTo() { return edgeTo; }
  
  public int longestSPweight () {
    int max = 0;
    for (Seq<DirectedEdgeI> s : allPaths()) {
      int w = weight(s);
      if (w > max) max = w;
    }
    return max;
  }

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
  
  public Seq<DirectedEdgeI> sptEdges() { return new Seq<>(edgeTo); }

  public boolean hasPathTo(int v) {
    // return true if exists path from source to v else false
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
  }

  public Iterable<DirectedEdgeI> pathTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<DirectedEdgeI> path = new Stack<DirectedEdgeI>();
    for (DirectedEdgeI e = edgeTo[v]; e != null; e = edgeTo[e.from()])
      path.push(e);
    return path;
  }
  
  public Seq<DirectedEdgeI> seqTo(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    Seq<DirectedEdgeI> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo(v)) return seq;
    Stack<DirectedEdgeI> path = new Stack<DirectedEdgeI>();
    for (DirectedEdgeI e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
    return new Seq<>(path);
  }
  
  public Seq<Seq<DirectedEdgeI>> allPaths() {
    // return a Seq of all shortest paths
    Seq<Seq<DirectedEdgeI>> paths = new Seq<>();
    for (int i = 0; i < V; i++) paths.add(seqTo(i));
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeI>> allPathsMono() {
    // return a Seq of all shortest monotonic or empty-if-non-monotonic paths
    Seq<Seq<DirectedEdgeI>> paths = new Seq<>();
    for (int i = 0; i < V; i++)
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeI>> justMonoPaths() {
    // return a Seq of all shortest monotonic paths
    Seq<Seq<DirectedEdgeI>> paths = new Seq<>();
    for (int i = 0; i < V; i++) 
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeI>> allPathsBitonic() {
    // return a Seq of all shortest bitonic or empty-if-non-monotonic paths
    Seq<Seq<DirectedEdgeI>> paths = new Seq<>();
    for (int i = 0; i < V; i++)
      if (isBitonicPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    return paths;
  }
  
  public Seq<Seq<DirectedEdgeI>> justBitonicPaths() {
    // return a Seq of all shortest bitonic paths
    Seq<Seq<DirectedEdgeI>> paths = new Seq<>();
    for (int i = 0; i < V; i++) 
      if (isBitonicPath(seqTo(i))) paths.add(seqTo(i));
    return paths;
  }
  
  public double diameter() {
    double max = Double.NEGATIVE_INFINITY;
    for (Seq<DirectedEdgeI> s : allPaths()) if (s.size() > max) max= s.size();
    return max;
  }

  private boolean check() {
    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    int s = source;
    // check that edge weights are nonnegative
    for (DirectedEdgeI e : G.edges()) {
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
      for (DirectedEdgeI e : G.adj(u)) {
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
      DirectedEdgeI e = edgeTo[v];
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
  
  public int weight(Seq<DirectedEdgeI> s) {
    if (s == null) return 0;
    int w = 0;
    for (DirectedEdgeI e : s) w += e.w();
    return w;
  }
  
  public static Seq<Seq<Seq<DirectedEdgeI>>> allPaths(EdgeWeightedDigraphI G) {
    Seq<Seq<Seq<DirectedEdgeI>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraSPI(G,i)).allPaths());
    return r;
  }
  
  public static Seq<Seq<Seq<DirectedEdgeI>>> allMonoPaths(EdgeWeightedDigraphI G) {
    Seq<Seq<Seq<DirectedEdgeI>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraSPI(G,i)).justMonoPaths());
    return r;
  }
  
  public static Seq<Seq<Seq<DirectedEdgeI>>> allBitonicPaths(EdgeWeightedDigraphI G) {
    Seq<Seq<Seq<DirectedEdgeI>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraSPI(G,i)).justBitonicPaths());
    return r;
  }
  
  public static void printAllDijkstraSP(EdgeWeightedDigraphI G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSP(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSP(EdgeWeightedDigraphI G, int source) {
    DijkstraSPI sp = new DijkstraSPI(G,source);
    sp.print(sp.allPaths());
  }
  
  public static void printAllDijkstraSPMono(EdgeWeightedDigraphI G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSPMono(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSPMono(EdgeWeightedDigraphI G, int source) {
    DijkstraSPI sp = new DijkstraSPI(G,source);
    sp.print(sp.justMonoPaths());
  }
  
  public static void printAllDijkstraSPBitonic(EdgeWeightedDigraphI G) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    for (int i = 0; i < V; i++) {
      System.out.println("source "+i+":");
      printDijkstraSPBitonic(G, i);
      System.out.println();
    }
  }
  
  public static void printDijkstraSPBitonic(EdgeWeightedDigraphI G, int source) {
    DijkstraSPI sp = new DijkstraSPI(G,source);
    sp.print(sp.justBitonicPaths());
  }
  
  @SafeVarargs // for ex4430 
  public static Seq<Seq<Seq<Integer>>> allPairsDijkstraSP(EdgeWeightedDigraphI G,
      Boolean...noisy) { return allDijkstraSP(G,noisy); } 
 
  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSP(EdgeWeightedDigraphI G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPI D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPI(G,i);
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
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPMono(EdgeWeightedDigraphI G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPI D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPI(G,i);
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
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPBitonic(EdgeWeightedDigraphI G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphI is null");
    int V = G.V();
    long relaxations = 0, inserts = 0, delmins = 0, chgpris = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraSPI D; Seq<Seq<Integer>> allp;    
    for (int i = 0; i < V; i++) {
      D = new DijkstraSPI(G,i);
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
  
  public static Seq<Integer> convert2(Iterable<DirectedEdgeI> edges) {
    // return Seq<Integer> of vertices in edges if it's a path else null
    if (edges == null) throw new IllegalArgumentException("convert: edges is null");
    Seq<Integer> seq = new Seq<>();
    Integer lastTo = null;
    for (DirectedEdgeI e : edges) {
      if (e == null)
        throw new IllegalArgumentException("convert: edges contains a null DirectedEdgeI");
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
  
  public void print(Seq<Seq<DirectedEdgeI>> paths) {
    if (paths == null) { System.out.println("null"); return; }
    for (int i = 0; i < paths.size(); i++) {
      Seq<DirectedEdgeI> p = paths.get(i);
      if (i != source && p.size() == 0) 
        System.out.println(source+">"+i+": no path");
      else System.out.println(source+">"+i+": ("
          +String.format("%d",weight(p))+") "+str(p));      
    }
  }
  
  public static String str(Seq<DirectedEdgeI> p) {
    StringBuilder sb = new StringBuilder();
    for (DirectedEdgeI e : p) sb.append(e + "   ");
    return sb.toString();
  }
  
  public static boolean isSimplePath(Seq<DirectedEdgeI> path) {
    // return true iff path is nonnull, nonempty, contains no null DirectedEdgeI,
    // no vertex is repeated in all its DirectedEdgeIs excluding duplicates that
    // link successive edges and every subsequence of two DirectedEdgeIs in it,
    // <edge1,edge2>, are linked, i.e. edge1.to() == edge2.from(), else return false
    if (path == null || path.isEmpty() || path.hasNull()) return false;
    Set<Integer> set = new HashSet<>();
    DirectedEdgeI[] d = path.to();
    for (int i = 0; i < d.length-1; i++) {
      if (d[i].to() != d[i+1].from()) return false;
      set.add(d[i].from());
      if (set.contains(d[i].to())) return false;
    }
    if (set.contains(d[d.length-1].to())) return false;
    return true;
  }
  
  public static boolean isMonoPath(Seq<DirectedEdgeI> path) {
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
  
  public static boolean isBitonicPath(Seq<DirectedEdgeI> path) {
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
  
  public static Integer sf(Double d) {
    // return the number of digits after the decimal point in d
    if (d == null) return null;
    if ((""+d).indexOf('.') == -1) return 0;
    else {
      String x = (""+d).replaceAll("[0-9+-]*\\.", "");
      return x.matches("0+") ? 0 : x.length();
    }
  }
  
  public static Integer sf(Seq<DirectedEdgeX> s) {
    // return multiplicative scale factor for the weights of edges 
    // in s for computing their sum using integer arithmetic
    if (s == null) return null;
    Integer sf = null;
    for (DirectedEdgeX e : s) {
      if (sf == null) sf = sf(e.w());
      else sf = Math.max(sf, sf(e.w()));
    }
    return sf; 
  }
  
  public static int sf(String ewd) {
    // return weight scale factor for graph ewd
    EdgeWeightedDigraphX g = new EdgeWeightedDigraphX(new In(ewd));
    return sf(g.edgeSeq());
  }
  
  public static EdgeWeightedDigraphI convert(String ewd) {
    EdgeWeightedDigraphX g = new EdgeWeightedDigraphX(new In(ewd));
    int sf = sf(g.edgeSeq());
    EdgeWeightedDigraphI i = new EdgeWeightedDigraphI(g.V());
    for (DirectedEdgeX e : g.edges()) i.addEdge(e.from(),e.to(),(int)(pow(10,sf)*e.w()));   
    return i;
  }

  public static void main(String[] args) {
    
//    System.out.println("sf(tinyEWD.txt)="+sf("tinyEWD.txt"));
    
    EdgeWeightedDigraphI G = convert("tinyEWD.txt");
    System.out.println("G:\n"+G);

    int s = 0;
    // compute shortest paths
    DijkstraSPI sp = new DijkstraSPI(G, s, "q");

    // print shortest path
    System.out.println(""+s+":");
    for (int t = 0; t < G.V(); t++) {
        if (sp.hasPathTo(t)) {
            StdOut.printf("%d to %d (%d)  ", s, t, sp.distTo(t));
            for (DirectedEdgeI e : sp.pathTo(t)) {
                StdOut.print(e + "   ");
            }
            StdOut.println();
        }
        else {
            StdOut.printf("%d to %d         no path\n", s, t);
        }
    }

  }

}
