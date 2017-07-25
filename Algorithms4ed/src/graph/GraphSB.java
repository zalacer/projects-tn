package graph;

import static v.ArrayUtils.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import v.Tuple2C;

//implemented using Seq<BagX<Integer>> instead of BagX<Integer>[] for adj

public class GraphSB {
  private final String NEWLINE = System.getProperty("line.separator");
  private final int DEFAULTLEN = 17;
  private int V;
  private int E;
  private Seq<BagX<Integer>> adj;
  private boolean[] marked;  
  private int[] id; // component ids
  private int[] size;  // size[id] = number of vertices in given component
  private int count = 0; // number of connected components
  private Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()
  boolean validate = true;
  
  public GraphSB() {
    V = E = 0;
    adj = new Seq<BagX<Integer>>(DEFAULTLEN);
    for (int i = 0; i < DEFAULTLEN; i++) adj.add(new BagX<Integer>());
  }
  
  public GraphSB(int v) {
    // for implementation of one pass SymbolGraph
    if (V < 0) throw new IllegalArgumentException(
        "GraphX constructor: Number of vertices must be nonnegative");
    V = v; E = 0;
    adj = new Seq<BagX<Integer>>(V);
    for (int i = 0; i < V; i++) adj.add(new BagX<Integer>());
  }
  
  public GraphSB(int v, int e, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "v is < 0");
    if (e < 0) throw new IllegalArgumentException("GraphX constructor: "
        + "e is < 0");
    if (x.length < e) throw new IllegalArgumentException("GraphX constructor: "
        + "the length of x is < e");
    V = v;
    if (v == 0) return;
    adj = new Seq<BagX<Integer>>(v);
    for (int i = 0; i < v; i++)  adj.add(new BagX<Integer>());
    for (int i = 0; i < e; i++) {
      if (x[i].length < 2) throw new IllegalArgumentException("GraphX constructor: "
          + "the number of elements in x["+i+"] is < 2");
      int a = x[i][0], b = x[i][1];
      validateVertex(a); validateVertex(b);
      addEdge(a,b);      
    }
    search();
  }
  
  public GraphSB(int v, String s) {
    if (v < 0) throw new IllegalArgumentException("GraphX constructor: v is < 0"); 
    if (s == null) throw new IllegalArgumentException("GraphX constructor: s is null");
    if (!s.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException("GraphX constructor: "
        + "s doesn't contain only integers and whitespace");
    String[] sa = s.split("\\s+");
    int[] ia = new int[sa.length];
    for (int i = 0; i < ia.length; i++) ia[i] = Integer.parseInt(sa[i]);
    V = v;
    int len = ia.length % 2 == 0 ? ia.length : ia.length-1, c = 0;
    Tuple2C<Integer,Integer>[] ta = ofDim(Tuple2C.class,len/2);
    for (int i = 0; i < len-1; i+=2)  ta[c++] = new Tuple2C<Integer,Integer>(ia[i],ia[i+1]);
    if (V == 0) return;
    adj = new Seq<BagX<Integer>>(V);
    for (int i = 0; i < V; i++) adj.add(new BagX<Integer>());
    if (E == 0) return;
    for (Tuple2C<Integer,Integer> t : ta) {
      validateVertex(t._1); validateVertex(t._2);
      addEdge(t._1,t._2);      
    }
    E = 0; for (BagX<Integer> seq : adj) E += seq.size(); E /= 2;
    search();
  }
  
  public GraphSB(String s) {
    // s should contain only edges as an even list of space-separated integers
    if (s == null) throw new IllegalArgumentException("GraphX constructor: s is null");
    if (!s.matches("(\\d+|\\s+)+")) throw new IllegalArgumentException("GraphX constructor: "
        + "s doesn't contain only integers and whitespace");
    String[] sa = s.split("\\s+");
    int[] ia = new int[sa.length];
    for (int i = 0; i < ia.length; i++) ia[i] = Integer.parseInt(sa[i]);
    int len = ia.length % 2 == 0 ? ia.length : ia.length-1, c = 0;
    if (len == 0) throw new IllegalArgumentException("GraphX constructor: insufficient data in s");
    Tuple2C<Integer,Integer>[] ta = ofDim(Tuple2C.class,len/2);
    for (int i = 0; i < len-1; i+=2)  ta[c++] = new Tuple2C<Integer,Integer>(ia[i],ia[i+1]);
    adj = new Seq<BagX<Integer>>(DEFAULTLEN);
    for (int i = 0; i < DEFAULTLEN; i++) adj.add(new BagX<Integer>());
    validate = false;
    V = 0;
    for (Tuple2C<Integer,Integer> t : ta) {
      if (V < t._2) V = t._2+1; if (V < t._1) V = t._1+1;
      if (adj.size() < V) for (int i = V; i < 2*V; i++) adj.add(new BagX<Integer>());
      addEdge(t._1,t._2);      
    }
    if (V <= 0) return;
    if (adj.size() > V) adj.deleteRange(V, adj.size());
    E = 0; for (BagX<Integer> bag : adj) E += bag.size(); E /= 2;
    search();
  }

  public GraphSB(In in) {
    try {
      this.V = in.readInt();
      if (V < 0) throw new IllegalArgumentException(
          "GraphX constructor: number of vertices in a Graph must be nonnegative");
      adj = new Seq<BagX<Integer>>(V);
      for (int v = 0; v < V; v++) adj.add(new BagX<Integer>());
      int E = in.readInt();
      if (E < 0) throw new IllegalArgumentException(
          "GraphX constructor: number of edges in a Graph must be nonnegative");
      int c = 0,v,w,i; String[] sa;
      while (in.hasNextLine()) {
        sa = in.readLine().split("\\s");
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
      E = c;
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("GraphX constructor: invalid input format", e);
    }
    search();
  }
  
  public GraphSB(GraphSB G) {
    this(G.V());
    this.E = G.E();
    for (int v = 0; v < G.V(); v++) {
      // reverse so that adjacency list is in same order as original
      Stack<Integer> reverse = new Stack<Integer>();
      for (int w : G.adj.get(v)) reverse.push(w);
      for (int w : reverse) adj.get(v).add(w);
    }
    search();
  }
  
  public int V() { return V; }

  public int E() { return E; }
    
  public void setE(int e) { E = e; }
  
  public void setV(int v) { V = v; }

  public Seq<BagX<Integer>> adj() { return adj.clone(); }
  
  public void setAdj(Seq<BagX<Integer>> seq) { adj = seq; }
  
  public boolean[] marked() { return marked == null ? null : marked.clone(); }
  
  public void setMarked(boolean[] ba) { marked = ba; }
  
  public int[] id() { return id == null ? null : id.clone(); }
  
  public void setId(int[] ia) { id = ia; }
  
  public int[] size() { return size == null ? null : size.clone(); }
  
  public void setSize(int[] ia) { size = ia; }
  
  public Stack<Integer> cycle() { return cycle; }
  
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

  public boolean connected(int v, int w) { return id[v] == id[w]; }

  public void addEdge(int v, int w) {
    if (validate) { validateVertex(v); validateVertex(w); }
    E++;
    adj.get(v).add(w);
    adj.get(w).add(v);
  }
  
  public void insertEdge(int v, int w) {
    if (v < 0 || w < 0) throw new IllegalArgumentException("insertEdge: a vertex is < 0");
    int max = Math.max(v, w); 
    if (max > V-1) V = max+1;
    int s = adj.size();
    if (V > s) for (int i = 0; i < 2*V-s; i++) adj.add(new BagX<Integer>());
    adj.get(v).add(w);
    adj.get(w).add(v);
    E++;
  }
  
  public void trim() { adj.deleteRange(V, adj.size()); }

  public boolean removeEdge(int v, int w) {
    // remove edge between v and w if it exists and is represented by
    // entries in both adj[v] and adj[w]. return true only if the edge
    // is found and removed else return false.
    validateVertex(v); validateVertex(w);
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
    validateVertex(v);
    validateVertex(w);
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
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(v);
          return true;
        }
      }
    }
    return false;
  }

  public Iterable<Integer> selfLoop() { return cycle; }

  public boolean hasParallelEdges() {
    // from Cycle
    boolean[] m = new boolean[V];
    for (int v = 0; v < V; v++) {
      // check for parallel edges incident to v
      for (int w : adj.get(v)) {
        if (m[w]) {
          cycle = new Stack<Integer>();
          cycle.push(v);
          cycle.push(w);
          cycle.push(v);
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

  public boolean hasCycle() { return hasParallelEdges(); }

  public Iterable<Integer> parallelEdges() { return cycle; }

  public Integer[] parallelEdgesArray() { return cycle.toArray(1); }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " vertices, " + E + " edges " + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (int w : adj.get(v)) {
        s.append(w + " ");
      }
      s.append(NEWLINE);
    }
    return s.toString();
  }
  
  public static void generateAllPermutations(int v, int e, int[][] x) {
    if (v < 0) throw new IllegalArgumentException("generateAllPermutations "
        +"v is < 0");
    if (e < 0) throw new IllegalArgumentException("generateAllPermutations "
        +"e is < 0");
    if (x.length < e) throw new IllegalArgumentException("generateAllPermutations "
        + "the length of x is < e"); 
    for (int i = 0; i < x.length; i++) if (x[i].length < 2) 
      throw new IllegalArgumentException("generateAllPermutations "
          + "the number of elements in x["+i+"] is < 2");
    int[] r = range(0,x.length);  Iterator<int[]> p = permutations(r); 
    GraphSB g = null;
    while(p.hasNext()) {
      int[] a = p.next();
      int[][] b = new int[x.length][];
      for (int i = 0; i < a.length; i++) b[i] = x[a[i]];
//      par(b);
      g = new GraphSB(v,e,b);
      System.out.println(g.adj());
    }  
  }

  public static void main(String[] args) {
//    // 0-1, 1-2, 2-3, 3-0
//    int[][] x = new int[4][];
//    x[0] = new int[]{0,1};
//    x[1] = new int[]{1,2};
//    x[2] = new int[]{2,3};
//    x[3] = new int[]{3,0};
//    generateAllPermutations(4,4,x);

//    In in = new In(args[0]);
//    In in = new In(); // read from stdin
//    GraphS G = new GraphS(in);
    // edges from tinyGex3.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 "
          +"6 2 5 2 5 10 8 1 4 1 1 8 3 10 2 5 9 9 7 7 5 5";
    GraphSB G = new GraphSB(edges);
    StdOut.println(G);
    System.out.print("marked="); par(G.marked);
    System.out.println("avgDegree="+G.avgDegree());
    System.out.println("numberOfSelfLoops="+G.numberOfSelfLoops());
    System.out.println("count="+G.count());
    System.out.print("id"); par(G.id());
    System.out.print("adj="); System.out.println(G.adj());
    System.out.println("hasSelfLoop="+G.hasSelfLoop());
    if (G.hasSelfLoop()) System.out.println("selfLoop="+G.selfLoop());
    System.out.println("hasParallelEdges="+G.hasParallelEdges());
    if (G.hasParallelEdges()) System.out.print("parallelEdges="+G.parallelEdges());
  }

}
