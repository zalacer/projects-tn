package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BreadthFirstPaths.java
// implemented using GraphS

public class BreadthFirstPathsS {
  private static final int INFINITY = Integer.MAX_VALUE;
  private boolean[] marked;  // marked[v] = is there an s-v path
  private Integer[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
  private int[] distTo;      // distTo[v] = number of edges shortest s-v path
  private int closestVertex = -1;
  private int farthestVertex = -1;
  private int shortestPathLength = -1;
  private int longestPathLength = -1;
  private int V;

  public BreadthFirstPathsS(GraphS G, int s) {
    if (G == null) throw new IllegalArgumentException("BreadthFirstPathsX: G is null");
    if (s < 0) throw new IllegalArgumentException("BreadthFirstPathsX: s is < 0");
    V = G.V();
    marked = new boolean[V];
    distTo = new int[V];
    edgeTo = new Integer[V];
    validateVertex(s);
    bfs(G, s);
//    boolean chk = check(G, s); 
//    System.out.println("check returned "+chk+"\n");
    assert check(G, s);
    int shortest = V, longest = 0, closest = 0, farthest = 0, x;
    for (int i = 0; i < V; i++) {
      x = distTo(i);
      if (x == s || x == INFINITY) continue;
      if (x < shortest) { shortest = x; closest = i; }
      if (x > longest) { longest = x; farthest = i; }
    }
    closestVertex = closest; farthestVertex = farthest;
    shortestPathLength = shortest; longestPathLength = longest;
  }
  
  public BreadthFirstPathsS(GraphS G, int s, String type) {
    // for Ex4114 - using a stack instead of a queue in bfs
    if (G == null) throw new IllegalArgumentException("BreadthFirstPathsX: G is null");
    if (s < 0) throw new IllegalArgumentException("BreadthFirstPathsX: s is < 0");
    if (!(type.equals("q")||type.equals("s"))) throw new IllegalArgumentException(
        "BreadthFirstPathsX: type isn't \"q\" or \"s\"");
    V = G.V();
    marked = new boolean[V];
    distTo = new int[V];
    edgeTo = new Integer[V];
    validateVertex(s);
    if (type == "s") { 
      bfstack(G, s); 
      boolean chk = check(G, s); 
      System.out.println("check returned "+chk+"\n");
      return; 
    }
    bfs(G, s);
    assert check(G, s);
    int shortest = V, longest = 0, closest = 0, farthest = 0, x;
    for (int i = 0; i < V; i++) {
      x = distTo(i);
      if (x == s || x == INFINITY) continue;
      if (x < shortest) { shortest = x; closest = i; }
      if (x > longest) { longest = x; farthest = i; }
    }
    closestVertex = closest; farthestVertex = farthest;
    shortestPathLength = shortest; longestPathLength = longest;
  }

  public BreadthFirstPathsS(GraphS G, Iterable<Integer> sources) {
    if (G == null) throw new IllegalArgumentException("BreadthFirstPathsX: file is null");
    if (sources == null) throw new IllegalArgumentException("BreadthFirstPathsX: sources is null");
    V = G.V();
    marked = new boolean[V];
    distTo = new int[V];
    edgeTo = new Integer[V];
    for (int v = 0; v < V; v++) distTo[v] = INFINITY;
    validateVertices(sources);
    bfs(G, sources);
  }
  
  public int V() { return V; }
  
  public Integer[] edgeTo() { return edgeTo.clone(); }
  
  public int[] distTo() { return distTo.clone(); }
  
  public boolean[] marked() { return marked.clone(); }

  private void bfs(GraphS G, int s) {
    Queue<Integer> q = new Queue<>();
    for (int v = 0; v < G.V(); v++) distTo[v] = INFINITY;
    distTo[s] = 0;
    marked[s] = true;
    q.enqueue(s);
    while (!q.isEmpty()) {
      int v = q.dequeue();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
  }
  
  private void bfstack(GraphS G, int s) {
    Stack<Integer> q = new Stack<>();
    for (int v = 0; v < G.V(); v++) distTo[v] = INFINITY;
    distTo[s] = 0;
    marked[s] = true;
    q.push(s);
    while (!q.isEmpty()) {
      int v = q.pop();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.push(w);
        }
      }
    }
  }

  private void bfs(GraphS G, Iterable<Integer> sources) {
    Queue<Integer> q = new Queue<Integer>();
    for (int s : sources) {
      marked[s] = true;
      distTo[s] = 0;
      q.enqueue(s);
    }
    while (!q.isEmpty()) {
      int v = q.dequeue();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.enqueue(w);
        }
      }
    }
  }
  
  @SuppressWarnings("unused")
  private void bfstack(GraphS G, Iterable<Integer> sources) {
    // for Ex4114 - using a stack instead of a queue in bfs
    // this gives incorrect results
    Stack<Integer> q = new Stack<Integer>();
    for (int s : sources) {
      marked[s] = true;
      distTo[s] = 0;
      q.push(s);
    }
    while (!q.isEmpty()) {
      int v = q.pop();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          distTo[w] = distTo[v] + 1;
          marked[w] = true;
          q.push(w);
        }
      }
    }
  }

  public boolean hasPathTo(int v) { validateVertex(v); return marked[v]; }

  public int distTo(int v) { validateVertex(v); return distTo[v]; }
  
  public int closestVertex() { return closestVertex; }
    
  public int farthestVertex() { return farthestVertex; }
  
  public int shortestPathLength() { return shortestPathLength; }
  
  public int longestPathLength() { return longestPathLength; }
  
  public int[][] shortestPaths() {
    
    return new int[1][];
  }
  
  public Iterable<Integer> pathTo(int v) {
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<>();
    int x;
    for (x = v; distTo[x] != 0; x = edgeTo[x]) path.push(x);
    path.push(x);
    return path;
  }
  
  public int[] arrayPathTo(int v) {
    validateVertex(v);
    if (!hasPathTo(v)) return null;
    Stack<Integer> path = new Stack<>();
    int x;
    for (x = v; distTo[x] != 0; x = edgeTo[x]) path.push(x);
    path.push(x);
    return (int[]) unbox(path.toArray(1));
  }
  
  private boolean check(GraphS G, int s) {
//    System.out.println("check() messages:");
    validateVertex(s);
    if (distTo[s] != 0) {
      StdOut.println("distance of source " + s + " to itself = " + distTo[s]);
      return false;
    }
    for (int v = 0; v < G.V(); v++) {
      for (int w : G.adj(v)) {
        if (hasPathTo(v) != hasPathTo(w)) {
          StdOut.println("edge " + v + "-" + w);
          StdOut.println("hasPathTo(" + v + ") = " + hasPathTo(v));
          StdOut.println("hasPathTo(" + w + ") = " + hasPathTo(w));
          return false;
        }
        if (hasPathTo(v) && (distTo[w] > distTo[v] + 1)) {
          StdOut.println("edge " + v + "-" + w);
          StdOut.println("distTo[" + v + "] = " + distTo[v]);
          StdOut.println("distTo[" + w + "] = " + distTo[w]);
          return false;
        }
      }
    }
    for (int w = 0; w < G.V(); w++) {
      if (!hasPathTo(w) || w == s) continue;
      int v = edgeTo[w];
      if (distTo[w] != distTo[v] + 1) {
        StdOut.println("shortest path edge " + v + "-" + w);
        StdOut.println("distTo[" + v + "] = " + distTo[v]);
        StdOut.println("distTo[" + w + "] = " + distTo[w]);
        return false;
      }
    }
    return true;
  }

  // throw an IllegalArgumentException unless {@code 0 <= v < V}
  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException(
        "vertex " + v + " is not between 0 and " + (V-1));
  }

  private void validateVertices(Iterable<Integer> vertices) {
    if (vertices == null) throw new IllegalArgumentException("argument is null");
    int V = marked.length;
    for (int v : vertices)
      if (v < 0 || v >= V) throw new IllegalArgumentException(
          "vertex " + v + " is not between 0 and " + (V-1));
  }
  
  public static void bfSearch(String file, int source) {
    if (file == null) throw new IllegalArgumentException("bfSearch: file is null");
    if (source < 0) throw new IllegalArgumentException("bfSearch: source is < 0");
    GraphS G = new GraphS(new In(file));
    BreadthFirstPathsS bfs = new BreadthFirstPathsS(G,source);
    StdOut.println("Paths:");
    for (int v = 0; v < bfs.V(); v++) {
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d (%d):  ", source, v, bfs.distTo(v));
        for (int x : bfs.pathTo(v)) {
          if (x == source) StdOut.print(x);
          else StdOut.print("-" + x);
        }
        StdOut.println();
      }
      else StdOut.printf("%d to %d (-):  not connected\n", source, v);
    }
    StdOut.print("\nedgeTo:"); par(bfs.edgeTo());
    StdOut.print("G.adj:"); par(G.adj());
  }
  
  public static void bfSearch(String file, int source, String type) {
    if (file == null) throw new IllegalArgumentException("bfSearch: file is null");
    if (source < 0) throw new IllegalArgumentException("bfSearch: source is < 0");
    if (!(type.equals("q")||type.equals("s"))) throw new IllegalArgumentException(
        "BreadthFirstPathsX: type isn't \"q\" or \"s\"");
    GraphS G = new GraphS(new In(file));
    BreadthFirstPathsS bfs = null;
    if (type.equals("s")) bfs = new BreadthFirstPathsS(G,source, "s");
    else bfs = new BreadthFirstPathsS(G,source);
    StdOut.println("Paths:");
    for (int v = 0; v < bfs.V(); v++) {
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d (%d):  ", source, v, bfs.distTo(v));
        for (int x : bfs.pathTo(v)) {
          if (x == source) StdOut.print(x);
          else StdOut.print("-" + x);
        }
        StdOut.println();
      }
      else StdOut.printf("%d to %d (-):  not connected\n", source, v);
    }
    StdOut.print("\nedgeTo:"); par(bfs.edgeTo());
    StdOut.print("G.adj:"); par(G.adj());
  }

  public static void main(String[] args) {
    
//    bfSearch("tinyGex2.txt",0);
//    bfSearch("tinyGex2.txt",0,"s");
    
    In in = new In(args[0]);
    GraphS G = new GraphS(in);
    System.out.print("G.adj="+G.adj());

    int s = Integer.parseInt(args[1]);
    BreadthFirstPathsS bfs = new BreadthFirstPathsS(G, s);

    for (int v = 0; v < G.V(); v++) {
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d (%d):  ", s, v, bfs.distTo(v));
        for (int x : bfs.pathTo(v)) {
          if (x == s) StdOut.print(x);
          else        StdOut.print("-" + x);
        }
        StdOut.println();
      }
      else StdOut.printf("%d to %d (-):  not connected\n", s, v);
    }
    
    for (int v = 0; v < G.V(); v++) 
      if (bfs.hasPathTo(v)) {
        StdOut.printf("%d to %d (%d):  ", s, v, bfs.distTo(v)); par(bfs.arrayPathTo(v)); }
    
    StdOut.print("edgeTo:"); par(bfs.edgeTo());
    StdOut.print("distTo:"); par(bfs.distTo());
    StdOut.print("marked:"); par(bfs.marked());
    System.out.println("shortestPathLength="+bfs.shortestPathLength());
    System.out.println("closestVertex="+bfs.closestVertex());
    System.out.println("longestPathLength="+bfs.longestPathLength());
    System.out.println("farthestVertex="+bfs.farthestVertex());



  }

}
