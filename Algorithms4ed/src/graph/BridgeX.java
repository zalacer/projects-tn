package graph;

import edu.princeton.cs.algs4.StdOut;
import st.HashSET;
import v.Tuple2;

// from http://algs4.cs.princeton.edu/41graph/Bridge.java
// assumes no parallel edges since they would incorrectly be identified as bridges

public class BridgeX {
  private int count;      // number of bridges
  private int cnt;          // counter
  private int[] pre;        // pre[v] = order in which dfs examines v
  private int[] low;        // low[v] = lowest preorder of any vertex connected to v
  private HashSET<Tuple2<Integer,Integer>> bridges;

  public BridgeX(GraphX G) {
    bridges = new HashSET<>();
    low = new int[G.V()];
    pre = new int[G.V()];
    for (int v = 0; v < G.V(); v++) low[v] = -1;
    for (int v = 0; v < G.V(); v++) pre[v] = -1;
    for (int v = 0; v < G.V(); v++) if (pre[v] == -1) dfs(G, v, v);
  }

  public HashSET<Tuple2<Integer,Integer>> bridges() { return bridges; }
  
  public int components() { return count + 1; }

  public boolean isEdgeConnected() { return count == 0 ; }

  private void dfs(GraphX G, int u, int v) {
    pre[v] = cnt++;
    low[v] = pre[v];
    for (int w : G.adj(v)) {
      if (pre[w] == -1) {
        dfs(G, v, w);
        low[v] = Math.min(low[v], low[w]);
        if (low[w] == pre[w]) {
          bridges.add(new Tuple2<>(v,w));
          bridges.add(new Tuple2<>(w,v));
//          StdOut.println(v + "-" + w + " is a bridge");
          count++;
        }
      }
      // update low number - ignore reverse of edge leading to v
      else if (w != u) low[v] = Math.min(low[v], pre[w]);
    }
  }

  // test client
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    GraphX G = GraphGeneratorX.simple(V, E);
    StdOut.println(G);

    BridgeX bridge = new BridgeX(G);
    StdOut.println("Edge connected components = " + bridge.components());
    System.out.println("isEdgeConnected = "+bridge.isEdgeConnected());
    System.out.println("bridges = "+bridge.bridges());
  }


}


