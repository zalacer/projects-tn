package graph;

import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/DepthFirstDirectedPaths.java

public class DepthFirstDirectedPathsX {
  private boolean[] marked;  // marked[v] = true if v is reachable from s
  private int[] edgeTo;      // edgeTo[v] = last edge on path from s to v
  private final int s;       // source vertex

 
  public DepthFirstDirectedPathsX(DigraphX G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    validateVertex(s);
    dfs(G, s);
  }

  private void dfs(DigraphX G, int v) { 
    marked[v] = true;
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, w);
      }
    }
  }

  public boolean hasPathTo(int v) {
    validateVertex(v);
    return marked[v];
  }

  public Iterable<Integer> pathTo(int v) {
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<Integer>();
    for (int x = v; x != s; x = edgeTo[x])
      path.push(x);
    path.push(s);
    return path;
  }

  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

 
  public static void main(String[] args) {
    In in = new In(args[0]);
    DigraphX G = new DigraphX(in);
    // StdOut.println(G);

    int s = Integer.parseInt(args[1]);
    DepthFirstDirectedPathsX dfs = new DepthFirstDirectedPathsX(G, s);

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
