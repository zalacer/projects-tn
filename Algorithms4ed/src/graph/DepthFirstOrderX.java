package graph;

import ds.Queue;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from http://algs4.cs.princeton.edu/42digraph/DepthFirstOrder.java

public class DepthFirstOrderX {
  private boolean[] marked;          // marked[v] = has v been marked in dfs?
  private int[] pre;                 // pre[v]    = preorder  number of v
  private int[] post;                // post[v]   = postorder number of v
  private Queue<Integer> preorder;   // vertices in preorder
  private Queue<Integer> postorder;  // vertices in postorder
  private int preCounter;            // counter or preorder numbering
  private int postCounter;           // counter for postorder numbering

  public DepthFirstOrderX(DigraphX G) {
    pre    = new int[G.V()];
    post   = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder  = new Queue<Integer>();
    marked    = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v]) dfs(G, v);

    assert check();
  }
  
  public DepthFirstOrderX(EuclidianDigraph G) {
    pre    = new int[G.V()];
    post   = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder  = new Queue<Integer>();
    marked    = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v]) dfs(G, v);

    assert check();
  }

  public DepthFirstOrderX(EdgeWeightedDigraphX G) {
    pre    = new int[G.V()];
    post   = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder  = new Queue<Integer>();
    marked    = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v]) dfs(G, v);
  }
  
  public DepthFirstOrderX(EdgeWeightedDigraphI G) {
    pre    = new int[G.V()];
    post   = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder  = new Queue<Integer>();
    marked    = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v]) dfs(G, v);
  }
  
  public DepthFirstOrderX(EuclidianEdgeWeightedDigraph G) {
    pre    = new int[G.V()];
    post   = new int[G.V()];
    postorder = new Queue<Integer>();
    preorder  = new Queue<Integer>();
    marked    = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++)
      if (!marked[v]) dfs(G, v);
  }

  // run DFS in digraph G from vertex v and compute preorder/postorder
  private void dfs(DigraphX G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
    postorder.enqueue(v);
    post[v] = postCounter++;
  }
  
  private void dfs(EuclidianDigraph G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (int w : G.adj(v)) if (!marked[w]) dfs(G, w);
    postorder.enqueue(v);
    post[v] = postCounter++;
  }

  private void dfs(EdgeWeightedDigraphX G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (!marked[w]) dfs(G, w);
    }
    postorder.enqueue(v); post[v] = postCounter++;
  }
  
  private void dfs(EdgeWeightedDigraphI G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (DirectedEdgeI e : G.adj(v)) {
      int w = e.to();
      if (!marked[w]) dfs(G, w);
    }
    postorder.enqueue(v); post[v] = postCounter++;
  }
  
  private void dfs(EuclidianEdgeWeightedDigraph G, int v) {
    marked[v] = true;
    pre[v] = preCounter++;
    preorder.enqueue(v);
    for (DirectedEdgeX e : G.adj(v)) {
      int w = e.to();
      if (!marked[w]) dfs(G, w);
    }
    postorder.enqueue(v); post[v] = postCounter++;
  }

  public int pre(int v) { validateVertex(v); return pre[v]; }

  public int post(int v) { validateVertex(v); return post[v]; }

  public Iterable<Integer> post() { return postorder; }

  public Iterable<Integer> pre() { return preorder; }

  public Iterable<Integer> reversePost() {
    Stack<Integer> reverse = new Stack<Integer>();
    for (int v : postorder) reverse.push(v);
    return reverse;
  }

  private boolean check() {
    // check that post(v) is consistent with post()
    int r = 0;
    for (int v : post()) {
      if (post(v) != r) {
        StdOut.println("post(v) and post() inconsistent");
        return false;
      }
      r++;
    }
    // check that pre(v) is consistent with pre()
    r = 0;
    for (int v : pre()) {
      if (pre(v) != r) {
        StdOut.println("pre(v) and pre() inconsistent");
        return false;
      }
      r++;
    }
    return true;
  }

  private void validateVertex(int v) {
    int V = marked.length;
    if (v < 0 || v >= V) throw new IllegalArgumentException("vertex "+v+" is out of bounds");
  }

  public static void main(String[] args) {
    In in = new In(args[0]);
    DigraphX G = new DigraphX(in);

    DepthFirstOrderX dfs = new DepthFirstOrderX(G);
    StdOut.println("   v  pre post");
    StdOut.println("--------------");
    for (int v = 0; v < G.V(); v++) {
      StdOut.printf("%4d %4d %4d\n", v, dfs.pre(v), dfs.post(v));
    }

    StdOut.print("Preorder:  ");
    for (int v : dfs.pre()) {
      StdOut.print(v + " ");
    }
    StdOut.println();

    StdOut.print("Postorder: ");
    for (int v : dfs.post()) {
      StdOut.print(v + " ");
    }
    StdOut.println();

    StdOut.print("Reverse postorder: ");
    for (int v : dfs.reversePost()) {
      StdOut.print(v + " ");
    }
    StdOut.println();


  }

}
