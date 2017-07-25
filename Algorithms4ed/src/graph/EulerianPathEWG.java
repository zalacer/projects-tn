package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Seq;
import ds.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class EulerianPathEWG {
  private Stack<Integer> path = null;   // Eulerian path; null if no such path

  // an undirected edge, with a field to indicate whether the edge has already been used
  private static class Edge {
    private final int v;
    private final int w;
    private boolean isUsed;

    public Edge(int v, int w) {
      this.v = v;
      this.w = w;
      isUsed = false;
    }

    // returns the other vertex of the edge
    public int other(int vertex) {
      if      (vertex == v) return w;
      else if (vertex == w) return v;
      else throw new IllegalArgumentException("Illegal endpoint");
    }
  }

  public EulerianPathEWG(EdgeWeightedGraphX G) {
    // find vertex from which to start potential Eulerian path:
    // a vertex v with odd degree(v) if it exits;
    // otherwise a vertex with degree(v) > 0
    int oddDegreeVertices = 0;
    int s = nonIsolatedVertex(G);
    for (int v = 0; v < G.V(); v++) {
      if (G.degree(v) % 2 != 0) {
        oddDegreeVertices++;
        s = v;
      }
    }

    // graph can't have an Eulerian path
    // (this condition is needed for correctness)
    if (oddDegreeVertices > 2) return;

    // special case for graph with zero edges (has a degenerate Eulerian path)
    if (s == -1) s = 0;

    // create local view of adjacency lists, to iterate one vertex at a time
    // the helper Edge data type is used to avoid exploring both copies of an edge v-w
    Queue<Edge>[] adj = ofDim(Queue.class,G.V());
    for (int v = 0; v < G.V(); v++)
      adj[v] = new Queue<Edge>();

    for (int v = 0; v < G.V(); v++) {
      int selfLoops = 0;
      for (EdgeX x : G.adj(v)) {
        // careful with self loops
        int w = x.other(v);
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
    Stack<Integer> stack = new Stack<Integer>();
    stack.push(s);

    // greedily search through edges in iterative DFS style
    path = new Stack<Integer>();
    while (!stack.isEmpty()) {
      int v = stack.pop();
      while (!adj[v].isEmpty()) {
        Edge edge = adj[v].dequeue();
        if (edge.isUsed) continue;
        edge.isUsed = true;
        stack.push(v);
        v = edge.other(v);
      }
      // push vertex with no more leaving edges to path
      path.push(v);
    }

    // check if all edges are used
    if (path.size() != G.E()+1) {
      System.err.println("path doesn't use all edges");
      path = null;
    }
    
    assert certifySolution(G);
  }

  public Iterable<Integer> path() { return path; }
  
  public Seq<Integer> spath() { return new Seq<>(path); }


  public boolean hasEulerianPath() { return path != null; }
  
  public int[] pathToArray() { return (int[])unbox(toArray(path().iterator())); }
  
  public int[] trail() {
    if (!hasEulerianPath()) return null;
    Integer[] p = toArray(path().iterator());
    int[] r = new int[p.length];
    for (int i = 0; i < p.length; i++) r[p[i]] = i;   
    return r;
  }
  
  public int[][] trailAndInvertedIndex() {
    if (!hasEulerianPath()) return null;
    int[] p = pathToArray();
    int[] r = new int[p.length];
    for (int i = 0; i < p.length; i++) r[p[i]] = i;   
    return new int[][]{p,r};
  }


  // returns any non-isolated vertex; -1 if no such vertex
  private static int nonIsolatedVertex(EdgeWeightedGraphX G) {
    for (int v = 0; v < G.V(); v++)
      if (G.degree(v) > 0)
        return v;
    return -1;
  }

  // Determines whether a graph has an Eulerian path using necessary
  // and sufficient conditions (without computing the path itself):
  //    - degree(v) is even for every vertex, except for possibly two
  //    - the graph is connected (ignoring isolated vertices)
  // This method is solely for unit testing.
  private static boolean hasEulerianPath(EdgeWeightedGraphX G) {
    if (G.E() == 0) return true;
    // Condition 1: degree(v) is even except for possibly two
    int oddDegreeVertices = 0;
    for (int v = 0; v < G.V(); v++)
      if (G.degree(v) % 2 != 0)
        oddDegreeVertices++;
    if (oddDegreeVertices > 2) return false;
    // Condition 2: graph is connected, ignoring isolated vertices
    int s = nonIsolatedVertex(G);
    BreadthFirstPathsEWG bfs = new BreadthFirstPathsEWG(G, s);
    for (int v = 0; v < G.V(); v++)
      if (G.degree(v) > 0 && !bfs.hasPathTo(v))
        return false;
    return true;
  }

  private boolean certifySolution(EdgeWeightedGraphX G) {
    // internal consistency check
    if (hasEulerianPath() == (path() == null)) {
      System.err.println("no eularian path");
      return false;
    }
    // hasEulerianPath() returns correct value
    if (hasEulerianPath() != hasEulerianPath(G)) {
      System.err.println("incorrect eularian path");
      System.out.println("hasEulerianPath()="+hasEulerianPath());
      System.out.println("hasEulerianPath(G)="+hasEulerianPath(G));
      return false;
    }
    // nothing else to check if no Eulerian path
    if (path == null) return true;
    // check that path() uses correct number of edges
    if (path.size() != G.E() + 1) {
      System.err.println("path has incorrect number of edges ("+path.size()+")");
      return false;
    }
    // check that path() is a path in G -  todo
    return true;
  }

  private static void unitTest(EdgeWeightedGraphX G, String description) {
    StdOut.println(description);
    StdOut.println("-------------------------------------");
    StdOut.print(G);

    EulerianPathEWG euler = new EulerianPathEWG(G);

    StdOut.print("Eulerian path:  ");
    if (euler.hasEulerianPath()) {
      for (int v : euler.path()) {
        StdOut.print(v + " ");
      }
      StdOut.println();
    }
    else {
      StdOut.println("none");
    }
    StdOut.println();
  }

  public static void main(String[] args) {
//    int V = Integer.parseInt(args[0]);
//    int E = Integer.parseInt(args[1]);
    int V = 6, E = 5;

    // Eulerian cycle
    EdgeWeightedGraphX G1 = EdgeWeightedGraphGeneratorX.eulerianCycle(V, E);
    unitTest(G1, "Eulerian cycle");

    // Eulerian path
    EdgeWeightedGraphX G2 = EdgeWeightedGraphGeneratorX.eulerianPath(V, E);
    unitTest(G2, "Eulerian path");

    // add one random edge
    EdgeWeightedGraphX G3 = new EdgeWeightedGraphX(G2);
    G3.addEdge(StdRandom.uniform(V), StdRandom.uniform(V),1);
    unitTest(G3, "one random edge added to Eulerian path");

    // self loop
    EdgeWeightedGraphX G4 = new EdgeWeightedGraphX(V);
    int v4 = StdRandom.uniform(V);
    G4.addEdge(v4, v4, 1);
    unitTest(G4, "single self loop");

    // single edge
    EdgeWeightedGraphX G5 = new EdgeWeightedGraphX(V);
    G5.addEdge(StdRandom.uniform(V), StdRandom.uniform(V), 1);
    unitTest(G5, "single edge");

    // empty graph
    EdgeWeightedGraphX G6 = new EdgeWeightedGraphX(V);
    unitTest(G6, "empty graph");

    // random graph
    EdgeWeightedGraphX G7 = EdgeWeightedGraphGeneratorX.simple(V, E);
    unitTest(G7, "simple graph");
    
    // path graph
    EdgeWeightedGraphX G8 = EdgeWeightedGraphGeneratorX.path(V);
    unitTest(G8, "path graph");
  }
}
