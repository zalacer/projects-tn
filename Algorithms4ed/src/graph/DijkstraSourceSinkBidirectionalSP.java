package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import pq.IndexMinPQ;

// modification of DijkstraSourceSinkSP using bidirectional search 
// for graphs with all nonnegative edge weights
// ex4441

public class DijkstraSourceSinkBidirectionalSP {
  private final double infinity = Double.POSITIVE_INFINITY;
  private double[] distTo1;         // forward
  private double[] distTo2;         // reverse
  private DirectedEdgeX[] edgeTo1;  // forward
  private DirectedEdgeX[] edgeTo2;  // reverse
  private boolean[] visited1;       // forward       
  private boolean[] visited2;       // reverse
  private IndexMinPQ<Double> pq1;   // forward
  private IndexMinPQ<Double> pq2;   // reverse
  private int source;               // starting vertex
  private int sink;                 // ending vertex
  private int V;                    // G1.V() (== G2.V())
  boolean done = false;             // stop processing pq1 and pq2 iff true 
  boolean forward = true;           // use pq1 if it's not empty
  private boolean overlap = false;  // true iff forward and backward paths share a vertex
  double m = infinity;              // weight of current best path
  private int relaxations1 = 0;     // counter for forward relaxations
  private int relaxations2 = 0;     // counter for reverse relaxations
  
  public DijkstraSourceSinkBidirectionalSP(EdgeWeightedDigraphX g, EdgeWeightedDigraphX r, 
      int s, int t) {
    if (g == null) throw new IllegalArgumentException(
        "DijkstraSPX: EdgeWeightedDigraphX is null");
    if (r == null) throw new IllegalArgumentException(
        "DijkstraSPX: EdgeWeightedDigraphX is null");
    // r should be g with all its edges reversed
    // assert r.equals(g.reverse());
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    V = g.V();
    validateVertex(s);
    source = s;
    validateVertex(t);
    sink = t;
    visited1 = new boolean[V];
    visited2 = new boolean[V];
    distTo1 = fillDouble(V,()->infinity);
    distTo2 = fillDouble(V,()->infinity);
    edgeTo1 = new DirectedEdgeX[V];  
    edgeTo2 = new DirectedEdgeX[V];    
    distTo1[source] = 0.0; 
    distTo2[sink] = 0.0;
    visited1[source] = true;
    visited2[sink] = true;
    pq1 = new IndexMinPQ<Double>(V);
    pq2 = new IndexMinPQ<Double>(V);
    pq1.insert(source, distTo1[source]);
    pq2.insert(sink, distTo2[sink]);
    int v;
    while (!(pq1.isEmpty() || pq2.isEmpty() || done)) {
      if (forward) {
        v = pq1.delMin();
        if (v == sink) break;
        for (DirectedEdgeX e : g.adj(v)) relax1(e);
        forward = false;
      } else {
        v = pq2.delMin();
        if (v == source) break;
        for (DirectedEdgeX e : r.adj(v)) relax2(e);
        forward = true;
      }
    }
  }

  private void relax1(DirectedEdgeX e) {
    relaxations1++;
    int from = e.from(), to = e.to();
    if (!overlap && distTo2[from] < infinity) overlap = true;
    double p = distTo1[from] + e.weight();
    if (distTo1[to] > p) {
      distTo1[to] = p;
      edgeTo1[to] = e;
      if (pq1.contains(to)) pq1.decreaseKey(to, distTo1[to]);
      else pq1.insert(to, distTo1[to ]);
    }
    // test stopping condition
    visited1[to] = true;
    if (visited2[to]) {
      if (overlap && p + distTo2[to] < m) m = pathWeight();
      if (!pq1.isEmpty() && !pq2.isEmpty() && pq1.minKey() + pq2.minKey() > m) 
        done = true;
    }
  }
  
  private void relax2(DirectedEdgeX e) {
    relaxations2++;
    int from = e.from(), to = e.to();
    if (!overlap && distTo1[from] < infinity) overlap = true;
    double p = distTo2[from] + e.weight();
    if (distTo2[to] > p) {
      distTo2[to] = p;
      edgeTo2[to] = e;
      if (pq2.contains(to)) pq2.decreaseKey(to, distTo2[to]);
      else pq2.insert(to, distTo2[to ]);
    }
    // test stopping condition
    visited2[to] = true;
    if (visited1[to]) {
      if (overlap && distTo2[from] + e.weight() + distTo1[to] < m) m = pathWeight();
      if (!pq1.isEmpty() && !pq2.isEmpty() && pq1.minKey() + pq2.minKey() > m) 
        done = true; 
    }
  }
  
  public Seq<DirectedEdgeX> path() {
    // construct and return the shortest path from source to sink
    int min = min();
    Seq<DirectedEdgeX> a = seqTo1(min);
    Seq<DirectedEdgeX> b = seqTo2(min); 
    if (!b.isEmpty()) a.append(b.map(x->x.reverse()).reverse());
    return a;
  }
  
  public double pathWeight() {
    // calculate and return the weight of path()
    // doing it this way instead of weight(seqTo1(min))+weight(seqTo2(min))
    // for faster runtime
    double w = 0;
    int min = min();
    for (DirectedEdgeX e = edgeTo1[min]; e != null; e = edgeTo1[e.from()]) w += e.w();
    for (DirectedEdgeX e = edgeTo2[min]; e != null; e = edgeTo2[e.from()]) w += e.w();
    return w;
  }
  
  public Iterable<DirectedEdgeX> pathTo1(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    validateVertex(v);
    if (!hasPathTo1(v)) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo1[v]; e != null; e = edgeTo1[e.from()])
      path.push(e);
    return path;
  }
  
  public int min() {
    // return vertex x with min distTo1[x] + distTo2[x]
    double min = infinity, sum;
    int x = -1;
    for (int i = 1; i < V; i++) {
      sum = distTo1[i] + distTo2[i];
      if (sum < min) { min = sum; x = i; }
    }
    return x;
  }
  
  public int relaxations() { return relaxations1 + relaxations2; }

  public boolean hasPathTo1(int v) {
    // return true if exists path from source to v else false
    validateVertex(v);
    return distTo1[v] < infinity;
  }
  
  public boolean hasPathTo2(int v) {
    // return true if exists path from sink to v else false
    validateVertex(v);
    return distTo2[v] < infinity;
  }
  
  public Seq<DirectedEdgeX> seqTo1(int v) {
    // return a shortest path from source to v if possible else returns null
    // if v is valid else throws exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo1(v)) return seq;
    Stack<DirectedEdgeX> stack = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo1[v]; e != null; e = edgeTo1[e.from()]) stack.push(e);
    return new Seq<>(stack);
  }
  
  public Seq<DirectedEdgeX> seqTo2(int v) {
    // return a shortest path from sink to v if possible else returns null
    // if v is valid else throws exception
    Seq<DirectedEdgeX> seq = new Seq<>();
    validateVertex(v);
    if (!hasPathTo2(v)) return seq;
    Stack<DirectedEdgeX> stack = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo2[v]; e != null; e = edgeTo2[e.from()]) stack.push(e);
    return new Seq<>(stack);
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
  
  public static void main(String[] args) {
    
    In in = new In("mediumEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    EdgeWeightedDigraphX R = G.reverse(); 
    int source = 4;
    int sink = 40;
    DijkstraSourceSinkBidirectionalSP sp = 
        new DijkstraSourceSinkBidirectionalSP(G, R, source, sink);
    int min = sp.min();
    System.out.println("min="+min);
    Seq<DirectedEdgeX> path = sp.path();
    System.out.println("path="+path);
    double pthWgt = sp.weight(path);
    System.out.println("pthWgt="+ pthWgt);
    double pathWeight = sp.pathWeight();
    System.out.println("pathWeight="+pathWeight);
//    double pathWeight2 = sp.pathWeight2();
//    System.out.println("pathWeight2="+pathWeight2);
    System.out.print("visited1="); par(sp.visited1);
    System.out.print("visited2="); par(sp.visited2);
    System.out.print("edgeTo1="); par(sp.edgeTo1);
    System.out.print("edgeTo2="); par(sp.edgeTo2);

  }

}
