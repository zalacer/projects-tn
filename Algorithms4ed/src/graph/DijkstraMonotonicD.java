package graph;

import static v.ArrayUtils.*;

import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;

// DijkstraSPX modified to shortest paths that are monotonic and
// processing edges for each vertex in decreasing order by weight

public class DijkstraMonotonicD {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int V;
  boolean quiet = false;
  private int relaxations;
  private int insertions;

  public DijkstraMonotonicD(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraMonotonicD: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraMonotonicD: edge " + e + " has negative weight");
    G = new EdgeWeightedDigraphX(g);
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = fillDouble(V,()->Double.POSITIVE_INFINITY);
    edgeTo = new DirectedEdgeX[V];    
    distTo[s] = 0.0;
    pq = new IndexMinPQ<Double>(V);
    pq.insert(s, distTo[s]);
    insertions++;
    // find mono paths with edges sorted decreasing for each vertex
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      Seq<DirectedEdgeX> es = (new Seq<>(G.adj(v))).sortBy(x->-x.w());
      for (DirectedEdgeX e : es) relax(e);
    }
    verifyMono();
  }

  private void relax(DirectedEdgeX e) {
    relaxations++;
    //    System.out.println("relax: "+e);
    // relax edge e using > and update pq if path remains monotonic and is extended
    int u = e.u(), v = e.v();
    if (distTo[v] > distTo[u] + e.weight()) {
      // test monociticity
      if (edgeTo[u] != null && edgeTo[edgeTo[u].from()] != null) {
        double w1 = edgeTo[edgeTo[u].from()].w();
        double w2 = edgeTo[u].w();
        double w3 = e.w();
        if ((w1-w2) < 0 && (w2-w3) > 0 || (w1-w2) > 0 && (w2-w3) < 0) return;
      }
      distTo[v] = distTo[u] + e.weight();
      edgeTo[v] = e;
      if (pq.contains(v)) pq.decreaseKey(v, distTo[v]);
      else { pq.insert(v, distTo[v]); insertions++; }
    }
  }

  public int source() { return source; }

  public int relaxations() { return relaxations; }

  public int insertions() { return insertions; }

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
    // remove nonempty paths with length < 2 since they can't be monotonic
    if (!path.isEmpty() && path.size() < 2) return new Stack<DirectedEdgeX>();
    return path;
  }

  public Seq<DirectedEdgeX> seqTo(int v) {
    // if v is valid return a shortest path from source to v if it satisfies 
    // monotonic condition requiring size > 1 else return an empty path and
    // if v is invalid throws an exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo(v)) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
    if (path.size() < 2) return new Seq<>();
    return new Seq<>(path);
  }

  public Seq<Seq<DirectedEdgeX>> allPaths() {
    // return a Seq of all shortest monotonic or empty-if-non-monotonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    }
    return paths;
  }

  public Seq<Seq<DirectedEdgeX>> justMonoPaths() {
    // return a Seq of all shortest monotonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) 
      if (isMonoPath(seqTo(i))) paths.add(seqTo(i));
    return paths;
  }


  public double diameter() {
    double max = Double.NEGATIVE_INFINITY;
    for (Seq<DirectedEdgeX> s : allPaths()) if (s.size() > max) max= s.size();
    return max;
  }

  public double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  private void verifyMono() {
    Seq<Seq<DirectedEdgeX>> ps = justMonoPaths();
    for (Seq<DirectedEdgeX> p : ps)
      if (!isMonoPath(p)) {
        isMonoPath(p,true); // print debug info
        throw new IllegalArgumentException("path "+p+" isn't monotonic"); 
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

  public static Seq<Seq<Seq<DirectedEdgeX>>> allMonoPaths(EdgeWeightedDigraphX G) {
    Seq<Seq<Seq<DirectedEdgeX>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraMonotonicD(G,i)).allPaths());
    return r;
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
    DijkstraMonotonicD sp = new DijkstraMonotonicD(G, source);
    sp.print(sp.justMonoPaths());
  }

  @SafeVarargs
  public static void  printDijkstraSPMonoDebug(EdgeWeightedDigraphX G, int source, boolean...full) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    if (source < 0 || source > G.V()-1) throw new IllegalArgumentException(
        "source is out of bounds");
    DijkstraMonotonicD sp = new DijkstraMonotonicD(G, source);
    if (full != null && full.length != 0 && full[0]) {
      System.out.print("\nedgeTo[]:"); par(sp.edgeTo);
      System.out.print("distTo[]:"); par(sp.distTo);
    }
    System.out.println("\nmonotonic shortest paths source "+sp.source()+":");
    sp.print(sp.allPaths());
  }

  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPMono(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    int relaxations = 0, insertions = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraMonotonicD D; Seq<Seq<Integer>> allp;
    for (int i = 0; i < V; i++) {
      D = new DijkstraMonotonicD(G,i);
      relaxations += D.relaxations();
      insertions += D.insertions();
      Seq<Seq<DirectedEdgeX>> a = D.justMonoPaths();
      allp = a.map(x->x.isEmpty() ? new Seq<>() : convert2(x));
      s.add(allp);
      if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
        System.out.println();
        pprint(allp,i);  
      }
    }
    System.out.println();
    if (noisy != null && noisy.length > 0 && noisy[0].equals(true)) {
      System.out.println("relaxations = "+relaxations);
      System.out.println("insertions = "+insertions);
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

  @SafeVarargs
  public static boolean isMonoPath(Seq<DirectedEdgeX> path, boolean...db) {
    // return true iff path is nonnull, nonempty, simple and the weights of 
    // successive edges in it are strictly increasing or decreasing 
    if (path == null) throw new IllegalArgumentException("isMonoPath: Seq is null");
    boolean debug = false;
    if (db != null && db.length > 0 && db[0] == true) debug = true;

    if (path.size() < 2) {
      if (debug) System.err.println("path size < 2");
      return false;
    }
    if (!isSimplePath(path)) {
      if (debug) System.err.println("path isn't simple");
      return false;
    }
    double[] d = (double[])unbox(path.map(x->x.w()).to()); int i;
    if (debug) System.err.println("weights = "+arrayToString(d,999,1,1));
    if (d[0] == d[1]) return false;
    if (d[0] > d[1]) for (i = 2; i < d.length; i++) if(d[i-1] <= d[i]) {
      if (debug) System.err.println(d[i-1]+" <= "+d[i]);
      return false;
    }
    if (d[0] < d[1]) for (i = 2; i < d.length; i++) if(d[i-1] >= d[i]) {
      if (debug) System.err.println(d[i-1]+" >= "+d[i]);
      return false;
    }
    return true;
  }

  public static void main(String[] args) {

    //    In in = new In(args[0]);
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    //    int s = Integer.parseInt(args[1]);
    //    System.out.println("G:\n"+G);
    //    
    //    allDijkstraSPMono(G,true);
    //    System.out.println();
    printAllDijkstraSPMono(G);

    //
    //    printDijkstraSPMonoDebug(G,5,true);
    //    
    //    dijkstraSP(G,0);

    System.exit(0);

    int s = 0;
    // compute shortest paths
    DijkstraMonotonicD sp = new DijkstraMonotonicD(G, s);
    par(sp.distTo());

    // print shortest monotonic paths
    System.out.println(""+s+":");
    for (int t = 0; t < G.V(); t++) {
      if (t == sp.source() || sp.hasPathTo(t) && !sp.seqTo(t).isEmpty()) {
        StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
        for (DirectedEdgeX e : sp.pathTo(t)) {
          StdOut.print(e + "   ");
        }
        StdOut.println();
      }
      else {
        StdOut.printf("%d to %d         no strictly monotonic path\n", s, t);
      }
    }
  }

}
