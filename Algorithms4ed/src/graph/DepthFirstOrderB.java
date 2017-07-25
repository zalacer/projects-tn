package graph;

import static v.ArrayUtils.*;

import ds.Queue;
import ds.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from text p580

public class DepthFirstOrderB {
  private boolean[] marked;
  private Queue<Integer> pre; // vertices in preorder
  private Queue<Integer> post; // vertices in postorder
  private Stack<Integer> reversePost; // vertices in reverse postorder
  private DigraphX G;
  private int V;
  
  public DepthFirstOrderB(DigraphX g)  {
    if (g == null) throw new IllegalArgumentException("DepthFirstOrderB: DigraphX g is null");
    G = g;
    V = G.V();
    pre = new Queue<Integer>();
    post = new Queue<Integer>();
    reversePost = new Stack<Integer>();
    marked = new boolean[V];
    for (int v = 0; v < V; v++) if (!marked[v]) dfs(v);
  }
  
  private void dfs(int v) {
    pre.enqueue(v);
    marked[v] = true;
    for (int w : G.adj(v)) if (!marked[w]) dfs(w);
    post.enqueue(v);
    reversePost.push(v);
  }
  
  public Iterable<Integer> pre() { return pre; }
  
  public Integer[] preArray() { return toArray(pre().iterator()); }
  
  public Iterable<Integer> post() { return post; }
  
  public Integer[] postArray() { return toArray(post().iterator()); }

  public Iterable<Integer> reversePost() { return reversePost; }
  
  public Integer[] reversePostArray() { return toArray(reversePost().iterator()); }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    DigraphX d = new DigraphX(in);
    StdOut.println(d);
    par(d.adj());
    DepthFirstOrderB dfo = new DepthFirstOrderB(d);
    System.out.print("pre="); par(dfo.preArray());
    System.out.print("post="); par(dfo.postArray());
    System.out.print("reversePost="); par(dfo.reversePostArray());

  }

}
