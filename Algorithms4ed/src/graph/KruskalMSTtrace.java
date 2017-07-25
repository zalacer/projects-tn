package graph;

import static v.ArrayUtils.*;

import java.io.IOException;
import java.util.Arrays;

import analysis.Draw;
import ds.Queue;
import ds.Seq;
import ds.UF;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import pq.MinPQ;
import v.Tuple6;

@SuppressWarnings("unused")
public class KruskalMSTtrace {
  private Queue<EdgeY> mst;
  private transient EdgeY pqMin;
  private transient Seq<Integer> mstv;
  private transient Seq<EdgeY> mste;
  private transient Seq<EdgeY> inel;
  private transient Seq<EdgeY> edges;
  private transient Seq<EdgeY> pqe;
  private transient Seq<Tuple6<EdgeY,Seq<Integer>,Seq<EdgeY>,Seq<EdgeY>,Seq<EdgeY>,Seq<EdgeY>>> data;
  private transient GraphX g;
  
  public KruskalMSTtrace(EdgeWeightedGraphY G) {
    if (G == null) throw new IllegalArgumentException(
        "KruskalMSTtrace: EdgeWeightedGraphY is null");
    g = G.graph();
    pqMin = null;
    mstv = new Seq<Integer>();
    mste = new Seq<EdgeY>();
    inel = new Seq<EdgeY>();
    edges = new Seq<EdgeY>(toArray(G.edges().iterator()));
    pqe = new Seq<EdgeY>();
    data = new Seq<>();
    data.add(new Tuple6<>(pqMin,mstv.clone(),mste.clone(),inel.clone(),edges.clone(),pqe.clone()));
    mst = new Queue<EdgeY>();
    MinPQ<EdgeY> pq = new MinPQ<EdgeY>(G.edges());
    UF uf = new UF(G.V());
    pqMin = pq.isEmpty() ? null : pq.min();
    pqe = pq.isEmpty() ? new Seq<EdgeY>() : new Seq<EdgeY>(pq.toArray2());  
    data.add(new Tuple6<>(pqMin,mstv.clone(),mste.clone(),inel.clone(),edges.clone(),pqe.clone()));   
    while (!pq.isEmpty() && mst.size() < G.V()-1) {
      EdgeY e = pq.delMin(); // Get min weight edge on pq
      int v = e.either(), w = e.other(v); // and its vertices.
      if (uf.connected(v, w)) {
        pqMin = pq.isEmpty() ? null : pq.min();
        pqe = pq.isEmpty() ? new Seq<EdgeY>() : new Seq<EdgeY>(pq.toArray2());  
        if (!mste.contains(e)) inel.add(e);
        data.add(new Tuple6<>(pqMin,mstv.clone(),mste.clone(),inel.clone(),edges.clone(),pqe.clone())); 
        continue; // Ignore ineligible edges.
      }
      uf.union(v, w); // Merge components.
      mst.enqueue(e); // Add edge to mst.
      pqMin = pq.isEmpty() ? null : pq.min();
      pqe = pq.isEmpty() ? new Seq<EdgeY>() : new Seq<EdgeY>(pq.toArray2());
      if (!mstv.contains(e.u())) mstv.add(e.u()); 
      if (!mstv.contains(e.v())) mstv.add(e.v());
      mste.add(e);
      data.add(new Tuple6<>(pqMin,mstv.clone(),mste.clone(),inel.clone(),edges.clone(),pqe.clone()));   
    }
  }
  public Iterable<EdgeY> edges() { return mst; }

  public double weight() { // See Exercise 4.3.31.
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
    
    KruskalMSTtrace K = new KruskalMSTtrace(G);
        
    EuclidianGraph eg = new EuclidianGraph(K.g);
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
    for (int i = 0; i < K.data.size()-1; i++)
      drawings.add(eg.showKruskalMSTtrace(labels,K.data.get(i),"Step "+c++));
    drawings.add(eg.showKruskalMSTtrace(labels,K.data.get(c),"Final"));
    return drawings;
  }
  
  public static void closeDrawings(Seq<Draw> s) {
    for (Draw d : s) {
      d.frame().setVisible(false);
      d.frame().dispose();
      d = null;
    }
  }
  
  public static void kruskalTrace() {
    Seq<Draw> drawings = traceTinyEWG();
    pause(10);
    closeDrawings(drawings);
  }
  
  public static void pause(int s) {
    try { Thread.sleep(s*1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  public static long getPID() {
    String processName =
      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
    return Long.parseLong(processName.split("@")[0]);
  }
  
  public static void kill(long pid) {
    String cmd = "taskkill /F /PID " + pid;
    try {
      Runtime.getRuntime().exec(cmd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
//  public static void presentKruskalTrace() {
//    Thread t = new Thread(() -> {
//      Seq<Draw> drawings = traceTinyEWG();
//      pause(10);
//      closeDrawings(drawings);
////      long pid = getPID();
////      kill(pid);
//    });
//    
//    t.start();
//    
//    try {
//      t.join();
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//    }
//  }
//  
//  public static void presentEagerPrimTrace() {
//    Thread t = new Thread(() -> {
//      Seq<Draw> drawings = EagerPrimMSTtrace.traceTinyEWG();
//      pause(10);
//      closeDrawings(drawings);
////      long pid = getPID();
////      kill(pid);
//    });
//    
//    t.start();
//    
//    try {
//      t.join();
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//    }
//  }
  
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
//    KruskalMSTtrace mst = new KruskalMSTtrace(G);
//    for (EdgeY e : mst.edges()) {
//        StdOut.println(e);
//    }
//    StdOut.printf("%.5f\n", mst.weight());
  }

}
