package graph;

import static v.ArrayUtils.*;

import ds.BagX;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/Topological.java

/* % java Topological jobs.txt "/"
  Calculus
  Linear Algebra
  Introduction to CS
  Advanced Programming
  Algorithms
  Theoretical CS
  Artificial Intelligence
  Robotics
  Machine Learning
  Neural Networks
  Databases
  Scientific Computing
  Computational Biology */

public class TopologicalX {
  private Iterable<Integer> order;  // topological order
  private int[] rank;               // rank[v] = position of vertex v in topological order
  private BagX<Integer>[] adj;

  public TopologicalX(DigraphX G) {
    if (G == null) throw new IllegalArgumentException("TopologicalX: DigraphX is null");
    DirectedCycleX finder = new DirectedCycleX(G);
    if (finder.hasCycle()) throw new IllegalArgumentException("TopologicalX: DigraphX has cycle");
    adj = G.adj();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G);
    order = dfs.reversePost();
    rank = new int[G.V()];
    int i = 0;
    for (int v : order) rank[v] = i++;
  }
  
  public TopologicalX(EuclidianDigraph G) {
    if (G == null) throw new IllegalArgumentException("TopologicalX: EuclidianDigraph is null");
    DirectedCycleX finder = new DirectedCycleX(G);
    if (finder.hasCycle()) throw new IllegalArgumentException("TopologicalX: EuclidianDigraph has cycle");
    adj = G.adj().to();
    DepthFirstOrderX dfs = new DepthFirstOrderX(G);
    order = dfs.reversePost();
    rank = new int[G.V()];
    int i = 0;
    for (int v : order) rank[v] = i++;
  }

  public TopologicalX(EdgeWeightedDigraphX G) {
    if (G == null) throw new IllegalArgumentException("TopologicalX: EdgeWeightedDigraphX is null");
    DirectedCycleX finder = new DirectedCycleX(G);
    if (!finder.hasCycle()) {
      DepthFirstOrderX dfs = new DepthFirstOrderX(G);
      order = dfs.reversePost();
      rank = new int[G.V()];
      int i = 0;
      for (int v : order) rank[v] = i++;
    }
  }
  
  public TopologicalX(EuclidianEdgeWeightedDigraph G) {
    DirectedCycleX finder = new DirectedCycleX(G);
    if (!finder.hasCycle()) {
      DepthFirstOrderX dfs = new DepthFirstOrderX(G);
      order = dfs.reversePost();
      rank = new int[G.V()];
      int i = 0;
      for (int v : order) rank[v] = i++;
    }
  }

  public Iterable<Integer> order() { return order; }
  
  public boolean isHamiltonianPath() {
    // ex4224
    if (!hasOrder()) return false;
    Integer[] ord = toArray(order().iterator());
    for (int i = 0; i < ord.length-1; i++)
      if (!adj[ord[i]].contains(ord[i+1])) return false;   
    return true;  
  }
  
  public boolean isOrderUnique() { 
    // ex4225
    return isHamiltonianPath(); 
  }
  
  public boolean hasOrder() { return order != null; }

  public boolean isDAG() { return hasOrder(); }

  public int rank(int v) {
    validateVertex(v);
    if (hasOrder()) return rank[v]; else return -1;
  }

  private void validateVertex(int v) {
    int V = rank.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
    String filename  = args[0];
    String delimiter = args[1];
    SymbolDigraphX sg = new SymbolDigraphX(filename, delimiter);
    TopologicalX topological = new TopologicalX(sg.digraph());
    for (int v : topological.order()) {
      StdOut.println(sg.nameOf(v));
    }
  }

}
