package graph;

import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/DirectedCycle.java

public class DirectedCycleEx4205 {
  private boolean[] marked;        // marked[v] = has vertex v been marked?
  private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
  private boolean[] onStack;       // onStack[v] = is vertex on the stack?
  private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)

  public DirectedCycleEx4205(DigraphEx4205 G) {
    marked  = new boolean[G.V()];
    onStack = new boolean[G.V()];
    edgeTo  = new int[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v] && cycle == null) dfs(G, v);
  }

  // check that algorithm computes either the topological order or finds a directed cycle
  private void dfs(DigraphEx4205 G, int v) {
    onStack[v] = true;
    marked[v] = true;
    for (int w : G.adj(v)) {
      // short circuit if directed cycle found
      if (cycle != null) return;
      // found new vertex, so recur
      else if (!marked[w]) {
        edgeTo[w] = v;
        dfs(G, w);
      }
      // trace back directed cycle
      else if (onStack[w]) {
        cycle = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x]) {
          cycle.push(x);
        }
        cycle.push(w);
        cycle.push(v);
        assert check();
      }
    }
    onStack[v] = false;
  }

  public boolean hasCycle() { return cycle != null; }

  public Iterable<Integer> cycle() { return cycle; }
  
  public Stack<Integer> getCycle() { return cycle; }

  private boolean check() {
    if (hasCycle()) {
      // verify cycle
      int first = -1, last = -1;
      for (int v : cycle()) {
        if (first == -1) first = v;
        last = v;
      }
      if (first != last) {
        System.err.printf("cycle begins with %d and ends with %d\n", first, last);
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    DigraphEx4205 G = new DigraphEx4205(in);

    DirectedCycleEx4205 finder = new DirectedCycleEx4205(G);
    if (finder.hasCycle()) {
      StdOut.print("Directed cycle: ");
      for (int v : finder.cycle()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
      System.out.println(finder.getCycle());
    }

    else {
      StdOut.println("No directed cycle");
    }
    StdOut.println();
  }

}
