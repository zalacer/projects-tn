package graph;

// from text p547

public class CycleB {
  private boolean[] marked;
  private boolean hasCycle;
  
  public CycleB(Graph G) {
    marked = new boolean[G.V()];
    for (int s = 0; s < G.V(); s++)
      if (!marked[s])
        dfs(G, s, s);
  }
  
  private void dfs(Graph G, int v, int u) {
    marked[v] = true;
    for (int w : G.adj(v))
      if (!marked[w])
        dfs(G, w, v);
      else if (w != u) hasCycle = true;
  }
  
  public boolean hasCycle() { return hasCycle; }


public static void main(String[] args) {
  // TODO Auto-generated method stub

}

}
