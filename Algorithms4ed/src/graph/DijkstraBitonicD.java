package graph;

import static v.ArrayUtils.*;

import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;

//DijkstraSPX modified to shortest paths that are bitonic and
//processing edges for each vertex in decreasing order by weight

public class DijkstraBitonicD {
  private double[] distTo;          // distTo[v] = distance  of shortest s->v path
  private DirectedEdgeX[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int V;
  boolean quiet = false;
  private int relaxations;
  private int insertions;

  public DijkstraBitonicD(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraBitonicA: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraBitonicA: edge " + e + " has negative weight");
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
    // find bitonic paths with edges sorted increasing for each vertex
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      Seq<DirectedEdgeX> es = (new Seq<>(G.adj(v))).sortBy(x->-x.w());
      for (DirectedEdgeX e : es) relax(e);
    }
    verifyBitonic();
  }

  private void relax(DirectedEdgeX e) {
    relaxations++;
    //    System.out.println("relax: "+e);
    // relax edge e using > and update pq if path remains bitonic and is extended
    int u = e.u(), v = e.v();
    if (distTo[v] > distTo[u] + e.weight()) {
      // test bitonicity
      if (edgeTo[u] != null) {
        Seq<DirectedEdgeX> p = seqTo(u);
        if (p.size() > 2 && !isBitonicPath(p)) return;
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
    // remove nonempty paths with length < 3 since they can't be bitonic
    if (!path.isEmpty() && path.size() < 3) return new Stack<DirectedEdgeX>();
    return path;
  }

  public Seq<DirectedEdgeX> seqTo(int v) {
    // if v is valid return a shortest path from source to v if it satisfies 
    // bitonic condition requiring size > 2 else return an empty path and
    // if v is invalid throws an exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo(v)) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo[v]; e != null; e = edgeTo[e.from()]) path.push(e);
    if (path.size() < 3) return new Seq<>();
    return new Seq<>(path);
  }

  public Seq<Seq<DirectedEdgeX>> allPaths() {
    // return a Seq of all shortest bitonic or empty-if-non-bitonic paths
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      if (isBitonicPath(seqTo(i))) paths.add(seqTo(i));
      else paths.add(new Seq<>());
    }
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

  public double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  private void verifyBitonic() {
    Seq<Seq<DirectedEdgeX>> ps = justBitonicPaths();
    for (Seq<DirectedEdgeX> p : ps)
      if (!isBitonicPath(p)) {
        throw new IllegalArgumentException("path "+p+" isn't bitonic"); 
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

  public static Seq<Seq<Seq<DirectedEdgeX>>> allBitonicPaths(EdgeWeightedDigraphX G) {
    Seq<Seq<Seq<DirectedEdgeX>>> r = new Seq<>();
    for (int i = 0; i < G.V(); i++) r.add((new DijkstraBitonicD(G,i)).allPaths());
    return r;
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
    DijkstraBitonicD sp = new DijkstraBitonicD(G, source);
    sp.print(sp.justBitonicPaths());
  }

  @SafeVarargs
  public static void  printDijkstraSPBitonicDebug(EdgeWeightedDigraphX G, int source, boolean...full) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    if (source < 0 || source > G.V()-1) throw new IllegalArgumentException(
        "source is out of bounds");
    DijkstraBitonicD sp = new DijkstraBitonicD(G, source);
    if (full != null && full.length != 0 && full[0]) {
      System.out.print("\nedgeTo[]:"); par(sp.edgeTo);
      System.out.print("distTo[]:"); par(sp.distTo);
    }
    System.out.println("\nbitonic shortest paths source "+sp.source()+":");
    sp.print(sp.allPaths());
  }

  @SafeVarargs
  public static Seq<Seq<Seq<Integer>>> allDijkstraSPBitonic(EdgeWeightedDigraphX G,
      Boolean...noisy) {
    if (G == null) throw new IllegalArgumentException("EdgeWeightedDigraphX is null");
    int V = G.V();
    int relaxations = 0, insertions = 0;
    Seq<Seq<Seq<Integer>>> s = new Seq<>(); 
    DijkstraBitonicD D; Seq<Seq<Integer>> allp;
    for (int i = 0; i < V; i++) {
      D = new DijkstraBitonicD(G,i);
      relaxations += D.relaxations();
      insertions += D.insertions();
      Seq<Seq<DirectedEdgeX>> a = D.justBitonicPaths();
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
    //    System.out.println("G:\n"+G);
    //    
    //    allDijkstraSPMono(G,true);
    //    System.out.println();
    printAllDijkstraSPBitonic(G);

    //
    //    printDijkstraSPMonoDebug(G,5,true);
    //    
    //    dijkstraSP(G,0);

    System.exit(0);

    int s = 0;
    // compute shortest paths
    DijkstraBitonicD sp = new DijkstraBitonicD(G, s);
    par(sp.distTo());

    // print shortest bitonic paths
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
