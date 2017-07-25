package graph;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.StdOut;

/******************************************************************************
 *  http://algs4.cs.princeton.edu/41graph/Bridge.java
 *  http://algs4.cs.princeton.edu/41graph/Bridge.java.html
 *  Compilation:  javac Bridge.java
 *  Execution:    java  Bridge V E
 *  Dependencies: Graph.java GraphGenerator.java
 *
 *  Identifies bridge edges and prints them out. This decomposes
 *  a directed graph into two-edge connected components.
 *  Runs in O(E + V) time.
 *
 *  Key quantity:  low[v] = minimum DFS preorder number of v
 *  and the set of vertices w for which there is a back edge (x, w)
 *  with x a descendant of v and w an ancestor of v.
 *
 *  Note: code assumes no parallel edges, e.g., two parallel edges
 *  would be (incorrectly) identified as bridges.
 *
 ******************************************************************************/

public class BridgeTrace {
  private int bridges;      // number of bridges
  private int cnt;          // counter
  private int[] pre;        // pre[v] = order in which dfs examines v
  private int[] low;        // low[v] = lowest preorder of any vertex connected to v
  private final int V;
  private StringBuilder sb;

  public BridgeTrace(Graph G) {
    V = G.V();
    if (V > 10) System.out.println("warning: formatting off when V > 10");
    sb = new StringBuilder();
    low = new int[V];
    pre = new int[V];
    for (int v = 0; v < V; v++)
      low[v] = -1;
    for (int v = 0; v < V; v++)
      pre[v] = -1;
    System.out.println("adj: "+arrayToString(G.adj(),900,1,1));
    String l = arrayToString(range(0,V),900,1,1);
    l = l.replaceAll("\\[|\\]","");
    String sp = space(l.length()-"pre[]".length()+4);
    System.out.println("\n              pre[]"+sp+"low[]");
    System.out.println("              "+l+"    "+l);
    for (int v = 0; v < V; v++) dfs(G, v, v);
    System.out.println(sb.toString());
  }

  public int components() { return bridges + 1; }

  public boolean isEdgeConnected() { return bridges == 0 ; }

  private void dfs(Graph G, int u, int v, String...sa) {
    pre[v] = cnt++;
    low[v] = pre[v];
    String l = arrayToString(low,900,1,1);
    l = l.replaceAll("\\[|\\]","") .replaceAll("-1|,"," ");;
    String p = arrayToString(pre,900,1,1);
    p = p.replaceAll("\\[|\\]","") .replaceAll("-1|,"," ");;
    String s = sa != null && sa.length > 0 ? sa[0] : "";
    System.out.printf("%-11s %"+(V+V+1)+"s    %s\n", s+"dfs("+u+","+v+")", p, l);
    for (int w : G.adj(v)) {
      if (pre[w] == -1) {
        dfs(G, v, w, s+" ");
        low[v] = Math.min(low[v], low[w]);
        if (low[w] == pre[w]) {
          sb.append(v + "-" + w + " is a bridge\n");
          bridges++;
        }
      } else if (w != u) // update low number - ignore reverse of edge leading to v
        low[v] = Math.min(low[v], pre[w]);
    }
  }

  // test client
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    Graph G = GraphGenerator.simple(V, E);
    StdOut.println(G);

    BridgeTrace bridge = new BridgeTrace(G);
    StdOut.println("Edge connected components = " + bridge.components());
    System.out.println("isEdgeConnected = "+bridge.isEdgeConnected());
  }


}


