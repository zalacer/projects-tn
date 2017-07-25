package graph;

import static v.ArrayUtils.*;

import edu.princeton.cs.algs4.In;

// from text p544

public class CCB {
  private boolean[] marked;
  private int[] id;
  private int count;
  
  public CCB(GraphX G) {
    marked = new boolean[G.V()];
    id = new int[G.V()];
    for (int s = 0; s < G.V(); s++)
      if (!marked[s])
      {
        dfs(G, s);
        count++;
      }
  }
  
  private void dfs(GraphX G, int v) {
    marked[v] = true;
    id[v] = count;
    for (int w : G.adj(v))
      if (!marked[w])
        dfs(G, w);
  }
  
  public boolean connected(int v, int w) { return id[v] == id[w]; }
  
  public int id(int v) { return id[v]; }
  
  public int count() { return count; }

  public static void main(String[] args) {
    
    In in = new In(args[0]);
    GraphX g = new GraphX(in);
    CCB cc = new CCB(g);
    System.out.println("count="+cc.count);
    System.out.print("id[]="); par(cc.id);

  }

}
