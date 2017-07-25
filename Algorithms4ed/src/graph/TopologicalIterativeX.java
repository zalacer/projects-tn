package graph;

import ds.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

// from http://algs4.cs.princeton.edu/42digraph/TopologicalX.java

public class TopologicalIterativeX {
  private Queue<Integer> order;     // vertices in topological order
  private int[] ranks;              // ranks[v] = order where vertex v appers in order

  public TopologicalIterativeX(DigraphX G) {
    int[] indegree = new int[G.V()];
    for (int v = 0; v < G.V(); v++) indegree[v] = G.indegree(v);
    ranks = new int[G.V()]; 
    order = new Queue<Integer>();
    int count = 0;
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) if (indegree[v] == 0) queue.enqueue(v);
    while (!queue.isEmpty()) {
      int v = queue.dequeue();
      order.enqueue(v);
      ranks[v] = count++;
      for (int w : G.adj(v)) {
        indegree[w]--;
        if (indegree[w] == 0) queue.enqueue(w);
      }
    }
    if (count != G.V())  order = null;
    assert check(G);
  }

  public TopologicalIterativeX(EdgeWeightedDigraphX G) {
    int[] indegree = new int[G.V()];
    for (int v = 0; v < G.V(); v++) indegree[v] = G.indegree(v);
    ranks = new int[G.V()]; 
    order = new Queue<Integer>();
    int count = 0;
    Queue<Integer> queue = new Queue<Integer>();
    for (int v = 0; v < G.V(); v++) if (indegree[v] == 0) queue.enqueue(v);
    while (!queue.isEmpty()) {
      int v = queue.dequeue();
      order.enqueue(v);
      ranks[v] = count++;
      for (DirectedEdgeX e : G.adj(v)) {
        int w = e.to();
        indegree[w]--;
        if (indegree[w] == 0) queue.enqueue(w);
      }
    }
    if (count != G.V()) order = null;
    assert check(G);
  }

  public Iterable<Integer> order() { return order; }

  public boolean hasOrder() { return order != null; }

  public int rank(int v) { validateVertex(v); return hasOrder() ? ranks[v] : -1; }

  private boolean check(DigraphX G) {
    // certify that digraph is acyclic
    // digraph is acyclic
    if (hasOrder()) {
      // check that ranks are a permutation of 0 to V-1
      boolean[] found = new boolean[G.V()];
      for (int i = 0; i < G.V(); i++) {
        found[rank(i)] = true;
      }
      for (int i = 0; i < G.V(); i++) {
        if (!found[i]) {
          System.err.println("No vertex with rank " + i);
          return false;
        }
      }
      // check that ranks provide a valid topological order
      for (int v = 0; v < G.V(); v++) {
        for (int w : G.adj(v)) {
          if (rank(v) > rank(w)) {
            System.err.printf("%d-%d: rank(%d) = %d, rank(%d) = %d\n",
                v, w, v, rank(v), w, rank(w));
            return false;
          }
        }
      }
      // check that order() is consistent with rank()
      int r = 0;
      for (int v : order()) {
        if (rank(v) != r) {
          System.err.println("order() and rank() inconsistent");
          return false;
        }
        r++;
      }
    }
    return true;
  }

  private boolean check(EdgeWeightedDigraphX G) {
    // certify that digraph is acyclic
    // digraph is acyclic
    if (hasOrder()) {
      // check that ranks are a permutation of 0 to V-1
      boolean[] found = new boolean[G.V()];
      for (int i = 0; i < G.V(); i++) {
        found[rank(i)] = true;
      }
      for (int i = 0; i < G.V(); i++) {
        if (!found[i]) {
          System.err.println("No vertex with rank " + i);
          return false;
        }
      }
      // check that ranks provide a valid topological order
      for (int v = 0; v < G.V(); v++) {
        for (DirectedEdgeX e : G.adj(v)) {
          int w = e.to();
          if (rank(v) > rank(w)) {
            System.err.printf("%d-%d: rank(%d) = %d, rank(%d) = %d\n",
                v, w, v, rank(v), w, rank(w));
            return false;
          }
        }
      }
      // check that order() is consistent with rank()
      int r = 0;
      for (int v : order()) {
        if (rank(v) != r) {
          System.err.println("order() and rank() inconsistent");
          return false;
        }
        r++;
      }
    }
    return true;
  }

  private void validateVertex(int v) {
    int V = ranks.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {

    // create random DAG with V vertices and E edges; then add F random edges
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    int F = Integer.parseInt(args[2]);

    DigraphX G1 = DigraphGeneratorX.dag(V, E);

    // corresponding edge-weighted digraph
    EdgeWeightedDigraphX G2 = new EdgeWeightedDigraphX(V);
    for (int v = 0; v < G1.V(); v++)
      for (int w : G1.adj(v))
        G2.addEdge(new DirectedEdgeX(v, w, 0.0));

    // add F extra edges
    for (int i = 0; i < F; i++) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      G1.addEdge(v, w);
      G2.addEdge(new DirectedEdgeX(v, w, 0.0));
    }

    StdOut.println(G1);
    StdOut.println();
    StdOut.println(G2);

    // find a directed cycle
    TopologicalIterativeX topological1 = new TopologicalIterativeX(G1);
    if (!topological1.hasOrder()) {
      StdOut.println("Not a DAG");
    }

    // or give topologial sort
    else {
      StdOut.print("Topological order: ");
      for (int v : topological1.order()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }

    // find a directed cycle
    TopologicalIterativeX topological2 = new TopologicalIterativeX(G2);
    if (!topological2.hasOrder()) {
      StdOut.println("Not a DAG");
    }

    // or give topologial sort
    else {
      StdOut.print("Topological order: ");
      for (int v : topological2.order()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
  }

}
