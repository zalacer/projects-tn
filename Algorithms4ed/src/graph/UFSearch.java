package graph;

import ds.Bag;
import ds.WeightedQuickUnionWithPathCompressionUF;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class UFSearch {
  private int s;
  private WeightedQuickUnionWithPathCompressionUF uf;
  
  public UFSearch(Graph G, int s) {
    if (G == null) throw new IllegalArgumentException("UFSearch: Graph G is null");
    if (s < 0) throw new IllegalArgumentException("UFSearch: s is < 0");
    this.s = s;
    uf = new WeightedQuickUnionWithPathCompressionUF(G.V());
    Bag<Integer>[] adj = G.adj();
    for (int i = 0; i < adj.length; i++) for (int j : adj[i]) uf.union(i, j);
  } 
  
  public boolean marked(int v) { return uf.connected(s,v); }
  
  public int count() { return uf.count(s); }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    Graph G = new Graph(in);
    int s = Integer.parseInt(args[1]);
    UFSearch search = new UFSearch(G, s);
    for (int v = 0; v < G.V(); v++)
      if (search.marked(v)) StdOut.print(v + " ");
    StdOut.println();
    System.out.println("G.V="+G.V());
    System.out.println("search.count="+search.count());
    if (search.count() != G.V()) StdOut.println("NOT connected");
    else StdOut.println("connected");

  }

}
