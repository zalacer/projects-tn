package graph;

import ds.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

// from http://algs4.cs.princeton.edu/44sp/EdgeWeightedDirectedCycle.java

public class EdgeWeightedDirectedCycleX {
  private boolean[] marked;             // marked[v] = has vertex v been marked?
  private DirectedEdgeX[] edgeTo;        // edgeTo[v] = previous edge on path to v
  private boolean[] onStack;            // onStack[v] = is vertex on the stack?
  private Stack<DirectedEdgeX> cycle;    // directed cycle (or null if no such cycle)

  public EdgeWeightedDirectedCycleX(EdgeWeightedDigraphX G) {
    marked  = new boolean[G.V()];
    onStack = new boolean[G.V()];
    edgeTo  = new DirectedEdgeX[G.V()];
    for (int v = 0; v < G.V(); v++) if (!marked[v]) dfs(G, v);
    // check that digraph has a cycle
    assert check();
  }

  // check that algorithm computes either the topological order or finds a directed cycle
  private void dfs(EdgeWeightedDigraphX G, int v) {
    onStack[v] = true;
    marked[v] = true;
    for (DirectedEdgeX e : G.adj(v)) {
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
        cycle = new Stack<DirectedEdgeX>();
        DirectedEdgeX f = e;
        while (f.from() != w) {
          cycle.push(f);
          f = edgeTo[f.from()];
        }
        cycle.push(f);
        return;
      }
    }
    onStack[v] = false;
  }

  public boolean hasCycle() { return cycle != null; }

  /**
   * Returns a directed cycle if the edge-weighted digraph has a directed cycle,
   * and {@code null} otherwise.
   * @return a directed cycle (as an iterable) if the edge-weighted digraph
   *    has a directed cycle, and {@code null} otherwise
   */
  public Iterable<DirectedEdgeX> cycle() { return cycle; }

  private boolean check() {
    // certify that digraph is either acyclic or has a directed cycle
    // edge-weighted digraph is cyclic
    if (hasCycle()) {
      // verify cycle
      DirectedEdgeX first = null, last = null;
      for (DirectedEdgeX e : cycle()) {
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
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(V);
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
      G.addEdge(new DirectedEdgeX(v, w, weight));
    }

    // add F extra edges
    for (int i = 0; i < F; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      double weight = StdRandom.uniform(0.0, 1.0);
      G.addEdge(new DirectedEdgeX(v, w, weight));
    }

    StdOut.println(G);

    // find a directed cycle
    EdgeWeightedDirectedCycleX finder = new EdgeWeightedDirectedCycleX(G);
    if (finder.hasCycle()) {
      StdOut.print("Cycle: ");
      for (DirectedEdgeX e : finder.cycle()) {
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
