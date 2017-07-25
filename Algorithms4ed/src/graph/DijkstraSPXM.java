package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;

import analysis.Draw;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQ;
import v.Tuple2;

// from DijkstraSPX for ex4.4.7 to find all shortest paths

@SuppressWarnings("unused")
public class DijkstraSPXM {
  private Seq<double[]> distTo;          // distTo[v] = distance  of shortest s->v path
  private Seq<DirectedEdgeX[]> edgeTo;    // edgeTo[v] = last edge on shortest s->v path
  private Seq<IndexMinPQ<Double>> pq;    // priority queue of vertices
  private EdgeWeightedDigraphX G;
  private int source;
  private int V;

  public DijkstraSPXM(EdgeWeightedDigraphX g, int s) {
    if (g == null) throw new IllegalArgumentException("DijkstraSPX: EdgeWeightedDigraphX is null");
    for (DirectedEdgeX e : g.edges())
      if (e.weight() < 0) throw new IllegalArgumentException(
          "DijkstraSPX: edge " + e + " has negative weight");
    G = g;
    V = G.V();
    validateVertex(s);
    source = s;
    distTo = new Seq<>();
    distTo.add(fillDouble(V,()->Double.POSITIVE_INFINITY));
    edgeTo = new Seq<>();
    edgeTo.add(new DirectedEdgeX[V]);    
    distTo.get(0)[s] = 0.0;
    pq = new Seq<>();
    pq.add(new IndexMinPQ<Double>(V));
    pq.get(0).insert(s, distTo.get(0)[s]);
    // relax vertices in order of distance from s for each IndexMinPQ<Double> in pq
    int z = 0;
    while (true) {
      if (pq.size()-1 < z) break;
      for (int i = z; i < pq.size(); i++) {
        while (!pq.get(i).isEmpty()) {
          relax(G, pq.get(i).delMin(),i);
        }
        z++;
      }
    }
    // check optimality conditions
    assert check();
  }
  
  private void relax(EdgeWeightedDigraphX G, int u, int z) {
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.to();
      if (distTo.get(z)[v] > distTo.get(z)[u] + e.weight()) {
        distTo.get(z)[v] = distTo.get(z)[u] + e.weight();
        edgeTo.get(z)[v] = e;
        if (pq.get(z).contains(v)) pq.get(z).changeKey(v, distTo.get(z)[v]);
        else pq.get(z).insert(v, distTo.get(z)[v]);
      }
      else if (distTo.get(z)[v] == distTo.get(z)[u] + e.weight()  && !e.equals(edgeTo.get(z)[v])) {
        pq.add(pq.get(z).clone());
        distTo.add(distTo.get(z).clone());
        edgeTo.add(edgeTo.get(z).clone());
        int y = pq.size()-1;
        edgeTo.get(y)[v] = e; // the main modification for pq.get(y)
        int b = e.from();
        if (b == source) System.out.println("b=source");
        if (b != source) { // backtrack to first common vertex from previous pq
          if (pq.get(y).contains(e.from())) pq.get(y).changeKey(b, distTo.get(y)[e.from()]);
          else pq.get(y).insert(b, distTo.get(y)[e.from()]);
        }
        if (pq.get(y).contains(v)) pq.get(y).changeKey(v, distTo.get(y)[v]);
        else pq.get(y).insert(v, distTo.get(y)[v]);
       }
    }     
  }

  public Seq<double[]> distTo() { return distTo; } ;
  
  public double distTo(int v, int z) {
    // return length of shortest path from source to v for index z
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    validateVertex(v);
    return distTo.get(z)[v];
  }
  
  public Seq<Double> distTo(int v) {
    // return a Seq of the length of all shortest paths from source to v
    Seq<Double> s = new Seq<>();
    for (double[] d : distTo) s.add(d[v]);
    return s;
  }
  
  public Seq<DirectedEdgeX[]> edgeTo() { return edgeTo; }

  public NonWeightedDirectedEdgeX[] nwedgeTo(int z) {
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    NonWeightedDirectedEdgeX[] nwde = ofDim(NonWeightedDirectedEdgeX.class, edgeTo.get(z).length);
    for (int i = 0; i < edgeTo.get(z).length; i++) 
      if( edgeTo.get(z)[i] == null) nwde[i] = null;
      else nwde[i] = edgeTo.get(z)[i].toNonWeightedDirectedEdgeX();
    return nwde;
  }
  
  public void parentEdgeRep(int z) {
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    NonWeightedDirectedEdgeX[] nwde = nwedgeTo(z);
    for (int i = 0; i < nwde.length; i++) System.out.println(i+"|"+nwde[i]);
  }
  
  public String[] parentEdges(int z) {
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    NonWeightedDirectedEdgeX[] nwde = nwedgeTo(z);
    String[] r = new String[nwde.length];
    for (int i = 0; i < nwde.length; i++) r[i] = i+"|"+nwde[i];
    return r;
  }
    
  public Seq<DirectedEdgeX> sptEdges(int z) { 
    // return Seq of edges of SPT for index z
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    Seq<DirectedEdgeX> s = new Seq<>();
    for (DirectedEdgeX ea : edgeTo.get(z)) s.add(ea);  
    return s; 
  }
  
  public Seq<Seq<DirectedEdgeX>> sptEdges() {
    Seq<Seq<DirectedEdgeX>> s = new Seq<>();
    for (int z = 0; z < edgeTo.size(); z++) s.add(sptEdges(z));
    return s;
  }

  public boolean hasPathTo(int v, int z) {
    // return true if exists path from source to v for index z else false
    if (z < 0 || z > distTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    validateVertex(v);
    return distTo.get(z)[v] < Double.POSITIVE_INFINITY;
  }
  
  public Seq<Boolean> hasPathTo(int v) {
    // return a Seq<Boolean> such that the ith one is true if there exists
    // a path from source to v for index i else false
    Seq<Boolean> s = new Seq<>();
    for (double[] d : distTo) s.add(d[v] < Double.POSITIVE_INFINITY);
    return s;
  }

  public Iterable<DirectedEdgeX> pathTo(int v, int z) {
    // return a shortest path from source to v for index z if possible else returns null
    // if v is valid else throws exception
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    validateVertex(v);
    if (!hasPathTo(v,z)) return null;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo.get(z)[v]; e != null; e = edgeTo.get(z)[e.from()])
      path.push(e);
    return path;
  }
  
  public Seq<Iterable<DirectedEdgeX>> pathTo(int v) {
    validateVertex(v);
    Seq<Iterable<DirectedEdgeX>> s = new Seq<>();
    for (int z = 0; z < edgeTo.size(); z++) s.add(pathTo(v,z));
    return s;      
  }
  
  public Seq<DirectedEdgeX> seqTo(int v, int z) {
    // return a shortest path from source to v for all indices if possible else returns null
    // if v is valid else throws exception
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    validateVertex(v);
    Seq<DirectedEdgeX> seq = new Seq<>();
    if (!hasPathTo(v,z)) return seq;
    Stack<DirectedEdgeX> path = new Stack<DirectedEdgeX>();
    for (DirectedEdgeX e = edgeTo.get(z)[v]; e != null; e = edgeTo.get(z)[e.from()]) path.push(e);
    return new Seq<>(path);
  }
  
  public Seq<Seq<DirectedEdgeX>> allPaths(int z) {
    // return all paths for index z
    if (z < 0 || z > edgeTo.size() -1) throw new IllegalArgumentException("index z is out of bounds");
    Seq<Seq<DirectedEdgeX>> paths = new Seq<>();
    for (int i = 0; i < V; i++) paths.add(seqTo(i,z));
    return paths;
  }
  
  public Seq<Seq<Seq<DirectedEdgeX>>> allPaths() {
    Seq<Seq<Seq<DirectedEdgeX>>> s = new Seq<>();
    for (int z = 0; z < edgeTo.size(); z++) s.add(allPaths(z));
    return s;
  }
  
  public Seq<String> paths(int z) {
    Seq<String> s = new Seq<>();
    StringBuilder sb = new StringBuilder();
    for (Seq<DirectedEdgeX> sde : allPaths(z)) {
      sb.delete(0, sb.length());
      if (sde.size() == 0) sb.append(source);
      else for (int i = 0; i < sde.size(); i++) {
        if (i == 0) sb.append(sde.get(0).from());
        sb.append("->").append(sde.get(i).to());
      }
      s.add(sb.toString());
    }
    return s;
  }
  
  public Seq<Seq<String>> paths() {
    Seq<Seq<String>> s = new Seq<>();
    for (int i = 0; i < edgeTo.size(); i++) s.append(paths(i));
    return s;
  }
  
  public Seq<String> secondPathOrNull() {
    // for ex4.4.7, return a sequence containing for each vertex 
    // the 2nd path to it from source if present in paths() or null
    // if paths contains no second path
    String[] sa = fill(V,()->"x");
    Seq<Seq<String>> paths = paths();
    for (int i = 0; i < paths.size()-1; i++) {
      Seq<String> pi = paths.get(i);
      for (int j = 1; j < paths.size(); j++) {
        Seq<String> pj = paths.get(j);
        for (int k = 0; k < V; k++) {
          if (k == source || !sa[k].equals("x") || pi.get(k) == null || pj.get(k) == null) continue;
          if (!pi.get(k).equals(pj.get(k)) && pj.get(k).length() > 0) sa[k] = pj.get(k);
        }
      }
    }
    Seq<String> s = new Seq<>();   int width = (""+V).length();
    String sp1, sp2; // format spacing strings
    if (width <= 6) { 
      sp1 = sp(2); sp2 = sp(6-width+2);
      s.add("target"+sp1+"2nd shortest path from source to target");
    } else {
      sp2 = sp(2); sp1 = sp(width-6+2);
      s.add("target"+sp1+"2ndshortest path from source to target");
    }
    int c = 0;
    for (int i = 0; i < V; i++) {
      if (i == source) continue;
      else if (sa[i] == null || sa[i].equals("x")) continue;
      else { s.add(String.format("%"+width+"d"+sp2+"%s", i, sa[i])); c++; }
    }
    if (c < V-2) s.add("shortest path from source to target is null for all other vertices");
    return s;
  }
  
  public Seq<String> secondPathOrNull2() {
    // for ex4.4.7, return a sequence containing for each vertex 
    // the 2nd path to it from source if present in paths() or null
    // if paths contains no second path
    String[] sa = fill(V,()->"x");
    Seq<Seq<String>> paths = paths();
    for (int i = 0; i < paths.size()-1; i++) {
      Seq<String> pi = paths.get(i);
      for (int j = 1; j < paths.size(); j++) {
        Seq<String> pj = paths.get(j);
        for (int k = 0; k < V; k++) {
          if (k == source || !sa[k].equals("x") || pi.get(k) == null || pj.get(k) == null) continue;
          if (!pi.get(k).equals(pj.get(k)) && pj.get(k).length() > 0) sa[k] = pj.get(k);
        }
      }
    }
    for (int i = 0; i < V; i++) 
      if (i == source) sa[i] = "(source)";
      else if (sa[i].equals("x")) sa[i] = null;      
    return new Seq<>(sa);
  }


  @SafeVarargs
  private final boolean check(int z, boolean...b) {
    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    if (b == null || b.length == 0|| b[0] == true) {
      // check that edge weights are nonnegative
      for (DirectedEdgeX e : G.edges()) {
        if (e.weight() < 0) {
          System.err.println("negative edge weight detected");
          return false;
        }
      }
    }

    int s = source;

    // check that distTo[v] and edgeTo[v] are consistent
    if (distTo.get(z)[s] != 0.0 || edgeTo.get(z)[s] != null) {
      System.err.println("distTo[s] and edgeTo[s] inconsistent for SPT"+z);
      return false;
    }
    for (int v = 0; v < V; v++) {
      if (v == s) continue;
      if (edgeTo.get(z)[v] == null && distTo.get(z)[v] != Double.POSITIVE_INFINITY) {
        System.err.println("distTo[] and edgeTo[] inconsistent for SPT"+z);
        return false;
      }
    }

    // check that all edges e = u->v satisfy distTo[v] <= distTo[u] + e.weight()
    for (int u = 0; u < V; u++) {
      for (DirectedEdgeX e : G.adj(u)) {
        int v = e.to();
        if (distTo.get(z)[u] + e.weight() < distTo.get(z)[v]) {
          System.err.println("edge " + e + " not relaxed for SPT"+z);
          return false;
        }
      }
    }

    // check that all edges e = u->v on SPT satisfy distTo[v] == distTo[u] + e.weight()
    for (int v = 0; v < V; v++) {
      if (edgeTo.get(z)[v] == null) continue;
      DirectedEdgeX e = edgeTo.get(z)[v];
      int u = e.from();
      if (v != e.to()) return false;
      if (distTo.get(z)[u] + e.w() != distTo.get(z)[v]) {
        System.err.println("edge " + e + " on shortest path not tight for SPT"+z);
        return false;
      }
    }
    System.out.println("SPT"+z+" satisfies optimality conditions");
    return true;
  }
  
  private boolean check() {
    if (distTo.size() != edgeTo.size()) {
      System.err.println("distTo.size() != edgeTo.size()");
      return false;
    }
    if (edgeTo.size() != pq.size()) {
      System.err.println("edgeTo.size() != pq.size()");
      return false;
    }
    check(0);
    if (distTo.size() == 1) return true;
    for (int i = 1; i < distTo.size(); i++) check(i,false);
    System.out.println("All SPTs satisfy optimality conditions\n");
    return true;
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
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
  
  public static void dispose(Seq<Draw> draw) {
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
  
  public static void secondPathsModTinyEWD() {
    System.out.println("\nsecondPathsModTinyEWD:");
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
 
    // reverse edge 0->2
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
    } else {
      System.out.println("could not find edge 0->2");
    }
    
    // change weight of 7->5 to 0.20
    x = G.findEdge(7, 5);
    if (x != null) {
      x.setW(0.20);
      G.search();
      System.out.println("weight of edge(7->5,0.34) changed to 0.20");
    } else {
      System.out.println("could not find edge 7->5");
    }
    
    // change weight of 5->1 to 0.09
    x = G.findEdge(5, 1);
    if (x != null) { 
      x.setW(.09);
      G.search();
      System.out.println("weight of edge(5->1,0.32) changed to 0.09");
    } else {
      System.out.println("could not find edge 5->1");
    }
    
    // change weight of 1->3 to 0.10
    x = G.findEdge(1, 3);
    if (x != null) { 
      x.setW(.10);
      G.search();
      System.out.println("weight of edge(1->3,0.29) changed to 0.10");
    } else {
      System.out.println("could not find edge 1->3");
    }
    
    // change weight of 5->4 to 0.10
    x = G.findEdge(5, 4);
    if (x != null) {
      x.setW(.10);
      G.search();
      System.out.println("weight of edge(5->4,0.35) changed to 0.10");
    } else {
      System.out.println("could not find edge 5->4");
    }
    
    int V = G.V();
    System.out.println("\nEdgeWeightedDigraphX G:\n"+G);
    
    int source = 2; // ex4405 requirement
    
    // define coordinates for EuclidianEdgeWeightedDigraphs
    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    
    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
    
    EuclidianEdgeWeightedDigraph E;
    String title;
    
    DijkstraSPXM sp = new DijkstraSPXM(G, source);    
    int index = sp.edgeTo().size();
    Seq<Draw> draw = new Seq<>();
    
    for (int i = 0; i < index; i++) {
//      System.out.println("SPT"+i+" edgeTo:");
//      for (DirectedEdgeX de : sp.edgeTo.get(i)) System.out.println(de);
      System.out.println("SPT"+i+" shortest paths from vertex "+source+":");
      for (String s : sp.paths(i)) System.out.println(s);
      E = new EuclidianEdgeWeightedDigraph(G,coords);
      title = "modified TinyEWD.txt|SPT"+i+" from 2 (SPT edges are black)";
      draw.add(E.showEx44040507(2.2,sp.sptEdges(i),labels,sp.parentEdges(i),title,E.midPoints()));
      System.out.println();
    }
    
    System.out.println("secondPathOrNull from source "+source+":");
    for (String s : sp.secondPathOrNull()) System.out.println(s);
    System.out.println();
    
    dispose(draw);
  }
  
  public static void secondPathsMediumEWD() {
    System.out.println("secondPathsMediumEWD:");
    In in = new In("mediumEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    
    int source = 0;
       
    DijkstraSPXM sp = new DijkstraSPXM(G, source);    
    int index = sp.edgeTo().size();
    
    for (int i = 0; i < index; i++) {
//      System.out.println("SPT"+i+" edgeTo:");
//      for (DirectedEdgeX de : sp.edgeTo.get(i)) System.out.println(de);
      System.out.println("SPT"+i+" shortest paths from vertex "+source+":");
      for (String s : sp.paths(i)) System.out.println(s);
      System.out.println();
    }
    
    System.out.println("secondPathOrNull from source "+source+":");
    for (String s : sp.secondPathOrNull()) System.out.println(s);
    System.out.println();
   
  }
  
  public static void main(String[] args) {
    
//    secondPathsMediumEWD();
//    System.exit(0);
    
//    //In in = new In(args[0]);
//    In in = new In("tinyEWD.txt");
//    EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
//    //int s = Integer.parseInt(args[1]);
//    int s = 0;
//
//    // compute shortest paths
//    DijkstraSP sp = new DijkstraSP(G, s);


    // print shortest path
//    for (int t = 0; t < G.V(); t++) {
//        if (sp.hasPathTo(t)) {
//            StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
//            for (DirectedEdge e : sp.pathTo(t)) {
//                StdOut.print(e + "   ");
//            }
//            StdOut.println();
//        }
//        else {
//            StdOut.printf("%d to %d         no path\n", s, t);
//        }
//    }
    
/*  tinyEWD.txt 
    reverse 0->2
    make weight of 7->5 + 5->1 + 1->3 = 7->3 == 0.39
    by setting weight 7->5 = 0.20, weight 5->1 = 0.09 and weight of 1->3 = 0.10
    make weight of 2->7 + 7-5 + 5->4 == 2->0 + 0->4 ==.26+.38 = .64
    by setting weight of 5->4 = .10
    8
    15
    4 5 0.35
    5 4 0.35 change weight to .10
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
    
    In in = new In("tinyEWD.txt");
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
 
    // reverse edge 0->2
    DirectedEdgeX x = G.findEdge(0, 2);
    if (x != null) { 
      G.removeEdge(x); 
      G.addEdge(x.reverse());
      G.search();
      System.out.println("edge(0->2,0.26) replaced with edge(2->0,0.26)");
    } else {
      System.out.println("could not find edge 0->2");
    }
    
    // change weight of 7->5 to 0.20
    x = G.findEdge(7, 5);
    if (x != null) {
      x.setW(0.20);
      G.search();
      System.out.println("weight of edge(7->5,0.34) changed to 0.20");
    } else {
      System.out.println("could not find edge 7->5");
    }
    
    // change weight of 5->1 to 0.09
    x = G.findEdge(5, 1);
    if (x != null) { 
      x.setW(.09);
      G.search();
      System.out.println("weight of edge(5->1,0.32) changed to 0.09");
    } else {
      System.out.println("could not find edge 5->1");
    }
    
    // change weight of 1->3 to 0.10
    x = G.findEdge(1, 3);
    if (x != null) { 
      x.setW(.10);
      G.search();
      System.out.println("weight of edge(1->3,0.29) changed to 0.10");
    } else {
      System.out.println("could not find edge 1->3");
    }
    
    // change weight of 5->4 to 0.10
    x = G.findEdge(5, 4);
    if (x != null) {
      x.setW(.10);
      G.search();
      System.out.println("weight of edge(5->4,0.35) changed to 0.10");
    } else {
      System.out.println("could not find edge 5->4");
    }
    
    int V = G.V();
    System.out.println("\nEdgeWeightedDigraphX G:\n"+G);
    
    int source = 2; // ex4405 requirement
    
    // define coordinates for EuclidianEdgeWeightedDigraphs
    double[] coordAr = {45,63.5,42.5,81,58,68,58,79.5,21.5,56,21.6,79.5,77.5,56,37,71};
    Seq<Tuple2<Double,Double>> coords = new Seq<>(V);
    for (int i = 0; i < 2*V-1; i+=2) 
      coords.add(new Tuple2<Double,Double>(coordAr[i],coordAr[i+1]));
    
    // define vertex labels for EuclidianEdgeWeightedDigraphs
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(", ");
    
    EuclidianEdgeWeightedDigraph E;
    String title;
    
    DijkstraSPXM sp = new DijkstraSPXM(G, source);    
    int index = sp.edgeTo().size();
    Seq<Draw> draw = new Seq<>();
    
    for (int i = 0; i < index; i++) {
//      System.out.println("SPT"+i+" edgeTo:");
//      for (DirectedEdgeX de : sp.edgeTo.get(i)) System.out.println(de);
      System.out.println("SPT"+i+" shortest paths from vertex "+source+":");
      for (String s : sp.paths(i)) System.out.println(s);
      E = new EuclidianEdgeWeightedDigraph(G,coords);
      title = "modified TinyEWD.txt|SPT"+i+" from 2 (SPT edges are black)";
      draw.add(E.showEx44040507(2.2,sp.sptEdges(i),labels,sp.parentEdges(i),title,E.midPoints()));
      System.out.println();
    }
    
    System.out.println("secondPathOrNull from source "+source+":");
    for (String s : sp.secondPathOrNull()) System.out.println(s);
    System.out.println();
    
    dispose(draw);
  
  }

}
