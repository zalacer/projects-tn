package graph;

import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from text p536 Algorithm 4.1

public class DepthFirstPathsB {
  private boolean[] marked; // Has dfs() been called for this vertex?
  private int[] edgeTo; // last vertex on known path to this vertex
  private final int s; // source
  
  public DepthFirstPathsB(Graph G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    dfs(G, s);
  }
  
  private void dfs(Graph G, int v) {
    marked[v] = true;
    for (int w : G.adj(v))
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, w);
      }
  }
  
  public boolean hasPathTo(int v) { return marked[v]; }
  
  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != s; x = edgeTo[x]) path.push(x);
    path.push(s);
    return path;
  }
  
  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    int s = Integer.parseInt(args[1]);
    DepthFirstPathsB dfs = new DepthFirstPathsB(G, s);

    for (int v = 0; v < G.V(); v++) {
      if (dfs.hasPathTo(v)) {
        StdOut.printf("%d to %d:  ", s, v);
        for (int x : dfs.pathTo(v)) {
          if (x == s) StdOut.print(x);
          else        StdOut.print("-" + x);
        }
        StdOut.println();
      }

      else {
        StdOut.printf("%d to %d:  not connected\n", s, v);
      }

    }
  }

}