package graph;

import ds.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import pq.MinPQ;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/GraphGenerator.java

public class GraphGeneratorS {
  private static final class Edge implements Comparable<Edge> {
    private int v;
    private int w;

    private Edge(int v, int w) {
      if (v < w) { this.v = v; this.w = w; return; }
      this.v = w; this.w = v;
    }

    public int compareTo(Edge that) {
      if (this.v < that.v) return -1;
      if (this.v > that.v) return +1;
      if (this.w < that.w) return -1;
      if (this.w > that.w) return +1;
      return 0;
    }
  }

  private GraphGeneratorS() { }

  public static GraphS simple(int V, int E) {
    // return a random simple graph containing V vertices and E edge
    if (E > (long) V*(V-1)/2) throw new IllegalArgumentException("Too many edges");
    if (E < 0)                throw new IllegalArgumentException("Too few edges");
    GraphS G = new GraphS(V);
    SET<Edge> set = new SET<Edge>();
    while (G.E() < E) {
      int v = StdRandom.uniform(V);
      int w = StdRandom.uniform(V);
      Edge e = new Edge(v, w);
      if ((v != w) && !set.contains(e)) { set.add(e); G.addEdge(v, w); }
    }
    return G;
  }

  public static GraphS simple(int V, double p) {
    // return a random simple graph on V vertices, with an  edge between any two 
    // vertices with probability p (Erdos-Renyi random graph model)
    if (p < 0.0 || p > 1.0)
      throw new IllegalArgumentException("Probability must be between 0 and 1");
    GraphS G = new GraphS(V);
    for (int v = 0; v < V; v++)
      for (int w = v+1; w < V; w++)
        if (StdRandom.bernoulli(p)) G.addEdge(v, w);
    return G;
  }

  public static GraphS complete(int V) {
    // return the complete graph on V vertices
    return simple(V, 1.0);
  }

  public static GraphS completeBipartite(int V1, int V2) {
    // return a complete bipartite graph on V1 and V2 vertices where V1 is the
    // number of vertices in one partition and V2 is the number of vertices in
    // the other
    return bipartite(V1, V2, V1*V2);
  }

  public static GraphS bipartite(int V1, int V2, int E) {
    // return a random bipartite simple graph on V1 and V2 vertices where V1 is the
    // number of vertices in one partition and V2 is the number of vertices in
    // the other and E is the total number of edges
    if (E > (long) V1*V2) throw new IllegalArgumentException("Too many edges");
    if (E < 0)            throw new IllegalArgumentException("Too few edges");
    GraphS G = new GraphS(V1 + V2);
    int[] vertices = new int[V1 + V2];
    for (int i = 0; i < V1 + V2; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    SET<Edge> set = new SET<Edge>();
    while (G.E() < E) {
      int i = StdRandom.uniform(V1);
      int j = V1 + StdRandom.uniform(V2);
      Edge e = new Edge(vertices[i], vertices[j]);
      if (!set.contains(e)) {
        set.add(e);
        G.addEdge(vertices[i], vertices[j]);
      }
    }
    return G;
  }
  
  public static GraphS bipartite(int V1, int V2, double p) {
    // return a random bipartite simple graph on V1 and V2 vertices where V1 is the
    // number of vertices in one partition and V2 is the number of vertices in
    // the other and containing each possible edge with probability p
    if (p < 0.0 || p > 1.0)
      throw new IllegalArgumentException("Probability must be between 0 and 1");
    int[] vertices = new int[V1 + V2];
    for (int i = 0; i < V1 + V2; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    GraphS G = new GraphS(V1 + V2);
    for (int i = 0; i < V1; i++)
      for (int j = 0; j < V2; j++)
        if (StdRandom.bernoulli(p))
          G.addEdge(vertices[i], vertices[V1+j]);
    return G;
  }

  public static GraphS path(int V) {
    // return a path graph on V vertices
    GraphS G = new GraphS(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    for (int i = 0; i < V-1; i++) G.addEdge(vertices[i], vertices[i+1]);
    return G;
  }
  
  public static GraphS binaryTree(int V) {
    // return a complete binary tree graph on V vertices
    GraphS G = new GraphS(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    for (int i = 1; i < V; i++) G.addEdge(vertices[i], vertices[(i-1)/2]);
    return G;
  }

  public static GraphS cycle(int V) {
    // return a cycle graph on V vertices
    GraphS G = new GraphS(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    for (int i = 0; i < V-1; i++) G.addEdge(vertices[i], vertices[i+1]);
    G.addEdge(vertices[V-1], vertices[0]);
    return G;
  }

  public static GraphS eulerianCycle(int V, int E) {
    // return an Eulerian cycle graph on V vertices
    if (E <= 0)
      throw new IllegalArgumentException("An Eulerian cycle must have at least one edge");
    if (V <= 0)
      throw new IllegalArgumentException("An Eulerian cycle must have at least one vertex");
    GraphS G = new GraphS(V);
    int[] vertices = new int[E];
    for (int i = 0; i < E; i++) vertices[i] = StdRandom.uniform(V);
    for (int i = 0; i < E-1; i++) G.addEdge(vertices[i], vertices[i+1]);
    G.addEdge(vertices[E-1], vertices[0]);
    return G;
  }

  public static GraphS eulerianPath(int V, int E) {
    // return an Eulerian path graph on V vertices
    if (E < 0)
      throw new IllegalArgumentException("negative number of edges");
    if (V <= 0)
      throw new IllegalArgumentException("An Eulerian path must have at least one vertex");
    GraphS G = new GraphS(V);
    int[] vertices = new int[E+1];
    for (int i = 0; i < E+1; i++) vertices[i] = StdRandom.uniform(V);
    for (int i = 0; i < E; i++) G.addEdge(vertices[i], vertices[i+1]);
    return G;
  }

  public static GraphS wheel(int V) {
    // return a wheel graph on V vertices
    if (V <= 1) throw new IllegalArgumentException("Number of vertices must be at least 2");
    GraphS G = new GraphS(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    // simple cycle on V-1 vertices
    for (int i = 1; i < V-1; i++) G.addEdge(vertices[i], vertices[i+1]);
    G.addEdge(vertices[V-1], vertices[1]);
    // connect vertices[0] to every vertex on cycle
    for (int i = 1; i < V; i++) G.addEdge(vertices[0], vertices[i]);
    return G;
  }

  public static GraphS star(int V) {
    // return an star graph on V vertices
    if (V <= 0) throw new IllegalArgumentException("Number of vertices must be at least 1");
    GraphS G = new GraphS(V);
    int[] vertices = new int[V];
    for (int i = 0; i < V; i++) vertices[i] = i;
    StdRandom.shuffle(vertices);
    // connect vertices[0] to each other vertex
    for (int i = 1; i < V; i++) G.addEdge(vertices[0], vertices[i]);
    return G;
  }

  /**
   * Returns a uniformly random {@code k}-regular graph on {@code V} vertices
   * (not necessarily simple). The graph is simple with probability only about e^(-k^2/4),
   * which is tiny when k = 14.
   *
   * @param V the number of vertices in the graph
   * @param k degree of each vertex
   * @return a uniformly random {@code k}-regular graph on {@code V} vertices.
   */
  public static GraphS regular(int V, int k) {
    // return a uniformly random k-regular graph on V vertices
    if (V*k % 2 != 0) throw new IllegalArgumentException("Number of vertices * k must be even");
    GraphS G = new GraphS(V);
    // create k copies of each vertex
    int[] vertices = new int[V*k];
    for (int v = 0; v < V; v++) for (int j = 0; j < k; j++) vertices[v + V*j] = v;
    // pick a random perfect matching
    StdRandom.shuffle(vertices);
    for (int i = 0; i < V*k/2; i++) G.addEdge(vertices[2*i], vertices[2*i + 1]);
    return G;
  }

  // http://www.proofwiki.org/wiki/Labeled_Tree_from_Prüfer_Sequence
  /**
   * Returns a uniformly random tree on {@code V} vertices.
   * This algorithm uses a Prufer sequence and takes time proportional to <em>V log V</em>.
   * @param V the number of vertices in the tree
   * @return a uniformly random tree on {@code V} vertices
   */
  public static GraphS tree(int V) {
    // return a uniformly random tree on V vertices
    // http://www.proofwiki.org/wiki/Labeled_Tree_from_Prüfer_Sequence
    // http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.36.6484&rep=rep1&type=pdf
    GraphS G = new GraphS(V);
    // special case
    if (V == 1) return G;
    // Cayley's theorem: there are V^(V-2) labeled trees on V vertices
    // Prufer sequence: sequence of V-2 values between 0 and V-1
    // Prufer's proof of Cayley's theorem: Prufer sequences are in 1-1
    // correspondence with labeled trees on V vertices
    int[] prufer = new int[V-2];
    for (int i = 0; i < V-2; i++) prufer[i] = StdRandom.uniform(V);
    // degree of vertex v = 1 + number of times it appers in Prufer sequence
    int[] degree = new int[V];
    for (int v = 0; v < V; v++) degree[v] = 1;
    for (int i = 0; i < V-2; i++) degree[prufer[i]]++;
    // pq contains all vertices of degree 1
    MinPQ<Integer> pq = new MinPQ<Integer>();
    for (int v = 0; v < V; v++) if (degree[v] == 1) pq.insert(v);
    // repeatedly delMin() degree 1 vertex that has the minimum index
    for (int i = 0; i < V-2; i++) {
      int v = pq.delMin();
      G.addEdge(v, prufer[i]);
      degree[v]--;
      degree[prufer[i]]--;
      if (degree[prufer[i]] == 1) pq.insert(prufer[i]);
    }
    G.addEdge(pq.delMin(), pq.delMin());
    return G;
  }

  public static void main(String[] args) {
    int V = Integer.parseInt(args[0]);
    int E = Integer.parseInt(args[1]);
    int V1 = V/2;
    int V2 = V - V1;

    StdOut.println("complete graph");
    StdOut.println(complete(V));
    StdOut.println();

    StdOut.println("simple");
    StdOut.println(simple(V, E));
    StdOut.println();

    StdOut.println("Erdos-Renyi");
    double p = (double) E / (V*(V-1)/2.0);
    StdOut.println(simple(V, p));
    StdOut.println();

    StdOut.println("complete bipartite");
    StdOut.println(completeBipartite(V1, V2));
    StdOut.println();

    StdOut.println("bipartite");
    StdOut.println(bipartite(V1, V2, E));
    StdOut.println();

    StdOut.println("Erdos Renyi bipartite");
    double q = (double) E / (V1*V2);
    StdOut.println(bipartite(V1, V2, q));
    StdOut.println();

    StdOut.println("path");
    StdOut.println(path(V));
    StdOut.println();

    StdOut.println("cycle");
    StdOut.println(cycle(V));
    StdOut.println();

    StdOut.println("binary tree");
    StdOut.println(binaryTree(V));
    StdOut.println();

    StdOut.println("tree");
    StdOut.println(tree(V));
    StdOut.println();

    StdOut.println("4-regular");
    StdOut.println(regular(V, 4));
    StdOut.println();

    StdOut.println("star");
    StdOut.println(star(V));
    StdOut.println();

    StdOut.println("wheel");
    StdOut.println(wheel(V));
    StdOut.println();
  }

}
