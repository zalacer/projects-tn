package graph;

import static java.lang.Math.*;
import static v.ArrayUtils.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import ds.BagX;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import exceptions.InvalidDataException;
import st.HashSET;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/EdgeWeightedGraph.java
// build parallel GraphX for testing

public class EdgeWeightedGraphX {
  private static final String NEWLINE = System.getProperty("line.separator");
  private final int V;
  private int E;
  private BagX<EdgeX>[] adj;
  private transient boolean validate = true;
  private transient GraphX g;

  public EdgeWeightedGraphX(int V) {
    if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
    this.V = V;
    adj = fill(V,()->new BagX<EdgeX>());
    g = new GraphX(V);
  }

  public EdgeWeightedGraphX(int V, int E) {
    this(V);
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    g = new GraphX(V);
    for (int i = 0; i < E; i++) {
      int u = StdRandom.uniform(V);
      int v = StdRandom.uniform(V);
      if (u == v || hasAnyEdge(u,v)) continue;
      double w = Math.round(100 * StdRandom.uniform()) / 100.0;
      EdgeX e = new EdgeX(u,v,w);
      addEdge(e);
      g.search();
    }
  }
  
  public EdgeWeightedGraphX(int V, String edges) {
    this(V);
    if (edges == null) throw new IllegalArgumentException("String edges == null");
    g = new GraphX(V);
    validate = false;
    String[] edgs = edges.split("\\s+"); int u,v; double w;
    int len = edgs.length %3 == 0 ? edgs.length : (edgs.length/3)*3;
    for (int i = 0; i < len-2; i+=3) {
      u = Integer.parseInt(edgs[i]);
      v = Integer.parseInt(edgs[i+1]);
      w = Double.parseDouble(edgs[i+2]);
      validateVertices(u,v);
      EdgeX e = new EdgeX(u,v,w);
      addEdge(e);
    }
    validate = true;
    g.search();
  }
  
  public EdgeWeightedGraphX(int v, Iterable<EdgeX> edges) {
    if (edges == null) throw new IllegalArgumentException("String edges == null");
    this.V = v;
    adj = fill(V,()->new BagX<EdgeX>());
    g = new GraphX(V);
    validate = false;
    for (EdgeX e : edges) {
      validateVertices(e.u(),e.v());
      addEdge(e);
    }
    validate = true;
    g.search();
  }
  
  public EdgeWeightedGraphX(Iterable<EdgeX> edges) {
    if (edges == null) throw new IllegalArgumentException("Iterable<EdgeX> edges == null");
    HashSET<Integer> set = new HashSET<>();
    for (EdgeX e : edges) { set.add(e.u()); set.add(e.v()); }
    if (set.size() == 0) {
      V = 0;
    } else {
      V = set.size();
      Integer[] vtcs = set.toArray();
      if (min(vtcs) < 0) throw new IllegalArgumentException(
          "EdgeWeightedGraphX: Iterable<EdgeX> contains a vertex < 0");
      if (max(vtcs) > V) throw new IllegalArgumentException(
          "EdgeWeightedGraphX: Iterable<EdgeX> contains a vertex > "+V);
    }
    adj = fill(V,()->new BagX<EdgeX>());
    g = new GraphX(V);
    validate = false;
    for (EdgeX e : edges) {
      validateVertices(e.u(),e.v());
      addEdge(e);
    }
    validate = true;
    g.search();
  }
  
  public EdgeWeightedGraphX(In in) {
    this(in.readInt());
    g = new GraphX(V);
    int E = in.readInt();
    if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    validate = false;
    for (int i = 0; i < E; i++) {
      int u = in.readInt();
      int v = in.readInt();
      validateVertices(u,v);
      double w = in.readDouble();
      EdgeX e = new EdgeX(u,v,w);
      addEdge(e);
    }
    validate = true;
    g.search();
  }

  public EdgeWeightedGraphX(EdgeWeightedGraphX G) {
    this(G.V());
    this.E = G.E();
    for (int v = 0; v < G.V(); v++) {
      Stack<EdgeX> reverse = new Stack<EdgeX>();
      for (EdgeX e : G.adj[v]) reverse.push(e);
      for (EdgeX e : reverse) adj[v].add(e);
    }
    g = new GraphX(V);
    for (EdgeX e : edges()) g.addEdge(e.from(),e.to());
    g.search();
  }
  
  public boolean isConnected() { g.search(); return g.count() == 1; }
  
  public int[] id() { return g.id(); }
  
  public GraphX graph() { return g; }
  
  public int V() { return V; }

  public int E() { return E; }
  
  public BagX<EdgeX>[] adj() { return adj; }
  
  public boolean isSimpleTrail() {
    if (!isConnected()) return false;
    if (E != V-1) return false;
    int d1 = 0, d2 = 0, d = -1;
    for (int i = 0; i < V; i++) {
      d = degree(i);
      if (d == 1) { d1++; continue; }
      if (d == 2) { d2++; continue; }
      return false;
    }
    if (d1 != 2) return false;
    if (d2 != V-d1) return false;
    if (hasCycle()) return false; // necessary?
    return true; 
  }
  
  public boolean hasCycle() {
    EdgeWeightedCycleX C = new EdgeWeightedCycleX(this);
    return C.hasCycle();
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }
  
  private void validateVertices(int u, int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
    if (u < 0 || u >= V) throw new IllegalArgumentException("vertex "+u+" is out of bounds");
  }

  public void addEdge(EdgeX e) {
    int u = e.u(), v = e.v();
    if (validate) validateVertices(u,v);
    adj[u].add(e);
    adj[v].add(e);
    E++;
    g.addEdge(u,v);
  }
  
  public void addEdge(int u, int v, double w) {
    EdgeX e = new EdgeX(u,v,w);
    if (validate) validateVertices(u,v);
    adj[u].add(e);
    adj[v].add(e);
    E++;
    g.addEdge(u,v);
  }
  
  public boolean hasEdge(int u, int v) {
    // if there's an edge "from" u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    for (EdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v) return true;
    return false;  
  }
  
  public boolean hasAnyEdge(int u, int v) {
    // if there's any direct edge between u to v return true else return false
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    for (EdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v || e.u() == v && e.v() == u) return true;
    return false;  
  }
  
  public boolean hasEdge(EdgeX x) {
    if (x  == null) return false;
    int u = x.u(), v = x.v();
    if (u < 0 || u > V-1 || v < 0 || v > V-1) return false;
    // if there's an edge between u and v return true else return false
    for (EdgeX e : adj[u]) 
      if (e.u() == u && e.v() == v || e.v() == u && e.u() == v) return true;
    return false;  
  }
  
  public EdgeX removeEdge(int u, int v) {
    // remove an edge from this and return it if possible else return null
    EdgeX x = null; boolean udone = false, vdone = false;
    for (EdgeX e : adj[u]) {
      if (e.u() == u && e.v() == v || e.u() == v && e.v() == u) {
        x = e;
        udone = adj[u].remove(x);
        if (!udone) {
          System.out.println("couldn't remove edge "+x+" from adj["+u+"]");
          return null;
        } else {
          vdone = adj[v].remove(x);
          if (!vdone) {
            adj[u].add(x);
            System.out.println(
                "edge "+x+" found in adj["+u+"] but not in adj["+v+"]");
            return null;
          }
        }
      }
    }
    if (udone && vdone || udone && !vdone || vdone && !udone) {
      E--; g.removeEdge(u,v);
      if (udone && !vdone) {
        System.err.println("edge removed from adj["+u+"] but not from adj["+v+"]");
        System.err.println("E decremented anyway");
      }
      if (vdone && !udone) {
        System.err.println("edge removed from adj["+v+"] but not from adj["+u+"]");
        System.err.println("E decremented anyway");
      }
      return x; 
    }
    return null;
  }
  
  public void removeEdge(EdgeX x) { 
    int u = x.u(), v = x.v();
    boolean udone = adj[u].remove(x);
    boolean vdone = adj[v].remove(x);
    if (udone && vdone || udone && !vdone || vdone && !udone) {
      E--; g.removeEdge(u,v);
      if (udone && !vdone) {
        System.err.println("edge removed from adj["+u+"] but not from adj["+v+"]");
        System.err.println("E decremented anyway");
      }
      if (vdone && !udone) {
        System.err.println("edge removed from adj["+v+"] but not from adj["+u+"]");
        System.err.println("E decremented anyway");
      }
    }
  }

  public Iterable<EdgeX> adj(int v) { validateVertex(v); return adj[v]; }

  public int degree(int v) { validateVertex(v); return adj[v].size();}

  public Iterable<EdgeX> edges() {
    BagX<EdgeX> list = new BagX<>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (EdgeX e : adj(v)) {
        if (e.other(v) > v) {
          list.add(e);
        }
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) list.add(e);
          selfLoops++;
        }
      }
    }
    return list;
  }
  
  public Seq<EdgeX> edgeSeq() {
    Seq<EdgeX> s = new Seq<>();
    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (EdgeX e : adj(v)) {
        if (e.other(v) > v) s.add(e);
        // only add one copy of each self loop (self loops will be consecutive)
        else if (e.other(v) == v) {
          if (selfLoops % 2 == 0) s.add(e);
          selfLoops++;
        }
      }
    }
    return s;
  }
  
  public EdgeWeightedGraphX mst() { return (new PrimMSTBX(this)).mst(); }
  
  public static EdgeWeightedGraphX fixMstAfterEdgeRemoval(EdgeWeightedGraphX ewg,
      EdgeWeightedGraphX mst, EdgeX edgeRemoved) {
    //ex4314
    if (ewg == null) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX ewg == null");
    if (ewg.V() == 0) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX ewg has no Vertices");
    if (ewg.E() == 0) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX ewg has no Edges");
    if (mst == null) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX mst == null");
    if (mst.V() == 0) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX mst.V() == 0");
    if (mst.E() == 0) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX mst.E() == 0");
    if (edgeRemoved == null) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX edgeRemoved == null");
    if (edgeRemoved.v() == edgeRemoved.w()) throw new IllegalArgumentException(
        "rebuildMST: EdgeWeightedGraphX edgeRemoved is a self-edge");
    int u = edgeRemoved.u(), v = edgeRemoved.v();
    DepthFirstSearchX d = new DepthFirstSearchX(mst.graph(),u);
    // accumulate the unique vertices in the u side in setu
    boolean[] marked = d.marked(); HashSET<Integer> setu = new HashSET<>();
    for (int i = 0; i < marked.length; i++) if (marked[i] ==  true) setu.add(i);
    // accumulate the unique vertices in the v side in setv
    d = new DepthFirstSearchX(mst.graph(),v);
    marked = d.marked(); HashSET<Integer> setv = new HashSET<>();
    for (int i = 0; i < marked.length; i++) if (marked[i] ==  true) setv.add(i);
    EdgeX lce = null; //lightest edge connecting mst TBD
    for (EdgeX e : ewg.edges()) {
      u = e.u(); v = e.v();
      if (setu.contains(u) && setv.contains(v)
          || setu.contains(v) && setv.contains(u)) {
        if (lce == null) lce = e;
        else if (e.weight() < lce.weight()) lce = e;
      }
    }
    if (lce != null) mst.addEdge(lce);
    mst.g.search(); //since GraphX g is used to determine connectivity of mst
    return mst;
  }
  
  public static EdgeWeightedGraphX fixMstAfterEdgeAddition(EdgeWeightedGraphX ewg,
      EdgeX edgeAdded) {
    // ex4315
    EdgeWeightedGraphX mst = ewg.mst();
    mst.addEdge(edgeAdded);
    Stack<Integer> cycleStack = mst.g.cycle();
    if (cycleStack == null || cycleStack.size() < 2) {
      System.out.println("fixMstAfterEdgeAddition: no cycle found in MST of the "
          + "graph after adding EdgeX("+edgeAdded+") to it");
      System.out.println("removing the added edge from the MST and returning it");
      return ewg.mst(); 
    }
    Integer[] cycle = cycleStack.toArray(1);
    EdgeX max = null; EdgeX e;
    for (int i = 0; i < cycle.length-1; i++) {
      e = mst.findEdge(cycle[i], cycle[i+1], ewg.adj(cycle[i]));
      if (e != null) {
        if (max == null) max = e;
        else if (e.w() > max.w()) max = e;
      }
    }
    if (max != null) mst.removeEdge(max.u(),max.v());
    mst.g.search(); //since GraphX g is used to determine connectivity of mst
    return mst;
  }
  
  public static Double findMaxWeight4EdgeToBeAddedToTheMST(EdgeWeightedGraphX g, EdgeX x) {
    //ex4316
    EdgeWeightedGraphX mst = g.mst();
    mst.addEdge(x);
  
    Stack<Integer> cycleStack = mst.g.cycle();
    if (cycleStack == null || cycleStack.size() < 2) {
      System.out.println("findMaxWeight4EdgeToBeAddedToTheMST: no cycle found in mst "
          + "after adding Edge("+x+") to it: returning null");
      return null;
    }
    Integer[] cycle = cycleStack.toArray(1);
    EdgeX max = null; EdgeX e;
    for (int i = 0; i < cycle.length-1; i++) {
      e = mst.findEdge(cycle[i], cycle[i+1], g.adj(cycle[i]));
      if (e != null) {
        if (max == null) max = e;
        else if (e.w() > max.w()) max = e;
      }
    }
    return max == null ? null : Math.nextDown(max.w());
  }
  
  public Double maxWeightInCycle() {
    Stack<Integer> cycleStack = g.cycle();
    if (cycleStack == null || cycleStack.size() < 2) return null;
    Integer[] cycle = cycleStack.toArray(1);
    EdgeX max = null; EdgeX e;
    for (int i = 0; i < cycle.length-1; i++) {
      e = findEdge(cycle[i], cycle[i+1], adj(cycle[i]));
      if (e != null) {
        if (max == null) max = e;
        else if (e.w() > max.w()) max = e;
      }
    }
    return max == null ? null : max.w();
  }
  
  public EdgeX findEdge(int u, int v, Iterable<EdgeX> itbl) {
    // return the first EdgeX with u and v vertices in itbl else return null
    for (EdgeX e : itbl) 
      if (e.u() == u && e.v() == v || e.u() == v && e.v() == u) return e;
    return null;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (EdgeX e : adj[v]) {
        s.append(e + "  ");
      }
      s.append(NEWLINE);
    }
    s.append("isConnected = "+isConnected()+NEWLINE);
    s.append("id = "+arrayToString(id(),900,1,1)+NEWLINE);
    return s.toString();
  }
  
  public String toString2() {
    StringBuilder s = new StringBuilder();
    s.append(V + " " + E + NEWLINE);
    for (int v = 0; v < V; v++) {
      s.append(v + ": ");
      for (EdgeX e : adj[v]) {
        s.append(e.toString2() + "  ");
      }
      s.append(NEWLINE);
    }
    s.append("isConnected = "+isConnected()+NEWLINE);
    s.append("id = "+arrayToString(id(),900,1,1)+NEWLINE);
    return s.toString();
  }
  
  public static Comparator<EdgeX> edgCmp = (e1,e2) -> {
    int u1 = e1.u(), v1 = e1.v(), u2 = e2.u(), v2 = e2.v();
    double w1 = e1.w(), w2 = e2.w();
    if (w1 != w2) Double.compare(w1,w2);
    if (w1 != w2) return Double.compare(w1,w2);
    if (min(u1,v1) < min(u2,v2)) return -1;
    if (min(u1,v1) > min(u2,v2)) return 1;
    if (max(u1,v1) < max(u2,v2)) return -1;
    if (max(u1,v1) > max(u2,v2)) return 1;
    return 0;
  };
  
  public EdgeX[] semiSortedAdj() {
    Seq<EdgeX> seq = new Seq<>();
    for (int i = 0; i < adj.length; i++) {
      Seq<EdgeX> seq2 = new Seq<>();
      for (EdgeX e : adj[i]) seq2.add(e);
      seq.addAll(seq2.sortWithComparator(edgCmp));
    }
    return seq.to();
  }
  
  public double minWeight() {
    double min = Double.POSITIVE_INFINITY;
    for (EdgeX e : edges()) if (e.w() < min) min = e.w();
    return min;
  }
  
  public double maxWeight() {
    double max = Double.POSITIVE_INFINITY;
    for (EdgeX e : edges()) if (e.w() > max) max = e.w();
    return max;
  }

  public Seq<EdgeX> unusedEdges() {
    Seq<EdgeX> s = new Seq<>(); 
    double w = minWeight();
    double min = w/2;
    if (!(min > 0 && min < w)) throw new InvalidDataException(
        "unusedEdges: a weight > 0 && < minWeight can't be determined");
    Iterator<int[]> it = combinations(range(0,V),2);
    while (it.hasNext()) {
      int[] a = it.next();
      if (!hasAnyEdge(a[0],a[1])) s.add(new EdgeX(a[0],a[1],min));
    }
    return s;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + E;
    result = prime * result + V;
    result = prime * result + Arrays.deepHashCode(semiSortedAdj());
    result = prime * result + ((g == null) ? 0 : g.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null)  return false;
    if (getClass() != obj.getClass())  return false;
    EdgeWeightedGraphX other = (EdgeWeightedGraphX) obj;
    if (E != other.E)  return false;
    if (V != other.V)  return false;
    if (!Arrays.deepEquals(semiSortedAdj(), other.semiSortedAdj()))  return false;
    if (g == null) {
      if (other.g != null) return false;
    } else if (!g.equals(other.g)) return false;
    return true;
  }
  
  public static void testFixMstAfterEdgeAddition(EdgeWeightedGraphX g) {
    // ex4315
    if (g == null || g.V == 0) { System.out.println("no unused edges to test"); return; }
    Seq<EdgeX> unusedEdges = g.unusedEdges();
    if (unusedEdges.size() == 0) { System.out.println("no unused edges to test"); return; }
    EdgeWeightedGraphX mst, mst2, mst3; int failed = 0;
    for (EdgeX e : unusedEdges) {
      mst = g.mst();
      mst.addEdge(e);
      mst2 = fixMstAfterEdgeAddition(g,e);
      g.addEdge(e);
      mst3 = g.mst();
      if (!mst2.equals(mst3)) {
        System.out.println("fixMstAfterEdgeAddition() failed for EdgeX "+e);
        failed++;
      }
      g.removeEdge(e);    
    }
    if (failed == 0) {
      System.out.println("fixMstAfterEdgeAddition() worked after inserting each of "
          + unusedEdges.size()+" unused edges");
    } else { System.out.println("fixMstAfterEdgeAddition() worked "
        + (unusedEdges.size()-failed)+" out of "+unusedEdges.size()+" times after "
        + "inserting each of "+ unusedEdges.size()+" unused edges");    
    }
    
    
  }

  public static void main(String[] args) {
//    int V = 5;
//    int E = 12;
//    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V, E);
//    System.out.println(G);
//    for (EdgeX e : G.edgeSeq()) System.out.println(e);
    
    In in = new In(args[0]);
    EdgeWeightedGraphX g = new EdgeWeightedGraphX(in);
    System.out.println("\ng:\n"+g);
//    
//    EdgeX addedEdge = new EdgeX(0,1,0.1);
//    System.out.println(findMaxWeight4EdgeToBeAddedToTheMST(g,addedEdge));
    
//    testFixMstAfterEdgeAddition(g);
    
//    EdgeWeightedGraphX mst = g.mst();
//    System.out.println("mst:\n"+mst); 
//    
//    EdgeX addedEdge = new EdgeX(0,1,0.1);
//    g.addEdge(addedEdge);
//    g.g.search();
//    System.out.println("added Edge "+addedEdge+" to g");
//    
//
//    mst.addEdge(addedEdge); mst.g.search();
//    System.out.println("\nadded Edge "+addedEdge+" to mst");
//    
//    System.out.println("\nmst with added Edge:\n"+mst); 
//    
//    System.out.println("mst.g.cycle() = "+mst.g.cycle()+"\n");    
//    
//    EdgeWeightedGraphX fixedMst = fixMstAfterEdgeAddition(g, mst, addedEdge);
//    System.out.println("fixed mst:\n"+fixedMst); 
//    
//    EdgeWeightedGraphX newMst = g.mst();
//    System.out.println("newMst:\n"+mst);
//    
//    assert fixedMst.equals(newMst);
// 
//    g.unusedEdges();

//    EdgeWeightedGraphX mst2 = g.mst();
//    System.out.println("mst2:\n"+mst2); 
    

  }

}

