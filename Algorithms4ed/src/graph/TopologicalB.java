package graph;

import edu.princeton.cs.algs4.StdOut;

// from text p581

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
  Computational Biology*/

public class TopologicalB {
  private Iterable<Integer> order; // topological order
  
  public TopologicalB(DigraphX G) {
    DirectedCycleX cyclefinder = new DirectedCycleX(G);
    if (!cyclefinder.hasCycle()) {
      DepthFirstOrderX dfs = new DepthFirstOrderX(G);
      order = dfs.reversePost();
    }
  }
  
  public Iterable<Integer> order() { return order; }
  
  public boolean isDAG() { return order != null; }

  public static void main(String[] args) {
    String filename  = args[0];
    String delimiter = args[1];
    SymbolDigraphX sg = new SymbolDigraphX(filename, delimiter);
    TopologicalB topological = new TopologicalB(sg.digraph());
    for (int v : topological.order()) {
      StdOut.println(sg.nameOf(v));
    }
  }

}
