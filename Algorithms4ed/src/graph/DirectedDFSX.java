package graph;

import ds.BagX;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/DirectedDFS.java

public class DirectedDFSX {
  private boolean[] marked;  // marked[v] = true if v is reachable from source (or sources)
  private int count;         // number of vertices reachable from s

  public DirectedDFSX(DigraphX G, int s) {
    marked = new boolean[G.V()];
    validateVertex(s);
    dfs(G, s);
  }
  
  public DirectedDFSX(EuclidianDigraph G, int s) {
    marked = new boolean[G.V()];
    validateVertex(s);
    dfs(G, s);
  }
  
  public DirectedDFSX(EdgeWeightedDigraphX G, int s) {
    marked = new boolean[G.V()];
    validateVertex(s);
    dfs(G, s);
  }
  
  public DirectedDFSX(EdgeWeightedDigraphI G, int s) {
    marked = new boolean[G.V()];
    validateVertex(s);
    dfs(G, s);
  }
  
  public DirectedDFSX(EuclidianEdgeWeightedDigraph G, int s) {
    marked = new boolean[G.V()];
    validateVertex(s);
    dfs(G, s);
  } 

  public DirectedDFSX(DigraphX G, Iterable<Integer> sources) {
    marked = new boolean[G.V()];
    validateVertices(sources);
    for (int v : sources) if (!marked[v]) dfs(G, v);
  }
  
  public DirectedDFSX(EuclidianDigraph G, Iterable<Integer> sources) {
    marked = new boolean[G.V()];
    validateVertices(sources);
    for (int v : sources) if (!marked[v]) dfs(G, v);
  }
  
  public DirectedDFSX(EdgeWeightedDigraphX G, Iterable<Integer> sources) {
    marked = new boolean[G.V()];
    validateVertices(sources);
    for (int v : sources) if (!marked[v]) dfs(G, v);
  }
  
  public DirectedDFSX(EuclidianEdgeWeightedDigraph G, Iterable<Integer> sources) {
    marked = new boolean[G.V()];
    validateVertices(sources);
    for (int v : sources) if (!marked[v]) dfs(G, v);
  }

  private void dfs(DigraphX G, int v) { 
    count++;
    marked[v] = true;
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
  }
  
  private void dfs(EuclidianDigraph G, int v) { 
    count++;
    marked[v] = true;
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
  }
  
  private void dfs(EdgeWeightedDigraphX G, int u) { 
    count++;
    marked[u] = true;
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }
  
  
  private void dfs(EdgeWeightedDigraphI G, int u) { 
    count++;
    marked[u] = true;
    for (DirectedEdgeI e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }
  
  private void dfs(EuclidianEdgeWeightedDigraph G, int u) { 
    count++;
    marked[u] = true;
    for (DirectedEdgeX e : G.adj(u)) {
      int v = e.v();
      if (!marked[v]) dfs(G, v);
    }
  }

  public boolean marked(int v) { validateVertex(v); return marked[v]; }

  public int count() { 
    // return number of vertices connected to the source vertex or vertices
    return count; 
  }

  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex " +v+" is out of bounds");
  }

  private void validateVertices(Iterable<Integer> vertices) {
    if (vertices == null) {
      throw new IllegalArgumentException("argument is null");
    }
    int V = marked.length;
    for (int v : vertices) {
      if (v < 0 || v >= V) {
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
      }
    }
  }

  public static void main(String[] args) {
    //tinyDGfromEWD.txt, tinyDG.txt, tinyEWD.txt
    // read in digraph from command-line argument
    In in = new In(args[0]);
//    DigraphX G = new DigraphX(in);
    EdgeWeightedDigraphX G = new EdgeWeightedDigraphX(in);
    
    // read in sources from command-line arguments
    BagX<Integer> sources = new BagX<Integer>();
    for (int i = 1; i < args.length; i++) {
      int s = Integer.parseInt(args[i]);
      sources.add(s);
    }

    // multiple-source reachability
    DirectedDFSX dfs = new DirectedDFSX(G, sources);

    // print out vertices reachable from sources
    for (int v = 0; v < G.V(); v++) {
      if (dfs.marked(v)) StdOut.print(v + " ");
    }
    StdOut.println();
    System.out.println("count="+dfs.count());
  }

}
