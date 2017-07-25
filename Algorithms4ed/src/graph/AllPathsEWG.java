package graph;

import static v.ArrayUtils.*;

import java.util.HashSet;
import java.util.Set;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2;

// enumerate all simple paths for an EdgeWeightedDigraphX, etc.
// based on AllPaths for Graph

@SuppressWarnings("unused")
public class AllPathsEWG {
  private boolean[] onPath;         // vertices in current path
  private Stack<Integer> stack;     // the current path in reversed order
  private Seq<Seq<Integer>> paths;  // all simple paths from source to target
//  private int n;                    // size of paths
  private EdgeWeightedDigraphX G;
  private int source;
  private int target;

  public AllPathsEWG(EdgeWeightedDigraphX G, int source, int target) {
    if (G == null) throw new IllegalArgumentException(
        "AllPathsEWG: EdgeWeightedDigraphX is null");
    this.G = G;
    int V = G.V();
    this.source = source;
    this.target = target;
    if (source < 0 || source > V-1) throw new IllegalArgumentException(
        "AllPathsEWG: source vertex is out of bounds");
    if (target < 0 || target > V-1) throw new IllegalArgumentException(
        "AllPathsEWG: target vertex is out of bounds");
    onPath = new boolean[G.V()];
    stack = new Stack<Integer>();
    paths = new Seq<>();
    dfs(G, source, target);
  }

  private void dfs(EdgeWeightedDigraphX G, int source, int target) {
    stack.push(source);
    onPath[source] = true;
    if (source == target) { updatePaths(); }
    else for (DirectedEdgeX e : G.adj(source))
      if (!onPath[e.to()]) dfs(G, e.to(), target);
    stack.pop();
    onPath[source] = false;
  }
  
  @SafeVarargs
  private final void updatePaths(boolean...noisy) {
    Stack<Integer> reverse = stack.reverse();
    paths.add(new Seq<>(reverse));
    if (noisy != null && noisy.length != 0 && noisy[0] == true) {
      if (reverse.size() >= 1) System.out.print(reverse.pop());
      while (!reverse.isEmpty()) System.out.print("-" + reverse.pop());
      System.out.println();
    }
  }
  
  public int numberOfPaths() { 
    if (paths == null) return 0;
    return paths.size(); 
  }

  public int n() { 
    if (paths == null) return 0;
    return paths.size(); 
  }
  
  public int source() { return source; }
  
  public int target() { return target; }

  public Seq<Seq<Integer>> paths() { return paths; }
  
  public Seq<Seq<DirectedEdgeX>> edges() {
    Seq<Seq<Integer>> paths = paths();
    Seq<Seq<DirectedEdgeX>> edges = new Seq<>(paths.size());
    for (Seq<Integer> p : paths) edges.add(convert(G,p));
    return edges;
  }
  
  public static Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<Seq<DirectedEdgeX>>> edges = new Seq<>();
    Seq<Seq<Seq<Integer>>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j);
        if (paths != null && !paths.isEmpty()) paths.add(A.paths());
        if (edges != null && !edges.isEmpty())edges.add(A.edges());
      }
    }
    return new Tuple2<>(paths,edges);
  }
  
  public static Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<DirectedEdgeX>> edges = new Seq<>();
    Seq<Seq<Integer>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j);
        if (A.edges() != null && !A.edges().isEmpty()) {
          Seq<DirectedEdgeX> lightest = lightest(A.edges());
          if (lightest != null && !lightest.isEmpty()) {
            paths.add(convert2(lightest));
            edges.add(lightest);
          }
        }
      }
    }
    return new Tuple2<>(paths,edges);
  }
  
  public static Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allMonoPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allMonoPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<Seq<DirectedEdgeX>>> edges = new Seq<>();
    Seq<Seq<Seq<Integer>>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j);
        Seq<Seq<DirectedEdgeX>> mono = A.edges().filter(x->isMonoPath(x));
        if (!mono.isEmpty()) {
          paths.add(mono.map(x->convert2(x)));
          edges.add(mono);
        }
      }
    }
    return new Tuple2<>(paths,edges);
  }
  
  public static Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestMonoPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allMonoPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<DirectedEdgeX>> edges = new Seq<>();
    Seq<Seq<Integer>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j); 
        int source = A.source(), target = A.target();
        Seq<Seq<DirectedEdgeX>> mono = A.edges().filter(x->isMonoPath(x));
        if (mono != null && !mono.isEmpty()) {
          Seq<DirectedEdgeX> lightest = lightest(mono, source, target);
          if (lightest != null && !lightest.isEmpty()) {
            paths.add(convert2(lightest));
            edges.add(lightest);
          }
        }
      }
    }
    return new Tuple2<>(paths,edges);
  }
  
  public static Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allBitoPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allBitoPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<Seq<DirectedEdgeX>>> edges = new Seq<>();
    Seq<Seq<Seq<Integer>>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j);
        Seq<Seq<DirectedEdgeX>> bito = A.edges().filter(x->isBitonicPath(x));
        if (!bito.isEmpty()) {
          paths.add(bito.map(x->convert2(x)));
          edges.add(bito);
        }
      }
    }
    return new Tuple2<>(paths,edges);
  }
  
  public static Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestBitonicPaths(
      EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException(
        "allBitoPaths: EdgeWeightedDigraphX is null");
    int V = G.V();
    Seq<Seq<DirectedEdgeX>> edges = new Seq<>();
    Seq<Seq<Integer>> paths = new Seq<>();
    for (int i = 0; i < V; i++) {
      for (int j = 0; j < V; j++) {
        if (i == j) continue;
        AllPathsEWG A = new AllPathsEWG(G,i,j);
        Seq<Seq<DirectedEdgeX>> bito = A.edges().filter(x->isBitonicPath(x));
        if (bito != null && !bito.isEmpty()) {
          Seq<DirectedEdgeX> lightest = lightest(bito);
          if (lightest != null && !lightest.isEmpty()) {
            paths.add(convert2(lightest));
            edges.add(lightest);
          }
        }
      }
    }   
    return new Tuple2<>(paths,edges);
  }
  
  public static Seq<DirectedEdgeX> convert(EdgeWeightedDigraphX G, Seq<Integer> path) {
    if (G == null) throw new IllegalArgumentException(
        "convert: EdgeWeightedDigraphX is null");
    if (path == null) throw new IllegalArgumentException("convert: path is null");
    Seq<DirectedEdgeX> edges = new Seq<>();
    if (path.size() < 2) return edges;
    Integer[] a = path.to();
    for (int i = 0; i < a.length-1; i++) edges.add(G.findEdge(a[i], a[i+1]));
    return edges;
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
  
  public static void pprint(Seq<Seq<Integer>> p, int V) {
    // pretty-print p
    if (p == null) throw new IllegalArgumentException("pprint: Seq is null");
    int d = 2*(""+V).length()+1;
    for (int i = 0; i < p.size(); i++) {
      Seq<Integer> s = p.get(i);
      if (s == null || s.isEmpty()) continue;
      if (1 == s.size()) 
        System.out.printf("%-"+d+"s: %s\n",s.head()+">"+s.head(),s.mkString(">"));
      else
        System.out.printf("%-"+d+"s: %s\n",s.head()+">"+s.last(),s.mkString(">"));
    }  
  }
  
  @SafeVarargs
  public static void print(Seq<Seq<DirectedEdgeX>> paths, boolean...annotate) {
    if (paths == null) { System.out.println("null"); return; }
    boolean anno = true;
    if (annotate != null && annotate.length > 0) anno = annotate[0];
    Seq<DirectedEdgeX> lightest = lightest(paths);
    for (int i = 0; i < paths.size(); i++) {
      Seq<DirectedEdgeX> p = paths.get(i);
      System.out.print(p.head().from()+">"+p.last().to()+": ("
          +String.format("%.2f",weight(p))+") "+str(p));   
      if (p.equals(lightest) && anno) System.out.println("(lightest)");
      else System.out.println();
    }
  }
  
  public static double weight(Seq<DirectedEdgeX> s) {
    if (s == null) return 0;
    double w = 0;
    for (DirectedEdgeX e : s) w += e.w();
    return w;
  }
  
  public static Seq<DirectedEdgeX> lightest(Seq<Seq<DirectedEdgeX>> edges) {
    // return the lightest and shortest nonnull Seq<DirectedEdgeX> in edges if possible
    if (edges == null) throw new IllegalArgumentException("lightest: Seq is null");
    if (edges.isEmpty()) throw new IllegalArgumentException("lightest: Seq is empty");
    Seq<Seq<DirectedEdgeX>> edgs = edges.sortBy(x->x!=null); 
    if (edgs.isEmpty()) throw new IllegalArgumentException("lightest: Seq has all null elements"); 
    edgs = edges.sortBy(x->weight(x)); 
    Seq<Seq<DirectedEdgeX>> prospective = new Seq<>();
    prospective.add(edgs.get(0));
    double[] d = (double[])unbox(edgs.map(x->weight(x)).to());
    for (int i = 0; i < d.length-1; i++) {
      if (d[i] == d[i+1]) prospective.add(edgs.get(i+1));
    }
    if (prospective.size() == 1) return prospective.get(0);
    else {
      prospective = prospective.sortBy(x->x.size());
      return prospective.get(0);
    }
  }
  
  public static Seq<DirectedEdgeX> lightest(Seq<Seq<DirectedEdgeX>> edges,
      int source, int target) {
    // return the lightest and shortest nonnull Seq<DirectedEdgeX> in edges if possible
    if (edges == null) throw new IllegalArgumentException("lightest: Seq is null");
    if (edges.isEmpty()) throw new IllegalArgumentException("lightest: Seq is empty");
    Seq<Seq<DirectedEdgeX>> edgs = edges.sortBy(x->x!=null); 
    if (edgs.isEmpty()) throw new IllegalArgumentException("lightest: Seq has all null elements"); 
    edgs = edges.sortBy(x->weight(x)); 
    Seq<Seq<DirectedEdgeX>> prospective = new Seq<>();
    prospective.add(edgs.get(0));
    double[] d = (double[])unbox(edgs.map(x->weight(x)).to());
    for (int i = 0; i < d.length-1; i++) {
      if (d[i] == d[i+1]) prospective.add(edgs.get(i+1));
    }
    if (prospective.size() == 1) return prospective.get(0);
    else {
      System.out.println("for source "+source+" and target "+target+" the "
          +"following paths are tied for least weight:");
      for (Seq<DirectedEdgeX> p : prospective) {
        System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
            +"("+weight(p)+") "+str(p));   
      }
      System.out.println("out of these paths the following was selected:");
      prospective = prospective.sortBy(x->x.size());
      Seq<DirectedEdgeX> p = prospective.get(0);
      System.out.println(">> "+p.head().from()+">"+p.last().to()+": "
          +"("+weight(p)+") "+str(p));   
      return p;
    }
  }
  
  public static boolean isPath(Seq<DirectedEdgeX> path) {
    if (path == null) throw new IllegalArgumentException("isPath: Seq is null");
    if (path.isEmpty()) throw new IllegalArgumentException("isPath: Seq is empty");
    if (path.size() == 1) return true;
    DirectedEdgeX[] d = path.to();
    for (int i = 0; i < d.length-1; i++) if (d[i].to() != d[i+1].from()) return false;
    return true;
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
  
  public static String str(Seq<DirectedEdgeX> p) {
    StringBuilder sb = new StringBuilder();
    for (DirectedEdgeX e : p) sb.append(e + "   ");
    return sb.toString();
  }
  
  public static String stfmt(Seq<DirectedEdgeX> p) {
    StringBuilder sb = new StringBuilder();
    if (p == null) return sb.append("null").toString();
    return sb.append(p.head().from()+">"+p.last().to()+": ("
        +String.format("%.2f",weight(p))+") "+str(p)).toString();  
  }

  // test client
  public static void main(String[] args) {
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);

    //        StdOut.println();
    //        StdOut.println("all simple paths between 0 and 6:");
    //        AllPathsEWG allpaths1 = new AllPathsEWG(G, 0, 6);
    //        StdOut.println("# paths = " + allpaths1.numberOfPaths());
    
//    Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allPaths;
//    Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allMonoPaths;
//    Tuple2<Seq<Seq<Seq<Integer>>>,Seq<Seq<Seq<DirectedEdgeX>>>> allBitoPaths;
//    Seq<Seq<Seq<Integer>>> paths;
//    Seq<Seq<Seq<DirectedEdgeX>>> edges;
//    
//    System.out.println("\nall paths:");
//    allPaths = allPaths(G);
//    paths = allPaths._1;
//    System.out.println("\npaths:");
//    for (Seq<Seq<Integer>> s : paths) { pprint(s,G.V()); System.out.println(); }
//    edges = allPaths._2;
//    System.out.println("\nedges:");
//    for (Seq<Seq<DirectedEdgeX>> s : edges) { print(s); System.out.println(); }
//
//    System.out.println("\nall mono paths:");
//    allMonoPaths = allMonoPaths(G);
//    paths = allMonoPaths._1;
//    System.out.println("\npaths:");
//    for (Seq<Seq<Integer>> s : paths) { pprint(s,G.V()); System.out.println(); }
//    edges = allMonoPaths._2;
//    System.out.println("\nedges:");
//    for (Seq<Seq<DirectedEdgeX>> s : edges) { print(s); System.out.println(); }
//    
//    
//    System.out.println("\nall bito paths:");
//    allBitoPaths = allBitoPaths(G);
//    paths = allBitoPaths._1;
//    System.out.println("\npaths:");
//    for (Seq<Seq<Integer>> s : paths) { pprint(s,G.V()); System.out.println(); }
//    edges = allBitoPaths._2;
//    System.out.println("\nedges:");
//    for (Seq<Seq<DirectedEdgeX>> s : edges) { print(s); System.out.println(); }
//    
    
    // lightest paths
//    Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestPaths;
    Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestMonoPaths;
//    Tuple2<Seq<Seq<Integer>>,Seq<Seq<DirectedEdgeX>>> allLightestBitoPaths;
    Seq<Seq<Integer>> lpaths;
    Seq<Seq<DirectedEdgeX>> ledges;
    
//    System.out.println("\nall lightest paths:");
//    allLightestPaths = allLightestPaths(G);
//    lpaths = allLightestPaths._1;
//    System.out.println("\npaths:");
//    pprint(lpaths,G.V()); 
//    ledges = allLightestPaths._2;
//    System.out.println("\nedges:");
//    print(ledges,false); 
//    
    System.out.println("\nall lightest mono paths:");
    allLightestMonoPaths = allLightestMonoPaths(G);
    lpaths = allLightestMonoPaths._1;
    System.out.println("\npaths:");
    pprint(lpaths,G.V()); 
    ledges = allLightestMonoPaths._2;
    System.out.println("\nedges:");
    print(ledges,false); 
//    
//    System.out.println("\nall lightest bito paths:");
//    allLightestBitoPaths = allLightestBitoPaths(G);
//    lpaths = allLightestBitoPaths._1;
//    System.out.println("\npaths:");
//    pprint(lpaths,G.V()); 
//    ledges = allLightestBitoPaths._2;
//    System.out.println("\nedges:");
//    print(ledges,false); 
    
//    StdOut.println();
//    int source = 1, target = 7;
//    StdOut.println("all simple paths between "+source+" and "+target+":");
//    AllPathsEWG allpaths2 = new AllPathsEWG(G, source, target);
//    StdOut.println("# paths = " + allpaths2.numberOfPaths());
//    System.out.println("\npaths:");
//    pprint(allpaths2.paths(),G.V());
//    System.out.println("\nedges:");
//    Seq<Seq<DirectedEdgeX>> edges = allpaths2.edges();
////    for (Seq<DirectedEdgeX> seq : edges) System.out.println(seq);
//    print(allpaths2.edges());
//    System.out.println("\nlightest: "+stfmt(lightest(edges)));
    
//    DirectedEdgeX a = new DirectedEdgeX(0,1,.1);
//    DirectedEdgeX b = new DirectedEdgeX(1,2,.2);
//    DirectedEdgeX c = new DirectedEdgeX(2,5,.3);
//    DirectedEdgeX d = new DirectedEdgeX(3,4,.2);
//    DirectedEdgeX e = new DirectedEdgeX(4,5,.5);
//    Seq<DirectedEdgeX> s = new Seq<>(a,b,c,d,e);
//    System.out.println(stfmt(s));
//    System.out.println("isPath = "+isPath(s));
//    System.out.println("isSimplePath = "+isSimplePath(s));
//    System.out.println("isMonoPath = "+isMonoPath(s));
//    System.out.println("isBitoPath = "+isBitoPath(s));

  }


}

