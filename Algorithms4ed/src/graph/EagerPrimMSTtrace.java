package graph;

import static java.lang.Double.POSITIVE_INFINITY;
import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import analysis.Draw;
import ds.Queue;
import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.IndexMinPQY;
import v.Tuple9;

//based on PrimMST from text p622
//for ex4318

@SuppressWarnings("unused")
public class EagerPrimMSTtrace {
  private EdgeY[] edgeTo; // shortest edge from tree vertex
  private double[] distTo; // distTo[w] = edgeTo[w].weight()
  private boolean[] marked; // true if v on tree
  private IndexMinPQY<Double> pq; // eligible crossing edges
  private transient EdgeWeightedGraphY mst;
  private transient Seq<EdgeY> mste;   // MST edges
  private transient Seq<EdgeY> inel;   // invalid edges
  private transient Seq<Integer> mstv;   // MST vertices
  private transient EdgeY nextEdge; // next edge to put in MST
  private transient EdgeWeightedGraphY G; // input graph
  private transient GraphX g; // parallel graph of input graph
  private Seq<Tuple9<Integer,EdgeY,Seq<Integer>,Seq<Integer>,Seq<EdgeY>,Seq<EdgeY>,EdgeY[],double[],boolean[]>> data;
  /* data components
     1    v                           Integer
     2    EdgeY                       nextEdge
     3    Seq<Integer>(pq.toArray())  Seq<Integer>
     4    mstv                        Seq<Integer>
     5    mste                        Seq<EdgeY>
     6    inel                        Seq<EdgeY>
     7    edgeTo                      EdgeY[]
     8    distTo                      double[]      // unused
     9    marked                      boolean[]     // unused
  */  
//  private Tuple9<Integer,EdgeY,Seq<Integer>,Seq<Integer>,Seq<EdgeY>,Seq<EdgeY>,EdgeY[],double[],boolean[]> t8;
  
  public EagerPrimMSTtrace(EdgeWeightedGraphY G) {
    if (G == null) throw new IllegalArgumentException(
        "EagerPrimMSTtrace: EdgeWeightedGraphY is null");
//    if (!G.isConnected()) throw new IllegalArgumentException(
//        "PrimMSTB: EdgeWeightedGraphY isn't connected");
    this.G = G;
    g= G.graph();
    edgeTo = new EdgeY[G.V()];
    distTo = new double[G.V()];
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) distTo[v] = POSITIVE_INFINITY;
    mste = new Seq<>();
    inel = new Seq<>();
    mstv = new Seq<>();
    pq = new IndexMinPQY<>(G.V());
    distTo[0] = 0.0;
    data = new Seq<>();
    data.add(new Tuple9<>(-2,null,new Seq<Integer>(),mstv.clone(),
        mste.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
    pq.insert(0, 0.0); // Initialize pq with 0, weight 0.    
    data.add(new Tuple9<>(-1,null,new Seq<Integer>(pq.toArray().clone()),mstv.clone(),
        mste.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
    while (!pq.isEmpty()) {
      visit(G, pq.delMin()); // Add closest vertex to tree.
    }
    mst();
  }
  
  private void visit(EdgeWeightedGraphY G, int v) { 
    // Add v to tree; update data structures.
    marked[v] = true;
    mstv.add(v);
    for (EdgeY e : G.adj(v)) {
      int w = e.other(v);
      if (marked[w]) { // v-w is ineligible.
        if (!mste.contains(e)) inel.add(e);
        continue; 
      }
      if (e.weight() < distTo[w]) { 
        // EdgeY e is new best connection from tree to w.
        if (edgeTo[w] != null) inel.add(edgeTo[w]);
        edgeTo[w] = e;
        distTo[w] = e.weight();
        if (pq.contains(w)) pq.changeKey(w, distTo[w]);
        else pq.insert(w, distTo[w]);
      }
    }
    if (nextEdge != null) mste.add(nextEdge);
    nextEdge = nextEdge();
    data.add(new Tuple9<>(v,nextEdge,new Seq<Integer>(pq.toArray().clone()),mstv.clone(),
        mste.clone(),inel.clone(),edgeTo.clone(),distTo.clone(),marked.clone()));
  }
  
  private EdgeY nextEdge() {
    // return the next edge to go into the MST else null
    EdgeY min = null;
    for (int v : mstv) {
      for (EdgeY e : edgeTo) {
        if (e == null || mste.contains(e)) continue;
        int x = e.u(), y = e.v();
        if (!(x == v || y == v)) continue;
        if (x == v && marked[y] || y == v && marked[x]) continue;    
        if (min == null) min = e;
        else if (e.w() < min.w()) min = e;
      }
    }
    return min;
  }
 
  public EdgeWeightedGraphY mst() { 
    mst = new EdgeWeightedGraphY(edges());
    return mst;  
  }
  
  public Iterable<EdgeY> edges() {
    // from PrimMST
    Queue<EdgeY> mst = new Queue<>();
    for (int v = 0; v < edgeTo.length; v++) {
      EdgeY e = edgeTo[v];
      if (e != null) mst.enqueue(e);
    }
    return mst;
  }
    
  public double weight() {
    // from PrimMST
    double weight = 0.0;
    for (EdgeY e : edges()) weight += e.weight();
    return weight;
  }
  
  public static Seq<Draw> traceTinyEWG() {
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    EdgeWeightedGraphY G = new EdgeWeightedGraphY(V,edges);
    
    EagerPrimMSTtrace L = new EagerPrimMSTtrace(G);
    
    EuclidianGraph eg = new EuclidianGraph(L.g);
    eg.addCoords(0,45,77.5);
    eg.addCoords(1,42.5,95);
    eg.addCoords(2,58,82);
    eg.addCoords(3,58,93.5);
    eg.addCoords(4,21.5,70);
    eg.addCoords(5,21.6,92.5);
    eg.addCoords(6,77.5,70);
    eg.addCoords(7,37,85);
    
    Seq<Draw> drawings = new Seq<>();

    int c = 0;
    String[] labels = Arrays.toString(range(0,V)).split("[\\[\\]]")[1].split(",");
    for (int i = 0; i < L.data.size()-1; i++)
      drawings.add(eg.showEagerPrimMSTtrace(labels,L.data.get(i),"Step "+c++));
    drawings.add(eg.showEagerPrimMSTtrace(labels,L.data.get(c),"Final"));
    return drawings;
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

  public static void main(String[] args) {
    
    Seq<Draw> draw = traceTinyEWG();
    dispose(draw);
    
//    In in = new In(args[0]);
//    EdgeWeightedGraphY G = new EdgeWeightedGraphY(in);
//    EagerPrimMSTtrace p = new EagerPrimMSTtrace(G);
//    System.out.println("edges in MST:");
//    for (EdgeY e : p.edges()) {
//        StdOut.println(e);
//    }
//    StdOut.printf("\ntotal weight of MST = %.5f\n", p.weight());
//    EdgeWeightedGraphY mst = p.mst;
//    System.out.println("mst.V() = "+mst.V());
//    System.out.println("mst.E() = "+mst.E());
//    System.out.println("mst.isConnected() = "+mst.isConnected());
    
//    EdgeWeightedGraphY g = new EdgeWeightedGraphY(3);
//    g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(0,2,2); 
//    
//    System.out.println("KruskalMSTB:");
//    KruskalMSTB k = new KruskalMSTB(g);
//    for (EdgeY e : k.edges()) System.out.println(e);
//    System.out.printf("%.5f\n", k.weight());
//    
//    System.out.println("\nPrimMSTB:");
//    PrimMSTBX p = new PrimMSTBX(g);
//    for (EdgeY e : p.edges()) StdOut.println(e);
//    System.out.printf("%.5f\n", p.weight());
    

  }
  
/*
Add 0 to the pq: mstv = (), mste = ()
edgeTo = [null,null,null,null,null,null,null,null]
distTo = [0.0,Infinity,Infinity,Infinity,Infinity,Infinity,Infinity,Infinity]
marked = [false,false,false,false,false,false,false,false]
pq = [0]

Add 0 to the MST:  mstv = (0), mste = ()
v = 0
changed weight of 6 from Infinity to 0.58
changed weight of 2 from Infinity to 0.26
changed weight of 4 from Infinity to 0.38
changed weight of 7 from Infinity to 0.16
edgeTo = [null,null,(0-2,0.26),null,(0-4,0.38),null,(6-0,0.58),(0-7,0.16)]
distTo = [0.0,Infinity,0.26,Infinity,0.38,Infinity,0.58,0.16]
marked = [true,false,false,false,false,false,false,false]
pq = [7,2,4,6]
mstv = (0)
mste = ()

Add 7 and  0-7 to the MSTmstv = (0,7), mste = (0-7)
v = 7
changed weight of 1 from Infinity to 0.19
changed weight of 5 from Infinity to 0.28
changed weight of 4 from 0.38 to 0.37
updated priority of 4 in pq
edgeTo = [null,(1-7,0.19),(0-2,0.26),null,(4-7,0.37),(5-7,0.28),(6-0,0.58),(0-7,0.16)]
distTo = [0.0,0.19,0.26,Infinity,0.37,0.28,0.58,0.16]
marked = [true,false,false,false,false,false,false,true]
pq = [1,2,5,4,6]
mstv = (0,7)
mste = ((0-7,0.16))

Add 1 and 1-7 to the MST: mstv = (0,7,1), mste = (0-7,1-7)
v = 1
changed weight of 3 from Infinity to 0.29
edgeTo = [null,(1-7,0.19),(0-2,0.26),(1-3,0.29),(4-7,0.37),(5-7,0.28),(6-0,0.58),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.29,0.37,0.28,0.58,0.16]
marked = [true,true,false,false,false,false,false,true]
pq = [2,5,3,4,6]
mstv = (0,7,1)
mste = ((0-7,0.16),(1-7,0.19))

Add 2 and 0-2 to the MST: mstv = (0,7,1,2), mste = (0-7,1-7,0-2)
v = 2
changed weight of 6 from 0.58 to 0.4
updated priority of 6 in pq
changed weight of 3 from 0.29 to 0.17
updated priority of 3 in pq
edgeTo = [null,(1-7,0.19),(0-2,0.26),(2-3,0.17),(4-7,0.37),(5-7,0.28),(6-2,0.40),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.17,0.37,0.28,0.4,0.16]
marked = [true,true,true,false,false,false,false,true]
pq = [3,5,4,6]
mstv = (0,7,1,2)
mste = ((0-7,0.16),(1-7,0.19),(0-2,0.26))


Add 3 and 2-3 to the MST: mstv = (0,7,1,2,3), mste = (0-7,1-7,0-2,2-3)
v = 3
edgeTo = [null,(1-7,0.19),(0-2,0.26),(2-3,0.17),(4-7,0.37),(5-7,0.28),(6-2,0.40),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.17,0.37,0.28,0.4,0.16]
marked = [true,true,true,true,false,false,false,true]
pq = [5,4,6]
mstv = (0,7,1,2,3)
mste = ((0-7,0.16),(1-7,0.19),(0-2,0.26),(2-3,0.17))

add 5 and and 5-7 to the MST: mstv = (0,7,1,2,3,5), mste = (0-7,1-7,0-2,2-3,5-7)
v = 5
changed weight of 4 from 0.37 to 0.35
updated priority of 4 in pq
edgeTo = [null,(1-7,0.19),(0-2,0.26),(2-3,0.17),(4-5,0.35),(5-7,0.28),(6-2,0.40),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.17,0.35,0.28,0.4,0.16]
marked = [true,true,true,true,false,true,false,true]
pq = [4,6]
mstv = (0,7,1,2,3,5)
mste = ((0-7,0.16),(1-7,0.19),(0-2,0.26),(2-3,0.17),(5-7,0.28))

add 4 and 4-5 to the MST: mstv = (0,7,1,2,3,5,4), mste = (0-7,1-7,0-2,2-3,5-7,4-5)
v = 4
edgeTo = [null,(1-7,0.19),(0-2,0.26),(2-3,0.17),(4-5,0.35),(5-7,0.28),(6-2,0.40),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.17,0.35,0.28,0.4,0.16]
marked = [true,true,true,true,true,true,false,true]
pq = [6]
mstv = (0,7,1,2,3,5,4)
mste = ((0-7,0.16),(1-7,0.19),(0-2,0.26),(2-3,0.17),(5-7,0.28),(4-5,0.35))

add 6 and 6-2 to the MST: mstv = (0,7,1,2,3,5,4,6), mste = (0-7,1-7,0-2,2-3,5-7,4-5,6-2)
v = 6
edgeTo = [null,(1-7,0.19),(0-2,0.26),(2-3,0.17),(4-5,0.35),(5-7,0.28),(6-2,0.40),(0-7,0.16)]
distTo = [0.0,0.19,0.26,0.17,0.35,0.28,0.4,0.16]
marked = [true,true,true,true,true,true,true,true]
pq = []
mstv = (0,7,1,2,3,5,4,6)
mste = ((0-7,0.16),(1-7,0.19),(0-2,0.26),(2-3,0.17),(5-7,0.28),(4-5,0.35),(6-2,0.40))


edges in MST:
(1-7,0.19)
(0-2,0.26)
(2-3,0.17)
(4-5,0.35)
(5-7,0.28)
(6-2,0.40)
(0-7,0.16)

total weight of MST = 1.81000
mst.V() = 8
mst.E() = 7
mst.isConnected() = true


*/

}
