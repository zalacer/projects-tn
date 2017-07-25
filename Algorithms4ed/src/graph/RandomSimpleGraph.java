package graph;

import static v.ArrayUtils.*;

import java.security.SecureRandom;
import java.util.Iterator;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import v.Tuple2;

// create random undirected simple graphs for ex4140

public class RandomSimpleGraph {
  private static final String NEWLINE = System.getProperty("line.separator");
  private int V;
  private int E;
  private BagX<Integer>[] adj;
  private boolean[] marked;  
  private int[] id; // component ids
  private int[] size;  // size[id] = number of vertices in given component
  private int count = 0; // number of connected components
  private Stack<Integer> cycle = null; // for hasSelfLoop() && hasParallelEdges()
  SecureRandom r = new SecureRandom(); 
  
  public RandomSimpleGraph(int V, int E) {
    // create a random simple graph for V vertices and E edges
    if (V < 0) throw new IllegalArgumentException("RandomSimpleGraph: V < 0");
    if (E < 0) throw new IllegalArgumentException("RandomSimpleGraph: E < 0");
    if (E > V*(V-1)/2 ) throw new IllegalArgumentException("RandomSimpleGraph: E > V*(V-1)/2");
    r.setSeed(System.currentTimeMillis());
    int u = 1+V*(V-1)/2;
    for (int i = 0; i < 137793; i++) r.nextInt(u);
    int stop  = r.nextInt(u);
    GraphX G = null;
    if (E == 0) G = new GraphX(0);
    else G = generateRSG(V, E, stop);
    this.V = G.V();
    this.E = G.E();
    adj = G.adj();
    marked = G.marked();
    id = G.id();
    size = G.size();
    count = G.count();
    cycle = G.cycle();
  }
  
  public GraphX generateRSG(int V, int E, int stop) {
    // return the stopth simple graph for V and E
    if (V < 0) throw new IllegalArgumentException("generateSRGs: V < 0");
    if (E < 0) throw new IllegalArgumentException("generateSRGs: E < 0");
    if (E > V*(V-1)/2 ) throw new IllegalArgumentException("generateSRGs: E > V*(V-1)/2");
    if (stop < 0 || stop > V*(V-1)/2) throw new IllegalArgumentException(
        "generateSRGs: stop is < 0 or > V*(V-1)/2");
    Seq<Tuple2<Integer,Integer>> s = new Seq<>(); // Seq of all possible edges for V
    for (int v = 0; v < V; v++)
      for (int w = v+1; w < V; w++) 
        s.add(new Tuple2<Integer,Integer>(v,w));
    // generate all all combinations of the elements of s taken E at a time
    int[] a = range(0,s.size());
    Iterator<int[]> it = combinations(a,E); // iterator over elements of a taken E at a time
    Tuple2<Integer,Integer>[] ta = s.to(); shuffle(ta,r);
    int d = -1; GraphX g = null;
    while (it.hasNext()) {
      int[] c = it.next();
      if (++d != stop) continue;
      Tuple2<Integer,Integer>[] tc = ofDim(Tuple2.class,E);
      for (int i = 0; i < E; i++) tc[i] = ta[c[i]]; // mask of c in ta
      int[] e = new int[2*E]; int j = 0; // convert mask from array of Tuple2s to ints
      for (int i = 0; i < E; i++) { e[j++] = tc[i]._1; e[j++] = tc[i]._2; }
      g = new GraphX(V,E,e); // create graph from V, E and int array e
      break;
    }  
    return g;
  }
  
  public Seq<GraphX> generateRSG(int V, int E) {
    // return a Seq of all simple graphs with V vertices and E edges
    if (V < 0) throw new IllegalArgumentException("generateSRGs: V < 0");
    if (E < 0) throw new IllegalArgumentException("generateSRGs: E < 0");
    if (E > V*(V-1)/2 ) throw new IllegalArgumentException("generateSRGs: E > V*(V-1)/2");
    Seq<Tuple2<Integer,Integer>> s = new Seq<>(); // Seq of all possible edges for V
    for (int v = 0; v < V; v++)
      for (int w = v+1; w < V; w++) 
        s.add(new Tuple2<Integer,Integer>(v,w));
    // generate all all combinations of the elements of s taken E at a time
    int[] a = range(0,s.size());
    Iterator<int[]> it = combinations(a,E); // iterator over elements of a taken E at a time
    Seq<GraphX> seq = new Seq<>(); // output sequence of GraphXs
    Tuple2<Integer,Integer>[] ta = s.to(); 
    while (it.hasNext()) {
      int[] c = it.next();
      Tuple2<Integer,Integer>[] tc = ofDim(Tuple2.class,E);
      for (int i = 0; i < E; i++) tc[i] = ta[c[i]]; // mask of c in ta
      int[] e = new int[2*E]; int j = 0; // convert mask from array of Tuple2s to ints
      for (int i = 0; i < E; i++) { e[j++] = tc[i]._1; e[j++] = tc[i]._2; }
      seq.add(new GraphX(V,E,e)); // create graph from V, E and int array e    
    }  
    return seq;
  }

  public int V() { return V; }

  public int E() { return E; }
  
  public void setE(int e) { E = e; }
  
  public void setV(int v) { V = v; }

  public BagX<Integer>[] adj() { return adj.clone(); }
  
  public void setAdj(BagX<Integer>[] ba) { adj = ba; }
  
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
      for (int j : adj[i]) a[i][j] = 1;
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
    for (int w : adj[v]) if (!marked[w]) dfs(w);
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
    validateVertex(v);
    validateVertex(w);
    E++;
    adj[v].add(w);
    adj[w].add(v);
  }

  public boolean removeEdge(int v, int w) {
    // remove edge between v and w if it exists and is represented by
    // entries in both adj[v] and adj[w]. return true only if the edge
    // is found and removed else return false.
    validateVertex(v); validateVertex(w);
    BagX<Integer> bv = adj[v]; boolean fw = false; int iw = -1;  
    BagX<Integer> bw = adj[w]; boolean fv = false; int iv = -1;
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
    return adj[v];
  }

  public int degree(int v) {
    validateVertex(v);
    return adj[v].size();
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
    for (int x : adj[v]) if (x == w) return true;
    return false;
  }

  public int numberOfSelfLoops() {
    // from GraphClient
    int count = 0;
    for (int v = 0; v < V; v++)
      for (int w : adj(v))
        if (v == w) count++;
    return count/2; // each edge counted twice
  }

  public boolean hasSelfLoop() {
    // from Cycle
    for (int v = 0; v < V; v++) {
      for (int w : adj[v]) {
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
      for (int w : adj[v]) {
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
      for (int w : adj[v]) {
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
      for (int w : adj[v]) {
        s.append(w + " ");
      }     
      s.append(NEWLINE);
    }
//    if (hasSelfLoop()) s.append("has self-loop");
//    else s.append("doesn't have self-loop");
//    s.append(NEWLINE);
//    if (hasParallelEdges()) s.append("has parallel edges");
//    else s.append("doesn't have parallel edges");
//    s.append(NEWLINE);
    return s.toString();
  }

  public static void main(String[] args) {
    
    RandomSimpleGraph g = new RandomSimpleGraph(5,7);
    System.out.println(g);

  }

}
