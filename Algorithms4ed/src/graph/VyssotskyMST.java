package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import ds.Stack;

// ex4322

@SuppressWarnings("unused")
public class VyssotskyMST {
  private Seq<EdgeX> mst;
  
  public VyssotskyMST(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException("VyssotskyMST: G == null");
    int V = G.V();
    Seq<EdgeX> edges = (new Seq<>(G.edges())).sort();
    mst = new Seq<>(); 
    int i = 0;
    while(mst.size() < V-1 && i < edges.size()) {
      mst.add(edges.get(i++));
      EdgeWeightedGraphX g = new EdgeWeightedGraphX(V,mst);
      Stack<Integer> cycleStack = g.graph().cycle();
      if (!(cycleStack == null || cycleStack.size() < 2)) {
        Seq<Integer> cycle = new Seq<>(cycleStack);
        EdgeX max = null, e = null;
        for (int j = 0; j < cycle.size()-1; j++) {
          e = g.findEdge(cycle.get(j), cycle.get(j+1), G.adj(cycle.get(j)));
          if (e != null) {
            if (max == null) max = e;
            else if (e.w() > max.w()) max = e;
          }
        }
        if (max != null) mst.remove(g.removeEdge(max.u(),max.v()));
      }
    }
  }
  
  public Seq<EdgeX> mst() { return mst; }
  
  public double weight() {
    double weight = 0.0;
    for (EdgeX e : mst) weight += e.weight();
    return weight;
  }
  
  public static Seq<EdgeX> vyssotskyMST(EdgeWeightedGraphX G) {
    if (G == null) throw new IllegalArgumentException("vyssotskyMST: G == null");
    int V = G.V();
    Seq<EdgeX> edges = (new Seq<>(G.edges())).sort();
    Seq<EdgeX> mst = new Seq<>(); 
    int i = 0;
    while(mst.size() < V-1 && i < edges.size()) {
      mst.add(edges.get(i++));
      EdgeWeightedGraphX g = new EdgeWeightedGraphX(V,mst);
      Stack<Integer> cycleStack = g.graph().cycle();
      if (!(cycleStack == null || cycleStack.size() < 2)) {
        Seq<Integer> cycle = new Seq<>(cycleStack);
        EdgeX max = null; EdgeX e = null;
        for (int j = 0; j < cycle.size()-1; j++) {
          e = g.findEdge(cycle.get(j), cycle.get(j+1), G.adj(cycle.get(j)));
          if (e != null) {
            if (max == null) max = e;
            else if (e.w() > max.w()) max = e;
          }
        }
        if (max != null) mst.remove(g.removeEdge(max.u(),max.v()));
      }
    }
    return mst;
  }

  public static void main(String[] args) {
    
    int V = 8;
    String edges = "4 5 0.35  4 7 0.37  5 7 0.28  0 7 0.16  1 5 0.32 "
       + "0 4 0.38  2 3 0.17  1 7 0.19  0 2 0.26  1 2 0.36  1 3 0.29 "
       + "2 7 0.34  6 2 0.40  3 6 0.52  6 0 0.58  6 4 0.93";
    
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V,edges);
    
    VyssotskyMST vys = new VyssotskyMST(G);
    Seq<EdgeX> mst = vys.mst();
    for (EdgeX e : mst) System.out.println(e);
    System.out.println(vys.weight());

  }

}
