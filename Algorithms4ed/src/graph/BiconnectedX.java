package graph;

import static v.ArrayUtils.*;

import ds.Seq;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/41graph/Biconnected.java

@SuppressWarnings("unused")
public class BiconnectedX {
  private int[] low;
  private int[] pre;
  private int cnt;
  private boolean[] articulation;
  int V;

  public BiconnectedX(GraphX G) {
    if (G == null) throw new IllegalArgumentException("BiconnectedX constructor: G is null");
    V = G.V();
    low = new int[G.V()];
    pre = new int[G.V()];
    articulation = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) low[v] = -1;
    for (int v = 0; v < G.V(); v++) pre[v] = -1;
    for (int v = 0; v < G.V(); v++) if (pre[v] == -1) dfs(G, v, v);
  }

  private void dfs(GraphX G, int u, int v) {
    int children = 0;
    pre[v] = cnt++;
    low[v] = pre[v];
    for (int w : G.adj(v)) {
      if (pre[w] == -1) {
        children++;
        dfs(G, v, w);
        // update low number
        low[v] = Math.min(low[v], low[w]);
        // non-root of DFS is an articulation point if low[w] >= pre[v]
        if (low[w] >= pre[v] && u != v) 
          articulation[v] = true;
      }
      // update low number - ignore reverse of edge leading to v
      else if (w != u)  low[v] = Math.min(low[v], pre[w]);
    }

    // root of DFS is an articulation point if it has more than 1 child
    if (u == v && children > 1) articulation[v] = true;
  }

  // is vertex v an articulation point?
  public boolean isArticulation(int v) { return articulation[v]; }
  
  public int[] articulationPoints() {
    Seq<Integer> s = new Seq<>();
    for (int v = 0; v < V; v++) if (isArticulation(v)) s.add(v);
    if (s.isEmpty()) return new int[0];
    return (int[])s.toPrimitiveArray();
  }
  
  public int[] joinPoints() {
    Seq<Integer> s = new Seq<>();
    for (int v = 0; v < V; v++) if (!isArticulation(v)) s.add(v);
    if (s.isEmpty()) return new int[0];
    return (int[])s.toPrimitiveArray();
  }
  
  public static int[] articulationPoints(String file) {
    return (new BiconnectedX(new GraphX(new In(file)))).articulationPoints();  
  }
  
  public static int[] joinPoints(String file) {
    return (new BiconnectedX(new GraphX(new In(file)))).joinPoints();  
  }

  // test client
  public static void main(String[] args) {
    
  System.out.print("articulationPoints="); par(articulationPoints("tinyCG2.txt"));
  System.out.print("joinPoints="); par(joinPoints("tinyCG2.txt"));
  
  System.out.print("articulationPoints="); par(articulationPoints("tinyGex2.txt"));
  System.out.print("joinPoints="); par(joinPoints("tinyGex2.txt"));
    
    
//    int V = Integer.parseInt(args[0]);
//    int E = Integer.parseInt(args[1]);
//    GraphX G = GraphGeneratorX.simple(V, E);
//    StdOut.println(G);
//    
//    In in = new In(args[0]);
//    GraphX G = new GraphX(in);
//    StdOut.println(G);
//    
//    BiconnectedX bic = new BiconnectedX(G);
//
//    // print out articulation points
//    StdOut.println();
//    StdOut.println("Articulation points");
//    StdOut.println("-------------------");
//    for (int v = 0; v < G.V(); v++)
//      if (bic.isArticulation(v)) StdOut.println(v);
//    System.out.print("articulationPoints="); par(bic.articulationPoints());
//    System.out.print("joinPoints="); par(bic.joinPoints());

  }


}
