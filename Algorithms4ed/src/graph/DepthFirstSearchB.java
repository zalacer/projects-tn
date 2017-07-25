package graph;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// from text p531
public class DepthFirstSearchB {
  private boolean[] marked;
  private int count;

  public DepthFirstSearchB(Graph G, int s)  {
    marked = new boolean[G.V()];
    dfs(G, s);
  }

  private void dfs(Graph G, int v)  {
    marked[v] = true;
    count++;
    for (int w : G.adj(v))
      if (!marked[w]) dfs(G, w);
  }

  public boolean marked(int w) { return marked[w]; }

  public int count() { return count; }

  public static void main(String[] args) {
    In in = new In(args[0]);
    Graph G = new Graph(in);
    int s = Integer.parseInt(args[1]);
    DepthFirstSearchB search = new DepthFirstSearchB(G, s);
    for (int v = 0; v < G.V(); v++)
      if (search.marked(v)) StdOut.print(v + " ");
    StdOut.println();
    if (search.count() != G.V()) StdOut.println("NOT connected");
    else StdOut.println("connected");
  }

}
