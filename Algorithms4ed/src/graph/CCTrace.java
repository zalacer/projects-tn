package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/CC.java

@SuppressWarnings("unused")
public class CCTrace {
  private Boolean[] marked;   // marked[v] = has vertex v been marked?
  private Integer[] id;           // id[v] = id of connected component containing v
  private int[] size;         // size[id] = number of vertices in given component
  private int count;          // number of connected components
  private final int V;

  public CCTrace(GraphX G) {
    if (G == null) throw new IllegalArgumentException("CCTrace: graph is null");
    V = G.V();
    System.out.println("adj: "+arrayToString(G.adj(),900,1,1));
    marked = new Boolean[V];
    id = new Integer[V];
    size = new int[V];
    String l = arrayToString(range(0,V),900,1,1);
    l = l.replaceAll("\\[|\\]","").replaceAll(",", " ");
    System.out.println("\n              marked[]                   id[]");
    System.out.println("              "+l+"  "+l);
    for (int v = 0; v < V; v++) {
      if (marked[v] == null || !marked[v]) {
        dfs(G, v);
        count++;
      }
    }
  }

  public CCTrace(EdgeWeightedGraph G) {
    if (G == null) throw new IllegalArgumentException("CCTrace: graph is null");
    V = G.V();
    marked = new Boolean[V];
    id = new Integer[V];
    size = new int[V];
    for (int v = 0; v < V; v++) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
  }

  private final void dfs(GraphX G, int v, String...sa) {
    marked[v] = true;
    id[v] = count;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true","T").replaceAll("false","F").replaceAll("\\[|\\]","")
        .replaceAll("null|,"," ");
    String i = arrayToString(id,900,1,1);
    i = i.replaceAll("\\[|\\]","").replaceAll("null|,", " ");
    String s = sa != null && sa.length > 0 ? sa[0] : "";
    System.out.printf("%-11s %"+(V+V+1)+"s    %s\n", s+"dfs("+v+")", m, i);
    size[count]++;
    for (int w : G.adj(v)) if (marked[w] == null) dfs(G, w, s+" ");
  }
  
  private void dfs2(GraphX G, int v) {
    marked[v] = true;
    id[v] = count;
    String m = arrayToString(marked,900,1,1);
    m = m.replaceAll("true", "T").replaceAll("false", "F").replaceAll("\\[|\\]","")
        .replaceAll("null|,", " ");
    String i = arrayToString(id,900,1,1);
    i = i.replaceAll("\\[|\\]","").replaceAll("null|,", " ");
    System.out.printf("%-7s %"+(V+V+1)+"s    %s\n", "dfs("+v+")", m, i);
    size[count]++;
    for (int w : G.adj(v)) {
      if (marked[w] == null || !marked[w]) {
        dfs(G, w);
      }
    }
  }

  // depth-first search for an EdgeWeightedGraph
  private void dfs(EdgeWeightedGraph G, int v) {
    marked[v] = true;
    id[v] = count;
    size[count]++;
    for (Edge e : G.adj(v)) {
      int w = e.other(v);
      if (!marked[w]) {
        dfs(G, w);
      }
    }
  }

  public int id(int v) {
    validateVertex(v);
    return id[v];
  }

  public int size(int v) {
    validateVertex(v);
    return size[id[v]];
  }

  public int count() {
    return count;
  }

  public boolean connected(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    return id(v) == id(w);
  }

  @Deprecated
  public boolean areConnected(int v, int w) {
    validateVertex(v);
    validateVertex(w);
    return id(v) == id(w);
  }

  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
  }

  public static void main(String[] args) {
    // edges are from tinyGex2.txt
    String edges = "8 4 2 3 1 11 0 6 3 6 10 3 7 11 7 8 11 8 2 0 6 2 5 2 5 10 3 10 8 1 4 1";
//    In in = new In(args[0]);
//    GraphX G = new GraphX(in);
    GraphX G = new GraphX(12,edges);
    CCTrace cc = new CCTrace(G);

    // number of connected components
    int m = cc.count();
    StdOut.println("\n"+m + " components:");

    // compute list of vertices in each connected component
    Queue<Integer>[] components = ofDim(Queue.class, m);
    for (int i = 0; i < m; i++) {
      components[i] = new Queue<Integer>();
    }
    for (int v = 0; v < G.V(); v++) {
      components[cc.id(v)].enqueue(v);
    }

    // print results
    for (int i = 0; i < m; i++) {
      for (int v : components[i]) {
        StdOut.print(" " + v);
      }
      StdOut.println();
    }
  }
}
