package graph;

import ds.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

// based on http://algs4.cs.princeton.edu/44sp/EdgeWeightedDirectedCycle.java

public class EdgeWeightedCycleX {
  private boolean[] marked;             // marked[v] = has vertex v been marked?
  private EdgeX[] edgeTo;        // edgeTo[v] = previous edge on path to v
  private boolean[] onStack;            // onStack[v] = is vertex on the stack?
  private Stack<EdgeX> cycle;    // directed cycle (or null if no such cycle)

  public EdgeWeightedCycleX(EdgeWeightedGraphX G) {
    marked  = new boolean[G.V()];
    onStack = new boolean[G.V()];
    edgeTo  = new EdgeX[G.V()];
    for (int v = 0; v < G.V(); v++) if (!marked[v]) dfs(G, v);
    // check that graph has a cycle
    assert check();
  }

  // check that algorithm computes either the topological order or finds a directed cycle
  private void dfs(EdgeWeightedGraphX G, int v) {
    onStack[v] = true;
    marked[v] = true;
    for (EdgeX e : G.adj(v)) {
      int w = e.to();
      // short circuit if directed cycle found
      if (cycle != null) return;
      // found new vertex, so recur
      else if (!marked[w]) {
        edgeTo[w] = e;
        dfs(G, w);
      }
      // trace back directed cycle
      else if (onStack[w]) {
        cycle = new Stack<EdgeX>();
        EdgeX f = e;
        while (f.from() != w) {
          cycle.push(f);
          f = edgeTo[f.from()];
          if (f == null) { cycle = null; return; }
        }
        cycle.push(f);
        return;
      }
    }
    onStack[v] = false;
  }

  public boolean hasCycle() { return cycle != null; }

  public Iterable<EdgeX> cycle() { return cycle; }

  private boolean check() {
    // certify that graph is either acyclic or has a cycle
    if (hasCycle()) {
      // verify cycle
      EdgeX first = null, last = null;
      for (EdgeX e : cycle()) {
        if (first == null) first = e;
        if (last != null) {
          if (last.to() != e.from()) {
            System.err.printf("cycle edges %s and %s not incident\n", last, e);
            return false;
          }
        }
        last = e;
      }
      if (last.to() != first.from()) {
        System.err.printf("cycle edges %s and %s not incident\n", last, first);
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {

    // create random DAG with V vertices and E edges; then add F random edges
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    int F = Integer.parseInt(args[2]);
    EdgeWeightedGraphX G = new EdgeWeightedGraphX(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++)
      vertices[i] = i;
    StdRandom.shuffle(vertices);
    for (int i = 0; i < E; i++) {
      int v, w;
      do {
        v = StdRandom.uniform(V);
        w = StdRandom.uniform(V);
      } while (v >= w);
      double weight = StdRandom.uniform();
      G.addEdge(new EdgeX(v, w, weight));
    }

    // add F extra edges
    for (int i = 0; i < F; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      double weight = StdRandom.uniform(0.0, 1.0);
      G.addEdge(new EdgeX(v, w, weight));
    }

    StdOut.println(G);

    // find a directed cycle
    EdgeWeightedCycleX finder = new EdgeWeightedCycleX(G);
    if (finder.hasCycle()) {
      StdOut.print("Cycle: ");
      for (EdgeX e : finder.cycle()) {
        StdOut.print(e + " ");
      }
      StdOut.println();
    }

    // or give topologial sort
    else {
      StdOut.println("No directed cycle");
    }
  }

}
