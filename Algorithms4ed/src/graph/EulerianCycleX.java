package graph;

import static v.ArrayUtils.*;

import java.util.Iterator;

import ds.Queue;
import ds.Stack;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class EulerianCycleX {
  private Stack<Integer> cycle;
  private String cause = "";
  
  private static class Edge {
    // an undirected edge, with a field to indicate whether the edge has already been used
    private final int v;
    private final int w;
    private boolean isUsed;

    public Edge(int v, int w) {
      this.v = v;
      this.w = w;
      isUsed = false;
    }

    public int other(int vertex) {
      if      (vertex == v) return w;
      else if (vertex == w) return v;
      else throw new IllegalArgumentException("Illegal endpoint");
    }
  }

  public EulerianCycleX(GraphX G) {
    // computes an Eulerian cycle in the specified graph, if one exists
    // must have at least one edge
    if (G == null) { cause = "the graph is null"; return; }
    if (G.E() == 0) { cause = "the graph has no edges"; return; }
    int V = G.V(), E = G.E();
    // necessary condition: all vertices have even degree
    // (this test is needed or it might find an Eulerian path instead of cycle)
    for (int v = 0; v < V; v++) if (G.degree(v) % 2 != 0) {
      cause = "a vertex has an odd degree";
      return;
    }

    // create local view of adjacency lists, to iterate one vertex at a time
    // the helper Edge data type is used to avoid exploring both copies of an edge v-w
    Queue<Edge>[] adj = ofDim(Queue.class, V);
    for (int v = 0; v < V; v++) adj[v] = new Queue<Edge>();

    for (int v = 0; v < V; v++) {
      int selfLoops = 0;
      for (int w : G.adj(v)) {
        // careful with self loops
        if (v == w) {
          if (selfLoops % 2 == 0) {
            Edge e = new Edge(v, w);
            adj[v].enqueue(e);
            adj[w].enqueue(e);
          }
          selfLoops++;
        }
        else if (v < w) {
          Edge e = new Edge(v, w);
          adj[v].enqueue(e);
          adj[w].enqueue(e);
        }
      }
    }

    // initialize stack with any non-isolated vertex
    int s = nonIsolatedVertex(G);
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);

    // greedily search through edges in iterative DFS style
    cycle = new Stack<Integer>();
    while (!stack.isEmpty()) {
      int v = stack.pop();
      while (!adj[v].isEmpty()) {
        Edge edge = adj[v].dequeue();
        if (edge.isUsed) continue;
        edge.isUsed = true;
        stack.push(v);
        v = edge.other(v);
      }
      // push vertex with no more leaving edges to cycle
      cycle.push(v);
    }

    // check if all edges are used
    if (cycle.size() != E + 1) {
      cycle = null;
      cause = "cycle found but didn't use all edges";
    }
    assert certifySolution(G);
  }

  public Iterable<Integer> cycle() { return cycle; }
  
  public void showCycle() {
    Iterator<Integer> it = cycle().iterator();
    while (it.hasNext()) System.out.print(it.next()+" "); System.out.println();
  }
  
  public String cycleToString() {
    StringBuilder sb = new StringBuilder();
    Iterator<Integer> it = cycle().iterator();
    while (it.hasNext()) sb.append(it.next()+" ");
    return sb.toString();
  }

  public boolean hasEulerianCycle() { return cycle != null; }

  private static int nonIsolatedVertex(GraphX G) {
    for (int v = 0; v < G.V(); v++) if (G.degree(v) > 0) return v;
    return -1;
  }

  // Determines whether a graph has an Eulerian cycle using necessary
  // and sufficient conditions (without computing the cycle itself):
  //    - at least one edge
  //    - degree(v) is even for every vertex v
  //    - the graph is connected (ignoring isolated vertices)
  private static boolean hasEulerianCycle(GraphX G) {

    // Condition 0: at least 1 edge
    if (G.E() == 0) return false;

    // Condition 1: degree(v) is even for every vertex
    for (int v = 0; v < G.V(); v++) if (G.degree(v) % 2 != 0) return false;

    // Condition 2: graph is connected, ignoring isolated vertices
    int s = nonIsolatedVertex(G);
    BreadthFirstPathsX bfs = new BreadthFirstPathsX(G, s);
    for (int v = 0; v < G.V(); v++)
      if (G.degree(v) > 0 && !bfs.hasPathTo(v))
        return false;

    return true;
  }
  
  public boolean hasCause() { return cause.length() > 0; }
  
  public String cause() { return cause; }

  // check that solution is correct
  private boolean certifySolution(GraphX G) {

    // internal consistency check
    if (hasEulerianCycle() == (cycle() == null)) return false;

    // hashEulerianCycle() returns correct value
    if (hasEulerianCycle() != hasEulerianCycle(G)) return false;

    // nothing else to check if no Eulerian cycle
    if (cycle == null) return true;

    // check that cycle() uses correct number of edges
    if (cycle.size() != G.E() + 1) return false;

    // check that cycle() is a cycle of G
    // to do

    // check that first and last vertices in cycle() are the same
    int first = -1, last = -1;
    for (int v : cycle()) {
      if (first == -1) first = v;
      last = v;
    }
    if (first != last) return false;

    return true;
  }

  private static void unitTest(GraphX G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);

    EulerianCycleX euler = new EulerianCycleX(G);

    StdOut.print("Eulerian cycle: ");
    if (euler.hasEulerianCycle()) {
      for (int v : euler.cycle()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
    else {
      StdOut.println("none");
    }
    StdOut.println();
  }


  /**
   * Unit tests the {@code EulerianCycle} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);

    // Eulerian cycle
    GraphX G1 = GraphGeneratorX.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");

    // Eulerian path
    GraphX G2 = GraphGeneratorX.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");

    // empty graph
    GraphX G3 = new GraphX(V);
    unitTest(G3, "empty graph");

    // self loop
    GraphX G4 = new GraphX(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4);
    unitTest(G4, "single self loop");

    // union of two disjoint cycles
    GraphX H1 = GraphGeneratorX.eulerianCycle(V/2, E/2);
    GraphX H2 = GraphGeneratorX.eulerianCycle(V - V/2, E - E/2);
    int[] perm = new int[V];
    for (int i = 0; i < V; i++)
      perm[i] = i;
    StdRandom.shuffle(perm);
    GraphX G5 = new GraphX(V);
    for (int v = 0; v < H1.V(); v++)
      for (int w : H1.adj(v))
        G5.addEdge(perm[v], perm[w]);
    for (int v = 0; v < H2.V(); v++)
      for (int w : H2.adj(v))
        G5.addEdge(perm[V/2 + v], perm[V/2 + w]);
    unitTest(G5, "Union of two disjoint cycles");

    // random digraph
    GraphX G6 = GraphGeneratorX.simple(V, E);
    unitTest(G6, "simple graph");
  }
}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/

